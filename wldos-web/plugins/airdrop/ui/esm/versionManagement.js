import { _ as _slicedToArray, j as _objectSpread2$1, a as _asyncToGenerator, b as _regenerator, t as taskAPI, F as versionAPI } from './api-72a577fe.js';
const React = window.React;
const React__default = window.React;
const { useState, useEffect } = window.React;
const { Select, Input, Tabs, Form, Row, Col, Card, Table, Space, Button, Descriptions, Tag, Divider, Alert, Modal, Popconfirm, message } = window.antd;
import { A as AntdIcon, _ as _objectSpread2 } from './AntdIcon-16063a91.js';
import { F as FileTextOutlined } from './FileTextOutlined-b77e98e3.js';
import { H as HistoryOutlined } from './HistoryOutlined-8527818a.js';
import { C as CodeOutlined } from './CodeOutlined-637fcd45.js';

// This icon file is generated automatically.
var RollbackOutlined$2 = { "icon": { "tag": "svg", "attrs": { "viewBox": "64 64 896 896", "focusable": "false" }, "children": [{ "tag": "path", "attrs": { "d": "M793 242H366v-74c0-6.7-7.7-10.4-12.9-6.3l-142 112a8 8 0 000 12.6l142 112c5.2 4.1 12.9.4 12.9-6.3v-74h415v470H175c-4.4 0-8 3.6-8 8v60c0 4.4 3.6 8 8 8h618c35.3 0 64-28.7 64-64V306c0-35.3-28.7-64-64-64z" } }] }, "name": "rollback", "theme": "outlined" };
var RollbackOutlinedSvg = RollbackOutlined$2;

var RollbackOutlined = function RollbackOutlined(props, ref) {
  return /*#__PURE__*/React.createElement(AntdIcon, _objectSpread2(_objectSpread2({}, props), {}, {
    ref: ref,
    icon: RollbackOutlinedSvg
  }));
};
var RefIcon$1 = /*#__PURE__*/React.forwardRef(RollbackOutlined);
var RollbackOutlined$1 = RefIcon$1;

// This icon file is generated automatically.
var SwapOutlined$2 = { "icon": { "tag": "svg", "attrs": { "viewBox": "64 64 896 896", "focusable": "false" }, "children": [{ "tag": "path", "attrs": { "d": "M847.9 592H152c-4.4 0-8 3.6-8 8v60c0 4.4 3.6 8 8 8h605.2L612.9 851c-4.1 5.2-.4 13 6.3 13h72.5c4.9 0 9.5-2.2 12.6-6.1l168.8-214.1c16.5-21 1.6-51.8-25.2-51.8zM872 356H266.8l144.3-183c4.1-5.2.4-13-6.3-13h-72.5c-4.9 0-9.5 2.2-12.6 6.1L150.9 380.2c-16.5 21-1.6 51.8 25.1 51.8h696c4.4 0 8-3.6 8-8v-60c0-4.4-3.6-8-8-8z" } }] }, "name": "swap", "theme": "outlined" };
var SwapOutlinedSvg = SwapOutlined$2;

var SwapOutlined = function SwapOutlined(props, ref) {
  return /*#__PURE__*/React.createElement(AntdIcon, _objectSpread2(_objectSpread2({}, props), {}, {
    ref: ref,
    icon: SwapOutlinedSvg
  }));
};
var RefIcon = /*#__PURE__*/React.forwardRef(SwapOutlined);
var SwapOutlined$1 = RefIcon;

