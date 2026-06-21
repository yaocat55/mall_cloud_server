@echo off
REM Mall Cloud 一键停止脚本
echo ========== Stopping Mall Cloud services ==========
echo Killing all mall-* java processes...
wmic process where "commandline like '%%mall-%%' and name='java.exe'" delete >nul 2>&1
echo ========== All services stopped ==========
pause
