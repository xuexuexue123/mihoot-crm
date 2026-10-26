--
-- PostgreSQL database dump
--

-- Dumped from database version 14.18 (Homebrew)
-- Dumped by pg_dump version 17.0

-- Started on 2026-10-19 20:41:49 CST

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
-- TOC entry 6 (class 2615 OID 357044)
-- Name: public; Type: SCHEMA; Schema: -; Owner: luohd
--

-- *not* creating schema, since initdb creates it


ALTER SCHEMA public OWNER TO luohd;

--
-- TOC entry 4866 (class 0 OID 0)
-- Dependencies: 6
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: luohd
--

COMMENT ON SCHEMA public IS '';


--
-- TOC entry 2 (class 3079 OID 409285)
-- Name: vector; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS vector WITH SCHEMA public;


--
-- TOC entry 4868 (class 0 OID 0)
-- Dependencies: 2
-- Name: EXTENSION vector; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION vector IS 'vector data type and ivfflat and hnsw access methods';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 301 (class 1259 OID 369144)
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
-- TOC entry 300 (class 1259 OID 369143)
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
-- TOC entry 4869 (class 0 OID 0)
-- Dependencies: 300
-- Name: act_evt_log_log_nr__seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.act_evt_log_log_nr__seq OWNED BY public.act_evt_log.log_nr_;


--
-- TOC entry 261 (class 1259 OID 368717)
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
-- TOC entry 260 (class 1259 OID 368712)
-- Name: act_ge_property; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_ge_property (
    name_ character varying(64) NOT NULL,
    value_ character varying(300),
    rev_ integer
);


ALTER TABLE public.act_ge_property OWNER TO luohd;

--
-- TOC entry 305 (class 1259 OID 369378)
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
-- TOC entry 308 (class 1259 OID 369401)
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
-- TOC entry 307 (class 1259 OID 369394)
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
-- TOC entry 306 (class 1259 OID 369387)
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
-- TOC entry 280 (class 1259 OID 368878)
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
-- TOC entry 278 (class 1259 OID 368856)
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
-- TOC entry 304 (class 1259 OID 369367)
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
-- TOC entry 283 (class 1259 OID 368911)
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
-- TOC entry 285 (class 1259 OID 368921)
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
-- TOC entry 284 (class 1259 OID 368920)
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
-- TOC entry 4870 (class 0 OID 0)
-- Dependencies: 284
-- Name: act_hi_tsk_log_id__seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.act_hi_tsk_log_id__seq OWNED BY public.act_hi_tsk_log.id_;


--
-- TOC entry 287 (class 1259 OID 368948)
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
-- TOC entry 269 (class 1259 OID 368769)
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
-- TOC entry 270 (class 1259 OID 368776)
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
-- TOC entry 273 (class 1259 OID 368796)
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
-- TOC entry 271 (class 1259 OID 368783)
-- Name: act_id_membership; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_id_membership (
    user_id_ character varying(64) NOT NULL,
    group_id_ character varying(64) NOT NULL
);


ALTER TABLE public.act_id_membership OWNER TO luohd;

--
-- TOC entry 275 (class 1259 OID 368810)
-- Name: act_id_priv; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_id_priv (
    id_ character varying(64) NOT NULL,
    name_ character varying(255) NOT NULL
);


ALTER TABLE public.act_id_priv OWNER TO luohd;

--
-- TOC entry 276 (class 1259 OID 368815)
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
-- TOC entry 268 (class 1259 OID 368764)
-- Name: act_id_property; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.act_id_property (
    name_ character varying(64) NOT NULL,
    value_ character varying(300),
    rev_ integer
);


ALTER TABLE public.act_id_property OWNER TO luohd;

--
-- TOC entry 274 (class 1259 OID 368803)
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
-- TOC entry 272 (class 1259 OID 368788)
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
-- TOC entry 302 (class 1259 OID 369153)
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
-- TOC entry 296 (class 1259 OID 369110)
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
-- TOC entry 297 (class 1259 OID 369118)
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
-- TOC entry 299 (class 1259 OID 369134)
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
-- TOC entry 303 (class 1259 OID 369158)
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
-- TOC entry 291 (class 1259 OID 368983)
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
-- TOC entry 279 (class 1259 OID 368867)
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
-- TOC entry 281 (class 1259 OID 368889)
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
-- TOC entry 298 (class 1259 OID 369126)
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
-- TOC entry 293 (class 1259 OID 368999)
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
-- TOC entry 292 (class 1259 OID 368991)
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
-- TOC entry 277 (class 1259 OID 368844)
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
-- TOC entry 288 (class 1259 OID 368959)
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
-- TOC entry 290 (class 1259 OID 368975)
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
-- TOC entry 282 (class 1259 OID 368899)
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
-- TOC entry 289 (class 1259 OID 368967)
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
-- TOC entry 286 (class 1259 OID 368933)
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
-- TOC entry 310 (class 1259 OID 371559)
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
-- TOC entry 4871 (class 0 OID 0)
-- Dependencies: 310
-- Name: TABLE crm_data_table; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_data_table IS 'Data Table 元数据（类 Airtable）';


--
-- TOC entry 311 (class 1259 OID 371570)
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


ALTER TABLE public.crm_data_table_column OWNER TO luohd;

--
-- TOC entry 4872 (class 0 OID 0)
-- Dependencies: 311
-- Name: TABLE crm_data_table_column; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_data_table_column IS 'Data Table 列定义';


--
-- TOC entry 312 (class 1259 OID 371587)
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
-- TOC entry 4873 (class 0 OID 0)
-- Dependencies: 312
-- Name: TABLE crm_data_table_row; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_data_table_row IS 'Data Table 行数据（JSONB）';


--
-- TOC entry 315 (class 1259 OID 404498)
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
-- TOC entry 314 (class 1259 OID 404497)
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
-- TOC entry 4874 (class 0 OID 0)
-- Dependencies: 314
-- Name: crm_event_mp_outreach_log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.crm_event_mp_outreach_log_id_seq OWNED BY public.crm_event_mp_outreach_log.id;


--
-- TOC entry 317 (class 1259 OID 411612)
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
    del_flag character(1) DEFAULT '0'::bpchar NOT NULL
);


ALTER TABLE public.crm_tenant_enterprise OWNER TO luohd;

--
-- TOC entry 4875 (class 0 OID 0)
-- Dependencies: 317
-- Name: TABLE crm_tenant_enterprise; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.crm_tenant_enterprise IS '越南工业园区入驻企业';


--
-- TOC entry 4876 (class 0 OID 0)
-- Dependencies: 317
-- Name: COLUMN crm_tenant_enterprise.park_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_tenant_enterprise.park_id IS '逻辑外键 → crm_industrial_park.id';


--
-- TOC entry 4877 (class 0 OID 0)
-- Dependencies: 317
-- Name: COLUMN crm_tenant_enterprise.park_name_cn; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_tenant_enterprise.park_name_cn IS '冗余园区中文名，列表免联表';


--
-- TOC entry 4878 (class 0 OID 0)
-- Dependencies: 317
-- Name: COLUMN crm_tenant_enterprise.enterprise_type; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_tenant_enterprise.enterprise_type IS 'FDI / WFOE / JV / Private / SOE / 分支机构等';


--
-- TOC entry 4879 (class 0 OID 0)
-- Dependencies: 317
-- Name: COLUMN crm_tenant_enterprise.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_tenant_enterprise.status IS '运营中 | 建设中 | 签约拟建';


--
-- TOC entry 4880 (class 0 OID 0)
-- Dependencies: 317
-- Name: COLUMN crm_tenant_enterprise.del_flag; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.crm_tenant_enterprise.del_flag IS '0 active, 1 soft-deleted';


--
-- TOC entry 267 (class 1259 OID 368756)
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
-- TOC entry 263 (class 1259 OID 368729)
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
-- TOC entry 262 (class 1259 OID 368724)
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
-- TOC entry 266 (class 1259 OID 368748)
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
-- TOC entry 264 (class 1259 OID 368734)
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
-- TOC entry 265 (class 1259 OID 368741)
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
-- TOC entry 294 (class 1259 OID 369088)
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
-- TOC entry 295 (class 1259 OID 369096)
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
-- TOC entry 246 (class 1259 OID 357317)
-- Name: gen_table; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.gen_table (
    table_id bigint NOT NULL,
    table_name character varying(200) DEFAULT ''::character varying,
    table_comment character varying(500) DEFAULT ''::character varying,
    sub_table_name character varying(64) DEFAULT NULL::character varying,
    sub_table_fk_name character varying(64) DEFAULT NULL::character varying,
    class_name character varying(100) DEFAULT ''::character varying,
    tpl_category character varying(200) DEFAULT 'crud'::character varying,
    package_name character varying(100),
    module_name character varying(30),
    business_name character varying(30),
    function_name character varying(50),
    function_author character varying(50),
    gen_type character(1) DEFAULT '0'::bpchar,
    gen_path character varying(200) DEFAULT '/'::character varying,
    options character varying(1000),
    create_by character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone,
    update_by character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone,
    remark character varying(500) DEFAULT NULL::character varying
);


ALTER TABLE public.gen_table OWNER TO luohd;

--
-- TOC entry 248 (class 1259 OID 357336)
-- Name: gen_table_column; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.gen_table_column (
    column_id bigint NOT NULL,
    table_id character varying(64),
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
    dict_type character varying(200) DEFAULT ''::character varying,
    sort integer,
    create_by character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone,
    update_by character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone
);


ALTER TABLE public.gen_table_column OWNER TO luohd;

--
-- TOC entry 247 (class 1259 OID 357335)
-- Name: gen_table_column_column_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
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
-- TOC entry 245 (class 1259 OID 357316)
-- Name: gen_table_table_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
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
-- TOC entry 212 (class 1259 OID 357054)
-- Name: master_registration; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.master_registration (
    regist_id bigint NOT NULL,
    contact_name character varying(100) DEFAULT ''::character varying,
    company_name character varying(100) DEFAULT ''::character varying,
    mobile character varying(100) DEFAULT ''::character varying,
    email character varying(100) DEFAULT ''::character varying,
    start_date date,
    end_date date,
    oper_ip character varying(128) DEFAULT ''::character varying,
    oper_location character varying(255) DEFAULT ''::character varying,
    plan character varying(255) DEFAULT ''::character varying,
    status integer DEFAULT 0,
    remark character varying(500),
    oper_time timestamp without time zone,
    create_by character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone,
    update_by character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone
);


ALTER TABLE public.master_registration OWNER TO luohd;

--
-- TOC entry 4881 (class 0 OID 0)
-- Dependencies: 212
-- Name: TABLE master_registration; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.master_registration IS '主注册表';


--
-- TOC entry 211 (class 1259 OID 357053)
-- Name: master_registration_regist_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.master_registration_regist_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.master_registration_regist_id_seq OWNER TO luohd;

--
-- TOC entry 4882 (class 0 OID 0)
-- Dependencies: 211
-- Name: master_registration_regist_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.master_registration_regist_id_seq OWNED BY public.master_registration.regist_id;


--
-- TOC entry 214 (class 1259 OID 357073)
-- Name: master_tenant; Type: TABLE; Schema: public; Owner: luohd
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
    expiration_date timestamp without time zone,
    website_slug character varying(63),
    website_status character varying(20) DEFAULT 'active'::character varying NOT NULL,
    website_created_at timestamp with time zone DEFAULT now() NOT NULL,
    company_name character varying(100)
);


ALTER TABLE public.master_tenant OWNER TO luohd;

--
-- TOC entry 4883 (class 0 OID 0)
-- Dependencies: 214
-- Name: TABLE master_tenant; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.master_tenant IS '租户数据源注册';


--
-- TOC entry 4884 (class 0 OID 0)
-- Dependencies: 214
-- Name: COLUMN master_tenant.status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.master_tenant.status IS '状态(1正常 2停止)';


--
-- TOC entry 4885 (class 0 OID 0)
-- Dependencies: 214
-- Name: COLUMN master_tenant.website_slug; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.master_tenant.website_slug IS '公开子域名 slug，全局唯一';


--
-- TOC entry 4886 (class 0 OID 0)
-- Dependencies: 214
-- Name: COLUMN master_tenant.website_status; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.master_tenant.website_status IS 'active / suspended';


