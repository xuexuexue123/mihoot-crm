--
-- PostgreSQL database dump
--

-- Dumped from database version 14.18 (Homebrew)
-- Dumped by pg_dump version 17.0

-- Started on 2026-06-13 20:01:19 CST

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 5 (class 2615 OID 360062)
-- Name: public; Type: SCHEMA; Schema: -; Owner: mega_vXnFBG5i
--

-- *not* creating schema, since initdb creates it


--
-- TOC entry 6175 (class 0 OID 0)
-- Dependencies: 5
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: mega_vXnFBG5i
--

COMMENT ON SCHEMA public IS '';


--
-- TOC entry 1682 (class 1247 OID 370878)
-- Name: CredentialType; Type: TYPE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TYPE public."CredentialType" AS ENUM (
    'OPENAI',
    'ANTHROPIC',
    'GEMINI',
    'HEADER_AUTH',
    'BASIC_AUTH',
    'QWEN',
    'DEEPSEEK'
);


--
-- TOC entry 1685 (class 1247 OID 370892)
-- Name: EdgeType; Type: TYPE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TYPE public."EdgeType" AS ENUM (
    'default',
    'IF',
    'ELSE'
);


--
-- TOC entry 1688 (class 1247 OID 370900)
-- Name: ExecutionStatus; Type: TYPE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TYPE public."ExecutionStatus" AS ENUM (
    'RUNNING',
    'SUCCESS',
    'FAILED'
);


--
-- TOC entry 1691 (class 1247 OID 370908)
-- Name: LlmProvider; Type: TYPE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TYPE public."LlmProvider" AS ENUM (
    'qwen',
    'gemini'
);


--
-- TOC entry 1694 (class 1247 OID 370914)
-- Name: NodeType; Type: TYPE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TYPE public."NodeType" AS ENUM (
    'INITIAL',
    'MANUAL_TRIGGER',
    'GOOGLE_FORM_TRIGGER',
    'STRIPE_TRIGGER',
    'FORM_SUBMISSION_TRIGGER',
    'WEBHOOK_TRIGGER',
    'HTTP_REQUEST',
    'GEMINI',
    'OPENAI',
    'ANTHROPIC',
    'DISCORD',
    'SLACK',
    'IFELSE',
    'WHILE',
    'LOOP_OVER_ITEMS',
    'SETPROPERTY',
    'WAIT',
    'FILTER',
    'EXECUTE_COMMAND',
    'CODE',
    'DO_NOTHING',
    'MERGE',
    'SPLITOUT',
    'STICKY_NOTE',
    'EDIT_FIELDS',
    'SWITCH',
    'READ_WRITE_FILES',
    'CONVERT_TO_FILE',
    'DATA_TABLE',
    'REMOVE_DUPLICATES',
    'AGGREGATE',
    'AI_AGENT',
    'QWEN',
    'ALIOSS',
    'ALIIMAGEAI',
    'DOCX_TEMPLATER',
    'ALIOSS_FILE',
    'SHARED_IMPORT',
    'SF_EXPRESS',
    'DATE_AND_TIME',
    'LLM',
    'MCP_CLIENT',
    'CRM_RECORD_TRIGGER',
    'WECOM_KF',
    'WECOM_UP_MEDIA',
    'WECOM_GP_MSG',
    'WECOM_UP_IMG',
    'WECOM_UP_ATTACHMENT',
    'WECOM_SEND_MOMENT',
    'WECOM_TAG_EDITOR',
    'WECOM_TAG_LIST',
    'BROWSER_SESSION_START',
    'BROWSER_SESSION_END',
    'BROWSER_GOTO',
    'BROWSER_WAIT',
    'BROWSER_FORM_DISCOVER',
    'BROWSER_APPLY_PLAN',
    'BROWSER_CLICK',
    'BROWSER_SCREENSHOT',
    'BROWSER_ASSERT',
    'WEB_FORM_AUTOFILL',
    'AI_FORM_MAP',
    'CAPTCHA_SOLVE'
);


--
-- TOC entry 1697 (class 1247 OID 370990)
-- Name: TaskStatus; Type: TYPE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TYPE public."TaskStatus" AS ENUM (
    'PENDING',
    'PROCESSING',
    'SUCCESS',
    'FAILED'
);


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 371 (class 1259 OID 369428)
-- Name: act_evt_log; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_evt_log (
    log_nr_ integer NOT NULL,
    type_ character varying(64),
    proc_def_id_ character varying(64),
    proc_inst_id_ character varying(64),
    execution_id_ character varying(64),
    task_id_ character varying(64),
    time_stamp_ timestamp without time zone NOT NULL,
    user_id_ character varying(255),
    data_ bytea,
    lock_owner_ character varying(255),
    lock_time_ timestamp without time zone,
    is_processed_ smallint DEFAULT 0
);


--
-- TOC entry 372 (class 1259 OID 369434)
-- Name: act_evt_log_log_nr__seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.act_evt_log_log_nr__seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 6178 (class 0 OID 0)
-- Dependencies: 372
-- Name: act_evt_log_log_nr__seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.act_evt_log_log_nr__seq OWNED BY public.act_evt_log.log_nr_;


--
-- TOC entry 373 (class 1259 OID 369435)
-- Name: act_ge_bytearray; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_ge_bytearray (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    name_ character varying(255),
    deployment_id_ character varying(64),
    bytes_ bytea,
    generated_ boolean
);


--
-- TOC entry 374 (class 1259 OID 369440)
-- Name: act_ge_property; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_ge_property (
    name_ character varying(64) NOT NULL,
    value_ character varying(300),
    rev_ integer
);


--
-- TOC entry 375 (class 1259 OID 369443)
-- Name: act_hi_actinst; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_hi_actinst (
    id_ character varying(64) NOT NULL,
    rev_ integer DEFAULT 1,
    proc_def_id_ character varying(64) NOT NULL,
    proc_inst_id_ character varying(64) NOT NULL,
    execution_id_ character varying(64) NOT NULL,
    act_id_ character varying(255) NOT NULL,
    task_id_ character varying(64),
    call_proc_inst_id_ character varying(64),
    act_name_ character varying(255),
    act_type_ character varying(255) NOT NULL,
    assignee_ character varying(255),
    start_time_ timestamp without time zone NOT NULL,
    end_time_ timestamp without time zone,
    transaction_order_ integer,
    duration_ bigint,
    delete_reason_ character varying(4000),
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


--
-- TOC entry 376 (class 1259 OID 369450)
-- Name: act_hi_attachment; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_hi_attachment (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    user_id_ character varying(255),
    name_ character varying(255),
    description_ character varying(4000),
    type_ character varying(255),
    task_id_ character varying(64),
    proc_inst_id_ character varying(64),
    url_ character varying(4000),
    content_id_ character varying(64),
    time_ timestamp without time zone
);


--
-- TOC entry 377 (class 1259 OID 369455)
-- Name: act_hi_comment; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_hi_comment (
    id_ character varying(64) NOT NULL,
    type_ character varying(255),
    time_ timestamp without time zone NOT NULL,
    user_id_ character varying(255),
    task_id_ character varying(64),
    proc_inst_id_ character varying(64),
    action_ character varying(255),
    message_ character varying(4000),
    full_msg_ bytea
);


--
-- TOC entry 378 (class 1259 OID 369460)
-- Name: act_hi_detail; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_hi_detail (
    id_ character varying(64) NOT NULL,
    type_ character varying(255) NOT NULL,
    proc_inst_id_ character varying(64),
    execution_id_ character varying(64),
    task_id_ character varying(64),
    act_inst_id_ character varying(64),
    name_ character varying(255) NOT NULL,
    var_type_ character varying(64),
    rev_ integer,
    time_ timestamp without time zone NOT NULL,
    bytearray_id_ character varying(64),
    double_ double precision,
    long_ bigint,
    text_ character varying(4000),
    text2_ character varying(4000)
);


--
-- TOC entry 379 (class 1259 OID 369465)
-- Name: act_hi_entitylink; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_hi_entitylink (
    id_ character varying(64) NOT NULL,
    link_type_ character varying(255),
    create_time_ timestamp without time zone,
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255),
    parent_element_id_ character varying(255),
    ref_scope_id_ character varying(255),
    ref_scope_type_ character varying(255),
    ref_scope_definition_id_ character varying(255),
    root_scope_id_ character varying(255),
    root_scope_type_ character varying(255),
    hierarchy_type_ character varying(255)
);


--
-- TOC entry 380 (class 1259 OID 369470)
-- Name: act_hi_identitylink; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_hi_identitylink (
    id_ character varying(64) NOT NULL,
    group_id_ character varying(255),
    type_ character varying(255),
    user_id_ character varying(255),
    task_id_ character varying(64),
    create_time_ timestamp without time zone,
    proc_inst_id_ character varying(64),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255)
);


--
-- TOC entry 381 (class 1259 OID 369475)
-- Name: act_hi_procinst; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_hi_procinst (
    id_ character varying(64) NOT NULL,
    rev_ integer DEFAULT 1,
    proc_inst_id_ character varying(64) NOT NULL,
    business_key_ character varying(255),
    proc_def_id_ character varying(64) NOT NULL,
    start_time_ timestamp without time zone NOT NULL,
    end_time_ timestamp without time zone,
    duration_ bigint,
    start_user_id_ character varying(255),
    start_act_id_ character varying(255),
    end_act_id_ character varying(255),
    super_process_instance_id_ character varying(64),
    delete_reason_ character varying(4000),
    tenant_id_ character varying(255) DEFAULT ''::character varying,
    name_ character varying(255),
    callback_id_ character varying(255),
    callback_type_ character varying(255),
    reference_id_ character varying(255),
    reference_type_ character varying(255),
    propagated_stage_inst_id_ character varying(255),
    business_status_ character varying(255)
);


--
-- TOC entry 382 (class 1259 OID 369482)
-- Name: act_hi_taskinst; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_hi_taskinst (
    id_ character varying(64) NOT NULL,
    rev_ integer DEFAULT 1,
    proc_def_id_ character varying(64),
    task_def_id_ character varying(64),
    task_def_key_ character varying(255),
    proc_inst_id_ character varying(64),
    execution_id_ character varying(64),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255),
    propagated_stage_inst_id_ character varying(255),
    name_ character varying(255),
    parent_task_id_ character varying(64),
    description_ character varying(4000),
    owner_ character varying(255),
    assignee_ character varying(255),
    start_time_ timestamp without time zone NOT NULL,
    claim_time_ timestamp without time zone,
    end_time_ timestamp without time zone,
    duration_ bigint,
    delete_reason_ character varying(4000),
    priority_ integer,
    due_date_ timestamp without time zone,
    form_key_ character varying(255),
    category_ character varying(255),
    tenant_id_ character varying(255) DEFAULT ''::character varying,
    last_updated_time_ timestamp without time zone
);


--
-- TOC entry 383 (class 1259 OID 369489)
-- Name: act_hi_tsk_log; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_hi_tsk_log (
    id_ integer NOT NULL,
    type_ character varying(64),
    task_id_ character varying(64) NOT NULL,
    time_stamp_ timestamp without time zone NOT NULL,
    user_id_ character varying(255),
    data_ character varying(4000),
    execution_id_ character varying(64),
    proc_inst_id_ character varying(64),
    proc_def_id_ character varying(64),
    scope_id_ character varying(255),
    scope_definition_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


--
-- TOC entry 384 (class 1259 OID 369495)
-- Name: act_hi_tsk_log_id__seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.act_hi_tsk_log_id__seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 6191 (class 0 OID 0)
-- Dependencies: 384
-- Name: act_hi_tsk_log_id__seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.act_hi_tsk_log_id__seq OWNED BY public.act_hi_tsk_log.id_;


--
-- TOC entry 385 (class 1259 OID 369496)
-- Name: act_hi_varinst; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_hi_varinst (
    id_ character varying(64) NOT NULL,
    rev_ integer DEFAULT 1,
    proc_inst_id_ character varying(64),
    execution_id_ character varying(64),
    task_id_ character varying(64),
    name_ character varying(255) NOT NULL,
    var_type_ character varying(100),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    bytearray_id_ character varying(64),
    double_ double precision,
    long_ bigint,
    text_ character varying(4000),
    text2_ character varying(4000),
    create_time_ timestamp without time zone,
    last_updated_time_ timestamp without time zone
);


--
-- TOC entry 386 (class 1259 OID 369502)
-- Name: act_id_bytearray; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_id_bytearray (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    name_ character varying(255),
    bytes_ bytea
);


--
-- TOC entry 387 (class 1259 OID 369507)
-- Name: act_id_group; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_id_group (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    name_ character varying(255),
    type_ character varying(255)
);


--
-- TOC entry 388 (class 1259 OID 369512)
-- Name: act_id_info; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_id_info (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    user_id_ character varying(64),
    type_ character varying(64),
    key_ character varying(255),
    value_ character varying(255),
    password_ bytea,
    parent_id_ character varying(255)
);


--
-- TOC entry 389 (class 1259 OID 369517)
-- Name: act_id_membership; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_id_membership (
    user_id_ character varying(64) NOT NULL,
    group_id_ character varying(64) NOT NULL
);


--
-- TOC entry 390 (class 1259 OID 369520)
-- Name: act_id_priv; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_id_priv (
    id_ character varying(64) NOT NULL,
    name_ character varying(255) NOT NULL
);


--
-- TOC entry 391 (class 1259 OID 369523)
-- Name: act_id_priv_mapping; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_id_priv_mapping (
    id_ character varying(64) NOT NULL,
    priv_id_ character varying(64) NOT NULL,
    user_id_ character varying(255),
    group_id_ character varying(255)
);


--
-- TOC entry 392 (class 1259 OID 369528)
-- Name: act_id_property; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_id_property (
    name_ character varying(64) NOT NULL,
    value_ character varying(300),
    rev_ integer
);


--
-- TOC entry 393 (class 1259 OID 369531)
-- Name: act_id_token; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_id_token (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    token_value_ character varying(255),
    token_date_ timestamp without time zone,
    ip_address_ character varying(255),
    user_agent_ character varying(255),
    user_id_ character varying(255),
    token_data_ character varying(2000)
);


--
-- TOC entry 394 (class 1259 OID 369536)
-- Name: act_id_user; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_id_user (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    first_ character varying(255),
    last_ character varying(255),
    display_name_ character varying(255),
    email_ character varying(255),
    pwd_ character varying(255),
    picture_id_ character varying(64),
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


--
-- TOC entry 395 (class 1259 OID 369542)
-- Name: act_procdef_info; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_procdef_info (
    id_ character varying(64) NOT NULL,
    proc_def_id_ character varying(64) NOT NULL,
    rev_ integer,
    info_json_id_ character varying(64)
);


--
-- TOC entry 396 (class 1259 OID 369545)
-- Name: act_re_deployment; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_re_deployment (
    id_ character varying(64) NOT NULL,
    name_ character varying(255),
    category_ character varying(255),
    key_ character varying(255),
    tenant_id_ character varying(255) DEFAULT ''::character varying,
    deploy_time_ timestamp without time zone,
    derived_from_ character varying(64),
    derived_from_root_ character varying(64),
    parent_deployment_id_ character varying(255),
    engine_version_ character varying(255)
);


--
-- TOC entry 397 (class 1259 OID 369551)
-- Name: act_re_model; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_re_model (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    name_ character varying(255),
    key_ character varying(255),
    category_ character varying(255),
    create_time_ timestamp without time zone,
    last_update_time_ timestamp without time zone,
    version_ integer,
    meta_info_ character varying(4000),
    deployment_id_ character varying(64),
    editor_source_value_id_ character varying(64),
    editor_source_extra_value_id_ character varying(64),
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


--
-- TOC entry 398 (class 1259 OID 369557)
-- Name: act_re_procdef; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_re_procdef (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    category_ character varying(255),
    name_ character varying(255),
    key_ character varying(255) NOT NULL,
    version_ integer NOT NULL,
    deployment_id_ character varying(64),
    resource_name_ character varying(4000),
    dgrm_resource_name_ character varying(4000),
    description_ character varying(4000),
    has_start_form_key_ boolean,
    has_graphical_notation_ boolean,
    suspension_state_ integer,
    tenant_id_ character varying(255) DEFAULT ''::character varying,
    derived_from_ character varying(64),
    derived_from_root_ character varying(64),
    derived_version_ integer DEFAULT 0 NOT NULL,
    engine_version_ character varying(255)
);


--
-- TOC entry 399 (class 1259 OID 369564)
-- Name: act_ru_actinst; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_ru_actinst (
    id_ character varying(64) NOT NULL,
    rev_ integer DEFAULT 1,
    proc_def_id_ character varying(64) NOT NULL,
    proc_inst_id_ character varying(64) NOT NULL,
    execution_id_ character varying(64) NOT NULL,
    act_id_ character varying(255) NOT NULL,
    task_id_ character varying(64),
    call_proc_inst_id_ character varying(64),
    act_name_ character varying(255),
    act_type_ character varying(255) NOT NULL,
    assignee_ character varying(255),
    start_time_ timestamp without time zone NOT NULL,
    end_time_ timestamp without time zone,
    duration_ bigint,
    transaction_order_ integer,
    delete_reason_ character varying(4000),
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


--
-- TOC entry 400 (class 1259 OID 369571)
-- Name: act_ru_deadletter_job; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_ru_deadletter_job (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    category_ character varying(255),
    type_ character varying(255) NOT NULL,
    exclusive_ boolean,
    execution_id_ character varying(64),
    process_instance_id_ character varying(64),
    proc_def_id_ character varying(64),
    element_id_ character varying(255),
    element_name_ character varying(255),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255),
    correlation_id_ character varying(255),
    exception_stack_id_ character varying(64),
    exception_msg_ character varying(4000),
    duedate_ timestamp without time zone,
    repeat_ character varying(255),
    handler_type_ character varying(255),
    handler_cfg_ character varying(4000),
    custom_values_id_ character varying(64),
    create_time_ timestamp without time zone,
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


--
-- TOC entry 401 (class 1259 OID 369577)
-- Name: act_ru_entitylink; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_ru_entitylink (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    create_time_ timestamp without time zone,
    link_type_ character varying(255),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255),
    parent_element_id_ character varying(255),
    ref_scope_id_ character varying(255),
    ref_scope_type_ character varying(255),
    ref_scope_definition_id_ character varying(255),
    root_scope_id_ character varying(255),
    root_scope_type_ character varying(255),
    hierarchy_type_ character varying(255)
);


--
-- TOC entry 402 (class 1259 OID 369582)
-- Name: act_ru_event_subscr; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_ru_event_subscr (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    event_type_ character varying(255) NOT NULL,
    event_name_ character varying(255),
    execution_id_ character varying(64),
    proc_inst_id_ character varying(64),
    activity_id_ character varying(64),
    configuration_ character varying(255),
    created_ timestamp without time zone NOT NULL,
    proc_def_id_ character varying(64),
    sub_scope_id_ character varying(64),
    scope_id_ character varying(64),
    scope_definition_id_ character varying(64),
    scope_type_ character varying(64),
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


--
-- TOC entry 403 (class 1259 OID 369588)
-- Name: act_ru_execution; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_ru_execution (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    proc_inst_id_ character varying(64),
    business_key_ character varying(255),
    parent_id_ character varying(64),
    proc_def_id_ character varying(64),
    super_exec_ character varying(64),
    root_proc_inst_id_ character varying(64),
    act_id_ character varying(255),
    is_active_ boolean,
    is_concurrent_ boolean,
    is_scope_ boolean,
    is_event_scope_ boolean,
    is_mi_root_ boolean,
    suspension_state_ integer,
    cached_ent_state_ integer,
    tenant_id_ character varying(255) DEFAULT ''::character varying,
    name_ character varying(255),
    start_act_id_ character varying(255),
    start_time_ timestamp without time zone,
    start_user_id_ character varying(255),
    lock_time_ timestamp without time zone,
    lock_owner_ character varying(255),
    is_count_enabled_ boolean,
    evt_subscr_count_ integer,
    task_count_ integer,
    job_count_ integer,
    timer_job_count_ integer,
    susp_job_count_ integer,
    deadletter_job_count_ integer,
    external_worker_job_count_ integer,
    var_count_ integer,
    id_link_count_ integer,
    callback_id_ character varying(255),
    callback_type_ character varying(255),
    reference_id_ character varying(255),
    reference_type_ character varying(255),
    propagated_stage_inst_id_ character varying(255),
    business_status_ character varying(255)
);


--
-- TOC entry 404 (class 1259 OID 369594)
-- Name: act_ru_external_job; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_ru_external_job (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    category_ character varying(255),
    type_ character varying(255) NOT NULL,
    lock_exp_time_ timestamp without time zone,
    lock_owner_ character varying(255),
    exclusive_ boolean,
    execution_id_ character varying(64),
    process_instance_id_ character varying(64),
    proc_def_id_ character varying(64),
    element_id_ character varying(255),
    element_name_ character varying(255),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255),
    correlation_id_ character varying(255),
    retries_ integer,
    exception_stack_id_ character varying(64),
    exception_msg_ character varying(4000),
    duedate_ timestamp without time zone,
    repeat_ character varying(255),
    handler_type_ character varying(255),
    handler_cfg_ character varying(4000),
    custom_values_id_ character varying(64),
    create_time_ timestamp without time zone,
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


--
-- TOC entry 405 (class 1259 OID 369600)
-- Name: act_ru_history_job; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_ru_history_job (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    lock_exp_time_ timestamp without time zone,
    lock_owner_ character varying(255),
    retries_ integer,
    exception_stack_id_ character varying(64),
    exception_msg_ character varying(4000),
    handler_type_ character varying(255),
    handler_cfg_ character varying(4000),
    custom_values_id_ character varying(64),
    adv_handler_cfg_id_ character varying(64),
    create_time_ timestamp without time zone,
    scope_type_ character varying(255),
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


--
-- TOC entry 406 (class 1259 OID 369606)
-- Name: act_ru_identitylink; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_ru_identitylink (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    group_id_ character varying(255),
    type_ character varying(255),
    user_id_ character varying(255),
    task_id_ character varying(64),
    proc_inst_id_ character varying(64),
    proc_def_id_ character varying(64),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255)
);


--
-- TOC entry 407 (class 1259 OID 369611)
-- Name: act_ru_job; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_ru_job (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    category_ character varying(255),
    type_ character varying(255) NOT NULL,
    lock_exp_time_ timestamp without time zone,
    lock_owner_ character varying(255),
    exclusive_ boolean,
    execution_id_ character varying(64),
    process_instance_id_ character varying(64),
    proc_def_id_ character varying(64),
    element_id_ character varying(255),
    element_name_ character varying(255),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255),
    correlation_id_ character varying(255),
    retries_ integer,
    exception_stack_id_ character varying(64),
    exception_msg_ character varying(4000),
    duedate_ timestamp without time zone,
    repeat_ character varying(255),
    handler_type_ character varying(255),
    handler_cfg_ character varying(4000),
    custom_values_id_ character varying(64),
    create_time_ timestamp without time zone,
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


--
-- TOC entry 408 (class 1259 OID 369617)
-- Name: act_ru_suspended_job; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_ru_suspended_job (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    category_ character varying(255),
    type_ character varying(255) NOT NULL,
    exclusive_ boolean,
    execution_id_ character varying(64),
    process_instance_id_ character varying(64),
    proc_def_id_ character varying(64),
    element_id_ character varying(255),
    element_name_ character varying(255),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255),
    correlation_id_ character varying(255),
    retries_ integer,
    exception_stack_id_ character varying(64),
    exception_msg_ character varying(4000),
    duedate_ timestamp without time zone,
    repeat_ character varying(255),
    handler_type_ character varying(255),
    handler_cfg_ character varying(4000),
    custom_values_id_ character varying(64),
    create_time_ timestamp without time zone,
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


--
-- TOC entry 409 (class 1259 OID 369623)
-- Name: act_ru_task; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_ru_task (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    execution_id_ character varying(64),
    proc_inst_id_ character varying(64),
    proc_def_id_ character varying(64),
    task_def_id_ character varying(64),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255),
    propagated_stage_inst_id_ character varying(255),
    name_ character varying(255),
    parent_task_id_ character varying(64),
    description_ character varying(4000),
    task_def_key_ character varying(255),
    owner_ character varying(255),
    assignee_ character varying(255),
    delegation_ character varying(64),
    priority_ integer,
    create_time_ timestamp without time zone,
    due_date_ timestamp without time zone,
    category_ character varying(255),
    suspension_state_ integer,
    tenant_id_ character varying(255) DEFAULT ''::character varying,
    form_key_ character varying(255),
    claim_time_ timestamp without time zone,
    is_count_enabled_ boolean,
    var_count_ integer,
    id_link_count_ integer,
    sub_task_count_ integer
);


--
-- TOC entry 410 (class 1259 OID 369629)
-- Name: act_ru_timer_job; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_ru_timer_job (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    category_ character varying(255),
    type_ character varying(255) NOT NULL,
    lock_exp_time_ timestamp without time zone,
    lock_owner_ character varying(255),
    exclusive_ boolean,
    execution_id_ character varying(64),
    process_instance_id_ character varying(64),
    proc_def_id_ character varying(64),
    element_id_ character varying(255),
    element_name_ character varying(255),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    scope_definition_id_ character varying(255),
    correlation_id_ character varying(255),
    retries_ integer,
    exception_stack_id_ character varying(64),
    exception_msg_ character varying(4000),
    duedate_ timestamp without time zone,
    repeat_ character varying(255),
    handler_type_ character varying(255),
    handler_cfg_ character varying(4000),
    custom_values_id_ character varying(64),
    create_time_ timestamp without time zone,
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


--
-- TOC entry 411 (class 1259 OID 369635)
-- Name: act_ru_variable; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_ru_variable (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    type_ character varying(255) NOT NULL,
    name_ character varying(255) NOT NULL,
    execution_id_ character varying(64),
    proc_inst_id_ character varying(64),
    task_id_ character varying(64),
    scope_id_ character varying(255),
    sub_scope_id_ character varying(255),
    scope_type_ character varying(255),
    bytearray_id_ character varying(64),
    double_ double precision,
    long_ bigint,
    text_ character varying(4000),
    text2_ character varying(4000)
);


--
-- TOC entry 209 (class 1259 OID 360271)
-- Name: crm_account; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_account (
    account_id character varying(100) NOT NULL,
    account_type character varying(100) NOT NULL,
    lead_id character varying(100),
    lead_name character varying(100),
    org_id character varying(100),
    name character varying(100),
    short_name character varying(100),
    full_name character varying(100),
    industry character varying(100),
    phone_number character varying(20),
    territory character varying(20),
    owner character varying(20),
    employee_count integer DEFAULT 0,
    website character varying(50),
    active_opty_count integer DEFAULT 0,
    active_contact_count integer DEFAULT 0,
    active_lead_count integer DEFAULT 0,
    active_opty_amount numeric(10,2) DEFAULT 0.00,
    revenue numeric(15,2) DEFAULT 0.00,
    is_target character(1) DEFAULT 'N'::bpchar,
    status character varying(10),
    description character varying(255),
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone,
    user_id bigint,
    dept_id bigint,
    code character varying(100),
    location character varying(255),
    address character varying(255),
    currency character varying(45) DEFAULT 'CNY'::character varying,
    custom_field jsonb,
    inactive_opty_count integer DEFAULT 0
);


--
-- TOC entry 430 (class 1259 OID 371309)
-- Name: crm_account_value; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_account_value (
    account_id character varying(64) NOT NULL,
    tenant_id character varying(64) NOT NULL,
    currency character varying(8) DEFAULT 'CNY'::character varying,
    member_count integer DEFAULT 0,
    booked_total numeric(18,2) DEFAULT 0,
    inflight_total numeric(18,2) DEFAULT 0,
    refund_total numeric(18,2) DEFAULT 0,
    net_clv numeric(18,2) DEFAULT 0,
    paid_total numeric(18,2) DEFAULT 0,
    pending_receivable numeric(18,2) DEFAULT 0,
    acquisition_cost numeric(18,2) DEFAULT 0,
    pltv_12m numeric(18,2) DEFAULT 0,
    tier character varying(16) DEFAULT 'BRONZE'::character varying,
    last_visit_at timestamp with time zone,
    days_since_last_visit integer,
    by_treatment jsonb,
    calculated_at timestamp with time zone DEFAULT now(),
    del_flag smallint DEFAULT 0
);


--
-- TOC entry 210 (class 1259 OID 360287)
-- Name: crm_accountrelation; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_accountrelation (
    relate_id character varying(100) NOT NULL,
    target_type character varying(100),
    target_name character varying(100),
    source_id character varying(100) NOT NULL,
    source_name character varying(100),
    relate_role character varying(100) DEFAULT '员工'::character varying,
    target_id character varying(100) NOT NULL,
    relation_desc character varying(100),
    start_date date,
    end_date date,
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone,
    status character varying(45),
    priority integer DEFAULT 0,
    owner character varying(45),
    user_id bigint,
    latest_update character varying(255),
    stage character varying(100),
    open_count bigint DEFAULT 0,
    open_status character varying(45) DEFAULT '未打开'::character varying,
    feedback character varying(100),
    validation_code character varying(45)
);


--
-- TOC entry 211 (class 1259 OID 360298)
-- Name: crm_activity; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_activity (
    activity_id character varying(100) NOT NULL,
    activity_subject character varying(500),
    communication_type character varying(100),
    started_date timestamp without time zone,
    assigned_to character varying(20),
    assigned_toid character varying(100),
    status_detail character varying(100) DEFAULT '未开始'::character varying,
    contacts text,
    internal_staff text,
    tasks text,
    outcome character varying(255),
    comments text,
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone,
    user_id bigint,
    dept_id bigint,
    pulse_id character varying(64),
    parent_id character varying(64),
    order_id bigint,
    pulse_type character varying(50),
    subject_type character varying(50),
    subject_id character varying(64),
    event_type character varying(100),
    event_name character varying(500),
    importance integer DEFAULT 10,
    ended_date timestamp without time zone,
    source character varying(50) DEFAULT 'manual'::character varying,
    payload jsonb,
    status character varying(20) DEFAULT 'completed'::character varying,
    parent_event_id character varying(64),
    relate_type character varying(50),
    relate_id character varying(64),
    relate_name character varying(200),
    message_data text,
    avatar character varying(512),
    creator_id character varying(64)
);


--
-- TOC entry 6223 (class 0 OID 0)
-- Dependencies: 211
-- Name: COLUMN crm_activity.pulse_id; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_activity.pulse_id IS 'pulseId，兼容 crm_pulse 迁移';


--
-- TOC entry 6224 (class 0 OID 0)
-- Dependencies: 211
-- Name: COLUMN crm_activity.parent_id; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_activity.parent_id IS '父编号，兼容 crm_pulse 迁移（"0"=timeline, "1"=notes）';


--
-- TOC entry 6225 (class 0 OID 0)
-- Dependencies: 211
-- Name: COLUMN crm_activity.order_id; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_activity.order_id IS '排序';


--
-- TOC entry 6226 (class 0 OID 0)
-- Dependencies: 211
-- Name: COLUMN crm_activity.pulse_type; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_activity.pulse_type IS '旧 pulse 事件类型（逐步废弃）';


--
-- TOC entry 6227 (class 0 OID 0)
-- Dependencies: 211
-- Name: COLUMN crm_activity.subject_type; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_activity.subject_type IS '主体类型: Lead|Contact|Account|Opportunity';


--
-- TOC entry 6228 (class 0 OID 0)
-- Dependencies: 211
-- Name: COLUMN crm_activity.subject_id; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_activity.subject_id IS '主体ID';


--
-- TOC entry 6229 (class 0 OID 0)
-- Dependencies: 211
-- Name: COLUMN crm_activity.event_type; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_activity.event_type IS '事件类型: lead.created|ai.scored|payment.deposit 等';


--
-- TOC entry 6230 (class 0 OID 0)
-- Dependencies: 211
-- Name: COLUMN crm_activity.event_name; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_activity.event_name IS '事件中文名称';


--
-- TOC entry 6231 (class 0 OID 0)
-- Dependencies: 211
-- Name: COLUMN crm_activity.importance; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_activity.importance IS '重要性: 10-30普通, 40-60重要, 70-100关键里程碑';


--
-- TOC entry 6232 (class 0 OID 0)
-- Dependencies: 211
-- Name: COLUMN crm_activity.ended_date; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_activity.ended_date IS '事件结束时间';


--
-- TOC entry 6233 (class 0 OID 0)
-- Dependencies: 211
-- Name: COLUMN crm_activity.source; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_activity.source IS '事件来源: manual|system|workflow|agent|douyin|wecom|...';


--
-- TOC entry 6234 (class 0 OID 0)
-- Dependencies: 211
-- Name: COLUMN crm_activity.payload; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_activity.payload IS '结构化扩展信息（JSON）';


--
-- TOC entry 6235 (class 0 OID 0)
-- Dependencies: 211
-- Name: COLUMN crm_activity.status; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_activity.status IS '事件状态: pending|in_progress|completed|cancelled';


--
-- TOC entry 6236 (class 0 OID 0)
-- Dependencies: 211
-- Name: COLUMN crm_activity.parent_event_id; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_activity.parent_event_id IS '父事件ID，支持事件嵌套';


--
-- TOC entry 6237 (class 0 OID 0)
-- Dependencies: 211
-- Name: COLUMN crm_activity.relate_type; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_activity.relate_type IS '事件来源业务对象类型（兼容旧查询）';


--
-- TOC entry 6238 (class 0 OID 0)
-- Dependencies: 211
-- Name: COLUMN crm_activity.relate_id; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_activity.relate_id IS '事件来源业务对象ID（兼容旧查询）';


--
-- TOC entry 6239 (class 0 OID 0)
-- Dependencies: 211
-- Name: COLUMN crm_activity.relate_name; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_activity.relate_name IS '关联对象显示名';


--
-- TOC entry 6240 (class 0 OID 0)
-- Dependencies: 211
-- Name: COLUMN crm_activity.message_data; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_activity.message_data IS '文本摘要（纯文本，非 HTML）';


--
-- TOC entry 6241 (class 0 OID 0)
-- Dependencies: 211
-- Name: COLUMN crm_activity.avatar; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_activity.avatar IS '发布人头像';


--
-- TOC entry 6242 (class 0 OID 0)
-- Dependencies: 211
-- Name: COLUMN crm_activity.creator_id; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_activity.creator_id IS '创建者ID';


--
-- TOC entry 212 (class 1259 OID 360306)
-- Name: crm_actrelate; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_actrelate (
    relate_id character varying(100) NOT NULL,
    activity_id character varying(100),
    relate_role character varying(100) DEFAULT '联系人'::character varying,
    relate_target character varying(100) NOT NULL,
    target_type character varying(100),
    target_id character varying(100) NOT NULL,
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone
);


--
-- TOC entry 214 (class 1259 OID 360315)
-- Name: crm_agent_tool; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_agent_tool (
    id bigint NOT NULL,
    agent_id character varying(64) NOT NULL,
    name character varying(128) NOT NULL,
    description text,
    category character varying(64),
    llm_model character varying(64),
    input_example jsonb,
    output_example jsonb,
    prompt_template text NOT NULL,
    enabled smallint DEFAULT 1,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- TOC entry 213 (class 1259 OID 360314)
-- Name: crm_agent_tool_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE public.crm_agent_tool ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.crm_agent_tool_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 450 (class 1259 OID 371819)
-- Name: crm_ai_chat_message; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_ai_chat_message (
    message_id character varying(36) NOT NULL,
    session_id character varying(36) NOT NULL,
    tenant_id character varying(64) NOT NULL,
    role character varying(16) NOT NULL,
    parts_json text NOT NULL,
    metadata_json text,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


--
-- TOC entry 6247 (class 0 OID 0)
-- Dependencies: 450
-- Name: TABLE crm_ai_chat_message; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_ai_chat_message IS 'UI message parts persisted per session (AI SDK UIMessage shape)';


--
-- TOC entry 449 (class 1259 OID 371810)
-- Name: crm_ai_chat_session; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_ai_chat_session (
    session_id character varying(36) NOT NULL,
    tenant_id character varying(64) NOT NULL,
    user_id character varying(64) NOT NULL,
    title character varying(500),
    create_time timestamp without time zone,
    update_time timestamp without time zone,
    del_flag character(1) DEFAULT '0'::bpchar NOT NULL
);


--
-- TOC entry 6249 (class 0 OID 0)
-- Dependencies: 449
-- Name: TABLE crm_ai_chat_session; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_ai_chat_session IS 'AI assistant chat sessions scoped by tenant + user';


--
-- TOC entry 446 (class 1259 OID 371741)
-- Name: crm_ai_model_config; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_ai_model_config (
    config_id character varying(64) NOT NULL,
    tenant_id character varying(64) NOT NULL,
    provider character varying(32) NOT NULL,
    credential_id character varying(64) NOT NULL,
    model character varying(128) NOT NULL,
    temperature numeric(3,2) DEFAULT 0.20 NOT NULL,
    default_translation_language character varying(16) NOT NULL,
    del_flag character(1) DEFAULT '0'::bpchar NOT NULL,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


--
-- TOC entry 6251 (class 0 OID 0)
-- Dependencies: 446
-- Name: TABLE crm_ai_model_config; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_ai_model_config IS '租户 AI 模型配置';


--
-- TOC entry 6252 (class 0 OID 0)
-- Dependencies: 446
-- Name: COLUMN crm_ai_model_config.config_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_ai_model_config.config_id IS 'UUID 主键';


--
-- TOC entry 6253 (class 0 OID 0)
-- Dependencies: 446
-- Name: COLUMN crm_ai_model_config.tenant_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_ai_model_config.tenant_id IS '租户标识（tenant 请求头）';


--
-- TOC entry 6254 (class 0 OID 0)
-- Dependencies: 446
-- Name: COLUMN crm_ai_model_config.provider; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_ai_model_config.provider IS 'LLM 提供商标识 qwen/deepseek/openai/gemini/anthropic';


--
-- TOC entry 6255 (class 0 OID 0)
-- Dependencies: 446
-- Name: COLUMN crm_ai_model_config.credential_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_ai_model_config.credential_id IS '关联 w_credential.id';


--
-- TOC entry 6256 (class 0 OID 0)
-- Dependencies: 446
-- Name: COLUMN crm_ai_model_config.model; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_ai_model_config.model IS '模型 ID';


--
-- TOC entry 6257 (class 0 OID 0)
-- Dependencies: 446
-- Name: COLUMN crm_ai_model_config.temperature; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_ai_model_config.temperature IS '采样温度 0.00-1.00';


--
-- TOC entry 6258 (class 0 OID 0)
-- Dependencies: 446
-- Name: COLUMN crm_ai_model_config.default_translation_language; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_ai_model_config.default_translation_language IS '默认翻译目标语言 BCP-47';


--
-- TOC entry 6259 (class 0 OID 0)
-- Dependencies: 446
-- Name: COLUMN crm_ai_model_config.del_flag; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_ai_model_config.del_flag IS '逻辑删除 0 正常';


--
-- TOC entry 461 (class 1259 OID 379543)
-- Name: crm_ai_playbook_run; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_ai_playbook_run (
    id bigint NOT NULL,
    tenant_id character varying(64) NOT NULL,
    playbook_id bigint NOT NULL,
    playbook_version integer NOT NULL,
    subject_type character varying(32) DEFAULT NULL::character varying,
    subject_id character varying(64) DEFAULT NULL::character varying,
    trigger_source character varying(32) NOT NULL,
    status character varying(32) NOT NULL,
    context_json text,
    final_output text,
    error_message text,
    started_at timestamp with time zone,
    finished_at timestamp with time zone,
    create_by character varying(64) DEFAULT NULL::character varying,
    create_time timestamp with time zone
);


--
-- TOC entry 6261 (class 0 OID 0)
-- Dependencies: 461
-- Name: TABLE crm_ai_playbook_run; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_ai_playbook_run IS 'Playbook 执行审计';


--
-- TOC entry 460 (class 1259 OID 379542)
-- Name: crm_ai_playbook_run_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_ai_playbook_run_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 6263 (class 0 OID 0)
-- Dependencies: 460
-- Name: crm_ai_playbook_run_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_ai_playbook_run_id_seq OWNED BY public.crm_ai_playbook_run.id;


--
-- TOC entry 453 (class 1259 OID 379467)
-- Name: crm_ai_skill; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_ai_skill (
    id bigint NOT NULL,
    tenant_id character varying(64) NOT NULL,
    name character varying(128) NOT NULL,
    description character varying(512) DEFAULT NULL::character varying,
    instruction text NOT NULL,
    trigger_keywords text,
    trigger_patterns text,
    scope character varying(32) DEFAULT 'all'::character varying NOT NULL,
    priority integer DEFAULT 0 NOT NULL,
    enabled smallint DEFAULT 1 NOT NULL,
    skill_type character varying(32) DEFAULT 'prompt'::character varying NOT NULL,
    domain character varying(64) DEFAULT NULL::character varying,
    track_key character varying(64) DEFAULT NULL::character varying,
    tool_bindings text,
    input_context text,
    output_key character varying(64) DEFAULT NULL::character varying,
    output_schema text,
    mcp_server_ids text,
    create_by character varying(64) DEFAULT NULL::character varying,
    create_time timestamp with time zone,
    update_by character varying(64) DEFAULT NULL::character varying,
    update_time timestamp with time zone
);


--
-- TOC entry 6265 (class 0 OID 0)
-- Dependencies: 453
-- Name: TABLE crm_ai_skill; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_ai_skill IS '租户 AI Skill 配置';


--
-- TOC entry 6266 (class 0 OID 0)
-- Dependencies: 453
-- Name: COLUMN crm_ai_skill.tenant_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_ai_skill.tenant_id IS '租户标识（tenant 请求头）';


--
-- TOC entry 6267 (class 0 OID 0)
-- Dependencies: 453
-- Name: COLUMN crm_ai_skill.trigger_keywords; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_ai_skill.trigger_keywords IS 'JSON 字符串数组';


--
-- TOC entry 6268 (class 0 OID 0)
-- Dependencies: 453
-- Name: COLUMN crm_ai_skill.tool_bindings; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_ai_skill.tool_bindings IS 'JSON tool binding 数组';


--
-- TOC entry 452 (class 1259 OID 379466)
-- Name: crm_ai_skill_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_ai_skill_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 6270 (class 0 OID 0)
-- Dependencies: 452
-- Name: crm_ai_skill_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_ai_skill_id_seq OWNED BY public.crm_ai_skill.id;


--
-- TOC entry 457 (class 1259 OID 379508)
-- Name: crm_ai_skill_playbook; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_ai_skill_playbook (
    id bigint NOT NULL,
    tenant_id character varying(64) NOT NULL,
    name character varying(128) NOT NULL,
    description character varying(512) DEFAULT NULL::character varying,
    trigger_keywords text,
    entry_skill_id bigint,
    output_schema text,
    enabled smallint DEFAULT 1 NOT NULL,
    version integer DEFAULT 1 NOT NULL,
    create_by character varying(64) DEFAULT NULL::character varying,
    create_time timestamp with time zone,
    update_by character varying(64) DEFAULT NULL::character varying,
    update_time timestamp with time zone
);


--
-- TOC entry 6272 (class 0 OID 0)
-- Dependencies: 457
-- Name: TABLE crm_ai_skill_playbook; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_ai_skill_playbook IS 'AI Skill Playbook 定义';


--
-- TOC entry 456 (class 1259 OID 379507)
-- Name: crm_ai_skill_playbook_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_ai_skill_playbook_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 6274 (class 0 OID 0)
-- Dependencies: 456
-- Name: crm_ai_skill_playbook_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_ai_skill_playbook_id_seq OWNED BY public.crm_ai_skill_playbook.id;


--
-- TOC entry 459 (class 1259 OID 379525)
-- Name: crm_ai_skill_playbook_step; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_ai_skill_playbook_step (
    id bigint NOT NULL,
    playbook_id bigint NOT NULL,
    step_key character varying(64) NOT NULL,
    skill_id bigint NOT NULL,
    step_type character varying(32) DEFAULT 'sequential'::character varying NOT NULL,
    parallel_group character varying(64) DEFAULT NULL::character varying,
    depends_on text,
    track_key character varying(64) DEFAULT NULL::character varying,
    sort_order integer DEFAULT 0 NOT NULL,
    enabled smallint DEFAULT 1 NOT NULL
);


--
-- TOC entry 6276 (class 0 OID 0)
-- Dependencies: 459
-- Name: TABLE crm_ai_skill_playbook_step; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_ai_skill_playbook_step IS 'Playbook DAG 步骤';


--
-- TOC entry 458 (class 1259 OID 379524)
-- Name: crm_ai_skill_playbook_step_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_ai_skill_playbook_step_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 6278 (class 0 OID 0)
-- Dependencies: 458
-- Name: crm_ai_skill_playbook_step_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_ai_skill_playbook_step_id_seq OWNED BY public.crm_ai_skill_playbook_step.id;


--
-- TOC entry 451 (class 1259 OID 371833)
-- Name: crm_analytics_query_log; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_analytics_query_log (
    log_id character varying(36) NOT NULL,
    tenant_id character varying(64) NOT NULL,
    user_id character varying(64) NOT NULL,
    metric character varying(128) NOT NULL,
    request_json text,
    response_json text,
    duration_ms integer,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


--
-- TOC entry 6280 (class 0 OID 0)
-- Dependencies: 451
-- Name: TABLE crm_analytics_query_log; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_analytics_query_log IS 'Optional audit trail for AI assistant analytics queries';


--
-- TOC entry 215 (class 1259 OID 360325)
-- Name: crm_appointment; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_appointment (
    appointment_id character varying(100) NOT NULL,
    appointment_name character varying(100),
    owner character varying(100),
    owner_id character varying(100),
    location character varying(200),
    is_virtual character(1) DEFAULT 'N'::bpchar,
    is_allday character(1) DEFAULT 'N'::bpchar,
    repeat_type character varying(45) DEFAULT '不重复'::character varying,
    end_repeat integer DEFAULT 0,
    duration integer DEFAULT 30,
    reminder integer DEFAULT 5,
    start_time timestamp without time zone,
    end_time timestamp without time zone,
    relate_id character varying(100),
    relate_name character varying(100),
    relate_type character varying(45),
    appointment_status character varying(20) DEFAULT '未开始'::character varying,
    agenda text,
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone,
    user_id bigint,
    dept_id bigint,
    notes text,
    conclusion character varying(255)
);


--
-- TOC entry 216 (class 1259 OID 360339)
-- Name: crm_attendee; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_attendee (
    attendee_id character varying(100) NOT NULL,
    appointment_id character varying(100),
    attendee_name character varying(100),
    attendee_type character varying(100),
    attendee_refid character varying(100),
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone,
    org_id character varying(100),
    full_name character varying(255)
);


--
-- TOC entry 217 (class 1259 OID 360346)
-- Name: crm_bulkemail; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_bulkemail (
    bulk_id character varying(100) NOT NULL,
    subject character varying(100) NOT NULL,
    template_id character varying(100) NOT NULL,
    unique_id character varying(10) NOT NULL,
    sender character varying(100) NOT NULL,
    recipients text,
    total integer DEFAULT 0,
    fails integer DEFAULT 0,
    opens integer DEFAULT 0,
    replies integer DEFAULT 0,
    email_content text,
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone,
    status character varying(45),
    send_date timestamp without time zone,
    spend_amount numeric(18,2) DEFAULT 0,
    send_mode character varying(32),
    scheduled_at timestamp with time zone,
    sent_at timestamp with time zone,
    cancelled_at timestamp with time zone,
    metadata jsonb
);


--
-- TOC entry 6284 (class 0 OID 0)
-- Dependencies: 217
-- Name: COLUMN crm_bulkemail.spend_amount; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_bulkemail.spend_amount IS 'Campaign spend for CAC amortization';


--
-- TOC entry 218 (class 1259 OID 360357)
-- Name: crm_campaign_contacts; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_campaign_contacts (
    id character varying(100) NOT NULL,
    campaign_id character varying(100) NOT NULL,
    contact_id character varying(100) NOT NULL,
    contact_name character varying(100),
    email character varying(100) NOT NULL,
    status character varying(100) DEFAULT 'pending'::character varying,
    metadata jsonb,
    open integer DEFAULT 0,
    opened_at timestamp without time zone,
    clicked_at timestamp without time zone,
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone,
    full_name character varying(255),
    delivery_status character varying(32) DEFAULT 'pending'::character varying,
    bounce_reason character varying(500),
    sent_at timestamp with time zone
);


--
-- TOC entry 219 (class 1259 OID 360366)
-- Name: crm_cashflow; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_cashflow (
    cashflow_id character varying(100) NOT NULL,
    cashflow_type character varying(100),
    relate_id character varying(100),
    relate_name character varying(200),
    relate_backaccount character varying(200),
    transaction_date date,
    amount numeric(15,2) DEFAULT 0.00,
    comment character varying(255),
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone,
    category character varying(45),
    account_id character varying(100)
);


--
-- TOC entry 220 (class 1259 OID 360374)
-- Name: crm_collect; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_collect (
    collect_id character varying(100) NOT NULL,
    relate_type character varying(100) NOT NULL,
    relate_id character varying(100),
    relate_name character varying(255),
    collect_status character varying(45) DEFAULT '已创建'::character varying,
    period character varying(100),
    category character varying(100),
    validation_code character varying(100),
    content jsonb,
    expired_date date,
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone
);


--
-- TOC entry 221 (class 1259 OID 360382)
-- Name: crm_competitor; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_competitor (
    competitor_id character varying(100) NOT NULL,
    competitor_name character varying(100),
    website character varying(30),
    industry character varying(30),
    employee_count integer DEFAULT 0,
    revenue numeric(15,2) DEFAULT 0.00,
    description character varying(255),
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone
);


--
-- TOC entry 222 (class 1259 OID 360391)
-- Name: crm_contact; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_contact (
    contact_id character varying(100) NOT NULL,
    org_id character varying(100),
    account_id character varying(100),
    name character varying(100) NOT NULL,
    full_name character varying(100),
    account_name character varying(100),
    salutation character varying(10) DEFAULT 'Mr.'::character varying,
    title character varying(100),
    marital_status character varying(10) DEFAULT '单身'::character varying,
    birthdate date,
    age integer DEFAULT 0,
    work_phone character varying(30),
    work_email character varying(30),
    gender character varying(10) DEFAULT '男'::character varying,
    personal_email character varying(30),
    mobile_phone character varying(20),
    client_type character varying(100),
    territory character varying(20),
    owner character varying(20),
    management_level character varying(20),
    department character varying(20),
    reports_to character varying(20),
    reports_to_id character varying(100),
    favorability_rating character varying(10),
    influence_rating character varying(10),
    assistant character varying(20),
    assistant_phone character varying(20),
    assistant_email character varying(100),
    allow_call character(1) DEFAULT 'Y'::bpchar,
    allow_email character(1) DEFAULT 'Y'::bpchar,
    is_customer character(1) DEFAULT 'N'::bpchar,
    is_org_customer character(1) DEFAULT 'N'::bpchar,
    responsibility character varying(255),
    active_opty_count integer DEFAULT 0,
    active_lead_count integer DEFAULT 0,
    active_opty_amount numeric(10,2) DEFAULT 0.00,
    status character varying(10),
    working_years integer DEFAULT 0,
    is_active character(1) DEFAULT 'Y'::bpchar,
    degree character varying(100),
    major character varying(100),
    origin character varying(100),
    wechat character varying(100),
    location character varying(255),
    school character varying(100),
    tag jsonb,
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone,
    user_id bigint,
    dept_id bigint,
    custom_field jsonb,
    sid character varying(100),
    resources jsonb,
    requirement jsonb,
    rr_description text,
    name_card jsonb,
    avatar character varying(255),
    priority integer DEFAULT 0,
    original_source character varying(255),
    first_visit_clinic_id character varying(128),
    imaging_portal_url text,
    imaging_patient_external_id character varying(255),
    first_visit_at timestamp with time zone,
    last_visit_at timestamp with time zone,
    visit_count integer DEFAULT 0,
    acquisition_cost numeric(18,2) DEFAULT 0,
    wecom_external_userid character varying(64),
    wecom_name character varying(128),
    wecom_avatar character varying(512),
    wecom_gender integer,
    wecom_type integer
);


--
-- TOC entry 6290 (class 0 OID 0)
-- Dependencies: 222
-- Name: COLUMN crm_contact.original_source; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_contact.original_source IS '终身初始来源（首次创建写入，勿随意覆盖）';


--
-- TOC entry 6291 (class 0 OID 0)
-- Dependencies: 222
-- Name: COLUMN crm_contact.first_visit_clinic_id; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_contact.first_visit_clinic_id IS '首诊门店主键或字典值';


--
-- TOC entry 6292 (class 0 OID 0)
-- Dependencies: 222
-- Name: COLUMN crm_contact.imaging_portal_url; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_contact.imaging_portal_url IS '影像厂商门户直链';


--
-- TOC entry 6293 (class 0 OID 0)
-- Dependencies: 222
-- Name: COLUMN crm_contact.imaging_patient_external_id; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_contact.imaging_patient_external_id IS '第三方影像系统患者标识';


--
-- TOC entry 6294 (class 0 OID 0)
-- Dependencies: 222
-- Name: COLUMN crm_contact.first_visit_at; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_contact.first_visit_at IS 'First visit timestamp (proxy: contract.sign_time in v1)';


--
-- TOC entry 6295 (class 0 OID 0)
-- Dependencies: 222
-- Name: COLUMN crm_contact.last_visit_at; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_contact.last_visit_at IS 'Most recent visit timestamp';


--
-- TOC entry 6296 (class 0 OID 0)
-- Dependencies: 222
-- Name: COLUMN crm_contact.visit_count; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_contact.visit_count IS 'Lifetime visit count';


--
-- TOC entry 6297 (class 0 OID 0)
-- Dependencies: 222
-- Name: COLUMN crm_contact.acquisition_cost; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_contact.acquisition_cost IS 'Accumulated CAC from campaign amortization';


--
-- TOC entry 6298 (class 0 OID 0)
-- Dependencies: 222
-- Name: COLUMN crm_contact.wecom_external_userid; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_contact.wecom_external_userid IS '企业微信外部联系人ID';


--
-- TOC entry 6299 (class 0 OID 0)
-- Dependencies: 222
-- Name: COLUMN crm_contact.wecom_name; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_contact.wecom_name IS '微信客服客户昵称';


--
-- TOC entry 6300 (class 0 OID 0)
-- Dependencies: 222
-- Name: COLUMN crm_contact.wecom_avatar; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_contact.wecom_avatar IS '微信客服客户头像 URL';


--
-- TOC entry 6301 (class 0 OID 0)
-- Dependencies: 222
-- Name: COLUMN crm_contact.wecom_gender; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_contact.wecom_gender IS '微信客服客户性别（0=未知, 1=男, 2=女）';


--
-- TOC entry 6302 (class 0 OID 0)
-- Dependencies: 222
-- Name: COLUMN crm_contact.wecom_type; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_contact.wecom_type IS '微信客服客户类型';


--
-- TOC entry 223 (class 1259 OID 360412)
-- Name: crm_contactmethod; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_contactmethod (
    method_id character varying(100) NOT NULL,
    method_type character varying(100),
    detail character varying(255),
    is_primary integer DEFAULT 0,
    relate_type character varying(100),
    relate_name character varying(100),
    source_id character varying(100) NOT NULL,
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone
);


--
-- TOC entry 224 (class 1259 OID 360420)
-- Name: crm_contactrelation; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_contactrelation (
    relate_id character varying(100) NOT NULL,
    target_type character varying(100),
    target_name character varying(100),
    source_id character varying(100) NOT NULL,
    source_name character varying(100),
    relate_role character varying(100) DEFAULT '员工'::character varying,
    target_id character varying(100) NOT NULL,
    relation_desc character varying(100),
    start_date date,
    end_date date,
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone
);


--
-- TOC entry 225 (class 1259 OID 360428)
-- Name: crm_contract; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_contract (
    contract_id character varying(100) NOT NULL,
    contract_name character varying(255),
    contract_type character varying(100),
    contract_code character varying(100),
    contract_status character varying(100),
    contract_identity character varying(100),
    contract_fundsdirec character varying(100),
    opty_id character varying(100),
    opty_name character varying(255),
    account_id character varying(100),
    account_name character varying(255),
    contact_id character varying(100),
    contact_name character varying(255),
    contact_phone character varying(255),
    amount numeric(15,2) DEFAULT 0.00,
    tax_ratio numeric(10,2) DEFAULT 0.00,
    tax_amount numeric(10,2) DEFAULT 0.00,
    total_amount numeric(15,2) DEFAULT 0.00,
    sign_amount numeric(10,2) DEFAULT 0.00,
    changed_amount numeric(10,2) DEFAULT 0.00,
    execution_amount numeric(10,2) DEFAULT 0.00,
    received_amount numeric(10,2) DEFAULT 0.00,
    receivable numeric(10,2) DEFAULT 0.00,
    pending_receivable numeric(10,2) DEFAULT 0.00,
    invoiced_amount numeric(10,2) DEFAULT 0.00,
    cost numeric(10,2) DEFAULT 0.00,
    expense numeric(10,2) DEFAULT 0.00,
    margin numeric(10,2) DEFAULT 0.00,
    payment_terms integer DEFAULT 1,
    is_change character(1) DEFAULT 'N'::bpchar,
    is_destroy character(1) DEFAULT 'N'::bpchar,
    operator_id character varying(100),
    operator_name character varying(100),
    owner_id character varying(100),
    owner_name character varying(100),
    status character varying(100),
    duration integer DEFAULT 1,
    effective_time timestamp without time zone,
    losteffective_time timestamp without time zone,
    end_time timestamp without time zone,
    sign_time timestamp without time zone,
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone,
    user_id bigint,
    dept_id bigint,
    comment_file jsonb
);


--
-- TOC entry 226 (class 1259 OID 360453)
-- Name: crm_conversion_history; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_conversion_history (
    id character varying(100) NOT NULL,
    lead_id character varying(100),
    lead_name character varying(100),
    conversion_type character varying(100) NOT NULL,
    target_id character varying(100),
    target_name character varying(100),
    converted_by character varying(20),
    remark character varying(255),
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone,
    user_id bigint,
    dept_id bigint
);


--
-- TOC entry 441 (class 1259 OID 371605)
-- Name: crm_data_table; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_data_table (
    id text NOT NULL,
    tenant_id text NOT NULL,
    name character varying(255) NOT NULL,
    description text,
    create_by character varying(64),
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


--
-- TOC entry 6308 (class 0 OID 0)
-- Dependencies: 441
-- Name: TABLE crm_data_table; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_data_table IS 'Data Table 元数据（类 Airtable）';


--
-- TOC entry 442 (class 1259 OID 371616)
-- Name: crm_data_table_column; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_data_table_column (
    id text NOT NULL,
    data_table_id text NOT NULL,
    name character varying(255) NOT NULL,
    type character varying(32) NOT NULL,
    required boolean DEFAULT false NOT NULL,
    default_value text,
    sort_order integer DEFAULT 0 NOT NULL,
    is_system boolean DEFAULT false NOT NULL
);


--
-- TOC entry 6310 (class 0 OID 0)
-- Dependencies: 442
-- Name: TABLE crm_data_table_column; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_data_table_column IS 'Data Table 列定义';


--
-- TOC entry 443 (class 1259 OID 371633)
-- Name: crm_data_table_row; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_data_table_row (
    id text NOT NULL,
    data_table_id text NOT NULL,
    data jsonb DEFAULT '{}'::jsonb NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


--
-- TOC entry 6312 (class 0 OID 0)
-- Dependencies: 443
-- Name: TABLE crm_data_table_row; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_data_table_row IS 'Data Table 行数据（JSONB）';


--
-- TOC entry 227 (class 1259 OID 360460)
-- Name: crm_dd; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_dd (
    dd_id character varying(100) NOT NULL,
    dd_code character varying(100),
    relate_id character varying(100),
    category character varying(100) DEFAULT '尽职调查'::character varying,
    group_name character varying(100),
    key_points character varying(255),
    question character varying(255),
    question_order integer DEFAULT 0,
    field_type character varying(255) DEFAULT 'input'::character varying,
    field_config character varying(255),
    answer text,
    answer_filename character varying(255),
    answer_corresid character varying(255),
    description text,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    scenario character varying(45) DEFAULT 'VC'::character varying
);


--
-- TOC entry 228 (class 1259 OID 360471)
-- Name: crm_docrelation; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_docrelation (
    relate_id character varying(100) NOT NULL,
    source_id character varying(100) NOT NULL,
    source_name character varying(255) NOT NULL,
    relate_role character varying(100),
    target_id character varying(100) NOT NULL,
    target_type character varying(100),
    target_name character varying(255),
    relation_desc character varying(100),
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone
);


--
-- TOC entry 447 (class 1259 OID 371783)
-- Name: crm_email_analysis; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_email_analysis (
    analysis_id character varying(36) NOT NULL,
    tenant_id character varying(64) NOT NULL,
    user_id character varying(64),
    message_external_id character varying(128) NOT NULL,
    provider character varying(32),
    external_name character varying(128),
    locale character varying(16) NOT NULL,
    subject character varying(512),
    sender character varying(512),
    body_hash character varying(64) NOT NULL,
    executive_summary text,
    category character varying(128),
    urgency character varying(16),
    sentiment_label character varying(16),
    sentiment_score integer,
    sentiment_description text,
    detected_intent text,
    suggested_action text,
    entities_json text,
    suggested_ticket_json text,
    suggested_lead_json text,
    analysis_json text NOT NULL,
    model character varying(128),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    del_flag character(1) DEFAULT '0'::bpchar NOT NULL
);


--
-- TOC entry 6316 (class 0 OID 0)
-- Dependencies: 447
-- Name: TABLE crm_email_analysis; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_email_analysis IS '邮件 AI 分析结果缓存';


--
-- TOC entry 6317 (class 0 OID 0)
-- Dependencies: 447
-- Name: COLUMN crm_email_analysis.analysis_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.analysis_id IS 'UUID 主键';


--
-- TOC entry 6318 (class 0 OID 0)
-- Dependencies: 447
-- Name: COLUMN crm_email_analysis.tenant_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.tenant_id IS '租户标识（tenant 请求头）';


--
-- TOC entry 6319 (class 0 OID 0)
-- Dependencies: 447
-- Name: COLUMN crm_email_analysis.user_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.user_id IS '触发分析的用户 ID';


--
-- TOC entry 6320 (class 0 OID 0)
-- Dependencies: 447
-- Name: COLUMN crm_email_analysis.message_external_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.message_external_id IS '邮件 provider message id';


--
-- TOC entry 6321 (class 0 OID 0)
-- Dependencies: 447
-- Name: COLUMN crm_email_analysis.provider; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.provider IS 'Gmail / Outlook 等';


--
-- TOC entry 6322 (class 0 OID 0)
-- Dependencies: 447
-- Name: COLUMN crm_email_analysis.external_name; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.external_name IS '邮箱账号标识';


--
-- TOC entry 6323 (class 0 OID 0)
-- Dependencies: 447
-- Name: COLUMN crm_email_analysis.locale; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.locale IS '分析输出语言（en / zh / vi …）';


--
-- TOC entry 6324 (class 0 OID 0)
-- Dependencies: 447
-- Name: COLUMN crm_email_analysis.subject; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.subject IS '分析时邮件主题快照';


--
-- TOC entry 6325 (class 0 OID 0)
-- Dependencies: 447
-- Name: COLUMN crm_email_analysis.sender; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.sender IS '发件人快照';


--
-- TOC entry 6326 (class 0 OID 0)
-- Dependencies: 447
-- Name: COLUMN crm_email_analysis.body_hash; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.body_hash IS 'SHA-256(subject + bodyText) hex，用于检测内容变更';


--
-- TOC entry 6327 (class 0 OID 0)
-- Dependencies: 447
-- Name: COLUMN crm_email_analysis.executive_summary; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.executive_summary IS '摘要';


--
-- TOC entry 6328 (class 0 OID 0)
-- Dependencies: 447
-- Name: COLUMN crm_email_analysis.category; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.category IS '分类';


--
-- TOC entry 6329 (class 0 OID 0)
-- Dependencies: 447
-- Name: COLUMN crm_email_analysis.urgency; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.urgency IS 'Low / Medium / High';


--
-- TOC entry 6330 (class 0 OID 0)
-- Dependencies: 447
-- Name: COLUMN crm_email_analysis.sentiment_label; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.sentiment_label IS 'Positive / Neutral / Negative';


--
-- TOC entry 6331 (class 0 OID 0)
-- Dependencies: 447
-- Name: COLUMN crm_email_analysis.sentiment_score; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.sentiment_score IS '0-100';


--
-- TOC entry 6332 (class 0 OID 0)
-- Dependencies: 447
-- Name: COLUMN crm_email_analysis.sentiment_description; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.sentiment_description IS '情感描述';


--
-- TOC entry 6333 (class 0 OID 0)
-- Dependencies: 447
-- Name: COLUMN crm_email_analysis.detected_intent; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.detected_intent IS '意图';


--
-- TOC entry 6334 (class 0 OID 0)
-- Dependencies: 447
-- Name: COLUMN crm_email_analysis.suggested_action; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.suggested_action IS '建议动作';


--
-- TOC entry 6335 (class 0 OID 0)
-- Dependencies: 447
-- Name: COLUMN crm_email_analysis.entities_json; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.entities_json IS 'JSON 数组 [{category,value,subLabel}]';


--
-- TOC entry 6336 (class 0 OID 0)
-- Dependencies: 447
-- Name: COLUMN crm_email_analysis.suggested_ticket_json; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.suggested_ticket_json IS 'JSON 对象';


--
-- TOC entry 6337 (class 0 OID 0)
-- Dependencies: 447
-- Name: COLUMN crm_email_analysis.suggested_lead_json; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.suggested_lead_json IS 'JSON 对象';


--
-- TOC entry 6338 (class 0 OID 0)
-- Dependencies: 447
-- Name: COLUMN crm_email_analysis.analysis_json; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.analysis_json IS '完整 EmailAnalysisResult JSON（canonical）';


--
-- TOC entry 6339 (class 0 OID 0)
-- Dependencies: 447
-- Name: COLUMN crm_email_analysis.model; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.model IS '实际使用的模型 id';


--
-- TOC entry 6340 (class 0 OID 0)
-- Dependencies: 447
-- Name: COLUMN crm_email_analysis.del_flag; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.del_flag IS '逻辑删除 0 正常';


--
-- TOC entry 229 (class 1259 OID 360478)
-- Name: crm_email_attachments; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_email_attachments (
    id numeric(20,0) NOT NULL,
    message_id character varying(256) NOT NULL,
    attachment_id character varying(512) NOT NULL,
    filename character varying(512),
    mime_type character varying(128),
    size bigint DEFAULT 0,
    content_id character varying(255),
    is_inline smallint DEFAULT 0,
    downloaded smallint DEFAULT 0,
    storage_url text,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- TOC entry 230 (class 1259 OID 360490)
-- Name: crm_email_messages; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_email_messages (
    id numeric(20,0) NOT NULL,
    user_id character varying(255) NOT NULL,
    provider character varying(32) NOT NULL,
    message_external_id character varying(256) NOT NULL,
    thread_id character varying(128),
    subject character varying(512),
    sender character varying(512),
    recipients text,
    snippet text,
    has_attachment smallint DEFAULT 0,
    body_plain text,
    body_html text,
    labels jsonb,
    is_read integer DEFAULT 0,
    is_downloaded integer DEFAULT 0,
    received_at timestamp without time zone,
    synced_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    raw_json text,
    folder character varying(100),
    is_deleted integer DEFAULT 0,
    external_name character varying(100)
);


--
-- TOC entry 231 (class 1259 OID 360502)
-- Name: crm_event; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_event (
    event_id character varying(100) NOT NULL,
    event_type character varying(100),
    subject character varying(255),
    owner character varying(200),
    contact_id character varying(100),
    contact_name character varying(100),
    stage character varying(20),
    mobile_phone character varying(30),
    work_phone character varying(30),
    work_email character varying(30),
    event_source character varying(255) DEFAULT '业务活动'::character varying,
    website character varying(255),
    industry character varying(255),
    budget numeric(9,2) DEFAULT 0.00,
    paid numeric(9,2) DEFAULT 0.00,
    balance numeric(9,2) DEFAULT 0.00,
    location character varying(255),
    description text,
    start_time timestamp without time zone,
    end_time timestamp without time zone,
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone,
    user_id bigint,
    dept_id bigint,
    status character varying(45) DEFAULT '状态'::character varying,
    avatar character varying(200) DEFAULT '头像'::character varying
);


--
-- TOC entry 448 (class 1259 OID 371800)
-- Name: crm_follow_up_cadence; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_follow_up_cadence (
    cadence_id character varying(64) NOT NULL,
    tenant_id character varying(64) NOT NULL,
    name character varying(200) NOT NULL,
    description character varying(1000),
    enabled character(1) DEFAULT '1'::bpchar NOT NULL,
    start_trigger character varying(200),
    steps_json text,
    remark character varying(500),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    del_flag character(1) DEFAULT '0'::bpchar NOT NULL
);


--
-- TOC entry 6345 (class 0 OID 0)
-- Dependencies: 448
-- Name: TABLE crm_follow_up_cadence; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_follow_up_cadence IS 'Tenant follow-up cadences: ordered drip steps stored as JSON';


--
-- TOC entry 6346 (class 0 OID 0)
-- Dependencies: 448
-- Name: COLUMN crm_follow_up_cadence.cadence_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_follow_up_cadence.cadence_id IS 'UUID 主键';


--
-- TOC entry 6347 (class 0 OID 0)
-- Dependencies: 448
-- Name: COLUMN crm_follow_up_cadence.tenant_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_follow_up_cadence.tenant_id IS '租户标识（tenant 请求头）';


--
-- TOC entry 6348 (class 0 OID 0)
-- Dependencies: 448
-- Name: COLUMN crm_follow_up_cadence.enabled; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_follow_up_cadence.enabled IS '0 disabled, 1 enabled';


--
-- TOC entry 6349 (class 0 OID 0)
-- Dependencies: 448
-- Name: COLUMN crm_follow_up_cadence.start_trigger; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_follow_up_cadence.start_trigger IS 'Human-readable starting event, e.g. Initial Email Sent';


--
-- TOC entry 6350 (class 0 OID 0)
-- Dependencies: 448
-- Name: COLUMN crm_follow_up_cadence.steps_json; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_follow_up_cadence.steps_json IS 'JSON array of steps: [{id, order, waitDays, actionType, title, body}]';


--
-- TOC entry 6351 (class 0 OID 0)
-- Dependencies: 448
-- Name: COLUMN crm_follow_up_cadence.del_flag; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_follow_up_cadence.del_flag IS '逻辑删除 0 正常';


--
-- TOC entry 232 (class 1259 OID 360515)
-- Name: crm_forecast; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_forecast (
    forecast_id character varying(100) NOT NULL,
    period_type character varying(100) NOT NULL,
    forecast_name character varying(100) NOT NULL,
    forecast_year integer DEFAULT 2024,
    forecast_month integer DEFAULT 1,
    owner_id character varying(100),
    owner character varying(20),
    closed_only numeric(10,2) DEFAULT 0.00,
    commit numeric(10,2) DEFAULT 0.00,
    close_commit numeric(10,2) DEFAULT 0.00,
    best_case numeric(10,2) DEFAULT 0.00,
    open_pipeline numeric(10,2) DEFAULT 0.00,
    subtotal numeric(10,2) DEFAULT 0.00,
    quota numeric(10,2) DEFAULT 0.00,
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone
);


--
-- TOC entry 233 (class 1259 OID 360531)
-- Name: crm_fundinvestor; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_fundinvestor (
    investor_id character varying(100) NOT NULL,
    relate_type character varying(100),
    relate_id character varying(100),
    relate_name character varying(100),
    account_id character varying(100) NOT NULL,
    account_name character varying(100),
    claim_amount numeric(15,2) DEFAULT 0.00,
    claim_percentage numeric(15,2) DEFAULT 0.00,
    claim_date date,
    invest_amount numeric(15,2) DEFAULT 0.00,
    paid_mgrfee numeric(10,2) DEFAULT 0.00,
    shared_fee numeric(10,2) DEFAULT 0.00,
    accumulated_amount numeric(10,2) DEFAULT 0.00,
    comment_file jsonb,
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone
);


--
-- TOC entry 234 (class 1259 OID 360544)
-- Name: crm_fundpaid; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_fundpaid (
    paid_id character varying(100) NOT NULL,
    org_id character varying(100),
    full_name character varying(100),
    opty_id character varying(100),
    opty_name character varying(100),
    interest_id character varying(100),
    account_id character varying(100),
    account_name character varying(100),
    category character varying(100),
    turn character varying(100),
    invest_amount numeric(15,2) DEFAULT 0.00,
    chiyou_ziben numeric(15,2) DEFAULT 0.00,
    chiyou_percentage numeric(5,2) DEFAULT 0.00,
    chiyou_value numeric(15,2) DEFAULT 0.00,
    paid_date timestamp without time zone,
    comment character varying(255),
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone
);


--
-- TOC entry 235 (class 1259 OID 360555)
-- Name: crm_household; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_household (
    household_id character varying(100) NOT NULL,
    name character varying(100),
    member_count integer DEFAULT 0,
    territory character varying(20),
    owner character varying(20),
    head_of_household character varying(20),
    work_phone character varying(30),
    description character varying(255),
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone,
    user_id bigint,
    dept_id bigint
);


--
-- TOC entry 236 (class 1259 OID 360563)
-- Name: crm_householdmember; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_householdmember (
    member_id character varying(100) NOT NULL,
    household_id character varying(100) NOT NULL,
    household_name character varying(100),
    contact_id character varying(100) NOT NULL,
    name character varying(100),
    role character varying(20),
    owner character varying(20),
    head_of_household character varying(20),
    work_phone character varying(30),
    start_date date,
    end_date date,
    description character varying(255),
    is_active character(1) DEFAULT 'N'::bpchar,
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone
);


--
-- TOC entry 237 (class 1259 OID 360571)
-- Name: crm_interest; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_interest (
    interest_id character varying(100) NOT NULL,
    org_id character varying(100),
    full_name character varying(100),
    opty_id character varying(100),
    opty_name character varying(100),
    change_date timestamp without time zone,
    turn character varying(100),
    turn_amount numeric(15,2) DEFAULT 0.00,
    touqian_value numeric(15,2) DEFAULT 0.00,
    touhou_value numeric(15,2) DEFAULT 0.00,
    zhuce_ziben numeric(15,2) DEFAULT 0.00,
    chiyou_ziben numeric(15,2) DEFAULT 0.00,
    chiyou_percentage numeric(5,2) DEFAULT 0.00,
    chiyou_value numeric(15,2) DEFAULT 0.00,
    invest_amount numeric(15,2) DEFAULT 0.00,
    comment character varying(255),
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone,
    category character varying(100),
    account_name character varying(100),
    account_id character varying(100)
);


--
-- TOC entry 238 (class 1259 OID 360586)
-- Name: crm_itinerary; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_itinerary (
    itin_id character varying(100) NOT NULL,
    travel_id character varying(100),
    itin_from character varying(100),
    itin_to character varying(100),
    itin_date date,
    transportation character varying(100),
    hotel character varying(255),
    assumed_cost numeric(10,2) DEFAULT 0.00,
    description character varying(255),
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone
);


--
-- TOC entry 239 (class 1259 OID 360594)
-- Name: crm_lead; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_lead (
    lead_id character varying(100) NOT NULL,
    lead_type character varying(100),
    opty_id character varying(100),
    opty_name character varying(100),
    org_id character varying(100),
    lead_name character varying(100) NOT NULL,
    full_name character varying(100),
    owner character varying(100),
    contact_id character varying(100),
    contact_name character varying(100),
    stage character varying(20),
    work_phone character varying(100),
    mobile_phone character varying(100),
    work_email character varying(100),
    lead_source character varying(255) DEFAULT '业务活动'::character varying,
    lead_rating character varying(100) DEFAULT '热情'::character varying,
    website character varying(255),
    territory character varying(255),
    industry character varying(255),
    employee_count integer DEFAULT 0,
    revenue numeric(15,2) DEFAULT 0.00,
    description character varying(255),
    days_inactive integer DEFAULT 0,
    partner character varying(100),
    convert_time timestamp without time zone,
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone,
    user_id bigint,
    dept_id bigint,
    province character varying(45),
    city character varying(45),
    sid character varying(100),
    custom_field text,
    number_of_founders bigint DEFAULT 0,
    funders character varying(255),
    funders_title character varying(255),
    number_of_funding_rounds bigint DEFAULT 0,
    last_funding_type character varying(45),
    total_funding_amount numeric(15,2) DEFAULT 0.00,
    funding_currency character varying(45) DEFAULT 'CNY'::character varying,
    founded_at date,
    status character varying(45),
    short_description character varying(255),
    avatar character varying(200),
    tag jsonb,
    survey_json jsonb,
    last_followup_at timestamp with time zone,
    assigned_at timestamp with time zone
);

CREATE INDEX IF NOT EXISTS idx_crm_lead_assigned_followup
    ON public.crm_lead (status, last_followup_at)
    WHERE lower(trim(coalesce(status, ''))) = 'assigned';

CREATE TABLE IF NOT EXISTS public.crm_lead_pool_config (
    config_id character varying(36) NOT NULL PRIMARY KEY,
    claim_daily_limit integer NOT NULL DEFAULT 30,
    protect_days integer NOT NULL DEFAULT 7,
    recycle_enabled boolean NOT NULL DEFAULT true,
    default_pool_org_scope character varying(32) NOT NULL DEFAULT 'dept',
    qualify_template_id character varying(36),
    idle_to_pool_hours integer NOT NULL DEFAULT 72,
    max_hold_count integer NOT NULL DEFAULT 100,
    create_time timestamp without time zone,
    update_time timestamp without time zone
);

CREATE TABLE IF NOT EXISTS public.crm_lead_dedupe_config (
    config_id character varying(36) NOT NULL PRIMARY KEY,
    lead_match_fields text NOT NULL DEFAULT '["mobilePhone","wecomExternalUserid"]',
    contact_match_fields text NOT NULL DEFAULT '["mobilePhone","wecomExternalUserid"]',
    match_mode character varying(8) NOT NULL DEFAULT 'any',
    create_time timestamp without time zone,
    update_time timestamp without time zone
);

CREATE TABLE IF NOT EXISTS public.crm_lead_convert_config (
    config_id character varying(36) NOT NULL PRIMARY KEY,
    tenant_id character varying(64) NOT NULL UNIQUE,
    auto_convert_on_first_visit boolean NOT NULL DEFAULT true,
    auto_convert_on_check_in boolean NOT NULL DEFAULT true,
    auto_convert_on_registration boolean NOT NULL DEFAULT false,
    merge_on_duplicate_phone boolean NOT NULL DEFAULT true,
    default_org_id character varying(36),
    contact_owner_mode character varying(32) NOT NULL DEFAULT 'keep_lead_owner',
    contact_owner_user_id bigint,
    create_time timestamp without time zone,
    update_time timestamp without time zone
);


--
-- TOC entry 463 (class 1259 OID 379602)
-- Name: crm_mcp_api_key; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_mcp_api_key (
    id bigint NOT NULL,
    tenant_id character varying(64) NOT NULL,
    name character varying(128) NOT NULL,
    key_prefix character varying(32) NOT NULL,
    key_hash character varying(128) NOT NULL,
    bound_user_id character varying(64) NOT NULL,
    allowed_tools jsonb,
    enabled smallint DEFAULT 1 NOT NULL,
    expires_at timestamp with time zone,
    service_token_enc text,
    create_by character varying(64) DEFAULT NULL::character varying,
    create_time timestamp with time zone,
    update_by character varying(64) DEFAULT NULL::character varying,
    update_time timestamp with time zone
);


--
-- TOC entry 6361 (class 0 OID 0)
-- Dependencies: 463
-- Name: TABLE crm_mcp_api_key; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_mcp_api_key IS '入站 MCP Server API Key';


--
-- TOC entry 6362 (class 0 OID 0)
-- Dependencies: 463
-- Name: COLUMN crm_mcp_api_key.key_hash; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_mcp_api_key.key_hash IS 'secret 部分 SHA-256 hex，禁止存明文';


--
-- TOC entry 6363 (class 0 OID 0)
-- Dependencies: 463
-- Name: COLUMN crm_mcp_api_key.allowed_tools; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_mcp_api_key.allowed_tools IS 'JSON 字符串数组，null 表示全量 Agent 工具';


--
-- TOC entry 6364 (class 0 OID 0)
-- Dependencies: 463
-- Name: COLUMN crm_mcp_api_key.service_token_enc; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_mcp_api_key.service_token_enc IS 'AES 加密的服务账号 JWT';


--
-- TOC entry 462 (class 1259 OID 379601)
-- Name: crm_mcp_api_key_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_mcp_api_key_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 6366 (class 0 OID 0)
-- Dependencies: 462
-- Name: crm_mcp_api_key_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_mcp_api_key_id_seq OWNED BY public.crm_mcp_api_key.id;


--
-- TOC entry 465 (class 1259 OID 379619)
-- Name: crm_mcp_audit_log; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_mcp_audit_log (
    id bigint NOT NULL,
    tenant_id character varying(64) NOT NULL,
    api_key_id bigint,
    bound_user_id character varying(64) DEFAULT NULL::character varying,
    tool_name character varying(128) NOT NULL,
    ok smallint DEFAULT 1 NOT NULL,
    error_code character varying(64) DEFAULT NULL::character varying,
    latency_ms integer,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- TOC entry 6368 (class 0 OID 0)
-- Dependencies: 465
-- Name: TABLE crm_mcp_audit_log; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_mcp_audit_log IS '入站 MCP tools/call 审计（Next 异步上报）';


--
-- TOC entry 464 (class 1259 OID 379618)
-- Name: crm_mcp_audit_log_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_mcp_audit_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 6370 (class 0 OID 0)
-- Dependencies: 464
-- Name: crm_mcp_audit_log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_mcp_audit_log_id_seq OWNED BY public.crm_mcp_audit_log.id;


--
-- TOC entry 455 (class 1259 OID 379491)
-- Name: crm_mcp_server; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_mcp_server (
    id bigint NOT NULL,
    tenant_id character varying(64) NOT NULL,
    name character varying(128) NOT NULL,
    url character varying(1024) NOT NULL,
    transport character varying(16) DEFAULT 'http'::character varying NOT NULL,
    auth_type character varying(16) DEFAULT 'none'::character varying NOT NULL,
    auth_secret_enc text,
    allowed_tools text,
    enabled smallint DEFAULT 1 NOT NULL,
    create_by character varying(64) DEFAULT NULL::character varying,
    create_time timestamp with time zone,
    update_by character varying(64) DEFAULT NULL::character varying,
    update_time timestamp with time zone
);


--
-- TOC entry 6372 (class 0 OID 0)
-- Dependencies: 455
-- Name: TABLE crm_mcp_server; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_mcp_server IS '租户 MCP Server 配置';


--
-- TOC entry 6373 (class 0 OID 0)
-- Dependencies: 455
-- Name: COLUMN crm_mcp_server.auth_secret_enc; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_mcp_server.auth_secret_enc IS 'AES 加密密文，管理接口不返回明文';


--
-- TOC entry 6374 (class 0 OID 0)
-- Dependencies: 455
-- Name: COLUMN crm_mcp_server.allowed_tools; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_mcp_server.allowed_tools IS 'JSON 字符串数组，null 表示全部 tool';


--
-- TOC entry 454 (class 1259 OID 379490)
-- Name: crm_mcp_server_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_mcp_server_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 6376 (class 0 OID 0)
-- Dependencies: 454
-- Name: crm_mcp_server_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_mcp_server_id_seq OWNED BY public.crm_mcp_server.id;


--
-- TOC entry 240 (class 1259 OID 360610)
-- Name: crm_oauth_connections; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_oauth_connections (
    id numeric(20,0) NOT NULL,
    user_id character varying(255) NOT NULL,
    integration_type character varying(100) DEFAULT 'Email'::character varying,
    provider character varying(50) NOT NULL,
    external_id character varying(255),
    external_name character varying(255),
    access_token text,
    refresh_token text,
    token_type character varying(50),
    scope text,
    expires_at timestamp without time zone,
    metadata jsonb,
    connected_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    last_refreshed_at timestamp without time zone,
    revoked smallint DEFAULT 0,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    status character varying(45)
);


--
-- TOC entry 241 (class 1259 OID 360622)
-- Name: crm_onepage; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_onepage (
    page_id character varying(100) NOT NULL,
    page_type character varying(100),
    relate_id character varying(100),
    relate_name character varying(100),
    page_content jsonb,
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone
);


--
-- TOC entry 242 (class 1259 OID 360629)
-- Name: crm_operator; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_operator (
    operator_id character varying(100) NOT NULL,
    operator_category character varying(100) NOT NULL,
    primary_territory character varying(100) DEFAULT '全国'::character varying,
    operator_identity character varying(20),
    operator_password character varying(100),
    operator_name character varying(20),
    "position" character varying(20),
    avatar character varying(200),
    title character varying(20),
    email character varying(20),
    phone character varying(20),
    report_to character varying(20),
    report_toid character varying(100),
    is_active character(1) DEFAULT 'N'::bpchar,
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone
);


--
-- TOC entry 243 (class 1259 OID 360638)
-- Name: crm_opty; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_opty (
    opty_id character varying(100) NOT NULL,
    opty_type character varying(100) NOT NULL,
    org_id character varying(100),
    account_id character varying(100),
    lead_id character varying(100),
    lead_name character varying(100),
    name character varying(255),
    full_name character varying(255),
    account_name character varying(255),
    contact_id character varying(100),
    contact_name character varying(200),
    owner character varying(200),
    amount numeric(10,2) DEFAULT 0.00,
    sales_type character varying(200),
    stage character varying(255),
    win_probability numeric(4,2) DEFAULT 0.00,
    forecast_category character varying(20),
    work_phone character varying(30),
    mobile_phone character varying(30),
    work_email character varying(255),
    opty_source character varying(30) DEFAULT '业务活动'::character varying,
    opty_rating character varying(30) DEFAULT '热情'::character varying,
    website character varying(255),
    territory character varying(255),
    industry character varying(255),
    employee_count integer DEFAULT 0,
    revenue numeric(15,2) DEFAULT 0.00,
    description text,
    is_mustwin character varying(10) DEFAULT 'No'::character varying,
    close_date date,
    contract_enddate date,
    close_reason character varying(100),
    reason_type character varying(100),
    close_comment text,
    custom_field text,
    create_by character varying(255),
    create_time timestamp without time zone,
    update_by character varying(255),
    update_time timestamp without time zone,
    start_date date,
    end_date date,
    user_id bigint,
    dept_id bigint,
    sid character varying(45),
    partner character varying(100),
    avatar character varying(200),
    code character varying(45),
    initial_status character varying(100) DEFAULT '待定'::character varying,
    dd_status character varying(100),
    invest_decision character varying(100),
    opty_process jsonb,
    treatment_item_code character varying(128),
    case_group_id character varying(128),
    closed_won_basis character varying(64),
    down_payment_amount numeric(18,2),
    agreement_signed_at timestamp with time zone
);


--
-- TOC entry 6381 (class 0 OID 0)
-- Dependencies: 243
-- Name: COLUMN crm_opty.treatment_item_code; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_opty.treatment_item_code IS '治疗项目编码（字典 crm_treatment_item.dict_value）';


--
-- TOC entry 6382 (class 0 OID 0)
-- Dependencies: 243
-- Name: COLUMN crm_opty.case_group_id; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_opty.case_group_id IS '病例分组 ID';


--
-- TOC entry 6383 (class 0 OID 0)
-- Dependencies: 243
-- Name: COLUMN crm_opty.closed_won_basis; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_opty.closed_won_basis IS '赢单依据（字典 crm_closed_won_basis）';


--
-- TOC entry 6384 (class 0 OID 0)
-- Dependencies: 243
-- Name: COLUMN crm_opty.down_payment_amount; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_opty.down_payment_amount IS '已收首付金额';


--
-- TOC entry 6385 (class 0 OID 0)
-- Dependencies: 243
-- Name: COLUMN crm_opty.agreement_signed_at; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_opty.agreement_signed_at IS '治疗协议签署时间';


--
-- TOC entry 244 (class 1259 OID 360653)
-- Name: crm_optyacceptance; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_optyacceptance (
    accept_id character varying(100) NOT NULL,
    stage_id character varying(100),
    stage_name character varying(100),
    task_name character varying(100),
    casetype_id character varying(100),
    task_checked integer DEFAULT 0,
    task_order integer DEFAULT 0,
    description character varying(255),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


--
-- TOC entry 245 (class 1259 OID 360662)
-- Name: crm_optyrelation; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_optyrelation (
    relate_id character varying(100) NOT NULL,
    source_id character varying(100) NOT NULL,
    source_name character varying(255) NOT NULL,
    relate_role character varying(100),
    target_id character varying(100) NOT NULL,
    target_type character varying(100),
    target_name character varying(255),
    relation_desc character varying(255),
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone,
    custom_field jsonb,
    status character varying(45),
    priority integer DEFAULT 0,
    owner character varying(45),
    user_id bigint,
    latest_update character varying(255),
    stage character varying(100),
    open_count bigint DEFAULT 0,
    open_status character varying(45) DEFAULT '未打开'::character varying,
    feedback character varying(100),
    validation_code character varying(45)
);


--
-- TOC entry 246 (class 1259 OID 360672)
-- Name: crm_optytask; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_optytask (
    task_id character varying(100) NOT NULL,
    opty_id character varying(100),
    opty_name character varying(100),
    stage_id character varying(100),
    stage_name character varying(100),
    task_name character varying(100),
    casetype_id character varying(100),
    task_checked integer DEFAULT 0,
    task_order integer DEFAULT 0,
    description character varying(255),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


--
-- TOC entry 247 (class 1259 OID 360681)
-- Name: crm_org; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_org (
    org_id character varying(100) NOT NULL,
    uuid character varying(30),
    category character varying(100),
    register_capital numeric(12,2) DEFAULT 0.00,
    full_name character varying(100),
    representative character varying(50),
    startup_date date,
    approved_date date,
    oper_start_date date,
    register_bureau character varying(100),
    register_status character varying(100),
    register_address character varying(100),
    short_name character varying(100),
    parent_org character varying(100),
    website character varying(50),
    avatar character varying(200),
    ticker_symbol character varying(10),
    phone_number character varying(20),
    industry character varying(100),
    territory character varying(20),
    owner character varying(20),
    employee_count integer DEFAULT 0,
    active_account_count integer DEFAULT 0,
    active_opty_count integer DEFAULT 0,
    active_opty_amount numeric(10,2) DEFAULT 0.00,
    revenue numeric(15,2) DEFAULT 0.00,
    register_comments text,
    is_target character(1) DEFAULT 'N'::bpchar,
    is_customer character(1) DEFAULT 'N'::bpchar,
    status character varying(10),
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone,
    custom_field jsonb,
    user_id bigint,
    dept_id bigint,
    resources jsonb,
    requirement jsonb,
    tag jsonb,
    english_name character varying(255),
    stakeholders jsonb,
    changes jsonb,
    rr_description text,
    funders character varying(255),
    funders_title character varying(255),
    number_of_funding_rounds bigint DEFAULT 0,
    last_funding_type character varying(45),
    last_funding_date date,
    last_funding_amount numeric(15,2) DEFAULT 0.00,
    total_funding_amount numeric(15,2) DEFAULT 0.00,
    funding_currency character varying(45) DEFAULT 'CNY'::character varying,
    short_description character varying(255),
    last_funding_currency character varying(10),
    top_five_investors character varying(255),
    number_of_lead_investors bigint DEFAULT 0,
    number_of_investors bigint DEFAULT 0,
    number_of_acquisitions bigint DEFAULT 0,
    acquisition_status character varying(45),
    ipo_status character varying(45),
    ipo_date date,
    lp_process jsonb,
    stage character varying(100) DEFAULT '适当性匹配'::character varying,
    latest_value numeric(15,2) DEFAULT 0.00
);


--
-- TOC entry 248 (class 1259 OID 360705)
-- Name: crm_ownerhistory; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_ownerhistory (
    oh_id character varying(100) NOT NULL,
    oh_category character varying(100) NOT NULL,
    target_id character varying(100) NOT NULL,
    owner character varying(100) NOT NULL,
    change_reason character varying(255),
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone
);


--
-- TOC entry 249 (class 1259 OID 360712)
-- Name: crm_partner; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_partner (
    partner_id character varying(100) NOT NULL,
    name character varying(100),
    owner character varying(20),
    owner_id character varying(20),
    contact_name character varying(20),
    mobile_phone character varying(30),
    work_email character varying(30),
    website character varying(30),
    territory character varying(20),
    industry character varying(100),
    employee_count integer DEFAULT 0,
    revenue numeric(15,2) DEFAULT 0.00,
    pipeline_open numeric(10,2) DEFAULT 0.00,
    won numeric(10,2) DEFAULT 0.00,
    quota numeric(10,2) DEFAULT 0.00,
    quota_attained numeric(10,2) DEFAULT 0.00,
    description character varying(255),
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone,
    del_flag integer DEFAULT 1
);


--
-- TOC entry 429 (class 1259 OID 371280)
-- Name: crm_patient_value; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_patient_value (
    contact_id character varying(64) NOT NULL,
    account_id character varying(64),
    tenant_id character varying(64) NOT NULL,
    currency character varying(8) DEFAULT 'CNY'::character varying,
    booked_total numeric(18,2) DEFAULT 0,
    inflight_total numeric(18,2) DEFAULT 0,
    refund_total numeric(18,2) DEFAULT 0,
    net_clv numeric(18,2) DEFAULT 0,
    paid_total numeric(18,2) DEFAULT 0,
    implant_total numeric(18,2) DEFAULT 0,
    ortho_total numeric(18,2) DEFAULT 0,
    restorative_total numeric(18,2) DEFAULT 0,
    preventive_total numeric(18,2) DEFAULT 0,
    pediatric_total numeric(18,2) DEFAULT 0,
    acquisition_cost numeric(18,2) DEFAULT 0,
    first_visit_at timestamp with time zone,
    last_visit_at timestamp with time zone,
    visit_count_12m integer DEFAULT 0,
    avg_visit_amount numeric(18,2) DEFAULT 0,
    days_since_last_visit integer,
    next_recall_due_at timestamp with time zone,
    retention_coefficient numeric(5,4) DEFAULT 0.6,
    pltv_12m numeric(18,2) DEFAULT 0,
    pltv_36m numeric(18,2) DEFAULT 0,
    churn_risk_score numeric(5,4) DEFAULT 0,
    tier character varying(16) DEFAULT 'BRONZE'::character varying,
    calculated_at timestamp with time zone DEFAULT now(),
    del_flag smallint DEFAULT 0
);


--
-- TOC entry 250 (class 1259 OID 360726)
-- Name: crm_paymentterm; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_paymentterm (
    term_id character varying(100) NOT NULL,
    term_name character varying(255),
    term_date date,
    term_index integer DEFAULT 1,
    paid_amount numeric(10,2) DEFAULT 0.00,
    term_amount numeric(10,2) DEFAULT 0.00,
    contract_id character varying(100) NOT NULL,
    contract_name character varying(255),
    account_id character varying(100),
    account_name character varying(255),
    owner_id character varying(100),
    owner_name character varying(100),
    is_paid character(1) DEFAULT 'N'::bpchar,
    operator_name character varying(45),
    collect_date timestamp without time zone,
    description character varying(255),
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone,
    user_id bigint,
    dept_id bigint,
    start_date date,
    end_date date,
    refund_amount numeric(18,2) DEFAULT 0,
    refund_reason character varying(255),
    refunded_at timestamp with time zone,
    refunded_by character varying(64),
    CONSTRAINT crm_paymentterm_refund_amount_nonneg CHECK ((refund_amount >= (0)::numeric))
);


--
-- TOC entry 6394 (class 0 OID 0)
-- Dependencies: 250
-- Name: COLUMN crm_paymentterm.refund_amount; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_paymentterm.refund_amount IS 'Refund amount; must be <= paid_amount';


--
-- TOC entry 251 (class 1259 OID 360737)
-- Name: crm_privacy; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_privacy (
    privacy_id character varying(100) NOT NULL,
    privacy_type character varying(100) NOT NULL,
    relate_id character varying(100) NOT NULL,
    relate_name character varying(100),
    privacy_data jsonb,
    owner character varying(100),
    owner_id character varying(100),
    sharing jsonb,
    is_public character(1) DEFAULT 'N'::bpchar,
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone
);


--
-- TOC entry 252 (class 1259 OID 360745)
-- Name: crm_product; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_product (
    product_id character varying(100) NOT NULL,
    product_name character varying(100),
    product_pinyin character varying(100),
    product_label character varying(100),
    product_code character varying(100),
    product_local_code character varying(100),
    avatar character varying(100),
    category character varying(20),
    status character varying(10),
    unit character varying(45),
    spec character varying(45),
    model character varying(45),
    brand character varying(45),
    barcode character varying(20),
    producer character varying(45),
    purchase_category character varying(10),
    approved_code character varying(20),
    provider character varying(30),
    min_count character varying(30),
    outbound_price numeric(10,2) DEFAULT 0.00,
    is_own character varying(10),
    is_own_price character varying(10),
    description character varying(255),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    inv_id character varying(100),
    inv_name character varying(100),
    wecom_product_id character varying(64) DEFAULT NULL::character varying
);


--
-- TOC entry 253 (class 1259 OID 360753)
-- Name: crm_pulse; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_pulse (
    pulse_id character varying(100) NOT NULL,
    parent_id character varying(100),
    order_id integer DEFAULT 0,
    pulse_type character varying(100) DEFAULT '消息'::character varying,
    relate_type character varying(100) DEFAULT '公共'::character varying,
    relate_id character varying(100),
    relate_name character varying(100),
    message_data text,
    creator_id character varying(100),
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone,
    avatar character varying(200),
    user_id bigint,
    dept_id bigint
);


--
-- TOC entry 254 (class 1259 OID 360763)
-- Name: crm_quote; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_quote (
    quote_id character varying(100) NOT NULL,
    quote_code character varying(100) NOT NULL,
    opty_id character varying(100) NOT NULL,
    opty_name character varying(100) NOT NULL,
    quote_name character varying(255),
    quote_type character varying(100),
    amount numeric(10,2) DEFAULT 0.00,
    discount numeric(10,2) DEFAULT 0.00,
    tax_ratio numeric(10,2) DEFAULT 0.00,
    tax_amount numeric(10,2) DEFAULT 0.00,
    discount_amount numeric(10,2) DEFAULT 0.00,
    total_amount numeric(10,2) DEFAULT 0.00,
    account_id character varying(100),
    account_name character varying(100),
    contact_id character varying(100),
    contact_name character varying(100),
    contact_phone character varying(100),
    owner_id character varying(100),
    owner_name character varying(100),
    payment_terms integer DEFAULT 12,
    quote_date date,
    expired_date date,
    effective_time timestamp without time zone,
    end_time timestamp without time zone,
    approval_status character varying(100),
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone,
    remark character varying(500) DEFAULT NULL::character varying
);


--
-- TOC entry 6399 (class 0 OID 0)
-- Dependencies: 254
-- Name: COLUMN crm_quote.remark; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_quote.remark IS '备注';


--
-- TOC entry 255 (class 1259 OID 360777)
-- Name: crm_quoteoption; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_quoteoption (
    option_id character varying(100) NOT NULL,
    quote_id character varying(100) NOT NULL,
    quote_code character varying(100) NOT NULL,
    quote_name character varying(255),
    quote_type character varying(100),
    total_amount numeric(10,2) DEFAULT 0.00,
    product_id character varying(100),
    product_name character varying(100),
    product_desc character varying(255),
    unit_price numeric(10,2) DEFAULT 0.00,
    quantity integer DEFAULT 1,
    discount numeric(10,2) DEFAULT 0.00,
    owner_id character varying(100),
    owner_name character varying(100),
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone
);


--
-- TOC entry 256 (class 1259 OID 360788)
-- Name: crm_reportdetail; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_reportdetail (
    detail_id character varying(100) NOT NULL,
    report_id character varying(100) NOT NULL,
    day_order character varying(100) NOT NULL,
    cold_call integer DEFAULT 0,
    followup_call integer DEFAULT 0,
    email_sent integer DEFAULT 0,
    meeting_arranged integer DEFAULT 0,
    visits_completed integer DEFAULT 0,
    lead_generated integer DEFAULT 0,
    deal_closed integer DEFAULT 0,
    sales_revenue numeric(10,2) DEFAULT 0.00,
    target_amount numeric(10,2) DEFAULT 0.00,
    variance numeric(10,2) DEFAULT 0.00,
    notes character varying(255) NOT NULL,
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone,
    user_id bigint,
    dept_id bigint,
    report_order integer DEFAULT 0
);


--
-- TOC entry 257 (class 1259 OID 360806)
-- Name: crm_research; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_research (
    research_id character varying(100) NOT NULL,
    category character varying(100) NOT NULL,
    subject character varying(100) NOT NULL,
    release_date timestamp without time zone,
    industry character varying(255) NOT NULL,
    favorite integer DEFAULT 0,
    ai_summary jsonb,
    catalog text,
    comment_file jsonb,
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone
);


--
-- TOC entry 258 (class 1259 OID 360814)
-- Name: crm_respondents; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_respondents (
    id character varying(100) NOT NULL,
    relate_id character varying(100),
    relate_name character varying(255),
    contact_id character varying(100),
    session_id character varying(255),
    metadata jsonb,
    description character varying(255),
    status character varying(255),
    survey_json jsonb,
    started_at timestamp without time zone,
    completed_at timestamp without time zone,
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone
);


--
-- TOC entry 259 (class 1259 OID 360821)
-- Name: crm_responses; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_responses (
    id character varying(100) NOT NULL,
    relate_id character varying(100),
    relate_name character varying(255),
    respondent_id character varying(100),
    response_json jsonb,
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone
);


--
-- TOC entry 260 (class 1259 OID 360828)
-- Name: crm_salesgoal; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_salesgoal (
    goal_id character varying(100) NOT NULL,
    goal_name character varying(100) DEFAULT '用户任务'::character varying,
    goal_type character varying(100),
    kpi_code character varying(64),
    period_id character varying(100),
    period_name character varying(100),
    period_type character varying(100),
    month_id character varying(100),
    month_name character varying(100),
    quarter_id character varying(100),
    quarter_name character varying(100),
    fiscal_id character varying(100),
    fiscal_name character varying(100),
    is_active integer DEFAULT 1,
    goal_value numeric(15,2) DEFAULT 0.00,
    owner character varying(100),
    owner_id character varying(100),
    comment character varying(255),
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone,
    user_id bigint,
    dept_id bigint
);


--
-- TOC entry 445 (class 1259 OID 371695)
-- Name: crm_semantic_profile; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_semantic_profile (
    profile_id character varying(64) NOT NULL,
    tenant_id character varying(64) NOT NULL,
    industry_key character varying(32) DEFAULT 'generic'::character varying NOT NULL,
    enabled character(1) DEFAULT '0'::bpchar NOT NULL,
    profile_version integer DEFAULT 1 NOT NULL,
    profile_json text,
    del_flag character(1) DEFAULT '0'::bpchar NOT NULL,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    remark character varying(500)
);


--
-- TOC entry 6407 (class 0 OID 0)
-- Dependencies: 445
-- Name: TABLE crm_semantic_profile; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_semantic_profile IS '租户语义层配置';


--
-- TOC entry 6408 (class 0 OID 0)
-- Dependencies: 445
-- Name: COLUMN crm_semantic_profile.profile_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_semantic_profile.profile_id IS 'UUID 主键';


--
-- TOC entry 6409 (class 0 OID 0)
-- Dependencies: 445
-- Name: COLUMN crm_semantic_profile.tenant_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_semantic_profile.tenant_id IS '租户标识（tenant 请求头）';


--
-- TOC entry 6410 (class 0 OID 0)
-- Dependencies: 445
-- Name: COLUMN crm_semantic_profile.industry_key; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_semantic_profile.industry_key IS 'dental / generic / custom';


--
-- TOC entry 6411 (class 0 OID 0)
-- Dependencies: 445
-- Name: COLUMN crm_semantic_profile.enabled; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_semantic_profile.enabled IS '0 停用 / 1 启用（停用时 AI 不注入语义块）';


--
-- TOC entry 6412 (class 0 OID 0)
-- Dependencies: 445
-- Name: COLUMN crm_semantic_profile.profile_version; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_semantic_profile.profile_version IS 'profile_json schema 版本';


--
-- TOC entry 6413 (class 0 OID 0)
-- Dependencies: 445
-- Name: COLUMN crm_semantic_profile.profile_json; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_semantic_profile.profile_json IS '租户覆盖 JSON 字符串';


--
-- TOC entry 6414 (class 0 OID 0)
-- Dependencies: 445
-- Name: COLUMN crm_semantic_profile.del_flag; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_semantic_profile.del_flag IS '逻辑删除 0 正常';


--
-- TOC entry 261 (class 1259 OID 360838)
-- Name: crm_sentiment; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_sentiment (
    id character varying(100) NOT NULL,
    relate_id character varying(100),
    relate_type character varying(30),
    relate_name character varying(255),
    respondent_id character varying(255),
    sentiment character varying(255) DEFAULT 'draft'::character varying,
    keywords text,
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone
);


--
-- TOC entry 438 (class 1259 OID 371448)
-- Name: crm_sf_express_config; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_sf_express_config (
    id bigint NOT NULL,
    customer_code character varying(64) NOT NULL,
    checkword_enc text NOT NULL,
    monthly_card character varying(64) DEFAULT NULL::character varying,
    sandbox boolean DEFAULT true NOT NULL,
    connected smallint DEFAULT 0 NOT NULL,
    last_error character varying(512) DEFAULT NULL::character varying,
    create_by character varying(64) DEFAULT NULL::character varying,
    create_time timestamp with time zone,
    update_by character varying(64) DEFAULT NULL::character varying,
    update_time timestamp with time zone,
    tenant_id character varying(64),
    updated_by character varying(64),
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- TOC entry 6417 (class 0 OID 0)
-- Dependencies: 438
-- Name: TABLE crm_sf_express_config; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_sf_express_config IS '顺丰租户丰桥凭证配置';


--
-- TOC entry 6418 (class 0 OID 0)
-- Dependencies: 438
-- Name: COLUMN crm_sf_express_config.customer_code; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_sf_express_config.customer_code IS '顾客编码 partnerID';


--
-- TOC entry 6419 (class 0 OID 0)
-- Dependencies: 438
-- Name: COLUMN crm_sf_express_config.checkword_enc; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_sf_express_config.checkword_enc IS '校验码 AES 密文';


--
-- TOC entry 6420 (class 0 OID 0)
-- Dependencies: 438
-- Name: COLUMN crm_sf_express_config.monthly_card; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_sf_express_config.monthly_card IS '月结卡号';


--
-- TOC entry 6421 (class 0 OID 0)
-- Dependencies: 438
-- Name: COLUMN crm_sf_express_config.sandbox; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_sf_express_config.sandbox IS '是否沙箱环境';


--
-- TOC entry 6422 (class 0 OID 0)
-- Dependencies: 438
-- Name: COLUMN crm_sf_express_config.tenant_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_sf_express_config.tenant_id IS '租户 ID';


--
-- TOC entry 437 (class 1259 OID 371447)
-- Name: crm_sf_express_config_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_sf_express_config_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 6424 (class 0 OID 0)
-- Dependencies: 437
-- Name: crm_sf_express_config_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_sf_express_config_id_seq OWNED BY public.crm_sf_express_config.id;


--
-- TOC entry 440 (class 1259 OID 371466)
-- Name: crm_sf_express_token; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_sf_express_token (
    id bigint NOT NULL,
    tenant_id character varying(64),
    access_token_enc character varying(512),
    expires_at timestamp with time zone,
    api_response_id character varying(64),
    last_refresh_at timestamp with time zone DEFAULT now() NOT NULL,
    last_error character varying(512)
);


--
-- TOC entry 6426 (class 0 OID 0)
-- Dependencies: 440
-- Name: TABLE crm_sf_express_token; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_sf_express_token IS '顺丰 OAuth2 accessToken';


--
-- TOC entry 6427 (class 0 OID 0)
-- Dependencies: 440
-- Name: COLUMN crm_sf_express_token.access_token_enc; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_sf_express_token.access_token_enc IS 'accessToken AES 密文';


--
-- TOC entry 6428 (class 0 OID 0)
-- Dependencies: 440
-- Name: COLUMN crm_sf_express_token.expires_at; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_sf_express_token.expires_at IS '过期时间';


--
-- TOC entry 6429 (class 0 OID 0)
-- Dependencies: 440
-- Name: COLUMN crm_sf_express_token.api_response_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_sf_express_token.api_response_id IS '顺丰 apiResponseID';


--
-- TOC entry 6430 (class 0 OID 0)
-- Dependencies: 440
-- Name: COLUMN crm_sf_express_token.last_refresh_at; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_sf_express_token.last_refresh_at IS '最近换票时间';


--
-- TOC entry 6431 (class 0 OID 0)
-- Dependencies: 440
-- Name: COLUMN crm_sf_express_token.last_error; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_sf_express_token.last_error IS '最近一次 OAuth 失败摘要';


--
-- TOC entry 439 (class 1259 OID 371465)
-- Name: crm_sf_express_token_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_sf_express_token_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 6433 (class 0 OID 0)
-- Dependencies: 439
-- Name: crm_sf_express_token_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_sf_express_token_id_seq OWNED BY public.crm_sf_express_token.id;


--
-- TOC entry 262 (class 1259 OID 360846)
-- Name: crm_stagehistory; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_stagehistory (
    sh_id character varying(100) NOT NULL,
    stage character varying(100) NOT NULL,
    opty_id character varying(100) NOT NULL,
    opty_name character varying(100),
    opty_amount numeric(10,2),
    owner character varying(100),
    duration integer DEFAULT 0,
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone
);


--
-- TOC entry 475 (class 1259 OID 380101)
-- Name: crm_subject_map; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_subject_map (
    id bigint NOT NULL,
    parent_subject_type character varying(50) NOT NULL,
    parent_subject_id character varying(64) NOT NULL,
    child_subject_type character varying(50) NOT NULL,
    child_subject_id character varying(64) NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- TOC entry 6436 (class 0 OID 0)
-- Dependencies: 475
-- Name: TABLE crm_subject_map; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_subject_map IS 'Subject 继承映射表';


--
-- TOC entry 6437 (class 0 OID 0)
-- Dependencies: 475
-- Name: COLUMN crm_subject_map.parent_subject_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_subject_map.parent_subject_type IS '父主体类型（如 Contact）';


--
-- TOC entry 6438 (class 0 OID 0)
-- Dependencies: 475
-- Name: COLUMN crm_subject_map.parent_subject_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_subject_map.parent_subject_id IS '父主体 ID';


--
-- TOC entry 6439 (class 0 OID 0)
-- Dependencies: 475
-- Name: COLUMN crm_subject_map.child_subject_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_subject_map.child_subject_type IS '子主体类型（如 Lead）';


--
-- TOC entry 6440 (class 0 OID 0)
-- Dependencies: 475
-- Name: COLUMN crm_subject_map.child_subject_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_subject_map.child_subject_id IS '子主体 ID';


--
-- TOC entry 6441 (class 0 OID 0)
-- Dependencies: 475
-- Name: COLUMN crm_subject_map.create_time; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_subject_map.create_time IS '创建时间';


--
-- TOC entry 474 (class 1259 OID 380100)
-- Name: crm_subject_map_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_subject_map_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 6443 (class 0 OID 0)
-- Dependencies: 474
-- Name: crm_subject_map_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_subject_map_id_seq OWNED BY public.crm_subject_map.id;


--
-- TOC entry 263 (class 1259 OID 360854)
-- Name: crm_surveyresult; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_surveyresult (
    result_id character varying(100) NOT NULL,
    template_id character varying(100),
    template_name character varying(255),
    relate_id character varying(100),
    relate_type character varying(30),
    relate_name character varying(255),
    survey_name character varying(255),
    survey_attachment jsonb,
    survey_result jsonb,
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone
);


--
-- TOC entry 264 (class 1259 OID 360861)
-- Name: crm_surveys; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_surveys (
    id character varying(100) NOT NULL,
    relate_id character varying(100),
    relate_type character varying(30),
    relate_name character varying(255),
    title character varying(255),
    description text,
    status character varying(255) DEFAULT 'draft'::character varying,
    survey_json jsonb,
    total_responses integer DEFAULT 0,
    completion_rate numeric(10,2) DEFAULT 0.00,
    nps_score numeric(5,2) DEFAULT 0.00,
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone,
    sent integer DEFAULT 0,
    limit_one integer DEFAULT 0
);


--
-- TOC entry 265 (class 1259 OID 360874)
-- Name: crm_task; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_task (
    task_id character varying(100) NOT NULL,
    activity_id character varying(100),
    task_subject character varying(255) NOT NULL,
    task_type character varying(100) NOT NULL,
    due_date timestamp without time zone,
    assigned_to character varying(20),
    assigned_toid character varying(100),
    priority character varying(10) DEFAULT '低'::character varying,
    start_date timestamp without time zone,
    status_detail character varying(100) DEFAULT '未开始'::character varying,
    completed_date timestamp without time zone,
    outcome text,
    related_totype character varying(100) NOT NULL,
    related_to character varying(100) NOT NULL,
    related_toid character varying(100) NOT NULL,
    comments text,
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone,
    user_id bigint,
    dept_id bigint,
    stage_name character varying(100),
    task_order integer DEFAULT 0,
    conclusion character varying(45),
    reviewers character varying(255),
    review_status character varying(45),
    followers character varying(255),
    files character varying(255),
    category character varying(45),
    group_name character varying(100)
);


--
-- TOC entry 266 (class 1259 OID 360884)
-- Name: crm_territory; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_territory (
    territory_id character varying(100) NOT NULL,
    territory_name character varying(100) NOT NULL,
    dict_code bigint,
    is_active character(1) DEFAULT 'N'::bpchar,
    parent_id character varying(100),
    parent_name character varying(20),
    forecast_manager character varying(20),
    forecast_managerid character varying(100),
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone
);


--
-- TOC entry 267 (class 1259 OID 360890)
-- Name: crm_ticket; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_ticket (
    id character varying(100) NOT NULL,
    account_id character varying(100) NOT NULL,
    account_name character varying(255),
    category character varying(255) NOT NULL,
    subject character varying(255),
    source character varying(255),
    description text,
    resolution_note text,
    priority character varying(20),
    owner character varying(200),
    status character varying(100),
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone,
    close_by character varying(20),
    close_time timestamp without time zone,
    user_id bigint,
    dept_id bigint,
    sla_due_date timestamp without time zone,
    sla_compliance_status character varying(45),
    overdue_by character varying(45),
    region character varying(100),
    channel character varying(45),
    source_type character varying(45),
    customer_satisfaction integer,
    first_response_time timestamp without time zone,
    resolution_time timestamp without time zone
);


--
-- TOC entry 268 (class 1259 OID 360897)
-- Name: crm_timeperiod; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_timeperiod (
    period_id character varying(100) NOT NULL,
    period_name character varying(100),
    period_type character varying(100),
    start_date date,
    end_date date,
    quarter_id character varying(100),
    quarter_name character varying(100),
    fiscal_id character varying(100),
    fiscal_name character varying(100),
    is_active integer DEFAULT 1,
    comment character varying(255),
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone
);


--
-- TOC entry 269 (class 1259 OID 360905)
-- Name: crm_travel; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_travel (
    travel_id character varying(100) NOT NULL,
    travel_type character varying(100),
    project_id character varying(100),
    project_name character varying(100),
    applicant character varying(100),
    location character varying(255),
    start_date date,
    end_date date,
    duration integer DEFAULT 0,
    purpose character varying(255),
    work_schedule character varying(500),
    assumed_cost numeric(10,2) DEFAULT 0.00,
    require_approval integer DEFAULT 0,
    approver character varying(100),
    approver_time timestamp without time zone,
    is_reply integer DEFAULT 0,
    status character varying(100),
    description character varying(255),
    reply_description text,
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone,
    user_id bigint,
    dept_id bigint,
    relate_task character varying(255),
    relate_taskid character varying(100)
);


--
-- TOC entry 477 (class 1259 OID 380121)
-- Name: crm_wecom_aibot_config; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wecom_aibot_config (
    id bigint NOT NULL,
    tenant character varying(64) NOT NULL,
    bot_id character varying(128),
    bot_secret character varying(512),
    enabled integer DEFAULT 0,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- TOC entry 6452 (class 0 OID 0)
-- Dependencies: 477
-- Name: TABLE crm_wecom_aibot_config; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wecom_aibot_config IS '企业微信智能机器人租户配置';


--
-- TOC entry 6453 (class 0 OID 0)
-- Dependencies: 477
-- Name: COLUMN crm_wecom_aibot_config.tenant; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_aibot_config.tenant IS '租户标识';


--
-- TOC entry 6454 (class 0 OID 0)
-- Dependencies: 477
-- Name: COLUMN crm_wecom_aibot_config.bot_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_aibot_config.bot_id IS '智能机器人 ID';


--
-- TOC entry 6455 (class 0 OID 0)
-- Dependencies: 477
-- Name: COLUMN crm_wecom_aibot_config.bot_secret; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_aibot_config.bot_secret IS '智能机器人 Secret（AES-256 加密存储）';


--
-- TOC entry 6456 (class 0 OID 0)
-- Dependencies: 477
-- Name: COLUMN crm_wecom_aibot_config.enabled; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_aibot_config.enabled IS '是否启用 0-否 1-是';


--
-- TOC entry 476 (class 1259 OID 380120)
-- Name: crm_wecom_aibot_config_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wecom_aibot_config_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 6458 (class 0 OID 0)
-- Dependencies: 476
-- Name: crm_wecom_aibot_config_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wecom_aibot_config_id_seq OWNED BY public.crm_wecom_aibot_config.id;


--
-- TOC entry 479 (class 1259 OID 380135)
-- Name: crm_wecom_aibot_connection; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wecom_aibot_connection (
    id bigint NOT NULL,
    tenant character varying(64) NOT NULL,
    bot_id character varying(128),
    status character varying(32) DEFAULT 'disconnected'::character varying NOT NULL,
    last_message_at timestamp without time zone,
    error_message character varying(512),
    error_count integer DEFAULT 0,
    last_ping_at timestamp without time zone,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- TOC entry 6460 (class 0 OID 0)
-- Dependencies: 479
-- Name: TABLE crm_wecom_aibot_connection; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wecom_aibot_connection IS '企业微信智能机器人连接状态';


--
-- TOC entry 6461 (class 0 OID 0)
-- Dependencies: 479
-- Name: COLUMN crm_wecom_aibot_connection.tenant; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_aibot_connection.tenant IS '租户标识';


--
-- TOC entry 6462 (class 0 OID 0)
-- Dependencies: 479
-- Name: COLUMN crm_wecom_aibot_connection.bot_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_aibot_connection.bot_id IS '智能机器人 ID';


--
-- TOC entry 6463 (class 0 OID 0)
-- Dependencies: 479
-- Name: COLUMN crm_wecom_aibot_connection.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_aibot_connection.status IS '连接状态：disconnected/connecting/connected/disconnecting/reconnecting/error';


--
-- TOC entry 6464 (class 0 OID 0)
-- Dependencies: 479
-- Name: COLUMN crm_wecom_aibot_connection.last_message_at; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_aibot_connection.last_message_at IS '最后一次收到消息的时间';


--
-- TOC entry 6465 (class 0 OID 0)
-- Dependencies: 479
-- Name: COLUMN crm_wecom_aibot_connection.error_message; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_aibot_connection.error_message IS '最近一次错误信息';


--
-- TOC entry 6466 (class 0 OID 0)
-- Dependencies: 479
-- Name: COLUMN crm_wecom_aibot_connection.error_count; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_aibot_connection.error_count IS '累计错误次数';


--
-- TOC entry 6467 (class 0 OID 0)
-- Dependencies: 479
-- Name: COLUMN crm_wecom_aibot_connection.last_ping_at; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_aibot_connection.last_ping_at IS '最后一次心跳时间';


--
-- TOC entry 6468 (class 0 OID 0)
-- Dependencies: 479
-- Name: COLUMN crm_wecom_aibot_connection.updated_at; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_aibot_connection.updated_at IS '最后更新时间';


--
-- TOC entry 478 (class 1259 OID 380134)
-- Name: crm_wecom_aibot_connection_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wecom_aibot_connection_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 6470 (class 0 OID 0)
-- Dependencies: 478
-- Name: crm_wecom_aibot_connection_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wecom_aibot_connection_id_seq OWNED BY public.crm_wecom_aibot_connection.id;


--
-- TOC entry 473 (class 1259 OID 380073)
-- Name: crm_wecom_callback_event; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wecom_callback_event (
    id bigint NOT NULL,
    corp_id character varying(64) NOT NULL,
    event character varying(64) NOT NULL,
    change_type character varying(64),
    user_id character varying(128),
    external_userid character varying(64),
    chat_id character varying(128),
    tag_id character varying(128),
    tag_type character varying(32),
    update_detail character varying(64),
    state character varying(512),
    source character varying(64),
    fail_reason character varying(64),
    raw_xml text NOT NULL,
    create_by character varying(64),
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    update_by character varying(64),
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    remark character varying(500)
);


--
-- TOC entry 6472 (class 0 OID 0)
-- Dependencies: 473
-- Name: TABLE crm_wecom_callback_event; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wecom_callback_event IS '企业微信回调事件记录';


--
-- TOC entry 6473 (class 0 OID 0)
-- Dependencies: 473
-- Name: COLUMN crm_wecom_callback_event.corp_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_callback_event.corp_id IS '企业ID';


--
-- TOC entry 6474 (class 0 OID 0)
-- Dependencies: 473
-- Name: COLUMN crm_wecom_callback_event.event; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_callback_event.event IS '事件类型：change_external_contact / change_external_chat / change_external_tag / kf_msg_or_event';


--
-- TOC entry 6475 (class 0 OID 0)
-- Dependencies: 473
-- Name: COLUMN crm_wecom_callback_event.change_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_callback_event.change_type IS '变更类型（change_external_contact: add_external_contact/edit_external_contact/add_half_external_contact/del_external_contact/del_follow_user/transfer_fail; change_external_chat: create/update/dismiss; change_external_tag: create/update/delete/shuffle; kf_msg_or_event 无此字段）';


--
-- TOC entry 6476 (class 0 OID 0)
-- Dependencies: 473
-- Name: COLUMN crm_wecom_callback_event.user_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_callback_event.user_id IS '企业服务人员的UserID';


--
-- TOC entry 6477 (class 0 OID 0)
-- Dependencies: 473
-- Name: COLUMN crm_wecom_callback_event.external_userid; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_callback_event.external_userid IS '外部联系人的userid';


--
-- TOC entry 6478 (class 0 OID 0)
-- Dependencies: 473
-- Name: COLUMN crm_wecom_callback_event.chat_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_callback_event.chat_id IS '客户群ID';


--
-- TOC entry 6479 (class 0 OID 0)
-- Dependencies: 473
-- Name: COLUMN crm_wecom_callback_event.tag_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_callback_event.tag_id IS '标签或标签组的ID';


--
-- TOC entry 6480 (class 0 OID 0)
-- Dependencies: 473
-- Name: COLUMN crm_wecom_callback_event.tag_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_callback_event.tag_type IS '标签类型：tag / tag_group';


--
-- TOC entry 6481 (class 0 OID 0)
-- Dependencies: 473
-- Name: COLUMN crm_wecom_callback_event.update_detail; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_callback_event.update_detail IS '客户群变更详情：add_member / del_member / change_owner / change_name / change_notice';


--
-- TOC entry 6482 (class 0 OID 0)
-- Dependencies: 473
-- Name: COLUMN crm_wecom_callback_event.state; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_callback_event.state IS '添加此用户的联系我方式配置的state参数';


--
-- TOC entry 6483 (class 0 OID 0)
-- Dependencies: 473
-- Name: COLUMN crm_wecom_callback_event.source; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_callback_event.source IS '删除客户的操作来源，如 DELETE_BY_TRANSFER';


--
-- TOC entry 6484 (class 0 OID 0)
-- Dependencies: 473
-- Name: COLUMN crm_wecom_callback_event.fail_reason; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_callback_event.fail_reason IS '接替失败原因：customer_refused / customer_limit_exceed';


--
-- TOC entry 6485 (class 0 OID 0)
-- Dependencies: 473
-- Name: COLUMN crm_wecom_callback_event.raw_xml; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_callback_event.raw_xml IS '解密后的完整明文 XML，保留全部原始信息';


--
-- TOC entry 6486 (class 0 OID 0)
-- Dependencies: 473
-- Name: COLUMN crm_wecom_callback_event.create_time; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_callback_event.create_time IS '创建时间';


--
-- TOC entry 472 (class 1259 OID 380072)
-- Name: crm_wecom_callback_event_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wecom_callback_event_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 6488 (class 0 OID 0)
-- Dependencies: 472
-- Name: crm_wecom_callback_event_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wecom_callback_event_id_seq OWNED BY public.crm_wecom_callback_event.id;


--
-- TOC entry 270 (class 1259 OID 360916)
-- Name: crm_wecom_config; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_wecom_config (
    id numeric(20,0) NOT NULL,
    corp_id character varying(64) NOT NULL,
    agent_id character varying(32) NOT NULL,
    agent_secret_enc text NOT NULL,
    callback_token character varying(128),
    encoding_aes_key character varying(128),
    connected smallint DEFAULT 0 NOT NULL,
    last_error character varying(512),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    welcome_enabled boolean DEFAULT false NOT NULL,
    welcome_text character varying(2048) DEFAULT NULL::character varying
);


--
-- TOC entry 6490 (class 0 OID 0)
-- Dependencies: 270
-- Name: TABLE crm_wecom_config; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON TABLE public.crm_wecom_config IS '企业微信租户配置（每租户库单行）';


--
-- TOC entry 6491 (class 0 OID 0)
-- Dependencies: 270
-- Name: COLUMN crm_wecom_config.welcome_enabled; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_wecom_config.welcome_enabled IS 'API 自动欢迎语开关';


--
-- TOC entry 6492 (class 0 OID 0)
-- Dependencies: 270
-- Name: COLUMN crm_wecom_config.welcome_text; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_wecom_config.welcome_text IS '租户默认欢迎语文案';


--
-- TOC entry 433 (class 1259 OID 371436)
-- Name: crm_wecom_config_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE SEQUENCE public.crm_wecom_config_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 6494 (class 0 OID 0)
-- Dependencies: 433
-- Name: crm_wecom_config_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: mega_vXnFBG5i
--

ALTER SEQUENCE public.crm_wecom_config_id_seq OWNED BY public.crm_wecom_config.id;


--
-- TOC entry 271 (class 1259 OID 360924)
-- Name: crm_wecom_contact_way; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_wecom_contact_way (
    id numeric(20,0) NOT NULL,
    config_id character varying(128) NOT NULL,
    state character varying(512),
    remark character varying(512),
    scene integer,
    type integer,
    skip_verify smallint DEFAULT 1,
    user_list jsonb,
    qr_code text,
    raw_request jsonb,
    raw_response jsonb,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    welcome_text character varying(2048) DEFAULT NULL::character varying
);


--
-- TOC entry 6496 (class 0 OID 0)
-- Dependencies: 271
-- Name: TABLE crm_wecom_contact_way; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON TABLE public.crm_wecom_contact_way IS '企业微信联系我/渠道活码';


--
-- TOC entry 6497 (class 0 OID 0)
-- Dependencies: 271
-- Name: COLUMN crm_wecom_contact_way.welcome_text; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.crm_wecom_contact_way.welcome_text IS '活码级欢迎语覆盖';


--
-- TOC entry 434 (class 1259 OID 371438)
-- Name: crm_wecom_contact_way_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE SEQUENCE public.crm_wecom_contact_way_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 6499 (class 0 OID 0)
-- Dependencies: 434
-- Name: crm_wecom_contact_way_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: mega_vXnFBG5i
--

ALTER SEQUENCE public.crm_wecom_contact_way_id_seq OWNED BY public.crm_wecom_contact_way.id;


--
-- TOC entry 272 (class 1259 OID 360932)
-- Name: crm_wecom_external_contact; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_wecom_external_contact (
    id numeric(20,0) NOT NULL,
    external_userid character varying(128) NOT NULL,
    follow_userid character varying(128),
    state character varying(512),
    crm_lead_id character varying(64),
    crm_contact_id character varying(64),
    crm_event_id character varying(64),
    raw_json text,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    remark character varying(255) DEFAULT NULL::character varying,
    add_way integer,
    tags text,
    description character varying(512) DEFAULT NULL::character varying,
    oper_userid character varying(128) DEFAULT NULL::character varying,
    follow_create_time timestamp without time zone,
    remark_mobiles text
);


--
-- TOC entry 6501 (class 0 OID 0)
-- Dependencies: 272
-- Name: TABLE crm_wecom_external_contact; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON TABLE public.crm_wecom_external_contact IS '企业微信外部联系人与 CRM 映射';


--
-- TOC entry 435 (class 1259 OID 371440)
-- Name: crm_wecom_external_contact_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE SEQUENCE public.crm_wecom_external_contact_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 6503 (class 0 OID 0)
-- Dependencies: 435
-- Name: crm_wecom_external_contact_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: mega_vXnFBG5i
--

ALTER SEQUENCE public.crm_wecom_external_contact_id_seq OWNED BY public.crm_wecom_external_contact.id;


--
-- TOC entry 467 (class 1259 OID 379682)
-- Name: crm_wecom_kf_config; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wecom_kf_config (
    id bigint NOT NULL,
    corp_id character varying(64) NOT NULL,
    webhook_url character varying(512),
    open_kfids text,
    enabled integer DEFAULT 0,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    workflow_id character varying(36)
);


--
-- TOC entry 6505 (class 0 OID 0)
-- Dependencies: 467
-- Name: COLUMN crm_wecom_kf_config.workflow_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_config.workflow_id IS '绑定的工作流 ID，关联 wworkflow.id';


--
-- TOC entry 466 (class 1259 OID 379681)
-- Name: crm_wecom_kf_config_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wecom_kf_config_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 6507 (class 0 OID 0)
-- Dependencies: 466
-- Name: crm_wecom_kf_config_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wecom_kf_config_id_seq OWNED BY public.crm_wecom_kf_config.id;


--
-- TOC entry 469 (class 1259 OID 379738)
-- Name: crm_wecom_kf_message; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wecom_kf_message (
    id bigint NOT NULL,
    corp_id character varying(64) NOT NULL,
    open_kfid character varying(64) NOT NULL,
    external_userid character varying(64) NOT NULL,
    msgid character varying(128) NOT NULL,
    msgtype character varying(32) DEFAULT 'text'::character varying NOT NULL,
    content text,
    direction smallint DEFAULT 1 NOT NULL,
    send_time bigint,
    raw_json text,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- TOC entry 6509 (class 0 OID 0)
-- Dependencies: 469
-- Name: TABLE crm_wecom_kf_message; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wecom_kf_message IS '微信客服会话消息记录';


--
-- TOC entry 6510 (class 0 OID 0)
-- Dependencies: 469
-- Name: COLUMN crm_wecom_kf_message.corp_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_message.corp_id IS '企业ID';


--
-- TOC entry 6511 (class 0 OID 0)
-- Dependencies: 469
-- Name: COLUMN crm_wecom_kf_message.open_kfid; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_message.open_kfid IS '客服账号ID';


--
-- TOC entry 6512 (class 0 OID 0)
-- Dependencies: 469
-- Name: COLUMN crm_wecom_kf_message.external_userid; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_message.external_userid IS '微信客户ID（关联 crm_wecom_external_contact）';


--
-- TOC entry 6513 (class 0 OID 0)
-- Dependencies: 469
-- Name: COLUMN crm_wecom_kf_message.msgid; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_message.msgid IS '消息唯一ID（全局唯一，用于幂等去重）';


--
-- TOC entry 6514 (class 0 OID 0)
-- Dependencies: 469
-- Name: COLUMN crm_wecom_kf_message.msgtype; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_message.msgtype IS '消息类型：text / image / voice / video / file 等';


--
-- TOC entry 6515 (class 0 OID 0)
-- Dependencies: 469
-- Name: COLUMN crm_wecom_kf_message.content; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_message.content IS '消息正文（文本消息时存储内容，其他类型可存储 media_id）';


--
-- TOC entry 6516 (class 0 OID 0)
-- Dependencies: 469
-- Name: COLUMN crm_wecom_kf_message.direction; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_message.direction IS '消息方向：1=客户→系统(incoming)，2=系统→客户(outgoing)';


--
-- TOC entry 6517 (class 0 OID 0)
-- Dependencies: 469
-- Name: COLUMN crm_wecom_kf_message.send_time; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_message.send_time IS '消息发送时间（Unix 时间戳，秒）';


--
-- TOC entry 6518 (class 0 OID 0)
-- Dependencies: 469
-- Name: COLUMN crm_wecom_kf_message.raw_json; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_message.raw_json IS '原始消息 JSON（完整 payload，便于扩展和排错）';


--
-- TOC entry 468 (class 1259 OID 379737)
-- Name: crm_wecom_kf_message_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wecom_kf_message_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 6520 (class 0 OID 0)
-- Dependencies: 468
-- Name: crm_wecom_kf_message_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wecom_kf_message_id_seq OWNED BY public.crm_wecom_kf_message.id;


--
-- TOC entry 273 (class 1259 OID 360941)
-- Name: crm_wecom_mass_task; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_wecom_mass_task (
    id numeric(20,0) NOT NULL,
    msg_id character varying(128),
    chat_type character varying(32),
    external_userids jsonb,
    sender_list jsonb,
    content_json jsonb,
    status character varying(64),
    send_result_json text,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


--
-- TOC entry 6522 (class 0 OID 0)
-- Dependencies: 273
-- Name: TABLE crm_wecom_mass_task; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON TABLE public.crm_wecom_mass_task IS '企业微信客户群发任务';


--
-- TOC entry 436 (class 1259 OID 371442)
-- Name: crm_wecom_mass_task_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE SEQUENCE public.crm_wecom_mass_task_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 6524 (class 0 OID 0)
-- Dependencies: 436
-- Name: crm_wecom_mass_task_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: mega_vXnFBG5i
--

ALTER SEQUENCE public.crm_wecom_mass_task_id_seq OWNED BY public.crm_wecom_mass_task.id;


--
-- TOC entry 471 (class 1259 OID 380019)
-- Name: crm_wecom_material; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wecom_material (
    id bigint NOT NULL,
    file_title character varying(255) NOT NULL,
    file_name character varying(255) NOT NULL,
    original_name character varying(500) NOT NULL,
    file_type character varying(50) NOT NULL,
    file_size bigint NOT NULL,
    media_type character varying(20),
    media_id character varying(128),
    wecom_uploaded_at timestamp without time zone,
    media_created_at bigint,
    expires_at timestamp without time zone,
    status character varying(20) DEFAULT 'pending'::character varying,
    errmsg text,
    create_by character varying(64),
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    update_by character varying(64),
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- TOC entry 6526 (class 0 OID 0)
-- Dependencies: 471
-- Name: TABLE crm_wecom_material; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wecom_material IS '企业微信临时素材管理表';


--
-- TOC entry 6527 (class 0 OID 0)
-- Dependencies: 471
-- Name: COLUMN crm_wecom_material.file_title; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_material.file_title IS '素材名称';


--
-- TOC entry 6528 (class 0 OID 0)
-- Dependencies: 471
-- Name: COLUMN crm_wecom_material.file_name; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_material.file_name IS '服务器存储文件名';


--
-- TOC entry 6529 (class 0 OID 0)
-- Dependencies: 471
-- Name: COLUMN crm_wecom_material.original_name; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_material.original_name IS '用户上传时的原始文件名';


--
-- TOC entry 6530 (class 0 OID 0)
-- Dependencies: 471
-- Name: COLUMN crm_wecom_material.file_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_material.file_type IS '文件扩展名';


--
-- TOC entry 6531 (class 0 OID 0)
-- Dependencies: 471
-- Name: COLUMN crm_wecom_material.file_size; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_material.file_size IS '文件大小（字节）';


--
-- TOC entry 6532 (class 0 OID 0)
-- Dependencies: 471
-- Name: COLUMN crm_wecom_material.media_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_material.media_type IS '企微素材类型：image / voice / video / file';


--
-- TOC entry 6533 (class 0 OID 0)
-- Dependencies: 471
-- Name: COLUMN crm_wecom_material.media_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_material.media_id IS '企微返回的临时 media_id，有效期 3 天';


--
-- TOC entry 6534 (class 0 OID 0)
-- Dependencies: 471
-- Name: COLUMN crm_wecom_material.wecom_uploaded_at; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_material.wecom_uploaded_at IS '上传到企微的时间';


--
-- TOC entry 6535 (class 0 OID 0)
-- Dependencies: 471
-- Name: COLUMN crm_wecom_material.media_created_at; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_material.media_created_at IS '企微返回的 created_at（Unix 秒级时间戳）';


--
-- TOC entry 6536 (class 0 OID 0)
-- Dependencies: 471
-- Name: COLUMN crm_wecom_material.expires_at; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_material.expires_at IS 'media_id 过期时间（media_created_at + 3 天）';


--
-- TOC entry 6537 (class 0 OID 0)
-- Dependencies: 471
-- Name: COLUMN crm_wecom_material.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_material.status IS 'pending: 待上传 / uploaded: 已上传 / expired: 已过期 / upload_failed: 上传失败';


--
-- TOC entry 6538 (class 0 OID 0)
-- Dependencies: 471
-- Name: COLUMN crm_wecom_material.errmsg; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_material.errmsg IS '上传到企微失败时的错误信息';


--
-- TOC entry 470 (class 1259 OID 380018)
-- Name: crm_wecom_material_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wecom_material_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 6540 (class 0 OID 0)
-- Dependencies: 470
-- Name: crm_wecom_material_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wecom_material_id_seq OWNED BY public.crm_wecom_material.id;


--
-- TOC entry 432 (class 1259 OID 371388)
-- Name: crm_wecom_welcome_log; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wecom_welcome_log (
    id bigint NOT NULL,
    external_userid character varying(128) NOT NULL,
    follow_userid character varying(128) DEFAULT NULL::character varying,
    state character varying(512) DEFAULT NULL::character varying,
    welcome_text_used character varying(2048) DEFAULT NULL::character varying,
    text_source character varying(32) DEFAULT NULL::character varying,
    errcode integer,
    errmsg character varying(512) DEFAULT NULL::character varying,
    skipped_reason character varying(64) DEFAULT NULL::character varying,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


--
-- TOC entry 6542 (class 0 OID 0)
-- Dependencies: 432
-- Name: TABLE crm_wecom_welcome_log; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wecom_welcome_log IS '企业微信欢迎语发送/跳过审计';


--
-- TOC entry 431 (class 1259 OID 371387)
-- Name: crm_wecom_welcome_log_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wecom_welcome_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 6544 (class 0 OID 0)
-- Dependencies: 431
-- Name: crm_wecom_welcome_log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wecom_welcome_log_id_seq OWNED BY public.crm_wecom_welcome_log.id;


--
-- TOC entry 274 (class 1259 OID 360948)
-- Name: crm_weeklyreport; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_weeklyreport (
    report_id character varying(100) NOT NULL,
    nick_name character varying(255),
    reviewed integer DEFAULT 0,
    approved integer DEFAULT 0,
    approver character varying(100),
    approver_id character varying(100),
    report_content text,
    manager_comment text,
    start_time timestamp without time zone,
    end_time timestamp without time zone,
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone,
    user_id bigint,
    dept_id bigint
);


--
-- TOC entry 428 (class 1259 OID 371261)
-- Name: crm_workflow_automation; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_workflow_automation (
    config_id character varying(36) NOT NULL,
    tenant_id character varying(64) NOT NULL,
    automation_key character varying(64) NOT NULL,
    workflow_id character varying(36),
    enabled character(1) DEFAULT '0'::bpchar NOT NULL,
    trigger_event character varying(64),
    config_json text,
    remark character varying(500),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    del_flag character(1) DEFAULT '0'::bpchar NOT NULL
);


--
-- TOC entry 6547 (class 0 OID 0)
-- Dependencies: 428
-- Name: TABLE crm_workflow_automation; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_workflow_automation IS 'Tenant workflow automation: business event -> wworkflow id';


--
-- TOC entry 6548 (class 0 OID 0)
-- Dependencies: 428
-- Name: COLUMN crm_workflow_automation.automation_key; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_workflow_automation.automation_key IS 'LEAD_TAGGING | LEAD_CONVERT | LEAD_FOLLOWUP | LEAD_CALLBACK | LEAD_APPOINTMENT';


--
-- TOC entry 6549 (class 0 OID 0)
-- Dependencies: 428
-- Name: COLUMN crm_workflow_automation.enabled; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_workflow_automation.enabled IS '0 disabled, 1 enabled';


--
-- TOC entry 6550 (class 0 OID 0)
-- Dependencies: 428
-- Name: COLUMN crm_workflow_automation.trigger_event; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_workflow_automation.trigger_event IS 'e.g. LEAD_CREATED — reserved for backend schedulers';


--
-- TOC entry 276 (class 1259 OID 360958)
-- Name: crm_workflow_trigger; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.crm_workflow_trigger (
    id bigint NOT NULL,
    process_definition_key character varying(100) NOT NULL,
    event_type character varying(50) NOT NULL,
    trigger_source_id character varying(100) NOT NULL,
    condition_expression text,
    variable_mapping jsonb,
    tenant_id character varying(64),
    is_active smallint DEFAULT 1,
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone
);


--
-- TOC entry 275 (class 1259 OID 360957)
-- Name: crm_workflow_trigger_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE public.crm_workflow_trigger ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.crm_workflow_trigger_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 277 (class 1259 OID 360966)
-- Name: den_inventory; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.den_inventory (
    inv_id character varying(100) NOT NULL,
    inv_name character varying(100),
    owner character varying(45),
    contact_phone character varying(20),
    description character varying(255),
    category character varying(20),
    status character varying(10),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


--
-- TOC entry 278 (class 1259 OID 360973)
-- Name: den_staff; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.den_staff (
    den_id character varying(100) NOT NULL,
    den_number character varying(20) NOT NULL,
    den_login_account character varying(20) NOT NULL,
    den_category character varying(20),
    name character varying(20),
    avatar character varying(200),
    gender character varying(10),
    birth_day date,
    age integer,
    id_type character varying(20),
    id_number character varying(20),
    occupation character varying(20),
    side_job character varying(100),
    den_level character varying(20),
    section character varying(20),
    room_number character varying(20),
    nurse_name character varying(20),
    nurse_id character varying(20),
    assistant_id character varying(20),
    assistant_name character varying(20),
    discount numeric(8,0) DEFAULT 0,
    mobile character varying(45),
    school_name character varying(50),
    home_address character varying(100),
    yb_doctor_code character varying(45),
    yb_doctor_name character varying(45),
    yb_section_code character varying(45),
    yb_section_name character varying(45),
    status character varying(10),
    comment character varying(255),
    create_by character varying(20),
    create_time timestamp without time zone,
    update_by character varying(20),
    update_time timestamp without time zone
);


--
-- TOC entry 279 (class 1259 OID 360981)
-- Name: den_staff_bank; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.den_staff_bank (
    bank_id character varying(100) NOT NULL,
    staff_id character varying(100),
    bank_type character varying(20),
    is_primary integer DEFAULT 0,
    bank_name character varying(100),
    bank_account character varying(65),
    bank_branch_city character varying(45),
    bank_branch_name character varying(65),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


--
-- TOC entry 280 (class 1259 OID 360989)
-- Name: den_vendor; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.den_vendor (
    vendor_id character varying(100) NOT NULL,
    py_name character varying(255),
    website character varying(45),
    address character varying(255),
    description character varying(255),
    category character varying(20),
    status character varying(10),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


--
-- TOC entry 281 (class 1259 OID 360996)
-- Name: ems_hcp; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.ems_hcp (
    hcp_id character varying(100) NOT NULL,
    name character varying(45),
    avatar character varying(100),
    gender character varying(10),
    age integer,
    physician_level character varying(45),
    working_place character varying(200),
    department character varying(45),
    specialty character varying(45),
    subdepartment character varying(45),
    province character varying(45),
    city character varying(45),
    is_domestic integer,
    address character varying(200),
    post_code character varying(45),
    administration_title character varying(100),
    professional_title character varying(100),
    is_speaker integer DEFAULT 0,
    speaker_level character varying(45),
    speaker_level_qualification text,
    cid_type character varying(45),
    cid_no character varying(45),
    mobile character varying(45),
    office_phone character varying(45),
    bank_type character varying(65),
    bank_name character varying(65),
    bank_account character varying(65),
    bank_branch_city character varying(45),
    bank_branch_name character varying(65),
    product character varying(45),
    region character varying(45),
    sales_rep character varying(45),
    status character varying(45),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


--
-- TOC entry 412 (class 1259 OID 369640)
-- Name: flw_channel_definition; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.flw_channel_definition (
    id_ character varying(255) NOT NULL,
    name_ character varying(255),
    version_ integer,
    key_ character varying(255),
    category_ character varying(255),
    deployment_id_ character varying(255),
    create_time_ timestamp(3) without time zone,
    tenant_id_ character varying(255),
    resource_name_ character varying(255),
    description_ character varying(255),
    type_ character varying(255),
    implementation_ character varying(255)
);


--
-- TOC entry 413 (class 1259 OID 369645)
-- Name: flw_ev_databasechangelog; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.flw_ev_databasechangelog (
    id character varying(255) NOT NULL,
    author character varying(255) NOT NULL,
    filename character varying(255) NOT NULL,
    dateexecuted timestamp without time zone NOT NULL,
    orderexecuted integer NOT NULL,
    exectype character varying(10) NOT NULL,
    md5sum character varying(35),
    description character varying(255),
    comments character varying(255),
    tag character varying(255),
    liquibase character varying(20),
    contexts character varying(255),
    labels character varying(255),
    deployment_id character varying(10)
);


--
-- TOC entry 414 (class 1259 OID 369650)
-- Name: flw_ev_databasechangeloglock; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.flw_ev_databasechangeloglock (
    id integer NOT NULL,
    locked boolean NOT NULL,
    lockgranted timestamp without time zone,
    lockedby character varying(255)
);


--
-- TOC entry 415 (class 1259 OID 369653)
-- Name: flw_event_definition; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.flw_event_definition (
    id_ character varying(255) NOT NULL,
    name_ character varying(255),
    version_ integer,
    key_ character varying(255),
    category_ character varying(255),
    deployment_id_ character varying(255),
    tenant_id_ character varying(255),
    resource_name_ character varying(255),
    description_ character varying(255)
);


--
-- TOC entry 416 (class 1259 OID 369658)
-- Name: flw_event_deployment; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.flw_event_deployment (
    id_ character varying(255) NOT NULL,
    name_ character varying(255),
    category_ character varying(255),
    deploy_time_ timestamp(3) without time zone,
    tenant_id_ character varying(255),
    parent_deployment_id_ character varying(255)
);


--
-- TOC entry 417 (class 1259 OID 369663)
-- Name: flw_event_resource; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.flw_event_resource (
    id_ character varying(255) NOT NULL,
    name_ character varying(255),
    deployment_id_ character varying(255),
    resource_bytes_ bytea
);


--
-- TOC entry 418 (class 1259 OID 369668)
-- Name: flw_ru_batch; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.flw_ru_batch (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    type_ character varying(64) NOT NULL,
    search_key_ character varying(255),
    search_key2_ character varying(255),
    create_time_ timestamp without time zone NOT NULL,
    complete_time_ timestamp without time zone,
    status_ character varying(255),
    batch_doc_id_ character varying(64),
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


--
-- TOC entry 419 (class 1259 OID 369674)
-- Name: flw_ru_batch_part; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.flw_ru_batch_part (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    batch_id_ character varying(64),
    type_ character varying(64) NOT NULL,
    scope_id_ character varying(64),
    sub_scope_id_ character varying(64),
    scope_type_ character varying(64),
    search_key_ character varying(255),
    search_key2_ character varying(255),
    create_time_ timestamp without time zone NOT NULL,
    complete_time_ timestamp without time zone,
    status_ character varying(255),
    result_doc_id_ character varying(64),
    tenant_id_ character varying(255) DEFAULT ''::character varying
);


--
-- TOC entry 283 (class 1259 OID 361050)
-- Name: gen_table; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.gen_table (
    table_id bigint NOT NULL,
    table_name character varying(200),
    table_comment character varying(500),
    sub_table_name character varying(64),
    sub_table_fk_name character varying(64),
    class_name character varying(100),
    tpl_category character varying(200) DEFAULT 'crud'::character varying,
    package_name character varying(100),
    module_name character varying(30),
    business_name character varying(30),
    function_name character varying(50),
    function_author character varying(50),
    gen_type character(1) DEFAULT '0'::bpchar,
    gen_path character varying(200) DEFAULT '/'::character varying,
    options character varying(1000),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    remark character varying(500)
);


--
-- TOC entry 285 (class 1259 OID 361061)
-- Name: gen_table_column; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.gen_table_column (
    column_id bigint NOT NULL,
    table_id bigint,
    column_name character varying(200),
    column_comment character varying(500),
    column_type character varying(100),
    java_type character varying(500),
    java_field character varying(200),
    is_pk character(1),
    is_increment character(1),
    is_required character(1),
    is_insert character(1),
    is_edit character(1),
    is_list character(1),
    is_query character(1),
    query_type character varying(200) DEFAULT 'EQ'::character varying,
    html_type character varying(200),
    dict_type character varying(200),
    sort integer,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


--
-- TOC entry 284 (class 1259 OID 361060)
-- Name: gen_table_column_column_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE public.gen_table_column ALTER COLUMN column_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.gen_table_column_column_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 282 (class 1259 OID 361049)
-- Name: gen_table_table_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE public.gen_table ALTER COLUMN table_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.gen_table_table_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 287 (class 1259 OID 361070)
-- Name: master_registration; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.master_registration (
    regist_id bigint NOT NULL,
    contact_name character varying(100),
    company_name character varying(100),
    mobile character varying(100),
    email character varying(100),
    start_date date,
    end_date date,
    oper_ip character varying(128),
    oper_location character varying(255),
    plan character varying(255),
    status integer DEFAULT 0,
    remark character varying(500),
    oper_time timestamp without time zone,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


--
-- TOC entry 286 (class 1259 OID 361069)
-- Name: master_registration_regist_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE public.master_registration ALTER COLUMN regist_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.master_registration_regist_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 289 (class 1259 OID 361079)
-- Name: master_tenant; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.master_tenant (
    id bigint NOT NULL,
    tenant character varying(32),
    url character varying(255),
    username character varying(255),
    password character varying(255),
    database_name character varying(64),
    host_name character varying(64),
    create_time timestamp without time zone,
    status character(1) DEFAULT '1'::bpchar,
    expiration_date timestamp without time zone
);


--
-- TOC entry 288 (class 1259 OID 361078)
-- Name: master_tenant_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE public.master_tenant ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.master_tenant_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 420 (class 1259 OID 371083)
-- Name: session; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.session (
    id text NOT NULL,
    "expiresAt" timestamp(3) without time zone NOT NULL,
    token text NOT NULL,
    "createdAt" timestamp(3) without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "updatedAt" timestamp(3) without time zone NOT NULL,
    "ipAddress" text,
    "userAgent" text,
    "userId" text NOT NULL
);


--
-- TOC entry 291 (class 1259 OID 361088)
-- Name: sys_config; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_config (
    config_id integer NOT NULL,
    config_name character varying(100),
    config_key character varying(100),
    config_value character varying(500),
    config_type character(1) DEFAULT 'N'::bpchar,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    remark character varying(500)
);


--
-- TOC entry 290 (class 1259 OID 361087)
-- Name: sys_config_config_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE public.sys_config ALTER COLUMN config_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.sys_config_config_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 293 (class 1259 OID 361097)
-- Name: sys_deploy_form; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_deploy_form (
    id bigint NOT NULL,
    form_id bigint,
    deploy_id character varying(50)
);


--
-- TOC entry 292 (class 1259 OID 361096)
-- Name: sys_deploy_form_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE public.sys_deploy_form ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.sys_deploy_form_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 295 (class 1259 OID 361103)
-- Name: sys_dept; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_dept (
    dept_id bigint NOT NULL,
    parent_id bigint DEFAULT 0,
    ancestors character varying(50),
    dept_name character varying(30),
    order_num integer DEFAULT 0,
    leader character varying(20),
    phone character varying(11),
    email character varying(50),
    status character(1) DEFAULT '0'::bpchar,
    del_flag character(1) DEFAULT '0'::bpchar,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


--
-- TOC entry 294 (class 1259 OID 361102)
-- Name: sys_dept_dept_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE public.sys_dept ALTER COLUMN dept_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.sys_dept_dept_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 297 (class 1259 OID 361113)
-- Name: sys_dict_data; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_dict_data (
    dict_code bigint NOT NULL,
    dict_sort integer DEFAULT 0,
    dict_label character varying(100),
    dict_label_en character varying(100),
    dict_label_th character varying(100),
    dict_value character varying(100),
    dict_type character varying(100),
    css_class character varying(100),
    list_class character varying(100),
    is_default character(1) DEFAULT 'N'::bpchar,
    status character(1) DEFAULT '0'::bpchar,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    remark character varying(500)
);


--
-- TOC entry 296 (class 1259 OID 361112)
-- Name: sys_dict_data_dict_code_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE public.sys_dict_data ALTER COLUMN dict_code ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.sys_dict_data_dict_code_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 299 (class 1259 OID 361124)
-- Name: sys_dict_type; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_dict_type (
    dict_id bigint NOT NULL,
    dict_name character varying(100),
    dict_type character varying(100),
    status character(1) DEFAULT '0'::bpchar,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    remark character varying(500)
);


--
-- TOC entry 298 (class 1259 OID 361123)
-- Name: sys_dict_type_dict_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE public.sys_dict_type ALTER COLUMN dict_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.sys_dict_type_dict_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 301 (class 1259 OID 361133)
-- Name: sys_ems_corres; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_ems_corres (
    corres_id bigint NOT NULL,
    category character varying(40) DEFAULT '0'::character varying,
    isbelongtocase character(1) DEFAULT '0'::bpchar,
    folder_id bigint DEFAULT 0,
    case_id character varying(40) DEFAULT '0'::character varying,
    file_title character varying(300),
    file_name character varying(300),
    file_type character varying(200),
    file_size bigint DEFAULT 0,
    order_num integer DEFAULT 0,
    status character(1) DEFAULT '0'::bpchar,
    del_flag character(1) DEFAULT '0'::bpchar,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


--
-- TOC entry 300 (class 1259 OID 361132)
-- Name: sys_ems_corres_corres_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE public.sys_ems_corres ALTER COLUMN corres_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.sys_ems_corres_corres_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 303 (class 1259 OID 361149)
-- Name: sys_form; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_form (
    form_id bigint NOT NULL,
    form_name character varying(50),
    case_type_id character varying(100),
    editor_content text,
    form_content text,
    create_time timestamp without time zone,
    update_time timestamp without time zone,
    create_by bigint,
    update_by bigint,
    remark character varying(255)
);


--
-- TOC entry 302 (class 1259 OID 361148)
-- Name: sys_form_form_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE public.sys_form ALTER COLUMN form_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.sys_form_form_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 304 (class 1259 OID 361156)
-- Name: sys_hi_audittrail; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_hi_audittrail (
    id character varying(64) NOT NULL,
    py_case_id character varying(255),
    type character varying(255),
    create_time timestamp(3) without time zone NOT NULL,
    user_id character varying(255),
    task_id character varying(64),
    proc_inst_id character varying(64),
    action character varying(255),
    message character varying(4000)
);


--
-- TOC entry 305 (class 1259 OID 361163)
-- Name: sys_job; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_job (
    job_id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    job_name character varying(64) NOT NULL,
    job_group character varying(64) DEFAULT 'DEFAULT'::character varying NOT NULL,
    invoke_target character varying(500) NOT NULL,
    cron_expression character varying(255),
    misfire_policy character varying(20) DEFAULT '3'::character varying,
    concurrent character(1) DEFAULT '1'::bpchar,
    status character(1) DEFAULT '0'::bpchar,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    remark character varying(500)
);


--
-- TOC entry 307 (class 1259 OID 361175)
-- Name: sys_job_log; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_job_log (
    job_log_id bigint NOT NULL,
    job_name character varying(64) NOT NULL,
    job_group character varying(64) NOT NULL,
    invoke_target character varying(500) NOT NULL,
    job_message character varying(500),
    status character(1) DEFAULT '0'::bpchar,
    exception_info character varying(2000),
    create_time timestamp without time zone
);


--
-- TOC entry 306 (class 1259 OID 361174)
-- Name: sys_job_log_job_log_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE public.sys_job_log ALTER COLUMN job_log_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.sys_job_log_job_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 309 (class 1259 OID 361184)
-- Name: sys_logininfor; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_logininfor (
    info_id bigint NOT NULL,
    user_name character varying(50),
    ipaddr character varying(128),
    login_location character varying(255),
    browser character varying(50),
    os character varying(50),
    status character(1) DEFAULT '0'::bpchar,
    msg character varying(255),
    login_time timestamp without time zone
);


--
-- TOC entry 308 (class 1259 OID 361183)
-- Name: sys_logininfor_info_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE public.sys_logininfor ALTER COLUMN info_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.sys_logininfor_info_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 311 (class 1259 OID 361193)
-- Name: sys_menu; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_menu (
    menu_id bigint NOT NULL,
    menu_name character varying(50) NOT NULL,
    parent_id bigint DEFAULT 0,
    order_num integer DEFAULT 0,
    path character varying(200),
    component character varying(255),
    query character varying(255),
    is_frame integer DEFAULT 1,
    is_cache integer DEFAULT 0,
    menu_type character(1),
    visible character(1) DEFAULT '0'::bpchar,
    status character(1) DEFAULT '0'::bpchar,
    perms character varying(100),
    icon character varying(100) DEFAULT '#'::character varying,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    remark character varying(500)
);


--
-- TOC entry 310 (class 1259 OID 361192)
-- Name: sys_menu_menu_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE public.sys_menu ALTER COLUMN menu_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.sys_menu_menu_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 313 (class 1259 OID 361208)
-- Name: sys_notice; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_notice (
    notice_id integer NOT NULL,
    notice_title character varying(50) NOT NULL,
    notice_type character(1) NOT NULL,
    notice_content bytea,
    status character(1) DEFAULT '0'::bpchar,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    remark character varying(255)
);


--
-- TOC entry 312 (class 1259 OID 361207)
-- Name: sys_notice_notice_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE public.sys_notice ALTER COLUMN notice_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.sys_notice_notice_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 315 (class 1259 OID 361217)
-- Name: sys_oper_log; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_oper_log (
    oper_id bigint NOT NULL,
    title character varying(50),
    business_type integer DEFAULT 0,
    method character varying(100),
    request_method character varying(10),
    operator_type integer DEFAULT 0,
    oper_name character varying(50),
    dept_name character varying(50),
    oper_url character varying(255),
    oper_ip character varying(128),
    oper_location character varying(255),
    oper_param character varying(2000),
    json_result character varying(2000),
    status integer DEFAULT 0,
    error_msg character varying(2000),
    oper_time timestamp without time zone
);


--
-- TOC entry 314 (class 1259 OID 361216)
-- Name: sys_oper_log_oper_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE public.sys_oper_log ALTER COLUMN oper_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.sys_oper_log_oper_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 316 (class 1259 OID 361227)
-- Name: sys_organization; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_organization (
    organization_id character varying(100) NOT NULL,
    py_name character varying(64),
    py_label character varying(255),
    website character varying(255),
    tel character varying(30),
    address character varying(255),
    description character varying(255),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    uuid character varying(100),
    bank_name character varying(255),
    bank_account character varying(255),
    fapiao_delivery character varying(255),
    avatar character varying(255),
    larger_avatar character varying(255)
);


--
-- TOC entry 318 (class 1259 OID 361235)
-- Name: sys_pm_log; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_pm_log (
    log_id bigint NOT NULL,
    project_id character varying(100),
    task_id character varying(100),
    title character varying(100),
    message_type character varying(20),
    message text,
    oper_name character varying(50),
    status integer DEFAULT 0,
    create_by character varying(64),
    create_time timestamp without time zone
);


--
-- TOC entry 317 (class 1259 OID 361234)
-- Name: sys_pm_log_log_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE public.sys_pm_log ALTER COLUMN log_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.sys_pm_log_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 320 (class 1259 OID 361244)
-- Name: sys_post; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_post (
    post_id bigint NOT NULL,
    post_code character varying(64) NOT NULL,
    post_name character varying(50) NOT NULL,
    post_sort integer NOT NULL,
    status character(1) NOT NULL,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    remark character varying(500)
);


--
-- TOC entry 319 (class 1259 OID 361243)
-- Name: sys_post_post_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE public.sys_post ALTER COLUMN post_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.sys_post_post_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 321 (class 1259 OID 361251)
-- Name: sys_re_acceptance; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_re_acceptance (
    accept_id character varying(100) NOT NULL,
    project_id character varying(100),
    task_id character varying(255) DEFAULT '0'::character varying,
    task_checked integer DEFAULT 0,
    description character varying(255),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    task_order integer DEFAULT 0
);


--
-- TOC entry 322 (class 1259 OID 361261)
-- Name: sys_re_actitem; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_re_actitem (
    py_id character varying(100) NOT NULL,
    act_id character varying(100) DEFAULT '0'::character varying,
    parent_id character varying(100) DEFAULT '0'::character varying,
    py_sort integer DEFAULT 0,
    py_label character varying(255),
    py_name character varying(255),
    action character varying(255),
    step_page character varying(255),
    property_name character varying(255),
    property_value character varying(255),
    case_type_id character varying(100),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


--
-- TOC entry 323 (class 1259 OID 361271)
-- Name: sys_re_activity; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_re_activity (
    py_id character varying(100) NOT NULL,
    case_type_id character varying(255) DEFAULT '0'::character varying,
    py_label character varying(255),
    py_name character varying(255),
    target character varying(255),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


--
-- TOC entry 324 (class 1259 OID 361279)
-- Name: sys_re_application; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_re_application (
    py_id character varying(64) NOT NULL,
    py_label character varying(255),
    py_name character varying(255),
    organization_id bigint DEFAULT 0,
    description character varying(255),
    business_objectives text,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


--
-- TOC entry 325 (class 1259 OID 361287)
-- Name: sys_re_board; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_re_board (
    py_id character varying(100) NOT NULL,
    project_id character varying(255) DEFAULT '0'::character varying,
    py_label character varying(255),
    header_color character varying(64),
    archived smallint DEFAULT 0,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    stage_sort integer DEFAULT 0
);


--
-- TOC entry 327 (class 1259 OID 361298)
-- Name: sys_re_casetype; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_re_casetype (
    id bigint NOT NULL,
    case_type_id character varying(255),
    case_sort integer DEFAULT 0,
    case_label character varying(255),
    case_name character varying(255),
    case_version character varying(20),
    case_application_id character varying(255),
    case_urgency integer DEFAULT 10,
    case_prefix character varying(255) DEFAULT 'C-'::character varying,
    case_icon character varying(30) DEFAULT 'el-icon-suitcase'::character varying,
    case_availability character(1) DEFAULT '0'::bpchar,
    case_status_work character varying(255) DEFAULT '0'::character varying,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    case_description character varying(500),
    initial_data character varying(45),
    field_tracking integer,
    email_initial_account character varying(45),
    goal date,
    deadline date,
    worklist_notify integer,
    workgroup_notify integer
);


--
-- TOC entry 326 (class 1259 OID 361297)
-- Name: sys_re_casetype_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE public.sys_re_casetype ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.sys_re_casetype_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 328 (class 1259 OID 361311)
-- Name: sys_re_checklist; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_re_checklist (
    py_id character varying(100) NOT NULL,
    task_id character varying(255) DEFAULT '0'::character varying,
    py_label character varying(255),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


--
-- TOC entry 329 (class 1259 OID 361319)
-- Name: sys_re_corres; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_re_corres (
    corres_id character varying(100) NOT NULL,
    category character varying(40),
    belongs_case character(1),
    folder_id character varying(100),
    case_id character varying(100),
    task_id character varying(100),
    file_title character varying(255),
    file_name character varying(255),
    file_type character varying(200),
    file_size bigint DEFAULT 0,
    order_num integer DEFAULT 0,
    status character(1) DEFAULT '0'::bpchar,
    del_flag character(1) DEFAULT '0'::bpchar,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    user_id bigint,
    dept_id bigint,
    summary text,
    catalog text
);


--
-- TOC entry 330 (class 1259 OID 361330)
-- Name: sys_re_datafield; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_re_datafield (
    field_id character varying(100) NOT NULL,
    field_sort integer DEFAULT 0,
    field_label character varying(255),
    field_name character varying(255),
    field_type character varying(255),
    dataobj_id character varying(100),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    is_show character(1) DEFAULT 'Y'::bpchar,
    field_category character varying(45) DEFAULT 'fixed'::character varying,
    field_detail character varying(100)
);


--
-- TOC entry 331 (class 1259 OID 361340)
-- Name: sys_re_dataobj; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_re_dataobj (
    dataobj_id character varying(100) NOT NULL,
    dataobj_sort integer DEFAULT 0,
    dataobj_label character varying(255),
    dataobj_name character varying(255),
    table_name character varying(255),
    schema_name character varying(255),
    database_name character varying(255),
    case_application_id character varying(255),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


--
-- TOC entry 333 (class 1259 OID 361349)
-- Name: sys_re_datatransform; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_re_datatransform (
    py_id bigint NOT NULL,
    case_type_id character varying(255) DEFAULT '0'::character varying,
    py_label character varying(255),
    py_name character varying(255),
    target character varying(255),
    source character varying(255),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


--
-- TOC entry 332 (class 1259 OID 361348)
-- Name: sys_re_datatransform_py_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE public.sys_re_datatransform ALTER COLUMN py_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.sys_re_datatransform_py_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 335 (class 1259 OID 361358)
-- Name: sys_re_datatype; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_re_datatype (
    data_id bigint NOT NULL,
    data_sort integer DEFAULT 0,
    data_label character varying(255),
    data_name character varying(255),
    data_type character varying(255),
    case_type_id character varying(100),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


--
-- TOC entry 334 (class 1259 OID 361357)
-- Name: sys_re_datatype_data_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE public.sys_re_datatype ALTER COLUMN data_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.sys_re_datatype_data_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 336 (class 1259 OID 361366)
-- Name: sys_re_decisiontable; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_re_decisiontable (
    py_id character varying(100) NOT NULL,
    case_type_id character varying(255) DEFAULT '0'::character varying,
    py_label character varying(255),
    py_name character varying(255),
    version character varying(64) DEFAULT '01-01-01'::character varying,
    result_property character varying(64),
    decision_table_header text,
    decision_table text,
    decision_table_rule text,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


--
-- TOC entry 337 (class 1259 OID 361375)
-- Name: sys_re_decisiontree; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_re_decisiontree (
    py_id character varying(100) NOT NULL,
    case_type_id character varying(255) DEFAULT '0'::character varying,
    py_label character varying(255),
    py_name character varying(255),
    version character varying(64) DEFAULT '01-01-01'::character varying,
    result_property character varying(64),
    decision_tree text,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


--
-- TOC entry 338 (class 1259 OID 361384)
-- Name: sys_re_dtitem; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_re_dtitem (
    py_id character varying(100) NOT NULL,
    dt_id integer DEFAULT 0,
    parent_id character varying(100) DEFAULT '0'::character varying,
    py_sort integer DEFAULT 0,
    py_label character varying(255),
    py_name character varying(255),
    case_type_id character varying(100),
    action character varying(255),
    target character varying(255),
    relation character varying(255),
    source character varying(255),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


--
-- TOC entry 339 (class 1259 OID 361394)
-- Name: sys_re_participant; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_re_participant (
    part_id character varying(100) NOT NULL,
    project_id character varying(255) DEFAULT '0'::character varying,
    part_name character varying(50),
    category character varying(20),
    role character varying(45),
    department character varying(45),
    title character varying(45),
    responsibility character varying(255),
    mobile_phone character varying(20),
    manager character varying(20),
    entry_date date,
    leave_date date,
    effort numeric(10,2) DEFAULT 0.00,
    status smallint DEFAULT 0,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    email character varying(200),
    code character varying(45)
);


--
-- TOC entry 340 (class 1259 OID 361404)
-- Name: sys_re_pmchange; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_re_pmchange (
    change_id character varying(100) NOT NULL,
    project_id character varying(100),
    task_id character varying(100),
    change_point character varying(255),
    reason character varying(255),
    applicant character varying(20),
    approver character varying(255),
    approved_result character varying(45),
    approved_comment character varying(255),
    impact_to_cost character varying(255),
    impact_to_progress character varying(255),
    impact_to_resource character varying(255),
    whatif_no_change character varying(255),
    impact_level character varying(10),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


--
-- TOC entry 341 (class 1259 OID 361411)
-- Name: sys_re_pmrisk; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_re_pmrisk (
    risk_id character varying(100) NOT NULL,
    project_id character varying(100) DEFAULT '0'::character varying,
    risk_title character varying(255),
    risk_ratio character varying(10),
    impact_level character varying(10),
    risk_level character varying(45),
    risk_plan text,
    owner character varying(45),
    status character varying(45),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    project_name character varying(255)
);


--
-- TOC entry 342 (class 1259 OID 361419)
-- Name: sys_re_process; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_re_process (
    process_id character varying(100) NOT NULL,
    process_sort integer DEFAULT 0,
    process_label character varying(255),
    process_name character varying(255),
    process_type character varying(20) DEFAULT 'sequential'::character varying,
    case_type_id character varying(100),
    stage_id character varying(100),
    skip_process character varying(20) DEFAULT 'never'::character varying,
    skip_condition character varying(255),
    deployment_name character varying(255),
    deployment_key character varying(255),
    deployment_id character varying(64),
    proc_def_id character varying(64),
    before_activity character varying(45),
    before_datatransform character varying(45),
    after_activity character varying(45),
    after_datatransform character varying(45),
    form_key character varying(64),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    reactflow_json jsonb,
    bpmn_xml text,
    status character varying(45) DEFAULT 'published'::character varying
);


--
-- TOC entry 343 (class 1259 OID 361430)
-- Name: sys_re_project; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_re_project (
    py_id character varying(100) NOT NULL,
    case_application_id character varying(255) DEFAULT '0'::character varying,
    py_name character varying(255),
    avatar character varying(255),
    category character varying(45),
    py_case_id character varying(100),
    description text,
    archived smallint DEFAULT 0,
    owner character varying(64),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    status character varying(100),
    customer_name character varying(100),
    contract_id character varying(100),
    cost_estimation numeric(10,2) DEFAULT 0.00,
    real_cost numeric(10,2) DEFAULT 0.00,
    cpi numeric(10,2) DEFAULT 0.00,
    pv numeric(10,2) DEFAULT 0.00,
    ac numeric(10,2) DEFAULT 0.00,
    ev numeric(10,2) DEFAULT 0.00,
    spi numeric(10,2) DEFAULT 0.00,
    bac numeric(10,2) DEFAULT 0.00,
    eac numeric(10,2) DEFAULT 0.00,
    etc numeric(10,2) DEFAULT 0.00,
    duration_estimation numeric(10,2) DEFAULT 0.00,
    real_duration numeric(10,2) DEFAULT 0.00,
    stage character varying(100)
);


--
-- TOC entry 344 (class 1259 OID 361451)
-- Name: sys_re_reportdefinition; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_re_reportdefinition (
    id character varying(100) NOT NULL,
    dataobj_id character varying(100),
    py_label character varying(255),
    py_name character varying(255),
    columns text,
    filters text,
    classjoins text,
    parameters text,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


--
-- TOC entry 346 (class 1259 OID 361459)
-- Name: sys_re_ruleset; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_re_ruleset (
    py_id bigint NOT NULL,
    case_application_id character varying(255) DEFAULT '0'::character varying,
    py_label character varying(255),
    py_name character varying(255),
    version character varying(64) DEFAULT '01-01-01'::character varying,
    approval_required smallint DEFAULT 0,
    locked smallint DEFAULT 0,
    password character varying(64),
    effective_start_time date,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


--
-- TOC entry 345 (class 1259 OID 361458)
-- Name: sys_re_ruleset_py_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE public.sys_re_ruleset ALTER COLUMN py_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.sys_re_ruleset_py_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 348 (class 1259 OID 361471)
-- Name: sys_re_stage; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_re_stage (
    stage_id bigint NOT NULL,
    stage_sort integer DEFAULT 0,
    stage_label character varying(255),
    stage_name character varying(255),
    stage_type character varying(100) DEFAULT 'primary'::character varying,
    case_type_id character varying(100),
    skip_stage character varying(20) DEFAULT 'never'::character varying,
    skip_condition character varying(255),
    action_after_all_completed character varying(255) DEFAULT 'movetonextstage'::character varying,
    stage_status_work character varying(100),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


--
-- TOC entry 347 (class 1259 OID 361470)
-- Name: sys_re_stage_stage_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE public.sys_re_stage ALTER COLUMN stage_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.sys_re_stage_stage_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 349 (class 1259 OID 361482)
-- Name: sys_re_strategy; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_re_strategy (
    py_id character varying(100) NOT NULL,
    case_type_id character varying(255) DEFAULT '0'::character varying,
    py_label character varying(255),
    py_name character varying(255),
    version character varying(64) DEFAULT '01-01-01'::character varying,
    result_property character varying(64),
    strategy_header text,
    strategy text,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


--
-- TOC entry 350 (class 1259 OID 361491)
-- Name: sys_re_talkingplan; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_re_talkingplan (
    plan_id character varying(100) NOT NULL,
    project_id character varying(100) DEFAULT '0'::character varying,
    counter_party character varying(50),
    required_info character varying(255),
    frequency character varying(45),
    prefer_way character varying(45),
    detail_info character varying(255),
    owner character varying(50),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


--
-- TOC entry 351 (class 1259 OID 361499)
-- Name: sys_re_template; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_re_template (
    template_id character varying(100) NOT NULL,
    case_application_id character varying(100),
    template_name character varying(100),
    category character varying(45),
    content text,
    archived smallint DEFAULT 0,
    owner character varying(64),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    design_content jsonb,
    description text,
    color character varying(45),
    cookie_name character varying(255)
);


--
-- TOC entry 352 (class 1259 OID 361507)
-- Name: sys_re_when; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_re_when (
    py_id character varying(100) NOT NULL,
    case_type_id character varying(255) DEFAULT '0'::character varying,
    py_label character varying(255),
    py_name character varying(255),
    version character varying(64) DEFAULT '01-01-01'::character varying,
    result_property character varying(64),
    when_logic_string character varying(255),
    when_expression character varying(255),
    when_relationship text,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


--
-- TOC entry 353 (class 1259 OID 361516)
-- Name: sys_re_workload; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_re_workload (
    workload_id character varying(100) NOT NULL,
    task_id character varying(100) NOT NULL,
    task_label character varying(255),
    start_date date,
    end_date date,
    duration numeric(10,2) DEFAULT 0.00,
    assignee character varying(64),
    assignee_id character varying(64),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


--
-- TOC entry 354 (class 1259 OID 361524)
-- Name: sys_re_worktask; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_re_worktask (
    task_id character varying(100) NOT NULL,
    project_id character varying(100),
    board_id character varying(255) DEFAULT '0'::character varying,
    task_label character varying(255),
    priority character varying(64),
    assignee character varying(64),
    due_date timestamp without time zone,
    category character varying(64),
    status character varying(64),
    description text,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    percentage integer DEFAULT 0,
    plan_start_date date,
    plan_end_date date,
    real_start_date date,
    real_end_date date,
    plan_duration numeric(10,2),
    human_resource integer,
    cost_estimation numeric(10,2),
    real_duration numeric(10,2),
    work_order integer DEFAULT 0,
    project_name character varying(200)
);


--
-- TOC entry 356 (class 1259 OID 361535)
-- Name: sys_role; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_role (
    role_id bigint NOT NULL,
    role_name character varying(30) NOT NULL,
    role_key character varying(100) NOT NULL,
    role_sort integer NOT NULL,
    data_scope character(1) DEFAULT '1'::bpchar,
    menu_check_strictly smallint DEFAULT 1,
    dept_check_strictly smallint DEFAULT 1,
    status character(1) NOT NULL,
    del_flag character(1) DEFAULT '0'::bpchar,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    remark character varying(500)
);


--
-- TOC entry 357 (class 1259 OID 361546)
-- Name: sys_role_dept; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_role_dept (
    role_id bigint NOT NULL,
    dept_id bigint NOT NULL
);


--
-- TOC entry 358 (class 1259 OID 361551)
-- Name: sys_role_menu; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_role_menu (
    role_id bigint NOT NULL,
    menu_id bigint NOT NULL
);


--
-- TOC entry 355 (class 1259 OID 361534)
-- Name: sys_role_role_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE public.sys_role ALTER COLUMN role_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.sys_role_role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 360 (class 1259 OID 361557)
-- Name: sys_ru_caseinstance; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_ru_caseinstance (
    id bigint NOT NULL,
    py_case_id character varying(255),
    py_label character varying(255),
    py_name character varying(255),
    case_type_id character varying(255),
    py_urgency integer DEFAULT 10,
    py_current_stage character varying(255),
    py_status_work character varying(255),
    py_case_structure text,
    py_work_page text,
    px_create_by character varying(64),
    px_create_time timestamp without time zone,
    py_update_by character varying(64),
    py_update_time timestamp without time zone,
    py_description character varying(500)
);


--
-- TOC entry 359 (class 1259 OID 361556)
-- Name: sys_ru_caseinstance_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE public.sys_ru_caseinstance ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.sys_ru_caseinstance_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 362 (class 1259 OID 361566)
-- Name: sys_sequence_id; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_sequence_id (
    sequenceid bigint NOT NULL,
    prefix character varying(255) NOT NULL,
    current_number bigint
);


--
-- TOC entry 361 (class 1259 OID 361565)
-- Name: sys_sequence_id_sequenceid_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE public.sys_sequence_id ALTER COLUMN sequenceid ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.sys_sequence_id_sequenceid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 364 (class 1259 OID 361572)
-- Name: sys_task_form; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_task_form (
    id bigint NOT NULL,
    form_id bigint,
    task_id character varying(50)
);


--
-- TOC entry 363 (class 1259 OID 361571)
-- Name: sys_task_form_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE public.sys_task_form ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.sys_task_form_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 366 (class 1259 OID 361578)
-- Name: sys_user; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_user (
    user_id bigint NOT NULL,
    organization_id bigint,
    dept_id bigint,
    user_name character varying(30) NOT NULL,
    nick_name character varying(30) NOT NULL,
    user_type character varying(2) DEFAULT '00'::character varying,
    email character varying(255),
    phonenumber character varying(11),
    sex character(1) DEFAULT '0'::bpchar,
    avatar character varying(100),
    password character varying(100),
    status character(1) DEFAULT '0'::bpchar,
    del_flag character(1) DEFAULT '0'::bpchar,
    login_ip character varying(128),
    login_date timestamp without time zone,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    remark character varying(500),
    theme character varying(45) DEFAULT 'Light'::character varying,
    font character varying(100) DEFAULT 'System'::character varying,
    language character varying(45) DEFAULT 'en'::character varying,
    sn_userid character varying(128) DEFAULT NULL::character varying
);


--
-- TOC entry 6651 (class 0 OID 0)
-- Dependencies: 366
-- Name: COLUMN sys_user.sn_userid; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.sys_user.sn_userid IS '社交媒体用户ID（当前用于企业微信userid，可扩展至其他平台）';


--
-- TOC entry 367 (class 1259 OID 361592)
-- Name: sys_user_post; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_user_post (
    user_id bigint NOT NULL,
    post_id bigint NOT NULL
);


--
-- TOC entry 368 (class 1259 OID 361597)
-- Name: sys_user_role; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.sys_user_role (
    user_id bigint NOT NULL,
    role_id bigint NOT NULL
);


--
-- TOC entry 365 (class 1259 OID 361577)
-- Name: sys_user_user_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE public.sys_user ALTER COLUMN user_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.sys_user_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 444 (class 1259 OID 371684)
-- Name: sys_webhook_path_index; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.sys_webhook_path_index (
    path character varying(255) NOT NULL,
    tenant_id character varying(64) NOT NULL,
    workflow_id character varying(64) NOT NULL,
    node_id character varying(64) NOT NULL,
    workflow_name character varying(255),
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now()
);


--
-- TOC entry 370 (class 1259 OID 361603)
-- Name: table_table_industry; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.table_table_industry (
    id integer NOT NULL,
    address character varying(100),
    gender character varying(100),
    dob date,
    name character varying(100),
    salary numeric(10,2),
    age integer
);


--
-- TOC entry 369 (class 1259 OID 361602)
-- Name: table_table_industry_id_seq; Type: SEQUENCE; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE public.table_table_industry ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.table_table_industry_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 425 (class 1259 OID 371182)
-- Name: w_connection; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.w_connection (
    id text NOT NULL,
    workflow_id text NOT NULL,
    from_node_id text NOT NULL,
    to_node_id text NOT NULL,
    from_output text DEFAULT 'main'::text NOT NULL,
    to_input text DEFAULT 'main'::text NOT NULL,
    type public."EdgeType" DEFAULT 'default'::public."EdgeType" NOT NULL,
    label text,
    data jsonb DEFAULT '{}'::jsonb NOT NULL,
    created_at timestamp(3) without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp(3) without time zone NOT NULL
);


--
-- TOC entry 423 (class 1259 OID 371168)
-- Name: w_credential; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.w_credential (
    id text NOT NULL,
    name text NOT NULL,
    value text NOT NULL,
    type public."CredentialType" NOT NULL,
    created_at timestamp(3) without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp(3) without time zone NOT NULL,
    user_id text NOT NULL,
    organization_id text,
    is_primary integer DEFAULT 0 NOT NULL
);


--
-- TOC entry 6660 (class 0 OID 0)
-- Dependencies: 423
-- Name: COLUMN w_credential.is_primary; Type: COMMENT; Schema: public; Owner: mega_vXnFBG5i
--

COMMENT ON COLUMN public.w_credential.is_primary IS '是否主凭证 0否 1是（同一 user_id + type 仅一条为 1）';


--
-- TOC entry 426 (class 1259 OID 371193)
-- Name: w_execution; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.w_execution (
    id text NOT NULL,
    workflow_id text NOT NULL,
    status public."ExecutionStatus" DEFAULT 'RUNNING'::public."ExecutionStatus" NOT NULL,
    error text,
    error_stack text,
    started_at timestamp(3) without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    completed_at timestamp(3) without time zone,
    inngest_event_id text NOT NULL,
    output jsonb
);


--
-- TOC entry 422 (class 1259 OID 371157)
-- Name: w_node; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.w_node (
    id text NOT NULL,
    workflow_id text NOT NULL,
    name text NOT NULL,
    type public."NodeType" NOT NULL,
    "position" jsonb NOT NULL,
    data jsonb DEFAULT '{}'::jsonb NOT NULL,
    inputs jsonb DEFAULT '{}'::jsonb NOT NULL,
    outputs jsonb DEFAULT '{}'::jsonb NOT NULL,
    credential_id text,
    created_at timestamp(3) without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp(3) without time zone NOT NULL
);


--
-- TOC entry 424 (class 1259 OID 371175)
-- Name: w_node_definition; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.w_node_definition (
    id text NOT NULL,
    type public."NodeType" NOT NULL,
    label text NOT NULL,
    description text NOT NULL,
    icon text NOT NULL,
    color text NOT NULL,
    parameters jsonb NOT NULL,
    created_at timestamp(3) without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp(3) without time zone NOT NULL
);


--
-- TOC entry 427 (class 1259 OID 371207)
-- Name: w_wait_resume; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.w_wait_resume (
    id text NOT NULL,
    resume_key text NOT NULL,
    execution_id text NOT NULL,
    node_id text NOT NULL,
    inngest_event_id text NOT NULL,
    created_at timestamp(3) without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp(3) without time zone NOT NULL,
    resumed_at timestamp(3) without time zone,
    workflow_id text,
    node_name text,
    mode text,
    http_method text,
    auth_config jsonb,
    response_config jsonb,
    form_schema jsonb,
    expires_at timestamp with time zone,
    payload jsonb
);


--
-- TOC entry 6665 (class 0 OID 0)
-- Dependencies: 427
-- Name: TABLE w_wait_resume; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.w_wait_resume IS 'Wait 节点预注册与恢复状态';


--
-- TOC entry 6666 (class 0 OID 0)
-- Dependencies: 427
-- Name: COLUMN w_wait_resume.resume_key; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.w_wait_resume.resume_key IS '公开回调 key，唯一';


--
-- TOC entry 6667 (class 0 OID 0)
-- Dependencies: 427
-- Name: COLUMN w_wait_resume.mode; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.w_wait_resume.mode IS 'on_webhook_call | on_form_submitted';


--
-- TOC entry 421 (class 1259 OID 371150)
-- Name: w_workflow; Type: TABLE; Schema: public; Owner: mega_vXnFBG5i
--

CREATE TABLE public.w_workflow (
    id text NOT NULL,
    name text NOT NULL,
    created_at timestamp(3) without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp(3) without time zone NOT NULL,
    user_id text NOT NULL,
    organization_id text,
    publish_assistant boolean DEFAULT false NOT NULL,
    publish_sidebar boolean DEFAULT false NOT NULL,
    is_template boolean DEFAULT false NOT NULL,
    description text DEFAULT ''::text NOT NULL
);


--
-- TOC entry 4849 (class 2604 OID 369680)
-- Name: act_evt_log log_nr_; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_evt_log ALTER COLUMN log_nr_ SET DEFAULT nextval('public.act_evt_log_log_nr__seq'::regclass);


--
-- TOC entry 4857 (class 2604 OID 369681)
-- Name: act_hi_tsk_log id_; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_tsk_log ALTER COLUMN id_ SET DEFAULT nextval('public.act_hi_tsk_log_id__seq'::regclass);


--
-- TOC entry 5000 (class 2604 OID 379546)
-- Name: crm_ai_playbook_run id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_playbook_run ALTER COLUMN id SET DEFAULT nextval('public.crm_ai_playbook_run_id_seq'::regclass);


--
-- TOC entry 4971 (class 2604 OID 379470)
-- Name: crm_ai_skill id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_skill ALTER COLUMN id SET DEFAULT nextval('public.crm_ai_skill_id_seq'::regclass);


--
-- TOC entry 4988 (class 2604 OID 379511)
-- Name: crm_ai_skill_playbook id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_skill_playbook ALTER COLUMN id SET DEFAULT nextval('public.crm_ai_skill_playbook_id_seq'::regclass);


--
-- TOC entry 4994 (class 2604 OID 379528)
-- Name: crm_ai_skill_playbook_step id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_skill_playbook_step ALTER COLUMN id SET DEFAULT nextval('public.crm_ai_skill_playbook_step_id_seq'::regclass);


--
-- TOC entry 5004 (class 2604 OID 379605)
-- Name: crm_mcp_api_key id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_mcp_api_key ALTER COLUMN id SET DEFAULT nextval('public.crm_mcp_api_key_id_seq'::regclass);


--
-- TOC entry 5008 (class 2604 OID 379622)
-- Name: crm_mcp_audit_log id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_mcp_audit_log ALTER COLUMN id SET DEFAULT nextval('public.crm_mcp_audit_log_id_seq'::regclass);


--
-- TOC entry 4982 (class 2604 OID 379494)
-- Name: crm_mcp_server id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_mcp_server ALTER COLUMN id SET DEFAULT nextval('public.crm_mcp_server_id_seq'::regclass);


--
-- TOC entry 4939 (class 2604 OID 371451)
-- Name: crm_sf_express_config id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_sf_express_config ALTER COLUMN id SET DEFAULT nextval('public.crm_sf_express_config_id_seq'::regclass);


--
-- TOC entry 4947 (class 2604 OID 371469)
-- Name: crm_sf_express_token id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_sf_express_token ALTER COLUMN id SET DEFAULT nextval('public.crm_sf_express_token_id_seq'::regclass);


--
-- TOC entry 5028 (class 2604 OID 380104)
-- Name: crm_subject_map id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_subject_map ALTER COLUMN id SET DEFAULT nextval('public.crm_subject_map_id_seq'::regclass);


--
-- TOC entry 5030 (class 2604 OID 380124)
-- Name: crm_wecom_aibot_config id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_aibot_config ALTER COLUMN id SET DEFAULT nextval('public.crm_wecom_aibot_config_id_seq'::regclass);


--
-- TOC entry 5034 (class 2604 OID 380138)
-- Name: crm_wecom_aibot_connection id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_aibot_connection ALTER COLUMN id SET DEFAULT nextval('public.crm_wecom_aibot_connection_id_seq'::regclass);


--
-- TOC entry 5025 (class 2604 OID 380076)
-- Name: crm_wecom_callback_event id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_callback_event ALTER COLUMN id SET DEFAULT nextval('public.crm_wecom_callback_event_id_seq'::regclass);


--
-- TOC entry 4700 (class 2604 OID 371437)
-- Name: crm_wecom_config id; Type: DEFAULT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_wecom_config ALTER COLUMN id SET DEFAULT nextval('public.crm_wecom_config_id_seq'::regclass);


--
-- TOC entry 4704 (class 2604 OID 371439)
-- Name: crm_wecom_contact_way id; Type: DEFAULT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_wecom_contact_way ALTER COLUMN id SET DEFAULT nextval('public.crm_wecom_contact_way_id_seq'::regclass);


--
-- TOC entry 4707 (class 2604 OID 371441)
-- Name: crm_wecom_external_contact id; Type: DEFAULT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_wecom_external_contact ALTER COLUMN id SET DEFAULT nextval('public.crm_wecom_external_contact_id_seq'::regclass);


--
-- TOC entry 5013 (class 2604 OID 379685)
-- Name: crm_wecom_kf_config id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_kf_config ALTER COLUMN id SET DEFAULT nextval('public.crm_wecom_kf_config_id_seq'::regclass);


--
-- TOC entry 5017 (class 2604 OID 379741)
-- Name: crm_wecom_kf_message id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_kf_message ALTER COLUMN id SET DEFAULT nextval('public.crm_wecom_kf_message_id_seq'::regclass);


--
-- TOC entry 4713 (class 2604 OID 371443)
-- Name: crm_wecom_mass_task id; Type: DEFAULT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_wecom_mass_task ALTER COLUMN id SET DEFAULT nextval('public.crm_wecom_mass_task_id_seq'::regclass);


--
-- TOC entry 5021 (class 2604 OID 380022)
-- Name: crm_wecom_material id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_material ALTER COLUMN id SET DEFAULT nextval('public.crm_wecom_material_id_seq'::regclass);


--
-- TOC entry 4931 (class 2604 OID 371391)
-- Name: crm_wecom_welcome_log id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_welcome_log ALTER COLUMN id SET DEFAULT nextval('public.crm_wecom_welcome_log_id_seq'::regclass);


--
-- TOC entry 6061 (class 0 OID 369428)
-- Dependencies: 371


--
-- TOC entry 6063 (class 0 OID 369435)
-- Dependencies: 373


--
-- TOC entry 6064 (class 0 OID 369440)
-- Dependencies: 374


--
-- TOC entry 6065 (class 0 OID 369443)
-- Dependencies: 375


--
-- TOC entry 6066 (class 0 OID 369450)
-- Dependencies: 376


--
-- TOC entry 6067 (class 0 OID 369455)
-- Dependencies: 377


--
-- TOC entry 6068 (class 0 OID 369460)
-- Dependencies: 378


--
-- TOC entry 6069 (class 0 OID 369465)
-- Dependencies: 379


--
-- TOC entry 6070 (class 0 OID 369470)
-- Dependencies: 380


--
-- TOC entry 6071 (class 0 OID 369475)
-- Dependencies: 381


--
-- TOC entry 6072 (class 0 OID 369482)
-- Dependencies: 382


--
-- TOC entry 6073 (class 0 OID 369489)
-- Dependencies: 383


--
-- TOC entry 6075 (class 0 OID 369496)
-- Dependencies: 385


--
-- TOC entry 6076 (class 0 OID 369502)
-- Dependencies: 386


--
-- TOC entry 6077 (class 0 OID 369507)
-- Dependencies: 387


--
-- TOC entry 6078 (class 0 OID 369512)
-- Dependencies: 388


--
-- TOC entry 6079 (class 0 OID 369517)
-- Dependencies: 389


--
-- TOC entry 6080 (class 0 OID 369520)
-- Dependencies: 390


--
-- TOC entry 6081 (class 0 OID 369523)
-- Dependencies: 391


--
-- TOC entry 6082 (class 0 OID 369528)
-- Dependencies: 392


--
-- TOC entry 6083 (class 0 OID 369531)
-- Dependencies: 393


--
-- TOC entry 6084 (class 0 OID 369536)
-- Dependencies: 394


--
-- TOC entry 6085 (class 0 OID 369542)
-- Dependencies: 395


--
-- TOC entry 6086 (class 0 OID 369545)
-- Dependencies: 396


--
-- TOC entry 6087 (class 0 OID 369551)
-- Dependencies: 397


--
-- TOC entry 6088 (class 0 OID 369557)
-- Dependencies: 398


--
-- TOC entry 6089 (class 0 OID 369564)
-- Dependencies: 399


--
-- TOC entry 6090 (class 0 OID 369571)
-- Dependencies: 400


--
-- TOC entry 6091 (class 0 OID 369577)
-- Dependencies: 401


--
-- TOC entry 6092 (class 0 OID 369582)
-- Dependencies: 402


--
-- TOC entry 6093 (class 0 OID 369588)
-- Dependencies: 403


--
-- TOC entry 6094 (class 0 OID 369594)
-- Dependencies: 404


--
-- TOC entry 6095 (class 0 OID 369600)
-- Dependencies: 405


--
-- TOC entry 6096 (class 0 OID 369606)
-- Dependencies: 406


--
-- TOC entry 6097 (class 0 OID 369611)
-- Dependencies: 407


--
-- TOC entry 6098 (class 0 OID 369617)
-- Dependencies: 408


--
-- TOC entry 6099 (class 0 OID 369623)
-- Dependencies: 409


--
-- TOC entry 6100 (class 0 OID 369629)
-- Dependencies: 410


--
-- TOC entry 6101 (class 0 OID 369635)
-- Dependencies: 411


--
-- TOC entry 5899 (class 0 OID 360271)
-- Dependencies: 209


--
-- TOC entry 6120 (class 0 OID 371309)
-- Dependencies: 430


--
-- TOC entry 5900 (class 0 OID 360287)
-- Dependencies: 210


--
-- TOC entry 5901 (class 0 OID 360298)
-- Dependencies: 211


--
-- TOC entry 5902 (class 0 OID 360306)
-- Dependencies: 212


--
-- TOC entry 5904 (class 0 OID 360315)
-- Dependencies: 214


--
-- TOC entry 6140 (class 0 OID 371819)
-- Dependencies: 450


--
-- TOC entry 6139 (class 0 OID 371810)
-- Dependencies: 449


--
-- TOC entry 6136 (class 0 OID 371741)
-- Dependencies: 446


--
-- TOC entry 6151 (class 0 OID 379543)
-- Dependencies: 461


--
-- TOC entry 6143 (class 0 OID 379467)
-- Dependencies: 453


--
-- TOC entry 6147 (class 0 OID 379508)
-- Dependencies: 457


--
-- TOC entry 6149 (class 0 OID 379525)
-- Dependencies: 459


--
-- TOC entry 6141 (class 0 OID 371833)
-- Dependencies: 451


--
-- TOC entry 5905 (class 0 OID 360325)
-- Dependencies: 215


--
-- TOC entry 5906 (class 0 OID 360339)
-- Dependencies: 216


--
-- TOC entry 5907 (class 0 OID 360346)
-- Dependencies: 217


--
-- TOC entry 5908 (class 0 OID 360357)
-- Dependencies: 218


--
-- TOC entry 5909 (class 0 OID 360366)
-- Dependencies: 219


--
-- TOC entry 5910 (class 0 OID 360374)
-- Dependencies: 220


--
-- TOC entry 5911 (class 0 OID 360382)
-- Dependencies: 221


--
-- TOC entry 5912 (class 0 OID 360391)
-- Dependencies: 222


--
-- TOC entry 5913 (class 0 OID 360412)
-- Dependencies: 223


--
-- TOC entry 5914 (class 0 OID 360420)
-- Dependencies: 224


--
-- TOC entry 5915 (class 0 OID 360428)
-- Dependencies: 225


--
-- TOC entry 5916 (class 0 OID 360453)
-- Dependencies: 226


--
-- TOC entry 6131 (class 0 OID 371605)
-- Dependencies: 441


--
-- TOC entry 6132 (class 0 OID 371616)
-- Dependencies: 442


--
-- TOC entry 6133 (class 0 OID 371633)
-- Dependencies: 443


--
-- TOC entry 5917 (class 0 OID 360460)
-- Dependencies: 227


--
-- TOC entry 5918 (class 0 OID 360471)
-- Dependencies: 228


--
-- TOC entry 6137 (class 0 OID 371783)
-- Dependencies: 447


--
-- TOC entry 5919 (class 0 OID 360478)
-- Dependencies: 229


--
-- TOC entry 5920 (class 0 OID 360490)
-- Dependencies: 230


--
-- TOC entry 5921 (class 0 OID 360502)
-- Dependencies: 231


--
-- TOC entry 6138 (class 0 OID 371800)
-- Dependencies: 448


--
-- TOC entry 5922 (class 0 OID 360515)
-- Dependencies: 232


--
-- TOC entry 5923 (class 0 OID 360531)
-- Dependencies: 233


--
-- TOC entry 5924 (class 0 OID 360544)
-- Dependencies: 234


--
-- TOC entry 5925 (class 0 OID 360555)
-- Dependencies: 235


--
-- TOC entry 5926 (class 0 OID 360563)
-- Dependencies: 236


--
-- TOC entry 5927 (class 0 OID 360571)
-- Dependencies: 237


--
-- TOC entry 5928 (class 0 OID 360586)
-- Dependencies: 238


--
-- TOC entry 5929 (class 0 OID 360594)
-- Dependencies: 239


--
-- TOC entry 6153 (class 0 OID 379602)
-- Dependencies: 463


--
-- TOC entry 6155 (class 0 OID 379619)
-- Dependencies: 465


--
-- TOC entry 6145 (class 0 OID 379491)
-- Dependencies: 455


--
-- TOC entry 5930 (class 0 OID 360610)
-- Dependencies: 240


--
-- TOC entry 5931 (class 0 OID 360622)
-- Dependencies: 241


--
-- TOC entry 5932 (class 0 OID 360629)
-- Dependencies: 242


--
-- TOC entry 5933 (class 0 OID 360638)
-- Dependencies: 243


--
-- TOC entry 5934 (class 0 OID 360653)
-- Dependencies: 244


--
-- TOC entry 5935 (class 0 OID 360662)
-- Dependencies: 245


--
-- TOC entry 5936 (class 0 OID 360672)
-- Dependencies: 246


--
-- TOC entry 5937 (class 0 OID 360681)
-- Dependencies: 247


--
-- TOC entry 5938 (class 0 OID 360705)
-- Dependencies: 248


--
-- TOC entry 5939 (class 0 OID 360712)
-- Dependencies: 249


--
-- TOC entry 6119 (class 0 OID 371280)
-- Dependencies: 429


--
-- TOC entry 5940 (class 0 OID 360726)
-- Dependencies: 250


--
-- TOC entry 5941 (class 0 OID 360737)
-- Dependencies: 251


--
-- TOC entry 5942 (class 0 OID 360745)
-- Dependencies: 252


--
-- TOC entry 5943 (class 0 OID 360753)
-- Dependencies: 253


--
-- TOC entry 5944 (class 0 OID 360763)
-- Dependencies: 254


--
-- TOC entry 5945 (class 0 OID 360777)
-- Dependencies: 255


--
-- TOC entry 5946 (class 0 OID 360788)
-- Dependencies: 256


--
-- TOC entry 5947 (class 0 OID 360806)
-- Dependencies: 257


--
-- TOC entry 5948 (class 0 OID 360814)
-- Dependencies: 258


--
-- TOC entry 5949 (class 0 OID 360821)
-- Dependencies: 259


--
-- TOC entry 5950 (class 0 OID 360828)
-- Dependencies: 260


--
-- TOC entry 6135 (class 0 OID 371695)
-- Dependencies: 445


--
-- TOC entry 5951 (class 0 OID 360838)
-- Dependencies: 261


--
-- TOC entry 6128 (class 0 OID 371448)
-- Dependencies: 438


--
-- TOC entry 6130 (class 0 OID 371466)
-- Dependencies: 440


--
-- TOC entry 5952 (class 0 OID 360846)
-- Dependencies: 262


--
-- TOC entry 6165 (class 0 OID 380101)
-- Dependencies: 475


--
-- TOC entry 5953 (class 0 OID 360854)
-- Dependencies: 263


--
-- TOC entry 5954 (class 0 OID 360861)
-- Dependencies: 264


--
-- TOC entry 5955 (class 0 OID 360874)
-- Dependencies: 265


--
-- TOC entry 5956 (class 0 OID 360884)
-- Dependencies: 266


--
-- TOC entry 5957 (class 0 OID 360890)
-- Dependencies: 267


--
-- TOC entry 5958 (class 0 OID 360897)
-- Dependencies: 268


--
-- TOC entry 5959 (class 0 OID 360905)
-- Dependencies: 269


--
-- TOC entry 6167 (class 0 OID 380121)
-- Dependencies: 477


--
-- TOC entry 6169 (class 0 OID 380135)
-- Dependencies: 479


--
-- TOC entry 6163 (class 0 OID 380073)
-- Dependencies: 473


--
-- TOC entry 5960 (class 0 OID 360916)
-- Dependencies: 270


--
-- TOC entry 5961 (class 0 OID 360924)
-- Dependencies: 271


--
-- TOC entry 5962 (class 0 OID 360932)
-- Dependencies: 272


--
-- TOC entry 6157 (class 0 OID 379682)
-- Dependencies: 467


--
-- TOC entry 6159 (class 0 OID 379738)
-- Dependencies: 469


--
-- TOC entry 5963 (class 0 OID 360941)
-- Dependencies: 273


--
-- TOC entry 6161 (class 0 OID 380019)
-- Dependencies: 471


--
-- TOC entry 6122 (class 0 OID 371388)
-- Dependencies: 432


--
-- TOC entry 5964 (class 0 OID 360948)
-- Dependencies: 274


--
-- TOC entry 6118 (class 0 OID 371261)
-- Dependencies: 428


--
-- TOC entry 5966 (class 0 OID 360958)
-- Dependencies: 276


--
-- TOC entry 5967 (class 0 OID 360966)
-- Dependencies: 277


--
-- TOC entry 5968 (class 0 OID 360973)
-- Dependencies: 278


--
-- TOC entry 5969 (class 0 OID 360981)
-- Dependencies: 279


--
-- TOC entry 5970 (class 0 OID 360989)
-- Dependencies: 280


--
-- TOC entry 5971 (class 0 OID 360996)
-- Dependencies: 281


--
-- TOC entry 6102 (class 0 OID 369640)
-- Dependencies: 412


--
-- TOC entry 6103 (class 0 OID 369645)
-- Dependencies: 413


--
-- TOC entry 6104 (class 0 OID 369650)
-- Dependencies: 414


--
-- TOC entry 6105 (class 0 OID 369653)
-- Dependencies: 415


--
-- TOC entry 6106 (class 0 OID 369658)
-- Dependencies: 416


--
-- TOC entry 6107 (class 0 OID 369663)
-- Dependencies: 417


--
-- TOC entry 6108 (class 0 OID 369668)
-- Dependencies: 418


--
-- TOC entry 6109 (class 0 OID 369674)
-- Dependencies: 419


--
-- TOC entry 5973 (class 0 OID 361050)
-- Dependencies: 283


--
-- TOC entry 5975 (class 0 OID 361061)
-- Dependencies: 285


--
-- TOC entry 5977 (class 0 OID 361070)
-- Dependencies: 287


--
-- TOC entry 5979 (class 0 OID 361079)
-- Dependencies: 289


--
-- TOC entry 6110 (class 0 OID 371083)
-- Dependencies: 420


--
-- TOC entry 5981 (class 0 OID 361088)
-- Dependencies: 291


--
-- TOC entry 5983 (class 0 OID 361097)
-- Dependencies: 293


--
-- TOC entry 5985 (class 0 OID 361103)
-- Dependencies: 295


--
-- TOC entry 5987 (class 0 OID 361113)
-- Dependencies: 297


--
-- TOC entry 5989 (class 0 OID 361124)
-- Dependencies: 299


--
-- TOC entry 5991 (class 0 OID 361133)
-- Dependencies: 301


--
-- TOC entry 5993 (class 0 OID 361149)
-- Dependencies: 303


--
-- TOC entry 5994 (class 0 OID 361156)
-- Dependencies: 304


--
-- TOC entry 5995 (class 0 OID 361163)
-- Dependencies: 305


--
-- TOC entry 5997 (class 0 OID 361175)
-- Dependencies: 307


--
-- TOC entry 5999 (class 0 OID 361184)
-- Dependencies: 309


--
-- TOC entry 6001 (class 0 OID 361193)
-- Dependencies: 311


--
-- TOC entry 6003 (class 0 OID 361208)
-- Dependencies: 313


--
-- TOC entry 6005 (class 0 OID 361217)
-- Dependencies: 315


--
-- TOC entry 6006 (class 0 OID 361227)
-- Dependencies: 316


--
-- TOC entry 6008 (class 0 OID 361235)
-- Dependencies: 318


--
-- TOC entry 6010 (class 0 OID 361244)
-- Dependencies: 320


--
-- TOC entry 6011 (class 0 OID 361251)
-- Dependencies: 321


--
-- TOC entry 6012 (class 0 OID 361261)
-- Dependencies: 322


--
-- TOC entry 6013 (class 0 OID 361271)
-- Dependencies: 323


--
-- TOC entry 6014 (class 0 OID 361279)
-- Dependencies: 324


--
-- TOC entry 6015 (class 0 OID 361287)
-- Dependencies: 325


--
-- TOC entry 6017 (class 0 OID 361298)
-- Dependencies: 327


--
-- TOC entry 6018 (class 0 OID 361311)
-- Dependencies: 328


--
-- TOC entry 6019 (class 0 OID 361319)
-- Dependencies: 329


--
-- TOC entry 6020 (class 0 OID 361330)
-- Dependencies: 330


--
-- TOC entry 6021 (class 0 OID 361340)
-- Dependencies: 331


--
-- TOC entry 6023 (class 0 OID 361349)
-- Dependencies: 333


--
-- TOC entry 6025 (class 0 OID 361358)
-- Dependencies: 335


--
-- TOC entry 6026 (class 0 OID 361366)
-- Dependencies: 336


--
-- TOC entry 6027 (class 0 OID 361375)
-- Dependencies: 337


--
-- TOC entry 6028 (class 0 OID 361384)
-- Dependencies: 338


--
-- TOC entry 6029 (class 0 OID 361394)
-- Dependencies: 339


--
-- TOC entry 6030 (class 0 OID 361404)
-- Dependencies: 340


--
-- TOC entry 6031 (class 0 OID 361411)
-- Dependencies: 341


--
-- TOC entry 6032 (class 0 OID 361419)
-- Dependencies: 342


--
-- TOC entry 6033 (class 0 OID 361430)
-- Dependencies: 343


--
-- TOC entry 6034 (class 0 OID 361451)
-- Dependencies: 344


--
-- TOC entry 6036 (class 0 OID 361459)
-- Dependencies: 346


--
-- TOC entry 6038 (class 0 OID 361471)
-- Dependencies: 348


--
-- TOC entry 6039 (class 0 OID 361482)
-- Dependencies: 349


--
-- TOC entry 6040 (class 0 OID 361491)
-- Dependencies: 350


--
-- TOC entry 6041 (class 0 OID 361499)
-- Dependencies: 351


--
-- TOC entry 6042 (class 0 OID 361507)
-- Dependencies: 352


--
-- TOC entry 6043 (class 0 OID 361516)
-- Dependencies: 353


--
-- TOC entry 6044 (class 0 OID 361524)
-- Dependencies: 354


--
-- TOC entry 6046 (class 0 OID 361535)
-- Dependencies: 356


--
-- TOC entry 6047 (class 0 OID 361546)
-- Dependencies: 357


--
-- TOC entry 6048 (class 0 OID 361551)
-- Dependencies: 358


--
-- TOC entry 6050 (class 0 OID 361557)
-- Dependencies: 360


--
-- TOC entry 6052 (class 0 OID 361566)
-- Dependencies: 362


--
-- TOC entry 6054 (class 0 OID 361572)
-- Dependencies: 364


--
-- TOC entry 6056 (class 0 OID 361578)
-- Dependencies: 366


--
-- TOC entry 6057 (class 0 OID 361592)
-- Dependencies: 367


--
-- TOC entry 6058 (class 0 OID 361597)
-- Dependencies: 368


--
-- TOC entry 6134 (class 0 OID 371684)
-- Dependencies: 444


--
-- TOC entry 6060 (class 0 OID 361603)
-- Dependencies: 370


--
-- TOC entry 6115 (class 0 OID 371182)
-- Dependencies: 425


--
-- TOC entry 6113 (class 0 OID 371168)
-- Dependencies: 423


--
-- TOC entry 6116 (class 0 OID 371193)
-- Dependencies: 426


--
-- TOC entry 6112 (class 0 OID 371157)
-- Dependencies: 422


--
-- TOC entry 6114 (class 0 OID 371175)
-- Dependencies: 424


--
-- TOC entry 6117 (class 0 OID 371207)
-- Dependencies: 427


--
-- TOC entry 6111 (class 0 OID 371150)
-- Dependencies: 421


--
-- TOC entry 6670 (class 0 OID 0)
-- Dependencies: 372
-- Name: act_evt_log_log_nr__seq; Type: SEQUENCE SET; Schema: public; Owner: luohd
--


--
-- TOC entry 6671 (class 0 OID 0)
-- Dependencies: 384
-- Name: act_hi_tsk_log_id__seq; Type: SEQUENCE SET; Schema: public; Owner: luohd
--


--
-- TOC entry 6672 (class 0 OID 0)
-- Dependencies: 213
-- Name: crm_agent_tool_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6673 (class 0 OID 0)
-- Dependencies: 460
-- Name: crm_ai_playbook_run_id_seq; Type: SEQUENCE SET; Schema: public; Owner: luohd
--


--
-- TOC entry 6674 (class 0 OID 0)
-- Dependencies: 452
-- Name: crm_ai_skill_id_seq; Type: SEQUENCE SET; Schema: public; Owner: luohd
--


--
-- TOC entry 6675 (class 0 OID 0)
-- Dependencies: 456
-- Name: crm_ai_skill_playbook_id_seq; Type: SEQUENCE SET; Schema: public; Owner: luohd
--


--
-- TOC entry 6676 (class 0 OID 0)
-- Dependencies: 458
-- Name: crm_ai_skill_playbook_step_id_seq; Type: SEQUENCE SET; Schema: public; Owner: luohd
--


--
-- TOC entry 6677 (class 0 OID 0)
-- Dependencies: 462
-- Name: crm_mcp_api_key_id_seq; Type: SEQUENCE SET; Schema: public; Owner: luohd
--


--
-- TOC entry 6678 (class 0 OID 0)
-- Dependencies: 464
-- Name: crm_mcp_audit_log_id_seq; Type: SEQUENCE SET; Schema: public; Owner: luohd
--


--
-- TOC entry 6679 (class 0 OID 0)
-- Dependencies: 454
-- Name: crm_mcp_server_id_seq; Type: SEQUENCE SET; Schema: public; Owner: luohd
--


--
-- TOC entry 6680 (class 0 OID 0)
-- Dependencies: 437
-- Name: crm_sf_express_config_id_seq; Type: SEQUENCE SET; Schema: public; Owner: luohd
--


--
-- TOC entry 6681 (class 0 OID 0)
-- Dependencies: 439
-- Name: crm_sf_express_token_id_seq; Type: SEQUENCE SET; Schema: public; Owner: luohd
--


--
-- TOC entry 6682 (class 0 OID 0)
-- Dependencies: 474
-- Name: crm_subject_map_id_seq; Type: SEQUENCE SET; Schema: public; Owner: luohd
--


--
-- TOC entry 6683 (class 0 OID 0)
-- Dependencies: 476
-- Name: crm_wecom_aibot_config_id_seq; Type: SEQUENCE SET; Schema: public; Owner: luohd
--


--
-- TOC entry 6684 (class 0 OID 0)
-- Dependencies: 478
-- Name: crm_wecom_aibot_connection_id_seq; Type: SEQUENCE SET; Schema: public; Owner: luohd
--


--
-- TOC entry 6685 (class 0 OID 0)
-- Dependencies: 472
-- Name: crm_wecom_callback_event_id_seq; Type: SEQUENCE SET; Schema: public; Owner: luohd
--


--
-- TOC entry 6686 (class 0 OID 0)
-- Dependencies: 433
-- Name: crm_wecom_config_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6687 (class 0 OID 0)
-- Dependencies: 434
-- Name: crm_wecom_contact_way_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6688 (class 0 OID 0)
-- Dependencies: 435
-- Name: crm_wecom_external_contact_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6689 (class 0 OID 0)
-- Dependencies: 466
-- Name: crm_wecom_kf_config_id_seq; Type: SEQUENCE SET; Schema: public; Owner: luohd
--


--
-- TOC entry 6690 (class 0 OID 0)
-- Dependencies: 468
-- Name: crm_wecom_kf_message_id_seq; Type: SEQUENCE SET; Schema: public; Owner: luohd
--


--
-- TOC entry 6691 (class 0 OID 0)
-- Dependencies: 436
-- Name: crm_wecom_mass_task_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6692 (class 0 OID 0)
-- Dependencies: 470
-- Name: crm_wecom_material_id_seq; Type: SEQUENCE SET; Schema: public; Owner: luohd
--


--
-- TOC entry 6693 (class 0 OID 0)
-- Dependencies: 431
-- Name: crm_wecom_welcome_log_id_seq; Type: SEQUENCE SET; Schema: public; Owner: luohd
--


--
-- TOC entry 6694 (class 0 OID 0)
-- Dependencies: 275
-- Name: crm_workflow_trigger_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6695 (class 0 OID 0)
-- Dependencies: 284
-- Name: gen_table_column_column_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6696 (class 0 OID 0)
-- Dependencies: 282
-- Name: gen_table_table_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6697 (class 0 OID 0)
-- Dependencies: 286
-- Name: master_registration_regist_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6698 (class 0 OID 0)
-- Dependencies: 288
-- Name: master_tenant_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6699 (class 0 OID 0)
-- Dependencies: 290
-- Name: sys_config_config_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6700 (class 0 OID 0)
-- Dependencies: 292
-- Name: sys_deploy_form_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6701 (class 0 OID 0)
-- Dependencies: 294
-- Name: sys_dept_dept_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6702 (class 0 OID 0)
-- Dependencies: 296
-- Name: sys_dict_data_dict_code_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6703 (class 0 OID 0)
-- Dependencies: 298
-- Name: sys_dict_type_dict_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6704 (class 0 OID 0)
-- Dependencies: 300
-- Name: sys_ems_corres_corres_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6705 (class 0 OID 0)
-- Dependencies: 302
-- Name: sys_form_form_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6706 (class 0 OID 0)
-- Dependencies: 306
-- Name: sys_job_log_job_log_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6707 (class 0 OID 0)
-- Dependencies: 308
-- Name: sys_logininfor_info_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6708 (class 0 OID 0)
-- Dependencies: 310
-- Name: sys_menu_menu_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6709 (class 0 OID 0)
-- Dependencies: 312
-- Name: sys_notice_notice_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6710 (class 0 OID 0)
-- Dependencies: 314
-- Name: sys_oper_log_oper_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6711 (class 0 OID 0)
-- Dependencies: 317
-- Name: sys_pm_log_log_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6712 (class 0 OID 0)
-- Dependencies: 319
-- Name: sys_post_post_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6713 (class 0 OID 0)
-- Dependencies: 326
-- Name: sys_re_casetype_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6714 (class 0 OID 0)
-- Dependencies: 332
-- Name: sys_re_datatransform_py_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6715 (class 0 OID 0)
-- Dependencies: 334
-- Name: sys_re_datatype_data_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6716 (class 0 OID 0)
-- Dependencies: 345
-- Name: sys_re_ruleset_py_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6717 (class 0 OID 0)
-- Dependencies: 347
-- Name: sys_re_stage_stage_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6718 (class 0 OID 0)
-- Dependencies: 355
-- Name: sys_role_role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6719 (class 0 OID 0)
-- Dependencies: 359
-- Name: sys_ru_caseinstance_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6720 (class 0 OID 0)
-- Dependencies: 361
-- Name: sys_sequence_id_sequenceid_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6721 (class 0 OID 0)
-- Dependencies: 363
-- Name: sys_task_form_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6722 (class 0 OID 0)
-- Dependencies: 365
-- Name: sys_user_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 6723 (class 0 OID 0)
-- Dependencies: 369
-- Name: table_table_industry_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mega_vXnFBG5i
--


--
-- TOC entry 5316 (class 2606 OID 369683)
-- Name: act_evt_log act_evt_log_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_evt_log
    ADD CONSTRAINT act_evt_log_pkey PRIMARY KEY (log_nr_);


--
-- TOC entry 5318 (class 2606 OID 369685)
-- Name: act_ge_bytearray act_ge_bytearray_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ge_bytearray
    ADD CONSTRAINT act_ge_bytearray_pkey PRIMARY KEY (id_);


--
-- TOC entry 5321 (class 2606 OID 369687)
-- Name: act_ge_property act_ge_property_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ge_property
    ADD CONSTRAINT act_ge_property_pkey PRIMARY KEY (name_);


--
-- TOC entry 5323 (class 2606 OID 369689)
-- Name: act_hi_actinst act_hi_actinst_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_actinst
    ADD CONSTRAINT act_hi_actinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 5329 (class 2606 OID 369691)
-- Name: act_hi_attachment act_hi_attachment_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_attachment
    ADD CONSTRAINT act_hi_attachment_pkey PRIMARY KEY (id_);


--
-- TOC entry 5331 (class 2606 OID 369693)
-- Name: act_hi_comment act_hi_comment_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_comment
    ADD CONSTRAINT act_hi_comment_pkey PRIMARY KEY (id_);


--
-- TOC entry 5333 (class 2606 OID 369695)
-- Name: act_hi_detail act_hi_detail_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_detail
    ADD CONSTRAINT act_hi_detail_pkey PRIMARY KEY (id_);


--
-- TOC entry 5340 (class 2606 OID 369697)
-- Name: act_hi_entitylink act_hi_entitylink_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_entitylink
    ADD CONSTRAINT act_hi_entitylink_pkey PRIMARY KEY (id_);


--
-- TOC entry 5346 (class 2606 OID 369699)
-- Name: act_hi_identitylink act_hi_identitylink_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_identitylink
    ADD CONSTRAINT act_hi_identitylink_pkey PRIMARY KEY (id_);


--
-- TOC entry 5354 (class 2606 OID 369701)
-- Name: act_hi_procinst act_hi_procinst_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_procinst
    ADD CONSTRAINT act_hi_procinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 5356 (class 2606 OID 369703)
-- Name: act_hi_procinst act_hi_procinst_proc_inst_id__key; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_procinst
    ADD CONSTRAINT act_hi_procinst_proc_inst_id__key UNIQUE (proc_inst_id_);


--
-- TOC entry 5360 (class 2606 OID 369705)
-- Name: act_hi_taskinst act_hi_taskinst_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_taskinst
    ADD CONSTRAINT act_hi_taskinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 5366 (class 2606 OID 369707)
-- Name: act_hi_tsk_log act_hi_tsk_log_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_tsk_log
    ADD CONSTRAINT act_hi_tsk_log_pkey PRIMARY KEY (id_);


--
-- TOC entry 5368 (class 2606 OID 369709)
-- Name: act_hi_varinst act_hi_varinst_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_varinst
    ADD CONSTRAINT act_hi_varinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 5376 (class 2606 OID 369711)
-- Name: act_id_bytearray act_id_bytearray_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_bytearray
    ADD CONSTRAINT act_id_bytearray_pkey PRIMARY KEY (id_);


--
-- TOC entry 5378 (class 2606 OID 369713)
-- Name: act_id_group act_id_group_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_group
    ADD CONSTRAINT act_id_group_pkey PRIMARY KEY (id_);


--
-- TOC entry 5380 (class 2606 OID 369715)
-- Name: act_id_info act_id_info_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_info
    ADD CONSTRAINT act_id_info_pkey PRIMARY KEY (id_);


--
-- TOC entry 5382 (class 2606 OID 369717)
-- Name: act_id_membership act_id_membership_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_membership
    ADD CONSTRAINT act_id_membership_pkey PRIMARY KEY (user_id_, group_id_);


--
-- TOC entry 5390 (class 2606 OID 369719)
-- Name: act_id_priv_mapping act_id_priv_mapping_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_priv_mapping
    ADD CONSTRAINT act_id_priv_mapping_pkey PRIMARY KEY (id_);


--
-- TOC entry 5386 (class 2606 OID 369721)
-- Name: act_id_priv act_id_priv_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_priv
    ADD CONSTRAINT act_id_priv_pkey PRIMARY KEY (id_);


--
-- TOC entry 5395 (class 2606 OID 369723)
-- Name: act_id_property act_id_property_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_property
    ADD CONSTRAINT act_id_property_pkey PRIMARY KEY (name_);


--
-- TOC entry 5397 (class 2606 OID 369725)
-- Name: act_id_token act_id_token_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_token
    ADD CONSTRAINT act_id_token_pkey PRIMARY KEY (id_);


--
-- TOC entry 5399 (class 2606 OID 369727)
-- Name: act_id_user act_id_user_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_user
    ADD CONSTRAINT act_id_user_pkey PRIMARY KEY (id_);


--
-- TOC entry 5403 (class 2606 OID 369729)
-- Name: act_procdef_info act_procdef_info_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_procdef_info
    ADD CONSTRAINT act_procdef_info_pkey PRIMARY KEY (id_);


--
-- TOC entry 5407 (class 2606 OID 369731)
-- Name: act_re_deployment act_re_deployment_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_re_deployment
    ADD CONSTRAINT act_re_deployment_pkey PRIMARY KEY (id_);


--
-- TOC entry 5412 (class 2606 OID 369733)
-- Name: act_re_model act_re_model_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_re_model
    ADD CONSTRAINT act_re_model_pkey PRIMARY KEY (id_);


--
-- TOC entry 5414 (class 2606 OID 369735)
-- Name: act_re_procdef act_re_procdef_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_re_procdef
    ADD CONSTRAINT act_re_procdef_pkey PRIMARY KEY (id_);


--
-- TOC entry 5425 (class 2606 OID 369737)
-- Name: act_ru_actinst act_ru_actinst_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_actinst
    ADD CONSTRAINT act_ru_actinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 5436 (class 2606 OID 369739)
-- Name: act_ru_deadletter_job act_ru_deadletter_job_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_deadletter_job
    ADD CONSTRAINT act_ru_deadletter_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 5442 (class 2606 OID 369741)
-- Name: act_ru_entitylink act_ru_entitylink_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_entitylink
    ADD CONSTRAINT act_ru_entitylink_pkey PRIMARY KEY (id_);


--
-- TOC entry 5446 (class 2606 OID 369743)
-- Name: act_ru_event_subscr act_ru_event_subscr_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_event_subscr
    ADD CONSTRAINT act_ru_event_subscr_pkey PRIMARY KEY (id_);


--
-- TOC entry 5455 (class 2606 OID 369745)
-- Name: act_ru_execution act_ru_execution_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_execution
    ADD CONSTRAINT act_ru_execution_pkey PRIMARY KEY (id_);


--
-- TOC entry 5463 (class 2606 OID 369747)
-- Name: act_ru_external_job act_ru_external_job_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_external_job
    ADD CONSTRAINT act_ru_external_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 5465 (class 2606 OID 369749)
-- Name: act_ru_history_job act_ru_history_job_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_history_job
    ADD CONSTRAINT act_ru_history_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 5475 (class 2606 OID 369751)
-- Name: act_ru_identitylink act_ru_identitylink_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_identitylink
    ADD CONSTRAINT act_ru_identitylink_pkey PRIMARY KEY (id_);


--
-- TOC entry 5486 (class 2606 OID 369753)
-- Name: act_ru_job act_ru_job_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_job
    ADD CONSTRAINT act_ru_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 5497 (class 2606 OID 369755)
-- Name: act_ru_suspended_job act_ru_suspended_job_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_suspended_job
    ADD CONSTRAINT act_ru_suspended_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 5506 (class 2606 OID 369757)
-- Name: act_ru_task act_ru_task_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_task
    ADD CONSTRAINT act_ru_task_pkey PRIMARY KEY (id_);


--
-- TOC entry 5518 (class 2606 OID 369759)
-- Name: act_ru_timer_job act_ru_timer_job_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_timer_job
    ADD CONSTRAINT act_ru_timer_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 5526 (class 2606 OID 369761)
-- Name: act_ru_variable act_ru_variable_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_variable
    ADD CONSTRAINT act_ru_variable_pkey PRIMARY KEY (id_);


--
-- TOC entry 5405 (class 2606 OID 369763)
-- Name: act_procdef_info act_uniq_info_procdef; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_procdef_info
    ADD CONSTRAINT act_uniq_info_procdef UNIQUE (proc_def_id_);


--
-- TOC entry 5388 (class 2606 OID 369765)
-- Name: act_id_priv act_uniq_priv_name; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_priv
    ADD CONSTRAINT act_uniq_priv_name UNIQUE (name_);


--
-- TOC entry 5416 (class 2606 OID 369767)
-- Name: act_re_procdef act_uniq_procdef; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_re_procdef
    ADD CONSTRAINT act_uniq_procdef UNIQUE (key_, version_, derived_version_, tenant_id_);


--
-- TOC entry 5040 (class 2606 OID 360286)
-- Name: crm_account crm_account_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_account
    ADD CONSTRAINT crm_account_pkey PRIMARY KEY (account_id);


--
-- TOC entry 5581 (class 2606 OID 371328)
-- Name: crm_account_value crm_account_value_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_account_value
    ADD CONSTRAINT crm_account_value_pkey PRIMARY KEY (account_id);


--
-- TOC entry 5042 (class 2606 OID 360297)
-- Name: crm_accountrelation crm_accountrelation_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_accountrelation
    ADD CONSTRAINT crm_accountrelation_pkey PRIMARY KEY (relate_id);


--
-- TOC entry 5044 (class 2606 OID 360305)
-- Name: crm_activity crm_activity_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_activity
    ADD CONSTRAINT crm_activity_pkey PRIMARY KEY (activity_id);


--
-- TOC entry 5053 (class 2606 OID 360313)
-- Name: crm_actrelate crm_actrelate_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_actrelate
    ADD CONSTRAINT crm_actrelate_pkey PRIMARY KEY (relate_id);


--
-- TOC entry 5055 (class 2606 OID 360324)
-- Name: crm_agent_tool crm_agent_tool_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_agent_tool
    ADD CONSTRAINT crm_agent_tool_pkey PRIMARY KEY (id);


--
-- TOC entry 5637 (class 2606 OID 371826)
-- Name: crm_ai_chat_message crm_ai_chat_message_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_chat_message
    ADD CONSTRAINT crm_ai_chat_message_pkey PRIMARY KEY (message_id);


--
-- TOC entry 5634 (class 2606 OID 371817)
-- Name: crm_ai_chat_session crm_ai_chat_session_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_chat_session
    ADD CONSTRAINT crm_ai_chat_session_pkey PRIMARY KEY (session_id);


--
-- TOC entry 5620 (class 2606 OID 371749)
-- Name: crm_ai_model_config crm_ai_model_config_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_model_config
    ADD CONSTRAINT crm_ai_model_config_pkey PRIMARY KEY (config_id);


--
-- TOC entry 5666 (class 2606 OID 379553)
-- Name: crm_ai_playbook_run crm_ai_playbook_run_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_playbook_run
    ADD CONSTRAINT crm_ai_playbook_run_pkey PRIMARY KEY (id);


--
-- TOC entry 5643 (class 2606 OID 379484)
-- Name: crm_ai_skill crm_ai_skill_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_skill
    ADD CONSTRAINT crm_ai_skill_pkey PRIMARY KEY (id);


--
-- TOC entry 5655 (class 2606 OID 379520)
-- Name: crm_ai_skill_playbook crm_ai_skill_playbook_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_skill_playbook
    ADD CONSTRAINT crm_ai_skill_playbook_pkey PRIMARY KEY (id);


--
-- TOC entry 5660 (class 2606 OID 379537)
-- Name: crm_ai_skill_playbook_step crm_ai_skill_playbook_step_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_skill_playbook_step
    ADD CONSTRAINT crm_ai_skill_playbook_step_pkey PRIMARY KEY (id);


--
-- TOC entry 5640 (class 2606 OID 371840)
-- Name: crm_analytics_query_log crm_analytics_query_log_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_analytics_query_log
    ADD CONSTRAINT crm_analytics_query_log_pkey PRIMARY KEY (log_id);


--
-- TOC entry 5057 (class 2606 OID 360338)
-- Name: crm_appointment crm_appointment_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_appointment
    ADD CONSTRAINT crm_appointment_pkey PRIMARY KEY (appointment_id);


--
-- TOC entry 5059 (class 2606 OID 360345)
-- Name: crm_attendee crm_attendee_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_attendee
    ADD CONSTRAINT crm_attendee_pkey PRIMARY KEY (attendee_id);


--
-- TOC entry 5061 (class 2606 OID 360356)
-- Name: crm_bulkemail crm_bulkemail_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_bulkemail
    ADD CONSTRAINT crm_bulkemail_pkey PRIMARY KEY (bulk_id);


--
-- TOC entry 5063 (class 2606 OID 360365)
-- Name: crm_campaign_contacts crm_campaign_contacts_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_campaign_contacts
    ADD CONSTRAINT crm_campaign_contacts_pkey PRIMARY KEY (id);


--
-- TOC entry 5065 (class 2606 OID 360373)
-- Name: crm_cashflow crm_cashflow_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_cashflow
    ADD CONSTRAINT crm_cashflow_pkey PRIMARY KEY (cashflow_id);


--
-- TOC entry 5067 (class 2606 OID 360381)
-- Name: crm_collect crm_collect_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_collect
    ADD CONSTRAINT crm_collect_pkey PRIMARY KEY (collect_id);


--
-- TOC entry 5069 (class 2606 OID 360390)
-- Name: crm_competitor crm_competitor_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_competitor
    ADD CONSTRAINT crm_competitor_pkey PRIMARY KEY (competitor_id);


--
-- TOC entry 5071 (class 2606 OID 360411)
-- Name: crm_contact crm_contact_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_contact
    ADD CONSTRAINT crm_contact_pkey PRIMARY KEY (contact_id);


--
-- TOC entry 5074 (class 2606 OID 360419)
-- Name: crm_contactmethod crm_contactmethod_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_contactmethod
    ADD CONSTRAINT crm_contactmethod_pkey PRIMARY KEY (method_id);


--
-- TOC entry 5076 (class 2606 OID 360427)
-- Name: crm_contactrelation crm_contactrelation_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_contactrelation
    ADD CONSTRAINT crm_contactrelation_pkey PRIMARY KEY (relate_id);


--
-- TOC entry 5078 (class 2606 OID 360452)
-- Name: crm_contract crm_contract_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_contract
    ADD CONSTRAINT crm_contract_pkey PRIMARY KEY (contract_id);


--
-- TOC entry 5080 (class 2606 OID 360459)
-- Name: crm_conversion_history crm_conversion_history_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_conversion_history
    ADD CONSTRAINT crm_conversion_history_pkey PRIMARY KEY (id);


--
-- TOC entry 5603 (class 2606 OID 371625)
-- Name: crm_data_table_column crm_data_table_column_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_data_table_column
    ADD CONSTRAINT crm_data_table_column_pkey PRIMARY KEY (id);


--
-- TOC entry 5598 (class 2606 OID 371613)
-- Name: crm_data_table crm_data_table_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_data_table
    ADD CONSTRAINT crm_data_table_pkey PRIMARY KEY (id);


--
-- TOC entry 5607 (class 2606 OID 371642)
-- Name: crm_data_table_row crm_data_table_row_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_data_table_row
    ADD CONSTRAINT crm_data_table_row_pkey PRIMARY KEY (id);


--
-- TOC entry 5082 (class 2606 OID 360470)
-- Name: crm_dd crm_dd_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_dd
    ADD CONSTRAINT crm_dd_pkey PRIMARY KEY (dd_id);


--
-- TOC entry 5084 (class 2606 OID 360477)
-- Name: crm_docrelation crm_docrelation_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_docrelation
    ADD CONSTRAINT crm_docrelation_pkey PRIMARY KEY (relate_id);


--
-- TOC entry 5625 (class 2606 OID 371790)
-- Name: crm_email_analysis crm_email_analysis_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_email_analysis
    ADD CONSTRAINT crm_email_analysis_pkey PRIMARY KEY (analysis_id);


--
-- TOC entry 5086 (class 2606 OID 360489)
-- Name: crm_email_attachments crm_email_attachments_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_email_attachments
    ADD CONSTRAINT crm_email_attachments_pkey PRIMARY KEY (id);


--
-- TOC entry 5088 (class 2606 OID 360501)
-- Name: crm_email_messages crm_email_messages_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_email_messages
    ADD CONSTRAINT crm_email_messages_pkey PRIMARY KEY (id);


--
-- TOC entry 5090 (class 2606 OID 360514)
-- Name: crm_event crm_event_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_event
    ADD CONSTRAINT crm_event_pkey PRIMARY KEY (event_id);


--
-- TOC entry 5631 (class 2606 OID 371808)
-- Name: crm_follow_up_cadence crm_follow_up_cadence_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_follow_up_cadence
    ADD CONSTRAINT crm_follow_up_cadence_pkey PRIMARY KEY (cadence_id);


--
-- TOC entry 5092 (class 2606 OID 360530)
-- Name: crm_forecast crm_forecast_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_forecast
    ADD CONSTRAINT crm_forecast_pkey PRIMARY KEY (forecast_id);


--
-- TOC entry 5094 (class 2606 OID 360543)
-- Name: crm_fundinvestor crm_fundinvestor_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_fundinvestor
    ADD CONSTRAINT crm_fundinvestor_pkey PRIMARY KEY (investor_id);


--
-- TOC entry 5096 (class 2606 OID 360554)
-- Name: crm_fundpaid crm_fundpaid_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_fundpaid
    ADD CONSTRAINT crm_fundpaid_pkey PRIMARY KEY (paid_id);


--
-- TOC entry 5098 (class 2606 OID 360562)
-- Name: crm_household crm_household_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_household
    ADD CONSTRAINT crm_household_pkey PRIMARY KEY (household_id);


--
-- TOC entry 5100 (class 2606 OID 360570)
-- Name: crm_householdmember crm_householdmember_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_householdmember
    ADD CONSTRAINT crm_householdmember_pkey PRIMARY KEY (member_id);


--
-- TOC entry 5102 (class 2606 OID 360585)
-- Name: crm_interest crm_interest_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_interest
    ADD CONSTRAINT crm_interest_pkey PRIMARY KEY (interest_id);


--
-- TOC entry 5104 (class 2606 OID 360593)
-- Name: crm_itinerary crm_itinerary_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_itinerary
    ADD CONSTRAINT crm_itinerary_pkey PRIMARY KEY (itin_id);


--
-- TOC entry 5106 (class 2606 OID 360609)
-- Name: crm_lead crm_lead_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_lead
    ADD CONSTRAINT crm_lead_pkey PRIMARY KEY (lead_id);


--
-- TOC entry 5671 (class 2606 OID 379612)
-- Name: crm_mcp_api_key crm_mcp_api_key_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_mcp_api_key
    ADD CONSTRAINT crm_mcp_api_key_pkey PRIMARY KEY (id);


--
-- TOC entry 5678 (class 2606 OID 379628)
-- Name: crm_mcp_audit_log crm_mcp_audit_log_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_mcp_audit_log
    ADD CONSTRAINT crm_mcp_audit_log_pkey PRIMARY KEY (id);


--
-- TOC entry 5650 (class 2606 OID 379503)
-- Name: crm_mcp_server crm_mcp_server_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_mcp_server
    ADD CONSTRAINT crm_mcp_server_pkey PRIMARY KEY (id);


--
-- TOC entry 5108 (class 2606 OID 360621)
-- Name: crm_oauth_connections crm_oauth_connections_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_oauth_connections
    ADD CONSTRAINT crm_oauth_connections_pkey PRIMARY KEY (id);


--
-- TOC entry 5110 (class 2606 OID 360628)
-- Name: crm_onepage crm_onepage_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_onepage
    ADD CONSTRAINT crm_onepage_pkey PRIMARY KEY (page_id);


--
-- TOC entry 5112 (class 2606 OID 360637)
-- Name: crm_operator crm_operator_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_operator
    ADD CONSTRAINT crm_operator_pkey PRIMARY KEY (operator_id);


--
-- TOC entry 5114 (class 2606 OID 360652)
-- Name: crm_opty crm_opty_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_opty
    ADD CONSTRAINT crm_opty_pkey PRIMARY KEY (opty_id);


--
-- TOC entry 5116 (class 2606 OID 360661)
-- Name: crm_optyacceptance crm_optyacceptance_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_optyacceptance
    ADD CONSTRAINT crm_optyacceptance_pkey PRIMARY KEY (accept_id);


--
-- TOC entry 5118 (class 2606 OID 360671)
-- Name: crm_optyrelation crm_optyrelation_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_optyrelation
    ADD CONSTRAINT crm_optyrelation_pkey PRIMARY KEY (relate_id);


--
-- TOC entry 5120 (class 2606 OID 360680)
-- Name: crm_optytask crm_optytask_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_optytask
    ADD CONSTRAINT crm_optytask_pkey PRIMARY KEY (task_id);


--
-- TOC entry 5122 (class 2606 OID 360704)
-- Name: crm_org crm_org_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_org
    ADD CONSTRAINT crm_org_pkey PRIMARY KEY (org_id);


--
-- TOC entry 5124 (class 2606 OID 360711)
-- Name: crm_ownerhistory crm_ownerhistory_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_ownerhistory
    ADD CONSTRAINT crm_ownerhistory_pkey PRIMARY KEY (oh_id);


--
-- TOC entry 5126 (class 2606 OID 360725)
-- Name: crm_partner crm_partner_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_partner
    ADD CONSTRAINT crm_partner_pkey PRIMARY KEY (partner_id);


--
-- TOC entry 5576 (class 2606 OID 371305)
-- Name: crm_patient_value crm_patient_value_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_patient_value
    ADD CONSTRAINT crm_patient_value_pkey PRIMARY KEY (contact_id);


--
-- TOC entry 5128 (class 2606 OID 360736)
-- Name: crm_paymentterm crm_paymentterm_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_paymentterm
    ADD CONSTRAINT crm_paymentterm_pkey PRIMARY KEY (term_id);


--
-- TOC entry 5130 (class 2606 OID 360744)
-- Name: crm_privacy crm_privacy_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_privacy
    ADD CONSTRAINT crm_privacy_pkey PRIMARY KEY (privacy_id);


--
-- TOC entry 5132 (class 2606 OID 360752)
-- Name: crm_product crm_product_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_product
    ADD CONSTRAINT crm_product_pkey PRIMARY KEY (product_id);


--
-- TOC entry 5134 (class 2606 OID 360762)
-- Name: crm_pulse crm_pulse_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_pulse
    ADD CONSTRAINT crm_pulse_pkey PRIMARY KEY (pulse_id);


--
-- TOC entry 5136 (class 2606 OID 360776)
-- Name: crm_quote crm_quote_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_quote
    ADD CONSTRAINT crm_quote_pkey PRIMARY KEY (quote_id);


--
-- TOC entry 5138 (class 2606 OID 360787)
-- Name: crm_quoteoption crm_quoteoption_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_quoteoption
    ADD CONSTRAINT crm_quoteoption_pkey PRIMARY KEY (option_id);


--
-- TOC entry 5140 (class 2606 OID 360805)
-- Name: crm_reportdetail crm_reportdetail_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_reportdetail
    ADD CONSTRAINT crm_reportdetail_pkey PRIMARY KEY (detail_id);


--
-- TOC entry 5142 (class 2606 OID 360813)
-- Name: crm_research crm_research_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_research
    ADD CONSTRAINT crm_research_pkey PRIMARY KEY (research_id);


--
-- TOC entry 5144 (class 2606 OID 360820)
-- Name: crm_respondents crm_respondents_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_respondents
    ADD CONSTRAINT crm_respondents_pkey PRIMARY KEY (id);


--
-- TOC entry 5146 (class 2606 OID 360827)
-- Name: crm_responses crm_responses_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_responses
    ADD CONSTRAINT crm_responses_pkey PRIMARY KEY (id);


--
-- TOC entry 5148 (class 2606 OID 360837)
-- Name: crm_salesgoal crm_salesgoal_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_salesgoal
    ADD CONSTRAINT crm_salesgoal_pkey PRIMARY KEY (goal_id);


--
-- TOC entry 5615 (class 2606 OID 371705)
-- Name: crm_semantic_profile crm_semantic_profile_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_semantic_profile
    ADD CONSTRAINT crm_semantic_profile_pkey PRIMARY KEY (profile_id);


--
-- TOC entry 5150 (class 2606 OID 360845)
-- Name: crm_sentiment crm_sentiment_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_sentiment
    ADD CONSTRAINT crm_sentiment_pkey PRIMARY KEY (id);


--
-- TOC entry 5589 (class 2606 OID 371461)
-- Name: crm_sf_express_config crm_sf_express_config_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_sf_express_config
    ADD CONSTRAINT crm_sf_express_config_pkey PRIMARY KEY (id);


--
-- TOC entry 5593 (class 2606 OID 371471)
-- Name: crm_sf_express_token crm_sf_express_token_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_sf_express_token
    ADD CONSTRAINT crm_sf_express_token_pkey PRIMARY KEY (id);


--
-- TOC entry 5152 (class 2606 OID 360853)
-- Name: crm_stagehistory crm_stagehistory_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_stagehistory
    ADD CONSTRAINT crm_stagehistory_pkey PRIMARY KEY (sh_id);


--
-- TOC entry 5699 (class 2606 OID 380107)
-- Name: crm_subject_map crm_subject_map_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_subject_map
    ADD CONSTRAINT crm_subject_map_pkey PRIMARY KEY (id);


--
-- TOC entry 5154 (class 2606 OID 360860)
-- Name: crm_surveyresult crm_surveyresult_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_surveyresult
    ADD CONSTRAINT crm_surveyresult_pkey PRIMARY KEY (result_id);


--
-- TOC entry 5156 (class 2606 OID 360873)
-- Name: crm_surveys crm_surveys_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_surveys
    ADD CONSTRAINT crm_surveys_pkey PRIMARY KEY (id);


--
-- TOC entry 5158 (class 2606 OID 360883)
-- Name: crm_task crm_task_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_task
    ADD CONSTRAINT crm_task_pkey PRIMARY KEY (task_id);


--
-- TOC entry 5160 (class 2606 OID 360889)
-- Name: crm_territory crm_territory_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_territory
    ADD CONSTRAINT crm_territory_pkey PRIMARY KEY (territory_id);


--
-- TOC entry 5162 (class 2606 OID 360896)
-- Name: crm_ticket crm_ticket_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_ticket
    ADD CONSTRAINT crm_ticket_pkey PRIMARY KEY (id);


--
-- TOC entry 5164 (class 2606 OID 360904)
-- Name: crm_timeperiod crm_timeperiod_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_timeperiod
    ADD CONSTRAINT crm_timeperiod_pkey PRIMARY KEY (period_id);


--
-- TOC entry 5166 (class 2606 OID 360915)
-- Name: crm_travel crm_travel_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_travel
    ADD CONSTRAINT crm_travel_pkey PRIMARY KEY (travel_id);


--
-- TOC entry 5704 (class 2606 OID 380131)
-- Name: crm_wecom_aibot_config crm_wecom_aibot_config_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_aibot_config
    ADD CONSTRAINT crm_wecom_aibot_config_pkey PRIMARY KEY (id);


--
-- TOC entry 5708 (class 2606 OID 380145)
-- Name: crm_wecom_aibot_connection crm_wecom_aibot_connection_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_aibot_connection
    ADD CONSTRAINT crm_wecom_aibot_connection_pkey PRIMARY KEY (id);


--
-- TOC entry 5695 (class 2606 OID 380082)
-- Name: crm_wecom_callback_event crm_wecom_callback_event_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_callback_event
    ADD CONSTRAINT crm_wecom_callback_event_pkey PRIMARY KEY (id);


--
-- TOC entry 5168 (class 2606 OID 360923)
-- Name: crm_wecom_config crm_wecom_config_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_wecom_config
    ADD CONSTRAINT crm_wecom_config_pkey PRIMARY KEY (id);


--
-- TOC entry 5170 (class 2606 OID 360931)
-- Name: crm_wecom_contact_way crm_wecom_contact_way_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_wecom_contact_way
    ADD CONSTRAINT crm_wecom_contact_way_pkey PRIMARY KEY (id);


--
-- TOC entry 5174 (class 2606 OID 360940)
-- Name: crm_wecom_external_contact crm_wecom_external_contact_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_wecom_external_contact
    ADD CONSTRAINT crm_wecom_external_contact_pkey PRIMARY KEY (id);


--
-- TOC entry 5681 (class 2606 OID 379692)
-- Name: crm_wecom_kf_config crm_wecom_kf_config_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_kf_config
    ADD CONSTRAINT crm_wecom_kf_config_pkey PRIMARY KEY (id);


--
-- TOC entry 5685 (class 2606 OID 379748)
-- Name: crm_wecom_kf_message crm_wecom_kf_message_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_kf_message
    ADD CONSTRAINT crm_wecom_kf_message_pkey PRIMARY KEY (id);


--
-- TOC entry 5179 (class 2606 OID 360947)
-- Name: crm_wecom_mass_task crm_wecom_mass_task_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_wecom_mass_task
    ADD CONSTRAINT crm_wecom_mass_task_pkey PRIMARY KEY (id);


--
-- TOC entry 5691 (class 2606 OID 380029)
-- Name: crm_wecom_material crm_wecom_material_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_material
    ADD CONSTRAINT crm_wecom_material_pkey PRIMARY KEY (id);


--
-- TOC entry 5585 (class 2606 OID 371402)
-- Name: crm_wecom_welcome_log crm_wecom_welcome_log_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_welcome_log
    ADD CONSTRAINT crm_wecom_welcome_log_pkey PRIMARY KEY (id);


--
-- TOC entry 5182 (class 2606 OID 360956)
-- Name: crm_weeklyreport crm_weeklyreport_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_weeklyreport
    ADD CONSTRAINT crm_weeklyreport_pkey PRIMARY KEY (report_id);


--
-- TOC entry 5571 (class 2606 OID 371269)
-- Name: crm_workflow_automation crm_workflow_automation_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_workflow_automation
    ADD CONSTRAINT crm_workflow_automation_pkey PRIMARY KEY (config_id);


--
-- TOC entry 5184 (class 2606 OID 360965)
-- Name: crm_workflow_trigger crm_workflow_trigger_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_workflow_trigger
    ADD CONSTRAINT crm_workflow_trigger_pkey PRIMARY KEY (id);


--
-- TOC entry 5186 (class 2606 OID 360972)
-- Name: den_inventory den_inventory_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.den_inventory
    ADD CONSTRAINT den_inventory_pkey PRIMARY KEY (inv_id);


--
-- TOC entry 5190 (class 2606 OID 360988)
-- Name: den_staff_bank den_staff_bank_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.den_staff_bank
    ADD CONSTRAINT den_staff_bank_pkey PRIMARY KEY (bank_id);


--
-- TOC entry 5188 (class 2606 OID 360980)
-- Name: den_staff den_staff_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.den_staff
    ADD CONSTRAINT den_staff_pkey PRIMARY KEY (den_id);


--
-- TOC entry 5192 (class 2606 OID 360995)
-- Name: den_vendor den_vendor_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.den_vendor
    ADD CONSTRAINT den_vendor_pkey PRIMARY KEY (vendor_id);


--
-- TOC entry 5194 (class 2606 OID 361003)
-- Name: ems_hcp ems_hcp_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.ems_hcp
    ADD CONSTRAINT ems_hcp_pkey PRIMARY KEY (hcp_id);


--
-- TOC entry 5529 (class 2606 OID 369769)
-- Name: flw_channel_definition flw_channel_definition_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.flw_channel_definition
    ADD CONSTRAINT flw_channel_definition_pkey PRIMARY KEY (id_);


--
-- TOC entry 5531 (class 2606 OID 369771)
-- Name: flw_ev_databasechangeloglock flw_ev_databasechangeloglock_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.flw_ev_databasechangeloglock
    ADD CONSTRAINT flw_ev_databasechangeloglock_pkey PRIMARY KEY (id);


--
-- TOC entry 5534 (class 2606 OID 369773)
-- Name: flw_event_definition flw_event_definition_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.flw_event_definition
    ADD CONSTRAINT flw_event_definition_pkey PRIMARY KEY (id_);


--
-- TOC entry 5536 (class 2606 OID 369775)
-- Name: flw_event_deployment flw_event_deployment_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.flw_event_deployment
    ADD CONSTRAINT flw_event_deployment_pkey PRIMARY KEY (id_);


--
-- TOC entry 5538 (class 2606 OID 369777)
-- Name: flw_event_resource flw_event_resource_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.flw_event_resource
    ADD CONSTRAINT flw_event_resource_pkey PRIMARY KEY (id_);


--
-- TOC entry 5543 (class 2606 OID 369779)
-- Name: flw_ru_batch_part flw_ru_batch_part_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.flw_ru_batch_part
    ADD CONSTRAINT flw_ru_batch_part_pkey PRIMARY KEY (id_);


--
-- TOC entry 5540 (class 2606 OID 369781)
-- Name: flw_ru_batch flw_ru_batch_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.flw_ru_batch
    ADD CONSTRAINT flw_ru_batch_pkey PRIMARY KEY (id_);


--
-- TOC entry 5198 (class 2606 OID 361068)
-- Name: gen_table_column gen_table_column_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.gen_table_column
    ADD CONSTRAINT gen_table_column_pkey PRIMARY KEY (column_id);


--
-- TOC entry 5196 (class 2606 OID 361059)
-- Name: gen_table gen_table_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.gen_table
    ADD CONSTRAINT gen_table_pkey PRIMARY KEY (table_id);


--
-- TOC entry 5200 (class 2606 OID 361077)
-- Name: master_registration master_registration_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.master_registration
    ADD CONSTRAINT master_registration_pkey PRIMARY KEY (regist_id);


--
-- TOC entry 5202 (class 2606 OID 361086)
-- Name: master_tenant master_tenant_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.master_tenant
    ADD CONSTRAINT master_tenant_pkey PRIMARY KEY (id);


--
-- TOC entry 5204 (class 2606 OID 361095)
-- Name: sys_config sys_config_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_config
    ADD CONSTRAINT sys_config_pkey PRIMARY KEY (config_id);


--
-- TOC entry 5206 (class 2606 OID 361101)
-- Name: sys_deploy_form sys_deploy_form_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_deploy_form
    ADD CONSTRAINT sys_deploy_form_pkey PRIMARY KEY (id);


--
-- TOC entry 5208 (class 2606 OID 361111)
-- Name: sys_dept sys_dept_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_dept
    ADD CONSTRAINT sys_dept_pkey PRIMARY KEY (dept_id);


--
-- TOC entry 5210 (class 2606 OID 361122)
-- Name: sys_dict_data sys_dict_data_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_dict_data
    ADD CONSTRAINT sys_dict_data_pkey PRIMARY KEY (dict_code);


--
-- TOC entry 5212 (class 2606 OID 361131)
-- Name: sys_dict_type sys_dict_type_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_dict_type
    ADD CONSTRAINT sys_dict_type_pkey PRIMARY KEY (dict_id);


--
-- TOC entry 5214 (class 2606 OID 361147)
-- Name: sys_ems_corres sys_ems_corres_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_ems_corres
    ADD CONSTRAINT sys_ems_corres_pkey PRIMARY KEY (corres_id);


--
-- TOC entry 5216 (class 2606 OID 361155)
-- Name: sys_form sys_form_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_form
    ADD CONSTRAINT sys_form_pkey PRIMARY KEY (form_id);


--
-- TOC entry 5218 (class 2606 OID 361162)
-- Name: sys_hi_audittrail sys_hi_audittrail_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_hi_audittrail
    ADD CONSTRAINT sys_hi_audittrail_pkey PRIMARY KEY (id);


--
-- TOC entry 5222 (class 2606 OID 361182)
-- Name: sys_job_log sys_job_log_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_job_log
    ADD CONSTRAINT sys_job_log_pkey PRIMARY KEY (job_log_id);


--
-- TOC entry 5220 (class 2606 OID 361173)
-- Name: sys_job sys_job_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_job
    ADD CONSTRAINT sys_job_pkey PRIMARY KEY (job_id, job_name, job_group);


--
-- TOC entry 5224 (class 2606 OID 361191)
-- Name: sys_logininfor sys_logininfor_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_logininfor
    ADD CONSTRAINT sys_logininfor_pkey PRIMARY KEY (info_id);


--
-- TOC entry 5226 (class 2606 OID 361206)
-- Name: sys_menu sys_menu_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_menu
    ADD CONSTRAINT sys_menu_pkey PRIMARY KEY (menu_id);


--
-- TOC entry 5228 (class 2606 OID 361215)
-- Name: sys_notice sys_notice_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_notice
    ADD CONSTRAINT sys_notice_pkey PRIMARY KEY (notice_id);


--
-- TOC entry 5230 (class 2606 OID 361226)
-- Name: sys_oper_log sys_oper_log_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_oper_log
    ADD CONSTRAINT sys_oper_log_pkey PRIMARY KEY (oper_id);


--
-- TOC entry 5232 (class 2606 OID 361233)
-- Name: sys_organization sys_organization_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_organization
    ADD CONSTRAINT sys_organization_pkey PRIMARY KEY (organization_id);


--
-- TOC entry 5234 (class 2606 OID 361242)
-- Name: sys_pm_log sys_pm_log_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_pm_log
    ADD CONSTRAINT sys_pm_log_pkey PRIMARY KEY (log_id);


--
-- TOC entry 5236 (class 2606 OID 361250)
-- Name: sys_post sys_post_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_post
    ADD CONSTRAINT sys_post_pkey PRIMARY KEY (post_id);


--
-- TOC entry 5238 (class 2606 OID 361260)
-- Name: sys_re_acceptance sys_re_acceptance_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_re_acceptance
    ADD CONSTRAINT sys_re_acceptance_pkey PRIMARY KEY (accept_id);


--
-- TOC entry 5240 (class 2606 OID 361270)
-- Name: sys_re_actitem sys_re_actitem_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_re_actitem
    ADD CONSTRAINT sys_re_actitem_pkey PRIMARY KEY (py_id);


--
-- TOC entry 5242 (class 2606 OID 361278)
-- Name: sys_re_activity sys_re_activity_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_re_activity
    ADD CONSTRAINT sys_re_activity_pkey PRIMARY KEY (py_id);


--
-- TOC entry 5244 (class 2606 OID 361286)
-- Name: sys_re_application sys_re_application_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_re_application
    ADD CONSTRAINT sys_re_application_pkey PRIMARY KEY (py_id);


--
-- TOC entry 5246 (class 2606 OID 361296)
-- Name: sys_re_board sys_re_board_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_re_board
    ADD CONSTRAINT sys_re_board_pkey PRIMARY KEY (py_id);


--
-- TOC entry 5248 (class 2606 OID 361310)
-- Name: sys_re_casetype sys_re_casetype_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_re_casetype
    ADD CONSTRAINT sys_re_casetype_pkey PRIMARY KEY (id);


--
-- TOC entry 5250 (class 2606 OID 361318)
-- Name: sys_re_checklist sys_re_checklist_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_re_checklist
    ADD CONSTRAINT sys_re_checklist_pkey PRIMARY KEY (py_id);


--
-- TOC entry 5252 (class 2606 OID 361329)
-- Name: sys_re_corres sys_re_corres_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_re_corres
    ADD CONSTRAINT sys_re_corres_pkey PRIMARY KEY (corres_id);


--
-- TOC entry 5254 (class 2606 OID 361339)
-- Name: sys_re_datafield sys_re_datafield_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_re_datafield
    ADD CONSTRAINT sys_re_datafield_pkey PRIMARY KEY (field_id);


--
-- TOC entry 5256 (class 2606 OID 361347)
-- Name: sys_re_dataobj sys_re_dataobj_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_re_dataobj
    ADD CONSTRAINT sys_re_dataobj_pkey PRIMARY KEY (dataobj_id);


--
-- TOC entry 5258 (class 2606 OID 361356)
-- Name: sys_re_datatransform sys_re_datatransform_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_re_datatransform
    ADD CONSTRAINT sys_re_datatransform_pkey PRIMARY KEY (py_id);


--
-- TOC entry 5260 (class 2606 OID 361365)
-- Name: sys_re_datatype sys_re_datatype_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_re_datatype
    ADD CONSTRAINT sys_re_datatype_pkey PRIMARY KEY (data_id);


--
-- TOC entry 5262 (class 2606 OID 361374)
-- Name: sys_re_decisiontable sys_re_decisiontable_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_re_decisiontable
    ADD CONSTRAINT sys_re_decisiontable_pkey PRIMARY KEY (py_id);


--
-- TOC entry 5264 (class 2606 OID 361383)
-- Name: sys_re_decisiontree sys_re_decisiontree_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_re_decisiontree
    ADD CONSTRAINT sys_re_decisiontree_pkey PRIMARY KEY (py_id);


--
-- TOC entry 5266 (class 2606 OID 361393)
-- Name: sys_re_dtitem sys_re_dtitem_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_re_dtitem
    ADD CONSTRAINT sys_re_dtitem_pkey PRIMARY KEY (py_id);


--
-- TOC entry 5268 (class 2606 OID 361403)
-- Name: sys_re_participant sys_re_participant_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_re_participant
    ADD CONSTRAINT sys_re_participant_pkey PRIMARY KEY (part_id);


--
-- TOC entry 5270 (class 2606 OID 361410)
-- Name: sys_re_pmchange sys_re_pmchange_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_re_pmchange
    ADD CONSTRAINT sys_re_pmchange_pkey PRIMARY KEY (change_id);


--
-- TOC entry 5272 (class 2606 OID 361418)
-- Name: sys_re_pmrisk sys_re_pmrisk_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_re_pmrisk
    ADD CONSTRAINT sys_re_pmrisk_pkey PRIMARY KEY (risk_id);


--
-- TOC entry 5274 (class 2606 OID 361429)
-- Name: sys_re_process sys_re_process_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_re_process
    ADD CONSTRAINT sys_re_process_pkey PRIMARY KEY (process_id);


--
-- TOC entry 5276 (class 2606 OID 361450)
-- Name: sys_re_project sys_re_project_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_re_project
    ADD CONSTRAINT sys_re_project_pkey PRIMARY KEY (py_id);


--
-- TOC entry 5278 (class 2606 OID 361457)
-- Name: sys_re_reportdefinition sys_re_reportdefinition_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_re_reportdefinition
    ADD CONSTRAINT sys_re_reportdefinition_pkey PRIMARY KEY (id);


--
-- TOC entry 5280 (class 2606 OID 361469)
-- Name: sys_re_ruleset sys_re_ruleset_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_re_ruleset
    ADD CONSTRAINT sys_re_ruleset_pkey PRIMARY KEY (py_id);


--
-- TOC entry 5282 (class 2606 OID 361481)
-- Name: sys_re_stage sys_re_stage_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_re_stage
    ADD CONSTRAINT sys_re_stage_pkey PRIMARY KEY (stage_id);


--
-- TOC entry 5284 (class 2606 OID 361490)
-- Name: sys_re_strategy sys_re_strategy_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_re_strategy
    ADD CONSTRAINT sys_re_strategy_pkey PRIMARY KEY (py_id);


--
-- TOC entry 5286 (class 2606 OID 361498)
-- Name: sys_re_talkingplan sys_re_talkingplan_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_re_talkingplan
    ADD CONSTRAINT sys_re_talkingplan_pkey PRIMARY KEY (plan_id);


--
-- TOC entry 5288 (class 2606 OID 361506)
-- Name: sys_re_template sys_re_template_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_re_template
    ADD CONSTRAINT sys_re_template_pkey PRIMARY KEY (template_id);


--
-- TOC entry 5290 (class 2606 OID 361515)
-- Name: sys_re_when sys_re_when_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_re_when
    ADD CONSTRAINT sys_re_when_pkey PRIMARY KEY (py_id);


--
-- TOC entry 5292 (class 2606 OID 361523)
-- Name: sys_re_workload sys_re_workload_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_re_workload
    ADD CONSTRAINT sys_re_workload_pkey PRIMARY KEY (workload_id);


--
-- TOC entry 5294 (class 2606 OID 361533)
-- Name: sys_re_worktask sys_re_worktask_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_re_worktask
    ADD CONSTRAINT sys_re_worktask_pkey PRIMARY KEY (task_id);


--
-- TOC entry 5298 (class 2606 OID 361550)
-- Name: sys_role_dept sys_role_dept_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_role_dept
    ADD CONSTRAINT sys_role_dept_pkey PRIMARY KEY (role_id, dept_id);


--
-- TOC entry 5300 (class 2606 OID 361555)
-- Name: sys_role_menu sys_role_menu_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_role_menu
    ADD CONSTRAINT sys_role_menu_pkey PRIMARY KEY (role_id, menu_id);


--
-- TOC entry 5296 (class 2606 OID 361545)
-- Name: sys_role sys_role_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_role
    ADD CONSTRAINT sys_role_pkey PRIMARY KEY (role_id);


--
-- TOC entry 5302 (class 2606 OID 361564)
-- Name: sys_ru_caseinstance sys_ru_caseinstance_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_ru_caseinstance
    ADD CONSTRAINT sys_ru_caseinstance_pkey PRIMARY KEY (id);


--
-- TOC entry 5304 (class 2606 OID 361570)
-- Name: sys_sequence_id sys_sequence_id_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_sequence_id
    ADD CONSTRAINT sys_sequence_id_pkey PRIMARY KEY (sequenceid);


--
-- TOC entry 5306 (class 2606 OID 361576)
-- Name: sys_task_form sys_task_form_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_task_form
    ADD CONSTRAINT sys_task_form_pkey PRIMARY KEY (id);


--
-- TOC entry 5308 (class 2606 OID 361591)
-- Name: sys_user sys_user_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_user
    ADD CONSTRAINT sys_user_pkey PRIMARY KEY (user_id);


--
-- TOC entry 5310 (class 2606 OID 361596)
-- Name: sys_user_post sys_user_post_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_user_post
    ADD CONSTRAINT sys_user_post_pkey PRIMARY KEY (user_id, post_id);


--
-- TOC entry 5312 (class 2606 OID 361601)
-- Name: sys_user_role sys_user_role_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.sys_user_role
    ADD CONSTRAINT sys_user_role_pkey PRIMARY KEY (user_id, role_id);


--
-- TOC entry 5613 (class 2606 OID 371692)
-- Name: sys_webhook_path_index sys_webhook_path_index_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.sys_webhook_path_index
    ADD CONSTRAINT sys_webhook_path_index_pkey PRIMARY KEY (path);


--
-- TOC entry 5314 (class 2606 OID 361607)
-- Name: table_table_industry table_table_industry_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.table_table_industry
    ADD CONSTRAINT table_table_industry_pkey PRIMARY KEY (id);


--
-- TOC entry 5710 (class 2606 OID 380147)
-- Name: crm_wecom_aibot_connection uk_aibot_conn_tenant; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_aibot_connection
    ADD CONSTRAINT uk_aibot_conn_tenant UNIQUE (tenant);


--
-- TOC entry 5706 (class 2606 OID 380133)
-- Name: crm_wecom_aibot_config uk_aibot_tenant; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_aibot_config
    ADD CONSTRAINT uk_aibot_tenant UNIQUE (tenant);


--
-- TOC entry 5702 (class 2606 OID 380109)
-- Name: crm_subject_map uk_child; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_subject_map
    ADD CONSTRAINT uk_child UNIQUE (child_subject_type, child_subject_id);


--
-- TOC entry 5683 (class 2606 OID 379694)
-- Name: crm_wecom_kf_config uk_corp; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_kf_config
    ADD CONSTRAINT uk_corp UNIQUE (corp_id);


--
-- TOC entry 5623 (class 2606 OID 371751)
-- Name: crm_ai_model_config uk_crm_ai_model_config_tenant; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_model_config
    ADD CONSTRAINT uk_crm_ai_model_config_tenant UNIQUE (tenant_id);


--
-- TOC entry 5664 (class 2606 OID 379539)
-- Name: crm_ai_skill_playbook_step uk_crm_ai_playbook_step_key; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_skill_playbook_step
    ADD CONSTRAINT uk_crm_ai_playbook_step_key UNIQUE (playbook_id, step_key);


--
-- TOC entry 5658 (class 2606 OID 379522)
-- Name: crm_ai_skill_playbook uk_crm_ai_playbook_tenant_name_ver; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_skill_playbook
    ADD CONSTRAINT uk_crm_ai_playbook_tenant_name_ver UNIQUE (tenant_id, name, version);


--
-- TOC entry 5648 (class 2606 OID 379486)
-- Name: crm_ai_skill uk_crm_ai_skill_tenant_name; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_skill
    ADD CONSTRAINT uk_crm_ai_skill_tenant_name UNIQUE (tenant_id, name);


--
-- TOC entry 5629 (class 2606 OID 371792)
-- Name: crm_email_analysis uk_crm_email_analysis_key; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_email_analysis
    ADD CONSTRAINT uk_crm_email_analysis_key UNIQUE (tenant_id, message_external_id, locale);


--
-- TOC entry 5674 (class 2606 OID 379616)
-- Name: crm_mcp_api_key uk_crm_mcp_api_key_prefix; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_mcp_api_key
    ADD CONSTRAINT uk_crm_mcp_api_key_prefix UNIQUE (key_prefix);


--
-- TOC entry 5676 (class 2606 OID 379614)
-- Name: crm_mcp_api_key uk_crm_mcp_api_key_tenant_name; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_mcp_api_key
    ADD CONSTRAINT uk_crm_mcp_api_key_tenant_name UNIQUE (tenant_id, name);


--
-- TOC entry 5653 (class 2606 OID 379505)
-- Name: crm_mcp_server uk_crm_mcp_server_tenant_name; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_mcp_server
    ADD CONSTRAINT uk_crm_mcp_server_tenant_name UNIQUE (tenant_id, name);


--
-- TOC entry 5618 (class 2606 OID 371707)
-- Name: crm_semantic_profile uk_crm_semantic_profile_tenant; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_semantic_profile
    ADD CONSTRAINT uk_crm_semantic_profile_tenant UNIQUE (tenant_id);


--
-- TOC entry 5574 (class 2606 OID 371271)
-- Name: crm_workflow_automation uk_crm_wf_auto_tenant_key; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_workflow_automation
    ADD CONSTRAINT uk_crm_wf_auto_tenant_key UNIQUE (tenant_id, automation_key);


--
-- TOC entry 5689 (class 2606 OID 379750)
-- Name: crm_wecom_kf_message uk_msgid; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_kf_message
    ADD CONSTRAINT uk_msgid UNIQUE (corp_id, msgid);


--
-- TOC entry 5591 (class 2606 OID 371464)
-- Name: crm_sf_express_config uk_sf_express_config_tenant; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_sf_express_config
    ADD CONSTRAINT uk_sf_express_config_tenant UNIQUE (tenant_id);


--
-- TOC entry 5596 (class 2606 OID 371476)
-- Name: crm_sf_express_token uk_sf_express_token_tenant; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_sf_express_token
    ADD CONSTRAINT uk_sf_express_token_tenant UNIQUE (tenant_id);


--
-- TOC entry 5177 (class 2606 OID 379783)
-- Name: crm_wecom_external_contact uk_wecom_ext_follow; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.crm_wecom_external_contact
    ADD CONSTRAINT uk_wecom_ext_follow UNIQUE (external_userid, follow_userid);


--
-- TOC entry 5605 (class 2606 OID 371627)
-- Name: crm_data_table_column uq_crm_data_table_column_name; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_data_table_column
    ADD CONSTRAINT uq_crm_data_table_column_name UNIQUE (data_table_id, name);


--
-- TOC entry 5601 (class 2606 OID 371615)
-- Name: crm_data_table uq_crm_data_table_tenant_name; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_data_table
    ADD CONSTRAINT uq_crm_data_table_tenant_name UNIQUE (tenant_id, name);


--
-- TOC entry 5558 (class 2606 OID 371225)
-- Name: w_connection w_connection_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.w_connection
    ADD CONSTRAINT w_connection_pkey PRIMARY KEY (id);


--
-- TOC entry 5552 (class 2606 OID 371223)
-- Name: w_credential w_credential_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.w_credential
    ADD CONSTRAINT w_credential_pkey PRIMARY KEY (id);


--
-- TOC entry 5561 (class 2606 OID 371227)
-- Name: w_execution w_execution_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.w_execution
    ADD CONSTRAINT w_execution_pkey PRIMARY KEY (id);


--
-- TOC entry 5554 (class 2606 OID 371221)
-- Name: w_node_definition w_node_definition_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.w_node_definition
    ADD CONSTRAINT w_node_definition_pkey PRIMARY KEY (id);


--
-- TOC entry 5549 (class 2606 OID 371167)
-- Name: w_node w_node_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.w_node
    ADD CONSTRAINT w_node_pkey PRIMARY KEY (id);


--
-- TOC entry 5567 (class 2606 OID 371214)
-- Name: w_wait_resume w_wait_resume_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.w_wait_resume
    ADD CONSTRAINT w_wait_resume_pkey PRIMARY KEY (id);


--
-- TOC entry 5569 (class 2606 OID 371352)
-- Name: w_wait_resume w_wait_resume_resume_key_key; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.w_wait_resume
    ADD CONSTRAINT w_wait_resume_resume_key_key UNIQUE (resume_key);


--
-- TOC entry 5547 (class 2606 OID 371236)
-- Name: w_workflow w_workflow_pkey; Type: CONSTRAINT; Schema: public; Owner: mega_vXnFBG5i
--

ALTER TABLE ONLY public.w_workflow
    ADD CONSTRAINT w_workflow_pkey PRIMARY KEY (id);


--
-- TOC entry 5466 (class 1259 OID 369782)
-- Name: act_idx_athrz_procedef; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_athrz_procedef ON public.act_ru_identitylink USING btree (proc_def_id_);


--
-- TOC entry 5319 (class 1259 OID 369783)
-- Name: act_idx_bytear_depl; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_bytear_depl ON public.act_ge_bytearray USING btree (deployment_id_);


--
-- TOC entry 5527 (class 1259 OID 369784)
-- Name: act_idx_channel_def_uniq; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX act_idx_channel_def_uniq ON public.flw_channel_definition USING btree (key_, version_, tenant_id_);


--
-- TOC entry 5426 (class 1259 OID 369785)
-- Name: act_idx_deadletter_job_correlation_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_deadletter_job_correlation_id ON public.act_ru_deadletter_job USING btree (correlation_id_);


--
-- TOC entry 5427 (class 1259 OID 369786)
-- Name: act_idx_deadletter_job_custom_values_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_deadletter_job_custom_values_id ON public.act_ru_deadletter_job USING btree (custom_values_id_);


--
-- TOC entry 5428 (class 1259 OID 369787)
-- Name: act_idx_deadletter_job_exception_stack_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_deadletter_job_exception_stack_id ON public.act_ru_deadletter_job USING btree (exception_stack_id_);


--
-- TOC entry 5429 (class 1259 OID 369788)
-- Name: act_idx_deadletter_job_execution_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_deadletter_job_execution_id ON public.act_ru_deadletter_job USING btree (execution_id_);


--
-- TOC entry 5430 (class 1259 OID 369789)
-- Name: act_idx_deadletter_job_proc_def_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_deadletter_job_proc_def_id ON public.act_ru_deadletter_job USING btree (proc_def_id_);


--
-- TOC entry 5431 (class 1259 OID 369790)
-- Name: act_idx_deadletter_job_process_instance_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_deadletter_job_process_instance_id ON public.act_ru_deadletter_job USING btree (process_instance_id_);


--
-- TOC entry 5432 (class 1259 OID 369791)
-- Name: act_idx_djob_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_djob_scope ON public.act_ru_deadletter_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 5433 (class 1259 OID 369792)
-- Name: act_idx_djob_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_djob_scope_def ON public.act_ru_deadletter_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 5434 (class 1259 OID 369793)
-- Name: act_idx_djob_sub_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_djob_sub_scope ON public.act_ru_deadletter_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 5456 (class 1259 OID 369794)
-- Name: act_idx_ejob_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ejob_scope ON public.act_ru_external_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 5457 (class 1259 OID 369795)
-- Name: act_idx_ejob_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ejob_scope_def ON public.act_ru_external_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 5458 (class 1259 OID 369796)
-- Name: act_idx_ejob_sub_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ejob_sub_scope ON public.act_ru_external_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 5437 (class 1259 OID 369797)
-- Name: act_idx_ent_lnk_ref_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ent_lnk_ref_scope ON public.act_ru_entitylink USING btree (ref_scope_id_, ref_scope_type_, link_type_);


--
-- TOC entry 5438 (class 1259 OID 369798)
-- Name: act_idx_ent_lnk_root_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ent_lnk_root_scope ON public.act_ru_entitylink USING btree (root_scope_id_, root_scope_type_, link_type_);


--
-- TOC entry 5439 (class 1259 OID 369799)
-- Name: act_idx_ent_lnk_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ent_lnk_scope ON public.act_ru_entitylink USING btree (scope_id_, scope_type_, link_type_);


--
-- TOC entry 5440 (class 1259 OID 369800)
-- Name: act_idx_ent_lnk_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ent_lnk_scope_def ON public.act_ru_entitylink USING btree (scope_definition_id_, scope_type_, link_type_);


--
-- TOC entry 5532 (class 1259 OID 369801)
-- Name: act_idx_event_def_uniq; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX act_idx_event_def_uniq ON public.flw_event_definition USING btree (key_, version_, tenant_id_);


--
-- TOC entry 5443 (class 1259 OID 369802)
-- Name: act_idx_event_subscr; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_event_subscr ON public.act_ru_event_subscr USING btree (execution_id_);


--
-- TOC entry 5444 (class 1259 OID 369803)
-- Name: act_idx_event_subscr_config_; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_event_subscr_config_ ON public.act_ru_event_subscr USING btree (configuration_);


--
-- TOC entry 5447 (class 1259 OID 369804)
-- Name: act_idx_exe_parent; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_exe_parent ON public.act_ru_execution USING btree (parent_id_);


--
-- TOC entry 5448 (class 1259 OID 369805)
-- Name: act_idx_exe_procdef; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_exe_procdef ON public.act_ru_execution USING btree (proc_def_id_);


--
-- TOC entry 5449 (class 1259 OID 369806)
-- Name: act_idx_exe_procinst; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_exe_procinst ON public.act_ru_execution USING btree (proc_inst_id_);


--
-- TOC entry 5450 (class 1259 OID 369807)
-- Name: act_idx_exe_root; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_exe_root ON public.act_ru_execution USING btree (root_proc_inst_id_);


--
-- TOC entry 5451 (class 1259 OID 369808)
-- Name: act_idx_exe_super; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_exe_super ON public.act_ru_execution USING btree (super_exec_);


--
-- TOC entry 5452 (class 1259 OID 369809)
-- Name: act_idx_exec_buskey; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_exec_buskey ON public.act_ru_execution USING btree (business_key_);


--
-- TOC entry 5453 (class 1259 OID 369810)
-- Name: act_idx_exec_ref_id_; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_exec_ref_id_ ON public.act_ru_execution USING btree (reference_id_);


--
-- TOC entry 5459 (class 1259 OID 369811)
-- Name: act_idx_external_job_correlation_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_external_job_correlation_id ON public.act_ru_external_job USING btree (correlation_id_);


--
-- TOC entry 5460 (class 1259 OID 369812)
-- Name: act_idx_external_job_custom_values_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_external_job_custom_values_id ON public.act_ru_external_job USING btree (custom_values_id_);


--
-- TOC entry 5461 (class 1259 OID 369813)
-- Name: act_idx_external_job_exception_stack_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_external_job_exception_stack_id ON public.act_ru_external_job USING btree (exception_stack_id_);


--
-- TOC entry 5324 (class 1259 OID 369814)
-- Name: act_idx_hi_act_inst_end; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_act_inst_end ON public.act_hi_actinst USING btree (end_time_);


--
-- TOC entry 5325 (class 1259 OID 369815)
-- Name: act_idx_hi_act_inst_exec; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_act_inst_exec ON public.act_hi_actinst USING btree (execution_id_, act_id_);


--
-- TOC entry 5326 (class 1259 OID 369816)
-- Name: act_idx_hi_act_inst_procinst; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_act_inst_procinst ON public.act_hi_actinst USING btree (proc_inst_id_, act_id_);


--
-- TOC entry 5327 (class 1259 OID 369817)
-- Name: act_idx_hi_act_inst_start; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_act_inst_start ON public.act_hi_actinst USING btree (start_time_);


--
-- TOC entry 5334 (class 1259 OID 369818)
-- Name: act_idx_hi_detail_act_inst; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_detail_act_inst ON public.act_hi_detail USING btree (act_inst_id_);


--
-- TOC entry 5335 (class 1259 OID 369819)
-- Name: act_idx_hi_detail_name; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_detail_name ON public.act_hi_detail USING btree (name_);


--
-- TOC entry 5336 (class 1259 OID 369820)
-- Name: act_idx_hi_detail_proc_inst; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_detail_proc_inst ON public.act_hi_detail USING btree (proc_inst_id_);


--
-- TOC entry 5337 (class 1259 OID 369821)
-- Name: act_idx_hi_detail_task_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_detail_task_id ON public.act_hi_detail USING btree (task_id_);


--
-- TOC entry 5338 (class 1259 OID 369822)
-- Name: act_idx_hi_detail_time; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_detail_time ON public.act_hi_detail USING btree (time_);


--
-- TOC entry 5341 (class 1259 OID 369823)
-- Name: act_idx_hi_ent_lnk_ref_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_ent_lnk_ref_scope ON public.act_hi_entitylink USING btree (ref_scope_id_, ref_scope_type_, link_type_);


--
-- TOC entry 5342 (class 1259 OID 369824)
-- Name: act_idx_hi_ent_lnk_root_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_ent_lnk_root_scope ON public.act_hi_entitylink USING btree (root_scope_id_, root_scope_type_, link_type_);


--
-- TOC entry 5343 (class 1259 OID 369825)
-- Name: act_idx_hi_ent_lnk_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_ent_lnk_scope ON public.act_hi_entitylink USING btree (scope_id_, scope_type_, link_type_);


--
-- TOC entry 5344 (class 1259 OID 369826)
-- Name: act_idx_hi_ent_lnk_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_ent_lnk_scope_def ON public.act_hi_entitylink USING btree (scope_definition_id_, scope_type_, link_type_);


--
-- TOC entry 5347 (class 1259 OID 369827)
-- Name: act_idx_hi_ident_lnk_procinst; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_ident_lnk_procinst ON public.act_hi_identitylink USING btree (proc_inst_id_);


--
-- TOC entry 5348 (class 1259 OID 369828)
-- Name: act_idx_hi_ident_lnk_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_ident_lnk_scope ON public.act_hi_identitylink USING btree (scope_id_, scope_type_);


--
-- TOC entry 5349 (class 1259 OID 369829)
-- Name: act_idx_hi_ident_lnk_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_ident_lnk_scope_def ON public.act_hi_identitylink USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 5350 (class 1259 OID 369830)
-- Name: act_idx_hi_ident_lnk_sub_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_ident_lnk_sub_scope ON public.act_hi_identitylink USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 5351 (class 1259 OID 369831)
-- Name: act_idx_hi_ident_lnk_task; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_ident_lnk_task ON public.act_hi_identitylink USING btree (task_id_);


--
-- TOC entry 5352 (class 1259 OID 369832)
-- Name: act_idx_hi_ident_lnk_user; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_ident_lnk_user ON public.act_hi_identitylink USING btree (user_id_);


--
-- TOC entry 5357 (class 1259 OID 369833)
-- Name: act_idx_hi_pro_i_buskey; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_pro_i_buskey ON public.act_hi_procinst USING btree (business_key_);


--
-- TOC entry 5358 (class 1259 OID 369834)
-- Name: act_idx_hi_pro_inst_end; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_pro_inst_end ON public.act_hi_procinst USING btree (end_time_);


--
-- TOC entry 5369 (class 1259 OID 369835)
-- Name: act_idx_hi_procvar_exe; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_procvar_exe ON public.act_hi_varinst USING btree (execution_id_);


--
-- TOC entry 5370 (class 1259 OID 369836)
-- Name: act_idx_hi_procvar_name_type; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_procvar_name_type ON public.act_hi_varinst USING btree (name_, var_type_);


--
-- TOC entry 5371 (class 1259 OID 369837)
-- Name: act_idx_hi_procvar_proc_inst; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_procvar_proc_inst ON public.act_hi_varinst USING btree (proc_inst_id_);


--
-- TOC entry 5372 (class 1259 OID 369838)
-- Name: act_idx_hi_procvar_task_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_procvar_task_id ON public.act_hi_varinst USING btree (task_id_);


--
-- TOC entry 5361 (class 1259 OID 369839)
-- Name: act_idx_hi_task_inst_procinst; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_task_inst_procinst ON public.act_hi_taskinst USING btree (proc_inst_id_);


--
-- TOC entry 5362 (class 1259 OID 369840)
-- Name: act_idx_hi_task_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_task_scope ON public.act_hi_taskinst USING btree (scope_id_, scope_type_);


--
-- TOC entry 5363 (class 1259 OID 369841)
-- Name: act_idx_hi_task_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_task_scope_def ON public.act_hi_taskinst USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 5364 (class 1259 OID 369842)
-- Name: act_idx_hi_task_sub_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_task_sub_scope ON public.act_hi_taskinst USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 5373 (class 1259 OID 369843)
-- Name: act_idx_hi_var_scope_id_type; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_var_scope_id_type ON public.act_hi_varinst USING btree (scope_id_, scope_type_);


--
-- TOC entry 5374 (class 1259 OID 369844)
-- Name: act_idx_hi_var_sub_id_type; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_var_sub_id_type ON public.act_hi_varinst USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 5467 (class 1259 OID 369845)
-- Name: act_idx_ident_lnk_group; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ident_lnk_group ON public.act_ru_identitylink USING btree (group_id_);


--
-- TOC entry 5468 (class 1259 OID 369846)
-- Name: act_idx_ident_lnk_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ident_lnk_scope ON public.act_ru_identitylink USING btree (scope_id_, scope_type_);


--
-- TOC entry 5469 (class 1259 OID 369847)
-- Name: act_idx_ident_lnk_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ident_lnk_scope_def ON public.act_ru_identitylink USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 5470 (class 1259 OID 369848)
-- Name: act_idx_ident_lnk_sub_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ident_lnk_sub_scope ON public.act_ru_identitylink USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 5471 (class 1259 OID 369849)
-- Name: act_idx_ident_lnk_user; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ident_lnk_user ON public.act_ru_identitylink USING btree (user_id_);


--
-- TOC entry 5472 (class 1259 OID 369850)
-- Name: act_idx_idl_procinst; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_idl_procinst ON public.act_ru_identitylink USING btree (proc_inst_id_);


--
-- TOC entry 5476 (class 1259 OID 369851)
-- Name: act_idx_job_correlation_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_job_correlation_id ON public.act_ru_job USING btree (correlation_id_);


--
-- TOC entry 5477 (class 1259 OID 369852)
-- Name: act_idx_job_custom_values_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_job_custom_values_id ON public.act_ru_job USING btree (custom_values_id_);


--
-- TOC entry 5478 (class 1259 OID 369853)
-- Name: act_idx_job_exception_stack_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_job_exception_stack_id ON public.act_ru_job USING btree (exception_stack_id_);


--
-- TOC entry 5479 (class 1259 OID 369854)
-- Name: act_idx_job_execution_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_job_execution_id ON public.act_ru_job USING btree (execution_id_);


--
-- TOC entry 5480 (class 1259 OID 369855)
-- Name: act_idx_job_proc_def_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_job_proc_def_id ON public.act_ru_job USING btree (proc_def_id_);


--
-- TOC entry 5481 (class 1259 OID 369856)
-- Name: act_idx_job_process_instance_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_job_process_instance_id ON public.act_ru_job USING btree (process_instance_id_);


--
-- TOC entry 5482 (class 1259 OID 369857)
-- Name: act_idx_job_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_job_scope ON public.act_ru_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 5483 (class 1259 OID 369858)
-- Name: act_idx_job_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_job_scope_def ON public.act_ru_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 5484 (class 1259 OID 369859)
-- Name: act_idx_job_sub_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_job_sub_scope ON public.act_ru_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 5383 (class 1259 OID 369860)
-- Name: act_idx_memb_group; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_memb_group ON public.act_id_membership USING btree (group_id_);


--
-- TOC entry 5384 (class 1259 OID 369861)
-- Name: act_idx_memb_user; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_memb_user ON public.act_id_membership USING btree (user_id_);


--
-- TOC entry 5408 (class 1259 OID 369862)
-- Name: act_idx_model_deployment; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_model_deployment ON public.act_re_model USING btree (deployment_id_);


--
-- TOC entry 5409 (class 1259 OID 369863)
-- Name: act_idx_model_source; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_model_source ON public.act_re_model USING btree (editor_source_value_id_);


--
-- TOC entry 5410 (class 1259 OID 369864)
-- Name: act_idx_model_source_extra; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_model_source_extra ON public.act_re_model USING btree (editor_source_extra_value_id_);


--
-- TOC entry 5391 (class 1259 OID 369865)
-- Name: act_idx_priv_group; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_priv_group ON public.act_id_priv_mapping USING btree (group_id_);


--
-- TOC entry 5392 (class 1259 OID 369866)
-- Name: act_idx_priv_mapping; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_priv_mapping ON public.act_id_priv_mapping USING btree (priv_id_);


--
-- TOC entry 5393 (class 1259 OID 369867)
-- Name: act_idx_priv_user; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_priv_user ON public.act_id_priv_mapping USING btree (user_id_);


--
-- TOC entry 5400 (class 1259 OID 369868)
-- Name: act_idx_procdef_info_json; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_procdef_info_json ON public.act_procdef_info USING btree (info_json_id_);


--
-- TOC entry 5401 (class 1259 OID 369869)
-- Name: act_idx_procdef_info_proc; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_procdef_info_proc ON public.act_procdef_info USING btree (proc_def_id_);


--
-- TOC entry 5417 (class 1259 OID 369870)
-- Name: act_idx_ru_acti_end; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ru_acti_end ON public.act_ru_actinst USING btree (end_time_);


--
-- TOC entry 5418 (class 1259 OID 369871)
-- Name: act_idx_ru_acti_exec; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ru_acti_exec ON public.act_ru_actinst USING btree (execution_id_);


--
-- TOC entry 5419 (class 1259 OID 369872)
-- Name: act_idx_ru_acti_exec_act; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ru_acti_exec_act ON public.act_ru_actinst USING btree (execution_id_, act_id_);


--
-- TOC entry 5420 (class 1259 OID 369873)
-- Name: act_idx_ru_acti_proc; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ru_acti_proc ON public.act_ru_actinst USING btree (proc_inst_id_);


--
-- TOC entry 5421 (class 1259 OID 369874)
-- Name: act_idx_ru_acti_proc_act; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ru_acti_proc_act ON public.act_ru_actinst USING btree (proc_inst_id_, act_id_);


--
-- TOC entry 5422 (class 1259 OID 369875)
-- Name: act_idx_ru_acti_start; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ru_acti_start ON public.act_ru_actinst USING btree (start_time_);


--
-- TOC entry 5423 (class 1259 OID 369876)
-- Name: act_idx_ru_acti_task; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ru_acti_task ON public.act_ru_actinst USING btree (task_id_);


--
-- TOC entry 5519 (class 1259 OID 369877)
-- Name: act_idx_ru_var_scope_id_type; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ru_var_scope_id_type ON public.act_ru_variable USING btree (scope_id_, scope_type_);


--
-- TOC entry 5520 (class 1259 OID 369878)
-- Name: act_idx_ru_var_sub_id_type; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ru_var_sub_id_type ON public.act_ru_variable USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 5487 (class 1259 OID 369879)
-- Name: act_idx_sjob_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_sjob_scope ON public.act_ru_suspended_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 5488 (class 1259 OID 369880)
-- Name: act_idx_sjob_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_sjob_scope_def ON public.act_ru_suspended_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 5489 (class 1259 OID 369881)
-- Name: act_idx_sjob_sub_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_sjob_sub_scope ON public.act_ru_suspended_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 5490 (class 1259 OID 369882)
-- Name: act_idx_suspended_job_correlation_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_suspended_job_correlation_id ON public.act_ru_suspended_job USING btree (correlation_id_);


--
-- TOC entry 5491 (class 1259 OID 369883)
-- Name: act_idx_suspended_job_custom_values_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_suspended_job_custom_values_id ON public.act_ru_suspended_job USING btree (custom_values_id_);


--
-- TOC entry 5492 (class 1259 OID 369884)
-- Name: act_idx_suspended_job_exception_stack_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_suspended_job_exception_stack_id ON public.act_ru_suspended_job USING btree (exception_stack_id_);


--
-- TOC entry 5493 (class 1259 OID 369885)
-- Name: act_idx_suspended_job_execution_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_suspended_job_execution_id ON public.act_ru_suspended_job USING btree (execution_id_);


--
-- TOC entry 5494 (class 1259 OID 369886)
-- Name: act_idx_suspended_job_proc_def_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_suspended_job_proc_def_id ON public.act_ru_suspended_job USING btree (proc_def_id_);


--
-- TOC entry 5495 (class 1259 OID 369887)
-- Name: act_idx_suspended_job_process_instance_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_suspended_job_process_instance_id ON public.act_ru_suspended_job USING btree (process_instance_id_);


--
-- TOC entry 5498 (class 1259 OID 369888)
-- Name: act_idx_task_create; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_task_create ON public.act_ru_task USING btree (create_time_);


--
-- TOC entry 5499 (class 1259 OID 369889)
-- Name: act_idx_task_exec; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_task_exec ON public.act_ru_task USING btree (execution_id_);


--
-- TOC entry 5500 (class 1259 OID 369890)
-- Name: act_idx_task_procdef; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_task_procdef ON public.act_ru_task USING btree (proc_def_id_);


--
-- TOC entry 5501 (class 1259 OID 369891)
-- Name: act_idx_task_procinst; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_task_procinst ON public.act_ru_task USING btree (proc_inst_id_);


--
-- TOC entry 5502 (class 1259 OID 369892)
-- Name: act_idx_task_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_task_scope ON public.act_ru_task USING btree (scope_id_, scope_type_);


--
-- TOC entry 5503 (class 1259 OID 369893)
-- Name: act_idx_task_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_task_scope_def ON public.act_ru_task USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 5504 (class 1259 OID 369894)
-- Name: act_idx_task_sub_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_task_sub_scope ON public.act_ru_task USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 5507 (class 1259 OID 369895)
-- Name: act_idx_timer_job_correlation_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_timer_job_correlation_id ON public.act_ru_timer_job USING btree (correlation_id_);


--
-- TOC entry 5508 (class 1259 OID 369896)
-- Name: act_idx_timer_job_custom_values_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_timer_job_custom_values_id ON public.act_ru_timer_job USING btree (custom_values_id_);


--
-- TOC entry 5509 (class 1259 OID 369897)
-- Name: act_idx_timer_job_duedate; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_timer_job_duedate ON public.act_ru_timer_job USING btree (duedate_);


--
-- TOC entry 5510 (class 1259 OID 369898)
-- Name: act_idx_timer_job_exception_stack_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_timer_job_exception_stack_id ON public.act_ru_timer_job USING btree (exception_stack_id_);


--
-- TOC entry 5511 (class 1259 OID 369899)
-- Name: act_idx_timer_job_execution_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_timer_job_execution_id ON public.act_ru_timer_job USING btree (execution_id_);


--
-- TOC entry 5512 (class 1259 OID 369900)
-- Name: act_idx_timer_job_proc_def_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_timer_job_proc_def_id ON public.act_ru_timer_job USING btree (proc_def_id_);


--
-- TOC entry 5513 (class 1259 OID 369901)
-- Name: act_idx_timer_job_process_instance_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_timer_job_process_instance_id ON public.act_ru_timer_job USING btree (process_instance_id_);


--
-- TOC entry 5514 (class 1259 OID 369902)
-- Name: act_idx_tjob_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_tjob_scope ON public.act_ru_timer_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 5515 (class 1259 OID 369903)
-- Name: act_idx_tjob_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_tjob_scope_def ON public.act_ru_timer_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 5516 (class 1259 OID 369904)
-- Name: act_idx_tjob_sub_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_tjob_sub_scope ON public.act_ru_timer_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 5473 (class 1259 OID 369905)
-- Name: act_idx_tskass_task; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_tskass_task ON public.act_ru_identitylink USING btree (task_id_);


--
-- TOC entry 5521 (class 1259 OID 369906)
-- Name: act_idx_var_bytearray; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_var_bytearray ON public.act_ru_variable USING btree (bytearray_id_);


--
-- TOC entry 5522 (class 1259 OID 369907)
-- Name: act_idx_var_exe; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_var_exe ON public.act_ru_variable USING btree (execution_id_);


--
-- TOC entry 5523 (class 1259 OID 369908)
-- Name: act_idx_var_procinst; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_var_procinst ON public.act_ru_variable USING btree (proc_inst_id_);


--
-- TOC entry 5524 (class 1259 OID 369909)
-- Name: act_idx_variable_task_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_variable_task_id ON public.act_ru_variable USING btree (task_id_);


--
-- TOC entry 5541 (class 1259 OID 369910)
-- Name: flw_idx_batch_part; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX flw_idx_batch_part ON public.flw_ru_batch_part USING btree (batch_id_);


--
-- TOC entry 5582 (class 1259 OID 371330)
-- Name: idx_account_value_clv; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_account_value_clv ON public.crm_account_value USING btree (tenant_id, net_clv DESC);


--
-- TOC entry 5583 (class 1259 OID 371329)
-- Name: idx_account_value_tenant_tier; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_account_value_tenant_tier ON public.crm_account_value USING btree (tenant_id, tier);


--
-- TOC entry 5045 (class 1259 OID 380095)
-- Name: idx_activity_event_type; Type: INDEX; Schema: public; Owner: mega_vXnFBG5i
--

CREATE INDEX idx_activity_event_type ON public.crm_activity USING btree (event_type);


--
-- TOC entry 5046 (class 1259 OID 380097)
-- Name: idx_activity_importance; Type: INDEX; Schema: public; Owner: mega_vXnFBG5i
--

CREATE INDEX idx_activity_importance ON public.crm_activity USING btree (importance);


--
-- TOC entry 5047 (class 1259 OID 380098)
-- Name: idx_activity_related; Type: INDEX; Schema: public; Owner: mega_vXnFBG5i
--

CREATE INDEX idx_activity_related ON public.crm_activity USING btree (relate_type, relate_id);


--
-- TOC entry 5048 (class 1259 OID 380099)
-- Name: idx_activity_source; Type: INDEX; Schema: public; Owner: mega_vXnFBG5i
--

CREATE INDEX idx_activity_source ON public.crm_activity USING btree (source);


--
-- TOC entry 5049 (class 1259 OID 380096)
-- Name: idx_activity_started_date; Type: INDEX; Schema: public; Owner: mega_vXnFBG5i
--

CREATE INDEX idx_activity_started_date ON public.crm_activity USING btree (started_date);


--
-- TOC entry 5050 (class 1259 OID 380093)
-- Name: idx_activity_subject; Type: INDEX; Schema: public; Owner: mega_vXnFBG5i
--

CREATE INDEX idx_activity_subject ON public.crm_activity USING btree (subject_type, subject_id);


--
-- TOC entry 5051 (class 1259 OID 380094)
-- Name: idx_activity_subject_date; Type: INDEX; Schema: public; Owner: mega_vXnFBG5i
--

CREATE INDEX idx_activity_subject_date ON public.crm_activity USING btree (subject_type, subject_id, started_date);


--
-- TOC entry 5696 (class 1259 OID 380083)
-- Name: idx_callback_event_corp_time; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_callback_event_corp_time ON public.crm_wecom_callback_event USING btree (corp_id, create_time);


--
-- TOC entry 5697 (class 1259 OID 380084)
-- Name: idx_callback_event_type; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_callback_event_type ON public.crm_wecom_callback_event USING btree (corp_id, event, change_type);


--
-- TOC entry 5072 (class 1259 OID 379753)
-- Name: idx_contact_wecom_external_userid; Type: INDEX; Schema: public; Owner: mega_vXnFBG5i
--

CREATE INDEX idx_contact_wecom_external_userid ON public.crm_contact USING btree (wecom_external_userid) WHERE (wecom_external_userid IS NOT NULL);


--
-- TOC entry 5638 (class 1259 OID 371832)
-- Name: idx_crm_ai_chat_message_session; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ai_chat_message_session ON public.crm_ai_chat_message USING btree (session_id, create_time);


--
-- TOC entry 5635 (class 1259 OID 371818)
-- Name: idx_crm_ai_chat_session_user; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ai_chat_session_user ON public.crm_ai_chat_session USING btree (tenant_id, user_id, update_time DESC) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 5621 (class 1259 OID 371752)
-- Name: idx_crm_ai_model_config_tenant; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ai_model_config_tenant ON public.crm_ai_model_config USING btree (tenant_id);


--
-- TOC entry 5667 (class 1259 OID 379555)
-- Name: idx_crm_ai_playbook_run_playbook; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ai_playbook_run_playbook ON public.crm_ai_playbook_run USING btree (playbook_id);


--
-- TOC entry 5668 (class 1259 OID 379556)
-- Name: idx_crm_ai_playbook_run_subject; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ai_playbook_run_subject ON public.crm_ai_playbook_run USING btree (tenant_id, subject_type, subject_id);


--
-- TOC entry 5669 (class 1259 OID 379554)
-- Name: idx_crm_ai_playbook_run_tenant; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ai_playbook_run_tenant ON public.crm_ai_playbook_run USING btree (tenant_id);


--
-- TOC entry 5661 (class 1259 OID 379540)
-- Name: idx_crm_ai_playbook_step_playbook; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ai_playbook_step_playbook ON public.crm_ai_skill_playbook_step USING btree (playbook_id);


--
-- TOC entry 5662 (class 1259 OID 379541)
-- Name: idx_crm_ai_playbook_step_skill; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ai_playbook_step_skill ON public.crm_ai_skill_playbook_step USING btree (skill_id);


--
-- TOC entry 5656 (class 1259 OID 379523)
-- Name: idx_crm_ai_playbook_tenant; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ai_playbook_tenant ON public.crm_ai_skill_playbook USING btree (tenant_id);


--
-- TOC entry 5644 (class 1259 OID 379487)
-- Name: idx_crm_ai_skill_tenant; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ai_skill_tenant ON public.crm_ai_skill USING btree (tenant_id);


--
-- TOC entry 5645 (class 1259 OID 379488)
-- Name: idx_crm_ai_skill_tenant_domain; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ai_skill_tenant_domain ON public.crm_ai_skill USING btree (tenant_id, domain);


--
-- TOC entry 5646 (class 1259 OID 379489)
-- Name: idx_crm_ai_skill_tenant_track; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ai_skill_tenant_track ON public.crm_ai_skill USING btree (tenant_id, track_key);


--
-- TOC entry 5641 (class 1259 OID 371841)
-- Name: idx_crm_analytics_query_log_tenant_time; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_analytics_query_log_tenant_time ON public.crm_analytics_query_log USING btree (tenant_id, create_time DESC);


--
-- TOC entry 5608 (class 1259 OID 371648)
-- Name: idx_crm_data_table_row_table; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_data_table_row_table ON public.crm_data_table_row USING btree (data_table_id);


--
-- TOC entry 5609 (class 1259 OID 371649)
-- Name: idx_crm_data_table_row_table_created; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_data_table_row_table_created ON public.crm_data_table_row USING btree (data_table_id, create_time DESC);


--
-- TOC entry 5599 (class 1259 OID 371650)
-- Name: idx_crm_data_table_tenant; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_data_table_tenant ON public.crm_data_table USING btree (tenant_id);


--
-- TOC entry 5626 (class 1259 OID 371793)
-- Name: idx_crm_email_analysis_lookup; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_email_analysis_lookup ON public.crm_email_analysis USING btree (tenant_id, message_external_id, locale) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 5627 (class 1259 OID 371794)
-- Name: idx_crm_email_analysis_user; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_email_analysis_user ON public.crm_email_analysis USING btree (tenant_id, user_id, update_time DESC);


--
-- TOC entry 5632 (class 1259 OID 371809)
-- Name: idx_crm_followup_cadence_tenant; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_followup_cadence_tenant ON public.crm_follow_up_cadence USING btree (tenant_id);


--
-- TOC entry 5672 (class 1259 OID 379617)
-- Name: idx_crm_mcp_api_key_tenant_enabled; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_mcp_api_key_tenant_enabled ON public.crm_mcp_api_key USING btree (tenant_id, enabled);


--
-- TOC entry 5679 (class 1259 OID 379629)
-- Name: idx_crm_mcp_audit_log_tenant_time; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_mcp_audit_log_tenant_time ON public.crm_mcp_audit_log USING btree (tenant_id, create_time DESC);


--
-- TOC entry 5651 (class 1259 OID 379506)
-- Name: idx_crm_mcp_server_tenant; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_mcp_server_tenant ON public.crm_mcp_server USING btree (tenant_id);


--
-- TOC entry 5616 (class 1259 OID 371708)
-- Name: idx_crm_sem_profile_tenant; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_sem_profile_tenant ON public.crm_semantic_profile USING btree (tenant_id);


--
-- TOC entry 5572 (class 1259 OID 371272)
-- Name: idx_crm_wf_auto_tenant; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_wf_auto_tenant ON public.crm_workflow_automation USING btree (tenant_id);


--
-- TOC entry 5686 (class 1259 OID 379751)
-- Name: idx_kf_msg_external_userid; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_kf_msg_external_userid ON public.crm_wecom_kf_message USING btree (corp_id, external_userid, send_time);


--
-- TOC entry 5687 (class 1259 OID 379752)
-- Name: idx_kf_msg_open_kfid; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_kf_msg_open_kfid ON public.crm_wecom_kf_message USING btree (corp_id, open_kfid, send_time);


--
-- TOC entry 5577 (class 1259 OID 371306)
-- Name: idx_patient_value_account; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_patient_value_account ON public.crm_patient_value USING btree (account_id);


--
-- TOC entry 5578 (class 1259 OID 371308)
-- Name: idx_patient_value_recall; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_patient_value_recall ON public.crm_patient_value USING btree (next_recall_due_at) WHERE (del_flag = 0);


--
-- TOC entry 5579 (class 1259 OID 371307)
-- Name: idx_patient_value_tenant_tier; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_patient_value_tenant_tier ON public.crm_patient_value USING btree (tenant_id, tier);


--
-- TOC entry 5594 (class 1259 OID 371477)
-- Name: idx_sf_express_token_expires; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_sf_express_token_expires ON public.crm_sf_express_token USING btree (expires_at);


--
-- TOC entry 5700 (class 1259 OID 380110)
-- Name: idx_subject_map_parent; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_subject_map_parent ON public.crm_subject_map USING btree (parent_subject_type, parent_subject_id);


--
-- TOC entry 5562 (class 1259 OID 371353)
-- Name: idx_w_wait_resume_execution_node; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_w_wait_resume_execution_node ON public.w_wait_resume USING btree (execution_id, node_id);


--
-- TOC entry 5610 (class 1259 OID 371693)
-- Name: idx_webhook_path_tenant; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_webhook_path_tenant ON public.sys_webhook_path_index USING btree (tenant_id);


--
-- TOC entry 5611 (class 1259 OID 371694)
-- Name: idx_webhook_path_workflow; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_webhook_path_workflow ON public.sys_webhook_path_index USING btree (workflow_id);


--
-- TOC entry 5171 (class 1259 OID 371431)
-- Name: idx_wecom_contact_way_state; Type: INDEX; Schema: public; Owner: mega_vXnFBG5i
--

CREATE INDEX idx_wecom_contact_way_state ON public.crm_wecom_contact_way USING btree (state);


--
-- TOC entry 5175 (class 1259 OID 371432)
-- Name: idx_wecom_ext_lead; Type: INDEX; Schema: public; Owner: mega_vXnFBG5i
--

CREATE INDEX idx_wecom_ext_lead ON public.crm_wecom_external_contact USING btree (crm_lead_id);


--
-- TOC entry 5180 (class 1259 OID 371433)
-- Name: idx_wecom_mass_msg; Type: INDEX; Schema: public; Owner: mega_vXnFBG5i
--

CREATE INDEX idx_wecom_mass_msg ON public.crm_wecom_mass_task USING btree (msg_id);


--
-- TOC entry 5692 (class 1259 OID 380031)
-- Name: idx_wecom_material_create_time; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_wecom_material_create_time ON public.crm_wecom_material USING btree (create_time DESC);


--
-- TOC entry 5693 (class 1259 OID 380030)
-- Name: idx_wecom_material_status; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_wecom_material_status ON public.crm_wecom_material USING btree (status);


--
-- TOC entry 5586 (class 1259 OID 371404)
-- Name: idx_wecom_welcome_log_ct; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_wecom_welcome_log_ct ON public.crm_wecom_welcome_log USING btree (create_time);


--
-- TOC entry 5587 (class 1259 OID 371403)
-- Name: idx_wecom_welcome_log_ext; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_wecom_welcome_log_ext ON public.crm_wecom_welcome_log USING btree (external_userid);


--
-- TOC entry 5544 (class 1259 OID 371089)
-- Name: session_token_key; Type: INDEX; Schema: public; Owner: mega_vXnFBG5i
--

CREATE UNIQUE INDEX session_token_key ON public.session USING btree (token);


--
-- TOC entry 5172 (class 1259 OID 371405)
-- Name: uk_wecom_contact_way_state; Type: INDEX; Schema: public; Owner: mega_vXnFBG5i
--

CREATE UNIQUE INDEX uk_wecom_contact_way_state ON public.crm_wecom_contact_way USING btree (state) WHERE ((state IS NOT NULL) AND (TRIM(BOTH FROM state) <> ''::text));


--
-- TOC entry 5556 (class 1259 OID 371192)
-- Name: w_connection_fromNodeId_toNodeId_fromOutput_toInput_key; Type: INDEX; Schema: public; Owner: mega_vXnFBG5i
--

CREATE UNIQUE INDEX "w_connection_fromNodeId_toNodeId_fromOutput_toInput_key" ON public.w_connection USING btree (from_node_id, to_node_id, from_output, to_input);


--
-- TOC entry 5550 (class 1259 OID 371174)
-- Name: w_credential_organizationId_idx; Type: INDEX; Schema: public; Owner: mega_vXnFBG5i
--

CREATE INDEX "w_credential_organizationId_idx" ON public.w_credential USING btree (organization_id);


--
-- TOC entry 5559 (class 1259 OID 371200)
-- Name: w_execution_inngestEventId_key; Type: INDEX; Schema: public; Owner: mega_vXnFBG5i
--

CREATE UNIQUE INDEX "w_execution_inngestEventId_key" ON public.w_execution USING btree (inngest_event_id);


--
-- TOC entry 5555 (class 1259 OID 371181)
-- Name: w_node_definition_type_key; Type: INDEX; Schema: public; Owner: mega_vXnFBG5i
--

CREATE UNIQUE INDEX w_node_definition_type_key ON public.w_node_definition USING btree (type);


--
-- TOC entry 5563 (class 1259 OID 371216)
-- Name: w_wait_resume_execution_id_idx; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX w_wait_resume_execution_id_idx ON public.w_wait_resume USING btree (execution_id);


--
-- TOC entry 5564 (class 1259 OID 371215)
-- Name: w_wait_resume_key_key; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX w_wait_resume_key_key ON public.w_wait_resume USING btree (resume_key);


--
-- TOC entry 5565 (class 1259 OID 371217)
-- Name: w_wait_resume_node_id_idx; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX w_wait_resume_node_id_idx ON public.w_wait_resume USING btree (node_id);


--
-- TOC entry 5545 (class 1259 OID 371156)
-- Name: w_workflow_organizationId_idx; Type: INDEX; Schema: public; Owner: mega_vXnFBG5i
--

CREATE INDEX "w_workflow_organizationId_idx" ON public.w_workflow USING btree (organization_id);

CREATE INDEX IF NOT EXISTS idx_w_workflow_is_template ON public.w_workflow USING btree (is_template);


--
-- TOC entry 5732 (class 2606 OID 369911)
-- Name: act_ru_identitylink act_fk_athrz_procedef; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_identitylink
    ADD CONSTRAINT act_fk_athrz_procedef FOREIGN KEY (proc_def_id_) REFERENCES public.act_re_procdef(id_);


--
-- TOC entry 5711 (class 2606 OID 369916)
-- Name: act_ge_bytearray act_fk_bytearr_depl; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ge_bytearray
    ADD CONSTRAINT act_fk_bytearr_depl FOREIGN KEY (deployment_id_) REFERENCES public.act_re_deployment(id_);


--
-- TOC entry 5720 (class 2606 OID 369921)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_custom_values; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 5721 (class 2606 OID 369926)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_exception; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 5722 (class 2606 OID 369931)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_execution; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_execution FOREIGN KEY (execution_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 5723 (class 2606 OID 369936)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_proc_def; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_proc_def FOREIGN KEY (proc_def_id_) REFERENCES public.act_re_procdef(id_);


--
-- TOC entry 5724 (class 2606 OID 369941)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_process_instance; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_process_instance FOREIGN KEY (process_instance_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 5725 (class 2606 OID 369946)
-- Name: act_ru_event_subscr act_fk_event_exec; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_event_subscr
    ADD CONSTRAINT act_fk_event_exec FOREIGN KEY (execution_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 5726 (class 2606 OID 369951)
-- Name: act_ru_execution act_fk_exe_parent; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_execution
    ADD CONSTRAINT act_fk_exe_parent FOREIGN KEY (parent_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 5727 (class 2606 OID 369956)
-- Name: act_ru_execution act_fk_exe_procdef; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_execution
    ADD CONSTRAINT act_fk_exe_procdef FOREIGN KEY (proc_def_id_) REFERENCES public.act_re_procdef(id_);


--
-- TOC entry 5728 (class 2606 OID 369961)
-- Name: act_ru_execution act_fk_exe_procinst; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_execution
    ADD CONSTRAINT act_fk_exe_procinst FOREIGN KEY (proc_inst_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 5729 (class 2606 OID 369966)
-- Name: act_ru_execution act_fk_exe_super; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_execution
    ADD CONSTRAINT act_fk_exe_super FOREIGN KEY (super_exec_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 5730 (class 2606 OID 369971)
-- Name: act_ru_external_job act_fk_external_job_custom_values; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_external_job
    ADD CONSTRAINT act_fk_external_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 5731 (class 2606 OID 369976)
-- Name: act_ru_external_job act_fk_external_job_exception; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_external_job
    ADD CONSTRAINT act_fk_external_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 5733 (class 2606 OID 369981)
-- Name: act_ru_identitylink act_fk_idl_procinst; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_identitylink
    ADD CONSTRAINT act_fk_idl_procinst FOREIGN KEY (proc_inst_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 5715 (class 2606 OID 369986)
-- Name: act_procdef_info act_fk_info_json_ba; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_procdef_info
    ADD CONSTRAINT act_fk_info_json_ba FOREIGN KEY (info_json_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 5716 (class 2606 OID 369991)
-- Name: act_procdef_info act_fk_info_procdef; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_procdef_info
    ADD CONSTRAINT act_fk_info_procdef FOREIGN KEY (proc_def_id_) REFERENCES public.act_re_procdef(id_);


--
-- TOC entry 5735 (class 2606 OID 369996)
-- Name: act_ru_job act_fk_job_custom_values; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_job
    ADD CONSTRAINT act_fk_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 5736 (class 2606 OID 370001)
-- Name: act_ru_job act_fk_job_exception; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_job
    ADD CONSTRAINT act_fk_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 5737 (class 2606 OID 370006)
-- Name: act_ru_job act_fk_job_execution; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_job
    ADD CONSTRAINT act_fk_job_execution FOREIGN KEY (execution_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 5738 (class 2606 OID 370011)
-- Name: act_ru_job act_fk_job_proc_def; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_job
    ADD CONSTRAINT act_fk_job_proc_def FOREIGN KEY (proc_def_id_) REFERENCES public.act_re_procdef(id_);


--
-- TOC entry 5739 (class 2606 OID 370016)
-- Name: act_ru_job act_fk_job_process_instance; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_job
    ADD CONSTRAINT act_fk_job_process_instance FOREIGN KEY (process_instance_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 5712 (class 2606 OID 370021)
-- Name: act_id_membership act_fk_memb_group; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_membership
    ADD CONSTRAINT act_fk_memb_group FOREIGN KEY (group_id_) REFERENCES public.act_id_group(id_);


--
-- TOC entry 5713 (class 2606 OID 370026)
-- Name: act_id_membership act_fk_memb_user; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_membership
    ADD CONSTRAINT act_fk_memb_user FOREIGN KEY (user_id_) REFERENCES public.act_id_user(id_);


--
-- TOC entry 5717 (class 2606 OID 370031)
-- Name: act_re_model act_fk_model_deployment; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_re_model
    ADD CONSTRAINT act_fk_model_deployment FOREIGN KEY (deployment_id_) REFERENCES public.act_re_deployment(id_);


--
-- TOC entry 5718 (class 2606 OID 370036)
-- Name: act_re_model act_fk_model_source; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_re_model
    ADD CONSTRAINT act_fk_model_source FOREIGN KEY (editor_source_value_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 5719 (class 2606 OID 370041)
-- Name: act_re_model act_fk_model_source_extra; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_re_model
    ADD CONSTRAINT act_fk_model_source_extra FOREIGN KEY (editor_source_extra_value_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 5714 (class 2606 OID 370046)
-- Name: act_id_priv_mapping act_fk_priv_mapping; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_priv_mapping
    ADD CONSTRAINT act_fk_priv_mapping FOREIGN KEY (priv_id_) REFERENCES public.act_id_priv(id_);


--
-- TOC entry 5740 (class 2606 OID 370051)
-- Name: act_ru_suspended_job act_fk_suspended_job_custom_values; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 5741 (class 2606 OID 370056)
-- Name: act_ru_suspended_job act_fk_suspended_job_exception; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 5742 (class 2606 OID 370061)
-- Name: act_ru_suspended_job act_fk_suspended_job_execution; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_execution FOREIGN KEY (execution_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 5743 (class 2606 OID 370066)
-- Name: act_ru_suspended_job act_fk_suspended_job_proc_def; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_proc_def FOREIGN KEY (proc_def_id_) REFERENCES public.act_re_procdef(id_);


--
-- TOC entry 5744 (class 2606 OID 370071)
-- Name: act_ru_suspended_job act_fk_suspended_job_process_instance; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_process_instance FOREIGN KEY (process_instance_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 5745 (class 2606 OID 370076)
-- Name: act_ru_task act_fk_task_exe; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_task
    ADD CONSTRAINT act_fk_task_exe FOREIGN KEY (execution_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 5746 (class 2606 OID 370081)
-- Name: act_ru_task act_fk_task_procdef; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_task
    ADD CONSTRAINT act_fk_task_procdef FOREIGN KEY (proc_def_id_) REFERENCES public.act_re_procdef(id_);


--
-- TOC entry 5747 (class 2606 OID 370086)
-- Name: act_ru_task act_fk_task_procinst; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_task
    ADD CONSTRAINT act_fk_task_procinst FOREIGN KEY (proc_inst_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 5748 (class 2606 OID 370091)
-- Name: act_ru_timer_job act_fk_timer_job_custom_values; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 5749 (class 2606 OID 370096)
-- Name: act_ru_timer_job act_fk_timer_job_exception; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 5750 (class 2606 OID 370101)
-- Name: act_ru_timer_job act_fk_timer_job_execution; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_execution FOREIGN KEY (execution_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 5751 (class 2606 OID 370106)
-- Name: act_ru_timer_job act_fk_timer_job_proc_def; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_proc_def FOREIGN KEY (proc_def_id_) REFERENCES public.act_re_procdef(id_);


--
-- TOC entry 5752 (class 2606 OID 370111)
-- Name: act_ru_timer_job act_fk_timer_job_process_instance; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_process_instance FOREIGN KEY (process_instance_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 5734 (class 2606 OID 370116)
-- Name: act_ru_identitylink act_fk_tskass_task; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_identitylink
    ADD CONSTRAINT act_fk_tskass_task FOREIGN KEY (task_id_) REFERENCES public.act_ru_task(id_);


--
-- TOC entry 5753 (class 2606 OID 370121)
-- Name: act_ru_variable act_fk_var_bytearray; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_variable
    ADD CONSTRAINT act_fk_var_bytearray FOREIGN KEY (bytearray_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 5754 (class 2606 OID 370126)
-- Name: act_ru_variable act_fk_var_exe; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_variable
    ADD CONSTRAINT act_fk_var_exe FOREIGN KEY (execution_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 5755 (class 2606 OID 370131)
-- Name: act_ru_variable act_fk_var_procinst; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_variable
    ADD CONSTRAINT act_fk_var_procinst FOREIGN KEY (proc_inst_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 5759 (class 2606 OID 371827)
-- Name: crm_ai_chat_message crm_ai_chat_message_session_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_chat_message
    ADD CONSTRAINT crm_ai_chat_message_session_id_fkey FOREIGN KEY (session_id) REFERENCES public.crm_ai_chat_session(session_id);


--
-- TOC entry 5757 (class 2606 OID 371628)
-- Name: crm_data_table_column crm_data_table_column_data_table_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_data_table_column
    ADD CONSTRAINT crm_data_table_column_data_table_id_fkey FOREIGN KEY (data_table_id) REFERENCES public.crm_data_table(id) ON DELETE CASCADE;


--
-- TOC entry 5758 (class 2606 OID 371643)
-- Name: crm_data_table_row crm_data_table_row_data_table_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_data_table_row
    ADD CONSTRAINT crm_data_table_row_data_table_id_fkey FOREIGN KEY (data_table_id) REFERENCES public.crm_data_table(id) ON DELETE CASCADE;


--
-- TOC entry 5756 (class 2606 OID 370136)
-- Name: flw_ru_batch_part flw_fk_batch_part_parent; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.flw_ru_batch_part
    ADD CONSTRAINT flw_fk_batch_part_parent FOREIGN KEY (batch_id_) REFERENCES public.flw_ru_batch(id_);


--

--
-- Website Builder Phase 1: tenant website page schema
--

CREATE TABLE IF NOT EXISTS public.crm_tenant_website_page (
  id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  path              VARCHAR(255) NOT NULL,
  title             VARCHAR(255) NOT NULL,
  draft_schema      JSONB,
  published_schema  JSONB,
  status            VARCHAR(20) NOT NULL DEFAULT 'draft',
  schema_version    INT NOT NULL DEFAULT 1,
  published_at      TIMESTAMPTZ,
  created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT uq_tenant_website_page_path UNIQUE (path)
);

CREATE INDEX IF NOT EXISTS idx_tenant_website_page_status
  ON public.crm_tenant_website_page (status);

COMMENT ON TABLE public.crm_tenant_website_page IS '租户官网页面 Schema（draft/published）';
COMMENT ON COLUMN public.crm_tenant_website_page.draft_schema IS 'Website Schema JSON，禁止 HTML';
COMMENT ON COLUMN public.crm_tenant_website_page.published_schema IS '已发布 Website Schema JSON';
COMMENT ON COLUMN public.crm_tenant_website_page.schema_version IS 'Schema 版本号，供未来 migration';

--
-- Tenant website asset library (images / videos)
--

CREATE TABLE IF NOT EXISTS public.crm_tenant_website_asset (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    file_title      VARCHAR(255)    NOT NULL,
    file_name       VARCHAR(500)    NOT NULL,
    original_name   VARCHAR(500)    NOT NULL,
    file_type       VARCHAR(50)     NOT NULL,
    file_size       BIGINT          NOT NULL,
    media_type      VARCHAR(20)     NOT NULL,
    mime_type       VARCHAR(100)    NOT NULL,
    width           INT,
    height          INT,
    duration_ms     INT,
    status          VARCHAR(20)     NOT NULL DEFAULT 'active',
    public_path     VARCHAR(255)    NOT NULL,
    create_by       VARCHAR(64),
    create_time     TIMESTAMPTZ     NOT NULL DEFAULT now(),
    update_time     TIMESTAMPTZ     NOT NULL DEFAULT now(),
    CONSTRAINT chk_website_asset_media_type CHECK (media_type IN ('image', 'video')),
    CONSTRAINT chk_website_asset_status CHECK (status IN ('active', 'deleted'))
);

CREATE INDEX IF NOT EXISTS idx_website_asset_list
    ON public.crm_tenant_website_asset (status, media_type, create_time DESC);

CREATE UNIQUE INDEX IF NOT EXISTS uq_website_asset_public_path
    ON public.crm_tenant_website_asset (public_path)
    WHERE status = 'active';

COMMENT ON TABLE public.crm_tenant_website_asset IS '租户网站公开素材（图片/视频）';
COMMENT ON COLUMN public.crm_tenant_website_asset.file_name IS '服务器存储相对路径，与 common/upload 一致';
COMMENT ON COLUMN public.crm_tenant_website_asset.public_path IS '公开引用路径，固定 /_assets/{id}';
COMMENT ON COLUMN public.crm_tenant_website_asset.status IS 'active=可公开访问；deleted=软删除';

--
-- WeCom sidebar P1: sales script library
--

CREATE TABLE IF NOT EXISTS public.crm_sales_script (
    script_id   varchar(64) PRIMARY KEY,
    script_type varchar(20) NOT NULL,
    category    varchar(100),
    title       varchar(200) NOT NULL,
    content     text NOT NULL,
    material_id bigint,
    sort_order  int DEFAULT 0,
    status      char(1) NOT NULL DEFAULT '0',
    create_by   varchar(64),
    create_time timestamp without time zone,
    update_by   varchar(64),
    update_time timestamp without time zone,
    del_flag    char(1) NOT NULL DEFAULT '0'
);

CREATE INDEX IF NOT EXISTS idx_crm_sales_script_type_status
    ON public.crm_sales_script (script_type, status, del_flag);

CREATE INDEX IF NOT EXISTS idx_crm_sales_script_category
    ON public.crm_sales_script (category);

CREATE INDEX IF NOT EXISTS idx_crm_sales_script_sort
    ON public.crm_sales_script (sort_order DESC, update_time DESC);

COMMENT ON TABLE public.crm_sales_script IS '侧边栏话术库：营销话术 / Q&A / SOP';
COMMENT ON COLUMN public.crm_sales_script.script_type IS 'marketing | qa | sop';
COMMENT ON COLUMN public.crm_sales_script.status IS '0 启用 / 1 停用';
COMMENT ON COLUMN public.crm_sales_script.del_flag IS '逻辑删除 0 正常 / 1 已删';


--
-- 企业知识库（pgvector RAG）：crm_kb_document / crm_kb_chunk
-- 同步维护：scripts/sql/seed-crm-kb-tables.sql、patch-sys-organization-kb-profile.sql
-- 前置：PostgreSQL 实例已安装 pgvector（HNSW 需 >= 0.5.0）
--

CREATE EXTENSION IF NOT EXISTS vector;

ALTER TABLE public.sys_organization
  ADD COLUMN IF NOT EXISTS industry varchar(128) DEFAULT '',
  ADD COLUMN IF NOT EXISTS tagline varchar(255) DEFAULT '',
  ADD COLUMN IF NOT EXISTS intro text;

CREATE TABLE IF NOT EXISTS public.crm_kb_document (
  document_id   varchar(36)  PRIMARY KEY,
  title         varchar(512) NOT NULL DEFAULT '',
  category      varchar(64)  NOT NULL DEFAULT 'other',
  content_md    text         NOT NULL DEFAULT '',
  status        varchar(32)  NOT NULL DEFAULT 'draft',
  source_type   varchar(64)  NOT NULL DEFAULT 'manual',
  source_id     varchar(64)  NULL,
  metadata      jsonb        NOT NULL DEFAULT '{}'::jsonb,
  content_hash  varchar(64)  NULL,
  index_status  varchar(32)  NOT NULL DEFAULT 'pending',
  index_error   varchar(1024) NULL,
  index_time    timestamp    NULL,
  create_by     varchar(64)  NULL,
  create_time   timestamp    NULL,
  update_by     varchar(64)  NULL,
  update_time   timestamp    NULL,
  del_flag      char(1)      NOT NULL DEFAULT '0'
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_crm_kb_document_source
  ON public.crm_kb_document (source_type, source_id)
  WHERE source_id IS NOT NULL AND del_flag = '0';

CREATE INDEX IF NOT EXISTS idx_crm_kb_document_status_cat
  ON public.crm_kb_document (status, category)
  WHERE del_flag = '0';

CREATE INDEX IF NOT EXISTS idx_crm_kb_document_index_status
  ON public.crm_kb_document (index_status)
  WHERE del_flag = '0';

CREATE TABLE IF NOT EXISTS public.crm_kb_chunk (
  chunk_id       varchar(36)  PRIMARY KEY,
  document_id    varchar(36)  NOT NULL REFERENCES public.crm_kb_document(document_id) ON DELETE CASCADE,
  chunk_index    int          NOT NULL DEFAULT 0,
  chunk_text     text         NOT NULL,
  token_estimate int          NULL,
  metadata       jsonb        NOT NULL DEFAULT '{}'::jsonb,
  embedding      vector(1024) NOT NULL,
  create_time    timestamp    NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_crm_kb_chunk_document
  ON public.crm_kb_chunk (document_id);

CREATE INDEX IF NOT EXISTS idx_crm_kb_chunk_embedding_hnsw
  ON public.crm_kb_chunk
  USING hnsw (embedding vector_cosine_ops);

COMMENT ON TABLE public.crm_kb_document IS '企业知识库文档（RAG）';
COMMENT ON TABLE public.crm_kb_chunk IS '知识库切块 + 向量（pgvector）';


--
-- 越南工业园区招商信息库：crm_industrial_park
-- 同步维护：scripts/sql/seed-crm-industrial-park-table.sql
--

CREATE TABLE IF NOT EXISTS public.crm_industrial_park (
  id                                    VARCHAR(64) PRIMARY KEY,
  name_cn                               VARCHAR(256) NOT NULL,
  name_native                           VARCHAR(512) NOT NULL DEFAULT '',
  region                                VARCHAR(16)  NOT NULL,
  province                              VARCHAR(128) NOT NULL DEFAULT '',
  address                               TEXT         NOT NULL DEFAULT '',
  coordinates                           VARCHAR(64)  NOT NULL DEFAULT '',
  lat                                   DOUBLE PRECISION NOT NULL DEFAULT 0,
  lng                                   DOUBLE PRECISION NOT NULL DEFAULT 0,
  established_date                      VARCHAR(32)  NOT NULL DEFAULT '',
  developer                             VARCHAR(512) NOT NULL DEFAULT '',
  website                               VARCHAR(1024) NOT NULL DEFAULT '',

  total_area_ha                         NUMERIC(14, 2) NOT NULL DEFAULT 0,
  developed_area_ha                     NUMERIC(14, 2) NOT NULL DEFAULT 0,
  occupancy_rate                        NUMERIC(6, 2)  NOT NULL DEFAULT 0,
  dominant_industries                   JSONB NOT NULL DEFAULT '[]'::jsonb,
  park_level                            VARCHAR(64) NOT NULL DEFAULT '',
  anchor_tenants                        JSONB NOT NULL DEFAULT '[]'::jsonb,

  land_rent_usd_per_sqm                 NUMERIC(14, 2) NOT NULL DEFAULT 0,
  lease_term_years                      INT NOT NULL DEFAULT 0,
  lease_expiry_year                     INT NOT NULL DEFAULT 0,
  standard_factory_rent_usd_per_sqm_month NUMERIC(14, 2) NOT NULL DEFAULT 0,
  management_fee_usd_per_sqm_year       NUMERIC(14, 2) NOT NULL DEFAULT 0,
  electricity_rate_usd_per_kwh          TEXT NOT NULL DEFAULT '',
  water_rate_usd_per_cum                NUMERIC(14, 4) NOT NULL DEFAULT 0,
  blue_collar_wage_usd                  NUMERIC(14, 2) NOT NULL DEFAULT 0,
  white_collar_wage_usd                 NUMERIC(14, 2) NOT NULL DEFAULT 0,

  cit_incentive                         TEXT NOT NULL DEFAULT '',
  import_tariff_incentive               TEXT NOT NULL DEFAULT '',
  is_sez                                BOOLEAN NOT NULL DEFAULT FALSE,
  special_incentives                    TEXT NOT NULL DEFAULT '',

  distance_to_sea_port_km               NUMERIC(10, 2) NOT NULL DEFAULT 0,
  sea_port_name                         VARCHAR(256) NOT NULL DEFAULT '',
  distance_to_airport_km                NUMERIC(10, 2) NOT NULL DEFAULT 0,
  airport_name                          VARCHAR(256) NOT NULL DEFAULT '',
  distance_to_china_border_km           NUMERIC(10, 2) NOT NULL DEFAULT 0,
  border_port_name                      VARCHAR(256) NOT NULL DEFAULT '',
  power_reliability                     VARCHAR(128) NOT NULL DEFAULT '',
  sewage_treatment_capacity             TEXT NOT NULL DEFAULT '',
  has_employee_dormitory                BOOLEAN NOT NULL DEFAULT FALSE,
  surrounding_amenities                 TEXT NOT NULL DEFAULT '',

  labor_supply                          VARCHAR(32) NOT NULL DEFAULT '',
  recruitment_sources                   TEXT NOT NULL DEFAULT '',
  has_vocational_school                 BOOLEAN NOT NULL DEFAULT FALSE,

  master_plan_url                       VARCHAR(2048),
  brochure_url                          VARCHAR(2048),
  photos                                JSONB NOT NULL DEFAULT '[]'::jsonb,
  data_source                           TEXT NOT NULL DEFAULT '',

  last_updated                          DATE,
  verification_status                   VARCHAR(32) NOT NULL DEFAULT '未核实',
  consultant_contact                    TEXT NOT NULL DEFAULT '',
  contact_page_url                      VARCHAR(2048),
  official_phone                        VARCHAR(256),
  official_email                        VARCHAR(256),
  operating_hours                       VARCHAR(256),

  create_by                             VARCHAR(64),
  create_time                           TIMESTAMP,
  update_by                             VARCHAR(64),
  update_time                           TIMESTAMP,
  del_flag                              CHAR(1) NOT NULL DEFAULT '0'
);

CREATE INDEX IF NOT EXISTS idx_crm_ip_region
  ON public.crm_industrial_park (region)
  WHERE del_flag = '0';

CREATE INDEX IF NOT EXISTS idx_crm_ip_province
  ON public.crm_industrial_park (province)
  WHERE del_flag = '0';

CREATE INDEX IF NOT EXISTS idx_crm_ip_park_level
  ON public.crm_industrial_park (park_level)
  WHERE del_flag = '0';

CREATE INDEX IF NOT EXISTS idx_crm_ip_verification
  ON public.crm_industrial_park (verification_status)
  WHERE del_flag = '0';

CREATE INDEX IF NOT EXISTS idx_crm_ip_lat_lng
  ON public.crm_industrial_park (lat, lng)
  WHERE del_flag = '0';

CREATE INDEX IF NOT EXISTS idx_crm_ip_is_sez
  ON public.crm_industrial_park (is_sez)
  WHERE del_flag = '0';

COMMENT ON TABLE public.crm_industrial_park IS '越南工业园区招商信息库';
COMMENT ON COLUMN public.crm_industrial_park.region IS '北部 | 中部 | 南部';
COMMENT ON COLUMN public.crm_industrial_park.dominant_industries IS 'JSON string array';
COMMENT ON COLUMN public.crm_industrial_park.anchor_tenants IS 'JSON string array';
COMMENT ON COLUMN public.crm_industrial_park.photos IS 'JSON string array of image URLs';
COMMENT ON COLUMN public.crm_industrial_park.verification_status IS '未核实 | AI抓取 | 人工核实';
COMMENT ON COLUMN public.crm_industrial_park.del_flag IS '0 active, 1 soft-deleted';


--
-- 越南工业园区入驻企业：crm_tenant_enterprise
-- 同步维护：scripts/sql/seed-crm-tenant-enterprise-table.sql
-- Logical FK: park_id → crm_industrial_park.id (no hard REFERENCES)
--

CREATE TABLE IF NOT EXISTS public.crm_tenant_enterprise (
  id                      VARCHAR(64) PRIMARY KEY,
  park_id                 VARCHAR(64) NOT NULL,
  park_name_cn            VARCHAR(256) NOT NULL DEFAULT '',
  name                    VARCHAR(512) NOT NULL,
  name_cn                 VARCHAR(512) NOT NULL,
  tax_code                VARCHAR(64),
  legal_representative    VARCHAR(256),
  established_year        INT NOT NULL DEFAULT 0,
  registered_address      TEXT NOT NULL DEFAULT '',
  industry                VARCHAR(256) NOT NULL DEFAULT '',
  enterprise_type         VARCHAR(128) NOT NULL DEFAULT '',
  country_of_origin       VARCHAR(128),
  investment_usd_million  NUMERIC(18, 2),
  employee_count          INT,
  factory_area_sqm        NUMERIC(18, 2),
  status                  VARCHAR(32),
  main_products           TEXT,
  contact_phone           VARCHAR(128),
  contact_email           VARCHAR(256),
  website                 VARCHAR(1024),
  notes                   TEXT,
  created_at              VARCHAR(32),

  create_by               VARCHAR(64),
  create_time             TIMESTAMP,
  update_by               VARCHAR(64),
  update_time             TIMESTAMP,
  del_flag                CHAR(1) NOT NULL DEFAULT '0'
);

-- 已有租户库增量（与 scripts/sql/alter-crm-tenant-enterprise-legal-rep.sql 对齐）
ALTER TABLE public.crm_tenant_enterprise
  ADD COLUMN IF NOT EXISTS legal_representative VARCHAR(256);

CREATE INDEX IF NOT EXISTS idx_crm_te_park_id
  ON public.crm_tenant_enterprise (park_id)
  WHERE del_flag = '0';

CREATE INDEX IF NOT EXISTS idx_crm_te_industry
  ON public.crm_tenant_enterprise (industry)
  WHERE del_flag = '0';

CREATE INDEX IF NOT EXISTS idx_crm_te_enterprise_type
  ON public.crm_tenant_enterprise (enterprise_type)
  WHERE del_flag = '0';

CREATE INDEX IF NOT EXISTS idx_crm_te_country
  ON public.crm_tenant_enterprise (country_of_origin)
  WHERE del_flag = '0';

CREATE INDEX IF NOT EXISTS idx_crm_te_status
  ON public.crm_tenant_enterprise (status)
  WHERE del_flag = '0';

COMMENT ON TABLE public.crm_tenant_enterprise IS '越南工业园区入驻企业';
COMMENT ON COLUMN public.crm_tenant_enterprise.park_id IS '逻辑外键 → crm_industrial_park.id';
COMMENT ON COLUMN public.crm_tenant_enterprise.park_name_cn IS '冗余园区中文名，列表免联表';
COMMENT ON COLUMN public.crm_tenant_enterprise.tax_code IS '企业税务代码 / 越南税号 (MST)';
COMMENT ON COLUMN public.crm_tenant_enterprise.legal_representative IS '法定代表人';
COMMENT ON COLUMN public.crm_tenant_enterprise.enterprise_type IS '外国直接投资 (FDI) | 合资企业 (JV) | 越南本土私人企业 (Private) | 越南国有企业 (SOE) | 跨国集团分支机构';
COMMENT ON COLUMN public.crm_tenant_enterprise.status IS '运营中 | 建设中 | 签约拟建 | 已停止经营活动 | 已注销';
COMMENT ON COLUMN public.crm_tenant_enterprise.del_flag IS '0 active, 1 soft-deleted';


--
-- 赴越中资企业：crm_chinese_enterprise
-- 同步维护：scripts/sql/seed-crm-chinese-enterprise-table.sql
-- Logical FK: park_id → crm_industrial_park.id (no hard REFERENCES)
--

CREATE TABLE IF NOT EXISTS public.crm_chinese_enterprise (
  id                        VARCHAR(64) PRIMARY KEY,
  parent_company_china      VARCHAR(512) NOT NULL,
  stock_code                VARCHAR(64),
  stock_listing_status      VARCHAR(64),
  parent_province_china     VARCHAR(256) NOT NULL DEFAULT '',
  vietnam_entity_name       VARCHAR(512) NOT NULL,
  vietnam_entity_name_cn    VARCHAR(512) NOT NULL,
  tax_code                  VARCHAR(64),
  park_id                   VARCHAR(64) NOT NULL,
  park_name_cn              VARCHAR(256) NOT NULL DEFAULT '',
  province_vietnam          VARCHAR(128) NOT NULL DEFAULT '',
  industry                  VARCHAR(256) NOT NULL DEFAULT '',
  supply_chain_role         TEXT,
  investment_mode           VARCHAR(128),
  investment_usd_million    NUMERIC(18, 2),
  registered_capital_usd_m  NUMERIC(18, 2),
  employee_count            INT,
  factory_area_sqm          NUMERIC(18, 2),
  established_year          INT,
  status                    VARCHAR(32) NOT NULL DEFAULT '运营中',
  main_products             TEXT,
  contact_phone             VARCHAR(128),
  contact_email             VARCHAR(256),
  website                   VARCHAR(1024),
  notes                     TEXT,
  created_at                VARCHAR(32),

  create_by                 VARCHAR(64),
  create_time               TIMESTAMP,
  update_by                 VARCHAR(64),
  update_time               TIMESTAMP,
  del_flag                  CHAR(1) NOT NULL DEFAULT '0'
);

CREATE INDEX IF NOT EXISTS idx_crm_ce_park_id
  ON public.crm_chinese_enterprise (park_id)
  WHERE del_flag = '0';

CREATE INDEX IF NOT EXISTS idx_crm_ce_province_vn
  ON public.crm_chinese_enterprise (province_vietnam)
  WHERE del_flag = '0';

CREATE INDEX IF NOT EXISTS idx_crm_ce_parent_province
  ON public.crm_chinese_enterprise (parent_province_china)
  WHERE del_flag = '0';

CREATE INDEX IF NOT EXISTS idx_crm_ce_industry
  ON public.crm_chinese_enterprise (industry)
  WHERE del_flag = '0';

CREATE INDEX IF NOT EXISTS idx_crm_ce_stock_listing
  ON public.crm_chinese_enterprise (stock_listing_status)
  WHERE del_flag = '0';

CREATE INDEX IF NOT EXISTS idx_crm_ce_investment_mode
  ON public.crm_chinese_enterprise (investment_mode)
  WHERE del_flag = '0';

CREATE INDEX IF NOT EXISTS idx_crm_ce_status
  ON public.crm_chinese_enterprise (status)
  WHERE del_flag = '0';

COMMENT ON TABLE public.crm_chinese_enterprise IS '赴越中资企业（国内母公司 + 越南落地实体）';
COMMENT ON COLUMN public.crm_chinese_enterprise.park_id IS '逻辑外键 → crm_industrial_park.id';
COMMENT ON COLUMN public.crm_chinese_enterprise.park_name_cn IS '冗余园区中文名，列表免联表';
COMMENT ON COLUMN public.crm_chinese_enterprise.stock_listing_status IS 'A股/港股/美股/拟上市/非上市等';
COMMENT ON COLUMN public.crm_chinese_enterprise.investment_mode IS '独资自建/租赁厂房/合资/并购等';
COMMENT ON COLUMN public.crm_chinese_enterprise.status IS '运营中 | 建设中 | 签约拟建';
COMMENT ON COLUMN public.crm_chinese_enterprise.del_flag IS '0 active, 1 soft-deleted';


--
-- 越南政府招商/审批机构目录：crm_government_agency
-- 同步维护：scripts/sql/seed-crm-government-agency-table.sql
--

CREATE TABLE IF NOT EXISTS public.crm_government_agency (
  id                      VARCHAR(64) PRIMARY KEY,
  name_cn                 VARCHAR(512) NOT NULL,
  name_vn                 VARCHAR(512) NOT NULL,
  name_en                 VARCHAR(512),
  abbreviation            VARCHAR(64) NOT NULL DEFAULT '',
  level                   VARCHAR(64) NOT NULL,
  category                VARCHAR(128) NOT NULL,
  province                VARCHAR(256),
  address                 TEXT,
  core_responsibilities   TEXT NOT NULL DEFAULT '',
  approval_items          JSONB NOT NULL DEFAULT '[]'::jsonb,
  one_stop_services       TEXT,
  contact_phone           VARCHAR(128),
  email                   VARCHAR(256),
  website                 VARCHAR(1024),
  working_hours           VARCHAR(256),
  notes                   TEXT,
  created_at              VARCHAR(32),

  create_by               VARCHAR(64),
  create_time             TIMESTAMP,
  update_by               VARCHAR(64),
  update_time             TIMESTAMP,
  del_flag                CHAR(1) NOT NULL DEFAULT '0'
);

CREATE INDEX IF NOT EXISTS idx_crm_ga_level
  ON public.crm_government_agency (level)
  WHERE del_flag = '0';

CREATE INDEX IF NOT EXISTS idx_crm_ga_category
  ON public.crm_government_agency (category)
  WHERE del_flag = '0';

CREATE INDEX IF NOT EXISTS idx_crm_ga_province
  ON public.crm_government_agency (province)
  WHERE del_flag = '0';

COMMENT ON TABLE public.crm_government_agency IS '越南政府招商/审批机构目录';
COMMENT ON COLUMN public.crm_government_agency.level IS '中央部委/级 | 地方省级厅局 | 园区一站式窗口/管理局';
COMMENT ON COLUMN public.crm_government_agency.category IS '职能类别（投资审批/财税/外汇/环保/消防/一站式等）';
COMMENT ON COLUMN public.crm_government_agency.approval_items IS 'JSON string array of approval items';
COMMENT ON COLUMN public.crm_government_agency.del_flag IS '0 active, 1 soft-deleted';


--
-- 越南出海专业服务机构：crm_professional_service
-- 同步维护：scripts/sql/seed-crm-professional-service-table.sql
--

CREATE TABLE IF NOT EXISTS public.crm_professional_service (
  id                      VARCHAR(64) PRIMARY KEY,
  name_cn                 VARCHAR(512) NOT NULL,
  name_vn                 VARCHAR(512) NOT NULL,
  name_en                 VARCHAR(512),
  category                VARCHAR(128) NOT NULL,
  background              VARCHAR(128) NOT NULL,
  province                VARCHAR(256),
  address                 TEXT,
  core_services           JSONB NOT NULL DEFAULT '[]'::jsonb,
  target_clients          TEXT,
  key_capabilities        TEXT NOT NULL DEFAULT '',
  contact_phone           VARCHAR(128),
  email                   VARCHAR(256),
  website                 VARCHAR(1024),
  notes                   TEXT,
  created_at              VARCHAR(32),

  create_by               VARCHAR(64),
  create_time             TIMESTAMP,
  update_by               VARCHAR(64),
  update_time             TIMESTAMP,
  del_flag                CHAR(1) NOT NULL DEFAULT '0'
);

CREATE INDEX IF NOT EXISTS idx_crm_ps_category
  ON public.crm_professional_service (category)
  WHERE del_flag = '0';

CREATE INDEX IF NOT EXISTS idx_crm_ps_background
  ON public.crm_professional_service (background)
  WHERE del_flag = '0';

CREATE INDEX IF NOT EXISTS idx_crm_ps_province
  ON public.crm_professional_service (province)
  WHERE del_flag = '0';

COMMENT ON TABLE public.crm_professional_service IS '越南出海专业服务机构（法律/审计/EPC/物流/金融/商会）';
COMMENT ON COLUMN public.crm_professional_service.category IS '法律服务/会计审计/EPC/物流报关/金融融资/民间商会';
COMMENT ON COLUMN public.crm_professional_service.background IS '国际Big4/第二梯队/本土领军/中资华人/台资/跨国联合等';
COMMENT ON COLUMN public.crm_professional_service.core_services IS 'JSON string array of core service modules';
COMMENT ON COLUMN public.crm_professional_service.del_flag IS '0 active, 1 soft-deleted';


-- 合规风险/制裁主体 + 制裁源标签
-- 同步维护：scripts/sql/seed-crm-compliance-risk-tables.sql
--

CREATE TABLE IF NOT EXISTS public.crm_sanctioned_entity (
  id                        VARCHAR(64) PRIMARY KEY,
  name_cn                   VARCHAR(512) NOT NULL,
  name_vn                   VARCHAR(512) NOT NULL,
  name_en                   VARCHAR(512),
  aka                       JSONB NOT NULL DEFAULT '[]'::jsonb,
  entity_type               VARCHAR(64) NOT NULL,
  tax_code                  VARCHAR(64),
  id_card_or_passport       VARCHAR(128),
  parent_company_or_ubo     TEXT,
  risk_category             VARCHAR(128) NOT NULL,
  risk_reason               TEXT NOT NULL,
  issuing_agency            VARCHAR(512) NOT NULL,
  risk_level                VARCHAR(64) NOT NULL,
  effective_date            VARCHAR(32) NOT NULL,
  expiry_date               VARCHAR(32),
  official_notice_url       VARCHAR(2048),
  matched_enterprise_code   VARCHAR(64),
  notes                     TEXT,
  created_at                VARCHAR(32),

  create_by                 VARCHAR(64),
  create_time               TIMESTAMP,
  update_by                 VARCHAR(64),
  update_time               TIMESTAMP,
  del_flag                  CHAR(1) NOT NULL DEFAULT '0'
);

CREATE INDEX IF NOT EXISTS idx_crm_sanc_risk_category
  ON public.crm_sanctioned_entity (risk_category)
  WHERE del_flag = '0';

CREATE INDEX IF NOT EXISTS idx_crm_sanc_risk_level
  ON public.crm_sanctioned_entity (risk_level)
  WHERE del_flag = '0';

CREATE INDEX IF NOT EXISTS idx_crm_sanc_entity_type
  ON public.crm_sanctioned_entity (entity_type)
  WHERE del_flag = '0';

CREATE INDEX IF NOT EXISTS idx_crm_sanc_tax_code
  ON public.crm_sanctioned_entity (tax_code)
  WHERE del_flag = '0';

COMMENT ON TABLE public.crm_sanctioned_entity IS '合规风险/制裁主体主档';
COMMENT ON COLUMN public.crm_sanctioned_entity.aka IS 'JSON string array of aliases / former names';
COMMENT ON COLUMN public.crm_sanctioned_entity.entity_type IS '企业/个人/政府官员(PEP)/特定组织';
COMMENT ON COLUMN public.crm_sanctioned_entity.risk_level IS '红灯(高危)/黄灯(中危)/绿灯(已解除)';
COMMENT ON COLUMN public.crm_sanctioned_entity.matched_enterprise_code IS '逻辑关联网点码或税号，无强制外键';
COMMENT ON COLUMN public.crm_sanctioned_entity.del_flag IS '0 active, 1 soft-deleted';

CREATE TABLE IF NOT EXISTS public.crm_sanction_source_tag (
  id                        VARCHAR(64) PRIMARY KEY,
  tag_code                  VARCHAR(64) NOT NULL,
  tag_name                  VARCHAR(256) NOT NULL,
  authority                 VARCHAR(256) NOT NULL,
  description               TEXT NOT NULL,

  create_by                 VARCHAR(64),
  create_time               TIMESTAMP,
  update_by                 VARCHAR(64),
  update_time               TIMESTAMP,
  del_flag                  CHAR(1) NOT NULL DEFAULT '0'
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_crm_sst_tag_code_active
  ON public.crm_sanction_source_tag (tag_code)
  WHERE del_flag = '0';

COMMENT ON TABLE public.crm_sanction_source_tag IS '制裁源标签说明字典';
COMMENT ON COLUMN public.crm_sanction_source_tag.tag_code IS '如 OFAC-SDN；活跃行唯一';
COMMENT ON COLUMN public.crm_sanction_source_tag.del_flag IS '0 active, 1 soft-deleted';

-- 市场活动库
-- 同步维护：scripts/sql/seed-crm-market-event-table.sql
-- Logical FK: related_park_id → crm_industrial_park.id (no hard REFERENCES)
--

CREATE TABLE IF NOT EXISTS public.crm_market_event (
  id                  VARCHAR(64) PRIMARY KEY,
  title_cn            VARCHAR(512) NOT NULL,
  title_vn            VARCHAR(512) NOT NULL,
  title_en            VARCHAR(512),
  event_type          VARCHAR(128) NOT NULL,
  status              VARCHAR(64) NOT NULL,
  organizer           VARCHAR(512) NOT NULL,
  industry            VARCHAR(512) NOT NULL DEFAULT '',
  start_date          VARCHAR(32) NOT NULL,
  end_date            VARCHAR(32) NOT NULL,
  province            VARCHAR(256) NOT NULL,
  venue               TEXT NOT NULL DEFAULT '',
  target_audience     TEXT NOT NULL DEFAULT '',
  scale               VARCHAR(512),
  entry_fee           VARCHAR(512),
  registration_link   VARCHAR(2048),
  contact_person      VARCHAR(512),
  highlights          TEXT NOT NULL,
  notes               TEXT,
  related_park_id     VARCHAR(64),

  create_by           VARCHAR(64),
  create_time         TIMESTAMP,
  update_by           VARCHAR(64),
  update_time         TIMESTAMP,
  del_flag            CHAR(1) NOT NULL DEFAULT '0'
);

CREATE INDEX IF NOT EXISTS idx_crm_me_event_type
  ON public.crm_market_event (event_type)
  WHERE del_flag = '0';

CREATE INDEX IF NOT EXISTS idx_crm_me_status
  ON public.crm_market_event (status)
  WHERE del_flag = '0';

CREATE INDEX IF NOT EXISTS idx_crm_me_province
  ON public.crm_market_event (province)
  WHERE del_flag = '0';

CREATE INDEX IF NOT EXISTS idx_crm_me_start_date
  ON public.crm_market_event (start_date)
  WHERE del_flag = '0';

COMMENT ON TABLE public.crm_market_event IS '越南出海市场活动主档';
COMMENT ON COLUMN public.crm_market_event.event_type IS '展会/博览会 (Exhibition) | 招商研讨会/推介会 (Seminar) | 政企对账会 (Dialogue) | 商务考察团 (Trade Mission) | 行业峰会/论坛 (Summit) | 企业开业/投产典礼 (Opening)';
COMMENT ON COLUMN public.crm_market_event.status IS '即将举办 (Upcoming) | 进行中 (Ongoing) | 已结束 (Completed) | 筹备中 (Planning)';
COMMENT ON COLUMN public.crm_market_event.related_park_id IS '逻辑关联园区 ID → crm_industrial_park.id';
COMMENT ON COLUMN public.crm_market_event.del_flag IS '0 active, 1 soft-deleted';


-- Follow-up rules (event -> delayed CRM task)
CREATE TABLE IF NOT EXISTS public.crm_followup_rule (
  rule_id           VARCHAR(36)  NOT NULL PRIMARY KEY,
  tenant_id         VARCHAR(64)  NOT NULL,
  name              VARCHAR(128) NOT NULL,
  description       VARCHAR(500),
  enabled           CHAR(1)      NOT NULL DEFAULT '1',
  is_system         CHAR(1)      NOT NULL DEFAULT '0',
  priority          INT          NOT NULL DEFAULT 100,
  trigger_type      VARCHAR(64)  NOT NULL,
  trigger_config    TEXT,
  delay_mode        VARCHAR(16)  NOT NULL DEFAULT 'immediate',
  delay_value       INT,
  delay_unit        VARCHAR(8),
  delay_field       VARCHAR(64),
  task_template     TEXT         NOT NULL,
  dedupe_config     TEXT,
  workflow_id       VARCHAR(36),
  catalog_key       VARCHAR(64),
  create_by         VARCHAR(64),
  create_time       TIMESTAMP,
  update_by         VARCHAR(64),
  update_time       TIMESTAMP,
  del_flag          CHAR(1)      NOT NULL DEFAULT '0'
);

CREATE INDEX IF NOT EXISTS idx_followup_rule_tenant_enabled
  ON public.crm_followup_rule (tenant_id, enabled, trigger_type)
  WHERE del_flag = '0';

COMMENT ON TABLE public.crm_followup_rule IS 'Tenant follow-up rules: event -> delayed task creation';

CREATE TABLE IF NOT EXISTS public.crm_followup_rule_enrollment (
  enrollment_id     VARCHAR(36)  NOT NULL PRIMARY KEY,
  tenant_id         VARCHAR(64)  NOT NULL,
  rule_id           VARCHAR(36)  NOT NULL,
  trigger_type      VARCHAR(64)  NOT NULL,
  subject_type      VARCHAR(32)  NOT NULL,
  subject_id        VARCHAR(100) NOT NULL,
  status            VARCHAR(16)  NOT NULL DEFAULT 'pending',
  next_run_at       TIMESTAMPTZ  NOT NULL,
  idempotency_key   VARCHAR(64)  NOT NULL,
  trigger_payload   TEXT,
  task_id           VARCHAR(100),
  skip_reason       VARCHAR(128),
  cancel_reason     VARCHAR(128),
  retry_count       INT          NOT NULL DEFAULT 0,
  fired_time        TIMESTAMP,
  create_time       TIMESTAMP,
  update_time       TIMESTAMP,
  CONSTRAINT uk_followup_enrollment_idempotency UNIQUE (tenant_id, idempotency_key)
);

CREATE INDEX IF NOT EXISTS idx_followup_enrollment_due
  ON public.crm_followup_rule_enrollment (tenant_id, status, next_run_at)
  WHERE status = 'pending';

CREATE INDEX IF NOT EXISTS idx_followup_enrollment_subject
  ON public.crm_followup_rule_enrollment (tenant_id, subject_type, subject_id, status);

COMMENT ON TABLE public.crm_followup_rule_enrollment IS 'Delayed follow-up rule executions';

CREATE TABLE IF NOT EXISTS public.crm_followup_rule_execution_log (
  execution_id      VARCHAR(36)  NOT NULL PRIMARY KEY,
  tenant_id         VARCHAR(64)  NOT NULL,
  enrollment_id     VARCHAR(36),
  rule_id           VARCHAR(36)  NOT NULL,
  action            VARCHAR(16)  NOT NULL,
  task_id           VARCHAR(100),
  skip_reason       VARCHAR(128),
  detail_json       TEXT,
  create_time       TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_followup_exec_log_rule
  ON public.crm_followup_rule_execution_log (tenant_id, rule_id, create_time DESC);

-- Recall policies (won opportunity -> next_due_at schedule)
CREATE TABLE IF NOT EXISTS public.crm_recall_policy (
  policy_id         VARCHAR(36)  NOT NULL PRIMARY KEY,
  tenant_id         VARCHAR(64)  NOT NULL,
  name              VARCHAR(128) NOT NULL,
  description       VARCHAR(500),
  enabled           CHAR(1)      NOT NULL DEFAULT '1',
  is_system         CHAR(1)      NOT NULL DEFAULT '0',
  priority          INT          NOT NULL DEFAULT 100,
  match_json        TEXT,
  interval_value    INT          NOT NULL,
  interval_unit     VARCHAR(8)   NOT NULL DEFAULT 'days',
  del_flag          CHAR(1)      NOT NULL DEFAULT '0',
  create_by         VARCHAR(64),
  create_time       TIMESTAMP,
  update_by         VARCHAR(64),
  update_time       TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_recall_policy_tenant_enabled
  ON public.crm_recall_policy (tenant_id, enabled, priority)
  WHERE del_flag = '0';

CREATE TABLE IF NOT EXISTS public.crm_recall_schedule (
  schedule_id           VARCHAR(36)  NOT NULL PRIMARY KEY,
  tenant_id             VARCHAR(64)  NOT NULL,
  contact_id            VARCHAR(100) NOT NULL,
  policy_id             VARCHAR(36)  NOT NULL,
  anchor_type           VARCHAR(32)  NOT NULL,
  anchor_id             VARCHAR(100),
  treatment_item_code   VARCHAR(64),
  next_due_at           TIMESTAMPTZ  NOT NULL,
  status                VARCHAR(16)  NOT NULL DEFAULT 'scheduled',
  last_fired_at         TIMESTAMP,
  create_time           TIMESTAMP,
  update_time           TIMESTAMP,
  CONSTRAINT uk_recall_schedule_contact_policy UNIQUE (tenant_id, contact_id, policy_id)
);

CREATE INDEX IF NOT EXISTS idx_recall_schedule_due
  ON public.crm_recall_schedule (tenant_id, status, next_due_at)
  WHERE status = 'scheduled';


-- Inbox mobile push device registry (FCM tokens)
CREATE TABLE IF NOT EXISTS public.crm_mobile_push_device (
  id           BIGSERIAL PRIMARY KEY,
  tenant_id    VARCHAR(64)   NOT NULL,
  user_id      BIGINT        NOT NULL,
  platform     VARCHAR(16)   NOT NULL,
  push_token   VARCHAR(512)  NOT NULL,
  app_version  VARCHAR(64),
  create_time  TIMESTAMP,
  update_time  TIMESTAMP,
  CONSTRAINT uk_crm_mobile_push_device_token UNIQUE (tenant_id, push_token)
);

CREATE INDEX IF NOT EXISTS idx_crm_mobile_push_device_user
  ON public.crm_mobile_push_device (tenant_id, user_id);

COMMENT ON TABLE public.crm_mobile_push_device IS 'Inbox mobile push device tokens (FCM)';


-- Clinic visit booking (上门预约), separate from meeting crm_appointment
CREATE TABLE IF NOT EXISTS public.crm_booking (
  booking_id     varchar(100) NOT NULL,
  lead_id        varchar(100),
  contact_id     varchar(100),
  org_id         varchar(100),
  booking_name   varchar(200),
  start_time     timestamptz NOT NULL,
  end_time       timestamptz NOT NULL,
  status         varchar(32)  NOT NULL DEFAULT 'scheduled',
  source         varchar(64),
  source_ref     varchar(200),
  mobile_phone   varchar(50),
  notes          text,
  owner          varchar(100),
  create_by      varchar(64),
  create_time    timestamp without time zone,
  update_by      varchar(64),
  update_time    timestamp without time zone,
  user_id        bigint,
  dept_id        bigint,
  CONSTRAINT crm_booking_pkey PRIMARY KEY (booking_id),
  CONSTRAINT crm_booking_subject_chk CHECK (
    lead_id IS NOT NULL OR contact_id IS NOT NULL
  ),
  CONSTRAINT crm_booking_time_chk CHECK (end_time > start_time)
);

CREATE INDEX IF NOT EXISTS idx_crm_booking_start
  ON public.crm_booking (start_time);
CREATE INDEX IF NOT EXISTS idx_crm_booking_org_start
  ON public.crm_booking (org_id, start_time);
CREATE INDEX IF NOT EXISTS idx_crm_booking_lead
  ON public.crm_booking (lead_id);
CREATE INDEX IF NOT EXISTS idx_crm_booking_contact
  ON public.crm_booking (contact_id);
CREATE INDEX IF NOT EXISTS idx_crm_booking_status_start
  ON public.crm_booking (status, start_time);

COMMENT ON TABLE public.crm_booking IS '诊所上门预约（与会议 crm_appointment 分离）';

-- Unified Inbox channel enable switches per tenant
CREATE TABLE IF NOT EXISTS public.crm_inbox_channel_settings (
  tenant_id           varchar(100) NOT NULL,
  whatsapp_enabled    smallint     NOT NULL DEFAULT 1,
  messenger_enabled   smallint     NOT NULL DEFAULT 1,
  wechat_mp_enabled   smallint     NOT NULL DEFAULT 1,
  webchat_enabled     smallint     NOT NULL DEFAULT 1,
  wecom_kf_enabled    smallint     NOT NULL DEFAULT 1,
  updated_by          varchar(64),
  updated_at          timestamp without time zone,
  CONSTRAINT crm_inbox_channel_settings_pkey PRIMARY KEY (tenant_id)
);

COMMENT ON TABLE public.crm_inbox_channel_settings IS 'Unified Inbox channel enable switches per tenant';

ALTER TABLE public.crm_inbox_channel_settings
    ADD COLUMN IF NOT EXISTS wecom_kf_enabled smallint NOT NULL DEFAULT 1;


-- WeCom KF Inbox conversation + sync cursor
CREATE TABLE IF NOT EXISTS public.crm_wecom_kf_conversation (
    id                          BIGSERIAL       PRIMARY KEY,
    corp_id                     VARCHAR(64)     NOT NULL,
    open_kfid                   VARCHAR(64)     NOT NULL,
    external_userid             VARCHAR(64)     NOT NULL,
    nickname                    VARCHAR(128),
    avatar_url                  TEXT,
    account_name                VARCHAR(128),
    last_message                VARCHAR(512),
    last_message_at             TIMESTAMP,
    message_count               INT             NOT NULL DEFAULT 0,
    unread_count                INT             NOT NULL DEFAULT 0,
    can_reply                   SMALLINT        NOT NULL DEFAULT 1,
    user_id                     BIGINT,
    owner                       VARCHAR(128),
    assigned_at                 TIMESTAMP,
    contact_id                  VARCHAR(64),
    contact_name                VARCHAR(128),
    create_time                 TIMESTAMP       DEFAULT now(),
    update_time                 TIMESTAMP       DEFAULT now(),
    CONSTRAINT uk_wecom_kf_conversation_kfid_userid UNIQUE (open_kfid, external_userid)
);

CREATE INDEX IF NOT EXISTS idx_kf_conv_user_last
    ON public.crm_wecom_kf_conversation (user_id, last_message_at DESC NULLS LAST);
CREATE INDEX IF NOT EXISTS idx_kf_conv_pool_last
    ON public.crm_wecom_kf_conversation (last_message_at DESC NULLS LAST)
    WHERE user_id IS NULL;
CREATE INDEX IF NOT EXISTS idx_kf_conv_corp_last
    ON public.crm_wecom_kf_conversation (corp_id, last_message_at DESC NULLS LAST);
CREATE INDEX IF NOT EXISTS idx_kf_conv_open_kfid_last
    ON public.crm_wecom_kf_conversation (open_kfid, last_message_at DESC NULLS LAST);
CREATE INDEX IF NOT EXISTS idx_kf_conv_contact
    ON public.crm_wecom_kf_conversation (contact_id)
    WHERE contact_id IS NOT NULL;

CREATE TABLE IF NOT EXISTS public.crm_wecom_kf_sync_cursor (
    id                          BIGSERIAL       PRIMARY KEY,
    corp_id                     VARCHAR(64)     NOT NULL,
    open_kfid                   VARCHAR(64)     NOT NULL DEFAULT '',
    cursor                      TEXT,
    enabled_at                  TIMESTAMP       NOT NULL DEFAULT now(),
    updated_at                  TIMESTAMP       DEFAULT now(),
    CONSTRAINT uk_wecom_kf_sync_cursor UNIQUE (corp_id, open_kfid)
);

ALTER TABLE public.crm_wecom_kf_message
    ADD COLUMN IF NOT EXISTS conversation_id BIGINT;
CREATE INDEX IF NOT EXISTS idx_kf_msg_conversation
    ON public.crm_wecom_kf_message (conversation_id)
    WHERE conversation_id IS NOT NULL;

ALTER TABLE public.crm_wecom_kf_message
    ADD COLUMN IF NOT EXISTS media_id VARCHAR(128);
ALTER TABLE public.crm_wecom_kf_message
    ADD COLUMN IF NOT EXISTS pic_url TEXT;
CREATE INDEX IF NOT EXISTS idx_kf_msg_media_id
    ON public.crm_wecom_kf_message (media_id)
    WHERE media_id IS NOT NULL;


-- WeCom one-contact-one-group + group reminders
-- PostgreSQL: WeCom one-contact-one-group + provision task + group reminders
-- Run on each tenant database.

CREATE TABLE IF NOT EXISTS public.crm_wecom_contact_group (
  id                  varchar(36)  NOT NULL,
  tenant_id           varchar(64)  NOT NULL,
  contact_id          varchar(100) NOT NULL,
  chat_id             varchar(64),
  owner_userid        varchar(64)  NOT NULL,
  group_name          varchar(128),
  status              varchar(32)  NOT NULL DEFAULT 'pending',
  provision_task_id   varchar(36),
  bot_activated_at    timestamptz,
  last_error          varchar(500),
  create_time         timestamp without time zone,
  update_time         timestamp without time zone,
  CONSTRAINT crm_wecom_contact_group_pkey PRIMARY KEY (id),
  CONSTRAINT uk_crm_wecom_contact_group_contact UNIQUE (tenant_id, contact_id)
);

CREATE INDEX IF NOT EXISTS idx_crm_wecom_cg_status
  ON public.crm_wecom_contact_group (tenant_id, status);
CREATE INDEX IF NOT EXISTS idx_crm_wecom_cg_chat
  ON public.crm_wecom_contact_group (tenant_id, chat_id)
  WHERE chat_id IS NOT NULL;

COMMENT ON TABLE public.crm_wecom_contact_group IS '企微一客一群绑定与状态机';

CREATE TABLE IF NOT EXISTS public.crm_wecom_group_provision_task (
  task_id               varchar(36)  NOT NULL,
  tenant_id             varchar(64)  NOT NULL,
  contact_id            varchar(100) NOT NULL,
  assignee_userid       varchar(64)  NOT NULL,
  assignee_crm_user_id  varchar(64),
  status                varchar(16)  NOT NULL DEFAULT 'pending',
  notified_at           timestamptz,
  remind_count          int          NOT NULL DEFAULT 0,
  expires_at            timestamptz,
  create_time           timestamp without time zone,
  update_time           timestamp without time zone,
  CONSTRAINT crm_wecom_group_provision_task_pkey PRIMARY KEY (task_id)
);

CREATE INDEX IF NOT EXISTS idx_crm_wecom_provision_assignee
  ON public.crm_wecom_group_provision_task (tenant_id, status, assignee_userid);

COMMENT ON TABLE public.crm_wecom_group_provision_task IS '企微服务群建群任务（员工 H5 一键建群）';

CREATE TABLE IF NOT EXISTS public.crm_wecom_group_reminder_rule (
  rule_id               varchar(36)  NOT NULL,
  tenant_id             varchar(64)  NOT NULL,
  name                  varchar(128) NOT NULL,
  description           varchar(500),
  enabled               char(1)      NOT NULL DEFAULT '1',
  is_system             char(1)      NOT NULL DEFAULT '0',
  priority              int          NOT NULL DEFAULT 100,
  anchor_type           varchar(32)  NOT NULL,
  offset_value          int          NOT NULL DEFAULT 0,
  offset_unit           varchar(8)   NOT NULL DEFAULT 'days',
  send_time             varchar(8),
  template_body         text         NOT NULL,
  match_json            text,
  require_active_group  char(1)      NOT NULL DEFAULT '1',
  del_flag              char(1)      NOT NULL DEFAULT '0',
  create_by             varchar(64),
  create_time           timestamp without time zone,
  update_by             varchar(64),
  update_time           timestamp without time zone,
  CONSTRAINT crm_wecom_group_reminder_rule_pkey PRIMARY KEY (rule_id)
);

CREATE INDEX IF NOT EXISTS idx_crm_wecom_reminder_rule_anchor
  ON public.crm_wecom_group_reminder_rule (tenant_id, enabled, anchor_type)
  WHERE del_flag = '0';

COMMENT ON TABLE public.crm_wecom_group_reminder_rule IS '企微客户群提醒规则（固定话术）';

CREATE TABLE IF NOT EXISTS public.crm_wecom_group_reminder (
  id                varchar(36)  NOT NULL,
  tenant_id         varchar(64)  NOT NULL,
  contact_id        varchar(100) NOT NULL,
  rule_id           varchar(36)  NOT NULL,
  source_ref        varchar(128) NOT NULL,
  chat_id           varchar(64),
  due_at            timestamptz  NOT NULL,
  status            varchar(16)  NOT NULL DEFAULT 'pending',
  payload_json      text,
  rendered_text     text,
  errcode           int,
  errmsg            varchar(500),
  retry_count       int          NOT NULL DEFAULT 0,
  sent_at           timestamptz,
  skipped_reason    varchar(200),
  claim_lease_until timestamptz,
  create_time       timestamp without time zone,
  update_time       timestamp without time zone,
  CONSTRAINT crm_wecom_group_reminder_pkey PRIMARY KEY (id),
  CONSTRAINT uk_crm_wecom_group_reminder UNIQUE (tenant_id, contact_id, rule_id, source_ref)
);

CREATE INDEX IF NOT EXISTS idx_crm_wecom_reminder_due
  ON public.crm_wecom_group_reminder (tenant_id, status, due_at)
  WHERE status = 'pending';

COMMENT ON TABLE public.crm_wecom_group_reminder IS '企微客户群提醒物化待发队列';

-- Treatment item dict (crm_opty.treatment_item_code / recall policy match key)
DO $$
DECLARE
  seqname text;
BEGIN
  seqname := pg_get_serial_sequence('sys_dict_type', 'dict_id');
  IF seqname IS NOT NULL THEN
    EXECUTE format('SELECT setval(%L, (SELECT COALESCE(MAX(dict_id), 0) FROM sys_dict_type))', seqname);
  END IF;
  seqname := pg_get_serial_sequence('sys_dict_data', 'dict_code');
  IF seqname IS NOT NULL THEN
    EXECUTE format('SELECT setval(%L, (SELECT COALESCE(MAX(dict_code), 0) FROM sys_dict_data))', seqname);
  END IF;
END $$;

INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '治疗项目', 'crm_treatment_item', '0', 'admin', CURRENT_TIMESTAMP,
       'Opportunity treatment item; recall policy match key'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'crm_treatment_item');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark, dict_label_en)
SELECT 10, '种植牙', 'implant', 'crm_treatment_item', '', 'default', 'N', '0', 'admin', CURRENT_TIMESTAMP, NULL, 'Implant'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'crm_treatment_item' AND dict_value = 'implant');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark, dict_label_en)
SELECT 20, '洁牙/预防', 'preventive', 'crm_treatment_item', '', 'default', 'N', '0', 'admin', CURRENT_TIMESTAMP, NULL, 'Preventive / cleaning'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'crm_treatment_item' AND dict_value = 'preventive');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark, dict_label_en)
SELECT 30, '补牙/修复', 'restorative', 'crm_treatment_item', '', 'default', 'N', '0', 'admin', CURRENT_TIMESTAMP, NULL, 'Restorative'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'crm_treatment_item' AND dict_value = 'restorative');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark, dict_label_en)
SELECT 40, '正畸', 'ortho', 'crm_treatment_item', '', 'default', 'N', '0', 'admin', CURRENT_TIMESTAMP, NULL, 'Orthodontics'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'crm_treatment_item' AND dict_value = 'ortho');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark, dict_label_en)
SELECT 50, '儿牙', 'pediatric', 'crm_treatment_item', '', 'default', 'N', '0', 'admin', CURRENT_TIMESTAMP, NULL, 'Pediatric'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'crm_treatment_item' AND dict_value = 'pediatric');



-- =============================================================================
-- 微信公众号图文留言 + 半自动回复规则（inbox 侧栏 lead_id）
-- =============================================================================

CREATE TABLE IF NOT EXISTS crm_wechat_mp_article_comment (
  id                bigserial PRIMARY KEY,
  app_id            varchar(64)  NOT NULL,
  msg_data_id       varchar(64)  NOT NULL,
  index_in_msg      int          NOT NULL DEFAULT 0,
  article_id        varchar(128) NULL,
  article_title     varchar(512) NULL,
  article_url       varchar(1024) NULL,
  user_comment_id   bigint       NOT NULL,
  openid            varchar(128) NULL,
  content           text         NOT NULL,
  comment_type      smallint     NOT NULL DEFAULT 0,
  comment_time      timestamptz  NOT NULL,
  reply_content     text         NULL,
  reply_time        timestamptz  NULL,
  status            varchar(16)  NOT NULL DEFAULT 'pending',
  is_read           boolean      NOT NULL DEFAULT false,
  contact_id        varchar(100) NULL,
  lead_id           varchar(100) NULL,
  review_required   boolean      NOT NULL DEFAULT false,
  auto_reply_rule_id bigint      NULL,
  synced_at         timestamptz  NOT NULL DEFAULT now(),
  created_at        timestamptz  NOT NULL DEFAULT now(),
  updated_at        timestamptz  NOT NULL DEFAULT now(),
  CONSTRAINT uk_mp_article_comment
    UNIQUE (app_id, msg_data_id, index_in_msg, user_comment_id)
);

COMMENT ON TABLE crm_wechat_mp_article_comment IS '微信公众号图文留言（comment/list 同步）';
COMMENT ON COLUMN crm_wechat_mp_article_comment.comment_type IS '0 普通 1 精选（微信 comment_type）';
COMMENT ON COLUMN crm_wechat_mp_article_comment.status IS 'pending | replied';
COMMENT ON COLUMN crm_wechat_mp_article_comment.user_comment_id IS '微信侧留言 ID';
COMMENT ON COLUMN crm_wechat_mp_article_comment.lead_id IS '侧栏关联线索 ID';

CREATE INDEX IF NOT EXISTS idx_mp_article_comment_list
  ON crm_wechat_mp_article_comment (comment_time DESC);

CREATE INDEX IF NOT EXISTS idx_mp_article_comment_status
  ON crm_wechat_mp_article_comment (status, comment_time DESC);

CREATE INDEX IF NOT EXISTS idx_mp_article_comment_app
  ON crm_wechat_mp_article_comment (app_id, comment_time DESC);

CREATE INDEX IF NOT EXISTS idx_mp_article_comment_lead
  ON crm_wechat_mp_article_comment (lead_id)
  WHERE lead_id IS NOT NULL;

CREATE TABLE IF NOT EXISTS crm_wechat_mp_comment_auto_reply_rule (
  id              bigserial PRIMARY KEY,
  app_id          varchar(64)  NULL,
  keyword         varchar(256) NOT NULL,
  match_mode      varchar(16)  NOT NULL DEFAULT 'contains',
  reply_content   text         NOT NULL,
  enabled         boolean      NOT NULL DEFAULT true,
  priority        int          NOT NULL DEFAULT 100,
  require_review  boolean      NOT NULL DEFAULT false,
  created_at      timestamptz  NOT NULL DEFAULT now(),
  updated_at      timestamptz  NOT NULL DEFAULT now()
);

COMMENT ON TABLE crm_wechat_mp_comment_auto_reply_rule IS '图文留言半自动回复规则（与私信自动回复表隔离）';

CREATE INDEX IF NOT EXISTS idx_mp_comment_auto_reply_enabled
  ON crm_wechat_mp_comment_auto_reply_rule (enabled, priority ASC);

REVOKE USAGE ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO PUBLIC;


--

