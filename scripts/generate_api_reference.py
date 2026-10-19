#!/usr/bin/env python3
"""
Generate docs/api-reference.md from *Controller.java sources.
Run from repo root: python3 scripts/generate_api_reference.py
"""

from __future__ import annotations

import re
from dataclasses import dataclass, field
from datetime import datetime, timezone
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parent.parent
OUTPUT = REPO_ROOT / "docs" / "api-reference.md"

# --- Mapping annotation patterns ---
MAPPING_ANN = re.compile(
    r"@(?P<ann>Get|Post|Put|Delete|Patch|Request)Mapping\s*"
    r"(?:\(\s*(?P<args>[^)]*)\))?",
    re.MULTILINE,
)
CLASS_REQUEST_MAPPING = re.compile(
    r"@RequestMapping\s*\(\s*(?:value\s*=\s*)?[\"']([^\"']+)[\"']",
)
CLASS_NAME = re.compile(r"public\s+class\s+(\w+)")
REST_CONTROLLER = re.compile(r"@RestController")
CONTROLLER_ANN = re.compile(r"@Controller\b")
PREAUTHORIZE = re.compile(r"@PreAuthorize\s*\([^)]*hasPermi\s*\(\s*['\"]([^'\"]+)['\"]")
ANONYMOUS = re.compile(r"@Anonymous\b")
JAVADOC_BLOCK = re.compile(r"/\*\*(.*?)\*/", re.DOTALL)

# SecurityConfig permitAll paths (static snapshot + regex parse attempt)
SECURITY_CONFIG = REPO_ROOT / "ruoyi-framework/src/main/java/com/ruoyi/framework/config/SecurityConfig.java"

TOPIC_LINKS = [
    ("/crm/wecom/", "企业微信", ["wecom-implemented-api-guide.md", "wecom-backend-contract.md"]),
    ("/crm/data-table", "Data Table", ["data-table-backend-contract.md"]),
    ("/crm/webhook-trigger", "Webhook Trigger", ["webhook-trigger-backend.md"]),
    ("/webhook", "Webhook 流程", ["webhook-trigger-backend.md"]),
    ("/crm/semanticprofile", "语义层", ["semantic-layer-config-contract.md"]),
    ("/crm/sf-express", "顺丰", ["sf-express-backend-contract.md"]),
    ("/crm/wwaitresume", "Wait Resume", ["wait-node.md", "wait-resume-inngest-integration.md"]),
    ("/waitresume", "Wait Resume 公开", ["wait-node.md", "wait-resume-inngest-integration.md"]),
]

MODULE_ORDER = [
    ("auth", "认证与其它（无模块前缀）", lambda b: b in ("", "/")),
    ("system", "系统管理 `/system/*`", lambda b: b.startswith("/system")),
    ("crm", "CRM `/crm/*`", lambda b: b.startswith("/crm") and not b.startswith("/crm/w")),
    ("ems", "EMS `/ems/*` 与 BRE", lambda b: b.startswith("/ems") or b.startswith("/bre")),
    ("workflow_crm", "CRM 工作流运行时 `/crm/w*`", lambda b: b.startswith("/crm/w")),
    ("flowable", "Flowable `/flowable/*`", lambda b: b.startswith("/flowable")),
    ("webhook", "Webhook / Wait", lambda b: b.startswith("/webhook") or b.startswith("/waitresume")),
    ("tenant", "租户 `/tenant/*`", lambda b: b.startswith("/tenant")),
    ("monitor", "监控 `/monitor/*`", lambda b: b.startswith("/monitor")),
    ("tool", "工具 `/tool/*`", lambda b: b.startswith("/tool")),
    ("common", "通用 `/common/*`", lambda b: b.startswith("/common")),
    ("outreach", "外联 Outreach", lambda b: any(b.startswith(p) for p in ("/outreach", "/track", "/correspreview"))),
    ("other", "其它", lambda b: True),
]

STANDARD_CRUD = [
    ("GET", "/list"),
    ("GET", "/{id}"),
    ("POST", ""),
    ("PUT", ""),
    ("DELETE", "/{ids}"),
    ("POST", "/export"),
]


@dataclass
class Endpoint:
    http_method: str
    path: str
    summary: str = ""
    permission: str = ""
    anonymous: bool = False
    note: str = ""

    @property
    def full_path(self) -> str:
        return self.path


