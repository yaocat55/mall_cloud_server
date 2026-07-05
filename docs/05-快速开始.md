# 快速开始

## 第零步：配置文件准备

> [!IMPORTANT]
> 出于安全考虑，所有服务的 `application.yml` 已被 `.gitignore` 排除，仓库中仅提供 `.template` 模板文件。
> **启动前必须将模板复制为实际配置文件：**

```bash
# 在项目根目录 mall_cloud_server/ 下执行以下命令，将模板复制为实际配置文件
for dir in mall-gateway mall-auth mall-basic mall-product mall-order mall-pay mall-marketing mall-recommend mall-message; do
  cp "$dir/src/main/resources/application.yml.template" "$dir/src/main/resources/application.yml"
done
```

然后编辑各服务的 `application.yml`，将模板中的占位符替换为你自己的实际配置：

| 占位符 | 说明 |
|--------|------|
| `your_mysql_password` | MySQL 数据库密码 |
| `your_redis_host` / `your_redis_password` | Redis 地址和密码 |
| `your_nacos_host` / `your_nacos_password` | Nacos 地址和密码 |
| `your_jwt_secret_here` | JWT 签名密钥（请使用足够长度的随机字符串） |
| `your_elasticsearch_password` | Elasticsearch 密码 |
| `your_rocketmq_host` | RocketMQ NameServer 地址 |
| `your_mongodb_host` / `your_mongodb_password` | MongoDB 地址和密码 |
| `your_minio_host` / `your_minio_secret_key` | MinIO 地址和密钥 |
| `your_alipay_app_id` / `your_alipay_private_key` / `your_alipay_public_key` | 支付宝沙箱应用配置 |
| `your_aliyun_access_key_id` / `your_aliyun_access_key_secret` | 阿里云 SMS AccessKey |
| `your_alipay_notify_url` | 支付宝异步通知回调地址 |

## 环境检查

| 依赖 | 版本要求 | 检查命令 |
|------|---------|---------|
| JDK | 17+ | `java -version` |
| Maven | 3.8+ | `mvn -v` |
| MySQL | 8.x | `mysql -u root -p -e "SELECT VERSION()"` |
| Nacos | 2.x | 浏览器打开 `http://<your-server-ip>:8848/nacos` |
| Redis | 6+ | `redis-cli -h <your-server-ip> -p 6379 PING` |
| RocketMQ | 5.x | NameServer `<your-server-ip>:9876` 可连通 |
| Elasticsearch | 7.17 | `curl http://<your-server-ip>:9200` |
| MongoDB | 5+ | `mongosh <your-server-ip>:27017 --eval "db.version()"` |
| MinIO | — | 浏览器打开 `http://<your-server-ip>:9002` |

## 初始化数据库

```sql
-- 1. 创建单库
CREATE DATABASE IF NOT EXISTS mall_auth    DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS mall_basic   DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS mall_product DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS mall_marketing DEFAULT CHARACTER SET utf8mb4;

-- 2. 创建分库（订单 8 库）
CREATE DATABASE IF NOT EXISTS mall_trade_0 DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS mall_trade_1 DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS mall_trade_2 DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS mall_trade_3 DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS mall_trade_4 DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS mall_trade_5 DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS mall_trade_6 DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS mall_trade_7 DEFAULT CHARACTER SET utf8mb4;

-- 3. 创建分库（消息 8 库）
CREATE DATABASE IF NOT EXISTS mall_message_0 DEFAULT CHARACTER SET utf8mb4;
-- ... 依此类推至 mall_message_7

-- 4. 创建分库（推荐 8 库）
CREATE DATABASE IF NOT EXISTS mall_recommend_0 DEFAULT CHARACTER SET utf8mb4;
-- ... 依此类推至 mall_recommend_7
```

```bash
# 5. 初始化分库分表（订单服务 —— 约 408KB 建表脚本）
#     在 MySQL 客户端中执行：
mysql -u root -p < mall-order/src/main/resources/sql/mall_trade_sharding.sql
```

> [!WARNING]
> 分库分表建表脚本非常大，务必用 `source` 或 `<` 重定向批量执行，**严禁逐表手动创建**。消息库和推荐库的建表 SQL 同样需要在对应库下执行。

## Nacos 配置确认

业务配置全部托管在 Nacos，本地只保留 bootstrap 骨架。启动前确认以下 Nacos 配置项存在且正确：

```
Namespace: mall
Group:     mall-cloud

配置列表（每个服务一个 YAML）：
  mall-auth-dev.yaml         mall-basic-dev.yaml
  mall-product-dev.yaml      mall-order-dev.yaml
  mall-pay-dev.yaml           mall-marketing-dev.yaml
  mall-recommend-dev.yaml    mall-message-dev.yaml
  mall-gateway-dev.yaml
```

> 至少需要保证各配置中的 **数据库连接串**、**Redis 地址**、**RocketMQ NameServer** 指向你的实际环境。

