import { _ as _slicedToArray, a as _asyncToGenerator, l as _typeof, b as _regenerator, C as debugAPI, t as taskAPI } from './api-72a577fe.js';
const React = window.React;
const React__default = window.React;
const { useState, useRef, useEffect } = window.React;
const { Select, Input, Form, Card, Alert, Button, Tag, Space, Descriptions, Row, Col, Modal, InputNumber, message } = window.antd;
import { A as AntdIcon, _ as _objectSpread2 } from './AntdIcon-16063a91.js';
import { C as CodeOutlined } from './CodeOutlined-637fcd45.js';
import { S as StopOutlined } from './StopOutlined-e433d766.js';
import { P as PlayCircleOutlined } from './PlayCircleOutlined-25bce8f0.js';
import { P as PauseCircleOutlined } from './PauseCircleOutlined-e36f23e4.js';

// This icon file is generated automatically.
var ArrowDownOutlined$2 = { "icon": { "tag": "svg", "attrs": { "viewBox": "64 64 896 896", "focusable": "false" }, "children": [{ "tag": "path", "attrs": { "d": "M862 465.3h-81c-4.6 0-9 2-12.1 5.5L550 723.1V160c0-4.4-3.6-8-8-8h-60c-4.4 0-8 3.6-8 8v563.1L255.1 470.8c-3-3.5-7.4-5.5-12.1-5.5h-81c-6.8 0-10.5 8.1-6 13.2L487.9 861a31.96 31.96 0 0048.3 0L868 478.5c4.5-5.2.8-13.2-6-13.2z" } }] }, "name": "arrow-down", "theme": "outlined" };
var ArrowDownOutlinedSvg = ArrowDownOutlined$2;

var ArrowDownOutlined = function ArrowDownOutlined(props, ref) {
  return /*#__PURE__*/React.createElement(AntdIcon, _objectSpread2(_objectSpread2({}, props), {}, {
    ref: ref,
    icon: ArrowDownOutlinedSvg
  }));
};
var RefIcon$4 = /*#__PURE__*/React.forwardRef(ArrowDownOutlined);
var ArrowDownOutlined$1 = RefIcon$4;

// This icon file is generated automatically.
var ArrowRightOutlined$2 = { "icon": { "tag": "svg", "attrs": { "viewBox": "64 64 896 896", "focusable": "false" }, "children": [{ "tag": "path", "attrs": { "d": "M869 487.8L491.2 159.9c-2.9-2.5-6.6-3.9-10.5-3.9h-88.5c-7.4 0-10.8 9.2-5.2 14l350.2 304H152c-4.4 0-8 3.6-8 8v60c0 4.4 3.6 8 8 8h585.1L386.9 854c-5.6 4.9-2.2 14 5.2 14h91.5c1.9 0 3.8-.7 5.2-2L869 536.2a32.07 32.07 0 000-48.4z" } }] }, "name": "arrow-right", "theme": "outlined" };
var ArrowRightOutlinedSvg = ArrowRightOutlined$2;

var ArrowRightOutlined = function ArrowRightOutlined(props, ref) {
  return /*#__PURE__*/React.createElement(AntdIcon, _objectSpread2(_objectSpread2({}, props), {}, {
    ref: ref,
    icon: ArrowRightOutlinedSvg
  }));
};
var RefIcon$3 = /*#__PURE__*/React.forwardRef(ArrowRightOutlined);
var ArrowRightOutlined$1 = RefIcon$3;

// This icon file is generated automatically.
var ArrowUpOutlined$2 = { "icon": { "tag": "svg", "attrs": { "viewBox": "64 64 896 896", "focusable": "false" }, "children": [{ "tag": "path", "attrs": { "d": "M868 545.5L536.1 163a31.96 31.96 0 00-48.3 0L156 545.5a7.97 7.97 0 006 13.2h81c4.6 0 9-2 12.1-5.5L474 300.9V864c0 4.4 3.6 8 8 8h60c4.4 0 8-3.6 8-8V300.9l218.9 252.3c3 3.5 7.4 5.5 12.1 5.5h81c6.8 0 10.5-8 6-13.2z" } }] }, "name": "arrow-up", "theme": "outlined" };
var ArrowUpOutlinedSvg = ArrowUpOutlined$2;

var ArrowUpOutlined = function ArrowUpOutlined(props, ref) {
  return /*#__PURE__*/React.createElement(AntdIcon, _objectSpread2(_objectSpread2({}, props), {}, {
    ref: ref,
    icon: ArrowUpOutlinedSvg
  }));
};
var RefIcon$2 = /*#__PURE__*/React.forwardRef(ArrowUpOutlined);
var ArrowUpOutlined$1 = RefIcon$2;

