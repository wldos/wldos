import React, {useEffect, useRef, useState} from 'react';
import {
  Button,
  Col,
  Divider,
  Form,
  Input,
  message,
  Modal,
  Radio,
  Row, Select, Spin, Switch, TreeSelect,
  Upload
} from 'antd';
import {connect, FormattedMessage, useIntl} from 'umi';
import styles from './index.less';
import GeographicView from "@/pages/account/settings/components/GeographicView";
import config from "@/utils/config";
import {UploadOutlined} from "@ant-design/icons";
import {
  fetchEnumMap,
} from "@/pages/book/create/service";
import Cropper from 'react-cropper';
import 'cropperjs/dist/cropper.css';
import {queryLayerCategory} from "@/pages/sys/category/service";
import {queryTagSelect} from "@/pages/sys/tag/service";
import {upParams} from "@/components/FileUpload";
import {loadTinyMCE} from "@/utils/loadTinyMCE";

const { SHOW_PARENT } = TreeSelect;

/** 与后端 k_pubs.visibility_scope 取值一致（见 CMS Pub 保存） */
export const VISIBILITY_SCOPE_OPTIONS = [
  { value: 'PUBLIC_LISTED', label: '前台可发现' },
  { value: 'UNLISTED', label: '仅链接访问（不参与列表/搜索）' },
  { value: 'INTERNAL_ONLY', label: '仅内部可见' },
];

export const formItemLayout = {
  labelCol: {
    span: 5,
  },
  wrapperCol: {
    span: 19,
  },
};

const {prefix} = config;

export const AvatarView = ({
                             avatar,
                             params = {},
                             beforeUp,
                             onChange,
                             style
                           }) => style === 'avatar' ? (
  <>
  <div className={styles.avatar}>
    {avatar && (<img src={avatar} alt="cover"/>)}
  </div>
  <Upload name="file"
          showUploadList={false}
          beforeUpload={(file) => { // @todo 其他全局前置约束，由后台同一配置驱动，前台获取结果
            if (params.accept.indexOf(`.${file.type.substring(file.type.indexOf('/')+1, file.type.length)}`) === -1)
              return message.error('book.create.step2.message.fileTypeNotAllowed').then(() => false)
            return beforeUp(file);
          }}
          onChange={onChange}
          action={`${prefix}/info/upload`}
          {...params}
  >
    <div className={`${styles.button_view} ${avatar ? styles.hidden : ''}`}>
      <Button>
        <UploadOutlined/><font style={{fontSize: 7}}>
        <FormattedMessage id="book.create.step2.cover.button" defaultMessage="封面" />
        </font>
      </Button>
    </div>
  </Upload>
  </>
) : (
  <>
  <div className={styles.mainPic}>
    {avatar && (<img src={avatar} alt="mainPic"/>)}
  </div>
  <Upload name="file"
          showUploadList={false}
          beforeUpload={(file) => { // @todo 其他全局前置约束，由后台同一配置驱动，前台获取结果
            if (params.accept.indexOf(`.${file.type.substring(file.type.indexOf('/')+1, file.type.length)}`) === -1)
              return message.error('book.create.step2.message.fileTypeNotAllowed').then(() => false)
            return beforeUp(file);
          }}
          onChange={onChange}
          action={`${prefix}/info/upload`}
          {...params}
  >
    <div className={`${styles.button_pic} ${avatar ? styles.hidden : ''}`}>
      <Button>
        <UploadOutlined/><font style={{fontSize: 7}}>
        <FormattedMessage id="book.create.step2.mainPic.button" defaultMessage="主图" />
        </font>
      </Button>
    </div>
  </Upload>
  </>
);