@dataclass
class ControllerInfo:
    name: str
    file_path: Path
    base_path: str
    is_rest: bool
    is_view_controller: bool
    endpoints: list[Endpoint] = field(default_factory=list)
    class_anonymous: bool = False

    @property
    def rel_file(self) -> str:
        return str(self.file_path.relative_to(REPO_ROOT)).replace("\\", "/")


def strip_html(s: str) -> str:
    s = re.sub(r"<[^>]+>", "", s)
    return " ".join(s.split())


def javadoc_summary(block: str) -> str:
    lines = []
    for line in block.splitlines():
        line = line.strip()
        if line.startswith("*"):
            line = line[1:].strip()
        if not line or line.startswith("@"):
            continue
        lines.append(line)
        if lines:
            break
    return strip_html(lines[0]) if lines else ""


def parse_mapping_args(args: str | None) -> tuple[str, str | None]:
    """Return (path, http_method or None for RequestMapping)."""
    if not args or not args.strip():
        return "", None
    path = ""
    method = None
    # value = "/x" or "/x"
    m = re.search(r'(?:value|path)\s*=\s*["\']([^"\']*)["\']', args)
    if m:
        path = m.group(1)
    else:
        m = re.search(r'["\']([^"\']*)["\']', args)
        if m:
            path = m.group(1)
    mm = re.search(r"method\s*=\s*\{?\s*RequestMethod\.(\w+)", args, re.I)
    if mm:
        method = mm.group(1).upper()
    elif re.search(r"method\s*=\s*RequestMethod\.(\w+)", args, re.I):
        method = re.search(r"method\s*=\s*RequestMethod\.(\w+)", args, re.I).group(1).upper()
    return path, method


def join_paths(base: str, sub: str) -> str:
    if not sub:
        return base or "/"
    if sub.startswith("/"):
        if not base:
            return sub
        return (base.rstrip("/") + sub) or "/"
    if not base:
        return "/" + sub
    return base.rstrip("/") + "/" + sub


def normalize_path(p: str) -> str:
    if not p:
        return "/"
    if not p.startswith("/"):
        p = "/" + p
    p = re.sub(r"/+", "/", p)
    return p


def parse_security_permit_paths() -> list[str]:
    paths = []
    if not SECURITY_CONFIG.exists():
        return paths
    text = SECURITY_CONFIG.read_text(encoding="utf-8", errors="replace")
    for m in re.finditer(r'\.antMatchers\((?:HttpMethod\.\w+,\s*)?([^)]+)\)\.permitAll', text):
        chunk = m.group(1)
        for s in re.findall(r'["\']([^"\']+)["\']', chunk):
            paths.append(s)
    return sorted(set(paths))


def scan_anonymous_endpoints(controllers: list[ControllerInfo]) -> list[str]:
    out = []
    for c in controllers:
        for ep in c.endpoints:
            if ep.anonymous or c.class_anonymous:
                out.append(f"{ep.http_method} {ep.path}")
    return sorted(set(out))


def is_standard_endpoint(ep: Endpoint, base: str) -> bool:
    rel = ep.path
    if base and rel.startswith(base):
        rel = rel[len(base) :] or "/"
    rel = normalize_path(rel if rel.startswith("/") else "/" + rel if rel else "/")
    method = ep.http_method.upper()
    for sm, sp in STANDARD_CRUD:
        if method != sm:
            continue
        if sp == "/list" and rel == "/list":
            return True
        if sp == "/export" and rel == "/export":
            return True
        if sp == "" and rel in ("/", base.rstrip("/") if base else "/"):
            return True
        if sp == "/{id}" and method == "GET" and re.fullmatch(r"/\{[^}]+\}", rel):
            return True
        if sp == "/{ids}" and method == "DELETE" and re.fullmatch(r"/\{[^}]+\}", rel):
            return True
    return False


def infer_perm_prefix(endpoints: list[Endpoint]) -> str | None:
    perms = [e.permission for e in endpoints if e.permission]
    if not perms:
        return None
    # crm:contact:list -> crm:contact
    parts = perms[0].rsplit(":", 1)
    return parts[0] if len(parts) == 2 else None


def topic_links_for_base(base: str) -> list[tuple[str, list[str]]]:
    hits = []
    for prefix, title, docs in TOPIC_LINKS:
        if base.startswith(prefix) or (prefix == "/webhook" and base.startswith("/webhook")):
            hits.append((title, docs))
    # dedupe
    seen = set()
    out = []
    for title, docs in hits:
        key = (title, tuple(docs))
        if key not in seen:
            seen.add(key)
            out.append((title, docs))
    return out