// This icon file is generated automatically.
var BugOutlined$2 = { "icon": { "tag": "svg", "attrs": { "viewBox": "64 64 896 896", "focusable": "false" }, "children": [{ "tag": "path", "attrs": { "d": "M304 280h56c4.4 0 8-3.6 8-8 0-28.3 5.9-53.2 17.1-73.5 10.6-19.4 26-34.8 45.4-45.4C450.9 142 475.7 136 504 136h16c28.3 0 53.2 5.9 73.5 17.1 19.4 10.6 34.8 26 45.4 45.4C650 218.9 656 243.7 656 272c0 4.4 3.6 8 8 8h56c4.4 0 8-3.6 8-8 0-40-8.8-76.7-25.9-108.1a184.31 184.31 0 00-74-74C596.7 72.8 560 64 520 64h-16c-40 0-76.7 8.8-108.1 25.9a184.31 184.31 0 00-74 74C304.8 195.3 296 232 296 272c0 4.4 3.6 8 8 8z" } }, { "tag": "path", "attrs": { "d": "M940 512H792V412c76.8 0 139-62.2 139-139 0-4.4-3.6-8-8-8h-60c-4.4 0-8 3.6-8 8a63 63 0 01-63 63H232a63 63 0 01-63-63c0-4.4-3.6-8-8-8h-60c-4.4 0-8 3.6-8 8 0 76.8 62.2 139 139 139v100H84c-4.4 0-8 3.6-8 8v56c0 4.4 3.6 8 8 8h148v96c0 6.5.2 13 .7 19.3C164.1 728.6 116 796.7 116 876c0 4.4 3.6 8 8 8h56c4.4 0 8-3.6 8-8 0-44.2 23.9-82.9 59.6-103.7a273 273 0 0022.7 49c24.3 41.5 59 76.2 100.5 100.5S460.5 960 512 960s99.8-13.9 141.3-38.2a281.38 281.38 0 00123.2-149.5A120 120 0 01836 876c0 4.4 3.6 8 8 8h56c4.4 0 8-3.6 8-8 0-79.3-48.1-147.4-116.7-176.7.4-6.4.7-12.8.7-19.3v-96h148c4.4 0 8-3.6 8-8v-56c0-4.4-3.6-8-8-8zM716 680c0 36.8-9.7 72-27.8 102.9-17.7 30.3-43 55.6-73.3 73.3C584 874.3 548.8 884 512 884s-72-9.7-102.9-27.8c-30.3-17.7-55.6-43-73.3-73.3A202.75 202.75 0 01308 680V412h408v268z" } }] }, "name": "bug", "theme": "outlined" };
var BugOutlinedSvg = BugOutlined$2;

var BugOutlined = function BugOutlined(props, ref) {
  return /*#__PURE__*/React.createElement(AntdIcon, _objectSpread2(_objectSpread2({}, props), {}, {
    ref: ref,
    icon: BugOutlinedSvg
  }));
};
var RefIcon$1 = /*#__PURE__*/React.forwardRef(BugOutlined);
var BugOutlined$1 = RefIcon$1;

// This icon file is generated automatically.
var StepForwardOutlined$2 = { "icon": { "tag": "svg", "attrs": { "viewBox": "0 0 1024 1024", "focusable": "false" }, "children": [{ "tag": "path", "attrs": { "d": "M676.4 528.95L293.2 829.97c-14.25 11.2-35.2 1.1-35.2-16.95V210.97c0-18.05 20.95-28.14 35.2-16.94l383.2 301.02a21.53 21.53 0 010 33.9M694 864h64a8 8 0 008-8V168a8 8 0 00-8-8h-64a8 8 0 00-8 8v688a8 8 0 008 8" } }] }, "name": "step-forward", "theme": "outlined" };
var StepForwardOutlinedSvg = StepForwardOutlined$2;

var StepForwardOutlined = function StepForwardOutlined(props, ref) {
  return /*#__PURE__*/React.createElement(AntdIcon, _objectSpread2(_objectSpread2({}, props), {}, {
    ref: ref,
    icon: StepForwardOutlinedSvg
  }));
};
var RefIcon = /*#__PURE__*/React.forwardRef(StepForwardOutlined);
var StepForwardOutlined$1 = RefIcon;

