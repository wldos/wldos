import { _ as _slicedToArray, a as _asyncToGenerator, b as _regenerator, t as taskAPI, H as performanceAPI } from './api-72a577fe.js';
const React = window.React;
const React__default = window.React;
const { useState, useEffect } = window.React;
const { Select, Tabs, Row, Col, Card, Table, Button, Statistic, Descriptions, Alert, Modal, Space, message } = window.antd;
import { A as AntdIcon, _ as _objectSpread2 } from './AntdIcon-16063a91.js';
import { F as FileTextOutlined } from './FileTextOutlined-b77e98e3.js';
import { B as BarChartOutlined } from './BarChartOutlined-2df7c7d3.js';

// This icon file is generated automatically.
var ThunderboltOutlined$2 = { "icon": { "tag": "svg", "attrs": { "viewBox": "64 64 896 896", "focusable": "false" }, "children": [{ "tag": "path", "attrs": { "d": "M848 359.3H627.7L825.8 109c4.1-5.3.4-13-6.3-13H436c-2.8 0-5.5 1.5-6.9 4L170 547.5c-3.1 5.3.7 12 6.9 12h174.4l-89.4 357.6c-1.9 7.8 7.5 13.3 13.3 7.7L853.5 373c5.2-4.9 1.7-13.7-5.5-13.7zM378.2 732.5l60.3-241H281.1l189.6-327.4h224.6L487 427.4h211L378.2 732.5z" } }] }, "name": "thunderbolt", "theme": "outlined" };
var ThunderboltOutlinedSvg = ThunderboltOutlined$2;

var ThunderboltOutlined = function ThunderboltOutlined(props, ref) {
  return /*#__PURE__*/React.createElement(AntdIcon, _objectSpread2(_objectSpread2({}, props), {}, {
    ref: ref,
    icon: ThunderboltOutlinedSvg
  }));
};
var RefIcon = /*#__PURE__*/React.forwardRef(ThunderboltOutlined);
var ThunderboltOutlined$1 = RefIcon;

