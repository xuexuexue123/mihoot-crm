# MihootCRM 系统架构说明书

## 1. 系统概述

MihootCRM 的后端基于 RuoYi 框架，版本号 3.8.4，系统版本 1.0.4。本仓库只包含后端。界面由独立的 React 应用提供，不再使用 RuoYi 自带的 `ruoyi-ui`（Vue.js）。系统集成了客户关系管理(CRM)、工作流引擎(Flowable)、智能决策引擎(Drools)等模块。

### 1.1 系统名称与版本
- **项目名称**: MihootCRM
- **框架版本**: RuoYi 3.8.4
- **系统版本**: 1.0.4
- **版权年份**: 2026

### 1.2 技术栈概览

| 层级 | 技术选择 |
|------|----------|
| 后端框架 | Spring Boot 2.5.14 |
| 工作流引擎 | Flowable 6.7.2 |
| 规则引擎 | Drools 7.6.0 |
| 状态机 | Spring StateMachine 3.2.0 |
| ORM框架 | MyBatis + MyBatis-Plus 3.4.0 |
| 数据库连接池 | Druid 1.2.14 |
| 数据库 | PostgreSQL 14（JDBC：`org.postgresql.Driver`） |
| 向量数据库 | pgvector（与业务库同实例，`vector` 扩展） |
| 缓存 | Redis (Lettuce) |
| 前端 | 独立 React 应用（不在本仓库；Next.js + React） |
| 代码生成 | Velocity 2.3 |
| 表达式引擎 | Aviator 5.3.3 |

---

## 2. 系统架构图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              客户端层 (Client Layer)                          │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │              独立 React 应用（仓库外，非 ruoyi-ui）                 │    │
│  │   - React + Next.js                    - 通过 REST API 访问后端      │    │
│  │   - 桌面浏览器                          - 不随本仓库发布              │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────────────┘
                                       │
                                       │ HTTP/HTTPS (REST API)
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              应用层 (Application Layer)                      │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                      ruoyi-admin (Web服务入口)                       │    │
│  │   - Swagger API文档                        - 登录认证                │    │
│  │   - 请求拦截/响应处理                      - 文件上传/下载            │    │
│  │   - XSS安全过滤                           - 验证码 (Kaptcha)          │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────────────┘
                                       │
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              业务模块层 (Business Module Layer)                │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐            │
│  │   CRM模块   │ │ Flowable   │ │   系统模块   │ │  租户模块   │            │
│  │  客户管理   │ │  工作流引擎 │ │  用户/角色   │ │  多租户管理 │            │
│  │  销售流程   │ │  流程设计   │ │  菜单权限   │ │  数据隔离   │            │
│  │  合同管理   │ │  任务调度   │ │  字典管理   │ │             │            │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘            │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐                            │
│  │  定时任务   │ │ 代码生成    │ │  规则引擎   │                            │
│  │  Quartz    │ │ Generator  │ │  Drools    │                            │
│  │  调度管理   │ │  模板引擎   │ │  决策表     │                            │
│  └─────────────┘ └─────────────┘ └─────────────┘                            │
└─────────────────────────────────────────────────────────────────────────────┘
                                       │
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              框架支撑层 (Framework Layer)                     │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                      ruoyi-framework (框架核心)                      │    │
│  │   - Spring MVC/boot                    - AOP切面                    │    │
│  │   - Druid数据库连接池                   - 缓存管理                   │    │
│  │   - JWT Token认证                       - 安全加密                   │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                      ruoyi-common (通用工具)                         │    │
│  │   - JSON处理(Fastjson2)               - 文件操作                     │    │
│  │   - Excel导入/导出(POI)               - HTTP客户端                   │    │
│  │   - 验证码                             - 邮件/短信                    │    │
│  │   - AI集成(OpenAI/DeepSeek)           - OCR识别                     │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────────────┘
                                       │
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              数据层 (Data Layer)                              │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                    PostgreSQL + pgvector                            │    │
│  │   - 主库与按库隔离的租户库               - Flowable 流程表            │    │
│  │   - 业务表 / 系统表                      - vector(1024) + HNSW 检索   │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                         Redis Cache                                 │    │
│  │   - Token缓存                            - 会话管理                  │    │
│  │   - 限流/计数器                         - 状态机持久化              │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. 模块架构

### 3.1 Maven 多模块结构

