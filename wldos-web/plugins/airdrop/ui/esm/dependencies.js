import { _ as _slicedToArray, a as _asyncToGenerator, b as _regenerator, t as taskAPI, z as _regeneratorValues, A as _createForOfIteratorHelper, B as dependencyAPI } from './api-72a577fe.js';
const React = window.React;
const React__default = window.React;
const { useState, useEffect } = window.React;
const { Select, Form, Row, Col, Card, Descriptions, Alert, Space, Button, Table, Modal, Drawer, Tag, message } = window.antd;
import { A as AntdIcon, _ as _objectSpread2 } from './AntdIcon-16063a91.js';

// This icon file is generated automatically.
var LinkOutlined$2 = { "icon": { "tag": "svg", "attrs": { "viewBox": "64 64 896 896", "focusable": "false" }, "children": [{ "tag": "path", "attrs": { "d": "M574 665.4a8.03 8.03 0 00-11.3 0L446.5 781.6c-53.8 53.8-144.6 59.5-204 0-59.5-59.5-53.8-150.2 0-204l116.2-116.2c3.1-3.1 3.1-8.2 0-11.3l-39.8-39.8a8.03 8.03 0 00-11.3 0L191.4 526.5c-84.6 84.6-84.6 221.5 0 306s221.5 84.6 306 0l116.2-116.2c3.1-3.1 3.1-8.2 0-11.3L574 665.4zm258.6-474c-84.6-84.6-221.5-84.6-306 0L410.3 307.6a8.03 8.03 0 000 11.3l39.7 39.7c3.1 3.1 8.2 3.1 11.3 0l116.2-116.2c53.8-53.8 144.6-59.5 204 0 59.5 59.5 53.8 150.2 0 204L665.3 562.6a8.03 8.03 0 000 11.3l39.8 39.8c3.1 3.1 8.2 3.1 11.3 0l116.2-116.2c84.5-84.6 84.5-221.5 0-306.1zM610.1 372.3a8.03 8.03 0 00-11.3 0L372.3 598.7a8.03 8.03 0 000 11.3l39.6 39.6c3.1 3.1 8.2 3.1 11.3 0l226.4-226.4c3.1-3.1 3.1-8.2 0-11.3l-39.5-39.6z" } }] }, "name": "link", "theme": "outlined" };
var LinkOutlinedSvg = LinkOutlined$2;

var LinkOutlined = function LinkOutlined(props, ref) {
  return /*#__PURE__*/React.createElement(AntdIcon, _objectSpread2(_objectSpread2({}, props), {}, {
    ref: ref,
    icon: LinkOutlinedSvg
  }));
};
var RefIcon$1 = /*#__PURE__*/React.forwardRef(LinkOutlined);
var LinkOutlined$1 = RefIcon$1;

// This icon file is generated automatically.
var WarningOutlined$2 = { "icon": { "tag": "svg", "attrs": { "viewBox": "64 64 896 896", "focusable": "false" }, "children": [{ "tag": "path", "attrs": { "d": "M464 720a48 48 0 1096 0 48 48 0 10-96 0zm16-304v184c0 4.4 3.6 8 8 8h48c4.4 0 8-3.6 8-8V416c0-4.4-3.6-8-8-8h-48c-4.4 0-8 3.6-8 8zm475.7 440l-416-720c-6.2-10.7-16.9-16-27.7-16s-21.6 5.3-27.7 16l-416 720C56 877.4 71.4 904 96 904h832c24.6 0 40-26.6 27.7-48zm-783.5-27.9L512 239.9l339.8 588.2H172.2z" } }] }, "name": "warning", "theme": "outlined" };
var WarningOutlinedSvg = WarningOutlined$2;

var WarningOutlined = function WarningOutlined(props, ref) {
  return /*#__PURE__*/React.createElement(AntdIcon, _objectSpread2(_objectSpread2({}, props), {}, {
    ref: ref,
    icon: WarningOutlinedSvg
  }));
};
var RefIcon = /*#__PURE__*/React.forwardRef(WarningOutlined);
var WarningOutlined$1 = RefIcon;

