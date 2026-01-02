import { h as _createClass, i as _objectWithoutProperties, j as _objectSpread2$1, k as _classCallCheck, _ as _slicedToArray, l as _typeof, a as _asyncToGenerator, b as _regenerator, v as visualizationAPI, m as _defineProperty, n as _toConsumableArray, o as executionAPI, t as taskAPI } from './api-72a577fe.js';
const React = window.React;
const React__default = window.React;
const { useState, useEffect, useRef } = window.React;
const { Tabs, Card, Empty, Spin, Table, Alert, message, Space, Button, Badge, Modal, Descriptions, Tag } = window.antd;
import { A as AntdIcon, _ as _objectSpread2 } from './AntdIcon-16063a91.js';
import { B as BarChartOutlined } from './BarChartOutlined-2df7c7d3.js';

// This icon file is generated automatically.
var DisconnectOutlined$2 = { "icon": { "tag": "svg", "attrs": { "viewBox": "64 64 896 896", "focusable": "false" }, "children": [{ "tag": "path", "attrs": { "d": "M832.6 191.4c-84.6-84.6-221.5-84.6-306 0l-96.9 96.9 51 51 96.9-96.9c53.8-53.8 144.6-59.5 204 0 59.5 59.5 53.8 150.2 0 204l-96.9 96.9 51.1 51.1 96.9-96.9c84.4-84.6 84.4-221.5-.1-306.1zM446.5 781.6c-53.8 53.8-144.6 59.5-204 0-59.5-59.5-53.8-150.2 0-204l96.9-96.9-51.1-51.1-96.9 96.9c-84.6 84.6-84.6 221.5 0 306s221.5 84.6 306 0l96.9-96.9-51-51-96.8 97zM260.3 209.4a8.03 8.03 0 00-11.3 0L209.4 249a8.03 8.03 0 000 11.3l554.4 554.4c3.1 3.1 8.2 3.1 11.3 0l39.6-39.6c3.1-3.1 3.1-8.2 0-11.3L260.3 209.4z" } }] }, "name": "disconnect", "theme": "outlined" };
var DisconnectOutlinedSvg = DisconnectOutlined$2;

var DisconnectOutlined = function DisconnectOutlined(props, ref) {
  return /*#__PURE__*/React.createElement(AntdIcon, _objectSpread2(_objectSpread2({}, props), {}, {
    ref: ref,
    icon: DisconnectOutlinedSvg
  }));
};
var RefIcon$2 = /*#__PURE__*/React.forwardRef(DisconnectOutlined);
var DisconnectOutlined$1 = RefIcon$2;

// This icon file is generated automatically.
var TableOutlined$2 = { "icon": { "tag": "svg", "attrs": { "viewBox": "64 64 896 896", "focusable": "false" }, "children": [{ "tag": "path", "attrs": { "d": "M928 160H96c-17.7 0-32 14.3-32 32v640c0 17.7 14.3 32 32 32h832c17.7 0 32-14.3 32-32V192c0-17.7-14.3-32-32-32zm-40 208H676V232h212v136zm0 224H676V432h212v160zM412 432h200v160H412V432zm200-64H412V232h200v136zm-476 64h212v160H136V432zm0-200h212v136H136V232zm0 424h212v136H136V656zm276 0h200v136H412V656zm476 136H676V656h212v136z" } }] }, "name": "table", "theme": "outlined" };
var TableOutlinedSvg = TableOutlined$2;

var TableOutlined = function TableOutlined(props, ref) {
  return /*#__PURE__*/React.createElement(AntdIcon, _objectSpread2(_objectSpread2({}, props), {}, {
    ref: ref,
    icon: TableOutlinedSvg
  }));
};
var RefIcon$1 = /*#__PURE__*/React.forwardRef(TableOutlined);
var TableOutlined$1 = RefIcon$1;