def class_body_start(text: str) -> int | None:
    """Return index after the opening `{` of the public class declaration."""
    m = re.search(r"public\s+class\s+\w+[^{]*\{", text)
    if not m:
        return None
    return m.end()


def parse_controller(path: Path) -> ControllerInfo | None:
    text = path.read_text(encoding="utf-8", errors="replace")
    cn = CLASS_NAME.search(text)
    if not cn:
        return None
    is_rest = bool(REST_CONTROLLER.search(text))
    is_view = bool(CONTROLLER_ANN.search(text)) and not is_rest
    base = ""
    header = text[: cn.start()]
    m = CLASS_REQUEST_MAPPING.search(header)
    if not m:
        m = CLASS_REQUEST_MAPPING.search(text[: class_body_start(text) or len(text)])
    if m:
        base = m.group(1)
    class_anon = bool(ANONYMOUS.search(header))

    body_start = class_body_start(text)
    if body_start is None:
        return None
    body = text[body_start:]

    endpoints: list[Endpoint] = []
    lines = text.splitlines()

    javadocs: list[tuple[int, str]] = []
    for jm in JAVADOC_BLOCK.finditer(text):
        line_no = text[: jm.end()].count("\n")
        javadocs.append((line_no, javadoc_summary(jm.group(1))))

    def line_number_at(pos: int) -> int:
        return text[:pos].count("\n")

    mapping_hits: list[tuple[re.Match[str], int]] = []
    for mm in MAPPING_ANN.finditer(body):
        mapping_hits.append((mm, body_start + mm.start()))

    for idx, (mm, abs_pos) in enumerate(mapping_hits):
        ann = mm.group("ann")
        args = mm.group("args")
        line_no = line_number_at(abs_pos)

        prev_end = mapping_hits[idx - 1][1] + len(mapping_hits[idx - 1][0].group(0)) if idx else body_start
        curr_start = abs_pos
        segment = text[prev_end:curr_start]
        perm_matches = list(PREAUTHORIZE.finditer(segment))
        perm = perm_matches[-1].group(1) if perm_matches else ""
        anon = bool(ANONYMOUS.search(segment)) or class_anon

        sub_path, req_method = parse_mapping_args(args)
        if ann == "Request":
            http = req_method or "GET"
            if req_method is None and "method" not in (args or ""):
                http = "*"
        else:
            http = ann.upper()
            if ann == "Patch":
                http = "PATCH"

        full = normalize_path(join_paths(base, sub_path))

        summary = ""
        for jl, js in reversed(javadocs):
            if jl < line_no:
                summary = js
                break

        note = ""
        if is_view and ann == "Get" and "redirect" in text[mm.end() : mm.end() + 200].lower():
            note = "页面跳转，非 JSON API"

        endpoints.append(
            Endpoint(
                http_method=http,
                path=full,
                summary=summary,
                permission=perm,
                anonymous=anon,
                note=note,
            )
        )

    return ControllerInfo(
        name=cn.group(1),
        file_path=path,
        base_path=normalize_path(base) if base else "",
        is_rest=is_rest,
        is_view_controller=is_view,
        endpoints=endpoints,
        class_anonymous=class_anon,
    )


def classify_module(base: str) -> str:
    for key, _, pred in MODULE_ORDER:
        if pred(base):
            return key
    return "other"


