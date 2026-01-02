import { _ as _slicedToArray, a as _asyncToGenerator, b as _regenerator, J as marketAPI } from './api-72a577fe.js';
const React = window.React;
const React__default = window.React;
const { useState, useEffect } = window.React;
const { Select, Input, Form, Card, Row, Col, Space, Button, Table, Modal, Descriptions, Rate, Divider, Alert, message } = window.antd;
import { A as AntdIcon, _ as _objectSpread2 } from './AntdIcon-16063a91.js';

// This icon file is generated automatically.
var DownloadOutlined$2 = { "icon": { "tag": "svg", "attrs": { "viewBox": "64 64 896 896", "focusable": "false" }, "children": [{ "tag": "path", "attrs": { "d": "M505.7 661a8 8 0 0012.6 0l112-141.7c4.1-5.2.4-12.9-6.3-12.9h-74.1V168c0-4.4-3.6-8-8-8h-60c-4.4 0-8 3.6-8 8v338.3H400c-6.7 0-10.4 7.7-6.3 12.9l112 141.8zM878 626h-60c-4.4 0-8 3.6-8 8v154H214V634c0-4.4-3.6-8-8-8h-60c-4.4 0-8 3.6-8 8v198c0 17.7 14.3 32 32 32h684c17.7 0 32-14.3 32-32V634c0-4.4-3.6-8-8-8z" } }] }, "name": "download", "theme": "outlined" };
var DownloadOutlinedSvg = DownloadOutlined$2;

var DownloadOutlined = function DownloadOutlined(props, ref) {
  return /*#__PURE__*/React.createElement(AntdIcon, _objectSpread2(_objectSpread2({}, props), {}, {
    ref: ref,
    icon: DownloadOutlinedSvg
  }));
};
var RefIcon$3 = /*#__PURE__*/React.forwardRef(DownloadOutlined);
var DownloadOutlined$1 = RefIcon$3;

// This icon file is generated automatically.
var EyeOutlined$2 = { "icon": { "tag": "svg", "attrs": { "viewBox": "64 64 896 896", "focusable": "false" }, "children": [{ "tag": "path", "attrs": { "d": "M942.2 486.2C847.4 286.5 704.1 186 512 186c-192.2 0-335.4 100.5-430.2 300.3a60.3 60.3 0 000 51.5C176.6 737.5 319.9 838 512 838c192.2 0 335.4-100.5 430.2-300.3 7.7-16.2 7.7-35 0-51.5zM512 766c-161.3 0-279.4-81.8-362.7-254C232.6 339.8 350.7 258 512 258c161.3 0 279.4 81.8 362.7 254C791.5 684.2 673.4 766 512 766zm-4-430c-97.2 0-176 78.8-176 176s78.8 176 176 176 176-78.8 176-176-78.8-176-176-176zm0 288c-61.9 0-112-50.1-112-112s50.1-112 112-112 112 50.1 112 112-50.1 112-112 112z" } }] }, "name": "eye", "theme": "outlined" };
var EyeOutlinedSvg = EyeOutlined$2;

var EyeOutlined = function EyeOutlined(props, ref) {
  return /*#__PURE__*/React.createElement(AntdIcon, _objectSpread2(_objectSpread2({}, props), {}, {
    ref: ref,
    icon: EyeOutlinedSvg
  }));
};
var RefIcon$2 = /*#__PURE__*/React.forwardRef(EyeOutlined);
var EyeOutlined$1 = RefIcon$2;

// This icon file is generated automatically.
var StarOutlined$2 = { "icon": { "tag": "svg", "attrs": { "viewBox": "64 64 896 896", "focusable": "false" }, "children": [{ "tag": "path", "attrs": { "d": "M908.1 353.1l-253.9-36.9L540.7 86.1c-3.1-6.3-8.2-11.4-14.5-14.5-15.8-7.8-35-1.3-42.9 14.5L369.8 316.2l-253.9 36.9c-7 1-13.4 4.3-18.3 9.3a32.05 32.05 0 00.6 45.3l183.7 179.1-43.4 252.9a31.95 31.95 0 0046.4 33.7L512 754l227.1 119.4c6.2 3.3 13.4 4.4 20.3 3.2 17.4-3 29.1-19.5 26.1-36.9l-43.4-252.9 183.7-179.1c5-4.9 8.3-11.3 9.3-18.3 2.7-17.5-9.5-33.7-27-36.3zM664.8 561.6l36.1 210.3L512 672.7 323.1 772l36.1-210.3-152.8-149L417.6 382 512 190.7 606.4 382l211.2 30.7-152.8 148.9z" } }] }, "name": "star", "theme": "outlined" };
var StarOutlinedSvg = StarOutlined$2;

