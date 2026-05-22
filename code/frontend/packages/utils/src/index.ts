export function maskMobile(mobile: string): string {
  if (mobile.length < 7) {
    return mobile;
  }
  return `${mobile.slice(0, 3)}****${mobile.slice(-4)}`;
}

