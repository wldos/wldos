/** 与后端 {@code Constants.DISPLAY_ORDER_MAX} 保持一致 */
export const DISPLAY_ORDER_MAX = 9999;

/** 1 ~ DISPLAY_ORDER_MAX，不允许前导 0 */
export const DISPLAY_ORDER_PATTERN = /^([1-9]\d{0,3})$/;

/** ProTable 等字符串 pattern 规则用 */
export const DISPLAY_ORDER_PATTERN_STRING = '^([1-9]\\d{0,3})$';