// This icon file is generated automatically.
var WifiOutlined$2 = { "icon": { "tag": "svg", "attrs": { "viewBox": "64 64 896 896", "focusable": "false" }, "children": [{ "tag": "path", "attrs": { "d": "M723 620.5C666.8 571.6 593.4 542 513 542s-153.8 29.6-210.1 78.6a8.1 8.1 0 00-.8 11.2l36 42.9c2.9 3.4 8 3.8 11.4.9C393.1 637.2 450.3 614 513 614s119.9 23.2 163.5 61.5c3.4 2.9 8.5 2.5 11.4-.9l36-42.9c2.8-3.3 2.4-8.3-.9-11.2zm117.4-140.1C751.7 406.5 637.6 362 513 362s-238.7 44.5-327.5 118.4a8.05 8.05 0 00-1 11.3l36 42.9c2.8 3.4 7.9 3.8 11.2 1C308 472.2 406.1 434 513 434s205 38.2 281.2 101.6c3.4 2.8 8.4 2.4 11.2-1l36-42.9c2.8-3.4 2.4-8.5-1-11.3zm116.7-139C835.7 241.8 680.3 182 511 182c-168.2 0-322.6 59-443.7 157.4a8 8 0 00-1.1 11.4l36 42.9c2.8 3.3 7.8 3.8 11.1 1.1C222 306.7 360.3 254 511 254c151.8 0 291 53.5 400 142.7 3.4 2.8 8.4 2.3 11.2-1.1l36-42.9c2.9-3.4 2.4-8.5-1.1-11.3zM448 778a64 64 0 10128 0 64 64 0 10-128 0z" } }] }, "name": "wifi", "theme": "outlined" };
var WifiOutlinedSvg = WifiOutlined$2;

var WifiOutlined = function WifiOutlined(props, ref) {
  return /*#__PURE__*/React.createElement(AntdIcon, _objectSpread2(_objectSpread2({}, props), {}, {
    ref: ref,
    icon: WifiOutlinedSvg
  }));
};
var RefIcon = /*#__PURE__*/React.forwardRef(WifiOutlined);
var WifiOutlined$1 = RefIcon;

var _excluded = ["type", "taskId"];
/**
 * WebSocket工具类
 * 用于实时接收任务状态更新
 */