```
ruoyi (Parent POM)
├── ruoyi-admin          # Web服务入口
├── ruoyi-framework     # 框架核心
├── ruoyi-system        # 系统模块
├── ruoyi-common        # 通用工具
├── ruoyi-quartz        # 定时任务
├── ruoyi-generator     # 代码生成
├── ruoyi-flowable      # 流程引擎
├── ruoyi-tenant        # 多租户
└── ruoyi-crm           # 客户关系管理
```

### 3.2 模块依赖关系

```
ruoyi-admin
    ├── ruoyi-framework
    │   ├── ruoyi-system
    │   │   └── ruoyi-common
    │   └── ruoyi-tenant
    │       └── ruoyi-common
    ├── ruoyi-quartz
    │   └── ruoyi-common
    ├── ruoyi-generator
    │   └── ruoyi-common
    ├── ruoyi-flowable
    │   ├── ruoyi-framework
    │   ├── ruoyi-system
    │   └── ruoyi-common
    └── ruoyi-crm
        ├── ruoyi-framework
        ├── ruoyi-system
        ├── ruoyi-common
        └── ruoyi-flowable
```

### 3.3 各模块职责

#### 3.3.1 ruoyi-admin (Web服务入口)
- 负责启动Spring Boot应用
- 配置Web过滤器(XSS、重复请求)
- 管理Swagger API文档
- 处理文件上传/下载
- 端口: 9090

#### 3.3.2 ruoyi-framework (框架核心)
- 提供数据库连接池配置(Druid)
- 实现JWT Token认证
- 提供系统日志拦截器
- 验证码生成(Kaptcha)

#### 3.3.3 ruoyi-system (系统模块)
- 用户管理
- 角色管理
- 菜单管理
- 部门管理
- 岗位管理
- 字典管理
- 通知公告
- 系统日志

#### 3.3.4 ruoyi-common (通用工具)
- JSON序列化/反序列化
- 文件操作工具
- Excel导入/导出(Apache POI)
- HTTP客户端封装
- 邮件发送(Aliyun DM、Mailgun、Gmail)
- 短信发送(Aliyun SMS)
- OCR识别(Aliyun OCR)
- AI集成(OpenAI、DeepSeek、阿里百炼对话与 Embedding)

#### 3.3.5 ruoyi-quartz (定时任务)
- 定时任务调度
- 任务执行日志
- Cron表达式管理

#### 3.3.6 ruoyi-generator (代码生成)
- Velocity模板引擎
- 代码生成器配置
- MyBatis Mapper生成

#### 3.3.7 ruoyi-flowable (流程引擎)
- Flowable 6.7.2 集成
- 流程定义管理
- 任务管理
- 历史记录
- Spring StateMachine集成
- Aviator表达式引擎

#### 3.3.8 ruoyi-tenant (多租户)
- 数据库级多租户隔离
- 租户配置管理
- 数据过滤

#### 3.3.9 ruoyi-crm (客户关系管理)
核心业务模块，包含以下主要功能：
- 客户管理 (Household)
- 联系人管理 (Contact)
- 销售机会管理 (Opty)
- 合同管理 (Contract)
- 报价管理 (Quote)
- 预约管理 (Appointment)
- 任务管理 (Task)
- 票据管理 (Ticket)
- 工作流触发 (Workflow)
- 邮件营销 (BulkEmail)
- 转化历史 (Conversion History)
- 企业知识库 (Knowledge Base，文档切块后写入 pgvector)
- 渠道收件箱（企业微信、微信公众号、WhatsApp、Messenger、Webchat）

---

## 4. 前端架构

当前界面不是本仓库里的 `ruoyi-ui`。RuoYi 原有的 Vue.js 前端（Vue 2、Element UI、Vuex、Vue Router）已不再使用。

界面是单独实现的 **React** 应用，工程名 MihootCRM，与后端分仓库维护。它通过 HTTP/HTTPS 调用本系统 REST API（后端端口 9090）。开发服务默认在 `http://localhost:3000`。

### 4.1 技术选型

| 类别 | 技术 |
|------|------|
| UI | React 19 |
| 应用框架 | Next.js 16（App Router） |
| 语言 | TypeScript |
| 服务端状态 | TanStack Query |
| 客户端状态 | Zustand |
| 样式 | Tailwind CSS |
| 国际化 | next-intl |
| 与后端的关系 | 独立部署，只消费本仓库 API |

