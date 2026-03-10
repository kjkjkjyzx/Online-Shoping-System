# 网上购物系统 (Online Shopping System)

一个基于 Spring Boot 3 + Vue 3 的全栈网上购物系统，支持用户、商家、管理员三种角色。

## 技术栈

### 后端
- **框架**: Spring Boot 3.2.5
- **ORM**: MyBatis-Plus 3.5.5
- **数据库**: MySQL 8.0
- **连接池**: Druid
- **缓存**: Redis
- **安全**: JWT + Spring Security Crypto
- **文档**: Knife4j (OpenAPI 3)
- **其他**: Lombok, Hutool

### 前端
- **框架**: Vue 3.3.4
- **UI 库**: Element Plus 2.3.14
- **路由**: Vue Router 4.2.4
- **状态管理**: Pinia 2.1.6
- **HTTP 客户端**: Axios 1.5.0
- **图表**: ECharts 6.0.0
- **构建工具**: Vite 4.4.9

## 系统功能

### 用户角色
- **普通用户 (role=1)**
  - 用户注册/登录
  - 浏览商品
  - 购物车管理
  - 订单管理
  - 个人信息管理

- **商家 (role=2)**
  - 商品管理（增删改查）
  - 订单管理（处理订单）
  - 商家资料管理

- **管理员 (role=0)**
  - 用户管理
  - 商品管理
  - 订单管理
  - 分类管理
  - 数据统计（Dashboard）

## 项目结构

```
Online-Shoping-System/
├── backend/                      # 后端项目
│   ├── src/main/java/com/shop/
│   │   ├── ShoppingApplication.java    # 启动类
│   │   ├── config/                     # 配置类
│   │   │   ├── CorsConfig.java         # 跨域配置
│   │   │   ├── JwtProperties.java      # JWT 配置
│   │   │   ├── MybatisPlusConfig.java  # MyBatis-Plus 配置
│   │   │   ├── RedisConfig.java        # Redis 配置
│   │   │   ├── SwaggerConfig.java      # API 文档配置
│   │   │   └── WebConfig.java          # Web 配置
│   │   ├── controller/                 # 控制器
│   │   │   ├── UserController.java     # 用户接口
│   │   │   ├── ProductController.java  # 商品接口
│   │   │   ├── OrderController.java    # 订单接口
│   │   │   ├── CartController.java     # 购物车接口
│   │   │   └── CategoryController.java # 分类接口
│   │   ├── service/                    # 服务层
│   │   │   └── impl/                   # 服务实现
│   │   ├── mapper/                     # 数据访问层
│   │   ├── entity/                     # 实体类
│   │   ├── common/                     # 公共类
│   │   │   ├── Result.java             # 统一响应
│   │   │   ├── BusinessException.java  # 业务异常
│   │   │   └── RoleEnum.java           # 角色枚举
│   │   ├── interceptor/                # 拦截器
│   │   ├── utils/                      # 工具类
│   │   │   └── JwtUtil.java            # JWT 工具
│   │   ├── vo/                         # 视图对象
│   │   └── runner/                     # 启动器
│   ├── src/main/resources/
│   │   ├── application.yml             # 主配置
│   │   ├── application-dev.yml         # 开发环境
│   │   └── application-prod.yml        # 生产环境
│   └── pom.xml                         # Maven 配置
│
└── frontend/                       # 前端项目
    ├── src/
    │   ├── api/                    # API 接口
    │   ├── components/             # 组件
    │   ├── router/                 # 路由配置
    │   ├── store/                  # 状态管理
    │   ├── utils/                  # 工具函数
    │   ├── views/                  # 页面视图
    │   │   ├── Admin/              # 管理员页面
    │   │   ├── Merchant/           # 商家页面
    │   │   ├── User/               # 用户页面
    │   │   ├── Product/            # 商品页面
    │   │   ├── Cart/               # 购物车页面
    │   │   └── Order/              # 订单页面
    │   ├── App.vue
    │   └── main.js
    ├── package.json
    └── vite.config.js
```

## 快速开始

### 环境要求
- JDK 21+
- Node.js 16+
- MySQL 8.0+
- Redis

### 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE shopping_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 修改 `backend/src/main/resources/application-dev.yml` 中的数据库连接配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/shopping_db?...
    username: your_username
    password: your_password
```

### 后端启动

```bash
cd backend

# 使用 Maven 构建并运行
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/online-shopping-system-1.0.0.jar
```

后端服务默认运行在：`http://localhost:8080/api`

API 文档地址：`http://localhost:8080/api/doc.html`

### 前端启动

```bash
cd frontend

# 安装依赖
npm install

# 开发模式
npm run dev

# 构建生产版本
npm run build
```

前端服务默认运行在：`http://localhost:5173`

## 主要 API 接口

| 模块 | 接口路径 | 说明 |
|------|----------|------|
| 用户 | `/api/users/*` | 注册、登录、信息管理 |
| 商品 | `/api/products/*` | 商品 CRUD、搜索 |
| 订单 | `/api/orders/*` | 订单创建、查询、管理 |
| 购物车 | `/api/cart/*` | 购物车管理 |
| 分类 | `/api/categories/*` | 商品分类管理 |

## 角色权限说明

| 角色 | 值 | 权限说明 |
|------|-----|----------|
| 管理员 | 0 | 全部权限 |
| 普通用户 | 1 | 基础购物功能 |
| 商家 | 2 | 商品和订单管理 |

## 配置说明

### JWT 配置
```yaml
jwt:
  expiration: 86400000  # Token 有效期（毫秒）
  header: Authorization
  prefix: Bearer
```

### 文件上传
- 最大文件大小：10MB
- 最大请求大小：10MB

## 开发团队

本项目为网上购物系统演示项目。

## License

MIT
