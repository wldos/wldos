import React, {useEffect, useRef, useState} from 'react';
import {
  Button, Col,
  Divider,
  Form, Input,
  message,
  Modal, Radio, Row, Select, TreeSelect,
} from 'antd';
import styles from "@/pages/book/create/components/Step2/index.less";
import {fetchEnumMap} from "@/pages/book/create/service";
import {
  AvatarView,
  picModal,
} from "@/pages/book/create/components/Step2";
import {upParams} from "@/components/FileUpload";
import Draggable from "react-draggable";

const RadioGroup = Radio.Group;

const { SHOW_PARENT } = TreeSelect;

let blobFile = null;

const formLayout = {
  labelCol: {
    span: 5,
  },
  wrapperCol: {
    span: 19,
  },
};

const UpdateForm = (props) => {
  const [form] = Form.useForm();
  const {
    onSubmit: updateChapter,
    onCancel: handleUpdateModalVisible,
    updateModalVisible,
    values,
    categories,
    tagData,
  } = props;
  const [coverUrl, setCoverUrl] = useState(undefined);
  const [aspect, setAspect] = useState(1);
  const [rewardStatus, setReward] = useState(false);
  const cropRef = useRef(null);
  const [cropVisible, setCropVisible] = useState(false);
  const [cropSrc, setCropSrc] = useState('');
  const [type, setFileType] = useState(undefined);
  const [privacyEnum, setPrivacy] = useState({});
  const [termValue, setTermValue] = useState([]);
  const [tagValue, setTagValue] = useState([]);

  useEffect(async () => {
    const resData = await fetchEnumMap();
    if (resData && resData.data) {
      setPrivacy(resData.data);
    }

    const {cover, ossUrl} = values;
    setCoverUrl(`${ossUrl}${cover}`);
  }, []);

  if (!values) {
    return null;
  }

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
    const isGt50K = file.size / 1024 / 1024 > 12;
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
      message.success(`上传成功！`, 1).then(() => {
        const {data: {url, path}} = response;
        if (index === 1) {
          setCoverUrl(url?? undefined);
          if (path)
            form.setFieldsValue({cover: path?? ''});
        }
      });
    } else if (status === 'error') {
      message.error(`上传失败！`, 2);
    }
  };
  const mainCover = {xs: 24, sm: 24, md: 24, lg: 24, xl: 24,};

  const handleNext = async () => {
    const formValues = await validateFields();
    if (!formValues.cover) {
      message.error("请上传封面！");
      return;
    }
    updateChapter({...values, ...formValues});
  };

  const typeProps = {
    showSearch: true,
    treeData: categories?? [],
    value: termValue,
    onChange: setTermValue,
    treeCheckable: true,
    treeDefaultExpandAll: true,
    treeLine: true,
    treeCheckStrictly: true,
    showCheckedStrategy: SHOW_PARENT,
    placeholder: '请选择',
    treeNodeFilterProp: "title",
    style: { width: '100%', },
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
          <div className={styles.left}>
            <Form
              {...formLayout}
              form={form}
              layout="horizontal"
              className={styles.stepForm}
              hideRequiredMark
              initialValues={values}
            >
              <Form.Item name="cover" noStyle>
                <Input hidden/>
              </Form.Item>
              <Form.Item
                label="别名"
                name="pubName"
              >
                <Input placeholder="设置有意义的别名"/>
              </Form.Item>
              <Form.Item
                name="pubExcerpt"
                label="摘要"
                rules={[
                  {
                    max: 1500,
                    type: 'string',
                    message: '最多1500个字'
                  },
                  {
                    min: 150,
                    type: 'string',
                    message: '最少150个字'
                  }
                ]}
              >
                <Input.TextArea
                  placeholder="请描述内容概要，不少于150字。"
                  rows={4}
                />
              </Form.Item>
              <Form.Item
                name="privacyLevel"
                label="查看方式"
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
              <Form.Item label="分类" name="termTypeIds"
               rules={[
                 {
                   required: true,
                   message: '请设置分类'
                 },
               ]}
              >
                <TreeSelect {...typeProps} />
              </Form.Item>
              <Form.Item label="标签" name="tagIds">
                <Select mode="tags" {...tagProps} />
              </Form.Item>
            </Form>
          </div>
          <div className={styles.right}>
            <Row gutter={2}>
              <Col key={0} {...mainCover}>
                <AvatarView avatar={coverUrl} params={upParams(532, 320)}
                            beforeUp={(file) => beforeUp(file, 133 / 80)}
                            onChange={(info) => handleChange(info, 1)} style="avatar"/>
              </Col>
            </Row>
          </div>
        </div>
        {picModal(cropVisible, setCropVisible, cropRef, cropSrc, aspect, setAspectRatio, setRotateTo, saveCropper, type)}
      </>
    );
  };

  const renderFooter = () =>
    (
      <>
        <Button onClick={() => handleUpdateModalVisible(false, values)}>取消</Button>
        <Button type="primary" onClick={() => handleNext()}>提交</Button>
      </>
    );

  const [disabled, setDisabled] = useState(false);
  const [bounds, setBounds] = useState({ left: 0, top: 0, bottom: 0, right: 0 });
  const draggleRef = useRef(null);

  const onStart = (_event, uiData) => {
    const { clientWidth, clientHeight } = window.document.documentElement;
    const targetRect = draggleRef.current?.getBoundingClientRect();
    if (!targetRect) {
      return;
    }
    setBounds({
      left: -targetRect.left + uiData.x,
      right: clientWidth - (targetRect.right - uiData.x),
      top: -targetRect.top + uiData.y,
      bottom: clientHeight - (targetRect.bottom - uiData.y),
    });
  };

  return (
    <Modal
      centered={true}
      maskClosable={false}
      width="fit-content"
      destroyOnClose
      title={
        <div
          style={{
            width: '100%',
            cursor: 'move',
          }}
          onMouseOver={() => {
            if (disabled) {
              setDisabled(false);
            }
          }}
          onMouseOut={() => {
            setDisabled(true);
          }}
          onFocus={() => {}}
          onBlur={() => {}}
        >
          内容配置
        </div>
      }
      visible={updateModalVisible}
      footer={renderFooter()}
      onCancel={() => handleUpdateModalVisible()}
      modalRender={modal => (
        <Draggable
          disabled={disabled}
          bounds={bounds}
          onStart={(event, uiData) => onStart(event, uiData)}
        >
          <div ref={draggleRef}>{modal}</div>
        </Draggable>
      )}
    >
      {renderContent()}
    </Modal>
  );
};

export default UpdateForm;