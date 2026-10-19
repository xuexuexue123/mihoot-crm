import type {
  ConnectionStatus,
  TenantBotCommand,
  StatusReport,
  ResolvedCrmUser,
  ResolveUserResponse,
} from '../types.js';

export interface AibotClientCallbacks {
  onStateChange: (report: StatusReport) => void;
}

/**
 * 构建以真实用户身份调用 CRM API 所需的请求头。
 * 与 Java 端 WecomAiBotActingFilter 配合使用。
 */
export function buildActingHeaders(
  resolvedUser: ResolvedCrmUser,
  tenant: string,
  crmToken: string,
  internalSecret: string
): Record<string, string> {
  return {
    Authorization: `Bearer ${crmToken}`,
    tenant,
    'X-Acting-User-Id': String(resolvedUser.userId),
    'X-Acting-User-Name': resolvedUser.nickName || resolvedUser.userName || 'unknown',
    'X-Internal-Secret': internalSecret,
  };
}

/**
 * Wraps @wecom/aibot-node-sdk WebSocket client.
 * Manages the lifecycle of a single tenant's WebSocket connection.
 */
export class AibotClient {
  private tenant: string;
  private botId: string;
  private botSecret: string;
  private crmToken: string;
  private backendUrl: string;
  private internalSecret: string;
  private status: ConnectionStatus = 'disconnected';
  private wsClient: unknown = null;
  private callbacks: AibotClientCallbacks;
  private errorCount = 0;
  /** 当前消息对应的已解析 CRM 用户 */
  private resolvedCrmUser: ResolvedCrmUser | null = null;

  constructor(
    cmd: TenantBotCommand,
    backendUrl: string,
    internalSecret: string,
    callbacks: AibotClientCallbacks
  ) {
    this.tenant = cmd.tenant;
    this.botId = cmd.botId;
    this.botSecret = cmd.botSecret;
    this.crmToken = cmd.crmToken;
    this.backendUrl = backendUrl.replace(/\/$/, '');
    this.internalSecret = internalSecret;
    this.callbacks = callbacks;
  }

  get tenantId(): string {
    return this.tenant;
  }

  get currentStatus(): ConnectionStatus {
    return this.status;
  }

  /** 获取当前解析的 CRM 用户信息，供调用方在 CRM API 请求中注入 acting headers */
  get actingCrmUser(): ResolvedCrmUser | null {
    return this.resolvedCrmUser;
  }

  /** 更新 CRM Token（由 PendingPoller 每次轮询时刷新） */
  updateCrmToken(newToken: string): void {
    if (this.crmToken !== newToken) {
      this.crmToken = newToken;
      console.log(
        `[AibotClient:${this.tenant}] crmToken refreshed`
      );
    }
  }

  /** 获取以真实用户身份调用 CRM API 所需的请求头 */
  get actingHeaders(): Record<string, string> | null {
    if (!this.resolvedCrmUser) return null;
    return buildActingHeaders(
      this.resolvedCrmUser,
      this.tenant,
      this.crmToken,
      this.internalSecret
    );
  }

  /**
   * 根据企业微信 userid 解析对应的 CRM 用户。
   * 调用 /crm/wecom/aibot/resolve-user 内部接口。
   */
  async resolveUser(wecomUserId: string): Promise<ResolvedCrmUser | null> {
    try {
      const url = `${this.backendUrl}/crm/wecom/aibot/resolve-user?userid=${encodeURIComponent(wecomUserId)}&tenant=${encodeURIComponent(this.tenant)}`;
      const response = await fetch(url, {
        headers: { 'X-Internal-Secret': this.internalSecret },
      });

      if (!response.ok) {
        console.error(
          `[AibotClient:${this.tenant}] resolve-user HTTP ${response.status}`
        );
        return null;
      }

      const json: ResolveUserResponse = await response.json();
      if (json.code !== 200 || !json.data) {
        console.error(
          `[AibotClient:${this.tenant}] resolve-user failed: ${json.msg}`
        );
        return null;
      }

      this.resolvedCrmUser = json.data;
      console.log(
        `[AibotClient:${this.tenant}] Resolved user: userId=${json.data.userId}, name=${json.data.nickName || json.data.userName}`
      );
      return json.data;
    } catch (err) {
      console.error(`[AibotClient:${this.tenant}] resolve-user error:`, err);
      return null;
    }
  }

  async connect(): Promise<void> {
    if (this.status === 'connected' || this.status === 'connecting') {
      console.log(`[AibotClient:${this.tenant}] Already connected/connecting, skipping`);
      return;
    }

    console.log(`[AibotClient:${this.tenant}] Starting WebSocket connection...`);

    this.updateStatus('connecting');

    try {
      // @wecom/aibot-node-sdk: the actual SDK API is unknown.
      // When the real SDK is available, replace this placeholder.
      //
      // Expected usage:
      //   import { WSClient } from '@wecom/aibot-node-sdk';
      //   this.wsClient = new WSClient({ botId: this.botId, botSecret: this.botSecret });
      //   this.wsClient.on('open', () => this.updateStatus('connected'));
      //   this.wsClient.on('close', () => this.updateStatus('disconnected'));
      //   this.wsClient.on('error', (err) => this.handleError(err));
      //   this.wsClient.on('message', (msg) => this.handleMessage(msg));
      //   this.wsClient.connect();

      this.updateStatus('connected');
      console.log(`[AibotClient:${this.tenant}] Connection established (placeholder)`);
    } catch (err) {
      this.handleError(err);
    }
  }

  async disconnect(): Promise<void> {
    console.log(`[AibotClient:${this.tenant}] Disconnecting...`);

    try {
      // this.wsClient?.close();
      this.wsClient = null;
      this.resolvedCrmUser = null;
      this.updateStatus('disconnected');
      console.log(`[AibotClient:${this.tenant}] Disconnected`);
    } catch (err) {
      console.error(`[AibotClient:${this.tenant}] Error during disconnect:`, err);
      this.updateStatus('error');
    }
  }

  private handleMessage(_message: unknown): void {
    // When the real WebSocket SDK is wired up:
    // 1. Extract frame.body.from.userid from the message
    // 2. Call this.resolveUser(userid) to get CRM user info
    // 3. When calling CRM APIs, use this.actingHeaders to set acting user headers
    //
    // Example:
    //   const wecomUserId = frame.body.from.userid;
    //   const crmUser = await this.resolveUser(wecomUserId);
    //   if (crmUser) {
    //     const headers = this.actingHeaders;
    //     // ... call CRM API with headers
    //   }

    this.callbacks.onStateChange({
      tenant: this.tenant,
      status: this.status as 'connected',
      lastMessageAt: new Date().toISOString(),
      errorCount: this.errorCount,
    });
  }

  private handleError(err: unknown): void {
    this.errorCount++;
    const errorMessage = err instanceof Error ? err.message : String(err);
    console.error(`[AibotClient:${this.tenant}] Error:`, errorMessage);

    this.callbacks.onStateChange({
      tenant: this.tenant,
      status: 'error',
      errorMessage,
      errorCount: this.errorCount,
    });
  }

  private updateStatus(newStatus: ConnectionStatus): void {
    const prevStatus = this.status;
    this.status = newStatus;

    if (prevStatus !== newStatus) {
      this.callbacks.onStateChange({
        tenant: this.tenant,
        status: newStatus as 'connected' | 'disconnected' | 'reconnecting' | 'error',
        errorCount: this.errorCount,
      });
    }
  }
}
