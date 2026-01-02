import { _ as _slicedToArray, a as _asyncToGenerator, b as _regenerator, t as taskAPI, G as testAPI } from './api-72a577fe.js';
const React = window.React;
const React__default = window.React;
const { useState, useEffect } = window.React;
const { Select, Input, Tabs, Form, Row, Col, Card, Table, Space, Button, Alert, Modal, Descriptions, Tag, message } = window.antd;
import { A as AntdIcon, _ as _objectSpread2 } from './AntdIcon-16063a91.js';
import { F as FileTextOutlined } from './FileTextOutlined-b77e98e3.js';
import { P as PlayCircleOutlined } from './PlayCircleOutlined-25bce8f0.js';
import { C as CheckCircleOutlined, a as CloseCircleOutlined } from './CloseCircleOutlined-9400c70b.js';

// This icon file is generated automatically.
var PlusOutlined$2 = { "icon": { "tag": "svg", "attrs": { "viewBox": "64 64 896 896", "focusable": "false" }, "children": [{ "tag": "path", "attrs": { "d": "M482 152h60q8 0 8 8v704q0 8-8 8h-60q-8 0-8-8V160q0-8 8-8z" } }, { "tag": "path", "attrs": { "d": "M192 474h672q8 0 8 8v60q0 8-8 8H160q-8 0-8-8v-60q0-8 8-8z" } }] }, "name": "plus", "theme": "outlined" };
var PlusOutlinedSvg = PlusOutlined$2;

var PlusOutlined = function PlusOutlined(props, ref) {
  return /*#__PURE__*/React.createElement(AntdIcon, _objectSpread2(_objectSpread2({}, props), {}, {
    ref: ref,
    icon: PlusOutlinedSvg
  }));
};
var RefIcon = /*#__PURE__*/React.forwardRef(PlusOutlined);
var PlusOutlined$1 = RefIcon;

