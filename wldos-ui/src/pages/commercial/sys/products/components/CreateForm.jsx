import React, { useEffect, useState, useMemo } from 'react';
import { Button, Form, Input, InputNumber, Select, TreeSelect, Row, Col, Card, Space, Tooltip } from 'antd';
import { AppstoreOutlined, FileTextOutlined, InfoCircleOutlined, PlusOutlined, TagsOutlined, DollarOutlined } from '@ant-design/icons';
import FullscreenModal from '@/components/FullscreenModal';
import DocSelectField from '@/components/DocSelectField';
import { getPluginListForSelect, getStoreProductCategories, getProductCategoryType } from '../service';

const FormItem = Form.Item;
const { Option } = Select;
const { SHOW_PARENT } = TreeSelect;

const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 18 },
};
const labelStyle = { whiteSpace: 'normal', wordBreak: 'keep-all' };

const commercialTypeOptions = [
  { value: 'OPEN_SOURCE', label: '开源' },
  { value: 'FREE_COMMERCIAL', label: '免费商业' },
  { value: 'PAID_COMMERCIAL', label: '付费' },
];

const productTypeOptions = [
  { value: 'PLUGIN', label: '插件' },
  { value: 'SCENARIO', label: '场景 / 解决方案' },
];

const sceneOptions = [
  { value: 'PERSONAL', label: '个人' },
  { value: 'ENTERPRISE', label: '企业' },
  { value: 'IOT', label: '云物互联' },
];

const licenseTypeOptions = [
  { value: 'TIME_BASED', label: '按时间购买' },
  { value: 'LIFETIME', label: '终身制' },
];

/** 分类树：value 用 categoryCode（分类实际值），title 用 categoryName；long 在 JSON 里已是字符串，直接用 */
function toTreeData(items) {
  if (!Array.isArray(items)) return [];
  return items.filter(Boolean).map((item) => {
    const code = item.categoryCode;
    const val = code != null && code !== '' ? code : undefined;
    const title = item.categoryName || item.name || item.title || val || '';
    const isRoot =
      (code != null && String(code) === '0') ||
      (item.categoryName === '根分类' && (item.parentId === null || item.parentId === undefined));
    const children = item.children?.length ? toTreeData(item.children) : undefined;
    if (val == null) return null;
    return {
      title,
      value: val,
      key: val,
      disabled: isRoot,
      selectable: !isRoot,
      children: children?.length ? children : undefined,
    };
  }).filter(Boolean);
}

