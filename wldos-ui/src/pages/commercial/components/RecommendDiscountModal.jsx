/*
 * 推荐优惠说明弹窗（方案 5.1）
 * 列表页、详情页共用；底部「去获取我的推荐码」由父组件决定跳转（未登录→登录并带回，已登录→个人推荐中心）
 */
import React from 'react';
import { Modal, Button } from 'antd';
import { GiftOutlined } from '@ant-design/icons';
import { Link } from 'umi';

const RecommendDiscountModal = ({ visible, onClose, onGetReferral }) => {
  return (
    <Modal
      title="推荐优惠说明"
      open={visible}
      onCancel={onClose}
      footer={
        <div>
          <Button onClick={onClose}>关闭</Button>
          <Button type="primary" icon={<GiftOutlined />} onClick={onGetReferral} style={{ marginLeft: 8 }}>
            去获取我的推荐码
          </Button>
        </div>
      }
      width={560}
      destroyOnClose
    >
      <div style={{ lineHeight: 1.8 }}>
        <p><strong>一、优惠规则</strong></p>
        <p>新用户通过推荐链接或推荐码下单，可享受相应产品优惠；具体优惠比例或金额以产品页及结算页展示为准。未输入推荐码则不享受推荐优惠。</p>
        <p><strong>二、推荐人佣金规则</strong></p>
        <p>推荐人成功推荐新用户下单并完成支付后，可按平台《推荐营销规则》获得相应佣金；佣金比例与发放方式以规则及后台配置为准。</p>
        <p><strong>三、推荐流程</strong></p>
        <ul style={{ marginBottom: 0, paddingLeft: 20 }}>
          <li>注册/登录后进入个人推荐中心，获取您的推荐码与推荐链接；</li>
          <li>将推荐链接或推荐码分享给新用户；</li>
          <li>新用户通过链接访问产品并下单时填写您的推荐码，即可享优惠，您可获得佣金。</li>
        </ul>
        <p style={{ marginTop: 12, marginBottom: 0 }}>
          详细规则请参阅 <Link to="/agreement" target="_blank" rel="noopener noreferrer">《推荐营销规则》</Link>（与服务协议一并展示）。
        </p>
      </div>
    </Modal>
  );
};

export default RecommendDiscountModal;
