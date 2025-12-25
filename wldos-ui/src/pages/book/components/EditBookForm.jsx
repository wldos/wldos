import React, {useEffect, useRef, useState} from 'react';
import {
  Button, Col,
  Form, Input,
  message,
  Radio, Row, Select, TreeSelect, Space, Card, Typography
} from 'antd';
import {EditOutlined, PictureOutlined, SettingOutlined, UploadOutlined, DeleteOutlined} from '@ant-design/icons';
import FullscreenModal from '@/components/FullscreenModal';
import styles from "@/pages/book/create/components/Step2/index.less";
import {fetchEnumMap} from "@/pages/book/create/service";
import {
  AvatarView,
  formContent,
  picModal
} from "@/pages/book/create/components/Step2";
import ImageConfig from "./ImageConfig";
import {fetchEnumPubType} from "@/services/enum";
import {upParams} from "@/components/FileUpload";

const RadioGroup = Radio.Group;

const { SHOW_PARENT } = TreeSelect;
const {Title, Text} = Typography;

let blobFile = null;

const formLayout = {
  labelCol: {
    span: 6,
  },
  wrapperCol: {
    span: 18,
  },
};

// 重新设计图片配置组件 - 使用新的ImageUpload组件
export const picHandle = (mainCover, coverUrl, beforeUp, handleChange, main, pic1Url, pic2Url, pic3Url, pic4Url, previewUrl, setPreviewUrl, setPic1Url, setPic2Url, setPic3Url, setPic4Url) => {
  const mainImageItems = [
    { key: 'pic1', url: pic1Url, label: '主图1', type: 'main', index: 2, setUrl: setPic1Url },
    { key: 'pic2', url: pic2Url, label: '主图2', type: 'main', index: 3, setUrl: setPic2Url },
    { key: 'pic3', url: pic3Url, label: '主图3', type: 'main', index: 4, setUrl: setPic3Url },
    { key: 'pic4', url: pic4Url, label: '主图4', type: 'main', index: 5, setUrl: setPic4Url },
  ];

  return (
    <div className={styles.uploadRow}>
      {mainImageItems.map((item) => (
        <div
          key={item.key}
          className={`${styles.uploadItem} ${styles.mainUpload}`}
          onMouseEnter={() => {
            if (item.url) {
              setPreviewUrl(item.url);
            }
          }}
          onMouseLeave={() => {
            // 鼠标离开时重新预览封面
            if (coverUrl) {
              setPreviewUrl(coverUrl);
            }
          }}
          data-item={item.key}
        >
          <ImageUpload
            avatar={item.url}
            params={upParams(277, 277)}
            beforeUp={(file) => beforeUp(file, 1)}
            onChange={(info) => handleChange(info, item.index)}
            onPreview={(url) => {
              if (url) {
                setPreviewUrl(url);
              }
            }}
            label={item.label}
            type="main"
          />
        </div>
      ))}
    </div>
  );
};