var Option = Select.Option;
var TextArea = Input.TextArea;
var AirdropDebugger = function AirdropDebugger() {
  var _debugSession$scriptL;
  var _useState = useState([]),
    _useState2 = _slicedToArray(_useState, 2),
    tasks = _useState2[0],
    setTasks = _useState2[1];
  var _useState3 = useState(null),
    _useState4 = _slicedToArray(_useState3, 2),
    debugSession = _useState4[0],
    setDebugSession = _useState4[1];
  var _useState5 = useState(null),
    _useState6 = _slicedToArray(_useState5, 2),
    sessionId = _useState6[0],
    setSessionId = _useState6[1];
  var _useState7 = useState([]),
    _useState8 = _slicedToArray(_useState7, 2),
    breakpoints = _useState8[0],
    setBreakpoints = _useState8[1];
  var _useState9 = useState({}),
    _useState0 = _slicedToArray(_useState9, 2),
    variables = _useState0[0],
    setVariables = _useState0[1];
  var _useState1 = useState([]),
    _useState10 = _slicedToArray(_useState1, 2),
    callStack = _useState10[0],
    setCallStack = _useState10[1];
  var _useState11 = useState([]),
    _useState12 = _slicedToArray(_useState11, 2),
    executionLog = _useState12[0],
    setExecutionLog = _useState12[1];
  var _useState13 = useState(false),
    _useState14 = _slicedToArray(_useState13, 2),
    createModalVisible = _useState14[0],
    setCreateModalVisible = _useState14[1];
  var _Form$useForm = Form.useForm(),
    _Form$useForm2 = _slicedToArray(_Form$useForm, 1),
    form = _Form$useForm2[0];
  var _Form$useForm3 = Form.useForm(),
    _Form$useForm4 = _slicedToArray(_Form$useForm3, 1),
    breakpointForm = _Form$useForm4[0];
  var _Form$useForm5 = Form.useForm(),
    _Form$useForm6 = _slicedToArray(_Form$useForm5, 1),
    variableForm = _Form$useForm6[0];
  var _useState15 = useState(false),
    _useState16 = _slicedToArray(_useState15, 2),
    breakpointModalVisible = _useState16[0],
    setBreakpointModalVisible = _useState16[1];
  var _useState17 = useState(false),
    _useState18 = _slicedToArray(_useState17, 2),
    variableModalVisible = _useState18[0],
    setVariableModalVisible = _useState18[1];
  var intervalRef = useRef(null);

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
  useEffect(function () {
    loadTasks();
    return function () {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
      }
    };
  }, []);

  // 轮询调试会话状态
  useEffect(function () {
    if (sessionId) {
      intervalRef.current = setInterval(/*#__PURE__*/_asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee2() {
        var res, session, bpRes, _t2;
        return _regenerator().w(function (_context2) {
          while (1) switch (_context2.p = _context2.n) {
            case 0:
              _context2.p = 0;
              _context2.n = 1;
              return debugAPI.getDebugSession(sessionId);
            case 1:
              res = _context2.v;
              session = res === null || res === void 0 ? void 0 : res.data;
              if (!session) {
                _context2.n = 3;
                break;
              }
              setDebugSession(session);
              setVariables(session.variables || {});
              setCallStack(session.callStack || []);
              setExecutionLog(session.executionLog || []);

              // 获取断点列表
              _context2.n = 2;
              return debugAPI.getBreakpoints(sessionId);
            case 2:
              bpRes = _context2.v;
              setBreakpoints((bpRes === null || bpRes === void 0 ? void 0 : bpRes.data) || []);
            case 3:
              _context2.n = 5;
              break;
            case 4:
              _context2.p = 4;
              _t2 = _context2.v;
              console.error('获取调试会话状态失败', _t2);
            case 5:
              return _context2.a(2);
          }
        }, _callee2, null, [[0, 4]]);
      })), 1000); // 每秒更新一次
    }
    return function () {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
      }
    };
  }, [sessionId]);

  // 创建调试会话
  var handleCreateDebugSession = /*#__PURE__*/function () {
    var _ref3 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee3(values) {
      var _res$data, task, res, newSessionId, sessionRes, _t3;
      return _regenerator().w(function (_context3) {
        while (1) switch (_context3.p = _context3.n) {
          case 0:
            _context3.p = 0;
            task = tasks.find(function (t) {
              return t.id === values.taskId;
            });
            if (task) {
              _context3.n = 1;
              break;
            }
            message.error('任务不存在');
            return _context3.a(2);
          case 1:
            _context3.n = 2;
            return debugAPI.createDebugSession(values.taskId, task.scriptContent || values.scriptContent, task.scriptType || values.scriptType);
          case 2:
            res = _context3.v;
            newSessionId = (res === null || res === void 0 || (_res$data = res.data) === null || _res$data === void 0 ? void 0 : _res$data.sessionId) || (res === null || res === void 0 ? void 0 : res.data);
            setSessionId(newSessionId);
            setCreateModalVisible(false);
            form.resetFields();
            message.success('调试会话创建成功');

            // 立即获取会话信息
            _context3.n = 3;
            return debugAPI.getDebugSession(newSessionId);
          case 3:
            sessionRes = _context3.v;
            setDebugSession(sessionRes === null || sessionRes === void 0 ? void 0 : sessionRes.data);
            _context3.n = 5;
            break;
          case 4:
            _context3.p = 4;
            _t3 = _context3.v;
            message.error('创建调试会话失败: ' + _t3.message);
          case 5:
            return _context3.a(2);
        }
      }, _callee3, null, [[0, 4]]);
    }));
    return function handleCreateDebugSession(_x) {
      return _ref3.apply(this, arguments);
    };
  }();

  // 开始调试
  var handleStartDebug = /*#__PURE__*/function () {
    var _ref4 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee4() {
      var _t4;
      return _regenerator().w(function (_context4) {
        while (1) switch (_context4.p = _context4.n) {
          case 0:
            _context4.p = 0;
            _context4.n = 1;
            return debugAPI.continueExecution(sessionId);
          case 1:
            message.success('调试已开始');
            _context4.n = 3;
            break;
          case 2:
            _context4.p = 2;
            _t4 = _context4.v;
            message.error('开始调试失败: ' + _t4.message);
          case 3:
            return _context4.a(2);
        }
      }, _callee4, null, [[0, 2]]);
    }));
    return function handleStartDebug() {
      return _ref4.apply(this, arguments);
    };
  }();

  // 继续执行
  var handleContinue = /*#__PURE__*/function () {
    var _ref5 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee5() {
      var _t5;
      return _regenerator().w(function (_context5) {
        while (1) switch (_context5.p = _context5.n) {
          case 0:
            _context5.p = 0;
            _context5.n = 1;
            return debugAPI.continueExecution(sessionId);
          case 1:
            message.success('继续执行');
            _context5.n = 3;
            break;
          case 2:
            _context5.p = 2;
            _t5 = _context5.v;
            message.error('继续执行失败: ' + _t5.message);
          case 3:
            return _context5.a(2);
        }
      }, _callee5, null, [[0, 2]]);
    }));
    return function handleContinue() {
      return _ref5.apply(this, arguments);
    };
  }();

  // 单步执行
  var handleStepOver = /*#__PURE__*/function () {
    var _ref6 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee6() {
      var _t6;
      return _regenerator().w(function (_context6) {
        while (1) switch (_context6.p = _context6.n) {
          case 0:
            _context6.p = 0;
            _context6.n = 1;
            return debugAPI.stepOver(sessionId);
          case 1:
            message.success('单步执行');
            _context6.n = 3;
            break;
          case 2:
            _context6.p = 2;
            _t6 = _context6.v;
            message.error('单步执行失败: ' + _t6.message);
          case 3:
            return _context6.a(2);
        }
      }, _callee6, null, [[0, 2]]);
    }));
    return function handleStepOver() {
      return _ref6.apply(this, arguments);
    };
  }();

  // 单步进入
  var handleStepInto = /*#__PURE__*/function () {
    var _ref7 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee7() {
      var _t7;
      return _regenerator().w(function (_context7) {
        while (1) switch (_context7.p = _context7.n) {
          case 0:
            _context7.p = 0;
            _context7.n = 1;
            return debugAPI.stepInto(sessionId);
          case 1:
            message.success('单步进入');
            _context7.n = 3;
            break;
          case 2:
            _context7.p = 2;
            _t7 = _context7.v;
            message.error('单步进入失败: ' + _t7.message);
          case 3:
            return _context7.a(2);
        }
      }, _callee7, null, [[0, 2]]);
    }));
    return function handleStepInto() {
      return _ref7.apply(this, arguments);
    };
  }();

  // 单步跳出
  var handleStepOut = /*#__PURE__*/function () {
    var _ref8 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee8() {
      var _t8;
      return _regenerator().w(function (_context8) {
        while (1) switch (_context8.p = _context8.n) {
          case 0:
            _context8.p = 0;
            _context8.n = 1;
            return debugAPI.stepOut(sessionId);
          case 1:
            message.success('单步跳出');
            _context8.n = 3;
            break;
          case 2:
            _context8.p = 2;
            _t8 = _context8.v;
            message.error('单步跳出失败: ' + _t8.message);
          case 3:
            return _context8.a(2);
        }
      }, _callee8, null, [[0, 2]]);
    }));
    return function handleStepOut() {
      return _ref8.apply(this, arguments);
    };
  }();

  // 暂停执行
  var handlePause = /*#__PURE__*/function () {
    var _ref9 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee9() {
      var _t9;
      return _regenerator().w(function (_context9) {
        while (1) switch (_context9.p = _context9.n) {
          case 0:
            _context9.p = 0;
            _context9.n = 1;
            return debugAPI.pauseExecution(sessionId);
          case 1:
            message.success('已暂停');
            _context9.n = 3;
            break;
          case 2:
            _context9.p = 2;
            _t9 = _context9.v;
            message.error('暂停失败: ' + _t9.message);
          case 3:
            return _context9.a(2);
        }
      }, _callee9, null, [[0, 2]]);
    }));
    return function handlePause() {
      return _ref9.apply(this, arguments);
    };
  }();

  // 停止调试
  var handleStop = /*#__PURE__*/function () {
    var _ref0 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee0() {
      var _t0;
      return _regenerator().w(function (_context0) {
        while (1) switch (_context0.p = _context0.n) {
          case 0:
            _context0.p = 0;
            _context0.n = 1;
            return debugAPI.stopDebug(sessionId);
          case 1:
            setSessionId(null);
            setDebugSession(null);
            setBreakpoints([]);
            setVariables({});
            setCallStack([]);
            setExecutionLog([]);
            message.success('调试已停止');
            _context0.n = 3;
            break;
          case 2:
            _context0.p = 2;
            _t0 = _context0.v;
            message.error('停止调试失败: ' + _t0.message);
          case 3:
            return _context0.a(2);
        }
      }, _callee0, null, [[0, 2]]);
    }));
    return function handleStop() {
      return _ref0.apply(this, arguments);
    };
  }();

  // 设置断点
  var handleSetBreakpoint = /*#__PURE__*/function () {
    var _ref1 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee1(values) {
      var res, _t1;
      return _regenerator().w(function (_context1) {
        while (1) switch (_context1.p = _context1.n) {
          case 0:
            _context1.p = 0;
            _context1.n = 1;
            return debugAPI.setBreakpoint(sessionId, values.lineNumber, values.condition);
          case 1:
            message.success('断点设置成功');
            setBreakpointModalVisible(false);
            breakpointForm.resetFields();

            // 刷新断点列表
            _context1.n = 2;
            return debugAPI.getBreakpoints(sessionId);
          case 2:
            res = _context1.v;
            setBreakpoints((res === null || res === void 0 ? void 0 : res.data) || []);
            _context1.n = 4;
            break;
          case 3:
            _context1.p = 3;
            _t1 = _context1.v;
            message.error('设置断点失败: ' + _t1.message);
          case 4:
            return _context1.a(2);
        }
      }, _callee1, null, [[0, 3]]);
    }));
    return function handleSetBreakpoint(_x2) {
      return _ref1.apply(this, arguments);
    };
  }();

  // 移除断点
  var handleRemoveBreakpoint = /*#__PURE__*/function () {
    var _ref10 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee10(lineNumber) {
      var res, _t10;
      return _regenerator().w(function (_context10) {
        while (1) switch (_context10.p = _context10.n) {
          case 0:
            _context10.p = 0;
            _context10.n = 1;
            return debugAPI.removeBreakpoint(sessionId, lineNumber);
          case 1:
            message.success('断点移除成功');

            // 刷新断点列表
            _context10.n = 2;
            return debugAPI.getBreakpoints(sessionId);
          case 2:
            res = _context10.v;
            setBreakpoints((res === null || res === void 0 ? void 0 : res.data) || []);
            _context10.n = 4;
            break;
          case 3:
            _context10.p = 3;
            _t10 = _context10.v;
            message.error('移除断点失败: ' + _t10.message);
          case 4:
            return _context10.a(2);
        }
      }, _callee10, null, [[0, 3]]);
    }));
    return function handleRemoveBreakpoint(_x3) {
      return _ref10.apply(this, arguments);
    };
  }();

  // 设置变量
  var handleSetVariable = /*#__PURE__*/function () {
    var _ref11 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee11(values) {
      var varRes, _t11;
      return _regenerator().w(function (_context11) {
        while (1) switch (_context11.p = _context11.n) {
          case 0:
            _context11.p = 0;
            _context11.n = 1;
            return debugAPI.setVariable(sessionId, values.variableName, values.value);
          case 1:
            message.success('变量设置成功');
            setVariableModalVisible(false);
            variableForm.resetFields();

            // 刷新变量列表
            _context11.n = 2;
            return debugAPI.getVariables(sessionId);
          case 2:
            varRes = _context11.v;
            setVariables((varRes === null || varRes === void 0 ? void 0 : varRes.data) || {});
            _context11.n = 4;
            break;
          case 3:
            _context11.p = 3;
            _t11 = _context11.v;
            message.error('设置变量失败: ' + _t11.message);
          case 4:
            return _context11.a(2);
        }
      }, _callee11, null, [[0, 3]]);
    }));
    return function handleSetVariable(_x4) {
      return _ref11.apply(this, arguments);
    };
  }();
  var statusConfig = {
    READY: {
      color: 'default',
      text: '就绪'
    },
    RUNNING: {
      color: 'processing',
      text: '运行中'
    },
    PAUSED: {
      color: 'warning',
      text: '已暂停'
    },
    STOPPED: {
      color: 'default',
      text: '已停止'
    },
    ERROR: {
      color: 'error',
      text: '错误'
    },
    COMPLETED: {
      color: 'success',
      text: '已完成'
    }
  };
  var statusInfo = debugSession ? statusConfig[debugSession.status] : null;
  return /*#__PURE__*/React__default.createElement("div", {
    className: "airdrop-debugger"
  }, !sessionId ? /*#__PURE__*/React__default.createElement(Card, null, /*#__PURE__*/React__default.createElement(Alert, {
    message: "\u521B\u5EFA\u8C03\u8BD5\u4F1A\u8BDD",
    description: "\u9009\u62E9\u4E00\u4E2A\u4EFB\u52A1\u5F00\u59CB\u8C03\u8BD5\uFF0C\u6216\u8005\u624B\u52A8\u8F93\u5165\u811A\u672C\u5185\u5BB9\u8FDB\u884C\u8C03\u8BD5\u3002",
    type: "info",
    showIcon: true,
    style: {
      marginBottom: 16
    }
  }), /*#__PURE__*/React__default.createElement(Button, {
    type: "primary",
    onClick: function onClick() {
      return setCreateModalVisible(true);
    }
  }, "\u521B\u5EFA\u8C03\u8BD5\u4F1A\u8BDD")) : /*#__PURE__*/React__default.createElement(React__default.Fragment, null, /*#__PURE__*/React__default.createElement(Card, {
    title: /*#__PURE__*/React__default.createElement("span", null, /*#__PURE__*/React__default.createElement(BugOutlined$1, null), " \u8C03\u8BD5\u4F1A\u8BDD: ", sessionId, statusInfo && /*#__PURE__*/React__default.createElement(Tag, {
      color: statusInfo.color,
      style: {
        marginLeft: 8
      }
    }, statusInfo.text)),
    extra: /*#__PURE__*/React__default.createElement(Space, null, /*#__PURE__*/React__default.createElement(Button, {
      icon: /*#__PURE__*/React__default.createElement(StopOutlined, null),
      danger: true,
      onClick: handleStop
    }, "\u505C\u6B62\u8C03\u8BD5")),
    style: {
      marginBottom: 16
    }
  }, /*#__PURE__*/React__default.createElement(Descriptions, {
    size: "small",
    column: 3
  }, /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u4EFB\u52A1ID"
  }, (debugSession === null || debugSession === void 0 ? void 0 : debugSession.taskId) || '-'), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u811A\u672C\u7C7B\u578B"
  }, (debugSession === null || debugSession === void 0 ? void 0 : debugSession.scriptType) || '-'), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u5F53\u524D\u884C"
  }, (debugSession === null || debugSession === void 0 ? void 0 : debugSession.currentLine) || '-'))), /*#__PURE__*/React__default.createElement(Row, {
    gutter: 16
  }, /*#__PURE__*/React__default.createElement(Col, {
    span: 16
  }, /*#__PURE__*/React__default.createElement(Card, {
    title: /*#__PURE__*/React__default.createElement("span", null, /*#__PURE__*/React__default.createElement(CodeOutlined, null), " \u811A\u672C\u5185\u5BB9"),
    extra: /*#__PURE__*/React__default.createElement(Space, null, (debugSession === null || debugSession === void 0 ? void 0 : debugSession.status) === 'PAUSED' || (debugSession === null || debugSession === void 0 ? void 0 : debugSession.status) === 'READY' ? /*#__PURE__*/React__default.createElement(React__default.Fragment, null, /*#__PURE__*/React__default.createElement(Button, {
      type: "primary",
      icon: /*#__PURE__*/React__default.createElement(PlayCircleOutlined, null),
      onClick: handleStartDebug,
      disabled: (debugSession === null || debugSession === void 0 ? void 0 : debugSession.status) === 'RUNNING'
    }, "\u5F00\u59CB"), /*#__PURE__*/React__default.createElement(Button, {
      icon: /*#__PURE__*/React__default.createElement(StepForwardOutlined$1, null),
      onClick: handleStepOver,
      disabled: (debugSession === null || debugSession === void 0 ? void 0 : debugSession.status) === 'RUNNING'
    }, "\u5355\u6B65"), /*#__PURE__*/React__default.createElement(Button, {
      icon: /*#__PURE__*/React__default.createElement(ArrowDownOutlined$1, null),
      onClick: handleStepInto,
      disabled: (debugSession === null || debugSession === void 0 ? void 0 : debugSession.status) === 'RUNNING'
    }, "\u8FDB\u5165"), /*#__PURE__*/React__default.createElement(Button, {
      icon: /*#__PURE__*/React__default.createElement(ArrowUpOutlined$1, null),
      onClick: handleStepOut,
      disabled: (debugSession === null || debugSession === void 0 ? void 0 : debugSession.status) === 'RUNNING'
    }, "\u8DF3\u51FA"), /*#__PURE__*/React__default.createElement(Button, {
      icon: /*#__PURE__*/React__default.createElement(ArrowRightOutlined$1, null),
      onClick: handleContinue,
      disabled: (debugSession === null || debugSession === void 0 ? void 0 : debugSession.status) === 'RUNNING'
    }, "\u7EE7\u7EED")) : /*#__PURE__*/React__default.createElement(Button, {
      icon: /*#__PURE__*/React__default.createElement(PauseCircleOutlined, null),
      onClick: handlePause,
      disabled: (debugSession === null || debugSession === void 0 ? void 0 : debugSession.status) === 'PAUSED'
    }, "\u6682\u505C")),
    style: {
      marginBottom: 16
    }
  }, /*#__PURE__*/React__default.createElement("div", {
    style: {
      background: '#f5f5f5',
      padding: 16,
      borderRadius: 4,
      fontFamily: 'monospace',
      fontSize: 12,
      maxHeight: 400,
      overflow: 'auto'
    }
  }, (debugSession === null || debugSession === void 0 || (_debugSession$scriptL = debugSession.scriptLines) === null || _debugSession$scriptL === void 0 ? void 0 : _debugSession$scriptL.map(function (line, index) {
    var lineNumber = index + 1;
    var isBreakpoint = breakpoints.some(function (bp) {
      return bp.lineNumber === lineNumber;
    });
    var isCurrentLine = (debugSession === null || debugSession === void 0 ? void 0 : debugSession.currentLine) === lineNumber;
    return /*#__PURE__*/React__default.createElement("div", {
      key: index,
      style: {
        padding: '2px 8px',
        background: isCurrentLine ? '#e6f7ff' : 'transparent',
        borderLeft: isCurrentLine ? '3px solid #1890ff' : 'none',
        display: 'flex',
        alignItems: 'center'
      }
    }, /*#__PURE__*/React__default.createElement("span", {
      style: {
        width: 40,
        textAlign: 'right',
        marginRight: 8,
        color: '#999'
      }
    }, lineNumber), isBreakpoint && /*#__PURE__*/React__default.createElement("span", {
      style: {
        width: 12,
        height: 12,
        borderRadius: '50%',
        background: '#ff4d4f',
        marginRight: 8,
        cursor: 'pointer'
      },
      onClick: function onClick() {
        return handleRemoveBreakpoint(lineNumber);
      },
      title: "\u70B9\u51FB\u79FB\u9664\u65AD\u70B9"
    }), !isBreakpoint && /*#__PURE__*/React__default.createElement("span", {
      style: {
        width: 12,
        marginRight: 8,
        cursor: 'pointer'
      },
      onClick: function onClick() {
        breakpointForm.setFieldsValue({
          lineNumber: lineNumber
        });
        setBreakpointModalVisible(true);
      },
      title: "\u70B9\u51FB\u8BBE\u7F6E\u65AD\u70B9"
    }), /*#__PURE__*/React__default.createElement("span", null, line));
  })) || /*#__PURE__*/React__default.createElement("div", {
    style: {
      color: '#999'
    }
  }, "\u6682\u65E0\u811A\u672C\u5185\u5BB9"))), /*#__PURE__*/React__default.createElement(Card, {
    title: "\u6267\u884C\u65E5\u5FD7",
    size: "small"
  }, /*#__PURE__*/React__default.createElement("div", {
    style: {
      background: '#f5f5f5',
      padding: 12,
      borderRadius: 4,
      fontFamily: 'monospace',
      fontSize: 12,
      maxHeight: 200,
      overflow: 'auto'
    }
  }, executionLog.length > 0 ? executionLog.map(function (log, index) {
    return /*#__PURE__*/React__default.createElement("div", {
      key: index,
      style: {
        marginBottom: 4
      }
    }, log);
  }) : /*#__PURE__*/React__default.createElement("div", {
    style: {
      color: '#999'
    }
  }, "\u6682\u65E0\u6267\u884C\u65E5\u5FD7")))), /*#__PURE__*/React__default.createElement(Col, {
    span: 8
  }, /*#__PURE__*/React__default.createElement(Card, {
    title: "\u65AD\u70B9",
    size: "small",
    extra: /*#__PURE__*/React__default.createElement(Button, {
      type: "link",
      size: "small",
      onClick: function onClick() {
        breakpointForm.resetFields();
        setBreakpointModalVisible(true);
      }
    }, "\u6DFB\u52A0"),
    style: {
      marginBottom: 16
    }
  }, breakpoints.length > 0 ? breakpoints.map(function (bp, index) {
    return /*#__PURE__*/React__default.createElement("div", {
      key: index,
      style: {
        marginBottom: 8
      }
    }, /*#__PURE__*/React__default.createElement(Space, null, /*#__PURE__*/React__default.createElement(Tag, {
      color: "red"
    }, "\u884C ", bp.lineNumber), bp.condition && /*#__PURE__*/React__default.createElement("span", {
      style: {
        fontSize: 12,
        color: '#666'
      }
    }, "\u6761\u4EF6: ", bp.condition), /*#__PURE__*/React__default.createElement(Button, {
      type: "link",
      size: "small",
      danger: true,
      onClick: function onClick() {
        return handleRemoveBreakpoint(bp.lineNumber);
      }
    }, "\u79FB\u9664")));
  }) : /*#__PURE__*/React__default.createElement("div", {
    style: {
      color: '#999',
      fontSize: 12
    }
  }, "\u6682\u65E0\u65AD\u70B9")), /*#__PURE__*/React__default.createElement(Card, {
    title: "\u53D8\u91CF",
    size: "small",
    extra: /*#__PURE__*/React__default.createElement(Button, {
      type: "link",
      size: "small",
      onClick: function onClick() {
        variableForm.resetFields();
        setVariableModalVisible(true);
      }
    }, "\u8BBE\u7F6E"),
    style: {
      marginBottom: 16
    }
  }, Object.keys(variables).length > 0 ? Object.entries(variables).map(function (_ref12) {
    var _ref13 = _slicedToArray(_ref12, 2),
      key = _ref13[0],
      value = _ref13[1];
    return /*#__PURE__*/React__default.createElement("div", {
      key: key,
      style: {
        marginBottom: 8
      }
    }, /*#__PURE__*/React__default.createElement("strong", null, key, ":"), /*#__PURE__*/React__default.createElement("span", {
      style: {
        marginLeft: 8,
        fontSize: 12
      }
    }, _typeof(value) === 'object' ? JSON.stringify(value) : String(value)));
  }) : /*#__PURE__*/React__default.createElement("div", {
    style: {
      color: '#999',
      fontSize: 12
    }
  }, "\u6682\u65E0\u53D8\u91CF")), /*#__PURE__*/React__default.createElement(Card, {
    title: "\u8C03\u7528\u6808",
    size: "small"
  }, callStack.length > 0 ? callStack.map(function (frame, index) {
    return /*#__PURE__*/React__default.createElement("div", {
      key: index,
      style: {
        marginBottom: 8,
        fontSize: 12
      }
    }, /*#__PURE__*/React__default.createElement("div", null, /*#__PURE__*/React__default.createElement("strong", null, frame.functionName || 'anonymous')), /*#__PURE__*/React__default.createElement("div", {
      style: {
        color: '#666'
      }
    }, frame.fileName, ":", frame.lineNumber));
  }) : /*#__PURE__*/React__default.createElement("div", {
    style: {
      color: '#999',
      fontSize: 12
    }
  }, "\u8C03\u7528\u6808\u4E3A\u7A7A"))))), /*#__PURE__*/React__default.createElement(Modal, {
    title: "\u521B\u5EFA\u8C03\u8BD5\u4F1A\u8BDD",
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
    onFinish: handleCreateDebugSession
  }, /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "taskId",
    label: "\u9009\u62E9\u4EFB\u52A1",
    rules: [{
      required: true,
      message: '请选择任务或输入脚本内容'
    }]
  }, /*#__PURE__*/React__default.createElement(Select, {
    placeholder: "\u9009\u62E9\u4EFB\u52A1\uFF08\u5C06\u4F7F\u7528\u4EFB\u52A1\u7684\u811A\u672C\u5185\u5BB9\uFF09",
    allowClear: true,
    showSearch: true
  }, tasks.map(function (task) {
    return /*#__PURE__*/React__default.createElement(Option, {
      key: task.id,
      value: task.id
    }, task.taskName, " (", task.scriptType, ")");
  }))), /*#__PURE__*/React__default.createElement(Form.Item, {
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
    label: "\u811A\u672C\u5185\u5BB9\uFF08\u53EF\u9009\uFF0C\u5982\u679C\u9009\u62E9\u4E86\u4EFB\u52A1\u5219\u4F7F\u7528\u4EFB\u52A1\u7684\u811A\u672C\uFF09"
  }, /*#__PURE__*/React__default.createElement(TextArea, {
    rows: 10,
    placeholder: "\u8F93\u5165\u8981\u8C03\u8BD5\u7684\u811A\u672C\u5185\u5BB9"
  })))), /*#__PURE__*/React__default.createElement(Modal, {
    title: "\u8BBE\u7F6E\u65AD\u70B9",
    visible: breakpointModalVisible,
    onCancel: function onCancel() {
      setBreakpointModalVisible(false);
      breakpointForm.resetFields();
    },
    onOk: function onOk() {
      return breakpointForm.submit();
    },
    width: 500
  }, /*#__PURE__*/React__default.createElement(Form, {
    form: breakpointForm,
    layout: "vertical",
    onFinish: handleSetBreakpoint
  }, /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "lineNumber",
    label: "\u884C\u53F7",
    rules: [{
      required: true,
      message: '请输入行号'
    }]
  }, /*#__PURE__*/React__default.createElement(InputNumber, {
    min: 1,
    style: {
      width: '100%'
    }
  })), /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "condition",
    label: "\u6761\u4EF6\uFF08\u53EF\u9009\uFF09",
    tooltip: "\u5F53\u6761\u4EF6\u4E3Atrue\u65F6\uFF0C\u65AD\u70B9\u624D\u4F1A\u89E6\u53D1"
  }, /*#__PURE__*/React__default.createElement(Input, {
    placeholder: "\u4F8B\u5982: x > 10"
  })))), /*#__PURE__*/React__default.createElement(Modal, {
    title: "\u8BBE\u7F6E\u53D8\u91CF",
    visible: variableModalVisible,
    onCancel: function onCancel() {
      setVariableModalVisible(false);
      variableForm.resetFields();
    },
    onOk: function onOk() {
      return variableForm.submit();
    },
    width: 500
  }, /*#__PURE__*/React__default.createElement(Form, {
    form: variableForm,
    layout: "vertical",
    onFinish: handleSetVariable
  }, /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "variableName",
    label: "\u53D8\u91CF\u540D",
    rules: [{
      required: true,
      message: '请输入变量名'
    }]
  }, /*#__PURE__*/React__default.createElement(Input, {
    placeholder: "\u53D8\u91CF\u540D"
  })), /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "value",
    label: "\u53D8\u91CF\u503C",
    rules: [{
      required: true,
      message: '请输入变量值'
    }]
  }, /*#__PURE__*/React__default.createElement(Input, {
    placeholder: "\u53D8\u91CF\u503C\uFF08\u652F\u6301JSON\u683C\u5F0F\uFF09"
  })))));
};

export { AirdropDebugger, AirdropDebugger as default };