var TaskStatusWebSocket = /*#__PURE__*/function () {
  function TaskStatusWebSocket(url) {
    _classCallCheck(this, TaskStatusWebSocket);
    this.url = url;
    this.ws = null;
    this.reconnectTimer = null;
    this.reconnectAttempts = 0;
    this.maxReconnectAttempts = 5;
    this.reconnectInterval = 3000;
    this.listeners = new Map();
    this.isConnected = false;
  }

  /**
   * 连接WebSocket
   */
  return _createClass(TaskStatusWebSocket, [{
    key: "connect",
    value: function connect() {
      var _this = this;
      if (this.ws && this.ws.readyState === WebSocket.OPEN) {
        console.log('WebSocket already connected');
        return;
      }
      try {
        this.ws = new WebSocket(this.url);
        this.ws.onopen = function () {
          console.log('WebSocket connected');
          _this.isConnected = true;
          _this.reconnectAttempts = 0;
          _this.emit('connected', {});
          _this.sendHeartbeat();
        };
        this.ws.onmessage = function (event) {
          try {
            var message = JSON.parse(event.data);
            _this.handleMessage(message);
          } catch (error) {
            console.error('Failed to parse WebSocket message:', error);
          }
        };
        this.ws.onerror = function (error) {
          console.error('WebSocket error:', error);
          _this.emit('error', {
            error: error
          });
        };
        this.ws.onclose = function () {
          console.log('WebSocket closed');
          _this.isConnected = false;
          _this.emit('disconnected', {});
          _this.attemptReconnect();
        };
      } catch (error) {
        console.error('Failed to create WebSocket connection:', error);
        this.attemptReconnect();
      }
    }

    /**
     * 处理收到的消息
     */
  }, {
    key: "handleMessage",
    value: function handleMessage(message) {
      var type = message.type,
        taskId = message.taskId,
        data = _objectWithoutProperties(message, _excluded);
      switch (type) {
        case 'TASK_STATUS_UPDATE':
          this.emit('taskStatusUpdate', _objectSpread2$1({
            taskId: taskId
          }, data));
          break;
        case 'TASK_LOG':
          this.emit('taskLog', _objectSpread2$1({
            taskId: taskId
          }, data));
          break;
        case 'DEBUG_INFO':
          this.emit('debugInfo', _objectSpread2$1({}, data));
          break;
        case 'SUBSCRIBED':
          this.emit('subscribed', _objectSpread2$1({
            taskId: taskId
          }, data));
          break;
        case 'UNSUBSCRIBED':
          this.emit('unsubscribed', _objectSpread2$1({
            taskId: taskId
          }, data));
          break;
        case 'PONG':
          // 心跳响应
          break;
        case 'ERROR':
          this.emit('error', {
            message: data.message
          });
          break;
        default:
          console.warn('Unknown message type:', type);
      }
    }

    /**
     * 订阅任务状态
     */
  }, {
    key: "subscribe",
    value: function subscribe(taskId) {
      if (!this.isConnected) {
        console.warn('WebSocket not connected, cannot subscribe');
        return;
      }
      this.send({
        action: 'SUBSCRIBE',
        taskId: taskId
      });
    }

    /**
     * 取消订阅任务状态
     */
  }, {
    key: "unsubscribe",
    value: function unsubscribe(taskId) {
      if (!this.isConnected) {
        console.warn('WebSocket not connected, cannot unsubscribe');
        return;
      }
      this.send({
        action: 'UNSUBSCRIBE',
        taskId: taskId
      });
    }

    /**
     * 发送消息
     */
  }, {
    key: "send",
    value: function send(data) {
      if (this.ws && this.ws.readyState === WebSocket.OPEN) {
        this.ws.send(JSON.stringify(data));
      } else {
        console.warn('WebSocket not connected, cannot send message');
      }
    }

    /**
     * 发送心跳
     */
  }, {
    key: "sendHeartbeat",
    value: function sendHeartbeat() {
      var _this2 = this;
      if (this.heartbeatTimer) {
        clearInterval(this.heartbeatTimer);
      }
      this.heartbeatTimer = setInterval(function () {
        if (_this2.isConnected) {
          _this2.send({
            action: 'HEARTBEAT'
          });
        }
      }, 30000); // 每30秒发送一次心跳
    }

    /**
     * 尝试重连
     */
  }, {
    key: "attemptReconnect",
    value: function attemptReconnect() {
      var _this3 = this;
      if (this.reconnectAttempts >= this.maxReconnectAttempts) {
        console.error('Max reconnection attempts reached');
        this.emit('reconnectFailed', {});
        return;
      }
      this.reconnectAttempts++;
      console.log("Attempting to reconnect (".concat(this.reconnectAttempts, "/").concat(this.maxReconnectAttempts, ")..."));
      this.reconnectTimer = setTimeout(function () {
        _this3.connect();
      }, this.reconnectInterval);
    }

    /**
     * 添加事件监听器
     */
  }, {
    key: "on",
    value: function on(event, callback) {
      if (!this.listeners.has(event)) {
        this.listeners.set(event, []);
      }
      this.listeners.get(event).push(callback);
    }

    /**
     * 移除事件监听器
     */
  }, {
    key: "off",
    value: function off(event, callback) {
      if (this.listeners.has(event)) {
        var callbacks = this.listeners.get(event);
        var index = callbacks.indexOf(callback);
        if (index > -1) {
          callbacks.splice(index, 1);
        }
      }
    }

    /**
     * 触发事件
     */
  }, {
    key: "emit",
    value: function emit(event, data) {
      if (this.listeners.has(event)) {
        this.listeners.get(event).forEach(function (callback) {
          try {
            callback(data);
          } catch (error) {
            console.error("Error in event listener for ".concat(event, ":"), error);
          }
        });
      }
    }

    /**
     * 断开连接
     */
  }, {
    key: "disconnect",
    value: function disconnect() {
      if (this.reconnectTimer) {
        clearTimeout(this.reconnectTimer);
      }
      if (this.heartbeatTimer) {
        clearInterval(this.heartbeatTimer);
      }
      if (this.ws) {
        this.ws.close();
        this.ws = null;
      }
      this.isConnected = false;
      this.listeners.clear();
    }
  }]);
}();
/**
 * 创建WebSocket连接
 * @param {string} baseUrl - WebSocket基础URL
 * @returns {TaskStatusWebSocket}
 */
