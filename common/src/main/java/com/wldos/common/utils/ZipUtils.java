/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import lombok.extern.slf4j.Slf4j;


/**
 * zip压缩工具。
 *
 * @author 树悉猿
 * @date 2022/2/12
 * @version 1.0
 */
@Slf4j
public class ZipUtils {
	private static final int bufferSize = 2 * 1024;

	public static void main(String[] args) throws Exception {

	}

	/**
	 *  压缩成zip
	 * @param srcDir 源文件夹
	 * @param out 代表压缩输出目标的输出流
	 * @param keepDirStructure 是否保持目录结构
	 */
	public static void toZip(String srcDir, OutputStream out, boolean keepDirStructure) throws IOException {

		try (ZipOutputStream zos = new ZipOutputStream(out)) {
			File sourceFile = new File(srcDir);
			compress(sourceFile, zos, sourceFile.getName(), keepDirStructure);
		}
	}

	public static void toZip(List<File> srcFiles, OutputStream out) throws IOException {

		try (ZipOutputStream zos = new ZipOutputStream(out)) {

			for (File srcFile : srcFiles) {

				byte[] buf = new byte[bufferSize];

				zos.putNextEntry(new ZipEntry(srcFile.getName()));

				int len;

				FileInputStream in = new FileInputStream(srcFile);

				while ((len = in.read(buf)) != -1) {

					zos.write(buf, 0, len);

				}

				zos.closeEntry();

				in.close();
			}
		}
		catch (Exception e) {

			throw new RuntimeException("zip error from ZipUtils", e);

		}
	}

	private static void compress(File sourceFile, ZipOutputStream zos, String name,
			boolean keepDirStructure) throws IOException {

		byte[] buf = new byte[bufferSize];

		if (sourceFile.isFile()) {
			// 向zip输出流添加一个zip实体，构造器中name为zip实体的文件的名字
			zos.putNextEntry(new ZipEntry(name));

			// copy文件到zip输出流中
			int len;
			FileInputStream in = new FileInputStream(sourceFile);
			while ((len = in.read(buf)) != -1) {
				zos.write(buf, 0, len);
			}

			// 完成实体
			zos.closeEntry();
			in.close();
		}
		else {
			File[] listFiles = sourceFile.listFiles();
			if (listFiles == null || listFiles.length == 0) {
				// 需要保留原来的文件结构时，需要对空文件夹进行处理
				if (keepDirStructure) {
					// 空文件夹的处理
					zos.putNextEntry(new ZipEntry(name + "/"));

					// 没有文件，不需要文件的copy
					zos.closeEntry();
				}
			}
			else {
				for (File file : listFiles) {
					// 判定是否需要保留原来的文件结构
					if (keepDirStructure) {
						// 注意file.getName()前需要带上父文件夹的名字加一斜杠
						// 不然最后压缩包中就不能保留原来的文件结构，即：所有文件都跑到压缩包根目录下
						compress(file, zos, name + "/" + file.getName(), true);
					}
					else {
						compress(file, zos, file.getName(), false);
					}
				}
			}
		}
	}

	/**
	 * 解压指定文件到指定目录
	 *
	 * @param zipPath 压缩文件路径
	 * @param descDir 解压缩目录
	 * @param target 要解压缩的特定文件或目录名，为null解压全部文件
	 * @throws IOException io
	 */
	public static void unZip(String zipPath, String descDir, String target) throws IOException {
		log.info("文件:{} 解压路径:{} 解压开始",zipPath,descDir);
		InputStream in = null;
		OutputStream out = null;
		ZipFile zip = null;
		try{
			File zipFile = new File(zipPath);
			if(!zipFile.exists()){
				throw new IOException("需解压文件不存在");
			}
			File pathFile = new File(descDir);
			if (!pathFile.exists()) {
				pathFile.mkdirs();
			}
			zip = new ZipFile(zipFile, Charset.forName(ShellUtils.isWindows() ? "GBK" : "UTF-8"));
			for (Enumeration<?> entries = zip.entries(); entries.hasMoreElements();) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				String zipEntryName = entry.getName();
				if (!ObjectUtils.isBlank(target) && !zipEntryName.equals(target))
					continue;
				in = zip.getInputStream(entry);
				String outPath = (descDir + File.separator + zipEntryName).replaceAll("\\*", "/");
				System.out.println(outPath);
				// 判断路径是否存在,不存在则创建文件路径
				File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
				if (!file.exists()) {
					file.mkdirs();
				}
				// 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
				if (new File(outPath).isDirectory()) {
					continue;
				}
				// 输出文件路径信息
				out = new FileOutputStream(outPath);
				byte[] buf1 = new byte[bufferSize];
				int len;
				while ((len = in.read(buf1)) > 0) {
					out.write(buf1, 0, len);
				}
				out.flush();
				out.close();
				in.close();
			}
		}catch(Exception e){
			throw new IOException(e);
		}finally {
			if (out != null)
				out.close();
			if (in != null)
				in.close();
			if (zip != null)
				zip.close();
		}
	}

	/**
	 * 级联删除文件夹及其子目录文件
	 *
	 * @param dirPath 文件夹路径
	 * @throws IOException io
	 */
	public static void delDir(String dirPath) throws IOException {
		try{
			File dirFile = new File(dirPath);
			if (!dirFile.exists()) {
				return;
			}
			if (dirFile.isFile()) {
				dirFile.delete();
				return;
			}
			File[] files = dirFile.listFiles();
			if(files==null){
				return;
			}
			for (File file : files) {
				delDir(file.toString());
			}
			dirFile.delete();
			System.out.println("删除文件: " + dirPath);
		}catch(Exception e){
			throw new IOException("删除文件异常");
		}
	}
}