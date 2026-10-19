import type { StatusReport } from '../types.js';

export class StatusReporter {
  private backendUrl: string;
  private internalSecret: string;

  constructor(backendUrl: string, internalSecret: string) {
    this.backendUrl = backendUrl.replace(/\/$/, '');
    this.internalSecret = internalSecret;
  }

  async report(report: StatusReport): Promise<void> {
    try {
      const url = `${this.backendUrl}/crm/wecom/aibot/status`;
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'X-Internal-Secret': this.internalSecret,
        },
        body: JSON.stringify(report),
      });

      if (!response.ok) {
        console.error(
          `[StatusReporter] HTTP ${response.status} reporting status for ${report.tenant}`
        );
      } else {
        console.log(
          `[StatusReporter] Reported status for ${report.tenant}: ${report.status}`
        );
      }
    } catch (err) {
      console.error(
        `[StatusReporter] Error reporting status for ${report.tenant}:`,
        err
      );
    }
  }
}