var createWebSocket = function createWebSocket() {
  var baseUrl = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : '';
  // 将http://或https://转换为ws://或wss://
  var wsUrl = baseUrl.replace(/^http:/, 'ws:').replace(/^https:/, 'wss:').replace(/\/$/, '') + '/ws/airdrop/task-status';
  return new TaskStatusWebSocket(wsUrl);
};

var TabPane = Tabs.TabPane;
var ResultVisualization = function ResultVisualization(_ref) {
  var result = _ref.result,
    resultType = _ref.resultType,
    onVisualize = _ref.onVisualize;
  var _useState = useState(null),
    _useState2 = _slicedToArray(_useState, 2),
    visualizationData = _useState2[0],
    setVisualizationData = _useState2[1];
  var _useState3 = useState(false),
    _useState4 = _slicedToArray(_useState3, 2),
    loading = _useState4[0],
    setLoading = _useState4[1];
  useEffect(function () {
    if (result) {
      loadVisualization();
    }
  }, [result, resultType]);

  // 加载可视化数据
  var loadVisualization = /*#__PURE__*/function () {
    var _ref2 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee() {
      var res, _t;
      return _regenerator().w(function (_context) {
        while (1) switch (_context.p = _context.n) {
          case 0:
            _context.p = 0;
            setLoading(true);
            _context.n = 1;
            return visualizationAPI.parseResult(result, resultType);
          case 1:
            res = _context.v;
            setVisualizationData(res === null || res === void 0 ? void 0 : res.data);
            if (onVisualize) {
              onVisualize(res === null || res === void 0 ? void 0 : res.data);
            }
            _context.n = 3;
            break;
          case 2:
            _context.p = 2;
            _t = _context.v;
            console.error('加载可视化数据失败', _t);
          case 3:
            _context.p = 3;
            setLoading(false);
            return _context.f(3);
          case 4:
            return _context.a(2);
        }
      }, _callee, null, [[0, 2, 3, 4]]);
    }));
    return function loadVisualization() {
      return _ref2.apply(this, arguments);
    };
  }();

  // 渲染表格
  var renderTable = function renderTable(data) {
    if (!data || !data.parsedData) {
      return /*#__PURE__*/React__default.createElement(Empty, {
        description: "\u65E0\u6570\u636E\u53EF\u5C55\u793A"
      });
    }
    var parsedData = data.parsedData;

    // 如果是CSV格式的数据
    if (parsedData.headers && parsedData.rows) {
      var columns = parsedData.headers.map(function (header) {
        return {
          title: header,
          dataIndex: header,
          key: header
        };
      });
      var dataSource = parsedData.rows.map(function (row, index) {
        return _objectSpread2$1({
          key: index
        }, row);
      });
      return /*#__PURE__*/React__default.createElement(Table, {
        columns: columns,
        dataSource: dataSource,
        pagination: {
          pageSize: 10
        }
      });
    }

    // 如果是列表数据
    if (parsedData.items) {
      if (parsedData.items.length === 0) {
        return /*#__PURE__*/React__default.createElement(Empty, {
          description: "\u65E0\u6570\u636E"
        });
      }

      // 获取第一个对象的所有键作为列
      var firstItem = parsedData.items[0];
      if (_typeof(firstItem) === 'object' && firstItem !== null) {
        var _columns = Object.keys(firstItem).map(function (key) {
          return {
            title: key,
            dataIndex: key,
            key: key
          };
        });
        var _dataSource = parsedData.items.map(function (item, index) {
          return _objectSpread2$1({
            key: index
          }, item);
        });
        return /*#__PURE__*/React__default.createElement(Table, {
          columns: _columns,
          dataSource: _dataSource,
          pagination: {
            pageSize: 10
          }
        });
      }
    }
    return /*#__PURE__*/React__default.createElement(Empty, {
      description: "\u6570\u636E\u683C\u5F0F\u4E0D\u652F\u6301\u8868\u683C\u5C55\u793A"
    });
  };

  // 渲染图表（简化版，实际应使用图表库）
  var renderChart = function renderChart(data) {
    if (!data || !data.visualizationConfig) {
      return /*#__PURE__*/React__default.createElement(Alert, {
        message: "\u56FE\u8868\u6570\u636E",
        description: "\u5B8C\u6574\u7684\u56FE\u8868\u53EF\u89C6\u5316\u9700\u8981\u4F7F\u7528\u56FE\u8868\u5E93\uFF08\u5982ECharts\u3001Ant Design Charts\u7B49\uFF09\u5B9E\u73B0\u3002\u5F53\u524D\u5C55\u793A\u7684\u662F\u914D\u7F6E\u4FE1\u606F\u3002",
        type: "info",
        showIcon: true
      });
    }
    var config = data.visualizationConfig;
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
    }, /*#__PURE__*/React__default.createElement("strong", null, "\u56FE\u8868\u7C7B\u578B\uFF1A"), config.chartType || '未知'), /*#__PURE__*/React__default.createElement("div", {
      style: {
        marginBottom: 16
      }
    }, /*#__PURE__*/React__default.createElement("strong", null, "\u6570\u636E\u70B9\u6570\u91CF\uFF1A"), config.dataPoints || 0), /*#__PURE__*/React__default.createElement("pre", {
      style: {
        background: '#fff',
        padding: 16,
        borderRadius: 4,
        overflow: 'auto'
      }
    }, JSON.stringify(config, null, 2)), /*#__PURE__*/React__default.createElement(Alert, {
      message: "\u56FE\u8868\u53EF\u89C6\u5316",
      description: "\u5B8C\u6574\u7684\u56FE\u8868\u53EF\u89C6\u5316\u9700\u8981\u4F7F\u7528\u56FE\u8868\u5E93\uFF08\u5982ECharts\u3001Ant Design Charts\u7B49\uFF09\u5B9E\u73B0\u3002\u5F53\u524D\u5C55\u793A\u7684\u662F\u914D\u7F6E\u4FE1\u606F\u3002",
      type: "warning",
      showIcon: true,
      style: {
        marginTop: 16
      }
    }));
  };
  if (!result) {
    return /*#__PURE__*/React__default.createElement(Card, null, /*#__PURE__*/React__default.createElement(Empty, {
      description: "\u6682\u65E0\u7ED3\u679C\u6570\u636E"
    }));
  }
  if (loading) {
    return /*#__PURE__*/React__default.createElement(Card, null, /*#__PURE__*/React__default.createElement(Spin, {
      tip: "\u6B63\u5728\u89E3\u6790\u6570\u636E..."
    }));
  }
  if (!visualizationData) {
    return /*#__PURE__*/React__default.createElement(Card, null, /*#__PURE__*/React__default.createElement(Empty, {
      description: "\u65E0\u6CD5\u89E3\u6790\u6570\u636E"
    }));
  }
  return /*#__PURE__*/React__default.createElement(Card, {
    title: "\u7ED3\u679C\u53EF\u89C6\u5316"
  }, /*#__PURE__*/React__default.createElement(Tabs, {
    defaultActiveKey: "table"
  }, /*#__PURE__*/React__default.createElement(TabPane, {
    tab: /*#__PURE__*/React__default.createElement("span", null, /*#__PURE__*/React__default.createElement(TableOutlined$1, null), " \u8868\u683C\u89C6\u56FE"),
    key: "table"
  }, renderTable(visualizationData)), /*#__PURE__*/React__default.createElement(TabPane, {
    tab: /*#__PURE__*/React__default.createElement("span", null, /*#__PURE__*/React__default.createElement(BarChartOutlined, null), " \u56FE\u8868\u89C6\u56FE"),
    key: "chart"
  }, renderChart(visualizationData)), /*#__PURE__*/React__default.createElement(TabPane, {
    tab: "\u539F\u59CB\u6570\u636E",
    key: "raw"
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
  }, _typeof(visualizationData.rawData) === 'object' ? JSON.stringify(visualizationData.rawData, null, 2) : String(visualizationData.rawData)))));
};

