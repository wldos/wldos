import React, {useEffect, useRef, useState} from 'react';
import {
  Button,
  Col,
  Form,
  message,
  Row,  TreeSelect,
} from 'antd';
import {connect} from 'umi';
import styles from './components/Step2/index.less';
import {
  fetchEnumMap,
} from "@/pages/book/create/service";
import 'cropperjs/dist/cropper.css';
import {queryLayerCategory} from "@/pages/sys/category/service";
import {queryTagSelect} from "@/pages/sys/tag/service";
import {upParams} from "@/components/FileUpload";
import {Editor} from "@tinymce/tinymce-react";

const {SHOW_PARENT} = TreeSelect;

export const formItemLayout = {
  labelCol: {
    span: 5,
  },
  wrapperCol: {
    span: 19,
  },
};

let blobFile = null;
const Step2 = ({data, dispatch, submitting, privacyEnum = []}) => {
  const [form] = Form.useForm();
  const editorRef = useRef(null);
  const [coverUrl, setCoverUrl] = useState(undefined);
  const [pic1Url, setPic1Url] = useState(undefined);
  const [pic2Url, setPic2Url] = useState(undefined);
  const [pic3Url, setPic3Url] = useState(undefined);
  const [pic4Url, setPic4Url] = useState(undefined);
  const [aspect, setAspect] = useState(1);
  const cropRef = useRef(null);
  const [cropVisible, setCropVisible] = useState(false);
  const [cropSrc, setCropSrc] = useState('');
  const [type, setFileType] = useState(undefined);
  const [termValue, setTermValue] = useState([]);
  const [tagValue, setTagValue] = useState([]);
  const [treeData, setTreeData] = useState([]);
  const [tagData, setTagData] = useState([]);
  const [content, setContent] = useState('');

  useEffect(async () => {
    const resData = await fetchEnumMap();
    if (resData && resData.data) {
      if (dispatch) {
        dispatch({
          type: 'bookInfo/savePrivacyEnum',
          payload: {privacyEnum: resData.data},
        });
      }
    }
  }, []);

  useEffect(async () => {
    let res = await queryLayerCategory();
    if (res?.data) {
      setTreeData(res.data);
    }

    res = await queryTagSelect();
    if (res?.data) {
      setTagData(res.data);
    }
  }, []);

  if (!data) {
    return null;
  }

  useEffect(() => {
    form.setFieldsValue({pubContent: content});
  }, [content]);

  const {validateFields, getFieldsValue} = form;

  const onValidateForm = async () => {
    const values = await validateFields();

    if (!values.pubContent) {
      message.error("请描述详情！");
      return;
    }

    const {geographic = {}, ...otherValues} = values;
    const base = {
      province: geographic?.province?.key ?? '',
      city: geographic?.city?.key ?? '',
      ...otherValues
    }

    if (dispatch) {
      dispatch({
        type: 'bookInfo/submitStepForm',
        payload: {...data, ...base, pubType: 'info'},
      });

      dispatch({
        type: 'bookInfo/saveGeographic',
        payload: geographic,
      });
    }
  };

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
      message.success(`上传成功！`, 1).then(() => {
        const {data: {url, path}} = response;
        if (index === 1) {
          setCoverUrl(url ?? undefined);
          if (path)
            form.setFieldsValue({cover: path ?? ''});
        } else if (index === 2) {
          setPic1Url(url ?? undefined);
          if (path)
            form.setFieldsValue({mainPic1: path ?? ''});
        } else if (index === 3) {
          setPic2Url(url ?? undefined);
          if (path)
            form.setFieldsValue({mainPic2: path ?? ''});
        } else if (index === 4) {
          setPic3Url(url ?? undefined);
          if (path)
            form.setFieldsValue({mainPic3: path ?? ''});
        } else {
          setPic4Url(url ?? undefined);
          if (path)
            form.setFieldsValue({mainPic4: path ?? ''});
        }
      });
    } else if (status === 'error') {
      message.error(`上传失败！`, 2);
    }
  };

  const typeProps = {
    treeData,
    showSearch: true,
    treeNodeFilterProp: "title",
    value: termValue,
    onChange: setTermValue,
    treeCheckable: true,
    treeDefaultExpandAll: true,
    treeLine: true,
    treeCheckStrictly: true,
    showCheckedStrategy: SHOW_PARENT,
    placeholder: '请选择',
    style: {
      width: '100%',
    },
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

  return (
    <>
      <div className={styles.baseView}>
        <div className={styles.content}>
          <Form
            {...formItemLayout}
            form={form}
            layout="horizontal"
            className={styles.stepForm}
            hideRequiredMark
            initialValues={data}
          >
            <Form.Item label="图片" className={styles.main}>
              {picHandle(coverUrl, beforeUp, handleChange, pic1Url, pic2Url, pic3Url, pic4Url)}
            </Form.Item>
            <Form.Item name="pubContent" noStyle>
              <textarea hidden
                        value={content} onChange={(e) => setContent(e.target.value)}/>
              <Editor
                onInit={(evt, editor) => {
                  editorRef.current = editor;
                }}
                init={{
                  height: '250px',
                  width: '100%',
                  menubar: false,
                  language: 'zh_CN',
                  branding: false,
                  resize: true,
                  convert_urls: false,
                  mobile: {
                    width: '100%',
                    height: '250px',
                    resize: true,
                    menubar: false,
                    toolbar: ['fullscreen bold italic | alignleft aligncenter alignright alignjustify undo redo | paste'],
                  },
                  plugins: [
                    'fullscreen table paste wordcount importcss hr nonbreaking toc textpattern noneditable'
                  ],
                  toolbar: ['fullscreen bold italic | forecolor backcolor table | alignleft aligncenter alignright alignjustify undo redo | paste'],
                }}
                onEditorChange={(e) => setContent(e)}
                value={content}
              />
            </Form.Item>
            <Form.Item
              wrapperCol={{
                xs: {
                  span: 24,
                  offset: formItemLayout.labelCol.span,
                },
                sm: {
                  span: formItemLayout.wrapperCol.span,
                  offset: formItemLayout.labelCol.span * 3,
                },
              }}
            >
              <Button type="primary" onClick={onValidateForm} loading={submitting}>
                发布
              </Button>
            </Form.Item>
          </Form>
        </div>
      </div>
    </>
  );
};

export default connect(({bookInfo, loading}) => ({
  submitting: loading.effects['bookInfo/submitStepForm'],
  data: bookInfo.step,
  privacyEnum: bookInfo.privacyEnum,
}))(Step2);