def build_preamble(
    permit_paths: list[str], generated_at: str, anon_endpoints: list[str]
) -> str:
    permit_table = "\n".join(f"| `{p}` | SecurityConfig `permitAll` |" for p in permit_paths)
    if anon_endpoints:
        anon_lines = "\n".join(f"- `{a}`" for a in anon_endpoints)
        anon_section = anon_lines
    else:
        anon_section = "_（无 `@Anonymous` 端点）_"

    return f"""# mega-ems API 接口参考

> **自动生成**：由 `scripts/generate_api_reference.py` 从 `*Controller.java` 提取。  
> 生成时间（UTC）：{generated_at}  
> 修改 Controller 后请重新运行：`python3 scripts/generate_api_reference.py`

本文档为**全量 HTTP 端点索引**；请求/响应字段级说明见 [docs/](.) 下各专题契约文档。

---

## 目录

1. [概述](#1-概述)
2. [通用约定](#2-通用约定)
3. [匿名与公开接口](#3-匿名与公开接口)
4. [标准 CRUD 模板](#4-标准-crud-模板)
5. [端点目录（按模块）](#5-端点目录按模块)
6. [专题契约索引](#6-专题契约索引)

---

## 1. 概述

| 项 | 说明 |
|----|------|
| 服务 | Spring Boot 入口 `ruoyi-admin`，默认端口 **9090** |
| 基址 | `http://{{host}}:9090` |
| 数据源 | 仓库内 `ruoyi-*/**/*Controller.java` |
| Swagger | `swagger.enabled=true` 时访问 `/swagger-ui.html`；仅含 `@ApiOperation` 方法，**不能**代表全量 API |
| 开发代理 | 前端 `swagger.pathMapping=/dev-api` 仅影响 Swagger UI 展示路径，**Controller 实际路径不含 `/dev-api` 前缀** |

---

## 2. 通用约定

### 2.1 请求头

| 头 | 说明 |
|----|------|
| `Authorization` | JWT Bearer（配置项 `token.header`，见 `ruoyi-admin/src/main/resources/application.yml`） |
| `tenant` | 多租户标识；`POST /login` **必填** |

### 2.2 响应格式

**单对象**（`AjaxResult`）：

```json
{{ "code": 200, "msg": "操作成功", "data": {{}} }}
```

**分页列表**（`TableDataInfo`）：

```json
{{ "code": 200, "msg": "查询成功", "rows": [], "total": 0 }}
```

常见分页查询参数：`pageNum`、`pageSize`（`BaseController.startPage()`）。

### 2.3 权限

- 控制器方法上的 `@PreAuthorize("@ss.hasPermi('module:resource:action')")` 在下方表格记为 **权限字**。
- 无 `@PreAuthorize`：若路径在 [§3](#3-匿名与公开接口) 中则为 **公开**；否则为 **需登录**（JWT）。
- 方法或类上的 `@Anonymous` 会在运行时注册到 `PermitAllUrlProperties`（路径变量会替换为 `*`）。

---

## 3. 匿名与公开接口

以下来自 `SecurityConfig` 的 `permitAll()` 规则（编译期静态摘录）：

| 路径模式 | 说明 |
|----------|------|
{permit_table}

另：`@Anonymous` 标注的端点见各 Controller 表格 **备注** 列。

### 3.1 `@Anonymous` 端点（静态扫描）

{anon_section}

---

## 4. 标准 CRUD 模板

若依代码生成器惯例（`BaseController` 资源类常见形态，`{{base}}` 为类级 `@RequestMapping` 路径）：

| HTTP | 路径 | 典型权限 |
|------|------|----------|
| GET | `{{base}}/list` | `{{permPrefix}}:list` |
| GET | `{{base}}/{{id}}` | `{{permPrefix}}:query` |
| POST | `{{base}}` | `{{permPrefix}}:add` |
| PUT | `{{base}}` | `{{permPrefix}}:edit` |
| DELETE | `{{base}}/{{ids}}` | `{{permPrefix}}:remove` |
| POST | `{{base}}/export` | `{{permPrefix}}:export` |

§5 中若某 Controller **仅含上述标准端点**，会折叠为「含标准 CRUD」；**非标准路径**仍逐条列出。

---

## 6. 专题契约索引

| 模块 | 文档 |
|------|------|
| 企业微信 `/crm/wecom/*` | [wecom-implemented-api-guide.md](./wecom-implemented-api-guide.md)、[wecom-backend-contract.md](./wecom-backend-contract.md) |
| Data Table `/crm/data-table/*` | [data-table-backend-contract.md](./data-table-backend-contract.md) |
| Webhook `/crm/webhook-trigger/*`、`/webhook/*` | [webhook-trigger-backend.md](./webhook-trigger-backend.md) |
| 语义层 `/crm/semanticprofile/*` | [semantic-layer-config-contract.md](./semantic-layer-config-contract.md) |
| 顺丰 `/crm/sf-express/*` | [sf-express-backend-contract.md](./sf-express-backend-contract.md) |
| Wait 节点 | [wait-node.md](./wait-node.md)、[wait-resume-inngest-integration.md](./wait-resume-inngest-integration.md) |

---

## 5. 端点目录（按模块）

"""


