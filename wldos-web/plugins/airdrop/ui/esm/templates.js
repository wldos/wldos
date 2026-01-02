import { _ as _slicedToArray, a as _asyncToGenerator, b as _regenerator, g as templateAPI } from './api-72a577fe.js';
const React__default = window.React;
const { useState } = window.React;
const { Select, Input, Form, Button, Table, Modal, Space, message } = window.antd;

var Option = Select.Option;
var TextArea = Input.TextArea;
var AirdropTemplates = function AirdropTemplates() {
  var _useState = useState([]),
    _useState2 = _slicedToArray(_useState, 2),
    templates = _useState2[0],
    setTemplates = _useState2[1];
  var _useState3 = useState(false),
    _useState4 = _slicedToArray(_useState3, 2),
    loading = _useState4[0],
    setLoading = _useState4[1];
  var _useState5 = useState(false),
    _useState6 = _slicedToArray(_useState5, 2),
    createModalVisible = _useState6[0],
    setCreateModalVisible = _useState6[1];
  var _useState7 = useState(false),
    _useState8 = _slicedToArray(_useState7, 2),
    editModalVisible = _useState8[0],
    setEditModalVisible = _useState8[1];
  var _useState9 = useState(null),
    _useState0 = _slicedToArray(_useState9, 2),
    currentTemplate = _useState0[0],
    setCurrentTemplate = _useState0[1];
  var _Form$useForm = Form.useForm(),
    _Form$useForm2 = _slicedToArray(_Form$useForm, 1),
    form = _Form$useForm2[0];

  // 加载模板列表
  var loadTemplates = /*#__PURE__*/function () {
    var _ref = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee() {
      var _res$data, res, data, _t;
      return _regenerator().w(function (_context) {
        while (1) switch (_context.p = _context.n) {
          case 0:
            _context.p = 0;
            setLoading(true);
            _context.n = 1;
            return templateAPI.getTemplates();
          case 1:
            res = _context.v;
            data = (_res$data = res === null || res === void 0 ? void 0 : res.data) !== null && _res$data !== void 0 ? _res$data : [];
            setTemplates(data);
            _context.n = 3;
            break;
          case 2:
            _context.p = 2;
            _t = _context.v;
            message.error('加载模板列表失败: ' + _t.message);
          case 3:
            _context.p = 3;
            setLoading(false);
            return _context.f(3);
          case 4:
            return _context.a(2);
        }
      }, _callee, null, [[0, 2, 3, 4]]);
    }));
    return function loadTemplates() {
      return _ref.apply(this, arguments);
    };
  }();

  // 组件挂载时加载数据
  React__default.useEffect(function () {
    loadTemplates();
  }, []);

  // 事件处理函数
  var handleCreateTemplate = function handleCreateTemplate() {
    setCurrentTemplate(null);
    form.resetFields();
    setCreateModalVisible(true);
  };
  var handleUseTemplate = function handleUseTemplate(record) {
    message.info("\u4F7F\u7528\u6A21\u677F: ".concat(record.templateName, "\uFF0C\u529F\u80FD\u5F00\u53D1\u4E2D..."));
  };
  var handleEditTemplate = function handleEditTemplate(record) {
    setCurrentTemplate(record);
    form.setFieldsValue(record);
    setEditModalVisible(true);
  };
  var handleSaveTemplate = /*#__PURE__*/function () {
    var _ref2 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee2(values) {
      var _t2;
      return _regenerator().w(function (_context2) {
        while (1) switch (_context2.p = _context2.n) {
          case 0:
            _context2.p = 0;
            if (!currentTemplate) {
              _context2.n = 2;
              break;
            }
            _context2.n = 1;
            return templateAPI.updateTemplate(currentTemplate.id, values);
          case 1:
            message.success('模板更新成功');
            setEditModalVisible(false);
            _context2.n = 4;
            break;
          case 2:
            _context2.n = 3;
            return templateAPI.createTemplate(values);
          case 3:
            message.success('模板创建成功');
            setCreateModalVisible(false);
          case 4:
            form.resetFields();
            setCurrentTemplate(null);
            loadTemplates();
            _context2.n = 6;
            break;
          case 5:
            _context2.p = 5;
            _t2 = _context2.v;
            message.error((currentTemplate ? '更新' : '创建') + '模板失败: ' + _t2.message);
          case 6:
            return _context2.a(2);
        }
      }, _callee2, null, [[0, 5]]);
    }));
    return function handleSaveTemplate(_x) {
      return _ref2.apply(this, arguments);
    };
  }();
  var handleDeleteTemplate = function handleDeleteTemplate(record) {
    Modal.confirm({
      title: '确认删除',
      content: "\u786E\u5B9A\u8981\u5220\u9664\u6A21\u677F \"".concat(record.templateName, "\" \u5417\uFF1F"),
      onOk: function onOk() {
        return _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee3() {
          var _t3;
          return _regenerator().w(function (_context3) {
            while (1) switch (_context3.p = _context3.n) {
              case 0:
                _context3.p = 0;
                _context3.n = 1;
                return templateAPI.deleteTemplate(record.id);
              case 1:
                message.success('模板删除成功');
                loadTemplates(); // 重新加载列表
                _context3.n = 3;
                break;
              case 2:
                _context3.p = 2;
                _t3 = _context3.v;
                message.error('删除模板失败: ' + _t3.message);
              case 3:
                return _context3.a(2);
            }
          }, _callee3, null, [[0, 2]]);
        }))();
      }
    });
  };
  var columns = [{
    title: '模板名称',
    dataIndex: 'templateName',
    key: 'templateName'
  }, {
    title: '模板类型',
    dataIndex: 'templateType',
    key: 'templateType'
  }, {
    title: '脚本类型',
    dataIndex: 'scriptType',
    key: 'scriptType'
  }, {
    title: '版本',
    dataIndex: 'version',
    key: 'version'
  }, {
    title: '使用次数',
    dataIndex: 'usageCount',
    key: 'usageCount'
  }, {
    title: '描述',
    dataIndex: 'description',
    key: 'description'
  }, {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime'
  }, {
    title: '操作',
    key: 'action',
    render: function render(_, record) {
      return /*#__PURE__*/React__default.createElement(Space, null, /*#__PURE__*/React__default.createElement(Button, {
        type: "link",
        size: "small",
        onClick: function onClick() {
          return handleUseTemplate(record);
        }
      }, "\u4F7F\u7528"), /*#__PURE__*/React__default.createElement(Button, {
        type: "link",
        size: "small",
        onClick: function onClick() {
          return handleEditTemplate(record);
        }
      }, "\u7F16\u8F91"), /*#__PURE__*/React__default.createElement(Button, {
        type: "link",
        size: "small",
        danger: true,
        onClick: function onClick() {
          return handleDeleteTemplate(record);
        }
      }, "\u5220\u9664"));
    }
  }];
  return /*#__PURE__*/React__default.createElement("div", {
    className: "airdrop-templates"
  }, /*#__PURE__*/React__default.createElement("div", {
    style: {
      marginBottom: 16
    }
  }, /*#__PURE__*/React__default.createElement(Button, {
    type: "primary",
    onClick: handleCreateTemplate
  }, "\u65B0\u5EFA\u6A21\u677F"), /*#__PURE__*/React__default.createElement(Button, {
    style: {
      marginLeft: 8
    },
    onClick: loadTemplates
  }, "\u5237\u65B0")), /*#__PURE__*/React__default.createElement(Table, {
    columns: columns,
    dataSource: templates,
    rowKey: "id",
    loading: loading,
    pagination: {
      pageSize: 10,
      showSizeChanger: true,
      showQuickJumper: true,
      showTotal: function showTotal(total) {
        return "\u5171 ".concat(total, " \u6761\u8BB0\u5F55");
      }
    }
  }), /*#__PURE__*/React__default.createElement(Modal, {
    title: currentTemplate ? '编辑模板' : '创建模板',
    visible: createModalVisible || editModalVisible,
    onCancel: function onCancel() {
      setCreateModalVisible(false);
      setEditModalVisible(false);
      form.resetFields();
      setCurrentTemplate(null);
    },
    onOk: function onOk() {
      return form.submit();
    },
    width: 800
  }, /*#__PURE__*/React__default.createElement(Form, {
    form: form,
    layout: "vertical",
    onFinish: handleSaveTemplate
  }, /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "templateName",
    label: "\u6A21\u677F\u540D\u79F0",
    rules: [{
      required: true,
      message: '请输入模板名称'
    }]
  }, /*#__PURE__*/React__default.createElement(Input, {
    placeholder: "\u8BF7\u8F93\u5165\u6A21\u677F\u540D\u79F0"
  })), /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "templateType",
    label: "\u6A21\u677F\u7C7B\u578B",
    rules: [{
      required: true,
      message: '请选择模板类型'
    }]
  }, /*#__PURE__*/React__default.createElement(Select, {
    placeholder: "\u8BF7\u9009\u62E9\u6A21\u677F\u7C7B\u578B"
  }, /*#__PURE__*/React__default.createElement(Option, {
    value: "standard"
  }, "\u6807\u51C6\u6A21\u677F"), /*#__PURE__*/React__default.createElement(Option, {
    value: "custom"
  }, "\u81EA\u5B9A\u4E49\u6A21\u677F"))), /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "scriptType",
    label: "\u811A\u672C\u7C7B\u578B",
    initialValue: "javascript"
  }, /*#__PURE__*/React__default.createElement(Select, null, /*#__PURE__*/React__default.createElement(Option, {
    value: "javascript"
  }, "JavaScript"), /*#__PURE__*/React__default.createElement(Option, {
    value: "python"
  }, "Python"), /*#__PURE__*/React__default.createElement(Option, {
    value: "shell"
  }, "Shell"))), /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "scriptContent",
    label: "\u811A\u672C\u5185\u5BB9",
    rules: [{
      required: true,
      message: '请输入脚本内容'
    }]
  }, /*#__PURE__*/React__default.createElement(TextArea, {
    rows: 10,
    placeholder: "\u8BF7\u8F93\u5165\u811A\u672C\u5185\u5BB9"
  })), /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "description",
    label: "\u63CF\u8FF0"
  }, /*#__PURE__*/React__default.createElement(TextArea, {
    rows: 3,
    placeholder: "\u8BF7\u8F93\u5165\u6A21\u677F\u63CF\u8FF0"
  })))));
};

export { AirdropTemplates, AirdropTemplates as default };
