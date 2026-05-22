export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export interface DemoInfo {
  service: string;
  module: string;
  version: string;
}