export const picModal = (cropVisible, setCropVisible, cropRef, cropSrc, aspect, setAspectRatio, setRotateTo, saveCropper, type) =>
  (<Modal
    title={<FormattedMessage id="book.create.step2.crop.title" defaultMessage="裁切" />}
    visible={cropVisible}
    footer={null}
    width={748}
    destroyOnClose
    onCancel={() => setCropVisible(false)}
  >
    <div className={styles.cropperModal}>
      <Row gutter={[16, 16]}>
        <Col span={18}>
          <Cropper // 函数组件请使用useRef ref，类组件使用onInitialized
            ref={cropRef}
            src={cropSrc}
            style={{height: 500, width: '100%'}}
            preview=".preview"
            viewMode={0} // 定义cropper的视图模式
            fixed={false} // 不锁定宽高比
            zoomable={true} // 是否允许放大图像
            guides={true} // 显示在裁剪框上方的虚线
            background={true} // 是否显示背景的马赛克
            rotatable={true} // 是否旋转
            // aspectRatio={aspect} // 裁切比列
          />
        </Col>
        <Col span={6} className={styles.right_content}>
          <div
            className="preview"
            style={{
              height: 200,
              width: '100%',
              float: 'left',
              flex: 1,
              overflow: 'hidden',
              border: '1px solid #e8e8e8',
            }}
          />

          <Radio.Group
            onChange={setAspectRatio}
            buttonStyle="solid"
            defaultValue={133 / 80}
            className={styles.button_group}
          >
            <Radio.Button value={133 / 80}>16:9</Radio.Button>
            <Radio.Button value={266 / 160}>9:16</Radio.Button>
            <Radio.Button value={399 / 240}>4:3</Radio.Button>
          </Radio.Group>
        </Col>
      </Row>

      <Row gutter={[16, 16]}>
        <Col span={9}>
          <Input.Group>
            <Button onClick={() => setRotateTo(-90)}>
              <FormattedMessage id="book.create.step2.crop.rotate.left" defaultMessage="左旋转" />
            </Button>
            <Button onClick={() => setRotateTo(-15)}>-15°</Button>
            <Button onClick={() => setRotateTo(-30)}>-30°</Button>
            <Button onClick={() => setRotateTo(-45)}>-45°</Button>
          </Input.Group>
        </Col>
        <Col span={9}>
          <Input.Group>
            <Button onClick={() => setRotateTo(90)}>
              <FormattedMessage id="book.create.step2.crop.rotate.right" defaultMessage="右旋转" />
            </Button>
            <Button onClick={() => setRotateTo(15)}>15°</Button>
            <Button onClick={() => setRotateTo(30)}>30°</Button>
            <Button onClick={() => setRotateTo(45)}>45°</Button>
          </Input.Group>
        </Col>
        <Col span={6}>
          <Button type="primary" block onClick={() => saveCropper(type)}>
            <FormattedMessage id="book.create.step2.crop.confirm" defaultMessage="确定" />
          </Button>
        </Col>
      </Row>
    </div>
  </Modal>);

const validatorGeographic = (_, value) => {
  const {province={}, city={}} = value?? {};

  if (!province.key)
    return Promise.resolve(); // 用Promise代替callback

  if (!city.key) {
    return Promise.reject(new Error('book.create.step2.validate.city.required'));
  }

  return Promise.resolve();
};

/**
 * 作品基本信息字段片段（标题、分类、发现范围、查看方式等）。
 * - visibilityScope：主表 k_pubs.visibility_scope
 * - privacyLevel：meta 扩展（查看方式）
 * privacyExtra：打赏与 Radio 联动；与上两字段无关。
 */
