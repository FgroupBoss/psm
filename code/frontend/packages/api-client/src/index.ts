import type { ApiResponse, DemoInfo } from '@psm/domain-types';

export async function fetchDemoInfo(): Promise<DemoInfo> {
  const response = await fetch('/api/demo');
  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`);
  }
  const body = (await response.json()) as ApiResponse<DemoInfo>;
  if (body.code !== 0) {
    throw new Error(body.message);
  }
  return body.data;
}

