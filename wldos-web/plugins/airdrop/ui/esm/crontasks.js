import { _ as _slicedToArray, a as _asyncToGenerator, b as _regenerator, j as _objectSpread2$1, y as cronAPI, t as taskAPI } from './api-72a577fe.js';
const React = window.React;
const React__default = window.React;
const { useState, useEffect } = window.React;
const { Select, Input, Form, Row, Col, Card, Statistic, Button, Table, Modal, Tooltip, Tag, Space, Popconfirm, message } = window.antd;
import { A as AntdIcon, _ as _objectSpread2 } from './AntdIcon-16063a91.js';
import { P as PauseCircleOutlined } from './PauseCircleOutlined-e36f23e4.js';
import { P as PlayCircleOutlined } from './PlayCircleOutlined-25bce8f0.js';

// This icon file is generated automatically.
var QuestionCircleOutlined$2 = { "icon": { "tag": "svg", "attrs": { "viewBox": "64 64 896 896", "focusable": "false" }, "children": [{ "tag": "path", "attrs": { "d": "M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z" } }, { "tag": "path", "attrs": { "d": "M623.6 316.7C593.6 290.4 554 276 512 276s-81.6 14.5-111.6 40.7C369.2 344 352 380.7 352 420v7.6c0 4.4 3.6 8 8 8h48c4.4 0 8-3.6 8-8V420c0-44.1 43.1-80 96-80s96 35.9 96 80c0 31.1-22 59.6-56.1 72.7-21.2 8.1-39.2 22.3-52.1 40.9-13.1 19-19.9 41.8-19.9 64.9V620c0 4.4 3.6 8 8 8h48c4.4 0 8-3.6 8-8v-22.7a48.3 48.3 0 0130.9-44.8c59-22.7 97.1-74.7 97.1-132.5.1-39.3-17.1-76-48.3-103.3zM472 732a40 40 0 1080 0 40 40 0 10-80 0z" } }] }, "name": "question-circle", "theme": "outlined" };
var QuestionCircleOutlinedSvg = QuestionCircleOutlined$2;

var QuestionCircleOutlined = function QuestionCircleOutlined(props, ref) {
  return /*#__PURE__*/React.createElement(AntdIcon, _objectSpread2(_objectSpread2({}, props), {}, {
    ref: ref,
    icon: QuestionCircleOutlinedSvg
  }));
};
var RefIcon = /*#__PURE__*/React.forwardRef(QuestionCircleOutlined);
var QuestionCircleOutlined$1 = RefIcon;

