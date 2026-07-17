#!/bin/bash
# Inventory（库存服务）启动脚本
# 用法: bash script/start-inventory.sh

BASE_DIR="$(cd "$(dirname "$0")/.." && pwd)"
PORT="${1:-8036}"

echo "======================================"
echo " Inventory 库存服务启动脚本"
echo " 端口: $PORT"
echo "======================================"

# 1. 打包
echo ""
echo "--- 1. 编译打包 ---"
cd "$BASE_DIR" && mvn package -pl mall-inventory -am -DskipTests -q
if [ $? -ne 0 ]; then
    echo -e "  \033[31m❌ 编译失败\033[0m"
    exit 1
fi
echo -e "  \033[32m✅ 编译成功\033[0m"

# 2. 杀掉旧进程
echo ""
echo "--- 2. 停止旧实例 ---"
OLD_PID=$(netstat -ano 2>/dev/null | grep ":$PORT " | grep LISTEN | awk '{print $5}')
if [ -n "$OLD_PID" ]; then
    kill "$OLD_PID" 2>/dev/null
    sleep 2
    echo -e "  \033[32m✅ 已停止旧进程 PID=$OLD_PID\033[0m"
else
    echo -e "  \033[33m⚠️  未检测到运行中的实例\033[0m"
fi

# 3. 启动
echo ""
echo "--- 3. 启动服务 ---"
nohup java -jar "$BASE_DIR/mall-inventory/target/mall-inventory-1.0.0.jar" \
    --server.port="$PORT" \
    > /tmp/inventory-startup.log 2>&1 &
PID=$!
echo -e "  \033[32m✅ 服务已启动 (PID=$PID)\033[0m"
echo ""

# 4. 等待启动
echo "--- 4. 等待启动完成 ---"
for i in $(seq 1 30); do
    sleep 1
    STATUS=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:$PORT/v1/inventory/1" 2>/dev/null)
    if [ "$STATUS" = "200" ] || [ "$STATUS" = "401" ]; then
        echo -e "  \033[32m✅ 服务启动完成，状态码: $STATUS\033[0m"
        break
    fi
    if [ "$i" -eq 30 ]; then
        echo -e "  \033[31m❌ 启动超时\033[0m"
        echo "日志:"
        tail -20 /tmp/inventory-startup.log
        exit 1
    fi
    echo -n "."
done

echo ""
echo "======================================"
echo -e " \033[32m🎉 库存服务启动成功!\033[0m"
echo " 地址: http://localhost:$PORT"
echo " 健康: http://localhost:$PORT/v1/inventory/1"
echo "======================================"
