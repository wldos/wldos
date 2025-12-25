import React from 'react';
import {Card, Col, Modal, Row} from 'antd';
import QRCode from "qrcode.react";
import styles from "@/pages/book/detail/style.less";
import telex from '@/assets/telwx.gif';
import phone from '@/assets/phone.gif';

const TelCode = (props) => {
  const { visible, onCancel, url, realNo } = props;
  const gutter = 24;
  const left = 6;
  const right = 18;
  return (
    <Modal
      destroyOnClose
      title="查看完整号码"
      visible={visible}
      onCancel={() => onCancel()}
      footer={null} >
      <Row gutter={gutter} style={{borderBottom: '#eee solid 1px'}}>
        <Col lg={left} md={gutter}>
          <Card bordered={false}>
            <img alt="电话" src={phone}/>
          </Card>
        </Col>
        <Col lg={right} md={gutter} >
          <Card className={styles.telCode} bordered={false}>
            <label className={styles.number}>{realNo}</label><br/>
            <label>联系时请说是在本站看到的, 谢谢</label>
          </Card>
        </Col>
      </Row>
      <Row gutter={gutter}>
        <Col lg={left} md={gutter}>
          <Card bordered={false}>
            <img alt="二维码" src={telex}/>
          </Card>
        </Col>
        <Col lg={right} md={gutter} >
          <Card className={styles.telCode} bordered={false}>
            <label>微信扫码，快速拨打电话</label>
            <QRCode
              value={url}
              width={150}
              size={150}
              fgColor="#000000"
            />
          </Card>
        </Col>
      </Row>
    </Modal>
  );
};

export default TelCode;