Select.Option;
var TextArea = Input.TextArea;
var TabPane = Tabs.TabPane;
var AirdropTesting = function AirdropTesting() {
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
    testCases = _useState6[0],
    setTestCases = _useState6[1];
  var _useState7 = useState([]),
    _useState8 = _slicedToArray(_useState7, 2),
    testReports = _useState8[0],
    setTestReports = _useState8[1];
  var _useState9 = useState(false),
    _useState0 = _slicedToArray(_useState9, 2),
    loading = _useState0[0],
    setLoading = _useState0[1];
  var _useState1 = useState(false),
    _useState10 = _slicedToArray(_useState1, 2),
    createModalVisible = _useState10[0],
    setCreateModalVisible = _useState10[1];
  var _useState11 = useState(false),
    _useState12 = _slicedToArray(_useState11, 2);
    _useState12[0];
    _useState12[1];
  var _useState13 = useState(false),
    _useState14 = _slicedToArray(_useState13, 2),
    reportModalVisible = _useState14[0],
    setReportModalVisible = _useState14[1];
  var _useState15 = useState(null),
    _useState16 = _slicedToArray(_useState15, 2);
    _useState16[0];
    _useState16[1];
  var _useState17 = useState(null),
    _useState18 = _slicedToArray(_useState17, 2),
    currentReport = _useState18[0],
    setCurrentReport = _useState18[1];
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

  // 加载测试用例
  var loadTestCases = /*#__PURE__*/function () {
    var _ref2 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee2(taskId) {
      var res, _t2;
      return _regenerator().w(function (_context2) {
        while (1) switch (_context2.p = _context2.n) {
          case 0:
            _context2.p = 0;
            setLoading(true);
            _context2.n = 1;
            return testAPI.getTestCases(taskId);
          case 1:
            res = _context2.v;
            setTestCases((res === null || res === void 0 ? void 0 : res.data) || []);
            _context2.n = 3;
            break;
          case 2:
            _context2.p = 2;
            _t2 = _context2.v;
            message.error('加载测试用例失败: ' + _t2.message);
          case 3:
            _context2.p = 3;
            setLoading(false);
            return _context2.f(3);
          case 4:
            return _context2.a(2);
        }
      }, _callee2, null, [[0, 2, 3, 4]]);
    }));
    return function loadTestCases(_x) {
      return _ref2.apply(this, arguments);
    };
  }();

  // 加载测试报告
  var loadTestReports = /*#__PURE__*/function () {
    var _ref3 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee3(taskId) {
      var res, _t3;
      return _regenerator().w(function (_context3) {
        while (1) switch (_context3.p = _context3.n) {
          case 0:
            _context3.p = 0;
            _context3.n = 1;
            return testAPI.getTestReports(taskId);
          case 1:
            res = _context3.v;
            setTestReports((res === null || res === void 0 ? void 0 : res.data) || []);
            _context3.n = 3;
            break;
          case 2:
            _context3.p = 2;
            _t3 = _context3.v;
            message.error('加载测试报告失败: ' + _t3.message);
          case 3:
            return _context3.a(2);
        }
      }, _callee3, null, [[0, 2]]);
    }));
    return function loadTestReports(_x2) {
      return _ref3.apply(this, arguments);
    };
  }();
  useEffect(function () {
    loadTasks();
  }, []);
  useEffect(function () {
    if (selectedTaskId) {
      loadTestCases(selectedTaskId);
      loadTestReports(selectedTaskId);
    }
  }, [selectedTaskId]);

  // 创建测试用例
  var handleCreateTestCase = /*#__PURE__*/function () {
    var _ref4 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee4(values) {
      var testCase, _t4;
      return _regenerator().w(function (_context4) {
        while (1) switch (_context4.p = _context4.n) {
          case 0:
            _context4.p = 0;
            testCase = {
              taskId: selectedTaskId,
              name: values.name,
              description: values.description,
              inputData: values.inputData ? JSON.parse(values.inputData) : {},
              expectedOutput: values.expectedOutput,
              mockData: values.mockData ? JSON.parse(values.mockData) : {}
            };
            _context4.n = 1;
            return testAPI.createTestCase(testCase);
          case 1:
            message.success('测试用例创建成功');
            setCreateModalVisible(false);
            form.resetFields();
            loadTestCases(selectedTaskId);
            _context4.n = 3;
            break;
          case 2:
            _context4.p = 2;
            _t4 = _context4.v;
            message.error('创建测试用例失败: ' + _t4.message);
          case 3:
            return _context4.a(2);
        }
      }, _callee4, null, [[0, 2]]);
    }));
    return function handleCreateTestCase(_x3) {
      return _ref4.apply(this, arguments);
    };
  }();

  // 执行测试用例
  var handleExecuteTestCase = /*#__PURE__*/function () {
    var _ref5 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee5(testCaseId) {
      var res, _t5;
      return _regenerator().w(function (_context5) {
        while (1) switch (_context5.p = _context5.n) {
          case 0:
            _context5.p = 0;
            _context5.n = 1;
            return testAPI.executeTestCase(testCaseId);
          case 1:
            res = _context5.v;
            message.success('测试执行完成');
            loadTestReports(selectedTaskId);

            // 显示测试结果
            if (res !== null && res !== void 0 && res.data) {
              setCurrentReport(res.data);
              setReportModalVisible(true);
            }
            _context5.n = 3;
            break;
          case 2:
            _context5.p = 2;
            _t5 = _context5.v;
            message.error('执行测试失败: ' + _t5.message);
          case 3:
            return _context5.a(2);
        }
      }, _callee5, null, [[0, 2]]);
    }));
    return function handleExecuteTestCase(_x4) {
      return _ref5.apply(this, arguments);
    };
  }();

  // 批量执行测试
  var handleBatchExecute = /*#__PURE__*/function () {
    var _ref6 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee6() {
      var testCaseIds, res, _t6;
      return _regenerator().w(function (_context6) {
        while (1) switch (_context6.p = _context6.n) {
          case 0:
            if (!(testCases.length === 0)) {
              _context6.n = 1;
              break;
            }
            message.warning('没有可执行的测试用例');
            return _context6.a(2);
          case 1:
            _context6.p = 1;
            testCaseIds = testCases.map(function (tc) {
              return tc.id;
            });
            _context6.n = 2;
            return testAPI.executeBatch(testCaseIds);
          case 2:
            res = _context6.v;
            message.success("\u6279\u91CF\u6267\u884C\u5B8C\u6210\uFF0C\u5171 ".concat(testCaseIds.length, " \u4E2A\u6D4B\u8BD5\u7528\u4F8B"));
            loadTestReports(selectedTaskId);
            if (res !== null && res !== void 0 && res.data) {
              setCurrentReport(res.data);
              setReportModalVisible(true);
            }
            _context6.n = 4;
            break;
          case 3:
            _context6.p = 3;
            _t6 = _context6.v;
            message.error('批量执行失败: ' + _t6.message);
          case 4:
            return _context6.a(2);
        }
      }, _callee6, null, [[1, 3]]);
    }));
    return function handleBatchExecute() {
      return _ref6.apply(this, arguments);
    };
  }();
  var taskColumns = [{
    title: '任务名称',
    dataIndex: 'taskName',
    key: 'taskName'
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
          loadTestCases(record.id);
          loadTestReports(record.id);
        }
      }, "\u7BA1\u7406\u6D4B\u8BD5");
    }
  }];
  var testCaseColumns = [{
    title: '测试用例名称',
    dataIndex: 'name',
    key: 'name'
  }, {
    title: '描述',
    dataIndex: 'description',
    key: 'description',
    ellipsis: true
  }, {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    render: function render(status) {
      var statusMap = {
        'PASSED': {
          color: 'success',
          text: '通过'
        },
        'FAILED': {
          color: 'error',
          text: '失败'
        },
        'PENDING': {
          color: 'default',
          text: '待执行'
        }
      };
      var statusInfo = statusMap[status] || {
        color: 'default',
        text: status || '待执行'
      };
      return /*#__PURE__*/React__default.createElement(Tag, {
        color: statusInfo.color
      }, statusInfo.text);
    }
  }, {
    title: '操作',
    key: 'action',
    render: function render(_, record) {
      return /*#__PURE__*/React__default.createElement(Space, null, /*#__PURE__*/React__default.createElement(Button, {
        type: "link",
        size: "small",
        icon: /*#__PURE__*/React__default.createElement(PlayCircleOutlined, null),
        onClick: function onClick() {
          return handleExecuteTestCase(record.id);
        }
      }, "\u6267\u884C"));
    }
  }];
  var reportColumns = [{
    title: '测试时间',
    dataIndex: 'testTime',
    key: 'testTime',
    render: function render(time) {
      return time ? new Date(time).toLocaleString() : '-';
    }
  }, {
    title: '测试用例数',
    dataIndex: 'totalCases',
    key: 'totalCases'
  }, {
    title: '通过数',
    dataIndex: 'passedCases',
    key: 'passedCases',
    render: function render(count) {
      return /*#__PURE__*/React__default.createElement(Tag, {
        color: "success"
      }, count || 0);
    }
  }, {
    title: '失败数',
    dataIndex: 'failedCases',
    key: 'failedCases',
    render: function render(count) {
      return /*#__PURE__*/React__default.createElement(Tag, {
        color: "error"
      }, count || 0);
    }
  }, {
    title: '成功率',
    dataIndex: 'successRate',
    key: 'successRate',
    render: function render(rate) {
      return "".concat((rate || 0).toFixed(2), "%");
    }
  }, {
    title: '操作',
    key: 'action',
    render: function render(_, record) {
      return /*#__PURE__*/React__default.createElement(Button, {
        type: "link",
        size: "small",
        icon: /*#__PURE__*/React__default.createElement(FileTextOutlined, null),
        onClick: function onClick() {
          setCurrentReport(record);
          setReportModalVisible(true);
        }
      }, "\u67E5\u770B\u8BE6\u60C5");
    }
  }];
  var selectedTask = tasks.find(function (t) {
    return t.id === selectedTaskId;
  });
  return /*#__PURE__*/React__default.createElement("div", {
    className: "airdrop-testing"
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
          loadTestCases(record.id);
          loadTestReports(record.id);
        },
        style: {
          cursor: 'pointer',
          background: selectedTaskId === record.id ? '#e6f7ff' : 'transparent'
        }
      };
    }
  }))), /*#__PURE__*/React__default.createElement(Col, {
    span: 16
  }, selectedTaskId ? /*#__PURE__*/React__default.createElement(Tabs, {
    defaultActiveKey: "testCases"
  }, /*#__PURE__*/React__default.createElement(TabPane, {
    tab: /*#__PURE__*/React__default.createElement("span", null, /*#__PURE__*/React__default.createElement(FileTextOutlined, null), " \u6D4B\u8BD5\u7528\u4F8B"),
    key: "testCases"
  }, /*#__PURE__*/React__default.createElement(Card, {
    title: "\u6D4B\u8BD5\u7528\u4F8B - ".concat((selectedTask === null || selectedTask === void 0 ? void 0 : selectedTask.taskName) || selectedTaskId),
    extra: /*#__PURE__*/React__default.createElement(Space, null, /*#__PURE__*/React__default.createElement(Button, {
      type: "primary",
      icon: /*#__PURE__*/React__default.createElement(PlusOutlined$1, null),
      onClick: function onClick() {
        form.resetFields();
        setCreateModalVisible(true);
      }
    }, "\u521B\u5EFA\u6D4B\u8BD5\u7528\u4F8B"), /*#__PURE__*/React__default.createElement(Button, {
      icon: /*#__PURE__*/React__default.createElement(PlayCircleOutlined, null),
      onClick: handleBatchExecute,
      disabled: testCases.length === 0
    }, "\u6279\u91CF\u6267\u884C"))
  }, /*#__PURE__*/React__default.createElement(Table, {
    columns: testCaseColumns,
    dataSource: testCases,
    rowKey: "id",
    loading: loading,
    pagination: {
      pageSize: 10
    },
    locale: {
      emptyText: '暂无测试用例，点击"创建测试用例"按钮添加'
    }
  }))), /*#__PURE__*/React__default.createElement(TabPane, {
    tab: /*#__PURE__*/React__default.createElement("span", null, /*#__PURE__*/React__default.createElement(CheckCircleOutlined, null), " \u6D4B\u8BD5\u62A5\u544A"),
    key: "reports"
  }, /*#__PURE__*/React__default.createElement(Card, {
    title: "\u6D4B\u8BD5\u62A5\u544A - ".concat((selectedTask === null || selectedTask === void 0 ? void 0 : selectedTask.taskName) || selectedTaskId)
  }, /*#__PURE__*/React__default.createElement(Table, {
    columns: reportColumns,
    dataSource: testReports,
    rowKey: function rowKey(record, index) {
      return "".concat(record.testTime, "_").concat(index);
    },
    pagination: {
      pageSize: 10
    },
    locale: {
      emptyText: '暂无测试报告，执行测试用例后生成报告'
    }
  })))) : /*#__PURE__*/React__default.createElement(Card, null, /*#__PURE__*/React__default.createElement(Alert, {
    message: "\u8BF7\u9009\u62E9\u4EFB\u52A1",
    description: "\u4ECE\u5DE6\u4FA7\u5217\u8868\u4E2D\u9009\u62E9\u4E00\u4E2A\u4EFB\u52A1\u6765\u7BA1\u7406\u6D4B\u8BD5\u7528\u4F8B",
    type: "info",
    showIcon: true
  })))), /*#__PURE__*/React__default.createElement(Modal, {
    title: "\u521B\u5EFA\u6D4B\u8BD5\u7528\u4F8B",
    visible: createModalVisible,
    onCancel: function onCancel() {
      setCreateModalVisible(false);
      form.resetFields();
    },
    onOk: function onOk() {
      return form.submit();
    },
    width: 700
  }, /*#__PURE__*/React__default.createElement(Form, {
    form: form,
    layout: "vertical",
    onFinish: handleCreateTestCase
  }, /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "name",
    label: "\u6D4B\u8BD5\u7528\u4F8B\u540D\u79F0",
    rules: [{
      required: true,
      message: '请输入测试用例名称'
    }]
  }, /*#__PURE__*/React__default.createElement(Input, {
    placeholder: "\u4F8B\u5982: \u6D4B\u8BD5\u57FA\u672C\u529F\u80FD"
  })), /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "description",
    label: "\u63CF\u8FF0"
  }, /*#__PURE__*/React__default.createElement(TextArea, {
    rows: 3,
    placeholder: "\u63CF\u8FF0\u6D4B\u8BD5\u7528\u4F8B\u7684\u76EE\u7684\u548C\u573A\u666F"
  })), /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "inputData",
    label: "\u8F93\u5165\u6570\u636E\uFF08JSON\u683C\u5F0F\uFF09",
    tooltip: "\u6D4B\u8BD5\u7528\u4F8B\u7684\u8F93\u5165\u6570\u636E\uFF0CJSON\u683C\u5F0F"
  }, /*#__PURE__*/React__default.createElement(TextArea, {
    rows: 5,
    placeholder: "\u4F8B\u5982: {\"param1\": \"value1\", \"param2\": 123}"
  })), /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "expectedOutput",
    label: "\u671F\u671B\u8F93\u51FA"
  }, /*#__PURE__*/React__default.createElement(TextArea, {
    rows: 3,
    placeholder: "\u63CF\u8FF0\u671F\u671B\u7684\u8F93\u51FA\u7ED3\u679C"
  })), /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "mockData",
    label: "Mock\u6570\u636E\uFF08JSON\u683C\u5F0F\uFF0C\u53EF\u9009\uFF09",
    tooltip: "\u7528\u4E8E\u6A21\u62DF\u5916\u90E8\u4F9D\u8D56\u7684\u6570\u636E"
  }, /*#__PURE__*/React__default.createElement(TextArea, {
    rows: 5,
    placeholder: "\u4F8B\u5982: {\"apiResponse\": \"mock data\"}"
  })), /*#__PURE__*/React__default.createElement(Alert, {
    message: "\u6D4B\u8BD5\u7528\u4F8B\u8BF4\u660E",
    description: "\u521B\u5EFA\u6D4B\u8BD5\u7528\u4F8B\u540E\uFF0C\u53EF\u4EE5\u6267\u884C\u6D4B\u8BD5\u6765\u9A8C\u8BC1\u811A\u672C\u7684\u6B63\u786E\u6027\u3002\u8F93\u5165\u6570\u636E\u548CMock\u6570\u636E\u5E94\u4E3A\u6709\u6548\u7684JSON\u683C\u5F0F\u3002",
    type: "info",
    showIcon: true,
    style: {
      marginTop: 16
    }
  }))), /*#__PURE__*/React__default.createElement(Modal, {
    title: "\u6D4B\u8BD5\u62A5\u544A\u8BE6\u60C5",
    visible: reportModalVisible,
    onCancel: function onCancel() {
      setReportModalVisible(false);
      setCurrentReport(null);
    },
    footer: [/*#__PURE__*/React__default.createElement(Button, {
      key: "close",
      onClick: function onClick() {
        setReportModalVisible(false);
        setCurrentReport(null);
      }
    }, "\u5173\u95ED")],
    width: 900
  }, currentReport && /*#__PURE__*/React__default.createElement(Tabs, null, /*#__PURE__*/React__default.createElement(TabPane, {
    tab: "\u62A5\u544A\u6982\u89C8",
    key: "overview"
  }, /*#__PURE__*/React__default.createElement(Descriptions, {
    column: 2,
    bordered: true
  }, /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u6D4B\u8BD5\u65F6\u95F4"
  }, currentReport.testTime ? new Date(currentReport.testTime).toLocaleString() : '-'), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u603B\u7528\u4F8B\u6570"
  }, currentReport.totalCases || 0), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u901A\u8FC7\u6570"
  }, /*#__PURE__*/React__default.createElement(Tag, {
    color: "success"
  }, currentReport.passedCases || 0)), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u5931\u8D25\u6570"
  }, /*#__PURE__*/React__default.createElement(Tag, {
    color: "error"
  }, currentReport.failedCases || 0)), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u6210\u529F\u7387",
    span: 2
  }, /*#__PURE__*/React__default.createElement(Tag, {
    color: currentReport.successRate >= 80 ? 'success' : 'warning'
  }, (currentReport.successRate || 0).toFixed(2), "%")))), /*#__PURE__*/React__default.createElement(TabPane, {
    tab: "\u8BE6\u7EC6\u7ED3\u679C",
    key: "details"
  }, /*#__PURE__*/React__default.createElement("div", {
    style: {
      maxHeight: 500,
      overflow: 'auto'
    }
  }, currentReport.testResults && currentReport.testResults.length > 0 ? currentReport.testResults.map(function (result, index) {
    return /*#__PURE__*/React__default.createElement(Card, {
      key: index,
      size: "small",
      style: {
        marginBottom: 8
      }
    }, /*#__PURE__*/React__default.createElement(Space, null, result.passed ? /*#__PURE__*/React__default.createElement(CheckCircleOutlined, {
      style: {
        color: '#52c41a'
      }
    }) : /*#__PURE__*/React__default.createElement(CloseCircleOutlined, {
      style: {
        color: '#ff4d4f'
      }
    }), /*#__PURE__*/React__default.createElement("strong", null, result.testCaseName), /*#__PURE__*/React__default.createElement(Tag, {
      color: result.passed ? 'success' : 'error'
    }, result.passed ? '通过' : '失败')), result.message && /*#__PURE__*/React__default.createElement("div", {
      style: {
        marginTop: 8,
        fontSize: 12,
        color: '#666'
      }
    }, result.message), result.error && /*#__PURE__*/React__default.createElement("div", {
      style: {
        marginTop: 8,
        fontSize: 12,
        color: '#ff4d4f'
      }
    }, "\u9519\u8BEF: ", result.error));
  }) : /*#__PURE__*/React__default.createElement("div", {
    style: {
      color: '#999',
      textAlign: 'center',
      padding: 40
    }
  }, "\u6682\u65E0\u8BE6\u7EC6\u7ED3\u679C"))))));
};

export { AirdropTesting, AirdropTesting as default };