var AirdropExecution = function AirdropExecution() {
  var _useState = useState([]),
    _useState2 = _slicedToArray(_useState, 2),
    executions = _useState2[0],
    setExecutions = _useState2[1];
  var _useState3 = useState(false),
    _useState4 = _slicedToArray(_useState3, 2),
    loading = _useState4[0],
    setLoading = _useState4[1];
  var _useState5 = useState(false),
    _useState6 = _slicedToArray(_useState5, 2),
    detailModalVisible = _useState6[0],
    setDetailModalVisible = _useState6[1];
  var _useState7 = useState(null),
    _useState8 = _slicedToArray(_useState7, 2),
    currentExecution = _useState8[0],
    setCurrentExecution = _useState8[1];
  var _useState9 = useState(false),
    _useState0 = _slicedToArray(_useState9, 2),
    wsConnected = _useState0[0],
    setWsConnected = _useState0[1];
  var _useState1 = useState({}),
    _useState10 = _slicedToArray(_useState1, 2),
    realTimeLogs = _useState10[0],
    setRealTimeLogs = _useState10[1]; // taskId -> logs[]
  var wsRef = useRef(null);
  var subscribedTasksRef = useRef(new Set());

  // 加载执行记录
  var loadExecutions = /*#__PURE__*/function () {
    var _ref = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee() {
      var _res$data, res, data, _t;
      return _regenerator().w(function (_context) {
        while (1) switch (_context.p = _context.n) {
          case 0:
            _context.p = 0;
            setLoading(true);
            _context.n = 1;
            return executionAPI.getExecutions();
          case 1:
            res = _context.v;
            data = (_res$data = res === null || res === void 0 ? void 0 : res.data) !== null && _res$data !== void 0 ? _res$data : [];
            setExecutions(data);
            _context.n = 3;
            break;
          case 2:
            _context.p = 2;
            _t = _context.v;
            message.error('加载执行记录失败: ' + _t.message);
          case 3:
            _context.p = 3;
            setLoading(false);
            return _context.f(3);
          case 4:
            return _context.a(2);
        }
      }, _callee, null, [[0, 2, 3, 4]]);
    }));
    return function loadExecutions() {
      return _ref.apply(this, arguments);
    };
  }();

  // 初始化WebSocket连接
  useEffect(function () {
    // 获取当前页面的基础URL
    var baseUrl = window.location.origin;
    var ws = createWebSocket(baseUrl);
    wsRef.current = ws;

    // 监听连接事件
    ws.on('connected', function () {
      setWsConnected(true);
      message.success('实时更新已连接');
      // 重新订阅所有正在运行的任务
      subscribedTasksRef.current.forEach(function (taskId) {
        ws.subscribe(taskId);
      });
    });
    ws.on('disconnected', function () {
      setWsConnected(false);
      message.warning('实时更新已断开');
    });
    ws.on('error', function (_ref2) {
      var error = _ref2.error,
        errorMsg = _ref2.message;
      console.error('WebSocket error:', error || errorMsg);
    });

    // 监听任务状态更新
    ws.on('taskStatusUpdate', function (_ref3) {
      var taskId = _ref3.taskId,
        status = _ref3.status,
        statusMessage = _ref3.message;
      // 更新执行记录中的状态
      setExecutions(function (prev) {
        return prev.map(function (exec) {
          if (exec.taskId === taskId || exec.id === taskId) {
            return _objectSpread2$1(_objectSpread2$1({}, exec), {}, {
              status: status,
              message: statusMessage
            });
          }
          return exec;
        });
      });
    });

    // 监听任务日志
    ws.on('taskLog', function (_ref4) {
      var taskId = _ref4.taskId,
        log = _ref4.log,
        timestamp = _ref4.timestamp;
      setRealTimeLogs(function (prev) {
        var taskLogs = prev[taskId] || [];
        return _objectSpread2$1(_objectSpread2$1({}, prev), {}, _defineProperty({}, taskId, [].concat(_toConsumableArray(taskLogs), [{
          log: log,
          timestamp: timestamp
        }])));
      });
    });

    // 连接WebSocket
    ws.connect();

    // 清理函数
    return function () {
      ws.disconnect();
    };
  }, []);

  // 组件挂载时加载数据
  useEffect(function () {
    loadExecutions();
  }, []);

  // 订阅运行中的任务
  useEffect(function () {
    if (wsRef.current && wsConnected) {
      executions.forEach(function (exec) {
        if ((exec.status === 'running' || exec.status === 'pending') && !subscribedTasksRef.current.has(exec.taskId || exec.id)) {
          var taskId = exec.taskId || exec.id;
          wsRef.current.subscribe(taskId);
          subscribedTasksRef.current.add(taskId);
        }
      });
    }
  }, [executions, wsConnected]);

  // 事件处理函数
  var handleRefresh = function handleRefresh() {
    loadExecutions();
  };
  var handleViewDetail = /*#__PURE__*/function () {
    var _ref5 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee2(record) {
      var res, _t2;
      return _regenerator().w(function (_context2) {
        while (1) switch (_context2.p = _context2.n) {
          case 0:
            _context2.p = 0;
            _context2.n = 1;
            return executionAPI.getExecutionDetail(record.id);
          case 1:
            res = _context2.v;
            setCurrentExecution((res === null || res === void 0 ? void 0 : res.data) || record);
            setDetailModalVisible(true);
            _context2.n = 3;
            break;
          case 2:
            _context2.p = 2;
            _t2 = _context2.v;
            message.error('获取执行详情失败: ' + _t2.message);
            // 如果API失败，至少显示基本信息
            setCurrentExecution(record);
            setDetailModalVisible(true);
          case 3:
            return _context2.a(2);
        }
      }, _callee2, null, [[0, 2]]);
    }));
    return function handleViewDetail(_x) {
      return _ref5.apply(this, arguments);
    };
  }();
  var handleReExecute = function handleReExecute(record) {
    Modal.confirm({
      title: '确认重新执行',
      content: "\u786E\u5B9A\u8981\u91CD\u65B0\u6267\u884C\u4EFB\u52A1 \"".concat(record.taskName, "\" \u5417\uFF1F"),
      onOk: function onOk() {
        return _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee3() {
          var _t3;
          return _regenerator().w(function (_context3) {
            while (1) switch (_context3.p = _context3.n) {
              case 0:
                _context3.p = 0;
                _context3.n = 1;
                return taskAPI.executeTask(record.taskId);
              case 1:
                message.success('任务重新执行中...');
                loadExecutions(); // 重新加载记录
                _context3.n = 3;
                break;
              case 2:
                _context3.p = 2;
                _t3 = _context3.v;
                message.error('重新执行失败: ' + _t3.message);
              case 3:
                return _context3.a(2);
            }
          }, _callee3, null, [[0, 2]]);
        }))();
      }
    });
  };
  var columns = [{
    title: '任务名称',
    dataIndex: 'taskName',
    key: 'taskName'
  }, {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    render: function render(status) {
      var statusMap = {
        'success': {
          color: 'green',
          text: '成功'
        },
        'failed': {
          color: 'red',
          text: '失败'
        },
        'running': {
          color: 'blue',
          text: '运行中'
        },
        'pending': {
          color: 'orange',
          text: '待执行'
        }
      };
      var statusInfo = statusMap[status] || {
        color: 'default',
        text: status
      };
      return /*#__PURE__*/React__default.createElement(Tag, {
        color: statusInfo.color
      }, statusInfo.text);
    }
  }, {
    title: '开始时间',
    dataIndex: 'startTime',
    key: 'startTime'
  }, {
    title: '结束时间',
    dataIndex: 'endTime',
    key: 'endTime'
  }, {
    title: '执行时长',
    dataIndex: 'duration',
    key: 'duration'
  }, {
    title: '错误信息',
    dataIndex: 'errorMessage',
    key: 'errorMessage',
    ellipsis: true
  }, {
    title: '操作',
    key: 'action',
    render: function render(_, record) {
      return /*#__PURE__*/React__default.createElement(Space, null, /*#__PURE__*/React__default.createElement(Button, {
        type: "link",
        size: "small",
        onClick: function onClick() {
          return handleViewDetail(record);
        }
      }, "\u67E5\u770B\u8BE6\u60C5"), /*#__PURE__*/React__default.createElement(Button, {
        type: "link",
        size: "small",
        onClick: function onClick() {
          return handleReExecute(record);
        }
      }, "\u91CD\u65B0\u6267\u884C"));
    }
  }];
  return /*#__PURE__*/React__default.createElement("div", {
    className: "airdrop-execution"
  }, /*#__PURE__*/React__default.createElement("div", {
    style: {
      marginBottom: 16,
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center'
    }
  }, /*#__PURE__*/React__default.createElement(Space, null, /*#__PURE__*/React__default.createElement(Button, {
    type: "primary",
    onClick: handleRefresh
  }, "\u5237\u65B0"), /*#__PURE__*/React__default.createElement(Badge, {
    status: wsConnected ? 'success' : 'error',
    text: /*#__PURE__*/React__default.createElement("span", null, wsConnected ? /*#__PURE__*/React__default.createElement(React__default.Fragment, null, /*#__PURE__*/React__default.createElement(WifiOutlined$1, {
      style: {
        marginRight: 4
      }
    }), "\u5B9E\u65F6\u66F4\u65B0\u5DF2\u8FDE\u63A5") : /*#__PURE__*/React__default.createElement(React__default.Fragment, null, /*#__PURE__*/React__default.createElement(DisconnectOutlined$1, {
      style: {
        marginRight: 4
      }
    }), "\u5B9E\u65F6\u66F4\u65B0\u5DF2\u65AD\u5F00"))
  }))), /*#__PURE__*/React__default.createElement(Table, {
    columns: columns,
    dataSource: executions,
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
    title: "\u6267\u884C\u8BE6\u60C5",
    visible: detailModalVisible,
    onCancel: function onCancel() {
      setDetailModalVisible(false);
      setCurrentExecution(null);
    },
    footer: [/*#__PURE__*/React__default.createElement(Button, {
      key: "close",
      onClick: function onClick() {
        setDetailModalVisible(false);
        setCurrentExecution(null);
      }
    }, "\u5173\u95ED")],
    width: 800
  }, currentExecution && /*#__PURE__*/React__default.createElement(Descriptions, {
    column: 1,
    bordered: true
  }, /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u4EFB\u52A1\u540D\u79F0"
  }, currentExecution.taskName), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u6267\u884CID"
  }, currentExecution.id), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u72B6\u6001"
  }, /*#__PURE__*/React__default.createElement(Tag, {
    color: currentExecution.status === 'success' ? 'green' : 'red'
  }, currentExecution.status === 'success' ? '成功' : '失败')), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u5F00\u59CB\u65F6\u95F4"
  }, currentExecution.startTime), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u7ED3\u675F\u65F6\u95F4"
  }, currentExecution.endTime || '未完成'), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u6267\u884C\u65F6\u957F"
  }, currentExecution.duration || '计算中...'), currentExecution.errorMessage && /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u9519\u8BEF\u4FE1\u606F"
  }, /*#__PURE__*/React__default.createElement("pre", {
    style: {
      background: '#fff2f0',
      padding: 10,
      borderRadius: 4,
      maxHeight: 200,
      overflow: 'auto'
    }
  }, currentExecution.errorMessage)), currentExecution.log && /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u6267\u884C\u65E5\u5FD7"
  }, /*#__PURE__*/React__default.createElement("pre", {
    style: {
      background: '#f5f5f5',
      padding: 10,
      borderRadius: 4,
      maxHeight: 300,
      overflow: 'auto'
    }
  }, currentExecution.log)), realTimeLogs[currentExecution.taskId || currentExecution.id] && /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u5B9E\u65F6\u65E5\u5FD7"
  }, /*#__PURE__*/React__default.createElement(Card, {
    size: "small",
    style: {
      maxHeight: 300,
      overflow: 'auto'
    }
  }, realTimeLogs[currentExecution.taskId || currentExecution.id].map(function (logEntry, index) {
    return /*#__PURE__*/React__default.createElement("div", {
      key: index,
      style: {
        marginBottom: 4,
        fontSize: 12
      }
    }, /*#__PURE__*/React__default.createElement("span", {
      style: {
        color: '#999'
      }
    }, new Date(logEntry.timestamp).toLocaleTimeString()), /*#__PURE__*/React__default.createElement("span", {
      style: {
        marginLeft: 8
      }
    }, logEntry.log));
  }))), currentExecution.result && /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u6267\u884C\u7ED3\u679C\u53EF\u89C6\u5316",
    span: 2
  }, /*#__PURE__*/React__default.createElement(ResultVisualization, {
    result: currentExecution.result,
    resultType: currentExecution.resultType || 'text'
  })))));
};

export { AirdropExecution, AirdropExecution as default };
