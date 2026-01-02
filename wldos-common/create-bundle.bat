@echo off
setlocal EnableDelayedExpansion

REM 先执行Maven构建（包含 sources 和 javadoc，Maven Central 要求）C:\Program Files (x86)\GnuPG\bin需要添加到环境变量
REM 先在wldos根目录下执行Maven构建：mvn install -N -DskipTests，再切换到wldos-common目录运行本脚本，否则会报错

echo Building with Maven (including sources and javadoc)...
call mvn clean package source:jar javadoc:jar -DskipTests

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
if exist "%BASE_NAME%-sources.jar" (
    copy "%BASE_NAME%-sources.jar" "%ARTIFACT_PATH%\%BASE_NAME%-sources.jar"
) else (
    echo Warning: %BASE_NAME%-sources.jar not found, skipping...
)
if exist "%BASE_NAME%-javadoc.jar" (
    copy "%BASE_NAME%-javadoc.jar" "%ARTIFACT_PATH%\%BASE_NAME%-javadoc.jar"
) else (
    echo Warning: %BASE_NAME%-javadoc.jar not found, skipping...
)
copy ..\pom.xml "%ARTIFACT_PATH%\%BASE_NAME%.pom"

REM 从Maven仓库获取父POM
set MAVEN_REPO=C:\java\apache-maven-3.6.3\repo
set PARENT_POM_SOURCE=%MAVEN_REPO%\com\wldos\%PARENT_MODULE%\%VERSION%\%PARENT_BASE_NAME%.pom
if not exist "%PARENT_POM_SOURCE%" (
    echo Error: Parent POM not found at %PARENT_POM_SOURCE%
    echo Please run 'mvn install -N' in the root directory first.
    cd ..
    pause
    exit /b 1
)
copy "%PARENT_POM_SOURCE%" "%PARENT_PATH%\%PARENT_BASE_NAME%.pom"

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
REM 对所有文件生成校验和（jar、pom、sources.jar、javadoc.jar）
for %%f in (%BASE_NAME%.jar %BASE_NAME%.pom) do (
    if exist "%%f" (
        echo Generating checksums for %%f
        powershell -Command "$md5 = Get-FileHash '%%f' -Algorithm MD5; $md5.Hash.ToLower() | Set-Content '%%f.md5' -NoNewline"
        powershell -Command "$sha1 = Get-FileHash '%%f' -Algorithm SHA1; $sha1.Hash.ToLower() | Set-Content '%%f.sha1' -NoNewline"
        powershell -Command "$sha512 = Get-FileHash '%%f' -Algorithm SHA512; $sha512.Hash.ToLower() | Set-Content '%%f.sha512' -NoNewline"
    )
)

if exist "%BASE_NAME%-sources.jar" (
    echo Generating checksums for %BASE_NAME%-sources.jar
    powershell -Command "$md5 = Get-FileHash '%BASE_NAME%-sources.jar' -Algorithm MD5; $md5.Hash.ToLower() | Set-Content '%BASE_NAME%-sources.jar.md5' -NoNewline"
    powershell -Command "$sha1 = Get-FileHash '%BASE_NAME%-sources.jar' -Algorithm SHA1; $sha1.Hash.ToLower() | Set-Content '%BASE_NAME%-sources.jar.sha1' -NoNewline"
    powershell -Command "$sha512 = Get-FileHash '%BASE_NAME%-sources.jar' -Algorithm SHA512; $sha512.Hash.ToLower() | Set-Content '%BASE_NAME%-sources.jar.sha512' -NoNewline"
)
if exist "%BASE_NAME%-javadoc.jar" (
    echo Generating checksums for %BASE_NAME%-javadoc.jar
    powershell -Command "$md5 = Get-FileHash '%BASE_NAME%-javadoc.jar' -Algorithm MD5; $md5.Hash.ToLower() | Set-Content '%BASE_NAME%-javadoc.jar.md5' -NoNewline"
    powershell -Command "$sha1 = Get-FileHash '%BASE_NAME%-javadoc.jar' -Algorithm SHA1; $sha1.Hash.ToLower() | Set-Content '%BASE_NAME%-javadoc.jar.sha1' -NoNewline"
    powershell -Command "$sha512 = Get-FileHash '%BASE_NAME%-javadoc.jar' -Algorithm SHA512; $sha512.Hash.ToLower() | Set-Content '%BASE_NAME%-javadoc.jar.sha512' -NoNewline"
)
popd

echo Generating checksums for parent pom...
pushd %PARENT_PATH%
powershell -Command "$md5 = Get-FileHash '%PARENT_BASE_NAME%.pom' -Algorithm MD5; $md5.Hash.ToLower() | Set-Content '%PARENT_BASE_NAME%.pom.md5' -NoNewline"
powershell -Command "$sha1 = Get-FileHash '%PARENT_BASE_NAME%.pom' -Algorithm SHA1; $sha1.Hash.ToLower() | Set-Content '%PARENT_BASE_NAME%.pom.sha1' -NoNewline"
powershell -Command "$sha512 = Get-FileHash '%PARENT_BASE_NAME%.pom' -Algorithm SHA512; $sha512.Hash.ToLower() | Set-Content '%PARENT_BASE_NAME%.pom.sha512' -NoNewline"
popd

REM 生成GPG签名
echo Checking GPG installation...
REM 直接写死 GPG 路径
set "GPG_CMD=C:\Program Files (x86)\GnuPG\bin\gpg.exe"
echo Using GPG at: %GPG_CMD%

echo Getting password from settings.xml...
for /f "tokens=*" %%a in ('powershell -Command "$settings = [xml](Get-Content 'C:\java\apache-maven-3.6.3\conf\settings.xml'); $profile = $settings.settings.profiles.profile | Where-Object { $_.id -eq 'gpg' }; $profile.properties.'gpg.passphrase'"') do (
    set GPG_PASS=%%a
)

