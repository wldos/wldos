/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React, {useState} from "react";
import qz from "@/assets/qqzone.svg";
import qq from "@/assets/qq-icon.svg";
import wb from "@/assets/weibo-icon.svg";
import wx from "@/assets/weixin-icon.svg";
import styles from './index.less';
import { Modal} from "antd";
import QRCode from "qrcode.react";

const Share = (props) => {
  const { title, src, style} = props;
  const [visible, setVisible] = useState(false);

  const shareTo = (stype) => {
    // qq空间接口的传参
    if(stype === 'qzone'){
      window.open(`https://sns.qzone.qq.com/cgi-bin/qzshare/cgi_qzshare_onekey?url=${document.location.href}?sharesource=qzone&title=${title}&pics=${src}&summary=${document.querySelector('meta[name="description"]').getAttribute('content')}`);
    } else
    // 新浪微博接口的传参
    if(stype=== 'sina'){
      window.open(`http://service.weibo.com/share/share.php?url=${document.location.href}?sharesource=weibo&title=${title}&pic=${src}&appkey=1983604403`);
    } else
    // qq好友接口的传参
    if(stype === 'qq'){
      window.open(`http://connect.qq.com/widget/shareqq/index.html?url=${document.location.href}?sharesource=qzone&title=${title}&pics=${src}&summary=${document.querySelector('meta[name="description"]').getAttribute('content')}&desc=wldos是一个SaaS开发平台`);
    } else
    // 生成二维码给微信扫描分享，php生成，也可以用jquery.qrcode.js插件实现二维码生成
    if(stype === 'wechat'){
      setVisible(true);
    }
  }

  return (
    <>
      <div className={style?? styles.proShare}>
        <img src={qz} onClick={() => shareTo('qzone')} />
        <img src={qq} onClick={() => shareTo('qq')}/>
        <img src={wb} onClick={() => shareTo('sina')}/>
        <img src={wx} onClick={() => shareTo('wechat')}/>
      </div>
      <div>
      <Modal destroyOnClose
             className={styles.QRModal}
             centered
             closable={false}
             visible={visible}
             onCancel={() => setVisible(false)}
             footer={null} >
          {visible &&
          <QRCode
            value={document.location.href}
            width={150}
            size={150}
            fgColor="#000000"
          />}
      </Modal>
      </div>
    </>
  );
};

export default Share;
