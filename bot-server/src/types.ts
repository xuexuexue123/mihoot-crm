export interface TenantBotCommand {
  tenant: string;
  botId: string;
  botSecret: string;
  crmToken: string;
  status: 'connecting' | 'disconnecting' | 'reconnecting';
}

export interface StatusReport {
  tenant: string;
  status: 'connected' | 'disconnected' | 'reconnecting' | 'error';
  lastMessageAt?: string;
  errorMessage?: string;
  errorCount: number;
}

export interface PendingResponse {
  code: number;
  data: TenantBotCommand[];
}

/** 企业微信 userid → CRM 用户解析结果 */
export interface ResolvedCrmUser {
  userId: number;
  userName: string;
  nickName: string;
  deptId: number;
  avatar: string;
}

/** /resolve-user 接口响应 */
export interface ResolveUserResponse {
  code: number;
  msg: string;
  data: ResolvedCrmUser | null;
}

export type ConnectionStatus =
  | 'disconnected'
  | 'connecting'
  | 'connected'
  | 'disconnecting'
  | 'reconnecting'
  | 'error';
