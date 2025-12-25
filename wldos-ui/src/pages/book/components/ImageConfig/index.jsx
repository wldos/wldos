import React from 'react';
import ImageUpload from '../ImageUpload';
import { upParams } from '@/components/FileUpload';
import styles from './index.less';

const ImageConfig = ({ 
  coverUrl, 
  setCoverUrl, 
  pic1Url, 
  setPic1Url, 
  pic2Url, 
  setPic2Url, 
  pic3Url, 
  setPic3Url, 
  pic4Url, 
  setPic4Url, 
  beforeUp, 
  handleChange 
}) => {
  const mainImages = [
    { url: pic1Url, setUrl: setPic1Url, label: '主图1', index: 2 },
    { url: pic2Url, setUrl: setPic2Url, label: '主图2', index: 3 },
    { url: pic3Url, setUrl: setPic3Url, label: '主图3', index: 4 },
    { url: pic4Url, setUrl: setPic4Url, label: '主图4', index: 5 },
  ];

  return (
    <div className={styles.imageConfigContainer}>
      {/* 封面区域 */}
      <div className={styles.coverSection}>
        <div className={styles.sectionLabel}>
          <span>📷</span>
          <span>封面图片</span>
        </div>
        <ImageUpload
          avatar={coverUrl}
          params={upParams(400, 300)}
          beforeUp={beforeUp}
          onChange={(info) => handleChange(info, 1)}
          label="上传封面"
          type="cover"
        />
      </div>

      {/* 主图区域 */}
      <div className={styles.mainImagesSection}>
        <div className={styles.sectionLabel}>
          <span>🖼️</span>
          <span>主图图片</span>
        </div>
        <div className={styles.mainImagesGrid}>
          {mainImages.map((item, index) => (
            <ImageUpload
              key={index}
              avatar={item.url}
              params={upParams(200, 200)}
              beforeUp={beforeUp}
              onChange={(info) => handleChange(info, item.index)}
              label={item.label}
              type="main"
            />
          ))}
        </div>
      </div>
    </div>
  );
};

export default ImageConfig;
