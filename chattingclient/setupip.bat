@ECHO OFF
SET IPV4_PATH=%~dp0app\src\main\assets\my_ipv4.json
break>"%IPV4_PATH%"
FOR /F "tokens=1,2,3 delims=:" %%a IN ('IPCONFIG ^| findstr IPv4') DO (
    SET MY_IPV4=%%b
)
SET MY_IPV4=%MY_IPV4: =%
ECHO { >> "%IPV4_PATH%"
ECHO    "ipv4": "%MY_IPV4%" >> "%IPV4_PATH%"
ECHO } >> "%IPV4_PATH%"
ECHO %MY_IPV4%