var StarOutlined = function StarOutlined(props, ref) {
  return /*#__PURE__*/React.createElement(AntdIcon, _objectSpread2(_objectSpread2({}, props), {}, {
    ref: ref,
    icon: StarOutlinedSvg
  }));
};
var RefIcon$1 = /*#__PURE__*/React.forwardRef(StarOutlined);
var StarOutlined$1 = RefIcon$1;

// This icon file is generated automatically.
var UploadOutlined$2 = { "icon": { "tag": "svg", "attrs": { "viewBox": "64 64 896 896", "focusable": "false" }, "children": [{ "tag": "path", "attrs": { "d": "M400 317.7h73.9V656c0 4.4 3.6 8 8 8h60c4.4 0 8-3.6 8-8V317.7H624c6.7 0 10.4-7.7 6.3-12.9L518.3 163a8 8 0 00-12.6 0l-112 141.7c-4.1 5.3-.4 13 6.3 13zM878 626h-60c-4.4 0-8 3.6-8 8v154H214V634c0-4.4-3.6-8-8-8h-60c-4.4 0-8 3.6-8 8v198c0 17.7 14.3 32 32 32h684c17.7 0 32-14.3 32-32V634c0-4.4-3.6-8-8-8z" } }] }, "name": "upload", "theme": "outlined" };
var UploadOutlinedSvg = UploadOutlined$2;

var UploadOutlined = function UploadOutlined(props, ref) {
  return /*#__PURE__*/React.createElement(AntdIcon, _objectSpread2(_objectSpread2({}, props), {}, {
    ref: ref,
    icon: UploadOutlinedSvg
  }));
};
var RefIcon = /*#__PURE__*/React.forwardRef(UploadOutlined);
var UploadOutlined$1 = RefIcon;

