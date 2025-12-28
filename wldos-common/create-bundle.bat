@echo off
setlocal EnableDelayedExpansion

REM 先执行Maven构建
echo Building with Maven...
call mvn clean package -DskipTests

REM 从pom.xml获取版本号和模块名
echo Getting version and module name from pom.xml...
for /f "tokens=*" %%a in ('powershell -Command "$pom = [xml](Get-Content pom.xml); $pom.project.parent.version"') do (
    set VERSION=%%a
)
for /f "tokens=*" %%a in ('powershell -Command "$pom = [xml](Get-Content pom.xml); $pom.project.artifactId"') do (
    set MODULE=%%a
)
for /f "tokens=*" %%a in ('powershell -Command "$pom = [xml](Get-Content pom.xml); $pom.project.parent.artifactId"') do (
    set PARENT_MODULE=%%a
)

if "%VERSION%"=="" (
    echo Error: Could not get version from pom.xml
    pause
    exit /b 1
)

if "%MODULE%"=="" (
    echo Error: Could not get module name from pom.xml
    pause
    exit /b 1
)

echo Version: %VERSION%
echo Module: %MODULE%
echo Parent Module: %PARENT_MODULE%
set BASE_NAME=%MODULE%-%VERSION%
set PARENT_BASE_NAME=%PARENT_MODULE%-%VERSION%

cd target

REM 创建正确的目录结构
echo Creating directory structure...
set ARTIFACT_PATH=com\wldos\%MODULE%\%VERSION%
set PARENT_PATH=com\wldos\%PARENT_MODULE%\%VERSION%
mkdir %ARTIFACT_PATH%
mkdir %PARENT_PATH%

REM 复制文件到正确的位置
echo Copying files to the correct location...
copy "%BASE_NAME%.jar" "%ARTIFACT_PATH%\%BASE_NAME%.jar"
copy "%BASE_NAME%-sources.jar" "%ARTIFACT_PATH%\%BASE_NAME%-sources.jar"
copy "%BASE_NAME%-javadoc.jar" "%ARTIFACT_PATH%\%BASE_NAME%-javadoc.jar"
copy ..\pom.xml "%ARTIFACT_PATH%\%BASE_NAME%.pom"
copy "..\..\target\%PARENT_BASE_NAME%.pom" "%PARENT_PATH%\%PARENT_BASE_NAME%.pom"

REM 验证父POM是否存在
if not exist "%PARENT_PATH%\%PARENT_BASE_NAME%.pom" (
    echo Error: Parent POM not found at %PARENT_PATH%\%PARENT_BASE_NAME%.pom
    cd ..
    pause
    exit /b 1
)

REM 生成校验和
echo Generating checksums for module files...
pushd %ARTIFACT_PATH%
for %%f in (%BASE_NAME%.jar %BASE_NAME%.pom %BASE_NAME%-sources.jar %BASE_NAME%-javadoc.jar) do (
    echo Generating checksums for %%f
    powershell -Command "$md5 = Get-FileHash '%%f' -Algorithm MD5; $md5.Hash.ToLower() | Set-Content '%%f.md5' -NoNewline"
    powershell -Command "$sha1 = Get-FileHash '%%f' -Algorithm SHA1; $sha1.Hash.ToLower() | Set-Content '%%f.sha1' -NoNewline"
    powershell -Command "$sha512 = Get-FileHash '%%f' -Algorithm SHA512; $sha512.Hash.ToLower() | Set-Content '%%f.sha512' -NoNewline"
)
popd

echo Generating checksums for parent pom...
pushd %PARENT_PATH%
powershell -Command "$md5 = Get-FileHash '%PARENT_BASE_NAME%.pom' -Algorithm MD5; $md5.Hash.ToLower() | Set-Content '%PARENT_BASE_NAME%.pom.md5' -NoNewline"
powershell -Command "$sha1 = Get-FileHash '%PARENT_BASE_NAME%.pom' -Algorithm SHA1; $sha1.Hash.ToLower() | Set-Content '%PARENT_BASE_NAME%.pom.sha1' -NoNewline"
powershell -Command "$sha512 = Get-FileHash '%PARENT_BASE_NAME%.pom' -Algorithm SHA512; $sha512.Hash.ToLower() | Set-Content '%PARENT_BASE_NAME%.pom.sha512' -NoNewline"
popd

REM 生成GPG签名
echo Getting GPG password...
for /f "tokens=*" %%a in ('powershell -Command "$settings = [xml](Get-Content 'C:\java\apache-maven-3.6.3\conf\settings.xml'); $profile = $settings.settings.profiles.profile | Where-Object { $_.id -eq 'gpg' }; $profile.properties.'gpg.passphrase'"') do (
    set GPG_PASS=%%a
)

echo Generating GPG signatures for module files...
pushd %ARTIFACT_PATH%
for %%f in (%BASE_NAME%.jar %BASE_NAME%.pom %BASE_NAME%-sources.jar %BASE_NAME%-javadoc.jar) do (
    echo %GPG_PASS%| gpg --batch --yes --passphrase-fd 0 --detach-sign --armor "%%f"
)
popd

echo Generating GPG signature for parent pom...
pushd %PARENT_PATH%
echo %GPG_PASS%| gpg --batch --yes --passphrase-fd 0 --detach-sign --armor "%PARENT_BASE_NAME%.pom"
popd

REM 创建bundle
echo Creating bundle...
powershell Compress-Archive -Path "com" -DestinationPath "%BASE_NAME%-bundle.zip" -Force

REM 清理临时文件
echo Cleaning up...
rmdir /s /q com

cd ..
echo Done! Created target/%BASE_NAME%-bundle.zip
pause