if "%GPG_PASS%"=="" (
    echo Warning: GPG passphrase not found in settings.xml
    echo Skipping GPG signatures...
) else (
    echo GPG passphrase found, starting signature generation...
        echo Generating GPG signatures for module files...
        pushd %ARTIFACT_PATH%
        REM 对所有必需的文件进行签名（jar、pom、sources.jar、javadoc.jar）
        for %%f in (%BASE_NAME%.jar %BASE_NAME%.pom) do (
            if exist "%%f" (
                echo Signing %%f...
                echo %GPG_PASS%| "%GPG_CMD%" --batch --yes --passphrase-fd 0 --detach-sign --armor "%%f"
                if errorlevel 1 (
                    echo ERROR: Failed to sign %%f
                    set SIGN_ERROR=1
                ) else (
                    if exist "%%f.asc" (
                        echo Successfully signed %%f - signature file created
                    ) else (
                        echo ERROR: Signature file %%f.asc not found after signing
                        set SIGN_ERROR=1
                    )
                )
            )
        )
        if exist "%BASE_NAME%-sources.jar" (
            echo Signing %BASE_NAME%-sources.jar...
            echo %GPG_PASS%| "%GPG_CMD%" --batch --yes --passphrase-fd 0 --detach-sign --armor "%BASE_NAME%-sources.jar"
            if errorlevel 1 (
                echo ERROR: Failed to sign %BASE_NAME%-sources.jar
                set SIGN_ERROR=1
            ) else (
                if exist "%BASE_NAME%-sources.jar.asc" (
                    echo Successfully signed %BASE_NAME%-sources.jar - signature file created
                ) else (
                    echo ERROR: Signature file %BASE_NAME%-sources.jar.asc not found after signing
                    set SIGN_ERROR=1
                )
            )
        ) else (
            echo ERROR: %BASE_NAME%-sources.jar not found - Maven Central requires sources.jar
            set SIGN_ERROR=1
        )
        if exist "%BASE_NAME%-javadoc.jar" (
            echo Signing %BASE_NAME%-javadoc.jar...
            echo %GPG_PASS%| "%GPG_CMD%" --batch --yes --passphrase-fd 0 --detach-sign --armor "%BASE_NAME%-javadoc.jar"
            if errorlevel 1 (
                echo ERROR: Failed to sign %BASE_NAME%-javadoc.jar
                set SIGN_ERROR=1
            ) else (
                if exist "%BASE_NAME%-javadoc.jar.asc" (
                    echo Successfully signed %BASE_NAME%-javadoc.jar - signature file created
                ) else (
                    echo ERROR: Signature file %BASE_NAME%-javadoc.jar.asc not found after signing
                    set SIGN_ERROR=1
                )
            )
        ) else (
            echo ERROR: %BASE_NAME%-javadoc.jar not found - Maven Central requires javadoc.jar
            echo Please check if javadoc generation succeeded in the Maven build output.
            set SIGN_ERROR=1
        )
        popd
        
        echo Generating GPG signature for parent pom...
        pushd %PARENT_PATH%
        if exist "%PARENT_BASE_NAME%.pom" (
            echo Signing %PARENT_BASE_NAME%.pom...
            echo %GPG_PASS%| "%GPG_CMD%" --batch --yes --passphrase-fd 0 --detach-sign --armor "%PARENT_BASE_NAME%.pom"
            if errorlevel 1 (
                echo ERROR: Failed to sign parent POM
                set SIGN_ERROR=1
            ) else (
                if exist "%PARENT_BASE_NAME%.pom.asc" (
                    echo Successfully signed %PARENT_BASE_NAME%.pom - signature file created
                ) else (
                    echo ERROR: Signature file %PARENT_BASE_NAME%.pom.asc not found after signing
                    set SIGN_ERROR=1
                )
            )
        ) else (
            echo ERROR: Parent POM not found for signing
            set SIGN_ERROR=1
        )
        popd
        
    if defined SIGN_ERROR (
        echo.
        echo WARNING: Some files failed to sign. Bundle may fail Central validation.
        echo Please check the error messages above.
    ) else (
        echo.
        echo All files successfully signed with GPG.
    )
)

REM 验证签名文件是否存在
echo.
echo Verifying signature files before creating bundle...
pushd %ARTIFACT_PATH%
set MISSING_SIG=0
for %%f in (%BASE_NAME%.jar %BASE_NAME%.pom %BASE_NAME%-sources.jar %BASE_NAME%-javadoc.jar) do (
    if exist "%%f" (
        if not exist "%%f.asc" (
            echo ERROR: Missing signature file: %%f.asc
            set MISSING_SIG=1
        ) else (
            echo Found signature: %%f.asc
        )
    )
)
popd
pushd %PARENT_PATH%
if exist "%PARENT_BASE_NAME%.pom" (
    if not exist "%PARENT_BASE_NAME%.pom.asc" (
        echo ERROR: Missing signature file: %PARENT_BASE_NAME%.pom.asc
        set MISSING_SIG=1
    ) else (
        echo Found signature: %PARENT_BASE_NAME%.pom.asc
    )
)
popd

if defined MISSING_SIG (
    echo.
    echo ERROR: Some signature files are missing. Please check GPG signing output above.
    echo Bundle will be created but will fail Maven Central validation.
    pause
)

REM 创建bundle
echo.
echo Creating bundle...
powershell Compress-Archive -Path "com" -DestinationPath "%BASE_NAME%-bundle.zip" -Force

REM 清理临时文件
echo Cleaning up...
rmdir /s /q com
cd ..
echo Done! Created target/%BASE_NAME%-bundle.zip
pause
