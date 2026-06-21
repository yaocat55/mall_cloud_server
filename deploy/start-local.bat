@echo off
REM Mall Cloud 本地一键启动脚本（Windows）

set ROOT_DIR=%~dp0..
set LOG_DIR=%ROOT_DIR%\logs
if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"

set GW_PORT=8080
set AUTH_PORT=8021
set BASIC_PORT=8022
set PRODUCT_PORT=8023
set MARKETING_PORT=8024
set ORDER_PORT=8026
set PAY_PORT=8027
set RECOMMEND_PORT=8028
set MESSAGE_PORT=8029

set GW_JAR=%ROOT_DIR%\mall-gateway\target\mall-gateway.jar
set AUTH_JAR=%ROOT_DIR%\mall-auth\target\mall-auth.jar
set BASIC_JAR=%ROOT_DIR%\mall-basic\target\mall-basic.jar
set PRODUCT_JAR=%ROOT_DIR%\mall-product\target\mall-product.jar
set ORDER_JAR=%ROOT_DIR%\mall-order\target\mall-order.jar
set PAY_JAR=%ROOT_DIR%\mall-pay\target\mall-pay.jar
set MARKETING_JAR=%ROOT_DIR%\mall-marketing\target\mall-marketing.jar
set RECOMMEND_JAR=%ROOT_DIR%\mall-recommend\target\mall-recommend.jar
set MESSAGE_JAR=%ROOT_DIR%\mall-message\target\mall-message.jar

echo ========== Mall Cloud Local Startup ==========

REM 检查是否已编译
if not exist "%GW_JAR%" (
    echo [INFO] Building project...
    cd /d "%ROOT_DIR%"
    REM 先编译全部模块（不打包），然后逐个打包有 main class 的服务
    call mvn compile -q
    if errorlevel 1 (
        echo [ERROR] Compile failed
        pause
        exit /b 1
    )
    call mvn package -DskipTests -q -pl mall-gateway,mall-auth,mall-basic,mall-product,mall-order,mall-pay,mall-marketing,mall-recommend,mall-message -am
    if errorlevel 1 (
        echo [ERROR] Package failed
        pause
        exit /b 1
    )
    echo [INFO] Build complete
)

echo Logs: %LOG_DIR%

echo [1/9] mall-gateway ^(port %GW_PORT%^)
start "mall-gateway" cmd /c java -jar "%GW_JAR%" --server.port=%GW_PORT% ^> "%LOG_DIR%\mall-gateway.log" 2^>^&1
timeout /t 8 /nobreak >nul

echo [2/9] mall-auth ^(port %AUTH_PORT%^)
start "mall-auth" cmd /c java -jar "%AUTH_JAR%" --server.port=%AUTH_PORT% ^> "%LOG_DIR%\mall-auth.log" 2^>^&1
timeout /t 8 /nobreak >nul

echo [3/9] mall-basic ^(port %BASIC_PORT%^)
start "mall-basic" cmd /c java -jar "%BASIC_JAR%" --server.port=%BASIC_PORT% ^> "%LOG_DIR%\mall-basic.log" 2^>^&1
timeout /t 8 /nobreak >nul

echo [4/9] mall-product ^(port %PRODUCT_PORT%^)
start "mall-product" cmd /c java -jar "%PRODUCT_JAR%" --server.port=%PRODUCT_PORT% ^> "%LOG_DIR%\mall-product.log" 2^>^&1
timeout /t 8 /nobreak >nul

echo [5/9] mall-order ^(port %ORDER_PORT%^)
start "mall-order" cmd /c java -jar "%ORDER_JAR%" --server.port=%ORDER_PORT% ^> "%LOG_DIR%\mall-order.log" 2^>^&1
timeout /t 8 /nobreak >nul

echo [6/9] mall-pay ^(port %PAY_PORT%^)
start "mall-pay" cmd /c java -jar "%PAY_JAR%" --server.port=%PAY_PORT% ^> "%LOG_DIR%\mall-pay.log" 2^>^&1
timeout /t 8 /nobreak >nul

echo [7/9] mall-marketing ^(port %MARKETING_PORT%^)
start "mall-marketing" cmd /c java -jar "%MARKETING_JAR%" --server.port=%MARKETING_PORT% ^> "%LOG_DIR%\mall-marketing.log" 2^>^&1
timeout /t 8 /nobreak >nul

echo [8/9] mall-recommend ^(port %RECOMMEND_PORT%^)
start "mall-recommend" cmd /c java -jar "%RECOMMEND_JAR%" --server.port=%RECOMMEND_PORT% ^> "%LOG_DIR%\mall-recommend.log" 2^>^&1
timeout /t 8 /nobreak >nul

echo [9/9] mall-message ^(port %MESSAGE_PORT%^)
start "mall-message" cmd /c java -jar "%MESSAGE_JAR%" --server.port=%MESSAGE_PORT% ^> "%LOG_DIR%\mall-message.log" 2^>^&1

echo.
echo ========== All services started ==========
echo Gateway: http://localhost:%GW_PORT%
echo Logs: %LOG_DIR%
echo Close each cmd window to stop the service.
pause
