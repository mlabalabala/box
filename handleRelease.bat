@echo off
set timestamp=%date:~0,4%%date:~5,2%%date:~8,2%%time:~0,2%%time:~3,2%%time:~6,2%
REM 去空格
set "timestamp=%timestamp: =0%"

set source_directory=E:\Github_Project\box\Release
set destination_directory=%USERPROFILE%\Desktop
if not exist %destination_directory% (
    echo "目录不存在！"
    mkdir %destination_directory%
)
for %%i in (%source_directory%\*.apk) do (
    copy /Y "%%i" %destination_directory%
    move /Y "%%i" %source_directory%\app-release.apk
)

del %source_directory%\output-metadata.json