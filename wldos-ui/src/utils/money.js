/**
 * 金额展示（默认人民币）
 */
export function formatMoney(amount, currency = 'CNY') {
  if (amount == null || amount === '') {
    return '—';
  }
  const n = Number(amount);
  if (Number.isNaN(n)) {
    return String(amount);
  }
  try {
    return new Intl.NumberFormat('zh-CN', { style: 'currency', currency }).format(n);
  } catch (e) {
    return `¥${n}`;
  }
}
