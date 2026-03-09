# 网上购物网站系统

基于 Spring Boot + Vue 的网上购物网站系统

## 项目结构

```
online-shopping-system/
├── backend/              # 后端项目（Spring Boot）
│   ├── src/main/java/com/shop/
│   │   ├── controller/   # 控制器层
│   │   ├── service/      # 服务层
│   │   ├── mapper/       # 数据访问层
│   │   ├── entity/       # 实体类
│   │   ├── dto/          # 数据传输对象
│   │   ├── vo/           # 视图对象
│   │   ├── config/       # 配置类
│   │   ├── common/       # 公共类
│   │   ├── interceptor/  # 拦截器
│   │   ├── utils/        # 工具类
│   │   └── runner/       # 启动器
│   ├── src/main/resources/
│   │   ├── application.yml  # 配置文件
│   │   └── mapper/          # MyBatis 映射文件
│   └── pom.xml              # Maven 配置
│
├── frontend/             # 前端项目（Vue 3）
│   ├── src/
│   │   ├── api/          # API 接口
│   │   ├── assets/       # 静态资源
│   │   ├── components/   # 公共组件
│   │   ├── router/       # 路由配置
│   │   ├── store/        # 状态管理 (Pinia)
│   │   ├── utils/        # 工具函数
│   │   └── views/        # 页面组件
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
│
├── database/
│   └── schema.sql        # 数据库初始化脚本
│
└── docs/                 # 设计文档
    ├── 01-项目小组规约.md
    ├── 02-需求规约.md
    ├── 03-概要设计规约.md
    ├── 04-详细设计.md
    └── 05-项目架构日志.md
```

## 环境要求

- JDK 1.8+
- MySQL 8.0+
- Redis (可选，用于缓存)
- Node.js 16+
- Maven 3.6+

## 快速开始

### 1. 数据库初始化

方法一：使用命令行
```bash
mysql -u root -p < database/schema.sql
```

方法二：使用 Navicat 或 MySQL Workbench
- 打开 database/schema.sql 文件
- 执行 SQL 脚本

### 2. 启动后端

**使用 IDEA：**
1. 打开 IntelliJ IDEA
2. File → Open → 选择 `backend` 目录
3. 等待 Maven 依赖下载完成
4. 修改 `src/main/resources/application.yml` 中的数据库配置
5. 运行 `ShoppingApplication.java`

**配置文件说明：**
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/shopping_db
    username: root
    password: 你的密码
```

**访问 API 文档：**
启动后访问 http://localhost:8080/api/doc.html

### 3. 启动前端

**使用 WebStorm 或 VS Code：**
```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

**访问地址：** http://localhost:3000

## 功能模块

### 前台商城
- 首页：轮播图、热销商品推荐
- 商品列表：分类筛选、搜索、排序
- 商品详情：商品图片、规格选择、加入购物车
- 购物车：商品管理、数量修改、结算
- 订单管理：订单列表、订单详情、取消订单、确认收货
- 用户中心：个人信息管理、密码修改

### 管理后台
- 控制台：数据统计、销售趋势
- 商品管理：商品增删改查、上下架
- 订单管理：订单列表、发货处理
- 用户管理：用户列表、状态管理
- 分类管理：商品分类维护

## 技术栈

### 后端
| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7 | 核心框架 |
| MyBatis Plus | 3.5.3 | ORM 框架 |
| MySQL | 8.0 | 数据库 |
| Redis | - | 缓存 |
| JWT | 0.9.1 | 身份认证 |
| Knife4j | 3.0.3 | API 文档 |
| Lombok | - | 代码简化 |
| Hutool | 5.8.22 | 工具类库 |

### 前端
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.3 | 核心框架 |
| Vue Router | 4.2 | 路由管理 |
| Pinia | 2.1 | 状态管理 |
| Element Plus | 2.3 | UI 组件库 |
| Axios | 1.5 | HTTP 客户端 |
| Vite | 4.4 | 构建工具 |

## 默认账号

### 前台用户
| 用户名 | 密码 | 角色 |
|-------|------|------|
| admin | 123456 | 管理员 |
| user1 | 123456 | 普通用户 |
| user2 | 123456 | 普通用户 |

### 管理后台
访问地址：http://localhost:3000/admin
- 用户名：admin
- 密码：123456

## API 接口

### 用户模块
| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/api/users/register` | 用户注册 |
| POST | `/api/users/login` | 用户登录 |
| GET | `/api/users/{id}` | 获取用户信息 |
| PUT | `/api/users/{id}` | 更新用户信息 |

### 商品模块
| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/api/products` | 获取商品列表 |
| GET | `/api/products/{id}` | 获取商品详情 |
| GET | `/api/products/search` | 搜索商品 |
| GET | `/api/products/hot` | 获取热销商品 |
| POST | `/api/products` | 创建商品（管理员） |
| PUT | `/api/products/{id}` | 更新商品（管理员） |
| DELETE | `/api/products/{id}` | 删除商品（管理员） |

### 购物车模块
| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/api/cart` | 获取购物车列表 |
| POST | `/api/cart/items` | 添加到购物车 |
| PUT | `/api/cart/items/{id}` | 更新数量 |
| DELETE | `/api/cart/items/{id}` | 删除商品 |
| DELETE | `/api/cart` | 清空购物车 |

### 订单模块
| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/api/orders` | 获取订单列表 |
| GET | `/api/orders/{id}` | 获取订单详情 |
| POST | `/api/orders` | 创建订单 |
| PUT | `/api/orders/{id}/cancel` | 取消订单 |
| PUT | `/api/orders/{id}/confirm` | 确认收货 |

## 开发工具

- 后端：IntelliJ IDEA
- 前端：WebStorm / VS Code
- 数据库：MySQL Workbench / Navicat
- API 测试：Postman / Knife4j
- 版本控制：Git

## 常见问题

### 1. 后端启动失败
- 检查 MySQL 服务是否启动
- 检查数据库配置是否正确
- 检查端口 8080 是否被占用

### 2. 前端无法访问后端 API
- 检查 `vite.config.js` 中的代理配置
- 确认后端服务已启动
- 检查浏览器控制台是否有跨域错误

### 3. 数据库连接失败
- 确认 MySQL 用户名密码正确
- 确认数据库 `shopping_db` 已创建
- 检查 MySQL 版本兼容性

## 项目文档

详细设计文档位于 `docs/` 目录：
- **项目小组规约.md** - 团队分工、命名规范、沟通机制
- **需求规约.md** - 用例图、功能需求分析
- **概要设计规约.md** - 体系结构、接口设计、数据库设计
- **详细设计.md** - 类图、顺序图、状态图
- **项目架构日志.md** - 开发日志、AI 辅助记录

## 许可证

本项目仅供学习和交流使用。

---

**开发日期：** 2026 年 3 月
**适用专业：** 计算机科学与技术、数据科学与大数据技术
**实训课程：** 综合实践能力创新实训 3