def format_endpoint_table(eps: list[Endpoint], permit_paths: list[str]) -> str:
    if not eps:
        return "_（无映射端点）_\n"

    def is_public(ep: Endpoint) -> bool:
        if ep.anonymous:
            return True
        for p in permit_paths:
            pat = "^" + re.escape(p).replace(r"\*", ".*") + "$"
            if re.match(pat, ep.path):
                return True
            if p.endswith("**") and ep.path.startswith(p[:-2]):
                return True
            if p.endswith("/**") and ep.path.startswith(p[:-3]):
                return True
        return False

    lines = [
        "| 方法 | 路径 | 说明 | 权限 | 备注 |",
        "|------|------|------|------|------|",
    ]
    for ep in eps:
        perm = ep.permission or ("公开" if is_public(ep) else "需登录")
        notes = []
        if ep.anonymous:
            notes.append("@Anonymous")
        if ep.note:
            notes.append(ep.note)
        note = "; ".join(notes) if notes else ""
        summary = ep.summary.replace("|", "\\|")
        lines.append(
            f"| {ep.http_method} | `{ep.path}` | {summary} | `{perm}` | {note} |"
        )
    return "\n".join(lines) + "\n"


def render_controller_section(ctrl: ControllerInfo, permit_paths: list[str]) -> str:
    base = ctrl.base_path or "(无类级前缀)"
    base_norm = ctrl.base_path

    standard = [ep for ep in ctrl.endpoints if is_standard_endpoint(ep, base_norm)]
    non_standard = [ep for ep in ctrl.endpoints if not is_standard_endpoint(ep, base_norm)]

    links = topic_links_for_base(base_norm)
    link_line = ""
    if links:
        parts = []
        for title, docs in links:
            doc_links = ", ".join(f"[{d}](./{d})" for d in docs)
            parts.append(f"**{title}**：{doc_links}")
        link_line = "> 详见专题契约：" + "；".join(parts) + "\n\n"

    perm_prefix = infer_perm_prefix(ctrl.endpoints)
    crud_note = ""
    if standard and not non_standard:
        pp = perm_prefix or "{permPrefix}"
        crud_note = (
            f"含标准 CRUD（见 [§4](#4-标准-crud-模板)），典型权限前缀 `{pp}`："
            f"`list` / `query` / `add` / `edit` / `remove` / `export`。\n\n"
        )
    elif standard and non_standard:
        crud_note = (
            f"另含标准 CRUD（见 [§4](#4-标准-crud-模板)），共 {len(standard)} 个常规端点未在下表重复列出。\n\n"
        )

    if ctrl.is_view_controller and not ctrl.is_rest:
        crud_note += "_`@Controller` 视图控制器，下列可能含非 JSON 端点。_\n\n"

    body = ""
    if non_standard or (not standard and ctrl.endpoints):
        body = format_endpoint_table(non_standard or ctrl.endpoints, permit_paths)
    elif not ctrl.endpoints:
        body = "_（无映射端点）_\n"

    return (
        f"### {ctrl.name}\n\n"
        f"- **基础路径**：`{base_norm or '/'}`\n"
        f"- **源文件**：`{ctrl.rel_file}`\n\n"
        f"{link_line}"
        f"{crud_note}"
        f"{body}"
    )


def main() -> None:
    controllers: list[ControllerInfo] = []
    for mod in sorted(REPO_ROOT.glob("ruoyi-*/src/main/java")):
        for path in mod.rglob("*Controller.java"):
            info = parse_controller(path)
            if info and (info.endpoints or info.is_view_controller):
                controllers.append(info)

    permit_paths = parse_security_permit_paths()
    generated_at = datetime.now(timezone.utc).strftime("%Y-%m-%d %H:%M:%S")

    # Group by module
    groups: dict[str, list[ControllerInfo]] = {k: [] for k, _, _ in MODULE_ORDER}
    for c in controllers:
        key = classify_module(c.base_path)
        groups.setdefault(key, []).append(c)

    for key in groups:
        groups[key].sort(key=lambda x: (x.base_path, x.name))

    anon_extra = scan_anonymous_endpoints(controllers)
    parts = [build_preamble(permit_paths, generated_at, anon_extra)]

    total_eps = sum(len(c.endpoints) for c in controllers)
    parts.append(f"\n**统计**：{len(controllers)} 个 Controller，{total_eps} 个端点映射。\n\n")

    for key, title, _ in MODULE_ORDER:
        ctrls = groups.get(key, [])
        if not ctrls:
            continue
        parts.append(f"### 模块：{title}\n\n")
        for ctrl in ctrls:
            parts.append(render_controller_section(ctrl, permit_paths))

    OUTPUT.parent.mkdir(parents=True, exist_ok=True)
    OUTPUT.write_text("".join(parts), encoding="utf-8")
    print(f"Wrote {OUTPUT} ({len(controllers)} controllers, {total_eps} endpoints)")


if __name__ == "__main__":
    main()
