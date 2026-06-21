#!/bin/bash
# Mall Cloud 本地一键启动脚本（Git Bash / Linux）
# 用法: bash deploy/start-local.sh [--build] [--tail]
#   --build  : 先 mvn package 再启动
#   --tail   : 启动后 tail 所有日志（Ctrl+C 停）

set -e

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
LOG_DIR="$ROOT_DIR/logs"
mkdir -p "$LOG_DIR"

# 各服务端口（与 README 端口表一致）
declare -A PORTS=(
  [mall-gateway]=8080
  [mall-auth]=8021
  [mall-basic]=8022
  [mall-product]=8023
  [mall-marketing]=8024
  [mall-order]=8026
  [mall-pay]=8027
  [mall-recommend]=8028
  [mall-message]=8029
)

# 启动顺序（按依赖关系）
SERVICES=(
  mall-gateway
  mall-auth
  mall-basic
  mall-product
  mall-order
  mall-pay
  mall-marketing
  mall-recommend
  mall-message
)

# 需要显式指定端口的服务（端口由 Nacos 管理，本地未配置则默认 8080）
NEED_PORT=("mall-auth" "mall-basic" "mall-product" "mall-marketing")

# 阶段间隔（秒）
INTER_SERVICE=8
INTER_PHASE=12

# 编译
if [ "$1" == "--build" ]; then
  echo "========== 编译全部模块 =========="
  cd "$ROOT_DIR"
  mvn clean package -DskipTests -q
  echo "编译完成"
  echo ""
  shift
fi

# 清理旧日志
for svc in "${SERVICES[@]}"; do
  > "$LOG_DIR/$svc.log"
done

PID_FILE="$LOG_DIR/startup.pid"
> "$PID_FILE"

cleanup() {
  echo ""
  echo "========== 关闭所有服务 =========="
  if [ -f "$PID_FILE" ]; then
    while read pid; do
      kill "$pid" 2>/dev/null && echo "已停止 PID $pid"
    done < "$PID_FILE"
    rm -f "$PID_FILE"
  fi
  echo "全部已停止"
}
trap cleanup EXIT INT TERM

start_service() {
  local svc=$1
  local port=${PORTS[$svc]}
  local jar=$(find "$ROOT_DIR/$svc/target" -name "*.jar" -not -name "*-sources.jar" 2>/dev/null | head -1)

  if [ -z "$jar" ]; then
    echo "[$svc] ⚠️  未找到 jar，跳过"
    return
  fi

  local port_arg=""
  for need in "${NEED_PORT[@]}"; do
    if [ "$need" == "$svc" ]; then
      port_arg="--server.port=$port"
      break
    fi
  done

  echo "[$svc] 启动中 → 端口 $port"
  nohup java -jar "$jar" $port_arg > "$LOG_DIR/$svc.log" 2>&1 &
  local pid=$!
  echo $pid >> "$PID_FILE"
  sleep "$INTER_SERVICE"
}

echo "========== Mall Cloud 本地启动 =========="
echo "日志目录: $LOG_DIR"
echo "全部启动预计耗时: $((${#SERVICES[@]} * INTER_SERVICE))s"
echo ""

for svc in "${SERVICES[@]}"; do
  start_service "$svc"
done

echo ""
echo "========== 全部已启动 =========="
echo "Gateway:  http://localhost:8080"
echo "日志文件: $LOG_DIR/*.log"

# 可选 tail
if [ "$1" == "--tail" ]; then
  echo ""
  echo "按 Ctrl+C 停止所有服务"
  tail -f "$LOG_DIR/mall-gateway.log"
fi