## 构建项目

```bash
# 在项目根目录 mall_cloud_server/ 下执行

# 方案 A：一次性全量构建（推荐首次）
mvn clean package -DskipTests

# 方案 B：仅构建修改过的模块（增量）
mvn clean package -DskipTests -pl mall-gateway -am

# 方案 C：IDEA 内构建
# 右侧 Maven 面板 → mall_cloud → Lifecycle → clean → package
```

构建产物位于各模块的 `target/` 目录下，如 `mall-gateway/target/mall-gateway.jar`。

## 启动服务

**启动必须遵循以下顺序，否则 Feign 客户端注册会出现找不到服务的报错。**

```
外部依赖（先确保以下中间件已启动）
  │
  ├── Nacos       → 必须第一个启动（服务注册/配置中心）
  ├── MySQL       → 所有数据库已初始化
  ├── Redis       → 缓存 + 雪花算法 workerId
  ├── RocketMQ    → 延迟消息（订单超时取消）
  ├── Elasticsearch → 商品/订单搜索
  └── MongoDB     → 基本服务依赖
        │
        ▼
微服务启动顺序（严格按以下顺序）
  │
  ├── ① mall-gateway     → 网关入口，路由转发（不依赖其他微服务）
  ├── ② mall-auth        → 认证服务，注册到 Nacos 为 mall-auth-api
  ├── ② mall-basic       → 基础服务，可和 auth 并行启动，注册为 mall-basic-api
  ├── ③ mall-product     → 商品服务，依赖 basic（Feign 调用 AreaFeignClient）
  ├── ④ mall-order       → 订单服务，依赖 product（Feign 调用 ProductFeignClient）
  ├── ④ mall-marketing   → 营销服务，依赖 product/auth/basic
  ├── ④ mall-pay         → 支付服务，依赖 order（Feign 调用 OrderFeignClient，无数据库）
  ├── ⑤ mall-recommend   → 推荐服务，依赖 product
  ├── ⑤ mall-message     → 消息推送，依赖 auth
  ├── ⑥ mall-admin-api   → 【BFF】管理后台聚合，依赖 auth/basic/product/order/marketing
  └── ⑥ mall-mobile-api  → 【BFF】移动端聚合，依赖 auth/basic/product/order/marketing/pay

最后启动 mall-admin-api & mall-mobile-api（BFF 服务）
```

### 方式 A：IDEA 启动（开发推荐）

每个服务模块下找到 `*Application.java` 主类，右键 → Run：

| 服务 | 主类 | 模块目录 |
|------|------|---------|
| Gateway | `GatewayApplication` | `mall-gateway` |
| Auth | `AuthApiApplication` | `mall-auth` |
| Basic | `BasicApiApplication` | `mall-basic` |
| Product | `ProductApiApplication` | `mall-product` |
| Order | `OrderApiApplication` | `mall-order` |
| Pay | `PayApiApplication` | `mall-pay` |
| Marketing | `MarketingApiApplication` | `mall-marketing` |
| Recommend | `RecommendApiApplication` | `mall-recommend` |
| Message | `MessageApplication` | `mall-message` |
| **Admin BFF** | **`AdminApiApplication`** | **`mall-admin-api`** |
| **Mobile BFF** | **`MobileApiApplication`** | **`mall-mobile-api`** |

> **IDEA 编译注意**：如果启动报 `-parameters` 相关错误，在 `File → Settings → Build, Execution, Deployment → Compiler → Java Compiler` 的 "Additional command line parameters" 中填入 `-parameters`。

> **IDEA Run Configurations 只显示部分服务**：右键根 `pom.xml` → Maven → Reload Project 即可刷新。

### 方式 B：Maven 命令行启动

```bash
# ───── 阶段 ①：编译依赖并启动 auth（测试单个服务时用） ─────
mvn compile -pl mall-auth -am          # 先编译依赖
mvn -pl mall-auth spring-boot:run      # 再启动

# ───── 阶段 ②：编译依赖并启动 basic ─────
mvn compile -pl mall-basic -am
mvn -pl mall-basic spring-boot:run

# ───── 阶段 ③：编译依赖并启动 product ─────
mvn compile -pl mall-product -am
mvn -pl mall-product spring-boot:run

# ───── 阶段 ④：编译依赖并启动 order/pay/marketing ─────
mvn compile -pl mall-order -am
mvn -pl mall-order spring-boot:run

# ───── 阶段 ⑤：编译依赖并启动 recommend/message ─────
mvn compile -pl mall-recommend -am
mvn -pl mall-recommend spring-boot:run
```

### 方式 C：打包后 JAR 启动（部署用）