const CreateForm = ({ modalVisible, onSubmit, onCancel }) => {
  const [form] = Form.useForm();
  const [pluginList, setPluginList] = useState([]);
  const [categories, setCategories] = useState([]);
  const [categoriesLoading, setCategoriesLoading] = useState(false);
  const [categoryType, setCategoryType] = useState(null);
  const [productType, setProductType] = useState('PLUGIN');

  const categoryTreeData = useMemo(() => toTreeData(categories), [categories]);

  useEffect(() => {
    if (modalVisible) {
      form.resetFields();
      form.setFieldsValue({
        productType: 'PLUGIN',
        isFeatured: '0',
        commercialType: 'OPEN_SOURCE',
        price: 0,
        displayOrder: 1,
      });
      setProductType('PLUGIN');
      getPluginListForSelect().then(setPluginList);
      getProductCategoryType().then(setCategoryType);
      setCategoriesLoading(true);
      getStoreProductCategories()
        .then(setCategories)
        .finally(() => setCategoriesLoading(false));
    }
  }, [modalVisible, form]);

  const handleOk = async () => {
    try {
      const values = await form.validateFields();
      values.productType = productType || 'PLUGIN';
      // 多标签：参照作品创建，提交 tagIds（实际为标签名称数组）
      values.tagIds = Array.isArray(values.tags) ? values.tags.filter(Boolean) : [];
      // 不提交表单内部字段 tags（后端不接收 tags 字段）
      delete values.tags;
      // 多分类：提交 categoryTermTypeIds 数组（与 k_term_object 一致）
      const rawIds = values.categoryTermTypeIds || values.categoryTermTypeId;
      const ids = Array.isArray(rawIds) ? rawIds : (rawIds != null && rawIds !== '' ? [rawIds] : []);
      // wldos 约定：long/id 在前端一律按字符串处理，由后端转换为 long（雪花ID）
      values.categoryTermTypeIds = ids;
      delete values.categoryTermTypeId;
      // 分类：参照作品创建字段命名，提交 termTypeIds
      values.termTypeIds = values.categoryTermTypeIds;
      delete values.categoryTermTypeIds;
      // 否则保持原值（字符串），不转 Number，避免 long 在 JS 中丢失精度
      if (Array.isArray(values.scene)) {
        values.scene = values.scene.filter(Boolean).join(',');
      } else if (values.scene === undefined || values.scene === null) {
        values.scene = undefined;
      }
      if (Array.isArray(values.licenseType)) {
        values.licenseType = values.licenseType.filter(Boolean).join(',');
      } else if (values.licenseType === undefined || values.licenseType === null) {
        values.licenseType = undefined;
      }
      if (values.supportRecommendDiscount === undefined || values.supportRecommendDiscount === null) {
        values.supportRecommendDiscount = '0';
      }
      await onSubmit(values);
    } catch (e) {
      // 校验失败
    }
  };

  const renderContent = () => (
    <div>
      <Card
        title={
          <Space>
            <AppstoreOutlined style={{ color: '#1890ff' }} />
            基础信息
          </Space>
        }
        size="small"
        style={{ marginBottom: '16px' }}
        extra={
          <Tooltip title="产品类型与展示标题、简介">
            <InfoCircleOutlined />
          </Tooltip>
        }
      >
        <Row gutter={16}>
          <Col span={12}>
            <FormItem
              name="productType"
              label={
                <Space>
                  产品类型
                  <Tooltip title="插件或场景/解决方案">
                    <InfoCircleOutlined style={{ color: '#999' }} />
                  </Tooltip>
                </Space>
              }
            >
              <Select
                placeholder="请选择产品类型"
                value={productType}
                onChange={(val) => {
                  setProductType(val);
                  form.setFieldsValue({ productType: val, pluginId: undefined, productRefId: undefined });
                }}
              >
                {productTypeOptions.map((o) => (
                  <Option key={o.value} value={o.value}>{o.label}</Option>
                ))}
              </Select>
            </FormItem>
            <FormItem
              name="displayTitle"
              label={
                <Space>
                  展示标题
                  <Tooltip title="留空则使用插件名">
                    <InfoCircleOutlined style={{ color: '#999' }} />
                  </Tooltip>
                </Space>
              }
            >
              <Input placeholder="可选" />
            </FormItem>
          </Col>
          <Col span={12}>
            {productType === 'PLUGIN' ? (
              <FormItem
                name="pluginId"
                label={
                  <Space>
                    关联插件
                    <Tooltip title="必选，关联 store_plugin">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
                rules={[{ required: true, message: '请选择插件' }]}
              >
                <Select
                  placeholder="请选择插件"
                  showSearch
                  optionFilterProp="children"
                  onChange={(pluginId) => {
                    const plugin = pluginList.find((p) => p.id === pluginId);
                    if (plugin?.icon && form.getFieldValue('coverImage') === undefined) {
                      form.setFieldsValue({ coverImage: plugin.icon });
                    }
                  }}
                >
                  {pluginList.map((p) => (
                    <Option key={p.id} value={p.id}>{p.name} ({p.pluginCode})</Option>
                  ))}
                </Select>
              </FormItem>
            ) : (
              <FormItem
                name="productRefId"
                label={
                  <Space>
                    产品标识
                    <Tooltip title="非插件型产品时，业务系统产品编码/ID">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
                rules={[{ required: true, message: '请输入产品标识' }]}
              >
                <Input placeholder="例如：应用编码、外部系统产品ID" />
              </FormItem>
            )}
            <FormItem
              name="displaySummary"
              label={
                <Space>
                  展示简介
                  <Tooltip title="列表/详情展示用">
                    <InfoCircleOutlined style={{ color: '#999' }} />
                  </Tooltip>
                </Space>
              }
            >
              <Input.TextArea rows={3} placeholder="可选" />
            </FormItem>
          </Col>
        </Row>
      </Card>

      <Card
        title={
          <Space>
            <FileTextOutlined style={{ color: '#52c41a' }} />
            展示配置
          </Space>
        }
        size="small"
        style={{ marginBottom: '16px' }}
        extra={
          <Tooltip title="适用场景、许可方式、推荐优惠、封面">
            <InfoCircleOutlined />
          </Tooltip>
        }
      >
        <Row gutter={16}>
          <Col span={12}>
            <FormItem
              name="scene"
              label={
                <Space>
                  适用场景
                  <Tooltip title="个人/企业/云物互联，可多选">
                    <InfoCircleOutlined style={{ color: '#999' }} />
                  </Tooltip>
                </Space>
              }
            >
              <Select mode="multiple" placeholder="请选择适用场景" allowClear>
                {sceneOptions.map((o) => (
                  <Option key={o.value} value={o.value}>{o.label}</Option>
                ))}
              </Select>
            </FormItem>
            <FormItem
              name="licenseType"
              label={
                <Space>
                  许可类型
                  <Tooltip title="按时间/终身制，可多选">
                    <InfoCircleOutlined style={{ color: '#999' }} />
                  </Tooltip>
                </Space>
              }
            >
              <Select mode="multiple" placeholder="按时间/终身制" allowClear>
                {licenseTypeOptions.map((o) => (
                  <Option key={o.value} value={o.value}>{o.label}</Option>
                ))}
              </Select>
            </FormItem>
            <FormItem
              name="supportRecommendDiscount"
              label={
                <Space>
                  推荐优惠
                  <Tooltip title="是否支持推荐码优惠">
                    <InfoCircleOutlined style={{ color: '#999' }} />
                  </Tooltip>
                </Space>
              }
              initialValue="0"
            >
              <Select>
                <Option value="0">否</Option>
                <Option value="1">是</Option>
              </Select>
            </FormItem>
          </Col>
          <Col span={12}>
            <FormItem
              name="originalPriceTimeBased"
              label={<span style={labelStyle}>按时间购买原价 <Tooltip title="选填，列表/详情展示用"><InfoCircleOutlined style={{ color: '#999' }} /></Tooltip></span>}
            >
              <InputNumber min={0} step={0.01} style={{ width: '100%' }} placeholder="选填" />
            </FormItem>
            <FormItem
              name="originalPriceLifetime"
              label={<span style={labelStyle}>终身制原价 <Tooltip title="选填，列表/详情展示用"><InfoCircleOutlined style={{ color: '#999' }} /></Tooltip></span>}
            >
              <InputNumber min={0} step={0.01} style={{ width: '100%' }} placeholder="选填" />
            </FormItem>
            <FormItem
              name="coverImage"
              label={
                <Space>
                  封面图 URL
                  <Tooltip title="可选">
                    <InfoCircleOutlined style={{ color: '#999' }} />
                  </Tooltip>
                </Space>
              }
            >
              <Input placeholder="可选" />
            </FormItem>
          </Col>
        </Row>
      </Card>

      <Card
        title={
          <Space>
            <TagsOutlined style={{ color: '#fa8c16' }} />
            分类与标签
          </Space>
        }
        size="small"
        style={{ marginBottom: '16px' }}
        extra={
          <Tooltip title="与【系统管理-类型管理】插件分类一致">
            <InfoCircleOutlined />
          </Tooltip>
        }
      >
        <Row gutter={16}>
          <Col span={12}>
            <FormItem
              name="categoryTermTypeIds"
              label={
                <Space>
                  分类
                  <Tooltip title="插件分类，可多选（根分类不可选），与 k_term_object 多分类一致">
                    <InfoCircleOutlined style={{ color: '#999' }} />
                  </Tooltip>
                </Space>
              }
            >
              <TreeSelect
                placeholder="请选择分类（可多选）"
                allowClear
                showSearch
                multiple
                treeCheckable
                showCheckedStrategy={SHOW_PARENT}
                treeData={categoryTreeData}
                treeDefaultExpandAll
                treeLine
                treeNodeFilterProp="title"
                dropdownStyle={{ maxHeight: 300, overflow: 'auto' }}
                style={{ width: '100%' }}
                loading={categoriesLoading}
              />
            </FormItem>
          </Col>
          <Col span={12}>
            <FormItem
              name="tags"
              label={
                <Space>
                  标签
                  <Tooltip title="可输入后回车添加">
                    <InfoCircleOutlined style={{ color: '#999' }} />
                  </Tooltip>
                </Space>
              }
            >
              <Select mode="tags" placeholder="输入标签后回车" tokenSeparators={[',']} />
            </FormItem>
          </Col>
        </Row>
      </Card>

      <Card
        title={
          <Space>
            <DollarOutlined style={{ color: '#722ed1' }} />
            价格与状态
          </Space>
        }
        size="small"
        style={{ marginBottom: '16px' }}
        extra={
          <Tooltip title="价格、商业类型、精选、展示顺序、详情文档">
            <InfoCircleOutlined />
          </Tooltip>
        }
      >
        <Row gutter={16}>
          <Col span={12}>
            <FormItem
              name="price"
              label={
                <Space>
                  价格
                  <Tooltip title="0 表示免费">
                    <InfoCircleOutlined style={{ color: '#999' }} />
                  </Tooltip>
                </Space>
              }
              initialValue={0}
            >
              <InputNumber min={0} step={0.01} style={{ width: '100%' }} />
            </FormItem>
            <FormItem
              name="commercialType"
              label={
                <Space>
                  商业类型
                  <Tooltip title="开源/免费商业/付费">
                    <InfoCircleOutlined style={{ color: '#999' }} />
                  </Tooltip>
                </Space>
              }
            >
              <Select>
                {commercialTypeOptions.map((o) => (
                  <Option key={o.value} value={o.value}>{o.label}</Option>
                ))}
              </Select>
            </FormItem>
            <FormItem
              name="isFeatured"
              label={
                <Space>
                  是否精选
                  <Tooltip title="精选产品优先展示">
                    <InfoCircleOutlined style={{ color: '#999' }} />
                  </Tooltip>
                </Space>
              }
              initialValue="0"
            >
              <Select>
                <Option value="0">否</Option>
                <Option value="1">是</Option>
              </Select>
            </FormItem>
            <FormItem
              name="displayOrder"
              label={
                <Space>
                  展示顺序
                  <Tooltip title="数值越大越靠前，从 1 开始">
                    <InfoCircleOutlined style={{ color: '#999' }} />
                  </Tooltip>
                </Space>
              }
              initialValue={1}
            >
              <InputNumber min={1} max={100} style={{ width: '100%' }} />
            </FormItem>
          </Col>
          <Col span={12}>
            <DocSelectField
              name="pubId"
              label="产品详情 CMS 文档"
              extra="关联存档文档，富文本走 CMS"
              placeholder="可选，点击选择"
              buttonText="选择"
              modalTitle="选择产品详情文档"
            />
          </Col>
        </Row>
      </Card>
    </div>
  );

  const renderFooter = () => (
    <Space>
      <Button onClick={onCancel}>取消</Button>
      <Button type="primary" onClick={handleOk}>确定</Button>
    </Space>
  );

  return (
    <FullscreenModal
      width={800}
      bodyStyle={{ padding: '24px' }}
      destroyOnClose
      title={
        <Space>
          <PlusOutlined style={{ color: '#52c41a' }} />
          新建展示配置
        </Space>
      }
      visible={modalVisible}
      footer={renderFooter()}
      onCancel={onCancel}
    >
      <Form
        {...formLayout}
        form={form}
        initialValues={{
          productType: 'PLUGIN',
          isFeatured: '0',
          commercialType: 'OPEN_SOURCE',
          price: 0,
          displayOrder: 1,
        }}
      >
        {renderContent()}
      </Form>
    </FullscreenModal>
  );
};

export default CreateForm;