var Option = Select.Option;
var AirdropDependencies = function AirdropDependencies() {
  var _dependencyGraph$edge, _dependencyGraph$edge2;
  var _useState = useState([]),
    _useState2 = _slicedToArray(_useState, 2),
    dependencies = _useState2[0],
    setDependencies = _useState2[1];
  var _useState3 = useState([]),
    _useState4 = _slicedToArray(_useState3, 2),
    tasks = _useState4[0],
    setTasks = _useState4[1];
  var _useState5 = useState(false),
    _useState6 = _slicedToArray(_useState5, 2),
    loading = _useState6[0],
    setLoading = _useState6[1];
  var _useState7 = useState(false),
    _useState8 = _slicedToArray(_useState7, 2),
    graphVisible = _useState8[0],
    setGraphVisible = _useState8[1];
  var _useState9 = useState(null),
    _useState0 = _slicedToArray(_useState9, 2),
    dependencyGraph = _useState0[0],
    setDependencyGraph = _useState0[1];
  var _useState1 = useState(false),
    _useState10 = _slicedToArray(_useState1, 2),
    addModalVisible = _useState10[0],
    setAddModalVisible = _useState10[1];
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

  // 加载依赖关系
  var loadDependencies = /*#__PURE__*/function () {
    var _ref2 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee2() {
      var allDependencies, _iterator, _step, _loop, _t3, _t4;
      return _regenerator().w(function (_context3) {
        while (1) switch (_context3.p = _context3.n) {
          case 0:
            _context3.p = 0;
            setLoading(true);
            // 获取所有任务的依赖关系
            allDependencies = [];
            _iterator = _createForOfIteratorHelper(tasks);
            _context3.p = 1;
            _loop = /*#__PURE__*/_regenerator().m(function _loop() {
              var task, res, deps;
              return _regenerator().w(function (_context2) {
                while (1) switch (_context2.p = _context2.n) {
                  case 0:
                    task = _step.value;
                    _context2.p = 1;
                    _context2.n = 2;
                    return dependencyAPI.getDependencies(task.id);
                  case 2:
                    res = _context2.v;
                    deps = (res === null || res === void 0 ? void 0 : res.data) || [];
                    deps.forEach(function (depTaskId) {
                      var _tasks$find;
                      allDependencies.push({
                        id: "".concat(task.id, "-").concat(depTaskId),
                        taskId: task.id,
                        taskName: task.taskName,
                        dependentTaskId: depTaskId,
                        dependentTaskName: ((_tasks$find = tasks.find(function (t) {
                          return t.id === depTaskId;
                        })) === null || _tasks$find === void 0 ? void 0 : _tasks$find.taskName) || depTaskId
                      });
                    });
                    _context2.n = 4;
                    break;
                  case 3:
                    _context2.p = 3;
                    _context2.v;
                  case 4:
                    return _context2.a(2);
                }
              }, _loop, null, [[1, 3]]);
            });
            _iterator.s();
          case 2:
            if ((_step = _iterator.n()).done) {
              _context3.n = 4;
              break;
            }
            return _context3.d(_regeneratorValues(_loop()), 3);
          case 3:
            _context3.n = 2;
            break;
          case 4:
            _context3.n = 6;
            break;
          case 5:
            _context3.p = 5;
            _t3 = _context3.v;
            _iterator.e(_t3);
          case 6:
            _context3.p = 6;
            _iterator.f();
            return _context3.f(6);
          case 7:
            setDependencies(allDependencies);
            _context3.n = 9;
            break;
          case 8:
            _context3.p = 8;
            _t4 = _context3.v;
            message.error('加载依赖关系失败: ' + _t4.message);
          case 9:
            _context3.p = 9;
            setLoading(false);
            return _context3.f(9);
          case 10:
            return _context3.a(2);
        }
      }, _callee2, null, [[1, 5, 6, 7], [0, 8, 9, 10]]);
    }));
    return function loadDependencies() {
      return _ref2.apply(this, arguments);
    };
  }();
  useEffect(function () {
    loadTasks();
  }, []);
  useEffect(function () {
    if (tasks.length > 0) {
      loadDependencies();
    }
  }, [tasks]);

  // 添加依赖
  var handleAddDependency = /*#__PURE__*/function () {
    var _ref3 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee3(values) {
      var _cycleCheck$data, cycleCheck, _t5;
      return _regenerator().w(function (_context4) {
        while (1) switch (_context4.p = _context4.n) {
          case 0:
            _context4.p = 0;
            _context4.n = 1;
            return dependencyAPI.checkCycle(values.taskId, values.dependentTaskId);
          case 1:
            cycleCheck = _context4.v;
            if (!(cycleCheck !== null && cycleCheck !== void 0 && (_cycleCheck$data = cycleCheck.data) !== null && _cycleCheck$data !== void 0 && _cycleCheck$data.hasCycle)) {
              _context4.n = 2;
              break;
            }
            message.error('检测到循环依赖，无法添加');
            return _context4.a(2);
          case 2:
            _context4.n = 3;
            return dependencyAPI.addDependency(values.taskId, values.dependentTaskId);
          case 3:
            message.success('依赖关系添加成功');
            setAddModalVisible(false);
            form.resetFields();
            loadDependencies();
            loadDependencyGraph();
            _context4.n = 5;
            break;
          case 4:
            _context4.p = 4;
            _t5 = _context4.v;
            message.error('添加依赖关系失败: ' + _t5.message);
          case 5:
            return _context4.a(2);
        }
      }, _callee3, null, [[0, 4]]);
    }));
    return function handleAddDependency(_x) {
      return _ref3.apply(this, arguments);
    };
  }();

  // 移除依赖
  var handleRemoveDependency = /*#__PURE__*/function () {
    var _ref4 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee4(taskId, dependentTaskId) {
      var _t6;
      return _regenerator().w(function (_context5) {
        while (1) switch (_context5.p = _context5.n) {
          case 0:
            _context5.p = 0;
            _context5.n = 1;
            return dependencyAPI.removeDependency(taskId, dependentTaskId);
          case 1:
            message.success('依赖关系移除成功');
            loadDependencies();
            loadDependencyGraph();
            _context5.n = 3;
            break;
          case 2:
            _context5.p = 2;
            _t6 = _context5.v;
            message.error('移除依赖关系失败: ' + _t6.message);
          case 3:
            return _context5.a(2);
        }
      }, _callee4, null, [[0, 2]]);
    }));
    return function handleRemoveDependency(_x2, _x3) {
      return _ref4.apply(this, arguments);
    };
  }();

  // 加载依赖图
  var loadDependencyGraph = /*#__PURE__*/function () {
    var _ref5 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee5() {
      var res, _t7;
      return _regenerator().w(function (_context6) {
        while (1) switch (_context6.p = _context6.n) {
          case 0:
            _context6.p = 0;
            _context6.n = 1;
            return dependencyAPI.getDependencyGraph();
          case 1:
            res = _context6.v;
            setDependencyGraph(res === null || res === void 0 ? void 0 : res.data);
            _context6.n = 3;
            break;
          case 2:
            _context6.p = 2;
            _t7 = _context6.v;
            message.error('加载依赖图失败: ' + _t7.message);
          case 3:
            return _context6.a(2);
        }
      }, _callee5, null, [[0, 2]]);
    }));
    return function loadDependencyGraph() {
      return _ref5.apply(this, arguments);
    };
  }();

  // 检查循环依赖
  var handleCheckCycle = /*#__PURE__*/function () {
    var _ref6 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee6() {
      var _res$data, res, _t8;
      return _regenerator().w(function (_context7) {
        while (1) switch (_context7.p = _context7.n) {
          case 0:
            _context7.p = 0;
            _context7.n = 1;
            return dependencyAPI.checkCycle();
          case 1:
            res = _context7.v;
            if (res !== null && res !== void 0 && (_res$data = res.data) !== null && _res$data !== void 0 && _res$data.hasCycle) {
              message.warning('检测到循环依赖');
            } else {
              message.success('未检测到循环依赖');
            }
            _context7.n = 3;
            break;
          case 2:
            _context7.p = 2;
            _t8 = _context7.v;
            message.error('检查循环依赖失败: ' + _t8.message);
          case 3:
            return _context7.a(2);
        }
      }, _callee6, null, [[0, 2]]);
    }));
    return function handleCheckCycle() {
      return _ref6.apply(this, arguments);
    };
  }();
  var columns = [{
    title: '任务',
    dataIndex: 'taskName',
    key: 'taskName',
    render: function render(text, record) {
      return /*#__PURE__*/React__default.createElement("span", null, /*#__PURE__*/React__default.createElement(LinkOutlined$1, {
        style: {
          marginRight: 4,
          color: '#1890ff'
        }
      }), text || record.taskId);
    }
  }, {
    title: '依赖的任务',
    dataIndex: 'dependentTaskName',
    key: 'dependentTaskName',
    render: function render(text, record) {
      return /*#__PURE__*/React__default.createElement("span", null, /*#__PURE__*/React__default.createElement(LinkOutlined$1, {
        style: {
          marginRight: 4,
          color: '#52c41a'
        }
      }), text || record.dependentTaskId);
    }
  }, {
    title: '操作',
    key: 'action',
    render: function render(_, record) {
      return /*#__PURE__*/React__default.createElement(Button, {
        type: "link",
        size: "small",
        danger: true,
        onClick: function onClick() {
          return handleRemoveDependency(record.taskId, record.dependentTaskId);
        }
      }, "\u79FB\u9664\u4F9D\u8D56");
    }
  }];

  // 统计信息
  var stats = {
    total: dependencies.length,
    tasksWithDeps: new Set(dependencies.map(function (d) {
      return d.taskId;
    })).size,
    tasksAsDeps: new Set(dependencies.map(function (d) {
      return d.dependentTaskId;
    })).size
  };
  return /*#__PURE__*/React__default.createElement("div", {
    className: "airdrop-dependencies"
  }, /*#__PURE__*/React__default.createElement(Row, {
    gutter: 16,
    style: {
      marginBottom: 16
    }
  }, /*#__PURE__*/React__default.createElement(Col, {
    span: 8
  }, /*#__PURE__*/React__default.createElement(Card, null, /*#__PURE__*/React__default.createElement(Descriptions, {
    title: "\u4F9D\u8D56\u7EDF\u8BA1",
    size: "small",
    column: 1
  }, /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u603B\u4F9D\u8D56\u6570"
  }, stats.total), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u6709\u4F9D\u8D56\u7684\u4EFB\u52A1\u6570"
  }, stats.tasksWithDeps), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u88AB\u4F9D\u8D56\u7684\u4EFB\u52A1\u6570"
  }, stats.tasksAsDeps)))), /*#__PURE__*/React__default.createElement(Col, {
    span: 16
  }, /*#__PURE__*/React__default.createElement(Alert, {
    message: "\u4EFB\u52A1\u4F9D\u8D56\u8BF4\u660E",
    description: "\u4EFB\u52A1\u4F9D\u8D56\u5173\u7CFB\u51B3\u5B9A\u4E86\u4EFB\u52A1\u7684\u6267\u884C\u987A\u5E8F\u3002\u4EFB\u52A1A\u4F9D\u8D56\u4E8E\u4EFB\u52A1B\uFF0C\u610F\u5473\u7740\u4EFB\u52A1B\u5FC5\u987B\u5148\u6267\u884C\u5B8C\u6210\uFF0C\u4EFB\u52A1A\u624D\u80FD\u5F00\u59CB\u6267\u884C\u3002",
    type: "info",
    showIcon: true,
    style: {
      marginBottom: 16
    }
  }))), /*#__PURE__*/React__default.createElement("div", {
    style: {
      marginBottom: 16
    }
  }, /*#__PURE__*/React__default.createElement(Space, null, /*#__PURE__*/React__default.createElement(Button, {
    type: "primary",
    onClick: function onClick() {
      return setAddModalVisible(true);
    }
  }, "\u6DFB\u52A0\u4F9D\u8D56\u5173\u7CFB"), /*#__PURE__*/React__default.createElement(Button, {
    onClick: handleCheckCycle,
    icon: /*#__PURE__*/React__default.createElement(WarningOutlined$1, null)
  }, "\u68C0\u67E5\u5FAA\u73AF\u4F9D\u8D56"), /*#__PURE__*/React__default.createElement(Button, {
    onClick: function onClick() {
      setGraphVisible(true);
      loadDependencyGraph();
    }
  }, "\u67E5\u770B\u4F9D\u8D56\u56FE"), /*#__PURE__*/React__default.createElement(Button, {
    onClick: loadDependencies
  }, "\u5237\u65B0"))), /*#__PURE__*/React__default.createElement(Table, {
    columns: columns,
    dataSource: dependencies,
    rowKey: "id",
    loading: loading,
    pagination: {
      pageSize: 10,
      showSizeChanger: true,
      showQuickJumper: true,
      showTotal: function showTotal(total) {
        return "\u5171 ".concat(total, " \u6761\u4F9D\u8D56\u5173\u7CFB");
      }
    },
    locale: {
      emptyText: '暂无依赖关系，点击"添加依赖关系"按钮创建'
    }
  }), /*#__PURE__*/React__default.createElement(Modal, {
    title: "\u6DFB\u52A0\u4F9D\u8D56\u5173\u7CFB",
    visible: addModalVisible,
    onCancel: function onCancel() {
      setAddModalVisible(false);
      form.resetFields();
    },
    onOk: function onOk() {
      return form.submit();
    },
    width: 600
  }, /*#__PURE__*/React__default.createElement(Form, {
    form: form,
    layout: "vertical",
    onFinish: handleAddDependency
  }, /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "taskId",
    label: "\u4EFB\u52A1",
    rules: [{
      required: true,
      message: '请选择任务'
    }],
    tooltip: "\u9009\u62E9\u9700\u8981\u4F9D\u8D56\u5176\u4ED6\u4EFB\u52A1\u7684\u4EFB\u52A1"
  }, /*#__PURE__*/React__default.createElement(Select, {
    placeholder: "\u8BF7\u9009\u62E9\u4EFB\u52A1",
    showSearch: true
  }, tasks.map(function (task) {
    return /*#__PURE__*/React__default.createElement(Option, {
      key: task.id,
      value: task.id
    }, task.taskName, " (", task.id, ")");
  }))), /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "dependentTaskId",
    label: "\u4F9D\u8D56\u7684\u4EFB\u52A1",
    rules: [{
      required: true,
      message: '请选择依赖的任务'
    }],
    tooltip: "\u9009\u62E9\u88AB\u4F9D\u8D56\u7684\u4EFB\u52A1\uFF08\u5FC5\u987B\u5148\u6267\u884C\u5B8C\u6210\uFF09"
  }, /*#__PURE__*/React__default.createElement(Select, {
    placeholder: "\u8BF7\u9009\u62E9\u4F9D\u8D56\u7684\u4EFB\u52A1",
    showSearch: true
  }, tasks.map(function (task) {
    return /*#__PURE__*/React__default.createElement(Option, {
      key: task.id,
      value: task.id
    }, task.taskName, " (", task.id, ")");
  }))), /*#__PURE__*/React__default.createElement(Alert, {
    message: "\u63D0\u793A",
    description: "\u7CFB\u7EDF\u4F1A\u81EA\u52A8\u68C0\u67E5\u5FAA\u73AF\u4F9D\u8D56\uFF0C\u5982\u679C\u68C0\u6D4B\u5230\u5FAA\u73AF\u4F9D\u8D56\uFF0C\u5C06\u65E0\u6CD5\u6DFB\u52A0\u6B64\u4F9D\u8D56\u5173\u7CFB\u3002",
    type: "info",
    showIcon: true,
    style: {
      marginTop: 16
    }
  }))), /*#__PURE__*/React__default.createElement(Drawer, {
    title: "\u4EFB\u52A1\u4F9D\u8D56\u56FE",
    placement: "right",
    width: 800,
    visible: graphVisible,
    onClose: function onClose() {
      return setGraphVisible(false);
    }
  }, dependencyGraph ? /*#__PURE__*/React__default.createElement("div", null, /*#__PURE__*/React__default.createElement(Alert, {
    message: "\u4F9D\u8D56\u56FE\u8BF4\u660E",
    description: "\u4F9D\u8D56\u56FE\u5C55\u793A\u4E86\u6240\u6709\u4EFB\u52A1\u4E4B\u95F4\u7684\u4F9D\u8D56\u5173\u7CFB\u3002\u7BAD\u5934\u4ECE\u88AB\u4F9D\u8D56\u7684\u4EFB\u52A1\u6307\u5411\u4F9D\u8D56\u5B83\u7684\u4EFB\u52A1\u3002",
    type: "info",
    showIcon: true,
    style: {
      marginBottom: 16
    }
  }), /*#__PURE__*/React__default.createElement("div", {
    style: {
      border: '1px solid #d9d9d9',
      borderRadius: 4,
      padding: 20,
      minHeight: 400,
      background: '#fafafa'
    }
  }, dependencyGraph.nodes && dependencyGraph.nodes.length > 0 ? /*#__PURE__*/React__default.createElement("div", null, /*#__PURE__*/React__default.createElement("h4", null, "\u8282\u70B9 (", dependencyGraph.nodes.length, ")"), /*#__PURE__*/React__default.createElement("div", {
    style: {
      marginBottom: 16
    }
  }, dependencyGraph.nodes.map(function (node) {
    return /*#__PURE__*/React__default.createElement(Tag, {
      key: node.id,
      style: {
        marginBottom: 8
      }
    }, node.label || node.id);
  })), /*#__PURE__*/React__default.createElement("h4", null, "\u4F9D\u8D56\u5173\u7CFB (", ((_dependencyGraph$edge = dependencyGraph.edges) === null || _dependencyGraph$edge === void 0 ? void 0 : _dependencyGraph$edge.length) || 0, ")"), /*#__PURE__*/React__default.createElement("div", null, (_dependencyGraph$edge2 = dependencyGraph.edges) === null || _dependencyGraph$edge2 === void 0 ? void 0 : _dependencyGraph$edge2.map(function (edge, index) {
    return /*#__PURE__*/React__default.createElement("div", {
      key: index,
      style: {
        marginBottom: 8,
        padding: 8,
        background: '#fff',
        borderRadius: 4
      }
    }, /*#__PURE__*/React__default.createElement(LinkOutlined$1, {
      style: {
        marginRight: 4,
        color: '#52c41a'
      }
    }), /*#__PURE__*/React__default.createElement("strong", null, edge.source), /*#__PURE__*/React__default.createElement("span", {
      style: {
        margin: '0 8px'
      }
    }, "\u2192"), /*#__PURE__*/React__default.createElement("strong", null, edge.target));
  })), /*#__PURE__*/React__default.createElement(Alert, {
    message: "\u53EF\u89C6\u5316\u63D0\u793A",
    description: "\u5B8C\u6574\u7684\u4F9D\u8D56\u56FE\u53EF\u89C6\u5316\u9700\u8981\u4F7F\u7528\u56FE\u5F62\u5E93\uFF08\u5982D3.js\u3001vis.js\u7B49\uFF09\u5B9E\u73B0\u3002\u5F53\u524D\u5C55\u793A\u7684\u662F\u4F9D\u8D56\u5173\u7CFB\u7684\u6587\u672C\u5F62\u5F0F\u3002",
    type: "warning",
    showIcon: true,
    style: {
      marginTop: 16
    }
  })) : /*#__PURE__*/React__default.createElement("div", {
    style: {
      textAlign: 'center',
      padding: 40,
      color: '#999'
    }
  }, "\u6682\u65E0\u4F9D\u8D56\u5173\u7CFB"))) : /*#__PURE__*/React__default.createElement("div", {
    style: {
      textAlign: 'center',
      padding: 40
    }
  }, "\u52A0\u8F7D\u4E2D...")));
};

export { AirdropDependencies, AirdropDependencies as default };