--
-- TOC entry 4887 (class 0 OID 0)
-- Dependencies: 214
-- Name: COLUMN master_tenant.website_created_at; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.master_tenant.website_created_at IS '子域记录创建时间';


--
-- TOC entry 4888 (class 0 OID 0)
-- Dependencies: 214
-- Name: COLUMN master_tenant.company_name; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.master_tenant.company_name IS '注册公司名（公开 API 展示）';


--
-- TOC entry 213 (class 1259 OID 357072)
-- Name: master_tenant_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
--

CREATE SEQUENCE public.master_tenant_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.master_tenant_id_seq OWNER TO luohd;

--
-- TOC entry 4889 (class 0 OID 0)
-- Dependencies: 213
-- Name: master_tenant_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luohd
--

ALTER SEQUENCE public.master_tenant_id_seq OWNED BY public.master_tenant.id;


--
-- TOC entry 253 (class 1259 OID 357390)
-- Name: qrtz_blob_triggers; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.qrtz_blob_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    blob_data bytea
);


ALTER TABLE public.qrtz_blob_triggers OWNER TO luohd;

--
-- TOC entry 254 (class 1259 OID 357402)
-- Name: qrtz_calendars; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.qrtz_calendars (
    sched_name character varying(120) NOT NULL,
    calendar_name character varying(200) NOT NULL,
    calendar bytea NOT NULL
);


ALTER TABLE public.qrtz_calendars OWNER TO luohd;

--
-- TOC entry 252 (class 1259 OID 357378)
-- Name: qrtz_cron_triggers; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.qrtz_cron_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    cron_expression character varying(200) NOT NULL,
    time_zone_id character varying(80)
);


ALTER TABLE public.qrtz_cron_triggers OWNER TO luohd;

--
-- TOC entry 256 (class 1259 OID 357414)
-- Name: qrtz_fired_triggers; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.qrtz_fired_triggers (
    sched_name character varying(120) NOT NULL,
    entry_id character varying(95) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    instance_name character varying(200) NOT NULL,
    fired_time bigint NOT NULL,
    sched_time bigint NOT NULL,
    priority integer NOT NULL,
    state character varying(16) NOT NULL,
    job_name character varying(200),
    job_group character varying(200),
    is_nonconcurrent character varying(1),
    requests_recovery character varying(1)
);


ALTER TABLE public.qrtz_fired_triggers OWNER TO luohd;

--
-- TOC entry 249 (class 1259 OID 357347)
-- Name: qrtz_job_details; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.qrtz_job_details (
    sched_name character varying(120) NOT NULL,
    job_name character varying(200) NOT NULL,
    job_group character varying(200) NOT NULL,
    description character varying(250),
    job_class_name character varying(250) NOT NULL,
    is_durable character varying(1) NOT NULL,
    is_nonconcurrent character varying(1) NOT NULL,
    is_update_data character varying(1) NOT NULL,
    requests_recovery character varying(1) NOT NULL,
    job_data bytea
);


ALTER TABLE public.qrtz_job_details OWNER TO luohd;

--
-- TOC entry 258 (class 1259 OID 357426)
-- Name: qrtz_locks; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.qrtz_locks (
    sched_name character varying(120) NOT NULL,
    lock_name character varying(40) NOT NULL
);


ALTER TABLE public.qrtz_locks OWNER TO luohd;

--
-- TOC entry 255 (class 1259 OID 357409)
-- Name: qrtz_paused_trigger_grps; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.qrtz_paused_trigger_grps (
    sched_name character varying(120) NOT NULL,
    trigger_group character varying(200) NOT NULL
);


ALTER TABLE public.qrtz_paused_trigger_grps OWNER TO luohd;

--
-- TOC entry 257 (class 1259 OID 357421)
-- Name: qrtz_scheduler_state; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.qrtz_scheduler_state (
    sched_name character varying(120) NOT NULL,
    instance_name character varying(200) NOT NULL,
    last_checkin_time bigint NOT NULL,
    checkin_interval bigint NOT NULL
);


ALTER TABLE public.qrtz_scheduler_state OWNER TO luohd;

--
-- TOC entry 251 (class 1259 OID 357366)
-- Name: qrtz_simple_triggers; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.qrtz_simple_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    repeat_count bigint NOT NULL,
    repeat_interval bigint NOT NULL,
    times_triggered bigint NOT NULL
);


ALTER TABLE public.qrtz_simple_triggers OWNER TO luohd;

--
-- TOC entry 259 (class 1259 OID 357431)
-- Name: qrtz_simprop_triggers; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.qrtz_simprop_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    str_prop_1 character varying(512),
    str_prop_2 character varying(512),
    str_prop_3 character varying(512),
    int_prop_1 integer,
    int_prop_2 integer,
    long_prop_1 bigint,
    long_prop_2 bigint,
    dec_prop_1 numeric(13,4),
    dec_prop_2 numeric(13,4),
    bool_prop_1 character varying(1),
    bool_prop_2 character varying(1)
);


ALTER TABLE public.qrtz_simprop_triggers OWNER TO luohd;

--
-- TOC entry 250 (class 1259 OID 357354)
-- Name: qrtz_triggers; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.qrtz_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    job_name character varying(200) NOT NULL,
    job_group character varying(200) NOT NULL,
    description character varying(250),
    next_fire_time bigint,
    prev_fire_time bigint,
    priority integer,
    trigger_state character varying(16) NOT NULL,
    trigger_type character varying(8) NOT NULL,
    start_time bigint NOT NULL,
    end_time bigint,
    calendar_name character varying(200),
    misfire_instr smallint,
    job_data bytea
);


ALTER TABLE public.qrtz_triggers OWNER TO luohd;

--
-- TOC entry 236 (class 1259 OID 357248)
-- Name: sys_config; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.sys_config (
    config_id integer NOT NULL,
    config_name character varying(100) DEFAULT ''::character varying,
    config_key character varying(100) DEFAULT ''::character varying,
    config_value character varying(500) DEFAULT ''::character varying,
    config_type character(1) DEFAULT 'N'::bpchar,
    create_by character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone,
    update_by character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone,
    remark character varying(500) DEFAULT NULL::character varying
);


ALTER TABLE public.sys_config OWNER TO luohd;

--
-- TOC entry 235 (class 1259 OID 357247)
-- Name: sys_config_config_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
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
-- TOC entry 216 (class 1259 OID 357085)
-- Name: sys_dept; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.sys_dept (
    dept_id bigint NOT NULL,
    parent_id bigint DEFAULT 0,
    ancestors character varying(50) DEFAULT ''::character varying,
    dept_name character varying(30) DEFAULT ''::character varying,
    order_num integer DEFAULT 0,
    leader character varying(20) DEFAULT NULL::character varying,
    phone character varying(11) DEFAULT NULL::character varying,
    email character varying(50) DEFAULT NULL::character varying,
    status character(1) DEFAULT '0'::bpchar,
    del_flag character(1) DEFAULT '0'::bpchar,
    create_by character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone,
    update_by character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone
);


ALTER TABLE public.sys_dept OWNER TO luohd;

--
-- TOC entry 215 (class 1259 OID 357084)
-- Name: sys_dept_dept_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
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
-- TOC entry 234 (class 1259 OID 357229)
-- Name: sys_dict_data; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.sys_dict_data (
    dict_code bigint NOT NULL,
    dict_sort integer DEFAULT 0,
    dict_label character varying(100) DEFAULT ''::character varying,
    dict_value character varying(100) DEFAULT ''::character varying,
    dict_type character varying(100) DEFAULT ''::character varying,
    css_class character varying(100) DEFAULT NULL::character varying,
    list_class character varying(100) DEFAULT NULL::character varying,
    is_default character(1) DEFAULT 'N'::bpchar,
    status character(1) DEFAULT '0'::bpchar,
    create_by character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone,
    update_by character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone,
    remark character varying(500) DEFAULT NULL::character varying
);


ALTER TABLE public.sys_dict_data OWNER TO luohd;

--
-- TOC entry 233 (class 1259 OID 357228)
-- Name: sys_dict_data_dict_code_seq; Type: SEQUENCE; Schema: public; Owner: luohd
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
-- TOC entry 232 (class 1259 OID 357213)
-- Name: sys_dict_type; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.sys_dict_type (
    dict_id bigint NOT NULL,
    dict_name character varying(100) DEFAULT ''::character varying,
    dict_type character varying(100) DEFAULT ''::character varying,
    status character(1) DEFAULT '0'::bpchar,
    create_by character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone,
    update_by character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone,
    remark character varying(500) DEFAULT NULL::character varying
);


ALTER TABLE public.sys_dict_type OWNER TO luohd;

--
-- TOC entry 231 (class 1259 OID 357212)
-- Name: sys_dict_type_dict_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
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
-- TOC entry 240 (class 1259 OID 357278)
-- Name: sys_job; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.sys_job (
    job_id bigint NOT NULL,
    job_name character varying(64) DEFAULT ''::character varying NOT NULL,
    job_group character varying(64) DEFAULT 'DEFAULT'::character varying NOT NULL,
    invoke_target character varying(500) NOT NULL,
    cron_expression character varying(255) DEFAULT ''::character varying,
    misfire_policy character varying(20) DEFAULT '3'::character varying,
    concurrent character(1) DEFAULT '1'::bpchar,
    status character(1) DEFAULT '0'::bpchar,
    create_by character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone,
    update_by character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone,
    remark character varying(500) DEFAULT ''::character varying
);


ALTER TABLE public.sys_job OWNER TO luohd;

--
-- TOC entry 239 (class 1259 OID 357277)
-- Name: sys_job_job_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
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
-- TOC entry 242 (class 1259 OID 357295)
-- Name: sys_job_log; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.sys_job_log (
    job_log_id bigint NOT NULL,
    job_name character varying(64) NOT NULL,
    job_group character varying(64) NOT NULL,
    invoke_target character varying(500) NOT NULL,
    job_message character varying(500),
    status character(1) DEFAULT '0'::bpchar,
    exception_info character varying(2000) DEFAULT ''::character varying,
    create_time timestamp without time zone
);


ALTER TABLE public.sys_job_log OWNER TO luohd;

--
-- TOC entry 241 (class 1259 OID 357294)
-- Name: sys_job_log_job_log_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
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
-- TOC entry 238 (class 1259 OID 357263)
-- Name: sys_logininfor; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.sys_logininfor (
    info_id bigint NOT NULL,
    user_name character varying(50) DEFAULT ''::character varying,
    ipaddr character varying(128) DEFAULT ''::character varying,
    login_location character varying(255) DEFAULT ''::character varying,
    browser character varying(50) DEFAULT ''::character varying,
    os character varying(50) DEFAULT ''::character varying,
    status character(1) DEFAULT '0'::bpchar,
    msg character varying(255) DEFAULT ''::character varying,
    login_time timestamp without time zone
);


ALTER TABLE public.sys_logininfor OWNER TO luohd;

--
-- TOC entry 237 (class 1259 OID 357262)
-- Name: sys_logininfor_info_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
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
-- TOC entry 224 (class 1259 OID 357148)
-- Name: sys_menu; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.sys_menu (
    menu_id bigint NOT NULL,
    menu_name character varying(50) NOT NULL,
    parent_id bigint DEFAULT 0,
    order_num integer DEFAULT 0,
    path character varying(200) DEFAULT ''::character varying,
    component character varying(255) DEFAULT NULL::character varying,
    query character varying(255) DEFAULT NULL::character varying,
    is_frame integer DEFAULT 1,
    is_cache integer DEFAULT 0,
    menu_type character(1) DEFAULT ''::bpchar,
    visible character(1) DEFAULT 0,
    status character(1) DEFAULT 0,
    perms character varying(100) DEFAULT NULL::character varying,
    icon character varying(100) DEFAULT '#'::character varying,
    create_by character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone,
    update_by character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone,
    remark character varying(500) DEFAULT ''::character varying
);


ALTER TABLE public.sys_menu OWNER TO luohd;

--
-- TOC entry 223 (class 1259 OID 357147)
-- Name: sys_menu_menu_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
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
-- TOC entry 318 (class 1259 OID 413131)
-- Name: sys_messenger_page_tenant; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.sys_messenger_page_tenant (
    page_id character varying(64) NOT NULL,
    tenant character varying(64) NOT NULL
);