Select.Option;
var TabPane = Tabs.TabPane;
var AirdropPerformance = function AirdropPerformance() {
  var _useState = useState([]),
    _useState2 = _slicedToArray(_useState, 2),
    tasks = _useState2[0],
    setTasks = _useState2[1];
  var _useState3 = useState(null),
    _useState4 = _slicedToArray(_useState3, 2),
    selectedTaskId = _useState4[0],
    setSelectedTaskId = _useState4[1];
  var _useState5 = useState(null),
    _useState6 = _slicedToArray(_useState5, 2),
    performanceReport = _useState6[0],
    setPerformanceReport = _useState6[1];
  var _useState7 = useState(false),
    _useState8 = _slicedToArray(_useState7, 2),
    loading = _useState8[0],
    setLoading = _useState8[1];
  var _useState9 = useState(false),
    _useState0 = _slicedToArray(_useState9, 2),
    reportModalVisible = _useState0[0],
    setReportModalVisible = _useState0[1];

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

  // 分析性能
  var handleAnalyzePerformance = /*#__PURE__*/function () {
    var _ref2 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee2(taskId) {
      var res, _t2;
      return _regenerator().w(function (_context2) {
        while (1) switch (_context2.p = _context2.n) {
          case 0:
            _context2.p = 0;
            setLoading(true);
            _context2.n = 1;
            return performanceAPI.analyzePerformance(taskId);
          case 1:
            res = _context2.v;
            setPerformanceReport(res === null || res === void 0 ? void 0 : res.data);
            message.success('性能分析完成');
            setReportModalVisible(true);
            _context2.n = 3;
            break;
          case 2:
            _context2.p = 2;
            _t2 = _context2.v;
            message.error('性能分析失败: ' + _t2.message);
          case 3:
            _context2.p = 3;
            setLoading(false);
            return _context2.f(3);
          case 4:
            return _context2.a(2);
        }
      }, _callee2, null, [[0, 2, 3, 4]]);
    }));
    return function handleAnalyzePerformance(_x) {
      return _ref2.apply(this, arguments);
    };
  }();

  // 获取性能报告
  var loadPerformanceReport = /*#__PURE__*/function () {
    var _ref3 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee3(taskId) {
      var res;
      return _regenerator().w(function (_context3) {
        while (1) switch (_context3.p = _context3.n) {
          case 0:
            _context3.p = 0;
            _context3.n = 1;
            return performanceAPI.getPerformanceReport(taskId);
          case 1:
            res = _context3.v;
            setPerformanceReport(res === null || res === void 0 ? void 0 : res.data);
            _context3.n = 3;
            break;
          case 2:
            _context3.p = 2;
            _context3.v;
          case 3:
            return _context3.a(2);
        }
      }, _callee3, null, [[0, 2]]);
    }));
    return function loadPerformanceReport(_x2) {
      return _ref3.apply(this, arguments);
    };
  }();
  useEffect(function () {
    loadTasks();
  }, []);
  useEffect(function () {
    if (selectedTaskId) {
      loadPerformanceReport(selectedTaskId);
    }
  }, [selectedTaskId]);
  var taskColumns = [{
    title: '任务名称',
    dataIndex: 'taskName',
    key: 'taskName'
  }, {
    title: '脚本类型',
    dataIndex: 'scriptType',
    key: 'scriptType'
  }, {
    title: '执行次数',
    dataIndex: 'executionCount',
    key: 'executionCount'
  }, {
    title: '操作',
    key: 'action',
    render: function render(_, record) {
      return /*#__PURE__*/React__default.createElement(Space, null, /*#__PURE__*/React__default.createElement(Button, {
        type: "link",
        size: "small",
        icon: /*#__PURE__*/React__default.createElement(BarChartOutlined, null),
        onClick: function onClick() {
          setSelectedTaskId(record.id);
          loadPerformanceReport(record.id);
        }
      }, "\u67E5\u770B\u6027\u80FD"), /*#__PURE__*/React__default.createElement(Button, {
        type: "link",
        size: "small",
        icon: /*#__PURE__*/React__default.createElement(ThunderboltOutlined$1, null),
        onClick: function onClick() {
          return handleAnalyzePerformance(record.id);
        },
        loading: loading && selectedTaskId === record.id
      }, "\u5206\u6790\u6027\u80FD"));
    }
  }];
  var selectedTask = tasks.find(function (t) {
    return t.id === selectedTaskId;
  });
  return /*#__PURE__*/React__default.createElement("div", {
    className: "airdrop-performance"
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
          loadPerformanceReport(record.id);
        },
        style: {
          cursor: 'pointer',
          background: selectedTaskId === record.id ? '#e6f7ff' : 'transparent'
        }
      };
    }
  }))), /*#__PURE__*/React__default.createElement(Col, {
    span: 16
  }, selectedTaskId ? /*#__PURE__*/React__default.createElement(Card, {
    title: /*#__PURE__*/React__default.createElement("span", null, /*#__PURE__*/React__default.createElement(ThunderboltOutlined$1, null), " \u6027\u80FD\u5206\u6790 - ", (selectedTask === null || selectedTask === void 0 ? void 0 : selectedTask.taskName) || selectedTaskId),
    extra: /*#__PURE__*/React__default.createElement(Button, {
      icon: /*#__PURE__*/React__default.createElement(ThunderboltOutlined$1, null),
      onClick: function onClick() {
        return handleAnalyzePerformance(selectedTaskId);
      },
      loading: loading
    }, "\u91CD\u65B0\u5206\u6790")
  }, performanceReport ? /*#__PURE__*/React__default.createElement(React__default.Fragment, null, /*#__PURE__*/React__default.createElement(Row, {
    gutter: 16,
    style: {
      marginBottom: 16
    }
  }, /*#__PURE__*/React__default.createElement(Col, {
    span: 6
  }, /*#__PURE__*/React__default.createElement(Card, null, /*#__PURE__*/React__default.createElement(Statistic, {
    title: "\u5E73\u5747\u6267\u884C\u65F6\u95F4",
    value: performanceReport.avgExecutionTime || 0,
    suffix: "ms",
    valueStyle: {
      color: '#1890ff'
    }
  }))), /*#__PURE__*/React__default.createElement(Col, {
    span: 6
  }, /*#__PURE__*/React__default.createElement(Card, null, /*#__PURE__*/React__default.createElement(Statistic, {
    title: "\u6700\u5927\u6267\u884C\u65F6\u95F4",
    value: performanceReport.maxExecutionTime || 0,
    suffix: "ms",
    valueStyle: {
      color: '#cf1322'
    }
  }))), /*#__PURE__*/React__default.createElement(Col, {
    span: 6
  }, /*#__PURE__*/React__default.createElement(Card, null, /*#__PURE__*/React__default.createElement(Statistic, {
    title: "\u6700\u5C0F\u6267\u884C\u65F6\u95F4",
    value: performanceReport.minExecutionTime || 0,
    suffix: "ms",
    valueStyle: {
      color: '#3f8600'
    }
  }))), /*#__PURE__*/React__default.createElement(Col, {
    span: 6
  }, /*#__PURE__*/React__default.createElement(Card, null, /*#__PURE__*/React__default.createElement(Statistic, {
    title: "\u5E73\u5747\u5185\u5B58\u4F7F\u7528",
    value: performanceReport.avgMemoryUsage || 0,
    suffix: "MB"
  })))), /*#__PURE__*/React__default.createElement(Card, {
    title: "\u6027\u80FD\u6307\u6807\u8BE6\u60C5",
    size: "small",
    style: {
      marginBottom: 16
    }
  }, /*#__PURE__*/React__default.createElement(Descriptions, {
    column: 2,
    bordered: true,
    size: "small"
  }, /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u603B\u6267\u884C\u6B21\u6570"
  }, performanceReport.totalExecutions || 0), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u5E73\u5747CPU\u4F7F\u7528\u7387"
  }, performanceReport.avgCpuUsage || 0, "%"), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u5CF0\u503C\u5185\u5B58\u4F7F\u7528"
  }, performanceReport.peakMemoryUsage || 0, " MB"), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u5E73\u5747\u7F51\u7EDC\u5EF6\u8FDF"
  }, performanceReport.avgNetworkLatency || 0, " ms"))), performanceReport.bottlenecks && performanceReport.bottlenecks.length > 0 && /*#__PURE__*/React__default.createElement(Card, {
    title: "\u6027\u80FD\u74F6\u9888",
    size: "small",
    style: {
      marginBottom: 16
    }
  }, /*#__PURE__*/React__default.createElement(Alert, {
    message: "\u68C0\u6D4B\u5230\u6027\u80FD\u74F6\u9888",
    description: "\u4EE5\u4E0B\u90E8\u5206\u53EF\u80FD\u5B58\u5728\u6027\u80FD\u95EE\u9898\uFF0C\u5EFA\u8BAE\u4F18\u5316",
    type: "warning",
    showIcon: true,
    style: {
      marginBottom: 16
    }
  }), performanceReport.bottlenecks.map(function (bottleneck, index) {
    return /*#__PURE__*/React__default.createElement(Card, {
      key: index,
      size: "small",
      style: {
        marginBottom: 8
      }
    }, /*#__PURE__*/React__default.createElement("div", null, /*#__PURE__*/React__default.createElement("strong", null, bottleneck.type)), /*#__PURE__*/React__default.createElement("div", {
      style: {
        marginTop: 4,
        fontSize: 12,
        color: '#666'
      }
    }, bottleneck.description), bottleneck.suggestion && /*#__PURE__*/React__default.createElement("div", {
      style: {
        marginTop: 8,
        fontSize: 12,
        color: '#1890ff'
      }
    }, "\u5EFA\u8BAE: ", bottleneck.suggestion));
  })), performanceReport.optimizationSuggestions && performanceReport.optimizationSuggestions.length > 0 && /*#__PURE__*/React__default.createElement(Card, {
    title: "\u4F18\u5316\u5EFA\u8BAE",
    size: "small"
  }, /*#__PURE__*/React__default.createElement("ul", {
    style: {
      margin: 0,
      paddingLeft: 20
    }
  }, performanceReport.optimizationSuggestions.map(function (suggestion, index) {
    return /*#__PURE__*/React__default.createElement("li", {
      key: index,
      style: {
        marginBottom: 8
      }
    }, suggestion);
  }))), /*#__PURE__*/React__default.createElement("div", {
    style: {
      marginTop: 16
    }
  }, /*#__PURE__*/React__default.createElement(Button, {
    icon: /*#__PURE__*/React__default.createElement(FileTextOutlined, null),
    onClick: function onClick() {
      return setReportModalVisible(true);
    }
  }, "\u67E5\u770B\u5B8C\u6574\u62A5\u544A"))) : /*#__PURE__*/React__default.createElement(Alert, {
    message: "\u6682\u65E0\u6027\u80FD\u6570\u636E",
    description: "\u70B9\u51FB[\u91CD\u65B0\u5206\u6790]\u6309\u94AE\u5F00\u59CB\u6027\u80FD\u5206\u6790",
    type: "info",
    showIcon: true
  })) : /*#__PURE__*/React__default.createElement(Card, null, /*#__PURE__*/React__default.createElement(Alert, {
    message: "\u8BF7\u9009\u62E9\u4EFB\u52A1",
    description: "\u4ECE\u5DE6\u4FA7\u5217\u8868\u4E2D\u9009\u62E9\u4E00\u4E2A\u4EFB\u52A1\u6765\u67E5\u770B\u6027\u80FD\u5206\u6790",
    type: "info",
    showIcon: true
  })))), /*#__PURE__*/React__default.createElement(Modal, {
    title: "\u6027\u80FD\u5206\u6790\u62A5\u544A",
    visible: reportModalVisible,
    onCancel: function onCancel() {
      setReportModalVisible(false);
    },
    footer: [/*#__PURE__*/React__default.createElement(Button, {
      key: "close",
      onClick: function onClick() {
        return setReportModalVisible(false);
      }
    }, "\u5173\u95ED")],
    width: 900
  }, performanceReport && /*#__PURE__*/React__default.createElement(Tabs, null, /*#__PURE__*/React__default.createElement(TabPane, {
    tab: "\u6027\u80FD\u6307\u6807",
    key: "metrics"
  }, /*#__PURE__*/React__default.createElement(Descriptions, {
    column: 2,
    bordered: true
  }, /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u603B\u6267\u884C\u6B21\u6570"
  }, performanceReport.totalExecutions || 0), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u5E73\u5747\u6267\u884C\u65F6\u95F4"
  }, performanceReport.avgExecutionTime || 0, " ms"), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u6700\u5927\u6267\u884C\u65F6\u95F4"
  }, performanceReport.maxExecutionTime || 0, " ms"), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u6700\u5C0F\u6267\u884C\u65F6\u95F4"
  }, performanceReport.minExecutionTime || 0, " ms"), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u5E73\u5747CPU\u4F7F\u7528\u7387"
  }, performanceReport.avgCpuUsage || 0, "%"), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u5E73\u5747\u5185\u5B58\u4F7F\u7528"
  }, performanceReport.avgMemoryUsage || 0, " MB"), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u5CF0\u503C\u5185\u5B58\u4F7F\u7528"
  }, performanceReport.peakMemoryUsage || 0, " MB"), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u5E73\u5747\u7F51\u7EDC\u5EF6\u8FDF"
  }, performanceReport.avgNetworkLatency || 0, " ms"))), /*#__PURE__*/React__default.createElement(TabPane, {
    tab: "\u6027\u80FD\u74F6\u9888",
    key: "bottlenecks"
  }, performanceReport.bottlenecks && performanceReport.bottlenecks.length > 0 ? performanceReport.bottlenecks.map(function (bottleneck, index) {
    return /*#__PURE__*/React__default.createElement(Card, {
      key: index,
      size: "small",
      style: {
        marginBottom: 8
      }
    }, /*#__PURE__*/React__default.createElement("div", null, /*#__PURE__*/React__default.createElement("strong", null, bottleneck.type)), /*#__PURE__*/React__default.createElement("div", {
      style: {
        marginTop: 4,
        fontSize: 12,
        color: '#666'
      }
    }, bottleneck.description), bottleneck.suggestion && /*#__PURE__*/React__default.createElement("div", {
      style: {
        marginTop: 8,
        fontSize: 12,
        color: '#1890ff'
      }
    }, "\u5EFA\u8BAE: ", bottleneck.suggestion));
  }) : /*#__PURE__*/React__default.createElement(Alert, {
    message: "\u672A\u68C0\u6D4B\u5230\u6027\u80FD\u74F6\u9888",
    type: "success",
    showIcon: true
  })), /*#__PURE__*/React__default.createElement(TabPane, {
    tab: "\u4F18\u5316\u5EFA\u8BAE",
    key: "suggestions"
  }, performanceReport.optimizationSuggestions && performanceReport.optimizationSuggestions.length > 0 ? /*#__PURE__*/React__default.createElement("ul", {
    style: {
      margin: 0,
      paddingLeft: 20
    }
  }, performanceReport.optimizationSuggestions.map(function (suggestion, index) {
    return /*#__PURE__*/React__default.createElement("li", {
      key: index,
      style: {
        marginBottom: 8
      }
    }, suggestion);
  })) : /*#__PURE__*/React__default.createElement(Alert, {
    message: "\u6682\u65E0\u4F18\u5316\u5EFA\u8BAE",
    type: "info",
    showIcon: true
  })))));
};

export { AirdropPerformance, AirdropPerformance as default };