export const formContent = (
  privacyEnum,
  tProps,
  tagProps,
  intl = {
    formatMessage: ({ defaultMessage, id }) => defaultMessage || id,
  },
  privacyExtra = {},
) => {
  const { rewardStatus, onPrivacyChange } = privacyExtra;
  return (
  <>
    <Form.Item name="cover" noStyle>
      <Input hidden/>
    </Form.Item>
    <Form.Item name="mainPic1" noStyle>
      <Input hidden/>
    </Form.Item>
    <Form.Item name="mainPic2" noStyle>
      <Input hidden/>
    </Form.Item>
    <Form.Item name="mainPic3" noStyle>
      <Input hidden/>
    </Form.Item>
    <Form.Item name="mainPic4" noStyle>
      <Input hidden/>
    </Form.Item>
    <Form.Item
      label={intl.formatMessage({ id: 'book.create.step2.field.title.label', defaultMessage: '标题' })}
      name="pubTitle"
      rules={[
        {
          required: true,
          message: intl.formatMessage({ id: 'book.create.step2.field.title.required', defaultMessage: '请输入标题' }),
        },
        {
          max: 50,
          type: 'string',
          message: intl.formatMessage({ id: 'book.create.step2.field.title.max', defaultMessage: '最多50个汉字' }),
        },
      ]}
    >
      <Input placeholder={intl.formatMessage({ id: 'book.create.step2.field.title.placeholder', defaultMessage: '请输入标题' })}/>
    </Form.Item>
    <Form.Item
      label={intl.formatMessage({ id: 'book.create.step2.field.price.label', defaultMessage: '价格' })}
      name="ornPrice"
      rules={[
        {
          required: false,
          message: intl.formatMessage({ id: 'book.create.step2.field.price.required', defaultMessage: '请输入价格' }),
        },
        {
          pattern: /^(\d+)((?:\.\d+)?)$/,
          message: intl.formatMessage({ id: 'book.create.step2.field.price.invalid', defaultMessage: '请输入合法金额数字' }),
        },
      ]}
    >
      <Input prefix="￥" placeholder={intl.formatMessage({ id: 'book.create.step2.field.price.placeholder', defaultMessage: '请输入价格' })}/>
    </Form.Item>
    <Form.Item
      name="geographic"
      label={intl.formatMessage({ id: 'book.create.step2.field.region.label', defaultMessage: '地区' })}
      rules={[
        {
          required: false,
          message: intl.formatMessage({ id: 'book.create.step2.field.region.required', defaultMessage: '请输入地区' })
        },
        {
          validator: validatorGeographic,
        },
      ]}
    >
      <GeographicView/>
    </Form.Item>
    <Form.Item
      name="contact"
      label={intl.formatMessage({ id: 'book.create.step2.field.contact.label', defaultMessage: '联系人' })}
      rules={[
        {
          required: false,
          message: intl.formatMessage({ id: 'book.create.step2.field.contact.required', defaultMessage: '请输入联系人姓名' })
        },
        {
          max: 10,
          type: 'string',
          message: intl.formatMessage({ id: 'book.create.step2.field.contact.max', defaultMessage: '姓名最多10个汉字！' }),
        },
      ]}
    >
      <Input/>
    </Form.Item>
    <Form.Item
      name="telephone"
      label={intl.formatMessage({ id: 'book.create.step2.field.telephone.label', defaultMessage: '联系电话' })}
      rules={[
        {
          required: false,
          message: intl.formatMessage({ id: 'book.create.step2.field.telephone.required', defaultMessage: '请输入电话' })
        },
        {
          pattern: /^1\d{10}$/,
          message: intl.formatMessage({ id: 'book.create.step2.field.telephone.invalid', defaultMessage: '手机号格式错误！' }),
        },
      ]}
    >
      <Input/>
    </Form.Item>
    <Form.Item label={intl.formatMessage({ id: 'book.create.step2.field.category.label', defaultMessage: '分类' })} name="termTypeIds"
               rules={[
                 {
                   required: true,
                   message: intl.formatMessage({ id: 'book.create.step2.field.category.required', defaultMessage: '请设置分类' })
                 },
               ]}
    >
      <TreeSelect {...tProps} />
    </Form.Item>
    <Form.Item label={intl.formatMessage({ id: 'book.create.step2.field.tags.label', defaultMessage: '标签' })} name="tagIds">
      <Select mode="tags" {...tagProps} />
    </Form.Item>
    <Form.Item
      name="visibilityScope"
      label={intl.formatMessage({ id: 'book.create.step2.field.visibilityScope.label', defaultMessage: '发现范围' })}
      tooltip={intl.formatMessage({
        id: 'book.create.step2.field.visibilityScope.tooltip',
        defaultMessage: '是否出现在前台列表/搜索；与「查看方式」独立',
      })}
    >
      <Select
        allowClear
        placeholder={intl.formatMessage({
          id: 'book.create.step2.field.visibilityScope.placeholder',
          defaultMessage: '默认：前台可发现',
        })}
        options={VISIBILITY_SCOPE_OPTIONS}
      />
    </Form.Item>
    <Form.Item label={intl.formatMessage({ id: 'book.create.step2.field.watermark.label', defaultMessage: '水印设置' })}>
      <Row gutter={8}>
        <Col span={16}>
          <Form.Item name="watermarkText" noStyle>
            <Input placeholder={intl.formatMessage({ id: 'book.create.step2.field.watermark.placeholder', defaultMessage: '请输入水印文字，如：WLDOS' })} />
          </Form.Item>
        </Col>
        <Col span={8}>
          <Form.Item name="watermarkEnabled" noStyle valuePropName="checked">
            <Switch
              checkedChildren={intl.formatMessage({ id: 'book.create.step2.field.watermark.enabled', defaultMessage: '启用' })}
              unCheckedChildren={intl.formatMessage({ id: 'book.create.step2.field.watermark.disabled', defaultMessage: '禁用' })}
            />
          </Form.Item>
        </Col>
      </Row>
    </Form.Item>
    <Form.Item
      name="privacyLevel"
      label={intl.formatMessage({
        id: 'book.create.step2.field.privacyLevel.label',
        defaultMessage: '查看方式',
      })}
      tooltip={intl.formatMessage({
        id: 'book.create.step2.field.privacyLevel.tooltip',
        defaultMessage: '访问与付费策略（存扩展 meta）；与「发现范围」独立',
      })}
      rules={[{ required: false, message: intl.formatMessage({ id: 'book.create.step2.field.privacyLevel.required', defaultMessage: '请设置查看方式' }) }]}
    >
      <Radio.Group options={privacyEnum} onChange={onPrivacyChange} />
    </Form.Item>
    {rewardStatus ? (
      <Form.Item
        label={intl.formatMessage({ id: 'book.create.step2.field.reward.label', defaultMessage: '打赏金额' })}
        name="reward"
        rules={[
          { required: true, message: intl.formatMessage({ id: 'book.create.step2.field.reward.required', defaultMessage: '请输入打赏金额' }) },
          { pattern: /^(\d+)((?:\.\d+)?)$/, message: intl.formatMessage({ id: 'book.create.step2.field.reward.invalid', defaultMessage: '请输入合法金额数字' }) },
        ]}
      >
        <Input
          prefix="￥"
          placeholder={intl.formatMessage({ id: 'book.create.step2.field.reward.placeholder', defaultMessage: '请输入打赏金额' })}
        />
      </Form.Item>
    ) : null}
  </>
  );
};

