--
-- PostgreSQL database dump
--

-- Dumped from database version 14.18 (Homebrew)
-- Dumped by pg_dump version 17.0

-- Started on 2026-10-19 20:44:36 CST

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 6 (class 2615 OID 360062)
-- Name: public; Type: SCHEMA; Schema: -; Owner: _tenant
--

-- *not* creating schema, since initdb creates it


ALTER SCHEMA public OWNER TO "_tenant";

--
-- TOC entry 7790 (class 0 OID 0)
-- Dependencies: 6
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: _tenant
--

COMMENT ON SCHEMA public IS '';


--
-- TOC entry 2 (class 3079 OID 409613)
-- Name: vector; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS vector WITH SCHEMA public;


--
-- TOC entry 7792 (class 0 OID 0)
-- Dependencies: 2
-- Name: EXTENSION vector; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION vector IS 'vector data type and ivfflat and hnsw access methods';


--
-- TOC entry 1979 (class 1247 OID 370878)
-- Name: CredentialType; Type: TYPE; Schema: public; Owner: _tenant
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


ALTER TYPE public."CredentialType" OWNER TO "_tenant";

--
-- TOC entry 1982 (class 1247 OID 370892)
-- Name: EdgeType; Type: TYPE; Schema: public; Owner: _tenant
--

CREATE TYPE public."EdgeType" AS ENUM (
    'default',
    'IF',
    'ELSE'
);


ALTER TYPE public."EdgeType" OWNER TO "_tenant";

--
-- TOC entry 1985 (class 1247 OID 370900)
-- Name: ExecutionStatus; Type: TYPE; Schema: public; Owner: _tenant
--

CREATE TYPE public."ExecutionStatus" AS ENUM (
    'RUNNING',
    'SUCCESS',
    'FAILED'
);


ALTER TYPE public."ExecutionStatus" OWNER TO "_tenant";

--
-- TOC entry 1988 (class 1247 OID 370908)
-- Name: LlmProvider; Type: TYPE; Schema: public; Owner: _tenant
--

CREATE TYPE public."LlmProvider" AS ENUM (
    'qwen',
    'gemini'
);


ALTER TYPE public."LlmProvider" OWNER TO "_tenant";

--
-- TOC entry 1991 (class 1247 OID 370914)
-- Name: NodeType; Type: TYPE; Schema: public; Owner: _tenant
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
    'LEAD_CONVERT_CONTACT',
    'WECOM_SIDEBAR_DELIVER',
    'CRM_EVENT_TRIGGER',
    'FEISHU_BITABLE',
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
    'CAPTCHA_SOLVE',
    'BROWSER_EXTRACT_HTML'
);


ALTER TYPE public."NodeType" OWNER TO "_tenant";

--
-- TOC entry 1994 (class 1247 OID 370990)
-- Name: TaskStatus; Type: TYPE; Schema: public; Owner: _tenant
--

CREATE TYPE public."TaskStatus" AS ENUM (
    'PENDING',
    'PROCESSING',
    'SUCCESS',
    'FAILED'
);


ALTER TYPE public."TaskStatus" OWNER TO "_tenant";

--
-- TOC entry 787 (class 1255 OID 411101)
-- Name: crm_tag_ids(text); Type: FUNCTION; Schema: public; Owner: luohd
--

CREATE FUNCTION public.crm_tag_ids(tag text) RETURNS text[]
    LANGUAGE plpgsql IMMUTABLE
    AS $$
BEGIN
  IF tag IS NULL OR btrim(tag) = '' OR lower(btrim(tag)) = 'null' THEN
    RETURN '{}';
  END IF;
  RETURN ARRAY(
    SELECT e->>'id'
    FROM jsonb_array_elements(tag::jsonb) AS e
    WHERE e->>'id' IS NOT NULL AND btrim(e->>'id') <> ''
  );
EXCEPTION WHEN others THEN
  RETURN '{}';
END;
$$;


ALTER FUNCTION public.crm_tag_ids(tag text) OWNER TO luohd;

--
-- TOC entry 7793 (class 0 OID 0)
-- Dependencies: 787
-- Name: FUNCTION crm_tag_ids(tag text); Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON FUNCTION public.crm_tag_ids(tag text) IS 'CRM tag JSON → dict_value id 数组；非法 JSON/非数组返回空数组';


--
-- TOC entry 661 (class 1255 OID 404138)
-- Name: fn_crm_identity_update_time(); Type: FUNCTION; Schema: public; Owner: luohd
--

CREATE FUNCTION public.fn_crm_identity_update_time() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
  NEW.update_time = now();
  RETURN NEW;
END;
$$;


ALTER FUNCTION public.fn_crm_identity_update_time() OWNER TO luohd;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 376 (class 1259 OID 369428)
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


ALTER TABLE public.act_evt_log OWNER TO luohd;

--
-- TOC entry 377 (class 1259 OID 369434)
-- Name: act_evt_log_log_nr__seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.act_evt_log_log_nr__seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.act_evt_log_log_nr__seq OWNER TO luohd;

--
-- TOC entry 7795 (class 0 OID 0)
-- Dependencies: 377
-- Name: act_evt_log_log_nr__seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.act_evt_log_log_nr__seq OWNED BY public.act_evt_log.log_nr_;


--
-- TOC entry 378 (class 1259 OID 369435)
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


ALTER TABLE public.act_ge_bytearray OWNER TO luohd;

--
-- TOC entry 379 (class 1259 OID 369440)
-- Name: act_ge_property; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_ge_property (
    name_ character varying(64) NOT NULL,
    value_ character varying(300),
    rev_ integer
);


ALTER TABLE public.act_ge_property OWNER TO luohd;

--
-- TOC entry 380 (class 1259 OID 369443)
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


ALTER TABLE public.act_hi_actinst OWNER TO luohd;

--
-- TOC entry 381 (class 1259 OID 369450)
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


ALTER TABLE public.act_hi_attachment OWNER TO luohd;

--
-- TOC entry 382 (class 1259 OID 369455)
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


ALTER TABLE public.act_hi_comment OWNER TO luohd;

--
-- TOC entry 383 (class 1259 OID 369460)
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


ALTER TABLE public.act_hi_detail OWNER TO luohd;

--
-- TOC entry 384 (class 1259 OID 369465)
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


ALTER TABLE public.act_hi_entitylink OWNER TO luohd;

--
-- TOC entry 385 (class 1259 OID 369470)
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


ALTER TABLE public.act_hi_identitylink OWNER TO luohd;

--
-- TOC entry 386 (class 1259 OID 369475)
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


ALTER TABLE public.act_hi_procinst OWNER TO luohd;

--
-- TOC entry 387 (class 1259 OID 369482)
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


ALTER TABLE public.act_hi_taskinst OWNER TO luohd;

--
-- TOC entry 388 (class 1259 OID 369489)
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


ALTER TABLE public.act_hi_tsk_log OWNER TO luohd;

--
-- TOC entry 389 (class 1259 OID 369495)
-- Name: act_hi_tsk_log_id__seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.act_hi_tsk_log_id__seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.act_hi_tsk_log_id__seq OWNER TO luohd;

--
-- TOC entry 7808 (class 0 OID 0)
-- Dependencies: 389
-- Name: act_hi_tsk_log_id__seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.act_hi_tsk_log_id__seq OWNED BY public.act_hi_tsk_log.id_;


--
-- TOC entry 390 (class 1259 OID 369496)
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


ALTER TABLE public.act_hi_varinst OWNER TO luohd;

--
-- TOC entry 391 (class 1259 OID 369502)
-- Name: act_id_bytearray; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_id_bytearray (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    name_ character varying(255),
    bytes_ bytea
);


ALTER TABLE public.act_id_bytearray OWNER TO luohd;

--
-- TOC entry 392 (class 1259 OID 369507)
-- Name: act_id_group; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_id_group (
    id_ character varying(64) NOT NULL,
    rev_ integer,
    name_ character varying(255),
    type_ character varying(255)
);


ALTER TABLE public.act_id_group OWNER TO luohd;

--
-- TOC entry 393 (class 1259 OID 369512)
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


ALTER TABLE public.act_id_info OWNER TO luohd;

--
-- TOC entry 394 (class 1259 OID 369517)
-- Name: act_id_membership; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_id_membership (
    user_id_ character varying(64) NOT NULL,
    group_id_ character varying(64) NOT NULL
);


ALTER TABLE public.act_id_membership OWNER TO luohd;

--
-- TOC entry 395 (class 1259 OID 369520)
-- Name: act_id_priv; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_id_priv (
    id_ character varying(64) NOT NULL,
    name_ character varying(255) NOT NULL
);


ALTER TABLE public.act_id_priv OWNER TO luohd;

--
-- TOC entry 396 (class 1259 OID 369523)
-- Name: act_id_priv_mapping; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_id_priv_mapping (
    id_ character varying(64) NOT NULL,
    priv_id_ character varying(64) NOT NULL,
    user_id_ character varying(255),
    group_id_ character varying(255)
);


ALTER TABLE public.act_id_priv_mapping OWNER TO luohd;

--
-- TOC entry 397 (class 1259 OID 369528)
-- Name: act_id_property; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_id_property (
    name_ character varying(64) NOT NULL,
    value_ character varying(300),
    rev_ integer
);


ALTER TABLE public.act_id_property OWNER TO luohd;

--
-- TOC entry 398 (class 1259 OID 369531)
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


ALTER TABLE public.act_id_token OWNER TO luohd;

--
-- TOC entry 399 (class 1259 OID 369536)
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


ALTER TABLE public.act_id_user OWNER TO luohd;

--
-- TOC entry 400 (class 1259 OID 369542)
-- Name: act_procdef_info; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_procdef_info (
    id_ character varying(64) NOT NULL,
    proc_def_id_ character varying(64) NOT NULL,
    rev_ integer,
    info_json_id_ character varying(64)
);


ALTER TABLE public.act_procdef_info OWNER TO luohd;

--
-- TOC entry 401 (class 1259 OID 369545)
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


ALTER TABLE public.act_re_deployment OWNER TO luohd;

--
-- TOC entry 402 (class 1259 OID 369551)
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


ALTER TABLE public.act_re_model OWNER TO luohd;

--
-- TOC entry 403 (class 1259 OID 369557)
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


ALTER TABLE public.act_re_procdef OWNER TO luohd;

--
-- TOC entry 404 (class 1259 OID 369564)
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


ALTER TABLE public.act_ru_actinst OWNER TO luohd;

--
-- TOC entry 405 (class 1259 OID 369571)
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


ALTER TABLE public.act_ru_deadletter_job OWNER TO luohd;

--
-- TOC entry 406 (class 1259 OID 369577)
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


ALTER TABLE public.act_ru_entitylink OWNER TO luohd;

--
-- TOC entry 407 (class 1259 OID 369582)
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


ALTER TABLE public.act_ru_event_subscr OWNER TO luohd;

--
-- TOC entry 408 (class 1259 OID 369588)
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


ALTER TABLE public.act_ru_execution OWNER TO luohd;

--
-- TOC entry 409 (class 1259 OID 369594)
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


ALTER TABLE public.act_ru_external_job OWNER TO luohd;

--
-- TOC entry 410 (class 1259 OID 369600)
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


ALTER TABLE public.act_ru_history_job OWNER TO luohd;

--
-- TOC entry 411 (class 1259 OID 369606)
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


ALTER TABLE public.act_ru_identitylink OWNER TO luohd;

--
-- TOC entry 412 (class 1259 OID 369611)
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


ALTER TABLE public.act_ru_job OWNER TO luohd;

--
-- TOC entry 413 (class 1259 OID 369617)
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


ALTER TABLE public.act_ru_suspended_job OWNER TO luohd;

--
-- TOC entry 414 (class 1259 OID 369623)
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


ALTER TABLE public.act_ru_task OWNER TO luohd;

--
-- TOC entry 415 (class 1259 OID 369629)
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


ALTER TABLE public.act_ru_timer_job OWNER TO luohd;

--
-- TOC entry 416 (class 1259 OID 369635)
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


ALTER TABLE public.act_ru_variable OWNER TO luohd;

--
-- TOC entry 214 (class 1259 OID 360271)
-- Name: crm_account; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_account OWNER TO "_tenant";

--
-- TOC entry 435 (class 1259 OID 371309)
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
    del_flag smallint DEFAULT 0,
    value_id character varying(64),
    account_name character varying(255),
    roi numeric(18,6)
);


ALTER TABLE public.crm_account_value OWNER TO luohd;

--
-- TOC entry 215 (class 1259 OID 360287)
-- Name: crm_accountrelation; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_accountrelation OWNER TO "_tenant";

--
-- TOC entry 216 (class 1259 OID 360298)
-- Name: crm_activity; Type: TABLE; Schema: public; Owner: _tenant
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
    creator_id character varying(64),
    event_category character varying(32),
    channel character varying(32),
    channel_event_id character varying(128),
    is_aggregated boolean DEFAULT false,
    aggregated_at timestamp without time zone,
    context jsonb DEFAULT '{}'::jsonb
);


ALTER TABLE public.crm_activity OWNER TO "_tenant";

--
-- TOC entry 7840 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN crm_activity.activity_subject; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_activity.activity_subject IS '活动主题（含公众号 VIEW 菜单完整 URL）';


--
-- TOC entry 7841 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN crm_activity.pulse_id; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_activity.pulse_id IS 'pulseId，兼容 crm_pulse 迁移';


--
-- TOC entry 7842 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN crm_activity.parent_id; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_activity.parent_id IS '父编号，兼容 crm_pulse 迁移（"0"=timeline, "1"=notes）';


--
-- TOC entry 7843 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN crm_activity.order_id; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_activity.order_id IS '排序';


--
-- TOC entry 7844 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN crm_activity.pulse_type; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_activity.pulse_type IS '旧 pulse 事件类型（逐步废弃）';


--
-- TOC entry 7845 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN crm_activity.subject_type; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_activity.subject_type IS '主体类型: Lead|Contact|Account|Opportunity';


--
-- TOC entry 7846 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN crm_activity.subject_id; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_activity.subject_id IS '主体ID';


--
-- TOC entry 7847 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN crm_activity.event_type; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_activity.event_type IS '事件类型: lead.created|ai.scored|payment.deposit 等';


--
-- TOC entry 7848 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN crm_activity.event_name; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_activity.event_name IS '事件名称（含公众号 VIEW 菜单完整 URL）';


--
-- TOC entry 7849 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN crm_activity.importance; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_activity.importance IS '重要性: 10-30普通, 40-60重要, 70-100关键里程碑';


--
-- TOC entry 7850 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN crm_activity.ended_date; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_activity.ended_date IS '事件结束时间';


--
-- TOC entry 7851 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN crm_activity.source; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_activity.source IS '事件来源: manual|system|workflow|agent|douyin|wecom|...';


--
-- TOC entry 7852 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN crm_activity.payload; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_activity.payload IS '结构化扩展信息（JSON）';


--
-- TOC entry 7853 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN crm_activity.status; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_activity.status IS '事件状态: pending|in_progress|completed|cancelled';


--
-- TOC entry 7854 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN crm_activity.parent_event_id; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_activity.parent_event_id IS '父事件ID，支持事件嵌套';


--
-- TOC entry 7855 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN crm_activity.relate_type; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_activity.relate_type IS '事件来源业务对象类型（兼容旧查询）';


--
-- TOC entry 7856 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN crm_activity.relate_id; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_activity.relate_id IS '事件来源业务对象ID（兼容旧查询）';


--
-- TOC entry 7857 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN crm_activity.relate_name; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_activity.relate_name IS '关联对象显示名';


--
-- TOC entry 7858 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN crm_activity.message_data; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_activity.message_data IS '文本摘要（纯文本，非 HTML）';


--
-- TOC entry 7859 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN crm_activity.avatar; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_activity.avatar IS '发布人头像';


--
-- TOC entry 7860 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN crm_activity.creator_id; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_activity.creator_id IS '创建者ID';


--
-- TOC entry 7861 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN crm_activity.event_category; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_activity.event_category IS '事件大类: channel|crm|ai|engagement|conversion|system';


--
-- TOC entry 7862 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN crm_activity.channel; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_activity.channel IS '渠道来源: wecom|wechat_mp|wechat_miniapp|douyin|manual|agent';


--
-- TOC entry 7863 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN crm_activity.channel_event_id; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_activity.channel_event_id IS '渠道侧原始事件ID（企微 MsgId/EventId），用于幂等去重';


--
-- TOC entry 7864 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN crm_activity.is_aggregated; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_activity.is_aggregated IS '是否已被 customer_metrics 聚合消费';


--
-- TOC entry 7865 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN crm_activity.aggregated_at; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_activity.aggregated_at IS '聚合消费时间';


--
-- TOC entry 7866 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN crm_activity.context; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_activity.context IS '技术上下文元数据（设备信息、IP、地理位置、UTM参数等），与业务 payload 分离存储';


--
-- TOC entry 491 (class 1259 OID 404208)
-- Name: crm_activity_unmatched; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_activity_unmatched (
    id bigint NOT NULL,
    pulse_id character varying(64) NOT NULL,
    identity_type character varying(32) NOT NULL,
    identity_value character varying(256) NOT NULL,
    retry_count integer DEFAULT 0 NOT NULL,
    max_retries integer DEFAULT 12 NOT NULL,
    next_retry_at timestamp without time zone,
    matched_at timestamp without time zone,
    contact_id character varying(64),
    abandoned boolean DEFAULT false NOT NULL,
    raw_payload text,
    create_time timestamp without time zone DEFAULT now(),
    update_time timestamp without time zone DEFAULT now(),
    union_id character varying(64),
    union_id_source character varying(32) DEFAULT 'xml_event'::character varying,
    app_id character varying(64)
);


ALTER TABLE public.crm_activity_unmatched OWNER TO luohd;

--
-- TOC entry 7868 (class 0 OID 0)
-- Dependencies: 491
-- Name: TABLE crm_activity_unmatched; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_activity_unmatched IS '未匹配身份的 Activity 记录。Webhook 收到事件但 openid/external_userid 无法解析为 contact_id 时写入，定时任务扫描重试匹配。全渠道通用（公众号/企微/小程序等）';


--
-- TOC entry 7869 (class 0 OID 0)
-- Dependencies: 491
-- Name: COLUMN crm_activity_unmatched.pulse_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_activity_unmatched.pulse_id IS 'crm_activity.pulse_id，用于匹配完成后回填 contact_id';


--
-- TOC entry 7870 (class 0 OID 0)
-- Dependencies: 491
-- Name: COLUMN crm_activity_unmatched.identity_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_activity_unmatched.identity_type IS 'wechat_mp_openid / wecom_external_userid / wechat_miniapp_openid 等';


--
-- TOC entry 7871 (class 0 OID 0)
-- Dependencies: 491
-- Name: COLUMN crm_activity_unmatched.next_retry_at; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_activity_unmatched.next_retry_at IS '下一次重试时间。初始 = now() + 5min，每次重试后 + 5min';


--
-- TOC entry 7872 (class 0 OID 0)
-- Dependencies: 491
-- Name: COLUMN crm_activity_unmatched.matched_at; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_activity_unmatched.matched_at IS '匹配成功时记录时间，同时 abandoned=false 且 contact_id 已填写';


--
-- TOC entry 7873 (class 0 OID 0)
-- Dependencies: 491
-- Name: COLUMN crm_activity_unmatched.union_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_activity_unmatched.union_id IS '微信 UnionID，subscribe 事件从 XML 提取，用于跨渠道身份关联的本地匹配';


--
-- TOC entry 7874 (class 0 OID 0)
-- Dependencies: 491
-- Name: COLUMN crm_activity_unmatched.union_id_source; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_activity_unmatched.union_id_source IS 'UnionID 来源: xml_event (回调中提取) | wechat_api (通过微信 API 获取)';


--
-- TOC entry 490 (class 1259 OID 404207)
-- Name: crm_activity_unmatched_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_activity_unmatched_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_activity_unmatched_id_seq OWNER TO luohd;

--
-- TOC entry 7876 (class 0 OID 0)
-- Dependencies: 490
-- Name: crm_activity_unmatched_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_activity_unmatched_id_seq OWNED BY public.crm_activity_unmatched.id;


--
-- TOC entry 217 (class 1259 OID 360306)
-- Name: crm_actrelate; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_actrelate OWNER TO "_tenant";

--
-- TOC entry 219 (class 1259 OID 360315)
-- Name: crm_agent_tool; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_agent_tool OWNER TO "_tenant";

--
-- TOC entry 218 (class 1259 OID 360314)
-- Name: crm_agent_tool_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
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
-- TOC entry 455 (class 1259 OID 371819)
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


ALTER TABLE public.crm_ai_chat_message OWNER TO luohd;

--
-- TOC entry 7881 (class 0 OID 0)
-- Dependencies: 455
-- Name: TABLE crm_ai_chat_message; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_ai_chat_message IS 'UI message parts persisted per session (AI SDK UIMessage shape)';


--
-- TOC entry 454 (class 1259 OID 371810)
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


ALTER TABLE public.crm_ai_chat_session OWNER TO luohd;

--
-- TOC entry 7883 (class 0 OID 0)
-- Dependencies: 454
-- Name: TABLE crm_ai_chat_session; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_ai_chat_session IS 'AI assistant chat sessions scoped by tenant + user';


--
-- TOC entry 451 (class 1259 OID 371741)
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


ALTER TABLE public.crm_ai_model_config OWNER TO luohd;

--
-- TOC entry 7885 (class 0 OID 0)
-- Dependencies: 451
-- Name: TABLE crm_ai_model_config; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_ai_model_config IS '租户 AI 模型配置';


--
-- TOC entry 7886 (class 0 OID 0)
-- Dependencies: 451
-- Name: COLUMN crm_ai_model_config.config_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_ai_model_config.config_id IS 'UUID 主键';


--
-- TOC entry 7887 (class 0 OID 0)
-- Dependencies: 451
-- Name: COLUMN crm_ai_model_config.tenant_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_ai_model_config.tenant_id IS '租户标识（tenant 请求头）';


--
-- TOC entry 7888 (class 0 OID 0)
-- Dependencies: 451
-- Name: COLUMN crm_ai_model_config.provider; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_ai_model_config.provider IS 'LLM 提供商标识 qwen/deepseek/openai/gemini/anthropic';


--
-- TOC entry 7889 (class 0 OID 0)
-- Dependencies: 451
-- Name: COLUMN crm_ai_model_config.credential_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_ai_model_config.credential_id IS '关联 w_credential.id';


--
-- TOC entry 7890 (class 0 OID 0)
-- Dependencies: 451
-- Name: COLUMN crm_ai_model_config.model; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_ai_model_config.model IS '模型 ID';


--
-- TOC entry 7891 (class 0 OID 0)
-- Dependencies: 451
-- Name: COLUMN crm_ai_model_config.temperature; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_ai_model_config.temperature IS '采样温度 0.00-1.00';


--
-- TOC entry 7892 (class 0 OID 0)
-- Dependencies: 451
-- Name: COLUMN crm_ai_model_config.default_translation_language; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_ai_model_config.default_translation_language IS '默认翻译目标语言 BCP-47';


--
-- TOC entry 7893 (class 0 OID 0)
-- Dependencies: 451
-- Name: COLUMN crm_ai_model_config.del_flag; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_ai_model_config.del_flag IS '逻辑删除 0 正常';


--
-- TOC entry 466 (class 1259 OID 379543)
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


ALTER TABLE public.crm_ai_playbook_run OWNER TO luohd;

--
-- TOC entry 7895 (class 0 OID 0)
-- Dependencies: 466
-- Name: TABLE crm_ai_playbook_run; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_ai_playbook_run IS 'Playbook 执行审计';


--
-- TOC entry 465 (class 1259 OID 379542)
-- Name: crm_ai_playbook_run_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_ai_playbook_run_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_ai_playbook_run_id_seq OWNER TO luohd;

--
-- TOC entry 7897 (class 0 OID 0)
-- Dependencies: 465
-- Name: crm_ai_playbook_run_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_ai_playbook_run_id_seq OWNED BY public.crm_ai_playbook_run.id;


--
-- TOC entry 458 (class 1259 OID 379467)
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


ALTER TABLE public.crm_ai_skill OWNER TO luohd;

--
-- TOC entry 7899 (class 0 OID 0)
-- Dependencies: 458
-- Name: TABLE crm_ai_skill; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_ai_skill IS '租户 AI Skill 配置';


--
-- TOC entry 7900 (class 0 OID 0)
-- Dependencies: 458
-- Name: COLUMN crm_ai_skill.tenant_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_ai_skill.tenant_id IS '租户标识（tenant 请求头）';


--
-- TOC entry 7901 (class 0 OID 0)
-- Dependencies: 458
-- Name: COLUMN crm_ai_skill.trigger_keywords; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_ai_skill.trigger_keywords IS 'JSON 字符串数组';


--
-- TOC entry 7902 (class 0 OID 0)
-- Dependencies: 458
-- Name: COLUMN crm_ai_skill.tool_bindings; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_ai_skill.tool_bindings IS 'JSON tool binding 数组';


--
-- TOC entry 457 (class 1259 OID 379466)
-- Name: crm_ai_skill_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_ai_skill_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_ai_skill_id_seq OWNER TO luohd;

--
-- TOC entry 7904 (class 0 OID 0)
-- Dependencies: 457
-- Name: crm_ai_skill_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_ai_skill_id_seq OWNED BY public.crm_ai_skill.id;


--
-- TOC entry 462 (class 1259 OID 379508)
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


ALTER TABLE public.crm_ai_skill_playbook OWNER TO luohd;

--
-- TOC entry 7906 (class 0 OID 0)
-- Dependencies: 462
-- Name: TABLE crm_ai_skill_playbook; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_ai_skill_playbook IS 'AI Skill Playbook 定义';


--
-- TOC entry 461 (class 1259 OID 379507)
-- Name: crm_ai_skill_playbook_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_ai_skill_playbook_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_ai_skill_playbook_id_seq OWNER TO luohd;

--
-- TOC entry 7908 (class 0 OID 0)
-- Dependencies: 461
-- Name: crm_ai_skill_playbook_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_ai_skill_playbook_id_seq OWNED BY public.crm_ai_skill_playbook.id;


--
-- TOC entry 464 (class 1259 OID 379525)
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


ALTER TABLE public.crm_ai_skill_playbook_step OWNER TO luohd;

--
-- TOC entry 7910 (class 0 OID 0)
-- Dependencies: 464
-- Name: TABLE crm_ai_skill_playbook_step; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_ai_skill_playbook_step IS 'Playbook DAG 步骤';


--
-- TOC entry 463 (class 1259 OID 379524)
-- Name: crm_ai_skill_playbook_step_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_ai_skill_playbook_step_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_ai_skill_playbook_step_id_seq OWNER TO luohd;

--
-- TOC entry 7912 (class 0 OID 0)
-- Dependencies: 463
-- Name: crm_ai_skill_playbook_step_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_ai_skill_playbook_step_id_seq OWNED BY public.crm_ai_skill_playbook_step.id;


--
-- TOC entry 456 (class 1259 OID 371833)
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


ALTER TABLE public.crm_analytics_query_log OWNER TO luohd;

--
-- TOC entry 7914 (class 0 OID 0)
-- Dependencies: 456
-- Name: TABLE crm_analytics_query_log; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_analytics_query_log IS 'Optional audit trail for AI assistant analytics queries';


--
-- TOC entry 220 (class 1259 OID 360325)
-- Name: crm_appointment; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_appointment OWNER TO "_tenant";

--
-- TOC entry 221 (class 1259 OID 360339)
-- Name: crm_attendee; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_attendee OWNER TO "_tenant";

--
-- TOC entry 642 (class 1259 OID 413411)
-- Name: crm_booking; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_booking (
    booking_id character varying(100) NOT NULL,
    lead_id character varying(100),
    contact_id character varying(100),
    org_id character varying(100),
    booking_name character varying(200),
    start_time timestamp with time zone NOT NULL,
    end_time timestamp with time zone NOT NULL,
    status character varying(32) DEFAULT 'scheduled'::character varying NOT NULL,
    source character varying(64),
    source_ref character varying(200),
    mobile_phone character varying(50),
    notes text,
    owner character varying(100),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    user_id bigint,
    dept_id bigint,
    CONSTRAINT crm_booking_subject_chk CHECK (((lead_id IS NOT NULL) OR (contact_id IS NOT NULL))),
    CONSTRAINT crm_booking_time_chk CHECK ((end_time > start_time))
);


ALTER TABLE public.crm_booking OWNER TO luohd;

--
-- TOC entry 7918 (class 0 OID 0)
-- Dependencies: 642
-- Name: TABLE crm_booking; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_booking IS '诊所上门预约（与会议 crm_appointment 分离）';


--
-- TOC entry 222 (class 1259 OID 360346)
-- Name: crm_bulkemail; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_bulkemail OWNER TO "_tenant";

--
-- TOC entry 7920 (class 0 OID 0)
-- Dependencies: 222
-- Name: COLUMN crm_bulkemail.spend_amount; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_bulkemail.spend_amount IS 'Campaign spend for CAC amortization';


--
-- TOC entry 7921 (class 0 OID 0)
-- Dependencies: 222
-- Name: COLUMN crm_bulkemail.send_mode; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_bulkemail.send_mode IS 'immediate / scheduled / draft';


--
-- TOC entry 7922 (class 0 OID 0)
-- Dependencies: 222
-- Name: COLUMN crm_bulkemail.scheduled_at; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_bulkemail.scheduled_at IS '计划发送时间';


--
-- TOC entry 7923 (class 0 OID 0)
-- Dependencies: 222
-- Name: COLUMN crm_bulkemail.sent_at; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_bulkemail.sent_at IS '全部 contact 处理完成时间';


--
-- TOC entry 7924 (class 0 OID 0)
-- Dependencies: 222
-- Name: COLUMN crm_bulkemail.cancelled_at; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_bulkemail.cancelled_at IS '取消时间';


--
-- TOC entry 7925 (class 0 OID 0)
-- Dependencies: 222
-- Name: COLUMN crm_bulkemail.metadata; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_bulkemail.metadata IS 'surveyId/eventId/audienceFilter 等';


--
-- TOC entry 533 (class 1259 OID 404713)
-- Name: crm_bulkemail_provider_config; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_bulkemail_provider_config (
    id bigint NOT NULL,
    provider character varying(32) DEFAULT 'mailgun'::character varying NOT NULL,
    domain character varying(255) NOT NULL,
    from_email character varying(255) NOT NULL,
    from_name character varying(128),
    region character varying(8) DEFAULT 'us'::character varying NOT NULL,
    api_key_enc text NOT NULL,
    status character varying(16) DEFAULT 'active'::character varying NOT NULL,
    last_test_at timestamp with time zone,
    last_error character varying(500),
    create_by character varying(64),
    create_time timestamp with time zone,
    update_by character varying(64),
    update_time timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.crm_bulkemail_provider_config OWNER TO luohd;

--
-- TOC entry 7927 (class 0 OID 0)
-- Dependencies: 533
-- Name: TABLE crm_bulkemail_provider_config; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_bulkemail_provider_config IS '租户级批量邮件发信配置（Mailgun MVP）';


--
-- TOC entry 7928 (class 0 OID 0)
-- Dependencies: 533
-- Name: COLUMN crm_bulkemail_provider_config.provider; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_bulkemail_provider_config.provider IS '发信通道，MVP 固定 mailgun';


--
-- TOC entry 7929 (class 0 OID 0)
-- Dependencies: 533
-- Name: COLUMN crm_bulkemail_provider_config.domain; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_bulkemail_provider_config.domain IS 'Mailgun 发送域名';


--
-- TOC entry 7930 (class 0 OID 0)
-- Dependencies: 533
-- Name: COLUMN crm_bulkemail_provider_config.from_email; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_bulkemail_provider_config.from_email IS '默认发件邮箱';


--
-- TOC entry 7931 (class 0 OID 0)
-- Dependencies: 533
-- Name: COLUMN crm_bulkemail_provider_config.from_name; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_bulkemail_provider_config.from_name IS '默认发件人显示名';


--
-- TOC entry 7932 (class 0 OID 0)
-- Dependencies: 533
-- Name: COLUMN crm_bulkemail_provider_config.region; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_bulkemail_provider_config.region IS 'mailgun API 区域: us | eu';


--
-- TOC entry 7933 (class 0 OID 0)
-- Dependencies: 533
-- Name: COLUMN crm_bulkemail_provider_config.api_key_enc; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_bulkemail_provider_config.api_key_enc IS 'AES 加密，GET 不返回';


--
-- TOC entry 7934 (class 0 OID 0)
-- Dependencies: 533
-- Name: COLUMN crm_bulkemail_provider_config.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_bulkemail_provider_config.status IS 'active | disabled';


--
-- TOC entry 7935 (class 0 OID 0)
-- Dependencies: 533
-- Name: COLUMN crm_bulkemail_provider_config.last_test_at; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_bulkemail_provider_config.last_test_at IS '最近一次连通性测试时间';


--
-- TOC entry 7936 (class 0 OID 0)
-- Dependencies: 533
-- Name: COLUMN crm_bulkemail_provider_config.last_error; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_bulkemail_provider_config.last_error IS '最近一次错误摘要';


--
-- TOC entry 532 (class 1259 OID 404712)
-- Name: crm_bulkemail_provider_config_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_bulkemail_provider_config_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_bulkemail_provider_config_id_seq OWNER TO luohd;

--
-- TOC entry 7938 (class 0 OID 0)
-- Dependencies: 532
-- Name: crm_bulkemail_provider_config_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_bulkemail_provider_config_id_seq OWNED BY public.crm_bulkemail_provider_config.id;


--
-- TOC entry 223 (class 1259 OID 360357)
-- Name: crm_campaign_contacts; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_campaign_contacts OWNER TO "_tenant";

--
-- TOC entry 7940 (class 0 OID 0)
-- Dependencies: 223
-- Name: COLUMN crm_campaign_contacts.delivery_status; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_campaign_contacts.delivery_status IS 'pending/sent/delivered/bounced/failed/suppressed';


--
-- TOC entry 7941 (class 0 OID 0)
-- Dependencies: 223
-- Name: COLUMN crm_campaign_contacts.bounce_reason; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_campaign_contacts.bounce_reason IS 'SMTP/网关退信原因';


--
-- TOC entry 7942 (class 0 OID 0)
-- Dependencies: 223
-- Name: COLUMN crm_campaign_contacts.sent_at; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_campaign_contacts.sent_at IS '单封发出时间';


--
-- TOC entry 578 (class 1259 OID 409257)
-- Name: crm_capture; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_capture (
    capture_id character varying(36) NOT NULL,
    template_id character varying(36) NOT NULL,
    title character varying(256) DEFAULT ''::character varying NOT NULL,
    status character varying(32) DEFAULT 'draft'::character varying NOT NULL,
    content_snapshot text,
    success_message character varying(512),
    allowed_origins text,
    published_at timestamp with time zone,
    unpublished_at timestamp with time zone,
    create_by character varying(64),
    create_time timestamp with time zone,
    update_by character varying(64),
    update_time timestamp with time zone
);


ALTER TABLE public.crm_capture OWNER TO luohd;

--
-- TOC entry 7944 (class 0 OID 0)
-- Dependencies: 578
-- Name: TABLE crm_capture; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_capture IS 'Captures 发布态（Survey 模版快照）';


--
-- TOC entry 7945 (class 0 OID 0)
-- Dependencies: 578
-- Name: COLUMN crm_capture.capture_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_capture.capture_id IS '公开 URL / Embed 使用的稳定 ID';


--
-- TOC entry 7946 (class 0 OID 0)
-- Dependencies: 578
-- Name: COLUMN crm_capture.template_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_capture.template_id IS '指向 sys_re_template.template_id（Survey）';


--
-- TOC entry 7947 (class 0 OID 0)
-- Dependencies: 578
-- Name: COLUMN crm_capture.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_capture.status IS 'draft | published | unpublished';


--
-- TOC entry 7948 (class 0 OID 0)
-- Dependencies: 578
-- Name: COLUMN crm_capture.content_snapshot; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_capture.content_snapshot IS '发布瞬间的 SurveyJS JSON；改模版草稿不自动更新';


--
-- TOC entry 7949 (class 0 OID 0)
-- Dependencies: 578
-- Name: COLUMN crm_capture.allowed_origins; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_capture.allowed_origins IS 'CORS 白名单 JSON 数组；NULL 表示不限制 origin';


--
-- TOC entry 224 (class 1259 OID 360366)
-- Name: crm_cashflow; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_cashflow OWNER TO "_tenant";

--
-- TOC entry 597 (class 1259 OID 411705)
-- Name: crm_chinese_enterprise; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_chinese_enterprise (
    id character varying(64) NOT NULL,
    parent_company_china character varying(512) NOT NULL,
    stock_code character varying(64),
    stock_listing_status character varying(64),
    parent_province_china character varying(256) DEFAULT ''::character varying NOT NULL,
    vietnam_entity_name character varying(512) NOT NULL,
    vietnam_entity_name_cn character varying(512) NOT NULL,
    tax_code character varying(64),
    park_id character varying(64) NOT NULL,
    park_name_cn character varying(256) DEFAULT ''::character varying NOT NULL,
    province_vietnam character varying(128) DEFAULT ''::character varying NOT NULL,
    industry character varying(256) DEFAULT ''::character varying NOT NULL,
    supply_chain_role text,
    investment_mode character varying(128),
    investment_usd_million numeric(18,2),
    registered_capital_usd_m numeric(18,2),
    employee_count integer,
    factory_area_sqm numeric(18,2),
    established_year integer,
    status character varying(32) DEFAULT '运营中'::character varying NOT NULL,
    main_products text,
    contact_phone character varying(128),
    contact_email character varying(256),
    website character varying(1024),
    notes text,
    created_at character varying(32),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    del_flag character(1) DEFAULT '0'::bpchar NOT NULL
);


ALTER TABLE public.crm_chinese_enterprise OWNER TO luohd;

--
-- TOC entry 7952 (class 0 OID 0)
-- Dependencies: 597
-- Name: TABLE crm_chinese_enterprise; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_chinese_enterprise IS '赴越中资企业（国内母公司 + 越南落地实体）';


--
-- TOC entry 7953 (class 0 OID 0)
-- Dependencies: 597
-- Name: COLUMN crm_chinese_enterprise.stock_listing_status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_chinese_enterprise.stock_listing_status IS 'A股/港股/美股/拟上市/非上市等';


--
-- TOC entry 7954 (class 0 OID 0)
-- Dependencies: 597
-- Name: COLUMN crm_chinese_enterprise.park_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_chinese_enterprise.park_id IS '逻辑外键 → crm_industrial_park.id';


--
-- TOC entry 7955 (class 0 OID 0)
-- Dependencies: 597
-- Name: COLUMN crm_chinese_enterprise.park_name_cn; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_chinese_enterprise.park_name_cn IS '冗余园区中文名，列表免联表';


--
-- TOC entry 7956 (class 0 OID 0)
-- Dependencies: 597
-- Name: COLUMN crm_chinese_enterprise.investment_mode; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_chinese_enterprise.investment_mode IS '独资自建/租赁厂房/合资/并购等';


--
-- TOC entry 7957 (class 0 OID 0)
-- Dependencies: 597
-- Name: COLUMN crm_chinese_enterprise.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_chinese_enterprise.status IS '运营中 | 建设中 | 签约拟建';


--
-- TOC entry 7958 (class 0 OID 0)
-- Dependencies: 597
-- Name: COLUMN crm_chinese_enterprise.del_flag; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_chinese_enterprise.del_flag IS '0 active, 1 soft-deleted';


--
-- TOC entry 225 (class 1259 OID 360374)
-- Name: crm_collect; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_collect OWNER TO "_tenant";

--
-- TOC entry 226 (class 1259 OID 360382)
-- Name: crm_competitor; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_competitor OWNER TO "_tenant";

--
-- TOC entry 227 (class 1259 OID 360391)
-- Name: crm_contact; Type: TABLE; Schema: public; Owner: _tenant
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
    wecom_type integer,
    wecom_follow_userid character varying(128) DEFAULT NULL::character varying,
    source_lead_id character varying(64)
);


ALTER TABLE public.crm_contact OWNER TO "_tenant";

--
-- TOC entry 7962 (class 0 OID 0)
-- Dependencies: 227
-- Name: COLUMN crm_contact.original_source; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_contact.original_source IS '终身初始来源（首次创建写入，勿随意覆盖）';


--
-- TOC entry 7963 (class 0 OID 0)
-- Dependencies: 227
-- Name: COLUMN crm_contact.first_visit_clinic_id; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_contact.first_visit_clinic_id IS '首诊门店主键或字典值';


--
-- TOC entry 7964 (class 0 OID 0)
-- Dependencies: 227
-- Name: COLUMN crm_contact.imaging_portal_url; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_contact.imaging_portal_url IS '影像厂商门户直链';


--
-- TOC entry 7965 (class 0 OID 0)
-- Dependencies: 227
-- Name: COLUMN crm_contact.imaging_patient_external_id; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_contact.imaging_patient_external_id IS '第三方影像系统患者标识';


--
-- TOC entry 7966 (class 0 OID 0)
-- Dependencies: 227
-- Name: COLUMN crm_contact.first_visit_at; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_contact.first_visit_at IS 'First visit timestamp (proxy: contract.sign_time in v1)';


--
-- TOC entry 7967 (class 0 OID 0)
-- Dependencies: 227
-- Name: COLUMN crm_contact.last_visit_at; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_contact.last_visit_at IS 'Most recent visit timestamp';


--
-- TOC entry 7968 (class 0 OID 0)
-- Dependencies: 227
-- Name: COLUMN crm_contact.visit_count; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_contact.visit_count IS 'Lifetime visit count';


--
-- TOC entry 7969 (class 0 OID 0)
-- Dependencies: 227
-- Name: COLUMN crm_contact.acquisition_cost; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_contact.acquisition_cost IS 'Accumulated CAC from campaign amortization';


--
-- TOC entry 7970 (class 0 OID 0)
-- Dependencies: 227
-- Name: COLUMN crm_contact.wecom_external_userid; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_contact.wecom_external_userid IS '企业微信外部联系人ID';


--
-- TOC entry 7971 (class 0 OID 0)
-- Dependencies: 227
-- Name: COLUMN crm_contact.wecom_name; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_contact.wecom_name IS '微信客服客户昵称';


--
-- TOC entry 7972 (class 0 OID 0)
-- Dependencies: 227
-- Name: COLUMN crm_contact.wecom_avatar; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_contact.wecom_avatar IS '微信客服客户头像 URL';


--
-- TOC entry 7973 (class 0 OID 0)
-- Dependencies: 227
-- Name: COLUMN crm_contact.wecom_gender; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_contact.wecom_gender IS '微信客服客户性别（0=未知, 1=男, 2=女）';


--
-- TOC entry 7974 (class 0 OID 0)
-- Dependencies: 227
-- Name: COLUMN crm_contact.wecom_type; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_contact.wecom_type IS '微信客服客户类型';


--
-- TOC entry 7975 (class 0 OID 0)
-- Dependencies: 227
-- Name: COLUMN crm_contact.wecom_follow_userid; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_contact.wecom_follow_userid IS '企业微信跟进成员 userid（来自 Lead 转化或企微同步）';


--
-- TOC entry 657 (class 1259 OID 417255)
-- Name: crm_contact_service_summary; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_contact_service_summary (
    tenant_id character varying(64) NOT NULL,
    contact_id character varying(64) NOT NULL,
    current_need text,
    confirmed_facts text,
    commitments_todos text,
    risks_sentiment text,
    last_touch_at timestamp with time zone,
    last_touch_channel character varying(32),
    last_touch_summary text,
    source_channels jsonb,
    last_event_id character varying(64),
    last_event_at timestamp with time zone,
    version bigint DEFAULT 1 NOT NULL,
    status character varying(16) DEFAULT 'ready'::character varying NOT NULL,
    generated_by character varying(16) DEFAULT 'ai'::character varying NOT NULL,
    locked_fields jsonb,
    model character varying(64),
    error_message text,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.crm_contact_service_summary OWNER TO luohd;

--
-- TOC entry 7977 (class 0 OID 0)
-- Dependencies: 657
-- Name: TABLE crm_contact_service_summary; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_contact_service_summary IS 'Contact 服务摘要当前态（一人一行）';


--
-- TOC entry 7978 (class 0 OID 0)
-- Dependencies: 657
-- Name: COLUMN crm_contact_service_summary.last_event_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_contact_service_summary.last_event_id IS '增量水位';


--
-- TOC entry 7979 (class 0 OID 0)
-- Dependencies: 657
-- Name: COLUMN crm_contact_service_summary.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_contact_service_summary.status IS 'ready | stale | generating | failed';


--
-- TOC entry 7980 (class 0 OID 0)
-- Dependencies: 657
-- Name: COLUMN crm_contact_service_summary.generated_by; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_contact_service_summary.generated_by IS 'ai | human | ai_human';


--
-- TOC entry 7981 (class 0 OID 0)
-- Dependencies: 657
-- Name: COLUMN crm_contact_service_summary.locked_fields; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_contact_service_summary.locked_fields IS '人工锁定段，如 ["commitmentsTodos"]';


--
-- TOC entry 659 (class 1259 OID 417269)
-- Name: crm_contact_service_summary_revision; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_contact_service_summary_revision (
    id bigint NOT NULL,
    tenant_id character varying(64) NOT NULL,
    contact_id character varying(64) NOT NULL,
    version bigint NOT NULL,
    snapshot jsonb NOT NULL,
    trigger character varying(32) NOT NULL,
    source_channels jsonb,
    model character varying(64),
    generated_by character varying(16) NOT NULL,
    created_by character varying(64),
    created_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.crm_contact_service_summary_revision OWNER TO luohd;

--
-- TOC entry 7983 (class 0 OID 0)
-- Dependencies: 659
-- Name: TABLE crm_contact_service_summary_revision; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_contact_service_summary_revision IS 'Contact 服务摘要历史版本';


--
-- TOC entry 7984 (class 0 OID 0)
-- Dependencies: 659
-- Name: COLUMN crm_contact_service_summary_revision.trigger; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_contact_service_summary_revision.trigger IS 'bootstrap | incremental | refresh | human_patch | convert | link';


--
-- TOC entry 658 (class 1259 OID 417268)
-- Name: crm_contact_service_summary_revision_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_contact_service_summary_revision_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_contact_service_summary_revision_id_seq OWNER TO luohd;

--
-- TOC entry 7986 (class 0 OID 0)
-- Dependencies: 658
-- Name: crm_contact_service_summary_revision_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_contact_service_summary_revision_id_seq OWNED BY public.crm_contact_service_summary_revision.id;


--
-- TOC entry 228 (class 1259 OID 360412)
-- Name: crm_contactmethod; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_contactmethod OWNER TO "_tenant";

--
-- TOC entry 229 (class 1259 OID 360420)
-- Name: crm_contactrelation; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_contactrelation OWNER TO "_tenant";

--
-- TOC entry 230 (class 1259 OID 360428)
-- Name: crm_contract; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_contract OWNER TO "_tenant";

--
-- TOC entry 660 (class 1259 OID 417279)
-- Name: crm_conversation_summary_draft; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_conversation_summary_draft (
    tenant_id character varying(64) NOT NULL,
    channel character varying(32) NOT NULL,
    conversation_key character varying(128) NOT NULL,
    draft_json jsonb NOT NULL,
    last_event_id character varying(64),
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.crm_conversation_summary_draft OWNER TO luohd;

--
-- TOC entry 7991 (class 0 OID 0)
-- Dependencies: 660
-- Name: TABLE crm_conversation_summary_draft; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_conversation_summary_draft IS '未绑定 Contact 的会话摘要草稿';


--
-- TOC entry 7992 (class 0 OID 0)
-- Dependencies: 660
-- Name: COLUMN crm_conversation_summary_draft.conversation_key; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_conversation_summary_draft.conversation_key IS '与 Unified Inbox key 一致';


--
-- TOC entry 231 (class 1259 OID 360453)
-- Name: crm_conversion_history; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_conversion_history OWNER TO "_tenant";

--
-- TOC entry 486 (class 1259 OID 404122)
-- Name: crm_customer_identity; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_customer_identity (
    identity_id bigint NOT NULL,
    contact_id character varying(64) NOT NULL,
    identity_type character varying(32) NOT NULL,
    identity_value character varying(512) NOT NULL,
    source character varying(32) DEFAULT 'sync_from_contact'::character varying NOT NULL,
    is_primary boolean DEFAULT false NOT NULL,
    is_verified boolean DEFAULT false NOT NULL,
    create_time timestamp without time zone DEFAULT now(),
    update_time timestamp without time zone DEFAULT now()
);


ALTER TABLE public.crm_customer_identity OWNER TO luohd;

--
-- TOC entry 7995 (class 0 OID 0)
-- Dependencies: 486
-- Name: TABLE crm_customer_identity; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_customer_identity IS 'Customer Identity: unified identity resolution across channels (mobile, email, WeCom, WeChat UnionID, etc.)';


--
-- TOC entry 7996 (class 0 OID 0)
-- Dependencies: 486
-- Name: COLUMN crm_customer_identity.contact_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_customer_identity.contact_id IS 'FK to crm_contact.contact_id';


--
-- TOC entry 7997 (class 0 OID 0)
-- Dependencies: 486
-- Name: COLUMN crm_customer_identity.identity_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_customer_identity.identity_type IS 'mobile_phone | email | wecom_external_userid | wechat_id | wechat_unionid | wechat_mp_openid | wechat_miniapp_openid | wechat_kf_openid';


--
-- TOC entry 7998 (class 0 OID 0)
-- Dependencies: 486
-- Name: COLUMN crm_customer_identity.identity_value; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_customer_identity.identity_value IS 'Actual identity value (phone digits, email address, external_userid, etc.)';


--
-- TOC entry 7999 (class 0 OID 0)
-- Dependencies: 486
-- Name: COLUMN crm_customer_identity.source; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_customer_identity.source IS 'sync_from_contact | wecom_callback | manual | import | contact_form';


--
-- TOC entry 8000 (class 0 OID 0)
-- Dependencies: 486
-- Name: COLUMN crm_customer_identity.is_primary; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_customer_identity.is_primary IS 'Primary identity for this contact (used for dedup priority)';


--
-- TOC entry 8001 (class 0 OID 0)
-- Dependencies: 486
-- Name: COLUMN crm_customer_identity.is_verified; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_customer_identity.is_verified IS 'Whether this identity has been verified (e.g., SMS code confirmed)';


--
-- TOC entry 485 (class 1259 OID 404121)
-- Name: crm_customer_identity_identity_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_customer_identity_identity_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_customer_identity_identity_id_seq OWNER TO luohd;

--
-- TOC entry 8003 (class 0 OID 0)
-- Dependencies: 485
-- Name: crm_customer_identity_identity_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_customer_identity_identity_id_seq OWNED BY public.crm_customer_identity.identity_id;


--
-- TOC entry 561 (class 1259 OID 408948)
-- Name: crm_dashboard_snapshot; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_dashboard_snapshot (
    id bigint NOT NULL,
    corp_id character varying(64) NOT NULL,
    snapshot_date date NOT NULL,
    metric_type character varying(32) NOT NULL,
    metric_data jsonb DEFAULT '{}'::jsonb NOT NULL,
    detail_data jsonb,
    raw_json text,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.crm_dashboard_snapshot OWNER TO luohd;

--
-- TOC entry 8005 (class 0 OID 0)
-- Dependencies: 561
-- Name: TABLE crm_dashboard_snapshot; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_dashboard_snapshot IS '企业仪表盘统计指标统一快照表';


--
-- TOC entry 8006 (class 0 OID 0)
-- Dependencies: 561
-- Name: COLUMN crm_dashboard_snapshot.metric_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_dashboard_snapshot.metric_type IS '指标分类: wecom_customer_total, store_visit, wechat_mp_fans, deal_retention, community_reach';


--
-- TOC entry 8007 (class 0 OID 0)
-- Dependencies: 561
-- Name: COLUMN crm_dashboard_snapshot.metric_data; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_dashboard_snapshot.metric_data IS '该指标的核心汇总数据(JSONB)，如 {"total_staff":15, "total_unique_customers":1280}';


--
-- TOC entry 8008 (class 0 OID 0)
-- Dependencies: 561
-- Name: COLUMN crm_dashboard_snapshot.detail_data; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_dashboard_snapshot.detail_data IS '该指标的明细数据(JSONB)，如 [{"userid":"zs","customer_cnt":120}]';


--
-- TOC entry 8009 (class 0 OID 0)
-- Dependencies: 561
-- Name: COLUMN crm_dashboard_snapshot.raw_json; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_dashboard_snapshot.raw_json IS '原始API响应存档(TEXT)，供排查或重处理';


--
-- TOC entry 560 (class 1259 OID 408947)
-- Name: crm_dashboard_snapshot_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_dashboard_snapshot_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_dashboard_snapshot_id_seq OWNER TO luohd;

--
-- TOC entry 8011 (class 0 OID 0)
-- Dependencies: 560
-- Name: crm_dashboard_snapshot_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_dashboard_snapshot_id_seq OWNED BY public.crm_dashboard_snapshot.id;


--
-- TOC entry 446 (class 1259 OID 371605)
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


ALTER TABLE public.crm_data_table OWNER TO luohd;

--
-- TOC entry 8013 (class 0 OID 0)
-- Dependencies: 446
-- Name: TABLE crm_data_table; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_data_table IS 'Data Table 元数据（类 Airtable）';


--
-- TOC entry 447 (class 1259 OID 371616)
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
    is_system boolean DEFAULT false NOT NULL,
    options text,
    unit text,
    format text,
    section text
);


ALTER TABLE public.crm_data_table_column OWNER TO luohd;

--
-- TOC entry 8015 (class 0 OID 0)
-- Dependencies: 447
-- Name: TABLE crm_data_table_column; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_data_table_column IS 'Data Table 列定义';


--
-- TOC entry 8016 (class 0 OID 0)
-- Dependencies: 447
-- Name: COLUMN crm_data_table_column.options; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_data_table_column.options IS 'JSON array of option strings for STRING/MULTI_SELECT';


--
-- TOC entry 8017 (class 0 OID 0)
-- Dependencies: 447
-- Name: COLUMN crm_data_table_column.unit; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_data_table_column.unit IS 'Display unit for NUMBER, e.g. 公顷, km, 美元/kWh';


--
-- TOC entry 8018 (class 0 OID 0)
-- Dependencies: 447
-- Name: COLUMN crm_data_table_column.format; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_data_table_column.format IS 'Display format: percent | decimal | null';


--
-- TOC entry 8019 (class 0 OID 0)
-- Dependencies: 447
-- Name: COLUMN crm_data_table_column.section; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_data_table_column.section IS 'Field group label, e.g. 基础信息';


--
-- TOC entry 448 (class 1259 OID 371633)
-- Name: crm_data_table_row; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_data_table_row (
    id text NOT NULL,
    data_table_id text NOT NULL,
    data jsonb DEFAULT '{}'::jsonb NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.crm_data_table_row OWNER TO luohd;

--
-- TOC entry 8021 (class 0 OID 0)
-- Dependencies: 448
-- Name: TABLE crm_data_table_row; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_data_table_row IS 'Data Table 行数据（JSONB）';


--
-- TOC entry 232 (class 1259 OID 360460)
-- Name: crm_dd; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_dd OWNER TO "_tenant";

--
-- TOC entry 233 (class 1259 OID 360471)
-- Name: crm_docrelation; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_docrelation OWNER TO "_tenant";

--
-- TOC entry 452 (class 1259 OID 371783)
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


ALTER TABLE public.crm_email_analysis OWNER TO luohd;

--
-- TOC entry 8025 (class 0 OID 0)
-- Dependencies: 452
-- Name: TABLE crm_email_analysis; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_email_analysis IS '邮件 AI 分析结果缓存';


--
-- TOC entry 8026 (class 0 OID 0)
-- Dependencies: 452
-- Name: COLUMN crm_email_analysis.analysis_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.analysis_id IS 'UUID 主键';


--
-- TOC entry 8027 (class 0 OID 0)
-- Dependencies: 452
-- Name: COLUMN crm_email_analysis.tenant_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.tenant_id IS '租户标识（tenant 请求头）';


--
-- TOC entry 8028 (class 0 OID 0)
-- Dependencies: 452
-- Name: COLUMN crm_email_analysis.user_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.user_id IS '触发分析的用户 ID';


--
-- TOC entry 8029 (class 0 OID 0)
-- Dependencies: 452
-- Name: COLUMN crm_email_analysis.message_external_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.message_external_id IS '邮件 provider message id';


--
-- TOC entry 8030 (class 0 OID 0)
-- Dependencies: 452
-- Name: COLUMN crm_email_analysis.provider; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.provider IS 'Gmail / Outlook 等';


--
-- TOC entry 8031 (class 0 OID 0)
-- Dependencies: 452
-- Name: COLUMN crm_email_analysis.external_name; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.external_name IS '邮箱账号标识';


--
-- TOC entry 8032 (class 0 OID 0)
-- Dependencies: 452
-- Name: COLUMN crm_email_analysis.locale; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.locale IS '分析输出语言（en / zh / vi …）';


--
-- TOC entry 8033 (class 0 OID 0)
-- Dependencies: 452
-- Name: COLUMN crm_email_analysis.subject; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.subject IS '分析时邮件主题快照';


--
-- TOC entry 8034 (class 0 OID 0)
-- Dependencies: 452
-- Name: COLUMN crm_email_analysis.sender; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.sender IS '发件人快照';


--
-- TOC entry 8035 (class 0 OID 0)
-- Dependencies: 452
-- Name: COLUMN crm_email_analysis.body_hash; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.body_hash IS 'SHA-256(subject + bodyText) hex，用于检测内容变更';


--
-- TOC entry 8036 (class 0 OID 0)
-- Dependencies: 452
-- Name: COLUMN crm_email_analysis.executive_summary; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.executive_summary IS '摘要';


--
-- TOC entry 8037 (class 0 OID 0)
-- Dependencies: 452
-- Name: COLUMN crm_email_analysis.category; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.category IS '分类';


--
-- TOC entry 8038 (class 0 OID 0)
-- Dependencies: 452
-- Name: COLUMN crm_email_analysis.urgency; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.urgency IS 'Low / Medium / High';


--
-- TOC entry 8039 (class 0 OID 0)
-- Dependencies: 452
-- Name: COLUMN crm_email_analysis.sentiment_label; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.sentiment_label IS 'Positive / Neutral / Negative';


--
-- TOC entry 8040 (class 0 OID 0)
-- Dependencies: 452
-- Name: COLUMN crm_email_analysis.sentiment_score; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.sentiment_score IS '0-100';


--
-- TOC entry 8041 (class 0 OID 0)
-- Dependencies: 452
-- Name: COLUMN crm_email_analysis.sentiment_description; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.sentiment_description IS '情感描述';


--
-- TOC entry 8042 (class 0 OID 0)
-- Dependencies: 452
-- Name: COLUMN crm_email_analysis.detected_intent; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.detected_intent IS '意图';


--
-- TOC entry 8043 (class 0 OID 0)
-- Dependencies: 452
-- Name: COLUMN crm_email_analysis.suggested_action; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.suggested_action IS '建议动作';


--
-- TOC entry 8044 (class 0 OID 0)
-- Dependencies: 452
-- Name: COLUMN crm_email_analysis.entities_json; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.entities_json IS 'JSON 数组 [{category,value,subLabel}]';


--
-- TOC entry 8045 (class 0 OID 0)
-- Dependencies: 452
-- Name: COLUMN crm_email_analysis.suggested_ticket_json; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.suggested_ticket_json IS 'JSON 对象';


--
-- TOC entry 8046 (class 0 OID 0)
-- Dependencies: 452
-- Name: COLUMN crm_email_analysis.suggested_lead_json; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.suggested_lead_json IS 'JSON 对象';


--
-- TOC entry 8047 (class 0 OID 0)
-- Dependencies: 452
-- Name: COLUMN crm_email_analysis.analysis_json; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.analysis_json IS '完整 EmailAnalysisResult JSON（canonical）';


--
-- TOC entry 8048 (class 0 OID 0)
-- Dependencies: 452
-- Name: COLUMN crm_email_analysis.model; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.model IS '实际使用的模型 id';


--
-- TOC entry 8049 (class 0 OID 0)
-- Dependencies: 452
-- Name: COLUMN crm_email_analysis.del_flag; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_email_analysis.del_flag IS '逻辑删除 0 正常';


--
-- TOC entry 234 (class 1259 OID 360478)
-- Name: crm_email_attachments; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_email_attachments OWNER TO "_tenant";

--
-- TOC entry 235 (class 1259 OID 360490)
-- Name: crm_email_messages; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_email_messages OWNER TO "_tenant";

--
-- TOC entry 236 (class 1259 OID 360502)
-- Name: crm_event; Type: TABLE; Schema: public; Owner: _tenant
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
    avatar character varying(200) DEFAULT '头像'::character varying,
    registration_template_id character varying(64),
    registration_form_json text,
    mp_registration_automation_enabled character(1) DEFAULT '0'::bpchar NOT NULL,
    mp_registration_preset_id bigint,
    mp_registration_delay_seconds integer DEFAULT 0 NOT NULL,
    mp_registration_skip_if_lead_exists character(1) DEFAULT '1'::bpchar NOT NULL,
    registration_enabled boolean DEFAULT true,
    registration_deadline timestamp without time zone,
    waitlist_enabled boolean DEFAULT false,
    check_in_enabled boolean DEFAULT true,
    allow_walk_in boolean DEFAULT true,
    default_session_id character varying(64),
    default_org_id character varying(36),
    auto_convert_on_registration boolean DEFAULT false NOT NULL,
    wechat_mp_app_id character varying(64),
    agenda text,
    language character varying(50)
);


ALTER TABLE public.crm_event OWNER TO "_tenant";

--
-- TOC entry 8053 (class 0 OID 0)
-- Dependencies: 236
-- Name: COLUMN crm_event.description; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_event.description IS '活动介绍（Markdown 格式）';


--
-- TOC entry 8054 (class 0 OID 0)
-- Dependencies: 236
-- Name: COLUMN crm_event.registration_template_id; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_event.registration_template_id IS '来源 EMS Survey 模版 ID，仅追溯';


--
-- TOC entry 8055 (class 0 OID 0)
-- Dependencies: 236
-- Name: COLUMN crm_event.registration_form_json; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_event.registration_form_json IS '报名表单 SurveyJS content 快照，运行时以此为准';


--
-- TOC entry 8056 (class 0 OID 0)
-- Dependencies: 236
-- Name: COLUMN crm_event.mp_registration_automation_enabled; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_event.mp_registration_automation_enabled IS '0=关 1=开；关注归因后自动发报名模板消息';


--
-- TOC entry 8057 (class 0 OID 0)
-- Dependencies: 236
-- Name: COLUMN crm_event.mp_registration_preset_id; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_event.mp_registration_preset_id IS 'FK crm_wechat_mp_message_preset.id';


--
-- TOC entry 8058 (class 0 OID 0)
-- Dependencies: 236
-- Name: COLUMN crm_event.mp_registration_delay_seconds; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_event.mp_registration_delay_seconds IS '关注后延迟秒数，与 subscribe 自动回复错开';


--
-- TOC entry 8059 (class 0 OID 0)
-- Dependencies: 236
-- Name: COLUMN crm_event.mp_registration_skip_if_lead_exists; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_event.mp_registration_skip_if_lead_exists IS '1=同活动已有可关联 lead 则跳过发送';


--
-- TOC entry 8060 (class 0 OID 0)
-- Dependencies: 236
-- Name: COLUMN crm_event.registration_enabled; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_event.registration_enabled IS '是否开放 H5/CRM 报名';


--
-- TOC entry 8061 (class 0 OID 0)
-- Dependencies: 236
-- Name: COLUMN crm_event.registration_deadline; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_event.registration_deadline IS '活动级报名截止时间，场次可覆盖';


--
-- TOC entry 8062 (class 0 OID 0)
-- Dependencies: 236
-- Name: COLUMN crm_event.waitlist_enabled; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_event.waitlist_enabled IS '满员时是否进入候补而非拒绝';


--
-- TOC entry 8063 (class 0 OID 0)
-- Dependencies: 236
-- Name: COLUMN crm_event.check_in_enabled; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_event.check_in_enabled IS '是否启用现场签到';


--
-- TOC entry 8064 (class 0 OID 0)
-- Dependencies: 236
-- Name: COLUMN crm_event.allow_walk_in; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_event.allow_walk_in IS '允许无预约现场补录并签到';


--
-- TOC entry 8065 (class 0 OID 0)
-- Dependencies: 236
-- Name: COLUMN crm_event.default_session_id; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_event.default_session_id IS '单场次快捷引用，可选';


--
-- TOC entry 8066 (class 0 OID 0)
-- Dependencies: 236
-- Name: COLUMN crm_event.wechat_mp_app_id; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_event.wechat_mp_app_id IS '本活动默认微信公众号 AppID；多号租户按活动配置，单号可空由运行时推断';


--
-- TOC entry 8067 (class 0 OID 0)
-- Dependencies: 236
-- Name: COLUMN crm_event.agenda; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_event.agenda IS '活动议程（Markdown 格式）';


--
-- TOC entry 8068 (class 0 OID 0)
-- Dependencies: 236
-- Name: COLUMN crm_event.language; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_event.language IS '活动语言，如 zh-CN / en-US';


--
-- TOC entry 529 (class 1259 OID 404670)
-- Name: crm_event_check_in; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_event_check_in (
    check_in_id character varying(64) NOT NULL,
    event_id character varying(64) NOT NULL,
    session_id character varying(64) NOT NULL,
    tenant_id character varying(64) NOT NULL,
    audience_key character varying(128) NOT NULL,
    registration_id character varying(64),
    check_in_method character varying(32) NOT NULL,
    checked_in_at timestamp without time zone DEFAULT now(),
    checked_in_by character varying(64),
    notes character varying(512),
    create_time timestamp without time zone DEFAULT now()
);


ALTER TABLE public.crm_event_check_in OWNER TO luohd;

--
-- TOC entry 515 (class 1259 OID 404482)
-- Name: crm_event_mp_outreach_log; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_event_mp_outreach_log (
    id bigint NOT NULL,
    tenant_id character varying(64) NOT NULL,
    event_id character varying(64) NOT NULL,
    app_id character varying(64) NOT NULL,
    open_id character varying(128) NOT NULL,
    trigger_type character varying(32) DEFAULT 'mp_subscribe_attributed'::character varying NOT NULL,
    preset_id bigint,
    registration_token_exp bigint,
    message_id bigint,
    status character varying(16) DEFAULT 'sent'::character varying NOT NULL,
    skip_reason character varying(64),
    errmsg text,
    create_time timestamp without time zone DEFAULT now()
);


ALTER TABLE public.crm_event_mp_outreach_log OWNER TO luohd;

--
-- TOC entry 514 (class 1259 OID 404481)
-- Name: crm_event_mp_outreach_log_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_event_mp_outreach_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_event_mp_outreach_log_id_seq OWNER TO luohd;

--
-- TOC entry 8072 (class 0 OID 0)
-- Dependencies: 514
-- Name: crm_event_mp_outreach_log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_event_mp_outreach_log_id_seq OWNED BY public.crm_event_mp_outreach_log.id;


--
-- TOC entry 524 (class 1259 OID 404596)
-- Name: crm_event_nurture_enrollment; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_event_nurture_enrollment (
    id bigint NOT NULL,
    tenant_id character varying(64) NOT NULL,
    event_id character varying(64) NOT NULL,
    sequence_id bigint NOT NULL,
    audience_key character varying(128) NOT NULL,
    lead_id character varying(64),
    contact_id character varying(64),
    wechat_mp_openid character varying(128),
    wechat_mp_app_id character varying(64),
    wecom_external_userid character varying(128),
    display_name character varying(200),
    mobile_phone character varying(32),
    status character varying(32) DEFAULT 'active'::character varying NOT NULL,
    current_step_order integer DEFAULT 1 NOT NULL,
    next_run_at timestamp with time zone NOT NULL,
    enrolled_at timestamp with time zone DEFAULT now() NOT NULL,
    exited_at timestamp with time zone,
    exit_reason character varying(64)
);


ALTER TABLE public.crm_event_nurture_enrollment OWNER TO luohd;

--
-- TOC entry 523 (class 1259 OID 404595)
-- Name: crm_event_nurture_enrollment_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_event_nurture_enrollment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_event_nurture_enrollment_id_seq OWNER TO luohd;

--
-- TOC entry 8075 (class 0 OID 0)
-- Dependencies: 523
-- Name: crm_event_nurture_enrollment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_event_nurture_enrollment_id_seq OWNED BY public.crm_event_nurture_enrollment.id;


--
-- TOC entry 520 (class 1259 OID 404560)
-- Name: crm_event_nurture_sequence; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_event_nurture_sequence (
    id bigint NOT NULL,
    tenant_id character varying(64) NOT NULL,
    event_id character varying(64) NOT NULL,
    name character varying(200) DEFAULT '默认培育序列'::character varying NOT NULL,
    enabled character(1) DEFAULT '0'::bpchar NOT NULL,
    trigger_type character varying(64) NOT NULL,
    trigger_config jsonb DEFAULT '{}'::jsonb NOT NULL,
    exit_on_contact character(1) DEFAULT '1'::bpchar NOT NULL,
    create_by character varying(64),
    create_time timestamp with time zone DEFAULT now(),
    update_by character varying(64),
    update_time timestamp with time zone,
    del_flag character(1) DEFAULT '0'::bpchar
);


ALTER TABLE public.crm_event_nurture_sequence OWNER TO luohd;

--
-- TOC entry 519 (class 1259 OID 404559)
-- Name: crm_event_nurture_sequence_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_event_nurture_sequence_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_event_nurture_sequence_id_seq OWNER TO luohd;

--
-- TOC entry 8078 (class 0 OID 0)
-- Dependencies: 519
-- Name: crm_event_nurture_sequence_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_event_nurture_sequence_id_seq OWNED BY public.crm_event_nurture_sequence.id;


--
-- TOC entry 522 (class 1259 OID 404577)
-- Name: crm_event_nurture_step; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_event_nurture_step (
    id bigint NOT NULL,
    tenant_id character varying(64) NOT NULL,
    sequence_id bigint NOT NULL,
    step_order integer NOT NULL,
    action_type character varying(32) NOT NULL,
    action_json jsonb DEFAULT '{}'::jsonb NOT NULL,
    delay_seconds integer DEFAULT 0 NOT NULL,
    create_time timestamp with time zone DEFAULT now()
);


ALTER TABLE public.crm_event_nurture_step OWNER TO luohd;

--
-- TOC entry 526 (class 1259 OID 404612)
-- Name: crm_event_nurture_step_execution; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_event_nurture_step_execution (
    id bigint NOT NULL,
    tenant_id character varying(64) NOT NULL,
    enrollment_id bigint NOT NULL,
    sequence_id bigint NOT NULL,
    step_id bigint NOT NULL,
    step_order integer NOT NULL,
    action_type character varying(32) NOT NULL,
    status character varying(32) NOT NULL,
    skip_reason character varying(64),
    external_ref character varying(128),
    error_msg character varying(500),
    create_time timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.crm_event_nurture_step_execution OWNER TO luohd;

--
-- TOC entry 525 (class 1259 OID 404611)
-- Name: crm_event_nurture_step_execution_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_event_nurture_step_execution_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_event_nurture_step_execution_id_seq OWNER TO luohd;

--
-- TOC entry 8082 (class 0 OID 0)
-- Dependencies: 525
-- Name: crm_event_nurture_step_execution_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_event_nurture_step_execution_id_seq OWNED BY public.crm_event_nurture_step_execution.id;


--
-- TOC entry 521 (class 1259 OID 404576)
-- Name: crm_event_nurture_step_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_event_nurture_step_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_event_nurture_step_id_seq OWNER TO luohd;

--
-- TOC entry 8084 (class 0 OID 0)
-- Dependencies: 521
-- Name: crm_event_nurture_step_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_event_nurture_step_id_seq OWNED BY public.crm_event_nurture_step.id;


--
-- TOC entry 516 (class 1259 OID 404537)
-- Name: crm_event_performance_snapshot; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_event_performance_snapshot (
    tenant_id character varying(64) NOT NULL,
    event_id character varying(64) NOT NULL,
    touch_count integer DEFAULT 0,
    lead_released_count integer DEFAULT 0,
    contact_converted_count integer DEFAULT 0,
    net_clv_total numeric(18,2) DEFAULT 0,
    paid numeric(18,2) DEFAULT 0,
    roi numeric(18,4),
    calculated_at timestamp with time zone NOT NULL
);


ALTER TABLE public.crm_event_performance_snapshot OWNER TO luohd;

--
-- TOC entry 528 (class 1259 OID 404645)
-- Name: crm_event_registration; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_event_registration (
    registration_id character varying(64) NOT NULL,
    event_id character varying(64) NOT NULL,
    session_id character varying(64) NOT NULL,
    tenant_id character varying(64) NOT NULL,
    lead_id character varying(64),
    audience_key character varying(128) NOT NULL,
    display_name character varying(256),
    mobile_phone character varying(32),
    registration_status character varying(32) DEFAULT 'confirmed'::character varying NOT NULL,
    registration_source character varying(32) DEFAULT 'h5'::character varying NOT NULL,
    ticket_token_hash character varying(128),
    registered_at timestamp without time zone DEFAULT now(),
    cancelled_at timestamp without time zone,
    create_by character varying(64),
    create_time timestamp without time zone DEFAULT now(),
    update_by character varying(64),
    update_time timestamp without time zone,
    contact_id character varying(64),
    form_data jsonb,
    custom_field text
);


ALTER TABLE public.crm_event_registration OWNER TO luohd;

--
-- TOC entry 8087 (class 0 OID 0)
-- Dependencies: 528
-- Name: COLUMN crm_event_registration.event_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_event_registration.event_id IS '营销活动 crm_event.event_id；当 registration_source=market_event 时为七库 crm_market_event.id';


--
-- TOC entry 8088 (class 0 OID 0)
-- Dependencies: 528
-- Name: COLUMN crm_event_registration.session_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_event_registration.session_id IS '营销场次 crm_event_session.session_id；market_event 行固定为 __market_event__';


--
-- TOC entry 8089 (class 0 OID 0)
-- Dependencies: 528
-- Name: COLUMN crm_event_registration.contact_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_event_registration.contact_id IS '已识别客户报名绑定的 Contact；与 lead_id 互斥，contact_known 分支填此列';


--
-- TOC entry 8090 (class 0 OID 0)
-- Dependencies: 528
-- Name: COLUMN crm_event_registration.form_data; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_event_registration.form_data IS 'H5 提交原始 Survey 键值对快照（leadName/mobilePhone/自定义题等）';


--
-- TOC entry 8091 (class 0 OID 0)
-- Dependencies: 528
-- Name: COLUMN crm_event_registration.custom_field; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_event_registration.custom_field IS '非标准字段 JSON 数组，与 crm_lead.custom_field 格式一致：[{fieldName,value}]';


--
-- TOC entry 527 (class 1259 OID 404628)
-- Name: crm_event_session; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_event_session (
    session_id character varying(64) NOT NULL,
    event_id character varying(64) NOT NULL,
    tenant_id character varying(64) NOT NULL,
    name character varying(128) NOT NULL,
    start_time timestamp without time zone,
    end_time timestamp without time zone,
    location character varying(512),
    capacity integer,
    registration_deadline timestamp without time zone,
    check_in_open_at timestamp without time zone,
    check_in_close_at timestamp without time zone,
    sort_order integer DEFAULT 0,
    status character varying(32) DEFAULT 'active'::character varying,
    create_by character varying(64),
    create_time timestamp without time zone DEFAULT now(),
    update_by character varying(64),
    update_time timestamp without time zone
);


ALTER TABLE public.crm_event_session OWNER TO luohd;

--
-- TOC entry 573 (class 1259 OID 409053)
-- Name: crm_event_speaker; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_event_speaker (
    id bigint NOT NULL,
    event_id character varying(100) NOT NULL,
    name character varying(100),
    avatar character varying(500),
    company character varying(255),
    "position" character varying(255),
    sort_order integer DEFAULT 0,
    create_time timestamp without time zone DEFAULT now(),
    description text
);


ALTER TABLE public.crm_event_speaker OWNER TO luohd;

--
-- TOC entry 8094 (class 0 OID 0)
-- Dependencies: 573
-- Name: TABLE crm_event_speaker; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_event_speaker IS '活动演讲者';


--
-- TOC entry 8095 (class 0 OID 0)
-- Dependencies: 573
-- Name: COLUMN crm_event_speaker.event_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_event_speaker.event_id IS 'FK crm_event.event_id';


--
-- TOC entry 8096 (class 0 OID 0)
-- Dependencies: 573
-- Name: COLUMN crm_event_speaker.name; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_event_speaker.name IS '演讲者姓名';


--
-- TOC entry 8097 (class 0 OID 0)
-- Dependencies: 573
-- Name: COLUMN crm_event_speaker.avatar; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_event_speaker.avatar IS '演讲者头像 URL';


--
-- TOC entry 8098 (class 0 OID 0)
-- Dependencies: 573
-- Name: COLUMN crm_event_speaker.company; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_event_speaker.company IS '演讲者公司';


--
-- TOC entry 8099 (class 0 OID 0)
-- Dependencies: 573
-- Name: COLUMN crm_event_speaker."position"; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_event_speaker."position" IS '演讲者职位';


--
-- TOC entry 8100 (class 0 OID 0)
-- Dependencies: 573
-- Name: COLUMN crm_event_speaker.sort_order; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_event_speaker.sort_order IS '排序，越小越靠前';


--
-- TOC entry 8101 (class 0 OID 0)
-- Dependencies: 573
-- Name: COLUMN crm_event_speaker.create_time; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_event_speaker.create_time IS '创建时间';


--
-- TOC entry 8102 (class 0 OID 0)
-- Dependencies: 573
-- Name: COLUMN crm_event_speaker.description; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_event_speaker.description IS '演讲者擅长领域（Markdown 格式）';


--
-- TOC entry 572 (class 1259 OID 409052)
-- Name: crm_event_speaker_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_event_speaker_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_event_speaker_id_seq OWNER TO luohd;

--
-- TOC entry 8104 (class 0 OID 0)
-- Dependencies: 572
-- Name: crm_event_speaker_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_event_speaker_id_seq OWNED BY public.crm_event_speaker.id;


--
-- TOC entry 518 (class 1259 OID 404548)
-- Name: crm_event_spend_item; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_event_spend_item (
    id bigint NOT NULL,
    tenant_id character varying(64) NOT NULL,
    event_id character varying(64) NOT NULL,
    channel_key character varying(32),
    category character varying(64) NOT NULL,
    amount numeric(18,2) DEFAULT 0 NOT NULL,
    spent_at timestamp with time zone,
    remark character varying(255),
    create_by character varying(64),
    create_time timestamp with time zone DEFAULT now(),
    update_time timestamp with time zone
);


ALTER TABLE public.crm_event_spend_item OWNER TO luohd;

--
-- TOC entry 517 (class 1259 OID 404547)
-- Name: crm_event_spend_item_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_event_spend_item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_event_spend_item_id_seq OWNER TO luohd;

--
-- TOC entry 8107 (class 0 OID 0)
-- Dependencies: 517
-- Name: crm_event_spend_item_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_event_spend_item_id_seq OWNED BY public.crm_event_spend_item.id;


--
-- TOC entry 594 (class 1259 OID 411320)
-- Name: crm_feishu_config; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_feishu_config (
    id bigint NOT NULL,
    app_id character varying(64) NOT NULL,
    app_secret_enc text,
    name character varying(128),
    connected boolean DEFAULT false NOT NULL,
    last_error text,
    create_by character varying(64),
    create_time timestamp with time zone DEFAULT now() NOT NULL,
    update_by character varying(64),
    update_time timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.crm_feishu_config OWNER TO luohd;

--
-- TOC entry 8109 (class 0 OID 0)
-- Dependencies: 594
-- Name: TABLE crm_feishu_config; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_feishu_config IS '飞书自建应用租户配置（一租户至多一行）';


--
-- TOC entry 8110 (class 0 OID 0)
-- Dependencies: 594
-- Name: COLUMN crm_feishu_config.app_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_feishu_config.app_id IS '飞书 App ID';


--
-- TOC entry 8111 (class 0 OID 0)
-- Dependencies: 594
-- Name: COLUMN crm_feishu_config.app_secret_enc; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_feishu_config.app_secret_enc IS 'App Secret（AES 加密存储）';


--
-- TOC entry 8112 (class 0 OID 0)
-- Dependencies: 594
-- Name: COLUMN crm_feishu_config.name; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_feishu_config.name IS '备注名称';


--
-- TOC entry 8113 (class 0 OID 0)
-- Dependencies: 594
-- Name: COLUMN crm_feishu_config.connected; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_feishu_config.connected IS '是否已验证连通（tenant_access_token 换票成功）';


--
-- TOC entry 8114 (class 0 OID 0)
-- Dependencies: 594
-- Name: COLUMN crm_feishu_config.last_error; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_feishu_config.last_error IS '最近一次错误摘要';


--
-- TOC entry 593 (class 1259 OID 411319)
-- Name: crm_feishu_config_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_feishu_config_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_feishu_config_id_seq OWNER TO luohd;

--
-- TOC entry 8116 (class 0 OID 0)
-- Dependencies: 593
-- Name: crm_feishu_config_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_feishu_config_id_seq OWNED BY public.crm_feishu_config.id;


--
-- TOC entry 453 (class 1259 OID 371800)
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


ALTER TABLE public.crm_follow_up_cadence OWNER TO luohd;

--
-- TOC entry 8118 (class 0 OID 0)
-- Dependencies: 453
-- Name: TABLE crm_follow_up_cadence; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_follow_up_cadence IS 'Tenant follow-up cadences: ordered drip steps stored as JSON';


--
-- TOC entry 8119 (class 0 OID 0)
-- Dependencies: 453
-- Name: COLUMN crm_follow_up_cadence.cadence_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_follow_up_cadence.cadence_id IS 'UUID 主键';


--
-- TOC entry 8120 (class 0 OID 0)
-- Dependencies: 453
-- Name: COLUMN crm_follow_up_cadence.tenant_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_follow_up_cadence.tenant_id IS '租户标识（tenant 请求头）';


--
-- TOC entry 8121 (class 0 OID 0)
-- Dependencies: 453
-- Name: COLUMN crm_follow_up_cadence.enabled; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_follow_up_cadence.enabled IS '0 disabled, 1 enabled';


--
-- TOC entry 8122 (class 0 OID 0)
-- Dependencies: 453
-- Name: COLUMN crm_follow_up_cadence.start_trigger; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_follow_up_cadence.start_trigger IS 'Human-readable starting event, e.g. Initial Email Sent';


--
-- TOC entry 8123 (class 0 OID 0)
-- Dependencies: 453
-- Name: COLUMN crm_follow_up_cadence.steps_json; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_follow_up_cadence.steps_json IS 'JSON array of steps: [{id, order, waitDays, actionType, title, body}]';


--
-- TOC entry 8124 (class 0 OID 0)
-- Dependencies: 453
-- Name: COLUMN crm_follow_up_cadence.del_flag; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_follow_up_cadence.del_flag IS '逻辑删除 0 正常';


--
-- TOC entry 632 (class 1259 OID 413302)
-- Name: crm_followup_rule; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_followup_rule (
    rule_id character varying(36) NOT NULL,
    tenant_id character varying(64) NOT NULL,
    name character varying(128) NOT NULL,
    description character varying(500),
    enabled character(1) DEFAULT '1'::bpchar NOT NULL,
    is_system character(1) DEFAULT '0'::bpchar NOT NULL,
    priority integer DEFAULT 100 NOT NULL,
    trigger_type character varying(64) NOT NULL,
    trigger_config text,
    delay_mode character varying(16) DEFAULT 'immediate'::character varying NOT NULL,
    delay_value integer,
    delay_unit character varying(8),
    delay_field character varying(64),
    task_template text NOT NULL,
    dedupe_config text,
    workflow_id character varying(36),
    catalog_key character varying(64),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    del_flag character(1) DEFAULT '0'::bpchar NOT NULL
);


ALTER TABLE public.crm_followup_rule OWNER TO luohd;

--
-- TOC entry 8126 (class 0 OID 0)
-- Dependencies: 632
-- Name: TABLE crm_followup_rule; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_followup_rule IS 'Tenant follow-up rules: event -> delayed task creation';


--
-- TOC entry 8127 (class 0 OID 0)
-- Dependencies: 632
-- Name: COLUMN crm_followup_rule.is_system; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_followup_rule.is_system IS '1 = seeded system rule, cannot delete';


--
-- TOC entry 8128 (class 0 OID 0)
-- Dependencies: 632
-- Name: COLUMN crm_followup_rule.trigger_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_followup_rule.trigger_type IS 'opportunity.won | lead.assigned | patient.recall_due | appointment.noshow';


--
-- TOC entry 8129 (class 0 OID 0)
-- Dependencies: 632
-- Name: COLUMN crm_followup_rule.delay_mode; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_followup_rule.delay_mode IS 'immediate | fixed_delay | business_days | until_field';


--
-- TOC entry 8130 (class 0 OID 0)
-- Dependencies: 632
-- Name: COLUMN crm_followup_rule.catalog_key; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_followup_rule.catalog_key IS 'Optional link to workflow-automation catalog key';


--
-- TOC entry 633 (class 1259 OID 413315)
-- Name: crm_followup_rule_enrollment; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_followup_rule_enrollment (
    enrollment_id character varying(36) NOT NULL,
    tenant_id character varying(64) NOT NULL,
    rule_id character varying(36) NOT NULL,
    trigger_type character varying(64) NOT NULL,
    subject_type character varying(32) NOT NULL,
    subject_id character varying(100) NOT NULL,
    status character varying(16) DEFAULT 'pending'::character varying NOT NULL,
    next_run_at timestamp with time zone NOT NULL,
    idempotency_key character varying(64) NOT NULL,
    trigger_payload text,
    task_id character varying(100),
    skip_reason character varying(128),
    cancel_reason character varying(128),
    retry_count integer DEFAULT 0 NOT NULL,
    fired_time timestamp without time zone,
    create_time timestamp without time zone,
    update_time timestamp without time zone
);


ALTER TABLE public.crm_followup_rule_enrollment OWNER TO luohd;

--
-- TOC entry 8132 (class 0 OID 0)
-- Dependencies: 633
-- Name: TABLE crm_followup_rule_enrollment; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_followup_rule_enrollment IS 'Delayed follow-up rule executions';


--
-- TOC entry 8133 (class 0 OID 0)
-- Dependencies: 633
-- Name: COLUMN crm_followup_rule_enrollment.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_followup_rule_enrollment.status IS 'pending | fired | skipped | cancelled';


--
-- TOC entry 634 (class 1259 OID 413328)
-- Name: crm_followup_rule_execution_log; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_followup_rule_execution_log (
    execution_id character varying(36) NOT NULL,
    tenant_id character varying(64) NOT NULL,
    enrollment_id character varying(36),
    rule_id character varying(36) NOT NULL,
    action character varying(16) NOT NULL,
    task_id character varying(100),
    skip_reason character varying(128),
    detail_json text,
    create_time timestamp without time zone
);


ALTER TABLE public.crm_followup_rule_execution_log OWNER TO luohd;

--
-- TOC entry 8135 (class 0 OID 0)
-- Dependencies: 634
-- Name: TABLE crm_followup_rule_execution_log; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_followup_rule_execution_log IS 'Optional follow-up rule audit log (P2)';


--
-- TOC entry 237 (class 1259 OID 360515)
-- Name: crm_forecast; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_forecast OWNER TO "_tenant";

--
-- TOC entry 238 (class 1259 OID 360531)
-- Name: crm_fundinvestor; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_fundinvestor OWNER TO "_tenant";

--
-- TOC entry 239 (class 1259 OID 360544)
-- Name: crm_fundpaid; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_fundpaid OWNER TO "_tenant";

--
-- TOC entry 598 (class 1259 OID 411726)
-- Name: crm_government_agency; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_government_agency (
    id character varying(64) NOT NULL,
    name_cn character varying(512) NOT NULL,
    name_vn character varying(512) NOT NULL,
    name_en character varying(512),
    abbreviation character varying(64) DEFAULT ''::character varying NOT NULL,
    level character varying(64) NOT NULL,
    category character varying(128) NOT NULL,
    province character varying(256),
    address text,
    core_responsibilities text DEFAULT ''::text NOT NULL,
    approval_items jsonb DEFAULT '[]'::jsonb NOT NULL,
    one_stop_services text,
    contact_phone character varying(128),
    email character varying(256),
    website character varying(1024),
    working_hours character varying(256),
    notes text,
    created_at character varying(32),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    del_flag character(1) DEFAULT '0'::bpchar NOT NULL
);


ALTER TABLE public.crm_government_agency OWNER TO luohd;

--
-- TOC entry 8140 (class 0 OID 0)
-- Dependencies: 598
-- Name: TABLE crm_government_agency; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_government_agency IS '越南政府招商/审批机构目录';


--
-- TOC entry 8141 (class 0 OID 0)
-- Dependencies: 598
-- Name: COLUMN crm_government_agency.level; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_government_agency.level IS '中央部委/级 | 地方省级厅局 | 园区一站式窗口/管理局';


--
-- TOC entry 8142 (class 0 OID 0)
-- Dependencies: 598
-- Name: COLUMN crm_government_agency.category; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_government_agency.category IS '职能类别（投资审批/财税/外汇/环保/消防/一站式等）';


--
-- TOC entry 8143 (class 0 OID 0)
-- Dependencies: 598
-- Name: COLUMN crm_government_agency.approval_items; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_government_agency.approval_items IS 'JSON string array of approval items';


--
-- TOC entry 8144 (class 0 OID 0)
-- Dependencies: 598
-- Name: COLUMN crm_government_agency.del_flag; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_government_agency.del_flag IS '0 active, 1 soft-deleted';


--
-- TOC entry 240 (class 1259 OID 360555)
-- Name: crm_household; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_household OWNER TO "_tenant";

--
-- TOC entry 241 (class 1259 OID 360563)
-- Name: crm_householdmember; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_householdmember OWNER TO "_tenant";

--
-- TOC entry 537 (class 1259 OID 408157)
-- Name: crm_identity_conflict; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_identity_conflict (
    id bigint NOT NULL,
    identity_type character varying(32) NOT NULL,
    identity_value character varying(256) NOT NULL,
    existing_contact_id character varying(64) NOT NULL,
    incoming_contact_id character varying(64) DEFAULT ''::character varying NOT NULL,
    source character varying(32),
    status character varying(16) DEFAULT 'pending'::character varying NOT NULL,
    resolved_by character varying(64),
    resolved_at timestamp without time zone,
    raw_context text,
    create_time timestamp without time zone DEFAULT now(),
    update_time timestamp without time zone DEFAULT now()
);


ALTER TABLE public.crm_identity_conflict OWNER TO luohd;

--
-- TOC entry 8148 (class 0 OID 0)
-- Dependencies: 537
-- Name: TABLE crm_identity_conflict; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_identity_conflict IS '身份冲突待人工合并：同一身份值被解析到不同 Contact 时记录，供运营合并';


--
-- TOC entry 8149 (class 0 OID 0)
-- Dependencies: 537
-- Name: COLUMN crm_identity_conflict.identity_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_identity_conflict.identity_type IS 'mobile_phone / email / wechat_unionid / wecom_external_userid / wechat_mp_openid ...';


--
-- TOC entry 8150 (class 0 OID 0)
-- Dependencies: 537
-- Name: COLUMN crm_identity_conflict.existing_contact_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_identity_conflict.existing_contact_id IS '该身份当前已绑定的 Contact';


--
-- TOC entry 8151 (class 0 OID 0)
-- Dependencies: 537
-- Name: COLUMN crm_identity_conflict.incoming_contact_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_identity_conflict.incoming_contact_id IS '本次尝试绑定的 Contact，可为空';


--
-- TOC entry 8152 (class 0 OID 0)
-- Dependencies: 537
-- Name: COLUMN crm_identity_conflict.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_identity_conflict.status IS 'pending 待处理 | resolved 已合并 | ignored 已忽略';


--
-- TOC entry 536 (class 1259 OID 408156)
-- Name: crm_identity_conflict_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_identity_conflict_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_identity_conflict_id_seq OWNER TO luohd;

--
-- TOC entry 8154 (class 0 OID 0)
-- Dependencies: 536
-- Name: crm_identity_conflict_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_identity_conflict_id_seq OWNED BY public.crm_identity_conflict.id;


--
-- TOC entry 643 (class 1259 OID 413426)
-- Name: crm_inbox_channel_settings; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_inbox_channel_settings (
    tenant_id character varying(100) NOT NULL,
    whatsapp_enabled smallint DEFAULT 1 NOT NULL,
    messenger_enabled smallint DEFAULT 1 NOT NULL,
    wechat_mp_enabled smallint DEFAULT 1 NOT NULL,
    webchat_enabled smallint DEFAULT 1 NOT NULL,
    updated_by character varying(64),
    updated_at timestamp without time zone,
    wecom_kf_enabled smallint DEFAULT 1 NOT NULL
);


ALTER TABLE public.crm_inbox_channel_settings OWNER TO luohd;

--
-- TOC entry 8156 (class 0 OID 0)
-- Dependencies: 643
-- Name: TABLE crm_inbox_channel_settings; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_inbox_channel_settings IS 'Unified Inbox channel enable switches per tenant';


--
-- TOC entry 8157 (class 0 OID 0)
-- Dependencies: 643
-- Name: COLUMN crm_inbox_channel_settings.wecom_kf_enabled; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_inbox_channel_settings.wecom_kf_enabled IS '微信客服 Inbox 渠道开关 1=开 0=关';


--
-- TOC entry 595 (class 1259 OID 411431)
-- Name: crm_industrial_park; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_industrial_park (
    id character varying(64) NOT NULL,
    name_cn character varying(256) NOT NULL,
    name_native character varying(512) DEFAULT ''::character varying NOT NULL,
    region character varying(16) NOT NULL,
    province character varying(128) DEFAULT ''::character varying NOT NULL,
    address text DEFAULT ''::text NOT NULL,
    coordinates character varying(64) DEFAULT ''::character varying NOT NULL,
    lat double precision DEFAULT 0 NOT NULL,
    lng double precision DEFAULT 0 NOT NULL,
    established_date character varying(32) DEFAULT ''::character varying NOT NULL,
    developer character varying(512) DEFAULT ''::character varying NOT NULL,
    website character varying(1024) DEFAULT ''::character varying NOT NULL,
    total_area_ha numeric(14,2) DEFAULT 0 NOT NULL,
    developed_area_ha numeric(14,2) DEFAULT 0 NOT NULL,
    occupancy_rate numeric(6,2) DEFAULT 0 NOT NULL,
    dominant_industries jsonb DEFAULT '[]'::jsonb NOT NULL,
    park_level character varying(64) DEFAULT ''::character varying NOT NULL,
    anchor_tenants jsonb DEFAULT '[]'::jsonb NOT NULL,
    land_rent_usd_per_sqm numeric(14,2) DEFAULT 0 NOT NULL,
    lease_term_years integer DEFAULT 0 NOT NULL,
    lease_expiry_year integer DEFAULT 0 NOT NULL,
    standard_factory_rent_usd_per_sqm_month numeric(14,2) DEFAULT 0 NOT NULL,
    management_fee_usd_per_sqm_year numeric(14,2) DEFAULT 0 NOT NULL,
    electricity_rate_usd_per_kwh text DEFAULT ''::text NOT NULL,
    water_rate_usd_per_cum numeric(14,4) DEFAULT 0 NOT NULL,
    blue_collar_wage_usd numeric(14,2) DEFAULT 0 NOT NULL,
    white_collar_wage_usd numeric(14,2) DEFAULT 0 NOT NULL,
    cit_incentive text DEFAULT ''::text NOT NULL,
    import_tariff_incentive text DEFAULT ''::text NOT NULL,
    is_sez boolean DEFAULT false NOT NULL,
    special_incentives text DEFAULT ''::text NOT NULL,
    distance_to_sea_port_km numeric(10,2) DEFAULT 0 NOT NULL,
    sea_port_name character varying(256) DEFAULT ''::character varying NOT NULL,
    distance_to_airport_km numeric(10,2) DEFAULT 0 NOT NULL,
    airport_name character varying(256) DEFAULT ''::character varying NOT NULL,
    distance_to_china_border_km numeric(10,2) DEFAULT 0 NOT NULL,
    border_port_name character varying(256) DEFAULT ''::character varying NOT NULL,
    power_reliability character varying(128) DEFAULT ''::character varying NOT NULL,
    sewage_treatment_capacity text DEFAULT ''::text NOT NULL,
    has_employee_dormitory boolean DEFAULT false NOT NULL,
    surrounding_amenities text DEFAULT ''::text NOT NULL,
    labor_supply character varying(32) DEFAULT ''::character varying NOT NULL,
    recruitment_sources text DEFAULT ''::text NOT NULL,
    has_vocational_school boolean DEFAULT false NOT NULL,
    master_plan_url character varying(2048),
    brochure_url character varying(2048),
    photos jsonb DEFAULT '[]'::jsonb NOT NULL,
    data_source text DEFAULT ''::text NOT NULL,
    last_updated date,
    verification_status character varying(32) DEFAULT '未核实'::character varying NOT NULL,
    consultant_contact text DEFAULT ''::text NOT NULL,
    contact_page_url character varying(2048),
    official_phone character varying(256),
    official_email character varying(256),
    operating_hours character varying(256),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    del_flag character(1) DEFAULT '0'::bpchar NOT NULL,
    encouraged_industries jsonb DEFAULT '[]'::jsonb NOT NULL,
    operation_expiry_date character varying(32) DEFAULT ''::character varying NOT NULL,
    nearest_highway_name character varying(512) DEFAULT ''::character varying NOT NULL,
    distance_to_highway_km numeric(10,2) DEFAULT 0 NOT NULL,
    nearest_railway_name character varying(512) DEFAULT ''::character varying NOT NULL,
    distance_to_railway_km numeric(10,2) DEFAULT 0 NOT NULL,
    main_road_spec text DEFAULT ''::text NOT NULL,
    internal_road_spec text DEFAULT ''::text NOT NULL,
    power_supply_detail text DEFAULT ''::text NOT NULL,
    has_national_grid boolean DEFAULT false NOT NULL,
    has_standby_power boolean DEFAULT false NOT NULL,
    water_supply_source text DEFAULT ''::text NOT NULL,
    water_supply_capacity text DEFAULT ''::text NOT NULL,
    competitive_advantages text DEFAULT ''::text NOT NULL
);


ALTER TABLE public.crm_industrial_park OWNER TO luohd;

--
-- TOC entry 8159 (class 0 OID 0)
-- Dependencies: 595
-- Name: TABLE crm_industrial_park; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_industrial_park IS '越南工业园区招商信息库';


--
-- TOC entry 8160 (class 0 OID 0)
-- Dependencies: 595
-- Name: COLUMN crm_industrial_park.region; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_industrial_park.region IS '北部 | 中部 | 南部';


--
-- TOC entry 8161 (class 0 OID 0)
-- Dependencies: 595
-- Name: COLUMN crm_industrial_park.dominant_industries; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_industrial_park.dominant_industries IS 'JSON string array';


--
-- TOC entry 8162 (class 0 OID 0)
-- Dependencies: 595
-- Name: COLUMN crm_industrial_park.anchor_tenants; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_industrial_park.anchor_tenants IS 'JSON string array';


--
-- TOC entry 8163 (class 0 OID 0)
-- Dependencies: 595
-- Name: COLUMN crm_industrial_park.photos; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_industrial_park.photos IS 'JSON string array of image URLs';


--
-- TOC entry 8164 (class 0 OID 0)
-- Dependencies: 595
-- Name: COLUMN crm_industrial_park.verification_status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_industrial_park.verification_status IS '未核实 | AI抓取 | 人工核实';


--
-- TOC entry 8165 (class 0 OID 0)
-- Dependencies: 595
-- Name: COLUMN crm_industrial_park.del_flag; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_industrial_park.del_flag IS '0 active, 1 soft-deleted';


--
-- TOC entry 8166 (class 0 OID 0)
-- Dependencies: 595
-- Name: COLUMN crm_industrial_park.encouraged_industries; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_industrial_park.encouraged_industries IS 'JSON string array — industries encouraged for investment';


--
-- TOC entry 8167 (class 0 OID 0)
-- Dependencies: 595
-- Name: COLUMN crm_industrial_park.operation_expiry_date; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_industrial_park.operation_expiry_date IS 'Full operation/lease expiry date e.g. 2058-01-29';


--
-- TOC entry 242 (class 1259 OID 360571)
-- Name: crm_interest; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_interest OWNER TO "_tenant";

--
-- TOC entry 243 (class 1259 OID 360586)
-- Name: crm_itinerary; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_itinerary OWNER TO "_tenant";

--
-- TOC entry 580 (class 1259 OID 410949)
-- Name: crm_kb_chunk; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_kb_chunk (
    chunk_id character varying(36) NOT NULL,
    document_id character varying(36) NOT NULL,
    chunk_index integer DEFAULT 0 NOT NULL,
    chunk_text text NOT NULL,
    token_estimate integer,
    metadata jsonb DEFAULT '{}'::jsonb NOT NULL,
    embedding public.vector(1024) NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.crm_kb_chunk OWNER TO luohd;

--
-- TOC entry 8171 (class 0 OID 0)
-- Dependencies: 580
-- Name: TABLE crm_kb_chunk; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_kb_chunk IS '知识库切块 + 向量（pgvector）';


--
-- TOC entry 579 (class 1259 OID 410931)
-- Name: crm_kb_document; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_kb_document (
    document_id character varying(36) NOT NULL,
    title character varying(512) DEFAULT ''::character varying NOT NULL,
    category character varying(64) DEFAULT 'other'::character varying NOT NULL,
    content_md text DEFAULT ''::text NOT NULL,
    status character varying(32) DEFAULT 'draft'::character varying NOT NULL,
    source_type character varying(64) DEFAULT 'manual'::character varying NOT NULL,
    source_id character varying(64),
    metadata jsonb DEFAULT '{}'::jsonb NOT NULL,
    content_hash character varying(64),
    index_status character varying(32) DEFAULT 'pending'::character varying NOT NULL,
    index_error character varying(1024),
    index_time timestamp without time zone,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    del_flag character(1) DEFAULT '0'::bpchar NOT NULL
);


ALTER TABLE public.crm_kb_document OWNER TO luohd;

--
-- TOC entry 8173 (class 0 OID 0)
-- Dependencies: 579
-- Name: TABLE crm_kb_document; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_kb_document IS '企业知识库文档（RAG）';


--
-- TOC entry 8174 (class 0 OID 0)
-- Dependencies: 579
-- Name: COLUMN crm_kb_document.category; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_kb_document.category IS 'company_intro/business/service/brand/faq/case/persona/product/marketing/other';


--
-- TOC entry 8175 (class 0 OID 0)
-- Dependencies: 579
-- Name: COLUMN crm_kb_document.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_kb_document.status IS 'draft/published/archived，仅 published 参与检索';


--
-- TOC entry 8176 (class 0 OID 0)
-- Dependencies: 579
-- Name: COLUMN crm_kb_document.source_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_kb_document.source_type IS 'manual/organization/product/sales_script/marketing_content';


--
-- TOC entry 8177 (class 0 OID 0)
-- Dependencies: 579
-- Name: COLUMN crm_kb_document.content_hash; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_kb_document.content_hash IS '正文哈希，未变更跳过重嵌';


--
-- TOC entry 8178 (class 0 OID 0)
-- Dependencies: 579
-- Name: COLUMN crm_kb_document.index_status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_kb_document.index_status IS 'pending/indexed/failed/skipped';


--
-- TOC entry 244 (class 1259 OID 360594)
-- Name: crm_lead; Type: TABLE; Schema: public; Owner: _tenant
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
    wechat_mp_openid character varying(128),
    wechat_mp_app_id character varying(64),
    wecom_external_userid character varying(64),
    wecom_follow_userid character varying(64),
    last_followup_at timestamp with time zone,
    assigned_at timestamp with time zone
);


ALTER TABLE public.crm_lead OWNER TO "_tenant";

--
-- TOC entry 8180 (class 0 OID 0)
-- Dependencies: 244
-- Name: COLUMN crm_lead.wechat_mp_openid; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_lead.wechat_mp_openid IS 'token payload o 验签后写入，供受众合并';


--
-- TOC entry 8181 (class 0 OID 0)
-- Dependencies: 244
-- Name: COLUMN crm_lead.wechat_mp_app_id; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_lead.wechat_mp_app_id IS 'token payload a，与 openid 成对';


--
-- TOC entry 8182 (class 0 OID 0)
-- Dependencies: 244
-- Name: COLUMN crm_lead.wecom_external_userid; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_lead.wecom_external_userid IS '企业微信外部联系人ID';


--
-- TOC entry 8183 (class 0 OID 0)
-- Dependencies: 244
-- Name: COLUMN crm_lead.wecom_follow_userid; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_lead.wecom_follow_userid IS '企业微信跟进成员userid';


--
-- TOC entry 543 (class 1259 OID 408465)
-- Name: crm_lead_convert_config; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_lead_convert_config (
    config_id character varying(36) NOT NULL,
    tenant_id character varying(64) NOT NULL,
    auto_convert_on_first_visit boolean DEFAULT true NOT NULL,
    auto_convert_on_check_in boolean DEFAULT true NOT NULL,
    auto_convert_on_registration boolean DEFAULT false NOT NULL,
    merge_on_duplicate_phone boolean DEFAULT true NOT NULL,
    default_org_id character varying(36),
    create_time timestamp without time zone,
    update_time timestamp without time zone,
    contact_owner_mode character varying(32) DEFAULT 'keep_lead_owner'::character varying NOT NULL,
    contact_owner_user_id bigint
);


ALTER TABLE public.crm_lead_convert_config OWNER TO luohd;

--
-- TOC entry 637 (class 1259 OID 413362)
-- Name: crm_lead_dedupe_config; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_lead_dedupe_config (
    config_id character varying(36) NOT NULL,
    lead_match_fields text DEFAULT '["mobilePhone","wecomExternalUserid"]'::text NOT NULL,
    contact_match_fields text DEFAULT '["mobilePhone","wecomExternalUserid"]'::text NOT NULL,
    match_mode character varying(8) DEFAULT 'any'::character varying NOT NULL,
    create_time timestamp without time zone,
    update_time timestamp without time zone
);


ALTER TABLE public.crm_lead_dedupe_config OWNER TO luohd;

--
-- TOC entry 545 (class 1259 OID 408655)
-- Name: crm_lead_pool_config; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_lead_pool_config (
    config_id character varying(36) NOT NULL,
    claim_daily_limit integer DEFAULT 30 NOT NULL,
    protect_days integer DEFAULT 7 NOT NULL,
    recycle_enabled boolean DEFAULT true NOT NULL,
    default_pool_org_scope character varying(32) DEFAULT 'dept'::character varying NOT NULL,
    create_time timestamp without time zone,
    update_time timestamp without time zone,
    qualify_template_id character varying(36),
    idle_to_pool_hours integer DEFAULT 72 NOT NULL,
    max_hold_count integer DEFAULT 100 NOT NULL
);


ALTER TABLE public.crm_lead_pool_config OWNER TO luohd;

--
-- TOC entry 602 (class 1259 OID 412779)
-- Name: crm_market_event; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_market_event (
    id character varying(64) NOT NULL,
    title_cn character varying(512) NOT NULL,
    title_vn character varying(512) NOT NULL,
    title_en character varying(512),
    event_type character varying(128) NOT NULL,
    status character varying(64) NOT NULL,
    organizer character varying(512) NOT NULL,
    industry character varying(512) DEFAULT ''::character varying NOT NULL,
    start_date character varying(32) NOT NULL,
    end_date character varying(32) NOT NULL,
    province character varying(256) NOT NULL,
    venue text DEFAULT ''::text NOT NULL,
    target_audience text DEFAULT ''::text NOT NULL,
    scale character varying(512),
    entry_fee character varying(512),
    registration_link character varying(2048),
    contact_person character varying(512),
    highlights text NOT NULL,
    notes text,
    related_park_id character varying(64),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    del_flag character(1) DEFAULT '0'::bpchar NOT NULL,
    cover_url character varying(2048)
);


ALTER TABLE public.crm_market_event OWNER TO luohd;

--
-- TOC entry 8188 (class 0 OID 0)
-- Dependencies: 602
-- Name: TABLE crm_market_event; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_market_event IS '越南出海市场活动主档';


--
-- TOC entry 8189 (class 0 OID 0)
-- Dependencies: 602
-- Name: COLUMN crm_market_event.event_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_market_event.event_type IS '展会/博览会 (Exhibition) | 招商研讨会/推介会 (Seminar) | 政企对账会 (Dialogue) | 商务考察团 (Trade Mission) | 行业峰会/论坛 (Summit) | 企业开业/投产典礼 (Opening)';


--
-- TOC entry 8190 (class 0 OID 0)
-- Dependencies: 602
-- Name: COLUMN crm_market_event.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_market_event.status IS '即将举办 (Upcoming) | 进行中 (Ongoing) | 已结束 (Completed) | 筹备中 (Planning)';


--
-- TOC entry 8191 (class 0 OID 0)
-- Dependencies: 602
-- Name: COLUMN crm_market_event.related_park_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_market_event.related_park_id IS '逻辑关联园区 ID → crm_industrial_park.id';


--
-- TOC entry 8192 (class 0 OID 0)
-- Dependencies: 602
-- Name: COLUMN crm_market_event.del_flag; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_market_event.del_flag IS '0 active, 1 soft-deleted';


--
-- TOC entry 8193 (class 0 OID 0)
-- Dependencies: 602
-- Name: COLUMN crm_market_event.cover_url; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_market_event.cover_url IS '封面图（upload 相对路径或绝对 URL）';


--
-- TOC entry 574 (class 1259 OID 409156)
-- Name: crm_marketing_content; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_marketing_content (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    title character varying(128) NOT NULL,
    author character varying(64),
    digest character varying(512),
    cover_url character varying(1024),
    content_md text DEFAULT ''::text NOT NULL,
    content_html text,
    status character varying(16) DEFAULT 'draft'::character varying NOT NULL,
    create_by character varying(64),
    create_time timestamp with time zone DEFAULT now() NOT NULL,
    update_by character varying(64),
    update_time timestamp with time zone DEFAULT now() NOT NULL,
    website_visible boolean DEFAULT false NOT NULL,
    website_published_at timestamp with time zone
);


ALTER TABLE public.crm_marketing_content OWNER TO luohd;

--
-- TOC entry 8195 (class 0 OID 0)
-- Dependencies: 574
-- Name: TABLE crm_marketing_content; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_marketing_content IS '营销内容（Markdown 主数据）';


--
-- TOC entry 8196 (class 0 OID 0)
-- Dependencies: 574
-- Name: COLUMN crm_marketing_content.content_md; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_marketing_content.content_md IS 'Markdown 编辑源（唯一权威编辑字段）';


--
-- TOC entry 8197 (class 0 OID 0)
-- Dependencies: 574
-- Name: COLUMN crm_marketing_content.content_html; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_marketing_content.content_html IS 'Paper 生成的微信友好 HTML 缓存；可为空';


--
-- TOC entry 8198 (class 0 OID 0)
-- Dependencies: 574
-- Name: COLUMN crm_marketing_content.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_marketing_content.status IS 'draft=草稿; ready=可发布; archived=已归档';


--
-- TOC entry 8199 (class 0 OID 0)
-- Dependencies: 574
-- Name: COLUMN crm_marketing_content.website_visible; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_marketing_content.website_visible IS '是否可被租户官网公开 API / contentList block 读取';


--
-- TOC entry 8200 (class 0 OID 0)
-- Dependencies: 574
-- Name: COLUMN crm_marketing_content.website_published_at; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_marketing_content.website_published_at IS '最近一次设为官网可见的时间；取消可见时清空';


--
-- TOC entry 575 (class 1259 OID 409170)
-- Name: crm_marketing_content_publish; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_marketing_content_publish (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    content_id uuid NOT NULL,
    channel character varying(32) DEFAULT 'wechat_mp'::character varying NOT NULL,
    app_id character varying(64) NOT NULL,
    media_id character varying(128),
    publish_id character varying(128),
    article_id character varying(128),
    article_url character varying(1024),
    thumb_media_id character varying(128),
    status character varying(16) DEFAULT 'synced'::character varying NOT NULL,
    error_msg character varying(1024),
    published_at timestamp with time zone,
    create_time timestamp with time zone DEFAULT now() NOT NULL,
    update_time timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.crm_marketing_content_publish OWNER TO luohd;

--
-- TOC entry 8202 (class 0 OID 0)
-- Dependencies: 575
-- Name: TABLE crm_marketing_content_publish; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_marketing_content_publish IS '营销内容渠道发布记录（一篇内容+一公众号一行）';


--
-- TOC entry 8203 (class 0 OID 0)
-- Dependencies: 575
-- Name: COLUMN crm_marketing_content_publish.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_marketing_content_publish.status IS 'synced=已同步草稿未发; publishing=已提交 freepublish; published=已发表; failed=失败';


--
-- TOC entry 468 (class 1259 OID 379602)
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


ALTER TABLE public.crm_mcp_api_key OWNER TO luohd;

--
-- TOC entry 8205 (class 0 OID 0)
-- Dependencies: 468
-- Name: TABLE crm_mcp_api_key; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_mcp_api_key IS '入站 MCP Server API Key';


--
-- TOC entry 8206 (class 0 OID 0)
-- Dependencies: 468
-- Name: COLUMN crm_mcp_api_key.key_hash; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_mcp_api_key.key_hash IS 'secret 部分 SHA-256 hex，禁止存明文';


--
-- TOC entry 8207 (class 0 OID 0)
-- Dependencies: 468
-- Name: COLUMN crm_mcp_api_key.allowed_tools; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_mcp_api_key.allowed_tools IS 'JSON 字符串数组，null 表示全量 Agent 工具';


--
-- TOC entry 8208 (class 0 OID 0)
-- Dependencies: 468
-- Name: COLUMN crm_mcp_api_key.service_token_enc; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_mcp_api_key.service_token_enc IS 'AES 加密的服务账号 JWT';


--
-- TOC entry 467 (class 1259 OID 379601)
-- Name: crm_mcp_api_key_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_mcp_api_key_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_mcp_api_key_id_seq OWNER TO luohd;

--
-- TOC entry 8210 (class 0 OID 0)
-- Dependencies: 467
-- Name: crm_mcp_api_key_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_mcp_api_key_id_seq OWNED BY public.crm_mcp_api_key.id;


--
-- TOC entry 470 (class 1259 OID 379619)
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


ALTER TABLE public.crm_mcp_audit_log OWNER TO luohd;

--
-- TOC entry 8212 (class 0 OID 0)
-- Dependencies: 470
-- Name: TABLE crm_mcp_audit_log; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_mcp_audit_log IS '入站 MCP tools/call 审计（Next 异步上报）';


--
-- TOC entry 469 (class 1259 OID 379618)
-- Name: crm_mcp_audit_log_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_mcp_audit_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_mcp_audit_log_id_seq OWNER TO luohd;

--
-- TOC entry 8214 (class 0 OID 0)
-- Dependencies: 469
-- Name: crm_mcp_audit_log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_mcp_audit_log_id_seq OWNED BY public.crm_mcp_audit_log.id;


--
-- TOC entry 460 (class 1259 OID 379491)
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


ALTER TABLE public.crm_mcp_server OWNER TO luohd;

--
-- TOC entry 8216 (class 0 OID 0)
-- Dependencies: 460
-- Name: TABLE crm_mcp_server; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_mcp_server IS '租户 MCP Server 配置';


--
-- TOC entry 8217 (class 0 OID 0)
-- Dependencies: 460
-- Name: COLUMN crm_mcp_server.auth_secret_enc; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_mcp_server.auth_secret_enc IS 'AES 加密密文，管理接口不返回明文';


--
-- TOC entry 8218 (class 0 OID 0)
-- Dependencies: 460
-- Name: COLUMN crm_mcp_server.allowed_tools; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_mcp_server.allowed_tools IS 'JSON 字符串数组，null 表示全部 tool';


--
-- TOC entry 459 (class 1259 OID 379490)
-- Name: crm_mcp_server_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_mcp_server_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_mcp_server_id_seq OWNER TO luohd;

--
-- TOC entry 8220 (class 0 OID 0)
-- Dependencies: 459
-- Name: crm_mcp_server_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_mcp_server_id_seq OWNED BY public.crm_mcp_server.id;


--
-- TOC entry 616 (class 1259 OID 413137)
-- Name: crm_messenger_config; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_messenger_config (
    id bigint NOT NULL,
    tenant_id character varying(64) NOT NULL,
    page_id character varying(64),
    page_name character varying(256),
    access_token_enc text,
    webhook_verify_token_enc text,
    app_secret_enc text,
    webhook_configured smallint DEFAULT 0 NOT NULL,
    last_error text,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


ALTER TABLE public.crm_messenger_config OWNER TO luohd;

--
-- TOC entry 8222 (class 0 OID 0)
-- Dependencies: 616
-- Name: TABLE crm_messenger_config; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_messenger_config IS '租户级 Facebook Messenger（Page）配置；一租户一行';


--
-- TOC entry 8223 (class 0 OID 0)
-- Dependencies: 616
-- Name: COLUMN crm_messenger_config.access_token_enc; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_messenger_config.access_token_enc IS 'AES-256-GCM 加密的 Page Access Token';


--
-- TOC entry 8224 (class 0 OID 0)
-- Dependencies: 616
-- Name: COLUMN crm_messenger_config.webhook_verify_token_enc; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_messenger_config.webhook_verify_token_enc IS 'AES-256-GCM 加密的 Webhook verify_token';


--
-- TOC entry 8225 (class 0 OID 0)
-- Dependencies: 616
-- Name: COLUMN crm_messenger_config.app_secret_enc; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_messenger_config.app_secret_enc IS 'AES-256-GCM 加密的 Meta App Secret';


--
-- TOC entry 8226 (class 0 OID 0)
-- Dependencies: 616
-- Name: COLUMN crm_messenger_config.webhook_configured; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_messenger_config.webhook_configured IS '0=未配置 1=已配置 Webhook';


--
-- TOC entry 8227 (class 0 OID 0)
-- Dependencies: 616
-- Name: COLUMN crm_messenger_config.last_error; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_messenger_config.last_error IS '最近一次 API 调用错误信息';


--
-- TOC entry 615 (class 1259 OID 413136)
-- Name: crm_messenger_config_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_messenger_config_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_messenger_config_id_seq OWNER TO luohd;

--
-- TOC entry 8229 (class 0 OID 0)
-- Dependencies: 615
-- Name: crm_messenger_config_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_messenger_config_id_seq OWNED BY public.crm_messenger_config.id;


--
-- TOC entry 620 (class 1259 OID 413165)
-- Name: crm_messenger_conversation; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_messenger_conversation (
    id bigint NOT NULL,
    tenant_id character varying(64) NOT NULL,
    psid character varying(64) NOT NULL,
    page_id character varying(64),
    profile_name character varying(256),
    contact_id character varying(64),
    lead_id character varying(64),
    last_message_at timestamp without time zone,
    last_message_preview character varying(256),
    last_customer_message_at timestamp without time zone,
    last_outbound_at timestamp without time zone,
    unread_count integer DEFAULT 0 NOT NULL,
    status character varying(16) DEFAULT 'active'::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT now() NOT NULL,
    update_time timestamp without time zone,
    user_id bigint,
    owner character varying(128),
    assigned_at timestamp without time zone,
    hosted_by_ai boolean
);


ALTER TABLE public.crm_messenger_conversation OWNER TO luohd;

--
-- TOC entry 8231 (class 0 OID 0)
-- Dependencies: 620
-- Name: TABLE crm_messenger_conversation; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_messenger_conversation IS 'Messenger 会话；一租户下一 psid 一行';


--
-- TOC entry 8232 (class 0 OID 0)
-- Dependencies: 620
-- Name: COLUMN crm_messenger_conversation.last_customer_message_at; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_messenger_conversation.last_customer_message_at IS '最近客户入站时间，用于 24h 窗';


--
-- TOC entry 8233 (class 0 OID 0)
-- Dependencies: 620
-- Name: COLUMN crm_messenger_conversation.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_messenger_conversation.status IS 'active | archived | blocked';


--
-- TOC entry 8234 (class 0 OID 0)
-- Dependencies: 620
-- Name: COLUMN crm_messenger_conversation.user_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_messenger_conversation.user_id IS '负责人系统用户 id；NULL = 公共池';


--
-- TOC entry 8235 (class 0 OID 0)
-- Dependencies: 620
-- Name: COLUMN crm_messenger_conversation.owner; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_messenger_conversation.owner IS '负责人展示名';


--
-- TOC entry 8236 (class 0 OID 0)
-- Dependencies: 620
-- Name: COLUMN crm_messenger_conversation.assigned_at; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_messenger_conversation.assigned_at IS '最近一次领取 / 指派时间';


--
-- TOC entry 8237 (class 0 OID 0)
-- Dependencies: 620
-- Name: COLUMN crm_messenger_conversation.hosted_by_ai; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_messenger_conversation.hosted_by_ai IS 'true=AI托管; false=人工接管; NULL=跟随渠道 hosted';


--
-- TOC entry 619 (class 1259 OID 413164)
-- Name: crm_messenger_conversation_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_messenger_conversation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_messenger_conversation_id_seq OWNER TO luohd;

--
-- TOC entry 8239 (class 0 OID 0)
-- Dependencies: 619
-- Name: crm_messenger_conversation_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_messenger_conversation_id_seq OWNED BY public.crm_messenger_conversation.id;


--
-- TOC entry 622 (class 1259 OID 413183)
-- Name: crm_messenger_message; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_messenger_message (
    id bigint NOT NULL,
    tenant_id character varying(64) NOT NULL,
    conversation_id bigint NOT NULL,
    psid character varying(64) NOT NULL,
    message_id character varying(128),
    message_type character varying(32),
    content text,
    direction smallint NOT NULL,
    send_time bigint,
    status character varying(32),
    media_id character varying(256),
    media_url character varying(1024),
    media_mime_type character varying(128),
    filename character varying(512),
    raw_json text,
    create_time timestamp without time zone DEFAULT now() NOT NULL,
    source character varying(32)
);


ALTER TABLE public.crm_messenger_message OWNER TO luohd;

--
-- TOC entry 8241 (class 0 OID 0)
-- Dependencies: 622
-- Name: TABLE crm_messenger_message; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_messenger_message IS 'Messenger 消息明细';


--
-- TOC entry 8242 (class 0 OID 0)
-- Dependencies: 622
-- Name: COLUMN crm_messenger_message.direction; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_messenger_message.direction IS '0=客户 1=Page 出站';


--
-- TOC entry 8243 (class 0 OID 0)
-- Dependencies: 622
-- Name: COLUMN crm_messenger_message.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_messenger_message.status IS 'sent | delivered | read | failed';


--
-- TOC entry 8244 (class 0 OID 0)
-- Dependencies: 622
-- Name: COLUMN crm_messenger_message.source; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_messenger_message.source IS 'outbound origin: null=human, ai_inbox_agent=Inbox AI';


--
-- TOC entry 621 (class 1259 OID 413182)
-- Name: crm_messenger_message_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_messenger_message_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_messenger_message_id_seq OWNER TO luohd;

--
-- TOC entry 8246 (class 0 OID 0)
-- Dependencies: 621
-- Name: crm_messenger_message_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_messenger_message_id_seq OWNED BY public.crm_messenger_message.id;


--
-- TOC entry 618 (class 1259 OID 413150)
-- Name: crm_messenger_webhook_event; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_messenger_webhook_event (
    id bigint NOT NULL,
    tenant_id character varying(64) NOT NULL,
    psid character varying(64),
    page_id character varying(64),
    event_type character varying(64) NOT NULL,
    message_id character varying(128),
    message_type character varying(32),
    content text,
    direction smallint,
    send_time bigint,
    raw_json text,
    crm_lead_id character varying(64),
    crm_contact_id character varying(64),
    status character varying(16) DEFAULT 'pending'::character varying NOT NULL,
    processing_error text,
    create_time timestamp without time zone DEFAULT now() NOT NULL,
    update_time timestamp without time zone
);


ALTER TABLE public.crm_messenger_webhook_event OWNER TO luohd;

--
-- TOC entry 8248 (class 0 OID 0)
-- Dependencies: 618
-- Name: TABLE crm_messenger_webhook_event; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_messenger_webhook_event IS 'Meta Messenger Webhook 事件日志';


--
-- TOC entry 8249 (class 0 OID 0)
-- Dependencies: 618
-- Name: COLUMN crm_messenger_webhook_event.event_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_messenger_webhook_event.event_type IS 'message | delivery | read | postback | other';


--
-- TOC entry 8250 (class 0 OID 0)
-- Dependencies: 618
-- Name: COLUMN crm_messenger_webhook_event.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_messenger_webhook_event.status IS 'pending | processed | failed | ignored';


--
-- TOC entry 617 (class 1259 OID 413149)
-- Name: crm_messenger_webhook_event_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_messenger_webhook_event_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_messenger_webhook_event_id_seq OWNER TO luohd;

--
-- TOC entry 8252 (class 0 OID 0)
-- Dependencies: 617
-- Name: crm_messenger_webhook_event_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_messenger_webhook_event_id_seq OWNED BY public.crm_messenger_webhook_event.id;


--
-- TOC entry 641 (class 1259 OID 413400)
-- Name: crm_mobile_push_device; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_mobile_push_device (
    id bigint NOT NULL,
    tenant_id character varying(64) NOT NULL,
    user_id bigint NOT NULL,
    platform character varying(16) NOT NULL,
    push_token character varying(512) NOT NULL,
    app_version character varying(64),
    create_time timestamp without time zone,
    update_time timestamp without time zone
);


ALTER TABLE public.crm_mobile_push_device OWNER TO luohd;

--
-- TOC entry 8254 (class 0 OID 0)
-- Dependencies: 641
-- Name: TABLE crm_mobile_push_device; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_mobile_push_device IS 'Inbox 移动推送设备 token（FCM/APNs via FCM）';


--
-- TOC entry 8255 (class 0 OID 0)
-- Dependencies: 641
-- Name: COLUMN crm_mobile_push_device.platform; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_mobile_push_device.platform IS 'ios | android';


--
-- TOC entry 8256 (class 0 OID 0)
-- Dependencies: 641
-- Name: COLUMN crm_mobile_push_device.push_token; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_mobile_push_device.push_token IS 'FCM 设备 token';


--
-- TOC entry 640 (class 1259 OID 413399)
-- Name: crm_mobile_push_device_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_mobile_push_device_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_mobile_push_device_id_seq OWNER TO luohd;

--
-- TOC entry 8258 (class 0 OID 0)
-- Dependencies: 640
-- Name: crm_mobile_push_device_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_mobile_push_device_id_seq OWNED BY public.crm_mobile_push_device.id;


--
-- TOC entry 245 (class 1259 OID 360610)
-- Name: crm_oauth_connections; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_oauth_connections OWNER TO "_tenant";

--
-- TOC entry 246 (class 1259 OID 360622)
-- Name: crm_onepage; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_onepage OWNER TO "_tenant";

--
-- TOC entry 247 (class 1259 OID 360629)
-- Name: crm_operator; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_operator OWNER TO "_tenant";

--
-- TOC entry 248 (class 1259 OID 360638)
-- Name: crm_opty; Type: TABLE; Schema: public; Owner: _tenant
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
    amount numeric(18,2) DEFAULT 0.00,
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


ALTER TABLE public.crm_opty OWNER TO "_tenant";

--
-- TOC entry 8263 (class 0 OID 0)
-- Dependencies: 248
-- Name: COLUMN crm_opty.treatment_item_code; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_opty.treatment_item_code IS '单治疗项目编码';


--
-- TOC entry 8264 (class 0 OID 0)
-- Dependencies: 248
-- Name: COLUMN crm_opty.case_group_id; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_opty.case_group_id IS '病例分组 ID';


--
-- TOC entry 8265 (class 0 OID 0)
-- Dependencies: 248
-- Name: COLUMN crm_opty.closed_won_basis; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_opty.closed_won_basis IS '赢单依据（字典 crm_closed_won_basis）';


--
-- TOC entry 8266 (class 0 OID 0)
-- Dependencies: 248
-- Name: COLUMN crm_opty.down_payment_amount; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_opty.down_payment_amount IS '已收首付金额';


--
-- TOC entry 8267 (class 0 OID 0)
-- Dependencies: 248
-- Name: COLUMN crm_opty.agreement_signed_at; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_opty.agreement_signed_at IS '治疗协议签署时间';


--
-- TOC entry 249 (class 1259 OID 360653)
-- Name: crm_optyacceptance; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_optyacceptance OWNER TO "_tenant";

--
-- TOC entry 250 (class 1259 OID 360662)
-- Name: crm_optyrelation; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_optyrelation OWNER TO "_tenant";

--
-- TOC entry 251 (class 1259 OID 360672)
-- Name: crm_optytask; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_optytask OWNER TO "_tenant";

--
-- TOC entry 252 (class 1259 OID 360681)
-- Name: crm_org; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_org OWNER TO "_tenant";

--
-- TOC entry 638 (class 1259 OID 413375)
-- Name: crm_outbound_webhook; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_outbound_webhook (
    webhook_id character varying(36) NOT NULL,
    tenant_id character varying(64) NOT NULL,
    name character varying(128) NOT NULL,
    url character varying(2048) NOT NULL,
    secret_enc character varying(512),
    event_type character varying(64) NOT NULL,
    payload_template text,
    retry_count integer DEFAULT 3 NOT NULL,
    timeout_ms integer DEFAULT 5000 NOT NULL,
    enabled character(1) DEFAULT '1'::bpchar NOT NULL,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    del_flag character(1) DEFAULT '0'::bpchar NOT NULL
);


ALTER TABLE public.crm_outbound_webhook OWNER TO luohd;

--
-- TOC entry 8273 (class 0 OID 0)
-- Dependencies: 638
-- Name: TABLE crm_outbound_webhook; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_outbound_webhook IS 'Tenant outbound webhook: domain event -> HTTPS POST';


--
-- TOC entry 8274 (class 0 OID 0)
-- Dependencies: 638
-- Name: COLUMN crm_outbound_webhook.secret_enc; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_outbound_webhook.secret_enc IS 'AES-GCM encrypted HMAC secret; never returned in API';


--
-- TOC entry 8275 (class 0 OID 0)
-- Dependencies: 638
-- Name: COLUMN crm_outbound_webhook.event_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_outbound_webhook.event_type IS 'opportunity.won | lead.assigned | lead.created | contact.created | patient.recall_due | appointment.noshow';


--
-- TOC entry 8276 (class 0 OID 0)
-- Dependencies: 638
-- Name: COLUMN crm_outbound_webhook.retry_count; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_outbound_webhook.retry_count IS 'Retries after first attempt (0-8)';


--
-- TOC entry 639 (class 1259 OID 413387)
-- Name: crm_outbound_webhook_delivery; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_outbound_webhook_delivery (
    delivery_id character varying(36) NOT NULL,
    tenant_id character varying(64) NOT NULL,
    webhook_id character varying(36) NOT NULL,
    event_id character varying(64) NOT NULL,
    event_type character varying(64) NOT NULL,
    source character varying(16) NOT NULL,
    status character varying(16) NOT NULL,
    attempt integer DEFAULT 0 NOT NULL,
    http_status integer,
    error_message character varying(500),
    request_id character varying(64),
    next_retry_at timestamp with time zone,
    payload_json text,
    create_time timestamp without time zone,
    update_time timestamp without time zone
);


ALTER TABLE public.crm_outbound_webhook_delivery OWNER TO luohd;

--
-- TOC entry 8278 (class 0 OID 0)
-- Dependencies: 639
-- Name: TABLE crm_outbound_webhook_delivery; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_outbound_webhook_delivery IS 'Outbound webhook delivery queue and log';


--
-- TOC entry 8279 (class 0 OID 0)
-- Dependencies: 639
-- Name: COLUMN crm_outbound_webhook_delivery.source; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_outbound_webhook_delivery.source IS 'event | test';


--
-- TOC entry 8280 (class 0 OID 0)
-- Dependencies: 639
-- Name: COLUMN crm_outbound_webhook_delivery.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_outbound_webhook_delivery.status IS 'pending | retrying | success | failed';


--
-- TOC entry 253 (class 1259 OID 360705)
-- Name: crm_ownerhistory; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_ownerhistory OWNER TO "_tenant";

--
-- TOC entry 254 (class 1259 OID 360712)
-- Name: crm_partner; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_partner OWNER TO "_tenant";

--
-- TOC entry 434 (class 1259 OID 371280)
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
    del_flag smallint DEFAULT 0,
    by_treatment jsonb DEFAULT '{}'::jsonb NOT NULL,
    original_source character varying(255)
);


ALTER TABLE public.crm_patient_value OWNER TO luohd;

--
-- TOC entry 255 (class 1259 OID 360726)
-- Name: crm_paymentterm; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_paymentterm OWNER TO "_tenant";

--
-- TOC entry 8285 (class 0 OID 0)
-- Dependencies: 255
-- Name: COLUMN crm_paymentterm.refund_amount; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_paymentterm.refund_amount IS 'Refund amount; must be <= paid_amount';


--
-- TOC entry 256 (class 1259 OID 360737)
-- Name: crm_privacy; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_privacy OWNER TO "_tenant";

--
-- TOC entry 257 (class 1259 OID 360745)
-- Name: crm_product; Type: TABLE; Schema: public; Owner: _tenant
--

CREATE TABLE public.crm_product (
    product_id character varying(100) NOT NULL,
    product_name character varying(100),
    product_pinyin character varying(100),
    product_label character varying(100),
    product_code character varying(100),
    product_local_code character varying(100),
    avatar character varying(512),
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
    description text,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    inv_id character varying(100),
    inv_name character varying(100),
    wecom_product_id character varying(64) DEFAULT NULL::character varying,
    del_flag character(1) DEFAULT '0'::bpchar NOT NULL,
    version integer DEFAULT 1 NOT NULL,
    category_id character varying(36),
    search_keywords character varying(512),
    short_description text,
    selling_points text,
    detail_md text,
    cover_image character varying(512),
    published_time timestamp without time zone,
    disabled_time timestamp without time zone,
    industry_attributes jsonb DEFAULT '{}'::jsonb NOT NULL
);


ALTER TABLE public.crm_product OWNER TO "_tenant";

--
-- TOC entry 8288 (class 0 OID 0)
-- Dependencies: 257
-- Name: COLUMN crm_product.status; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_product.status IS '生命周期 draft/active/inactive/archived（迁移期兼容旧 1/0/已启用/停用）';


--
-- TOC entry 8289 (class 0 OID 0)
-- Dependencies: 257
-- Name: COLUMN crm_product.del_flag; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_product.del_flag IS '逻辑删除 0正常 1已删';


--
-- TOC entry 8290 (class 0 OID 0)
-- Dependencies: 257
-- Name: COLUMN crm_product.version; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_product.version IS '乐观锁版本';


--
-- TOC entry 8291 (class 0 OID 0)
-- Dependencies: 257
-- Name: COLUMN crm_product.category_id; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_product.category_id IS '产品分类ID（crm_product_category）';


--
-- TOC entry 8292 (class 0 OID 0)
-- Dependencies: 257
-- Name: COLUMN crm_product.search_keywords; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_product.search_keywords IS '搜索关键词';


--
-- TOC entry 8293 (class 0 OID 0)
-- Dependencies: 257
-- Name: COLUMN crm_product.short_description; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_product.short_description IS '短描述（目录/企微摘要）';


--
-- TOC entry 8294 (class 0 OID 0)
-- Dependencies: 257
-- Name: COLUMN crm_product.selling_points; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_product.selling_points IS '销售卖点';


--
-- TOC entry 8295 (class 0 OID 0)
-- Dependencies: 257
-- Name: COLUMN crm_product.detail_md; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_product.detail_md IS '详细介绍 Markdown';


--
-- TOC entry 8296 (class 0 OID 0)
-- Dependencies: 257
-- Name: COLUMN crm_product.cover_image; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_product.cover_image IS '封面图缓存（与 avatar 兼容）';


--
-- TOC entry 8297 (class 0 OID 0)
-- Dependencies: 257
-- Name: COLUMN crm_product.published_time; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_product.published_time IS '首次/最近发布时间';


--
-- TOC entry 8298 (class 0 OID 0)
-- Dependencies: 257
-- Name: COLUMN crm_product.disabled_time; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_product.disabled_time IS '停售时间';


--
-- TOC entry 8299 (class 0 OID 0)
-- Dependencies: 257
-- Name: COLUMN crm_product.industry_attributes; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_product.industry_attributes IS '行业扩展属性 jsonb';


--
-- TOC entry 587 (class 1259 OID 411250)
-- Name: crm_product_category; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_product_category (
    category_id character varying(36) NOT NULL,
    parent_id character varying(36),
    category_code character varying(64) NOT NULL,
    category_name character varying(128) NOT NULL,
    ancestors character varying(1024) DEFAULT ''::character varying NOT NULL,
    sort_order integer DEFAULT 0 NOT NULL,
    status character(1) DEFAULT '1'::bpchar NOT NULL,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    del_flag character(1) DEFAULT '0'::bpchar NOT NULL
);


ALTER TABLE public.crm_product_category OWNER TO luohd;

--
-- TOC entry 8301 (class 0 OID 0)
-- Dependencies: 587
-- Name: TABLE crm_product_category; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_product_category IS '产品分类树';


--
-- TOC entry 8302 (class 0 OID 0)
-- Dependencies: 587
-- Name: COLUMN crm_product_category.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_product_category.status IS '1启用 0停用';


--
-- TOC entry 589 (class 1259 OID 411275)
-- Name: crm_product_channel_binding; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_product_channel_binding (
    binding_id character varying(36) NOT NULL,
    product_id character varying(100) NOT NULL,
    channel_type character varying(32) NOT NULL,
    external_id character varying(64),
    sync_status character varying(16) DEFAULT 'synced'::character varying NOT NULL,
    last_sync_hash character varying(64),
    last_sync_time timestamp without time zone,
    last_sync_by character varying(64),
    last_error character varying(1024),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    del_flag character(1) DEFAULT '0'::bpchar NOT NULL
);


ALTER TABLE public.crm_product_channel_binding OWNER TO luohd;

--
-- TOC entry 8304 (class 0 OID 0)
-- Dependencies: 589
-- Name: TABLE crm_product_channel_binding; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_product_channel_binding IS '产品渠道映射（一期 wecom_album）';


--
-- TOC entry 8305 (class 0 OID 0)
-- Dependencies: 589
-- Name: COLUMN crm_product_channel_binding.channel_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_product_channel_binding.channel_type IS 'wecom_album';


--
-- TOC entry 8306 (class 0 OID 0)
-- Dependencies: 589
-- Name: COLUMN crm_product_channel_binding.sync_status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_product_channel_binding.sync_status IS 'synced|dirty|failed';


--
-- TOC entry 588 (class 1259 OID 411263)
-- Name: crm_product_media; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_product_media (
    media_id character varying(36) NOT NULL,
    product_id character varying(100) NOT NULL,
    media_type character varying(16) DEFAULT 'image'::character varying NOT NULL,
    url character varying(512) NOT NULL,
    file_name character varying(255),
    mime_type character varying(128),
    is_cover character(1) DEFAULT '0'::bpchar NOT NULL,
    sort_order integer DEFAULT 0 NOT NULL,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    del_flag character(1) DEFAULT '0'::bpchar NOT NULL
);


ALTER TABLE public.crm_product_media OWNER TO luohd;

--
-- TOC entry 8308 (class 0 OID 0)
-- Dependencies: 588
-- Name: TABLE crm_product_media; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_product_media IS '产品媒体（主图/多图）';


--
-- TOC entry 8309 (class 0 OID 0)
-- Dependencies: 588
-- Name: COLUMN crm_product_media.media_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_product_media.media_type IS 'image|file';


--
-- TOC entry 8310 (class 0 OID 0)
-- Dependencies: 588
-- Name: COLUMN crm_product_media.is_cover; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_product_media.is_cover IS '1封面 0否';


--
-- TOC entry 599 (class 1259 OID 411741)
-- Name: crm_professional_service; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_professional_service (
    id character varying(64) NOT NULL,
    name_cn character varying(512) NOT NULL,
    name_vn character varying(512) NOT NULL,
    name_en character varying(512),
    category character varying(128) NOT NULL,
    background character varying(128) NOT NULL,
    province character varying(256),
    address text,
    core_services jsonb DEFAULT '[]'::jsonb NOT NULL,
    target_clients text,
    key_capabilities text DEFAULT ''::text NOT NULL,
    contact_phone character varying(128),
    email character varying(256),
    website character varying(1024),
    notes text,
    created_at character varying(32),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    del_flag character(1) DEFAULT '0'::bpchar NOT NULL
);


ALTER TABLE public.crm_professional_service OWNER TO luohd;

--
-- TOC entry 8312 (class 0 OID 0)
-- Dependencies: 599
-- Name: TABLE crm_professional_service; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_professional_service IS '越南出海专业服务机构（法律/审计/EPC/物流/金融/商会）';


--
-- TOC entry 8313 (class 0 OID 0)
-- Dependencies: 599
-- Name: COLUMN crm_professional_service.category; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_professional_service.category IS '法律服务/会计审计/EPC/物流报关/金融融资/民间商会';


--
-- TOC entry 8314 (class 0 OID 0)
-- Dependencies: 599
-- Name: COLUMN crm_professional_service.background; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_professional_service.background IS '国际Big4/第二梯队/本土领军/中资华人/台资/跨国联合等';


--
-- TOC entry 8315 (class 0 OID 0)
-- Dependencies: 599
-- Name: COLUMN crm_professional_service.core_services; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_professional_service.core_services IS 'JSON string array of core service modules';


--
-- TOC entry 8316 (class 0 OID 0)
-- Dependencies: 599
-- Name: COLUMN crm_professional_service.del_flag; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_professional_service.del_flag IS '0 active, 1 soft-deleted';


--
-- TOC entry 258 (class 1259 OID 360753)
-- Name: crm_pulse; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_pulse OWNER TO "_tenant";

--
-- TOC entry 259 (class 1259 OID 360763)
-- Name: crm_quote; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_quote OWNER TO "_tenant";

--
-- TOC entry 8319 (class 0 OID 0)
-- Dependencies: 259
-- Name: COLUMN crm_quote.remark; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_quote.remark IS '备注';


--
-- TOC entry 260 (class 1259 OID 360777)
-- Name: crm_quoteoption; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_quoteoption OWNER TO "_tenant";

--
-- TOC entry 635 (class 1259 OID 413337)
-- Name: crm_recall_policy; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_recall_policy (
    policy_id character varying(36) NOT NULL,
    tenant_id character varying(64) NOT NULL,
    name character varying(128) NOT NULL,
    description character varying(500),
    enabled character(1) DEFAULT '1'::bpchar NOT NULL,
    is_system character(1) DEFAULT '0'::bpchar NOT NULL,
    priority integer DEFAULT 100 NOT NULL,
    match_json text,
    interval_value integer NOT NULL,
    interval_unit character varying(8) DEFAULT 'days'::character varying NOT NULL,
    del_flag character(1) DEFAULT '0'::bpchar NOT NULL,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


ALTER TABLE public.crm_recall_policy OWNER TO luohd;

--
-- TOC entry 8322 (class 0 OID 0)
-- Dependencies: 635
-- Name: TABLE crm_recall_policy; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_recall_policy IS 'Tenant recall interval policies matched by treatment_item_code';


--
-- TOC entry 8323 (class 0 OID 0)
-- Dependencies: 635
-- Name: COLUMN crm_recall_policy.is_system; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_recall_policy.is_system IS '1 = seeded system policy, cannot delete';


--
-- TOC entry 8324 (class 0 OID 0)
-- Dependencies: 635
-- Name: COLUMN crm_recall_policy.match_json; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_recall_policy.match_json IS '{"treatmentItemCodes":["种植牙"]} empty = catch-all';


--
-- TOC entry 8325 (class 0 OID 0)
-- Dependencies: 635
-- Name: COLUMN crm_recall_policy.interval_unit; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_recall_policy.interval_unit IS 'days | months';


--
-- TOC entry 636 (class 1259 OID 413350)
-- Name: crm_recall_schedule; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_recall_schedule (
    schedule_id character varying(36) NOT NULL,
    tenant_id character varying(64) NOT NULL,
    contact_id character varying(100) NOT NULL,
    policy_id character varying(36) NOT NULL,
    anchor_type character varying(32) NOT NULL,
    anchor_id character varying(100),
    treatment_item_code character varying(64),
    next_due_at timestamp with time zone NOT NULL,
    status character varying(16) DEFAULT 'scheduled'::character varying NOT NULL,
    last_fired_at timestamp without time zone,
    create_time timestamp without time zone,
    update_time timestamp without time zone
);


ALTER TABLE public.crm_recall_schedule OWNER TO luohd;

--
-- TOC entry 8327 (class 0 OID 0)
-- Dependencies: 636
-- Name: TABLE crm_recall_schedule; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_recall_schedule IS 'Materialized next recall due date per contact+policy';


--
-- TOC entry 8328 (class 0 OID 0)
-- Dependencies: 636
-- Name: COLUMN crm_recall_schedule.anchor_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_recall_schedule.anchor_type IS 'MVP: opportunity.won';


--
-- TOC entry 8329 (class 0 OID 0)
-- Dependencies: 636
-- Name: COLUMN crm_recall_schedule.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_recall_schedule.status IS 'scheduled | fired | cancelled';


--
-- TOC entry 610 (class 1259 OID 413020)
-- Name: crm_reminder_link; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_reminder_link (
    id bigint NOT NULL,
    tenant_id character varying(64) NOT NULL,
    token character varying(32) NOT NULL,
    type character varying(64) NOT NULL,
    contact_id character varying(64),
    wa_id character varying(32),
    ref_type character varying(64),
    ref_id character varying(64),
    payload jsonb DEFAULT '{}'::jsonb NOT NULL,
    actions jsonb,
    status character varying(16) DEFAULT 'active'::character varying NOT NULL,
    expires_at timestamp with time zone NOT NULL,
    opened_at timestamp with time zone,
    open_count integer DEFAULT 0 NOT NULL,
    created_by character varying(64),
    create_time timestamp with time zone DEFAULT now() NOT NULL,
    update_time timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.crm_reminder_link OWNER TO luohd;

--
-- TOC entry 8331 (class 0 OID 0)
-- Dependencies: 610
-- Name: TABLE crm_reminder_link; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_reminder_link IS 'WhatsApp Reminder 短链；落地页 /reminder/{token}';


--
-- TOC entry 8332 (class 0 OID 0)
-- Dependencies: 610
-- Name: COLUMN crm_reminder_link.token; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_reminder_link.token IS '短 token（10-12），禁止 UUID；租户内唯一';


--
-- TOC entry 8333 (class 0 OID 0)
-- Dependencies: 610
-- Name: COLUMN crm_reminder_link.type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_reminder_link.type IS 'appointment_cancel | appointment_reminder | revisit_reminder | post_treatment | lead_followup | ...';


--
-- TOC entry 8334 (class 0 OID 0)
-- Dependencies: 610
-- Name: COLUMN crm_reminder_link.contact_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_reminder_link.contact_id IS 'CRM 联系人 ID（VARCHAR，与 WhatsApp 会话一致）';


--
-- TOC entry 8335 (class 0 OID 0)
-- Dependencies: 610
-- Name: COLUMN crm_reminder_link.payload; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_reminder_link.payload IS '发送时快照 JSON，落地页优先展示';


--
-- TOC entry 8336 (class 0 OID 0)
-- Dependencies: 610
-- Name: COLUMN crm_reminder_link.actions; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_reminder_link.actions IS '前端可渲染动作白名单 JSON 数组';


--
-- TOC entry 8337 (class 0 OID 0)
-- Dependencies: 610
-- Name: COLUMN crm_reminder_link.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_reminder_link.status IS 'active | used | expired | revoked';


--
-- TOC entry 609 (class 1259 OID 413019)
-- Name: crm_reminder_link_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_reminder_link_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_reminder_link_id_seq OWNER TO luohd;

--
-- TOC entry 8339 (class 0 OID 0)
-- Dependencies: 609
-- Name: crm_reminder_link_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_reminder_link_id_seq OWNED BY public.crm_reminder_link.id;


--
-- TOC entry 261 (class 1259 OID 360788)
-- Name: crm_reportdetail; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_reportdetail OWNER TO "_tenant";

--
-- TOC entry 262 (class 1259 OID 360806)
-- Name: crm_research; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_research OWNER TO "_tenant";

--
-- TOC entry 263 (class 1259 OID 360814)
-- Name: crm_respondents; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_respondents OWNER TO "_tenant";

--
-- TOC entry 264 (class 1259 OID 360821)
-- Name: crm_responses; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_responses OWNER TO "_tenant";

--
-- TOC entry 544 (class 1259 OID 408596)
-- Name: crm_sales_script; Type: TABLE; Schema: public; Owner: _tenant
--

CREATE TABLE public.crm_sales_script (
    script_id character varying(64) NOT NULL,
    script_type character varying(20) NOT NULL,
    category character varying(100),
    title character varying(200) NOT NULL,
    content text NOT NULL,
    material_id bigint,
    sort_order integer DEFAULT 0,
    status character(1) DEFAULT '0'::bpchar NOT NULL,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    del_flag character(1) DEFAULT '0'::bpchar NOT NULL
);


ALTER TABLE public.crm_sales_script OWNER TO "_tenant";

--
-- TOC entry 8345 (class 0 OID 0)
-- Dependencies: 544
-- Name: TABLE crm_sales_script; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON TABLE public.crm_sales_script IS '侧边栏话术库：营销话术 / Q&A / SOP';


--
-- TOC entry 8346 (class 0 OID 0)
-- Dependencies: 544
-- Name: COLUMN crm_sales_script.script_id; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_sales_script.script_id IS 'UUID 主键';


--
-- TOC entry 8347 (class 0 OID 0)
-- Dependencies: 544
-- Name: COLUMN crm_sales_script.script_type; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_sales_script.script_type IS 'marketing | qa | sop';


--
-- TOC entry 8348 (class 0 OID 0)
-- Dependencies: 544
-- Name: COLUMN crm_sales_script.category; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_sales_script.category IS '自由分类名';


--
-- TOC entry 8349 (class 0 OID 0)
-- Dependencies: 544
-- Name: COLUMN crm_sales_script.title; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_sales_script.title IS '标题；Q&A 即问题';


--
-- TOC entry 8350 (class 0 OID 0)
-- Dependencies: 544
-- Name: COLUMN crm_sales_script.content; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_sales_script.content IS '正文；Q&A 即答案；SOP 多步骤文本';


--
-- TOC entry 8351 (class 0 OID 0)
-- Dependencies: 544
-- Name: COLUMN crm_sales_script.material_id; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_sales_script.material_id IS '可选关联 wecom_material.id';


--
-- TOC entry 8352 (class 0 OID 0)
-- Dependencies: 544
-- Name: COLUMN crm_sales_script.sort_order; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_sales_script.sort_order IS '排序权重，越大越靠前';


--
-- TOC entry 8353 (class 0 OID 0)
-- Dependencies: 544
-- Name: COLUMN crm_sales_script.status; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_sales_script.status IS '0 启用 / 1 停用';


--
-- TOC entry 8354 (class 0 OID 0)
-- Dependencies: 544
-- Name: COLUMN crm_sales_script.del_flag; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_sales_script.del_flag IS '逻辑删除 0 正常 / 1 已删';


--
-- TOC entry 265 (class 1259 OID 360828)
-- Name: crm_salesgoal; Type: TABLE; Schema: public; Owner: _tenant
--

CREATE TABLE public.crm_salesgoal (
    goal_id character varying(100) NOT NULL,
    goal_name character varying(100) DEFAULT '用户任务'::character varying,
    goal_type character varying(100),
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
    dept_id bigint,
    kpi_code character varying(64)
);


ALTER TABLE public.crm_salesgoal OWNER TO "_tenant";

--
-- TOC entry 8355 (class 0 OID 0)
-- Dependencies: 265
-- Name: COLUMN crm_salesgoal.kpi_code; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_salesgoal.kpi_code IS 'KPI 编码（9 固定枚举）；查询以本字段为准，旧季度行可为空';


--
-- TOC entry 601 (class 1259 OID 411767)
-- Name: crm_sanction_source_tag; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_sanction_source_tag (
    id character varying(64) NOT NULL,
    tag_code character varying(64) NOT NULL,
    tag_name character varying(256) NOT NULL,
    authority character varying(256) NOT NULL,
    description text NOT NULL,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    del_flag character(1) DEFAULT '0'::bpchar NOT NULL
);


ALTER TABLE public.crm_sanction_source_tag OWNER TO luohd;

--
-- TOC entry 8357 (class 0 OID 0)
-- Dependencies: 601
-- Name: TABLE crm_sanction_source_tag; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_sanction_source_tag IS '制裁源标签说明字典';


--
-- TOC entry 8358 (class 0 OID 0)
-- Dependencies: 601
-- Name: COLUMN crm_sanction_source_tag.tag_code; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_sanction_source_tag.tag_code IS '如 OFAC-SDN；活跃行唯一';


--
-- TOC entry 8359 (class 0 OID 0)
-- Dependencies: 601
-- Name: COLUMN crm_sanction_source_tag.del_flag; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_sanction_source_tag.del_flag IS '0 active, 1 soft-deleted';


--
-- TOC entry 600 (class 1259 OID 411754)
-- Name: crm_sanctioned_entity; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_sanctioned_entity (
    id character varying(64) NOT NULL,
    name_cn character varying(512) NOT NULL,
    name_vn character varying(512) NOT NULL,
    name_en character varying(512),
    aka jsonb DEFAULT '[]'::jsonb NOT NULL,
    entity_type character varying(64) NOT NULL,
    tax_code character varying(64),
    id_card_or_passport character varying(128),
    parent_company_or_ubo text,
    risk_category character varying(128) NOT NULL,
    risk_reason text NOT NULL,
    issuing_agency character varying(512) NOT NULL,
    risk_level character varying(64) NOT NULL,
    effective_date character varying(32) NOT NULL,
    expiry_date character varying(32),
    official_notice_url character varying(2048),
    matched_enterprise_code character varying(64),
    notes text,
    created_at character varying(32),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    del_flag character(1) DEFAULT '0'::bpchar NOT NULL
);


ALTER TABLE public.crm_sanctioned_entity OWNER TO luohd;

--
-- TOC entry 8361 (class 0 OID 0)
-- Dependencies: 600
-- Name: TABLE crm_sanctioned_entity; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_sanctioned_entity IS '合规风险/制裁主体主档';


--
-- TOC entry 8362 (class 0 OID 0)
-- Dependencies: 600
-- Name: COLUMN crm_sanctioned_entity.aka; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_sanctioned_entity.aka IS 'JSON string array of aliases / former names';


--
-- TOC entry 8363 (class 0 OID 0)
-- Dependencies: 600
-- Name: COLUMN crm_sanctioned_entity.entity_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_sanctioned_entity.entity_type IS '企业/个人/政府官员(PEP)/特定组织';


--
-- TOC entry 8364 (class 0 OID 0)
-- Dependencies: 600
-- Name: COLUMN crm_sanctioned_entity.risk_level; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_sanctioned_entity.risk_level IS '红灯(高危)/黄灯(中危)/绿灯(已解除)';


--
-- TOC entry 8365 (class 0 OID 0)
-- Dependencies: 600
-- Name: COLUMN crm_sanctioned_entity.matched_enterprise_code; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_sanctioned_entity.matched_enterprise_code IS '逻辑关联网点码或税号，无强制外键';


--
-- TOC entry 8366 (class 0 OID 0)
-- Dependencies: 600
-- Name: COLUMN crm_sanctioned_entity.del_flag; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_sanctioned_entity.del_flag IS '0 active, 1 soft-deleted';


--
-- TOC entry 495 (class 1259 OID 404322)
-- Name: crm_segment; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_segment (
    id bigint NOT NULL,
    tenant_id character varying(64),
    name character varying(128) NOT NULL,
    description text,
    rule_json jsonb NOT NULL,
    type character varying(16) DEFAULT 'dynamic'::character varying NOT NULL,
    est_size integer,
    computed_at timestamp without time zone,
    status character varying(16) DEFAULT 'active'::character varying NOT NULL,
    create_by character varying(64),
    create_time timestamp without time zone DEFAULT now(),
    update_by character varying(64),
    update_time timestamp without time zone DEFAULT now()
);


ALTER TABLE public.crm_segment OWNER TO luohd;

--
-- TOC entry 8368 (class 0 OID 0)
-- Dependencies: 495
-- Name: TABLE crm_segment; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_segment IS '客户分群定义（动态分群，仅存规则）';


--
-- TOC entry 8369 (class 0 OID 0)
-- Dependencies: 495
-- Name: COLUMN crm_segment.rule_json; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_segment.rule_json IS '分群规则 AST（AND/OR/NOT 条件树）';


--
-- TOC entry 8370 (class 0 OID 0)
-- Dependencies: 495
-- Name: COLUMN crm_segment.est_size; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_segment.est_size IS '最近一次估算命中人数';


--
-- TOC entry 8371 (class 0 OID 0)
-- Dependencies: 495
-- Name: COLUMN crm_segment.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_segment.status IS 'active | archived';


--
-- TOC entry 494 (class 1259 OID 404321)
-- Name: crm_segment_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_segment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_segment_id_seq OWNER TO luohd;

--
-- TOC entry 8373 (class 0 OID 0)
-- Dependencies: 494
-- Name: crm_segment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_segment_id_seq OWNED BY public.crm_segment.id;


--
-- TOC entry 450 (class 1259 OID 371695)
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


ALTER TABLE public.crm_semantic_profile OWNER TO luohd;

--
-- TOC entry 8375 (class 0 OID 0)
-- Dependencies: 450
-- Name: TABLE crm_semantic_profile; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_semantic_profile IS '租户语义层配置';


--
-- TOC entry 8376 (class 0 OID 0)
-- Dependencies: 450
-- Name: COLUMN crm_semantic_profile.profile_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_semantic_profile.profile_id IS 'UUID 主键';


--
-- TOC entry 8377 (class 0 OID 0)
-- Dependencies: 450
-- Name: COLUMN crm_semantic_profile.tenant_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_semantic_profile.tenant_id IS '租户标识（tenant 请求头）';


--
-- TOC entry 8378 (class 0 OID 0)
-- Dependencies: 450
-- Name: COLUMN crm_semantic_profile.industry_key; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_semantic_profile.industry_key IS 'dental / generic / custom';


--
-- TOC entry 8379 (class 0 OID 0)
-- Dependencies: 450
-- Name: COLUMN crm_semantic_profile.enabled; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_semantic_profile.enabled IS '0 停用 / 1 启用（停用时 AI 不注入语义块）';


--
-- TOC entry 8380 (class 0 OID 0)
-- Dependencies: 450
-- Name: COLUMN crm_semantic_profile.profile_version; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_semantic_profile.profile_version IS 'profile_json schema 版本';


--
-- TOC entry 8381 (class 0 OID 0)
-- Dependencies: 450
-- Name: COLUMN crm_semantic_profile.profile_json; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_semantic_profile.profile_json IS '租户覆盖 JSON 字符串';


--
-- TOC entry 8382 (class 0 OID 0)
-- Dependencies: 450
-- Name: COLUMN crm_semantic_profile.del_flag; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_semantic_profile.del_flag IS '逻辑删除 0 正常';


--
-- TOC entry 266 (class 1259 OID 360838)
-- Name: crm_sentiment; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_sentiment OWNER TO "_tenant";

--
-- TOC entry 443 (class 1259 OID 371448)
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


ALTER TABLE public.crm_sf_express_config OWNER TO luohd;

--
-- TOC entry 8385 (class 0 OID 0)
-- Dependencies: 443
-- Name: TABLE crm_sf_express_config; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_sf_express_config IS '顺丰租户丰桥凭证配置';


--
-- TOC entry 8386 (class 0 OID 0)
-- Dependencies: 443
-- Name: COLUMN crm_sf_express_config.customer_code; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_sf_express_config.customer_code IS '顾客编码 partnerID';


--
-- TOC entry 8387 (class 0 OID 0)
-- Dependencies: 443
-- Name: COLUMN crm_sf_express_config.checkword_enc; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_sf_express_config.checkword_enc IS '校验码 AES 密文';


--
-- TOC entry 8388 (class 0 OID 0)
-- Dependencies: 443
-- Name: COLUMN crm_sf_express_config.monthly_card; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_sf_express_config.monthly_card IS '月结卡号';


--
-- TOC entry 8389 (class 0 OID 0)
-- Dependencies: 443
-- Name: COLUMN crm_sf_express_config.sandbox; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_sf_express_config.sandbox IS '是否沙箱环境';


--
-- TOC entry 8390 (class 0 OID 0)
-- Dependencies: 443
-- Name: COLUMN crm_sf_express_config.tenant_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_sf_express_config.tenant_id IS '租户 ID';


--
-- TOC entry 442 (class 1259 OID 371447)
-- Name: crm_sf_express_config_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_sf_express_config_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_sf_express_config_id_seq OWNER TO luohd;

--
-- TOC entry 8392 (class 0 OID 0)
-- Dependencies: 442
-- Name: crm_sf_express_config_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_sf_express_config_id_seq OWNED BY public.crm_sf_express_config.id;


--
-- TOC entry 445 (class 1259 OID 371466)
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


ALTER TABLE public.crm_sf_express_token OWNER TO luohd;

--
-- TOC entry 8394 (class 0 OID 0)
-- Dependencies: 445
-- Name: TABLE crm_sf_express_token; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_sf_express_token IS '顺丰 OAuth2 accessToken';


--
-- TOC entry 8395 (class 0 OID 0)
-- Dependencies: 445
-- Name: COLUMN crm_sf_express_token.access_token_enc; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_sf_express_token.access_token_enc IS 'accessToken AES 密文';


--
-- TOC entry 8396 (class 0 OID 0)
-- Dependencies: 445
-- Name: COLUMN crm_sf_express_token.expires_at; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_sf_express_token.expires_at IS '过期时间';


--
-- TOC entry 8397 (class 0 OID 0)
-- Dependencies: 445
-- Name: COLUMN crm_sf_express_token.api_response_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_sf_express_token.api_response_id IS '顺丰 apiResponseID';


--
-- TOC entry 8398 (class 0 OID 0)
-- Dependencies: 445
-- Name: COLUMN crm_sf_express_token.last_refresh_at; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_sf_express_token.last_refresh_at IS '最近换票时间';


--
-- TOC entry 8399 (class 0 OID 0)
-- Dependencies: 445
-- Name: COLUMN crm_sf_express_token.last_error; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_sf_express_token.last_error IS '最近一次 OAuth 失败摘要';


--
-- TOC entry 444 (class 1259 OID 371465)
-- Name: crm_sf_express_token_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_sf_express_token_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_sf_express_token_id_seq OWNER TO luohd;

--
-- TOC entry 8401 (class 0 OID 0)
-- Dependencies: 444
-- Name: crm_sf_express_token_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_sf_express_token_id_seq OWNED BY public.crm_sf_express_token.id;


--
-- TOC entry 267 (class 1259 OID 360846)
-- Name: crm_stagehistory; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_stagehistory OWNER TO "_tenant";

--
-- TOC entry 480 (class 1259 OID 380101)
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


ALTER TABLE public.crm_subject_map OWNER TO luohd;

--
-- TOC entry 8404 (class 0 OID 0)
-- Dependencies: 480
-- Name: TABLE crm_subject_map; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_subject_map IS 'Subject 继承映射表';


--
-- TOC entry 8405 (class 0 OID 0)
-- Dependencies: 480
-- Name: COLUMN crm_subject_map.parent_subject_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_subject_map.parent_subject_type IS '父主体类型（如 Contact）';


--
-- TOC entry 8406 (class 0 OID 0)
-- Dependencies: 480
-- Name: COLUMN crm_subject_map.parent_subject_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_subject_map.parent_subject_id IS '父主体 ID';


--
-- TOC entry 8407 (class 0 OID 0)
-- Dependencies: 480
-- Name: COLUMN crm_subject_map.child_subject_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_subject_map.child_subject_type IS '子主体类型（如 Lead）';


--
-- TOC entry 8408 (class 0 OID 0)
-- Dependencies: 480
-- Name: COLUMN crm_subject_map.child_subject_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_subject_map.child_subject_id IS '子主体 ID';


--
-- TOC entry 8409 (class 0 OID 0)
-- Dependencies: 480
-- Name: COLUMN crm_subject_map.create_time; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_subject_map.create_time IS '创建时间';


--
-- TOC entry 479 (class 1259 OID 380100)
-- Name: crm_subject_map_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_subject_map_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_subject_map_id_seq OWNER TO luohd;

--
-- TOC entry 8411 (class 0 OID 0)
-- Dependencies: 479
-- Name: crm_subject_map_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_subject_map_id_seq OWNED BY public.crm_subject_map.id;


--
-- TOC entry 531 (class 1259 OID 404693)
-- Name: crm_survey_outreach_log; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_survey_outreach_log (
    id bigint NOT NULL,
    survey_id character varying(64) NOT NULL,
    channel character varying(32) NOT NULL,
    recipient_type character varying(32) NOT NULL,
    recipient_id character varying(128) NOT NULL,
    task_id character varying(64),
    status character varying(16) DEFAULT 'sent'::character varying NOT NULL,
    sent_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.crm_survey_outreach_log OWNER TO luohd;

--
-- TOC entry 530 (class 1259 OID 404692)
-- Name: crm_survey_outreach_log_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_survey_outreach_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_survey_outreach_log_id_seq OWNER TO luohd;

--
-- TOC entry 8414 (class 0 OID 0)
-- Dependencies: 530
-- Name: crm_survey_outreach_log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_survey_outreach_log_id_seq OWNED BY public.crm_survey_outreach_log.id;


--
-- TOC entry 268 (class 1259 OID 360854)
-- Name: crm_surveyresult; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_surveyresult OWNER TO "_tenant";

--
-- TOC entry 269 (class 1259 OID 360861)
-- Name: crm_surveys; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_surveys OWNER TO "_tenant";

--
-- TOC entry 582 (class 1259 OID 411115)
-- Name: crm_tag_channel_mapping; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_tag_channel_mapping (
    id bigint NOT NULL,
    dict_value character varying(100) NOT NULL,
    channel character varying(32) NOT NULL,
    channel_tag_id character varying(64) NOT NULL,
    channel_tag_name character varying(200) DEFAULT NULL::character varying,
    status character(1) DEFAULT '0'::bpchar NOT NULL,
    create_by character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    update_by character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.crm_tag_channel_mapping OWNER TO luohd;

--
-- TOC entry 8418 (class 0 OID 0)
-- Dependencies: 582
-- Name: TABLE crm_tag_channel_mapping; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_tag_channel_mapping IS 'CRM 中央标签 → 渠道标签单向映射';


--
-- TOC entry 8419 (class 0 OID 0)
-- Dependencies: 582
-- Name: COLUMN crm_tag_channel_mapping.channel; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_tag_channel_mapping.channel IS 'wecom / wechat_mp';


--
-- TOC entry 8420 (class 0 OID 0)
-- Dependencies: 582
-- Name: COLUMN crm_tag_channel_mapping.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_tag_channel_mapping.status IS '0 有效 / 1 失效（渠道侧已删除）';


--
-- TOC entry 581 (class 1259 OID 411114)
-- Name: crm_tag_channel_mapping_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_tag_channel_mapping_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_tag_channel_mapping_id_seq OWNER TO luohd;

--
-- TOC entry 8422 (class 0 OID 0)
-- Dependencies: 581
-- Name: crm_tag_channel_mapping_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_tag_channel_mapping_id_seq OWNED BY public.crm_tag_channel_mapping.id;


--
-- TOC entry 585 (class 1259 OID 411165)
-- Name: crm_tag_dict_ids_backfill_backup; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_tag_dict_ids_backfill_backup (
    entity text NOT NULL,
    entity_id text NOT NULL,
    tag jsonb,
    backed_up_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.crm_tag_dict_ids_backfill_backup OWNER TO luohd;

--
-- TOC entry 584 (class 1259 OID 411133)
-- Name: crm_tag_dispatch_task; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_tag_dispatch_task (
    id bigint NOT NULL,
    segment_id bigint NOT NULL,
    dict_value character varying(100) NOT NULL,
    channel character varying(32) NOT NULL,
    channel_tag_id character varying(64) NOT NULL,
    app_id character varying(64) DEFAULT NULL::character varying,
    status character varying(16) DEFAULT 'pending'::character varying NOT NULL,
    total integer DEFAULT 0 NOT NULL,
    succeeded integer DEFAULT 0 NOT NULL,
    failed integer DEFAULT 0 NOT NULL,
    error_message text,
    create_by character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.crm_tag_dispatch_task OWNER TO luohd;

--
-- TOC entry 8425 (class 0 OID 0)
-- Dependencies: 584
-- Name: TABLE crm_tag_dispatch_task; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_tag_dispatch_task IS 'CRM 标签渠道下发异步任务';


--
-- TOC entry 8426 (class 0 OID 0)
-- Dependencies: 584
-- Name: COLUMN crm_tag_dispatch_task.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_tag_dispatch_task.status IS 'pending / running / done / failed';


--
-- TOC entry 583 (class 1259 OID 411132)
-- Name: crm_tag_dispatch_task_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_tag_dispatch_task_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_tag_dispatch_task_id_seq OWNER TO luohd;

--
-- TOC entry 8428 (class 0 OID 0)
-- Dependencies: 583
-- Name: crm_tag_dispatch_task_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_tag_dispatch_task_id_seq OWNED BY public.crm_tag_dispatch_task.id;


--
-- TOC entry 586 (class 1259 OID 411207)
-- Name: crm_tag_legacy_ids_backfill_backup; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_tag_legacy_ids_backfill_backup (
    entity text NOT NULL,
    entity_id text NOT NULL,
    tag jsonb,
    backed_up_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.crm_tag_legacy_ids_backfill_backup OWNER TO luohd;

--
-- TOC entry 270 (class 1259 OID 360874)
-- Name: crm_task; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_task OWNER TO "_tenant";

--
-- TOC entry 596 (class 1259 OID 411684)
-- Name: crm_tenant_enterprise; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_tenant_enterprise (
    id character varying(64) NOT NULL,
    park_id character varying(64) NOT NULL,
    park_name_cn character varying(256) DEFAULT ''::character varying NOT NULL,
    name character varying(512) NOT NULL,
    name_cn character varying(512) NOT NULL,
    tax_code character varying(64),
    established_year integer DEFAULT 0 NOT NULL,
    registered_address text DEFAULT ''::text NOT NULL,
    industry character varying(256) DEFAULT ''::character varying NOT NULL,
    enterprise_type character varying(128) DEFAULT ''::character varying NOT NULL,
    country_of_origin character varying(128),
    investment_usd_million numeric(18,2),
    employee_count integer,
    factory_area_sqm numeric(18,2),
    status character varying(32),
    main_products text,
    contact_phone character varying(128),
    contact_email character varying(256),
    website character varying(1024),
    notes text,
    created_at character varying(32),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    del_flag character(1) DEFAULT '0'::bpchar NOT NULL,
    legal_representative character varying(256)
);


ALTER TABLE public.crm_tenant_enterprise OWNER TO luohd;

--
-- TOC entry 8432 (class 0 OID 0)
-- Dependencies: 596
-- Name: TABLE crm_tenant_enterprise; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_tenant_enterprise IS '越南工业园区入驻企业';


--
-- TOC entry 8433 (class 0 OID 0)
-- Dependencies: 596
-- Name: COLUMN crm_tenant_enterprise.park_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_tenant_enterprise.park_id IS '逻辑外键 → crm_industrial_park.id';


--
-- TOC entry 8434 (class 0 OID 0)
-- Dependencies: 596
-- Name: COLUMN crm_tenant_enterprise.park_name_cn; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_tenant_enterprise.park_name_cn IS '冗余园区中文名，列表免联表';


--
-- TOC entry 8435 (class 0 OID 0)
-- Dependencies: 596
-- Name: COLUMN crm_tenant_enterprise.tax_code; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_tenant_enterprise.tax_code IS '企业税务代码 / 越南税号 (MST)';


--
-- TOC entry 8436 (class 0 OID 0)
-- Dependencies: 596
-- Name: COLUMN crm_tenant_enterprise.enterprise_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_tenant_enterprise.enterprise_type IS 'FDI / WFOE / JV / Private / SOE / 分支机构等';


--
-- TOC entry 8437 (class 0 OID 0)
-- Dependencies: 596
-- Name: COLUMN crm_tenant_enterprise.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_tenant_enterprise.status IS '运营中 | 建设中 | 签约拟建 | 已停止经营活动 | 已注销';


--
-- TOC entry 8438 (class 0 OID 0)
-- Dependencies: 596
-- Name: COLUMN crm_tenant_enterprise.del_flag; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_tenant_enterprise.del_flag IS '0 active, 1 soft-deleted';


--
-- TOC entry 8439 (class 0 OID 0)
-- Dependencies: 596
-- Name: COLUMN crm_tenant_enterprise.legal_representative; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_tenant_enterprise.legal_representative IS '法定代表人';


--
-- TOC entry 535 (class 1259 OID 408083)
-- Name: crm_tenant_website_asset; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_tenant_website_asset (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    file_title character varying(255) NOT NULL,
    file_name character varying(500) NOT NULL,
    original_name character varying(500) NOT NULL,
    file_type character varying(50) NOT NULL,
    file_size bigint NOT NULL,
    media_type character varying(20) NOT NULL,
    mime_type character varying(100) NOT NULL,
    width integer,
    height integer,
    duration_ms integer,
    status character varying(20) DEFAULT 'active'::character varying NOT NULL,
    public_path character varying(255) NOT NULL,
    create_by character varying(64),
    create_time timestamp with time zone DEFAULT now() NOT NULL,
    update_time timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT chk_website_asset_media_type CHECK (((media_type)::text = ANY ((ARRAY['image'::character varying, 'video'::character varying])::text[]))),
    CONSTRAINT chk_website_asset_status CHECK (((status)::text = ANY ((ARRAY['active'::character varying, 'deleted'::character varying])::text[])))
);


ALTER TABLE public.crm_tenant_website_asset OWNER TO luohd;

--
-- TOC entry 8441 (class 0 OID 0)
-- Dependencies: 535
-- Name: TABLE crm_tenant_website_asset; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_tenant_website_asset IS '租户网站公开素材（图片/视频）';


--
-- TOC entry 8442 (class 0 OID 0)
-- Dependencies: 535
-- Name: COLUMN crm_tenant_website_asset.file_name; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_tenant_website_asset.file_name IS '服务器存储相对路径，与 common/upload 一致';


--
-- TOC entry 8443 (class 0 OID 0)
-- Dependencies: 535
-- Name: COLUMN crm_tenant_website_asset.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_tenant_website_asset.status IS 'active=可公开访问；deleted=软删除';


--
-- TOC entry 8444 (class 0 OID 0)
-- Dependencies: 535
-- Name: COLUMN crm_tenant_website_asset.public_path; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_tenant_website_asset.public_path IS '公开引用路径，固定 /_assets/{id}';


--
-- TOC entry 534 (class 1259 OID 407533)
-- Name: crm_tenant_website_page; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_tenant_website_page (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    path character varying(255) NOT NULL,
    title character varying(255) NOT NULL,
    draft_schema jsonb,
    published_schema jsonb,
    status character varying(20) DEFAULT 'draft'::character varying NOT NULL,
    schema_version integer DEFAULT 1 NOT NULL,
    published_at timestamp with time zone,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.crm_tenant_website_page OWNER TO luohd;

--
-- TOC entry 8446 (class 0 OID 0)
-- Dependencies: 534
-- Name: TABLE crm_tenant_website_page; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_tenant_website_page IS '租户官网页面 Schema（draft/published）';


--
-- TOC entry 8447 (class 0 OID 0)
-- Dependencies: 534
-- Name: COLUMN crm_tenant_website_page.draft_schema; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_tenant_website_page.draft_schema IS 'Website Schema JSON，禁止 HTML';


--
-- TOC entry 8448 (class 0 OID 0)
-- Dependencies: 534
-- Name: COLUMN crm_tenant_website_page.published_schema; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_tenant_website_page.published_schema IS '已发布 Website Schema JSON';


--
-- TOC entry 8449 (class 0 OID 0)
-- Dependencies: 534
-- Name: COLUMN crm_tenant_website_page.schema_version; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_tenant_website_page.schema_version IS 'Schema 版本号，供未来 migration';


--
-- TOC entry 271 (class 1259 OID 360884)
-- Name: crm_territory; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_territory OWNER TO "_tenant";

--
-- TOC entry 272 (class 1259 OID 360890)
-- Name: crm_ticket; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_ticket OWNER TO "_tenant";

--
-- TOC entry 273 (class 1259 OID 360897)
-- Name: crm_timeperiod; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_timeperiod OWNER TO "_tenant";

--
-- TOC entry 274 (class 1259 OID 360905)
-- Name: crm_travel; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_travel OWNER TO "_tenant";

--
-- TOC entry 624 (class 1259 OID 413201)
-- Name: crm_webchat_config; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_webchat_config (
    id bigint NOT NULL,
    tenant_id character varying(64) NOT NULL,
    website_token character varying(64) NOT NULL,
    enabled smallint DEFAULT 1 NOT NULL,
    display_name character varying(128),
    logo_url character varying(1024),
    welcome_message character varying(1024),
    theme_color character varying(32),
    "position" character varying(32) DEFAULT 'bottom-right'::character varying NOT NULL,
    allowed_domains text DEFAULT '[]'::text NOT NULL,
    ai_enabled smallint DEFAULT 0 NOT NULL,
    offline_message character varying(1024),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


ALTER TABLE public.crm_webchat_config OWNER TO luohd;

--
-- TOC entry 8455 (class 0 OID 0)
-- Dependencies: 624
-- Name: TABLE crm_webchat_config; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_webchat_config IS '租户级 Website Chat 配置；一租户一行';


--
-- TOC entry 8456 (class 0 OID 0)
-- Dependencies: 624
-- Name: COLUMN crm_webchat_config.website_token; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_webchat_config.website_token IS '公开 Widget 标识 wc_xxx；非密钥';


--
-- TOC entry 8457 (class 0 OID 0)
-- Dependencies: 624
-- Name: COLUMN crm_webchat_config."position"; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_webchat_config."position" IS 'bottom-right | bottom-left';


--
-- TOC entry 8458 (class 0 OID 0)
-- Dependencies: 624
-- Name: COLUMN crm_webchat_config.allowed_domains; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_webchat_config.allowed_domains IS 'JSON 字符串数组；Hostname 白名单（不含 scheme/path）';


--
-- TOC entry 8459 (class 0 OID 0)
-- Dependencies: 624
-- Name: COLUMN crm_webchat_config.ai_enabled; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_webchat_config.ai_enabled IS '渠道级 AI 总开关；与 Inbox Agent channels[].hosted 双层门闩（P2）';


--
-- TOC entry 623 (class 1259 OID 413200)
-- Name: crm_webchat_config_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_webchat_config_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_webchat_config_id_seq OWNER TO luohd;

--
-- TOC entry 8461 (class 0 OID 0)
-- Dependencies: 623
-- Name: crm_webchat_config_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_webchat_config_id_seq OWNED BY public.crm_webchat_config.id;


--
-- TOC entry 628 (class 1259 OID 413236)
-- Name: crm_webchat_conversation; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_webchat_conversation (
    id bigint NOT NULL,
    tenant_id character varying(64) NOT NULL,
    visitor_id character varying(64) NOT NULL,
    profile_name character varying(256),
    contact_id character varying(64),
    lead_id character varying(64),
    last_message_at timestamp without time zone,
    last_message_preview character varying(256),
    last_customer_message_at timestamp without time zone,
    last_outbound_at timestamp without time zone,
    unread_count integer DEFAULT 0 NOT NULL,
    status character varying(16) DEFAULT 'active'::character varying NOT NULL,
    user_id character varying(64),
    owner character varying(128),
    assigned_at timestamp without time zone,
    hosted_by_ai boolean,
    closed_at timestamp without time zone,
    create_time timestamp without time zone DEFAULT now() NOT NULL,
    update_time timestamp without time zone
);


ALTER TABLE public.crm_webchat_conversation OWNER TO luohd;

--
-- TOC entry 8463 (class 0 OID 0)
-- Dependencies: 628
-- Name: TABLE crm_webchat_conversation; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_webchat_conversation IS 'Web Chat 会话；一 visitor 同时最多一条 active';


--
-- TOC entry 8464 (class 0 OID 0)
-- Dependencies: 628
-- Name: COLUMN crm_webchat_conversation.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_webchat_conversation.status IS 'active | closed | archived | blocked';


--
-- TOC entry 8465 (class 0 OID 0)
-- Dependencies: 628
-- Name: COLUMN crm_webchat_conversation.hosted_by_ai; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_webchat_conversation.hosted_by_ai IS 'null/true=可跟渠道托管；false=人工已接管';


--
-- TOC entry 627 (class 1259 OID 413235)
-- Name: crm_webchat_conversation_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_webchat_conversation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_webchat_conversation_id_seq OWNER TO luohd;

--
-- TOC entry 8467 (class 0 OID 0)
-- Dependencies: 627
-- Name: crm_webchat_conversation_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_webchat_conversation_id_seq OWNED BY public.crm_webchat_conversation.id;


--
-- TOC entry 630 (class 1259 OID 413253)
-- Name: crm_webchat_message; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_webchat_message (
    id bigint NOT NULL,
    tenant_id character varying(64) NOT NULL,
    conversation_id bigint NOT NULL,
    visitor_id character varying(64) NOT NULL,
    client_message_id character varying(64),
    message_type character varying(32) DEFAULT 'text'::character varying NOT NULL,
    content text,
    direction smallint NOT NULL,
    sender_type character varying(16) NOT NULL,
    status character varying(32) DEFAULT 'sent'::character varying NOT NULL,
    error_message character varying(512),
    agent_user_id character varying(64),
    source character varying(64),
    create_time timestamp without time zone DEFAULT now() NOT NULL,
    update_time timestamp without time zone
);


ALTER TABLE public.crm_webchat_message OWNER TO luohd;

--
-- TOC entry 8469 (class 0 OID 0)
-- Dependencies: 630
-- Name: TABLE crm_webchat_message; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_webchat_message IS 'Web Chat 消息明细';


--
-- TOC entry 8470 (class 0 OID 0)
-- Dependencies: 630
-- Name: COLUMN crm_webchat_message.client_message_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_webchat_message.client_message_id IS '访客侧幂等键；重试不重复落库';


--
-- TOC entry 8471 (class 0 OID 0)
-- Dependencies: 630
-- Name: COLUMN crm_webchat_message.direction; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_webchat_message.direction IS '0=visitor inbound 1=outbound';


--
-- TOC entry 8472 (class 0 OID 0)
-- Dependencies: 630
-- Name: COLUMN crm_webchat_message.sender_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_webchat_message.sender_type IS 'visitor | agent | ai | system';


--
-- TOC entry 8473 (class 0 OID 0)
-- Dependencies: 630
-- Name: COLUMN crm_webchat_message.source; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_webchat_message.source IS 'null=人工；ai_inbox_agent=Inbox Agent';


--
-- TOC entry 629 (class 1259 OID 413252)
-- Name: crm_webchat_message_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_webchat_message_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_webchat_message_id_seq OWNER TO luohd;

--
-- TOC entry 8475 (class 0 OID 0)
-- Dependencies: 629
-- Name: crm_webchat_message_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_webchat_message_id_seq OWNED BY public.crm_webchat_message.id;


--
-- TOC entry 626 (class 1259 OID 413219)
-- Name: crm_webchat_visitor; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_webchat_visitor (
    id bigint NOT NULL,
    tenant_id character varying(64) NOT NULL,
    visitor_id character varying(64) NOT NULL,
    website_session_id character varying(64),
    display_name character varying(256),
    email character varying(256),
    mobile_phone character varying(64),
    whatsapp character varying(64),
    contact_id character varying(64),
    lead_id character varying(64),
    user_agent character varying(512),
    first_seen_at timestamp without time zone DEFAULT now() NOT NULL,
    last_seen_at timestamp without time zone DEFAULT now() NOT NULL,
    create_time timestamp without time zone DEFAULT now() NOT NULL,
    update_time timestamp without time zone
);


ALTER TABLE public.crm_webchat_visitor OWNER TO luohd;

--
-- TOC entry 8477 (class 0 OID 0)
-- Dependencies: 626
-- Name: TABLE crm_webchat_visitor; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_webchat_visitor IS 'Web Chat 访客；visitor_id 由浏览器持久化';


--
-- TOC entry 8478 (class 0 OID 0)
-- Dependencies: 626
-- Name: COLUMN crm_webchat_visitor.website_session_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_webchat_visitor.website_session_id IS '关联 crm_website_visitor_session.session_id';


--
-- TOC entry 625 (class 1259 OID 413218)
-- Name: crm_webchat_visitor_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_webchat_visitor_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_webchat_visitor_id_seq OWNER TO luohd;

--
-- TOC entry 8480 (class 0 OID 0)
-- Dependencies: 625
-- Name: crm_webchat_visitor_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_webchat_visitor_id_seq OWNED BY public.crm_webchat_visitor.id;


--
-- TOC entry 577 (class 1259 OID 409225)
-- Name: crm_website_page_view; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_website_page_view (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    session_id character varying(64) NOT NULL,
    page_url character varying(2048) NOT NULL,
    page_description character varying(512),
    referrer character varying(2048),
    viewed_at timestamp with time zone NOT NULL,
    synced_to_activity boolean DEFAULT false NOT NULL,
    activity_id character varying(64),
    create_time timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.crm_website_page_view OWNER TO luohd;

--
-- TOC entry 8482 (class 0 OID 0)
-- Dependencies: 577
-- Name: TABLE crm_website_page_view; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_website_page_view IS '官网页面浏览明细（独立表，投影后写 crm_activity）';


--
-- TOC entry 8483 (class 0 OID 0)
-- Dependencies: 577
-- Name: COLUMN crm_website_page_view.synced_to_activity; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_website_page_view.synced_to_activity IS 'true=已投影为 website.page.view activity';


--
-- TOC entry 576 (class 1259 OID 409214)
-- Name: crm_website_visitor_session; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_website_visitor_session (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    session_id character varying(64) NOT NULL,
    subject_type character varying(16),
    subject_id character varying(64),
    bound_at timestamp with time zone,
    visitor_token_hash character varying(128),
    create_time timestamp with time zone DEFAULT now() NOT NULL,
    update_time timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.crm_website_visitor_session OWNER TO luohd;

--
-- TOC entry 8485 (class 0 OID 0)
-- Dependencies: 576
-- Name: TABLE crm_website_visitor_session; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_website_visitor_session IS '官网访客会话（Cookie _sess）';


--
-- TOC entry 8486 (class 0 OID 0)
-- Dependencies: 576
-- Name: COLUMN crm_website_visitor_session.session_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_website_visitor_session.session_id IS '浏览器 Cookie _sess；租户库内唯一';


--
-- TOC entry 8487 (class 0 OID 0)
-- Dependencies: 576
-- Name: COLUMN crm_website_visitor_session.subject_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_website_visitor_session.subject_type IS '绑定后为 Lead 或 Contact；未报名前为空';


--
-- TOC entry 592 (class 1259 OID 411305)
-- Name: crm_wechat_miniapp_config; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wechat_miniapp_config (
    id bigint NOT NULL,
    app_id character varying(64) NOT NULL,
    app_secret_enc text,
    name character varying(128),
    connected boolean DEFAULT false NOT NULL,
    last_error text,
    create_by character varying(64),
    create_time timestamp with time zone DEFAULT now() NOT NULL,
    update_by character varying(64),
    update_time timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.crm_wechat_miniapp_config OWNER TO luohd;

--
-- TOC entry 8489 (class 0 OID 0)
-- Dependencies: 592
-- Name: TABLE crm_wechat_miniapp_config; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wechat_miniapp_config IS '微信小程序租户配置（一租户至多一行）';


--
-- TOC entry 8490 (class 0 OID 0)
-- Dependencies: 592
-- Name: COLUMN crm_wechat_miniapp_config.app_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_miniapp_config.app_id IS '微信小程序 AppID';


--
-- TOC entry 8491 (class 0 OID 0)
-- Dependencies: 592
-- Name: COLUMN crm_wechat_miniapp_config.app_secret_enc; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_miniapp_config.app_secret_enc IS 'AppSecret（AES 加密存储）';


--
-- TOC entry 8492 (class 0 OID 0)
-- Dependencies: 592
-- Name: COLUMN crm_wechat_miniapp_config.name; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_miniapp_config.name IS '备注名称';


--
-- TOC entry 8493 (class 0 OID 0)
-- Dependencies: 592
-- Name: COLUMN crm_wechat_miniapp_config.connected; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_miniapp_config.connected IS '是否已验证连通（cgi-bin/token 成功）';


--
-- TOC entry 8494 (class 0 OID 0)
-- Dependencies: 592
-- Name: COLUMN crm_wechat_miniapp_config.last_error; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_miniapp_config.last_error IS '最近一次错误摘要';


--
-- TOC entry 591 (class 1259 OID 411304)
-- Name: crm_wechat_miniapp_config_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wechat_miniapp_config_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wechat_miniapp_config_id_seq OWNER TO luohd;

--
-- TOC entry 8496 (class 0 OID 0)
-- Dependencies: 591
-- Name: crm_wechat_miniapp_config_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wechat_miniapp_config_id_seq OWNED BY public.crm_wechat_miniapp_config.id;


--
-- TOC entry 551 (class 1259 OID 408734)
-- Name: crm_wechat_mp_article; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wechat_mp_article (
    id bigint NOT NULL,
    app_id character varying(64) NOT NULL,
    article_id character varying(128) NOT NULL,
    title character varying(512),
    url character varying(1024) NOT NULL,
    thumb_url character varying(512),
    digest character varying(1024),
    author character varying(128),
    index_in_msg integer DEFAULT 0 NOT NULL,
    update_time timestamp with time zone,
    content text,
    content_source_url character varying(1024),
    thumb_media_id character varying(128),
    need_open_comment smallint DEFAULT 0,
    only_fans_can_comment smallint DEFAULT 0,
    is_deleted boolean DEFAULT false NOT NULL,
    detail_fetched boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.crm_wechat_mp_article OWNER TO luohd;

--
-- TOC entry 8498 (class 0 OID 0)
-- Dependencies: 551
-- Name: TABLE crm_wechat_mp_article; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wechat_mp_article IS '微信公众号已发布图文本地缓存';


--
-- TOC entry 8499 (class 0 OID 0)
-- Dependencies: 551
-- Name: COLUMN crm_wechat_mp_article.article_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article.article_id IS '微信 article_id，同一发布批次中多篇图文共享';


--
-- TOC entry 8500 (class 0 OID 0)
-- Dependencies: 551
-- Name: COLUMN crm_wechat_mp_article.index_in_msg; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article.index_in_msg IS '图文在当次发布中的序号（0-based）';


--
-- TOC entry 8501 (class 0 OID 0)
-- Dependencies: 551
-- Name: COLUMN crm_wechat_mp_article.content; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article.content IS '图文正文 HTML，来自 freepublish/getarticle';


--
-- TOC entry 8502 (class 0 OID 0)
-- Dependencies: 551
-- Name: COLUMN crm_wechat_mp_article.content_source_url; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article.content_source_url IS '原文地址（阅读原文链接）';


--
-- TOC entry 8503 (class 0 OID 0)
-- Dependencies: 551
-- Name: COLUMN crm_wechat_mp_article.thumb_media_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article.thumb_media_id IS '封面永久素材 MediaID';


--
-- TOC entry 8504 (class 0 OID 0)
-- Dependencies: 551
-- Name: COLUMN crm_wechat_mp_article.detail_fetched; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article.detail_fetched IS '是否已通过 getarticle 拉取详细数据';


--
-- TOC entry 650 (class 1259 OID 413496)
-- Name: crm_wechat_mp_article_comment; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wechat_mp_article_comment (
    id bigint NOT NULL,
    app_id character varying(64) NOT NULL,
    msg_data_id character varying(64) NOT NULL,
    index_in_msg integer DEFAULT 0 NOT NULL,
    article_id character varying(128),
    article_title character varying(512),
    article_url character varying(1024),
    user_comment_id bigint NOT NULL,
    openid character varying(128),
    content text NOT NULL,
    comment_type smallint DEFAULT 0 NOT NULL,
    comment_time timestamp with time zone NOT NULL,
    reply_content text,
    reply_time timestamp with time zone,
    status character varying(16) DEFAULT 'pending'::character varying NOT NULL,
    is_read boolean DEFAULT false NOT NULL,
    contact_id character varying(100),
    review_required boolean DEFAULT false NOT NULL,
    auto_reply_rule_id bigint,
    synced_at timestamp with time zone DEFAULT now() NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    lead_id character varying(100)
);


ALTER TABLE public.crm_wechat_mp_article_comment OWNER TO luohd;

--
-- TOC entry 8506 (class 0 OID 0)
-- Dependencies: 650
-- Name: TABLE crm_wechat_mp_article_comment; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wechat_mp_article_comment IS '微信公众号图文留言（comment/list 同步）';


--
-- TOC entry 8507 (class 0 OID 0)
-- Dependencies: 650
-- Name: COLUMN crm_wechat_mp_article_comment.user_comment_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article_comment.user_comment_id IS '微信侧留言 ID';


--
-- TOC entry 8508 (class 0 OID 0)
-- Dependencies: 650
-- Name: COLUMN crm_wechat_mp_article_comment.comment_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article_comment.comment_type IS '0 普通 1 精选（微信 comment_type）';


--
-- TOC entry 8509 (class 0 OID 0)
-- Dependencies: 650
-- Name: COLUMN crm_wechat_mp_article_comment.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article_comment.status IS 'pending | replied';


--
-- TOC entry 8510 (class 0 OID 0)
-- Dependencies: 650
-- Name: COLUMN crm_wechat_mp_article_comment.lead_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article_comment.lead_id IS '侧栏关联线索 ID';


--
-- TOC entry 649 (class 1259 OID 413495)
-- Name: crm_wechat_mp_article_comment_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wechat_mp_article_comment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wechat_mp_article_comment_id_seq OWNER TO luohd;

--
-- TOC entry 8512 (class 0 OID 0)
-- Dependencies: 649
-- Name: crm_wechat_mp_article_comment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wechat_mp_article_comment_id_seq OWNED BY public.crm_wechat_mp_article_comment.id;


--
-- TOC entry 553 (class 1259 OID 408771)
-- Name: crm_wechat_mp_article_draft; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wechat_mp_article_draft (
    id bigint NOT NULL,
    app_id character varying(64) NOT NULL,
    media_id character varying(128) NOT NULL,
    index_in_msg integer DEFAULT 0 NOT NULL,
    article_type character varying(16) DEFAULT 'news'::character varying NOT NULL,
    title character varying(512),
    author character varying(128),
    digest character varying(1024),
    content text,
    content_md text,
    content_source_url character varying(1024),
    thumb_media_id character varying(128),
    need_open_comment smallint DEFAULT 0,
    only_fans_can_comment smallint DEFAULT 0,
    url character varying(1024),
    update_time timestamp with time zone,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    synced boolean DEFAULT true NOT NULL,
    published boolean DEFAULT false NOT NULL
);


ALTER TABLE public.crm_wechat_mp_article_draft OWNER TO luohd;

--
-- TOC entry 8514 (class 0 OID 0)
-- Dependencies: 553
-- Name: TABLE crm_wechat_mp_article_draft; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wechat_mp_article_draft IS '微信公众号草稿文章本地缓存';


--
-- TOC entry 8515 (class 0 OID 0)
-- Dependencies: 553
-- Name: COLUMN crm_wechat_mp_article_draft.media_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article_draft.media_id IS '微信草稿 media_id';


--
-- TOC entry 8516 (class 0 OID 0)
-- Dependencies: 553
-- Name: COLUMN crm_wechat_mp_article_draft.index_in_msg; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article_draft.index_in_msg IS '草稿多条图文中的序号（0-based）';


--
-- TOC entry 8517 (class 0 OID 0)
-- Dependencies: 553
-- Name: COLUMN crm_wechat_mp_article_draft.article_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article_draft.article_type IS '文章类型: news(图文消息) / newspic(图片消息)';


--
-- TOC entry 8518 (class 0 OID 0)
-- Dependencies: 553
-- Name: COLUMN crm_wechat_mp_article_draft.content; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article_draft.content IS '图文正文 HTML，来自 draft/get';


--
-- TOC entry 8519 (class 0 OID 0)
-- Dependencies: 553
-- Name: COLUMN crm_wechat_mp_article_draft.content_md; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article_draft.content_md IS '图文正文 Markdown。用户通过本系统创建/编辑时有值，微信同步回的数据为 NULL';


--
-- TOC entry 8520 (class 0 OID 0)
-- Dependencies: 553
-- Name: COLUMN crm_wechat_mp_article_draft.content_source_url; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article_draft.content_source_url IS '原文地址（阅读原文链接）';


--
-- TOC entry 8521 (class 0 OID 0)
-- Dependencies: 553
-- Name: COLUMN crm_wechat_mp_article_draft.thumb_media_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article_draft.thumb_media_id IS '封面永久素材 MediaID';


--
-- TOC entry 8522 (class 0 OID 0)
-- Dependencies: 553
-- Name: COLUMN crm_wechat_mp_article_draft.url; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article_draft.url IS '草稿临时链接';


--
-- TOC entry 8523 (class 0 OID 0)
-- Dependencies: 553
-- Name: COLUMN crm_wechat_mp_article_draft.published; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article_draft.published IS '是否已发布: false=未发布, true=已发布';


--
-- TOC entry 552 (class 1259 OID 408770)
-- Name: crm_wechat_mp_article_draft_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wechat_mp_article_draft_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wechat_mp_article_draft_id_seq OWNER TO luohd;

--
-- TOC entry 8525 (class 0 OID 0)
-- Dependencies: 552
-- Name: crm_wechat_mp_article_draft_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wechat_mp_article_draft_id_seq OWNED BY public.crm_wechat_mp_article_draft.id;


--
-- TOC entry 550 (class 1259 OID 408733)
-- Name: crm_wechat_mp_article_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wechat_mp_article_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wechat_mp_article_id_seq OWNER TO luohd;

--
-- TOC entry 8527 (class 0 OID 0)
-- Dependencies: 550
-- Name: crm_wechat_mp_article_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wechat_mp_article_id_seq OWNED BY public.crm_wechat_mp_article.id;


--
-- TOC entry 559 (class 1259 OID 408916)
-- Name: crm_wechat_mp_article_stat; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wechat_mp_article_stat (
    id bigint NOT NULL,
    app_id character varying(64) NOT NULL,
    article_id character varying(128) NOT NULL,
    index_in_msg integer DEFAULT 0 NOT NULL,
    ref_date date NOT NULL,
    stat_date date NOT NULL,
    read_user integer DEFAULT 0,
    share_user integer DEFAULT 0,
    zaikan_user integer DEFAULT 0,
    like_user integer DEFAULT 0,
    comment_count integer DEFAULT 0,
    collection_user integer DEFAULT 0,
    praise_money integer DEFAULT 0,
    read_subscribe_user integer DEFAULT 0,
    read_delivery_rate numeric(10,7) DEFAULT 0,
    read_finish_rate numeric(10,7) DEFAULT 0,
    read_avg_activetime numeric(10,7) DEFAULT 0,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.crm_wechat_mp_article_stat OWNER TO luohd;

--
-- TOC entry 8529 (class 0 OID 0)
-- Dependencies: 559
-- Name: TABLE crm_wechat_mp_article_stat; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wechat_mp_article_stat IS '微信公众号文章统计数据';


--
-- TOC entry 8530 (class 0 OID 0)
-- Dependencies: 559
-- Name: COLUMN crm_wechat_mp_article_stat.article_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article_stat.article_id IS '微信 article_id，关联 crm_wechat_mp_article 表';


--
-- TOC entry 8531 (class 0 OID 0)
-- Dependencies: 559
-- Name: COLUMN crm_wechat_mp_article_stat.index_in_msg; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article_stat.index_in_msg IS '多图文中的文章序号（0-based）';


--
-- TOC entry 8532 (class 0 OID 0)
-- Dependencies: 559
-- Name: COLUMN crm_wechat_mp_article_stat.ref_date; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article_stat.ref_date IS '文章发表日期';


--
-- TOC entry 8533 (class 0 OID 0)
-- Dependencies: 559
-- Name: COLUMN crm_wechat_mp_article_stat.stat_date; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article_stat.stat_date IS '统计日期（数据所属日期）';


--
-- TOC entry 8534 (class 0 OID 0)
-- Dependencies: 559
-- Name: COLUMN crm_wechat_mp_article_stat.read_user; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article_stat.read_user IS '阅读人数';


--
-- TOC entry 8535 (class 0 OID 0)
-- Dependencies: 559
-- Name: COLUMN crm_wechat_mp_article_stat.share_user; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article_stat.share_user IS '分享人数';


--
-- TOC entry 8536 (class 0 OID 0)
-- Dependencies: 559
-- Name: COLUMN crm_wechat_mp_article_stat.zaikan_user; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article_stat.zaikan_user IS '爱心赞人数';


--
-- TOC entry 8537 (class 0 OID 0)
-- Dependencies: 559
-- Name: COLUMN crm_wechat_mp_article_stat.like_user; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article_stat.like_user IS '拇指赞人数';


--
-- TOC entry 8538 (class 0 OID 0)
-- Dependencies: 559
-- Name: COLUMN crm_wechat_mp_article_stat.comment_count; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article_stat.comment_count IS '留言条数';


--
-- TOC entry 8539 (class 0 OID 0)
-- Dependencies: 559
-- Name: COLUMN crm_wechat_mp_article_stat.collection_user; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article_stat.collection_user IS '微信收藏人数';


--
-- TOC entry 8540 (class 0 OID 0)
-- Dependencies: 559
-- Name: COLUMN crm_wechat_mp_article_stat.praise_money; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article_stat.praise_money IS '赞赏金额（分）';


--
-- TOC entry 8541 (class 0 OID 0)
-- Dependencies: 559
-- Name: COLUMN crm_wechat_mp_article_stat.read_subscribe_user; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article_stat.read_subscribe_user IS '阅读后关注人数';


--
-- TOC entry 8542 (class 0 OID 0)
-- Dependencies: 559
-- Name: COLUMN crm_wechat_mp_article_stat.read_delivery_rate; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article_stat.read_delivery_rate IS '阅读送达率';


--
-- TOC entry 8543 (class 0 OID 0)
-- Dependencies: 559
-- Name: COLUMN crm_wechat_mp_article_stat.read_finish_rate; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article_stat.read_finish_rate IS '阅读完成率';


--
-- TOC entry 8544 (class 0 OID 0)
-- Dependencies: 559
-- Name: COLUMN crm_wechat_mp_article_stat.read_avg_activetime; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_article_stat.read_avg_activetime IS '平均阅读时长（分钟）';


--
-- TOC entry 558 (class 1259 OID 408915)
-- Name: crm_wechat_mp_article_stat_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wechat_mp_article_stat_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wechat_mp_article_stat_id_seq OWNER TO luohd;

--
-- TOC entry 8546 (class 0 OID 0)
-- Dependencies: 558
-- Name: crm_wechat_mp_article_stat_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wechat_mp_article_stat_id_seq OWNED BY public.crm_wechat_mp_article_stat.id;


--
-- TOC entry 511 (class 1259 OID 404440)
-- Name: crm_wechat_mp_auto_reply; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wechat_mp_auto_reply (
    id bigint NOT NULL,
    app_id character varying(64) NOT NULL,
    rule_type character varying(32) NOT NULL,
    match_value character varying(256),
    reply_type character varying(16) DEFAULT 'text'::character varying NOT NULL,
    reply_content text NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    priority integer DEFAULT 0 NOT NULL,
    create_by character varying(64),
    create_time timestamp without time zone DEFAULT now(),
    update_time timestamp without time zone DEFAULT now()
);


ALTER TABLE public.crm_wechat_mp_auto_reply OWNER TO luohd;

--
-- TOC entry 510 (class 1259 OID 404439)
-- Name: crm_wechat_mp_auto_reply_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wechat_mp_auto_reply_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wechat_mp_auto_reply_id_seq OWNER TO luohd;

--
-- TOC entry 8549 (class 0 OID 0)
-- Dependencies: 510
-- Name: crm_wechat_mp_auto_reply_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wechat_mp_auto_reply_id_seq OWNED BY public.crm_wechat_mp_auto_reply.id;


--
-- TOC entry 489 (class 1259 OID 404196)
-- Name: crm_wechat_mp_callback_log; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wechat_mp_callback_log (
    id bigint NOT NULL,
    msg_type character varying(32),
    event character varying(64),
    from_user character varying(128),
    to_user character varying(128),
    raw_xml text,
    parsed boolean DEFAULT false,
    error_msg text,
    create_time timestamp without time zone DEFAULT now()
);


ALTER TABLE public.crm_wechat_mp_callback_log OWNER TO luohd;

--
-- TOC entry 8551 (class 0 OID 0)
-- Dependencies: 489
-- Name: TABLE crm_wechat_mp_callback_log; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wechat_mp_callback_log IS '微信公众号回调原始日志';


--
-- TOC entry 488 (class 1259 OID 404195)
-- Name: crm_wechat_mp_callback_log_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wechat_mp_callback_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wechat_mp_callback_log_id_seq OWNER TO luohd;

--
-- TOC entry 8553 (class 0 OID 0)
-- Dependencies: 488
-- Name: crm_wechat_mp_callback_log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wechat_mp_callback_log_id_seq OWNED BY public.crm_wechat_mp_callback_log.id;


--
-- TOC entry 652 (class 1259 OID 413518)
-- Name: crm_wechat_mp_comment_auto_reply_rule; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wechat_mp_comment_auto_reply_rule (
    id bigint NOT NULL,
    app_id character varying(64),
    keyword character varying(256) NOT NULL,
    match_mode character varying(16) DEFAULT 'contains'::character varying NOT NULL,
    reply_content text NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    priority integer DEFAULT 100 NOT NULL,
    require_review boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.crm_wechat_mp_comment_auto_reply_rule OWNER TO luohd;

--
-- TOC entry 651 (class 1259 OID 413517)
-- Name: crm_wechat_mp_comment_auto_reply_rule_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wechat_mp_comment_auto_reply_rule_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wechat_mp_comment_auto_reply_rule_id_seq OWNER TO luohd;

--
-- TOC entry 8556 (class 0 OID 0)
-- Dependencies: 651
-- Name: crm_wechat_mp_comment_auto_reply_rule_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wechat_mp_comment_auto_reply_rule_id_seq OWNED BY public.crm_wechat_mp_comment_auto_reply_rule.id;


--
-- TOC entry 493 (class 1259 OID 404293)
-- Name: crm_wechat_mp_config; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wechat_mp_config (
    id bigint NOT NULL,
    app_id character varying(64) NOT NULL,
    secret_enc text NOT NULL,
    token character varying(128),
    encoding_aes_key character varying(128),
    verify_signature boolean DEFAULT true NOT NULL,
    connected boolean DEFAULT false NOT NULL,
    last_error character varying(512),
    create_by character varying(64),
    create_time timestamp with time zone,
    update_by character varying(64),
    update_time timestamp with time zone,
    account_type character varying(16),
    account_name character varying(128),
    account_synced_at timestamp without time zone
);


ALTER TABLE public.crm_wechat_mp_config OWNER TO luohd;

--
-- TOC entry 8558 (class 0 OID 0)
-- Dependencies: 493
-- Name: TABLE crm_wechat_mp_config; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wechat_mp_config IS '微信公众号租户配置（每租户可配多个公众号）';


--
-- TOC entry 8559 (class 0 OID 0)
-- Dependencies: 493
-- Name: COLUMN crm_wechat_mp_config.app_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_config.app_id IS '微信公众号 AppID';


--
-- TOC entry 8560 (class 0 OID 0)
-- Dependencies: 493
-- Name: COLUMN crm_wechat_mp_config.secret_enc; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_config.secret_enc IS 'AppSecret（AES 加密存储）';


--
-- TOC entry 8561 (class 0 OID 0)
-- Dependencies: 493
-- Name: COLUMN crm_wechat_mp_config.token; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_config.token IS '回调验证 Token';


--
-- TOC entry 8562 (class 0 OID 0)
-- Dependencies: 493
-- Name: COLUMN crm_wechat_mp_config.encoding_aes_key; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_config.encoding_aes_key IS '回调消息加解密 AES Key';


--
-- TOC entry 8563 (class 0 OID 0)
-- Dependencies: 493
-- Name: COLUMN crm_wechat_mp_config.verify_signature; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_config.verify_signature IS '是否启用回调签名校验';


--
-- TOC entry 8564 (class 0 OID 0)
-- Dependencies: 493
-- Name: COLUMN crm_wechat_mp_config.connected; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_config.connected IS '是否已验证连通（getAccessToken 成功）';


--
-- TOC entry 8565 (class 0 OID 0)
-- Dependencies: 493
-- Name: COLUMN crm_wechat_mp_config.last_error; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_config.last_error IS '最近一次错误摘要';


--
-- TOC entry 492 (class 1259 OID 404292)
-- Name: crm_wechat_mp_config_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wechat_mp_config_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wechat_mp_config_id_seq OWNER TO luohd;

--
-- TOC entry 8567 (class 0 OID 0)
-- Dependencies: 492
-- Name: crm_wechat_mp_config_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wechat_mp_config_id_seq OWNED BY public.crm_wechat_mp_config.id;


--
-- TOC entry 612 (class 1259 OID 413065)
-- Name: crm_wechat_mp_conversation; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wechat_mp_conversation (
    id bigint NOT NULL,
    app_id character varying(64) NOT NULL,
    open_id character varying(128) NOT NULL,
    nickname character varying(128),
    avatar_url text,
    last_message character varying(512),
    last_message_at timestamp without time zone,
    last_customer_message_at timestamp without time zone,
    message_count integer DEFAULT 0 NOT NULL,
    unread_count integer DEFAULT 0 NOT NULL,
    user_id bigint,
    owner character varying(128),
    assigned_at timestamp without time zone,
    contact_id character varying(64),
    lead_id character varying(64),
    create_time timestamp without time zone DEFAULT now(),
    update_time timestamp without time zone DEFAULT now(),
    hosted_by_ai boolean
);


ALTER TABLE public.crm_wechat_mp_conversation OWNER TO luohd;

--
-- TOC entry 8569 (class 0 OID 0)
-- Dependencies: 612
-- Name: TABLE crm_wechat_mp_conversation; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wechat_mp_conversation IS '公众号坐席会话（公共池 / 我的 / 团队）';


--
-- TOC entry 8570 (class 0 OID 0)
-- Dependencies: 612
-- Name: COLUMN crm_wechat_mp_conversation.last_customer_message_at; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_conversation.last_customer_message_at IS '最近一次粉丝入站时间（48h 客服窗起点）';


--
-- TOC entry 8571 (class 0 OID 0)
-- Dependencies: 612
-- Name: COLUMN crm_wechat_mp_conversation.message_count; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_conversation.message_count IS '入站消息合计';


--
-- TOC entry 8572 (class 0 OID 0)
-- Dependencies: 612
-- Name: COLUMN crm_wechat_mp_conversation.unread_count; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_conversation.unread_count IS '未读入站条数';


--
-- TOC entry 8573 (class 0 OID 0)
-- Dependencies: 612
-- Name: COLUMN crm_wechat_mp_conversation.user_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_conversation.user_id IS '负责人系统用户 id；NULL = 公共池';


--
-- TOC entry 8574 (class 0 OID 0)
-- Dependencies: 612
-- Name: COLUMN crm_wechat_mp_conversation.owner; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_conversation.owner IS '负责人展示名';


--
-- TOC entry 8575 (class 0 OID 0)
-- Dependencies: 612
-- Name: COLUMN crm_wechat_mp_conversation.assigned_at; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_conversation.assigned_at IS '最近一次领取 / 指派时间';


--
-- TOC entry 8576 (class 0 OID 0)
-- Dependencies: 612
-- Name: COLUMN crm_wechat_mp_conversation.hosted_by_ai; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_conversation.hosted_by_ai IS 'true=AI托管; false=人工接管; NULL=跟随渠道 hosted';


--
-- TOC entry 611 (class 1259 OID 413064)
-- Name: crm_wechat_mp_conversation_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wechat_mp_conversation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wechat_mp_conversation_id_seq OWNER TO luohd;

--
-- TOC entry 8578 (class 0 OID 0)
-- Dependencies: 611
-- Name: crm_wechat_mp_conversation_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wechat_mp_conversation_id_seq OWNED BY public.crm_wechat_mp_conversation.id;


--
-- TOC entry 503 (class 1259 OID 404380)
-- Name: crm_wechat_mp_fan; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wechat_mp_fan (
    id bigint NOT NULL,
    app_id character varying(64) NOT NULL,
    open_id character varying(128) NOT NULL,
    union_id character varying(64),
    nickname character varying(128),
    avatar_url text,
    subscribe boolean DEFAULT true NOT NULL,
    subscribe_time timestamp without time zone,
    unsubscribe_time timestamp without time zone,
    sex smallint,
    country character varying(64),
    province character varying(64),
    city character varying(64),
    remark character varying(128),
    tag_ids jsonb,
    contact_id character varying(64),
    last_sync_at timestamp without time zone,
    create_time timestamp without time zone DEFAULT now(),
    update_time timestamp without time zone DEFAULT now(),
    subscribe_scene character varying(64)
);


ALTER TABLE public.crm_wechat_mp_fan OWNER TO luohd;

--
-- TOC entry 502 (class 1259 OID 404379)
-- Name: crm_wechat_mp_fan_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wechat_mp_fan_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wechat_mp_fan_id_seq OWNER TO luohd;

--
-- TOC entry 8581 (class 0 OID 0)
-- Dependencies: 502
-- Name: crm_wechat_mp_fan_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wechat_mp_fan_id_seq OWNED BY public.crm_wechat_mp_fan.id;


--
-- TOC entry 614 (class 1259 OID 413084)
-- Name: crm_wechat_mp_mass_task; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wechat_mp_mass_task (
    id bigint NOT NULL,
    app_id character varying(64) NOT NULL,
    msg_type character varying(16) NOT NULL,
    content_json jsonb NOT NULL,
    audience_type character varying(16) NOT NULL,
    tag_id bigint,
    is_to_all boolean DEFAULT false NOT NULL,
    client_msg_id character varying(64),
    status character varying(16) DEFAULT 'submitted'::character varying NOT NULL,
    msg_id character varying(64),
    msg_data_id character varying(64),
    total_count integer,
    filter_count integer,
    sent_count integer,
    error_count integer,
    error_code integer,
    error_msg character varying(512),
    create_by character varying(64),
    create_time timestamp without time zone DEFAULT now(),
    update_time timestamp without time zone DEFAULT now(),
    finish_time timestamp without time zone
);


ALTER TABLE public.crm_wechat_mp_mass_task OWNER TO luohd;

--
-- TOC entry 8583 (class 0 OID 0)
-- Dependencies: 614
-- Name: TABLE crm_wechat_mp_mass_task; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wechat_mp_mass_task IS '公众号官方群发任务（mass/sendall，与 message/batch 隔离）';


--
-- TOC entry 8584 (class 0 OID 0)
-- Dependencies: 614
-- Name: COLUMN crm_wechat_mp_mass_task.is_to_all; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_mass_task.is_to_all IS '是否全体群发（计入官方次数且可进历史消息）';


--
-- TOC entry 8585 (class 0 OID 0)
-- Dependencies: 614
-- Name: COLUMN crm_wechat_mp_mass_task.client_msg_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_mass_task.client_msg_id IS '开发者幂等键，最长 32 字节';


--
-- TOC entry 8586 (class 0 OID 0)
-- Dependencies: 614
-- Name: COLUMN crm_wechat_mp_mass_task.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_mass_task.status IS 'submitted|sending|finished|failed|deleted';


--
-- TOC entry 613 (class 1259 OID 413083)
-- Name: crm_wechat_mp_mass_task_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wechat_mp_mass_task_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wechat_mp_mass_task_id_seq OWNER TO luohd;

--
-- TOC entry 8588 (class 0 OID 0)
-- Dependencies: 613
-- Name: crm_wechat_mp_mass_task_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wechat_mp_mass_task_id_seq OWNED BY public.crm_wechat_mp_mass_task.id;


--
-- TOC entry 555 (class 1259 OID 408835)
-- Name: crm_wechat_mp_material; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wechat_mp_material (
    id bigint NOT NULL,
    app_id character varying(64) NOT NULL,
    media_id character varying(128) NOT NULL,
    type character varying(16) NOT NULL,
    name character varying(512),
    url character varying(1024),
    title character varying(512),
    digest character varying(1024),
    thumb_url character varying(1024),
    description text,
    down_url character varying(1024),
    content_type character varying(128),
    news_items_json text,
    update_time timestamp with time zone,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    is_default boolean DEFAULT false NOT NULL
);


ALTER TABLE public.crm_wechat_mp_material OWNER TO luohd;

--
-- TOC entry 8590 (class 0 OID 0)
-- Dependencies: 555
-- Name: TABLE crm_wechat_mp_material; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wechat_mp_material IS '微信公众号永久素材本地缓存';


--
-- TOC entry 8591 (class 0 OID 0)
-- Dependencies: 555
-- Name: COLUMN crm_wechat_mp_material.media_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_material.media_id IS '微信永久素材 media_id';


--
-- TOC entry 8592 (class 0 OID 0)
-- Dependencies: 555
-- Name: COLUMN crm_wechat_mp_material.type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_material.type IS '素材类型: image / video / voice / news / thumb';


--
-- TOC entry 8593 (class 0 OID 0)
-- Dependencies: 555
-- Name: COLUMN crm_wechat_mp_material.down_url; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_material.down_url IS '视频下载地址，来自 get_material';


--
-- TOC entry 8594 (class 0 OID 0)
-- Dependencies: 555
-- Name: COLUMN crm_wechat_mp_material.news_items_json; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_material.news_items_json IS '图文篇章 JSON 数组（type=news）';


--
-- TOC entry 8595 (class 0 OID 0)
-- Dependencies: 555
-- Name: COLUMN crm_wechat_mp_material.is_default; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_material.is_default IS '是否为该公众号默认素材（如草稿缺省封面）';


--
-- TOC entry 554 (class 1259 OID 408834)
-- Name: crm_wechat_mp_material_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wechat_mp_material_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wechat_mp_material_id_seq OWNER TO luohd;

--
-- TOC entry 8597 (class 0 OID 0)
-- Dependencies: 554
-- Name: crm_wechat_mp_material_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wechat_mp_material_id_seq OWNED BY public.crm_wechat_mp_material.id;


--
-- TOC entry 497 (class 1259 OID 404340)
-- Name: crm_wechat_mp_menu; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wechat_mp_menu (
    id bigint NOT NULL,
    app_id character varying(64) NOT NULL,
    template_id character varying(64),
    menu_json jsonb NOT NULL,
    status character varying(16) DEFAULT 'draft'::character varying NOT NULL,
    version integer DEFAULT 1 NOT NULL,
    published_at timestamp without time zone,
    published_by character varying(64),
    create_by character varying(64),
    create_time timestamp without time zone DEFAULT now(),
    update_time timestamp without time zone DEFAULT now()
);


ALTER TABLE public.crm_wechat_mp_menu OWNER TO luohd;

--
-- TOC entry 496 (class 1259 OID 404339)
-- Name: crm_wechat_mp_menu_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wechat_mp_menu_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wechat_mp_menu_id_seq OWNER TO luohd;

--
-- TOC entry 8600 (class 0 OID 0)
-- Dependencies: 496
-- Name: crm_wechat_mp_menu_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wechat_mp_menu_id_seq OWNED BY public.crm_wechat_mp_menu.id;


--
-- TOC entry 499 (class 1259 OID 404354)
-- Name: crm_wechat_mp_menu_key_label; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wechat_mp_menu_key_label (
    id bigint NOT NULL,
    app_id character varying(64) NOT NULL,
    menu_key character varying(128) NOT NULL,
    menu_name character varying(128) NOT NULL,
    create_time timestamp without time zone DEFAULT now()
);


ALTER TABLE public.crm_wechat_mp_menu_key_label OWNER TO luohd;

--
-- TOC entry 498 (class 1259 OID 404353)
-- Name: crm_wechat_mp_menu_key_label_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wechat_mp_menu_key_label_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wechat_mp_menu_key_label_id_seq OWNER TO luohd;

--
-- TOC entry 8603 (class 0 OID 0)
-- Dependencies: 498
-- Name: crm_wechat_mp_menu_key_label_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wechat_mp_menu_key_label_id_seq OWNED BY public.crm_wechat_mp_menu_key_label.id;


--
-- TOC entry 505 (class 1259 OID 404396)
-- Name: crm_wechat_mp_message; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wechat_mp_message (
    id bigint NOT NULL,
    app_id character varying(64) NOT NULL,
    open_id character varying(128) NOT NULL,
    direction character varying(8) NOT NULL,
    msg_type character varying(32) NOT NULL,
    content text,
    media_id character varying(128),
    template_id character varying(64),
    wx_msg_id character varying(64),
    activity_pulse_id character varying(64),
    errcode integer,
    errmsg text,
    create_time timestamp without time zone DEFAULT now(),
    pic_url character varying(512),
    source character varying(32)
);


ALTER TABLE public.crm_wechat_mp_message OWNER TO luohd;

--
-- TOC entry 8605 (class 0 OID 0)
-- Dependencies: 505
-- Name: COLUMN crm_wechat_mp_message.pic_url; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_message.pic_url IS '微信回调 PicUrl，临时链接约 3 天';


--
-- TOC entry 8606 (class 0 OID 0)
-- Dependencies: 505
-- Name: COLUMN crm_wechat_mp_message.source; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_message.source IS 'outbound origin: null=human, ai_inbox_agent=Inbox AI';


--
-- TOC entry 504 (class 1259 OID 404395)
-- Name: crm_wechat_mp_message_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wechat_mp_message_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wechat_mp_message_id_seq OWNER TO luohd;

--
-- TOC entry 8608 (class 0 OID 0)
-- Dependencies: 504
-- Name: crm_wechat_mp_message_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wechat_mp_message_id_seq OWNED BY public.crm_wechat_mp_message.id;


--
-- TOC entry 513 (class 1259 OID 404464)
-- Name: crm_wechat_mp_message_preset; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wechat_mp_message_preset (
    id bigint NOT NULL,
    preset_name character varying(128) NOT NULL,
    description text,
    app_id character varying(64) NOT NULL,
    wechat_template_id character varying(64) NOT NULL,
    url_template text,
    data_json jsonb NOT NULL,
    tags character varying(256),
    status character(1) DEFAULT '0'::bpchar NOT NULL,
    create_by character varying(64),
    create_time timestamp without time zone DEFAULT now(),
    update_by character varying(64),
    update_time timestamp without time zone DEFAULT now()
);


ALTER TABLE public.crm_wechat_mp_message_preset OWNER TO luohd;

--
-- TOC entry 8610 (class 0 OID 0)
-- Dependencies: 513
-- Name: TABLE crm_wechat_mp_message_preset; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wechat_mp_message_preset IS '公众号模板消息预设（文案+跳转，不含腾讯模板定义）';


--
-- TOC entry 8611 (class 0 OID 0)
-- Dependencies: 513
-- Name: COLUMN crm_wechat_mp_message_preset.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_message_preset.status IS '0=启用 1=停用';


--
-- TOC entry 512 (class 1259 OID 404463)
-- Name: crm_wechat_mp_message_preset_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wechat_mp_message_preset_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wechat_mp_message_preset_id_seq OWNER TO luohd;

--
-- TOC entry 8613 (class 0 OID 0)
-- Dependencies: 512
-- Name: crm_wechat_mp_message_preset_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wechat_mp_message_preset_id_seq OWNED BY public.crm_wechat_mp_message_preset.id;


--
-- TOC entry 507 (class 1259 OID 404408)
-- Name: crm_wechat_mp_message_task; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wechat_mp_message_task (
    id bigint NOT NULL,
    app_id character varying(64) NOT NULL,
    task_type character varying(32) NOT NULL,
    audience_type character varying(32) NOT NULL,
    audience_ref character varying(128),
    template_id character varying(64),
    content_json jsonb NOT NULL,
    status character varying(16) DEFAULT 'pending'::character varying NOT NULL,
    total_count integer DEFAULT 0,
    success_count integer DEFAULT 0,
    fail_count integer DEFAULT 0,
    skip_count integer DEFAULT 0,
    scheduled_at timestamp without time zone,
    started_at timestamp without time zone,
    finished_at timestamp without time zone,
    create_by character varying(64),
    create_time timestamp without time zone DEFAULT now(),
    update_time timestamp without time zone DEFAULT now(),
    filter_json jsonb
);


ALTER TABLE public.crm_wechat_mp_message_task OWNER TO luohd;

--
-- TOC entry 506 (class 1259 OID 404407)
-- Name: crm_wechat_mp_message_task_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wechat_mp_message_task_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wechat_mp_message_task_id_seq OWNER TO luohd;

--
-- TOC entry 8616 (class 0 OID 0)
-- Dependencies: 506
-- Name: crm_wechat_mp_message_task_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wechat_mp_message_task_id_seq OWNED BY public.crm_wechat_mp_message_task.id;


--
-- TOC entry 509 (class 1259 OID 404424)
-- Name: crm_wechat_mp_message_task_item; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wechat_mp_message_task_item (
    id bigint NOT NULL,
    task_id bigint NOT NULL,
    open_id character varying(128) NOT NULL,
    status character varying(16) DEFAULT 'pending'::character varying NOT NULL,
    errcode integer,
    errmsg text,
    sent_at timestamp without time zone
);


ALTER TABLE public.crm_wechat_mp_message_task_item OWNER TO luohd;

--
-- TOC entry 508 (class 1259 OID 404423)
-- Name: crm_wechat_mp_message_task_item_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wechat_mp_message_task_item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wechat_mp_message_task_item_id_seq OWNER TO luohd;

--
-- TOC entry 8619 (class 0 OID 0)
-- Dependencies: 508
-- Name: crm_wechat_mp_message_task_item_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wechat_mp_message_task_item_id_seq OWNED BY public.crm_wechat_mp_message_task_item.id;


--
-- TOC entry 557 (class 1259 OID 408899)
-- Name: crm_wechat_mp_publish_task; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wechat_mp_publish_task (
    id bigint NOT NULL,
    app_id character varying(64) NOT NULL,
    media_id character varying(128) NOT NULL,
    publish_id character varying(32),
    msg_data_id character varying(32),
    article_id character varying(128),
    index_in_msg integer DEFAULT 0 NOT NULL,
    publish_status smallint DEFAULT 1,
    fail_idxs character varying(64),
    article_url character varying(1024),
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.crm_wechat_mp_publish_task OWNER TO luohd;

--
-- TOC entry 8621 (class 0 OID 0)
-- Dependencies: 557
-- Name: TABLE crm_wechat_mp_publish_task; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wechat_mp_publish_task IS '微信公众号发布任务';


--
-- TOC entry 8622 (class 0 OID 0)
-- Dependencies: 557
-- Name: COLUMN crm_wechat_mp_publish_task.media_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_publish_task.media_id IS '关联的草稿 media_id';


--
-- TOC entry 8623 (class 0 OID 0)
-- Dependencies: 557
-- Name: COLUMN crm_wechat_mp_publish_task.publish_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_publish_task.publish_id IS '微信发布任务ID，用于 PUBLISHJOBFINISH 回调匹配';


--
-- TOC entry 8624 (class 0 OID 0)
-- Dependencies: 557
-- Name: COLUMN crm_wechat_mp_publish_task.msg_data_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_publish_task.msg_data_id IS '消息数据ID（freepublish/submit 返回），统计 API 中 msgid 的前缀部分';


--
-- TOC entry 8625 (class 0 OID 0)
-- Dependencies: 557
-- Name: COLUMN crm_wechat_mp_publish_task.article_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_publish_task.article_id IS '发布成功后微信分配的 article_id';


--
-- TOC entry 8626 (class 0 OID 0)
-- Dependencies: 557
-- Name: COLUMN crm_wechat_mp_publish_task.index_in_msg; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_publish_task.index_in_msg IS '多图文中的文章序号（0-based）';


--
-- TOC entry 8627 (class 0 OID 0)
-- Dependencies: 557
-- Name: COLUMN crm_wechat_mp_publish_task.publish_status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_publish_task.publish_status IS '发布状态: 0=成功, 1=发布中, 2=原创失败, 3=常规失败, 4=审核不通过, 5=用户删除, 6=系统封禁';


--
-- TOC entry 8628 (class 0 OID 0)
-- Dependencies: 557
-- Name: COLUMN crm_wechat_mp_publish_task.fail_idxs; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_publish_task.fail_idxs IS '发布失败的文章序号，逗号分隔（如 "1,3"）';


--
-- TOC entry 8629 (class 0 OID 0)
-- Dependencies: 557
-- Name: COLUMN crm_wechat_mp_publish_task.article_url; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wechat_mp_publish_task.article_url IS '发布成功后的文章永久链接';


--
-- TOC entry 556 (class 1259 OID 408898)
-- Name: crm_wechat_mp_publish_task_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wechat_mp_publish_task_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wechat_mp_publish_task_id_seq OWNER TO luohd;

--
-- TOC entry 8631 (class 0 OID 0)
-- Dependencies: 556
-- Name: crm_wechat_mp_publish_task_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wechat_mp_publish_task_id_seq OWNED BY public.crm_wechat_mp_publish_task.id;


--
-- TOC entry 501 (class 1259 OID 404364)
-- Name: crm_wechat_mp_qrcode; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wechat_mp_qrcode (
    id bigint NOT NULL,
    app_id character varying(64) NOT NULL,
    name character varying(128) NOT NULL,
    scene_str character varying(64) NOT NULL,
    qr_type character varying(16) DEFAULT 'permanent'::character varying NOT NULL,
    expire_seconds integer,
    ticket text,
    qr_url text,
    crm_event_id character varying(64),
    campaign_id character varying(36),
    stats_json jsonb DEFAULT '{}'::jsonb,
    create_by character varying(64),
    create_time timestamp without time zone DEFAULT now(),
    update_time timestamp without time zone DEFAULT now()
);


ALTER TABLE public.crm_wechat_mp_qrcode OWNER TO luohd;

--
-- TOC entry 500 (class 1259 OID 404363)
-- Name: crm_wechat_mp_qrcode_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wechat_mp_qrcode_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wechat_mp_qrcode_id_seq OWNER TO luohd;

--
-- TOC entry 8634 (class 0 OID 0)
-- Dependencies: 500
-- Name: crm_wechat_mp_qrcode_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wechat_mp_qrcode_id_seq OWNED BY public.crm_wechat_mp_qrcode.id;


--
-- TOC entry 482 (class 1259 OID 380121)
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


ALTER TABLE public.crm_wecom_aibot_config OWNER TO luohd;

--
-- TOC entry 8636 (class 0 OID 0)
-- Dependencies: 482
-- Name: TABLE crm_wecom_aibot_config; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wecom_aibot_config IS '企业微信智能机器人租户配置';


--
-- TOC entry 8637 (class 0 OID 0)
-- Dependencies: 482
-- Name: COLUMN crm_wecom_aibot_config.tenant; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_aibot_config.tenant IS '租户标识';


--
-- TOC entry 8638 (class 0 OID 0)
-- Dependencies: 482
-- Name: COLUMN crm_wecom_aibot_config.bot_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_aibot_config.bot_id IS '智能机器人 ID';


--
-- TOC entry 8639 (class 0 OID 0)
-- Dependencies: 482
-- Name: COLUMN crm_wecom_aibot_config.bot_secret; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_aibot_config.bot_secret IS '智能机器人 Secret（AES-256 加密存储）';


--
-- TOC entry 8640 (class 0 OID 0)
-- Dependencies: 482
-- Name: COLUMN crm_wecom_aibot_config.enabled; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_aibot_config.enabled IS '是否启用 0-否 1-是';


--
-- TOC entry 481 (class 1259 OID 380120)
-- Name: crm_wecom_aibot_config_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wecom_aibot_config_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wecom_aibot_config_id_seq OWNER TO luohd;

--
-- TOC entry 8642 (class 0 OID 0)
-- Dependencies: 481
-- Name: crm_wecom_aibot_config_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wecom_aibot_config_id_seq OWNED BY public.crm_wecom_aibot_config.id;


--
-- TOC entry 484 (class 1259 OID 380135)
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


ALTER TABLE public.crm_wecom_aibot_connection OWNER TO luohd;

--
-- TOC entry 8644 (class 0 OID 0)
-- Dependencies: 484
-- Name: TABLE crm_wecom_aibot_connection; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wecom_aibot_connection IS '企业微信智能机器人连接状态';


--
-- TOC entry 8645 (class 0 OID 0)
-- Dependencies: 484
-- Name: COLUMN crm_wecom_aibot_connection.tenant; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_aibot_connection.tenant IS '租户标识';


--
-- TOC entry 8646 (class 0 OID 0)
-- Dependencies: 484
-- Name: COLUMN crm_wecom_aibot_connection.bot_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_aibot_connection.bot_id IS '智能机器人 ID';


--
-- TOC entry 8647 (class 0 OID 0)
-- Dependencies: 484
-- Name: COLUMN crm_wecom_aibot_connection.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_aibot_connection.status IS '连接状态：disconnected/connecting/connected/disconnecting/reconnecting/error';


--
-- TOC entry 8648 (class 0 OID 0)
-- Dependencies: 484
-- Name: COLUMN crm_wecom_aibot_connection.last_message_at; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_aibot_connection.last_message_at IS '最后一次收到消息的时间';


--
-- TOC entry 8649 (class 0 OID 0)
-- Dependencies: 484
-- Name: COLUMN crm_wecom_aibot_connection.error_message; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_aibot_connection.error_message IS '最近一次错误信息';


--
-- TOC entry 8650 (class 0 OID 0)
-- Dependencies: 484
-- Name: COLUMN crm_wecom_aibot_connection.error_count; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_aibot_connection.error_count IS '累计错误次数';


--
-- TOC entry 8651 (class 0 OID 0)
-- Dependencies: 484
-- Name: COLUMN crm_wecom_aibot_connection.last_ping_at; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_aibot_connection.last_ping_at IS '最后一次心跳时间';


--
-- TOC entry 8652 (class 0 OID 0)
-- Dependencies: 484
-- Name: COLUMN crm_wecom_aibot_connection.updated_at; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_aibot_connection.updated_at IS '最后更新时间';


--
-- TOC entry 483 (class 1259 OID 380134)
-- Name: crm_wecom_aibot_connection_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wecom_aibot_connection_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wecom_aibot_connection_id_seq OWNER TO luohd;

--
-- TOC entry 8654 (class 0 OID 0)
-- Dependencies: 483
-- Name: crm_wecom_aibot_connection_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wecom_aibot_connection_id_seq OWNED BY public.crm_wecom_aibot_connection.id;


--
-- TOC entry 539 (class 1259 OID 408174)
-- Name: crm_wecom_behavior_daily; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wecom_behavior_daily (
    id bigint NOT NULL,
    corp_id character varying(64),
    userid character varying(64) NOT NULL,
    user_name character varying(128),
    stat_time bigint NOT NULL,
    new_apply_cnt integer DEFAULT 0 NOT NULL,
    new_contact_cnt integer DEFAULT 0 NOT NULL,
    chat_cnt integer DEFAULT 0 NOT NULL,
    message_cnt integer DEFAULT 0 NOT NULL,
    reply_percentage numeric(6,2),
    avg_reply_time integer,
    negative_feedback_cnt integer DEFAULT 0 NOT NULL,
    raw_json text,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.crm_wecom_behavior_daily OWNER TO luohd;

--
-- TOC entry 8656 (class 0 OID 0)
-- Dependencies: 539
-- Name: TABLE crm_wecom_behavior_daily; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wecom_behavior_daily IS '企业微信成员联系客户行为数据（按成员按天快照）';


--
-- TOC entry 8657 (class 0 OID 0)
-- Dependencies: 539
-- Name: COLUMN crm_wecom_behavior_daily.userid; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_behavior_daily.userid IS '企微成员 userid（对应 CRM 用户 sn_userid）';


--
-- TOC entry 8658 (class 0 OID 0)
-- Dependencies: 539
-- Name: COLUMN crm_wecom_behavior_daily.user_name; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_behavior_daily.user_name IS '快照时刻的成员显示名，保证删除后仍可读';


--
-- TOC entry 8659 (class 0 OID 0)
-- Dependencies: 539
-- Name: COLUMN crm_wecom_behavior_daily.stat_time; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_behavior_daily.stat_time IS '统计自然日 0 点 Unix 时间戳（秒）';


--
-- TOC entry 538 (class 1259 OID 408173)
-- Name: crm_wecom_behavior_daily_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wecom_behavior_daily_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wecom_behavior_daily_id_seq OWNER TO luohd;

--
-- TOC entry 8661 (class 0 OID 0)
-- Dependencies: 538
-- Name: crm_wecom_behavior_daily_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wecom_behavior_daily_id_seq OWNED BY public.crm_wecom_behavior_daily.id;


--
-- TOC entry 478 (class 1259 OID 380073)
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


ALTER TABLE public.crm_wecom_callback_event OWNER TO luohd;

--
-- TOC entry 8663 (class 0 OID 0)
-- Dependencies: 478
-- Name: TABLE crm_wecom_callback_event; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wecom_callback_event IS '企业微信回调事件记录';


--
-- TOC entry 8664 (class 0 OID 0)
-- Dependencies: 478
-- Name: COLUMN crm_wecom_callback_event.corp_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_callback_event.corp_id IS '企业ID';


--
-- TOC entry 8665 (class 0 OID 0)
-- Dependencies: 478
-- Name: COLUMN crm_wecom_callback_event.event; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_callback_event.event IS '事件类型：change_external_contact / change_external_chat / change_external_tag / kf_msg_or_event';


--
-- TOC entry 8666 (class 0 OID 0)
-- Dependencies: 478
-- Name: COLUMN crm_wecom_callback_event.change_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_callback_event.change_type IS '变更类型（change_external_contact: add_external_contact/edit_external_contact/add_half_external_contact/del_external_contact/del_follow_user/transfer_fail; change_external_chat: create/update/dismiss; change_external_tag: create/update/delete/shuffle; kf_msg_or_event 无此字段）';


--
-- TOC entry 8667 (class 0 OID 0)
-- Dependencies: 478
-- Name: COLUMN crm_wecom_callback_event.user_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_callback_event.user_id IS '企业服务人员的UserID';


--
-- TOC entry 8668 (class 0 OID 0)
-- Dependencies: 478
-- Name: COLUMN crm_wecom_callback_event.external_userid; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_callback_event.external_userid IS '外部联系人的userid';


--
-- TOC entry 8669 (class 0 OID 0)
-- Dependencies: 478
-- Name: COLUMN crm_wecom_callback_event.chat_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_callback_event.chat_id IS '客户群ID';


--
-- TOC entry 8670 (class 0 OID 0)
-- Dependencies: 478
-- Name: COLUMN crm_wecom_callback_event.tag_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_callback_event.tag_id IS '标签或标签组的ID';


--
-- TOC entry 8671 (class 0 OID 0)
-- Dependencies: 478
-- Name: COLUMN crm_wecom_callback_event.tag_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_callback_event.tag_type IS '标签类型：tag / tag_group';


--
-- TOC entry 8672 (class 0 OID 0)
-- Dependencies: 478
-- Name: COLUMN crm_wecom_callback_event.update_detail; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_callback_event.update_detail IS '客户群变更详情：add_member / del_member / change_owner / change_name / change_notice';


--
-- TOC entry 8673 (class 0 OID 0)
-- Dependencies: 478
-- Name: COLUMN crm_wecom_callback_event.state; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_callback_event.state IS '添加此用户的联系我方式配置的state参数';


--
-- TOC entry 8674 (class 0 OID 0)
-- Dependencies: 478
-- Name: COLUMN crm_wecom_callback_event.source; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_callback_event.source IS '删除客户的操作来源，如 DELETE_BY_TRANSFER';


--
-- TOC entry 8675 (class 0 OID 0)
-- Dependencies: 478
-- Name: COLUMN crm_wecom_callback_event.fail_reason; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_callback_event.fail_reason IS '接替失败原因：customer_refused / customer_limit_exceed';


--
-- TOC entry 8676 (class 0 OID 0)
-- Dependencies: 478
-- Name: COLUMN crm_wecom_callback_event.raw_xml; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_callback_event.raw_xml IS '解密后的完整明文 XML，保留全部原始信息';


--
-- TOC entry 8677 (class 0 OID 0)
-- Dependencies: 478
-- Name: COLUMN crm_wecom_callback_event.create_time; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_callback_event.create_time IS '创建时间';


--
-- TOC entry 477 (class 1259 OID 380072)
-- Name: crm_wecom_callback_event_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wecom_callback_event_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wecom_callback_event_id_seq OWNER TO luohd;

--
-- TOC entry 8679 (class 0 OID 0)
-- Dependencies: 477
-- Name: crm_wecom_callback_event_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wecom_callback_event_id_seq OWNED BY public.crm_wecom_callback_event.id;


--
-- TOC entry 275 (class 1259 OID 360916)
-- Name: crm_wecom_config; Type: TABLE; Schema: public; Owner: _tenant
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
    welcome_text character varying(2048) DEFAULT NULL::character varying,
    default_lead_user_id bigint,
    default_lead_dept_id bigint
);


ALTER TABLE public.crm_wecom_config OWNER TO "_tenant";

--
-- TOC entry 8681 (class 0 OID 0)
-- Dependencies: 275
-- Name: TABLE crm_wecom_config; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON TABLE public.crm_wecom_config IS '企业微信租户配置（每租户库单行）';


--
-- TOC entry 8682 (class 0 OID 0)
-- Dependencies: 275
-- Name: COLUMN crm_wecom_config.welcome_enabled; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_wecom_config.welcome_enabled IS 'API 自动欢迎语开关';


--
-- TOC entry 8683 (class 0 OID 0)
-- Dependencies: 275
-- Name: COLUMN crm_wecom_config.welcome_text; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_wecom_config.welcome_text IS '租户默认欢迎语文案';


--
-- TOC entry 8684 (class 0 OID 0)
-- Dependencies: 275
-- Name: COLUMN crm_wecom_config.default_lead_user_id; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_wecom_config.default_lead_user_id IS '企微回调建线索默认负责人 sys_user.user_id（sn_userid 未命中时兜底）';


--
-- TOC entry 8685 (class 0 OID 0)
-- Dependencies: 275
-- Name: COLUMN crm_wecom_config.default_lead_dept_id; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_wecom_config.default_lead_dept_id IS '默认线索部门 sys_user.dept_id 覆盖（可选；为空则用负责人部门）';


--
-- TOC entry 438 (class 1259 OID 371436)
-- Name: crm_wecom_config_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
--

CREATE SEQUENCE public.crm_wecom_config_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wecom_config_id_seq OWNER TO "_tenant";

--
-- TOC entry 8687 (class 0 OID 0)
-- Dependencies: 438
-- Name: crm_wecom_config_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: _tenant
--

ALTER SEQUENCE public.crm_wecom_config_id_seq OWNED BY public.crm_wecom_config.id;


--
-- TOC entry 644 (class 1259 OID 413435)
-- Name: crm_wecom_contact_group; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wecom_contact_group (
    id character varying(36) NOT NULL,
    tenant_id character varying(64) NOT NULL,
    contact_id character varying(100) NOT NULL,
    chat_id character varying(64),
    owner_userid character varying(64) NOT NULL,
    group_name character varying(128),
    status character varying(32) DEFAULT 'pending'::character varying NOT NULL,
    provision_task_id character varying(36),
    bot_activated_at timestamp with time zone,
    last_error character varying(500),
    create_time timestamp without time zone,
    update_time timestamp without time zone
);


ALTER TABLE public.crm_wecom_contact_group OWNER TO luohd;

--
-- TOC entry 8689 (class 0 OID 0)
-- Dependencies: 644
-- Name: TABLE crm_wecom_contact_group; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wecom_contact_group IS '企微一客一群绑定与状态机';


--
-- TOC entry 276 (class 1259 OID 360924)
-- Name: crm_wecom_contact_way; Type: TABLE; Schema: public; Owner: _tenant
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
    welcome_text character varying(2048) DEFAULT NULL::character varying,
    crm_event_id character varying(64)
);


ALTER TABLE public.crm_wecom_contact_way OWNER TO "_tenant";

--
-- TOC entry 8691 (class 0 OID 0)
-- Dependencies: 276
-- Name: TABLE crm_wecom_contact_way; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON TABLE public.crm_wecom_contact_way IS '企业微信联系我/渠道活码';


--
-- TOC entry 8692 (class 0 OID 0)
-- Dependencies: 276
-- Name: COLUMN crm_wecom_contact_way.welcome_text; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_wecom_contact_way.welcome_text IS '活码级欢迎语覆盖';


--
-- TOC entry 8693 (class 0 OID 0)
-- Dependencies: 276
-- Name: COLUMN crm_wecom_contact_way.crm_event_id; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.crm_wecom_contact_way.crm_event_id IS '关联市场活动，创建时解析 state 写入';


--
-- TOC entry 439 (class 1259 OID 371438)
-- Name: crm_wecom_contact_way_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
--

CREATE SEQUENCE public.crm_wecom_contact_way_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wecom_contact_way_id_seq OWNER TO "_tenant";

--
-- TOC entry 8695 (class 0 OID 0)
-- Dependencies: 439
-- Name: crm_wecom_contact_way_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: _tenant
--

ALTER SEQUENCE public.crm_wecom_contact_way_id_seq OWNED BY public.crm_wecom_contact_way.id;


--
-- TOC entry 277 (class 1259 OID 360932)
-- Name: crm_wecom_external_contact; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_wecom_external_contact OWNER TO "_tenant";

--
-- TOC entry 8697 (class 0 OID 0)
-- Dependencies: 277
-- Name: TABLE crm_wecom_external_contact; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON TABLE public.crm_wecom_external_contact IS '企业微信外部联系人与 CRM 映射';


--
-- TOC entry 440 (class 1259 OID 371440)
-- Name: crm_wecom_external_contact_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
--

CREATE SEQUENCE public.crm_wecom_external_contact_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wecom_external_contact_id_seq OWNER TO "_tenant";

--
-- TOC entry 8699 (class 0 OID 0)
-- Dependencies: 440
-- Name: crm_wecom_external_contact_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: _tenant
--

ALTER SEQUENCE public.crm_wecom_external_contact_id_seq OWNED BY public.crm_wecom_external_contact.id;


--
-- TOC entry 645 (class 1259 OID 413447)
-- Name: crm_wecom_group_provision_task; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wecom_group_provision_task (
    task_id character varying(36) NOT NULL,
    tenant_id character varying(64) NOT NULL,
    contact_id character varying(100) NOT NULL,
    assignee_userid character varying(64) NOT NULL,
    assignee_crm_user_id character varying(64),
    status character varying(16) DEFAULT 'pending'::character varying NOT NULL,
    notified_at timestamp with time zone,
    remind_count integer DEFAULT 0 NOT NULL,
    expires_at timestamp with time zone,
    create_time timestamp without time zone,
    update_time timestamp without time zone
);


ALTER TABLE public.crm_wecom_group_provision_task OWNER TO luohd;

--
-- TOC entry 8701 (class 0 OID 0)
-- Dependencies: 645
-- Name: TABLE crm_wecom_group_provision_task; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wecom_group_provision_task IS '企微服务群建群任务（员工 H5 一键建群）';


--
-- TOC entry 647 (class 1259 OID 413470)
-- Name: crm_wecom_group_reminder; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wecom_group_reminder (
    id character varying(36) NOT NULL,
    tenant_id character varying(64) NOT NULL,
    contact_id character varying(100) NOT NULL,
    rule_id character varying(36) NOT NULL,
    source_ref character varying(128) NOT NULL,
    chat_id character varying(64),
    due_at timestamp with time zone NOT NULL,
    status character varying(16) DEFAULT 'pending'::character varying NOT NULL,
    payload_json text,
    rendered_text text,
    errcode integer,
    errmsg character varying(500),
    retry_count integer DEFAULT 0 NOT NULL,
    sent_at timestamp with time zone,
    skipped_reason character varying(200),
    claim_lease_until timestamp with time zone,
    create_time timestamp without time zone,
    update_time timestamp without time zone
);


ALTER TABLE public.crm_wecom_group_reminder OWNER TO luohd;

--
-- TOC entry 8703 (class 0 OID 0)
-- Dependencies: 647
-- Name: TABLE crm_wecom_group_reminder; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wecom_group_reminder IS '企微客户群提醒物化待发队列';


--
-- TOC entry 646 (class 1259 OID 413455)
-- Name: crm_wecom_group_reminder_rule; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wecom_group_reminder_rule (
    rule_id character varying(36) NOT NULL,
    tenant_id character varying(64) NOT NULL,
    name character varying(128) NOT NULL,
    description character varying(500),
    enabled character(1) DEFAULT '1'::bpchar NOT NULL,
    is_system character(1) DEFAULT '0'::bpchar NOT NULL,
    priority integer DEFAULT 100 NOT NULL,
    anchor_type character varying(32) NOT NULL,
    offset_value integer DEFAULT 0 NOT NULL,
    offset_unit character varying(8) DEFAULT 'days'::character varying NOT NULL,
    send_time character varying(8),
    template_body text NOT NULL,
    match_json text,
    require_active_group character(1) DEFAULT '1'::bpchar NOT NULL,
    del_flag character(1) DEFAULT '0'::bpchar NOT NULL,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone
);


ALTER TABLE public.crm_wecom_group_reminder_rule OWNER TO luohd;

--
-- TOC entry 8705 (class 0 OID 0)
-- Dependencies: 646
-- Name: TABLE crm_wecom_group_reminder_rule; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wecom_group_reminder_rule IS '企微客户群提醒规则（固定话术）';


--
-- TOC entry 541 (class 1259 OID 408193)
-- Name: crm_wecom_groupchat_daily; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wecom_groupchat_daily (
    id bigint NOT NULL,
    corp_id character varying(64),
    owner_userid character varying(64) NOT NULL,
    owner_name character varying(128),
    stat_time bigint NOT NULL,
    new_chat_cnt integer DEFAULT 0 NOT NULL,
    chat_total integer DEFAULT 0 NOT NULL,
    chat_has_msg integer DEFAULT 0 NOT NULL,
    new_member_cnt integer DEFAULT 0 NOT NULL,
    member_total integer DEFAULT 0 NOT NULL,
    member_has_msg integer DEFAULT 0 NOT NULL,
    msg_total integer DEFAULT 0 NOT NULL,
    migrate_trainee_chat_cnt integer DEFAULT 0 NOT NULL,
    raw_json text,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.crm_wecom_groupchat_daily OWNER TO luohd;

--
-- TOC entry 8707 (class 0 OID 0)
-- Dependencies: 541
-- Name: TABLE crm_wecom_groupchat_daily; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wecom_groupchat_daily IS '企业微信群聊数据统计（按群主按天快照）';


--
-- TOC entry 8708 (class 0 OID 0)
-- Dependencies: 541
-- Name: COLUMN crm_wecom_groupchat_daily.owner_userid; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_groupchat_daily.owner_userid IS '群主 userid（对应 CRM 用户 sn_userid）';


--
-- TOC entry 8709 (class 0 OID 0)
-- Dependencies: 541
-- Name: COLUMN crm_wecom_groupchat_daily.owner_name; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_groupchat_daily.owner_name IS '快照时刻的群主显示名，保证删除后仍可读';


--
-- TOC entry 8710 (class 0 OID 0)
-- Dependencies: 541
-- Name: COLUMN crm_wecom_groupchat_daily.stat_time; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_groupchat_daily.stat_time IS '统计自然日 0 点 Unix 时间戳（秒）';


--
-- TOC entry 540 (class 1259 OID 408192)
-- Name: crm_wecom_groupchat_daily_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wecom_groupchat_daily_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wecom_groupchat_daily_id_seq OWNER TO luohd;

--
-- TOC entry 8712 (class 0 OID 0)
-- Dependencies: 540
-- Name: crm_wecom_groupchat_daily_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wecom_groupchat_daily_id_seq OWNED BY public.crm_wecom_groupchat_daily.id;


--
-- TOC entry 472 (class 1259 OID 379682)
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


ALTER TABLE public.crm_wecom_kf_config OWNER TO luohd;

--
-- TOC entry 8714 (class 0 OID 0)
-- Dependencies: 472
-- Name: COLUMN crm_wecom_kf_config.workflow_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_config.workflow_id IS '绑定的工作流 ID，关联 wworkflow.id';


--
-- TOC entry 471 (class 1259 OID 379681)
-- Name: crm_wecom_kf_config_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wecom_kf_config_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wecom_kf_config_id_seq OWNER TO luohd;

--
-- TOC entry 8716 (class 0 OID 0)
-- Dependencies: 471
-- Name: crm_wecom_kf_config_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wecom_kf_config_id_seq OWNED BY public.crm_wecom_kf_config.id;


--
-- TOC entry 654 (class 1259 OID 413542)
-- Name: crm_wecom_kf_conversation; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wecom_kf_conversation (
    id bigint NOT NULL,
    corp_id character varying(64) NOT NULL,
    open_kfid character varying(64) NOT NULL,
    external_userid character varying(64) NOT NULL,
    nickname character varying(128),
    avatar_url text,
    account_name character varying(128),
    last_message character varying(512),
    last_message_at timestamp without time zone,
    message_count integer DEFAULT 0 NOT NULL,
    unread_count integer DEFAULT 0 NOT NULL,
    can_reply smallint DEFAULT 1 NOT NULL,
    user_id bigint,
    owner character varying(128),
    assigned_at timestamp without time zone,
    contact_id character varying(64),
    contact_name character varying(128),
    create_time timestamp without time zone DEFAULT now(),
    update_time timestamp without time zone DEFAULT now(),
    hosted_by_ai boolean
);


ALTER TABLE public.crm_wecom_kf_conversation OWNER TO luohd;

--
-- TOC entry 8718 (class 0 OID 0)
-- Dependencies: 654
-- Name: TABLE crm_wecom_kf_conversation; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wecom_kf_conversation IS '微信客服坐席会话（公共池 / 我的 / 团队）';


--
-- TOC entry 8719 (class 0 OID 0)
-- Dependencies: 654
-- Name: COLUMN crm_wecom_kf_conversation.account_name; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_conversation.account_name IS '客服账号名（列表 Badge）';


--
-- TOC entry 8720 (class 0 OID 0)
-- Dependencies: 654
-- Name: COLUMN crm_wecom_kf_conversation.message_count; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_conversation.message_count IS '入站消息合计';


--
-- TOC entry 8721 (class 0 OID 0)
-- Dependencies: 654
-- Name: COLUMN crm_wecom_kf_conversation.unread_count; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_conversation.unread_count IS '未读入站条数';


--
-- TOC entry 8722 (class 0 OID 0)
-- Dependencies: 654
-- Name: COLUMN crm_wecom_kf_conversation.can_reply; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_conversation.can_reply IS '是否可回复（1=可，0=否）';


--
-- TOC entry 8723 (class 0 OID 0)
-- Dependencies: 654
-- Name: COLUMN crm_wecom_kf_conversation.user_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_conversation.user_id IS '负责人系统用户 id；NULL = 公共池';


--
-- TOC entry 8724 (class 0 OID 0)
-- Dependencies: 654
-- Name: COLUMN crm_wecom_kf_conversation.owner; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_conversation.owner IS '负责人展示名';


--
-- TOC entry 8725 (class 0 OID 0)
-- Dependencies: 654
-- Name: COLUMN crm_wecom_kf_conversation.assigned_at; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_conversation.assigned_at IS '最近一次领取 / 指派时间';


--
-- TOC entry 8726 (class 0 OID 0)
-- Dependencies: 654
-- Name: COLUMN crm_wecom_kf_conversation.hosted_by_ai; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_conversation.hosted_by_ai IS 'null/true=可跟渠道托管；false=人工已接管';


--
-- TOC entry 653 (class 1259 OID 413541)
-- Name: crm_wecom_kf_conversation_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wecom_kf_conversation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wecom_kf_conversation_id_seq OWNER TO luohd;

--
-- TOC entry 8728 (class 0 OID 0)
-- Dependencies: 653
-- Name: crm_wecom_kf_conversation_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wecom_kf_conversation_id_seq OWNED BY public.crm_wecom_kf_conversation.id;


--
-- TOC entry 474 (class 1259 OID 379738)
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
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    conversation_id bigint,
    media_id character varying(128),
    pic_url text,
    source character varying(64)
);


ALTER TABLE public.crm_wecom_kf_message OWNER TO luohd;

--
-- TOC entry 8730 (class 0 OID 0)
-- Dependencies: 474
-- Name: TABLE crm_wecom_kf_message; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wecom_kf_message IS '微信客服会话消息记录';


--
-- TOC entry 8731 (class 0 OID 0)
-- Dependencies: 474
-- Name: COLUMN crm_wecom_kf_message.corp_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_message.corp_id IS '企业ID';


--
-- TOC entry 8732 (class 0 OID 0)
-- Dependencies: 474
-- Name: COLUMN crm_wecom_kf_message.open_kfid; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_message.open_kfid IS '客服账号ID';


--
-- TOC entry 8733 (class 0 OID 0)
-- Dependencies: 474
-- Name: COLUMN crm_wecom_kf_message.external_userid; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_message.external_userid IS '微信客户ID（关联 crm_wecom_external_contact）';


--
-- TOC entry 8734 (class 0 OID 0)
-- Dependencies: 474
-- Name: COLUMN crm_wecom_kf_message.msgid; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_message.msgid IS '消息唯一ID（全局唯一，用于幂等去重）';


--
-- TOC entry 8735 (class 0 OID 0)
-- Dependencies: 474
-- Name: COLUMN crm_wecom_kf_message.msgtype; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_message.msgtype IS '消息类型：text / image / voice / video / file 等';


--
-- TOC entry 8736 (class 0 OID 0)
-- Dependencies: 474
-- Name: COLUMN crm_wecom_kf_message.content; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_message.content IS '消息正文（文本消息时存储内容，其他类型可存储 media_id）';


--
-- TOC entry 8737 (class 0 OID 0)
-- Dependencies: 474
-- Name: COLUMN crm_wecom_kf_message.direction; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_message.direction IS '消息方向：1=客户→系统(incoming)，2=系统→客户(outgoing)';


--
-- TOC entry 8738 (class 0 OID 0)
-- Dependencies: 474
-- Name: COLUMN crm_wecom_kf_message.send_time; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_message.send_time IS '消息发送时间（Unix 时间戳，秒）';


--
-- TOC entry 8739 (class 0 OID 0)
-- Dependencies: 474
-- Name: COLUMN crm_wecom_kf_message.raw_json; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_message.raw_json IS '原始消息 JSON（完整 payload，便于扩展和排错）';


--
-- TOC entry 8740 (class 0 OID 0)
-- Dependencies: 474
-- Name: COLUMN crm_wecom_kf_message.media_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_message.media_id IS '临时素材 media_id（图片/语音/视频/文件）';


--
-- TOC entry 8741 (class 0 OID 0)
-- Dependencies: 474
-- Name: COLUMN crm_wecom_kf_message.pic_url; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_message.pic_url IS '可选临时图片 URL';


--
-- TOC entry 8742 (class 0 OID 0)
-- Dependencies: 474
-- Name: COLUMN crm_wecom_kf_message.source; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_message.source IS 'null=人工；ai_inbox_agent=Inbox Agent';


--
-- TOC entry 473 (class 1259 OID 379737)
-- Name: crm_wecom_kf_message_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wecom_kf_message_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wecom_kf_message_id_seq OWNER TO luohd;

--
-- TOC entry 8744 (class 0 OID 0)
-- Dependencies: 473
-- Name: crm_wecom_kf_message_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wecom_kf_message_id_seq OWNED BY public.crm_wecom_kf_message.id;


--
-- TOC entry 656 (class 1259 OID 413563)
-- Name: crm_wecom_kf_sync_cursor; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wecom_kf_sync_cursor (
    id bigint NOT NULL,
    corp_id character varying(64) NOT NULL,
    open_kfid character varying(64) DEFAULT ''::character varying NOT NULL,
    cursor text,
    enabled_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now()
);


ALTER TABLE public.crm_wecom_kf_sync_cursor OWNER TO luohd;

--
-- TOC entry 8746 (class 0 OID 0)
-- Dependencies: 656
-- Name: TABLE crm_wecom_kf_sync_cursor; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wecom_kf_sync_cursor IS '微信客服 sync_msg 游标；enabled_at 为开通回调日起点';


--
-- TOC entry 8747 (class 0 OID 0)
-- Dependencies: 656
-- Name: COLUMN crm_wecom_kf_sync_cursor.cursor; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_sync_cursor.cursor IS '上次 kf/sync_msg 返回的 next_cursor';


--
-- TOC entry 8748 (class 0 OID 0)
-- Dependencies: 656
-- Name: COLUMN crm_wecom_kf_sync_cursor.enabled_at; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_kf_sync_cursor.enabled_at IS '开通回调日；早于该时刻的消息不主动回填';


--
-- TOC entry 655 (class 1259 OID 413562)
-- Name: crm_wecom_kf_sync_cursor_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wecom_kf_sync_cursor_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wecom_kf_sync_cursor_id_seq OWNER TO luohd;

--
-- TOC entry 8750 (class 0 OID 0)
-- Dependencies: 655
-- Name: crm_wecom_kf_sync_cursor_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wecom_kf_sync_cursor_id_seq OWNED BY public.crm_wecom_kf_sync_cursor.id;


--
-- TOC entry 278 (class 1259 OID 360941)
-- Name: crm_wecom_mass_task; Type: TABLE; Schema: public; Owner: _tenant
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
    update_time timestamp without time zone,
    crm_survey_id character varying(64)
);


ALTER TABLE public.crm_wecom_mass_task OWNER TO "_tenant";

--
-- TOC entry 8752 (class 0 OID 0)
-- Dependencies: 278
-- Name: TABLE crm_wecom_mass_task; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON TABLE public.crm_wecom_mass_task IS '企业微信客户群发任务';


--
-- TOC entry 441 (class 1259 OID 371442)
-- Name: crm_wecom_mass_task_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
--

CREATE SEQUENCE public.crm_wecom_mass_task_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wecom_mass_task_id_seq OWNER TO "_tenant";

--
-- TOC entry 8754 (class 0 OID 0)
-- Dependencies: 441
-- Name: crm_wecom_mass_task_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: _tenant
--

ALTER SEQUENCE public.crm_wecom_mass_task_id_seq OWNED BY public.crm_wecom_mass_task.id;


--
-- TOC entry 476 (class 1259 OID 380019)
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
    media_id character varying(256),
    wecom_uploaded_at timestamp without time zone,
    media_created_at bigint,
    expires_at timestamp without time zone,
    status character varying(20) DEFAULT 'pending'::character varying,
    errmsg text,
    create_by character varying(64),
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    update_by character varying(64),
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    attachment_type smallint DEFAULT 0
);


ALTER TABLE public.crm_wecom_material OWNER TO luohd;

--
-- TOC entry 8756 (class 0 OID 0)
-- Dependencies: 476
-- Name: TABLE crm_wecom_material; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wecom_material IS '企业微信临时素材管理表';


--
-- TOC entry 8757 (class 0 OID 0)
-- Dependencies: 476
-- Name: COLUMN crm_wecom_material.file_title; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_material.file_title IS '素材名称';


--
-- TOC entry 8758 (class 0 OID 0)
-- Dependencies: 476
-- Name: COLUMN crm_wecom_material.file_name; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_material.file_name IS '服务器存储文件名';


--
-- TOC entry 8759 (class 0 OID 0)
-- Dependencies: 476
-- Name: COLUMN crm_wecom_material.original_name; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_material.original_name IS '用户上传时的原始文件名';


--
-- TOC entry 8760 (class 0 OID 0)
-- Dependencies: 476
-- Name: COLUMN crm_wecom_material.file_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_material.file_type IS '文件扩展名';


--
-- TOC entry 8761 (class 0 OID 0)
-- Dependencies: 476
-- Name: COLUMN crm_wecom_material.file_size; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_material.file_size IS '文件大小（字节）';


--
-- TOC entry 8762 (class 0 OID 0)
-- Dependencies: 476
-- Name: COLUMN crm_wecom_material.media_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_material.media_type IS '企微素材类型：image / voice / video / file';


--
-- TOC entry 8763 (class 0 OID 0)
-- Dependencies: 476
-- Name: COLUMN crm_wecom_material.media_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_material.media_id IS '企微返回的临时 media_id，有效期 3 天';


--
-- TOC entry 8764 (class 0 OID 0)
-- Dependencies: 476
-- Name: COLUMN crm_wecom_material.wecom_uploaded_at; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_material.wecom_uploaded_at IS '上传到企微的时间';


--
-- TOC entry 8765 (class 0 OID 0)
-- Dependencies: 476
-- Name: COLUMN crm_wecom_material.media_created_at; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_material.media_created_at IS '企微返回的 created_at（Unix 秒级时间戳）';


--
-- TOC entry 8766 (class 0 OID 0)
-- Dependencies: 476
-- Name: COLUMN crm_wecom_material.expires_at; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_material.expires_at IS 'media_id 过期时间（media_created_at + 3 天）';


--
-- TOC entry 8767 (class 0 OID 0)
-- Dependencies: 476
-- Name: COLUMN crm_wecom_material.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_material.status IS 'pending: 待上传 / uploaded: 已上传 / expired: 已过期 / upload_failed: 上传失败';


--
-- TOC entry 8768 (class 0 OID 0)
-- Dependencies: 476
-- Name: COLUMN crm_wecom_material.errmsg; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_material.errmsg IS '上传到企微失败时的错误信息';


--
-- TOC entry 8769 (class 0 OID 0)
-- Dependencies: 476
-- Name: COLUMN crm_wecom_material.attachment_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_material.attachment_type IS '附件类型: 0=普通临时素材 1=朋友圈附件 2=商品图册';


--
-- TOC entry 475 (class 1259 OID 380018)
-- Name: crm_wecom_material_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wecom_material_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wecom_material_id_seq OWNER TO luohd;

--
-- TOC entry 8771 (class 0 OID 0)
-- Dependencies: 475
-- Name: crm_wecom_material_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wecom_material_id_seq OWNED BY public.crm_wecom_material.id;


--
-- TOC entry 542 (class 1259 OID 408215)
-- Name: crm_wecom_stats_meta; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_wecom_stats_meta (
    id integer DEFAULT 1 NOT NULL,
    init_status character varying(16) DEFAULT 'none'::character varying NOT NULL,
    backfilled_at timestamp without time zone,
    backfill_days integer,
    lock_until timestamp without time zone,
    last_error text,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT crm_wecom_stats_meta_id_check CHECK ((id = 1))
);


ALTER TABLE public.crm_wecom_stats_meta OWNER TO luohd;

--
-- TOC entry 8773 (class 0 OID 0)
-- Dependencies: 542
-- Name: TABLE crm_wecom_stats_meta; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wecom_stats_meta IS '企业微信统计快照 · 租户级初始化标记与冷启动锁';


--
-- TOC entry 8774 (class 0 OID 0)
-- Dependencies: 542
-- Name: COLUMN crm_wecom_stats_meta.init_status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_stats_meta.init_status IS 'none=从未初始化 / running=冷启动进行中 / done=已完成 / failed=失败';


--
-- TOC entry 8775 (class 0 OID 0)
-- Dependencies: 542
-- Name: COLUMN crm_wecom_stats_meta.lock_until; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_wecom_stats_meta.lock_until IS '冷启动锁到期时间，过期可被抢占';


--
-- TOC entry 437 (class 1259 OID 371388)
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


ALTER TABLE public.crm_wecom_welcome_log OWNER TO luohd;

--
-- TOC entry 8777 (class 0 OID 0)
-- Dependencies: 437
-- Name: TABLE crm_wecom_welcome_log; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_wecom_welcome_log IS '企业微信欢迎语发送/跳过审计';


--
-- TOC entry 436 (class 1259 OID 371387)
-- Name: crm_wecom_welcome_log_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_wecom_welcome_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_wecom_welcome_log_id_seq OWNER TO luohd;

--
-- TOC entry 8779 (class 0 OID 0)
-- Dependencies: 436
-- Name: crm_wecom_welcome_log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_wecom_welcome_log_id_seq OWNED BY public.crm_wecom_welcome_log.id;


--
-- TOC entry 279 (class 1259 OID 360948)
-- Name: crm_weeklyreport; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_weeklyreport OWNER TO "_tenant";

--
-- TOC entry 608 (class 1259 OID 413006)
-- Name: crm_whatsapp_automation_rule; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_whatsapp_automation_rule (
    id bigint NOT NULL,
    tenant_id character varying(64) NOT NULL,
    rule_name character varying(128) NOT NULL,
    trigger_type character varying(64) NOT NULL,
    trigger_config jsonb,
    template_name character varying(256) NOT NULL,
    template_language character varying(16) NOT NULL,
    template_params jsonb,
    delay_seconds integer DEFAULT 0 NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.crm_whatsapp_automation_rule OWNER TO luohd;

--
-- TOC entry 8782 (class 0 OID 0)
-- Dependencies: 608
-- Name: TABLE crm_whatsapp_automation_rule; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_whatsapp_automation_rule IS 'WhatsApp 营销自动化规则';


--
-- TOC entry 8783 (class 0 OID 0)
-- Dependencies: 608
-- Name: COLUMN crm_whatsapp_automation_rule.trigger_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_whatsapp_automation_rule.trigger_type IS 'appointment_reminder | lead_no_reply | 预留其它';


--
-- TOC entry 8784 (class 0 OID 0)
-- Dependencies: 608
-- Name: COLUMN crm_whatsapp_automation_rule.delay_seconds; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_whatsapp_automation_rule.delay_seconds IS 'appointment_reminder: 预约开始前秒数; lead_no_reply: 线索创建后无回复等待秒数';


--
-- TOC entry 607 (class 1259 OID 413005)
-- Name: crm_whatsapp_automation_rule_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_whatsapp_automation_rule_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_whatsapp_automation_rule_id_seq OWNER TO luohd;

--
-- TOC entry 8786 (class 0 OID 0)
-- Dependencies: 607
-- Name: crm_whatsapp_automation_rule_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_whatsapp_automation_rule_id_seq OWNED BY public.crm_whatsapp_automation_rule.id;


--
-- TOC entry 563 (class 1259 OID 408972)
-- Name: crm_whatsapp_config; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_whatsapp_config (
    id bigint NOT NULL,
    tenant_id character varying(64) NOT NULL,
    phone_number_id character varying(64),
    waba_id character varying(64),
    business_name character varying(256),
    access_token_enc text,
    webhook_verify_token_enc text,
    webhook_configured smallint DEFAULT 0 NOT NULL,
    last_error text,
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    app_secret_enc text
);


ALTER TABLE public.crm_whatsapp_config OWNER TO luohd;

--
-- TOC entry 8788 (class 0 OID 0)
-- Dependencies: 563
-- Name: TABLE crm_whatsapp_config; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_whatsapp_config IS '租户级 WhatsApp Business Platform 配置';


--
-- TOC entry 8789 (class 0 OID 0)
-- Dependencies: 563
-- Name: COLUMN crm_whatsapp_config.access_token_enc; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_whatsapp_config.access_token_enc IS 'AES-256-GCM 加密的 System User Access Token';


--
-- TOC entry 8790 (class 0 OID 0)
-- Dependencies: 563
-- Name: COLUMN crm_whatsapp_config.webhook_verify_token_enc; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_whatsapp_config.webhook_verify_token_enc IS 'AES-256-GCM 加密的 Webhook verify_token，用于 Meta hub.challenge 验证';


--
-- TOC entry 8791 (class 0 OID 0)
-- Dependencies: 563
-- Name: COLUMN crm_whatsapp_config.webhook_configured; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_whatsapp_config.webhook_configured IS '0=未配置 1=已配置 Webhook 回调';


--
-- TOC entry 8792 (class 0 OID 0)
-- Dependencies: 563
-- Name: COLUMN crm_whatsapp_config.last_error; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_whatsapp_config.last_error IS '最近一次 API 调用错误信息，用于诊断';


--
-- TOC entry 8793 (class 0 OID 0)
-- Dependencies: 563
-- Name: COLUMN crm_whatsapp_config.app_secret_enc; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_whatsapp_config.app_secret_enc IS 'AES-256-GCM 加密的 Meta App Secret，用于 X-Hub-Signature-256 验签';


--
-- TOC entry 562 (class 1259 OID 408971)
-- Name: crm_whatsapp_config_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_whatsapp_config_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_whatsapp_config_id_seq OWNER TO luohd;

--
-- TOC entry 8795 (class 0 OID 0)
-- Dependencies: 562
-- Name: crm_whatsapp_config_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_whatsapp_config_id_seq OWNED BY public.crm_whatsapp_config.id;


--
-- TOC entry 567 (class 1259 OID 409000)
-- Name: crm_whatsapp_conversation; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_whatsapp_conversation (
    id bigint NOT NULL,
    tenant_id character varying(64) NOT NULL,
    wa_id character varying(64) NOT NULL,
    phone_number character varying(32),
    contact_id character varying(64),
    lead_id character varying(64),
    phone_number_id character varying(64),
    last_message_at timestamp without time zone,
    last_message_preview character varying(256),
    last_customer_message_at timestamp without time zone,
    last_outbound_at timestamp without time zone,
    unread_count integer DEFAULT 0 NOT NULL,
    status character varying(16) DEFAULT 'active'::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT now() NOT NULL,
    update_time timestamp without time zone,
    user_id bigint,
    owner character varying(128),
    assigned_at timestamp without time zone,
    hosted_by_ai boolean
);


ALTER TABLE public.crm_whatsapp_conversation OWNER TO luohd;

--
-- TOC entry 8797 (class 0 OID 0)
-- Dependencies: 567
-- Name: TABLE crm_whatsapp_conversation; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_whatsapp_conversation IS 'WhatsApp 会话主表，一个 wa_id 在租户下仅一条记录';


--
-- TOC entry 8798 (class 0 OID 0)
-- Dependencies: 567
-- Name: COLUMN crm_whatsapp_conversation.last_customer_message_at; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_whatsapp_conversation.last_customer_message_at IS '最近一次客户回复时间，用于判断 24h messaging window 是否仍然有效';


--
-- TOC entry 8799 (class 0 OID 0)
-- Dependencies: 567
-- Name: COLUMN crm_whatsapp_conversation.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_whatsapp_conversation.status IS 'active=活跃 archived=归档 blocked=已屏蔽';


--
-- TOC entry 8800 (class 0 OID 0)
-- Dependencies: 567
-- Name: COLUMN crm_whatsapp_conversation.user_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_whatsapp_conversation.user_id IS '负责人系统用户 id；NULL = 公共池';


--
-- TOC entry 8801 (class 0 OID 0)
-- Dependencies: 567
-- Name: COLUMN crm_whatsapp_conversation.owner; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_whatsapp_conversation.owner IS '负责人展示名';


--
-- TOC entry 8802 (class 0 OID 0)
-- Dependencies: 567
-- Name: COLUMN crm_whatsapp_conversation.assigned_at; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_whatsapp_conversation.assigned_at IS '最近一次领取 / 指派时间';


--
-- TOC entry 8803 (class 0 OID 0)
-- Dependencies: 567
-- Name: COLUMN crm_whatsapp_conversation.hosted_by_ai; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_whatsapp_conversation.hosted_by_ai IS 'true=AI托管; false=人工接管; NULL=跟随渠道 hosted';


--
-- TOC entry 566 (class 1259 OID 408999)
-- Name: crm_whatsapp_conversation_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_whatsapp_conversation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_whatsapp_conversation_id_seq OWNER TO luohd;

--
-- TOC entry 8805 (class 0 OID 0)
-- Dependencies: 566
-- Name: crm_whatsapp_conversation_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_whatsapp_conversation_id_seq OWNED BY public.crm_whatsapp_conversation.id;


--
-- TOC entry 569 (class 1259 OID 409018)
-- Name: crm_whatsapp_message; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_whatsapp_message (
    id bigint NOT NULL,
    tenant_id character varying(64) NOT NULL,
    conversation_id bigint NOT NULL,
    wa_id character varying(64) NOT NULL,
    message_id character varying(128),
    message_type character varying(32),
    content text,
    direction smallint NOT NULL,
    send_time bigint,
    status character varying(32),
    media_id character varying(256),
    media_url character varying(1024),
    media_mime_type character varying(128),
    filename character varying(512),
    raw_json text,
    create_time timestamp without time zone DEFAULT now() NOT NULL,
    source character varying(32)
);


ALTER TABLE public.crm_whatsapp_message OWNER TO luohd;

--
-- TOC entry 8807 (class 0 OID 0)
-- Dependencies: 569
-- Name: TABLE crm_whatsapp_message; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_whatsapp_message IS 'WhatsApp 消息明细表';


--
-- TOC entry 8808 (class 0 OID 0)
-- Dependencies: 569
-- Name: COLUMN crm_whatsapp_message.direction; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_whatsapp_message.direction IS '0=客户发送（incoming） 1=我方发送（outgoing）';


--
-- TOC entry 8809 (class 0 OID 0)
-- Dependencies: 569
-- Name: COLUMN crm_whatsapp_message.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_whatsapp_message.status IS '投递状态：sent | delivered | read | failed';


--
-- TOC entry 8810 (class 0 OID 0)
-- Dependencies: 569
-- Name: COLUMN crm_whatsapp_message.media_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_whatsapp_message.media_id IS 'Meta 媒体 ID，下载媒体使用 GET /v22.0/{media_id}';


--
-- TOC entry 8811 (class 0 OID 0)
-- Dependencies: 569
-- Name: COLUMN crm_whatsapp_message.media_mime_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_whatsapp_message.media_mime_type IS '媒体文件 MIME 类型';


--
-- TOC entry 8812 (class 0 OID 0)
-- Dependencies: 569
-- Name: COLUMN crm_whatsapp_message.source; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_whatsapp_message.source IS 'outbound origin: null=human, ai_inbox_agent=Inbox AI';


--
-- TOC entry 568 (class 1259 OID 409017)
-- Name: crm_whatsapp_message_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_whatsapp_message_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_whatsapp_message_id_seq OWNER TO luohd;

--
-- TOC entry 8814 (class 0 OID 0)
-- Dependencies: 568
-- Name: crm_whatsapp_message_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_whatsapp_message_id_seq OWNED BY public.crm_whatsapp_message.id;


--
-- TOC entry 571 (class 1259 OID 409031)
-- Name: crm_whatsapp_template; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_whatsapp_template (
    id bigint NOT NULL,
    tenant_id character varying(64) NOT NULL,
    template_name character varying(256) NOT NULL,
    language character varying(16) NOT NULL,
    category character varying(32),
    status character varying(16) DEFAULT 'pending'::character varying NOT NULL,
    rejection_reason text,
    components jsonb,
    preview_url character varying(1024),
    last_sync_time timestamp without time zone,
    create_time timestamp without time zone DEFAULT now() NOT NULL,
    update_time timestamp without time zone
);


ALTER TABLE public.crm_whatsapp_template OWNER TO luohd;

--
-- TOC entry 8816 (class 0 OID 0)
-- Dependencies: 571
-- Name: TABLE crm_whatsapp_template; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_whatsapp_template IS 'WhatsApp Template Message 缓存，定期从 Meta 拉取或手动同步';


--
-- TOC entry 8817 (class 0 OID 0)
-- Dependencies: 571
-- Name: COLUMN crm_whatsapp_template.components; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_whatsapp_template.components IS 'Meta 返回的 template components JSON 结构';


--
-- TOC entry 8818 (class 0 OID 0)
-- Dependencies: 571
-- Name: COLUMN crm_whatsapp_template.preview_url; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_whatsapp_template.preview_url IS 'Meta 返回的模板预览图片 URL';


--
-- TOC entry 570 (class 1259 OID 409030)
-- Name: crm_whatsapp_template_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_whatsapp_template_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_whatsapp_template_id_seq OWNER TO luohd;

--
-- TOC entry 8820 (class 0 OID 0)
-- Dependencies: 570
-- Name: crm_whatsapp_template_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_whatsapp_template_id_seq OWNED BY public.crm_whatsapp_template.id;


--
-- TOC entry 606 (class 1259 OID 412986)
-- Name: crm_whatsapp_template_send_item; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_whatsapp_template_send_item (
    id bigint NOT NULL,
    tenant_id character varying(64) NOT NULL,
    task_id bigint NOT NULL,
    wa_id character varying(64) NOT NULL,
    contact_id character varying(64),
    rule_id bigint,
    business_key character varying(128),
    status character varying(32) DEFAULT 'pending'::character varying NOT NULL,
    message_id character varying(128),
    error_code character varying(64),
    error_message text,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.crm_whatsapp_template_send_item OWNER TO luohd;

--
-- TOC entry 8822 (class 0 OID 0)
-- Dependencies: 606
-- Name: TABLE crm_whatsapp_template_send_item; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_whatsapp_template_send_item IS 'WhatsApp 模板群发任务明细';


--
-- TOC entry 8823 (class 0 OID 0)
-- Dependencies: 606
-- Name: COLUMN crm_whatsapp_template_send_item.business_key; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_whatsapp_template_send_item.business_key IS '自动化防重键，如 appointment:{id}';


--
-- TOC entry 8824 (class 0 OID 0)
-- Dependencies: 606
-- Name: COLUMN crm_whatsapp_template_send_item.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_whatsapp_template_send_item.status IS 'pending | sent | delivered | read | failed | skipped';


--
-- TOC entry 605 (class 1259 OID 412985)
-- Name: crm_whatsapp_template_send_item_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_whatsapp_template_send_item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_whatsapp_template_send_item_id_seq OWNER TO luohd;

--
-- TOC entry 8826 (class 0 OID 0)
-- Dependencies: 605
-- Name: crm_whatsapp_template_send_item_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_whatsapp_template_send_item_id_seq OWNED BY public.crm_whatsapp_template_send_item.id;


--
-- TOC entry 604 (class 1259 OID 412968)
-- Name: crm_whatsapp_template_send_task; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_whatsapp_template_send_task (
    id bigint NOT NULL,
    tenant_id character varying(64) NOT NULL,
    phone_number_id character varying(64),
    template_name character varying(256) NOT NULL,
    language character varying(16) NOT NULL,
    components jsonb,
    audience_type character varying(32) DEFAULT 'wa_ids'::character varying NOT NULL,
    audience_ref jsonb,
    status character varying(32) DEFAULT 'pending'::character varying NOT NULL,
    total_count integer DEFAULT 0 NOT NULL,
    success_count integer DEFAULT 0 NOT NULL,
    failed_count integer DEFAULT 0 NOT NULL,
    skipped_count integer DEFAULT 0 NOT NULL,
    error_message text,
    created_by character varying(64),
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    started_at timestamp with time zone,
    finished_at timestamp with time zone
);


ALTER TABLE public.crm_whatsapp_template_send_task OWNER TO luohd;

--
-- TOC entry 8828 (class 0 OID 0)
-- Dependencies: 604
-- Name: TABLE crm_whatsapp_template_send_task; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_whatsapp_template_send_task IS 'WhatsApp 模板群发任务头';


--
-- TOC entry 8829 (class 0 OID 0)
-- Dependencies: 604
-- Name: COLUMN crm_whatsapp_template_send_task.audience_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_whatsapp_template_send_task.audience_type IS 'wa_ids | tag | segment';


--
-- TOC entry 8830 (class 0 OID 0)
-- Dependencies: 604
-- Name: COLUMN crm_whatsapp_template_send_task.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_whatsapp_template_send_task.status IS 'pending | running | completed | failed | cancelled';


--
-- TOC entry 603 (class 1259 OID 412967)
-- Name: crm_whatsapp_template_send_task_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_whatsapp_template_send_task_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_whatsapp_template_send_task_id_seq OWNER TO luohd;

--
-- TOC entry 8832 (class 0 OID 0)
-- Dependencies: 603
-- Name: crm_whatsapp_template_send_task_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_whatsapp_template_send_task_id_seq OWNED BY public.crm_whatsapp_template_send_task.id;


--
-- TOC entry 565 (class 1259 OID 408985)
-- Name: crm_whatsapp_webhook_event; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.crm_whatsapp_webhook_event (
    id bigint NOT NULL,
    tenant_id character varying(64) NOT NULL,
    wa_id character varying(64),
    phone_number character varying(32),
    phone_number_id character varying(64),
    waba_id character varying(64),
    event_type character varying(64) NOT NULL,
    message_id character varying(128),
    message_type character varying(32),
    content text,
    direction smallint,
    send_time bigint,
    raw_json text,
    ad_campaign_id character varying(128),
    ad_set_id character varying(128),
    ad_id character varying(128),
    crm_lead_id character varying(64),
    crm_contact_id character varying(64),
    status character varying(16) DEFAULT 'pending'::character varying NOT NULL,
    processing_error text,
    create_time timestamp without time zone DEFAULT now() NOT NULL,
    update_time timestamp without time zone
);


ALTER TABLE public.crm_whatsapp_webhook_event OWNER TO luohd;

--
-- TOC entry 8834 (class 0 OID 0)
-- Dependencies: 565
-- Name: TABLE crm_whatsapp_webhook_event; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_whatsapp_webhook_event IS 'Meta WhatsApp Webhook 事件日志（原始记录）';


--
-- TOC entry 8835 (class 0 OID 0)
-- Dependencies: 565
-- Name: COLUMN crm_whatsapp_webhook_event.event_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_whatsapp_webhook_event.event_type IS 'messages | message_status | message_template_status_update';


--
-- TOC entry 8836 (class 0 OID 0)
-- Dependencies: 565
-- Name: COLUMN crm_whatsapp_webhook_event.ad_campaign_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_whatsapp_webhook_event.ad_campaign_id IS 'Click to WhatsApp 广告 campaign_id';


--
-- TOC entry 8837 (class 0 OID 0)
-- Dependencies: 565
-- Name: COLUMN crm_whatsapp_webhook_event.crm_lead_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_whatsapp_webhook_event.crm_lead_id IS '发现 Click to WhatsApp 广告则自动创建 Lead，存储 leadId';


--
-- TOC entry 8838 (class 0 OID 0)
-- Dependencies: 565
-- Name: COLUMN crm_whatsapp_webhook_event.crm_contact_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_whatsapp_webhook_event.crm_contact_id IS '经由 Identity Resolver 解析得出的 Contact ID，非必填';


--
-- TOC entry 8839 (class 0 OID 0)
-- Dependencies: 565
-- Name: COLUMN crm_whatsapp_webhook_event.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_whatsapp_webhook_event.status IS 'pending=待处理 processed=处理完成 failed=处理失败 ignored=忽略';


--
-- TOC entry 564 (class 1259 OID 408984)
-- Name: crm_whatsapp_webhook_event_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.crm_whatsapp_webhook_event_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.crm_whatsapp_webhook_event_id_seq OWNER TO luohd;

--
-- TOC entry 8841 (class 0 OID 0)
-- Dependencies: 564
-- Name: crm_whatsapp_webhook_event_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_whatsapp_webhook_event_id_seq OWNED BY public.crm_whatsapp_webhook_event.id;


--
-- TOC entry 433 (class 1259 OID 371261)
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


ALTER TABLE public.crm_workflow_automation OWNER TO luohd;

--
-- TOC entry 8843 (class 0 OID 0)
-- Dependencies: 433
-- Name: TABLE crm_workflow_automation; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_workflow_automation IS 'Tenant workflow automation: business event -> wworkflow id';


--
-- TOC entry 8844 (class 0 OID 0)
-- Dependencies: 433
-- Name: COLUMN crm_workflow_automation.automation_key; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_workflow_automation.automation_key IS 'LEAD_TAGGING | LEAD_CONVERT | LEAD_FOLLOWUP | LEAD_CALLBACK | LEAD_APPOINTMENT';


--
-- TOC entry 8845 (class 0 OID 0)
-- Dependencies: 433
-- Name: COLUMN crm_workflow_automation.enabled; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_workflow_automation.enabled IS '0 disabled, 1 enabled';


--
-- TOC entry 8846 (class 0 OID 0)
-- Dependencies: 433
-- Name: COLUMN crm_workflow_automation.trigger_event; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_workflow_automation.trigger_event IS 'e.g. LEAD_CREATED — reserved for backend schedulers';


--
-- TOC entry 281 (class 1259 OID 360958)
-- Name: crm_workflow_trigger; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.crm_workflow_trigger OWNER TO "_tenant";

--
-- TOC entry 280 (class 1259 OID 360957)
-- Name: crm_workflow_trigger_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
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
-- TOC entry 487 (class 1259 OID 404156)
-- Name: customer_metrics; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.customer_metrics (
    contact_id character varying(64) NOT NULL,
    wecom_added_at timestamp without time zone,
    wecom_message_count integer DEFAULT 0,
    wecom_outbound_count integer DEFAULT 0,
    wecom_inbound_count integer DEFAULT 0,
    wecom_last_message_at timestamp without time zone,
    wecom_group_count integer DEFAULT 0,
    wecom_active_days_30d integer DEFAULT 0,
    wechat_mp_followed_at timestamp without time zone,
    wechat_mp_article_read_30d integer DEFAULT 0,
    wechat_miniapp_login_30d integer DEFAULT 0,
    wechat_miniapp_form_submit integer DEFAULT 0,
    lead_created_at timestamp without time zone,
    lead_count integer DEFAULT 0,
    appointment_count integer DEFAULT 0,
    appointment_noshow_count integer DEFAULT 0,
    visit_count integer DEFAULT 0,
    first_visit_at timestamp without time zone,
    last_visit_at timestamp without time zone,
    opportunity_count integer DEFAULT 0,
    opportunity_won_count integer DEFAULT 0,
    total_revenue numeric(15,2) DEFAULT 0,
    avg_deal_amount numeric(15,2) DEFAULT 0,
    last_payment_at timestamp without time zone,
    ai_score_latest integer,
    ai_score_updated_at timestamp without time zone,
    ai_tag_count integer DEFAULT 0,
    ai_intent_latest character varying(64),
    ai_risk_level character varying(16),
    lifecycle_stage character varying(32),
    lifecycle_stage_at timestamp without time zone,
    churn_risk_score integer,
    total_event_count integer DEFAULT 0,
    last_event_at timestamp without time zone,
    computed_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now()
);


ALTER TABLE public.customer_metrics OWNER TO luohd;

--
-- TOC entry 8850 (class 0 OID 0)
-- Dependencies: 487
-- Name: TABLE customer_metrics; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.customer_metrics IS 'Customer Metrics: 客户聚合指标快照，定期从 crm_activity 计算';


--
-- TOC entry 8851 (class 0 OID 0)
-- Dependencies: 487
-- Name: COLUMN customer_metrics.lifecycle_stage; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.customer_metrics.lifecycle_stage IS 'lead|opportunity|customer|churned';


--
-- TOC entry 8852 (class 0 OID 0)
-- Dependencies: 487
-- Name: COLUMN customer_metrics.churn_risk_score; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.customer_metrics.churn_risk_score IS '流失风险评分 (0-100)';


--
-- TOC entry 282 (class 1259 OID 360966)
-- Name: den_inventory; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.den_inventory OWNER TO "_tenant";

--
-- TOC entry 283 (class 1259 OID 360973)
-- Name: den_staff; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.den_staff OWNER TO "_tenant";

--
-- TOC entry 284 (class 1259 OID 360981)
-- Name: den_staff_bank; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.den_staff_bank OWNER TO "_tenant";

--
-- TOC entry 285 (class 1259 OID 360989)
-- Name: den_vendor; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.den_vendor OWNER TO "_tenant";

--
-- TOC entry 286 (class 1259 OID 360996)
-- Name: ems_hcp; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.ems_hcp OWNER TO "_tenant";

--
-- TOC entry 417 (class 1259 OID 369640)
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


ALTER TABLE public.flw_channel_definition OWNER TO luohd;

--
-- TOC entry 418 (class 1259 OID 369645)
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


ALTER TABLE public.flw_ev_databasechangelog OWNER TO luohd;

--
-- TOC entry 419 (class 1259 OID 369650)
-- Name: flw_ev_databasechangeloglock; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.flw_ev_databasechangeloglock (
    id integer NOT NULL,
    locked boolean NOT NULL,
    lockgranted timestamp without time zone,
    lockedby character varying(255)
);


ALTER TABLE public.flw_ev_databasechangeloglock OWNER TO luohd;

--
-- TOC entry 420 (class 1259 OID 369653)
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


ALTER TABLE public.flw_event_definition OWNER TO luohd;

--
-- TOC entry 421 (class 1259 OID 369658)
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


ALTER TABLE public.flw_event_deployment OWNER TO luohd;

--
-- TOC entry 422 (class 1259 OID 369663)
-- Name: flw_event_resource; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.flw_event_resource (
    id_ character varying(255) NOT NULL,
    name_ character varying(255),
    deployment_id_ character varying(255),
    resource_bytes_ bytea
);


ALTER TABLE public.flw_event_resource OWNER TO luohd;

--
-- TOC entry 423 (class 1259 OID 369668)
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


ALTER TABLE public.flw_ru_batch OWNER TO luohd;

--
-- TOC entry 424 (class 1259 OID 369674)
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


ALTER TABLE public.flw_ru_batch_part OWNER TO luohd;

--
-- TOC entry 288 (class 1259 OID 361050)
-- Name: gen_table; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.gen_table OWNER TO "_tenant";

--
-- TOC entry 290 (class 1259 OID 361061)
-- Name: gen_table_column; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.gen_table_column OWNER TO "_tenant";

--
-- TOC entry 289 (class 1259 OID 361060)
-- Name: gen_table_column_column_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
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
-- TOC entry 287 (class 1259 OID 361049)
-- Name: gen_table_table_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
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
-- TOC entry 292 (class 1259 OID 361070)
-- Name: master_registration; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.master_registration OWNER TO "_tenant";

--
-- TOC entry 291 (class 1259 OID 361069)
-- Name: master_registration_regist_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
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
-- TOC entry 294 (class 1259 OID 361079)
-- Name: master_tenant; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.master_tenant OWNER TO "_tenant";

--
-- TOC entry 293 (class 1259 OID 361078)
-- Name: master_tenant_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
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
-- TOC entry 425 (class 1259 OID 371083)
-- Name: session; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.session OWNER TO "_tenant";

--
-- TOC entry 549 (class 1259 OID 408720)
-- Name: smart_push_execution_log; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.smart_push_execution_log (
    id bigint NOT NULL,
    strategy_id bigint NOT NULL,
    strategy_name character varying(100) NOT NULL,
    target_user_sn character varying(100) NOT NULL,
    target_user_name character varying(100) DEFAULT ''::character varying,
    contact_id character varying(50),
    contact_name character varying(200) NOT NULL,
    success smallint NOT NULL,
    error_msg character varying(500),
    executed_at timestamp without time zone NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    event_type character varying(50)
);


ALTER TABLE public.smart_push_execution_log OWNER TO luohd;

--
-- TOC entry 8876 (class 0 OID 0)
-- Dependencies: 549
-- Name: TABLE smart_push_execution_log; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.smart_push_execution_log IS '自动推送执行日志';


--
-- TOC entry 8877 (class 0 OID 0)
-- Dependencies: 549
-- Name: COLUMN smart_push_execution_log.strategy_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.smart_push_execution_log.strategy_id IS '策略 ID';


--
-- TOC entry 8878 (class 0 OID 0)
-- Dependencies: 549
-- Name: COLUMN smart_push_execution_log.strategy_name; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.smart_push_execution_log.strategy_name IS '策略名称（冗余，方便查询）';


--
-- TOC entry 8879 (class 0 OID 0)
-- Dependencies: 549
-- Name: COLUMN smart_push_execution_log.target_user_sn; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.smart_push_execution_log.target_user_sn IS '企微目标用户的 snUserid';


--
-- TOC entry 8880 (class 0 OID 0)
-- Dependencies: 549
-- Name: COLUMN smart_push_execution_log.target_user_name; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.smart_push_execution_log.target_user_name IS '目标用户显示名';


--
-- TOC entry 8881 (class 0 OID 0)
-- Dependencies: 549
-- Name: COLUMN smart_push_execution_log.contact_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.smart_push_execution_log.contact_id IS '关联的联系人 ID';


--
-- TOC entry 8882 (class 0 OID 0)
-- Dependencies: 549
-- Name: COLUMN smart_push_execution_log.contact_name; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.smart_push_execution_log.contact_name IS '联系人姓名';


--
-- TOC entry 8883 (class 0 OID 0)
-- Dependencies: 549
-- Name: COLUMN smart_push_execution_log.success; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.smart_push_execution_log.success IS '是否成功 0-失败 1-成功';


--
-- TOC entry 8884 (class 0 OID 0)
-- Dependencies: 549
-- Name: COLUMN smart_push_execution_log.error_msg; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.smart_push_execution_log.error_msg IS '失败原因';


--
-- TOC entry 8885 (class 0 OID 0)
-- Dependencies: 549
-- Name: COLUMN smart_push_execution_log.executed_at; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.smart_push_execution_log.executed_at IS '执行时间';


--
-- TOC entry 548 (class 1259 OID 408719)
-- Name: smart_push_execution_log_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.smart_push_execution_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.smart_push_execution_log_id_seq OWNER TO luohd;

--
-- TOC entry 8887 (class 0 OID 0)
-- Dependencies: 548
-- Name: smart_push_execution_log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.smart_push_execution_log_id_seq OWNED BY public.smart_push_execution_log.id;


--
-- TOC entry 547 (class 1259 OID 408708)
-- Name: smart_push_strategy; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.smart_push_strategy (
    id bigint NOT NULL,
    strategy_type character varying(50) NOT NULL,
    name character varying(100) NOT NULL,
    enabled smallint DEFAULT 0 NOT NULL,
    cron_expression character varying(50),
    channel character varying(50) NOT NULL,
    target_mode character varying(20) NOT NULL,
    message_template text NOT NULL,
    config_json jsonb,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.smart_push_strategy OWNER TO luohd;

--
-- TOC entry 8889 (class 0 OID 0)
-- Dependencies: 547
-- Name: TABLE smart_push_strategy; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.smart_push_strategy IS '智能推送策略配置';


--
-- TOC entry 8890 (class 0 OID 0)
-- Dependencies: 547
-- Name: COLUMN smart_push_strategy.strategy_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.smart_push_strategy.strategy_type IS '策略类型: birthday';


--
-- TOC entry 8891 (class 0 OID 0)
-- Dependencies: 547
-- Name: COLUMN smart_push_strategy.name; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.smart_push_strategy.name IS '策略名称';


--
-- TOC entry 8892 (class 0 OID 0)
-- Dependencies: 547
-- Name: COLUMN smart_push_strategy.enabled; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.smart_push_strategy.enabled IS '是否启用 0-否 1-是';


--
-- TOC entry 8893 (class 0 OID 0)
-- Dependencies: 547
-- Name: COLUMN smart_push_strategy.cron_expression; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.smart_push_strategy.cron_expression IS 'Cron 表达式（参考用，实际由系统定时任务控制）';


--
-- TOC entry 8894 (class 0 OID 0)
-- Dependencies: 547
-- Name: COLUMN smart_push_strategy.channel; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.smart_push_strategy.channel IS '推送渠道: wecom/kf/mp_template';


--
-- TOC entry 8895 (class 0 OID 0)
-- Dependencies: 547
-- Name: COLUMN smart_push_strategy.target_mode; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.smart_push_strategy.target_mode IS '发送目标: owner-给负责人发提醒, customer-直接给客户发';


--
-- TOC entry 8896 (class 0 OID 0)
-- Dependencies: 547
-- Name: COLUMN smart_push_strategy.message_template; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.smart_push_strategy.message_template IS '消息模版，支持 {{name}} {{phone}} {{birthdate}} {{age}} {{company}} {{gender}}';


--
-- TOC entry 8897 (class 0 OID 0)
-- Dependencies: 547
-- Name: COLUMN smart_push_strategy.config_json; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.smart_push_strategy.config_json IS '扩展配置 JSON';


--
-- TOC entry 546 (class 1259 OID 408707)
-- Name: smart_push_strategy_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.smart_push_strategy_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.smart_push_strategy_id_seq OWNER TO luohd;

--
-- TOC entry 8899 (class 0 OID 0)
-- Dependencies: 546
-- Name: smart_push_strategy_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.smart_push_strategy_id_seq OWNED BY public.smart_push_strategy.id;


--
-- TOC entry 296 (class 1259 OID 361088)
-- Name: sys_config; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_config OWNER TO "_tenant";

--
-- TOC entry 295 (class 1259 OID 361087)
-- Name: sys_config_config_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
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
-- TOC entry 298 (class 1259 OID 361097)
-- Name: sys_deploy_form; Type: TABLE; Schema: public; Owner: _tenant
--

CREATE TABLE public.sys_deploy_form (
    id bigint NOT NULL,
    form_id bigint,
    deploy_id character varying(50)
);


ALTER TABLE public.sys_deploy_form OWNER TO "_tenant";

--
-- TOC entry 297 (class 1259 OID 361096)
-- Name: sys_deploy_form_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
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
-- TOC entry 300 (class 1259 OID 361103)
-- Name: sys_dept; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_dept OWNER TO "_tenant";

--
-- TOC entry 299 (class 1259 OID 361102)
-- Name: sys_dept_dept_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
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
-- TOC entry 302 (class 1259 OID 361113)
-- Name: sys_dict_data; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_dict_data OWNER TO "_tenant";

--
-- TOC entry 301 (class 1259 OID 361112)
-- Name: sys_dict_data_dict_code_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
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
-- TOC entry 304 (class 1259 OID 361124)
-- Name: sys_dict_type; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_dict_type OWNER TO "_tenant";

--
-- TOC entry 303 (class 1259 OID 361123)
-- Name: sys_dict_type_dict_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
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
-- TOC entry 306 (class 1259 OID 361133)
-- Name: sys_ems_corres; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_ems_corres OWNER TO "_tenant";

--
-- TOC entry 305 (class 1259 OID 361132)
-- Name: sys_ems_corres_corres_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
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
-- TOC entry 308 (class 1259 OID 361149)
-- Name: sys_form; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_form OWNER TO "_tenant";

--
-- TOC entry 307 (class 1259 OID 361148)
-- Name: sys_form_form_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
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
-- TOC entry 309 (class 1259 OID 361156)
-- Name: sys_hi_audittrail; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_hi_audittrail OWNER TO "_tenant";

--
-- TOC entry 310 (class 1259 OID 361163)
-- Name: sys_job; Type: TABLE; Schema: public; Owner: _tenant
--

CREATE TABLE public.sys_job (
    job_id bigint NOT NULL,
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


ALTER TABLE public.sys_job OWNER TO "_tenant";

--
-- TOC entry 648 (class 1259 OID 413494)
-- Name: sys_job_job_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
--

ALTER TABLE public.sys_job ALTER COLUMN job_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.sys_job_job_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 312 (class 1259 OID 361175)
-- Name: sys_job_log; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_job_log OWNER TO "_tenant";

--
-- TOC entry 311 (class 1259 OID 361174)
-- Name: sys_job_log_job_log_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
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
-- TOC entry 314 (class 1259 OID 361184)
-- Name: sys_logininfor; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_logininfor OWNER TO "_tenant";

--
-- TOC entry 313 (class 1259 OID 361183)
-- Name: sys_logininfor_info_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
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
-- TOC entry 316 (class 1259 OID 361193)
-- Name: sys_menu; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_menu OWNER TO "_tenant";

--
-- TOC entry 315 (class 1259 OID 361192)
-- Name: sys_menu_menu_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
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
-- TOC entry 318 (class 1259 OID 361208)
-- Name: sys_notice; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_notice OWNER TO "_tenant";

--
-- TOC entry 317 (class 1259 OID 361207)
-- Name: sys_notice_notice_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
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
-- TOC entry 320 (class 1259 OID 361217)
-- Name: sys_oper_log; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_oper_log OWNER TO "_tenant";

--
-- TOC entry 319 (class 1259 OID 361216)
-- Name: sys_oper_log_oper_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
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
-- TOC entry 321 (class 1259 OID 361227)
-- Name: sys_organization; Type: TABLE; Schema: public; Owner: _tenant
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
    larger_avatar character varying(255),
    industry character varying(128) DEFAULT ''::character varying,
    tagline character varying(255) DEFAULT ''::character varying,
    intro text
);


ALTER TABLE public.sys_organization OWNER TO "_tenant";

--
-- TOC entry 8927 (class 0 OID 0)
-- Dependencies: 321
-- Name: COLUMN sys_organization.industry; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.sys_organization.industry IS '行业（知识库主档）';


--
-- TOC entry 8928 (class 0 OID 0)
-- Dependencies: 321
-- Name: COLUMN sys_organization.tagline; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.sys_organization.tagline IS '一句话定位';


--
-- TOC entry 8929 (class 0 OID 0)
-- Dependencies: 321
-- Name: COLUMN sys_organization.intro; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.sys_organization.intro IS '企业简介长文；可空，空则同步时用 description';


--
-- TOC entry 323 (class 1259 OID 361235)
-- Name: sys_pm_log; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_pm_log OWNER TO "_tenant";

--
-- TOC entry 322 (class 1259 OID 361234)
-- Name: sys_pm_log_log_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
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
-- TOC entry 325 (class 1259 OID 361244)
-- Name: sys_post; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_post OWNER TO "_tenant";

--
-- TOC entry 324 (class 1259 OID 361243)
-- Name: sys_post_post_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
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
-- TOC entry 326 (class 1259 OID 361251)
-- Name: sys_re_acceptance; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_re_acceptance OWNER TO "_tenant";

--
-- TOC entry 327 (class 1259 OID 361261)
-- Name: sys_re_actitem; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_re_actitem OWNER TO "_tenant";

--
-- TOC entry 328 (class 1259 OID 361271)
-- Name: sys_re_activity; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_re_activity OWNER TO "_tenant";

--
-- TOC entry 329 (class 1259 OID 361279)
-- Name: sys_re_application; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_re_application OWNER TO "_tenant";

--
-- TOC entry 330 (class 1259 OID 361287)
-- Name: sys_re_board; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_re_board OWNER TO "_tenant";

--
-- TOC entry 332 (class 1259 OID 361298)
-- Name: sys_re_casetype; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_re_casetype OWNER TO "_tenant";

--
-- TOC entry 331 (class 1259 OID 361297)
-- Name: sys_re_casetype_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
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
-- TOC entry 333 (class 1259 OID 361311)
-- Name: sys_re_checklist; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_re_checklist OWNER TO "_tenant";

--
-- TOC entry 334 (class 1259 OID 361319)
-- Name: sys_re_corres; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_re_corres OWNER TO "_tenant";

--
-- TOC entry 335 (class 1259 OID 361330)
-- Name: sys_re_datafield; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_re_datafield OWNER TO "_tenant";

--
-- TOC entry 336 (class 1259 OID 361340)
-- Name: sys_re_dataobj; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_re_dataobj OWNER TO "_tenant";

--
-- TOC entry 338 (class 1259 OID 361349)
-- Name: sys_re_datatransform; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_re_datatransform OWNER TO "_tenant";

--
-- TOC entry 337 (class 1259 OID 361348)
-- Name: sys_re_datatransform_py_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
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
-- TOC entry 340 (class 1259 OID 361358)
-- Name: sys_re_datatype; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_re_datatype OWNER TO "_tenant";

--
-- TOC entry 339 (class 1259 OID 361357)
-- Name: sys_re_datatype_data_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
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
-- TOC entry 341 (class 1259 OID 361366)
-- Name: sys_re_decisiontable; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_re_decisiontable OWNER TO "_tenant";

--
-- TOC entry 342 (class 1259 OID 361375)
-- Name: sys_re_decisiontree; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_re_decisiontree OWNER TO "_tenant";

--
-- TOC entry 343 (class 1259 OID 361384)
-- Name: sys_re_dtitem; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_re_dtitem OWNER TO "_tenant";

--
-- TOC entry 344 (class 1259 OID 361394)
-- Name: sys_re_participant; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_re_participant OWNER TO "_tenant";

--
-- TOC entry 345 (class 1259 OID 361404)
-- Name: sys_re_pmchange; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_re_pmchange OWNER TO "_tenant";

--
-- TOC entry 346 (class 1259 OID 361411)
-- Name: sys_re_pmrisk; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_re_pmrisk OWNER TO "_tenant";

--
-- TOC entry 347 (class 1259 OID 361419)
-- Name: sys_re_process; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_re_process OWNER TO "_tenant";

--
-- TOC entry 348 (class 1259 OID 361430)
-- Name: sys_re_project; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_re_project OWNER TO "_tenant";

--
-- TOC entry 349 (class 1259 OID 361451)
-- Name: sys_re_reportdefinition; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_re_reportdefinition OWNER TO "_tenant";

--
-- TOC entry 351 (class 1259 OID 361459)
-- Name: sys_re_ruleset; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_re_ruleset OWNER TO "_tenant";

--
-- TOC entry 350 (class 1259 OID 361458)
-- Name: sys_re_ruleset_py_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
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
-- TOC entry 353 (class 1259 OID 361471)
-- Name: sys_re_stage; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_re_stage OWNER TO "_tenant";

--
-- TOC entry 352 (class 1259 OID 361470)
-- Name: sys_re_stage_stage_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
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
-- TOC entry 354 (class 1259 OID 361482)
-- Name: sys_re_strategy; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_re_strategy OWNER TO "_tenant";

--
-- TOC entry 355 (class 1259 OID 361491)
-- Name: sys_re_talkingplan; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_re_talkingplan OWNER TO "_tenant";

--
-- TOC entry 356 (class 1259 OID 361499)
-- Name: sys_re_template; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_re_template OWNER TO "_tenant";

--
-- TOC entry 357 (class 1259 OID 361507)
-- Name: sys_re_when; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_re_when OWNER TO "_tenant";

--
-- TOC entry 358 (class 1259 OID 361516)
-- Name: sys_re_workload; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_re_workload OWNER TO "_tenant";

--
-- TOC entry 359 (class 1259 OID 361524)
-- Name: sys_re_worktask; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_re_worktask OWNER TO "_tenant";

--
-- TOC entry 361 (class 1259 OID 361535)
-- Name: sys_role; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_role OWNER TO "_tenant";

--
-- TOC entry 362 (class 1259 OID 361546)
-- Name: sys_role_dept; Type: TABLE; Schema: public; Owner: _tenant
--

CREATE TABLE public.sys_role_dept (
    role_id bigint NOT NULL,
    dept_id bigint NOT NULL
);


ALTER TABLE public.sys_role_dept OWNER TO "_tenant";

--
-- TOC entry 363 (class 1259 OID 361551)
-- Name: sys_role_menu; Type: TABLE; Schema: public; Owner: _tenant
--

CREATE TABLE public.sys_role_menu (
    role_id bigint NOT NULL,
    menu_id bigint NOT NULL
);


ALTER TABLE public.sys_role_menu OWNER TO "_tenant";

--
-- TOC entry 360 (class 1259 OID 361534)
-- Name: sys_role_role_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
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
-- TOC entry 365 (class 1259 OID 361557)
-- Name: sys_ru_caseinstance; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_ru_caseinstance OWNER TO "_tenant";

--
-- TOC entry 364 (class 1259 OID 361556)
-- Name: sys_ru_caseinstance_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
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
-- TOC entry 367 (class 1259 OID 361566)
-- Name: sys_sequence_id; Type: TABLE; Schema: public; Owner: _tenant
--

CREATE TABLE public.sys_sequence_id (
    sequenceid bigint NOT NULL,
    prefix character varying(255) NOT NULL,
    current_number bigint
);


ALTER TABLE public.sys_sequence_id OWNER TO "_tenant";

--
-- TOC entry 366 (class 1259 OID 361565)
-- Name: sys_sequence_id_sequenceid_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
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
-- TOC entry 369 (class 1259 OID 361572)
-- Name: sys_task_form; Type: TABLE; Schema: public; Owner: _tenant
--

CREATE TABLE public.sys_task_form (
    id bigint NOT NULL,
    form_id bigint,
    task_id character varying(50)
);


ALTER TABLE public.sys_task_form OWNER TO "_tenant";

--
-- TOC entry 368 (class 1259 OID 361571)
-- Name: sys_task_form_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
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
-- TOC entry 371 (class 1259 OID 361578)
-- Name: sys_user; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.sys_user OWNER TO "_tenant";

--
-- TOC entry 8979 (class 0 OID 0)
-- Dependencies: 371
-- Name: COLUMN sys_user.sn_userid; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.sys_user.sn_userid IS '社交媒体用户ID（当前用于企业微信userid，可扩展至其他平台）';


--
-- TOC entry 372 (class 1259 OID 361592)
-- Name: sys_user_post; Type: TABLE; Schema: public; Owner: _tenant
--

CREATE TABLE public.sys_user_post (
    user_id bigint NOT NULL,
    post_id bigint NOT NULL
);


ALTER TABLE public.sys_user_post OWNER TO "_tenant";

--
-- TOC entry 373 (class 1259 OID 361597)
-- Name: sys_user_role; Type: TABLE; Schema: public; Owner: _tenant
--

CREATE TABLE public.sys_user_role (
    user_id bigint NOT NULL,
    role_id bigint NOT NULL
);


ALTER TABLE public.sys_user_role OWNER TO "_tenant";

--
-- TOC entry 370 (class 1259 OID 361577)
-- Name: sys_user_user_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
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
-- TOC entry 631 (class 1259 OID 413267)
-- Name: sys_webchat_token_tenant; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.sys_webchat_token_tenant (
    website_token character varying(64) NOT NULL,
    tenant character varying(64) NOT NULL
);


ALTER TABLE public.sys_webchat_token_tenant OWNER TO luohd;

--
-- TOC entry 8984 (class 0 OID 0)
-- Dependencies: 631
-- Name: TABLE sys_webchat_token_tenant; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.sys_webchat_token_tenant IS 'Web Chat website_token → tenant；公开 API 路由。一租户一行。';


--
-- TOC entry 449 (class 1259 OID 371684)
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


ALTER TABLE public.sys_webhook_path_index OWNER TO luohd;

--
-- TOC entry 375 (class 1259 OID 361603)
-- Name: table_table_industry; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.table_table_industry OWNER TO "_tenant";

--
-- TOC entry 374 (class 1259 OID 361602)
-- Name: table_table_industry_id_seq; Type: SEQUENCE; Schema: public; Owner: _tenant
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
-- TOC entry 430 (class 1259 OID 371182)
-- Name: w_connection; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.w_connection OWNER TO "_tenant";

--
-- TOC entry 428 (class 1259 OID 371168)
-- Name: w_credential; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.w_credential OWNER TO "_tenant";

--
-- TOC entry 8990 (class 0 OID 0)
-- Dependencies: 428
-- Name: COLUMN w_credential.is_primary; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.w_credential.is_primary IS '是否主凭证 0否 1是（同一 user_id + type 仅一条为 1）';


--
-- TOC entry 431 (class 1259 OID 371193)
-- Name: w_execution; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.w_execution OWNER TO "_tenant";

--
-- TOC entry 427 (class 1259 OID 371157)
-- Name: w_node; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.w_node OWNER TO "_tenant";

--
-- TOC entry 429 (class 1259 OID 371175)
-- Name: w_node_definition; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.w_node_definition OWNER TO "_tenant";

--
-- TOC entry 590 (class 1259 OID 411295)
-- Name: w_scheduled_task; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.w_scheduled_task (
    id character varying(64) NOT NULL,
    name character varying(200) NOT NULL,
    workflow_id character varying(64) NOT NULL,
    workflow_name character varying(200),
    schedule_type character varying(16) NOT NULL,
    run_time character varying(5) NOT NULL,
    days_of_week character varying(32),
    enabled character(1) DEFAULT '1'::bpchar NOT NULL,
    last_fired_slot character varying(32),
    create_by character varying(64),
    create_time timestamp without time zone,
    update_by character varying(64),
    update_time timestamp without time zone,
    remark character varying(500)
);


ALTER TABLE public.w_scheduled_task OWNER TO luohd;

--
-- TOC entry 8995 (class 0 OID 0)
-- Dependencies: 590
-- Name: TABLE w_scheduled_task; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.w_scheduled_task IS '工作流定时任务配置（租户内）';


--
-- TOC entry 8996 (class 0 OID 0)
-- Dependencies: 590
-- Name: COLUMN w_scheduled_task.schedule_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.w_scheduled_task.schedule_type IS 'daily | weekly';


--
-- TOC entry 8997 (class 0 OID 0)
-- Dependencies: 590
-- Name: COLUMN w_scheduled_task.run_time; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.w_scheduled_task.run_time IS 'HH:mm，分钟仅 00 或 30';


--
-- TOC entry 8998 (class 0 OID 0)
-- Dependencies: 590
-- Name: COLUMN w_scheduled_task.days_of_week; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.w_scheduled_task.days_of_week IS 'weekly: 逗号分隔 1-7（ISO，1=周一）；daily 为 NULL';


--
-- TOC entry 8999 (class 0 OID 0)
-- Dependencies: 590
-- Name: COLUMN w_scheduled_task.enabled; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.w_scheduled_task.enabled IS '1 启用 / 0 停用';


--
-- TOC entry 9000 (class 0 OID 0)
-- Dependencies: 590
-- Name: COLUMN w_scheduled_task.last_fired_slot; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.w_scheduled_task.last_fired_slot IS '幂等抢占标记；等于当前 slot 则本轮不再返回';


--
-- TOC entry 432 (class 1259 OID 371207)
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


ALTER TABLE public.w_wait_resume OWNER TO luohd;

--
-- TOC entry 9002 (class 0 OID 0)
-- Dependencies: 432
-- Name: TABLE w_wait_resume; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.w_wait_resume IS 'Wait 节点预注册与恢复状态';


--
-- TOC entry 9003 (class 0 OID 0)
-- Dependencies: 432
-- Name: COLUMN w_wait_resume.resume_key; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.w_wait_resume.resume_key IS '公开回调 key，唯一';


--
-- TOC entry 9004 (class 0 OID 0)
-- Dependencies: 432
-- Name: COLUMN w_wait_resume.mode; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.w_wait_resume.mode IS 'on_webhook_call | on_form_submitted';


--
-- TOC entry 426 (class 1259 OID 371150)
-- Name: w_workflow; Type: TABLE; Schema: public; Owner: _tenant
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


ALTER TABLE public.w_workflow OWNER TO "_tenant";

--
-- TOC entry 9006 (class 0 OID 0)
-- Dependencies: 426
-- Name: COLUMN w_workflow.publish_assistant; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.w_workflow.publish_assistant IS 'Derived: MANUAL_TRIGGER publish.assistant is complete';


--
-- TOC entry 9007 (class 0 OID 0)
-- Dependencies: 426
-- Name: COLUMN w_workflow.publish_sidebar; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.w_workflow.publish_sidebar IS 'Derived: MANUAL_TRIGGER publish.wecomSidebar is complete';


--
-- TOC entry 9008 (class 0 OID 0)
-- Dependencies: 426
-- Name: COLUMN w_workflow.is_template; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.w_workflow.is_template IS 'true=模板蓝图，false=可执行实例';


--
-- TOC entry 9009 (class 0 OID 0)
-- Dependencies: 426
-- Name: COLUMN w_workflow.description; Type: COMMENT; Schema: public; Owner: _tenant
--

COMMENT ON COLUMN public.w_workflow.description IS '流程使用场景说明（非发布渠道 description）';


--
-- TOC entry 5625 (class 2604 OID 369680)
-- Name: act_evt_log log_nr_; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_evt_log ALTER COLUMN log_nr_ SET DEFAULT nextval('public.act_evt_log_log_nr__seq'::regclass);


--
-- TOC entry 5633 (class 2604 OID 369681)
-- Name: act_hi_tsk_log id_; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_tsk_log ALTER COLUMN id_ SET DEFAULT nextval('public.act_hi_tsk_log_id__seq'::regclass);


--
-- TOC entry 5849 (class 2604 OID 404211)
-- Name: crm_activity_unmatched id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_activity_unmatched ALTER COLUMN id SET DEFAULT nextval('public.crm_activity_unmatched_id_seq'::regclass);


--
-- TOC entry 5781 (class 2604 OID 379546)
-- Name: crm_ai_playbook_run id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_playbook_run ALTER COLUMN id SET DEFAULT nextval('public.crm_ai_playbook_run_id_seq'::regclass);


--
-- TOC entry 5752 (class 2604 OID 379470)
-- Name: crm_ai_skill id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_skill ALTER COLUMN id SET DEFAULT nextval('public.crm_ai_skill_id_seq'::regclass);


--
-- TOC entry 5769 (class 2604 OID 379511)
-- Name: crm_ai_skill_playbook id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_skill_playbook ALTER COLUMN id SET DEFAULT nextval('public.crm_ai_skill_playbook_id_seq'::regclass);


--
-- TOC entry 5775 (class 2604 OID 379528)
-- Name: crm_ai_skill_playbook_step id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_skill_playbook_step ALTER COLUMN id SET DEFAULT nextval('public.crm_ai_skill_playbook_step_id_seq'::regclass);


--
-- TOC entry 5943 (class 2604 OID 404716)
-- Name: crm_bulkemail_provider_config id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_bulkemail_provider_config ALTER COLUMN id SET DEFAULT nextval('public.crm_bulkemail_provider_config_id_seq'::regclass);


--
-- TOC entry 6353 (class 2604 OID 417272)
-- Name: crm_contact_service_summary_revision id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_contact_service_summary_revision ALTER COLUMN id SET DEFAULT nextval('public.crm_contact_service_summary_revision_id_seq'::regclass);


--
-- TOC entry 5820 (class 2604 OID 404125)
-- Name: crm_customer_identity identity_id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_customer_identity ALTER COLUMN identity_id SET DEFAULT nextval('public.crm_customer_identity_identity_id_seq'::regclass);


--
-- TOC entry 6047 (class 2604 OID 408951)
-- Name: crm_dashboard_snapshot id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_dashboard_snapshot ALTER COLUMN id SET DEFAULT nextval('public.crm_dashboard_snapshot_id_seq'::regclass);


--
-- TOC entry 5902 (class 2604 OID 404485)
-- Name: crm_event_mp_outreach_log id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_event_mp_outreach_log ALTER COLUMN id SET DEFAULT nextval('public.crm_event_mp_outreach_log_id_seq'::regclass);


--
-- TOC entry 5925 (class 2604 OID 404599)
-- Name: crm_event_nurture_enrollment id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_event_nurture_enrollment ALTER COLUMN id SET DEFAULT nextval('public.crm_event_nurture_enrollment_id_seq'::regclass);


--
-- TOC entry 5914 (class 2604 OID 404563)
-- Name: crm_event_nurture_sequence id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_event_nurture_sequence ALTER COLUMN id SET DEFAULT nextval('public.crm_event_nurture_sequence_id_seq'::regclass);


--
-- TOC entry 5921 (class 2604 OID 404580)
-- Name: crm_event_nurture_step id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_event_nurture_step ALTER COLUMN id SET DEFAULT nextval('public.crm_event_nurture_step_id_seq'::regclass);


--
-- TOC entry 5929 (class 2604 OID 404615)
-- Name: crm_event_nurture_step_execution id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_event_nurture_step_execution ALTER COLUMN id SET DEFAULT nextval('public.crm_event_nurture_step_execution_id_seq'::regclass);


--
-- TOC entry 6065 (class 2604 OID 409056)
-- Name: crm_event_speaker id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_event_speaker ALTER COLUMN id SET DEFAULT nextval('public.crm_event_speaker_id_seq'::regclass);


--
-- TOC entry 5911 (class 2604 OID 404551)
-- Name: crm_event_spend_item id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_event_spend_item ALTER COLUMN id SET DEFAULT nextval('public.crm_event_spend_item_id_seq'::regclass);


--
-- TOC entry 6131 (class 2604 OID 411323)
-- Name: crm_feishu_config id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_feishu_config ALTER COLUMN id SET DEFAULT nextval('public.crm_feishu_config_id_seq'::regclass);


--
-- TOC entry 5957 (class 2604 OID 408160)
-- Name: crm_identity_conflict id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_identity_conflict ALTER COLUMN id SET DEFAULT nextval('public.crm_identity_conflict_id_seq'::regclass);


--
-- TOC entry 5785 (class 2604 OID 379605)
-- Name: crm_mcp_api_key id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_mcp_api_key ALTER COLUMN id SET DEFAULT nextval('public.crm_mcp_api_key_id_seq'::regclass);


--
-- TOC entry 5789 (class 2604 OID 379622)
-- Name: crm_mcp_audit_log id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_mcp_audit_log ALTER COLUMN id SET DEFAULT nextval('public.crm_mcp_audit_log_id_seq'::regclass);


--
-- TOC entry 5763 (class 2604 OID 379494)
-- Name: crm_mcp_server id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_mcp_server ALTER COLUMN id SET DEFAULT nextval('public.crm_mcp_server_id_seq'::regclass);


--
-- TOC entry 6254 (class 2604 OID 413140)
-- Name: crm_messenger_config id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_messenger_config ALTER COLUMN id SET DEFAULT nextval('public.crm_messenger_config_id_seq'::regclass);


--
-- TOC entry 6259 (class 2604 OID 413168)
-- Name: crm_messenger_conversation id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_messenger_conversation ALTER COLUMN id SET DEFAULT nextval('public.crm_messenger_conversation_id_seq'::regclass);


--
-- TOC entry 6263 (class 2604 OID 413186)
-- Name: crm_messenger_message id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_messenger_message ALTER COLUMN id SET DEFAULT nextval('public.crm_messenger_message_id_seq'::regclass);


--
-- TOC entry 6256 (class 2604 OID 413153)
-- Name: crm_messenger_webhook_event id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_messenger_webhook_event ALTER COLUMN id SET DEFAULT nextval('public.crm_messenger_webhook_event_id_seq'::regclass);


--
-- TOC entry 6303 (class 2604 OID 413403)
-- Name: crm_mobile_push_device id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_mobile_push_device ALTER COLUMN id SET DEFAULT nextval('public.crm_mobile_push_device_id_seq'::regclass);


--
-- TOC entry 6238 (class 2604 OID 413023)
-- Name: crm_reminder_link id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_reminder_link ALTER COLUMN id SET DEFAULT nextval('public.crm_reminder_link_id_seq'::regclass);


--
-- TOC entry 5859 (class 2604 OID 404325)
-- Name: crm_segment id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_segment ALTER COLUMN id SET DEFAULT nextval('public.crm_segment_id_seq'::regclass);


--
-- TOC entry 5720 (class 2604 OID 371451)
-- Name: crm_sf_express_config id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_sf_express_config ALTER COLUMN id SET DEFAULT nextval('public.crm_sf_express_config_id_seq'::regclass);


--
-- TOC entry 5728 (class 2604 OID 371469)
-- Name: crm_sf_express_token id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_sf_express_token ALTER COLUMN id SET DEFAULT nextval('public.crm_sf_express_token_id_seq'::regclass);


--
-- TOC entry 5810 (class 2604 OID 380104)
-- Name: crm_subject_map id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_subject_map ALTER COLUMN id SET DEFAULT nextval('public.crm_subject_map_id_seq'::regclass);


--
-- TOC entry 5940 (class 2604 OID 404696)
-- Name: crm_survey_outreach_log id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_survey_outreach_log ALTER COLUMN id SET DEFAULT nextval('public.crm_survey_outreach_log_id_seq'::regclass);


--
-- TOC entry 6098 (class 2604 OID 411118)
-- Name: crm_tag_channel_mapping id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_tag_channel_mapping ALTER COLUMN id SET DEFAULT nextval('public.crm_tag_channel_mapping_id_seq'::regclass);


--
-- TOC entry 6105 (class 2604 OID 411136)
-- Name: crm_tag_dispatch_task id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_tag_dispatch_task ALTER COLUMN id SET DEFAULT nextval('public.crm_tag_dispatch_task_id_seq'::regclass);


--
-- TOC entry 6265 (class 2604 OID 413204)
-- Name: crm_webchat_config id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_webchat_config ALTER COLUMN id SET DEFAULT nextval('public.crm_webchat_config_id_seq'::regclass);


--
-- TOC entry 6274 (class 2604 OID 413239)
-- Name: crm_webchat_conversation id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_webchat_conversation ALTER COLUMN id SET DEFAULT nextval('public.crm_webchat_conversation_id_seq'::regclass);


--
-- TOC entry 6278 (class 2604 OID 413256)
-- Name: crm_webchat_message id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_webchat_message ALTER COLUMN id SET DEFAULT nextval('public.crm_webchat_message_id_seq'::regclass);


--
-- TOC entry 6270 (class 2604 OID 413222)
-- Name: crm_webchat_visitor id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_webchat_visitor ALTER COLUMN id SET DEFAULT nextval('public.crm_webchat_visitor_id_seq'::regclass);


--
-- TOC entry 6127 (class 2604 OID 411308)
-- Name: crm_wechat_miniapp_config id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_miniapp_config ALTER COLUMN id SET DEFAULT nextval('public.crm_wechat_miniapp_config_id_seq'::regclass);


--
-- TOC entry 6006 (class 2604 OID 408737)
-- Name: crm_wechat_mp_article id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_article ALTER COLUMN id SET DEFAULT nextval('public.crm_wechat_mp_article_id_seq'::regclass);


--
-- TOC entry 6322 (class 2604 OID 413499)
-- Name: crm_wechat_mp_article_comment id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_article_comment ALTER COLUMN id SET DEFAULT nextval('public.crm_wechat_mp_article_comment_id_seq'::regclass);


--
-- TOC entry 6014 (class 2604 OID 408774)
-- Name: crm_wechat_mp_article_draft id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_article_draft ALTER COLUMN id SET DEFAULT nextval('public.crm_wechat_mp_article_draft_id_seq'::regclass);


--
-- TOC entry 6032 (class 2604 OID 408919)
-- Name: crm_wechat_mp_article_stat id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_article_stat ALTER COLUMN id SET DEFAULT nextval('public.crm_wechat_mp_article_stat_id_seq'::regclass);


--
-- TOC entry 5892 (class 2604 OID 404443)
-- Name: crm_wechat_mp_auto_reply id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_auto_reply ALTER COLUMN id SET DEFAULT nextval('public.crm_wechat_mp_auto_reply_id_seq'::regclass);


--
-- TOC entry 5846 (class 2604 OID 404199)
-- Name: crm_wechat_mp_callback_log id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_callback_log ALTER COLUMN id SET DEFAULT nextval('public.crm_wechat_mp_callback_log_id_seq'::regclass);


--
-- TOC entry 6331 (class 2604 OID 413521)
-- Name: crm_wechat_mp_comment_auto_reply_rule id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_comment_auto_reply_rule ALTER COLUMN id SET DEFAULT nextval('public.crm_wechat_mp_comment_auto_reply_rule_id_seq'::regclass);


--
-- TOC entry 5856 (class 2604 OID 404296)
-- Name: crm_wechat_mp_config id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_config ALTER COLUMN id SET DEFAULT nextval('public.crm_wechat_mp_config_id_seq'::regclass);


--
-- TOC entry 6244 (class 2604 OID 413068)
-- Name: crm_wechat_mp_conversation id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_conversation ALTER COLUMN id SET DEFAULT nextval('public.crm_wechat_mp_conversation_id_seq'::regclass);


--
-- TOC entry 5876 (class 2604 OID 404383)
-- Name: crm_wechat_mp_fan id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_fan ALTER COLUMN id SET DEFAULT nextval('public.crm_wechat_mp_fan_id_seq'::regclass);


--
-- TOC entry 6249 (class 2604 OID 413087)
-- Name: crm_wechat_mp_mass_task id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_mass_task ALTER COLUMN id SET DEFAULT nextval('public.crm_wechat_mp_mass_task_id_seq'::regclass);


--
-- TOC entry 6023 (class 2604 OID 408838)
-- Name: crm_wechat_mp_material id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_material ALTER COLUMN id SET DEFAULT nextval('public.crm_wechat_mp_material_id_seq'::regclass);


--
-- TOC entry 5864 (class 2604 OID 404343)
-- Name: crm_wechat_mp_menu id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_menu ALTER COLUMN id SET DEFAULT nextval('public.crm_wechat_mp_menu_id_seq'::regclass);


--
-- TOC entry 5869 (class 2604 OID 404357)
-- Name: crm_wechat_mp_menu_key_label id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_menu_key_label ALTER COLUMN id SET DEFAULT nextval('public.crm_wechat_mp_menu_key_label_id_seq'::regclass);


--
-- TOC entry 5880 (class 2604 OID 404399)
-- Name: crm_wechat_mp_message id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_message ALTER COLUMN id SET DEFAULT nextval('public.crm_wechat_mp_message_id_seq'::regclass);


--
-- TOC entry 5898 (class 2604 OID 404467)
-- Name: crm_wechat_mp_message_preset id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_message_preset ALTER COLUMN id SET DEFAULT nextval('public.crm_wechat_mp_message_preset_id_seq'::regclass);


--
-- TOC entry 5882 (class 2604 OID 404411)
-- Name: crm_wechat_mp_message_task id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_message_task ALTER COLUMN id SET DEFAULT nextval('public.crm_wechat_mp_message_task_id_seq'::regclass);


--
-- TOC entry 5890 (class 2604 OID 404427)
-- Name: crm_wechat_mp_message_task_item id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_message_task_item ALTER COLUMN id SET DEFAULT nextval('public.crm_wechat_mp_message_task_item_id_seq'::regclass);


--
-- TOC entry 6027 (class 2604 OID 408902)
-- Name: crm_wechat_mp_publish_task id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_publish_task ALTER COLUMN id SET DEFAULT nextval('public.crm_wechat_mp_publish_task_id_seq'::regclass);


--
-- TOC entry 5871 (class 2604 OID 404367)
-- Name: crm_wechat_mp_qrcode id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_qrcode ALTER COLUMN id SET DEFAULT nextval('public.crm_wechat_mp_qrcode_id_seq'::regclass);


--
-- TOC entry 5812 (class 2604 OID 380124)
-- Name: crm_wecom_aibot_config id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_aibot_config ALTER COLUMN id SET DEFAULT nextval('public.crm_wecom_aibot_config_id_seq'::regclass);


--
-- TOC entry 5816 (class 2604 OID 380138)
-- Name: crm_wecom_aibot_connection id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_aibot_connection ALTER COLUMN id SET DEFAULT nextval('public.crm_wecom_aibot_connection_id_seq'::regclass);


--
-- TOC entry 5962 (class 2604 OID 408177)
-- Name: crm_wecom_behavior_daily id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_behavior_daily ALTER COLUMN id SET DEFAULT nextval('public.crm_wecom_behavior_daily_id_seq'::regclass);


--
-- TOC entry 5807 (class 2604 OID 380076)
-- Name: crm_wecom_callback_event id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_callback_event ALTER COLUMN id SET DEFAULT nextval('public.crm_wecom_callback_event_id_seq'::regclass);


--
-- TOC entry 5474 (class 2604 OID 371437)
-- Name: crm_wecom_config id; Type: DEFAULT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_wecom_config ALTER COLUMN id SET DEFAULT nextval('public.crm_wecom_config_id_seq'::regclass);


--
-- TOC entry 5478 (class 2604 OID 371439)
-- Name: crm_wecom_contact_way id; Type: DEFAULT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_wecom_contact_way ALTER COLUMN id SET DEFAULT nextval('public.crm_wecom_contact_way_id_seq'::regclass);


--
-- TOC entry 5481 (class 2604 OID 371441)
-- Name: crm_wecom_external_contact id; Type: DEFAULT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_wecom_external_contact ALTER COLUMN id SET DEFAULT nextval('public.crm_wecom_external_contact_id_seq'::regclass);


--
-- TOC entry 5970 (class 2604 OID 408196)
-- Name: crm_wecom_groupchat_daily id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_groupchat_daily ALTER COLUMN id SET DEFAULT nextval('public.crm_wecom_groupchat_daily_id_seq'::regclass);


--
-- TOC entry 5794 (class 2604 OID 379685)
-- Name: crm_wecom_kf_config id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_kf_config ALTER COLUMN id SET DEFAULT nextval('public.crm_wecom_kf_config_id_seq'::regclass);


--
-- TOC entry 6338 (class 2604 OID 413545)
-- Name: crm_wecom_kf_conversation id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_kf_conversation ALTER COLUMN id SET DEFAULT nextval('public.crm_wecom_kf_conversation_id_seq'::regclass);


--
-- TOC entry 5798 (class 2604 OID 379741)
-- Name: crm_wecom_kf_message id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_kf_message ALTER COLUMN id SET DEFAULT nextval('public.crm_wecom_kf_message_id_seq'::regclass);


--
-- TOC entry 6344 (class 2604 OID 413566)
-- Name: crm_wecom_kf_sync_cursor id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_kf_sync_cursor ALTER COLUMN id SET DEFAULT nextval('public.crm_wecom_kf_sync_cursor_id_seq'::regclass);


--
-- TOC entry 5487 (class 2604 OID 371443)
-- Name: crm_wecom_mass_task id; Type: DEFAULT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_wecom_mass_task ALTER COLUMN id SET DEFAULT nextval('public.crm_wecom_mass_task_id_seq'::regclass);


--
-- TOC entry 5802 (class 2604 OID 380022)
-- Name: crm_wecom_material id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_material ALTER COLUMN id SET DEFAULT nextval('public.crm_wecom_material_id_seq'::regclass);


--
-- TOC entry 5712 (class 2604 OID 371391)
-- Name: crm_wecom_welcome_log id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_welcome_log ALTER COLUMN id SET DEFAULT nextval('public.crm_wecom_welcome_log_id_seq'::regclass);


--
-- TOC entry 6233 (class 2604 OID 413009)
-- Name: crm_whatsapp_automation_rule id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_whatsapp_automation_rule ALTER COLUMN id SET DEFAULT nextval('public.crm_whatsapp_automation_rule_id_seq'::regclass);


--
-- TOC entry 6051 (class 2604 OID 408975)
-- Name: crm_whatsapp_config id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_whatsapp_config ALTER COLUMN id SET DEFAULT nextval('public.crm_whatsapp_config_id_seq'::regclass);


--
-- TOC entry 6056 (class 2604 OID 409003)
-- Name: crm_whatsapp_conversation id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_whatsapp_conversation ALTER COLUMN id SET DEFAULT nextval('public.crm_whatsapp_conversation_id_seq'::regclass);


--
-- TOC entry 6060 (class 2604 OID 409021)
-- Name: crm_whatsapp_message id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_whatsapp_message ALTER COLUMN id SET DEFAULT nextval('public.crm_whatsapp_message_id_seq'::regclass);


--
-- TOC entry 6062 (class 2604 OID 409034)
-- Name: crm_whatsapp_template id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_whatsapp_template ALTER COLUMN id SET DEFAULT nextval('public.crm_whatsapp_template_id_seq'::regclass);


--
-- TOC entry 6229 (class 2604 OID 412989)
-- Name: crm_whatsapp_template_send_item id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_whatsapp_template_send_item ALTER COLUMN id SET DEFAULT nextval('public.crm_whatsapp_template_send_item_id_seq'::regclass);


--
-- TOC entry 6221 (class 2604 OID 412971)
-- Name: crm_whatsapp_template_send_task id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_whatsapp_template_send_task ALTER COLUMN id SET DEFAULT nextval('public.crm_whatsapp_template_send_task_id_seq'::regclass);


--
-- TOC entry 6053 (class 2604 OID 408988)
-- Name: crm_whatsapp_webhook_event id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_whatsapp_webhook_event ALTER COLUMN id SET DEFAULT nextval('public.crm_whatsapp_webhook_event_id_seq'::regclass);


--
-- TOC entry 6003 (class 2604 OID 408723)
-- Name: smart_push_execution_log id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.smart_push_execution_log ALTER COLUMN id SET DEFAULT nextval('public.smart_push_execution_log_id_seq'::regclass);


--
-- TOC entry 5999 (class 2604 OID 408711)
-- Name: smart_push_strategy id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.smart_push_strategy ALTER COLUMN id SET DEFAULT nextval('public.smart_push_strategy_id_seq'::regclass);


--
-- TOC entry 6673 (class 2606 OID 369683)
-- Name: act_evt_log act_evt_log_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_evt_log
    ADD CONSTRAINT act_evt_log_pkey PRIMARY KEY (log_nr_);


--
-- TOC entry 6675 (class 2606 OID 369685)
-- Name: act_ge_bytearray act_ge_bytearray_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ge_bytearray
    ADD CONSTRAINT act_ge_bytearray_pkey PRIMARY KEY (id_);


--
-- TOC entry 6678 (class 2606 OID 369687)
-- Name: act_ge_property act_ge_property_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ge_property
    ADD CONSTRAINT act_ge_property_pkey PRIMARY KEY (name_);


--
-- TOC entry 6680 (class 2606 OID 369689)
-- Name: act_hi_actinst act_hi_actinst_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_actinst
    ADD CONSTRAINT act_hi_actinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 6686 (class 2606 OID 369691)
-- Name: act_hi_attachment act_hi_attachment_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_attachment
    ADD CONSTRAINT act_hi_attachment_pkey PRIMARY KEY (id_);


--
-- TOC entry 6688 (class 2606 OID 369693)
-- Name: act_hi_comment act_hi_comment_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_comment
    ADD CONSTRAINT act_hi_comment_pkey PRIMARY KEY (id_);


--
-- TOC entry 6690 (class 2606 OID 369695)
-- Name: act_hi_detail act_hi_detail_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_detail
    ADD CONSTRAINT act_hi_detail_pkey PRIMARY KEY (id_);


--
-- TOC entry 6697 (class 2606 OID 369697)
-- Name: act_hi_entitylink act_hi_entitylink_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_entitylink
    ADD CONSTRAINT act_hi_entitylink_pkey PRIMARY KEY (id_);


--
-- TOC entry 6703 (class 2606 OID 369699)
-- Name: act_hi_identitylink act_hi_identitylink_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_identitylink
    ADD CONSTRAINT act_hi_identitylink_pkey PRIMARY KEY (id_);


--
-- TOC entry 6711 (class 2606 OID 369701)
-- Name: act_hi_procinst act_hi_procinst_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_procinst
    ADD CONSTRAINT act_hi_procinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 6713 (class 2606 OID 369703)
-- Name: act_hi_procinst act_hi_procinst_proc_inst_id__key; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_procinst
    ADD CONSTRAINT act_hi_procinst_proc_inst_id__key UNIQUE (proc_inst_id_);


--
-- TOC entry 6717 (class 2606 OID 369705)
-- Name: act_hi_taskinst act_hi_taskinst_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_taskinst
    ADD CONSTRAINT act_hi_taskinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 6723 (class 2606 OID 369707)
-- Name: act_hi_tsk_log act_hi_tsk_log_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_tsk_log
    ADD CONSTRAINT act_hi_tsk_log_pkey PRIMARY KEY (id_);


--
-- TOC entry 6725 (class 2606 OID 369709)
-- Name: act_hi_varinst act_hi_varinst_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_varinst
    ADD CONSTRAINT act_hi_varinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 6733 (class 2606 OID 369711)
-- Name: act_id_bytearray act_id_bytearray_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_bytearray
    ADD CONSTRAINT act_id_bytearray_pkey PRIMARY KEY (id_);


--
-- TOC entry 6735 (class 2606 OID 369713)
-- Name: act_id_group act_id_group_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_group
    ADD CONSTRAINT act_id_group_pkey PRIMARY KEY (id_);


--
-- TOC entry 6737 (class 2606 OID 369715)
-- Name: act_id_info act_id_info_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_info
    ADD CONSTRAINT act_id_info_pkey PRIMARY KEY (id_);


--
-- TOC entry 6739 (class 2606 OID 369717)
-- Name: act_id_membership act_id_membership_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_membership
    ADD CONSTRAINT act_id_membership_pkey PRIMARY KEY (user_id_, group_id_);


--
-- TOC entry 6747 (class 2606 OID 369719)
-- Name: act_id_priv_mapping act_id_priv_mapping_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_priv_mapping
    ADD CONSTRAINT act_id_priv_mapping_pkey PRIMARY KEY (id_);


--
-- TOC entry 6743 (class 2606 OID 369721)
-- Name: act_id_priv act_id_priv_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_priv
    ADD CONSTRAINT act_id_priv_pkey PRIMARY KEY (id_);


--
-- TOC entry 6752 (class 2606 OID 369723)
-- Name: act_id_property act_id_property_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_property
    ADD CONSTRAINT act_id_property_pkey PRIMARY KEY (name_);


--
-- TOC entry 6754 (class 2606 OID 369725)
-- Name: act_id_token act_id_token_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_token
    ADD CONSTRAINT act_id_token_pkey PRIMARY KEY (id_);


--
-- TOC entry 6756 (class 2606 OID 369727)
-- Name: act_id_user act_id_user_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_user
    ADD CONSTRAINT act_id_user_pkey PRIMARY KEY (id_);


--
-- TOC entry 6760 (class 2606 OID 369729)
-- Name: act_procdef_info act_procdef_info_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_procdef_info
    ADD CONSTRAINT act_procdef_info_pkey PRIMARY KEY (id_);


--
-- TOC entry 6764 (class 2606 OID 369731)
-- Name: act_re_deployment act_re_deployment_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_re_deployment
    ADD CONSTRAINT act_re_deployment_pkey PRIMARY KEY (id_);


--
-- TOC entry 6769 (class 2606 OID 369733)
-- Name: act_re_model act_re_model_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_re_model
    ADD CONSTRAINT act_re_model_pkey PRIMARY KEY (id_);


--
-- TOC entry 6771 (class 2606 OID 369735)
-- Name: act_re_procdef act_re_procdef_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_re_procdef
    ADD CONSTRAINT act_re_procdef_pkey PRIMARY KEY (id_);


--
-- TOC entry 6782 (class 2606 OID 369737)
-- Name: act_ru_actinst act_ru_actinst_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_actinst
    ADD CONSTRAINT act_ru_actinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 6793 (class 2606 OID 369739)
-- Name: act_ru_deadletter_job act_ru_deadletter_job_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_deadletter_job
    ADD CONSTRAINT act_ru_deadletter_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 6799 (class 2606 OID 369741)
-- Name: act_ru_entitylink act_ru_entitylink_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_entitylink
    ADD CONSTRAINT act_ru_entitylink_pkey PRIMARY KEY (id_);


--
-- TOC entry 6803 (class 2606 OID 369743)
-- Name: act_ru_event_subscr act_ru_event_subscr_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_event_subscr
    ADD CONSTRAINT act_ru_event_subscr_pkey PRIMARY KEY (id_);


--
-- TOC entry 6812 (class 2606 OID 369745)
-- Name: act_ru_execution act_ru_execution_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_execution
    ADD CONSTRAINT act_ru_execution_pkey PRIMARY KEY (id_);


--
-- TOC entry 6820 (class 2606 OID 369747)
-- Name: act_ru_external_job act_ru_external_job_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_external_job
    ADD CONSTRAINT act_ru_external_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 6822 (class 2606 OID 369749)
-- Name: act_ru_history_job act_ru_history_job_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_history_job
    ADD CONSTRAINT act_ru_history_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 6832 (class 2606 OID 369751)
-- Name: act_ru_identitylink act_ru_identitylink_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_identitylink
    ADD CONSTRAINT act_ru_identitylink_pkey PRIMARY KEY (id_);


--
-- TOC entry 6843 (class 2606 OID 369753)
-- Name: act_ru_job act_ru_job_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_job
    ADD CONSTRAINT act_ru_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 6854 (class 2606 OID 369755)
-- Name: act_ru_suspended_job act_ru_suspended_job_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_suspended_job
    ADD CONSTRAINT act_ru_suspended_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 6863 (class 2606 OID 369757)
-- Name: act_ru_task act_ru_task_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_task
    ADD CONSTRAINT act_ru_task_pkey PRIMARY KEY (id_);


--
-- TOC entry 6875 (class 2606 OID 369759)
-- Name: act_ru_timer_job act_ru_timer_job_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_timer_job
    ADD CONSTRAINT act_ru_timer_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 6883 (class 2606 OID 369761)
-- Name: act_ru_variable act_ru_variable_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_variable
    ADD CONSTRAINT act_ru_variable_pkey PRIMARY KEY (id_);


--
-- TOC entry 6762 (class 2606 OID 369763)
-- Name: act_procdef_info act_uniq_info_procdef; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_procdef_info
    ADD CONSTRAINT act_uniq_info_procdef UNIQUE (proc_def_id_);


--
-- TOC entry 6745 (class 2606 OID 369765)
-- Name: act_id_priv act_uniq_priv_name; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_priv
    ADD CONSTRAINT act_uniq_priv_name UNIQUE (name_);


--
-- TOC entry 6773 (class 2606 OID 369767)
-- Name: act_re_procdef act_uniq_procdef; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_re_procdef
    ADD CONSTRAINT act_uniq_procdef UNIQUE (key_, version_, derived_version_, tenant_id_);


--
-- TOC entry 6363 (class 2606 OID 360286)
-- Name: crm_account crm_account_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_account
    ADD CONSTRAINT crm_account_pkey PRIMARY KEY (account_id);


--
-- TOC entry 6941 (class 2606 OID 371328)
-- Name: crm_account_value crm_account_value_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_account_value
    ADD CONSTRAINT crm_account_value_pkey PRIMARY KEY (account_id);


--
-- TOC entry 6365 (class 2606 OID 360297)
-- Name: crm_accountrelation crm_accountrelation_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_accountrelation
    ADD CONSTRAINT crm_accountrelation_pkey PRIMARY KEY (relate_id);


--
-- TOC entry 6367 (class 2606 OID 360305)
-- Name: crm_activity crm_activity_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_activity
    ADD CONSTRAINT crm_activity_pkey PRIMARY KEY (activity_id);


--
-- TOC entry 7089 (class 2606 OID 404220)
-- Name: crm_activity_unmatched crm_activity_unmatched_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_activity_unmatched
    ADD CONSTRAINT crm_activity_unmatched_pkey PRIMARY KEY (id);


--
-- TOC entry 6388 (class 2606 OID 360313)
-- Name: crm_actrelate crm_actrelate_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_actrelate
    ADD CONSTRAINT crm_actrelate_pkey PRIMARY KEY (relate_id);


--
-- TOC entry 6390 (class 2606 OID 360324)
-- Name: crm_agent_tool crm_agent_tool_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_agent_tool
    ADD CONSTRAINT crm_agent_tool_pkey PRIMARY KEY (id);


--
-- TOC entry 6997 (class 2606 OID 371826)
-- Name: crm_ai_chat_message crm_ai_chat_message_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_chat_message
    ADD CONSTRAINT crm_ai_chat_message_pkey PRIMARY KEY (message_id);


--
-- TOC entry 6994 (class 2606 OID 371817)
-- Name: crm_ai_chat_session crm_ai_chat_session_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_chat_session
    ADD CONSTRAINT crm_ai_chat_session_pkey PRIMARY KEY (session_id);


--
-- TOC entry 6980 (class 2606 OID 371749)
-- Name: crm_ai_model_config crm_ai_model_config_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_model_config
    ADD CONSTRAINT crm_ai_model_config_pkey PRIMARY KEY (config_id);


--
-- TOC entry 7026 (class 2606 OID 379553)
-- Name: crm_ai_playbook_run crm_ai_playbook_run_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_playbook_run
    ADD CONSTRAINT crm_ai_playbook_run_pkey PRIMARY KEY (id);


--
-- TOC entry 7003 (class 2606 OID 379484)
-- Name: crm_ai_skill crm_ai_skill_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_skill
    ADD CONSTRAINT crm_ai_skill_pkey PRIMARY KEY (id);


--
-- TOC entry 7015 (class 2606 OID 379520)
-- Name: crm_ai_skill_playbook crm_ai_skill_playbook_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_skill_playbook
    ADD CONSTRAINT crm_ai_skill_playbook_pkey PRIMARY KEY (id);


--
-- TOC entry 7020 (class 2606 OID 379537)
-- Name: crm_ai_skill_playbook_step crm_ai_skill_playbook_step_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_skill_playbook_step
    ADD CONSTRAINT crm_ai_skill_playbook_step_pkey PRIMARY KEY (id);


--
-- TOC entry 7000 (class 2606 OID 371840)
-- Name: crm_analytics_query_log crm_analytics_query_log_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_analytics_query_log
    ADD CONSTRAINT crm_analytics_query_log_pkey PRIMARY KEY (log_id);


--
-- TOC entry 6392 (class 2606 OID 360338)
-- Name: crm_appointment crm_appointment_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_appointment
    ADD CONSTRAINT crm_appointment_pkey PRIMARY KEY (appointment_id);


--
-- TOC entry 6394 (class 2606 OID 360345)
-- Name: crm_attendee crm_attendee_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_attendee
    ADD CONSTRAINT crm_attendee_pkey PRIMARY KEY (attendee_id);


--
-- TOC entry 7531 (class 2606 OID 413420)
-- Name: crm_booking crm_booking_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_booking
    ADD CONSTRAINT crm_booking_pkey PRIMARY KEY (booking_id);


--
-- TOC entry 6396 (class 2606 OID 360356)
-- Name: crm_bulkemail crm_bulkemail_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_bulkemail
    ADD CONSTRAINT crm_bulkemail_pkey PRIMARY KEY (bulk_id);


--
-- TOC entry 7188 (class 2606 OID 404724)
-- Name: crm_bulkemail_provider_config crm_bulkemail_provider_config_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_bulkemail_provider_config
    ADD CONSTRAINT crm_bulkemail_provider_config_pkey PRIMARY KEY (id);


--
-- TOC entry 6399 (class 2606 OID 360365)
-- Name: crm_campaign_contacts crm_campaign_contacts_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_campaign_contacts
    ADD CONSTRAINT crm_campaign_contacts_pkey PRIMARY KEY (id);


--
-- TOC entry 7314 (class 2606 OID 409265)
-- Name: crm_capture crm_capture_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_capture
    ADD CONSTRAINT crm_capture_pkey PRIMARY KEY (capture_id);


--
-- TOC entry 6401 (class 2606 OID 360373)
-- Name: crm_cashflow crm_cashflow_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_cashflow
    ADD CONSTRAINT crm_cashflow_pkey PRIMARY KEY (cashflow_id);


--
-- TOC entry 7374 (class 2606 OID 411717)
-- Name: crm_chinese_enterprise crm_chinese_enterprise_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_chinese_enterprise
    ADD CONSTRAINT crm_chinese_enterprise_pkey PRIMARY KEY (id);


--
-- TOC entry 6403 (class 2606 OID 360381)
-- Name: crm_collect crm_collect_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_collect
    ADD CONSTRAINT crm_collect_pkey PRIMARY KEY (collect_id);


--
-- TOC entry 6405 (class 2606 OID 360390)
-- Name: crm_competitor crm_competitor_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_competitor
    ADD CONSTRAINT crm_competitor_pkey PRIMARY KEY (competitor_id);


--
-- TOC entry 6407 (class 2606 OID 360411)
-- Name: crm_contact crm_contact_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_contact
    ADD CONSTRAINT crm_contact_pkey PRIMARY KEY (contact_id);


--
-- TOC entry 7581 (class 2606 OID 417266)
-- Name: crm_contact_service_summary crm_contact_service_summary_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_contact_service_summary
    ADD CONSTRAINT crm_contact_service_summary_pkey PRIMARY KEY (tenant_id, contact_id);


--
-- TOC entry 7584 (class 2606 OID 417277)
-- Name: crm_contact_service_summary_revision crm_contact_service_summary_revision_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_contact_service_summary_revision
    ADD CONSTRAINT crm_contact_service_summary_revision_pkey PRIMARY KEY (id);


--
-- TOC entry 6414 (class 2606 OID 360419)
-- Name: crm_contactmethod crm_contactmethod_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_contactmethod
    ADD CONSTRAINT crm_contactmethod_pkey PRIMARY KEY (method_id);


--
-- TOC entry 6416 (class 2606 OID 360427)
-- Name: crm_contactrelation crm_contactrelation_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_contactrelation
    ADD CONSTRAINT crm_contactrelation_pkey PRIMARY KEY (relate_id);


--
-- TOC entry 6418 (class 2606 OID 360452)
-- Name: crm_contract crm_contract_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_contract
    ADD CONSTRAINT crm_contract_pkey PRIMARY KEY (contract_id);


--
-- TOC entry 7587 (class 2606 OID 417286)
-- Name: crm_conversation_summary_draft crm_conversation_summary_draft_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_conversation_summary_draft
    ADD CONSTRAINT crm_conversation_summary_draft_pkey PRIMARY KEY (tenant_id, channel, conversation_key);


--
-- TOC entry 6420 (class 2606 OID 360459)
-- Name: crm_conversion_history crm_conversion_history_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_conversion_history
    ADD CONSTRAINT crm_conversion_history_pkey PRIMARY KEY (id);


--
-- TOC entry 7075 (class 2606 OID 404134)
-- Name: crm_customer_identity crm_customer_identity_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_customer_identity
    ADD CONSTRAINT crm_customer_identity_pkey PRIMARY KEY (identity_id);


--
-- TOC entry 7256 (class 2606 OID 408958)
-- Name: crm_dashboard_snapshot crm_dashboard_snapshot_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_dashboard_snapshot
    ADD CONSTRAINT crm_dashboard_snapshot_pkey PRIMARY KEY (id);


--
-- TOC entry 6963 (class 2606 OID 371625)
-- Name: crm_data_table_column crm_data_table_column_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_data_table_column
    ADD CONSTRAINT crm_data_table_column_pkey PRIMARY KEY (id);


--
-- TOC entry 6958 (class 2606 OID 371613)
-- Name: crm_data_table crm_data_table_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_data_table
    ADD CONSTRAINT crm_data_table_pkey PRIMARY KEY (id);


--
-- TOC entry 6967 (class 2606 OID 371642)
-- Name: crm_data_table_row crm_data_table_row_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_data_table_row
    ADD CONSTRAINT crm_data_table_row_pkey PRIMARY KEY (id);


--
-- TOC entry 6422 (class 2606 OID 360470)
-- Name: crm_dd crm_dd_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_dd
    ADD CONSTRAINT crm_dd_pkey PRIMARY KEY (dd_id);


--
-- TOC entry 6424 (class 2606 OID 360477)
-- Name: crm_docrelation crm_docrelation_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_docrelation
    ADD CONSTRAINT crm_docrelation_pkey PRIMARY KEY (relate_id);


--
-- TOC entry 6985 (class 2606 OID 371790)
-- Name: crm_email_analysis crm_email_analysis_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_email_analysis
    ADD CONSTRAINT crm_email_analysis_pkey PRIMARY KEY (analysis_id);


--
-- TOC entry 6426 (class 2606 OID 360489)
-- Name: crm_email_attachments crm_email_attachments_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_email_attachments
    ADD CONSTRAINT crm_email_attachments_pkey PRIMARY KEY (id);


--
-- TOC entry 6428 (class 2606 OID 360501)
-- Name: crm_email_messages crm_email_messages_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_email_messages
    ADD CONSTRAINT crm_email_messages_pkey PRIMARY KEY (id);


--
-- TOC entry 7179 (class 2606 OID 404678)
-- Name: crm_event_check_in crm_event_check_in_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_event_check_in
    ADD CONSTRAINT crm_event_check_in_pkey PRIMARY KEY (check_in_id);


--
-- TOC entry 7137 (class 2606 OID 404492)
-- Name: crm_event_mp_outreach_log crm_event_mp_outreach_log_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_event_mp_outreach_log
    ADD CONSTRAINT crm_event_mp_outreach_log_pkey PRIMARY KEY (id);


--
-- TOC entry 7156 (class 2606 OID 404606)
-- Name: crm_event_nurture_enrollment crm_event_nurture_enrollment_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_event_nurture_enrollment
    ADD CONSTRAINT crm_event_nurture_enrollment_pkey PRIMARY KEY (id);


--
-- TOC entry 7158 (class 2606 OID 404608)
-- Name: crm_event_nurture_enrollment crm_event_nurture_enrollment_tenant_id_sequence_id_audience_key; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_event_nurture_enrollment
    ADD CONSTRAINT crm_event_nurture_enrollment_tenant_id_sequence_id_audience_key UNIQUE (tenant_id, sequence_id, audience_key);


--
-- TOC entry 7148 (class 2606 OID 404573)
-- Name: crm_event_nurture_sequence crm_event_nurture_sequence_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_event_nurture_sequence
    ADD CONSTRAINT crm_event_nurture_sequence_pkey PRIMARY KEY (id);


--
-- TOC entry 7162 (class 2606 OID 404622)
-- Name: crm_event_nurture_step_execution crm_event_nurture_step_execution_enrollment_id_step_id_key; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_event_nurture_step_execution
    ADD CONSTRAINT crm_event_nurture_step_execution_enrollment_id_step_id_key UNIQUE (enrollment_id, step_id);


--
-- TOC entry 7164 (class 2606 OID 404620)
-- Name: crm_event_nurture_step_execution crm_event_nurture_step_execution_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_event_nurture_step_execution
    ADD CONSTRAINT crm_event_nurture_step_execution_pkey PRIMARY KEY (id);


--
-- TOC entry 7152 (class 2606 OID 404587)
-- Name: crm_event_nurture_step crm_event_nurture_step_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_event_nurture_step
    ADD CONSTRAINT crm_event_nurture_step_pkey PRIMARY KEY (id);


--
-- TOC entry 7154 (class 2606 OID 404589)
-- Name: crm_event_nurture_step crm_event_nurture_step_sequence_id_step_order_key; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_event_nurture_step
    ADD CONSTRAINT crm_event_nurture_step_sequence_id_step_order_key UNIQUE (sequence_id, step_order);


--
-- TOC entry 7143 (class 2606 OID 404546)
-- Name: crm_event_performance_snapshot crm_event_performance_snapshot_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_event_performance_snapshot
    ADD CONSTRAINT crm_event_performance_snapshot_pkey PRIMARY KEY (tenant_id, event_id);


--
-- TOC entry 6430 (class 2606 OID 360514)
-- Name: crm_event crm_event_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_event
    ADD CONSTRAINT crm_event_pkey PRIMARY KEY (event_id);


--
-- TOC entry 7171 (class 2606 OID 404655)
-- Name: crm_event_registration crm_event_registration_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_event_registration
    ADD CONSTRAINT crm_event_registration_pkey PRIMARY KEY (registration_id);


--
-- TOC entry 7167 (class 2606 OID 404637)
-- Name: crm_event_session crm_event_session_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_event_session
    ADD CONSTRAINT crm_event_session_pkey PRIMARY KEY (session_id);


--
-- TOC entry 7291 (class 2606 OID 409062)
-- Name: crm_event_speaker crm_event_speaker_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_event_speaker
    ADD CONSTRAINT crm_event_speaker_pkey PRIMARY KEY (id);


--
-- TOC entry 7145 (class 2606 OID 404557)
-- Name: crm_event_spend_item crm_event_spend_item_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_event_spend_item
    ADD CONSTRAINT crm_event_spend_item_pkey PRIMARY KEY (id);


--
-- TOC entry 7355 (class 2606 OID 411330)
-- Name: crm_feishu_config crm_feishu_config_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_feishu_config
    ADD CONSTRAINT crm_feishu_config_pkey PRIMARY KEY (id);


--
-- TOC entry 6991 (class 2606 OID 371808)
-- Name: crm_follow_up_cadence crm_follow_up_cadence_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_follow_up_cadence
    ADD CONSTRAINT crm_follow_up_cadence_pkey PRIMARY KEY (cadence_id);


--
-- TOC entry 7498 (class 2606 OID 413323)
-- Name: crm_followup_rule_enrollment crm_followup_rule_enrollment_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_followup_rule_enrollment
    ADD CONSTRAINT crm_followup_rule_enrollment_pkey PRIMARY KEY (enrollment_id);


--
-- TOC entry 7504 (class 2606 OID 413334)
-- Name: crm_followup_rule_execution_log crm_followup_rule_execution_log_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_followup_rule_execution_log
    ADD CONSTRAINT crm_followup_rule_execution_log_pkey PRIMARY KEY (execution_id);


--
-- TOC entry 7495 (class 2606 OID 413313)
-- Name: crm_followup_rule crm_followup_rule_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_followup_rule
    ADD CONSTRAINT crm_followup_rule_pkey PRIMARY KEY (rule_id);


--
-- TOC entry 6433 (class 2606 OID 360530)
-- Name: crm_forecast crm_forecast_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_forecast
    ADD CONSTRAINT crm_forecast_pkey PRIMARY KEY (forecast_id);


--
-- TOC entry 6435 (class 2606 OID 360543)
-- Name: crm_fundinvestor crm_fundinvestor_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_fundinvestor
    ADD CONSTRAINT crm_fundinvestor_pkey PRIMARY KEY (investor_id);


--
-- TOC entry 6437 (class 2606 OID 360554)
-- Name: crm_fundpaid crm_fundpaid_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_fundpaid
    ADD CONSTRAINT crm_fundpaid_pkey PRIMARY KEY (paid_id);


--
-- TOC entry 7383 (class 2606 OID 411736)
-- Name: crm_government_agency crm_government_agency_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_government_agency
    ADD CONSTRAINT crm_government_agency_pkey PRIMARY KEY (id);


--
-- TOC entry 6439 (class 2606 OID 360562)
-- Name: crm_household crm_household_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_household
    ADD CONSTRAINT crm_household_pkey PRIMARY KEY (household_id);


--
-- TOC entry 6441 (class 2606 OID 360570)
-- Name: crm_householdmember crm_householdmember_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_householdmember
    ADD CONSTRAINT crm_householdmember_pkey PRIMARY KEY (member_id);


--
-- TOC entry 7199 (class 2606 OID 408168)
-- Name: crm_identity_conflict crm_identity_conflict_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_identity_conflict
    ADD CONSTRAINT crm_identity_conflict_pkey PRIMARY KEY (id);


--
-- TOC entry 7538 (class 2606 OID 413434)
-- Name: crm_inbox_channel_settings crm_inbox_channel_settings_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_inbox_channel_settings
    ADD CONSTRAINT crm_inbox_channel_settings_pkey PRIMARY KEY (tenant_id);


--
-- TOC entry 7359 (class 2606 OID 411483)
-- Name: crm_industrial_park crm_industrial_park_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_industrial_park
    ADD CONSTRAINT crm_industrial_park_pkey PRIMARY KEY (id);


--
-- TOC entry 6443 (class 2606 OID 360585)
-- Name: crm_interest crm_interest_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_interest
    ADD CONSTRAINT crm_interest_pkey PRIMARY KEY (interest_id);


--
-- TOC entry 6445 (class 2606 OID 360593)
-- Name: crm_itinerary crm_itinerary_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_itinerary
    ADD CONSTRAINT crm_itinerary_pkey PRIMARY KEY (itin_id);


--
-- TOC entry 7325 (class 2606 OID 410958)
-- Name: crm_kb_chunk crm_kb_chunk_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_kb_chunk
    ADD CONSTRAINT crm_kb_chunk_pkey PRIMARY KEY (chunk_id);


--
-- TOC entry 7320 (class 2606 OID 410945)
-- Name: crm_kb_document crm_kb_document_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_kb_document
    ADD CONSTRAINT crm_kb_document_pkey PRIMARY KEY (document_id);


--
-- TOC entry 7215 (class 2606 OID 408473)
-- Name: crm_lead_convert_config crm_lead_convert_config_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_lead_convert_config
    ADD CONSTRAINT crm_lead_convert_config_pkey PRIMARY KEY (config_id);


--
-- TOC entry 7217 (class 2606 OID 408475)
-- Name: crm_lead_convert_config crm_lead_convert_config_tenant_id_key; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_lead_convert_config
    ADD CONSTRAINT crm_lead_convert_config_tenant_id_key UNIQUE (tenant_id);


--
-- TOC entry 7515 (class 2606 OID 413371)
-- Name: crm_lead_dedupe_config crm_lead_dedupe_config_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_lead_dedupe_config
    ADD CONSTRAINT crm_lead_dedupe_config_pkey PRIMARY KEY (config_id);


--
-- TOC entry 6447 (class 2606 OID 360609)
-- Name: crm_lead crm_lead_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_lead
    ADD CONSTRAINT crm_lead_pkey PRIMARY KEY (lead_id);


--
-- TOC entry 7224 (class 2606 OID 408663)
-- Name: crm_lead_pool_config crm_lead_pool_config_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_lead_pool_config
    ADD CONSTRAINT crm_lead_pool_config_pkey PRIMARY KEY (config_id);


--
-- TOC entry 7402 (class 2606 OID 412789)
-- Name: crm_market_event crm_market_event_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_market_event
    ADD CONSTRAINT crm_market_event_pkey PRIMARY KEY (id);


--
-- TOC entry 7294 (class 2606 OID 409167)
-- Name: crm_marketing_content crm_marketing_content_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_marketing_content
    ADD CONSTRAINT crm_marketing_content_pkey PRIMARY KEY (id);


--
-- TOC entry 7299 (class 2606 OID 409181)
-- Name: crm_marketing_content_publish crm_marketing_content_publish_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_marketing_content_publish
    ADD CONSTRAINT crm_marketing_content_publish_pkey PRIMARY KEY (id);


--
-- TOC entry 7031 (class 2606 OID 379612)
-- Name: crm_mcp_api_key crm_mcp_api_key_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_mcp_api_key
    ADD CONSTRAINT crm_mcp_api_key_pkey PRIMARY KEY (id);


--
-- TOC entry 7038 (class 2606 OID 379628)
-- Name: crm_mcp_audit_log crm_mcp_audit_log_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_mcp_audit_log
    ADD CONSTRAINT crm_mcp_audit_log_pkey PRIMARY KEY (id);


--
-- TOC entry 7010 (class 2606 OID 379503)
-- Name: crm_mcp_server crm_mcp_server_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_mcp_server
    ADD CONSTRAINT crm_mcp_server_pkey PRIMARY KEY (id);


--
-- TOC entry 7440 (class 2606 OID 413145)
-- Name: crm_messenger_config crm_messenger_config_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_messenger_config
    ADD CONSTRAINT crm_messenger_config_pkey PRIMARY KEY (id);


--
-- TOC entry 7451 (class 2606 OID 413175)
-- Name: crm_messenger_conversation crm_messenger_conversation_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_messenger_conversation
    ADD CONSTRAINT crm_messenger_conversation_pkey PRIMARY KEY (id);


--
-- TOC entry 7461 (class 2606 OID 413191)
-- Name: crm_messenger_message crm_messenger_message_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_messenger_message
    ADD CONSTRAINT crm_messenger_message_pkey PRIMARY KEY (id);


--
-- TOC entry 7445 (class 2606 OID 413159)
-- Name: crm_messenger_webhook_event crm_messenger_webhook_event_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_messenger_webhook_event
    ADD CONSTRAINT crm_messenger_webhook_event_pkey PRIMARY KEY (id);


--
-- TOC entry 7526 (class 2606 OID 413407)
-- Name: crm_mobile_push_device crm_mobile_push_device_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_mobile_push_device
    ADD CONSTRAINT crm_mobile_push_device_pkey PRIMARY KEY (id);


--
-- TOC entry 6456 (class 2606 OID 360621)
-- Name: crm_oauth_connections crm_oauth_connections_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_oauth_connections
    ADD CONSTRAINT crm_oauth_connections_pkey PRIMARY KEY (id);


--
-- TOC entry 6458 (class 2606 OID 360628)
-- Name: crm_onepage crm_onepage_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_onepage
    ADD CONSTRAINT crm_onepage_pkey PRIMARY KEY (page_id);


--
-- TOC entry 6460 (class 2606 OID 360637)
-- Name: crm_operator crm_operator_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_operator
    ADD CONSTRAINT crm_operator_pkey PRIMARY KEY (operator_id);


--
-- TOC entry 6462 (class 2606 OID 360652)
-- Name: crm_opty crm_opty_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_opty
    ADD CONSTRAINT crm_opty_pkey PRIMARY KEY (opty_id);


--
-- TOC entry 6464 (class 2606 OID 360661)
-- Name: crm_optyacceptance crm_optyacceptance_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_optyacceptance
    ADD CONSTRAINT crm_optyacceptance_pkey PRIMARY KEY (accept_id);


--
-- TOC entry 6466 (class 2606 OID 360671)
-- Name: crm_optyrelation crm_optyrelation_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_optyrelation
    ADD CONSTRAINT crm_optyrelation_pkey PRIMARY KEY (relate_id);


--
-- TOC entry 6468 (class 2606 OID 360680)
-- Name: crm_optytask crm_optytask_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_optytask
    ADD CONSTRAINT crm_optytask_pkey PRIMARY KEY (task_id);


--
-- TOC entry 6470 (class 2606 OID 360704)
-- Name: crm_org crm_org_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_org
    ADD CONSTRAINT crm_org_pkey PRIMARY KEY (org_id);


--
-- TOC entry 7520 (class 2606 OID 413394)
-- Name: crm_outbound_webhook_delivery crm_outbound_webhook_delivery_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_outbound_webhook_delivery
    ADD CONSTRAINT crm_outbound_webhook_delivery_pkey PRIMARY KEY (delivery_id);


--
-- TOC entry 7517 (class 2606 OID 413385)
-- Name: crm_outbound_webhook crm_outbound_webhook_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_outbound_webhook
    ADD CONSTRAINT crm_outbound_webhook_pkey PRIMARY KEY (webhook_id);


--
-- TOC entry 6472 (class 2606 OID 360711)
-- Name: crm_ownerhistory crm_ownerhistory_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_ownerhistory
    ADD CONSTRAINT crm_ownerhistory_pkey PRIMARY KEY (oh_id);


--
-- TOC entry 6474 (class 2606 OID 360725)
-- Name: crm_partner crm_partner_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_partner
    ADD CONSTRAINT crm_partner_pkey PRIMARY KEY (partner_id);


--
-- TOC entry 6936 (class 2606 OID 371305)
-- Name: crm_patient_value crm_patient_value_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_patient_value
    ADD CONSTRAINT crm_patient_value_pkey PRIMARY KEY (contact_id);


--
-- TOC entry 6476 (class 2606 OID 360736)
-- Name: crm_paymentterm crm_paymentterm_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_paymentterm
    ADD CONSTRAINT crm_paymentterm_pkey PRIMARY KEY (term_id);


--
-- TOC entry 6478 (class 2606 OID 360744)
-- Name: crm_privacy crm_privacy_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_privacy
    ADD CONSTRAINT crm_privacy_pkey PRIMARY KEY (privacy_id);


--
-- TOC entry 7337 (class 2606 OID 411260)
-- Name: crm_product_category crm_product_category_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_product_category
    ADD CONSTRAINT crm_product_category_pkey PRIMARY KEY (category_id);


--
-- TOC entry 7344 (class 2606 OID 411283)
-- Name: crm_product_channel_binding crm_product_channel_binding_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_product_channel_binding
    ADD CONSTRAINT crm_product_channel_binding_pkey PRIMARY KEY (binding_id);


--
-- TOC entry 7341 (class 2606 OID 411273)
-- Name: crm_product_media crm_product_media_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_product_media
    ADD CONSTRAINT crm_product_media_pkey PRIMARY KEY (media_id);


--
-- TOC entry 6480 (class 2606 OID 360752)
-- Name: crm_product crm_product_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_product
    ADD CONSTRAINT crm_product_pkey PRIMARY KEY (product_id);


--
-- TOC entry 7388 (class 2606 OID 411750)
-- Name: crm_professional_service crm_professional_service_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_professional_service
    ADD CONSTRAINT crm_professional_service_pkey PRIMARY KEY (id);


--
-- TOC entry 6486 (class 2606 OID 360762)
-- Name: crm_pulse crm_pulse_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_pulse
    ADD CONSTRAINT crm_pulse_pkey PRIMARY KEY (pulse_id);


--
-- TOC entry 6488 (class 2606 OID 360776)
-- Name: crm_quote crm_quote_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_quote
    ADD CONSTRAINT crm_quote_pkey PRIMARY KEY (quote_id);


--
-- TOC entry 6490 (class 2606 OID 360787)
-- Name: crm_quoteoption crm_quoteoption_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_quoteoption
    ADD CONSTRAINT crm_quoteoption_pkey PRIMARY KEY (option_id);


--
-- TOC entry 7507 (class 2606 OID 413348)
-- Name: crm_recall_policy crm_recall_policy_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_recall_policy
    ADD CONSTRAINT crm_recall_policy_pkey PRIMARY KEY (policy_id);


--
-- TOC entry 7510 (class 2606 OID 413355)
-- Name: crm_recall_schedule crm_recall_schedule_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_recall_schedule
    ADD CONSTRAINT crm_recall_schedule_pkey PRIMARY KEY (schedule_id);


--
-- TOC entry 7420 (class 2606 OID 413032)
-- Name: crm_reminder_link crm_reminder_link_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_reminder_link
    ADD CONSTRAINT crm_reminder_link_pkey PRIMARY KEY (id);


--
-- TOC entry 6492 (class 2606 OID 360805)
-- Name: crm_reportdetail crm_reportdetail_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_reportdetail
    ADD CONSTRAINT crm_reportdetail_pkey PRIMARY KEY (detail_id);


--
-- TOC entry 6494 (class 2606 OID 360813)
-- Name: crm_research crm_research_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_research
    ADD CONSTRAINT crm_research_pkey PRIMARY KEY (research_id);


--
-- TOC entry 6496 (class 2606 OID 360820)
-- Name: crm_respondents crm_respondents_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_respondents
    ADD CONSTRAINT crm_respondents_pkey PRIMARY KEY (id);


--
-- TOC entry 6499 (class 2606 OID 360827)
-- Name: crm_responses crm_responses_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_responses
    ADD CONSTRAINT crm_responses_pkey PRIMARY KEY (id);


--
-- TOC entry 7219 (class 2606 OID 408605)
-- Name: crm_sales_script crm_sales_script_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_sales_script
    ADD CONSTRAINT crm_sales_script_pkey PRIMARY KEY (script_id);


--
-- TOC entry 6501 (class 2606 OID 360837)
-- Name: crm_salesgoal crm_salesgoal_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_salesgoal
    ADD CONSTRAINT crm_salesgoal_pkey PRIMARY KEY (goal_id);


--
-- TOC entry 7399 (class 2606 OID 411774)
-- Name: crm_sanction_source_tag crm_sanction_source_tag_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_sanction_source_tag
    ADD CONSTRAINT crm_sanction_source_tag_pkey PRIMARY KEY (id);


--
-- TOC entry 7393 (class 2606 OID 411762)
-- Name: crm_sanctioned_entity crm_sanctioned_entity_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_sanctioned_entity
    ADD CONSTRAINT crm_sanctioned_entity_pkey PRIMARY KEY (id);


--
-- TOC entry 7099 (class 2606 OID 404333)
-- Name: crm_segment crm_segment_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_segment
    ADD CONSTRAINT crm_segment_pkey PRIMARY KEY (id);


--
-- TOC entry 6975 (class 2606 OID 371705)
-- Name: crm_semantic_profile crm_semantic_profile_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_semantic_profile
    ADD CONSTRAINT crm_semantic_profile_pkey PRIMARY KEY (profile_id);


--
-- TOC entry 6504 (class 2606 OID 360845)
-- Name: crm_sentiment crm_sentiment_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_sentiment
    ADD CONSTRAINT crm_sentiment_pkey PRIMARY KEY (id);


--
-- TOC entry 6949 (class 2606 OID 371461)
-- Name: crm_sf_express_config crm_sf_express_config_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_sf_express_config
    ADD CONSTRAINT crm_sf_express_config_pkey PRIMARY KEY (id);


--
-- TOC entry 6953 (class 2606 OID 371471)
-- Name: crm_sf_express_token crm_sf_express_token_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_sf_express_token
    ADD CONSTRAINT crm_sf_express_token_pkey PRIMARY KEY (id);


--
-- TOC entry 6506 (class 2606 OID 360853)
-- Name: crm_stagehistory crm_stagehistory_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_stagehistory
    ADD CONSTRAINT crm_stagehistory_pkey PRIMARY KEY (sh_id);


--
-- TOC entry 7062 (class 2606 OID 380107)
-- Name: crm_subject_map crm_subject_map_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_subject_map
    ADD CONSTRAINT crm_subject_map_pkey PRIMARY KEY (id);


--
-- TOC entry 7183 (class 2606 OID 404700)
-- Name: crm_survey_outreach_log crm_survey_outreach_log_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_survey_outreach_log
    ADD CONSTRAINT crm_survey_outreach_log_pkey PRIMARY KEY (id);


--
-- TOC entry 6508 (class 2606 OID 360860)
-- Name: crm_surveyresult crm_surveyresult_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_surveyresult
    ADD CONSTRAINT crm_surveyresult_pkey PRIMARY KEY (result_id);


--
-- TOC entry 6510 (class 2606 OID 360873)
-- Name: crm_surveys crm_surveys_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_surveys
    ADD CONSTRAINT crm_surveys_pkey PRIMARY KEY (id);


--
-- TOC entry 7329 (class 2606 OID 411128)
-- Name: crm_tag_channel_mapping crm_tag_channel_mapping_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_tag_channel_mapping
    ADD CONSTRAINT crm_tag_channel_mapping_pkey PRIMARY KEY (id);


--
-- TOC entry 7334 (class 2606 OID 411148)
-- Name: crm_tag_dispatch_task crm_tag_dispatch_task_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_tag_dispatch_task
    ADD CONSTRAINT crm_tag_dispatch_task_pkey PRIMARY KEY (id);


--
-- TOC entry 6512 (class 2606 OID 360883)
-- Name: crm_task crm_task_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_task
    ADD CONSTRAINT crm_task_pkey PRIMARY KEY (task_id);


--
-- TOC entry 7367 (class 2606 OID 411696)
-- Name: crm_tenant_enterprise crm_tenant_enterprise_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_tenant_enterprise
    ADD CONSTRAINT crm_tenant_enterprise_pkey PRIMARY KEY (id);


--
-- TOC entry 7195 (class 2606 OID 408095)
-- Name: crm_tenant_website_asset crm_tenant_website_asset_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_tenant_website_asset
    ADD CONSTRAINT crm_tenant_website_asset_pkey PRIMARY KEY (id);


--
-- TOC entry 7190 (class 2606 OID 407544)
-- Name: crm_tenant_website_page crm_tenant_website_page_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_tenant_website_page
    ADD CONSTRAINT crm_tenant_website_page_pkey PRIMARY KEY (id);


--
-- TOC entry 6514 (class 2606 OID 360889)
-- Name: crm_territory crm_territory_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_territory
    ADD CONSTRAINT crm_territory_pkey PRIMARY KEY (territory_id);


--
-- TOC entry 6516 (class 2606 OID 360896)
-- Name: crm_ticket crm_ticket_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_ticket
    ADD CONSTRAINT crm_ticket_pkey PRIMARY KEY (id);


--
-- TOC entry 6518 (class 2606 OID 360904)
-- Name: crm_timeperiod crm_timeperiod_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_timeperiod
    ADD CONSTRAINT crm_timeperiod_pkey PRIMARY KEY (period_id);


--
-- TOC entry 6520 (class 2606 OID 360915)
-- Name: crm_travel crm_travel_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_travel
    ADD CONSTRAINT crm_travel_pkey PRIMARY KEY (travel_id);


--
-- TOC entry 7466 (class 2606 OID 413212)
-- Name: crm_webchat_config crm_webchat_config_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_webchat_config
    ADD CONSTRAINT crm_webchat_config_pkey PRIMARY KEY (id);


--
-- TOC entry 7480 (class 2606 OID 413246)
-- Name: crm_webchat_conversation crm_webchat_conversation_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_webchat_conversation
    ADD CONSTRAINT crm_webchat_conversation_pkey PRIMARY KEY (id);


--
-- TOC entry 7487 (class 2606 OID 413263)
-- Name: crm_webchat_message crm_webchat_message_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_webchat_message
    ADD CONSTRAINT crm_webchat_message_pkey PRIMARY KEY (id);


--
-- TOC entry 7473 (class 2606 OID 413229)
-- Name: crm_webchat_visitor crm_webchat_visitor_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_webchat_visitor
    ADD CONSTRAINT crm_webchat_visitor_pkey PRIMARY KEY (id);


--
-- TOC entry 7310 (class 2606 OID 409234)
-- Name: crm_website_page_view crm_website_page_view_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_website_page_view
    ADD CONSTRAINT crm_website_page_view_pkey PRIMARY KEY (id);


--
-- TOC entry 7305 (class 2606 OID 409221)
-- Name: crm_website_visitor_session crm_website_visitor_session_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_website_visitor_session
    ADD CONSTRAINT crm_website_visitor_session_pkey PRIMARY KEY (id);


--
-- TOC entry 7351 (class 2606 OID 411315)
-- Name: crm_wechat_miniapp_config crm_wechat_miniapp_config_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_miniapp_config
    ADD CONSTRAINT crm_wechat_miniapp_config_pkey PRIMARY KEY (id);


--
-- TOC entry 7557 (class 2606 OID 413511)
-- Name: crm_wechat_mp_article_comment crm_wechat_mp_article_comment_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_article_comment
    ADD CONSTRAINT crm_wechat_mp_article_comment_pkey PRIMARY KEY (id);


--
-- TOC entry 7237 (class 2606 OID 408784)
-- Name: crm_wechat_mp_article_draft crm_wechat_mp_article_draft_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_article_draft
    ADD CONSTRAINT crm_wechat_mp_article_draft_pkey PRIMARY KEY (id);


--
-- TOC entry 7232 (class 2606 OID 408748)
-- Name: crm_wechat_mp_article crm_wechat_mp_article_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_article
    ADD CONSTRAINT crm_wechat_mp_article_pkey PRIMARY KEY (id);


--
-- TOC entry 7252 (class 2606 OID 408935)
-- Name: crm_wechat_mp_article_stat crm_wechat_mp_article_stat_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_article_stat
    ADD CONSTRAINT crm_wechat_mp_article_stat_pkey PRIMARY KEY (id);


--
-- TOC entry 7130 (class 2606 OID 404452)
-- Name: crm_wechat_mp_auto_reply crm_wechat_mp_auto_reply_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_auto_reply
    ADD CONSTRAINT crm_wechat_mp_auto_reply_pkey PRIMARY KEY (id);


--
-- TOC entry 7086 (class 2606 OID 404205)
-- Name: crm_wechat_mp_callback_log crm_wechat_mp_callback_log_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_callback_log
    ADD CONSTRAINT crm_wechat_mp_callback_log_pkey PRIMARY KEY (id);


--
-- TOC entry 7565 (class 2606 OID 413531)
-- Name: crm_wechat_mp_comment_auto_reply_rule crm_wechat_mp_comment_auto_reply_rule_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_comment_auto_reply_rule
    ADD CONSTRAINT crm_wechat_mp_comment_auto_reply_rule_pkey PRIMARY KEY (id);


--
-- TOC entry 7095 (class 2606 OID 404302)
-- Name: crm_wechat_mp_config crm_wechat_mp_config_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_config
    ADD CONSTRAINT crm_wechat_mp_config_pkey PRIMARY KEY (id);


--
-- TOC entry 7427 (class 2606 OID 413076)
-- Name: crm_wechat_mp_conversation crm_wechat_mp_conversation_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_conversation
    ADD CONSTRAINT crm_wechat_mp_conversation_pkey PRIMARY KEY (id);


--
-- TOC entry 7114 (class 2606 OID 404390)
-- Name: crm_wechat_mp_fan crm_wechat_mp_fan_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_fan
    ADD CONSTRAINT crm_wechat_mp_fan_pkey PRIMARY KEY (id);


--
-- TOC entry 7435 (class 2606 OID 413095)
-- Name: crm_wechat_mp_mass_task crm_wechat_mp_mass_task_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_mass_task
    ADD CONSTRAINT crm_wechat_mp_mass_task_pkey PRIMARY KEY (id);


--
-- TOC entry 7241 (class 2606 OID 408844)
-- Name: crm_wechat_mp_material crm_wechat_mp_material_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_material
    ADD CONSTRAINT crm_wechat_mp_material_pkey PRIMARY KEY (id);


--
-- TOC entry 7105 (class 2606 OID 404360)
-- Name: crm_wechat_mp_menu_key_label crm_wechat_mp_menu_key_label_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_menu_key_label
    ADD CONSTRAINT crm_wechat_mp_menu_key_label_pkey PRIMARY KEY (id);


--
-- TOC entry 7102 (class 2606 OID 404351)
-- Name: crm_wechat_mp_menu crm_wechat_mp_menu_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_menu
    ADD CONSTRAINT crm_wechat_mp_menu_pkey PRIMARY KEY (id);


--
-- TOC entry 7121 (class 2606 OID 404404)
-- Name: crm_wechat_mp_message crm_wechat_mp_message_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_message
    ADD CONSTRAINT crm_wechat_mp_message_pkey PRIMARY KEY (id);


--
-- TOC entry 7133 (class 2606 OID 404474)
-- Name: crm_wechat_mp_message_preset crm_wechat_mp_message_preset_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_message_preset
    ADD CONSTRAINT crm_wechat_mp_message_preset_pkey PRIMARY KEY (id);


--
-- TOC entry 7127 (class 2606 OID 404432)
-- Name: crm_wechat_mp_message_task_item crm_wechat_mp_message_task_item_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_message_task_item
    ADD CONSTRAINT crm_wechat_mp_message_task_item_pkey PRIMARY KEY (id);


--
-- TOC entry 7125 (class 2606 OID 404422)
-- Name: crm_wechat_mp_message_task crm_wechat_mp_message_task_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_message_task
    ADD CONSTRAINT crm_wechat_mp_message_task_pkey PRIMARY KEY (id);


--
-- TOC entry 7246 (class 2606 OID 408910)
-- Name: crm_wechat_mp_publish_task crm_wechat_mp_publish_task_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_publish_task
    ADD CONSTRAINT crm_wechat_mp_publish_task_pkey PRIMARY KEY (id);


--
-- TOC entry 7109 (class 2606 OID 404375)
-- Name: crm_wechat_mp_qrcode crm_wechat_mp_qrcode_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_qrcode
    ADD CONSTRAINT crm_wechat_mp_qrcode_pkey PRIMARY KEY (id);


--
-- TOC entry 7067 (class 2606 OID 380131)
-- Name: crm_wecom_aibot_config crm_wecom_aibot_config_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_aibot_config
    ADD CONSTRAINT crm_wecom_aibot_config_pkey PRIMARY KEY (id);


--
-- TOC entry 7071 (class 2606 OID 380145)
-- Name: crm_wecom_aibot_connection crm_wecom_aibot_connection_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_aibot_connection
    ADD CONSTRAINT crm_wecom_aibot_connection_pkey PRIMARY KEY (id);


--
-- TOC entry 7203 (class 2606 OID 408188)
-- Name: crm_wecom_behavior_daily crm_wecom_behavior_daily_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_behavior_daily
    ADD CONSTRAINT crm_wecom_behavior_daily_pkey PRIMARY KEY (id);


--
-- TOC entry 7058 (class 2606 OID 380082)
-- Name: crm_wecom_callback_event crm_wecom_callback_event_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_callback_event
    ADD CONSTRAINT crm_wecom_callback_event_pkey PRIMARY KEY (id);


--
-- TOC entry 6522 (class 2606 OID 360923)
-- Name: crm_wecom_config crm_wecom_config_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_wecom_config
    ADD CONSTRAINT crm_wecom_config_pkey PRIMARY KEY (id);


--
-- TOC entry 7540 (class 2606 OID 413442)
-- Name: crm_wecom_contact_group crm_wecom_contact_group_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_contact_group
    ADD CONSTRAINT crm_wecom_contact_group_pkey PRIMARY KEY (id);


--
-- TOC entry 6524 (class 2606 OID 360931)
-- Name: crm_wecom_contact_way crm_wecom_contact_way_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_wecom_contact_way
    ADD CONSTRAINT crm_wecom_contact_way_pkey PRIMARY KEY (id);


--
-- TOC entry 6529 (class 2606 OID 360940)
-- Name: crm_wecom_external_contact crm_wecom_external_contact_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_wecom_external_contact
    ADD CONSTRAINT crm_wecom_external_contact_pkey PRIMARY KEY (id);


--
-- TOC entry 7546 (class 2606 OID 413453)
-- Name: crm_wecom_group_provision_task crm_wecom_group_provision_task_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_group_provision_task
    ADD CONSTRAINT crm_wecom_group_provision_task_pkey PRIMARY KEY (task_id);


--
-- TOC entry 7552 (class 2606 OID 413478)
-- Name: crm_wecom_group_reminder crm_wecom_group_reminder_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_group_reminder
    ADD CONSTRAINT crm_wecom_group_reminder_pkey PRIMARY KEY (id);


--
-- TOC entry 7549 (class 2606 OID 413468)
-- Name: crm_wecom_group_reminder_rule crm_wecom_group_reminder_rule_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_group_reminder_rule
    ADD CONSTRAINT crm_wecom_group_reminder_rule_pkey PRIMARY KEY (rule_id);


--
-- TOC entry 7208 (class 2606 OID 408210)
-- Name: crm_wecom_groupchat_daily crm_wecom_groupchat_daily_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_groupchat_daily
    ADD CONSTRAINT crm_wecom_groupchat_daily_pkey PRIMARY KEY (id);


--
-- TOC entry 7041 (class 2606 OID 379692)
-- Name: crm_wecom_kf_config crm_wecom_kf_config_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_kf_config
    ADD CONSTRAINT crm_wecom_kf_config_pkey PRIMARY KEY (id);


--
-- TOC entry 7568 (class 2606 OID 413554)
-- Name: crm_wecom_kf_conversation crm_wecom_kf_conversation_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_kf_conversation
    ADD CONSTRAINT crm_wecom_kf_conversation_pkey PRIMARY KEY (id);


--
-- TOC entry 7045 (class 2606 OID 379748)
-- Name: crm_wecom_kf_message crm_wecom_kf_message_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_kf_message
    ADD CONSTRAINT crm_wecom_kf_message_pkey PRIMARY KEY (id);


--
-- TOC entry 7577 (class 2606 OID 413573)
-- Name: crm_wecom_kf_sync_cursor crm_wecom_kf_sync_cursor_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_kf_sync_cursor
    ADD CONSTRAINT crm_wecom_kf_sync_cursor_pkey PRIMARY KEY (id);


--
-- TOC entry 6535 (class 2606 OID 360947)
-- Name: crm_wecom_mass_task crm_wecom_mass_task_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_wecom_mass_task
    ADD CONSTRAINT crm_wecom_mass_task_pkey PRIMARY KEY (id);


--
-- TOC entry 7053 (class 2606 OID 380029)
-- Name: crm_wecom_material crm_wecom_material_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_material
    ADD CONSTRAINT crm_wecom_material_pkey PRIMARY KEY (id);


--
-- TOC entry 7213 (class 2606 OID 408226)
-- Name: crm_wecom_stats_meta crm_wecom_stats_meta_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_stats_meta
    ADD CONSTRAINT crm_wecom_stats_meta_pkey PRIMARY KEY (id);


--
-- TOC entry 6945 (class 2606 OID 371402)
-- Name: crm_wecom_welcome_log crm_wecom_welcome_log_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_welcome_log
    ADD CONSTRAINT crm_wecom_welcome_log_pkey PRIMARY KEY (id);


--
-- TOC entry 6539 (class 2606 OID 360956)
-- Name: crm_weeklyreport crm_weeklyreport_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_weeklyreport
    ADD CONSTRAINT crm_weeklyreport_pkey PRIMARY KEY (report_id);


--
-- TOC entry 7417 (class 2606 OID 413017)
-- Name: crm_whatsapp_automation_rule crm_whatsapp_automation_rule_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_whatsapp_automation_rule
    ADD CONSTRAINT crm_whatsapp_automation_rule_pkey PRIMARY KEY (id);


--
-- TOC entry 7260 (class 2606 OID 408980)
-- Name: crm_whatsapp_config crm_whatsapp_config_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_whatsapp_config
    ADD CONSTRAINT crm_whatsapp_config_pkey PRIMARY KEY (id);


--
-- TOC entry 7271 (class 2606 OID 409010)
-- Name: crm_whatsapp_conversation crm_whatsapp_conversation_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_whatsapp_conversation
    ADD CONSTRAINT crm_whatsapp_conversation_pkey PRIMARY KEY (id);


--
-- TOC entry 7281 (class 2606 OID 409026)
-- Name: crm_whatsapp_message crm_whatsapp_message_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_whatsapp_message
    ADD CONSTRAINT crm_whatsapp_message_pkey PRIMARY KEY (id);


--
-- TOC entry 7286 (class 2606 OID 409040)
-- Name: crm_whatsapp_template crm_whatsapp_template_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_whatsapp_template
    ADD CONSTRAINT crm_whatsapp_template_pkey PRIMARY KEY (id);


--
-- TOC entry 7412 (class 2606 OID 412996)
-- Name: crm_whatsapp_template_send_item crm_whatsapp_template_send_item_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_whatsapp_template_send_item
    ADD CONSTRAINT crm_whatsapp_template_send_item_pkey PRIMARY KEY (id);


--
-- TOC entry 7408 (class 2606 OID 412982)
-- Name: crm_whatsapp_template_send_task crm_whatsapp_template_send_task_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_whatsapp_template_send_task
    ADD CONSTRAINT crm_whatsapp_template_send_task_pkey PRIMARY KEY (id);


--
-- TOC entry 7265 (class 2606 OID 408994)
-- Name: crm_whatsapp_webhook_event crm_whatsapp_webhook_event_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_whatsapp_webhook_event
    ADD CONSTRAINT crm_whatsapp_webhook_event_pkey PRIMARY KEY (id);


--
-- TOC entry 6931 (class 2606 OID 371269)
-- Name: crm_workflow_automation crm_workflow_automation_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_workflow_automation
    ADD CONSTRAINT crm_workflow_automation_pkey PRIMARY KEY (config_id);


--
-- TOC entry 6541 (class 2606 OID 360965)
-- Name: crm_workflow_trigger crm_workflow_trigger_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_workflow_trigger
    ADD CONSTRAINT crm_workflow_trigger_pkey PRIMARY KEY (id);


--
-- TOC entry 7081 (class 2606 OID 404180)
-- Name: customer_metrics customer_metrics_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.customer_metrics
    ADD CONSTRAINT customer_metrics_pkey PRIMARY KEY (contact_id);


--
-- TOC entry 6543 (class 2606 OID 360972)
-- Name: den_inventory den_inventory_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.den_inventory
    ADD CONSTRAINT den_inventory_pkey PRIMARY KEY (inv_id);


--
-- TOC entry 6547 (class 2606 OID 360988)
-- Name: den_staff_bank den_staff_bank_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.den_staff_bank
    ADD CONSTRAINT den_staff_bank_pkey PRIMARY KEY (bank_id);


--
-- TOC entry 6545 (class 2606 OID 360980)
-- Name: den_staff den_staff_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.den_staff
    ADD CONSTRAINT den_staff_pkey PRIMARY KEY (den_id);


--
-- TOC entry 6549 (class 2606 OID 360995)
-- Name: den_vendor den_vendor_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.den_vendor
    ADD CONSTRAINT den_vendor_pkey PRIMARY KEY (vendor_id);


--
-- TOC entry 6551 (class 2606 OID 361003)
-- Name: ems_hcp ems_hcp_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.ems_hcp
    ADD CONSTRAINT ems_hcp_pkey PRIMARY KEY (hcp_id);


--
-- TOC entry 6886 (class 2606 OID 369769)
-- Name: flw_channel_definition flw_channel_definition_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.flw_channel_definition
    ADD CONSTRAINT flw_channel_definition_pkey PRIMARY KEY (id_);


--
-- TOC entry 6888 (class 2606 OID 369771)
-- Name: flw_ev_databasechangeloglock flw_ev_databasechangeloglock_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.flw_ev_databasechangeloglock
    ADD CONSTRAINT flw_ev_databasechangeloglock_pkey PRIMARY KEY (id);


--
-- TOC entry 6891 (class 2606 OID 369773)
-- Name: flw_event_definition flw_event_definition_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.flw_event_definition
    ADD CONSTRAINT flw_event_definition_pkey PRIMARY KEY (id_);


--
-- TOC entry 6893 (class 2606 OID 369775)
-- Name: flw_event_deployment flw_event_deployment_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.flw_event_deployment
    ADD CONSTRAINT flw_event_deployment_pkey PRIMARY KEY (id_);


--
-- TOC entry 6895 (class 2606 OID 369777)
-- Name: flw_event_resource flw_event_resource_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.flw_event_resource
    ADD CONSTRAINT flw_event_resource_pkey PRIMARY KEY (id_);


--
-- TOC entry 6900 (class 2606 OID 369779)
-- Name: flw_ru_batch_part flw_ru_batch_part_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.flw_ru_batch_part
    ADD CONSTRAINT flw_ru_batch_part_pkey PRIMARY KEY (id_);


--
-- TOC entry 6897 (class 2606 OID 369781)
-- Name: flw_ru_batch flw_ru_batch_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.flw_ru_batch
    ADD CONSTRAINT flw_ru_batch_pkey PRIMARY KEY (id_);


--
-- TOC entry 6555 (class 2606 OID 361068)
-- Name: gen_table_column gen_table_column_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.gen_table_column
    ADD CONSTRAINT gen_table_column_pkey PRIMARY KEY (column_id);


--
-- TOC entry 6553 (class 2606 OID 361059)
-- Name: gen_table gen_table_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.gen_table
    ADD CONSTRAINT gen_table_pkey PRIMARY KEY (table_id);


--
-- TOC entry 6557 (class 2606 OID 361077)
-- Name: master_registration master_registration_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.master_registration
    ADD CONSTRAINT master_registration_pkey PRIMARY KEY (regist_id);


--
-- TOC entry 6559 (class 2606 OID 361086)
-- Name: master_tenant master_tenant_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.master_tenant
    ADD CONSTRAINT master_tenant_pkey PRIMARY KEY (id);


--
-- TOC entry 7230 (class 2606 OID 408729)
-- Name: smart_push_execution_log smart_push_execution_log_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.smart_push_execution_log
    ADD CONSTRAINT smart_push_execution_log_pkey PRIMARY KEY (id);


--
-- TOC entry 7226 (class 2606 OID 408718)
-- Name: smart_push_strategy smart_push_strategy_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.smart_push_strategy
    ADD CONSTRAINT smart_push_strategy_pkey PRIMARY KEY (id);


--
-- TOC entry 6561 (class 2606 OID 361095)
-- Name: sys_config sys_config_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_config
    ADD CONSTRAINT sys_config_pkey PRIMARY KEY (config_id);


--
-- TOC entry 6563 (class 2606 OID 361101)
-- Name: sys_deploy_form sys_deploy_form_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_deploy_form
    ADD CONSTRAINT sys_deploy_form_pkey PRIMARY KEY (id);


--
-- TOC entry 6565 (class 2606 OID 361111)
-- Name: sys_dept sys_dept_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_dept
    ADD CONSTRAINT sys_dept_pkey PRIMARY KEY (dept_id);


--
-- TOC entry 6567 (class 2606 OID 361122)
-- Name: sys_dict_data sys_dict_data_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_dict_data
    ADD CONSTRAINT sys_dict_data_pkey PRIMARY KEY (dict_code);


--
-- TOC entry 6569 (class 2606 OID 361131)
-- Name: sys_dict_type sys_dict_type_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_dict_type
    ADD CONSTRAINT sys_dict_type_pkey PRIMARY KEY (dict_id);


--
-- TOC entry 6571 (class 2606 OID 361147)
-- Name: sys_ems_corres sys_ems_corres_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_ems_corres
    ADD CONSTRAINT sys_ems_corres_pkey PRIMARY KEY (corres_id);


--
-- TOC entry 6573 (class 2606 OID 361155)
-- Name: sys_form sys_form_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_form
    ADD CONSTRAINT sys_form_pkey PRIMARY KEY (form_id);


--
-- TOC entry 6575 (class 2606 OID 361162)
-- Name: sys_hi_audittrail sys_hi_audittrail_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_hi_audittrail
    ADD CONSTRAINT sys_hi_audittrail_pkey PRIMARY KEY (id);


--
-- TOC entry 6579 (class 2606 OID 361182)
-- Name: sys_job_log sys_job_log_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_job_log
    ADD CONSTRAINT sys_job_log_pkey PRIMARY KEY (job_log_id);


--
-- TOC entry 6577 (class 2606 OID 361173)
-- Name: sys_job sys_job_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_job
    ADD CONSTRAINT sys_job_pkey PRIMARY KEY (job_id, job_name, job_group);


--
-- TOC entry 6581 (class 2606 OID 361191)
-- Name: sys_logininfor sys_logininfor_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_logininfor
    ADD CONSTRAINT sys_logininfor_pkey PRIMARY KEY (info_id);


--
-- TOC entry 6583 (class 2606 OID 361206)
-- Name: sys_menu sys_menu_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_menu
    ADD CONSTRAINT sys_menu_pkey PRIMARY KEY (menu_id);


--
-- TOC entry 6585 (class 2606 OID 361215)
-- Name: sys_notice sys_notice_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_notice
    ADD CONSTRAINT sys_notice_pkey PRIMARY KEY (notice_id);


--
-- TOC entry 6587 (class 2606 OID 361226)
-- Name: sys_oper_log sys_oper_log_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_oper_log
    ADD CONSTRAINT sys_oper_log_pkey PRIMARY KEY (oper_id);


--
-- TOC entry 6589 (class 2606 OID 361233)
-- Name: sys_organization sys_organization_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_organization
    ADD CONSTRAINT sys_organization_pkey PRIMARY KEY (organization_id);


--
-- TOC entry 6591 (class 2606 OID 361242)
-- Name: sys_pm_log sys_pm_log_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_pm_log
    ADD CONSTRAINT sys_pm_log_pkey PRIMARY KEY (log_id);


--
-- TOC entry 6593 (class 2606 OID 361250)
-- Name: sys_post sys_post_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_post
    ADD CONSTRAINT sys_post_pkey PRIMARY KEY (post_id);


--
-- TOC entry 6595 (class 2606 OID 361260)
-- Name: sys_re_acceptance sys_re_acceptance_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_re_acceptance
    ADD CONSTRAINT sys_re_acceptance_pkey PRIMARY KEY (accept_id);


--
-- TOC entry 6597 (class 2606 OID 361270)
-- Name: sys_re_actitem sys_re_actitem_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_re_actitem
    ADD CONSTRAINT sys_re_actitem_pkey PRIMARY KEY (py_id);


--
-- TOC entry 6599 (class 2606 OID 361278)
-- Name: sys_re_activity sys_re_activity_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_re_activity
    ADD CONSTRAINT sys_re_activity_pkey PRIMARY KEY (py_id);


--
-- TOC entry 6601 (class 2606 OID 361286)
-- Name: sys_re_application sys_re_application_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_re_application
    ADD CONSTRAINT sys_re_application_pkey PRIMARY KEY (py_id);


--
-- TOC entry 6603 (class 2606 OID 361296)
-- Name: sys_re_board sys_re_board_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_re_board
    ADD CONSTRAINT sys_re_board_pkey PRIMARY KEY (py_id);


--
-- TOC entry 6605 (class 2606 OID 361310)
-- Name: sys_re_casetype sys_re_casetype_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_re_casetype
    ADD CONSTRAINT sys_re_casetype_pkey PRIMARY KEY (id);


--
-- TOC entry 6607 (class 2606 OID 361318)
-- Name: sys_re_checklist sys_re_checklist_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_re_checklist
    ADD CONSTRAINT sys_re_checklist_pkey PRIMARY KEY (py_id);


--
-- TOC entry 6609 (class 2606 OID 361329)
-- Name: sys_re_corres sys_re_corres_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_re_corres
    ADD CONSTRAINT sys_re_corres_pkey PRIMARY KEY (corres_id);


--
-- TOC entry 6611 (class 2606 OID 361339)
-- Name: sys_re_datafield sys_re_datafield_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_re_datafield
    ADD CONSTRAINT sys_re_datafield_pkey PRIMARY KEY (field_id);


--
-- TOC entry 6613 (class 2606 OID 361347)
-- Name: sys_re_dataobj sys_re_dataobj_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_re_dataobj
    ADD CONSTRAINT sys_re_dataobj_pkey PRIMARY KEY (dataobj_id);


--
-- TOC entry 6615 (class 2606 OID 361356)
-- Name: sys_re_datatransform sys_re_datatransform_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_re_datatransform
    ADD CONSTRAINT sys_re_datatransform_pkey PRIMARY KEY (py_id);


--
-- TOC entry 6617 (class 2606 OID 361365)
-- Name: sys_re_datatype sys_re_datatype_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_re_datatype
    ADD CONSTRAINT sys_re_datatype_pkey PRIMARY KEY (data_id);


--
-- TOC entry 6619 (class 2606 OID 361374)
-- Name: sys_re_decisiontable sys_re_decisiontable_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_re_decisiontable
    ADD CONSTRAINT sys_re_decisiontable_pkey PRIMARY KEY (py_id);


--
-- TOC entry 6621 (class 2606 OID 361383)
-- Name: sys_re_decisiontree sys_re_decisiontree_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_re_decisiontree
    ADD CONSTRAINT sys_re_decisiontree_pkey PRIMARY KEY (py_id);


--
-- TOC entry 6623 (class 2606 OID 361393)
-- Name: sys_re_dtitem sys_re_dtitem_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_re_dtitem
    ADD CONSTRAINT sys_re_dtitem_pkey PRIMARY KEY (py_id);


--
-- TOC entry 6625 (class 2606 OID 361403)
-- Name: sys_re_participant sys_re_participant_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_re_participant
    ADD CONSTRAINT sys_re_participant_pkey PRIMARY KEY (part_id);


--
-- TOC entry 6627 (class 2606 OID 361410)
-- Name: sys_re_pmchange sys_re_pmchange_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_re_pmchange
    ADD CONSTRAINT sys_re_pmchange_pkey PRIMARY KEY (change_id);


--
-- TOC entry 6629 (class 2606 OID 361418)
-- Name: sys_re_pmrisk sys_re_pmrisk_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_re_pmrisk
    ADD CONSTRAINT sys_re_pmrisk_pkey PRIMARY KEY (risk_id);


--
-- TOC entry 6631 (class 2606 OID 361429)
-- Name: sys_re_process sys_re_process_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_re_process
    ADD CONSTRAINT sys_re_process_pkey PRIMARY KEY (process_id);


--
-- TOC entry 6633 (class 2606 OID 361450)
-- Name: sys_re_project sys_re_project_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_re_project
    ADD CONSTRAINT sys_re_project_pkey PRIMARY KEY (py_id);


--
-- TOC entry 6635 (class 2606 OID 361457)
-- Name: sys_re_reportdefinition sys_re_reportdefinition_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_re_reportdefinition
    ADD CONSTRAINT sys_re_reportdefinition_pkey PRIMARY KEY (id);


--
-- TOC entry 6637 (class 2606 OID 361469)
-- Name: sys_re_ruleset sys_re_ruleset_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_re_ruleset
    ADD CONSTRAINT sys_re_ruleset_pkey PRIMARY KEY (py_id);


--
-- TOC entry 6639 (class 2606 OID 361481)
-- Name: sys_re_stage sys_re_stage_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_re_stage
    ADD CONSTRAINT sys_re_stage_pkey PRIMARY KEY (stage_id);


--
-- TOC entry 6641 (class 2606 OID 361490)
-- Name: sys_re_strategy sys_re_strategy_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_re_strategy
    ADD CONSTRAINT sys_re_strategy_pkey PRIMARY KEY (py_id);


--
-- TOC entry 6643 (class 2606 OID 361498)
-- Name: sys_re_talkingplan sys_re_talkingplan_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_re_talkingplan
    ADD CONSTRAINT sys_re_talkingplan_pkey PRIMARY KEY (plan_id);


--
-- TOC entry 6645 (class 2606 OID 361506)
-- Name: sys_re_template sys_re_template_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_re_template
    ADD CONSTRAINT sys_re_template_pkey PRIMARY KEY (template_id);


--
-- TOC entry 6647 (class 2606 OID 361515)
-- Name: sys_re_when sys_re_when_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_re_when
    ADD CONSTRAINT sys_re_when_pkey PRIMARY KEY (py_id);


--
-- TOC entry 6649 (class 2606 OID 361523)
-- Name: sys_re_workload sys_re_workload_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_re_workload
    ADD CONSTRAINT sys_re_workload_pkey PRIMARY KEY (workload_id);


--
-- TOC entry 6651 (class 2606 OID 361533)
-- Name: sys_re_worktask sys_re_worktask_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_re_worktask
    ADD CONSTRAINT sys_re_worktask_pkey PRIMARY KEY (task_id);


--
-- TOC entry 6655 (class 2606 OID 361550)
-- Name: sys_role_dept sys_role_dept_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_role_dept
    ADD CONSTRAINT sys_role_dept_pkey PRIMARY KEY (role_id, dept_id);


--
-- TOC entry 6657 (class 2606 OID 361555)
-- Name: sys_role_menu sys_role_menu_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_role_menu
    ADD CONSTRAINT sys_role_menu_pkey PRIMARY KEY (role_id, menu_id);


--
-- TOC entry 6653 (class 2606 OID 361545)
-- Name: sys_role sys_role_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_role
    ADD CONSTRAINT sys_role_pkey PRIMARY KEY (role_id);


--
-- TOC entry 6659 (class 2606 OID 361564)
-- Name: sys_ru_caseinstance sys_ru_caseinstance_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_ru_caseinstance
    ADD CONSTRAINT sys_ru_caseinstance_pkey PRIMARY KEY (id);


--
-- TOC entry 6661 (class 2606 OID 361570)
-- Name: sys_sequence_id sys_sequence_id_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_sequence_id
    ADD CONSTRAINT sys_sequence_id_pkey PRIMARY KEY (sequenceid);


--
-- TOC entry 6663 (class 2606 OID 361576)
-- Name: sys_task_form sys_task_form_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_task_form
    ADD CONSTRAINT sys_task_form_pkey PRIMARY KEY (id);


--
-- TOC entry 6665 (class 2606 OID 361591)
-- Name: sys_user sys_user_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_user
    ADD CONSTRAINT sys_user_pkey PRIMARY KEY (user_id);


--
-- TOC entry 6667 (class 2606 OID 361596)
-- Name: sys_user_post sys_user_post_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_user_post
    ADD CONSTRAINT sys_user_post_pkey PRIMARY KEY (user_id, post_id);


--
-- TOC entry 6669 (class 2606 OID 361601)
-- Name: sys_user_role sys_user_role_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.sys_user_role
    ADD CONSTRAINT sys_user_role_pkey PRIMARY KEY (user_id, role_id);


--
-- TOC entry 7492 (class 2606 OID 413271)
-- Name: sys_webchat_token_tenant sys_webchat_token_tenant_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.sys_webchat_token_tenant
    ADD CONSTRAINT sys_webchat_token_tenant_pkey PRIMARY KEY (website_token);


--
-- TOC entry 6973 (class 2606 OID 371692)
-- Name: sys_webhook_path_index sys_webhook_path_index_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.sys_webhook_path_index
    ADD CONSTRAINT sys_webhook_path_index_pkey PRIMARY KEY (path);


--
-- TOC entry 6671 (class 2606 OID 361607)
-- Name: table_table_industry table_table_industry_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.table_table_industry
    ADD CONSTRAINT table_table_industry_pkey PRIMARY KEY (id);


--
-- TOC entry 7073 (class 2606 OID 380147)
-- Name: crm_wecom_aibot_connection uk_aibot_conn_tenant; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_aibot_connection
    ADD CONSTRAINT uk_aibot_conn_tenant UNIQUE (tenant);


--
-- TOC entry 7069 (class 2606 OID 380133)
-- Name: crm_wecom_aibot_config uk_aibot_tenant; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_aibot_config
    ADD CONSTRAINT uk_aibot_tenant UNIQUE (tenant);


--
-- TOC entry 7206 (class 2606 OID 408190)
-- Name: crm_wecom_behavior_daily uk_behavior_daily; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_behavior_daily
    ADD CONSTRAINT uk_behavior_daily UNIQUE (userid, stat_time);


--
-- TOC entry 7065 (class 2606 OID 380109)
-- Name: crm_subject_map uk_child; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_subject_map
    ADD CONSTRAINT uk_child UNIQUE (child_subject_type, child_subject_id);


--
-- TOC entry 7043 (class 2606 OID 379694)
-- Name: crm_wecom_kf_config uk_corp; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_kf_config
    ADD CONSTRAINT uk_corp UNIQUE (corp_id);


--
-- TOC entry 6983 (class 2606 OID 371751)
-- Name: crm_ai_model_config uk_crm_ai_model_config_tenant; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_model_config
    ADD CONSTRAINT uk_crm_ai_model_config_tenant UNIQUE (tenant_id);


--
-- TOC entry 7024 (class 2606 OID 379539)
-- Name: crm_ai_skill_playbook_step uk_crm_ai_playbook_step_key; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_skill_playbook_step
    ADD CONSTRAINT uk_crm_ai_playbook_step_key UNIQUE (playbook_id, step_key);


--
-- TOC entry 7018 (class 2606 OID 379522)
-- Name: crm_ai_skill_playbook uk_crm_ai_playbook_tenant_name_ver; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_skill_playbook
    ADD CONSTRAINT uk_crm_ai_playbook_tenant_name_ver UNIQUE (tenant_id, name, version);


--
-- TOC entry 7008 (class 2606 OID 379486)
-- Name: crm_ai_skill uk_crm_ai_skill_tenant_name; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_skill
    ADD CONSTRAINT uk_crm_ai_skill_tenant_name UNIQUE (tenant_id, name);


--
-- TOC entry 7318 (class 2606 OID 409267)
-- Name: crm_capture uk_crm_capture_template; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_capture
    ADD CONSTRAINT uk_crm_capture_template UNIQUE (template_id);


--
-- TOC entry 6989 (class 2606 OID 371792)
-- Name: crm_email_analysis uk_crm_email_analysis_key; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_email_analysis
    ADD CONSTRAINT uk_crm_email_analysis_key UNIQUE (tenant_id, message_external_id, locale);


--
-- TOC entry 7079 (class 2606 OID 404136)
-- Name: crm_customer_identity uk_crm_identity_type_value; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_customer_identity
    ADD CONSTRAINT uk_crm_identity_type_value UNIQUE (identity_type, identity_value);


--
-- TOC entry 7034 (class 2606 OID 379616)
-- Name: crm_mcp_api_key uk_crm_mcp_api_key_prefix; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_mcp_api_key
    ADD CONSTRAINT uk_crm_mcp_api_key_prefix UNIQUE (key_prefix);


--
-- TOC entry 7036 (class 2606 OID 379614)
-- Name: crm_mcp_api_key uk_crm_mcp_api_key_tenant_name; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_mcp_api_key
    ADD CONSTRAINT uk_crm_mcp_api_key_tenant_name UNIQUE (tenant_id, name);


--
-- TOC entry 7013 (class 2606 OID 379505)
-- Name: crm_mcp_server uk_crm_mcp_server_tenant_name; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_mcp_server
    ADD CONSTRAINT uk_crm_mcp_server_tenant_name UNIQUE (tenant_id, name);


--
-- TOC entry 7443 (class 2606 OID 413147)
-- Name: crm_messenger_config uk_crm_messenger_config_tenant; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_messenger_config
    ADD CONSTRAINT uk_crm_messenger_config_tenant UNIQUE (tenant_id);


--
-- TOC entry 7459 (class 2606 OID 413177)
-- Name: crm_messenger_conversation uk_crm_messenger_conversation; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_messenger_conversation
    ADD CONSTRAINT uk_crm_messenger_conversation UNIQUE (tenant_id, psid);


--
-- TOC entry 7529 (class 2606 OID 413409)
-- Name: crm_mobile_push_device uk_crm_mobile_push_device_token; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_mobile_push_device
    ADD CONSTRAINT uk_crm_mobile_push_device_token UNIQUE (tenant_id, push_token);


--
-- TOC entry 7425 (class 2606 OID 413034)
-- Name: crm_reminder_link uk_crm_reminder_link_tenant_token; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_reminder_link
    ADD CONSTRAINT uk_crm_reminder_link_tenant_token UNIQUE (tenant_id, token);


--
-- TOC entry 6978 (class 2606 OID 371707)
-- Name: crm_semantic_profile uk_crm_semantic_profile_tenant; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_semantic_profile
    ADD CONSTRAINT uk_crm_semantic_profile_tenant UNIQUE (tenant_id);


--
-- TOC entry 7332 (class 2606 OID 411130)
-- Name: crm_tag_channel_mapping uk_crm_tag_channel_mapping; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_tag_channel_mapping
    ADD CONSTRAINT uk_crm_tag_channel_mapping UNIQUE (dict_value, channel);


--
-- TOC entry 7469 (class 2606 OID 413214)
-- Name: crm_webchat_config uk_crm_webchat_config_tenant; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_webchat_config
    ADD CONSTRAINT uk_crm_webchat_config_tenant UNIQUE (tenant_id);


--
-- TOC entry 7471 (class 2606 OID 413216)
-- Name: crm_webchat_config uk_crm_webchat_config_token; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_webchat_config
    ADD CONSTRAINT uk_crm_webchat_config_token UNIQUE (website_token);


--
-- TOC entry 7478 (class 2606 OID 413231)
-- Name: crm_webchat_visitor uk_crm_webchat_visitor; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_webchat_visitor
    ADD CONSTRAINT uk_crm_webchat_visitor UNIQUE (tenant_id, visitor_id);


--
-- TOC entry 7544 (class 2606 OID 413444)
-- Name: crm_wecom_contact_group uk_crm_wecom_contact_group_contact; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_contact_group
    ADD CONSTRAINT uk_crm_wecom_contact_group_contact UNIQUE (tenant_id, contact_id);


--
-- TOC entry 7555 (class 2606 OID 413480)
-- Name: crm_wecom_group_reminder uk_crm_wecom_group_reminder; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_group_reminder
    ADD CONSTRAINT uk_crm_wecom_group_reminder UNIQUE (tenant_id, contact_id, rule_id, source_ref);


--
-- TOC entry 6934 (class 2606 OID 371271)
-- Name: crm_workflow_automation uk_crm_wf_auto_tenant_key; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_workflow_automation
    ADD CONSTRAINT uk_crm_wf_auto_tenant_key UNIQUE (tenant_id, automation_key);


--
-- TOC entry 7263 (class 2606 OID 408982)
-- Name: crm_whatsapp_config uk_crm_whatsapp_config_tenant; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_whatsapp_config
    ADD CONSTRAINT uk_crm_whatsapp_config_tenant UNIQUE (tenant_id);


--
-- TOC entry 7279 (class 2606 OID 409012)
-- Name: crm_whatsapp_conversation uk_crm_whatsapp_conversation; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_whatsapp_conversation
    ADD CONSTRAINT uk_crm_whatsapp_conversation UNIQUE (tenant_id, wa_id);


--
-- TOC entry 7289 (class 2606 OID 409042)
-- Name: crm_whatsapp_template uk_crm_whatsapp_template_tenant; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_whatsapp_template
    ADD CONSTRAINT uk_crm_whatsapp_template_tenant UNIQUE (tenant_id, template_name, language);


--
-- TOC entry 7258 (class 2606 OID 408960)
-- Name: crm_dashboard_snapshot uk_dashboard_snapshot; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_dashboard_snapshot
    ADD CONSTRAINT uk_dashboard_snapshot UNIQUE (corp_id, snapshot_date, metric_type);


--
-- TOC entry 7141 (class 2606 OID 404494)
-- Name: crm_event_mp_outreach_log uk_event_mp_outreach; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_event_mp_outreach_log
    ADD CONSTRAINT uk_event_mp_outreach UNIQUE (event_id, open_id, trigger_type);


--
-- TOC entry 7502 (class 2606 OID 413325)
-- Name: crm_followup_rule_enrollment uk_followup_enrollment_idempotency; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_followup_rule_enrollment
    ADD CONSTRAINT uk_followup_enrollment_idempotency UNIQUE (tenant_id, idempotency_key);


--
-- TOC entry 7211 (class 2606 OID 408212)
-- Name: crm_wecom_groupchat_daily uk_groupchat_daily; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_groupchat_daily
    ADD CONSTRAINT uk_groupchat_daily UNIQUE (owner_userid, stat_time);


--
-- TOC entry 7303 (class 2606 OID 409183)
-- Name: crm_marketing_content_publish uk_mkt_content_publish; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_marketing_content_publish
    ADD CONSTRAINT uk_mkt_content_publish UNIQUE (content_id, channel, app_id);


--
-- TOC entry 7563 (class 2606 OID 413513)
-- Name: crm_wechat_mp_article_comment uk_mp_article_comment; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_article_comment
    ADD CONSTRAINT uk_mp_article_comment UNIQUE (app_id, msg_data_id, index_in_msg, user_comment_id);


--
-- TOC entry 7433 (class 2606 OID 413078)
-- Name: crm_wechat_mp_conversation uk_mp_conversation_app_openid; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_conversation
    ADD CONSTRAINT uk_mp_conversation_app_openid UNIQUE (app_id, open_id);


--
-- TOC entry 7119 (class 2606 OID 404392)
-- Name: crm_wechat_mp_fan uk_mp_fan_openid; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_fan
    ADD CONSTRAINT uk_mp_fan_openid UNIQUE (app_id, open_id);


--
-- TOC entry 7107 (class 2606 OID 404362)
-- Name: crm_wechat_mp_menu_key_label uk_mp_menu_key_label; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_menu_key_label
    ADD CONSTRAINT uk_mp_menu_key_label UNIQUE (app_id, menu_key);


--
-- TOC entry 7112 (class 2606 OID 404377)
-- Name: crm_wechat_mp_qrcode uk_mp_qrcode_scene; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_qrcode
    ADD CONSTRAINT uk_mp_qrcode_scene UNIQUE (app_id, scene_str);


--
-- TOC entry 7051 (class 2606 OID 379750)
-- Name: crm_wecom_kf_message uk_msgid; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_kf_message
    ADD CONSTRAINT uk_msgid UNIQUE (corp_id, msgid);


--
-- TOC entry 7524 (class 2606 OID 413396)
-- Name: crm_outbound_webhook_delivery uk_outbound_webhook_delivery; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_outbound_webhook_delivery
    ADD CONSTRAINT uk_outbound_webhook_delivery UNIQUE (tenant_id, webhook_id, event_id);


--
-- TOC entry 7513 (class 2606 OID 413357)
-- Name: crm_recall_schedule uk_recall_schedule_contact_policy; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_recall_schedule
    ADD CONSTRAINT uk_recall_schedule_contact_policy UNIQUE (tenant_id, contact_id, policy_id);


--
-- TOC entry 6951 (class 2606 OID 371464)
-- Name: crm_sf_express_config uk_sf_express_config_tenant; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_sf_express_config
    ADD CONSTRAINT uk_sf_express_config_tenant UNIQUE (tenant_id);


--
-- TOC entry 6956 (class 2606 OID 371476)
-- Name: crm_sf_express_token uk_sf_express_token_tenant; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_sf_express_token
    ADD CONSTRAINT uk_sf_express_token_tenant UNIQUE (tenant_id);


--
-- TOC entry 7308 (class 2606 OID 409223)
-- Name: crm_website_visitor_session uk_website_visitor_session; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_website_visitor_session
    ADD CONSTRAINT uk_website_visitor_session UNIQUE (session_id);


--
-- TOC entry 7097 (class 2606 OID 404304)
-- Name: crm_wechat_mp_config uk_wechat_mp_config_app; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_config
    ADD CONSTRAINT uk_wechat_mp_config_app UNIQUE (app_id);


--
-- TOC entry 6533 (class 2606 OID 379783)
-- Name: crm_wecom_external_contact uk_wecom_ext_follow; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.crm_wecom_external_contact
    ADD CONSTRAINT uk_wecom_ext_follow UNIQUE (external_userid, follow_userid);


--
-- TOC entry 7575 (class 2606 OID 413556)
-- Name: crm_wecom_kf_conversation uk_wecom_kf_conversation_kfid_userid; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_kf_conversation
    ADD CONSTRAINT uk_wecom_kf_conversation_kfid_userid UNIQUE (open_kfid, external_userid);


--
-- TOC entry 7579 (class 2606 OID 413575)
-- Name: crm_wecom_kf_sync_cursor uk_wecom_kf_sync_cursor; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wecom_kf_sync_cursor
    ADD CONSTRAINT uk_wecom_kf_sync_cursor UNIQUE (corp_id, open_kfid);


--
-- TOC entry 6965 (class 2606 OID 371627)
-- Name: crm_data_table_column uq_crm_data_table_column_name; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_data_table_column
    ADD CONSTRAINT uq_crm_data_table_column_name UNIQUE (data_table_id, name);


--
-- TOC entry 6961 (class 2606 OID 371615)
-- Name: crm_data_table uq_crm_data_table_tenant_name; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_data_table
    ADD CONSTRAINT uq_crm_data_table_tenant_name UNIQUE (tenant_id, name);


--
-- TOC entry 7193 (class 2606 OID 407546)
-- Name: crm_tenant_website_page uq_tenant_website_page_path; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_tenant_website_page
    ADD CONSTRAINT uq_tenant_website_page_path UNIQUE (path);


--
-- TOC entry 6918 (class 2606 OID 371225)
-- Name: w_connection w_connection_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.w_connection
    ADD CONSTRAINT w_connection_pkey PRIMARY KEY (id);


--
-- TOC entry 6912 (class 2606 OID 371223)
-- Name: w_credential w_credential_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.w_credential
    ADD CONSTRAINT w_credential_pkey PRIMARY KEY (id);


--
-- TOC entry 6921 (class 2606 OID 371227)
-- Name: w_execution w_execution_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.w_execution
    ADD CONSTRAINT w_execution_pkey PRIMARY KEY (id);


--
-- TOC entry 6914 (class 2606 OID 371221)
-- Name: w_node_definition w_node_definition_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.w_node_definition
    ADD CONSTRAINT w_node_definition_pkey PRIMARY KEY (id);


--
-- TOC entry 6909 (class 2606 OID 371167)
-- Name: w_node w_node_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.w_node
    ADD CONSTRAINT w_node_pkey PRIMARY KEY (id);


--
-- TOC entry 7349 (class 2606 OID 411302)
-- Name: w_scheduled_task w_scheduled_task_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.w_scheduled_task
    ADD CONSTRAINT w_scheduled_task_pkey PRIMARY KEY (id);


--
-- TOC entry 6927 (class 2606 OID 371214)
-- Name: w_wait_resume w_wait_resume_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.w_wait_resume
    ADD CONSTRAINT w_wait_resume_pkey PRIMARY KEY (id);


--
-- TOC entry 6929 (class 2606 OID 371352)
-- Name: w_wait_resume w_wait_resume_resume_key_key; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.w_wait_resume
    ADD CONSTRAINT w_wait_resume_resume_key_key UNIQUE (resume_key);


--
-- TOC entry 6907 (class 2606 OID 371236)
-- Name: w_workflow w_workflow_pkey; Type: CONSTRAINT; Schema: public; Owner: _tenant
--

ALTER TABLE ONLY public.w_workflow
    ADD CONSTRAINT w_workflow_pkey PRIMARY KEY (id);


--
-- TOC entry 6823 (class 1259 OID 369782)
-- Name: act_idx_athrz_procedef; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_athrz_procedef ON public.act_ru_identitylink USING btree (proc_def_id_);


--
-- TOC entry 6676 (class 1259 OID 369783)
-- Name: act_idx_bytear_depl; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_bytear_depl ON public.act_ge_bytearray USING btree (deployment_id_);


--
-- TOC entry 6884 (class 1259 OID 369784)
-- Name: act_idx_channel_def_uniq; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX act_idx_channel_def_uniq ON public.flw_channel_definition USING btree (key_, version_, tenant_id_);


--
-- TOC entry 6783 (class 1259 OID 369785)
-- Name: act_idx_deadletter_job_correlation_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_deadletter_job_correlation_id ON public.act_ru_deadletter_job USING btree (correlation_id_);


--
-- TOC entry 6784 (class 1259 OID 369786)
-- Name: act_idx_deadletter_job_custom_values_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_deadletter_job_custom_values_id ON public.act_ru_deadletter_job USING btree (custom_values_id_);


--
-- TOC entry 6785 (class 1259 OID 369787)
-- Name: act_idx_deadletter_job_exception_stack_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_deadletter_job_exception_stack_id ON public.act_ru_deadletter_job USING btree (exception_stack_id_);


--
-- TOC entry 6786 (class 1259 OID 369788)
-- Name: act_idx_deadletter_job_execution_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_deadletter_job_execution_id ON public.act_ru_deadletter_job USING btree (execution_id_);


--
-- TOC entry 6787 (class 1259 OID 369789)
-- Name: act_idx_deadletter_job_proc_def_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_deadletter_job_proc_def_id ON public.act_ru_deadletter_job USING btree (proc_def_id_);


--
-- TOC entry 6788 (class 1259 OID 369790)
-- Name: act_idx_deadletter_job_process_instance_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_deadletter_job_process_instance_id ON public.act_ru_deadletter_job USING btree (process_instance_id_);


--
-- TOC entry 6789 (class 1259 OID 369791)
-- Name: act_idx_djob_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_djob_scope ON public.act_ru_deadletter_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 6790 (class 1259 OID 369792)
-- Name: act_idx_djob_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_djob_scope_def ON public.act_ru_deadletter_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 6791 (class 1259 OID 369793)
-- Name: act_idx_djob_sub_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_djob_sub_scope ON public.act_ru_deadletter_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 6813 (class 1259 OID 369794)
-- Name: act_idx_ejob_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ejob_scope ON public.act_ru_external_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 6814 (class 1259 OID 369795)
-- Name: act_idx_ejob_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ejob_scope_def ON public.act_ru_external_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 6815 (class 1259 OID 369796)
-- Name: act_idx_ejob_sub_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ejob_sub_scope ON public.act_ru_external_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 6794 (class 1259 OID 369797)
-- Name: act_idx_ent_lnk_ref_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ent_lnk_ref_scope ON public.act_ru_entitylink USING btree (ref_scope_id_, ref_scope_type_, link_type_);


--
-- TOC entry 6795 (class 1259 OID 369798)
-- Name: act_idx_ent_lnk_root_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ent_lnk_root_scope ON public.act_ru_entitylink USING btree (root_scope_id_, root_scope_type_, link_type_);


--
-- TOC entry 6796 (class 1259 OID 369799)
-- Name: act_idx_ent_lnk_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ent_lnk_scope ON public.act_ru_entitylink USING btree (scope_id_, scope_type_, link_type_);


--
-- TOC entry 6797 (class 1259 OID 369800)
-- Name: act_idx_ent_lnk_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ent_lnk_scope_def ON public.act_ru_entitylink USING btree (scope_definition_id_, scope_type_, link_type_);


--
-- TOC entry 6889 (class 1259 OID 369801)
-- Name: act_idx_event_def_uniq; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX act_idx_event_def_uniq ON public.flw_event_definition USING btree (key_, version_, tenant_id_);


--
-- TOC entry 6800 (class 1259 OID 369802)
-- Name: act_idx_event_subscr; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_event_subscr ON public.act_ru_event_subscr USING btree (execution_id_);


--
-- TOC entry 6801 (class 1259 OID 369803)
-- Name: act_idx_event_subscr_config_; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_event_subscr_config_ ON public.act_ru_event_subscr USING btree (configuration_);


--
-- TOC entry 6804 (class 1259 OID 369804)
-- Name: act_idx_exe_parent; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_exe_parent ON public.act_ru_execution USING btree (parent_id_);


--
-- TOC entry 6805 (class 1259 OID 369805)
-- Name: act_idx_exe_procdef; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_exe_procdef ON public.act_ru_execution USING btree (proc_def_id_);


--
-- TOC entry 6806 (class 1259 OID 369806)
-- Name: act_idx_exe_procinst; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_exe_procinst ON public.act_ru_execution USING btree (proc_inst_id_);


--
-- TOC entry 6807 (class 1259 OID 369807)
-- Name: act_idx_exe_root; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_exe_root ON public.act_ru_execution USING btree (root_proc_inst_id_);


--
-- TOC entry 6808 (class 1259 OID 369808)
-- Name: act_idx_exe_super; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_exe_super ON public.act_ru_execution USING btree (super_exec_);


--
-- TOC entry 6809 (class 1259 OID 369809)
-- Name: act_idx_exec_buskey; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_exec_buskey ON public.act_ru_execution USING btree (business_key_);


--
-- TOC entry 6810 (class 1259 OID 369810)
-- Name: act_idx_exec_ref_id_; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_exec_ref_id_ ON public.act_ru_execution USING btree (reference_id_);


--
-- TOC entry 6816 (class 1259 OID 369811)
-- Name: act_idx_external_job_correlation_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_external_job_correlation_id ON public.act_ru_external_job USING btree (correlation_id_);


--
-- TOC entry 6817 (class 1259 OID 369812)
-- Name: act_idx_external_job_custom_values_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_external_job_custom_values_id ON public.act_ru_external_job USING btree (custom_values_id_);


--
-- TOC entry 6818 (class 1259 OID 369813)
-- Name: act_idx_external_job_exception_stack_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_external_job_exception_stack_id ON public.act_ru_external_job USING btree (exception_stack_id_);


--
-- TOC entry 6681 (class 1259 OID 369814)
-- Name: act_idx_hi_act_inst_end; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_act_inst_end ON public.act_hi_actinst USING btree (end_time_);


--
-- TOC entry 6682 (class 1259 OID 369815)
-- Name: act_idx_hi_act_inst_exec; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_act_inst_exec ON public.act_hi_actinst USING btree (execution_id_, act_id_);


--
-- TOC entry 6683 (class 1259 OID 369816)
-- Name: act_idx_hi_act_inst_procinst; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_act_inst_procinst ON public.act_hi_actinst USING btree (proc_inst_id_, act_id_);


--
-- TOC entry 6684 (class 1259 OID 369817)
-- Name: act_idx_hi_act_inst_start; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_act_inst_start ON public.act_hi_actinst USING btree (start_time_);


--
-- TOC entry 6691 (class 1259 OID 369818)
-- Name: act_idx_hi_detail_act_inst; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_detail_act_inst ON public.act_hi_detail USING btree (act_inst_id_);


--
-- TOC entry 6692 (class 1259 OID 369819)
-- Name: act_idx_hi_detail_name; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_detail_name ON public.act_hi_detail USING btree (name_);


--
-- TOC entry 6693 (class 1259 OID 369820)
-- Name: act_idx_hi_detail_proc_inst; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_detail_proc_inst ON public.act_hi_detail USING btree (proc_inst_id_);


--
-- TOC entry 6694 (class 1259 OID 369821)
-- Name: act_idx_hi_detail_task_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_detail_task_id ON public.act_hi_detail USING btree (task_id_);


--
-- TOC entry 6695 (class 1259 OID 369822)
-- Name: act_idx_hi_detail_time; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_detail_time ON public.act_hi_detail USING btree (time_);


--
-- TOC entry 6698 (class 1259 OID 369823)
-- Name: act_idx_hi_ent_lnk_ref_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_ent_lnk_ref_scope ON public.act_hi_entitylink USING btree (ref_scope_id_, ref_scope_type_, link_type_);


--
-- TOC entry 6699 (class 1259 OID 369824)
-- Name: act_idx_hi_ent_lnk_root_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_ent_lnk_root_scope ON public.act_hi_entitylink USING btree (root_scope_id_, root_scope_type_, link_type_);


--
-- TOC entry 6700 (class 1259 OID 369825)
-- Name: act_idx_hi_ent_lnk_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_ent_lnk_scope ON public.act_hi_entitylink USING btree (scope_id_, scope_type_, link_type_);


--
-- TOC entry 6701 (class 1259 OID 369826)
-- Name: act_idx_hi_ent_lnk_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_ent_lnk_scope_def ON public.act_hi_entitylink USING btree (scope_definition_id_, scope_type_, link_type_);


--
-- TOC entry 6704 (class 1259 OID 369827)
-- Name: act_idx_hi_ident_lnk_procinst; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_ident_lnk_procinst ON public.act_hi_identitylink USING btree (proc_inst_id_);


--
-- TOC entry 6705 (class 1259 OID 369828)
-- Name: act_idx_hi_ident_lnk_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_ident_lnk_scope ON public.act_hi_identitylink USING btree (scope_id_, scope_type_);


--
-- TOC entry 6706 (class 1259 OID 369829)
-- Name: act_idx_hi_ident_lnk_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_ident_lnk_scope_def ON public.act_hi_identitylink USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 6707 (class 1259 OID 369830)
-- Name: act_idx_hi_ident_lnk_sub_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_ident_lnk_sub_scope ON public.act_hi_identitylink USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 6708 (class 1259 OID 369831)
-- Name: act_idx_hi_ident_lnk_task; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_ident_lnk_task ON public.act_hi_identitylink USING btree (task_id_);


--
-- TOC entry 6709 (class 1259 OID 369832)
-- Name: act_idx_hi_ident_lnk_user; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_ident_lnk_user ON public.act_hi_identitylink USING btree (user_id_);


--
-- TOC entry 6714 (class 1259 OID 369833)
-- Name: act_idx_hi_pro_i_buskey; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_pro_i_buskey ON public.act_hi_procinst USING btree (business_key_);


--
-- TOC entry 6715 (class 1259 OID 369834)
-- Name: act_idx_hi_pro_inst_end; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_pro_inst_end ON public.act_hi_procinst USING btree (end_time_);


--
-- TOC entry 6726 (class 1259 OID 369835)
-- Name: act_idx_hi_procvar_exe; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_procvar_exe ON public.act_hi_varinst USING btree (execution_id_);


--
-- TOC entry 6727 (class 1259 OID 369836)
-- Name: act_idx_hi_procvar_name_type; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_procvar_name_type ON public.act_hi_varinst USING btree (name_, var_type_);


--
-- TOC entry 6728 (class 1259 OID 369837)
-- Name: act_idx_hi_procvar_proc_inst; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_procvar_proc_inst ON public.act_hi_varinst USING btree (proc_inst_id_);


--
-- TOC entry 6729 (class 1259 OID 369838)
-- Name: act_idx_hi_procvar_task_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_procvar_task_id ON public.act_hi_varinst USING btree (task_id_);


--
-- TOC entry 6718 (class 1259 OID 369839)
-- Name: act_idx_hi_task_inst_procinst; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_task_inst_procinst ON public.act_hi_taskinst USING btree (proc_inst_id_);


--
-- TOC entry 6719 (class 1259 OID 369840)
-- Name: act_idx_hi_task_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_task_scope ON public.act_hi_taskinst USING btree (scope_id_, scope_type_);


--
-- TOC entry 6720 (class 1259 OID 369841)
-- Name: act_idx_hi_task_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_task_scope_def ON public.act_hi_taskinst USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 6721 (class 1259 OID 369842)
-- Name: act_idx_hi_task_sub_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_task_sub_scope ON public.act_hi_taskinst USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 6730 (class 1259 OID 369843)
-- Name: act_idx_hi_var_scope_id_type; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_var_scope_id_type ON public.act_hi_varinst USING btree (scope_id_, scope_type_);


--
-- TOC entry 6731 (class 1259 OID 369844)
-- Name: act_idx_hi_var_sub_id_type; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_var_sub_id_type ON public.act_hi_varinst USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 6824 (class 1259 OID 369845)
-- Name: act_idx_ident_lnk_group; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ident_lnk_group ON public.act_ru_identitylink USING btree (group_id_);


--
-- TOC entry 6825 (class 1259 OID 369846)
-- Name: act_idx_ident_lnk_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ident_lnk_scope ON public.act_ru_identitylink USING btree (scope_id_, scope_type_);


--
-- TOC entry 6826 (class 1259 OID 369847)
-- Name: act_idx_ident_lnk_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ident_lnk_scope_def ON public.act_ru_identitylink USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 6827 (class 1259 OID 369848)
-- Name: act_idx_ident_lnk_sub_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ident_lnk_sub_scope ON public.act_ru_identitylink USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 6828 (class 1259 OID 369849)
-- Name: act_idx_ident_lnk_user; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ident_lnk_user ON public.act_ru_identitylink USING btree (user_id_);


--
-- TOC entry 6829 (class 1259 OID 369850)
-- Name: act_idx_idl_procinst; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_idl_procinst ON public.act_ru_identitylink USING btree (proc_inst_id_);


--
-- TOC entry 6833 (class 1259 OID 369851)
-- Name: act_idx_job_correlation_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_job_correlation_id ON public.act_ru_job USING btree (correlation_id_);


--
-- TOC entry 6834 (class 1259 OID 369852)
-- Name: act_idx_job_custom_values_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_job_custom_values_id ON public.act_ru_job USING btree (custom_values_id_);


--
-- TOC entry 6835 (class 1259 OID 369853)
-- Name: act_idx_job_exception_stack_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_job_exception_stack_id ON public.act_ru_job USING btree (exception_stack_id_);


--
-- TOC entry 6836 (class 1259 OID 369854)
-- Name: act_idx_job_execution_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_job_execution_id ON public.act_ru_job USING btree (execution_id_);


--
-- TOC entry 6837 (class 1259 OID 369855)
-- Name: act_idx_job_proc_def_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_job_proc_def_id ON public.act_ru_job USING btree (proc_def_id_);


--
-- TOC entry 6838 (class 1259 OID 369856)
-- Name: act_idx_job_process_instance_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_job_process_instance_id ON public.act_ru_job USING btree (process_instance_id_);


--
-- TOC entry 6839 (class 1259 OID 369857)
-- Name: act_idx_job_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_job_scope ON public.act_ru_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 6840 (class 1259 OID 369858)
-- Name: act_idx_job_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_job_scope_def ON public.act_ru_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 6841 (class 1259 OID 369859)
-- Name: act_idx_job_sub_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_job_sub_scope ON public.act_ru_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 6740 (class 1259 OID 369860)
-- Name: act_idx_memb_group; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_memb_group ON public.act_id_membership USING btree (group_id_);


--
-- TOC entry 6741 (class 1259 OID 369861)
-- Name: act_idx_memb_user; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_memb_user ON public.act_id_membership USING btree (user_id_);


--
-- TOC entry 6765 (class 1259 OID 369862)
-- Name: act_idx_model_deployment; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_model_deployment ON public.act_re_model USING btree (deployment_id_);


--
-- TOC entry 6766 (class 1259 OID 369863)
-- Name: act_idx_model_source; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_model_source ON public.act_re_model USING btree (editor_source_value_id_);


--
-- TOC entry 6767 (class 1259 OID 369864)
-- Name: act_idx_model_source_extra; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_model_source_extra ON public.act_re_model USING btree (editor_source_extra_value_id_);


--
-- TOC entry 6748 (class 1259 OID 369865)
-- Name: act_idx_priv_group; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_priv_group ON public.act_id_priv_mapping USING btree (group_id_);


--
-- TOC entry 6749 (class 1259 OID 369866)
-- Name: act_idx_priv_mapping; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_priv_mapping ON public.act_id_priv_mapping USING btree (priv_id_);


--
-- TOC entry 6750 (class 1259 OID 369867)
-- Name: act_idx_priv_user; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_priv_user ON public.act_id_priv_mapping USING btree (user_id_);


--
-- TOC entry 6757 (class 1259 OID 369868)
-- Name: act_idx_procdef_info_json; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_procdef_info_json ON public.act_procdef_info USING btree (info_json_id_);


--
-- TOC entry 6758 (class 1259 OID 369869)
-- Name: act_idx_procdef_info_proc; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_procdef_info_proc ON public.act_procdef_info USING btree (proc_def_id_);


--
-- TOC entry 6774 (class 1259 OID 369870)
-- Name: act_idx_ru_acti_end; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ru_acti_end ON public.act_ru_actinst USING btree (end_time_);


--
-- TOC entry 6775 (class 1259 OID 369871)
-- Name: act_idx_ru_acti_exec; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ru_acti_exec ON public.act_ru_actinst USING btree (execution_id_);


--
-- TOC entry 6776 (class 1259 OID 369872)
-- Name: act_idx_ru_acti_exec_act; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ru_acti_exec_act ON public.act_ru_actinst USING btree (execution_id_, act_id_);


--
-- TOC entry 6777 (class 1259 OID 369873)
-- Name: act_idx_ru_acti_proc; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ru_acti_proc ON public.act_ru_actinst USING btree (proc_inst_id_);


--
-- TOC entry 6778 (class 1259 OID 369874)
-- Name: act_idx_ru_acti_proc_act; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ru_acti_proc_act ON public.act_ru_actinst USING btree (proc_inst_id_, act_id_);


--
-- TOC entry 6779 (class 1259 OID 369875)
-- Name: act_idx_ru_acti_start; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ru_acti_start ON public.act_ru_actinst USING btree (start_time_);


--
-- TOC entry 6780 (class 1259 OID 369876)
-- Name: act_idx_ru_acti_task; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ru_acti_task ON public.act_ru_actinst USING btree (task_id_);


--
-- TOC entry 6876 (class 1259 OID 369877)
-- Name: act_idx_ru_var_scope_id_type; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ru_var_scope_id_type ON public.act_ru_variable USING btree (scope_id_, scope_type_);


--
-- TOC entry 6877 (class 1259 OID 369878)
-- Name: act_idx_ru_var_sub_id_type; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ru_var_sub_id_type ON public.act_ru_variable USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 6844 (class 1259 OID 369879)
-- Name: act_idx_sjob_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_sjob_scope ON public.act_ru_suspended_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 6845 (class 1259 OID 369880)
-- Name: act_idx_sjob_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_sjob_scope_def ON public.act_ru_suspended_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 6846 (class 1259 OID 369881)
-- Name: act_idx_sjob_sub_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_sjob_sub_scope ON public.act_ru_suspended_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 6847 (class 1259 OID 369882)
-- Name: act_idx_suspended_job_correlation_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_suspended_job_correlation_id ON public.act_ru_suspended_job USING btree (correlation_id_);


--
-- TOC entry 6848 (class 1259 OID 369883)
-- Name: act_idx_suspended_job_custom_values_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_suspended_job_custom_values_id ON public.act_ru_suspended_job USING btree (custom_values_id_);


--
-- TOC entry 6849 (class 1259 OID 369884)
-- Name: act_idx_suspended_job_exception_stack_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_suspended_job_exception_stack_id ON public.act_ru_suspended_job USING btree (exception_stack_id_);


--
-- TOC entry 6850 (class 1259 OID 369885)
-- Name: act_idx_suspended_job_execution_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_suspended_job_execution_id ON public.act_ru_suspended_job USING btree (execution_id_);


--
-- TOC entry 6851 (class 1259 OID 369886)
-- Name: act_idx_suspended_job_proc_def_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_suspended_job_proc_def_id ON public.act_ru_suspended_job USING btree (proc_def_id_);


--
-- TOC entry 6852 (class 1259 OID 369887)
-- Name: act_idx_suspended_job_process_instance_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_suspended_job_process_instance_id ON public.act_ru_suspended_job USING btree (process_instance_id_);


--
-- TOC entry 6855 (class 1259 OID 369888)
-- Name: act_idx_task_create; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_task_create ON public.act_ru_task USING btree (create_time_);


--
-- TOC entry 6856 (class 1259 OID 369889)
-- Name: act_idx_task_exec; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_task_exec ON public.act_ru_task USING btree (execution_id_);


--
-- TOC entry 6857 (class 1259 OID 369890)
-- Name: act_idx_task_procdef; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_task_procdef ON public.act_ru_task USING btree (proc_def_id_);


--
-- TOC entry 6858 (class 1259 OID 369891)
-- Name: act_idx_task_procinst; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_task_procinst ON public.act_ru_task USING btree (proc_inst_id_);


--
-- TOC entry 6859 (class 1259 OID 369892)
-- Name: act_idx_task_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_task_scope ON public.act_ru_task USING btree (scope_id_, scope_type_);


--
-- TOC entry 6860 (class 1259 OID 369893)
-- Name: act_idx_task_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_task_scope_def ON public.act_ru_task USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 6861 (class 1259 OID 369894)
-- Name: act_idx_task_sub_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_task_sub_scope ON public.act_ru_task USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 6864 (class 1259 OID 369895)
-- Name: act_idx_timer_job_correlation_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_timer_job_correlation_id ON public.act_ru_timer_job USING btree (correlation_id_);


--
-- TOC entry 6865 (class 1259 OID 369896)
-- Name: act_idx_timer_job_custom_values_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_timer_job_custom_values_id ON public.act_ru_timer_job USING btree (custom_values_id_);


--
-- TOC entry 6866 (class 1259 OID 369897)
-- Name: act_idx_timer_job_duedate; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_timer_job_duedate ON public.act_ru_timer_job USING btree (duedate_);


--
-- TOC entry 6867 (class 1259 OID 369898)
-- Name: act_idx_timer_job_exception_stack_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_timer_job_exception_stack_id ON public.act_ru_timer_job USING btree (exception_stack_id_);


--
-- TOC entry 6868 (class 1259 OID 369899)
-- Name: act_idx_timer_job_execution_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_timer_job_execution_id ON public.act_ru_timer_job USING btree (execution_id_);


--
-- TOC entry 6869 (class 1259 OID 369900)
-- Name: act_idx_timer_job_proc_def_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_timer_job_proc_def_id ON public.act_ru_timer_job USING btree (proc_def_id_);


--
-- TOC entry 6870 (class 1259 OID 369901)
-- Name: act_idx_timer_job_process_instance_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_timer_job_process_instance_id ON public.act_ru_timer_job USING btree (process_instance_id_);


--
-- TOC entry 6871 (class 1259 OID 369902)
-- Name: act_idx_tjob_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_tjob_scope ON public.act_ru_timer_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 6872 (class 1259 OID 369903)
-- Name: act_idx_tjob_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_tjob_scope_def ON public.act_ru_timer_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 6873 (class 1259 OID 369904)
-- Name: act_idx_tjob_sub_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_tjob_sub_scope ON public.act_ru_timer_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 6830 (class 1259 OID 369905)
-- Name: act_idx_tskass_task; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_tskass_task ON public.act_ru_identitylink USING btree (task_id_);


--
-- TOC entry 6878 (class 1259 OID 369906)
-- Name: act_idx_var_bytearray; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_var_bytearray ON public.act_ru_variable USING btree (bytearray_id_);


--
-- TOC entry 6879 (class 1259 OID 369907)
-- Name: act_idx_var_exe; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_var_exe ON public.act_ru_variable USING btree (execution_id_);


--
-- TOC entry 6880 (class 1259 OID 369908)
-- Name: act_idx_var_procinst; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_var_procinst ON public.act_ru_variable USING btree (proc_inst_id_);


--
-- TOC entry 6881 (class 1259 OID 369909)
-- Name: act_idx_variable_task_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_variable_task_id ON public.act_ru_variable USING btree (task_id_);


--
-- TOC entry 6898 (class 1259 OID 369910)
-- Name: flw_idx_batch_part; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX flw_idx_batch_part ON public.flw_ru_batch_part USING btree (batch_id_);


--
-- TOC entry 6942 (class 1259 OID 371330)
-- Name: idx_account_value_clv; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_account_value_clv ON public.crm_account_value USING btree (tenant_id, net_clv DESC);


--
-- TOC entry 6943 (class 1259 OID 371329)
-- Name: idx_account_value_tenant_tier; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_account_value_tenant_tier ON public.crm_account_value USING btree (tenant_id, tier);


--
-- TOC entry 7090 (class 1259 OID 408430)
-- Name: idx_act_unmatched_identity; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_act_unmatched_identity ON public.crm_activity_unmatched USING btree (identity_type, identity_value, app_id);


--
-- TOC entry 7091 (class 1259 OID 404221)
-- Name: idx_act_unmatched_retry; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_act_unmatched_retry ON public.crm_activity_unmatched USING btree (next_retry_at, abandoned) WHERE (abandoned = false);


--
-- TOC entry 7092 (class 1259 OID 404320)
-- Name: idx_act_unmatched_union_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_act_unmatched_union_id ON public.crm_activity_unmatched USING btree (union_id) WHERE (union_id IS NOT NULL);


--
-- TOC entry 6368 (class 1259 OID 404151)
-- Name: idx_activity_channel_date; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_activity_channel_date ON public.crm_activity USING btree (channel, started_date) WHERE (channel IS NOT NULL);


--
-- TOC entry 6369 (class 1259 OID 404461)
-- Name: idx_activity_context_event_id; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_activity_context_event_id ON public.crm_activity USING btree (((context ->> 'crm_event_id'::text))) WHERE ((context ->> 'crm_event_id'::text) IS NOT NULL);


--
-- TOC entry 6370 (class 1259 OID 380095)
-- Name: idx_activity_event_type; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_activity_event_type ON public.crm_activity USING btree (event_type);


--
-- TOC entry 6371 (class 1259 OID 404150)
-- Name: idx_activity_event_type_date; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_activity_event_type_date ON public.crm_activity USING btree (event_type, started_date);


--
-- TOC entry 6372 (class 1259 OID 380097)
-- Name: idx_activity_importance; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_activity_importance ON public.crm_activity USING btree (importance);


--
-- TOC entry 6373 (class 1259 OID 404153)
-- Name: idx_activity_importance_milestone; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_activity_importance_milestone ON public.crm_activity USING btree (subject_type, subject_id, started_date) WHERE (importance >= 70);


--
-- TOC entry 6374 (class 1259 OID 404154)
-- Name: idx_activity_payload_gin; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_activity_payload_gin ON public.crm_activity USING gin (payload jsonb_path_ops);


--
-- TOC entry 6375 (class 1259 OID 380098)
-- Name: idx_activity_related; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_activity_related ON public.crm_activity USING btree (relate_type, relate_id);


--
-- TOC entry 6376 (class 1259 OID 380099)
-- Name: idx_activity_source; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_activity_source ON public.crm_activity USING btree (source);


--
-- TOC entry 6377 (class 1259 OID 404155)
-- Name: idx_activity_started_brin; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_activity_started_brin ON public.crm_activity USING brin (started_date) WITH (pages_per_range='32');


--
-- TOC entry 6378 (class 1259 OID 380096)
-- Name: idx_activity_started_date; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_activity_started_date ON public.crm_activity USING btree (started_date);


--
-- TOC entry 6379 (class 1259 OID 380093)
-- Name: idx_activity_subject; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_activity_subject ON public.crm_activity USING btree (subject_type, subject_id);


--
-- TOC entry 6380 (class 1259 OID 404148)
-- Name: idx_activity_subject_contact; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_activity_subject_contact ON public.crm_activity USING btree (subject_id, started_date DESC) WHERE (((subject_type)::text = 'Contact'::text) AND (started_date IS NOT NULL));


--
-- TOC entry 6381 (class 1259 OID 380094)
-- Name: idx_activity_subject_date; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_activity_subject_date ON public.crm_activity USING btree (subject_type, subject_id, started_date);


--
-- TOC entry 6382 (class 1259 OID 408437)
-- Name: idx_activity_subject_date_desc; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_activity_subject_date_desc ON public.crm_activity USING btree (subject_type, subject_id, started_date DESC) INCLUDE (event_type, event_name, importance, source, create_by);


--
-- TOC entry 6383 (class 1259 OID 404152)
-- Name: idx_activity_unaggregated; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_activity_unaggregated ON public.crm_activity USING btree (started_date) WHERE (is_aggregated = false);


--
-- TOC entry 6384 (class 1259 OID 408441)
-- Name: idx_activity_wechat_mp_app_id_expr; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_activity_wechat_mp_app_id_expr ON public.crm_activity USING btree (COALESCE((context ->> 'app_id'::text), (context ->> 'appId'::text))) WHERE ((channel)::text = 'wechat_mp'::text);


--
-- TOC entry 6385 (class 1259 OID 408440)
-- Name: idx_activity_wechat_mp_event_time; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_activity_wechat_mp_event_time ON public.crm_activity USING btree (channel, event_type, started_date DESC) WHERE ((channel)::text = 'wechat_mp'::text);


--
-- TOC entry 7204 (class 1259 OID 408191)
-- Name: idx_behavior_daily_query; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_behavior_daily_query ON public.crm_wecom_behavior_daily USING btree (stat_time, userid);


--
-- TOC entry 6397 (class 1259 OID 404704)
-- Name: idx_bulkemail_scheduled_due; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_bulkemail_scheduled_due ON public.crm_bulkemail USING btree (status, scheduled_at) WHERE ((status)::text = 'scheduled'::text);


--
-- TOC entry 7059 (class 1259 OID 380083)
-- Name: idx_callback_event_corp_time; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_callback_event_corp_time ON public.crm_wecom_callback_event USING btree (corp_id, create_time);


--
-- TOC entry 7060 (class 1259 OID 380084)
-- Name: idx_callback_event_type; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_callback_event_type ON public.crm_wecom_callback_event USING btree (corp_id, event, change_type);


--
-- TOC entry 7180 (class 1259 OID 404690)
-- Name: idx_checkin_event_session; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_checkin_event_session ON public.crm_event_check_in USING btree (event_id, session_id);


--
-- TOC entry 7082 (class 1259 OID 404183)
-- Name: idx_cm_churn; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_cm_churn ON public.customer_metrics USING btree (churn_risk_score DESC) WHERE (((lifecycle_stage)::text = 'customer'::text) AND (churn_risk_score IS NOT NULL));


--
-- TOC entry 7083 (class 1259 OID 404181)
-- Name: idx_cm_lifecycle; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_cm_lifecycle ON public.customer_metrics USING btree (lifecycle_stage);


--
-- TOC entry 7084 (class 1259 OID 404182)
-- Name: idx_cm_score; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_cm_score ON public.customer_metrics USING btree (ai_score_latest DESC) WHERE (ai_score_latest IS NOT NULL);


--
-- TOC entry 6408 (class 1259 OID 404709)
-- Name: idx_contact_last_visit; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_contact_last_visit ON public.crm_contact USING btree (last_visit_at);


--
-- TOC entry 7585 (class 1259 OID 417278)
-- Name: idx_contact_service_summary_rev_contact; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_contact_service_summary_rev_contact ON public.crm_contact_service_summary_revision USING btree (tenant_id, contact_id, version DESC);


--
-- TOC entry 7582 (class 1259 OID 417267)
-- Name: idx_contact_service_summary_updated; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_contact_service_summary_updated ON public.crm_contact_service_summary USING btree (tenant_id, updated_at DESC);


--
-- TOC entry 6409 (class 1259 OID 379753)
-- Name: idx_contact_wecom_external_userid; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_contact_wecom_external_userid ON public.crm_contact USING btree (wecom_external_userid) WHERE (wecom_external_userid IS NOT NULL);


--
-- TOC entry 6410 (class 1259 OID 404708)
-- Name: idx_contact_work_email; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_contact_work_email ON public.crm_contact USING btree (work_email) WHERE (work_email IS NOT NULL);


--
-- TOC entry 6998 (class 1259 OID 371832)
-- Name: idx_crm_ai_chat_message_session; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ai_chat_message_session ON public.crm_ai_chat_message USING btree (session_id, create_time);


--
-- TOC entry 6995 (class 1259 OID 371818)
-- Name: idx_crm_ai_chat_session_user; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ai_chat_session_user ON public.crm_ai_chat_session USING btree (tenant_id, user_id, update_time DESC) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 6981 (class 1259 OID 371752)
-- Name: idx_crm_ai_model_config_tenant; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ai_model_config_tenant ON public.crm_ai_model_config USING btree (tenant_id);


--
-- TOC entry 7027 (class 1259 OID 379555)
-- Name: idx_crm_ai_playbook_run_playbook; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ai_playbook_run_playbook ON public.crm_ai_playbook_run USING btree (playbook_id);


--
-- TOC entry 7028 (class 1259 OID 379556)
-- Name: idx_crm_ai_playbook_run_subject; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ai_playbook_run_subject ON public.crm_ai_playbook_run USING btree (tenant_id, subject_type, subject_id);


--
-- TOC entry 7029 (class 1259 OID 379554)
-- Name: idx_crm_ai_playbook_run_tenant; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ai_playbook_run_tenant ON public.crm_ai_playbook_run USING btree (tenant_id);


--
-- TOC entry 7021 (class 1259 OID 379540)
-- Name: idx_crm_ai_playbook_step_playbook; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ai_playbook_step_playbook ON public.crm_ai_skill_playbook_step USING btree (playbook_id);


--
-- TOC entry 7022 (class 1259 OID 379541)
-- Name: idx_crm_ai_playbook_step_skill; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ai_playbook_step_skill ON public.crm_ai_skill_playbook_step USING btree (skill_id);


--
-- TOC entry 7016 (class 1259 OID 379523)
-- Name: idx_crm_ai_playbook_tenant; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ai_playbook_tenant ON public.crm_ai_skill_playbook USING btree (tenant_id);


--
-- TOC entry 7004 (class 1259 OID 379487)
-- Name: idx_crm_ai_skill_tenant; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ai_skill_tenant ON public.crm_ai_skill USING btree (tenant_id);


--
-- TOC entry 7005 (class 1259 OID 379488)
-- Name: idx_crm_ai_skill_tenant_domain; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ai_skill_tenant_domain ON public.crm_ai_skill USING btree (tenant_id, domain);


--
-- TOC entry 7006 (class 1259 OID 379489)
-- Name: idx_crm_ai_skill_tenant_track; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ai_skill_tenant_track ON public.crm_ai_skill USING btree (tenant_id, track_key);


--
-- TOC entry 7001 (class 1259 OID 371841)
-- Name: idx_crm_analytics_query_log_tenant_time; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_analytics_query_log_tenant_time ON public.crm_analytics_query_log USING btree (tenant_id, create_time DESC);


--
-- TOC entry 7532 (class 1259 OID 413424)
-- Name: idx_crm_booking_contact; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_booking_contact ON public.crm_booking USING btree (contact_id);


--
-- TOC entry 7533 (class 1259 OID 413423)
-- Name: idx_crm_booking_lead; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_booking_lead ON public.crm_booking USING btree (lead_id);


--
-- TOC entry 7534 (class 1259 OID 413422)
-- Name: idx_crm_booking_org_start; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_booking_org_start ON public.crm_booking USING btree (org_id, start_time);


--
-- TOC entry 7535 (class 1259 OID 413421)
-- Name: idx_crm_booking_start; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_booking_start ON public.crm_booking USING btree (start_time);


--
-- TOC entry 7536 (class 1259 OID 413425)
-- Name: idx_crm_booking_status_start; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_booking_status_start ON public.crm_booking USING btree (status, start_time);


--
-- TOC entry 7315 (class 1259 OID 409268)
-- Name: idx_crm_capture_status; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_capture_status ON public.crm_capture USING btree (status);


--
-- TOC entry 7316 (class 1259 OID 409269)
-- Name: idx_crm_capture_template; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_capture_template ON public.crm_capture USING btree (template_id);


--
-- TOC entry 7375 (class 1259 OID 411721)
-- Name: idx_crm_ce_industry; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ce_industry ON public.crm_chinese_enterprise USING btree (industry) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7376 (class 1259 OID 411723)
-- Name: idx_crm_ce_investment_mode; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ce_investment_mode ON public.crm_chinese_enterprise USING btree (investment_mode) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7377 (class 1259 OID 411720)
-- Name: idx_crm_ce_parent_province; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ce_parent_province ON public.crm_chinese_enterprise USING btree (parent_province_china) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7378 (class 1259 OID 411718)
-- Name: idx_crm_ce_park_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ce_park_id ON public.crm_chinese_enterprise USING btree (park_id) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7379 (class 1259 OID 411719)
-- Name: idx_crm_ce_province_vn; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ce_province_vn ON public.crm_chinese_enterprise USING btree (province_vietnam) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7380 (class 1259 OID 411724)
-- Name: idx_crm_ce_status; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ce_status ON public.crm_chinese_enterprise USING btree (status) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7381 (class 1259 OID 411722)
-- Name: idx_crm_ce_stock_listing; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ce_stock_listing ON public.crm_chinese_enterprise USING btree (stock_listing_status) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 6411 (class 1259 OID 408654)
-- Name: idx_crm_contact_source_lead; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_crm_contact_source_lead ON public.crm_contact USING btree (source_lead_id);


--
-- TOC entry 6412 (class 1259 OID 411102)
-- Name: idx_crm_contact_tag_ids; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_crm_contact_tag_ids ON public.crm_contact USING gin (public.crm_tag_ids((tag)::text));


--
-- TOC entry 6968 (class 1259 OID 371648)
-- Name: idx_crm_data_table_row_table; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_data_table_row_table ON public.crm_data_table_row USING btree (data_table_id);


--
-- TOC entry 6969 (class 1259 OID 371649)
-- Name: idx_crm_data_table_row_table_created; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_data_table_row_table_created ON public.crm_data_table_row USING btree (data_table_id, create_time DESC);


--
-- TOC entry 6959 (class 1259 OID 371650)
-- Name: idx_crm_data_table_tenant; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_data_table_tenant ON public.crm_data_table USING btree (tenant_id);


--
-- TOC entry 6986 (class 1259 OID 371793)
-- Name: idx_crm_email_analysis_lookup; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_email_analysis_lookup ON public.crm_email_analysis USING btree (tenant_id, message_external_id, locale) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 6987 (class 1259 OID 371794)
-- Name: idx_crm_email_analysis_user; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_email_analysis_user ON public.crm_email_analysis USING btree (tenant_id, user_id, update_time DESC);


--
-- TOC entry 7172 (class 1259 OID 408539)
-- Name: idx_crm_event_registration_contact_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_event_registration_contact_id ON public.crm_event_registration USING btree (tenant_id, event_id, contact_id, session_id) WHERE (contact_id IS NOT NULL);


--
-- TOC entry 7292 (class 1259 OID 409063)
-- Name: idx_crm_event_speaker_event_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_event_speaker_event_id ON public.crm_event_speaker USING btree (event_id);


--
-- TOC entry 6431 (class 1259 OID 408529)
-- Name: idx_crm_event_wechat_mp_app_id; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_crm_event_wechat_mp_app_id ON public.crm_event USING btree (wechat_mp_app_id) WHERE (wechat_mp_app_id IS NOT NULL);


--
-- TOC entry 6992 (class 1259 OID 371809)
-- Name: idx_crm_followup_cadence_tenant; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_followup_cadence_tenant ON public.crm_follow_up_cadence USING btree (tenant_id);


--
-- TOC entry 7384 (class 1259 OID 411738)
-- Name: idx_crm_ga_category; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ga_category ON public.crm_government_agency USING btree (category) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7385 (class 1259 OID 411737)
-- Name: idx_crm_ga_level; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ga_level ON public.crm_government_agency USING btree (level) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7386 (class 1259 OID 411739)
-- Name: idx_crm_ga_province; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ga_province ON public.crm_government_agency USING btree (province) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7076 (class 1259 OID 404137)
-- Name: idx_crm_identity_contact; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_identity_contact ON public.crm_customer_identity USING btree (contact_id);


--
-- TOC entry 7360 (class 1259 OID 411489)
-- Name: idx_crm_ip_is_sez; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ip_is_sez ON public.crm_industrial_park USING btree (is_sez) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7361 (class 1259 OID 411488)
-- Name: idx_crm_ip_lat_lng; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ip_lat_lng ON public.crm_industrial_park USING btree (lat, lng) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7362 (class 1259 OID 411486)
-- Name: idx_crm_ip_park_level; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ip_park_level ON public.crm_industrial_park USING btree (park_level) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7363 (class 1259 OID 411485)
-- Name: idx_crm_ip_province; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ip_province ON public.crm_industrial_park USING btree (province) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7364 (class 1259 OID 411484)
-- Name: idx_crm_ip_region; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ip_region ON public.crm_industrial_park USING btree (region) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7365 (class 1259 OID 411487)
-- Name: idx_crm_ip_verification; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ip_verification ON public.crm_industrial_park USING btree (verification_status) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7326 (class 1259 OID 410964)
-- Name: idx_crm_kb_chunk_document; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_kb_chunk_document ON public.crm_kb_chunk USING btree (document_id);


--
-- TOC entry 7327 (class 1259 OID 410965)
-- Name: idx_crm_kb_chunk_embedding_hnsw; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_kb_chunk_embedding_hnsw ON public.crm_kb_chunk USING hnsw (embedding public.vector_cosine_ops);


--
-- TOC entry 7321 (class 1259 OID 410948)
-- Name: idx_crm_kb_document_index_status; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_kb_document_index_status ON public.crm_kb_document USING btree (index_status) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7322 (class 1259 OID 410947)
-- Name: idx_crm_kb_document_status_cat; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_kb_document_status_cat ON public.crm_kb_document USING btree (status, category) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 6448 (class 1259 OID 413373)
-- Name: idx_crm_lead_assigned_followup; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_crm_lead_assigned_followup ON public.crm_lead USING btree (status, last_followup_at) WHERE (lower(TRIM(BOTH FROM COALESCE(status, ''::character varying))) = 'assigned'::text);


--
-- TOC entry 6449 (class 1259 OID 408476)
-- Name: idx_crm_lead_contact_id; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_crm_lead_contact_id ON public.crm_lead USING btree (contact_id);


--
-- TOC entry 6450 (class 1259 OID 411103)
-- Name: idx_crm_lead_tag_ids; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_crm_lead_tag_ids ON public.crm_lead USING gin (public.crm_tag_ids((tag)::text));


--
-- TOC entry 7032 (class 1259 OID 379617)
-- Name: idx_crm_mcp_api_key_tenant_enabled; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_mcp_api_key_tenant_enabled ON public.crm_mcp_api_key USING btree (tenant_id, enabled);


--
-- TOC entry 7039 (class 1259 OID 379629)
-- Name: idx_crm_mcp_audit_log_tenant_time; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_mcp_audit_log_tenant_time ON public.crm_mcp_audit_log USING btree (tenant_id, create_time DESC);


--
-- TOC entry 7011 (class 1259 OID 379506)
-- Name: idx_crm_mcp_server_tenant; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_mcp_server_tenant ON public.crm_mcp_server USING btree (tenant_id);


--
-- TOC entry 7403 (class 1259 OID 412790)
-- Name: idx_crm_me_event_type; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_me_event_type ON public.crm_market_event USING btree (event_type) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7404 (class 1259 OID 412792)
-- Name: idx_crm_me_province; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_me_province ON public.crm_market_event USING btree (province) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7405 (class 1259 OID 412793)
-- Name: idx_crm_me_start_date; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_me_start_date ON public.crm_market_event USING btree (start_date) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7406 (class 1259 OID 412791)
-- Name: idx_crm_me_status; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_me_status ON public.crm_market_event USING btree (status) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7441 (class 1259 OID 413148)
-- Name: idx_crm_messenger_config_page; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_messenger_config_page ON public.crm_messenger_config USING btree (tenant_id, page_id);


--
-- TOC entry 7452 (class 1259 OID 413179)
-- Name: idx_crm_messenger_conversation_tenant_contact; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_messenger_conversation_tenant_contact ON public.crm_messenger_conversation USING btree (tenant_id, contact_id);


--
-- TOC entry 7453 (class 1259 OID 413180)
-- Name: idx_crm_messenger_conversation_tenant_lead; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_messenger_conversation_tenant_lead ON public.crm_messenger_conversation USING btree (tenant_id, lead_id);


--
-- TOC entry 7454 (class 1259 OID 413198)
-- Name: idx_crm_messenger_conversation_tenant_pool_last; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_messenger_conversation_tenant_pool_last ON public.crm_messenger_conversation USING btree (tenant_id, last_message_at DESC) WHERE (user_id IS NULL);


--
-- TOC entry 7455 (class 1259 OID 413178)
-- Name: idx_crm_messenger_conversation_tenant_status; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_messenger_conversation_tenant_status ON public.crm_messenger_conversation USING btree (tenant_id, status, last_message_at DESC);


--
-- TOC entry 7456 (class 1259 OID 413197)
-- Name: idx_crm_messenger_conversation_tenant_user_last; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_messenger_conversation_tenant_user_last ON public.crm_messenger_conversation USING btree (tenant_id, user_id, last_message_at DESC);


--
-- TOC entry 7457 (class 1259 OID 413181)
-- Name: idx_crm_messenger_conversation_tenant_window; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_messenger_conversation_tenant_window ON public.crm_messenger_conversation USING btree (tenant_id, last_customer_message_at);


--
-- TOC entry 7446 (class 1259 OID 413163)
-- Name: idx_crm_messenger_event_tenant_event_type; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_messenger_event_tenant_event_type ON public.crm_messenger_webhook_event USING btree (tenant_id, event_type, create_time DESC);


--
-- TOC entry 7447 (class 1259 OID 413162)
-- Name: idx_crm_messenger_event_tenant_message_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_messenger_event_tenant_message_id ON public.crm_messenger_webhook_event USING btree (tenant_id, message_id);


--
-- TOC entry 7448 (class 1259 OID 413161)
-- Name: idx_crm_messenger_event_tenant_psid; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_messenger_event_tenant_psid ON public.crm_messenger_webhook_event USING btree (tenant_id, psid, create_time DESC);


--
-- TOC entry 7449 (class 1259 OID 413160)
-- Name: idx_crm_messenger_event_tenant_status; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_messenger_event_tenant_status ON public.crm_messenger_webhook_event USING btree (tenant_id, status, create_time DESC);


--
-- TOC entry 7462 (class 1259 OID 413192)
-- Name: idx_crm_messenger_msg_conversation; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_messenger_msg_conversation ON public.crm_messenger_message USING btree (conversation_id, send_time, id);


--
-- TOC entry 7463 (class 1259 OID 413193)
-- Name: idx_crm_messenger_msg_tenant_message_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_messenger_msg_tenant_message_id ON public.crm_messenger_message USING btree (tenant_id, message_id);


--
-- TOC entry 7464 (class 1259 OID 413194)
-- Name: idx_crm_messenger_msg_tenant_psid; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_messenger_msg_tenant_psid ON public.crm_messenger_message USING btree (tenant_id, psid, create_time DESC);


--
-- TOC entry 7527 (class 1259 OID 413410)
-- Name: idx_crm_mobile_push_device_user; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_mobile_push_device_user ON public.crm_mobile_push_device USING btree (tenant_id, user_id);


--
-- TOC entry 6481 (class 1259 OID 411248)
-- Name: idx_crm_product_category_id; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_crm_product_category_id ON public.crm_product USING btree (category_id) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7338 (class 1259 OID 411262)
-- Name: idx_crm_product_category_parent; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_product_category_parent ON public.crm_product_category USING btree (parent_id) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 6482 (class 1259 OID 411249)
-- Name: idx_crm_product_keyword; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_crm_product_keyword ON public.crm_product USING btree (product_name, product_code) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7342 (class 1259 OID 411274)
-- Name: idx_crm_product_media_product; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_product_media_product ON public.crm_product_media USING btree (product_id) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 6483 (class 1259 OID 411247)
-- Name: idx_crm_product_status; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_crm_product_status ON public.crm_product USING btree (status) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7389 (class 1259 OID 411752)
-- Name: idx_crm_ps_background; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ps_background ON public.crm_professional_service USING btree (background) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7390 (class 1259 OID 411751)
-- Name: idx_crm_ps_category; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ps_category ON public.crm_professional_service USING btree (category) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7391 (class 1259 OID 411753)
-- Name: idx_crm_ps_province; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_ps_province ON public.crm_professional_service USING btree (province) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7421 (class 1259 OID 413037)
-- Name: idx_crm_reminder_link_expires; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_reminder_link_expires ON public.crm_reminder_link USING btree (tenant_id, status, expires_at);


--
-- TOC entry 7422 (class 1259 OID 413036)
-- Name: idx_crm_reminder_link_tenant_ref; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_reminder_link_tenant_ref ON public.crm_reminder_link USING btree (tenant_id, ref_type, ref_id);


--
-- TOC entry 7423 (class 1259 OID 413035)
-- Name: idx_crm_reminder_link_tenant_type; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_reminder_link_tenant_type ON public.crm_reminder_link USING btree (tenant_id, type, create_time DESC);


--
-- TOC entry 7220 (class 1259 OID 408607)
-- Name: idx_crm_sales_script_category; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_crm_sales_script_category ON public.crm_sales_script USING btree (category);


--
-- TOC entry 7221 (class 1259 OID 408608)
-- Name: idx_crm_sales_script_sort; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_crm_sales_script_sort ON public.crm_sales_script USING btree (sort_order DESC, update_time DESC);


--
-- TOC entry 7222 (class 1259 OID 408606)
-- Name: idx_crm_sales_script_type_status; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_crm_sales_script_type_status ON public.crm_sales_script USING btree (script_type, status, del_flag);


--
-- TOC entry 7394 (class 1259 OID 411765)
-- Name: idx_crm_sanc_entity_type; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_sanc_entity_type ON public.crm_sanctioned_entity USING btree (entity_type) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7395 (class 1259 OID 411763)
-- Name: idx_crm_sanc_risk_category; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_sanc_risk_category ON public.crm_sanctioned_entity USING btree (risk_category) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7396 (class 1259 OID 411764)
-- Name: idx_crm_sanc_risk_level; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_sanc_risk_level ON public.crm_sanctioned_entity USING btree (risk_level) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7397 (class 1259 OID 411766)
-- Name: idx_crm_sanc_tax_code; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_sanc_tax_code ON public.crm_sanctioned_entity USING btree (tax_code) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7100 (class 1259 OID 404334)
-- Name: idx_crm_segment_tenant_status; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_segment_tenant_status ON public.crm_segment USING btree (tenant_id, status);


--
-- TOC entry 6976 (class 1259 OID 371708)
-- Name: idx_crm_sem_profile_tenant; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_sem_profile_tenant ON public.crm_semantic_profile USING btree (tenant_id);


--
-- TOC entry 7330 (class 1259 OID 411131)
-- Name: idx_crm_tag_channel_mapping_dict; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_tag_channel_mapping_dict ON public.crm_tag_channel_mapping USING btree (dict_value);


--
-- TOC entry 7335 (class 1259 OID 411149)
-- Name: idx_crm_tag_dispatch_task_status; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_tag_dispatch_task_status ON public.crm_tag_dispatch_task USING btree (status);


--
-- TOC entry 7368 (class 1259 OID 411700)
-- Name: idx_crm_te_country; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_te_country ON public.crm_tenant_enterprise USING btree (country_of_origin) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7369 (class 1259 OID 411699)
-- Name: idx_crm_te_enterprise_type; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_te_enterprise_type ON public.crm_tenant_enterprise USING btree (enterprise_type) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7370 (class 1259 OID 411698)
-- Name: idx_crm_te_industry; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_te_industry ON public.crm_tenant_enterprise USING btree (industry) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7371 (class 1259 OID 411697)
-- Name: idx_crm_te_park_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_te_park_id ON public.crm_tenant_enterprise USING btree (park_id) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7372 (class 1259 OID 411701)
-- Name: idx_crm_te_status; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_te_status ON public.crm_tenant_enterprise USING btree (status) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7467 (class 1259 OID 413217)
-- Name: idx_crm_webchat_config_token; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_webchat_config_token ON public.crm_webchat_config USING btree (website_token);


--
-- TOC entry 7481 (class 1259 OID 413250)
-- Name: idx_crm_webchat_conv_contact; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_webchat_conv_contact ON public.crm_webchat_conversation USING btree (tenant_id, contact_id);


--
-- TOC entry 7482 (class 1259 OID 413251)
-- Name: idx_crm_webchat_conv_lead; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_webchat_conv_lead ON public.crm_webchat_conversation USING btree (tenant_id, lead_id);


--
-- TOC entry 7483 (class 1259 OID 413248)
-- Name: idx_crm_webchat_conv_tenant_pool; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_webchat_conv_tenant_pool ON public.crm_webchat_conversation USING btree (tenant_id, user_id, last_message_at DESC);


--
-- TOC entry 7484 (class 1259 OID 413249)
-- Name: idx_crm_webchat_conv_tenant_status; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_webchat_conv_tenant_status ON public.crm_webchat_conversation USING btree (tenant_id, status, last_message_at DESC);


--
-- TOC entry 7488 (class 1259 OID 413265)
-- Name: idx_crm_webchat_msg_conversation; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_webchat_msg_conversation ON public.crm_webchat_message USING btree (conversation_id, create_time, id);


--
-- TOC entry 7489 (class 1259 OID 413266)
-- Name: idx_crm_webchat_msg_visitor; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_webchat_msg_visitor ON public.crm_webchat_message USING btree (tenant_id, visitor_id, create_time DESC);


--
-- TOC entry 7474 (class 1259 OID 413233)
-- Name: idx_crm_webchat_visitor_contact; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_webchat_visitor_contact ON public.crm_webchat_visitor USING btree (tenant_id, contact_id);


--
-- TOC entry 7475 (class 1259 OID 413234)
-- Name: idx_crm_webchat_visitor_lead; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_webchat_visitor_lead ON public.crm_webchat_visitor USING btree (tenant_id, lead_id);


--
-- TOC entry 7476 (class 1259 OID 413232)
-- Name: idx_crm_webchat_visitor_session; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_webchat_visitor_session ON public.crm_webchat_visitor USING btree (tenant_id, website_session_id);


--
-- TOC entry 7541 (class 1259 OID 413446)
-- Name: idx_crm_wecom_cg_chat; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_wecom_cg_chat ON public.crm_wecom_contact_group USING btree (tenant_id, chat_id) WHERE (chat_id IS NOT NULL);


--
-- TOC entry 7542 (class 1259 OID 413445)
-- Name: idx_crm_wecom_cg_status; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_wecom_cg_status ON public.crm_wecom_contact_group USING btree (tenant_id, status);


--
-- TOC entry 7547 (class 1259 OID 413454)
-- Name: idx_crm_wecom_provision_assignee; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_wecom_provision_assignee ON public.crm_wecom_group_provision_task USING btree (tenant_id, status, assignee_userid);


--
-- TOC entry 7553 (class 1259 OID 413481)
-- Name: idx_crm_wecom_reminder_due; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_wecom_reminder_due ON public.crm_wecom_group_reminder USING btree (tenant_id, status, due_at) WHERE ((status)::text = 'pending'::text);


--
-- TOC entry 7550 (class 1259 OID 413469)
-- Name: idx_crm_wecom_reminder_rule_anchor; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_wecom_reminder_rule_anchor ON public.crm_wecom_group_reminder_rule USING btree (tenant_id, enabled, anchor_type) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 6932 (class 1259 OID 371272)
-- Name: idx_crm_wf_auto_tenant; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_wf_auto_tenant ON public.crm_workflow_automation USING btree (tenant_id);


--
-- TOC entry 7261 (class 1259 OID 408983)
-- Name: idx_crm_whatsapp_config_phone_number; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_whatsapp_config_phone_number ON public.crm_whatsapp_config USING btree (tenant_id, phone_number_id);


--
-- TOC entry 7272 (class 1259 OID 409014)
-- Name: idx_crm_whatsapp_conversation_tenant_contact; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_whatsapp_conversation_tenant_contact ON public.crm_whatsapp_conversation USING btree (tenant_id, contact_id);


--
-- TOC entry 7273 (class 1259 OID 409015)
-- Name: idx_crm_whatsapp_conversation_tenant_lead; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_whatsapp_conversation_tenant_lead ON public.crm_whatsapp_conversation USING btree (tenant_id, lead_id);


--
-- TOC entry 7274 (class 1259 OID 412959)
-- Name: idx_crm_whatsapp_conversation_tenant_pool_last; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_whatsapp_conversation_tenant_pool_last ON public.crm_whatsapp_conversation USING btree (tenant_id, last_message_at DESC) WHERE (user_id IS NULL);


--
-- TOC entry 7275 (class 1259 OID 409013)
-- Name: idx_crm_whatsapp_conversation_tenant_status; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_whatsapp_conversation_tenant_status ON public.crm_whatsapp_conversation USING btree (tenant_id, status, last_message_at DESC);


--
-- TOC entry 7276 (class 1259 OID 412958)
-- Name: idx_crm_whatsapp_conversation_tenant_user_last; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_whatsapp_conversation_tenant_user_last ON public.crm_whatsapp_conversation USING btree (tenant_id, user_id, last_message_at DESC);


--
-- TOC entry 7277 (class 1259 OID 409016)
-- Name: idx_crm_whatsapp_conversation_tenant_window; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_whatsapp_conversation_tenant_window ON public.crm_whatsapp_conversation USING btree (tenant_id, last_customer_message_at);


--
-- TOC entry 7266 (class 1259 OID 408998)
-- Name: idx_crm_whatsapp_event_tenant_event_type; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_whatsapp_event_tenant_event_type ON public.crm_whatsapp_webhook_event USING btree (tenant_id, event_type, create_time DESC);


--
-- TOC entry 7267 (class 1259 OID 408997)
-- Name: idx_crm_whatsapp_event_tenant_message_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_whatsapp_event_tenant_message_id ON public.crm_whatsapp_webhook_event USING btree (tenant_id, message_id);


--
-- TOC entry 7268 (class 1259 OID 408995)
-- Name: idx_crm_whatsapp_event_tenant_status; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_whatsapp_event_tenant_status ON public.crm_whatsapp_webhook_event USING btree (tenant_id, status, create_time DESC);


--
-- TOC entry 7269 (class 1259 OID 408996)
-- Name: idx_crm_whatsapp_event_tenant_wa_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_whatsapp_event_tenant_wa_id ON public.crm_whatsapp_webhook_event USING btree (tenant_id, wa_id, create_time DESC);


--
-- TOC entry 7282 (class 1259 OID 409027)
-- Name: idx_crm_whatsapp_msg_conversation; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_whatsapp_msg_conversation ON public.crm_whatsapp_message USING btree (conversation_id, send_time, id);


--
-- TOC entry 7283 (class 1259 OID 409028)
-- Name: idx_crm_whatsapp_msg_tenant_message_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_whatsapp_msg_tenant_message_id ON public.crm_whatsapp_message USING btree (tenant_id, message_id);


--
-- TOC entry 7284 (class 1259 OID 409029)
-- Name: idx_crm_whatsapp_msg_tenant_wa_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_whatsapp_msg_tenant_wa_id ON public.crm_whatsapp_message USING btree (tenant_id, wa_id, create_time DESC);


--
-- TOC entry 7287 (class 1259 OID 409043)
-- Name: idx_crm_whatsapp_template_tenant_status; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_whatsapp_template_tenant_status ON public.crm_whatsapp_template USING btree (tenant_id, status);


--
-- TOC entry 7168 (class 1259 OID 404643)
-- Name: idx_event_session_event; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_event_session_event ON public.crm_event_session USING btree (event_id, tenant_id);


--
-- TOC entry 7146 (class 1259 OID 404558)
-- Name: idx_event_spend_tenant_event; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_event_spend_tenant_event ON public.crm_event_spend_item USING btree (tenant_id, event_id);


--
-- TOC entry 7227 (class 1259 OID 408731)
-- Name: idx_executed_at; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_executed_at ON public.smart_push_execution_log USING btree (executed_at);


--
-- TOC entry 7499 (class 1259 OID 413326)
-- Name: idx_followup_enrollment_due; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_followup_enrollment_due ON public.crm_followup_rule_enrollment USING btree (tenant_id, status, next_run_at) WHERE ((status)::text = 'pending'::text);


--
-- TOC entry 7500 (class 1259 OID 413336)
-- Name: idx_followup_enrollment_subject; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_followup_enrollment_subject ON public.crm_followup_rule_enrollment USING btree (tenant_id, subject_type, subject_id, status);


--
-- TOC entry 7505 (class 1259 OID 413335)
-- Name: idx_followup_exec_log_rule; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_followup_exec_log_rule ON public.crm_followup_rule_execution_log USING btree (tenant_id, rule_id, create_time DESC);


--
-- TOC entry 7496 (class 1259 OID 413314)
-- Name: idx_followup_rule_tenant_enabled; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_followup_rule_tenant_enabled ON public.crm_followup_rule USING btree (tenant_id, enabled, trigger_type) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7209 (class 1259 OID 408213)
-- Name: idx_groupchat_daily_query; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_groupchat_daily_query ON public.crm_wecom_groupchat_daily USING btree (stat_time, owner_userid);


--
-- TOC entry 7200 (class 1259 OID 408169)
-- Name: idx_identity_conflict_status; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_identity_conflict_status ON public.crm_identity_conflict USING btree (status, create_time DESC);


--
-- TOC entry 7077 (class 1259 OID 404462)
-- Name: idx_identity_contact_type; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_identity_contact_type ON public.crm_customer_identity USING btree (contact_id, identity_type);


--
-- TOC entry 7569 (class 1259 OID 413561)
-- Name: idx_kf_conv_contact; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_kf_conv_contact ON public.crm_wecom_kf_conversation USING btree (contact_id) WHERE (contact_id IS NOT NULL);


--
-- TOC entry 7570 (class 1259 OID 413559)
-- Name: idx_kf_conv_corp_last; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_kf_conv_corp_last ON public.crm_wecom_kf_conversation USING btree (corp_id, last_message_at DESC NULLS LAST);


--
-- TOC entry 7571 (class 1259 OID 413560)
-- Name: idx_kf_conv_open_kfid_last; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_kf_conv_open_kfid_last ON public.crm_wecom_kf_conversation USING btree (open_kfid, last_message_at DESC NULLS LAST);


--
-- TOC entry 7572 (class 1259 OID 413558)
-- Name: idx_kf_conv_pool_last; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_kf_conv_pool_last ON public.crm_wecom_kf_conversation USING btree (last_message_at DESC NULLS LAST) WHERE (user_id IS NULL);


--
-- TOC entry 7573 (class 1259 OID 413557)
-- Name: idx_kf_conv_user_last; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_kf_conv_user_last ON public.crm_wecom_kf_conversation USING btree (user_id, last_message_at DESC NULLS LAST);


--
-- TOC entry 7046 (class 1259 OID 413576)
-- Name: idx_kf_msg_conversation; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_kf_msg_conversation ON public.crm_wecom_kf_message USING btree (conversation_id) WHERE (conversation_id IS NOT NULL);


--
-- TOC entry 7047 (class 1259 OID 379751)
-- Name: idx_kf_msg_external_userid; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_kf_msg_external_userid ON public.crm_wecom_kf_message USING btree (corp_id, external_userid, send_time);


--
-- TOC entry 7048 (class 1259 OID 413579)
-- Name: idx_kf_msg_media_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_kf_msg_media_id ON public.crm_wecom_kf_message USING btree (media_id) WHERE (media_id IS NOT NULL);


--
-- TOC entry 7049 (class 1259 OID 379752)
-- Name: idx_kf_msg_open_kfid; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_kf_msg_open_kfid ON public.crm_wecom_kf_message USING btree (corp_id, open_kfid, send_time);


--
-- TOC entry 6451 (class 1259 OID 404458)
-- Name: idx_lead_sid; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_lead_sid ON public.crm_lead USING btree (sid);


--
-- TOC entry 6452 (class 1259 OID 404532)
-- Name: idx_lead_sid_mp_openid; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_lead_sid_mp_openid ON public.crm_lead USING btree (sid, wechat_mp_app_id, wechat_mp_openid) WHERE (wechat_mp_openid IS NOT NULL);


--
-- TOC entry 6453 (class 1259 OID 404459)
-- Name: idx_lead_sid_status; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_lead_sid_status ON public.crm_lead USING btree (sid, status);


--
-- TOC entry 6454 (class 1259 OID 408228)
-- Name: idx_lead_wecom_external_userid; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_lead_wecom_external_userid ON public.crm_lead USING btree (wecom_external_userid) WHERE (wecom_external_userid IS NOT NULL);


--
-- TOC entry 7300 (class 1259 OID 409189)
-- Name: idx_mkt_content_publish_app; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mkt_content_publish_app ON public.crm_marketing_content_publish USING btree (app_id);


--
-- TOC entry 7301 (class 1259 OID 409190)
-- Name: idx_mkt_content_publish_content; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mkt_content_publish_content ON public.crm_marketing_content_publish USING btree (content_id);


--
-- TOC entry 7295 (class 1259 OID 409168)
-- Name: idx_mkt_content_status; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mkt_content_status ON public.crm_marketing_content USING btree (status);


--
-- TOC entry 7296 (class 1259 OID 409169)
-- Name: idx_mkt_content_updated; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mkt_content_updated ON public.crm_marketing_content USING btree (update_time DESC);


--
-- TOC entry 7297 (class 1259 OID 409198)
-- Name: idx_mkt_content_website_visible; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mkt_content_website_visible ON public.crm_marketing_content USING btree (website_visible, update_time DESC) WHERE (website_visible = true);


--
-- TOC entry 7233 (class 1259 OID 408751)
-- Name: idx_mp_article_app_update; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_article_app_update ON public.crm_wechat_mp_article USING btree (app_id, update_time DESC);


--
-- TOC entry 7558 (class 1259 OID 413516)
-- Name: idx_mp_article_comment_app; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_article_comment_app ON public.crm_wechat_mp_article_comment USING btree (app_id, comment_time DESC);


--
-- TOC entry 7559 (class 1259 OID 413533)
-- Name: idx_mp_article_comment_lead; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_article_comment_lead ON public.crm_wechat_mp_article_comment USING btree (lead_id) WHERE (lead_id IS NOT NULL);


--
-- TOC entry 7560 (class 1259 OID 413514)
-- Name: idx_mp_article_comment_list; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_article_comment_list ON public.crm_wechat_mp_article_comment USING btree (comment_time DESC);


--
-- TOC entry 7561 (class 1259 OID 413515)
-- Name: idx_mp_article_comment_status; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_article_comment_status ON public.crm_wechat_mp_article_comment USING btree (status, comment_time DESC);


--
-- TOC entry 7238 (class 1259 OID 408786)
-- Name: idx_mp_article_draft_update_time; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_article_draft_update_time ON public.crm_wechat_mp_article_draft USING btree (app_id, update_time DESC);


--
-- TOC entry 7253 (class 1259 OID 408937)
-- Name: idx_mp_article_stat_ref; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_article_stat_ref ON public.crm_wechat_mp_article_stat USING btree (article_id, index_in_msg, ref_date, stat_date);


--
-- TOC entry 7234 (class 1259 OID 408750)
-- Name: idx_mp_article_update_time; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_article_update_time ON public.crm_wechat_mp_article USING btree (app_id, update_time DESC);


--
-- TOC entry 7131 (class 1259 OID 404453)
-- Name: idx_mp_auto_reply_lookup; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_auto_reply_lookup ON public.crm_wechat_mp_auto_reply USING btree (app_id, rule_type, enabled);


--
-- TOC entry 7087 (class 1259 OID 404206)
-- Name: idx_mp_callback_log_create_time; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_callback_log_create_time ON public.crm_wechat_mp_callback_log USING btree (create_time DESC);


--
-- TOC entry 7566 (class 1259 OID 413532)
-- Name: idx_mp_comment_auto_reply_enabled; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_comment_auto_reply_enabled ON public.crm_wechat_mp_comment_auto_reply_rule USING btree (enabled, priority);


--
-- TOC entry 7428 (class 1259 OID 413081)
-- Name: idx_mp_conversation_app_last; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_conversation_app_last ON public.crm_wechat_mp_conversation USING btree (app_id, last_message_at DESC NULLS LAST);


--
-- TOC entry 7429 (class 1259 OID 413082)
-- Name: idx_mp_conversation_contact; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_conversation_contact ON public.crm_wechat_mp_conversation USING btree (contact_id) WHERE (contact_id IS NOT NULL);


--
-- TOC entry 7430 (class 1259 OID 413080)
-- Name: idx_mp_conversation_pool_last; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_conversation_pool_last ON public.crm_wechat_mp_conversation USING btree (last_message_at DESC NULLS LAST) WHERE (user_id IS NULL);


--
-- TOC entry 7431 (class 1259 OID 413079)
-- Name: idx_mp_conversation_user_last; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_conversation_user_last ON public.crm_wechat_mp_conversation USING btree (user_id, last_message_at DESC NULLS LAST);


--
-- TOC entry 7115 (class 1259 OID 404393)
-- Name: idx_mp_fan_contact; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_fan_contact ON public.crm_wechat_mp_fan USING btree (contact_id);


--
-- TOC entry 7116 (class 1259 OID 404394)
-- Name: idx_mp_fan_subscribe; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_fan_subscribe ON public.crm_wechat_mp_fan USING btree (app_id, subscribe);


--
-- TOC entry 7117 (class 1259 OID 408577)
-- Name: idx_mp_fan_union_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_fan_union_id ON public.crm_wechat_mp_fan USING btree (union_id) WHERE (union_id IS NOT NULL);


--
-- TOC entry 7436 (class 1259 OID 413096)
-- Name: idx_mp_mass_task_app_status_ctime; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_mass_task_app_status_ctime ON public.crm_wechat_mp_mass_task USING btree (app_id, status, create_time DESC);


--
-- TOC entry 7437 (class 1259 OID 413097)
-- Name: idx_mp_mass_task_msg_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_mass_task_msg_id ON public.crm_wechat_mp_mass_task USING btree (msg_id) WHERE (msg_id IS NOT NULL);


--
-- TOC entry 7242 (class 1259 OID 408846)
-- Name: idx_mp_material_type_update_time; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_material_type_update_time ON public.crm_wechat_mp_material USING btree (app_id, type, update_time DESC);


--
-- TOC entry 7103 (class 1259 OID 404352)
-- Name: idx_mp_menu_app_status; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_menu_app_status ON public.crm_wechat_mp_menu USING btree (app_id, status, update_time DESC);


--
-- TOC entry 7122 (class 1259 OID 404405)
-- Name: idx_mp_msg_conversation; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_msg_conversation ON public.crm_wechat_mp_message USING btree (app_id, open_id, create_time DESC);


--
-- TOC entry 7134 (class 1259 OID 404475)
-- Name: idx_mp_msg_preset_app_status; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_msg_preset_app_status ON public.crm_wechat_mp_message_preset USING btree (app_id, status);


--
-- TOC entry 7135 (class 1259 OID 404476)
-- Name: idx_mp_msg_preset_name; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_msg_preset_name ON public.crm_wechat_mp_message_preset USING btree (preset_name);


--
-- TOC entry 7138 (class 1259 OID 404495)
-- Name: idx_mp_outreach_event_time; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_outreach_event_time ON public.crm_event_mp_outreach_log USING btree (event_id, create_time DESC);


--
-- TOC entry 7139 (class 1259 OID 404496)
-- Name: idx_mp_outreach_openid; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_outreach_openid ON public.crm_event_mp_outreach_log USING btree (app_id, open_id);


--
-- TOC entry 7247 (class 1259 OID 408914)
-- Name: idx_mp_publish_task_article; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_publish_task_article ON public.crm_wechat_mp_publish_task USING btree (article_id);


--
-- TOC entry 7248 (class 1259 OID 408912)
-- Name: idx_mp_publish_task_media; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_publish_task_media ON public.crm_wechat_mp_publish_task USING btree (app_id, media_id);


--
-- TOC entry 7249 (class 1259 OID 408913)
-- Name: idx_mp_publish_task_msg; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_publish_task_msg ON public.crm_wechat_mp_publish_task USING btree (msg_data_id);


--
-- TOC entry 7110 (class 1259 OID 404378)
-- Name: idx_mp_qrcode_event; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_qrcode_event ON public.crm_wechat_mp_qrcode USING btree (crm_event_id);


--
-- TOC entry 7128 (class 1259 OID 404438)
-- Name: idx_mp_task_item_task; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_task_item_task ON public.crm_wechat_mp_message_task_item USING btree (task_id);


--
-- TOC entry 7159 (class 1259 OID 404609)
-- Name: idx_nurture_enroll_due; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_nurture_enroll_due ON public.crm_event_nurture_enrollment USING btree (tenant_id, status, next_run_at) WHERE ((status)::text = 'active'::text);


--
-- TOC entry 7160 (class 1259 OID 404610)
-- Name: idx_nurture_enroll_event; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_nurture_enroll_event ON public.crm_event_nurture_enrollment USING btree (tenant_id, event_id, sequence_id);


--
-- TOC entry 7165 (class 1259 OID 404623)
-- Name: idx_nurture_exec_enrollment; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_nurture_exec_enrollment ON public.crm_event_nurture_step_execution USING btree (enrollment_id, create_time DESC);


--
-- TOC entry 7149 (class 1259 OID 404575)
-- Name: idx_nurture_seq_event; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_nurture_seq_event ON public.crm_event_nurture_sequence USING btree (tenant_id, event_id, del_flag);


--
-- TOC entry 7521 (class 1259 OID 413398)
-- Name: idx_outbound_webhook_delivery_due; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_outbound_webhook_delivery_due ON public.crm_outbound_webhook_delivery USING btree (tenant_id, status, next_retry_at) WHERE ((status)::text = ANY ((ARRAY['pending'::character varying, 'retrying'::character varying])::text[]));


--
-- TOC entry 7522 (class 1259 OID 413397)
-- Name: idx_outbound_webhook_delivery_log; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_outbound_webhook_delivery_log ON public.crm_outbound_webhook_delivery USING btree (tenant_id, webhook_id, create_time DESC);


--
-- TOC entry 7518 (class 1259 OID 413386)
-- Name: idx_outbound_webhook_tenant_event; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_outbound_webhook_tenant_event ON public.crm_outbound_webhook USING btree (tenant_id, enabled, event_type) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 6937 (class 1259 OID 371306)
-- Name: idx_patient_value_account; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_patient_value_account ON public.crm_patient_value USING btree (account_id);


--
-- TOC entry 6938 (class 1259 OID 371308)
-- Name: idx_patient_value_recall; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_patient_value_recall ON public.crm_patient_value USING btree (next_recall_due_at) WHERE (del_flag = 0);


--
-- TOC entry 6939 (class 1259 OID 371307)
-- Name: idx_patient_value_tenant_tier; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_patient_value_tenant_tier ON public.crm_patient_value USING btree (tenant_id, tier);


--
-- TOC entry 7508 (class 1259 OID 413349)
-- Name: idx_recall_policy_tenant_enabled; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_recall_policy_tenant_enabled ON public.crm_recall_policy USING btree (tenant_id, enabled, priority) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7511 (class 1259 OID 413358)
-- Name: idx_recall_schedule_due; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_recall_schedule_due ON public.crm_recall_schedule USING btree (tenant_id, status, next_due_at) WHERE ((status)::text = 'scheduled'::text);


--
-- TOC entry 7173 (class 1259 OID 404667)
-- Name: idx_reg_audience; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_reg_audience ON public.crm_event_registration USING btree (event_id, audience_key);


--
-- TOC entry 7174 (class 1259 OID 404666)
-- Name: idx_reg_event_session; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_reg_event_session ON public.crm_event_registration USING btree (event_id, session_id);


--
-- TOC entry 7175 (class 1259 OID 404668)
-- Name: idx_reg_lead; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_reg_lead ON public.crm_event_registration USING btree (lead_id);


--
-- TOC entry 6497 (class 1259 OID 408610)
-- Name: idx_respondents_contact; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_respondents_contact ON public.crm_respondents USING btree (contact_id) WHERE (contact_id IS NOT NULL);


--
-- TOC entry 6954 (class 1259 OID 371477)
-- Name: idx_sf_express_token_expires; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_sf_express_token_expires ON public.crm_sf_express_token USING btree (expires_at);


--
-- TOC entry 7228 (class 1259 OID 408730)
-- Name: idx_strategy_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_strategy_id ON public.smart_push_execution_log USING btree (strategy_id);


--
-- TOC entry 7063 (class 1259 OID 380110)
-- Name: idx_subject_map_parent; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_subject_map_parent ON public.crm_subject_map USING btree (parent_subject_type, parent_subject_id);


--
-- TOC entry 7184 (class 1259 OID 404702)
-- Name: idx_survey_outreach_channel; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_survey_outreach_channel ON public.crm_survey_outreach_log USING btree (survey_id, channel);


--
-- TOC entry 7185 (class 1259 OID 404701)
-- Name: idx_survey_outreach_survey; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_survey_outreach_survey ON public.crm_survey_outreach_log USING btree (survey_id);


--
-- TOC entry 7191 (class 1259 OID 407547)
-- Name: idx_tenant_website_page_status; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_tenant_website_page_status ON public.crm_tenant_website_page USING btree (status);


--
-- TOC entry 7347 (class 1259 OID 411303)
-- Name: idx_w_scheduled_task_enabled_time; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_w_scheduled_task_enabled_time ON public.w_scheduled_task USING btree (enabled, run_time);


--
-- TOC entry 6922 (class 1259 OID 371353)
-- Name: idx_w_wait_resume_execution_node; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_w_wait_resume_execution_node ON public.w_wait_resume USING btree (execution_id, node_id);


--
-- TOC entry 6902 (class 1259 OID 413062)
-- Name: idx_w_workflow_is_template; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_w_workflow_is_template ON public.w_workflow USING btree (is_template);


--
-- TOC entry 6903 (class 1259 OID 408643)
-- Name: idx_w_workflow_publish_assistant; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_w_workflow_publish_assistant ON public.w_workflow USING btree (publish_assistant) WHERE (publish_assistant = true);


--
-- TOC entry 6904 (class 1259 OID 408644)
-- Name: idx_w_workflow_publish_sidebar; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_w_workflow_publish_sidebar ON public.w_workflow USING btree (publish_sidebar) WHERE (publish_sidebar = true);


--
-- TOC entry 7418 (class 1259 OID 413018)
-- Name: idx_wa_auto_rule_tenant_enabled; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_wa_auto_rule_tenant_enabled ON public.crm_whatsapp_automation_rule USING btree (tenant_id, enabled);


--
-- TOC entry 7413 (class 1259 OID 413003)
-- Name: idx_wa_tpl_send_item_message_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_wa_tpl_send_item_message_id ON public.crm_whatsapp_template_send_item USING btree (tenant_id, message_id) WHERE (message_id IS NOT NULL);


--
-- TOC entry 7414 (class 1259 OID 413002)
-- Name: idx_wa_tpl_send_item_task; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_wa_tpl_send_item_task ON public.crm_whatsapp_template_send_item USING btree (task_id, status);


--
-- TOC entry 7409 (class 1259 OID 412983)
-- Name: idx_wa_tpl_send_task_tenant_created; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_wa_tpl_send_task_tenant_created ON public.crm_whatsapp_template_send_task USING btree (tenant_id, created_at DESC);


--
-- TOC entry 7410 (class 1259 OID 412984)
-- Name: idx_wa_tpl_send_task_tenant_status; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_wa_tpl_send_task_tenant_status ON public.crm_whatsapp_template_send_task USING btree (tenant_id, status);


--
-- TOC entry 6970 (class 1259 OID 371693)
-- Name: idx_webhook_path_tenant; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_webhook_path_tenant ON public.sys_webhook_path_index USING btree (tenant_id);


--
-- TOC entry 6971 (class 1259 OID 371694)
-- Name: idx_webhook_path_workflow; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_webhook_path_workflow ON public.sys_webhook_path_index USING btree (workflow_id);


--
-- TOC entry 7196 (class 1259 OID 408096)
-- Name: idx_website_asset_list; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_website_asset_list ON public.crm_tenant_website_asset USING btree (status, media_type, create_time DESC);


--
-- TOC entry 7311 (class 1259 OID 409235)
-- Name: idx_website_page_view_session; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_website_page_view_session ON public.crm_website_page_view USING btree (session_id, viewed_at);


--
-- TOC entry 7312 (class 1259 OID 409236)
-- Name: idx_website_page_view_unsynced; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_website_page_view_unsynced ON public.crm_website_page_view USING btree (session_id) WHERE (synced_to_activity = false);


--
-- TOC entry 7306 (class 1259 OID 409224)
-- Name: idx_website_visitor_session_subject; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_website_visitor_session_subject ON public.crm_website_visitor_session USING btree (subject_type, subject_id) WHERE (subject_id IS NOT NULL);


--
-- TOC entry 6525 (class 1259 OID 371431)
-- Name: idx_wecom_contact_way_state; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_wecom_contact_way_state ON public.crm_wecom_contact_way USING btree (state);


--
-- TOC entry 6526 (class 1259 OID 404457)
-- Name: idx_wecom_cw_event; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_wecom_cw_event ON public.crm_wecom_contact_way USING btree (crm_event_id);


--
-- TOC entry 6530 (class 1259 OID 404460)
-- Name: idx_wecom_ext_event; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_wecom_ext_event ON public.crm_wecom_external_contact USING btree (crm_event_id);


--
-- TOC entry 6531 (class 1259 OID 371432)
-- Name: idx_wecom_ext_lead; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_wecom_ext_lead ON public.crm_wecom_external_contact USING btree (crm_lead_id);


--
-- TOC entry 6536 (class 1259 OID 371433)
-- Name: idx_wecom_mass_msg; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_wecom_mass_msg ON public.crm_wecom_mass_task USING btree (msg_id);


--
-- TOC entry 6537 (class 1259 OID 404691)
-- Name: idx_wecom_mass_survey; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX idx_wecom_mass_survey ON public.crm_wecom_mass_task USING btree (crm_survey_id);


--
-- TOC entry 7054 (class 1259 OID 408970)
-- Name: idx_wecom_material_attach_type; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_wecom_material_attach_type ON public.crm_wecom_material USING btree (attachment_type);


--
-- TOC entry 7055 (class 1259 OID 380031)
-- Name: idx_wecom_material_create_time; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_wecom_material_create_time ON public.crm_wecom_material USING btree (create_time DESC);


--
-- TOC entry 7056 (class 1259 OID 380030)
-- Name: idx_wecom_material_status; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_wecom_material_status ON public.crm_wecom_material USING btree (status);


--
-- TOC entry 6946 (class 1259 OID 371404)
-- Name: idx_wecom_welcome_log_ct; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_wecom_welcome_log_ct ON public.crm_wecom_welcome_log USING btree (create_time);


--
-- TOC entry 6947 (class 1259 OID 371403)
-- Name: idx_wecom_welcome_log_ext; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_wecom_welcome_log_ext ON public.crm_wecom_welcome_log USING btree (external_userid);


--
-- TOC entry 6901 (class 1259 OID 371089)
-- Name: session_token_key; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE UNIQUE INDEX session_token_key ON public.session USING btree (token);


--
-- TOC entry 7093 (class 1259 OID 404222)
-- Name: uk_act_unmatched_pulse; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uk_act_unmatched_pulse ON public.crm_activity_unmatched USING btree (pulse_id);


--
-- TOC entry 6386 (class 1259 OID 404149)
-- Name: uk_activity_channel_event; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE UNIQUE INDEX uk_activity_channel_event ON public.crm_activity USING btree (channel, channel_event_id) WHERE (channel_event_id IS NOT NULL);


--
-- TOC entry 7323 (class 1259 OID 410946)
-- Name: uk_crm_kb_document_source; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uk_crm_kb_document_source ON public.crm_kb_document USING btree (source_type, source_id) WHERE ((source_id IS NOT NULL) AND (del_flag = '0'::bpchar));


--
-- TOC entry 7339 (class 1259 OID 411261)
-- Name: uk_crm_product_category_code; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uk_crm_product_category_code ON public.crm_product_category USING btree (lower((category_code)::text)) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7345 (class 1259 OID 411285)
-- Name: uk_crm_product_channel_external; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uk_crm_product_channel_external ON public.crm_product_channel_binding USING btree (channel_type, external_id) WHERE ((del_flag = '0'::bpchar) AND (external_id IS NOT NULL) AND (TRIM(BOTH FROM external_id) <> ''::text));


--
-- TOC entry 7346 (class 1259 OID 411284)
-- Name: uk_crm_product_channel_product; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uk_crm_product_channel_product ON public.crm_product_channel_binding USING btree (product_id, channel_type) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 6484 (class 1259 OID 411246)
-- Name: uk_crm_product_code_active; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE UNIQUE INDEX uk_crm_product_code_active ON public.crm_product USING btree (lower((product_code)::text)) WHERE ((del_flag = '0'::bpchar) AND (product_code IS NOT NULL) AND (TRIM(BOTH FROM product_code) <> ''::text));


--
-- TOC entry 6502 (class 1259 OID 417244)
-- Name: uk_crm_salesgoal_owner_period_kpi; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE UNIQUE INDEX uk_crm_salesgoal_owner_period_kpi ON public.crm_salesgoal USING btree (owner_id, period_id, kpi_code) WHERE (kpi_code IS NOT NULL);


--
-- TOC entry 7400 (class 1259 OID 411775)
-- Name: uk_crm_sst_tag_code_active; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uk_crm_sst_tag_code_active ON public.crm_sanction_source_tag USING btree (tag_code) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 7490 (class 1259 OID 413264)
-- Name: uk_crm_webchat_msg_client; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uk_crm_webchat_msg_client ON public.crm_webchat_message USING btree (tenant_id, conversation_id, client_message_id) WHERE (client_message_id IS NOT NULL);


--
-- TOC entry 7150 (class 1259 OID 404574)
-- Name: uk_event_nurture_one_active; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uk_event_nurture_one_active ON public.crm_event_nurture_sequence USING btree (tenant_id, event_id) WHERE ((enabled = '1'::bpchar) AND (del_flag = '0'::bpchar));


--
-- TOC entry 7201 (class 1259 OID 408170)
-- Name: uk_identity_conflict_pending; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uk_identity_conflict_pending ON public.crm_identity_conflict USING btree (identity_type, identity_value, existing_contact_id, incoming_contact_id) WHERE ((status)::text = 'pending'::text);


--
-- TOC entry 7235 (class 1259 OID 408749)
-- Name: uk_mp_article; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uk_mp_article ON public.crm_wechat_mp_article USING btree (app_id, article_id, index_in_msg);


--
-- TOC entry 7239 (class 1259 OID 408785)
-- Name: uk_mp_article_draft; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uk_mp_article_draft ON public.crm_wechat_mp_article_draft USING btree (app_id, media_id, index_in_msg);


--
-- TOC entry 7254 (class 1259 OID 408936)
-- Name: uk_mp_article_stat; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uk_mp_article_stat ON public.crm_wechat_mp_article_stat USING btree (article_id, index_in_msg, stat_date);


--
-- TOC entry 7438 (class 1259 OID 413098)
-- Name: uk_mp_mass_task_app_client_msg; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uk_mp_mass_task_app_client_msg ON public.crm_wechat_mp_mass_task USING btree (app_id, client_msg_id) WHERE (client_msg_id IS NOT NULL);


--
-- TOC entry 7243 (class 1259 OID 408845)
-- Name: uk_mp_material; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uk_mp_material ON public.crm_wechat_mp_material USING btree (app_id, media_id);


--
-- TOC entry 7244 (class 1259 OID 408848)
-- Name: uk_mp_material_default; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uk_mp_material_default ON public.crm_wechat_mp_material USING btree (app_id) WHERE (is_default = true);


--
-- TOC entry 7123 (class 1259 OID 404406)
-- Name: uk_mp_msg_wx_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uk_mp_msg_wx_id ON public.crm_wechat_mp_message USING btree (app_id, wx_msg_id) WHERE (wx_msg_id IS NOT NULL);


--
-- TOC entry 7250 (class 1259 OID 408911)
-- Name: uk_mp_publish_task_pid; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uk_mp_publish_task_pid ON public.crm_wechat_mp_publish_task USING btree (publish_id);


--
-- TOC entry 7493 (class 1259 OID 413272)
-- Name: uk_sys_webchat_token_tenant_tenant; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uk_sys_webchat_token_tenant_tenant ON public.sys_webchat_token_tenant USING btree (tenant);


--
-- TOC entry 7415 (class 1259 OID 413004)
-- Name: uk_wa_tpl_send_item_rule_biz; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uk_wa_tpl_send_item_rule_biz ON public.crm_whatsapp_template_send_item USING btree (rule_id, business_key) WHERE ((rule_id IS NOT NULL) AND (business_key IS NOT NULL));


--
-- TOC entry 7485 (class 1259 OID 413247)
-- Name: uk_webchat_conv_active; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uk_webchat_conv_active ON public.crm_webchat_conversation USING btree (tenant_id, visitor_id) WHERE ((status)::text = 'active'::text);


--
-- TOC entry 6527 (class 1259 OID 371405)
-- Name: uk_wecom_contact_way_state; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE UNIQUE INDEX uk_wecom_contact_way_state ON public.crm_wecom_contact_way USING btree (state) WHERE ((state IS NOT NULL) AND (TRIM(BOTH FROM state) <> ''::text));


--
-- TOC entry 7181 (class 1259 OID 404689)
-- Name: uq_checkin_slot; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uq_checkin_slot ON public.crm_event_check_in USING btree (event_id, session_id, audience_key);


--
-- TOC entry 7169 (class 1259 OID 404644)
-- Name: uq_event_session_name; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uq_event_session_name ON public.crm_event_session USING btree (event_id, name) WHERE ((status)::text <> 'deleted'::text);


--
-- TOC entry 7356 (class 1259 OID 411332)
-- Name: uq_feishu_config_app_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uq_feishu_config_app_id ON public.crm_feishu_config USING btree (app_id);


--
-- TOC entry 7357 (class 1259 OID 411331)
-- Name: uq_feishu_config_singleton; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uq_feishu_config_singleton ON public.crm_feishu_config USING btree ((true));


--
-- TOC entry 7176 (class 1259 OID 404669)
-- Name: uq_reg_active_slot; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uq_reg_active_slot ON public.crm_event_registration USING btree (event_id, session_id, audience_key) WHERE ((registration_status)::text = ANY ((ARRAY['confirmed'::character varying, 'waitlist'::character varying])::text[]));


--
-- TOC entry 7177 (class 1259 OID 412854)
-- Name: uq_reg_market_event_audience; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uq_reg_market_event_audience ON public.crm_event_registration USING btree (event_id, audience_key) WHERE (((registration_source)::text = 'market_event'::text) AND ((registration_status)::text = ANY ((ARRAY['confirmed'::character varying, 'waitlist'::character varying])::text[])));


--
-- TOC entry 7186 (class 1259 OID 408649)
-- Name: uq_survey_outreach_recipient; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uq_survey_outreach_recipient ON public.crm_survey_outreach_log USING btree (survey_id, channel, recipient_id);


--
-- TOC entry 7197 (class 1259 OID 408097)
-- Name: uq_website_asset_public_path; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uq_website_asset_public_path ON public.crm_tenant_website_asset USING btree (public_path) WHERE ((status)::text = 'active'::text);


--
-- TOC entry 7352 (class 1259 OID 411317)
-- Name: uq_wechat_miniapp_app_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uq_wechat_miniapp_app_id ON public.crm_wechat_miniapp_config USING btree (app_id);


--
-- TOC entry 7353 (class 1259 OID 411316)
-- Name: uq_wechat_miniapp_singleton; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uq_wechat_miniapp_singleton ON public.crm_wechat_miniapp_config USING btree ((true));


--
-- TOC entry 6916 (class 1259 OID 371192)
-- Name: w_connection_fromNodeId_toNodeId_fromOutput_toInput_key; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE UNIQUE INDEX "w_connection_fromNodeId_toNodeId_fromOutput_toInput_key" ON public.w_connection USING btree (from_node_id, to_node_id, from_output, to_input);


--
-- TOC entry 6910 (class 1259 OID 371174)
-- Name: w_credential_organizationId_idx; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX "w_credential_organizationId_idx" ON public.w_credential USING btree (organization_id);


--
-- TOC entry 6919 (class 1259 OID 371200)
-- Name: w_execution_inngestEventId_key; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE UNIQUE INDEX "w_execution_inngestEventId_key" ON public.w_execution USING btree (inngest_event_id);


--
-- TOC entry 6915 (class 1259 OID 371181)
-- Name: w_node_definition_type_key; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE UNIQUE INDEX w_node_definition_type_key ON public.w_node_definition USING btree (type);


--
-- TOC entry 6923 (class 1259 OID 371216)
-- Name: w_wait_resume_execution_id_idx; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX w_wait_resume_execution_id_idx ON public.w_wait_resume USING btree (execution_id);


--
-- TOC entry 6924 (class 1259 OID 371215)
-- Name: w_wait_resume_key_key; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX w_wait_resume_key_key ON public.w_wait_resume USING btree (resume_key);


--
-- TOC entry 6925 (class 1259 OID 371217)
-- Name: w_wait_resume_node_id_idx; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX w_wait_resume_node_id_idx ON public.w_wait_resume USING btree (node_id);


--
-- TOC entry 6905 (class 1259 OID 371156)
-- Name: w_workflow_organizationId_idx; Type: INDEX; Schema: public; Owner: _tenant
--

CREATE INDEX "w_workflow_organizationId_idx" ON public.w_workflow USING btree (organization_id);


--
-- TOC entry 7645 (class 2620 OID 404256)
-- Name: crm_customer_identity trg_crm_identity_update_time; Type: TRIGGER; Schema: public; Owner: luohd
--

CREATE TRIGGER trg_crm_identity_update_time BEFORE UPDATE ON public.crm_customer_identity FOR EACH ROW EXECUTE FUNCTION public.fn_crm_identity_update_time();


--
-- TOC entry 7609 (class 2606 OID 369911)
-- Name: act_ru_identitylink act_fk_athrz_procedef; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_identitylink
    ADD CONSTRAINT act_fk_athrz_procedef FOREIGN KEY (proc_def_id_) REFERENCES public.act_re_procdef(id_);


--
-- TOC entry 7588 (class 2606 OID 369916)
-- Name: act_ge_bytearray act_fk_bytearr_depl; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ge_bytearray
    ADD CONSTRAINT act_fk_bytearr_depl FOREIGN KEY (deployment_id_) REFERENCES public.act_re_deployment(id_);


--
-- TOC entry 7597 (class 2606 OID 369921)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_custom_values; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 7598 (class 2606 OID 369926)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_exception; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 7599 (class 2606 OID 369931)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_execution; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_execution FOREIGN KEY (execution_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 7600 (class 2606 OID 369936)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_proc_def; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_proc_def FOREIGN KEY (proc_def_id_) REFERENCES public.act_re_procdef(id_);


--
-- TOC entry 7601 (class 2606 OID 369941)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_process_instance; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_process_instance FOREIGN KEY (process_instance_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 7602 (class 2606 OID 369946)
-- Name: act_ru_event_subscr act_fk_event_exec; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_event_subscr
    ADD CONSTRAINT act_fk_event_exec FOREIGN KEY (execution_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 7603 (class 2606 OID 369951)
-- Name: act_ru_execution act_fk_exe_parent; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_execution
    ADD CONSTRAINT act_fk_exe_parent FOREIGN KEY (parent_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 7604 (class 2606 OID 369956)
-- Name: act_ru_execution act_fk_exe_procdef; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_execution
    ADD CONSTRAINT act_fk_exe_procdef FOREIGN KEY (proc_def_id_) REFERENCES public.act_re_procdef(id_);


--
-- TOC entry 7605 (class 2606 OID 369961)
-- Name: act_ru_execution act_fk_exe_procinst; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_execution
    ADD CONSTRAINT act_fk_exe_procinst FOREIGN KEY (proc_inst_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 7606 (class 2606 OID 369966)
-- Name: act_ru_execution act_fk_exe_super; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_execution
    ADD CONSTRAINT act_fk_exe_super FOREIGN KEY (super_exec_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 7607 (class 2606 OID 369971)
-- Name: act_ru_external_job act_fk_external_job_custom_values; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_external_job
    ADD CONSTRAINT act_fk_external_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 7608 (class 2606 OID 369976)
-- Name: act_ru_external_job act_fk_external_job_exception; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_external_job
    ADD CONSTRAINT act_fk_external_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 7610 (class 2606 OID 369981)
-- Name: act_ru_identitylink act_fk_idl_procinst; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_identitylink
    ADD CONSTRAINT act_fk_idl_procinst FOREIGN KEY (proc_inst_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 7592 (class 2606 OID 369986)
-- Name: act_procdef_info act_fk_info_json_ba; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_procdef_info
    ADD CONSTRAINT act_fk_info_json_ba FOREIGN KEY (info_json_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 7593 (class 2606 OID 369991)
-- Name: act_procdef_info act_fk_info_procdef; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_procdef_info
    ADD CONSTRAINT act_fk_info_procdef FOREIGN KEY (proc_def_id_) REFERENCES public.act_re_procdef(id_);


--
-- TOC entry 7612 (class 2606 OID 369996)
-- Name: act_ru_job act_fk_job_custom_values; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_job
    ADD CONSTRAINT act_fk_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 7613 (class 2606 OID 370001)
-- Name: act_ru_job act_fk_job_exception; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_job
    ADD CONSTRAINT act_fk_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 7614 (class 2606 OID 370006)
-- Name: act_ru_job act_fk_job_execution; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_job
    ADD CONSTRAINT act_fk_job_execution FOREIGN KEY (execution_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 7615 (class 2606 OID 370011)
-- Name: act_ru_job act_fk_job_proc_def; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_job
    ADD CONSTRAINT act_fk_job_proc_def FOREIGN KEY (proc_def_id_) REFERENCES public.act_re_procdef(id_);


--
-- TOC entry 7616 (class 2606 OID 370016)
-- Name: act_ru_job act_fk_job_process_instance; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_job
    ADD CONSTRAINT act_fk_job_process_instance FOREIGN KEY (process_instance_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 7589 (class 2606 OID 370021)
-- Name: act_id_membership act_fk_memb_group; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_membership
    ADD CONSTRAINT act_fk_memb_group FOREIGN KEY (group_id_) REFERENCES public.act_id_group(id_);


--
-- TOC entry 7590 (class 2606 OID 370026)
-- Name: act_id_membership act_fk_memb_user; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_membership
    ADD CONSTRAINT act_fk_memb_user FOREIGN KEY (user_id_) REFERENCES public.act_id_user(id_);


--
-- TOC entry 7594 (class 2606 OID 370031)
-- Name: act_re_model act_fk_model_deployment; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_re_model
    ADD CONSTRAINT act_fk_model_deployment FOREIGN KEY (deployment_id_) REFERENCES public.act_re_deployment(id_);


--
-- TOC entry 7595 (class 2606 OID 370036)
-- Name: act_re_model act_fk_model_source; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_re_model
    ADD CONSTRAINT act_fk_model_source FOREIGN KEY (editor_source_value_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 7596 (class 2606 OID 370041)
-- Name: act_re_model act_fk_model_source_extra; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_re_model
    ADD CONSTRAINT act_fk_model_source_extra FOREIGN KEY (editor_source_extra_value_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 7591 (class 2606 OID 370046)
-- Name: act_id_priv_mapping act_fk_priv_mapping; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_priv_mapping
    ADD CONSTRAINT act_fk_priv_mapping FOREIGN KEY (priv_id_) REFERENCES public.act_id_priv(id_);


--
-- TOC entry 7617 (class 2606 OID 370051)
-- Name: act_ru_suspended_job act_fk_suspended_job_custom_values; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 7618 (class 2606 OID 370056)
-- Name: act_ru_suspended_job act_fk_suspended_job_exception; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 7619 (class 2606 OID 370061)
-- Name: act_ru_suspended_job act_fk_suspended_job_execution; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_execution FOREIGN KEY (execution_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 7620 (class 2606 OID 370066)
-- Name: act_ru_suspended_job act_fk_suspended_job_proc_def; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_proc_def FOREIGN KEY (proc_def_id_) REFERENCES public.act_re_procdef(id_);


--
-- TOC entry 7621 (class 2606 OID 370071)
-- Name: act_ru_suspended_job act_fk_suspended_job_process_instance; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_process_instance FOREIGN KEY (process_instance_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 7622 (class 2606 OID 370076)
-- Name: act_ru_task act_fk_task_exe; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_task
    ADD CONSTRAINT act_fk_task_exe FOREIGN KEY (execution_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 7623 (class 2606 OID 370081)
-- Name: act_ru_task act_fk_task_procdef; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_task
    ADD CONSTRAINT act_fk_task_procdef FOREIGN KEY (proc_def_id_) REFERENCES public.act_re_procdef(id_);


--
-- TOC entry 7624 (class 2606 OID 370086)
-- Name: act_ru_task act_fk_task_procinst; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_task
    ADD CONSTRAINT act_fk_task_procinst FOREIGN KEY (proc_inst_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 7625 (class 2606 OID 370091)
-- Name: act_ru_timer_job act_fk_timer_job_custom_values; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 7626 (class 2606 OID 370096)
-- Name: act_ru_timer_job act_fk_timer_job_exception; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 7627 (class 2606 OID 370101)
-- Name: act_ru_timer_job act_fk_timer_job_execution; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_execution FOREIGN KEY (execution_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 7628 (class 2606 OID 370106)
-- Name: act_ru_timer_job act_fk_timer_job_proc_def; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_proc_def FOREIGN KEY (proc_def_id_) REFERENCES public.act_re_procdef(id_);


--
-- TOC entry 7629 (class 2606 OID 370111)
-- Name: act_ru_timer_job act_fk_timer_job_process_instance; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_process_instance FOREIGN KEY (process_instance_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 7611 (class 2606 OID 370116)
-- Name: act_ru_identitylink act_fk_tskass_task; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_identitylink
    ADD CONSTRAINT act_fk_tskass_task FOREIGN KEY (task_id_) REFERENCES public.act_ru_task(id_);


--
-- TOC entry 7630 (class 2606 OID 370121)
-- Name: act_ru_variable act_fk_var_bytearray; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_variable
    ADD CONSTRAINT act_fk_var_bytearray FOREIGN KEY (bytearray_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 7631 (class 2606 OID 370126)
-- Name: act_ru_variable act_fk_var_exe; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_variable
    ADD CONSTRAINT act_fk_var_exe FOREIGN KEY (execution_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 7632 (class 2606 OID 370131)
-- Name: act_ru_variable act_fk_var_procinst; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_variable
    ADD CONSTRAINT act_fk_var_procinst FOREIGN KEY (proc_inst_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 7636 (class 2606 OID 371827)
-- Name: crm_ai_chat_message crm_ai_chat_message_session_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_ai_chat_message
    ADD CONSTRAINT crm_ai_chat_message_session_id_fkey FOREIGN KEY (session_id) REFERENCES public.crm_ai_chat_session(session_id);


--
-- TOC entry 7634 (class 2606 OID 371628)
-- Name: crm_data_table_column crm_data_table_column_data_table_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_data_table_column
    ADD CONSTRAINT crm_data_table_column_data_table_id_fkey FOREIGN KEY (data_table_id) REFERENCES public.crm_data_table(id) ON DELETE CASCADE;


--
-- TOC entry 7635 (class 2606 OID 371643)
-- Name: crm_data_table_row crm_data_table_row_data_table_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_data_table_row
    ADD CONSTRAINT crm_data_table_row_data_table_id_fkey FOREIGN KEY (data_table_id) REFERENCES public.crm_data_table(id) ON DELETE CASCADE;


--
-- TOC entry 7638 (class 2606 OID 404590)
-- Name: crm_event_nurture_step crm_event_nurture_step_sequence_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_event_nurture_step
    ADD CONSTRAINT crm_event_nurture_step_sequence_id_fkey FOREIGN KEY (sequence_id) REFERENCES public.crm_event_nurture_sequence(id) ON DELETE CASCADE;


--
-- TOC entry 7643 (class 2606 OID 410959)
-- Name: crm_kb_chunk crm_kb_chunk_document_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_kb_chunk
    ADD CONSTRAINT crm_kb_chunk_document_id_fkey FOREIGN KEY (document_id) REFERENCES public.crm_kb_document(document_id) ON DELETE CASCADE;


--
-- TOC entry 7642 (class 2606 OID 409184)
-- Name: crm_marketing_content_publish crm_marketing_content_publish_content_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_marketing_content_publish
    ADD CONSTRAINT crm_marketing_content_publish_content_id_fkey FOREIGN KEY (content_id) REFERENCES public.crm_marketing_content(id) ON DELETE CASCADE;


--
-- TOC entry 7637 (class 2606 OID 404433)
-- Name: crm_wechat_mp_message_task_item crm_wechat_mp_message_task_item_task_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_wechat_mp_message_task_item
    ADD CONSTRAINT crm_wechat_mp_message_task_item_task_id_fkey FOREIGN KEY (task_id) REFERENCES public.crm_wechat_mp_message_task(id);


--
-- TOC entry 7644 (class 2606 OID 412997)
-- Name: crm_whatsapp_template_send_item crm_whatsapp_template_send_item_task_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_whatsapp_template_send_item
    ADD CONSTRAINT crm_whatsapp_template_send_item_task_id_fkey FOREIGN KEY (task_id) REFERENCES public.crm_whatsapp_template_send_task(id);


--
-- TOC entry 7640 (class 2606 OID 404679)
-- Name: crm_event_check_in fk_checkin_event; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_event_check_in
    ADD CONSTRAINT fk_checkin_event FOREIGN KEY (event_id) REFERENCES public.crm_event(event_id);


--
-- TOC entry 7641 (class 2606 OID 404684)
-- Name: crm_event_check_in fk_checkin_session; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_event_check_in
    ADD CONSTRAINT fk_checkin_session FOREIGN KEY (session_id) REFERENCES public.crm_event_session(session_id);


--
-- TOC entry 7639 (class 2606 OID 404638)
-- Name: crm_event_session fk_event_session_event; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_event_session
    ADD CONSTRAINT fk_event_session_event FOREIGN KEY (event_id) REFERENCES public.crm_event(event_id);


--
-- TOC entry 7633 (class 2606 OID 370136)
-- Name: flw_ru_batch_part flw_fk_batch_part_parent; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.flw_ru_batch_part
    ADD CONSTRAINT flw_fk_batch_part_parent FOREIGN KEY (batch_id_) REFERENCES public.flw_ru_batch(id_);


--
-- TOC entry 7791 (class 0 OID 0)
-- Dependencies: 6
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: _tenant
--

REVOKE USAGE ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO PUBLIC;
GRANT ALL ON SCHEMA public TO "mega_mn6CoEbT";


--
-- TOC entry 7794 (class 0 OID 0)
-- Dependencies: 376
-- Name: TABLE act_evt_log; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_evt_log TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_evt_log TO "_tenant";


--
-- TOC entry 7796 (class 0 OID 0)
-- Dependencies: 377
-- Name: SEQUENCE act_evt_log_log_nr__seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.act_evt_log_log_nr__seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.act_evt_log_log_nr__seq TO "_tenant";


--
-- TOC entry 7797 (class 0 OID 0)
-- Dependencies: 378
-- Name: TABLE act_ge_bytearray; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_ge_bytearray TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_ge_bytearray TO "_tenant";


--
-- TOC entry 7798 (class 0 OID 0)
-- Dependencies: 379
-- Name: TABLE act_ge_property; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_ge_property TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_ge_property TO "_tenant";


--
-- TOC entry 7799 (class 0 OID 0)
-- Dependencies: 380
-- Name: TABLE act_hi_actinst; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_hi_actinst TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_hi_actinst TO "_tenant";


--
-- TOC entry 7800 (class 0 OID 0)
-- Dependencies: 381
-- Name: TABLE act_hi_attachment; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_hi_attachment TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_hi_attachment TO "_tenant";


--
-- TOC entry 7801 (class 0 OID 0)
-- Dependencies: 382
-- Name: TABLE act_hi_comment; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_hi_comment TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_hi_comment TO "_tenant";


--
-- TOC entry 7802 (class 0 OID 0)
-- Dependencies: 383
-- Name: TABLE act_hi_detail; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_hi_detail TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_hi_detail TO "_tenant";


--
-- TOC entry 7803 (class 0 OID 0)
-- Dependencies: 384
-- Name: TABLE act_hi_entitylink; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_hi_entitylink TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_hi_entitylink TO "_tenant";


--
-- TOC entry 7804 (class 0 OID 0)
-- Dependencies: 385
-- Name: TABLE act_hi_identitylink; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_hi_identitylink TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_hi_identitylink TO "_tenant";


--
-- TOC entry 7805 (class 0 OID 0)
-- Dependencies: 386
-- Name: TABLE act_hi_procinst; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_hi_procinst TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_hi_procinst TO "_tenant";


--
-- TOC entry 7806 (class 0 OID 0)
-- Dependencies: 387
-- Name: TABLE act_hi_taskinst; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_hi_taskinst TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_hi_taskinst TO "_tenant";


--
-- TOC entry 7807 (class 0 OID 0)
-- Dependencies: 388
-- Name: TABLE act_hi_tsk_log; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_hi_tsk_log TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_hi_tsk_log TO "_tenant";


--
-- TOC entry 7809 (class 0 OID 0)
-- Dependencies: 389
-- Name: SEQUENCE act_hi_tsk_log_id__seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.act_hi_tsk_log_id__seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.act_hi_tsk_log_id__seq TO "_tenant";


--
-- TOC entry 7810 (class 0 OID 0)
-- Dependencies: 390
-- Name: TABLE act_hi_varinst; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_hi_varinst TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_hi_varinst TO "_tenant";


--
-- TOC entry 7811 (class 0 OID 0)
-- Dependencies: 391
-- Name: TABLE act_id_bytearray; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_id_bytearray TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_id_bytearray TO "_tenant";


--
-- TOC entry 7812 (class 0 OID 0)
-- Dependencies: 392
-- Name: TABLE act_id_group; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_id_group TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_id_group TO "_tenant";


--
-- TOC entry 7813 (class 0 OID 0)
-- Dependencies: 393
-- Name: TABLE act_id_info; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_id_info TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_id_info TO "_tenant";


--
-- TOC entry 7814 (class 0 OID 0)
-- Dependencies: 394
-- Name: TABLE act_id_membership; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_id_membership TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_id_membership TO "_tenant";


--
-- TOC entry 7815 (class 0 OID 0)
-- Dependencies: 395
-- Name: TABLE act_id_priv; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_id_priv TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_id_priv TO "_tenant";


--
-- TOC entry 7816 (class 0 OID 0)
-- Dependencies: 396
-- Name: TABLE act_id_priv_mapping; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_id_priv_mapping TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_id_priv_mapping TO "_tenant";


--
-- TOC entry 7817 (class 0 OID 0)
-- Dependencies: 397
-- Name: TABLE act_id_property; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_id_property TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_id_property TO "_tenant";


--
-- TOC entry 7818 (class 0 OID 0)
-- Dependencies: 398
-- Name: TABLE act_id_token; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_id_token TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_id_token TO "_tenant";


--
-- TOC entry 7819 (class 0 OID 0)
-- Dependencies: 399
-- Name: TABLE act_id_user; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_id_user TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_id_user TO "_tenant";


--
-- TOC entry 7820 (class 0 OID 0)
-- Dependencies: 400
-- Name: TABLE act_procdef_info; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_procdef_info TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_procdef_info TO "_tenant";


--
-- TOC entry 7821 (class 0 OID 0)
-- Dependencies: 401
-- Name: TABLE act_re_deployment; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_re_deployment TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_re_deployment TO "_tenant";


--
-- TOC entry 7822 (class 0 OID 0)
-- Dependencies: 402
-- Name: TABLE act_re_model; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_re_model TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_re_model TO "_tenant";


--
-- TOC entry 7823 (class 0 OID 0)
-- Dependencies: 403
-- Name: TABLE act_re_procdef; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_re_procdef TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_re_procdef TO "_tenant";


--
-- TOC entry 7824 (class 0 OID 0)
-- Dependencies: 404
-- Name: TABLE act_ru_actinst; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_ru_actinst TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_ru_actinst TO "_tenant";


--
-- TOC entry 7825 (class 0 OID 0)
-- Dependencies: 405
-- Name: TABLE act_ru_deadletter_job; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_ru_deadletter_job TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_ru_deadletter_job TO "_tenant";


--
-- TOC entry 7826 (class 0 OID 0)
-- Dependencies: 406
-- Name: TABLE act_ru_entitylink; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_ru_entitylink TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_ru_entitylink TO "_tenant";


--
-- TOC entry 7827 (class 0 OID 0)
-- Dependencies: 407
-- Name: TABLE act_ru_event_subscr; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_ru_event_subscr TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_ru_event_subscr TO "_tenant";


--
-- TOC entry 7828 (class 0 OID 0)
-- Dependencies: 408
-- Name: TABLE act_ru_execution; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_ru_execution TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_ru_execution TO "_tenant";


--
-- TOC entry 7829 (class 0 OID 0)
-- Dependencies: 409
-- Name: TABLE act_ru_external_job; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_ru_external_job TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_ru_external_job TO "_tenant";


--
-- TOC entry 7830 (class 0 OID 0)
-- Dependencies: 410
-- Name: TABLE act_ru_history_job; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_ru_history_job TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_ru_history_job TO "_tenant";


--
-- TOC entry 7831 (class 0 OID 0)
-- Dependencies: 411
-- Name: TABLE act_ru_identitylink; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_ru_identitylink TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_ru_identitylink TO "_tenant";


--
-- TOC entry 7832 (class 0 OID 0)
-- Dependencies: 412
-- Name: TABLE act_ru_job; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_ru_job TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_ru_job TO "_tenant";


--
-- TOC entry 7833 (class 0 OID 0)
-- Dependencies: 413
-- Name: TABLE act_ru_suspended_job; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_ru_suspended_job TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_ru_suspended_job TO "_tenant";


--
-- TOC entry 7834 (class 0 OID 0)
-- Dependencies: 414
-- Name: TABLE act_ru_task; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_ru_task TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_ru_task TO "_tenant";


--
-- TOC entry 7835 (class 0 OID 0)
-- Dependencies: 415
-- Name: TABLE act_ru_timer_job; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_ru_timer_job TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_ru_timer_job TO "_tenant";


--
-- TOC entry 7836 (class 0 OID 0)
-- Dependencies: 416
-- Name: TABLE act_ru_variable; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.act_ru_variable TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.act_ru_variable TO "_tenant";


--
-- TOC entry 7837 (class 0 OID 0)
-- Dependencies: 214
-- Name: TABLE crm_account; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_account TO "mega_mn6CoEbT";


--
-- TOC entry 7838 (class 0 OID 0)
-- Dependencies: 435
-- Name: TABLE crm_account_value; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_account_value TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_account_value TO "_tenant";


--
-- TOC entry 7839 (class 0 OID 0)
-- Dependencies: 215
-- Name: TABLE crm_accountrelation; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_accountrelation TO "mega_mn6CoEbT";


--
-- TOC entry 7867 (class 0 OID 0)
-- Dependencies: 216
-- Name: TABLE crm_activity; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_activity TO "mega_mn6CoEbT";


--
-- TOC entry 7875 (class 0 OID 0)
-- Dependencies: 491
-- Name: TABLE crm_activity_unmatched; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_activity_unmatched TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_activity_unmatched TO "_tenant";


--
-- TOC entry 7877 (class 0 OID 0)
-- Dependencies: 490
-- Name: SEQUENCE crm_activity_unmatched_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_activity_unmatched_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_activity_unmatched_id_seq TO "_tenant";


--
-- TOC entry 7878 (class 0 OID 0)
-- Dependencies: 217
-- Name: TABLE crm_actrelate; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_actrelate TO "mega_mn6CoEbT";


--
-- TOC entry 7879 (class 0 OID 0)
-- Dependencies: 219
-- Name: TABLE crm_agent_tool; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_agent_tool TO "mega_mn6CoEbT";


--
-- TOC entry 7880 (class 0 OID 0)
-- Dependencies: 218
-- Name: SEQUENCE crm_agent_tool_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.crm_agent_tool_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 7882 (class 0 OID 0)
-- Dependencies: 455
-- Name: TABLE crm_ai_chat_message; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_ai_chat_message TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_ai_chat_message TO "_tenant";


--
-- TOC entry 7884 (class 0 OID 0)
-- Dependencies: 454
-- Name: TABLE crm_ai_chat_session; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_ai_chat_session TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_ai_chat_session TO "_tenant";


--
-- TOC entry 7894 (class 0 OID 0)
-- Dependencies: 451
-- Name: TABLE crm_ai_model_config; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_ai_model_config TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_ai_model_config TO "_tenant";


--
-- TOC entry 7896 (class 0 OID 0)
-- Dependencies: 466
-- Name: TABLE crm_ai_playbook_run; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_ai_playbook_run TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_ai_playbook_run TO "_tenant";


--
-- TOC entry 7898 (class 0 OID 0)
-- Dependencies: 465
-- Name: SEQUENCE crm_ai_playbook_run_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_ai_playbook_run_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_ai_playbook_run_id_seq TO "_tenant";


--
-- TOC entry 7903 (class 0 OID 0)
-- Dependencies: 458
-- Name: TABLE crm_ai_skill; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_ai_skill TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_ai_skill TO "_tenant";


--
-- TOC entry 7905 (class 0 OID 0)
-- Dependencies: 457
-- Name: SEQUENCE crm_ai_skill_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_ai_skill_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_ai_skill_id_seq TO "_tenant";


--
-- TOC entry 7907 (class 0 OID 0)
-- Dependencies: 462
-- Name: TABLE crm_ai_skill_playbook; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_ai_skill_playbook TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_ai_skill_playbook TO "_tenant";


--
-- TOC entry 7909 (class 0 OID 0)
-- Dependencies: 461
-- Name: SEQUENCE crm_ai_skill_playbook_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_ai_skill_playbook_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_ai_skill_playbook_id_seq TO "_tenant";


--
-- TOC entry 7911 (class 0 OID 0)
-- Dependencies: 464
-- Name: TABLE crm_ai_skill_playbook_step; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_ai_skill_playbook_step TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_ai_skill_playbook_step TO "_tenant";


--
-- TOC entry 7913 (class 0 OID 0)
-- Dependencies: 463
-- Name: SEQUENCE crm_ai_skill_playbook_step_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_ai_skill_playbook_step_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_ai_skill_playbook_step_id_seq TO "_tenant";


--
-- TOC entry 7915 (class 0 OID 0)
-- Dependencies: 456
-- Name: TABLE crm_analytics_query_log; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_analytics_query_log TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_analytics_query_log TO "_tenant";


--
-- TOC entry 7916 (class 0 OID 0)
-- Dependencies: 220
-- Name: TABLE crm_appointment; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_appointment TO "mega_mn6CoEbT";


--
-- TOC entry 7917 (class 0 OID 0)
-- Dependencies: 221
-- Name: TABLE crm_attendee; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_attendee TO "mega_mn6CoEbT";


--
-- TOC entry 7919 (class 0 OID 0)
-- Dependencies: 642
-- Name: TABLE crm_booking; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_booking TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_booking TO "_tenant";


--
-- TOC entry 7926 (class 0 OID 0)
-- Dependencies: 222
-- Name: TABLE crm_bulkemail; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_bulkemail TO "mega_mn6CoEbT";


--
-- TOC entry 7937 (class 0 OID 0)
-- Dependencies: 533
-- Name: TABLE crm_bulkemail_provider_config; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_bulkemail_provider_config TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_bulkemail_provider_config TO "_tenant";


--
-- TOC entry 7939 (class 0 OID 0)
-- Dependencies: 532
-- Name: SEQUENCE crm_bulkemail_provider_config_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_bulkemail_provider_config_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_bulkemail_provider_config_id_seq TO "_tenant";


--
-- TOC entry 7943 (class 0 OID 0)
-- Dependencies: 223
-- Name: TABLE crm_campaign_contacts; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_campaign_contacts TO "mega_mn6CoEbT";


--
-- TOC entry 7950 (class 0 OID 0)
-- Dependencies: 578
-- Name: TABLE crm_capture; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_capture TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_capture TO "_tenant";


--
-- TOC entry 7951 (class 0 OID 0)
-- Dependencies: 224
-- Name: TABLE crm_cashflow; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_cashflow TO "mega_mn6CoEbT";


--
-- TOC entry 7959 (class 0 OID 0)
-- Dependencies: 597
-- Name: TABLE crm_chinese_enterprise; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_chinese_enterprise TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_chinese_enterprise TO "_tenant";


--
-- TOC entry 7960 (class 0 OID 0)
-- Dependencies: 225
-- Name: TABLE crm_collect; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_collect TO "mega_mn6CoEbT";


--
-- TOC entry 7961 (class 0 OID 0)
-- Dependencies: 226
-- Name: TABLE crm_competitor; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_competitor TO "mega_mn6CoEbT";


--
-- TOC entry 7976 (class 0 OID 0)
-- Dependencies: 227
-- Name: TABLE crm_contact; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_contact TO "mega_mn6CoEbT";


--
-- TOC entry 7982 (class 0 OID 0)
-- Dependencies: 657
-- Name: TABLE crm_contact_service_summary; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_contact_service_summary TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_contact_service_summary TO "_tenant";


--
-- TOC entry 7985 (class 0 OID 0)
-- Dependencies: 659
-- Name: TABLE crm_contact_service_summary_revision; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_contact_service_summary_revision TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_contact_service_summary_revision TO "_tenant";


--
-- TOC entry 7987 (class 0 OID 0)
-- Dependencies: 658
-- Name: SEQUENCE crm_contact_service_summary_revision_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_contact_service_summary_revision_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_contact_service_summary_revision_id_seq TO "_tenant";


--
-- TOC entry 7988 (class 0 OID 0)
-- Dependencies: 228
-- Name: TABLE crm_contactmethod; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_contactmethod TO "mega_mn6CoEbT";


--
-- TOC entry 7989 (class 0 OID 0)
-- Dependencies: 229
-- Name: TABLE crm_contactrelation; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_contactrelation TO "mega_mn6CoEbT";


--
-- TOC entry 7990 (class 0 OID 0)
-- Dependencies: 230
-- Name: TABLE crm_contract; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_contract TO "mega_mn6CoEbT";


--
-- TOC entry 7993 (class 0 OID 0)
-- Dependencies: 660
-- Name: TABLE crm_conversation_summary_draft; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_conversation_summary_draft TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_conversation_summary_draft TO "_tenant";


--
-- TOC entry 7994 (class 0 OID 0)
-- Dependencies: 231
-- Name: TABLE crm_conversion_history; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_conversion_history TO "mega_mn6CoEbT";


--
-- TOC entry 8002 (class 0 OID 0)
-- Dependencies: 486
-- Name: TABLE crm_customer_identity; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_customer_identity TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_customer_identity TO "_tenant";


--
-- TOC entry 8004 (class 0 OID 0)
-- Dependencies: 485
-- Name: SEQUENCE crm_customer_identity_identity_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_customer_identity_identity_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_customer_identity_identity_id_seq TO "_tenant";


--
-- TOC entry 8010 (class 0 OID 0)
-- Dependencies: 561
-- Name: TABLE crm_dashboard_snapshot; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_dashboard_snapshot TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_dashboard_snapshot TO "_tenant";


--
-- TOC entry 8012 (class 0 OID 0)
-- Dependencies: 560
-- Name: SEQUENCE crm_dashboard_snapshot_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_dashboard_snapshot_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_dashboard_snapshot_id_seq TO "_tenant";


--
-- TOC entry 8014 (class 0 OID 0)
-- Dependencies: 446
-- Name: TABLE crm_data_table; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_data_table TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_data_table TO "_tenant";


--
-- TOC entry 8020 (class 0 OID 0)
-- Dependencies: 447
-- Name: TABLE crm_data_table_column; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_data_table_column TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_data_table_column TO "_tenant";


--
-- TOC entry 8022 (class 0 OID 0)
-- Dependencies: 448
-- Name: TABLE crm_data_table_row; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_data_table_row TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_data_table_row TO "_tenant";


--
-- TOC entry 8023 (class 0 OID 0)
-- Dependencies: 232
-- Name: TABLE crm_dd; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_dd TO "mega_mn6CoEbT";


--
-- TOC entry 8024 (class 0 OID 0)
-- Dependencies: 233
-- Name: TABLE crm_docrelation; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_docrelation TO "mega_mn6CoEbT";


--
-- TOC entry 8050 (class 0 OID 0)
-- Dependencies: 452
-- Name: TABLE crm_email_analysis; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_email_analysis TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_email_analysis TO "_tenant";


--
-- TOC entry 8051 (class 0 OID 0)
-- Dependencies: 234
-- Name: TABLE crm_email_attachments; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_email_attachments TO "mega_mn6CoEbT";


--
-- TOC entry 8052 (class 0 OID 0)
-- Dependencies: 235
-- Name: TABLE crm_email_messages; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_email_messages TO "mega_mn6CoEbT";


--
-- TOC entry 8069 (class 0 OID 0)
-- Dependencies: 236
-- Name: TABLE crm_event; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_event TO "mega_mn6CoEbT";


--
-- TOC entry 8070 (class 0 OID 0)
-- Dependencies: 529
-- Name: TABLE crm_event_check_in; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_event_check_in TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_event_check_in TO "_tenant";


--
-- TOC entry 8071 (class 0 OID 0)
-- Dependencies: 515
-- Name: TABLE crm_event_mp_outreach_log; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_event_mp_outreach_log TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_event_mp_outreach_log TO "_tenant";


--
-- TOC entry 8073 (class 0 OID 0)
-- Dependencies: 514
-- Name: SEQUENCE crm_event_mp_outreach_log_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_event_mp_outreach_log_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_event_mp_outreach_log_id_seq TO "_tenant";


--
-- TOC entry 8074 (class 0 OID 0)
-- Dependencies: 524
-- Name: TABLE crm_event_nurture_enrollment; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_event_nurture_enrollment TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_event_nurture_enrollment TO "_tenant";


--
-- TOC entry 8076 (class 0 OID 0)
-- Dependencies: 523
-- Name: SEQUENCE crm_event_nurture_enrollment_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_event_nurture_enrollment_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_event_nurture_enrollment_id_seq TO "_tenant";


--
-- TOC entry 8077 (class 0 OID 0)
-- Dependencies: 520
-- Name: TABLE crm_event_nurture_sequence; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_event_nurture_sequence TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_event_nurture_sequence TO "_tenant";


--
-- TOC entry 8079 (class 0 OID 0)
-- Dependencies: 519
-- Name: SEQUENCE crm_event_nurture_sequence_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_event_nurture_sequence_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_event_nurture_sequence_id_seq TO "_tenant";


--
-- TOC entry 8080 (class 0 OID 0)
-- Dependencies: 522
-- Name: TABLE crm_event_nurture_step; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_event_nurture_step TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_event_nurture_step TO "_tenant";


--
-- TOC entry 8081 (class 0 OID 0)
-- Dependencies: 526
-- Name: TABLE crm_event_nurture_step_execution; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_event_nurture_step_execution TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_event_nurture_step_execution TO "_tenant";


--
-- TOC entry 8083 (class 0 OID 0)
-- Dependencies: 525
-- Name: SEQUENCE crm_event_nurture_step_execution_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_event_nurture_step_execution_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_event_nurture_step_execution_id_seq TO "_tenant";


--
-- TOC entry 8085 (class 0 OID 0)
-- Dependencies: 521
-- Name: SEQUENCE crm_event_nurture_step_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_event_nurture_step_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_event_nurture_step_id_seq TO "_tenant";


--
-- TOC entry 8086 (class 0 OID 0)
-- Dependencies: 516
-- Name: TABLE crm_event_performance_snapshot; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_event_performance_snapshot TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_event_performance_snapshot TO "_tenant";


--
-- TOC entry 8092 (class 0 OID 0)
-- Dependencies: 528
-- Name: TABLE crm_event_registration; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_event_registration TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_event_registration TO "_tenant";


--
-- TOC entry 8093 (class 0 OID 0)
-- Dependencies: 527
-- Name: TABLE crm_event_session; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_event_session TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_event_session TO "_tenant";


--
-- TOC entry 8103 (class 0 OID 0)
-- Dependencies: 573
-- Name: TABLE crm_event_speaker; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_event_speaker TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_event_speaker TO "_tenant";


--
-- TOC entry 8105 (class 0 OID 0)
-- Dependencies: 572
-- Name: SEQUENCE crm_event_speaker_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_event_speaker_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_event_speaker_id_seq TO "_tenant";


--
-- TOC entry 8106 (class 0 OID 0)
-- Dependencies: 518
-- Name: TABLE crm_event_spend_item; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_event_spend_item TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_event_spend_item TO "_tenant";


--
-- TOC entry 8108 (class 0 OID 0)
-- Dependencies: 517
-- Name: SEQUENCE crm_event_spend_item_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_event_spend_item_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_event_spend_item_id_seq TO "_tenant";


--
-- TOC entry 8115 (class 0 OID 0)
-- Dependencies: 594
-- Name: TABLE crm_feishu_config; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_feishu_config TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_feishu_config TO "_tenant";


--
-- TOC entry 8117 (class 0 OID 0)
-- Dependencies: 593
-- Name: SEQUENCE crm_feishu_config_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_feishu_config_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_feishu_config_id_seq TO "_tenant";


--
-- TOC entry 8125 (class 0 OID 0)
-- Dependencies: 453
-- Name: TABLE crm_follow_up_cadence; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_follow_up_cadence TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_follow_up_cadence TO "_tenant";


--
-- TOC entry 8131 (class 0 OID 0)
-- Dependencies: 632
-- Name: TABLE crm_followup_rule; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_followup_rule TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_followup_rule TO "_tenant";


--
-- TOC entry 8134 (class 0 OID 0)
-- Dependencies: 633
-- Name: TABLE crm_followup_rule_enrollment; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_followup_rule_enrollment TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_followup_rule_enrollment TO "_tenant";


--
-- TOC entry 8136 (class 0 OID 0)
-- Dependencies: 634
-- Name: TABLE crm_followup_rule_execution_log; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_followup_rule_execution_log TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_followup_rule_execution_log TO "_tenant";


--
-- TOC entry 8137 (class 0 OID 0)
-- Dependencies: 237
-- Name: TABLE crm_forecast; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_forecast TO "mega_mn6CoEbT";


--
-- TOC entry 8138 (class 0 OID 0)
-- Dependencies: 238
-- Name: TABLE crm_fundinvestor; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_fundinvestor TO "mega_mn6CoEbT";


--
-- TOC entry 8139 (class 0 OID 0)
-- Dependencies: 239
-- Name: TABLE crm_fundpaid; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_fundpaid TO "mega_mn6CoEbT";


--
-- TOC entry 8145 (class 0 OID 0)
-- Dependencies: 598
-- Name: TABLE crm_government_agency; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_government_agency TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_government_agency TO "_tenant";


--
-- TOC entry 8146 (class 0 OID 0)
-- Dependencies: 240
-- Name: TABLE crm_household; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_household TO "mega_mn6CoEbT";


--
-- TOC entry 8147 (class 0 OID 0)
-- Dependencies: 241
-- Name: TABLE crm_householdmember; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_householdmember TO "mega_mn6CoEbT";


--
-- TOC entry 8153 (class 0 OID 0)
-- Dependencies: 537
-- Name: TABLE crm_identity_conflict; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_identity_conflict TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_identity_conflict TO "_tenant";


--
-- TOC entry 8155 (class 0 OID 0)
-- Dependencies: 536
-- Name: SEQUENCE crm_identity_conflict_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_identity_conflict_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_identity_conflict_id_seq TO "_tenant";


--
-- TOC entry 8158 (class 0 OID 0)
-- Dependencies: 643
-- Name: TABLE crm_inbox_channel_settings; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_inbox_channel_settings TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_inbox_channel_settings TO "_tenant";


--
-- TOC entry 8168 (class 0 OID 0)
-- Dependencies: 595
-- Name: TABLE crm_industrial_park; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_industrial_park TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_industrial_park TO "_tenant";


--
-- TOC entry 8169 (class 0 OID 0)
-- Dependencies: 242
-- Name: TABLE crm_interest; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_interest TO "mega_mn6CoEbT";


--
-- TOC entry 8170 (class 0 OID 0)
-- Dependencies: 243
-- Name: TABLE crm_itinerary; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_itinerary TO "mega_mn6CoEbT";


--
-- TOC entry 8172 (class 0 OID 0)
-- Dependencies: 580
-- Name: TABLE crm_kb_chunk; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_kb_chunk TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_kb_chunk TO "_tenant";


--
-- TOC entry 8179 (class 0 OID 0)
-- Dependencies: 579
-- Name: TABLE crm_kb_document; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_kb_document TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_kb_document TO "_tenant";


--
-- TOC entry 8184 (class 0 OID 0)
-- Dependencies: 244
-- Name: TABLE crm_lead; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_lead TO "mega_mn6CoEbT";


--
-- TOC entry 8185 (class 0 OID 0)
-- Dependencies: 543
-- Name: TABLE crm_lead_convert_config; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_lead_convert_config TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_lead_convert_config TO "_tenant";


--
-- TOC entry 8186 (class 0 OID 0)
-- Dependencies: 637
-- Name: TABLE crm_lead_dedupe_config; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_lead_dedupe_config TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_lead_dedupe_config TO "_tenant";


--
-- TOC entry 8187 (class 0 OID 0)
-- Dependencies: 545
-- Name: TABLE crm_lead_pool_config; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_lead_pool_config TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_lead_pool_config TO "_tenant";


--
-- TOC entry 8194 (class 0 OID 0)
-- Dependencies: 602
-- Name: TABLE crm_market_event; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_market_event TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_market_event TO "_tenant";


--
-- TOC entry 8201 (class 0 OID 0)
-- Dependencies: 574
-- Name: TABLE crm_marketing_content; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_marketing_content TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_marketing_content TO "_tenant";


--
-- TOC entry 8204 (class 0 OID 0)
-- Dependencies: 575
-- Name: TABLE crm_marketing_content_publish; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_marketing_content_publish TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_marketing_content_publish TO "_tenant";


--
-- TOC entry 8209 (class 0 OID 0)
-- Dependencies: 468
-- Name: TABLE crm_mcp_api_key; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_mcp_api_key TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_mcp_api_key TO "_tenant";


--
-- TOC entry 8211 (class 0 OID 0)
-- Dependencies: 467
-- Name: SEQUENCE crm_mcp_api_key_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_mcp_api_key_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_mcp_api_key_id_seq TO "_tenant";


--
-- TOC entry 8213 (class 0 OID 0)
-- Dependencies: 470
-- Name: TABLE crm_mcp_audit_log; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_mcp_audit_log TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_mcp_audit_log TO "_tenant";


--
-- TOC entry 8215 (class 0 OID 0)
-- Dependencies: 469
-- Name: SEQUENCE crm_mcp_audit_log_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_mcp_audit_log_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_mcp_audit_log_id_seq TO "_tenant";


--
-- TOC entry 8219 (class 0 OID 0)
-- Dependencies: 460
-- Name: TABLE crm_mcp_server; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_mcp_server TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_mcp_server TO "_tenant";


--
-- TOC entry 8221 (class 0 OID 0)
-- Dependencies: 459
-- Name: SEQUENCE crm_mcp_server_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_mcp_server_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_mcp_server_id_seq TO "_tenant";


--
-- TOC entry 8228 (class 0 OID 0)
-- Dependencies: 616
-- Name: TABLE crm_messenger_config; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_messenger_config TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_messenger_config TO "_tenant";


--
-- TOC entry 8230 (class 0 OID 0)
-- Dependencies: 615
-- Name: SEQUENCE crm_messenger_config_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_messenger_config_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_messenger_config_id_seq TO "_tenant";


--
-- TOC entry 8238 (class 0 OID 0)
-- Dependencies: 620
-- Name: TABLE crm_messenger_conversation; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_messenger_conversation TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_messenger_conversation TO "_tenant";


--
-- TOC entry 8240 (class 0 OID 0)
-- Dependencies: 619
-- Name: SEQUENCE crm_messenger_conversation_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_messenger_conversation_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_messenger_conversation_id_seq TO "_tenant";


--
-- TOC entry 8245 (class 0 OID 0)
-- Dependencies: 622
-- Name: TABLE crm_messenger_message; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_messenger_message TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_messenger_message TO "_tenant";


--
-- TOC entry 8247 (class 0 OID 0)
-- Dependencies: 621
-- Name: SEQUENCE crm_messenger_message_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_messenger_message_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_messenger_message_id_seq TO "_tenant";


--
-- TOC entry 8251 (class 0 OID 0)
-- Dependencies: 618
-- Name: TABLE crm_messenger_webhook_event; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_messenger_webhook_event TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_messenger_webhook_event TO "_tenant";


--
-- TOC entry 8253 (class 0 OID 0)
-- Dependencies: 617
-- Name: SEQUENCE crm_messenger_webhook_event_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_messenger_webhook_event_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_messenger_webhook_event_id_seq TO "_tenant";


--
-- TOC entry 8257 (class 0 OID 0)
-- Dependencies: 641
-- Name: TABLE crm_mobile_push_device; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_mobile_push_device TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_mobile_push_device TO "_tenant";


--
-- TOC entry 8259 (class 0 OID 0)
-- Dependencies: 640
-- Name: SEQUENCE crm_mobile_push_device_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_mobile_push_device_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_mobile_push_device_id_seq TO "_tenant";


--
-- TOC entry 8260 (class 0 OID 0)
-- Dependencies: 245
-- Name: TABLE crm_oauth_connections; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_oauth_connections TO "mega_mn6CoEbT";


--
-- TOC entry 8261 (class 0 OID 0)
-- Dependencies: 246
-- Name: TABLE crm_onepage; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_onepage TO "mega_mn6CoEbT";


--
-- TOC entry 8262 (class 0 OID 0)
-- Dependencies: 247
-- Name: TABLE crm_operator; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_operator TO "mega_mn6CoEbT";


--
-- TOC entry 8268 (class 0 OID 0)
-- Dependencies: 248
-- Name: TABLE crm_opty; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_opty TO "mega_mn6CoEbT";


--
-- TOC entry 8269 (class 0 OID 0)
-- Dependencies: 249
-- Name: TABLE crm_optyacceptance; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_optyacceptance TO "mega_mn6CoEbT";


--
-- TOC entry 8270 (class 0 OID 0)
-- Dependencies: 250
-- Name: TABLE crm_optyrelation; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_optyrelation TO "mega_mn6CoEbT";


--
-- TOC entry 8271 (class 0 OID 0)
-- Dependencies: 251
-- Name: TABLE crm_optytask; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_optytask TO "mega_mn6CoEbT";


--
-- TOC entry 8272 (class 0 OID 0)
-- Dependencies: 252
-- Name: TABLE crm_org; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_org TO "mega_mn6CoEbT";


--
-- TOC entry 8277 (class 0 OID 0)
-- Dependencies: 638
-- Name: TABLE crm_outbound_webhook; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_outbound_webhook TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_outbound_webhook TO "_tenant";


--
-- TOC entry 8281 (class 0 OID 0)
-- Dependencies: 639
-- Name: TABLE crm_outbound_webhook_delivery; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_outbound_webhook_delivery TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_outbound_webhook_delivery TO "_tenant";


--
-- TOC entry 8282 (class 0 OID 0)
-- Dependencies: 253
-- Name: TABLE crm_ownerhistory; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_ownerhistory TO "mega_mn6CoEbT";


--
-- TOC entry 8283 (class 0 OID 0)
-- Dependencies: 254
-- Name: TABLE crm_partner; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_partner TO "mega_mn6CoEbT";


--
-- TOC entry 8284 (class 0 OID 0)
-- Dependencies: 434
-- Name: TABLE crm_patient_value; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_patient_value TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_patient_value TO "_tenant";


--
-- TOC entry 8286 (class 0 OID 0)
-- Dependencies: 255
-- Name: TABLE crm_paymentterm; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_paymentterm TO "mega_mn6CoEbT";


--
-- TOC entry 8287 (class 0 OID 0)
-- Dependencies: 256
-- Name: TABLE crm_privacy; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_privacy TO "mega_mn6CoEbT";


--
-- TOC entry 8300 (class 0 OID 0)
-- Dependencies: 257
-- Name: TABLE crm_product; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_product TO "mega_mn6CoEbT";


--
-- TOC entry 8303 (class 0 OID 0)
-- Dependencies: 587
-- Name: TABLE crm_product_category; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_product_category TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_product_category TO "_tenant";


--
-- TOC entry 8307 (class 0 OID 0)
-- Dependencies: 589
-- Name: TABLE crm_product_channel_binding; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_product_channel_binding TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_product_channel_binding TO "_tenant";


--
-- TOC entry 8311 (class 0 OID 0)
-- Dependencies: 588
-- Name: TABLE crm_product_media; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_product_media TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_product_media TO "_tenant";


--
-- TOC entry 8317 (class 0 OID 0)
-- Dependencies: 599
-- Name: TABLE crm_professional_service; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_professional_service TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_professional_service TO "_tenant";


--
-- TOC entry 8318 (class 0 OID 0)
-- Dependencies: 258
-- Name: TABLE crm_pulse; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_pulse TO "mega_mn6CoEbT";


--
-- TOC entry 8320 (class 0 OID 0)
-- Dependencies: 259
-- Name: TABLE crm_quote; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_quote TO "mega_mn6CoEbT";


--
-- TOC entry 8321 (class 0 OID 0)
-- Dependencies: 260
-- Name: TABLE crm_quoteoption; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_quoteoption TO "mega_mn6CoEbT";


--
-- TOC entry 8326 (class 0 OID 0)
-- Dependencies: 635
-- Name: TABLE crm_recall_policy; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_recall_policy TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_recall_policy TO "_tenant";


--
-- TOC entry 8330 (class 0 OID 0)
-- Dependencies: 636
-- Name: TABLE crm_recall_schedule; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_recall_schedule TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_recall_schedule TO "_tenant";


--
-- TOC entry 8338 (class 0 OID 0)
-- Dependencies: 610
-- Name: TABLE crm_reminder_link; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_reminder_link TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_reminder_link TO "_tenant";


--
-- TOC entry 8340 (class 0 OID 0)
-- Dependencies: 609
-- Name: SEQUENCE crm_reminder_link_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_reminder_link_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_reminder_link_id_seq TO "_tenant";


--
-- TOC entry 8341 (class 0 OID 0)
-- Dependencies: 261
-- Name: TABLE crm_reportdetail; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_reportdetail TO "mega_mn6CoEbT";


--
-- TOC entry 8342 (class 0 OID 0)
-- Dependencies: 262
-- Name: TABLE crm_research; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_research TO "mega_mn6CoEbT";


--
-- TOC entry 8343 (class 0 OID 0)
-- Dependencies: 263
-- Name: TABLE crm_respondents; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_respondents TO "mega_mn6CoEbT";


--
-- TOC entry 8344 (class 0 OID 0)
-- Dependencies: 264
-- Name: TABLE crm_responses; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_responses TO "mega_mn6CoEbT";


--
-- TOC entry 8356 (class 0 OID 0)
-- Dependencies: 265
-- Name: TABLE crm_salesgoal; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_salesgoal TO "mega_mn6CoEbT";


--
-- TOC entry 8360 (class 0 OID 0)
-- Dependencies: 601
-- Name: TABLE crm_sanction_source_tag; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_sanction_source_tag TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_sanction_source_tag TO "_tenant";


--
-- TOC entry 8367 (class 0 OID 0)
-- Dependencies: 600
-- Name: TABLE crm_sanctioned_entity; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_sanctioned_entity TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_sanctioned_entity TO "_tenant";


--
-- TOC entry 8372 (class 0 OID 0)
-- Dependencies: 495
-- Name: TABLE crm_segment; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_segment TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_segment TO "_tenant";


--
-- TOC entry 8374 (class 0 OID 0)
-- Dependencies: 494
-- Name: SEQUENCE crm_segment_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_segment_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_segment_id_seq TO "_tenant";


--
-- TOC entry 8383 (class 0 OID 0)
-- Dependencies: 450
-- Name: TABLE crm_semantic_profile; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_semantic_profile TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_semantic_profile TO "_tenant";


--
-- TOC entry 8384 (class 0 OID 0)
-- Dependencies: 266
-- Name: TABLE crm_sentiment; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_sentiment TO "mega_mn6CoEbT";


--
-- TOC entry 8391 (class 0 OID 0)
-- Dependencies: 443
-- Name: TABLE crm_sf_express_config; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_sf_express_config TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_sf_express_config TO "_tenant";


--
-- TOC entry 8393 (class 0 OID 0)
-- Dependencies: 442
-- Name: SEQUENCE crm_sf_express_config_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_sf_express_config_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_sf_express_config_id_seq TO "_tenant";


--
-- TOC entry 8400 (class 0 OID 0)
-- Dependencies: 445
-- Name: TABLE crm_sf_express_token; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_sf_express_token TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_sf_express_token TO "_tenant";


--
-- TOC entry 8402 (class 0 OID 0)
-- Dependencies: 444
-- Name: SEQUENCE crm_sf_express_token_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_sf_express_token_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_sf_express_token_id_seq TO "_tenant";


--
-- TOC entry 8403 (class 0 OID 0)
-- Dependencies: 267
-- Name: TABLE crm_stagehistory; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_stagehistory TO "mega_mn6CoEbT";


--
-- TOC entry 8410 (class 0 OID 0)
-- Dependencies: 480
-- Name: TABLE crm_subject_map; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_subject_map TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_subject_map TO "_tenant";


--
-- TOC entry 8412 (class 0 OID 0)
-- Dependencies: 479
-- Name: SEQUENCE crm_subject_map_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_subject_map_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_subject_map_id_seq TO "_tenant";


--
-- TOC entry 8413 (class 0 OID 0)
-- Dependencies: 531
-- Name: TABLE crm_survey_outreach_log; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_survey_outreach_log TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_survey_outreach_log TO "_tenant";


--
-- TOC entry 8415 (class 0 OID 0)
-- Dependencies: 530
-- Name: SEQUENCE crm_survey_outreach_log_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_survey_outreach_log_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_survey_outreach_log_id_seq TO "_tenant";


--
-- TOC entry 8416 (class 0 OID 0)
-- Dependencies: 268
-- Name: TABLE crm_surveyresult; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_surveyresult TO "mega_mn6CoEbT";


--
-- TOC entry 8417 (class 0 OID 0)
-- Dependencies: 269
-- Name: TABLE crm_surveys; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_surveys TO "mega_mn6CoEbT";


--
-- TOC entry 8421 (class 0 OID 0)
-- Dependencies: 582
-- Name: TABLE crm_tag_channel_mapping; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_tag_channel_mapping TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_tag_channel_mapping TO "_tenant";


--
-- TOC entry 8423 (class 0 OID 0)
-- Dependencies: 581
-- Name: SEQUENCE crm_tag_channel_mapping_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_tag_channel_mapping_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_tag_channel_mapping_id_seq TO "_tenant";


--
-- TOC entry 8424 (class 0 OID 0)
-- Dependencies: 585
-- Name: TABLE crm_tag_dict_ids_backfill_backup; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_tag_dict_ids_backfill_backup TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_tag_dict_ids_backfill_backup TO "_tenant";


--
-- TOC entry 8427 (class 0 OID 0)
-- Dependencies: 584
-- Name: TABLE crm_tag_dispatch_task; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_tag_dispatch_task TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_tag_dispatch_task TO "_tenant";


--
-- TOC entry 8429 (class 0 OID 0)
-- Dependencies: 583
-- Name: SEQUENCE crm_tag_dispatch_task_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_tag_dispatch_task_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_tag_dispatch_task_id_seq TO "_tenant";


--
-- TOC entry 8430 (class 0 OID 0)
-- Dependencies: 586
-- Name: TABLE crm_tag_legacy_ids_backfill_backup; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_tag_legacy_ids_backfill_backup TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_tag_legacy_ids_backfill_backup TO "_tenant";


--
-- TOC entry 8431 (class 0 OID 0)
-- Dependencies: 270
-- Name: TABLE crm_task; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_task TO "mega_mn6CoEbT";


--
-- TOC entry 8440 (class 0 OID 0)
-- Dependencies: 596
-- Name: TABLE crm_tenant_enterprise; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_tenant_enterprise TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_tenant_enterprise TO "_tenant";


--
-- TOC entry 8445 (class 0 OID 0)
-- Dependencies: 535
-- Name: TABLE crm_tenant_website_asset; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_tenant_website_asset TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_tenant_website_asset TO "_tenant";


--
-- TOC entry 8450 (class 0 OID 0)
-- Dependencies: 534
-- Name: TABLE crm_tenant_website_page; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_tenant_website_page TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_tenant_website_page TO "_tenant";


--
-- TOC entry 8451 (class 0 OID 0)
-- Dependencies: 271
-- Name: TABLE crm_territory; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_territory TO "mega_mn6CoEbT";


--
-- TOC entry 8452 (class 0 OID 0)
-- Dependencies: 272
-- Name: TABLE crm_ticket; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_ticket TO "mega_mn6CoEbT";


--
-- TOC entry 8453 (class 0 OID 0)
-- Dependencies: 273
-- Name: TABLE crm_timeperiod; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_timeperiod TO "mega_mn6CoEbT";


--
-- TOC entry 8454 (class 0 OID 0)
-- Dependencies: 274
-- Name: TABLE crm_travel; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_travel TO "mega_mn6CoEbT";


--
-- TOC entry 8460 (class 0 OID 0)
-- Dependencies: 624
-- Name: TABLE crm_webchat_config; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_webchat_config TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_webchat_config TO "_tenant";


--
-- TOC entry 8462 (class 0 OID 0)
-- Dependencies: 623
-- Name: SEQUENCE crm_webchat_config_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_webchat_config_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_webchat_config_id_seq TO "_tenant";


--
-- TOC entry 8466 (class 0 OID 0)
-- Dependencies: 628
-- Name: TABLE crm_webchat_conversation; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_webchat_conversation TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_webchat_conversation TO "_tenant";


--
-- TOC entry 8468 (class 0 OID 0)
-- Dependencies: 627
-- Name: SEQUENCE crm_webchat_conversation_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_webchat_conversation_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_webchat_conversation_id_seq TO "_tenant";


--
-- TOC entry 8474 (class 0 OID 0)
-- Dependencies: 630
-- Name: TABLE crm_webchat_message; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_webchat_message TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_webchat_message TO "_tenant";


--
-- TOC entry 8476 (class 0 OID 0)
-- Dependencies: 629
-- Name: SEQUENCE crm_webchat_message_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_webchat_message_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_webchat_message_id_seq TO "_tenant";


--
-- TOC entry 8479 (class 0 OID 0)
-- Dependencies: 626
-- Name: TABLE crm_webchat_visitor; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_webchat_visitor TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_webchat_visitor TO "_tenant";


--
-- TOC entry 8481 (class 0 OID 0)
-- Dependencies: 625
-- Name: SEQUENCE crm_webchat_visitor_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_webchat_visitor_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_webchat_visitor_id_seq TO "_tenant";


--
-- TOC entry 8484 (class 0 OID 0)
-- Dependencies: 577
-- Name: TABLE crm_website_page_view; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_website_page_view TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_website_page_view TO "_tenant";


--
-- TOC entry 8488 (class 0 OID 0)
-- Dependencies: 576
-- Name: TABLE crm_website_visitor_session; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_website_visitor_session TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_website_visitor_session TO "_tenant";


--
-- TOC entry 8495 (class 0 OID 0)
-- Dependencies: 592
-- Name: TABLE crm_wechat_miniapp_config; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wechat_miniapp_config TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wechat_miniapp_config TO "_tenant";


--
-- TOC entry 8497 (class 0 OID 0)
-- Dependencies: 591
-- Name: SEQUENCE crm_wechat_miniapp_config_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wechat_miniapp_config_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wechat_miniapp_config_id_seq TO "_tenant";


--
-- TOC entry 8505 (class 0 OID 0)
-- Dependencies: 551
-- Name: TABLE crm_wechat_mp_article; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wechat_mp_article TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wechat_mp_article TO "_tenant";


--
-- TOC entry 8511 (class 0 OID 0)
-- Dependencies: 650
-- Name: TABLE crm_wechat_mp_article_comment; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wechat_mp_article_comment TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wechat_mp_article_comment TO "_tenant";


--
-- TOC entry 8513 (class 0 OID 0)
-- Dependencies: 649
-- Name: SEQUENCE crm_wechat_mp_article_comment_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wechat_mp_article_comment_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wechat_mp_article_comment_id_seq TO "_tenant";


--
-- TOC entry 8524 (class 0 OID 0)
-- Dependencies: 553
-- Name: TABLE crm_wechat_mp_article_draft; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wechat_mp_article_draft TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wechat_mp_article_draft TO "_tenant";


--
-- TOC entry 8526 (class 0 OID 0)
-- Dependencies: 552
-- Name: SEQUENCE crm_wechat_mp_article_draft_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wechat_mp_article_draft_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wechat_mp_article_draft_id_seq TO "_tenant";


--
-- TOC entry 8528 (class 0 OID 0)
-- Dependencies: 550
-- Name: SEQUENCE crm_wechat_mp_article_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wechat_mp_article_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wechat_mp_article_id_seq TO "_tenant";


--
-- TOC entry 8545 (class 0 OID 0)
-- Dependencies: 559
-- Name: TABLE crm_wechat_mp_article_stat; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wechat_mp_article_stat TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wechat_mp_article_stat TO "_tenant";


--
-- TOC entry 8547 (class 0 OID 0)
-- Dependencies: 558
-- Name: SEQUENCE crm_wechat_mp_article_stat_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wechat_mp_article_stat_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wechat_mp_article_stat_id_seq TO "_tenant";


--
-- TOC entry 8548 (class 0 OID 0)
-- Dependencies: 511
-- Name: TABLE crm_wechat_mp_auto_reply; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wechat_mp_auto_reply TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wechat_mp_auto_reply TO "_tenant";


--
-- TOC entry 8550 (class 0 OID 0)
-- Dependencies: 510
-- Name: SEQUENCE crm_wechat_mp_auto_reply_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wechat_mp_auto_reply_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wechat_mp_auto_reply_id_seq TO "_tenant";


--
-- TOC entry 8552 (class 0 OID 0)
-- Dependencies: 489
-- Name: TABLE crm_wechat_mp_callback_log; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wechat_mp_callback_log TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wechat_mp_callback_log TO "_tenant";


--
-- TOC entry 8554 (class 0 OID 0)
-- Dependencies: 488
-- Name: SEQUENCE crm_wechat_mp_callback_log_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wechat_mp_callback_log_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wechat_mp_callback_log_id_seq TO "_tenant";


--
-- TOC entry 8555 (class 0 OID 0)
-- Dependencies: 652
-- Name: TABLE crm_wechat_mp_comment_auto_reply_rule; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wechat_mp_comment_auto_reply_rule TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wechat_mp_comment_auto_reply_rule TO "_tenant";


--
-- TOC entry 8557 (class 0 OID 0)
-- Dependencies: 651
-- Name: SEQUENCE crm_wechat_mp_comment_auto_reply_rule_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wechat_mp_comment_auto_reply_rule_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wechat_mp_comment_auto_reply_rule_id_seq TO "_tenant";


--
-- TOC entry 8566 (class 0 OID 0)
-- Dependencies: 493
-- Name: TABLE crm_wechat_mp_config; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wechat_mp_config TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wechat_mp_config TO "_tenant";


--
-- TOC entry 8568 (class 0 OID 0)
-- Dependencies: 492
-- Name: SEQUENCE crm_wechat_mp_config_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wechat_mp_config_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wechat_mp_config_id_seq TO "_tenant";


--
-- TOC entry 8577 (class 0 OID 0)
-- Dependencies: 612
-- Name: TABLE crm_wechat_mp_conversation; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wechat_mp_conversation TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wechat_mp_conversation TO "_tenant";


--
-- TOC entry 8579 (class 0 OID 0)
-- Dependencies: 611
-- Name: SEQUENCE crm_wechat_mp_conversation_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wechat_mp_conversation_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wechat_mp_conversation_id_seq TO "_tenant";


--
-- TOC entry 8580 (class 0 OID 0)
-- Dependencies: 503
-- Name: TABLE crm_wechat_mp_fan; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wechat_mp_fan TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wechat_mp_fan TO "_tenant";


--
-- TOC entry 8582 (class 0 OID 0)
-- Dependencies: 502
-- Name: SEQUENCE crm_wechat_mp_fan_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wechat_mp_fan_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wechat_mp_fan_id_seq TO "_tenant";


--
-- TOC entry 8587 (class 0 OID 0)
-- Dependencies: 614
-- Name: TABLE crm_wechat_mp_mass_task; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wechat_mp_mass_task TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wechat_mp_mass_task TO "_tenant";


--
-- TOC entry 8589 (class 0 OID 0)
-- Dependencies: 613
-- Name: SEQUENCE crm_wechat_mp_mass_task_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wechat_mp_mass_task_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wechat_mp_mass_task_id_seq TO "_tenant";


--
-- TOC entry 8596 (class 0 OID 0)
-- Dependencies: 555
-- Name: TABLE crm_wechat_mp_material; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wechat_mp_material TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wechat_mp_material TO "_tenant";


--
-- TOC entry 8598 (class 0 OID 0)
-- Dependencies: 554
-- Name: SEQUENCE crm_wechat_mp_material_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wechat_mp_material_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wechat_mp_material_id_seq TO "_tenant";


--
-- TOC entry 8599 (class 0 OID 0)
-- Dependencies: 497
-- Name: TABLE crm_wechat_mp_menu; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wechat_mp_menu TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wechat_mp_menu TO "_tenant";


--
-- TOC entry 8601 (class 0 OID 0)
-- Dependencies: 496
-- Name: SEQUENCE crm_wechat_mp_menu_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wechat_mp_menu_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wechat_mp_menu_id_seq TO "_tenant";


--
-- TOC entry 8602 (class 0 OID 0)
-- Dependencies: 499
-- Name: TABLE crm_wechat_mp_menu_key_label; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wechat_mp_menu_key_label TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wechat_mp_menu_key_label TO "_tenant";


--
-- TOC entry 8604 (class 0 OID 0)
-- Dependencies: 498
-- Name: SEQUENCE crm_wechat_mp_menu_key_label_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wechat_mp_menu_key_label_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wechat_mp_menu_key_label_id_seq TO "_tenant";


--
-- TOC entry 8607 (class 0 OID 0)
-- Dependencies: 505
-- Name: TABLE crm_wechat_mp_message; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wechat_mp_message TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wechat_mp_message TO "_tenant";


--
-- TOC entry 8609 (class 0 OID 0)
-- Dependencies: 504
-- Name: SEQUENCE crm_wechat_mp_message_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wechat_mp_message_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wechat_mp_message_id_seq TO "_tenant";


--
-- TOC entry 8612 (class 0 OID 0)
-- Dependencies: 513
-- Name: TABLE crm_wechat_mp_message_preset; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wechat_mp_message_preset TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wechat_mp_message_preset TO "_tenant";


--
-- TOC entry 8614 (class 0 OID 0)
-- Dependencies: 512
-- Name: SEQUENCE crm_wechat_mp_message_preset_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wechat_mp_message_preset_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wechat_mp_message_preset_id_seq TO "_tenant";


--
-- TOC entry 8615 (class 0 OID 0)
-- Dependencies: 507
-- Name: TABLE crm_wechat_mp_message_task; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wechat_mp_message_task TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wechat_mp_message_task TO "_tenant";


--
-- TOC entry 8617 (class 0 OID 0)
-- Dependencies: 506
-- Name: SEQUENCE crm_wechat_mp_message_task_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wechat_mp_message_task_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wechat_mp_message_task_id_seq TO "_tenant";


--
-- TOC entry 8618 (class 0 OID 0)
-- Dependencies: 509
-- Name: TABLE crm_wechat_mp_message_task_item; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wechat_mp_message_task_item TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wechat_mp_message_task_item TO "_tenant";


--
-- TOC entry 8620 (class 0 OID 0)
-- Dependencies: 508
-- Name: SEQUENCE crm_wechat_mp_message_task_item_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wechat_mp_message_task_item_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wechat_mp_message_task_item_id_seq TO "_tenant";


--
-- TOC entry 8630 (class 0 OID 0)
-- Dependencies: 557
-- Name: TABLE crm_wechat_mp_publish_task; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wechat_mp_publish_task TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wechat_mp_publish_task TO "_tenant";


--
-- TOC entry 8632 (class 0 OID 0)
-- Dependencies: 556
-- Name: SEQUENCE crm_wechat_mp_publish_task_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wechat_mp_publish_task_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wechat_mp_publish_task_id_seq TO "_tenant";


--
-- TOC entry 8633 (class 0 OID 0)
-- Dependencies: 501
-- Name: TABLE crm_wechat_mp_qrcode; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wechat_mp_qrcode TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wechat_mp_qrcode TO "_tenant";


--
-- TOC entry 8635 (class 0 OID 0)
-- Dependencies: 500
-- Name: SEQUENCE crm_wechat_mp_qrcode_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wechat_mp_qrcode_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wechat_mp_qrcode_id_seq TO "_tenant";


--
-- TOC entry 8641 (class 0 OID 0)
-- Dependencies: 482
-- Name: TABLE crm_wecom_aibot_config; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wecom_aibot_config TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wecom_aibot_config TO "_tenant";


--
-- TOC entry 8643 (class 0 OID 0)
-- Dependencies: 481
-- Name: SEQUENCE crm_wecom_aibot_config_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wecom_aibot_config_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wecom_aibot_config_id_seq TO "_tenant";


--
-- TOC entry 8653 (class 0 OID 0)
-- Dependencies: 484
-- Name: TABLE crm_wecom_aibot_connection; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wecom_aibot_connection TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wecom_aibot_connection TO "_tenant";


--
-- TOC entry 8655 (class 0 OID 0)
-- Dependencies: 483
-- Name: SEQUENCE crm_wecom_aibot_connection_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wecom_aibot_connection_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wecom_aibot_connection_id_seq TO "_tenant";


--
-- TOC entry 8660 (class 0 OID 0)
-- Dependencies: 539
-- Name: TABLE crm_wecom_behavior_daily; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wecom_behavior_daily TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wecom_behavior_daily TO "_tenant";


--
-- TOC entry 8662 (class 0 OID 0)
-- Dependencies: 538
-- Name: SEQUENCE crm_wecom_behavior_daily_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wecom_behavior_daily_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wecom_behavior_daily_id_seq TO "_tenant";


--
-- TOC entry 8678 (class 0 OID 0)
-- Dependencies: 478
-- Name: TABLE crm_wecom_callback_event; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wecom_callback_event TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wecom_callback_event TO "_tenant";


--
-- TOC entry 8680 (class 0 OID 0)
-- Dependencies: 477
-- Name: SEQUENCE crm_wecom_callback_event_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wecom_callback_event_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wecom_callback_event_id_seq TO "_tenant";


--
-- TOC entry 8686 (class 0 OID 0)
-- Dependencies: 275
-- Name: TABLE crm_wecom_config; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wecom_config TO "mega_mn6CoEbT";


--
-- TOC entry 8688 (class 0 OID 0)
-- Dependencies: 438
-- Name: SEQUENCE crm_wecom_config_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.crm_wecom_config_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8690 (class 0 OID 0)
-- Dependencies: 644
-- Name: TABLE crm_wecom_contact_group; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wecom_contact_group TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wecom_contact_group TO "_tenant";


--
-- TOC entry 8694 (class 0 OID 0)
-- Dependencies: 276
-- Name: TABLE crm_wecom_contact_way; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wecom_contact_way TO "mega_mn6CoEbT";


--
-- TOC entry 8696 (class 0 OID 0)
-- Dependencies: 439
-- Name: SEQUENCE crm_wecom_contact_way_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.crm_wecom_contact_way_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8698 (class 0 OID 0)
-- Dependencies: 277
-- Name: TABLE crm_wecom_external_contact; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wecom_external_contact TO "mega_mn6CoEbT";


--
-- TOC entry 8700 (class 0 OID 0)
-- Dependencies: 440
-- Name: SEQUENCE crm_wecom_external_contact_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.crm_wecom_external_contact_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8702 (class 0 OID 0)
-- Dependencies: 645
-- Name: TABLE crm_wecom_group_provision_task; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wecom_group_provision_task TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wecom_group_provision_task TO "_tenant";


--
-- TOC entry 8704 (class 0 OID 0)
-- Dependencies: 647
-- Name: TABLE crm_wecom_group_reminder; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wecom_group_reminder TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wecom_group_reminder TO "_tenant";


--
-- TOC entry 8706 (class 0 OID 0)
-- Dependencies: 646
-- Name: TABLE crm_wecom_group_reminder_rule; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wecom_group_reminder_rule TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wecom_group_reminder_rule TO "_tenant";


--
-- TOC entry 8711 (class 0 OID 0)
-- Dependencies: 541
-- Name: TABLE crm_wecom_groupchat_daily; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wecom_groupchat_daily TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wecom_groupchat_daily TO "_tenant";


--
-- TOC entry 8713 (class 0 OID 0)
-- Dependencies: 540
-- Name: SEQUENCE crm_wecom_groupchat_daily_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wecom_groupchat_daily_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wecom_groupchat_daily_id_seq TO "_tenant";


--
-- TOC entry 8715 (class 0 OID 0)
-- Dependencies: 472
-- Name: TABLE crm_wecom_kf_config; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wecom_kf_config TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wecom_kf_config TO "_tenant";


--
-- TOC entry 8717 (class 0 OID 0)
-- Dependencies: 471
-- Name: SEQUENCE crm_wecom_kf_config_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wecom_kf_config_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wecom_kf_config_id_seq TO "_tenant";


--
-- TOC entry 8727 (class 0 OID 0)
-- Dependencies: 654
-- Name: TABLE crm_wecom_kf_conversation; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wecom_kf_conversation TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wecom_kf_conversation TO "_tenant";


--
-- TOC entry 8729 (class 0 OID 0)
-- Dependencies: 653
-- Name: SEQUENCE crm_wecom_kf_conversation_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wecom_kf_conversation_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wecom_kf_conversation_id_seq TO "_tenant";


--
-- TOC entry 8743 (class 0 OID 0)
-- Dependencies: 474
-- Name: TABLE crm_wecom_kf_message; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wecom_kf_message TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wecom_kf_message TO "_tenant";


--
-- TOC entry 8745 (class 0 OID 0)
-- Dependencies: 473
-- Name: SEQUENCE crm_wecom_kf_message_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wecom_kf_message_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wecom_kf_message_id_seq TO "_tenant";


--
-- TOC entry 8749 (class 0 OID 0)
-- Dependencies: 656
-- Name: TABLE crm_wecom_kf_sync_cursor; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wecom_kf_sync_cursor TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wecom_kf_sync_cursor TO "_tenant";


--
-- TOC entry 8751 (class 0 OID 0)
-- Dependencies: 655
-- Name: SEQUENCE crm_wecom_kf_sync_cursor_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wecom_kf_sync_cursor_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wecom_kf_sync_cursor_id_seq TO "_tenant";


--
-- TOC entry 8753 (class 0 OID 0)
-- Dependencies: 278
-- Name: TABLE crm_wecom_mass_task; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wecom_mass_task TO "mega_mn6CoEbT";


--
-- TOC entry 8755 (class 0 OID 0)
-- Dependencies: 441
-- Name: SEQUENCE crm_wecom_mass_task_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.crm_wecom_mass_task_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8770 (class 0 OID 0)
-- Dependencies: 476
-- Name: TABLE crm_wecom_material; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wecom_material TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wecom_material TO "_tenant";


--
-- TOC entry 8772 (class 0 OID 0)
-- Dependencies: 475
-- Name: SEQUENCE crm_wecom_material_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wecom_material_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wecom_material_id_seq TO "_tenant";


--
-- TOC entry 8776 (class 0 OID 0)
-- Dependencies: 542
-- Name: TABLE crm_wecom_stats_meta; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wecom_stats_meta TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wecom_stats_meta TO "_tenant";


--
-- TOC entry 8778 (class 0 OID 0)
-- Dependencies: 437
-- Name: TABLE crm_wecom_welcome_log; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_wecom_welcome_log TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_wecom_welcome_log TO "_tenant";


--
-- TOC entry 8780 (class 0 OID 0)
-- Dependencies: 436
-- Name: SEQUENCE crm_wecom_welcome_log_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_wecom_welcome_log_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_wecom_welcome_log_id_seq TO "_tenant";


--
-- TOC entry 8781 (class 0 OID 0)
-- Dependencies: 279
-- Name: TABLE crm_weeklyreport; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_weeklyreport TO "mega_mn6CoEbT";


--
-- TOC entry 8785 (class 0 OID 0)
-- Dependencies: 608
-- Name: TABLE crm_whatsapp_automation_rule; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_whatsapp_automation_rule TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_whatsapp_automation_rule TO "_tenant";


--
-- TOC entry 8787 (class 0 OID 0)
-- Dependencies: 607
-- Name: SEQUENCE crm_whatsapp_automation_rule_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_whatsapp_automation_rule_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_whatsapp_automation_rule_id_seq TO "_tenant";


--
-- TOC entry 8794 (class 0 OID 0)
-- Dependencies: 563
-- Name: TABLE crm_whatsapp_config; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_whatsapp_config TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_whatsapp_config TO "_tenant";


--
-- TOC entry 8796 (class 0 OID 0)
-- Dependencies: 562
-- Name: SEQUENCE crm_whatsapp_config_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_whatsapp_config_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_whatsapp_config_id_seq TO "_tenant";


--
-- TOC entry 8804 (class 0 OID 0)
-- Dependencies: 567
-- Name: TABLE crm_whatsapp_conversation; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_whatsapp_conversation TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_whatsapp_conversation TO "_tenant";


--
-- TOC entry 8806 (class 0 OID 0)
-- Dependencies: 566
-- Name: SEQUENCE crm_whatsapp_conversation_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_whatsapp_conversation_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_whatsapp_conversation_id_seq TO "_tenant";


--
-- TOC entry 8813 (class 0 OID 0)
-- Dependencies: 569
-- Name: TABLE crm_whatsapp_message; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_whatsapp_message TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_whatsapp_message TO "_tenant";


--
-- TOC entry 8815 (class 0 OID 0)
-- Dependencies: 568
-- Name: SEQUENCE crm_whatsapp_message_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_whatsapp_message_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_whatsapp_message_id_seq TO "_tenant";


--
-- TOC entry 8819 (class 0 OID 0)
-- Dependencies: 571
-- Name: TABLE crm_whatsapp_template; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_whatsapp_template TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_whatsapp_template TO "_tenant";


--
-- TOC entry 8821 (class 0 OID 0)
-- Dependencies: 570
-- Name: SEQUENCE crm_whatsapp_template_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_whatsapp_template_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_whatsapp_template_id_seq TO "_tenant";


--
-- TOC entry 8825 (class 0 OID 0)
-- Dependencies: 606
-- Name: TABLE crm_whatsapp_template_send_item; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_whatsapp_template_send_item TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_whatsapp_template_send_item TO "_tenant";


--
-- TOC entry 8827 (class 0 OID 0)
-- Dependencies: 605
-- Name: SEQUENCE crm_whatsapp_template_send_item_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_whatsapp_template_send_item_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_whatsapp_template_send_item_id_seq TO "_tenant";


--
-- TOC entry 8831 (class 0 OID 0)
-- Dependencies: 604
-- Name: TABLE crm_whatsapp_template_send_task; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_whatsapp_template_send_task TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_whatsapp_template_send_task TO "_tenant";


--
-- TOC entry 8833 (class 0 OID 0)
-- Dependencies: 603
-- Name: SEQUENCE crm_whatsapp_template_send_task_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_whatsapp_template_send_task_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_whatsapp_template_send_task_id_seq TO "_tenant";


--
-- TOC entry 8840 (class 0 OID 0)
-- Dependencies: 565
-- Name: TABLE crm_whatsapp_webhook_event; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_whatsapp_webhook_event TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_whatsapp_webhook_event TO "_tenant";


--
-- TOC entry 8842 (class 0 OID 0)
-- Dependencies: 564
-- Name: SEQUENCE crm_whatsapp_webhook_event_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.crm_whatsapp_webhook_event_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.crm_whatsapp_webhook_event_id_seq TO "_tenant";


--
-- TOC entry 8847 (class 0 OID 0)
-- Dependencies: 433
-- Name: TABLE crm_workflow_automation; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_workflow_automation TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.crm_workflow_automation TO "_tenant";


--
-- TOC entry 8848 (class 0 OID 0)
-- Dependencies: 281
-- Name: TABLE crm_workflow_trigger; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.crm_workflow_trigger TO "mega_mn6CoEbT";


--
-- TOC entry 8849 (class 0 OID 0)
-- Dependencies: 280
-- Name: SEQUENCE crm_workflow_trigger_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.crm_workflow_trigger_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8853 (class 0 OID 0)
-- Dependencies: 487
-- Name: TABLE customer_metrics; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.customer_metrics TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.customer_metrics TO "_tenant";


--
-- TOC entry 8854 (class 0 OID 0)
-- Dependencies: 282
-- Name: TABLE den_inventory; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.den_inventory TO "mega_mn6CoEbT";


--
-- TOC entry 8855 (class 0 OID 0)
-- Dependencies: 283
-- Name: TABLE den_staff; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.den_staff TO "mega_mn6CoEbT";


--
-- TOC entry 8856 (class 0 OID 0)
-- Dependencies: 284
-- Name: TABLE den_staff_bank; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.den_staff_bank TO "mega_mn6CoEbT";


--
-- TOC entry 8857 (class 0 OID 0)
-- Dependencies: 285
-- Name: TABLE den_vendor; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.den_vendor TO "mega_mn6CoEbT";


--
-- TOC entry 8858 (class 0 OID 0)
-- Dependencies: 286
-- Name: TABLE ems_hcp; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.ems_hcp TO "mega_mn6CoEbT";


--
-- TOC entry 8859 (class 0 OID 0)
-- Dependencies: 417
-- Name: TABLE flw_channel_definition; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.flw_channel_definition TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.flw_channel_definition TO "_tenant";


--
-- TOC entry 8860 (class 0 OID 0)
-- Dependencies: 418
-- Name: TABLE flw_ev_databasechangelog; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.flw_ev_databasechangelog TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.flw_ev_databasechangelog TO "_tenant";


--
-- TOC entry 8861 (class 0 OID 0)
-- Dependencies: 419
-- Name: TABLE flw_ev_databasechangeloglock; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.flw_ev_databasechangeloglock TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.flw_ev_databasechangeloglock TO "_tenant";


--
-- TOC entry 8862 (class 0 OID 0)
-- Dependencies: 420
-- Name: TABLE flw_event_definition; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.flw_event_definition TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.flw_event_definition TO "_tenant";


--
-- TOC entry 8863 (class 0 OID 0)
-- Dependencies: 421
-- Name: TABLE flw_event_deployment; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.flw_event_deployment TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.flw_event_deployment TO "_tenant";


--
-- TOC entry 8864 (class 0 OID 0)
-- Dependencies: 422
-- Name: TABLE flw_event_resource; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.flw_event_resource TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.flw_event_resource TO "_tenant";


--
-- TOC entry 8865 (class 0 OID 0)
-- Dependencies: 423
-- Name: TABLE flw_ru_batch; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.flw_ru_batch TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.flw_ru_batch TO "_tenant";


--
-- TOC entry 8866 (class 0 OID 0)
-- Dependencies: 424
-- Name: TABLE flw_ru_batch_part; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.flw_ru_batch_part TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.flw_ru_batch_part TO "_tenant";


--
-- TOC entry 8867 (class 0 OID 0)
-- Dependencies: 288
-- Name: TABLE gen_table; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.gen_table TO "mega_mn6CoEbT";


--
-- TOC entry 8868 (class 0 OID 0)
-- Dependencies: 290
-- Name: TABLE gen_table_column; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.gen_table_column TO "mega_mn6CoEbT";


--
-- TOC entry 8869 (class 0 OID 0)
-- Dependencies: 289
-- Name: SEQUENCE gen_table_column_column_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.gen_table_column_column_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8870 (class 0 OID 0)
-- Dependencies: 287
-- Name: SEQUENCE gen_table_table_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.gen_table_table_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8871 (class 0 OID 0)
-- Dependencies: 292
-- Name: TABLE master_registration; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.master_registration TO "mega_mn6CoEbT";


--
-- TOC entry 8872 (class 0 OID 0)
-- Dependencies: 291
-- Name: SEQUENCE master_registration_regist_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.master_registration_regist_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8873 (class 0 OID 0)
-- Dependencies: 294
-- Name: TABLE master_tenant; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.master_tenant TO "mega_mn6CoEbT";


--
-- TOC entry 8874 (class 0 OID 0)
-- Dependencies: 293
-- Name: SEQUENCE master_tenant_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.master_tenant_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8875 (class 0 OID 0)
-- Dependencies: 425
-- Name: TABLE session; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.session TO "mega_mn6CoEbT";


--
-- TOC entry 8886 (class 0 OID 0)
-- Dependencies: 549
-- Name: TABLE smart_push_execution_log; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.smart_push_execution_log TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.smart_push_execution_log TO "_tenant";


--
-- TOC entry 8888 (class 0 OID 0)
-- Dependencies: 548
-- Name: SEQUENCE smart_push_execution_log_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.smart_push_execution_log_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.smart_push_execution_log_id_seq TO "_tenant";


--
-- TOC entry 8898 (class 0 OID 0)
-- Dependencies: 547
-- Name: TABLE smart_push_strategy; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.smart_push_strategy TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.smart_push_strategy TO "_tenant";


--
-- TOC entry 8900 (class 0 OID 0)
-- Dependencies: 546
-- Name: SEQUENCE smart_push_strategy_id_seq; Type: ACL; Schema: public; Owner: luohd
--

GRANT ALL ON SEQUENCE public.smart_push_strategy_id_seq TO "mega_mn6CoEbT";
GRANT ALL ON SEQUENCE public.smart_push_strategy_id_seq TO "_tenant";


--
-- TOC entry 8901 (class 0 OID 0)
-- Dependencies: 296
-- Name: TABLE sys_config; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_config TO "mega_mn6CoEbT";


--
-- TOC entry 8902 (class 0 OID 0)
-- Dependencies: 295
-- Name: SEQUENCE sys_config_config_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.sys_config_config_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8903 (class 0 OID 0)
-- Dependencies: 298
-- Name: TABLE sys_deploy_form; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_deploy_form TO "mega_mn6CoEbT";


--
-- TOC entry 8904 (class 0 OID 0)
-- Dependencies: 297
-- Name: SEQUENCE sys_deploy_form_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.sys_deploy_form_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8905 (class 0 OID 0)
-- Dependencies: 300
-- Name: TABLE sys_dept; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_dept TO "mega_mn6CoEbT";


--
-- TOC entry 8906 (class 0 OID 0)
-- Dependencies: 299
-- Name: SEQUENCE sys_dept_dept_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.sys_dept_dept_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8907 (class 0 OID 0)
-- Dependencies: 302
-- Name: TABLE sys_dict_data; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_dict_data TO "mega_mn6CoEbT";


--
-- TOC entry 8908 (class 0 OID 0)
-- Dependencies: 301
-- Name: SEQUENCE sys_dict_data_dict_code_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.sys_dict_data_dict_code_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8909 (class 0 OID 0)
-- Dependencies: 304
-- Name: TABLE sys_dict_type; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_dict_type TO "mega_mn6CoEbT";


--
-- TOC entry 8910 (class 0 OID 0)
-- Dependencies: 303
-- Name: SEQUENCE sys_dict_type_dict_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.sys_dict_type_dict_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8911 (class 0 OID 0)
-- Dependencies: 306
-- Name: TABLE sys_ems_corres; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_ems_corres TO "mega_mn6CoEbT";


--
-- TOC entry 8912 (class 0 OID 0)
-- Dependencies: 305
-- Name: SEQUENCE sys_ems_corres_corres_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.sys_ems_corres_corres_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8913 (class 0 OID 0)
-- Dependencies: 308
-- Name: TABLE sys_form; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_form TO "mega_mn6CoEbT";


--
-- TOC entry 8914 (class 0 OID 0)
-- Dependencies: 307
-- Name: SEQUENCE sys_form_form_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.sys_form_form_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8915 (class 0 OID 0)
-- Dependencies: 309
-- Name: TABLE sys_hi_audittrail; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_hi_audittrail TO "mega_mn6CoEbT";


--
-- TOC entry 8916 (class 0 OID 0)
-- Dependencies: 310
-- Name: TABLE sys_job; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_job TO "mega_mn6CoEbT";


--
-- TOC entry 8917 (class 0 OID 0)
-- Dependencies: 312
-- Name: TABLE sys_job_log; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_job_log TO "mega_mn6CoEbT";


--
-- TOC entry 8918 (class 0 OID 0)
-- Dependencies: 311
-- Name: SEQUENCE sys_job_log_job_log_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.sys_job_log_job_log_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8919 (class 0 OID 0)
-- Dependencies: 314
-- Name: TABLE sys_logininfor; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_logininfor TO "mega_mn6CoEbT";


--
-- TOC entry 8920 (class 0 OID 0)
-- Dependencies: 313
-- Name: SEQUENCE sys_logininfor_info_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.sys_logininfor_info_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8921 (class 0 OID 0)
-- Dependencies: 316
-- Name: TABLE sys_menu; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_menu TO "mega_mn6CoEbT";


--
-- TOC entry 8922 (class 0 OID 0)
-- Dependencies: 315
-- Name: SEQUENCE sys_menu_menu_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.sys_menu_menu_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8923 (class 0 OID 0)
-- Dependencies: 318
-- Name: TABLE sys_notice; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_notice TO "mega_mn6CoEbT";


--
-- TOC entry 8924 (class 0 OID 0)
-- Dependencies: 317
-- Name: SEQUENCE sys_notice_notice_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.sys_notice_notice_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8925 (class 0 OID 0)
-- Dependencies: 320
-- Name: TABLE sys_oper_log; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_oper_log TO "mega_mn6CoEbT";


--
-- TOC entry 8926 (class 0 OID 0)
-- Dependencies: 319
-- Name: SEQUENCE sys_oper_log_oper_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.sys_oper_log_oper_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8930 (class 0 OID 0)
-- Dependencies: 321
-- Name: TABLE sys_organization; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_organization TO "mega_mn6CoEbT";


--
-- TOC entry 8931 (class 0 OID 0)
-- Dependencies: 323
-- Name: TABLE sys_pm_log; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_pm_log TO "mega_mn6CoEbT";


--
-- TOC entry 8932 (class 0 OID 0)
-- Dependencies: 322
-- Name: SEQUENCE sys_pm_log_log_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.sys_pm_log_log_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8933 (class 0 OID 0)
-- Dependencies: 325
-- Name: TABLE sys_post; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_post TO "mega_mn6CoEbT";


--
-- TOC entry 8934 (class 0 OID 0)
-- Dependencies: 324
-- Name: SEQUENCE sys_post_post_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.sys_post_post_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8935 (class 0 OID 0)
-- Dependencies: 326
-- Name: TABLE sys_re_acceptance; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_re_acceptance TO "mega_mn6CoEbT";


--
-- TOC entry 8936 (class 0 OID 0)
-- Dependencies: 327
-- Name: TABLE sys_re_actitem; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_re_actitem TO "mega_mn6CoEbT";


--
-- TOC entry 8937 (class 0 OID 0)
-- Dependencies: 328
-- Name: TABLE sys_re_activity; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_re_activity TO "mega_mn6CoEbT";


--
-- TOC entry 8938 (class 0 OID 0)
-- Dependencies: 329
-- Name: TABLE sys_re_application; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_re_application TO "mega_mn6CoEbT";


--
-- TOC entry 8939 (class 0 OID 0)
-- Dependencies: 330
-- Name: TABLE sys_re_board; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_re_board TO "mega_mn6CoEbT";


--
-- TOC entry 8940 (class 0 OID 0)
-- Dependencies: 332
-- Name: TABLE sys_re_casetype; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_re_casetype TO "mega_mn6CoEbT";


--
-- TOC entry 8941 (class 0 OID 0)
-- Dependencies: 331
-- Name: SEQUENCE sys_re_casetype_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.sys_re_casetype_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8942 (class 0 OID 0)
-- Dependencies: 333
-- Name: TABLE sys_re_checklist; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_re_checklist TO "mega_mn6CoEbT";


--
-- TOC entry 8943 (class 0 OID 0)
-- Dependencies: 334
-- Name: TABLE sys_re_corres; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_re_corres TO "mega_mn6CoEbT";


--
-- TOC entry 8944 (class 0 OID 0)
-- Dependencies: 335
-- Name: TABLE sys_re_datafield; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_re_datafield TO "mega_mn6CoEbT";


--
-- TOC entry 8945 (class 0 OID 0)
-- Dependencies: 336
-- Name: TABLE sys_re_dataobj; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_re_dataobj TO "mega_mn6CoEbT";


--
-- TOC entry 8946 (class 0 OID 0)
-- Dependencies: 338
-- Name: TABLE sys_re_datatransform; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_re_datatransform TO "mega_mn6CoEbT";


--
-- TOC entry 8947 (class 0 OID 0)
-- Dependencies: 337
-- Name: SEQUENCE sys_re_datatransform_py_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.sys_re_datatransform_py_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8948 (class 0 OID 0)
-- Dependencies: 340
-- Name: TABLE sys_re_datatype; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_re_datatype TO "mega_mn6CoEbT";


--
-- TOC entry 8949 (class 0 OID 0)
-- Dependencies: 339
-- Name: SEQUENCE sys_re_datatype_data_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.sys_re_datatype_data_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8950 (class 0 OID 0)
-- Dependencies: 341
-- Name: TABLE sys_re_decisiontable; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_re_decisiontable TO "mega_mn6CoEbT";


--
-- TOC entry 8951 (class 0 OID 0)
-- Dependencies: 342
-- Name: TABLE sys_re_decisiontree; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_re_decisiontree TO "mega_mn6CoEbT";


--
-- TOC entry 8952 (class 0 OID 0)
-- Dependencies: 343
-- Name: TABLE sys_re_dtitem; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_re_dtitem TO "mega_mn6CoEbT";


--
-- TOC entry 8953 (class 0 OID 0)
-- Dependencies: 344
-- Name: TABLE sys_re_participant; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_re_participant TO "mega_mn6CoEbT";


--
-- TOC entry 8954 (class 0 OID 0)
-- Dependencies: 345
-- Name: TABLE sys_re_pmchange; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_re_pmchange TO "mega_mn6CoEbT";


--
-- TOC entry 8955 (class 0 OID 0)
-- Dependencies: 346
-- Name: TABLE sys_re_pmrisk; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_re_pmrisk TO "mega_mn6CoEbT";


--
-- TOC entry 8956 (class 0 OID 0)
-- Dependencies: 347
-- Name: TABLE sys_re_process; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_re_process TO "mega_mn6CoEbT";


--
-- TOC entry 8957 (class 0 OID 0)
-- Dependencies: 348
-- Name: TABLE sys_re_project; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_re_project TO "mega_mn6CoEbT";


--
-- TOC entry 8958 (class 0 OID 0)
-- Dependencies: 349
-- Name: TABLE sys_re_reportdefinition; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_re_reportdefinition TO "mega_mn6CoEbT";


--
-- TOC entry 8959 (class 0 OID 0)
-- Dependencies: 351
-- Name: TABLE sys_re_ruleset; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_re_ruleset TO "mega_mn6CoEbT";


--
-- TOC entry 8960 (class 0 OID 0)
-- Dependencies: 350
-- Name: SEQUENCE sys_re_ruleset_py_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.sys_re_ruleset_py_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8961 (class 0 OID 0)
-- Dependencies: 353
-- Name: TABLE sys_re_stage; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_re_stage TO "mega_mn6CoEbT";


--
-- TOC entry 8962 (class 0 OID 0)
-- Dependencies: 352
-- Name: SEQUENCE sys_re_stage_stage_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.sys_re_stage_stage_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8963 (class 0 OID 0)
-- Dependencies: 354
-- Name: TABLE sys_re_strategy; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_re_strategy TO "mega_mn6CoEbT";


--
-- TOC entry 8964 (class 0 OID 0)
-- Dependencies: 355
-- Name: TABLE sys_re_talkingplan; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_re_talkingplan TO "mega_mn6CoEbT";


--
-- TOC entry 8965 (class 0 OID 0)
-- Dependencies: 356
-- Name: TABLE sys_re_template; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_re_template TO "mega_mn6CoEbT";


--
-- TOC entry 8966 (class 0 OID 0)
-- Dependencies: 357
-- Name: TABLE sys_re_when; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_re_when TO "mega_mn6CoEbT";


--
-- TOC entry 8967 (class 0 OID 0)
-- Dependencies: 358
-- Name: TABLE sys_re_workload; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_re_workload TO "mega_mn6CoEbT";


--
-- TOC entry 8968 (class 0 OID 0)
-- Dependencies: 359
-- Name: TABLE sys_re_worktask; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_re_worktask TO "mega_mn6CoEbT";


--
-- TOC entry 8969 (class 0 OID 0)
-- Dependencies: 361
-- Name: TABLE sys_role; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_role TO "mega_mn6CoEbT";


--
-- TOC entry 8970 (class 0 OID 0)
-- Dependencies: 362
-- Name: TABLE sys_role_dept; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_role_dept TO "mega_mn6CoEbT";


--
-- TOC entry 8971 (class 0 OID 0)
-- Dependencies: 363
-- Name: TABLE sys_role_menu; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_role_menu TO "mega_mn6CoEbT";


--
-- TOC entry 8972 (class 0 OID 0)
-- Dependencies: 360
-- Name: SEQUENCE sys_role_role_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.sys_role_role_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8973 (class 0 OID 0)
-- Dependencies: 365
-- Name: TABLE sys_ru_caseinstance; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_ru_caseinstance TO "mega_mn6CoEbT";


--
-- TOC entry 8974 (class 0 OID 0)
-- Dependencies: 364
-- Name: SEQUENCE sys_ru_caseinstance_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.sys_ru_caseinstance_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8975 (class 0 OID 0)
-- Dependencies: 367
-- Name: TABLE sys_sequence_id; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_sequence_id TO "mega_mn6CoEbT";


--
-- TOC entry 8976 (class 0 OID 0)
-- Dependencies: 366
-- Name: SEQUENCE sys_sequence_id_sequenceid_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.sys_sequence_id_sequenceid_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8977 (class 0 OID 0)
-- Dependencies: 369
-- Name: TABLE sys_task_form; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_task_form TO "mega_mn6CoEbT";


--
-- TOC entry 8978 (class 0 OID 0)
-- Dependencies: 368
-- Name: SEQUENCE sys_task_form_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.sys_task_form_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8980 (class 0 OID 0)
-- Dependencies: 371
-- Name: TABLE sys_user; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_user TO "mega_mn6CoEbT";


--
-- TOC entry 8981 (class 0 OID 0)
-- Dependencies: 372
-- Name: TABLE sys_user_post; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_user_post TO "mega_mn6CoEbT";


--
-- TOC entry 8982 (class 0 OID 0)
-- Dependencies: 373
-- Name: TABLE sys_user_role; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_user_role TO "mega_mn6CoEbT";


--
-- TOC entry 8983 (class 0 OID 0)
-- Dependencies: 370
-- Name: SEQUENCE sys_user_user_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.sys_user_user_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8985 (class 0 OID 0)
-- Dependencies: 631
-- Name: TABLE sys_webchat_token_tenant; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_webchat_token_tenant TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.sys_webchat_token_tenant TO "_tenant";


--
-- TOC entry 8986 (class 0 OID 0)
-- Dependencies: 449
-- Name: TABLE sys_webhook_path_index; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.sys_webhook_path_index TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.sys_webhook_path_index TO "_tenant";


--
-- TOC entry 8987 (class 0 OID 0)
-- Dependencies: 375
-- Name: TABLE table_table_industry; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.table_table_industry TO "mega_mn6CoEbT";


--
-- TOC entry 8988 (class 0 OID 0)
-- Dependencies: 374
-- Name: SEQUENCE table_table_industry_id_seq; Type: ACL; Schema: public; Owner: _tenant
--

GRANT ALL ON SEQUENCE public.table_table_industry_id_seq TO "mega_mn6CoEbT";


--
-- TOC entry 8989 (class 0 OID 0)
-- Dependencies: 430
-- Name: TABLE w_connection; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.w_connection TO "mega_mn6CoEbT";


--
-- TOC entry 8991 (class 0 OID 0)
-- Dependencies: 428
-- Name: TABLE w_credential; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.w_credential TO "mega_mn6CoEbT";


--
-- TOC entry 8992 (class 0 OID 0)
-- Dependencies: 431
-- Name: TABLE w_execution; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.w_execution TO "mega_mn6CoEbT";


--
-- TOC entry 8993 (class 0 OID 0)
-- Dependencies: 427
-- Name: TABLE w_node; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.w_node TO "mega_mn6CoEbT";


--
-- TOC entry 8994 (class 0 OID 0)
-- Dependencies: 429
-- Name: TABLE w_node_definition; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.w_node_definition TO luohd;


--
-- TOC entry 9001 (class 0 OID 0)
-- Dependencies: 590
-- Name: TABLE w_scheduled_task; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.w_scheduled_task TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.w_scheduled_task TO "_tenant";


--
-- TOC entry 9005 (class 0 OID 0)
-- Dependencies: 432
-- Name: TABLE w_wait_resume; Type: ACL; Schema: public; Owner: luohd
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.w_wait_resume TO "mega_mn6CoEbT";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.w_wait_resume TO "_tenant";


--
-- TOC entry 9010 (class 0 OID 0)
-- Dependencies: 426
-- Name: TABLE w_workflow; Type: ACL; Schema: public; Owner: _tenant
--

GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE public.w_workflow TO "mega_mn6CoEbT";


--
-- TOC entry 3697 (class 826 OID 363013)
-- Name: DEFAULT PRIVILEGES FOR SEQUENCES; Type: DEFAULT ACL; Schema: public; Owner: luohd
--

ALTER DEFAULT PRIVILEGES FOR ROLE luohd IN SCHEMA public GRANT ALL ON SEQUENCES TO "mega_mn6CoEbT";
ALTER DEFAULT PRIVILEGES FOR ROLE luohd IN SCHEMA public GRANT ALL ON SEQUENCES TO "_tenant";


--
-- TOC entry 3696 (class 826 OID 363012)
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: public; Owner: luohd
--

ALTER DEFAULT PRIVILEGES FOR ROLE luohd IN SCHEMA public GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLES TO "mega_mn6CoEbT";
ALTER DEFAULT PRIVILEGES FOR ROLE luohd IN SCHEMA public GRANT SELECT,INSERT,DELETE,UPDATE ON TABLES TO "_tenant";


-- Completed on 2026-10-19 20:44:37 CST

--
-- PostgreSQL database dump complete
--

