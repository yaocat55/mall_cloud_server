@echo off
chcp 65001 >nul
REM Mall Cloud 本地一键启动脚本（Windows）
REM 双击运行，或 cmd 中执行: deploy\start-local.bat

set ROOT_DIR=%~dp0..
set LOG_DIR=%ROOT_DIR%\logs
if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"

REM 各服务端口
set GW_PORT=8080
set AUTH_PORT=8021
set BASIC_PORT=8022
set PRODUCT_PORT=8023
set MARKETING_PORT=8024
set ORDER_PORT=8026
set PAY_PORT=8027
set RECOMMEND_PORT=8028
set MESSAGE_PORT=8029

REM 服务 jar 路径
set GW_JAR=%ROOT_DIR%\mall-gateway\target\mall-gateway.jar
set AUTH_JAR=%ROOT_DIR%\mall-auth\target\mall-auth.jar
set BASIC_JAR=%ROOT_DIR%\mall-basic\target\mall-basic.jar
set PRODUCT_JAR=%ROOT_DIR%\mall-product\target\mall-product.jar
set ORDER_JAR=%ROOT_DIR%\mall-order\target\mall-order.jar
set PAY_JAR=%ROOT_DIR%\mall-pay\target\mall-pay.jar
set MARKETING_JAR=%ROOT_DIR%\mall-marketing\target\mall-marketing.jar
set RECOMMEND_JAR=%ROOT_DIR%\mall-recommend\target\mall-recommend.jar
set MESSAGE_JAR=%ROOT_DIR%\mall-message\target\mall-message.jar

echo ========== Mall Cloud 本地启动 ==========

REM 检查是否已编译
if not exist "%GW_JAR%" (
    echo [INFO] 检测到未编译，开始编译...
    cd /d "%ROOT_DIR%"
    call mvn clean package -DskipTests -q
    if errorlevel 1 (
        echo [ERROR] 编译失败，请检查错误信息
        pause
        exit /b 1
    )
    echo [INFO] 编译完成
)

REM 清理旧日志
for %%s in (mall-gateway mall-auth mall-basic mall-product mall-order mall-pay mall-marketing mall-recommend mall-message) do (
    type nul > "%LOG_DIR%\%%s.log" 2>nul
)

echo 日志目录: %LOG_DIR%

REM 启动顺序：按依赖关系
echo [1/9] mall-gateway → %GW_PORT%
start "mall-gateway" cmd /c java -jar "%GW_JAR%" --server.port=%GW_PORT% ^>"%LOG_DIR%\mall-gateway.log" 2^>&1
timeout /t 8 /nobreak >nul

echo [2/9] mall-auth → %AUTH_PORT%
start "mall-auth" cmd /c java -jar "%AUTH_JAR%" --server.port=%AUTH_PORT% ^>"%LOG_DIR%\mall-auth.log" 2^>&1
timeout /t 8 /nobreak >nul

echo [3/9] mall-basic → %BASIC_PORT%
start "mall-basic" cmd /c java -jar "%BASIC_JAR%" --server.port=%BASIC_PORT% ^>"%LOG_DIR%\mall-basic.log" 2^>&1
timeout /t 8 /nobreak >nul

echo [4/9] mall-product → %PRODUCT_PORT%
start "mall-product" cmd /c java -jar "%PRODUCT_JAR%" --server.port=%PRODUCT_PORT% ^>"%LOG_DIR%\mall-product.log" 2^>&1
timeout /t 8 /nobreak >nul

echo [5/9] mall-order → %ORDER_PORT%
start "mall-order" cmd /c java -jar "%ORDER_JAR%" --server.port=%ORDER_PORT% ^>"%LOG_DIR%\mall-order.log" 2^>&1
timeout /t 8 /nobreak >nul

echo [6/9] mall-pay → %PAY_PORT%
start "mall-pay" cmd /c java -jar "%PAY_JAR%" --server.port=%PAY_PORT% ^>"%LOG_DIR%\mall-pay.log" 2^>&1
timeout /t 8 /nobreak >nul

echo [7/9] mall-marketing → %MARKETING_PORT%
start "mall-marketing" cmd /c java -jar "%MARKETING_JAR%" --server.port=%MARKETING_PORT% ^>"%LOG_DIR%\mall-marketing.log" 2^>&1
timeout /t 8 /nobreak >nul

echo [8/9] mall-recommend → %RECOMMEND_PORT%
start "mall-recommend" cmd /c java -jar "%RECOMMEND_JAR%" --server.port=%RECOMMEND_PORT% ^>"%LOG_DIR%\mall-recommend.log" 2^>&1
timeout /t 8 /nobreak >nul

echo [9/9] mall-message → %MESSAGE_PORT%
start "mall-message" cmd /c java -jar "%MESSAGE_JAR%" --server.port=%MESSAGE_PORT% ^>"%LOG_DIR%\mall-message.log" 2^>&1

echo.
echo ========== 全部已启动 ==========
echo Gateway: http://localhost:8080
echo 日志路径: %LOG_DIR%
echo 各服务运行在独立的 cmd 窗口中，关闭窗口即可停止服务。
echo.
pause