const picHandle = (coverUrl, beforeUp, handleChange, pic1Url, pic2Url, pic3Url, pic4Url) =>
  (<Row gutter={2}>
    <Col key={0}>
      <AvatarView avatar={coverUrl} params={upParams(532, 320)}
                  beforeUp={(file) => beforeUp(file, 133 / 80)}
                  onChange={(info) => handleChange(info, 1)} style="avatar"/>
    </Col>
    <Col key={1}>
      <AvatarView avatar={pic1Url} params={upParams(277, 277)}
                  beforeUp={(file) => beforeUp(file, 144 / 144)}
                  onChange={(info) => handleChange(info, 2)} style="mainPic"/>
    </Col>
    <Col key={2}>
      <AvatarView avatar={pic2Url} params={upParams(277, 277)}
                  beforeUp={(file) => beforeUp(file, 144 / 144)}
                  onChange={(info) => handleChange(info, 3)} style="mainPic"/>
    </Col>
    <Col key={3}>
      <AvatarView avatar={pic3Url} params={upParams(277, 277)}
                  beforeUp={(file) => beforeUp(file, 144 / 144)}
                  onChange={(info) => handleChange(info, 4)} style="mainPic"/>
    </Col>
    <Col key={4}>
      <AvatarView avatar={pic4Url} params={upParams(277, 277)}
                  beforeUp={(file) => beforeUp(file, 144 / 144)}
                  onChange={(info) => handleChange(info, 5)} style="mainPic"/>
    </Col>
  </Row>);