ALTER TABLE public.sys_messenger_page_tenant OWNER TO luohd;

--
-- TOC entry 4890 (class 0 OID 0)
-- Dependencies: 318
-- Name: TABLE sys_messenger_page_tenant; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.sys_messenger_page_tenant IS 'Messenger page_id → tenant；Webhook 路由。写入：POST /crm/messenger/config';


--
-- TOC entry 244 (class 1259 OID 357305)
-- Name: sys_notice; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.sys_notice (
    notice_id integer NOT NULL,
    notice_title character varying(50) NOT NULL,
    notice_type character(1) NOT NULL,
    notice_content bytea,
    status character(1) DEFAULT '0'::bpchar,
    create_by character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone,
    update_by character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone,
    remark character varying(255) DEFAULT NULL::character varying
);


ALTER TABLE public.sys_notice OWNER TO luohd;

--
-- TOC entry 243 (class 1259 OID 357304)
-- Name: sys_notice_notice_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
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
-- TOC entry 230 (class 1259 OID 357191)
-- Name: sys_oper_log; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.sys_oper_log (
    oper_id bigint NOT NULL,
    title character varying(50) DEFAULT ''::character varying,
    business_type integer DEFAULT 0,
    method character varying(100) DEFAULT ''::character varying,
    request_method character varying(10) DEFAULT ''::character varying,
    operator_type integer DEFAULT 0,
    oper_name character varying(50) DEFAULT ''::character varying,
    dept_name character varying(50) DEFAULT ''::character varying,
    oper_url character varying(255) DEFAULT ''::character varying,
    oper_ip character varying(128) DEFAULT ''::character varying,
    oper_location character varying(255) DEFAULT ''::character varying,
    oper_param character varying(2000) DEFAULT ''::character varying,
    json_result character varying(2000) DEFAULT ''::character varying,
    status integer DEFAULT 0,
    error_msg character varying(2000) DEFAULT ''::character varying,
    oper_time timestamp without time zone
);


ALTER TABLE public.sys_oper_log OWNER TO luohd;

--
-- TOC entry 229 (class 1259 OID 357190)
-- Name: sys_oper_log_oper_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
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
-- TOC entry 220 (class 1259 OID 357122)
-- Name: sys_post; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.sys_post (
    post_id bigint NOT NULL,
    post_code character varying(64) NOT NULL,
    post_name character varying(50) NOT NULL,
    post_sort integer NOT NULL,
    status character(1) NOT NULL,
    create_by character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone,
    update_by character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone,
    remark character varying(500) DEFAULT NULL::character varying
);


ALTER TABLE public.sys_post OWNER TO luohd;

--
-- TOC entry 219 (class 1259 OID 357121)
-- Name: sys_post_post_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
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
-- TOC entry 222 (class 1259 OID 357133)
-- Name: sys_role; Type: TABLE; Schema: public; Owner: luohd
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
    create_by character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone,
    update_by character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone,
    remark character varying(500) DEFAULT NULL::character varying
);


ALTER TABLE public.sys_role OWNER TO luohd;

--
-- TOC entry 227 (class 1259 OID 357180)
-- Name: sys_role_dept; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.sys_role_dept (
    role_id bigint NOT NULL,
    dept_id bigint NOT NULL
);


ALTER TABLE public.sys_role_dept OWNER TO luohd;

--
-- TOC entry 226 (class 1259 OID 357175)
-- Name: sys_role_menu; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.sys_role_menu (
    role_id bigint NOT NULL,
    menu_id bigint NOT NULL
);


ALTER TABLE public.sys_role_menu OWNER TO luohd;

--
-- TOC entry 221 (class 1259 OID 357132)
-- Name: sys_role_role_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
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
-- TOC entry 218 (class 1259 OID 357102)
-- Name: sys_user; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.sys_user (
    user_id bigint NOT NULL,
    dept_id bigint,
    user_name character varying(30) NOT NULL,
    nick_name character varying(30) NOT NULL,
    user_type character varying(2) DEFAULT '00'::character varying,
    email character varying(50) DEFAULT ''::character varying,
    phonenumber character varying(11) DEFAULT ''::character varying,
    sex character(1) DEFAULT '0'::bpchar,
    avatar character varying(100) DEFAULT ''::character varying,
    password character varying(100) DEFAULT ''::character varying,
    status character(1) DEFAULT '0'::bpchar,
    del_flag character(1) DEFAULT '0'::bpchar,
    login_ip character varying(128) DEFAULT ''::character varying,
    login_date timestamp without time zone,
    create_by character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone,
    update_by character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone,
    remark character varying(500) DEFAULT NULL::character varying
);


ALTER TABLE public.sys_user OWNER TO luohd;

--
-- TOC entry 228 (class 1259 OID 357185)
-- Name: sys_user_post; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.sys_user_post (
    user_id bigint NOT NULL,
    post_id bigint NOT NULL
);


ALTER TABLE public.sys_user_post OWNER TO luohd;

--
-- TOC entry 225 (class 1259 OID 357170)
-- Name: sys_user_role; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.sys_user_role (
    user_id bigint NOT NULL,
    role_id bigint NOT NULL
);


ALTER TABLE public.sys_user_role OWNER TO luohd;

