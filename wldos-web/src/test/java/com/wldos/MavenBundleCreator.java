/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Maven Central Bundle 创建工具
 * 用于创建符合 Maven Central 要求的 bundle（包含签名和校验和）
 * 
 * @author 元悉宇宙
 * @date 2025-12-28
 */
public class MavenBundleCreator {
    
    private static final String[] GPG_PATHS = {
        "gpg",  // PATH 中的 gpg
        "C:\\Program Files (x86)\\GnuPG\\bin\\gpg.exe"
    };
    
    private String moduleDir;
    private String version;
    private String module;
    private String parentModule;
    private String baseName;
    private String parentBaseName;
    private String mavenRepo;
    private String gpgPassphrase;
    private String gpgCmd;
    
    public static void main(String[] args) {
        try {
            MavenBundleCreator creator = new MavenBundleCreator();
            creator.run();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public void run() throws Exception {
        System.out.println("=== Maven Central Bundle Creator ===");
        
        // 获取当前工作目录（应该是 wldos-common 目录）
        moduleDir = new File(".").getCanonicalPath();
        System.out.println("Module directory: " + moduleDir);
        
        // 读取 pom.xml
        File pomFile = new File(moduleDir, "pom.xml");
        if (!pomFile.exists()) {
            throw new IOException("pom.xml not found in current directory");
        }
        
        readPomInfo(pomFile);
        System.out.println("Version: " + version);
        System.out.println("Module: " + module);
        System.out.println("Parent Module: " + parentModule);
        
        baseName = module + "-" + version;
        parentBaseName = parentModule + "-" + version;
        
        // 检测 Maven 仓库路径
        mavenRepo = detectMavenRepo();
        System.out.println("Maven repository: " + mavenRepo);
        
        // 执行 Maven 构建
        System.out.println("\nBuilding with Maven (including sources and javadoc)...");
        executeMavenBuild();
        
        // 创建目录结构
        File targetDir = new File(moduleDir, "target");
        File artifactPath = new File(targetDir, "com/wldos/" + module + "/" + version);
        File parentPath = new File(targetDir, "com/wldos/" + parentModule + "/" + version);
        
        artifactPath.mkdirs();
        parentPath.mkdirs();
        
        System.out.println("\nCopying files...");
        copyFiles(targetDir, artifactPath, parentPath);
        
        System.out.println("\nGenerating checksums...");
        generateChecksums(artifactPath, parentPath);
        
        System.out.println("\nGenerating GPG signatures...");
        if (findGpg() && loadGpgPassphrase()) {
            generateGpgSignatures(artifactPath, parentPath);
        } else {
            System.err.println("WARNING: GPG not found or passphrase not configured. Bundle will fail Central validation!");
        }
        
        System.out.println("\nCreating bundle...");
        createBundle(targetDir, artifactPath, parentPath);
        
        System.out.println("\nDone! Created target/" + baseName + "-bundle.zip");
    }
    
    private void readPomInfo(File pomFile) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(pomFile);
        
        Element root = doc.getDocumentElement();
        
        // 读取 parent version
        NodeList parentNodes = root.getElementsByTagName("parent");
        if (parentNodes.getLength() > 0) {
            Element parent = (Element) parentNodes.item(0);
            version = getElementText(parent, "version");
            parentModule = getElementText(parent, "artifactId");
        }
        
        // 读取 artifactId
        module = getElementText(root, "artifactId");
        
        if (version == null || module == null || parentModule == null) {
            throw new IOException("Failed to read required information from pom.xml");
        }
    }
    
    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            Node node = nodes.item(0);
            return node.getTextContent().trim();
        }
        return null;
    }
    
    private String detectMavenRepo() {
        // 优先检查环境变量
        String userHome = System.getProperty("user.home");
        File m2Repo = new File(userHome, ".m2/repository");
        if (m2Repo.exists()) {
            return m2Repo.getAbsolutePath();
        }
        
        // 检查常见的 Maven 安装路径
        String[] commonPaths = {
            "C:\\java\\apache-maven-3.6.3\\repo"
        };
        
        for (String path : commonPaths) {
            File repo = new File(path);
            if (repo.exists()) {
                return path;
            }
        }
        
        return m2Repo.getAbsolutePath(); // 默认返回
    }
    
    private void executeMavenBuild() throws Exception {
        ProcessBuilder pb = new ProcessBuilder("mvn", "clean", "package", 
            "source:jar", "javadoc:jar", "-DskipTests");
        pb.directory(new File(moduleDir));
        pb.redirectErrorStream(true);
        
        Process process = pb.start();
        
        // 输出构建日志
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
        
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Maven build failed with exit code: " + exitCode);
        }
    }
    
    private void copyFiles(File targetDir, File artifactPath, File parentPath) throws IOException {
        // 复制模块文件
        File[] filesToCopy = {
            new File(targetDir, baseName + ".jar"),
            new File(targetDir, baseName + "-sources.jar"),
            new File(targetDir, baseName + "-javadoc.jar"),
            new File(moduleDir, "pom.xml")
        };
        
        String[] targetNames = {
            baseName + ".jar",
            baseName + "-sources.jar",
            baseName + "-javadoc.jar",
            baseName + ".pom"
        };
        
        for (int i = 0; i < filesToCopy.length; i++) {
            File source = filesToCopy[i];
            if (source.exists()) {
                Files.copy(source.toPath(), 
                    new File(artifactPath, targetNames[i]).toPath(), 
                    StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Copied: " + targetNames[i]);
            } else {
                System.out.println("Warning: " + source.getName() + " not found");
            }
        }
        
        // 复制父 POM
        File parentPomSource = new File(mavenRepo, 
            "com/wldos/" + parentModule + "/" + version + "/" + parentBaseName + ".pom");
        if (!parentPomSource.exists()) {
            throw new IOException("Parent POM not found at: " + parentPomSource.getAbsolutePath() + 
                "\nPlease run 'mvn install -N' in the root directory first.");
        }
        
        Files.copy(parentPomSource.toPath(), 
            new File(parentPath, parentBaseName + ".pom").toPath(), 
            StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Copied parent POM");
    }
    
    private void generateChecksums(File artifactPath, File parentPath) throws Exception {
        // 为模块文件生成校验和
        String[] artifactFiles = {
            baseName + ".jar",
            baseName + ".pom",
            baseName + "-sources.jar",
            baseName + "-javadoc.jar"
        };
        
        for (String fileName : artifactFiles) {
            File file = new File(artifactPath, fileName);
            if (file.exists()) {
                generateChecksum(file);
            }
        }
        
        // 为父 POM 生成校验和
        File parentPom = new File(parentPath, parentBaseName + ".pom");
        if (parentPom.exists()) {
            generateChecksum(parentPom);
        }
    }
    
    private void generateChecksum(File file) throws Exception {
        String[] algorithms = {"MD5", "SHA-1", "SHA-512"};
        String[] extensions = {".md5", ".sha1", ".sha512"};
        
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        
        for (int i = 0; i < algorithms.length; i++) {
            MessageDigest digest = MessageDigest.getInstance(algorithms[i]);
            byte[] hash = digest.digest(fileBytes);
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            
            File checksumFile = new File(file.getParent(), file.getName() + extensions[i]);
            Files.write(checksumFile.toPath(), hexString.toString().getBytes("UTF-8"));
        }
        
        System.out.println("Generated checksums for: " + file.getName());
    }
    
    private boolean findGpg() {
        for (String path : GPG_PATHS) {
            if (path.equals("gpg")) {
                // 检查 PATH 中的 gpg
                try {
                    Process process = new ProcessBuilder("gpg", "--version").start();
                    int exitCode = process.waitFor();
                    if (exitCode == 0) {
                        gpgCmd = "gpg";
                        System.out.println("Found GPG in PATH");
                        return true;
                    }
                } catch (Exception e) {
                    // 继续检查其他路径
                }
            } else {
                File gpgFile = new File(path);
                if (gpgFile.exists()) {
                    gpgCmd = path;
                    System.out.println("Found GPG at: " + path);
                    return true;
                }
            }
        }
        
        System.err.println("GPG not found. Please install GPG or add it to PATH.");
        return false;
    }
    
    private boolean loadGpgPassphrase() {
        // 从 settings.xml 读取 passphrase
        String[] settingsPaths = {
            "C:\\java\\apache-maven-3.6.3\\conf\\settings.xml"            
        };
        
        for (String settingsPath : settingsPaths) {
            File settingsFile = new File(settingsPath);
            if (settingsFile.exists()) {
                try {
                    // 简单的 XML 解析（可以使用更完善的 XML 解析库）
                    String content = new String(Files.readAllBytes(settingsFile.toPath()), "UTF-8");
                    int profileStart = content.indexOf("<profile>");
                    while (profileStart >= 0) {
                        int profileEnd = content.indexOf("</profile>", profileStart);
                        if (profileEnd > profileStart) {
                            String profile = content.substring(profileStart, profileEnd);
                            if (profile.contains("<id>gpg</id>")) {
                                int passphraseStart = profile.indexOf("<gpg.passphrase>");
                                if (passphraseStart >= 0) {
                                    passphraseStart += "<gpg.passphrase>".length();
                                    int passphraseEnd = profile.indexOf("</gpg.passphrase>", passphraseStart);
                                    if (passphraseEnd > passphraseStart) {
                                        gpgPassphrase = profile.substring(passphraseStart, passphraseEnd).trim();
                                        System.out.println("GPG passphrase loaded from settings.xml");
                                        return true;
                                    }
                                }
                            }
                        }
                        profileStart = content.indexOf("<profile>", profileEnd);
                    }
                } catch (Exception e) {
                    System.err.println("Failed to read settings.xml: " + e.getMessage());
                }
            }
        }
        
        System.err.println("GPG passphrase not found in settings.xml");
        return false;
    }
    
    private void generateGpgSignatures(File artifactPath, File parentPath) throws Exception {
        // 为模块文件签名
        String[] artifactFiles = {
            baseName + ".jar",
            baseName + ".pom",
            baseName + "-sources.jar",
            baseName + "-javadoc.jar"
        };
        
        for (String fileName : artifactFiles) {
            File file = new File(artifactPath, fileName);
            if (file.exists()) {
                signFile(file);
            }
        }
        
        // 为父 POM 签名
        File parentPom = new File(parentPath, parentBaseName + ".pom");
        if (parentPom.exists()) {
            signFile(parentPom);
        }
    }
    
    private void signFile(File file) throws Exception {
        // ProcessBuilder 会自动处理路径中的空格，不需要加引号
        List<String> command = new ArrayList<>();
        command.add(gpgCmd);
        command.add("--batch");
        command.add("--yes");
        command.add("--passphrase-fd");
        command.add("0");
        command.add("--detach-sign");
        command.add("--armor");
        command.add(file.getAbsolutePath());
        
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(file.getParentFile());
        
        Process process = pb.start();
        
        // 写入 passphrase
        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(process.getOutputStream(), "UTF-8"))) {
            writer.println(gpgPassphrase);
            writer.flush();
        }
        
        // 读取输出
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("GPG: " + line);
            }
        }
        
        // 读取错误输出
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getErrorStream(), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.err.println("GPG Error: " + line);
            }
        }
        
        int exitCode = process.waitFor();
        if (exitCode == 0) {
            File sigFile = new File(file.getParent(), file.getName() + ".asc");
            if (sigFile.exists()) {
                System.out.println("Successfully signed: " + file.getName());
            } else {
                throw new IOException("Signature file not created: " + sigFile.getName());
            }
        } else {
            throw new IOException("GPG signing failed for: " + file.getName() + " (exit code: " + exitCode + ")");
        }
    }
    
    private void createBundle(File targetDir, File artifactPath, File parentPath) throws IOException {
        File bundleFile = new File(targetDir, baseName + "-bundle.zip");
        
        try (ZipOutputStream zos = new ZipOutputStream(
                new FileOutputStream(bundleFile))) {
            
            // 添加模块文件
            addDirectoryToZip(artifactPath, artifactPath, zos);
            
            // 添加父 POM
            addDirectoryToZip(parentPath, parentPath, zos);
        }
        
        System.out.println("Bundle created: " + bundleFile.getAbsolutePath());
    }
    
    private void addDirectoryToZip(File sourceDir, File baseDir, ZipOutputStream zos) throws IOException {
        Files.walkFileTree(sourceDir.toPath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String relativePath = baseDir.toPath().relativize(file).toString().replace('\\', '/');
                ZipEntry entry = new ZipEntry(relativePath);
                zos.putNextEntry(entry);
                Files.copy(file, zos);
                zos.closeEntry();
                return FileVisitResult.CONTINUE;
            }
        });
    }
}

