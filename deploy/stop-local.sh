#!/bin/bash
# Mall Cloud 一键停止脚本
echo "========== Stopping Mall Cloud services =========="
pids=$(ps aux | grep 'mall-' | grep -v grep | awk '{print $2}')
if [ -n "$pids" ]; then
  kill $pids 2>/dev/null
  echo "Stopped PIDs: $pids"
else
  echo "No running mall services found"
fi
echo "========== All services stopped =========="