--
-- TOC entry 217 (class 1259 OID 357101)
-- Name: sys_user_user_id_seq; Type: SEQUENCE; Schema: public; Owner: luohd
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
-- TOC entry 309 (class 1259 OID 371361)
-- Name: sys_wait_resume_tenant; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.sys_wait_resume_tenant (
    resume_key text NOT NULL,
    tenant character varying(64) NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.sys_wait_resume_tenant OWNER TO luohd;

--
-- TOC entry 4891 (class 0 OID 0)
-- Dependencies: 309
-- Name: TABLE sys_wait_resume_tenant; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.sys_wait_resume_tenant IS 'Wait resume key 与租户映射（公开回调路由）';


--
-- TOC entry 4892 (class 0 OID 0)
-- Dependencies: 309
-- Name: COLUMN sys_wait_resume_tenant.resume_key; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.sys_wait_resume_tenant.resume_key IS 'w_wait_resume.resume_key，全局唯一';


--
-- TOC entry 4893 (class 0 OID 0)
-- Dependencies: 309
-- Name: COLUMN sys_wait_resume_tenant.tenant; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.sys_wait_resume_tenant.tenant IS '租户标识，对应 master_tenant.tenant';


--
-- TOC entry 319 (class 1259 OID 413279)
-- Name: sys_webchat_token_tenant; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.sys_webchat_token_tenant (
    website_token character varying(64) NOT NULL,
    tenant character varying(64) NOT NULL
);


ALTER TABLE public.sys_webchat_token_tenant OWNER TO luohd;

--
-- TOC entry 4894 (class 0 OID 0)
-- Dependencies: 319
-- Name: TABLE sys_webchat_token_tenant; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.sys_webchat_token_tenant IS 'Web Chat website_token → tenant；公开 API 路由。一租户一行。';


--
-- TOC entry 313 (class 1259 OID 404287)
-- Name: sys_wechat_mp_tenant; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.sys_wechat_mp_tenant (
    app_id character varying(64) NOT NULL,
    tenant character varying(64) NOT NULL
);


ALTER TABLE public.sys_wechat_mp_tenant OWNER TO luohd;

--
-- TOC entry 4895 (class 0 OID 0)
-- Dependencies: 313
-- Name: TABLE sys_wechat_mp_tenant; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.sys_wechat_mp_tenant IS '微信公众号 appId -> tenant 映射，用于回调路由';


--
-- TOC entry 4896 (class 0 OID 0)
-- Dependencies: 313
-- Name: COLUMN sys_wechat_mp_tenant.app_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.sys_wechat_mp_tenant.app_id IS '微信公众号 AppID';


--
-- TOC entry 4897 (class 0 OID 0)
-- Dependencies: 313
-- Name: COLUMN sys_wechat_mp_tenant.tenant; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.sys_wechat_mp_tenant.tenant IS '租户标识，对应 master_tenant.tenant';


--
-- TOC entry 210 (class 1259 OID 357046)
-- Name: sys_wecom_corp_tenant; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.sys_wecom_corp_tenant (
    corp_id character varying(64) NOT NULL,
    tenant character varying(64) NOT NULL
);


ALTER TABLE public.sys_wecom_corp_tenant OWNER TO luohd;

--
-- TOC entry 4898 (class 0 OID 0)
-- Dependencies: 210
-- Name: TABLE sys_wecom_corp_tenant; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.sys_wecom_corp_tenant IS '企业微信 Corp 与租户映射';


--
-- TOC entry 4899 (class 0 OID 0)
-- Dependencies: 210
-- Name: COLUMN sys_wecom_corp_tenant.corp_id; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.sys_wecom_corp_tenant.corp_id IS '企业微信 CorpID';


--
-- TOC entry 4900 (class 0 OID 0)
-- Dependencies: 210
-- Name: COLUMN sys_wecom_corp_tenant.tenant; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON COLUMN public.sys_wecom_corp_tenant.tenant IS '租户标识，对应 master_tenant.tenant';


--
-- TOC entry 316 (class 1259 OID 409045)
-- Name: sys_whatsapp_phone_tenant; Type: TABLE; Schema: public; Owner: luohd
--

CREATE TABLE public.sys_whatsapp_phone_tenant (
    phone_number_id character varying(64) NOT NULL,
    tenant character varying(64) NOT NULL
);


ALTER TABLE public.sys_whatsapp_phone_tenant OWNER TO luohd;

--
-- TOC entry 4901 (class 0 OID 0)
-- Dependencies: 316
-- Name: TABLE sys_whatsapp_phone_tenant; Type: COMMENT; Schema: public; Owner: luohd
--

COMMENT ON TABLE public.sys_whatsapp_phone_tenant IS 'WhatsApp phone_number_id 与租户映射，用于 Webhook 回调路由。写入时机：POST /crm/whatsapp/config 保存时同步。';


--
-- TOC entry 4303 (class 2604 OID 369147)
-- Name: act_evt_log log_nr_; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_evt_log ALTER COLUMN log_nr_ SET DEFAULT nextval('public.act_evt_log_log_nr__seq'::regclass);


--
-- TOC entry 4287 (class 2604 OID 368924)
-- Name: act_hi_tsk_log id_; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_tsk_log ALTER COLUMN id_ SET DEFAULT nextval('public.act_hi_tsk_log_id__seq'::regclass);


--
-- TOC entry 4320 (class 2604 OID 404501)
-- Name: crm_event_mp_outreach_log id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_event_mp_outreach_log ALTER COLUMN id SET DEFAULT nextval('public.crm_event_mp_outreach_log_id_seq'::regclass);


--
-- TOC entry 4144 (class 2604 OID 357057)
-- Name: master_registration regist_id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.master_registration ALTER COLUMN regist_id SET DEFAULT nextval('public.master_registration_regist_id_seq'::regclass);


--
-- TOC entry 4155 (class 2604 OID 357076)
-- Name: master_tenant id; Type: DEFAULT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.master_tenant ALTER COLUMN id SET DEFAULT nextval('public.master_tenant_id_seq'::regclass);


--
-- TOC entry 4593 (class 2606 OID 369152)
-- Name: act_evt_log act_evt_log_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_evt_log
    ADD CONSTRAINT act_evt_log_pkey PRIMARY KEY (log_nr_);


--
-- TOC entry 4406 (class 2606 OID 368723)
-- Name: act_ge_bytearray act_ge_bytearray_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ge_bytearray
    ADD CONSTRAINT act_ge_bytearray_pkey PRIMARY KEY (id_);


--
-- TOC entry 4404 (class 2606 OID 368716)
-- Name: act_ge_property act_ge_property_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ge_property
    ADD CONSTRAINT act_ge_property_pkey PRIMARY KEY (name_);


--
-- TOC entry 4616 (class 2606 OID 369386)
-- Name: act_hi_actinst act_hi_actinst_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_actinst
    ADD CONSTRAINT act_hi_actinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 4631 (class 2606 OID 369407)
-- Name: act_hi_attachment act_hi_attachment_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_attachment
    ADD CONSTRAINT act_hi_attachment_pkey PRIMARY KEY (id_);


--
-- TOC entry 4629 (class 2606 OID 369400)
-- Name: act_hi_comment act_hi_comment_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_comment
    ADD CONSTRAINT act_hi_comment_pkey PRIMARY KEY (id_);


--
-- TOC entry 4622 (class 2606 OID 369393)
-- Name: act_hi_detail act_hi_detail_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_detail
    ADD CONSTRAINT act_hi_detail_pkey PRIMARY KEY (id_);


--
-- TOC entry 4470 (class 2606 OID 368884)
-- Name: act_hi_entitylink act_hi_entitylink_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_entitylink
    ADD CONSTRAINT act_hi_entitylink_pkey PRIMARY KEY (id_);


--
-- TOC entry 4456 (class 2606 OID 368862)
-- Name: act_hi_identitylink act_hi_identitylink_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_identitylink
    ADD CONSTRAINT act_hi_identitylink_pkey PRIMARY KEY (id_);


--
-- TOC entry 4610 (class 2606 OID 369375)
-- Name: act_hi_procinst act_hi_procinst_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_procinst
    ADD CONSTRAINT act_hi_procinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 4612 (class 2606 OID 369377)
-- Name: act_hi_procinst act_hi_procinst_proc_inst_id__key; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_procinst
    ADD CONSTRAINT act_hi_procinst_proc_inst_id__key UNIQUE (proc_inst_id_);


--
-- TOC entry 4489 (class 2606 OID 368919)
-- Name: act_hi_taskinst act_hi_taskinst_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_taskinst
    ADD CONSTRAINT act_hi_taskinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 4495 (class 2606 OID 368929)
-- Name: act_hi_tsk_log act_hi_tsk_log_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_tsk_log
    ADD CONSTRAINT act_hi_tsk_log_pkey PRIMARY KEY (id_);


--
-- TOC entry 4505 (class 2606 OID 368955)
-- Name: act_hi_varinst act_hi_varinst_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_hi_varinst
    ADD CONSTRAINT act_hi_varinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 4423 (class 2606 OID 368775)
-- Name: act_id_bytearray act_id_bytearray_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_bytearray
    ADD CONSTRAINT act_id_bytearray_pkey PRIMARY KEY (id_);


--
-- TOC entry 4425 (class 2606 OID 368782)
-- Name: act_id_group act_id_group_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_group
    ADD CONSTRAINT act_id_group_pkey PRIMARY KEY (id_);


--
-- TOC entry 4433 (class 2606 OID 368802)
-- Name: act_id_info act_id_info_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_info
    ADD CONSTRAINT act_id_info_pkey PRIMARY KEY (id_);


--
-- TOC entry 4427 (class 2606 OID 368787)
-- Name: act_id_membership act_id_membership_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_membership
    ADD CONSTRAINT act_id_membership_pkey PRIMARY KEY (user_id_, group_id_);


--
-- TOC entry 4441 (class 2606 OID 368821)
-- Name: act_id_priv_mapping act_id_priv_mapping_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_priv_mapping
    ADD CONSTRAINT act_id_priv_mapping_pkey PRIMARY KEY (id_);


--
-- TOC entry 4437 (class 2606 OID 368814)
-- Name: act_id_priv act_id_priv_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_priv
    ADD CONSTRAINT act_id_priv_pkey PRIMARY KEY (id_);


--
-- TOC entry 4421 (class 2606 OID 368768)
-- Name: act_id_property act_id_property_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_property
    ADD CONSTRAINT act_id_property_pkey PRIMARY KEY (name_);


--
-- TOC entry 4435 (class 2606 OID 368809)
-- Name: act_id_token act_id_token_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_token
    ADD CONSTRAINT act_id_token_pkey PRIMARY KEY (id_);


--
-- TOC entry 4431 (class 2606 OID 368795)
-- Name: act_id_user act_id_user_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_user
    ADD CONSTRAINT act_id_user_pkey PRIMARY KEY (id_);


--
-- TOC entry 4597 (class 2606 OID 369157)
-- Name: act_procdef_info act_procdef_info_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_procdef_info
    ADD CONSTRAINT act_procdef_info_pkey PRIMARY KEY (id_);


--
-- TOC entry 4573 (class 2606 OID 369117)
-- Name: act_re_deployment act_re_deployment_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_re_deployment
    ADD CONSTRAINT act_re_deployment_pkey PRIMARY KEY (id_);


--
-- TOC entry 4578 (class 2606 OID 369125)
-- Name: act_re_model act_re_model_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_re_model
    ADD CONSTRAINT act_re_model_pkey PRIMARY KEY (id_);


--
-- TOC entry 4589 (class 2606 OID 369142)
-- Name: act_re_procdef act_re_procdef_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_re_procdef
    ADD CONSTRAINT act_re_procdef_pkey PRIMARY KEY (id_);


--
-- TOC entry 4608 (class 2606 OID 369166)
-- Name: act_ru_actinst act_ru_actinst_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_actinst
    ADD CONSTRAINT act_ru_actinst_pkey PRIMARY KEY (id_);


--
-- TOC entry 4556 (class 2606 OID 368990)
-- Name: act_ru_deadletter_job act_ru_deadletter_job_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_deadletter_job
    ADD CONSTRAINT act_ru_deadletter_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 4468 (class 2606 OID 368873)
-- Name: act_ru_entitylink act_ru_entitylink_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_entitylink
    ADD CONSTRAINT act_ru_entitylink_pkey PRIMARY KEY (id_);


--
-- TOC entry 4478 (class 2606 OID 368896)
-- Name: act_ru_event_subscr act_ru_event_subscr_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_event_subscr
    ADD CONSTRAINT act_ru_event_subscr_pkey PRIMARY KEY (id_);


--
-- TOC entry 4587 (class 2606 OID 369133)
-- Name: act_ru_execution act_ru_execution_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_execution
    ADD CONSTRAINT act_ru_execution_pkey PRIMARY KEY (id_);


--
-- TOC entry 4566 (class 2606 OID 369006)
-- Name: act_ru_external_job act_ru_external_job_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_external_job
    ADD CONSTRAINT act_ru_external_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 4558 (class 2606 OID 368998)
-- Name: act_ru_history_job act_ru_history_job_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_history_job
    ADD CONSTRAINT act_ru_history_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 4454 (class 2606 OID 368850)
-- Name: act_ru_identitylink act_ru_identitylink_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_identitylink
    ADD CONSTRAINT act_ru_identitylink_pkey PRIMARY KEY (id_);


--
-- TOC entry 4522 (class 2606 OID 368966)
-- Name: act_ru_job act_ru_job_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_job
    ADD CONSTRAINT act_ru_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 4545 (class 2606 OID 368982)
-- Name: act_ru_suspended_job act_ru_suspended_job_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_suspended_job
    ADD CONSTRAINT act_ru_suspended_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 4487 (class 2606 OID 368906)
-- Name: act_ru_task act_ru_task_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_task
    ADD CONSTRAINT act_ru_task_pkey PRIMARY KEY (id_);


--
-- TOC entry 4534 (class 2606 OID 368974)
-- Name: act_ru_timer_job act_ru_timer_job_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_timer_job
    ADD CONSTRAINT act_ru_timer_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 4503 (class 2606 OID 368939)
-- Name: act_ru_variable act_ru_variable_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_variable
    ADD CONSTRAINT act_ru_variable_pkey PRIMARY KEY (id_);


--
-- TOC entry 4599 (class 2606 OID 369366)
-- Name: act_procdef_info act_uniq_info_procdef; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_procdef_info
    ADD CONSTRAINT act_uniq_info_procdef UNIQUE (proc_def_id_);


--
-- TOC entry 4439 (class 2606 OID 368843)
-- Name: act_id_priv act_uniq_priv_name; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_priv
    ADD CONSTRAINT act_uniq_priv_name UNIQUE (name_);


--
-- TOC entry 4591 (class 2606 OID 369185)
-- Name: act_re_procdef act_uniq_procdef; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_re_procdef
    ADD CONSTRAINT act_uniq_procdef UNIQUE (key_, version_, derived_version_, tenant_id_);


--
-- TOC entry 4640 (class 2606 OID 371579)
-- Name: crm_data_table_column crm_data_table_column_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_data_table_column
    ADD CONSTRAINT crm_data_table_column_pkey PRIMARY KEY (id);


--
-- TOC entry 4635 (class 2606 OID 371567)
-- Name: crm_data_table crm_data_table_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_data_table
    ADD CONSTRAINT crm_data_table_pkey PRIMARY KEY (id);


--
-- TOC entry 4644 (class 2606 OID 371596)
-- Name: crm_data_table_row crm_data_table_row_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_data_table_row
    ADD CONSTRAINT crm_data_table_row_pkey PRIMARY KEY (id);


--
-- TOC entry 4650 (class 2606 OID 404508)
-- Name: crm_event_mp_outreach_log crm_event_mp_outreach_log_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_event_mp_outreach_log
    ADD CONSTRAINT crm_event_mp_outreach_log_pkey PRIMARY KEY (id);


--
-- TOC entry 4658 (class 2606 OID 411624)
-- Name: crm_tenant_enterprise crm_tenant_enterprise_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_tenant_enterprise
    ADD CONSTRAINT crm_tenant_enterprise_pkey PRIMARY KEY (id);


--
-- TOC entry 4419 (class 2606 OID 368762)
-- Name: flw_channel_definition flw_channel_definition_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.flw_channel_definition
    ADD CONSTRAINT flw_channel_definition_pkey PRIMARY KEY (id_);


--
-- TOC entry 4409 (class 2606 OID 368728)
-- Name: flw_ev_databasechangeloglock flw_ev_databasechangeloglock_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.flw_ev_databasechangeloglock
    ADD CONSTRAINT flw_ev_databasechangeloglock_pkey PRIMARY KEY (id);


--
-- TOC entry 4416 (class 2606 OID 368754)
-- Name: flw_event_definition flw_event_definition_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.flw_event_definition
    ADD CONSTRAINT flw_event_definition_pkey PRIMARY KEY (id_);


--
-- TOC entry 4411 (class 2606 OID 368740)
-- Name: flw_event_deployment flw_event_deployment_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.flw_event_deployment
    ADD CONSTRAINT flw_event_deployment_pkey PRIMARY KEY (id_);


--
-- TOC entry 4413 (class 2606 OID 368747)
-- Name: flw_event_resource flw_event_resource_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.flw_event_resource
    ADD CONSTRAINT flw_event_resource_pkey PRIMARY KEY (id_);


--
-- TOC entry 4571 (class 2606 OID 369103)
-- Name: flw_ru_batch_part flw_ru_batch_part_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.flw_ru_batch_part
    ADD CONSTRAINT flw_ru_batch_part_pkey PRIMARY KEY (id_);


--
-- TOC entry 4568 (class 2606 OID 369095)
-- Name: flw_ru_batch flw_ru_batch_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.flw_ru_batch
    ADD CONSTRAINT flw_ru_batch_pkey PRIMARY KEY (id_);


--
-- TOC entry 4380 (class 2606 OID 357346)
-- Name: gen_table_column gen_table_column_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.gen_table_column
    ADD CONSTRAINT gen_table_column_pkey PRIMARY KEY (column_id);


--
-- TOC entry 4378 (class 2606 OID 357334)
-- Name: gen_table gen_table_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.gen_table
    ADD CONSTRAINT gen_table_pkey PRIMARY KEY (table_id);


--
-- TOC entry 4337 (class 2606 OID 357083)
-- Name: master_tenant idx_master_tenant_tenant; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.master_tenant
    ADD CONSTRAINT idx_master_tenant_tenant UNIQUE (tenant);


--
-- TOC entry 4335 (class 2606 OID 357071)
-- Name: master_registration master_registration_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.master_registration
    ADD CONSTRAINT master_registration_pkey PRIMARY KEY (regist_id);


--
-- TOC entry 4339 (class 2606 OID 357081)
-- Name: master_tenant master_tenant_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.master_tenant
    ADD CONSTRAINT master_tenant_pkey PRIMARY KEY (id);


--
-- TOC entry 4390 (class 2606 OID 357396)
-- Name: qrtz_blob_triggers qrtz_blob_triggers_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.qrtz_blob_triggers
    ADD CONSTRAINT qrtz_blob_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- TOC entry 4392 (class 2606 OID 357408)
-- Name: qrtz_calendars qrtz_calendars_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.qrtz_calendars
    ADD CONSTRAINT qrtz_calendars_pkey PRIMARY KEY (sched_name, calendar_name);


--
-- TOC entry 4388 (class 2606 OID 357384)
-- Name: qrtz_cron_triggers qrtz_cron_triggers_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.qrtz_cron_triggers
    ADD CONSTRAINT qrtz_cron_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- TOC entry 4396 (class 2606 OID 357420)
-- Name: qrtz_fired_triggers qrtz_fired_triggers_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.qrtz_fired_triggers
    ADD CONSTRAINT qrtz_fired_triggers_pkey PRIMARY KEY (sched_name, entry_id);


--
-- TOC entry 4382 (class 2606 OID 357353)
-- Name: qrtz_job_details qrtz_job_details_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.qrtz_job_details
    ADD CONSTRAINT qrtz_job_details_pkey PRIMARY KEY (sched_name, job_name, job_group);


--
-- TOC entry 4400 (class 2606 OID 357430)
-- Name: qrtz_locks qrtz_locks_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.qrtz_locks
    ADD CONSTRAINT qrtz_locks_pkey PRIMARY KEY (sched_name, lock_name);


--
-- TOC entry 4394 (class 2606 OID 357413)
-- Name: qrtz_paused_trigger_grps qrtz_paused_trigger_grps_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.qrtz_paused_trigger_grps
    ADD CONSTRAINT qrtz_paused_trigger_grps_pkey PRIMARY KEY (sched_name, trigger_group);


--
-- TOC entry 4398 (class 2606 OID 357425)
-- Name: qrtz_scheduler_state qrtz_scheduler_state_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.qrtz_scheduler_state
    ADD CONSTRAINT qrtz_scheduler_state_pkey PRIMARY KEY (sched_name, instance_name);


--
-- TOC entry 4386 (class 2606 OID 357372)
-- Name: qrtz_simple_triggers qrtz_simple_triggers_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.qrtz_simple_triggers
    ADD CONSTRAINT qrtz_simple_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- TOC entry 4402 (class 2606 OID 357437)
-- Name: qrtz_simprop_triggers qrtz_simprop_triggers_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.qrtz_simprop_triggers
    ADD CONSTRAINT qrtz_simprop_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- TOC entry 4384 (class 2606 OID 357360)
-- Name: qrtz_triggers qrtz_triggers_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.qrtz_triggers
    ADD CONSTRAINT qrtz_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- TOC entry 4368 (class 2606 OID 357261)
-- Name: sys_config sys_config_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.sys_config
    ADD CONSTRAINT sys_config_pkey PRIMARY KEY (config_id);


--
-- TOC entry 4342 (class 2606 OID 357100)
-- Name: sys_dept sys_dept_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.sys_dept
    ADD CONSTRAINT sys_dept_pkey PRIMARY KEY (dept_id);


--
-- TOC entry 4366 (class 2606 OID 357246)
-- Name: sys_dict_data sys_dict_data_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.sys_dict_data
    ADD CONSTRAINT sys_dict_data_pkey PRIMARY KEY (dict_code);


--
-- TOC entry 4362 (class 2606 OID 357227)
-- Name: sys_dict_type sys_dict_type_dict_type_key; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.sys_dict_type
    ADD CONSTRAINT sys_dict_type_dict_type_key UNIQUE (dict_type);


--
-- TOC entry 4364 (class 2606 OID 357225)
-- Name: sys_dict_type sys_dict_type_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.sys_dict_type
    ADD CONSTRAINT sys_dict_type_pkey PRIMARY KEY (dict_id);


--
-- TOC entry 4374 (class 2606 OID 357303)
-- Name: sys_job_log sys_job_log_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.sys_job_log
    ADD CONSTRAINT sys_job_log_pkey PRIMARY KEY (job_log_id);


--
-- TOC entry 4372 (class 2606 OID 357293)
-- Name: sys_job sys_job_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.sys_job
    ADD CONSTRAINT sys_job_pkey PRIMARY KEY (job_id, job_name, job_group);


--
-- TOC entry 4370 (class 2606 OID 357276)
-- Name: sys_logininfor sys_logininfor_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.sys_logininfor
    ADD CONSTRAINT sys_logininfor_pkey PRIMARY KEY (info_id);


--
-- TOC entry 4350 (class 2606 OID 357169)
-- Name: sys_menu sys_menu_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.sys_menu
    ADD CONSTRAINT sys_menu_pkey PRIMARY KEY (menu_id);


--
-- TOC entry 4665 (class 2606 OID 413135)
-- Name: sys_messenger_page_tenant sys_messenger_page_tenant_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.sys_messenger_page_tenant
    ADD CONSTRAINT sys_messenger_page_tenant_pkey PRIMARY KEY (page_id);


--
-- TOC entry 4376 (class 2606 OID 357315)
-- Name: sys_notice sys_notice_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.sys_notice
    ADD CONSTRAINT sys_notice_pkey PRIMARY KEY (notice_id);


--
-- TOC entry 4360 (class 2606 OID 357211)
-- Name: sys_oper_log sys_oper_log_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.sys_oper_log
    ADD CONSTRAINT sys_oper_log_pkey PRIMARY KEY (oper_id);


--
-- TOC entry 4346 (class 2606 OID 357131)
-- Name: sys_post sys_post_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.sys_post
    ADD CONSTRAINT sys_post_pkey PRIMARY KEY (post_id);


--
-- TOC entry 4356 (class 2606 OID 357184)
-- Name: sys_role_dept sys_role_dept_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.sys_role_dept
    ADD CONSTRAINT sys_role_dept_pkey PRIMARY KEY (role_id, dept_id);


--
-- TOC entry 4354 (class 2606 OID 357179)
-- Name: sys_role_menu sys_role_menu_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.sys_role_menu
    ADD CONSTRAINT sys_role_menu_pkey PRIMARY KEY (role_id, menu_id);


--
-- TOC entry 4348 (class 2606 OID 357146)
-- Name: sys_role sys_role_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.sys_role
    ADD CONSTRAINT sys_role_pkey PRIMARY KEY (role_id);


--
-- TOC entry 4344 (class 2606 OID 357120)
-- Name: sys_user sys_user_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.sys_user
    ADD CONSTRAINT sys_user_pkey PRIMARY KEY (user_id);


--
-- TOC entry 4358 (class 2606 OID 357189)
-- Name: sys_user_post sys_user_post_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.sys_user_post
    ADD CONSTRAINT sys_user_post_pkey PRIMARY KEY (user_id, post_id);


--
-- TOC entry 4352 (class 2606 OID 357174)
-- Name: sys_user_role sys_user_role_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.sys_user_role
    ADD CONSTRAINT sys_user_role_pkey PRIMARY KEY (user_id, role_id);


--
-- TOC entry 4633 (class 2606 OID 371368)
-- Name: sys_wait_resume_tenant sys_wait_resume_tenant_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.sys_wait_resume_tenant
    ADD CONSTRAINT sys_wait_resume_tenant_pkey PRIMARY KEY (resume_key);


--
-- TOC entry 4667 (class 2606 OID 413283)
-- Name: sys_webchat_token_tenant sys_webchat_token_tenant_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.sys_webchat_token_tenant
    ADD CONSTRAINT sys_webchat_token_tenant_pkey PRIMARY KEY (website_token);


--
-- TOC entry 4648 (class 2606 OID 404291)
-- Name: sys_wechat_mp_tenant sys_wechat_mp_tenant_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.sys_wechat_mp_tenant
    ADD CONSTRAINT sys_wechat_mp_tenant_pkey PRIMARY KEY (app_id, tenant);


--
-- TOC entry 4331 (class 2606 OID 357050)
-- Name: sys_wecom_corp_tenant sys_wecom_corp_tenant_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.sys_wecom_corp_tenant
    ADD CONSTRAINT sys_wecom_corp_tenant_pkey PRIMARY KEY (corp_id);


--
-- TOC entry 4656 (class 2606 OID 409049)
-- Name: sys_whatsapp_phone_tenant sys_whatsapp_phone_tenant_pkey; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.sys_whatsapp_phone_tenant
    ADD CONSTRAINT sys_whatsapp_phone_tenant_pkey PRIMARY KEY (phone_number_id);


--
-- TOC entry 4654 (class 2606 OID 404510)
-- Name: crm_event_mp_outreach_log uk_event_mp_outreach; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_event_mp_outreach_log
    ADD CONSTRAINT uk_event_mp_outreach UNIQUE (event_id, open_id, trigger_type);


--
-- TOC entry 4333 (class 2606 OID 357052)
-- Name: sys_wecom_corp_tenant uk_sys_wecom_corp_tenant_tenant; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.sys_wecom_corp_tenant
    ADD CONSTRAINT uk_sys_wecom_corp_tenant_tenant UNIQUE (tenant);


--
-- TOC entry 4642 (class 2606 OID 371581)
-- Name: crm_data_table_column uq_crm_data_table_column_name; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_data_table_column
    ADD CONSTRAINT uq_crm_data_table_column_name UNIQUE (data_table_id, name);


--
-- TOC entry 4638 (class 2606 OID 371569)
-- Name: crm_data_table uq_crm_data_table_tenant_name; Type: CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_data_table
    ADD CONSTRAINT uq_crm_data_table_tenant_name UNIQUE (tenant_id, name);


--
-- TOC entry 4445 (class 1259 OID 369216)
-- Name: act_idx_athrz_procedef; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_athrz_procedef ON public.act_ru_identitylink USING btree (proc_def_id_);


--
-- TOC entry 4407 (class 1259 OID 369171)
-- Name: act_idx_bytear_depl; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_bytear_depl ON public.act_ge_bytearray USING btree (deployment_id_);


--
-- TOC entry 4417 (class 1259 OID 368763)
-- Name: act_idx_channel_def_uniq; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX act_idx_channel_def_uniq ON public.flw_channel_definition USING btree (key_, version_, tenant_id_);


--
-- TOC entry 4546 (class 1259 OID 369019)
-- Name: act_idx_deadletter_job_correlation_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_deadletter_job_correlation_id ON public.act_ru_deadletter_job USING btree (correlation_id_);


--
-- TOC entry 4547 (class 1259 OID 369018)
-- Name: act_idx_deadletter_job_custom_values_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_deadletter_job_custom_values_id ON public.act_ru_deadletter_job USING btree (custom_values_id_);


--
-- TOC entry 4548 (class 1259 OID 369017)
-- Name: act_idx_deadletter_job_exception_stack_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_deadletter_job_exception_stack_id ON public.act_ru_deadletter_job USING btree (exception_stack_id_);


--
-- TOC entry 4549 (class 1259 OID 369312)
-- Name: act_idx_deadletter_job_execution_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_deadletter_job_execution_id ON public.act_ru_deadletter_job USING btree (execution_id_);


--
-- TOC entry 4550 (class 1259 OID 369324)
-- Name: act_idx_deadletter_job_proc_def_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_deadletter_job_proc_def_id ON public.act_ru_deadletter_job USING btree (proc_def_id_);


--
-- TOC entry 4551 (class 1259 OID 369318)
-- Name: act_idx_deadletter_job_process_instance_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_deadletter_job_process_instance_id ON public.act_ru_deadletter_job USING btree (process_instance_id_);


--
-- TOC entry 4552 (class 1259 OID 369082)
-- Name: act_idx_djob_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_djob_scope ON public.act_ru_deadletter_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 4553 (class 1259 OID 369084)
-- Name: act_idx_djob_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_djob_scope_def ON public.act_ru_deadletter_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4554 (class 1259 OID 369083)
-- Name: act_idx_djob_sub_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_djob_sub_scope ON public.act_ru_deadletter_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4559 (class 1259 OID 369085)
-- Name: act_idx_ejob_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ejob_scope ON public.act_ru_external_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 4560 (class 1259 OID 369087)
-- Name: act_idx_ejob_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ejob_scope_def ON public.act_ru_external_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4561 (class 1259 OID 369086)
-- Name: act_idx_ejob_sub_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ejob_sub_scope ON public.act_ru_external_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4463 (class 1259 OID 368875)
-- Name: act_idx_ent_lnk_ref_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ent_lnk_ref_scope ON public.act_ru_entitylink USING btree (ref_scope_id_, ref_scope_type_, link_type_);


--
-- TOC entry 4464 (class 1259 OID 368876)
-- Name: act_idx_ent_lnk_root_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ent_lnk_root_scope ON public.act_ru_entitylink USING btree (root_scope_id_, root_scope_type_, link_type_);


--
-- TOC entry 4465 (class 1259 OID 368874)
-- Name: act_idx_ent_lnk_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ent_lnk_scope ON public.act_ru_entitylink USING btree (scope_id_, scope_type_, link_type_);


--
-- TOC entry 4466 (class 1259 OID 368877)
-- Name: act_idx_ent_lnk_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ent_lnk_scope_def ON public.act_ru_entitylink USING btree (scope_definition_id_, scope_type_, link_type_);


--
-- TOC entry 4414 (class 1259 OID 368755)
-- Name: act_idx_event_def_uniq; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX act_idx_event_def_uniq ON public.flw_event_definition USING btree (key_, version_, tenant_id_);


--
-- TOC entry 4475 (class 1259 OID 368898)
-- Name: act_idx_event_subscr; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_event_subscr ON public.act_ru_event_subscr USING btree (execution_id_);


--
-- TOC entry 4476 (class 1259 OID 368897)
-- Name: act_idx_event_subscr_config_; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_event_subscr_config_ ON public.act_ru_event_subscr USING btree (configuration_);


--
-- TOC entry 4579 (class 1259 OID 369192)
-- Name: act_idx_exe_parent; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_exe_parent ON public.act_ru_execution USING btree (parent_id_);


--
-- TOC entry 4580 (class 1259 OID 369204)
-- Name: act_idx_exe_procdef; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_exe_procdef ON public.act_ru_execution USING btree (proc_def_id_);


--
-- TOC entry 4581 (class 1259 OID 369186)
-- Name: act_idx_exe_procinst; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_exe_procinst ON public.act_ru_execution USING btree (proc_inst_id_);


--
-- TOC entry 4582 (class 1259 OID 369168)
-- Name: act_idx_exe_root; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_exe_root ON public.act_ru_execution USING btree (root_proc_inst_id_);


--
-- TOC entry 4583 (class 1259 OID 369198)
-- Name: act_idx_exe_super; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_exe_super ON public.act_ru_execution USING btree (super_exec_);


--
-- TOC entry 4584 (class 1259 OID 369167)
-- Name: act_idx_exec_buskey; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_exec_buskey ON public.act_ru_execution USING btree (business_key_);


--
-- TOC entry 4585 (class 1259 OID 369169)
-- Name: act_idx_exec_ref_id_; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_exec_ref_id_ ON public.act_ru_execution USING btree (reference_id_);


--
-- TOC entry 4562 (class 1259 OID 369022)
-- Name: act_idx_external_job_correlation_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_external_job_correlation_id ON public.act_ru_external_job USING btree (correlation_id_);


--
-- TOC entry 4563 (class 1259 OID 369021)
-- Name: act_idx_external_job_custom_values_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_external_job_custom_values_id ON public.act_ru_external_job USING btree (custom_values_id_);


--
-- TOC entry 4564 (class 1259 OID 369020)
-- Name: act_idx_external_job_exception_stack_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_external_job_exception_stack_id ON public.act_ru_external_job USING btree (exception_stack_id_);


--
-- TOC entry 4617 (class 1259 OID 369411)
-- Name: act_idx_hi_act_inst_end; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_act_inst_end ON public.act_hi_actinst USING btree (end_time_);


--
-- TOC entry 4618 (class 1259 OID 369421)
-- Name: act_idx_hi_act_inst_exec; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_act_inst_exec ON public.act_hi_actinst USING btree (execution_id_, act_id_);


--
-- TOC entry 4619 (class 1259 OID 369420)
-- Name: act_idx_hi_act_inst_procinst; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_act_inst_procinst ON public.act_hi_actinst USING btree (proc_inst_id_, act_id_);


--
-- TOC entry 4620 (class 1259 OID 369410)
-- Name: act_idx_hi_act_inst_start; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_act_inst_start ON public.act_hi_actinst USING btree (start_time_);


--
-- TOC entry 4623 (class 1259 OID 369413)
-- Name: act_idx_hi_detail_act_inst; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_detail_act_inst ON public.act_hi_detail USING btree (act_inst_id_);


--
-- TOC entry 4624 (class 1259 OID 369415)
-- Name: act_idx_hi_detail_name; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_detail_name ON public.act_hi_detail USING btree (name_);


--
-- TOC entry 4625 (class 1259 OID 369412)
-- Name: act_idx_hi_detail_proc_inst; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_detail_proc_inst ON public.act_hi_detail USING btree (proc_inst_id_);


--
-- TOC entry 4626 (class 1259 OID 369416)
-- Name: act_idx_hi_detail_task_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_detail_task_id ON public.act_hi_detail USING btree (task_id_);


--
-- TOC entry 4627 (class 1259 OID 369414)
-- Name: act_idx_hi_detail_time; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_detail_time ON public.act_hi_detail USING btree (time_);


--
-- TOC entry 4471 (class 1259 OID 368886)
-- Name: act_idx_hi_ent_lnk_ref_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_ent_lnk_ref_scope ON public.act_hi_entitylink USING btree (ref_scope_id_, ref_scope_type_, link_type_);


--
-- TOC entry 4472 (class 1259 OID 368887)
-- Name: act_idx_hi_ent_lnk_root_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_ent_lnk_root_scope ON public.act_hi_entitylink USING btree (root_scope_id_, root_scope_type_, link_type_);


--
-- TOC entry 4473 (class 1259 OID 368885)
-- Name: act_idx_hi_ent_lnk_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_ent_lnk_scope ON public.act_hi_entitylink USING btree (scope_id_, scope_type_, link_type_);


--
-- TOC entry 4474 (class 1259 OID 368888)
-- Name: act_idx_hi_ent_lnk_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_ent_lnk_scope_def ON public.act_hi_entitylink USING btree (scope_definition_id_, scope_type_, link_type_);


--
-- TOC entry 4457 (class 1259 OID 369423)
-- Name: act_idx_hi_ident_lnk_procinst; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_ident_lnk_procinst ON public.act_hi_identitylink USING btree (proc_inst_id_);


--
-- TOC entry 4458 (class 1259 OID 368864)
-- Name: act_idx_hi_ident_lnk_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_ident_lnk_scope ON public.act_hi_identitylink USING btree (scope_id_, scope_type_);


--
-- TOC entry 4459 (class 1259 OID 368866)
-- Name: act_idx_hi_ident_lnk_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_ident_lnk_scope_def ON public.act_hi_identitylink USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4460 (class 1259 OID 368865)
-- Name: act_idx_hi_ident_lnk_sub_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_ident_lnk_sub_scope ON public.act_hi_identitylink USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4461 (class 1259 OID 369422)
-- Name: act_idx_hi_ident_lnk_task; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_ident_lnk_task ON public.act_hi_identitylink USING btree (task_id_);


--
-- TOC entry 4462 (class 1259 OID 368863)
-- Name: act_idx_hi_ident_lnk_user; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_ident_lnk_user ON public.act_hi_identitylink USING btree (user_id_);


--
-- TOC entry 4613 (class 1259 OID 369409)
-- Name: act_idx_hi_pro_i_buskey; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_pro_i_buskey ON public.act_hi_procinst USING btree (business_key_);


--
-- TOC entry 4614 (class 1259 OID 369408)
-- Name: act_idx_hi_pro_inst_end; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_pro_inst_end ON public.act_hi_procinst USING btree (end_time_);


--
-- TOC entry 4506 (class 1259 OID 369419)
-- Name: act_idx_hi_procvar_exe; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_procvar_exe ON public.act_hi_varinst USING btree (execution_id_);


--
-- TOC entry 4507 (class 1259 OID 368956)
-- Name: act_idx_hi_procvar_name_type; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_procvar_name_type ON public.act_hi_varinst USING btree (name_, var_type_);


--
-- TOC entry 4508 (class 1259 OID 369417)
-- Name: act_idx_hi_procvar_proc_inst; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_procvar_proc_inst ON public.act_hi_varinst USING btree (proc_inst_id_);


--
-- TOC entry 4509 (class 1259 OID 369418)
-- Name: act_idx_hi_procvar_task_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_procvar_task_id ON public.act_hi_varinst USING btree (task_id_);


--
-- TOC entry 4490 (class 1259 OID 369424)
-- Name: act_idx_hi_task_inst_procinst; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_task_inst_procinst ON public.act_hi_taskinst USING btree (proc_inst_id_);


--
-- TOC entry 4491 (class 1259 OID 368930)
-- Name: act_idx_hi_task_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_task_scope ON public.act_hi_taskinst USING btree (scope_id_, scope_type_);


--
-- TOC entry 4492 (class 1259 OID 368932)
-- Name: act_idx_hi_task_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_task_scope_def ON public.act_hi_taskinst USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4493 (class 1259 OID 368931)
-- Name: act_idx_hi_task_sub_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_task_sub_scope ON public.act_hi_taskinst USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4510 (class 1259 OID 368957)
-- Name: act_idx_hi_var_scope_id_type; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_var_scope_id_type ON public.act_hi_varinst USING btree (scope_id_, scope_type_);


--
-- TOC entry 4511 (class 1259 OID 368958)
-- Name: act_idx_hi_var_sub_id_type; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_hi_var_sub_id_type ON public.act_hi_varinst USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4446 (class 1259 OID 368852)
-- Name: act_idx_ident_lnk_group; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ident_lnk_group ON public.act_ru_identitylink USING btree (group_id_);


--
-- TOC entry 4447 (class 1259 OID 368853)
-- Name: act_idx_ident_lnk_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ident_lnk_scope ON public.act_ru_identitylink USING btree (scope_id_, scope_type_);


--
-- TOC entry 4448 (class 1259 OID 368855)
-- Name: act_idx_ident_lnk_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ident_lnk_scope_def ON public.act_ru_identitylink USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4449 (class 1259 OID 368854)
-- Name: act_idx_ident_lnk_sub_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ident_lnk_sub_scope ON public.act_ru_identitylink USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4450 (class 1259 OID 368851)
-- Name: act_idx_ident_lnk_user; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ident_lnk_user ON public.act_ru_identitylink USING btree (user_id_);


--
-- TOC entry 4451 (class 1259 OID 369222)
-- Name: act_idx_idl_procinst; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_idl_procinst ON public.act_ru_identitylink USING btree (proc_inst_id_);


--
-- TOC entry 4512 (class 1259 OID 369009)
-- Name: act_idx_job_correlation_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_job_correlation_id ON public.act_ru_job USING btree (correlation_id_);


--
-- TOC entry 4513 (class 1259 OID 369008)
-- Name: act_idx_job_custom_values_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_job_custom_values_id ON public.act_ru_job USING btree (custom_values_id_);


--
-- TOC entry 4514 (class 1259 OID 369007)
-- Name: act_idx_job_exception_stack_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_job_exception_stack_id ON public.act_ru_job USING btree (exception_stack_id_);


--
-- TOC entry 4515 (class 1259 OID 369258)
-- Name: act_idx_job_execution_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_job_execution_id ON public.act_ru_job USING btree (execution_id_);


--
-- TOC entry 4516 (class 1259 OID 369270)
-- Name: act_idx_job_proc_def_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_job_proc_def_id ON public.act_ru_job USING btree (proc_def_id_);


--
-- TOC entry 4517 (class 1259 OID 369264)
-- Name: act_idx_job_process_instance_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_job_process_instance_id ON public.act_ru_job USING btree (process_instance_id_);


--
-- TOC entry 4518 (class 1259 OID 369073)
-- Name: act_idx_job_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_job_scope ON public.act_ru_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 4519 (class 1259 OID 369075)
-- Name: act_idx_job_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_job_scope_def ON public.act_ru_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4520 (class 1259 OID 369074)
-- Name: act_idx_job_sub_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_job_sub_scope ON public.act_ru_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4428 (class 1259 OID 368822)
-- Name: act_idx_memb_group; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_memb_group ON public.act_id_membership USING btree (group_id_);


--
-- TOC entry 4429 (class 1259 OID 368828)
-- Name: act_idx_memb_user; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_memb_user ON public.act_id_membership USING btree (user_id_);


--
-- TOC entry 4574 (class 1259 OID 369347)
-- Name: act_idx_model_deployment; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_model_deployment ON public.act_re_model USING btree (deployment_id_);


--
-- TOC entry 4575 (class 1259 OID 369335)
-- Name: act_idx_model_source; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_model_source ON public.act_re_model USING btree (editor_source_value_id_);


--
-- TOC entry 4576 (class 1259 OID 369341)
-- Name: act_idx_model_source_extra; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_model_source_extra ON public.act_re_model USING btree (editor_source_extra_value_id_);


--
-- TOC entry 4442 (class 1259 OID 368841)
-- Name: act_idx_priv_group; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_priv_group ON public.act_id_priv_mapping USING btree (group_id_);


--
-- TOC entry 4443 (class 1259 OID 368834)
-- Name: act_idx_priv_mapping; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_priv_mapping ON public.act_id_priv_mapping USING btree (priv_id_);


--
-- TOC entry 4444 (class 1259 OID 368840)
-- Name: act_idx_priv_user; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_priv_user ON public.act_id_priv_mapping USING btree (user_id_);


--
-- TOC entry 4594 (class 1259 OID 369353)
-- Name: act_idx_procdef_info_json; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_procdef_info_json ON public.act_procdef_info USING btree (info_json_id_);


--
-- TOC entry 4595 (class 1259 OID 369359)
-- Name: act_idx_procdef_info_proc; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_procdef_info_proc ON public.act_procdef_info USING btree (proc_def_id_);


--
-- TOC entry 4600 (class 1259 OID 369173)
-- Name: act_idx_ru_acti_end; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ru_acti_end ON public.act_ru_actinst USING btree (end_time_);


--
-- TOC entry 4601 (class 1259 OID 369176)
-- Name: act_idx_ru_acti_exec; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ru_acti_exec ON public.act_ru_actinst USING btree (execution_id_);


--
-- TOC entry 4602 (class 1259 OID 369177)
-- Name: act_idx_ru_acti_exec_act; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ru_acti_exec_act ON public.act_ru_actinst USING btree (execution_id_, act_id_);


--
-- TOC entry 4603 (class 1259 OID 369174)
-- Name: act_idx_ru_acti_proc; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ru_acti_proc ON public.act_ru_actinst USING btree (proc_inst_id_);


--
-- TOC entry 4604 (class 1259 OID 369175)
-- Name: act_idx_ru_acti_proc_act; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ru_acti_proc_act ON public.act_ru_actinst USING btree (proc_inst_id_, act_id_);


--
-- TOC entry 4605 (class 1259 OID 369172)
-- Name: act_idx_ru_acti_start; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ru_acti_start ON public.act_ru_actinst USING btree (start_time_);


--
-- TOC entry 4606 (class 1259 OID 369178)
-- Name: act_idx_ru_acti_task; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ru_acti_task ON public.act_ru_actinst USING btree (task_id_);


--
-- TOC entry 4496 (class 1259 OID 368940)
-- Name: act_idx_ru_var_scope_id_type; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ru_var_scope_id_type ON public.act_ru_variable USING btree (scope_id_, scope_type_);


--
-- TOC entry 4497 (class 1259 OID 368941)
-- Name: act_idx_ru_var_sub_id_type; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_ru_var_sub_id_type ON public.act_ru_variable USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4535 (class 1259 OID 369079)
-- Name: act_idx_sjob_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_sjob_scope ON public.act_ru_suspended_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 4536 (class 1259 OID 369081)
-- Name: act_idx_sjob_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_sjob_scope_def ON public.act_ru_suspended_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4537 (class 1259 OID 369080)
-- Name: act_idx_sjob_sub_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_sjob_sub_scope ON public.act_ru_suspended_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4538 (class 1259 OID 369016)
-- Name: act_idx_suspended_job_correlation_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_suspended_job_correlation_id ON public.act_ru_suspended_job USING btree (correlation_id_);


--
-- TOC entry 4539 (class 1259 OID 369015)
-- Name: act_idx_suspended_job_custom_values_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_suspended_job_custom_values_id ON public.act_ru_suspended_job USING btree (custom_values_id_);


--
-- TOC entry 4540 (class 1259 OID 369014)
-- Name: act_idx_suspended_job_exception_stack_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_suspended_job_exception_stack_id ON public.act_ru_suspended_job USING btree (exception_stack_id_);


--
-- TOC entry 4541 (class 1259 OID 369294)
-- Name: act_idx_suspended_job_execution_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_suspended_job_execution_id ON public.act_ru_suspended_job USING btree (execution_id_);


--
-- TOC entry 4542 (class 1259 OID 369306)
-- Name: act_idx_suspended_job_proc_def_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_suspended_job_proc_def_id ON public.act_ru_suspended_job USING btree (proc_def_id_);


--
-- TOC entry 4543 (class 1259 OID 369300)
-- Name: act_idx_suspended_job_process_instance_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_suspended_job_process_instance_id ON public.act_ru_suspended_job USING btree (process_instance_id_);


--
-- TOC entry 4479 (class 1259 OID 368907)
-- Name: act_idx_task_create; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_task_create ON public.act_ru_task USING btree (create_time_);


--
-- TOC entry 4480 (class 1259 OID 369228)
-- Name: act_idx_task_exec; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_task_exec ON public.act_ru_task USING btree (execution_id_);


--
-- TOC entry 4481 (class 1259 OID 369240)
-- Name: act_idx_task_procdef; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_task_procdef ON public.act_ru_task USING btree (proc_def_id_);


--
-- TOC entry 4482 (class 1259 OID 369234)
-- Name: act_idx_task_procinst; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_task_procinst ON public.act_ru_task USING btree (proc_inst_id_);


--
-- TOC entry 4483 (class 1259 OID 368908)
-- Name: act_idx_task_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_task_scope ON public.act_ru_task USING btree (scope_id_, scope_type_);


--
-- TOC entry 4484 (class 1259 OID 368910)
-- Name: act_idx_task_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_task_scope_def ON public.act_ru_task USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4485 (class 1259 OID 368909)
-- Name: act_idx_task_sub_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_task_sub_scope ON public.act_ru_task USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4523 (class 1259 OID 369012)
-- Name: act_idx_timer_job_correlation_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_timer_job_correlation_id ON public.act_ru_timer_job USING btree (correlation_id_);


--
-- TOC entry 4524 (class 1259 OID 369011)
-- Name: act_idx_timer_job_custom_values_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_timer_job_custom_values_id ON public.act_ru_timer_job USING btree (custom_values_id_);


--
-- TOC entry 4525 (class 1259 OID 369013)
-- Name: act_idx_timer_job_duedate; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_timer_job_duedate ON public.act_ru_timer_job USING btree (duedate_);


--
-- TOC entry 4526 (class 1259 OID 369010)
-- Name: act_idx_timer_job_exception_stack_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_timer_job_exception_stack_id ON public.act_ru_timer_job USING btree (exception_stack_id_);


--
-- TOC entry 4527 (class 1259 OID 369276)
-- Name: act_idx_timer_job_execution_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_timer_job_execution_id ON public.act_ru_timer_job USING btree (execution_id_);


--
-- TOC entry 4528 (class 1259 OID 369288)
-- Name: act_idx_timer_job_proc_def_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_timer_job_proc_def_id ON public.act_ru_timer_job USING btree (proc_def_id_);


--
-- TOC entry 4529 (class 1259 OID 369282)
-- Name: act_idx_timer_job_process_instance_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_timer_job_process_instance_id ON public.act_ru_timer_job USING btree (process_instance_id_);


--
-- TOC entry 4530 (class 1259 OID 369076)
-- Name: act_idx_tjob_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_tjob_scope ON public.act_ru_timer_job USING btree (scope_id_, scope_type_);


--
-- TOC entry 4531 (class 1259 OID 369078)
-- Name: act_idx_tjob_scope_def; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_tjob_scope_def ON public.act_ru_timer_job USING btree (scope_definition_id_, scope_type_);


--
-- TOC entry 4532 (class 1259 OID 369077)
-- Name: act_idx_tjob_sub_scope; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_tjob_sub_scope ON public.act_ru_timer_job USING btree (sub_scope_id_, scope_type_);


--
-- TOC entry 4452 (class 1259 OID 369210)
-- Name: act_idx_tskass_task; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_tskass_task ON public.act_ru_identitylink USING btree (task_id_);


--
-- TOC entry 4498 (class 1259 OID 368942)
-- Name: act_idx_var_bytearray; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_var_bytearray ON public.act_ru_variable USING btree (bytearray_id_);


--
-- TOC entry 4499 (class 1259 OID 369246)
-- Name: act_idx_var_exe; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_var_exe ON public.act_ru_variable USING btree (execution_id_);


--
-- TOC entry 4500 (class 1259 OID 369252)
-- Name: act_idx_var_procinst; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_var_procinst ON public.act_ru_variable USING btree (proc_inst_id_);


--
-- TOC entry 4501 (class 1259 OID 369170)
-- Name: act_idx_variable_task_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX act_idx_variable_task_id ON public.act_ru_variable USING btree (task_id_);


--
-- TOC entry 4569 (class 1259 OID 369104)
-- Name: flw_idx_batch_part; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX flw_idx_batch_part ON public.flw_ru_batch_part USING btree (batch_id_);


--
-- TOC entry 4645 (class 1259 OID 371602)
-- Name: idx_crm_data_table_row_table; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_data_table_row_table ON public.crm_data_table_row USING btree (data_table_id);


--
-- TOC entry 4646 (class 1259 OID 371603)
-- Name: idx_crm_data_table_row_table_created; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_data_table_row_table_created ON public.crm_data_table_row USING btree (data_table_id, create_time DESC);


--
-- TOC entry 4636 (class 1259 OID 371604)
-- Name: idx_crm_data_table_tenant; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_data_table_tenant ON public.crm_data_table USING btree (tenant_id);


--
-- TOC entry 4659 (class 1259 OID 411628)
-- Name: idx_crm_te_country; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_te_country ON public.crm_tenant_enterprise USING btree (country_of_origin) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 4660 (class 1259 OID 411627)
-- Name: idx_crm_te_enterprise_type; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_te_enterprise_type ON public.crm_tenant_enterprise USING btree (enterprise_type) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 4661 (class 1259 OID 411626)
-- Name: idx_crm_te_industry; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_te_industry ON public.crm_tenant_enterprise USING btree (industry) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 4662 (class 1259 OID 411625)
-- Name: idx_crm_te_park_id; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_te_park_id ON public.crm_tenant_enterprise USING btree (park_id) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 4663 (class 1259 OID 411629)
-- Name: idx_crm_te_status; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_crm_te_status ON public.crm_tenant_enterprise USING btree (status) WHERE (del_flag = '0'::bpchar);


--
-- TOC entry 4651 (class 1259 OID 404511)
-- Name: idx_mp_outreach_event_time; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_outreach_event_time ON public.crm_event_mp_outreach_log USING btree (event_id, create_time DESC);


--
-- TOC entry 4652 (class 1259 OID 404512)
-- Name: idx_mp_outreach_openid; Type: INDEX; Schema: public; Owner: luohd
--

CREATE INDEX idx_mp_outreach_openid ON public.crm_event_mp_outreach_log USING btree (app_id, open_id);


--
-- TOC entry 4668 (class 1259 OID 413284)
-- Name: uk_sys_webchat_token_tenant_tenant; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uk_sys_webchat_token_tenant_tenant ON public.sys_webchat_token_tenant USING btree (tenant);


--
-- TOC entry 4340 (class 1259 OID 404727)
-- Name: uq_master_tenant_website_slug; Type: INDEX; Schema: public; Owner: luohd
--

CREATE UNIQUE INDEX uq_master_tenant_website_slug ON public.master_tenant USING btree (website_slug);


--
-- TOC entry 4678 (class 2606 OID 369217)
-- Name: act_ru_identitylink act_fk_athrz_procedef; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_identitylink
    ADD CONSTRAINT act_fk_athrz_procedef FOREIGN KEY (proc_def_id_) REFERENCES public.act_re_procdef(id_);


--
-- TOC entry 4674 (class 2606 OID 369179)
-- Name: act_ge_bytearray act_fk_bytearr_depl; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ge_bytearray
    ADD CONSTRAINT act_fk_bytearr_depl FOREIGN KEY (deployment_id_) REFERENCES public.act_re_deployment(id_);


--
-- TOC entry 4703 (class 2606 OID 369058)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_custom_values; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 4704 (class 2606 OID 369053)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_exception; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 4705 (class 2606 OID 369313)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_execution; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_execution FOREIGN KEY (execution_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 4706 (class 2606 OID 369325)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_proc_def; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_proc_def FOREIGN KEY (proc_def_id_) REFERENCES public.act_re_procdef(id_);


--
-- TOC entry 4707 (class 2606 OID 369319)
-- Name: act_ru_deadletter_job act_fk_deadletter_job_process_instance; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_deadletter_job
    ADD CONSTRAINT act_fk_deadletter_job_process_instance FOREIGN KEY (process_instance_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 4681 (class 2606 OID 369330)
-- Name: act_ru_event_subscr act_fk_event_exec; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_event_subscr
    ADD CONSTRAINT act_fk_event_exec FOREIGN KEY (execution_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 4714 (class 2606 OID 369193)
-- Name: act_ru_execution act_fk_exe_parent; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_execution
    ADD CONSTRAINT act_fk_exe_parent FOREIGN KEY (parent_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 4715 (class 2606 OID 369205)
-- Name: act_ru_execution act_fk_exe_procdef; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_execution
    ADD CONSTRAINT act_fk_exe_procdef FOREIGN KEY (proc_def_id_) REFERENCES public.act_re_procdef(id_);


--
-- TOC entry 4716 (class 2606 OID 369187)
-- Name: act_ru_execution act_fk_exe_procinst; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_execution
    ADD CONSTRAINT act_fk_exe_procinst FOREIGN KEY (proc_inst_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 4717 (class 2606 OID 369199)
-- Name: act_ru_execution act_fk_exe_super; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_execution
    ADD CONSTRAINT act_fk_exe_super FOREIGN KEY (super_exec_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 4708 (class 2606 OID 369068)
-- Name: act_ru_external_job act_fk_external_job_custom_values; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_external_job
    ADD CONSTRAINT act_fk_external_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 4709 (class 2606 OID 369063)
-- Name: act_ru_external_job act_fk_external_job_exception; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_external_job
    ADD CONSTRAINT act_fk_external_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 4679 (class 2606 OID 369223)
-- Name: act_ru_identitylink act_fk_idl_procinst; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_identitylink
    ADD CONSTRAINT act_fk_idl_procinst FOREIGN KEY (proc_inst_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 4718 (class 2606 OID 369354)
-- Name: act_procdef_info act_fk_info_json_ba; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_procdef_info
    ADD CONSTRAINT act_fk_info_json_ba FOREIGN KEY (info_json_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 4719 (class 2606 OID 369360)
-- Name: act_procdef_info act_fk_info_procdef; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_procdef_info
    ADD CONSTRAINT act_fk_info_procdef FOREIGN KEY (proc_def_id_) REFERENCES public.act_re_procdef(id_);


--
-- TOC entry 4688 (class 2606 OID 369028)
-- Name: act_ru_job act_fk_job_custom_values; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_job
    ADD CONSTRAINT act_fk_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 4689 (class 2606 OID 369023)
-- Name: act_ru_job act_fk_job_exception; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_job
    ADD CONSTRAINT act_fk_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 4690 (class 2606 OID 369259)
-- Name: act_ru_job act_fk_job_execution; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_job
    ADD CONSTRAINT act_fk_job_execution FOREIGN KEY (execution_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 4691 (class 2606 OID 369271)
-- Name: act_ru_job act_fk_job_proc_def; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_job
    ADD CONSTRAINT act_fk_job_proc_def FOREIGN KEY (proc_def_id_) REFERENCES public.act_re_procdef(id_);


--
-- TOC entry 4692 (class 2606 OID 369265)
-- Name: act_ru_job act_fk_job_process_instance; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_job
    ADD CONSTRAINT act_fk_job_process_instance FOREIGN KEY (process_instance_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 4675 (class 2606 OID 368823)
-- Name: act_id_membership act_fk_memb_group; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_membership
    ADD CONSTRAINT act_fk_memb_group FOREIGN KEY (group_id_) REFERENCES public.act_id_group(id_);


--
-- TOC entry 4676 (class 2606 OID 368829)
-- Name: act_id_membership act_fk_memb_user; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_membership
    ADD CONSTRAINT act_fk_memb_user FOREIGN KEY (user_id_) REFERENCES public.act_id_user(id_);


--
-- TOC entry 4711 (class 2606 OID 369348)
-- Name: act_re_model act_fk_model_deployment; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_re_model
    ADD CONSTRAINT act_fk_model_deployment FOREIGN KEY (deployment_id_) REFERENCES public.act_re_deployment(id_);


--
-- TOC entry 4712 (class 2606 OID 369336)
-- Name: act_re_model act_fk_model_source; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_re_model
    ADD CONSTRAINT act_fk_model_source FOREIGN KEY (editor_source_value_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 4713 (class 2606 OID 369342)
-- Name: act_re_model act_fk_model_source_extra; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_re_model
    ADD CONSTRAINT act_fk_model_source_extra FOREIGN KEY (editor_source_extra_value_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 4677 (class 2606 OID 368835)
-- Name: act_id_priv_mapping act_fk_priv_mapping; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_id_priv_mapping
    ADD CONSTRAINT act_fk_priv_mapping FOREIGN KEY (priv_id_) REFERENCES public.act_id_priv(id_);


--
-- TOC entry 4698 (class 2606 OID 369048)
-- Name: act_ru_suspended_job act_fk_suspended_job_custom_values; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 4699 (class 2606 OID 369043)
-- Name: act_ru_suspended_job act_fk_suspended_job_exception; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 4700 (class 2606 OID 369295)
-- Name: act_ru_suspended_job act_fk_suspended_job_execution; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_execution FOREIGN KEY (execution_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 4701 (class 2606 OID 369307)
-- Name: act_ru_suspended_job act_fk_suspended_job_proc_def; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_proc_def FOREIGN KEY (proc_def_id_) REFERENCES public.act_re_procdef(id_);


--
-- TOC entry 4702 (class 2606 OID 369301)
-- Name: act_ru_suspended_job act_fk_suspended_job_process_instance; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_suspended_job
    ADD CONSTRAINT act_fk_suspended_job_process_instance FOREIGN KEY (process_instance_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 4682 (class 2606 OID 369229)
-- Name: act_ru_task act_fk_task_exe; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_task
    ADD CONSTRAINT act_fk_task_exe FOREIGN KEY (execution_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 4683 (class 2606 OID 369241)
-- Name: act_ru_task act_fk_task_procdef; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_task
    ADD CONSTRAINT act_fk_task_procdef FOREIGN KEY (proc_def_id_) REFERENCES public.act_re_procdef(id_);


--
-- TOC entry 4684 (class 2606 OID 369235)
-- Name: act_ru_task act_fk_task_procinst; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_task
    ADD CONSTRAINT act_fk_task_procinst FOREIGN KEY (proc_inst_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 4693 (class 2606 OID 369038)
-- Name: act_ru_timer_job act_fk_timer_job_custom_values; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_custom_values FOREIGN KEY (custom_values_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 4694 (class 2606 OID 369033)
-- Name: act_ru_timer_job act_fk_timer_job_exception; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_exception FOREIGN KEY (exception_stack_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 4695 (class 2606 OID 369277)
-- Name: act_ru_timer_job act_fk_timer_job_execution; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_execution FOREIGN KEY (execution_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 4696 (class 2606 OID 369289)
-- Name: act_ru_timer_job act_fk_timer_job_proc_def; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_proc_def FOREIGN KEY (proc_def_id_) REFERENCES public.act_re_procdef(id_);


--
-- TOC entry 4697 (class 2606 OID 369283)
-- Name: act_ru_timer_job act_fk_timer_job_process_instance; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_timer_job
    ADD CONSTRAINT act_fk_timer_job_process_instance FOREIGN KEY (process_instance_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 4680 (class 2606 OID 369211)
-- Name: act_ru_identitylink act_fk_tskass_task; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_identitylink
    ADD CONSTRAINT act_fk_tskass_task FOREIGN KEY (task_id_) REFERENCES public.act_ru_task(id_);


--
-- TOC entry 4685 (class 2606 OID 368943)
-- Name: act_ru_variable act_fk_var_bytearray; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_variable
    ADD CONSTRAINT act_fk_var_bytearray FOREIGN KEY (bytearray_id_) REFERENCES public.act_ge_bytearray(id_);


--
-- TOC entry 4686 (class 2606 OID 369247)
-- Name: act_ru_variable act_fk_var_exe; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_variable
    ADD CONSTRAINT act_fk_var_exe FOREIGN KEY (execution_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 4687 (class 2606 OID 369253)
-- Name: act_ru_variable act_fk_var_procinst; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.act_ru_variable
    ADD CONSTRAINT act_fk_var_procinst FOREIGN KEY (proc_inst_id_) REFERENCES public.act_ru_execution(id_);


--
-- TOC entry 4720 (class 2606 OID 371582)
-- Name: crm_data_table_column crm_data_table_column_data_table_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_data_table_column
    ADD CONSTRAINT crm_data_table_column_data_table_id_fkey FOREIGN KEY (data_table_id) REFERENCES public.crm_data_table(id) ON DELETE CASCADE;


--
-- TOC entry 4721 (class 2606 OID 371597)
-- Name: crm_data_table_row crm_data_table_row_data_table_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.crm_data_table_row
    ADD CONSTRAINT crm_data_table_row_data_table_id_fkey FOREIGN KEY (data_table_id) REFERENCES public.crm_data_table(id) ON DELETE CASCADE;


--
-- TOC entry 4710 (class 2606 OID 369105)
-- Name: flw_ru_batch_part flw_fk_batch_part_parent; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.flw_ru_batch_part
    ADD CONSTRAINT flw_fk_batch_part_parent FOREIGN KEY (batch_id_) REFERENCES public.flw_ru_batch(id_);


--
-- TOC entry 4672 (class 2606 OID 357397)
-- Name: qrtz_blob_triggers qrtz_blob_triggers_sched_name_trigger_name_trigger_group_fkey; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.qrtz_blob_triggers
    ADD CONSTRAINT qrtz_blob_triggers_sched_name_trigger_name_trigger_group_fkey FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES public.qrtz_triggers(sched_name, trigger_name, trigger_group);


--
-- TOC entry 4671 (class 2606 OID 357385)
-- Name: qrtz_cron_triggers qrtz_cron_triggers_sched_name_trigger_name_trigger_group_fkey; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.qrtz_cron_triggers
    ADD CONSTRAINT qrtz_cron_triggers_sched_name_trigger_name_trigger_group_fkey FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES public.qrtz_triggers(sched_name, trigger_name, trigger_group);


--
-- TOC entry 4670 (class 2606 OID 357373)
-- Name: qrtz_simple_triggers qrtz_simple_triggers_sched_name_trigger_name_trigger_group_fkey; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.qrtz_simple_triggers
    ADD CONSTRAINT qrtz_simple_triggers_sched_name_trigger_name_trigger_group_fkey FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES public.qrtz_triggers(sched_name, trigger_name, trigger_group);


--
-- TOC entry 4673 (class 2606 OID 357438)
-- Name: qrtz_simprop_triggers qrtz_simprop_triggers_sched_name_trigger_name_trigger_grou_fkey; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.qrtz_simprop_triggers
    ADD CONSTRAINT qrtz_simprop_triggers_sched_name_trigger_name_trigger_grou_fkey FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES public.qrtz_triggers(sched_name, trigger_name, trigger_group);


--
-- TOC entry 4669 (class 2606 OID 357361)
-- Name: qrtz_triggers qrtz_triggers_sched_name_job_name_job_group_fkey; Type: FK CONSTRAINT; Schema: public; Owner: luohd
--

ALTER TABLE ONLY public.qrtz_triggers
    ADD CONSTRAINT qrtz_triggers_sched_name_job_name_job_group_fkey FOREIGN KEY (sched_name, job_name, job_group) REFERENCES public.qrtz_job_details(sched_name, job_name, job_group);


--
-- TOC entry 4867 (class 0 OID 0)
-- Dependencies: 6
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: luohd
--

REVOKE USAGE ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2026-10-19 20:41:50 CST

--
-- PostgreSQL database dump complete
--