### 4.2 工程位置

前端源码不在本 Maven 工程中。主要目录包括 `app/`（路由页面）、`components/`、`features/`、`views/`、`stores/`、`hooks/` 与 `lib/`。流程设计数据在后端以 `reactflow_json`（jsonb）保存，供该 React 界面使用。

---

## 5. 数据库架构

当前系统的关系库是 **PostgreSQL**。向量检索不单独部署向量库，而是在同一 PostgreSQL 实例上启用 **pgvector** 扩展，用 `vector` 类型保存 Embedding。主库脚本见 `sql/master.sql`，租户库脚本见 `sql/tenant.sql`，二者都会执行 `CREATE EXTENSION IF NOT EXISTS vector`。运行中的库由 `pg_dump` 记录为 PostgreSQL 14.18。

### 5.1 数据库配置

主数据源由 `DataSourceConfigurer` 读取，驱动为 PostgreSQL JDBC。连接账号写在运行配置中，不入库。

```yaml
spring:
  datasource:
    driverClassName: org.postgresql.Driver
    druid:
      master:
        url: jdbc:postgresql://localhost:5432/<master_db>
        username: <username>
        password: <password>
```

连接池仍使用 Druid。应用启动时把主库注册为 `MASTER` / `default_db`，请求进入后按租户切换数据源。

### 5.2 多租户配置

租户隔离方式是**一租户一数据库**。`tenant.database.db-type` 默认值为 `postgresql`。新建租户时连接维护库 `postgres` 执行 `CREATE DATABASE`，再依次执行：

- `init-sql-script/tenant_bootstrap_postgresql_full.sql`
- `init-sql-script/tenant_seed_postgresql.sql`

```yaml
tenant:
  database:
    db-type: postgresql
    host: localhost
    port: 5432
    username: <username>
    password: <password>
  prefix: <tenant_db_prefix>
```

租户库 JDBC 地址形如 `jdbc:postgresql://{host}:{port}/{tenantDatabase}`。库名规范为小写，且须匹配 `[a-z][a-z0-9_]*`。

### 5.2.1 向量数据库（pgvector）

pgvector 作为本系统的向量数据库，与业务数据放在同一个 PostgreSQL 实例里。知识库入库脚本要求实例已安装 pgvector，HNSW 索引需要 **0.5.0 及以上**。

| 项 | 实现 |
|----|------|
| 扩展 | `CREATE EXTENSION IF NOT EXISTS vector` |
| 向量列 | `crm_kb_chunk.embedding vector(1024) NOT NULL` |
| 索引 | `USING hnsw (embedding vector_cosine_ops)` |
| 相似度 | 余弦距离运算符 `<=>`，得分 `1 - (embedding <=> query)` |
| 元数据 | `jsonb` |
| 写入 / 查询 | `CrmKbChunkMapper` 中 `CAST(? AS vector)` |

Embedding 由 `EmbeddingClient` 调用阿里百炼兼容接口生成，默认模型 `qwen3.7-text-embedding`，维度固定 **1024**，与 `vector(1024)` 绑定。入库与检索必须使用同一 provider、模型和维度。检索只命中已发布且索引状态为 `indexed` 的文档。

相关表：

| 表名 | 说明 |
|------|------|
| crm_kb_document | 知识库文档（Markdown、分类、索引状态） |
| crm_kb_chunk | 文档切块及其 pgvector 向量 |

### 5.3 核心业务表 (CRM模块)

| 表名 | 说明 |
|------|------|
| crm_household | 客户主表 |
| crm_household_member | 家庭成员 |
| crm_contactrelation | 联系人关系 |
| crm_opty | 销售机会 |
| crm_contract | 合同管理 |
| crm_quote | 报价单 |
| crm_appointment | 预约记录 |
| crm_task | 任务管理 |
| crm_ticket | 工单/票据 |
| crm_bulkemail | 邮件营销 |
| crm_email_messages | 邮件消息 |
| crm_conversion_history | 转化历史 |
| crm_workflow_trigger | 工作流触发器 |
| crm_product | 产品管理 |
| crm_partner | 合作伙伴 |
| crm_forecast | 销售预测 |
| crm_salesgoal | 销售目标 |
| crm_cashflow | 现金流 |

### 5.4 系统表

