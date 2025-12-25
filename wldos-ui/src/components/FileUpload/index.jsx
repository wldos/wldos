import styles from "@/pages/account/settings/components/BaseView.less";
import {FormattedMessage} from "umi";
import {Button, message, Upload} from "antd";
import {UploadOutlined} from "@ant-design/icons";
import React from "react";
import config from "@/utils/config";
import {headerFix} from "@/utils/utils";

const {prefix} = config;

export const UploadView = ({uploadTitle, buttonTitle, src, params = {}, beforeUp, onChange}) => (
  <>
    {uploadTitle && <div className={styles.avatar_title}>
      uploadTitle
    </div>}
    <div className={styles.avatar}>
      {src && <img src={src} alt="file"/>}
    </div>
    <Upload name="file"
      showUploadList={false}
      beforeUpload={(file) => { // @todo 其他全局前置约束，由后台同一配置驱动，前台获取结果
        if (params.accept.indexOf(`.${file.type.substring(file.type.indexOf('/')+1, file.type.length)}`) === -1) {
          return message.error('不允许的文件类型').then(() => false)
        }

        return beforeUp(file);
      }}
      onChange={onChange}
      action={`${prefix}/file/store`}
      {...params}
    >
      <div className={styles.button_view}>
        <Button>
          <UploadOutlined/>
          {!buttonTitle ?
          <FormattedMessage
            id="account.basic.change-avatar"
            defaultMessage="Change avatar"
          /> : buttonTitle}
        </Button>
      </div>
    </Upload>
  </>
);

export const upParams = (width, height) => {
  const params = {
    // multiple: true,
    accept: '.jpg,.png,.gif,.jpeg,.bmp,.x-icon,.ico', // @todo 后期从后台配置获取，由后台驱动, .x-icon匹配ico图片
    headers: headerFix,
  };

  return !width ? params : {...params, params: {width, height}};
};