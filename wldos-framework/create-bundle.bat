@echo off
setlocal EnableDelayedExpansion

set WLDOS_ROOT=C:\ideaspace\wldos
set CURRENT_DIR=%CD%

REM 先在根目录下执行Maven构建
echo Building from root directory...
cd %WLDOS_ROOT%
call mvn clean install -pl com.wldos:wldos-framework -am -DskipTests

REM 返回模块目录继续处理
cd %CURRENT_DIR%

REM 设置固定版本号和模块名
set VERSION=2.0.1
set MODULE=wldos-framework

echo Version: %VERSION%
echo Module: %MODULE%
set BASE_NAME=%MODULE%-%VERSION%

cd target

REM 创建正确的目录结构
echo Creating directory structure...
set ARTIFACT_PATH=com\wldos\%MODULE%\%VERSION%
mkdir %ARTIFACT_PATH%

REM 复制文件到正确的位置
echo Copying files to the correct location...
copy "%BASE_NAME%.jar" "%ARTIFACT_PATH%\%BASE_NAME%.jar"
copy "%BASE_NAME%-sources.jar" "%ARTIFACT_PATH%\%BASE_NAME%-sources.jar"
copy "%BASE_NAME%-javadoc.jar" "%ARTIFACT_PATH%\%BASE_NAME%-javadoc.jar"
copy ..\pom.xml "%ARTIFACT_PATH%\%BASE_NAME%.pom"

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

REM 创建bundle
echo Creating bundle...
powershell Compress-Archive -Path "com" -DestinationPath "%BASE_NAME%-bundle.zip" -Force

REM 清理临时文件
echo Cleaning up...
rmdir /s /q com

cd ..
echo Done! Created target/%BASE_NAME%-bundle.zip
pause