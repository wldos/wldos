import React, { useEffect, useState } from 'react';
import { Card, Form, Checkbox, Button, Input, message, Alert } from 'antd';
import { history, useModel } from 'umi';
import { PageContainer } from '@ant-design/pro-layout';
import { createOrder, getStoreProductDetail, validateReferralCode } from './service';
import { getAgreementListByType } from '@/pages/user/login/agreement';
import {getReferralEligible} from "@/pages/commercial/order/service";

const Checkout = () => {
  const { initialState } = useModel('@@initialState');
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [agreements, setAgreements] = useState([]);
  const [product, setProduct] = useState(null);
  const [referralValid, setReferralValid] = useState(null); // null=未填, true/false=校验结果
  const [eligibleForDiscount, setEligibleForDiscount] = useState(true); // 是否新用户可享推荐优惠

  const query = history.location.query || {};
  const productId = query.productId;
  const productCode = query.productCode;
  const amount = parseFloat(query.amount) || 0;
  const displayId = query.displayId;
  const referralCodeFromQuery = query.referralCode ? decodeURIComponent(query.referralCode) : '';

  useEffect(() => {
    if (!initialState?.currentUser) {
      message.warning('请先登录');
      history.push(`/user/login?redirect=${encodeURIComponent(history.location.pathname + history.location.search)}`);
      return;
    }
    if (!productId || !productCode) {
      message.error('缺少商品信息');
      return;
    }
    // 获取 ORDER 类型协议列表（11.2.3：多条如服务协议/支付条款/退款政策/推荐营销规则）
    getAgreementListByType('ORDER')
      .then((rows) => setAgreements(Array.isArray(rows) ? rows : []))
      .catch(() => setAgreements([]));
    // 获取产品详情（可选）
    if (displayId) {
      getStoreProductDetail(displayId)
        .then((res) => setProduct(res?.data))
        .catch(() => {});
    }
    if (referralCodeFromQuery) {
      form.setFieldsValue({ referralCode: referralCodeFromQuery });
      setReferralValid(null);
    }
    getReferralEligible()
      .then((res) => {
        const d = res?.data?.data != null ? res.data.data : res?.data;
        if (d && typeof d.eligibleForDiscount === 'boolean') setEligibleForDiscount(d.eligibleForDiscount);
      })
      .catch(() => {});
  }, [productId, productCode, displayId, initialState?.currentUser, referralCodeFromQuery]);

  const handleReferralBlur = async () => {
    const code = form.getFieldValue('referralCode');
    if (!code || !code.trim()) {
      setReferralValid(null);
      return;
    }
    try {
      const res = await validateReferralCode(code.trim());
      const data = res?.data?.data != null ? res.data.data : res?.data;
      setReferralValid(data?.valid === true);
      if (data?.valid !== true && data?.message) {
        message.warning(data.message);
      }
    } catch (e) {
      setReferralValid(false);
      message.warning(e?.data?.message || e?.message || '推荐码校验失败');
    }
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      const agreementIds = values.agreementIds || [];
      if (agreements.length > 0 && agreementIds.length === 0) {
        message.error('请勾选并同意相关协议');
        return;
      }
      const referralCode = values.referralCode && values.referralCode.trim ? values.referralCode.trim() : '';
      let referrerUserId;
      let discountAmount = 0;
      if (referralCode) {
        const resValidate = await validateReferralCode(referralCode);
        const data = resValidate?.data?.data != null ? resValidate.data.data : resValidate?.data;
        if (data?.valid !== true) {
          message.error(data?.message || '推荐码无效，请修正后再提交');
          return;
        }
        referrerUserId = data?.referrerUserId;
        discountAmount = data?.discountAmount != null ? Number(data.discountAmount) : 0;
      }
      setLoading(true);
      const payload = {
        productType: 'PLUGIN',
        productId: Number(productId),
        productCode,
        productVersion: product?.version || '',
        amount,
        agreementIds: agreementIds || [],
      };
      if (referralCode) {
        payload.referralCode = referralCode;
        if (referrerUserId) payload.referrerUserId = referrerUserId;
        if (discountAmount > 0) payload.discountAmount = discountAmount;
      }
      const res = await createOrder(payload);
      const data = res?.data;
      if (data?.payUrl) {
        window.location.href = data.payUrl;
      } else {
        message.success('订单创建成功');
        history.push(`/order/${data?.orderNo}`);
      }
    } catch (e) {
      message.error(e?.data?.message || e?.message || '创建订单失败');
    } finally {
      setLoading(false);
    }
  };

  const productName = product?.displayTitle || product?.pluginName || productCode;

  return (
    <PageContainer title="结算" content="确认订单信息并完成支付">
      <Card>
        <Form form={form} layout="vertical" initialValues={{ agreementConsent: false, agreementIds: [], referralCode: referralCodeFromQuery || undefined }}>
          <Form.Item label="商品信息">
            <div>
              <strong>{productName}</strong>
              <span style={{ marginLeft: 16, color: '#ff4d4f' }}>¥{amount}</span>
            </div>
          </Form.Item>
          <Form.Item
            name="referralCode"
            label="推荐码（选填）"
            extra="输入推荐码享优惠，未输入则不享受"
          >
            <Input
              placeholder="输入推荐码享优惠，未输入则不享受"
              allowClear
              onBlur={handleReferralBlur}
            />
          </Form.Item>
          {referralValid === true && !eligibleForDiscount && (
            <Alert message="您已非新用户，不享受推荐优惠，推荐关系仍会记录" type="info" showIcon style={{ marginBottom: 16 }} />
          )}
          {agreements.length > 0 && (
            <Form.Item
              name="agreementIds"
              label="服务协议"
              rules={[{ required: true, message: '请勾选协议', type: 'array', min: 1 }]}
            >
              <Checkbox.Group>
                {agreements.map((a) => (
                  <div key={a.id} style={{ marginBottom: 8 }}>
                    <Checkbox value={a.id}>
                      《{a.title}》
                    </Checkbox>
                  </div>
                ))}
              </Checkbox.Group>
            </Form.Item>
          )}
          <Form.Item>
            <Button type="primary" size="large" loading={loading} onClick={handleSubmit}>
              提交订单
            </Button>
            <Button style={{ marginLeft: 8 }} onClick={() => history.goBack()}>
              返回
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </PageContainer>
  );
};

export default Checkout;
