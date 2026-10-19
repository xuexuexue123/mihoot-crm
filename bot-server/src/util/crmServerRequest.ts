import type { ResolvedCrmUser } from '../types.js';

export interface CrmRequestContext {
  backendUrl: string;
  tenant: string;
  crmToken: string;
  internalSecret: string;
  actingUser?: ResolvedCrmUser | null;
}

export interface CrmApiResponse<T = unknown> {
  code: number;
  msg: string;
  data: T;
}

/**
 * 构建以真实用户身份调用 CRM API 所需的请求头。
 * 与 Java 端 WecomAiBotActingFilter 配合使用。
 *
 * 必带头：Authorization、tenant
 * 当 actingUser 存在时追加：X-Acting-User-Id、X-Acting-User-Name、X-Internal-Secret
 */
export function buildCrmHeaders(ctx: CrmRequestContext): Record<string, string> {
  const headers: Record<string, string> = {
    Authorization: `Bearer ${ctx.crmToken}`,
    tenant: ctx.tenant,
    'Content-Type': 'application/json;charset=UTF-8',
  };

  if (ctx.actingUser) {
    headers['X-Acting-User-Id'] = String(ctx.actingUser.userId);
    headers['X-Acting-User-Name'] =
      ctx.actingUser.nickName || ctx.actingUser.userName || 'unknown';
    headers['X-Internal-Secret'] = ctx.internalSecret;
  }

  return headers;
}

/**
 * 通用的 CRM API 请求函数。
 * 自动注入认证头和 acting user 头，供 bot-server 的 CRM tools 使用。
 *
 * @param ctx - 请求上下文（包含认证信息和 acting user）
 * @param method - HTTP 方法
 * @param path - API 路径，如 '/crm/activity/list'
 * @param body - 请求体，仅 POST/PUT 时拼接
 */
export async function crmServerRequest<T = unknown>(
  ctx: CrmRequestContext,
  method: 'GET' | 'POST' | 'PUT' | 'DELETE',
  path: string,
  body?: Record<string, unknown>
): Promise<CrmApiResponse<T>> {
  const url = `${ctx.backendUrl.replace(/\/$/, '')}${path}`;
  const headers = buildCrmHeaders(ctx);

  const init: RequestInit = {
    method,
    headers,
  };

  if (body && (method === 'POST' || method === 'PUT')) {
    init.body = JSON.stringify(body);
  }

  const response = await fetch(url, init);
  const result: CrmApiResponse<T> = await response.json();

  // 检测 401 并输出详细诊断信息
  if (result.code === 401 || response.status === 401) {
    console.error(
      `[crmServerRequest] 401 Unauthorized for ${method} ${path}` +
        ` (HTTP ${response.status}, body code=${result.code})` +
        ` | tenant=${ctx.tenant}` +
        ` | actingUser=${ctx.actingUser ? ctx.actingUser.userId : 'none'}` +
        ` | crmToken=${ctx.crmToken ? ctx.crmToken.substring(0, 16) + '...' : 'MISSING'}` +
        ` | msg=${result.msg}`
    );
  }

  return result;