```bash
mvn clean package -DskipTests

# 严格按启动顺序
java -jar mall-gateway/target/mall-gateway.jar &        # ① 网关
java -jar mall-auth/target/mall-auth.jar &               # ② 认证
java -jar mall-basic/target/mall-basic.jar &             # ② 基础
sleep 15                                                  # 等 auth/basic 注册完毕
java -jar mall-product/target/mall-product.jar &         # ③ 商品
sleep 10
java -jar mall-order/target/mall-order.jar &             # ④ 订单
java -jar mall-pay/target/mall-pay.jar &                 # ④ 支付
java -jar mall-marketing/target/mall-marketing.jar &     # ④ 营销
sleep 10
java -jar mall-recommend/target/mall-recommend.jar &     # ⑤ 推荐
java -jar mall-message/target/mall-message.jar &         # ⑤ 消息
```

### 方式 D：一键脚本启动（本地开发推荐）

编译 + 启动全部 9 个服务，一次搞定：

```bash
# 首次使用（先编译再启动）
bash deploy/start-local.sh --build

# 后续（只启动，跳过编译）
bash deploy/start-local.sh

# 启动后实时查看 Gateway 日志（Ctrl+C 停止所有）
bash deploy/start-local.sh --tail
```

**Windows 用户**：双击 `deploy\start-local.bat` 即可，首次会自动编译，每个服务运行在独立的 cmd 窗口中。

**一键停止：**

```bash
bash deploy/stop-local.sh        # Linux / Git Bash
deploy\stop-local.bat            # Windows（双击）
```

> 启动脚本会自动为 auth、basic、product、marketing 分配本地端口（8021–8024），避免端口冲突。日志输出在 `logs/` 目录下。

```bash
# 1. 检查 Nacos 注册状态
#    浏览器打开 http://<your-server-ip>:8848/nacos → 服务管理 → 服务列表
#    确认以下 9 个服务均在"健康实例"列表中：
#      mall-gateway / mall-auth-api / mall-basic-api / mall-product-api
#      mall-order-api / mall-pay-api / mall-marketing-api
#      mall-recommend-api / mall-message-api

# 2. 验证网关健康检查
curl http://localhost:8080/actuator/health

# 3. 获取短信验证码（白名单接口，无需 Token）
curl -X POST http://localhost:8080/api/auth/v1/web/user/getCode \
  -H "Content-Type: application/json" \
  -d '{"phone": "13800138000"}'

# 4. 验证需要鉴权的接口（会返回 401 说明鉴权链正常工作）
curl http://localhost:8080/api/product/v1/product/list

# 5. 测试 WebSocket 连接
#     浏览器控制台：
#     const ws = new WebSocket('ws://localhost:8080/api/message/ws');
#     ws.onopen = () => console.log('WebSocket 连接成功');
```

### BFF 启动说明

**最后启动 mall-admin-api & mall-mobile-api（BFF 服务）**。这两个服务依赖于下游所有业务微服务（auth、basic、product、order、marketing、pay），必须在它们全部注册到 Nacos 之后再启动。

### 端口总览

所有服务的端口统一在本地 `application.yml` 中通过 `server.port` 指定，不再依赖 Nacos 远程配置。

| 服务 | 端口 | 说明 |
|------|:----:|------|
| mall-gateway | 8080 | 网关统一入口 |
| **mall-admin-api** | **8090** | **【BFF】管理后台聚合** |
| **mall-mobile-api** | **8091** | **【BFF】移动端聚合** |
| mall-auth | 8021 | 用户认证与 RBAC 权限 |
| mall-basic | 8022 | 文件、短信、敏感词等基础服务 |
| mall-product | 8023 | 商品、分类与搜索 |
| mall-marketing | 8024 | 营销活动与优惠 |
| mall-order | 8026 | 订单与购物车 |
| mall-pay | 8027 | 支付与退款 |
| mall-recommend | 8028 | 商品推荐与热门 |
| mall-message | 8029 | 消息与 WebSocket |

### 验证

```bash
# 1. 检查 Nacos 注册状态
#    浏览器打开 http://<your-server-ip>:8848/nacos → 服务管理 → 服务列表
#    确认以下 9 个服务均在"健康实例"列表中：
#      mall-gateway / mall-auth-api / mall-basic-api / mall-product-api
#      mall-order-api / mall-pay-api / mall-marketing-api
#      mall-recommend-api / mall-message-api

# 2. 验证网关健康检查
curl http://localhost:8080/actuator/health

# 3. 获取短信验证码（白名单接口，无需 Token）
curl -X POST http://localhost:8080/api/auth/v1/web/user/getCode \
  -H "Content-Type: application/json" \
  -d '{"phone": "13800138000"}'

# 4. 验证需要鉴权的接口（会返回 401 说明鉴权链正常工作）
curl http://localhost:8080/api/product/v1/product/list

# 5. 测试 WebSocket 连接
#     浏览器控制台：
#     const ws = new WebSocket('ws://localhost:8080/api/message/ws');
#     ws.onopen = () => console.log('WebSocket 连接成功');
```
