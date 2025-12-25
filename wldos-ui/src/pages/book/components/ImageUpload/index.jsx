import React, { useState } from 'react';
import { Upload, Button } from 'antd';
import { UploadOutlined, DeleteOutlined } from '@ant-design/icons';
import config from '@/utils/config';
import styles from './index.less';

const { prefix } = config;

const ImageUpload = ({ 
  avatar,
  params = {},
  beforeUp,
  onChange,
  onPreview,
  label = "上传图片",
  type = "main"
}) => {
  const [hovering, setHovering] = useState(false);

  const handleDelete = (e) => {
    e.stopPropagation();
    onChange?.({ file: { status: 'done', response: { data: { url: '' } } } });
    onPreview?.('');
  };

  const handleMouseEnter = () => {
    setHovering(true);
    if (avatar) {
      onPreview?.(avatar);
    }
  };

  const handleMouseLeave = () => {
    setHovering(false);
  };

  return (
    <div 
      className={`${styles.imageUpload} ${styles[type]}`}
      onMouseEnter={handleMouseEnter}
      onMouseLeave={handleMouseLeave}
    >
      {/* 图片显示 */}
      {avatar && (
        <img src={avatar} alt={type} className={styles.uploadedImage} />
      )}

      {/* 上传组件 */}
      <Upload 
        name="file"
        showUploadList={false}
        beforeUpload={beforeUp}
        onChange={onChange}
        action={`${prefix}/info/upload`}
        {...params}
      >
        <div className={`${styles.uploadButton} ${avatar ? styles.hidden : ''}`}>
          <Button>
            <UploadOutlined />
            {label}
          </Button>
        </div>
      </Upload>

      {/* 操作按钮 */}
      {avatar && hovering && (
        <div className={styles.actionButtons}>
          <a 
            className={styles.reuploadLink}
            onClick={(e) => {
              e.stopPropagation();
              const fileInput = e.target.closest(`.${styles.imageUpload}`).querySelector('input[type="file"]');
              if (fileInput) fileInput.click();
            }}
          >
            重传
          </a>
          <span 
            className={styles.deleteIcon}
            onClick={handleDelete}
            title="删除"
          >
            <DeleteOutlined />
          </span>
        </div>
      )}
    </div>
  );
};

export default ImageUpload;