| 表名 | 说明 |
|------|------|
| sys_user | 用户表 |
| sys_role | 角色表 |
| sys_menu | 菜单表 |
| sys_dept | 部门表 |
| sys_post | 岗位表 |
| sys_dict_data | 字典数据 |
| sys_dict_type | 字典类型 |
| sys_notice | 通知公告 |
| sys_config | 系统配置 |
| sys_login_log | 登录日志 |
| sys_oper_log | 操作日志 |

### 5.5 Flowable流程表

Flowable 引擎会自动创建以下表：
- ACT_RE_* : 流程定义与部署
- ACT_RU_* : 运行时数据
- ACT_HI_* : 历史数据
- ACT_ID_* : 身份数据

---

## 6. 安全架构

### 6.1 认证机制
- **JWT Token认证**
  - Token 密钥由运行配置提供，不写入本文档
  - 请求头: Authorization

### 6.2 密码安全
- 密码最大错误次数: 5次
- 密码锁定时间: 10分钟

### 6.3 请求安全
- **XSS过滤**: 开启状态
- **重复请求过滤**: RepeatableFilter
- **验证码**: 字符型验证码

---

## 7. 第三方集成

### 7.1 AI集成

对话与 Embedding 的密钥放在运行配置或租户凭证表中，本文档不记录密钥。

| 用途 | 服务 | 说明 |
|------|------|------|
| 对话 | OpenAI、DeepSeek、阿里百炼 | 通用 AI 调用 |
| 知识库向量 | 阿里百炼 Embedding | 默认 `qwen3.7-text-embedding`，1024 维，结果写入 pgvector |

### 7.2 邮件服务

| 服务商 | 用途 |
|--------|------|
| Aliyun DM | 阿里云邮件推送 |
| Gmail API | Google邮件服务 |
| Mailgun | 第三方邮件服务 |

### 7.3 短信服务
- **Aliyun Dysms**: 阿里云短信服务

### 7.4 OCR服务
- **Aliyun OCR**: 文字识别服务

---

## 8. 部署架构

### 8.1 环境要求

| 组件 | 最低要求 |
|------|----------|
| Java | 1.8+ |
| Node.js | 20+（仅构建、运行独立 React 前端时需要） |
| PostgreSQL | 14+，并安装 pgvector（HNSW 需 0.5.0+） |
| Redis | 3.0+ |

### 8.2 端口配置

| 服务 | 端口 |
|------|------|
| 后端服务 | 9090 |
| React 前端（开发） | 3000 |
| PostgreSQL | 5432 |
| Redis | 6379 |

### 8.3 文件存储

上传文件目录由运行配置项 `ruoyi.profile` 指定。

---

## 9. 核心特性

### 9.1 工作流引擎 (Flowable)
- BPMN 2.0流程定义
- 流程设计器集成
- 任务分配与执行
- 历史记录查询
- 定时触发器

### 9.2 规则引擎 (Drools)
- 决策表支持
- 规则模板
- 业务规则动态管理

### 9.3 状态机 (Spring StateMachine)
- 销售流程状态管理
- 状态转换事件
- Redis持久化

### 9.4 表达式引擎 (Aviator)
- 动态表达式计算
- 业务规则表达式
- 数据验证

### 9.5 向量检索 (pgvector)
- 知识库文档切块后生成 1024 维 Embedding
- 向量与业务数据同库存储，使用 HNSW 余弦索引
- 按相似度召回已发布文档，供企业知识库 RAG 使用

---

## 10. 总结

MihootCRM 是一个功能完善、技术先进的企业级管理系统，采用了当前主流的微服务架构设计思想(单体应用模块化)。系统具备以下特点：

1. **模块化设计**: 各模块职责清晰，依赖关系明确
2. **前后端分离**: 独立 React 应用 + Spring Boot，本仓库不包含 Vue 版 ruoyi-ui
3. **多引擎集成**: Flowable工作流 + Drools规则 + StateMachine状态机
4. **多租户支持**: PostgreSQL 数据库级隔离
5. **向量能力**: 以 pgvector 作为向量数据库，支撑知识库语义检索
6. **AI能力集成**: 对话模型与 Embedding 写入同一 PostgreSQL 实例
7. **企业功能**: CRM、合同、任务、流程、渠道收件箱与知识库

---

*文档版本: 1.2*
*更新日期: 2026-10-19*
