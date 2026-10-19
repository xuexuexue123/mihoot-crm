import { PendingPoller } from './poller/PendingPoller.js';
import { StatusReporter } from './reporter/StatusReporter.js';
import { AibotClient } from './client/AibotClient.js';
import type { TenantBotCommand, StatusReport } from './types.js';

const BACKEND_URL = process.env.ELEVEN_LABS_BACKEND_URL || 'http://localhost:9090';
const INTERNAL_SECRET = process.env.INTERNAL_SERVICE_SECRET || 'default-internal-secret-change-me';

console.log('[bot-server] Starting...');
console.log(`[bot-server] Backend URL: ${BACKEND_URL}`);
console.log(`[bot-server] Internal secret: ${'*'.repeat(Math.min(INTERNAL_SECRET.length, 8))}`);

const clients = new Map<string, AibotClient>();
const reporter = new StatusReporter(BACKEND_URL, INTERNAL_SECRET);

function onStateChange(report: StatusReport): void {
  reporter.report(report);
}

function handleCommand(cmd: TenantBotCommand): void {
  const existing = clients.get(cmd.tenant);

  if (cmd.status === 'connecting' || cmd.status === 'reconnecting') {
    if (existing) {
      // 已有连接 → 仅刷新 crmToken，不重建 WebSocket
      existing.updateCrmToken(cmd.crmToken);
      return;
    }

    const client = new AibotClient(cmd, BACKEND_URL, INTERNAL_SECRET, { onStateChange });
    clients.set(cmd.tenant, client);
    client.connect();
  } else if (cmd.status === 'disconnecting') {
    if (existing) {
      existing.disconnect().then(() => {
        clients.delete(cmd.tenant);
      });
    }
  }
}

const poller = new PendingPoller(BACKEND_URL, INTERNAL_SECRET, handleCommand);
poller.start();

// Graceful shutdown
function shutdown(): void {
  console.log('[bot-server] Shutting down...');
  poller.stop();

  for (const [tenant, client] of clients) {
    client.disconnect();
  }

  clients.clear();
  process.exit(0);
}

process.on('SIGINT', shutdown);
process.on('SIGTERM', shutdown);
