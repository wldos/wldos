@echo off
setlocal EnableDelayedExpansion

REM 从pom.xml获取版本号和模块名
echo Getting version and module name from pom.xml...
for /f "tokens=*" %%a in ('powershell -Command "$pom = [xml](Get-Content pom.xml); $pom.project.parent.version"') do (
    set VERSION=%%a
)
for /f "tokens=*" %%a in ('powershell -Command "$pom = [xml](Get-Content pom.xml); $pom.project.artifactId"') do (
    set MODULE=%%a
)

echo Version: %VERSION%
echo Module: %MODULE%
set BASE_NAME=%MODULE%-%VERSION%

REM 检查bundle文件是否存在
if not exist "target\%BASE_NAME%-bundle.zip" (
    echo Error: target\%BASE_NAME%-bundle.zip not found!
    pause
    exit /b 1
)

REM 从Maven配置目录的settings.xml读取Sonatype认证信息
echo Getting Sonatype credentials from settings.xml...
for /f "tokens=*" %%a in ('powershell -Command "$settings = [xml](Get-Content 'C:\java\apache-maven-3.5.3\conf\settings.xml'); $server = $settings.settings.servers.server | Where-Object { $_.id -eq 'ossrh' }; Write-Host $server.username"') do (
    set SONATYPE_USER=%%a
)
for /f "tokens=*" %%a in ('powershell -Command "$settings = [xml](Get-Content 'C:\java\apache-maven-3.5.3\conf\settings.xml'); $server = $settings.settings.servers.server | Where-Object { $_.id -eq 'ossrh' }; Write-Host $server.password"') do (
    set SONATYPE_TOKEN=%%a
)

if "%SONATYPE_USER%"=="" (
    echo Error: Could not get Sonatype username from settings.xml
    pause
    exit /b 1
)

if "%SONATYPE_TOKEN%"=="" (
    echo Error: Could not get Sonatype token from settings.xml
    pause
    exit /b 1
)

REM 生成认证token
echo Step 1: Generating authentication token...
for /f "tokens=*" %%a in ('powershell -Command "[Convert]::ToBase64String([Text.Encoding]::UTF8.GetBytes('%SONATYPE_USER%:%SONATYPE_TOKEN%'))"') do (
    set AUTH_TOKEN=%%a
)

REM 上传到Sonatype
echo Step 2: Uploading to Sonatype...
cd target
curl --request POST ^
     --header "Authorization: Bearer %AUTH_TOKEN%" ^
     --form "bundle=@%BASE_NAME%-bundle.zip" ^
     https://central.sonatype.com/api/v1/publisher/upload > response.json

REM 检查上传响应
for /f "tokens=*" %%a in ('powershell -Command "if ((Get-Content 'response.json' | ConvertFrom-Json).id) { exit 0 } else { exit 1 }"') do (
    if errorlevel 1 (
        echo Upload failed! Response:
        type response.json
        cd ..
        pause
        exit /b 1
    )
)

REM 获取部署ID
echo Step 3: Getting deployment ID...
for /f "tokens=*" %%a in ('powershell -Command "(Get-Content 'response.json' | ConvertFrom-Json).id"') do (
    set DEPLOY_ID=%%a
)

REM 检查部署状态
echo Step 4: Checking deployment status...
set RETRY_COUNT=0
:check_status
curl --request GET ^
     --header "Authorization: Bearer %AUTH_TOKEN%" ^
     "https://central.sonatype.com/api/v1/publisher/status?id=%DEPLOY_ID%" > status.json

for /f "tokens=*" %%a in ('powershell -Command "$status = Get-Content 'status.json' | ConvertFrom-Json; Write-Host 'Status: ' $status.status; if ($status.status -eq 'PUBLISHED') { exit 0 } elseif ($status.status -eq 'FAILED') { Write-Host 'Validation Errors:'; $status.validationErrors | ForEach-Object { Write-Host ' -' $_.message }; exit 1 } else { exit 2 }"') do (
    REM 这里不需要做任何事
)

if errorlevel 2 (
    echo Deployment still in progress, waiting...
    set /a RETRY_COUNT+=1
    if !RETRY_COUNT! gtr 20 (
        echo Deployment timeout after 10 minutes
        cd ..
        pause
        exit /b 1
    )
    timeout /t 30 >nul
    goto check_status
) else if errorlevel 1 (
    echo Deployment failed! Details:
    type status.json
    cd ..
    pause
    exit /b 1
) else (
    echo Deployment successful!
)

del response.json status.json 2>nul
cd ..
echo Done!
pause