var Option = Select.Option;
var TextArea = Input.TextArea;
var Search = Input.Search;
var AirdropMarket = function AirdropMarket() {
  var _currentTemplate$rati;
  var _useState = useState([]),
    _useState2 = _slicedToArray(_useState, 2);
    _useState2[0];
    var setTemplates = _useState2[1];
  var _useState3 = useState([]),
    _useState4 = _slicedToArray(_useState3, 2),
    filteredTemplates = _useState4[0],
    setFilteredTemplates = _useState4[1];
  var _useState5 = useState(false),
    _useState6 = _slicedToArray(_useState5, 2),
    loading = _useState6[0],
    setLoading = _useState6[1];
  var _useState7 = useState(''),
    _useState8 = _slicedToArray(_useState7, 2),
    searchKeyword = _useState8[0],
    setSearchKeyword = _useState8[1];
  var _useState9 = useState(''),
    _useState0 = _slicedToArray(_useState9, 2),
    category = _useState0[0],
    setCategory = _useState0[1];
  var _useState1 = useState('rating'),
    _useState10 = _slicedToArray(_useState1, 2),
    sortBy = _useState10[0],
    setSortBy = _useState10[1];
  var _useState11 = useState(false),
    _useState12 = _slicedToArray(_useState11, 2),
    viewModalVisible = _useState12[0],
    setViewModalVisible = _useState12[1];
  var _useState13 = useState(false),
    _useState14 = _slicedToArray(_useState13, 2),
    rateModalVisible = _useState14[0],
    setRateModalVisible = _useState14[1];
  var _useState15 = useState(false),
    _useState16 = _slicedToArray(_useState15, 2),
    uploadModalVisible = _useState16[0],
    setUploadModalVisible = _useState16[1];
  var _useState17 = useState(null),
    _useState18 = _slicedToArray(_useState17, 2),
    currentTemplate = _useState18[0],
    setCurrentTemplate = _useState18[1];
  var _Form$useForm = Form.useForm(),
    _Form$useForm2 = _slicedToArray(_Form$useForm, 1),
    form = _Form$useForm2[0];
  var _Form$useForm3 = Form.useForm(),
    _Form$useForm4 = _slicedToArray(_Form$useForm3, 1),
    rateForm = _Form$useForm4[0];

  // 加载模板列表
  var loadTemplates = /*#__PURE__*/function () {
    var _ref = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee() {
      var params, res, data, _t;
      return _regenerator().w(function (_context) {
        while (1) switch (_context.p = _context.n) {
          case 0:
            _context.p = 0;
            setLoading(true);
            params = {
              keyword: searchKeyword || undefined,
              category: category || undefined,
              sortBy: sortBy || 'rating',
              page: 1,
              pageSize: 100
            };
            _context.n = 1;
            return marketAPI.getTemplates(params);
          case 1:
            res = _context.v;
            data = (res === null || res === void 0 ? void 0 : res.data) || [];
            setTemplates(data);
            setFilteredTemplates(data);
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
  useEffect(function () {
    loadTemplates();
  }, [category, sortBy]);

  // 搜索模板
  var handleSearch = function handleSearch() {
    loadTemplates();
  };

  // 下载模板
  var handleDownloadTemplate = /*#__PURE__*/function () {
    var _ref2 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee2(templateId) {
      var _t2;
      return _regenerator().w(function (_context2) {
        while (1) switch (_context2.p = _context2.n) {
          case 0:
            _context2.p = 0;
            _context2.n = 1;
            return marketAPI.downloadTemplate(templateId);
          case 1:
            _context2.v;
            message.success('模板下载成功');
            // 这里可以触发模板导入到本地
            _context2.n = 3;
            break;
          case 2:
            _context2.p = 2;
            _t2 = _context2.v;
            message.error('下载模板失败: ' + _t2.message);
          case 3:
            return _context2.a(2);
        }
      }, _callee2, null, [[0, 2]]);
    }));
    return function handleDownloadTemplate(_x) {
      return _ref2.apply(this, arguments);
    };
  }();

  // 评分模板
  var handleRateTemplate = /*#__PURE__*/function () {
    var _ref3 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee3(values) {
      var _t3;
      return _regenerator().w(function (_context3) {
        while (1) switch (_context3.p = _context3.n) {
          case 0:
            _context3.p = 0;
            _context3.n = 1;
            return marketAPI.rateTemplate(currentTemplate.id, values.rating, values.comment);
          case 1:
            message.success('评分成功');
            setRateModalVisible(false);
            rateForm.resetFields();
            loadTemplates();
            _context3.n = 3;
            break;
          case 2:
            _context3.p = 2;
            _t3 = _context3.v;
            message.error('评分失败: ' + _t3.message);
          case 3:
            return _context3.a(2);
        }
      }, _callee3, null, [[0, 2]]);
    }));
    return function handleRateTemplate(_x2) {
      return _ref3.apply(this, arguments);
    };
  }();

  // 上传模板
  var handleUploadTemplate = /*#__PURE__*/function () {
    var _ref4 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee4(values) {
      var template, _t4;
      return _regenerator().w(function (_context4) {
        while (1) switch (_context4.p = _context4.n) {
          case 0:
            _context4.p = 0;
            template = {
              name: values.name,
              description: values.description,
              category: values.category,
              scriptType: values.scriptType,
              scriptContent: values.scriptContent,
              tags: values.tags ? values.tags.split(',').map(function (t) {
                return t.trim();
              }) : []
            };
            _context4.n = 1;
            return marketAPI.uploadTemplate(template);
          case 1:
            message.success('模板上传成功，等待审核');
            setUploadModalVisible(false);
            form.resetFields();
            loadTemplates();
            _context4.n = 3;
            break;
          case 2:
            _context4.p = 2;
            _t4 = _context4.v;
            message.error('上传模板失败: ' + _t4.message);
          case 3:
            return _context4.a(2);
        }
      }, _callee4, null, [[0, 2]]);
    }));
    return function handleUploadTemplate(_x3) {
      return _ref4.apply(this, arguments);
    };
  }();

  // 查看模板详情
  var handleViewTemplate = /*#__PURE__*/function () {
    var _ref5 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee5(templateId) {
      var res, _t5;
      return _regenerator().w(function (_context5) {
        while (1) switch (_context5.p = _context5.n) {
          case 0:
            _context5.p = 0;
            _context5.n = 1;
            return marketAPI.getTemplate(templateId);
          case 1:
            res = _context5.v;
            setCurrentTemplate(res === null || res === void 0 ? void 0 : res.data);
            setViewModalVisible(true);
            _context5.n = 3;
            break;
          case 2:
            _context5.p = 2;
            _t5 = _context5.v;
            message.error('获取模板详情失败: ' + _t5.message);
          case 3:
            return _context5.a(2);
        }
      }, _callee5, null, [[0, 2]]);
    }));
    return function handleViewTemplate(_x4) {
      return _ref5.apply(this, arguments);
    };
  }();
  var columns = [{
    title: '模板名称',
    dataIndex: 'name',
    key: 'name',
    width: 200
  }, {
    title: '分类',
    dataIndex: 'category',
    key: 'category',
    width: 120
  }, {
    title: '脚本类型',
    dataIndex: 'scriptType',
    key: 'scriptType',
    width: 100
  }, {
    title: '评分',
    dataIndex: 'rating',
    key: 'rating',
    width: 150,
    render: function render(rating) {
      return /*#__PURE__*/React__default.createElement(Space, null, /*#__PURE__*/React__default.createElement(Rate, {
        disabled: true,
        value: rating || 0
      }), /*#__PURE__*/React__default.createElement("span", null, "(", (rating === null || rating === void 0 ? void 0 : rating.toFixed(1)) || 0, ")"));
    }
  }, {
    title: '下载次数',
    dataIndex: 'downloadCount',
    key: 'downloadCount',
    width: 100
  }, {
    title: '作者',
    dataIndex: 'author',
    key: 'author',
    width: 120
  }, {
    title: '操作',
    key: 'action',
    width: 200,
    render: function render(_, record) {
      return /*#__PURE__*/React__default.createElement(Space, null, /*#__PURE__*/React__default.createElement(Button, {
        type: "link",
        size: "small",
        icon: /*#__PURE__*/React__default.createElement(EyeOutlined$1, null),
        onClick: function onClick() {
          return handleViewTemplate(record.id);
        }
      }, "\u67E5\u770B"), /*#__PURE__*/React__default.createElement(Button, {
        type: "link",
        size: "small",
        icon: /*#__PURE__*/React__default.createElement(DownloadOutlined$1, null),
        onClick: function onClick() {
          return handleDownloadTemplate(record.id);
        }
      }, "\u4E0B\u8F7D"), /*#__PURE__*/React__default.createElement(Button, {
        type: "link",
        size: "small",
        icon: /*#__PURE__*/React__default.createElement(StarOutlined$1, null),
        onClick: function onClick() {
          setCurrentTemplate(record);
          rateForm.resetFields();
          setRateModalVisible(true);
        }
      }, "\u8BC4\u5206"));
    }
  }];
  return /*#__PURE__*/React__default.createElement("div", {
    className: "airdrop-market"
  }, /*#__PURE__*/React__default.createElement(Card, {
    size: "small",
    style: {
      marginBottom: 16
    }
  }, /*#__PURE__*/React__default.createElement(Row, {
    gutter: 16,
    align: "middle"
  }, /*#__PURE__*/React__default.createElement(Col, {
    flex: "auto"
  }, /*#__PURE__*/React__default.createElement(Search, {
    placeholder: "\u641C\u7D22\u6A21\u677F\u540D\u79F0\u3001\u63CF\u8FF0...",
    value: searchKeyword,
    onChange: function onChange(e) {
      return setSearchKeyword(e.target.value);
    },
    onSearch: handleSearch,
    allowClear: true,
    enterButton: true
  })), /*#__PURE__*/React__default.createElement(Col, null, /*#__PURE__*/React__default.createElement(Space, null, /*#__PURE__*/React__default.createElement(Select, {
    placeholder: "\u9009\u62E9\u5206\u7C7B",
    value: category,
    onChange: setCategory,
    allowClear: true,
    style: {
      width: 150
    }
  }, /*#__PURE__*/React__default.createElement(Option, {
    value: "standard"
  }, "\u6807\u51C6\u6A21\u677F"), /*#__PURE__*/React__default.createElement(Option, {
    value: "custom"
  }, "\u81EA\u5B9A\u4E49\u6A21\u677F"), /*#__PURE__*/React__default.createElement(Option, {
    value: "automation"
  }, "\u81EA\u52A8\u5316\u6A21\u677F"), /*#__PURE__*/React__default.createElement(Option, {
    value: "data-processing"
  }, "\u6570\u636E\u5904\u7406")), /*#__PURE__*/React__default.createElement(Select, {
    placeholder: "\u6392\u5E8F\u65B9\u5F0F",
    value: sortBy,
    onChange: setSortBy,
    style: {
      width: 150
    }
  }, /*#__PURE__*/React__default.createElement(Option, {
    value: "rating"
  }, "\u6309\u8BC4\u5206"), /*#__PURE__*/React__default.createElement(Option, {
    value: "downloadCount"
  }, "\u6309\u4E0B\u8F7D\u91CF"), /*#__PURE__*/React__default.createElement(Option, {
    value: "createTime"
  }, "\u6309\u65F6\u95F4")), /*#__PURE__*/React__default.createElement(Button, {
    type: "primary",
    icon: /*#__PURE__*/React__default.createElement(UploadOutlined$1, null),
    onClick: function onClick() {
      form.resetFields();
      setUploadModalVisible(true);
    }
  }, "\u4E0A\u4F20\u6A21\u677F"))))), /*#__PURE__*/React__default.createElement(Card, null, /*#__PURE__*/React__default.createElement(Table, {
    columns: columns,
    dataSource: filteredTemplates,
    rowKey: "id",
    loading: loading,
    pagination: {
      pageSize: 10,
      showSizeChanger: true,
      showQuickJumper: true,
      showTotal: function showTotal(total) {
        return "\u5171 ".concat(total, " \u4E2A\u6A21\u677F");
      }
    },
    locale: {
      emptyText: '暂无模板，点击"上传模板"按钮分享你的模板'
    }
  })), /*#__PURE__*/React__default.createElement(Modal, {
    title: "\u6A21\u677F\u8BE6\u60C5 - ".concat((currentTemplate === null || currentTemplate === void 0 ? void 0 : currentTemplate.name) || ''),
    visible: viewModalVisible,
    onCancel: function onCancel() {
      setViewModalVisible(false);
      setCurrentTemplate(null);
    },
    footer: [/*#__PURE__*/React__default.createElement(Button, {
      key: "close",
      onClick: function onClick() {
        setViewModalVisible(false);
        setCurrentTemplate(null);
      }
    }, "\u5173\u95ED"), currentTemplate && /*#__PURE__*/React__default.createElement(Button, {
      key: "download",
      type: "primary",
      icon: /*#__PURE__*/React__default.createElement(DownloadOutlined$1, null),
      onClick: function onClick() {
        return handleDownloadTemplate(currentTemplate.id);
      }
    }, "\u4E0B\u8F7D\u6A21\u677F")],
    width: 800
  }, currentTemplate && /*#__PURE__*/React__default.createElement(React__default.Fragment, null, /*#__PURE__*/React__default.createElement(Descriptions, {
    column: 2,
    bordered: true
  }, /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u6A21\u677F\u540D\u79F0"
  }, currentTemplate.name), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u5206\u7C7B"
  }, currentTemplate.category), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u811A\u672C\u7C7B\u578B"
  }, currentTemplate.scriptType), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u8BC4\u5206"
  }, /*#__PURE__*/React__default.createElement(Rate, {
    disabled: true,
    value: currentTemplate.rating || 0
  }), /*#__PURE__*/React__default.createElement("span", {
    style: {
      marginLeft: 8
    }
  }, "(", ((_currentTemplate$rati = currentTemplate.rating) === null || _currentTemplate$rati === void 0 ? void 0 : _currentTemplate$rati.toFixed(1)) || 0, ")")), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u4E0B\u8F7D\u6B21\u6570"
  }, currentTemplate.downloadCount || 0), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u4F5C\u8005"
  }, currentTemplate.author || '未知'), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u63CF\u8FF0",
    span: 2
  }, currentTemplate.description || '无描述')), /*#__PURE__*/React__default.createElement(Divider, null), /*#__PURE__*/React__default.createElement("div", null, /*#__PURE__*/React__default.createElement("strong", null, "\u811A\u672C\u5185\u5BB9\uFF1A"), /*#__PURE__*/React__default.createElement("pre", {
    style: {
      background: '#f5f5f5',
      padding: 16,
      borderRadius: 4,
      maxHeight: 400,
      overflow: 'auto',
      fontFamily: 'monospace',
      fontSize: 12
    }
  }, currentTemplate.scriptContent || '无脚本内容')))), /*#__PURE__*/React__default.createElement(Modal, {
    title: "\u4E3A\u6A21\u677F\u8BC4\u5206 - ".concat((currentTemplate === null || currentTemplate === void 0 ? void 0 : currentTemplate.name) || ''),
    visible: rateModalVisible,
    onCancel: function onCancel() {
      setRateModalVisible(false);
      rateForm.resetFields();
    },
    onOk: function onOk() {
      return rateForm.submit();
    },
    width: 500
  }, /*#__PURE__*/React__default.createElement(Form, {
    form: rateForm,
    layout: "vertical",
    onFinish: handleRateTemplate
  }, /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "rating",
    label: "\u8BC4\u5206",
    rules: [{
      required: true,
      message: '请选择评分'
    }]
  }, /*#__PURE__*/React__default.createElement(Rate, null)), /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "comment",
    label: "\u8BC4\u8BBA\uFF08\u53EF\u9009\uFF09"
  }, /*#__PURE__*/React__default.createElement(TextArea, {
    rows: 4,
    placeholder: "\u5206\u4EAB\u4F60\u7684\u4F7F\u7528\u4F53\u9A8C..."
  })))), /*#__PURE__*/React__default.createElement(Modal, {
    title: "\u4E0A\u4F20\u6A21\u677F",
    visible: uploadModalVisible,
    onCancel: function onCancel() {
      setUploadModalVisible(false);
      form.resetFields();
    },
    onOk: function onOk() {
      return form.submit();
    },
    width: 700
  }, /*#__PURE__*/React__default.createElement(Form, {
    form: form,
    layout: "vertical",
    onFinish: handleUploadTemplate
  }, /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "name",
    label: "\u6A21\u677F\u540D\u79F0",
    rules: [{
      required: true,
      message: '请输入模板名称'
    }]
  }, /*#__PURE__*/React__default.createElement(Input, {
    placeholder: "\u4F8B\u5982: \u6570\u636E\u5904\u7406\u6A21\u677F"
  })), /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "description",
    label: "\u6A21\u677F\u63CF\u8FF0",
    rules: [{
      required: true,
      message: '请输入模板描述'
    }]
  }, /*#__PURE__*/React__default.createElement(TextArea, {
    rows: 3,
    placeholder: "\u63CF\u8FF0\u6A21\u677F\u7684\u529F\u80FD\u548C\u7528\u9014"
  })), /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "category",
    label: "\u5206\u7C7B",
    rules: [{
      required: true,
      message: '请选择分类'
    }]
  }, /*#__PURE__*/React__default.createElement(Select, {
    placeholder: "\u9009\u62E9\u6A21\u677F\u5206\u7C7B"
  }, /*#__PURE__*/React__default.createElement(Option, {
    value: "standard"
  }, "\u6807\u51C6\u6A21\u677F"), /*#__PURE__*/React__default.createElement(Option, {
    value: "custom"
  }, "\u81EA\u5B9A\u4E49\u6A21\u677F"), /*#__PURE__*/React__default.createElement(Option, {
    value: "automation"
  }, "\u81EA\u52A8\u5316\u6A21\u677F"), /*#__PURE__*/React__default.createElement(Option, {
    value: "data-processing"
  }, "\u6570\u636E\u5904\u7406"))), /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "scriptType",
    label: "\u811A\u672C\u7C7B\u578B",
    rules: [{
      required: true,
      message: '请选择脚本类型'
    }]
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
    placeholder: "\u8F93\u5165\u811A\u672C\u5185\u5BB9"
  })), /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "tags",
    label: "\u6807\u7B7E\uFF08\u53EF\u9009\uFF0C\u7528\u9017\u53F7\u5206\u9694\uFF09"
  }, /*#__PURE__*/React__default.createElement(Input, {
    placeholder: "\u4F8B\u5982: \u6570\u636E\u5904\u7406,\u81EA\u52A8\u5316,\u6279\u91CF"
  })), /*#__PURE__*/React__default.createElement(Alert, {
    message: "\u4E0A\u4F20\u8BF4\u660E",
    description: "\u4E0A\u4F20\u7684\u6A21\u677F\u9700\u8981\u7ECF\u8FC7\u5BA1\u6838\u540E\u624D\u80FD\u53D1\u5E03\u5230\u5E02\u573A\u3002\u8BF7\u786E\u4FDD\u6A21\u677F\u5185\u5BB9\u5408\u6CD5\u4E14\u6709\u7528\u3002",
    type: "info",
    showIcon: true,
    style: {
      marginTop: 16
    }
  }))));
};

export { AirdropMarket, AirdropMarket as default };
