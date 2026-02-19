/*
 * 试用申请页（方案 4.1、4.2、11.2.4）
 * 表单：用户名、联系方式、企业名称（可选）、勾选同意服务协议；提交后展示试用 Key 与 4.2 文案；协议勾选传 agreementIds
 */
import React, { useState, useEffect } from 'react';
import { Card, Form, Input, Checkbox, Button, Alert, Result, Spin } from 'antd';
import { ArrowLeftOutlined, SafetyOutlined } from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-layout';
import { Link, history } from 'umi';
import { applyTrial } from './service';
import { getAgreementListByType } from '@/pages/user/login/agreement';

const TrialApply = () => {
  const [form] = Form.useForm();
  const [agreements, setAgreements] = useState([]);
  const [submitting, setSubmitting] = useState(false);
  const [success, setSuccess] = useState(false);
  const [resultData, setResultData] = useState(null);
  const [errorMsg, setErrorMsg] = useState('');

  useEffect(() => {
    getAgreementListByType('ORDER')
      .then((rows) => setAgreements(Array.isArray(rows) ? rows : []))
      .catch(() => setAgreements([]));
  }, []);

  const productId = history.location.query?.productId
    ? Number(history.location.query.productId)
    : undefined;

  const onFinish = async (values) => {
    const agreed = values.agreedProtocol === true || (values.agreementIds && values.agreementIds.length > 0);
    if (!agreed) {
      setErrorMsg('请先阅读并同意服务协议');
      return;
    }
    setSubmitting(true);
    setErrorMsg('');
    try {
      const res = await applyTrial({
        productId: productId || values.productId,
        contactName: values.contactName,
        contactInfo: values.contactInfo,
        companyName: values.companyName,
        agreedProtocol: true,
        agreementIds: values.agreementIds || [],
      });
      const data = res?.data?.data != null ? res.data.data : res?.data;
      setResultData(data);
      setSuccess(true);
    } catch (e) {
      const msg = e?.data?.message || e?.message || '申请失败，请稍后重试';
      setErrorMsg(msg);
    } finally {
      setSubmitting(false);
    }
  };

  if (success && resultData) {
    return (
      <PageContainer title="试用申请结果" onBack={() => setSuccess(false)}>
        <Result
          status="success"
          title="试用申请已提交"
          subTitle="您的试用 Key 已生成，请妥善保存"
          extra={[
            <Card key="key" size="small" title="试用 Key" style={{ marginBottom: 16, textAlign: 'left' }}>
              <pre style={{ margin: 0, fontSize: 14, wordBreak: 'break-all' }}>{resultData.licenseKey}</pre>
              {resultData.expireTime && (
                <p style={{ marginTop: 8, color: '#666', fontSize: 12 }}>
                  有效期至：{resultData.expireTime}
                </p>
              )}
            </Card>,
            <Alert
              key="tip"
              message="注册登录后可获取个人推荐码，推荐新用户下单享佣金"
              description="您可在产品中心或个人中心查看推荐优惠说明，获取您的推荐码与推荐链接。"
              type="info"
              showIcon
              style={{ marginBottom: 24 }}
            />,
            <Link key="back" to="/product"><Button type="primary">返回产品中心</Button></Link>,
            <Link key="referral" to="/account/referral" style={{ marginLeft: 8 }}><Button>去获取推荐码</Button></Link>,
          ]}
        />
      </PageContainer>
    );
  }

  return (
    <PageContainer title="试用申请" content="填写信息并同意服务协议后提交，即可获取试用 Key">
      <Card>
        <Alert
          message="请阅读并同意《服务协议》后再提交"
          description={<Link to="/agreement" target="_blank" rel="noopener noreferrer">《wldos 服务协议》</Link>}
          type="info"
          showIcon
          icon={<SafetyOutlined />}
          style={{ marginBottom: 24 }}
        />
        <Form
          form={form}
          layout="vertical"
          onFinish={onFinish}
          initialValues={{ agreedProtocol: false, agreementIds: [], productId }}
        >
          {productId && (
            <Form.Item name="productId" hidden>
              <input type="hidden" />
            </Form.Item>
          )}
          {!productId && (
            <Form.Item
              name="productId"
              label="产品ID"
              rules={[{ required: true, message: '请填写产品ID（可从产品详情页通过试用入口进入以自动带入）' }]}
            >
              <Input type="number" placeholder="产品ID" />
            </Form.Item>
          )}
          <Form.Item
            name="contactName"
            label="联系人姓名"
          >
            <Input placeholder="您的姓名" />
          </Form.Item>
          <Form.Item
            name="contactInfo"
            label="联系方式"
            rules={[{ required: true, message: '请填写手机或邮箱' }]}
          >
            <Input placeholder="手机号或邮箱" />
          </Form.Item>
          <Form.Item
            name="companyName"
            label="企业名称（选填）"
          >
            <Input placeholder="选填" />
          </Form.Item>
          {agreements.length > 0 ? (
            <Form.Item
              name="agreementIds"
              label="服务协议"
              rules={[{ required: true, message: '请先确认已阅读服务协议', type: 'array', min: 1 }]}
            >
              <Checkbox.Group>
                {agreements.map((a) => (
                  <div key={a.id} style={{ marginBottom: 8 }}>
                    <Checkbox value={a.id}>
                      《{a.title || a.agreementCode || '协议'}》
                    </Checkbox>
                  </div>
                ))}
              </Checkbox.Group>
            </Form.Item>
          ) : (
            <Form.Item
              name="agreedProtocol"
              valuePropName="checked"
              rules={[{ required: true, message: '请先同意服务协议' }]}
            >
              <Checkbox>我已阅读并同意<Link to="/agreement" target="_blank" rel="noopener noreferrer">《wldos 服务协议》</Link></Checkbox>
            </Form.Item>
          )}
          {errorMsg && (
            <Alert message={errorMsg} type="error" showIcon style={{ marginBottom: 16 }} />
          )}
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={submitting}>
              提交申请
            </Button>
            <Link to="/product" style={{ marginLeft: 8 }}><Button>返回产品中心</Button></Link>
          </Form.Item>
        </Form>
      </Card>
    </PageContainer>
  );
};

export default TrialApply;
