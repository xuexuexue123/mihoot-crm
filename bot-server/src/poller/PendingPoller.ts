import type { PendingResponse, TenantBotCommand } from '../types.js';

const POLL_INTERVAL_MS = 5000;

export class PendingPoller {
  private backendUrl: string;
  private internalSecret: string;
  private timer: ReturnType<typeof setInterval> | null = null;
  private knownTenants = new Map<string, { status: string; crmToken: string }>();
  private onCommand: (cmd: TenantBotCommand) => void;

  constructor(
    backendUrl: string,
    internalSecret: string,
    onCommand: (cmd: TenantBotCommand) => void
  ) {
    this.backendUrl = backendUrl.replace(/\/$/, '');
    this.internalSecret = internalSecret;
    this.onCommand = onCommand;
  }

  start(): void {
    console.log('[PendingPoller] Starting poll loop (interval: 5s)');
    this.poll();
    this.timer = setInterval(() => this.poll(), POLL_INTERVAL_MS);
  }

  stop(): void {
    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
    }
    console.log('[PendingPoller] Stopped');
  }

  private async poll(): Promise<void> {
    try {
      const url = `${this.backendUrl}/crm/wecom/aibot/pending`;
      const response = await fetch(url, {
        headers: { 'X-Internal-Secret': this.internalSecret },
      });

      if (!response.ok) {
        console.error(`[PendingPoller] HTTP ${response.status} from /pending`);
        return;
      }

      const json: PendingResponse = await response.json();
      if (json.code !== 200 || !Array.isArray(json.data)) {
        return;
      }

      for (const cmd of json.data) {
        this.handleCommand(cmd);
      }
    } catch (err) {
      console.error('[PendingPoller] Poll error:', err);
    }
  }

  private handleCommand(cmd: TenantBotCommand): void {
    const prev = this.knownTenants.get(cmd.tenant);

    // 状态和 token 都没变 → 跳过
    if (prev && prev.status === cmd.status && prev.crmToken === cmd.crmToken) {
      return;
    }

    this.knownTenants.set(cmd.tenant, { status: cmd.status, crmToken: cmd.crmToken });

    if (cmd.status === 'connecting' || cmd.status === 'reconnecting') {
      this.onCommand(cmd);
    } else if (cmd.status === 'disconnecting') {
      this.onCommand(cmd);
    } else {
      // 状态未变但 crmToken 已刷新 → 仅通知 token 更新
      if (prev && prev.crmToken !== cmd.crmToken) {
        this.onCommand(cmd);
      }
    }
  }
}
