import { _ as _slicedToArray, a as _asyncToGenerator, b as _regenerator, c as templateAPI } from './api-7374ad8a.js';
const React = window.React;
const { Button, Table, message, Space, Modal } = window.antd;

var AirdropTemplates = function AirdropTemplates() {
  var _React$useState = React.useState([]),
    _React$useState2 = _slicedToArray(_React$useState, 2),
    templates = _React$useState2[0],
    setTemplates = _React$useState2[1];
  var _React$useState3 = React.useState(false),
    _React$useState4 = _slicedToArray(_React$useState3, 2),
    loading = _React$useState4[0],
    setLoading = _React$useState4[1];

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
  React.useEffect(function () {
    loadTemplates();
  }, []);

  // 事件处理函数
  var handleCreateTemplate = function handleCreateTemplate() {
    message.info('新建模板功能开发中...');
  };
  var handleUseTemplate = function handleUseTemplate(record) {
    message.info("\u4F7F\u7528\u6A21\u677F: ".concat(record.templateName));
  };
  var handleEditTemplate = function handleEditTemplate(record) {
    message.info("\u7F16\u8F91\u6A21\u677F: ".concat(record.templateName));
  };
  var handleDeleteTemplate = function handleDeleteTemplate(record) {
    Modal.confirm({
      title: '确认删除',
      content: "\u786E\u5B9A\u8981\u5220\u9664\u6A21\u677F \"".concat(record.templateName, "\" \u5417\uFF1F"),
      onOk: function onOk() {
        return _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee2() {
          var _t2;
          return _regenerator().w(function (_context2) {
            while (1) switch (_context2.p = _context2.n) {
              case 0:
                _context2.p = 0;
                _context2.n = 1;
                return templateAPI.deleteTemplate(record.id);
              case 1:
                message.success('模板删除成功');
                loadTemplates(); // 重新加载列表
                _context2.n = 3;
                break;
              case 2:
                _context2.p = 2;
                _t2 = _context2.v;
                message.error('删除模板失败: ' + _t2.message);
              case 3:
                return _context2.a(2);
            }
          }, _callee2, null, [[0, 2]]);
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
      return /*#__PURE__*/React.createElement(Space, null, /*#__PURE__*/React.createElement(Button, {
        type: "link",
        size: "small",
        onClick: function onClick() {
          return handleUseTemplate(record);
        }
      }, "\u4F7F\u7528"), /*#__PURE__*/React.createElement(Button, {
        type: "link",
        size: "small",
        onClick: function onClick() {
          return handleEditTemplate(record);
        }
      }, "\u7F16\u8F91"), /*#__PURE__*/React.createElement(Button, {
        type: "link",
        size: "small",
        danger: true,
        onClick: function onClick() {
          return handleDeleteTemplate(record);
        }
      }, "\u5220\u9664"));
    }
  }];
  return /*#__PURE__*/React.createElement("div", {
    className: "airdrop-templates"
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      marginBottom: 16
    }
  }, /*#__PURE__*/React.createElement(Button, {
    type: "primary",
    onClick: handleCreateTemplate
  }, "\u65B0\u5EFA\u6A21\u677F"), /*#__PURE__*/React.createElement(Button, {
    style: {
      marginLeft: 8
    },
    onClick: loadTemplates
  }, "\u5237\u65B0")), /*#__PURE__*/React.createElement(Table, {
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
  }));
};

export { AirdropTemplates, AirdropTemplates as default };
