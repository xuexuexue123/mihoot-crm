-- 新租户库最小结构（PostgreSQL），供 TenantRegisterService 在 PG 上执行；完整业务表请后续用迁移工具或模板库补充
DROP TABLE IF EXISTS sys_user CASCADE;
DROP TABLE IF EXISTS sys_dept CASCADE;
DROP TABLE IF EXISTS sys_organization CASCADE;

CREATE TABLE sys_organization (
  organization_id varchar(100) NOT NULL,
  py_name varchar(64) DEFAULT '',
  py_label varchar(255) DEFAULT '',
  website varchar(255) DEFAULT '',
  tel varchar(30) DEFAULT '',
  address varchar(255) DEFAULT '',
  description varchar(255) DEFAULT '',
  create_by varchar(64) DEFAULT '',
  create_time timestamp NULL,
  update_by varchar(64) DEFAULT '',
  update_time timestamp NULL,
  uuid varchar(100) DEFAULT '',
  bank_name varchar(255) DEFAULT '',
  bank_account varchar(255) DEFAULT '',
  fapiao_delivery varchar(255) DEFAULT '',
  avatar varchar(255) DEFAULT '',
  larger_avatar varchar(255) DEFAULT '',
  PRIMARY KEY (organization_id)
);

CREATE TABLE sys_dept (
  dept_id bigint NOT NULL,
  parent_id bigint DEFAULT 0,
  ancestors varchar(50) DEFAULT '',
  dept_name varchar(30) DEFAULT '',
  order_num int DEFAULT 0,
  leader varchar(20) NULL,
  phone varchar(11) NULL,
  email varchar(50) NULL,
  status char(1) DEFAULT '0',
  del_flag char(1) DEFAULT '0',
  create_by varchar(64) DEFAULT '',
  create_time timestamp NULL,
  update_by varchar(64) DEFAULT '',
  update_time timestamp NULL,
  PRIMARY KEY (dept_id)
);

CREATE TABLE sys_user (
  user_id bigint NOT NULL,
  organization_id bigint NULL,
  dept_id bigint NULL,
  user_name varchar(30) NOT NULL,
  nick_name varchar(30) NOT NULL,
  user_type varchar(2) DEFAULT '00',
  email varchar(50) DEFAULT '',
  phonenumber varchar(11) DEFAULT '',
  sex char(1) DEFAULT '0',
  avatar varchar(100) DEFAULT '',
  password varchar(100) DEFAULT '',
  status char(1) DEFAULT '0',
  del_flag char(1) DEFAULT '0',
  login_ip varchar(128) DEFAULT '',
  login_date timestamp NULL,
  create_by varchar(64) DEFAULT '',
  create_time timestamp NULL,
  update_by varchar(64) DEFAULT '',
  update_time timestamp NULL,
  remark varchar(500) NULL,
  PRIMARY KEY (user_id)
);