var Option = Select.Option;
Input.TextArea;
var AirdropCronTasks = function AirdropCronTasks() {
  var _useState = useState([]),
    _useState2 = _slicedToArray(_useState, 2),
    cronTasks = _useState2[0],
    setCronTasks = _useState2[1];
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
    createModalVisible = _useState8[0],
    setCreateModalVisible = _useState8[1];
  var _useState9 = useState(false),
    _useState0 = _slicedToArray(_useState9, 2),
    updateModalVisible = _useState0[0],
    setUpdateModalVisible = _useState0[1];
  var _useState1 = useState(null),
    _useState10 = _slicedToArray(_useState1, 2),
    currentCronTask = _useState10[0],
    setCurrentCronTask = _useState10[1];
  var _Form$useForm = Form.useForm(),
    _Form$useForm2 = _slicedToArray(_Form$useForm, 1),
    form = _Form$useForm2[0];
  var _Form$useForm3 = Form.useForm(),
    _Form$useForm4 = _slicedToArray(_Form$useForm3, 1),
    updateForm = _Form$useForm4[0];

  // 加载Cron任务列表
  var loadCronTasks = /*#__PURE__*/function () {
    var _ref = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee() {
      var res, data, taskList, _t;
      return _regenerator().w(function (_context) {
        while (1) switch (_context.p = _context.n) {
          case 0:
            _context.p = 0;
            setLoading(true);
            _context.n = 1;
            return cronAPI.getCronTasks();
          case 1:
            res = _context.v;
            data = (res === null || res === void 0 ? void 0 : res.data) || {}; // 将Map转换为数组
            taskList = Object.entries(data).map(function (_ref2) {
              var _ref3 = _slicedToArray(_ref2, 2),
                id = _ref3[0],
                task = _ref3[1];
              return _objectSpread2$1({
                id: id
              }, task);
            });
            setCronTasks(taskList);
            _context.n = 3;
            break;
          case 2:
            _context.p = 2;
            _t = _context.v;
            message.error('加载Cron任务失败: ' + _t.message);
          case 3:
            _context.p = 3;
            setLoading(false);
            return _context.f(3);
          case 4:
            return _context.a(2);
        }
      }, _callee, null, [[0, 2, 3, 4]]);
    }));
    return function loadCronTasks() {
      return _ref.apply(this, arguments);
    };
  }();

  // 加载任务列表（用于创建Cron任务时选择）
  var loadTasks = /*#__PURE__*/function () {
    var _ref4 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee2() {
      var res, _t2;
      return _regenerator().w(function (_context2) {
        while (1) switch (_context2.p = _context2.n) {
          case 0:
            _context2.p = 0;
            _context2.n = 1;
            return taskAPI.getTasks();
          case 1:
            res = _context2.v;
            setTasks((res === null || res === void 0 ? void 0 : res.data) || []);
            _context2.n = 3;
            break;
          case 2:
            _context2.p = 2;
            _t2 = _context2.v;
            message.error('加载任务列表失败: ' + _t2.message);
          case 3:
            return _context2.a(2);
        }
      }, _callee2, null, [[0, 2]]);
    }));
    return function loadTasks() {
      return _ref4.apply(this, arguments);
    };
  }();
  useEffect(function () {
    loadCronTasks();
    loadTasks();
  }, []);

  // 创建Cron任务
  var handleCreateCronTask = /*#__PURE__*/function () {
    var _ref5 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee3(values) {
      var _t3;
      return _regenerator().w(function (_context3) {
        while (1) switch (_context3.p = _context3.n) {
          case 0:
            _context3.p = 0;
            _context3.n = 1;
            return cronAPI.createCronTask(values.taskId, values.cronExpression);
          case 1:
            message.success('Cron任务创建成功');
            setCreateModalVisible(false);
            form.resetFields();
            loadCronTasks();
            _context3.n = 3;
            break;
          case 2:
            _context3.p = 2;
            _t3 = _context3.v;
            message.error('创建Cron任务失败: ' + _t3.message);
          case 3:
            return _context3.a(2);
        }
      }, _callee3, null, [[0, 2]]);
    }));
    return function handleCreateCronTask(_x) {
      return _ref5.apply(this, arguments);
    };
  }();

  // 暂停Cron任务
  var handlePauseCronTask = /*#__PURE__*/function () {
    var _ref6 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee4(cronTaskId) {
      var _t4;
      return _regenerator().w(function (_context4) {
        while (1) switch (_context4.p = _context4.n) {
          case 0:
            _context4.p = 0;
            _context4.n = 1;
            return cronAPI.pauseCronTask(cronTaskId);
          case 1:
            message.success('Cron任务已暂停');
            loadCronTasks();
            _context4.n = 3;
            break;
          case 2:
            _context4.p = 2;
            _t4 = _context4.v;
            message.error('暂停Cron任务失败: ' + _t4.message);
          case 3:
            return _context4.a(2);
        }
      }, _callee4, null, [[0, 2]]);
    }));
    return function handlePauseCronTask(_x2) {
      return _ref6.apply(this, arguments);
    };
  }();

  // 恢复Cron任务
  var handleResumeCronTask = /*#__PURE__*/function () {
    var _ref7 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee5(cronTaskId) {
      var _t5;
      return _regenerator().w(function (_context5) {
        while (1) switch (_context5.p = _context5.n) {
          case 0:
            _context5.p = 0;
            _context5.n = 1;
            return cronAPI.resumeCronTask(cronTaskId);
          case 1:
            message.success('Cron任务已恢复');
            loadCronTasks();
            _context5.n = 3;
            break;
          case 2:
            _context5.p = 2;
            _t5 = _context5.v;
            message.error('恢复Cron任务失败: ' + _t5.message);
          case 3:
            return _context5.a(2);
        }
      }, _callee5, null, [[0, 2]]);
    }));
    return function handleResumeCronTask(_x3) {
      return _ref7.apply(this, arguments);
    };
  }();

  // 删除Cron任务
  var handleDeleteCronTask = /*#__PURE__*/function () {
    var _ref8 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee6(cronTaskId) {
      var _t6;
      return _regenerator().w(function (_context6) {
        while (1) switch (_context6.p = _context6.n) {
          case 0:
            _context6.p = 0;
            _context6.n = 1;
            return cronAPI.deleteCronTask(cronTaskId);
          case 1:
            message.success('Cron任务删除成功');
            loadCronTasks();
            _context6.n = 3;
            break;
          case 2:
            _context6.p = 2;
            _t6 = _context6.v;
            message.error('删除Cron任务失败: ' + _t6.message);
          case 3:
            return _context6.a(2);
        }
      }, _callee6, null, [[0, 2]]);
    }));
    return function handleDeleteCronTask(_x4) {
      return _ref8.apply(this, arguments);
    };
  }();

  // 更新Cron表达式
  var handleUpdateCronExpression = /*#__PURE__*/function () {
    var _ref9 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee7(values) {
      var _t7;
      return _regenerator().w(function (_context7) {
        while (1) switch (_context7.p = _context7.n) {
          case 0:
            _context7.p = 0;
            _context7.n = 1;
            return cronAPI.updateCronExpression(currentCronTask.id, values.cronExpression);
          case 1:
            message.success('Cron表达式更新成功');
            setUpdateModalVisible(false);
            updateForm.resetFields();
            setCurrentCronTask(null);
            loadCronTasks();
            _context7.n = 3;
            break;
          case 2:
            _context7.p = 2;
            _t7 = _context7.v;
            message.error('更新Cron表达式失败: ' + _t7.message);
          case 3:
            return _context7.a(2);
        }
      }, _callee7, null, [[0, 2]]);
    }));
    return function handleUpdateCronExpression(_x5) {
      return _ref9.apply(this, arguments);
    };
  }();

  // 打开更新Cron表达式对话框
  var handleOpenUpdateModal = function handleOpenUpdateModal(record) {
    var _record$queueConfig;
    setCurrentCronTask(record);
    updateForm.setFieldsValue({
      cronExpression: ((_record$queueConfig = record.queueConfig) === null || _record$queueConfig === void 0 ? void 0 : _record$queueConfig.cronExpression) || ''
    });
    setUpdateModalVisible(true);
  };

  // Cron表达式示例
  var cronExamples = [{
    label: '每分钟执行',
    value: '0 * * * * ?'
  }, {
    label: '每小时执行',
    value: '0 0 * * * ?'
  }, {
    label: '每天凌晨2点执行',
    value: '0 0 2 * * ?'
  }, {
    label: '每周一凌晨2点执行',
    value: '0 0 2 ? * MON'
  }, {
    label: '每月1号凌晨2点执行',
    value: '0 0 2 1 * ?'
  }, {
    label: '每5分钟执行',
    value: '0 */5 * * * ?'
  }, {
    label: '工作日上午9点执行',
    value: '0 0 9 ? * MON-FRI'
  }];
  var columns = [{
    title: '任务ID',
    dataIndex: 'id',
    key: 'id',
    width: 200,
    ellipsis: true
  }, {
    title: '任务名称',
    dataIndex: ['queueConfig', 'taskName'],
    key: 'taskName',
    render: function render(text, record) {
      var task = tasks.find(function (t) {
        return t.id === record.id;
      });
      return (task === null || task === void 0 ? void 0 : task.taskName) || text || record.id;
    }
  }, {
    title: 'Cron表达式',
    dataIndex: ['queueConfig', 'cronExpression'],
    key: 'cronExpression',
    render: function render(text) {
      return /*#__PURE__*/React__default.createElement("code", {
        style: {
          background: '#f5f5f5',
          padding: '2px 6px',
          borderRadius: 3
        }
      }, text || '-');
    }
  }, {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    render: function render(status) {
      var statusMap = {
        'PENDING': {
          color: 'orange',
          text: '待执行'
        },
        'PAUSED': {
          color: 'default',
          text: '已暂停'
        },
        'RUNNING': {
          color: 'blue',
          text: '运行中'
        },
        'COMPLETED': {
          color: 'green',
          text: '已完成'
        },
        'FAILED': {
          color: 'red',
          text: '失败'
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
    title: '操作',
    key: 'action',
    width: 300,
    render: function render(_, record) {
      var isPaused = record.status === 'PAUSED';
      return /*#__PURE__*/React__default.createElement(Space, null, isPaused ? /*#__PURE__*/React__default.createElement(Tooltip, {
        title: "\u6062\u590D\u4EFB\u52A1"
      }, /*#__PURE__*/React__default.createElement(Button, {
        type: "link",
        size: "small",
        icon: /*#__PURE__*/React__default.createElement(PlayCircleOutlined, null),
        onClick: function onClick() {
          return handleResumeCronTask(record.id);
        }
      }, "\u6062\u590D")) : /*#__PURE__*/React__default.createElement(Tooltip, {
        title: "\u6682\u505C\u4EFB\u52A1"
      }, /*#__PURE__*/React__default.createElement(Button, {
        type: "link",
        size: "small",
        icon: /*#__PURE__*/React__default.createElement(PauseCircleOutlined, null),
        onClick: function onClick() {
          return handlePauseCronTask(record.id);
        },
        disabled: record.status === 'RUNNING'
      }, "\u6682\u505C")), /*#__PURE__*/React__default.createElement(Button, {
        type: "link",
        size: "small",
        onClick: function onClick() {
          return handleOpenUpdateModal(record);
        }
      }, "\u66F4\u65B0Cron"), /*#__PURE__*/React__default.createElement(Popconfirm, {
        title: "\u786E\u8BA4\u5220\u9664",
        description: "\u786E\u5B9A\u8981\u5220\u9664Cron\u4EFB\u52A1 \"".concat(record.id, "\" \u5417\uFF1F"),
        onConfirm: function onConfirm() {
          return handleDeleteCronTask(record.id);
        },
        okText: "\u786E\u5B9A",
        cancelText: "\u53D6\u6D88"
      }, /*#__PURE__*/React__default.createElement(Button, {
        type: "link",
        size: "small",
        danger: true
      }, "\u5220\u9664")));
    }
  }];

  // 统计信息
  var stats = {
    total: cronTasks.length,
    running: cronTasks.filter(function (t) {
      return t.status === 'RUNNING';
    }).length,
    paused: cronTasks.filter(function (t) {
      return t.status === 'PAUSED';
    }).length,
    pending: cronTasks.filter(function (t) {
      return t.status === 'PENDING';
    }).length
  };
  return /*#__PURE__*/React__default.createElement("div", {
    className: "airdrop-cron-tasks"
  }, /*#__PURE__*/React__default.createElement(Row, {
    gutter: 16,
    style: {
      marginBottom: 16
    }
  }, /*#__PURE__*/React__default.createElement(Col, {
    span: 6
  }, /*#__PURE__*/React__default.createElement(Card, null, /*#__PURE__*/React__default.createElement(Statistic, {
    title: "\u603B\u4EFB\u52A1\u6570",
    value: stats.total
  }))), /*#__PURE__*/React__default.createElement(Col, {
    span: 6
  }, /*#__PURE__*/React__default.createElement(Card, null, /*#__PURE__*/React__default.createElement(Statistic, {
    title: "\u8FD0\u884C\u4E2D",
    value: stats.running,
    valueStyle: {
      color: '#1890ff'
    }
  }))), /*#__PURE__*/React__default.createElement(Col, {
    span: 6
  }, /*#__PURE__*/React__default.createElement(Card, null, /*#__PURE__*/React__default.createElement(Statistic, {
    title: "\u5DF2\u6682\u505C",
    value: stats.paused,
    valueStyle: {
      color: '#8c8c8c'
    }
  }))), /*#__PURE__*/React__default.createElement(Col, {
    span: 6
  }, /*#__PURE__*/React__default.createElement(Card, null, /*#__PURE__*/React__default.createElement(Statistic, {
    title: "\u5F85\u6267\u884C",
    value: stats.pending,
    valueStyle: {
      color: '#faad14'
    }
  })))), /*#__PURE__*/React__default.createElement("div", {
    style: {
      marginBottom: 16
    }
  }, /*#__PURE__*/React__default.createElement(Button, {
    type: "primary",
    onClick: function onClick() {
      return setCreateModalVisible(true);
    }
  }, "\u521B\u5EFACron\u4EFB\u52A1"), /*#__PURE__*/React__default.createElement(Button, {
    style: {
      marginLeft: 8
    },
    onClick: loadCronTasks
  }, "\u5237\u65B0")), /*#__PURE__*/React__default.createElement(Table, {
    columns: columns,
    dataSource: cronTasks,
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
    title: "\u521B\u5EFACron\u4EFB\u52A1",
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
    onFinish: handleCreateCronTask
  }, /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "taskId",
    label: "\u9009\u62E9\u4EFB\u52A1",
    rules: [{
      required: true,
      message: '请选择任务'
    }]
  }, /*#__PURE__*/React__default.createElement(Select, {
    placeholder: "\u8BF7\u9009\u62E9\u8981\u8C03\u5EA6\u7684\u4EFB\u52A1",
    showSearch: true
  }, tasks.map(function (task) {
    return /*#__PURE__*/React__default.createElement(Option, {
      key: task.id,
      value: task.id
    }, task.taskName, " (", task.id, ")");
  }))), /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "cronExpression",
    label: /*#__PURE__*/React__default.createElement("span", null, "Cron\u8868\u8FBE\u5F0F", /*#__PURE__*/React__default.createElement(Tooltip, {
      title: "Cron\u8868\u8FBE\u5F0F\u683C\u5F0F\uFF1A\u79D2 \u5206 \u65F6 \u65E5 \u6708 \u5468"
    }, /*#__PURE__*/React__default.createElement(QuestionCircleOutlined$1, {
      style: {
        marginLeft: 4
      }
    }))),
    rules: [{
      required: true,
      message: '请输入Cron表达式'
    }, {
      pattern: /^(\*|([0-9]|[1-5][0-9])|\*\/([0-9]|[1-5][0-9])) (\*|([0-9]|[1-5][0-9])|\*\/([0-9]|[1-5][0-9])) (\*|([0-9]|1[0-9]|2[0-3])|\*\/([0-9]|1[0-9]|2[0-3])) (\*|([1-9]|[12][0-9]|3[01])|\*\/([1-9]|[12][0-9]|3[01])) (\*|([1-9]|1[0-2])|\*\/([1-9]|1[0-2])) (\?|\*|([0-6])|([0-6]-[0-6])|([0-6],)+[0-6])$/,
      message: 'Cron表达式格式不正确'
    }]
  }, /*#__PURE__*/React__default.createElement(Input, {
    placeholder: "\u4F8B\u5982: 0 0 2 * * ? (\u6BCF\u5929\u51CC\u66682\u70B9\u6267\u884C)"
  })), /*#__PURE__*/React__default.createElement(Form.Item, {
    label: "\u5E38\u7528\u793A\u4F8B"
  }, /*#__PURE__*/React__default.createElement(Select, {
    placeholder: "\u9009\u62E9\u5E38\u7528Cron\u8868\u8FBE\u5F0F",
    onChange: function onChange(value) {
      return form.setFieldsValue({
        cronExpression: value
      });
    }
  }, cronExamples.map(function (example) {
    return /*#__PURE__*/React__default.createElement(Option, {
      key: example.value,
      value: example.value
    }, example.label, " - ", example.value);
  }))), /*#__PURE__*/React__default.createElement(Form.Item, {
    label: "Cron\u8868\u8FBE\u5F0F\u8BF4\u660E"
  }, /*#__PURE__*/React__default.createElement("div", {
    style: {
      background: '#f5f5f5',
      padding: 12,
      borderRadius: 4,
      fontSize: 12
    }
  }, /*#__PURE__*/React__default.createElement("p", null, /*#__PURE__*/React__default.createElement("strong", null, "\u683C\u5F0F\uFF1A"), "\u79D2 \u5206 \u65F6 \u65E5 \u6708 \u5468"), /*#__PURE__*/React__default.createElement("p", null, /*#__PURE__*/React__default.createElement("strong", null, "\u793A\u4F8B\uFF1A")), /*#__PURE__*/React__default.createElement("ul", {
    style: {
      margin: 0,
      paddingLeft: 20
    }
  }, /*#__PURE__*/React__default.createElement("li", null, /*#__PURE__*/React__default.createElement("code", null, "0 * * * * ?"), " - \u6BCF\u5206\u949F\u6267\u884C"), /*#__PURE__*/React__default.createElement("li", null, /*#__PURE__*/React__default.createElement("code", null, "0 0 2 * * ?"), " - \u6BCF\u5929\u51CC\u66682\u70B9\u6267\u884C"), /*#__PURE__*/React__default.createElement("li", null, /*#__PURE__*/React__default.createElement("code", null, "0 */5 * * * ?"), " - \u6BCF5\u5206\u949F\u6267\u884C"), /*#__PURE__*/React__default.createElement("li", null, /*#__PURE__*/React__default.createElement("code", null, "0 0 9 ? * MON-FRI"), " - \u5DE5\u4F5C\u65E5\u4E0A\u53489\u70B9\u6267\u884C")))))), /*#__PURE__*/React__default.createElement(Modal, {
    title: "\u66F4\u65B0Cron\u8868\u8FBE\u5F0F",
    visible: updateModalVisible,
    onCancel: function onCancel() {
      setUpdateModalVisible(false);
      updateForm.resetFields();
      setCurrentCronTask(null);
    },
    onOk: function onOk() {
      return updateForm.submit();
    },
    width: 700
  }, /*#__PURE__*/React__default.createElement(Form, {
    form: updateForm,
    layout: "vertical",
    onFinish: handleUpdateCronExpression
  }, /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "cronExpression",
    label: /*#__PURE__*/React__default.createElement("span", null, "Cron\u8868\u8FBE\u5F0F", /*#__PURE__*/React__default.createElement(Tooltip, {
      title: "Cron\u8868\u8FBE\u5F0F\u683C\u5F0F\uFF1A\u79D2 \u5206 \u65F6 \u65E5 \u6708 \u5468"
    }, /*#__PURE__*/React__default.createElement(QuestionCircleOutlined$1, {
      style: {
        marginLeft: 4
      }
    }))),
    rules: [{
      required: true,
      message: '请输入Cron表达式'
    }, {
      pattern: /^(\*|([0-9]|[1-5][0-9])|\*\/([0-9]|[1-5][0-9])) (\*|([0-9]|[1-5][0-9])|\*\/([0-9]|[1-5][0-9])) (\*|([0-9]|1[0-9]|2[0-3])|\*\/([0-9]|1[0-9]|2[0-3])) (\*|([1-9]|[12][0-9]|3[01])|\*\/([1-9]|[12][0-9]|3[01])) (\*|([1-9]|1[0-2])|\*\/([1-9]|1[0-2])) (\?|\*|([0-6])|([0-6]-[0-6])|([0-6],)+[0-6])$/,
      message: 'Cron表达式格式不正确'
    }]
  }, /*#__PURE__*/React__default.createElement(Input, {
    placeholder: "\u4F8B\u5982: 0 0 2 * * ? (\u6BCF\u5929\u51CC\u66682\u70B9\u6267\u884C)"
  })), /*#__PURE__*/React__default.createElement(Form.Item, {
    label: "\u5E38\u7528\u793A\u4F8B"
  }, /*#__PURE__*/React__default.createElement(Select, {
    placeholder: "\u9009\u62E9\u5E38\u7528Cron\u8868\u8FBE\u5F0F",
    onChange: function onChange(value) {
      return updateForm.setFieldsValue({
        cronExpression: value
      });
    }
  }, cronExamples.map(function (example) {
    return /*#__PURE__*/React__default.createElement(Option, {
      key: example.value,
      value: example.value
    }, example.label, " - ", example.value);
  }))))));
};

export { AirdropCronTasks, AirdropCronTasks as default };