let blobFile = null;
// @todo 按照Pro Antd分步表单的方式，实现分片式信息表单，突出内容信息、交易信息。
const Step2 = ({data, dispatch, submitting, privacyEnum = []}) => {
  const intl = useIntl();
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
  // TinyMCE 按需加载状态
  const [tinymceReady, setTinymceReady] = useState(false);
  const [EditorComponent, setEditorComponent] = useState(null);
  const [rewardStatus, setReward] = useState(false);

  // TinyMCE 按需加载
  useEffect(() => {
    let mounted = true;
    const initTinyMCE = async () => {
      try {
        await loadTinyMCE();
        const { Editor } = await import('@tinymce/tinymce-react');
        if (mounted) {
          setEditorComponent(() => Editor);
          setTinymceReady(true);
        }
      } catch (error) {
        console.error('TinyMCE 加载失败:', error);
      }
    };
    initTinyMCE();
    return () => { mounted = false; };
  }, []);

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

  useEffect(() => {
    setReward(data?.privacyLevel === 'reward');
  }, [data?.id, data?.privacyLevel]);

  useEffect(() => {
    form.setFieldsValue({pubContent: content});
  }, [content, form]);

  if (!data) {
    return null;
  }

  const {validateFields, getFieldsValue} = form;

  const onPrev = () => {
    if (dispatch) {
      const values = getFieldsValue();
      dispatch({
        type: 'bookInfo/saveStepFormData',
        payload: {...data, ...values},
      });
      dispatch({
        type: 'bookInfo/saveCurrentStep',
        payload: 'category',
      });
    }
  };

  const onValidateForm = async () => {
    const values = await validateFields();

    if (!values.pubContent) {
      message.error(intl.formatMessage({ id: 'book.create.step2.message.contentRequired', defaultMessage: '请描述详情！' }));
      return;
    }

    const {geographic = {}, ...otherValues} = values;
    const base = {
      province: geographic?.province?.key?? '',
      city: geographic?.city?.key?? '',
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
      return message.error(intl.formatMessage({ id: 'book.create.step2.message.imageTooLarge', defaultMessage: '图片大小超限' })).then(() => false);
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
      message.success(intl.formatMessage({ id: 'book.create.step2.message.uploadSuccess', defaultMessage: '上传成功！' }), 1).then(() => {
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
      message.error(intl.formatMessage({ id: 'book.create.step2.message.uploadFail', defaultMessage: '上传失败！' }), 2);
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
    placeholder: intl.formatMessage({ id: 'book.create.step2.field.category.placeholder', defaultMessage: '请选择' }),
    style: {
      width: '100%',
    },
  };

  const tagProps = {
    value: tagValue,
    onChange: setTagValue,
    placeholder: intl.formatMessage({ id: 'book.create.step2.field.tags.placeholder', defaultMessage: '设置标签' }),
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
            <Form.Item
              label={intl.formatMessage({ id: 'book.create.step2.section.images', defaultMessage: '封面与主图' })}
              className={styles.main}
            >
              {picHandle(coverUrl, beforeUp, handleChange, pic1Url, pic2Url, pic3Url, pic4Url)}
            </Form.Item>
            {formContent(privacyEnum, typeProps, tagProps, intl, {
              rewardStatus,
              onPrivacyChange: (e) => {
                if (e?.target?.value === 'reward') setReward(true);
                else setReward(false);
              },
            })}
            <Form.Item name="pubContent" noStyle>
              <textarea hidden
                    value={content} onChange={(e) => setContent(e.target.value)}/>
              {tinymceReady && EditorComponent ? (
                <EditorComponent
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
              ) : (
                <div style={{ height: '250px', display: 'flex', alignItems: 'center', justifyContent: 'center', border: '1px solid #d9d9d9', borderRadius: '4px' }}>
                  <Spin tip="编辑器加载中..." />
                </div>
              )}
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
              <Button onClick={onPrev}>
                <FormattedMessage id="book.create.step2.button.prev" defaultMessage="上一步" />
              </Button>
              <Button type="primary" onClick={onValidateForm} loading={submitting}  style={{ marginLeft: 8, }}>
                <FormattedMessage id="book.create.step2.button.submit" defaultMessage="提交" />
              </Button>
            </Form.Item>
          </Form>
        </div>
      </div>
      <Divider style={{ margin: '40px 0 24px', }} />
      <div className={styles.desc}>
        <h3>
          <FormattedMessage id="book.create.step2.guide.title" defaultMessage="说明" />
        </h3>
        <h4>
          <FormattedMessage id="book.create.step2.guide.category.title" defaultMessage="选择分类" />
        </h4>
        <p>
          <FormattedMessage
            id="book.create.step2.guide.category.desc"
            defaultMessage="PC端鼠标移动到分类上，弹出小类选择框，点击选定的小类打开信息编辑页；移动端需要点击分类弹出小类选择框。"
          />
        </p>
        <h4>
          <FormattedMessage id="book.create.step2.guide.info.title" defaultMessage="填写信息" />
        </h4>
        <p>
          <FormattedMessage
            id="book.create.step2.guide.info.desc"
            defaultMessage="填写信息需要上传封面图和主图，如果不上传，会展示默认封面，默认封面不具备显著特征不易被识别，主图至少上传一张。"
          />
        </p>
      </div>
      {picModal(cropVisible, setCropVisible, cropRef, cropSrc, aspect, setAspectRatio, setRotateTo, saveCropper, type)}
    </>
  );
};

export default connect(({bookInfo, loading}) => ({
  submitting: loading.effects['bookInfo/submitStepForm'],
  data: bookInfo.step,
  privacyEnum: bookInfo.privacyEnum,
}))(Step2);