const UpdateForm = (props) => {
  const [form] = Form.useForm();
  const {
    onSubmit: updateBook,
    onCancel: handleUpdateModalVisible,
    updateModalVisible,
    values,
    categories,
    tagData,
  } = props;
  const [coverUrl, setCoverUrl] = useState(undefined);
  const [pic1Url, setPic1Url] = useState(undefined);
  const [pic2Url, setPic2Url] = useState(undefined);
  const [pic3Url, setPic3Url] = useState(undefined);
  const [pic4Url, setPic4Url] = useState(undefined);
  const [previewUrl, setPreviewUrl] = useState(undefined);
  const [aspect, setAspect] = useState(1);
  const [rewardStatus, setReward] = useState(false);
  const cropRef = useRef(null);
  const [cropVisible, setCropVisible] = useState(false);
  const [cropSrc, setCropSrc] = useState('');
  const [type, setFileType] = useState(undefined);
  const [pubTypeOptions, setPubType] = useState([]);
  const [privacyEnum, setPrivacy] = useState({});
  const [termValue, setTermValue] = useState([]);
  const [tagValue, setTagValue] = useState([]);

  useEffect(async () => {
    const resData = await fetchEnumMap();
    if (resData?.data) {
      setPrivacy(resData.data);
    }

    const res = await fetchEnumPubType();
    if (res?.data) {
      setPubType(res.data);
    }

    const {cover, mainPic1, mainPic2, mainPic3, mainPic4, ossUrl} = values;
    if (ossUrl) {
      if (cover)
        setCoverUrl(`${ossUrl}${cover}`);
      if (mainPic1)
        setPic1Url(`${ossUrl}${mainPic1}`);
      if (mainPic2)
        setPic2Url(`${ossUrl}${mainPic2}`);
      if (mainPic3)
        setPic3Url(`${ossUrl}${mainPic3}`);
      if (mainPic4)
        setPic4Url(`${ossUrl}${mainPic4}`);
    }
    // 初始化预览优先级：封面优先，然后按主图顺序
    const firstAvailable =
      (cover && `${ossUrl}${cover}`) ||
      (mainPic1 && `${ossUrl}${mainPic1}`) ||
      (mainPic2 && `${ossUrl}${mainPic2}`) ||
      (mainPic3 && `${ossUrl}${mainPic3}`) ||
      (mainPic4 && `${ossUrl}${mainPic4}`) ||
      undefined;
    setPreviewUrl(firstAvailable);
  }, []);

  const { validateFields } = form;

  // 设置裁切比例
  const setAspectRatio = (e) => {
    cropRef.current.cropper.setAspectRatio(e.target.value);
  };

  // 设置旋转角度
  const setRotateTo = (value) => {
    cropRef.current.cropper.rotate(value);
  };

  // 确认裁切
  const saveCropper = (ftype) => { // 函数组件请使用useRef ref，类组件使用onInitialized,注意两种方式函数引用链不同
    cropRef.current.cropper.getCroppedCanvas().toBlob((blob) => {
      blobFile = blob;
    }, ftype || 'image/png');
  };

  const beforeUp = (file, asp) => {
    const isGt50K = file.size / 1024 / 1024 > 15;
    if (isGt50K) {
      return message.error('图片大小超限').then(() => false);
    }

    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = (e) => {
      setAspect(asp);
      setCropSrc(e.target.result);
      setCropVisible(true);
      setFileType(file.type);
    };

    // eslint-disable-next-line no-unused-vars
    return new Promise((resolve, reject) => {
      const itv = setInterval(() => {
        if (blobFile) {// 异步监听
          blobFile.uid = file.uid;
          blobFile.name = file.name;
          // 持续上传
          resolve(blobFile);
          setCropVisible(false);
          blobFile = null;
          window.clearInterval(itv);
        }
      }, 100);
    });
  };

  const handleChange = (info, index) => {
    const {file: {status, response}} = info;

    if (status === 'done') {
      const {data: {url, path}} = response;
      if (index === 1) {
        setCoverUrl(url?? undefined);
        setPreviewUrl(url?? undefined);
        if (path)
          form.setFieldsValue({cover: path?? ''});
      }else if (index === 2) {
        setPic1Url(url?? undefined);
        setPreviewUrl(url?? undefined);
        if (path)
          form.setFieldsValue({mainPic1: path?? ''});
      }else if (index === 3) {
        setPic2Url(url?? undefined);
        setPreviewUrl(url?? undefined);
        if (path)
          form.setFieldsValue({mainPic2: path?? ''});
      }else if (index === 4) {
        setPic3Url(url?? undefined);
        setPreviewUrl(url?? undefined);
        if (path)
          form.setFieldsValue({mainPic3: path?? ''});
      }else {
        setPic4Url(url?? undefined);
        setPreviewUrl(url?? undefined);
        if (path)
          form.setFieldsValue({mainPic4: path?? ''});
      }
    } else if (status === 'error') {
      message.error(`上传失败！`, 2).then();
    }
  };
  const mainCover = {xs: 24, sm: 24, md: 24, lg: 24, xl: 24,};
  const main = {xs: 24, sm: 24, md: 12, lg: 12, xl: 12,};

  const handleNext = async () => {
    const formValues = await validateFields();

    const {geographic = {}, ...otherValues} = formValues;
    const fieldsValue = {
      province: geographic?.province?.key?? '',
      city: geographic?.city?.key?? '',
      ...otherValues
    }
    const value = {...values, ...fieldsValue};
    updateBook(value);
  };

  const typeProps = {
    treeData: categories?? [],
    showSearch: true,
    value: termValue,
    onChange: setTermValue,
    treeCheckable: true,
    treeDefaultExpandAll: true,
    treeLine: true,
    treeCheckStrictly: true,
    showCheckedStrategy: SHOW_PARENT,
    placeholder: '请选择',
    treeNodeFilterProp: "title",
    style: { width: '100%',},
  };

  const tagProps = {
    value: tagValue,
    onChange: setTagValue,
    placeholder: '设置标签',
    style: {
      width: '100%',
    },
    options: tagData,
  }

  const renderContent = () => {
    return (
      <>
        <div className={styles.baseView}>
          <Row gutter={24}>
            <Col xs={24} lg={12}>
              <Card
                title={
                  <Space>
                    <PictureOutlined style={{ color: '#1890ff' }} />
                    <span>图片配置</span>
                  </Space>
                }
                className={styles.uploadCard}
              >
                <ImageConfig
                  coverUrl={coverUrl}
                  setCoverUrl={setCoverUrl}
                  pic1Url={pic1Url}
                  setPic1Url={setPic1Url}
                  pic2Url={pic2Url}
                  setPic2Url={setPic2Url}
                  pic3Url={pic3Url}
                  setPic3Url={setPic3Url}
                  pic4Url={pic4Url}
                  setPic4Url={setPic4Url}
                  beforeUp={beforeUp}
                  handleChange={handleChange}
                />
              </Card>
            </Col>
            <Col xs={24} lg={12}>
              <Card
                title={
                  <Space>
                    <SettingOutlined style={{ color: '#1890ff' }} />
                    <span>基本信息</span>
                  </Space>
                }
                className={styles.formCard}
              >
                <Form
                  {...formLayout}
                  form={form}
                  layout="horizontal"
                  className={styles.stepForm}
                  hideRequiredMark
                  initialValues={{
                    ...values,
                    watermarkEnabled: values?.watermarkEnabled === 'true' || values?.watermarkEnabled === true
                  }}
                >
                  <Form.Item label="发布类型" name="pubType"
                    rules={[{
                      required: true,
                      message: '请选择发布类型',
                    },]}
                  >
                    <Select
                      style={{
                        width: '100%',
                      }}
                      filterOption={(input, option) =>
                        option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                      }
                      options={pubTypeOptions}
                    >
                    </Select>
                  </Form.Item>
                  {formContent(privacyEnum, typeProps, tagProps)}                  
                  <Form.Item
                    label="别名"
                    name="pubName"
                  >
                    <Input placeholder="设置有意义的别名"/>
                  </Form.Item>
                  <Form.Item
                    name="privacyLevel"
                    label="查看方式"
                    rules={[
                      {
                        required: false,
                        message: '请设置查看方式'
                      },
                    ]}
                  >
                    <RadioGroup
                      options={privacyEnum}
                      onChange={(e) => {
                        if (e.target.value === 'reward') setReward(true); else setReward(false);
                      }}
                    />
                  </Form.Item>
                  {rewardStatus && <Form.Item
                    label="打赏金额"
                    name="reward"
                    rules={[
                      {
                        required: true,
                        message: '请输入打赏金额',
                      },
                      {
                        pattern: /^(\d+)((?:\.\d+)?)$/,
                        message: '请输入合法金额数字',
                      },
                    ]}
                  >
                    <Input prefix="￥" placeholder="请输入打赏金额"/>
                  </Form.Item>}
                  <Form.Item
                    name="pubExcerpt"
                    label="摘要"
                    rules={[
                      {
                        required: false,
                      },
                      {
                        max: 300,
                        type: 'string',
                        message: '最多300个汉字'
                      },
                      {
                        min: 10,
                        type: 'string',
                        message: '最少10个汉字'
                      }
                    ]}
                  >
                    <Input.TextArea
                      placeholder="简介不少于10字。"
                      rows={3}
                    />
                  </Form.Item>
                </Form>
              </Card>
            </Col>
          </Row>
        </div>
        {picModal(cropVisible, setCropVisible, cropRef, cropSrc, aspect, setAspectRatio, setRotateTo, saveCropper, type)}
      </>
    );
  };

  const renderFooter = () =>
    (
      <Space>
        <Button onClick={() => handleUpdateModalVisible(false, values)}>取消</Button>
        <Button type="primary" onClick={() => handleNext()}>保存配置</Button>
      </Space>
    );

  return (
    <FullscreenModal
      width={1200}
      bodyStyle={{
        padding: '24px',
      }}
      destroyOnClose
      title={
        <Space>
          <EditOutlined style={{ color: '#1890ff' }} />
          作品配置
        </Space>
      }
      visible={updateModalVisible}
      footer={renderFooter()}
      onCancel={() => handleUpdateModalVisible()}
    >
      {renderContent()}
    </FullscreenModal>
  );
};

export default UpdateForm;