var Option = Select.Option;
var TextArea = Input.TextArea;
var TabPane = Tabs.TabPane;
var AirdropVersionManagement = function AirdropVersionManagement() {
  var _useState = useState([]),
    _useState2 = _slicedToArray(_useState, 2),
    tasks = _useState2[0],
    setTasks = _useState2[1];
  var _useState3 = useState(null),
    _useState4 = _slicedToArray(_useState3, 2),
    selectedTaskId = _useState4[0],
    setSelectedTaskId = _useState4[1];
  var _useState5 = useState([]),
    _useState6 = _slicedToArray(_useState5, 2),
    versions = _useState6[0],
    setVersions = _useState6[1];
  var _useState7 = useState(null),
    _useState8 = _slicedToArray(_useState7, 2),
    currentVersion = _useState8[0],
    setCurrentVersion = _useState8[1];
  var _useState9 = useState({
      version1: null,
      version2: null
    }),
    _useState0 = _slicedToArray(_useState9, 2),
    compareVersions = _useState0[0],
    setCompareVersions = _useState0[1];
  var _useState1 = useState(false),
    _useState10 = _slicedToArray(_useState1, 2),
    loading = _useState10[0],
    setLoading = _useState10[1];
  var _useState11 = useState(false),
    _useState12 = _slicedToArray(_useState11, 2),
    createModalVisible = _useState12[0],
    setCreateModalVisible = _useState12[1];
  var _useState13 = useState(false),
    _useState14 = _slicedToArray(_useState13, 2),
    compareModalVisible = _useState14[0],
    setCompareModalVisible = _useState14[1];
  var _useState15 = useState(false),
    _useState16 = _slicedToArray(_useState15, 2),
    viewModalVisible = _useState16[0],
    setViewModalVisible = _useState16[1];
  var _Form$useForm = Form.useForm(),
    _Form$useForm2 = _slicedToArray(_Form$useForm, 1),
    form = _Form$useForm2[0];

  // 加载任务列表
  var loadTasks = /*#__PURE__*/function () {
    var _ref = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee() {
      var res, _t;
      return _regenerator().w(function (_context) {
        while (1) switch (_context.p = _context.n) {
          case 0:
            _context.p = 0;
            _context.n = 1;
            return taskAPI.getTasks();
          case 1:
            res = _context.v;
            setTasks((res === null || res === void 0 ? void 0 : res.data) || []);
            _context.n = 3;
            break;
          case 2:
            _context.p = 2;
            _t = _context.v;
            message.error('加载任务列表失败: ' + _t.message);
          case 3:
            return _context.a(2);
        }
      }, _callee, null, [[0, 2]]);
    }));
    return function loadTasks() {
      return _ref.apply(this, arguments);
    };
  }();

  // 加载版本列表
  var loadVersions = /*#__PURE__*/function () {
    var _ref2 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee2(taskId) {
      var res, _t2;
      return _regenerator().w(function (_context2) {
        while (1) switch (_context2.p = _context2.n) {
          case 0:
            _context2.p = 0;
            setLoading(true);
            _context2.n = 1;
            return versionAPI.getVersions(taskId);
          case 1:
            res = _context2.v;
            setVersions((res === null || res === void 0 ? void 0 : res.data) || []);
            _context2.n = 3;
            break;
          case 2:
            _context2.p = 2;
            _t2 = _context2.v;
            message.error('加载版本列表失败: ' + _t2.message);
          case 3:
            _context2.p = 3;
            setLoading(false);
            return _context2.f(3);
          case 4:
            return _context2.a(2);
        }
      }, _callee2, null, [[0, 2, 3, 4]]);
    }));
    return function loadVersions(_x) {
      return _ref2.apply(this, arguments);
    };
  }();

  // 加载当前版本
  var loadCurrentVersion = /*#__PURE__*/function () {
    var _ref3 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee3(taskId) {
      var task;
      return _regenerator().w(function (_context3) {
        while (1) switch (_context3.n) {
          case 0:
            try {
              task = tasks.find(function (t) {
                return t.id === taskId;
              });
              if (task) {
                setCurrentVersion({
                  versionNumber: 'current',
                  scriptContent: task.scriptContent,
                  description: '当前版本',
                  createTime: task.updateTime || task.createTime
                });
              }
            } catch (error) {
              console.error('加载当前版本失败', error);
            }
          case 1:
            return _context3.a(2);
        }
      }, _callee3);
    }));
    return function loadCurrentVersion(_x2) {
      return _ref3.apply(this, arguments);
    };
  }();
  useEffect(function () {
    loadTasks();
  }, []);
  useEffect(function () {
    if (selectedTaskId) {
      loadVersions(selectedTaskId);
      loadCurrentVersion(selectedTaskId);
    }
  }, [selectedTaskId, tasks]);

  // 创建新版本
  var handleCreateVersion = /*#__PURE__*/function () {
    var _ref4 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee4(values) {
      var _t3;
      return _regenerator().w(function (_context4) {
        while (1) switch (_context4.p = _context4.n) {
          case 0:
            _context4.p = 0;
            _context4.n = 1;
            return versionAPI.createVersion(selectedTaskId, values.versionNumber, values.description);
          case 1:
            message.success('版本创建成功');
            setCreateModalVisible(false);
            form.resetFields();
            loadVersions(selectedTaskId);
            _context4.n = 3;
            break;
          case 2:
            _context4.p = 2;
            _t3 = _context4.v;
            message.error('创建版本失败: ' + _t3.message);
          case 3:
            return _context4.a(2);
        }
      }, _callee4, null, [[0, 2]]);
    }));
    return function handleCreateVersion(_x3) {
      return _ref4.apply(this, arguments);
    };
  }();

  // 版本回滚
  var handleRollbackVersion = /*#__PURE__*/function () {
    var _ref5 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee5(versionNumber) {
      var _t4;
      return _regenerator().w(function (_context5) {
        while (1) switch (_context5.p = _context5.n) {
          case 0:
            _context5.p = 0;
            _context5.n = 1;
            return versionAPI.rollbackVersion(selectedTaskId, versionNumber);
          case 1:
            message.success('版本回滚成功');
            loadVersions(selectedTaskId);
            loadCurrentVersion(selectedTaskId);
            _context5.n = 3;
            break;
          case 2:
            _context5.p = 2;
            _t4 = _context5.v;
            message.error('版本回滚失败: ' + _t4.message);
          case 3:
            return _context5.a(2);
        }
      }, _callee5, null, [[0, 2]]);
    }));
    return function handleRollbackVersion(_x4) {
      return _ref5.apply(this, arguments);
    };
  }();

  // 版本对比
  var handleCompareVersions = /*#__PURE__*/function () {
    var _ref6 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee6() {
      var _t5;
      return _regenerator().w(function (_context6) {
        while (1) switch (_context6.p = _context6.n) {
          case 0:
            if (!(!compareVersions.version1 || !compareVersions.version2)) {
              _context6.n = 1;
              break;
            }
            message.warning('请选择两个版本进行对比');
            return _context6.a(2);
          case 1:
            _context6.p = 1;
            _context6.n = 2;
            return versionAPI.compareVersions(selectedTaskId, compareVersions.version1, compareVersions.version2);
          case 2:
            _context6.v;
            setCompareModalVisible(true);
            // 这里应该显示对比结果，简化处理
            message.success('版本对比完成');
            _context6.n = 4;
            break;
          case 3:
            _context6.p = 3;
            _t5 = _context6.v;
            message.error('版本对比失败: ' + _t5.message);
          case 4:
            return _context6.a(2);
        }
      }, _callee6, null, [[1, 3]]);
    }));
    return function handleCompareVersions() {
      return _ref6.apply(this, arguments);
    };
  }();

  // 查看版本详情
  var handleViewVersion = /*#__PURE__*/function () {
    var _ref7 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee7(versionNumber) {
      var res, _t6;
      return _regenerator().w(function (_context7) {
        while (1) switch (_context7.p = _context7.n) {
          case 0:
            _context7.p = 0;
            _context7.n = 1;
            return versionAPI.getVersion(selectedTaskId, versionNumber);
          case 1:
            res = _context7.v;
            setCurrentVersion(res === null || res === void 0 ? void 0 : res.data);
            setViewModalVisible(true);
            _context7.n = 3;
            break;
          case 2:
            _context7.p = 2;
            _t6 = _context7.v;
            message.error('获取版本详情失败: ' + _t6.message);
          case 3:
            return _context7.a(2);
        }
      }, _callee7, null, [[0, 2]]);
    }));
    return function handleViewVersion(_x5) {
      return _ref7.apply(this, arguments);
    };
  }();
  var taskColumns = [{
    title: '任务名称',
    dataIndex: 'taskName',
    key: 'taskName'
  }, {
    title: '任务类型',
    dataIndex: 'taskType',
    key: 'taskType'
  }, {
    title: '脚本类型',
    dataIndex: 'scriptType',
    key: 'scriptType'
  }, {
    title: '操作',
    key: 'action',
    render: function render(_, record) {
      return /*#__PURE__*/React__default.createElement(Button, {
        type: "link",
        size: "small",
        onClick: function onClick() {
          setSelectedTaskId(record.id);
          loadVersions(record.id);
          loadCurrentVersion(record.id);
        }
      }, "\u7BA1\u7406\u7248\u672C");
    }
  }];
  var versionColumns = [{
    title: '版本号',
    dataIndex: 'versionNumber',
    key: 'versionNumber'
  }, {
    title: '描述',
    dataIndex: 'description',
    key: 'description',
    ellipsis: true
  }, {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    render: function render(time) {
      return time ? new Date(time).toLocaleString() : '-';
    }
  }, {
    title: '操作',
    key: 'action',
    render: function render(_, record) {
      return /*#__PURE__*/React__default.createElement(Space, null, /*#__PURE__*/React__default.createElement(Button, {
        type: "link",
        size: "small",
        icon: /*#__PURE__*/React__default.createElement(FileTextOutlined, null),
        onClick: function onClick() {
          return handleViewVersion(record.versionNumber);
        }
      }, "\u67E5\u770B"), /*#__PURE__*/React__default.createElement(Popconfirm, {
        title: "\u786E\u8BA4\u56DE\u6EDA",
        description: "\u786E\u5B9A\u8981\u56DE\u6EDA\u5230\u7248\u672C ".concat(record.versionNumber, " \u5417\uFF1F"),
        onConfirm: function onConfirm() {
          return handleRollbackVersion(record.versionNumber);
        },
        okText: "\u786E\u5B9A",
        cancelText: "\u53D6\u6D88"
      }, /*#__PURE__*/React__default.createElement(Button, {
        type: "link",
        size: "small",
        icon: /*#__PURE__*/React__default.createElement(RollbackOutlined$1, null),
        danger: true
      }, "\u56DE\u6EDA")));
    }
  }];
  var selectedTask = tasks.find(function (t) {
    return t.id === selectedTaskId;
  });
  return /*#__PURE__*/React__default.createElement("div", {
    className: "airdrop-version-management"
  }, /*#__PURE__*/React__default.createElement(Row, {
    gutter: 16
  }, /*#__PURE__*/React__default.createElement(Col, {
    span: 8
  }, /*#__PURE__*/React__default.createElement(Card, {
    title: "\u4EFB\u52A1\u5217\u8868",
    style: {
      marginBottom: 16
    }
  }, /*#__PURE__*/React__default.createElement(Table, {
    columns: taskColumns,
    dataSource: tasks,
    rowKey: "id",
    pagination: {
      pageSize: 10
    },
    onRow: function onRow(record) {
      return {
        onClick: function onClick() {
          setSelectedTaskId(record.id);
          loadVersions(record.id);
          loadCurrentVersion(record.id);
        },
        style: {
          cursor: 'pointer',
          background: selectedTaskId === record.id ? '#e6f7ff' : 'transparent'
        }
      };
    }
  }))), /*#__PURE__*/React__default.createElement(Col, {
    span: 16
  }, selectedTaskId ? /*#__PURE__*/React__default.createElement(React__default.Fragment, null, /*#__PURE__*/React__default.createElement(Card, {
    title: /*#__PURE__*/React__default.createElement("span", null, /*#__PURE__*/React__default.createElement(HistoryOutlined, null), " \u7248\u672C\u7BA1\u7406 - ", (selectedTask === null || selectedTask === void 0 ? void 0 : selectedTask.taskName) || selectedTaskId),
    extra: /*#__PURE__*/React__default.createElement(Space, null, /*#__PURE__*/React__default.createElement(Button, {
      type: "primary",
      icon: /*#__PURE__*/React__default.createElement(CodeOutlined, null),
      onClick: function onClick() {
        form.resetFields();
        setCreateModalVisible(true);
      }
    }, "\u521B\u5EFA\u7248\u672C"), /*#__PURE__*/React__default.createElement(Button, {
      icon: /*#__PURE__*/React__default.createElement(SwapOutlined$1, null),
      onClick: function onClick() {
        setCompareVersions({
          version1: null,
          version2: null
        });
        setCompareModalVisible(true);
      }
    }, "\u7248\u672C\u5BF9\u6BD4")),
    style: {
      marginBottom: 16
    }
  }, /*#__PURE__*/React__default.createElement(Descriptions, {
    column: 2,
    size: "small",
    style: {
      marginBottom: 16
    }
  }, /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u5F53\u524D\u7248\u672C"
  }, /*#__PURE__*/React__default.createElement(Tag, {
    color: "success"
  }, "\u5F53\u524D\u7248\u672C")), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u7248\u672C\u603B\u6570"
  }, versions.length + 1, " \u4E2A\u7248\u672C")), /*#__PURE__*/React__default.createElement(Divider, null), /*#__PURE__*/React__default.createElement(Table, {
    columns: versionColumns,
    dataSource: versions,
    rowKey: "versionNumber",
    loading: loading,
    pagination: {
      pageSize: 10
    },
    locale: {
      emptyText: '暂无版本历史，点击"创建版本"保存当前版本'
    }
  })), currentVersion && /*#__PURE__*/React__default.createElement(Card, {
    title: "\u5F53\u524D\u7248\u672C",
    size: "small"
  }, /*#__PURE__*/React__default.createElement(Descriptions, {
    column: 1,
    size: "small"
  }, /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u7248\u672C\u53F7"
  }, currentVersion.versionNumber), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u63CF\u8FF0"
  }, currentVersion.description || '无'), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u66F4\u65B0\u65F6\u95F4"
  }, currentVersion.createTime ? new Date(currentVersion.createTime).toLocaleString() : '-')))) : /*#__PURE__*/React__default.createElement(Card, null, /*#__PURE__*/React__default.createElement(Alert, {
    message: "\u8BF7\u9009\u62E9\u4EFB\u52A1",
    description: "\u4ECE\u5DE6\u4FA7\u5217\u8868\u4E2D\u9009\u62E9\u4E00\u4E2A\u4EFB\u52A1\u6765\u67E5\u770B\u548C\u7BA1\u7406\u7248\u672C",
    type: "info",
    showIcon: true
  })))), /*#__PURE__*/React__default.createElement(Modal, {
    title: "\u521B\u5EFA\u65B0\u7248\u672C",
    visible: createModalVisible,
    onCancel: function onCancel() {
      setCreateModalVisible(false);
      form.resetFields();
    },
    onOk: function onOk() {
      return form.submit();
    },
    width: 600
  }, /*#__PURE__*/React__default.createElement(Form, {
    form: form,
    layout: "vertical",
    onFinish: handleCreateVersion
  }, /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "versionNumber",
    label: "\u7248\u672C\u53F7",
    rules: [{
      required: true,
      message: '请输入版本号'
    }],
    tooltip: "\u9075\u5FAA\u8BED\u4E49\u5316\u7248\u672C\u89C4\u8303\uFF0C\u5982\uFF1A1.0.0, 1.1.0, 2.0.0"
  }, /*#__PURE__*/React__default.createElement(Input, {
    placeholder: "\u4F8B\u5982: 1.0.0"
  })), /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "description",
    label: "\u7248\u672C\u63CF\u8FF0"
  }, /*#__PURE__*/React__default.createElement(TextArea, {
    rows: 4,
    placeholder: "\u63CF\u8FF0\u6B64\u7248\u672C\u7684\u53D8\u66F4\u5185\u5BB9"
  })), /*#__PURE__*/React__default.createElement(Alert, {
    message: "\u7248\u672C\u8BF4\u660E",
    description: "\u521B\u5EFA\u7248\u672C\u4F1A\u4FDD\u5B58\u5F53\u524D\u4EFB\u52A1\u7684\u811A\u672C\u5185\u5BB9\u3002\u7248\u672C\u53F7\u5E94\u9075\u5FAA\u8BED\u4E49\u5316\u7248\u672C\u89C4\u8303\uFF08\u4E3B\u7248\u672C\u53F7.\u6B21\u7248\u672C\u53F7.\u4FEE\u8BA2\u53F7\uFF09\u3002",
    type: "info",
    showIcon: true,
    style: {
      marginTop: 16
    }
  }))), /*#__PURE__*/React__default.createElement(Modal, {
    title: "\u7248\u672C\u5BF9\u6BD4",
    visible: compareModalVisible,
    onCancel: function onCancel() {
      setCompareModalVisible(false);
      setCompareVersions({
        version1: null,
        version2: null
      });
    },
    footer: [/*#__PURE__*/React__default.createElement(Button, {
      key: "cancel",
      onClick: function onClick() {
        setCompareModalVisible(false);
        setCompareVersions({
          version1: null,
          version2: null
        });
      }
    }, "\u5173\u95ED"), /*#__PURE__*/React__default.createElement(Button, {
      key: "compare",
      type: "primary",
      onClick: handleCompareVersions
    }, "\u5BF9\u6BD4")],
    width: 900
  }, /*#__PURE__*/React__default.createElement(Form, {
    layout: "vertical"
  }, /*#__PURE__*/React__default.createElement(Form.Item, {
    label: "\u9009\u62E9\u7248\u672C1"
  }, /*#__PURE__*/React__default.createElement(Select, {
    placeholder: "\u9009\u62E9\u7B2C\u4E00\u4E2A\u7248\u672C",
    value: compareVersions.version1,
    onChange: function onChange(value) {
      return setCompareVersions(_objectSpread2$1(_objectSpread2$1({}, compareVersions), {}, {
        version1: value
      }));
    }
  }, /*#__PURE__*/React__default.createElement(Option, {
    value: "current"
  }, "\u5F53\u524D\u7248\u672C"), versions.map(function (v) {
    return /*#__PURE__*/React__default.createElement(Option, {
      key: v.versionNumber,
      value: v.versionNumber
    }, v.versionNumber, " - ", v.description || '无描述');
  }))), /*#__PURE__*/React__default.createElement(Form.Item, {
    label: "\u9009\u62E9\u7248\u672C2"
  }, /*#__PURE__*/React__default.createElement(Select, {
    placeholder: "\u9009\u62E9\u7B2C\u4E8C\u4E2A\u7248\u672C",
    value: compareVersions.version2,
    onChange: function onChange(value) {
      return setCompareVersions(_objectSpread2$1(_objectSpread2$1({}, compareVersions), {}, {
        version2: value
      }));
    }
  }, /*#__PURE__*/React__default.createElement(Option, {
    value: "current"
  }, "\u5F53\u524D\u7248\u672C"), versions.map(function (v) {
    return /*#__PURE__*/React__default.createElement(Option, {
      key: v.versionNumber,
      value: v.versionNumber
    }, v.versionNumber, " - ", v.description || '无描述');
  }))), /*#__PURE__*/React__default.createElement(Alert, {
    message: "\u5BF9\u6BD4\u8BF4\u660E",
    description: "\u9009\u62E9\u4E24\u4E2A\u7248\u672C\u8FDB\u884C\u5BF9\u6BD4\uFF0C\u7CFB\u7EDF\u5C06\u663E\u793A\u811A\u672C\u5185\u5BB9\u7684\u5DEE\u5F02\u3002",
    type: "info",
    showIcon: true
  }))), /*#__PURE__*/React__default.createElement(Modal, {
    title: "\u7248\u672C\u8BE6\u60C5 - ".concat((currentVersion === null || currentVersion === void 0 ? void 0 : currentVersion.versionNumber) || ''),
    visible: viewModalVisible,
    onCancel: function onCancel() {
      setViewModalVisible(false);
      setCurrentVersion(null);
    },
    footer: [/*#__PURE__*/React__default.createElement(Button, {
      key: "close",
      onClick: function onClick() {
        setViewModalVisible(false);
        setCurrentVersion(null);
      }
    }, "\u5173\u95ED")],
    width: 800
  }, currentVersion && /*#__PURE__*/React__default.createElement(Tabs, null, /*#__PURE__*/React__default.createElement(TabPane, {
    tab: "\u7248\u672C\u4FE1\u606F",
    key: "info"
  }, /*#__PURE__*/React__default.createElement(Descriptions, {
    column: 1,
    bordered: true
  }, /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u7248\u672C\u53F7"
  }, currentVersion.versionNumber), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u63CF\u8FF0"
  }, currentVersion.description || '无'), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u521B\u5EFA\u65F6\u95F4"
  }, currentVersion.createTime ? new Date(currentVersion.createTime).toLocaleString() : '-'))), /*#__PURE__*/React__default.createElement(TabPane, {
    tab: "\u811A\u672C\u5185\u5BB9",
    key: "script"
  }, /*#__PURE__*/React__default.createElement("pre", {
    style: {
      background: '#f5f5f5',
      padding: 16,
      borderRadius: 4,
      maxHeight: 500,
      overflow: 'auto',
      fontFamily: 'monospace',
      fontSize: 12
    }
  }, currentVersion.scriptContent || '无脚本内容')))));
};

export { AirdropVersionManagement, AirdropVersionManagement as default };
