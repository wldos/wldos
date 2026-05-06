/**
 * CMS 书本单篇「同步到本站 / 提交审核」：与本站 pubStatus、章节保存链路相关。
 * 与 commercial 内容发布助手（第三方自媒体任务）无关。
 */

/**
 * 将待发布内容置为待审核并触发表单提交保存（若当前已是审核态则不再重复提交）。
 *
 * @param {import('antd').FormInstance} form
 * @param {{ current: any }} timerIdRef doSave 节流定时器，与 BookView 一致传入
 * @returns {void}
 */
export function submitCmsReviewFromBookEditor(form, timerIdRef) {
  const status = form.getFieldValue('pubStatus') ?? '';
  if (status !== 'in_review' && status !== 'inherit') {
    form.setFieldsValue({ pubStatus: 'in_review' });
    form.submit();
    if (timerIdRef?.current) {
      clearTimeout(timerIdRef.current);
    }
  }
}

/**
 * Web 浏览器内「申请发布」：提示已申请/已发布后不再提交；否则写入 in_review 并 form.submit() → onFinish(doSave) → saveChapter API。
 * 与桌面壳「发布中心 / 助手」链路区分使用。
 *
 * @param {import('antd').FormInstance} form
 * @param {{ current: any }} timerIdRef
 * @param {{ warning: Function, info: Function }} messageApi antd message
 */
export function applyWebBookPublishRequest(form, timerIdRef, messageApi) {
  const status = form.getFieldValue('pubStatus') ?? '';
  if (status === 'in_review') {
    messageApi.warning('已申请，请等待');
    return;
  }
  if (status === 'inherit') {
    messageApi.info('已发布，请不要频繁修改');
    return;
  }
  form.setFieldsValue({ pubStatus: 'in_review' });
  form.submit();
  if (timerIdRef?.current) {
    clearTimeout(timerIdRef.current);
  }
}
