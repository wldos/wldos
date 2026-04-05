import React, {useEffect, useState} from 'react';
import {Button, Form, Input, Select, TreeSelect, Typography} from 'antd';
import FullscreenModal from '@/components/FullscreenModal';
import PortalStaticExtraPropsEditor from './PortalStaticExtraPropsEditor';

const FormItem = Form.Item;

const formLayout = {
  labelCol: {
    span: 7,
  },
  wrapperCol: {
    span: 13,
  },
};

const UpdateForm = (props) => {
  const [form] = Form.useForm();
  const [urlVisible, setUrlVisible] = useState(false);
  const [moduleNameWatch, setModuleNameWatch] = useState(props.values?.moduleName);
  const [urlWatch, setUrlWatch] = useState(props.values?.url);
  const {
    onSubmit: handleUpdate,
    onCancel: handleUpdateModalVisible,
    updateModalVisible,
    values,
    templates,
    categoryList
  } = props;

  useEffect(() => {
    setUrlVisible(values?.url);
  }, [values?.url]);

  useEffect(() => {
    if (updateModalVisible && values) {
      setModuleNameWatch(values.moduleName);
      setUrlWatch(values.url);
    }
  }, [updateModalVisible, values]);

  const handleNext = async () => {
    const fieldsValue = await form.validateFields();
    const value = {...values, ...fieldsValue};
    handleUpdate(value);
  };

  const handleChange = (v) => {
    setUrlVisible(v === 'url' || v === 'component');
    setModuleNameWatch(v);
  };

  const isPortalStaticHome =
    moduleNameWatch === 'component' && String(urlWatch || '').trim() === 'home/static';

  const renderContent = () => {
    return (
      <>
        <FormItem name="moduleName" label="动态模板">
          <Select
            style={{
              width: '100%',
            }}
            filterOption={(input, option) =>
              option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
            }
            onChange={handleChange}
            options={templates}
          >
          </Select>
        </FormItem>
        <FormItem name="termTypeId" label="关联分类">
          <TreeSelect
            showSearch
            treeData={categoryList}
            treeDefaultExpandAll
            allowClear
            dropdownStyle={{ maxHeight: 400, overflow: 'auto'}}
            placeholder="请选择"
            treeNodeFilterProp="title"
          />
        </FormItem>
        {urlVisible && (
          <FormItem
            name="url"
            label="指定url"
            rules={[
              {
                required: false,
                message: '最长256个字符！',
                max: 256,
              },
            ]}
          >
            <Input
              placeholder="静态门户填 home/static；外链填完整 http(s)://..."
              onChange={(e) => setUrlWatch(e.target.value)}
            />
          </FormItem>
        )}
        <FormItem
          name="extraProps"
          label={isPortalStaticHome ? '门户首页版块（分区块 HTML）' : '扩展配置(JSON)'}
          tooltip={
            isPortalStaticHome
              ? '仅当动态模板=绑定组件且指定 url=home/static 时启用分区块多行文本编辑原始 HTML；存库仍为 portalStatic JSON。'
              : '可选。JSON 扩展；门户静态首页见 docs/CMS官网静态首页可配置方案.md'
          }
          labelCol={isPortalStaticHome ? { span: 24 } : undefined}
          wrapperCol={isPortalStaticHome ? { span: 24 } : undefined}
        >
          {isPortalStaticHome ? (
            <PortalStaticExtraPropsEditor />
          ) : (
            <Input.TextArea
              rows={10}
              placeholder={`示例（勿带注释）：\n{\n  "schemaType": "portalStatic",\n  "schemaVersion": 1,\n  "sections": {\n    "hero": "<div>自定义 Hero HTML</div>"\n  }\n}`}
            />
          )}
        </FormItem>
        {!isPortalStaticHome ? (
          <Typography.Paragraph type="secondary" style={{ marginBottom: 0 }}>
            若使用门户静态首页：模板选「绑定组件」、指定 url 填 <Typography.Text code>home/static</Typography.Text>
            ，将切换为分区块 HTML 文本编辑。其它模板仍可在此填原始 JSON。
          </Typography.Paragraph>
        ) : null}
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

  return (
    <FullscreenModal
      width={isPortalStaticHome ? 960 : 640}
      bodyStyle={{
        padding: '32px 40px 48px'
      }}
      destroyOnClose
      title={`域资源配置-${props.values.resourceName}`}
      visible={updateModalVisible}
      footer={renderFooter()}
      onCancel={() => handleUpdateModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
        initialValues={{
          moduleName: props.values.moduleName,
          termTypeId: props.values.termTypeId,
          url: props.values.url,
          extraProps: props.values.extraProps,
        }}
        onValuesChange={(changed) => {
          if (Object.prototype.hasOwnProperty.call(changed, 'moduleName')) {
            setModuleNameWatch(changed.moduleName);
          }
          if (Object.prototype.hasOwnProperty.call(changed, 'url')) {
            setUrlWatch(changed.url);
          }
        }}
      >
        {renderContent()}
      </Form>
    </FullscreenModal>
  );
};

export default UpdateForm;
