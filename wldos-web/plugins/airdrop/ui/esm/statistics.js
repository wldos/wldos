import { _ as _slicedToArray, a as _asyncToGenerator, b as _regenerator, E as statisticsAPI, f as exportAPI } from './api-72a577fe.js';
const React__default = window.React;
const { useState, useEffect } = window.React;
const { DatePicker, Select, Card, Space, Button, Row, Col, Statistic, Table, Alert, message } = window.antd;
import { R as ReloadOutlined, E as ExportOutlined } from './ReloadOutlined-f837a332.js';
import { B as BarChartOutlined } from './BarChartOutlined-2df7c7d3.js';
import './AntdIcon-16063a91.js';

var RangePicker = DatePicker.RangePicker;
var Option = Select.Option;
var AirdropStatistics = function AirdropStatistics() {
  var _useState = useState({}),
    _useState2 = _slicedToArray(_useState, 2),
    overallStats = _useState2[0],
    setOverallStats = _useState2[1];
  var _useState3 = useState({}),
    _useState4 = _slicedToArray(_useState3, 2),
    chartData = _useState4[0],
    setChartData = _useState4[1];
  var _useState5 = useState(false),
    _useState6 = _slicedToArray(_useState5, 2);
    _useState6[0];
    var setLoading = _useState6[1];
  var _useState7 = useState('7d'),
    _useState8 = _slicedToArray(_useState7, 2),
    timeRange = _useState8[0],
    setTimeRange = _useState8[1]; // 7天, 30天, 90天, 自定义
  var _useState9 = useState(null),
    _useState0 = _slicedToArray(_useState9, 2),
    customDateRange = _useState0[0],
    setCustomDateRange = _useState0[1];
  var _useState1 = useState('execution_trend'),
    _useState10 = _slicedToArray(_useState1, 2),
    chartType = _useState10[0],
    setChartType = _useState10[1];

  // 加载总体统计
  var loadOverallStatistics = /*#__PURE__*/function () {
    var _ref = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee() {
      var res, _t;
      return _regenerator().w(function (_context) {
        while (1) switch (_context.p = _context.n) {
          case 0:
            _context.p = 0;
            setLoading(true);
            _context.n = 1;
            return statisticsAPI.getOverallStatistics();
          case 1:
            res = _context.v;
            setOverallStats((res === null || res === void 0 ? void 0 : res.data) || {});
            _context.n = 3;
            break;
          case 2:
            _context.p = 2;
            _t = _context.v;
            message.error('加载统计数据失败: ' + _t.message);
          case 3:
            _context.p = 3;
            setLoading(false);
            return _context.f(3);
          case 4:
            return _context.a(2);
        }
      }, _callee, null, [[0, 2, 3, 4]]);
    }));
    return function loadOverallStatistics() {
      return _ref.apply(this, arguments);
    };
  }();

  // 加载图表数据
  var loadChartData = /*#__PURE__*/function () {
    var _ref2 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee2() {
      var res, _t2;
      return _regenerator().w(function (_context2) {
        while (1) switch (_context2.p = _context2.n) {
          case 0:
            _context2.p = 0;
            _context2.n = 1;
            return statisticsAPI.getChartData(chartType, timeRange);
          case 1:
            res = _context2.v;
            setChartData((res === null || res === void 0 ? void 0 : res.data) || {});
            _context2.n = 3;
            break;
          case 2:
            _context2.p = 2;
            _t2 = _context2.v;
            message.error('加载图表数据失败: ' + _t2.message);
          case 3:
            return _context2.a(2);
        }
      }, _callee2, null, [[0, 2]]);
    }));
    return function loadChartData() {
      return _ref2.apply(this, arguments);
    };
  }();
  useEffect(function () {
    loadOverallStatistics();
  }, []);
  useEffect(function () {
    loadChartData();
  }, [chartType, timeRange]);

  // 导出统计报表
  var handleExport = /*#__PURE__*/function () {
    var _ref3 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee3() {
      var format,
        res,
        blob,
        link,
        url,
        _args3 = arguments,
        _t3;
      return _regenerator().w(function (_context3) {
        while (1) switch (_context3.p = _context3.n) {
          case 0:
            format = _args3.length > 0 && _args3[0] !== undefined ? _args3[0] : 'csv';
            _context3.p = 1;
            _context3.n = 2;
            return exportAPI.exportStatistics(format);
          case 2:
            res = _context3.v;
            blob = new Blob([res], {
              type: 'text/csv;charset=utf-8;'
            });
            link = document.createElement('a');
            url = URL.createObjectURL(blob);
            link.setAttribute('href', url);
            link.setAttribute('download', "statistics_".concat(new Date().getTime(), ".").concat(format));
            link.style.visibility = 'hidden';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            message.success('导出成功');
            _context3.n = 4;
            break;
          case 3:
            _context3.p = 3;
            _t3 = _context3.v;
            message.error('导出失败: ' + _t3.message);
          case 4:
            return _context3.a(2);
        }
      }, _callee3, null, [[1, 3]]);
    }));
    return function handleExport() {
      return _ref3.apply(this, arguments);
    };
  }();

  // 渲染图表（简化版，实际应使用ECharts或Ant Design Charts）
  var renderChart = function renderChart() {
    if (!chartData || Object.keys(chartData).length === 0) {
      return /*#__PURE__*/React__default.createElement(Alert, {
        message: "\u6682\u65E0\u56FE\u8868\u6570\u636E",
        description: "\u5F53\u524D\u65F6\u95F4\u8303\u56F4\u5185\u6CA1\u6709\u7EDF\u8BA1\u6570\u636E",
        type: "info",
        showIcon: true
      });
    }

    // 这里应该使用图表库（如ECharts）来渲染图表
    // 当前使用简单的文本展示
    return /*#__PURE__*/React__default.createElement("div", {
      style: {
        padding: 20,
        background: '#fafafa',
        borderRadius: 4,
        minHeight: 300
      }
    }, /*#__PURE__*/React__default.createElement("div", {
      style: {
        marginBottom: 16
      }
    }, /*#__PURE__*/React__default.createElement("strong", null, "\u56FE\u8868\u7C7B\u578B\uFF1A"), chartType), /*#__PURE__*/React__default.createElement("div", {
      style: {
        marginBottom: 16
      }
    }, /*#__PURE__*/React__default.createElement("strong", null, "\u65F6\u95F4\u8303\u56F4\uFF1A"), timeRange), /*#__PURE__*/React__default.createElement("pre", {
      style: {
        background: '#fff',
        padding: 16,
        borderRadius: 4,
        overflow: 'auto'
      }
    }, JSON.stringify(chartData, null, 2)), /*#__PURE__*/React__default.createElement(Alert, {
      message: "\u56FE\u8868\u53EF\u89C6\u5316",
      description: "\u5B8C\u6574\u7684\u56FE\u8868\u53EF\u89C6\u5316\u9700\u8981\u4F7F\u7528\u56FE\u8868\u5E93\uFF08\u5982ECharts\u3001Ant Design Charts\u7B49\uFF09\u5B9E\u73B0\u3002\u5F53\u524D\u5C55\u793A\u7684\u662F\u539F\u59CB\u6570\u636E\u3002",
      type: "warning",
      showIcon: true,
      style: {
        marginTop: 16
      }
    }));
  };
  return /*#__PURE__*/React__default.createElement("div", {
    className: "airdrop-statistics"
  }, /*#__PURE__*/React__default.createElement(Card, {
    size: "small",
    style: {
      marginBottom: 16
    }
  }, /*#__PURE__*/React__default.createElement(Space, null, /*#__PURE__*/React__default.createElement("span", null, "\u65F6\u95F4\u8303\u56F4\uFF1A"), /*#__PURE__*/React__default.createElement(Select, {
    value: timeRange,
    onChange: setTimeRange,
    style: {
      width: 120
    }
  }, /*#__PURE__*/React__default.createElement(Option, {
    value: "7d"
  }, "\u6700\u8FD17\u5929"), /*#__PURE__*/React__default.createElement(Option, {
    value: "30d"
  }, "\u6700\u8FD130\u5929"), /*#__PURE__*/React__default.createElement(Option, {
    value: "90d"
  }, "\u6700\u8FD190\u5929"), /*#__PURE__*/React__default.createElement(Option, {
    value: "custom"
  }, "\u81EA\u5B9A\u4E49")), timeRange === 'custom' && /*#__PURE__*/React__default.createElement(RangePicker, {
    value: customDateRange,
    onChange: setCustomDateRange
  }), /*#__PURE__*/React__default.createElement(Select, {
    value: chartType,
    onChange: setChartType,
    style: {
      width: 150
    }
  }, /*#__PURE__*/React__default.createElement(Option, {
    value: "execution_trend"
  }, "\u6267\u884C\u8D8B\u52BF"), /*#__PURE__*/React__default.createElement(Option, {
    value: "success_rate"
  }, "\u6210\u529F\u7387"), /*#__PURE__*/React__default.createElement(Option, {
    value: "task_distribution"
  }, "\u4EFB\u52A1\u5206\u5E03"), /*#__PURE__*/React__default.createElement(Option, {
    value: "performance"
  }, "\u6027\u80FD\u5206\u6790")), /*#__PURE__*/React__default.createElement(Button, {
    icon: /*#__PURE__*/React__default.createElement(ReloadOutlined, null),
    onClick: function onClick() {
      loadOverallStatistics();
      loadChartData();
    }
  }, "\u5237\u65B0"), /*#__PURE__*/React__default.createElement(Button, {
    icon: /*#__PURE__*/React__default.createElement(ExportOutlined, null),
    onClick: function onClick() {
      return handleExport('csv');
    }
  }, "\u5BFC\u51FA\u62A5\u8868"))), /*#__PURE__*/React__default.createElement(Row, {
    gutter: 16,
    style: {
      marginBottom: 16
    }
  }, /*#__PURE__*/React__default.createElement(Col, {
    span: 6
  }, /*#__PURE__*/React__default.createElement(Card, null, /*#__PURE__*/React__default.createElement(Statistic, {
    title: "\u603B\u4EFB\u52A1\u6570",
    value: overallStats.totalTasks || 0,
    prefix: /*#__PURE__*/React__default.createElement(BarChartOutlined, null)
  }))), /*#__PURE__*/React__default.createElement(Col, {
    span: 6
  }, /*#__PURE__*/React__default.createElement(Card, null, /*#__PURE__*/React__default.createElement(Statistic, {
    title: "\u603B\u6267\u884C\u6B21\u6570",
    value: overallStats.totalExecutions || 0,
    valueStyle: {
      color: '#1890ff'
    }
  }))), /*#__PURE__*/React__default.createElement(Col, {
    span: 6
  }, /*#__PURE__*/React__default.createElement(Card, null, /*#__PURE__*/React__default.createElement(Statistic, {
    title: "\u6210\u529F\u6B21\u6570",
    value: overallStats.successCount || 0,
    valueStyle: {
      color: '#3f8600'
    }
  }))), /*#__PURE__*/React__default.createElement(Col, {
    span: 6
  }, /*#__PURE__*/React__default.createElement(Card, null, /*#__PURE__*/React__default.createElement(Statistic, {
    title: "\u5931\u8D25\u6B21\u6570",
    value: overallStats.failureCount || 0,
    valueStyle: {
      color: '#cf1322'
    }
  })))), /*#__PURE__*/React__default.createElement(Row, {
    gutter: 16,
    style: {
      marginBottom: 16
    }
  }, /*#__PURE__*/React__default.createElement(Col, {
    span: 6
  }, /*#__PURE__*/React__default.createElement(Card, null, /*#__PURE__*/React__default.createElement(Statistic, {
    title: "\u6210\u529F\u7387",
    value: overallStats.successRate || 0,
    precision: 2,
    suffix: "%",
    valueStyle: {
      color: '#3f8600'
    }
  }))), /*#__PURE__*/React__default.createElement(Col, {
    span: 6
  }, /*#__PURE__*/React__default.createElement(Card, null, /*#__PURE__*/React__default.createElement(Statistic, {
    title: "\u5E73\u5747\u6267\u884C\u65F6\u957F",
    value: overallStats.avgExecutionTime || 0,
    suffix: "ms"
  }))), /*#__PURE__*/React__default.createElement(Col, {
    span: 6
  }, /*#__PURE__*/React__default.createElement(Card, null, /*#__PURE__*/React__default.createElement(Statistic, {
    title: "\u8FD0\u884C\u4E2D\u4EFB\u52A1",
    value: overallStats.runningTasks || 0,
    valueStyle: {
      color: '#1890ff'
    }
  }))), /*#__PURE__*/React__default.createElement(Col, {
    span: 6
  }, /*#__PURE__*/React__default.createElement(Card, null, /*#__PURE__*/React__default.createElement(Statistic, {
    title: "\u5F85\u6267\u884C\u4EFB\u52A1",
    value: overallStats.pendingTasks || 0,
    valueStyle: {
      color: '#faad14'
    }
  })))), /*#__PURE__*/React__default.createElement(Card, {
    title: /*#__PURE__*/React__default.createElement("span", null, /*#__PURE__*/React__default.createElement(BarChartOutlined, null), " \u7EDF\u8BA1\u56FE\u8868"),
    extra: /*#__PURE__*/React__default.createElement(Space, null, /*#__PURE__*/React__default.createElement(Button, {
      icon: /*#__PURE__*/React__default.createElement(ExportOutlined, null),
      onClick: function onClick() {
        return handleExport('csv');
      }
    }, "\u5BFC\u51FA\u6570\u636E"))
  }, renderChart()), overallStats.dailyStats && overallStats.dailyStats.length > 0 && /*#__PURE__*/React__default.createElement(Card, {
    title: "\u6BCF\u65E5\u7EDF\u8BA1",
    style: {
      marginTop: 16
    }
  }, /*#__PURE__*/React__default.createElement(Table, {
    columns: [{
      title: '日期',
      dataIndex: 'date',
      key: 'date'
    }, {
      title: '执行次数',
      dataIndex: 'executionCount',
      key: 'executionCount'
    }, {
      title: '成功次数',
      dataIndex: 'successCount',
      key: 'successCount'
    }, {
      title: '失败次数',
      dataIndex: 'failureCount',
      key: 'failureCount'
    }, {
      title: '成功率',
      dataIndex: 'successRate',
      key: 'successRate',
      render: function render(rate) {
        return "".concat((rate || 0).toFixed(2), "%");
      }
    }],
    dataSource: overallStats.dailyStats,
    rowKey: "date",
    pagination: {
      pageSize: 10
    }
  })));
};

export { AirdropStatistics, AirdropStatistics as default };
