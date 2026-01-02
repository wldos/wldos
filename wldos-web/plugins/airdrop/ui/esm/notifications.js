import { _ as _slicedToArray, a as _asyncToGenerator, b as _regenerator, t as taskAPI, D as notificationAPI } from './api-72a577fe.js';
const React = window.React;
const React__default = window.React;
const { useState, useEffect } = window.React;
const { Select, Input, Form, Card, Table, Space, Button, Descriptions, Tag, Alert, Modal, Switch, message } = window.antd;
import { A as AntdIcon, _ as _objectSpread2 } from './AntdIcon-16063a91.js';
import { H as HistoryOutlined } from './HistoryOutlined-8527818a.js';

// This icon file is generated automatically.
var MailOutlined$2 = { "icon": { "tag": "svg", "attrs": { "viewBox": "64 64 896 896", "focusable": "false" }, "children": [{ "tag": "path", "attrs": { "d": "M928 160H96c-17.7 0-32 14.3-32 32v640c0 17.7 14.3 32 32 32h832c17.7 0 32-14.3 32-32V192c0-17.7-14.3-32-32-32zm-40 110.8V792H136V270.8l-27.6-21.5 39.3-50.5 42.8 33.3h643.1l42.8-33.3 39.3 50.5-27.7 21.5zM833.6 232L512 482 190.4 232l-42.8-33.3-39.3 50.5 27.6 21.5 341.6 265.6a55.99 55.99 0 0068.7 0L888 270.8l27.6-21.5-39.3-50.5-42.7 33.2z" } }] }, "name": "mail", "theme": "outlined" };
var MailOutlinedSvg = MailOutlined$2;

var MailOutlined = function MailOutlined(props, ref) {
  return /*#__PURE__*/React.createElement(AntdIcon, _objectSpread2(_objectSpread2({}, props), {}, {
    ref: ref,
    icon: MailOutlinedSvg
  }));
};
var RefIcon$1 = /*#__PURE__*/React.forwardRef(MailOutlined);
var MailOutlined$1 = RefIcon$1;

// This icon file is generated automatically.
var SendOutlined$2 = { "icon": { "tag": "svg", "attrs": { "viewBox": "64 64 896 896", "focusable": "false" }, "children": [{ "tag": "defs", "attrs": {}, "children": [{ "tag": "style", "attrs": {} }] }, { "tag": "path", "attrs": { "d": "M931.4 498.9L94.9 79.5c-3.4-1.7-7.3-2.1-11-1.2a15.99 15.99 0 00-11.7 19.3l86.2 352.2c1.3 5.3 5.2 9.6 10.4 11.3l147.7 50.7-147.6 50.7c-5.2 1.8-9.1 6-10.3 11.3L72.2 926.5c-.9 3.7-.5 7.6 1.2 10.9 3.9 7.9 13.5 11.1 21.5 7.2l836.5-417c3.1-1.5 5.6-4.1 7.2-7.1 3.9-8 .7-17.6-7.2-21.6zM170.8 826.3l50.3-205.6 295.2-101.3c2.3-.8 4.2-2.6 5-5 1.4-4.2-.8-8.7-5-10.2L221.1 403 171 198.2l628 314.9-628.2 313.2z" } }] }, "name": "send", "theme": "outlined" };
var SendOutlinedSvg = SendOutlined$2;

var SendOutlined = function SendOutlined(props, ref) {
  return /*#__PURE__*/React.createElement(AntdIcon, _objectSpread2(_objectSpread2({}, props), {}, {
    ref: ref,
    icon: SendOutlinedSvg
  }));
};
var RefIcon = /*#__PURE__*/React.forwardRef(SendOutlined);
var SendOutlined$1 = RefIcon;

Select.Option;
var TextArea = Input.TextArea;
var AirdropNotifications = function AirdropNotifications() {
  var _notificationRule$rec2;
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
    notificationRule = _useState6[0],
    setNotificationRule = _useState6[1];
  var _useState7 = useState([]),
    _useState8 = _slicedToArray(_useState7, 2),
    notificationHistory = _useState8[0],
    setNotificationHistory = _useState8[1];
  var _useState9 = useState(false),
    _useState0 = _slicedToArray(_useState9, 2),
    loading = _useState0[0],
    setLoading = _useState0[1];
  var _useState1 = useState(false),
    _useState10 = _slicedToArray(_useState1, 2),
    configModalVisible = _useState10[0],
    setConfigModalVisible = _useState10[1];
  var _useState11 = useState(false),
    _useState12 = _slicedToArray(_useState11, 2),
    testModalVisible = _useState12[0],
    setTestModalVisible = _useState12[1];
  var _Form$useForm = Form.useForm(),
    _Form$useForm2 = _slicedToArray(_Form$useForm, 1),
    form = _Form$useForm2[0];
  var _Form$useForm3 = Form.useForm(),
    _Form$useForm4 = _slicedToArray(_Form$useForm3, 1),
    testForm = _Form$useForm4[0];

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

  // 加载通知规则
  var loadNotificationRule = /*#__PURE__*/function () {
    var _ref2 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee2(taskId) {
      var res;
      return _regenerator().w(function (_context2) {
        while (1) switch (_context2.p = _context2.n) {
          case 0:
            _context2.p = 0;
            _context2.n = 1;
            return notificationAPI.getNotificationRule(taskId);
          case 1:
            res = _context2.v;
            setNotificationRule(res === null || res === void 0 ? void 0 : res.data);
            _context2.n = 3;
            break;
          case 2:
            _context2.p = 2;
            _context2.v;
            // 如果没有配置规则，返回null
            setNotificationRule(null);
          case 3:
            return _context2.a(2);
        }
      }, _callee2, null, [[0, 2]]);
    }));
    return function loadNotificationRule(_x) {
      return _ref2.apply(this, arguments);
    };
  }();

  // 加载通知历史
  var loadNotificationHistory = /*#__PURE__*/function () {
    var _ref3 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee3(taskId) {
      var res, _t3;
      return _regenerator().w(function (_context3) {
        while (1) switch (_context3.p = _context3.n) {
          case 0:
            _context3.p = 0;
            setLoading(true);
            _context3.n = 1;
            return notificationAPI.getNotificationHistory(taskId, 50);
          case 1:
            res = _context3.v;
            setNotificationHistory((res === null || res === void 0 ? void 0 : res.data) || []);
            _context3.n = 3;
            break;
          case 2:
            _context3.p = 2;
            _t3 = _context3.v;
            message.error('加载通知历史失败: ' + _t3.message);
          case 3:
            _context3.p = 3;
            setLoading(false);
            return _context3.f(3);
          case 4:
            return _context3.a(2);
        }
      }, _callee3, null, [[0, 2, 3, 4]]);
    }));
    return function loadNotificationHistory(_x2) {
      return _ref3.apply(this, arguments);
    };
  }();
  useEffect(function () {
    loadTasks();
  }, []);
  useEffect(function () {
    if (selectedTaskId) {
      loadNotificationRule(selectedTaskId);
      loadNotificationHistory(selectedTaskId);
    }
  }, [selectedTaskId]);

  // 配置通知规则
  var handleConfigureNotification = /*#__PURE__*/function () {
    var _ref4 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee4(values) {
      var rule, _t4;
      return _regenerator().w(function (_context4) {
        while (1) switch (_context4.p = _context4.n) {
          case 0:
            _context4.p = 0;
            rule = {
              taskId: selectedTaskId,
              recipients: values.recipients ? values.recipients.split(',').map(function (e) {
                return e.trim();
              }) : [],
              notifyOnSuccess: values.notifyOnSuccess !== false,
              notifyOnFailure: values.notifyOnFailure !== false,
              template: values.template
            };
            _context4.n = 1;
            return notificationAPI.configureNotification(selectedTaskId, rule);
          case 1:
            message.success('通知规则配置成功');
            setConfigModalVisible(false);
            form.resetFields();
            loadNotificationRule(selectedTaskId);
            _context4.n = 3;
            break;
          case 2:
            _context4.p = 2;
            _t4 = _context4.v;
            message.error('配置通知规则失败: ' + _t4.message);
          case 3:
            return _context4.a(2);
        }
      }, _callee4, null, [[0, 2]]);
    }));
    return function handleConfigureNotification(_x3) {
      return _ref4.apply(this, arguments);
    };
  }();

  // 发送测试邮件
  var handleSendTestEmail = /*#__PURE__*/function () {
    var _ref5 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee5(values) {
      var recipients, _t5;
      return _regenerator().w(function (_context5) {
        while (1) switch (_context5.p = _context5.n) {
          case 0:
            _context5.p = 0;
            recipients = values.recipients ? values.recipients.split(',').map(function (e) {
              return e.trim();
            }) : [];
            _context5.n = 1;
            return notificationAPI.sendTestEmail(selectedTaskId, recipients);
          case 1:
            message.success('测试邮件已发送');
            setTestModalVisible(false);
            testForm.resetFields();
            _context5.n = 3;
            break;
          case 2:
            _context5.p = 2;
            _t5 = _context5.v;
            message.error('发送测试邮件失败: ' + _t5.message);
          case 3:
            return _context5.a(2);
        }
      }, _callee5, null, [[0, 2]]);
    }));
    return function handleSendTestEmail(_x4) {
      return _ref5.apply(this, arguments);
    };
  }();

  // 打开配置对话框
  var handleOpenConfig = function handleOpenConfig(taskId) {
    setSelectedTaskId(taskId);
    if (notificationRule) {
      var _notificationRule$rec;
      form.setFieldsValue({
        recipients: ((_notificationRule$rec = notificationRule.recipients) === null || _notificationRule$rec === void 0 ? void 0 : _notificationRule$rec.join(',')) || '',
        notifyOnSuccess: notificationRule.notifyOnSuccess !== false,
        notifyOnFailure: notificationRule.notifyOnFailure !== false,
        template: notificationRule.template || ''
      });
    } else {
      form.resetFields();
      form.setFieldsValue({
        notifyOnSuccess: true,
        notifyOnFailure: true
      });
    }
    setConfigModalVisible(true);
  };
  var taskColumns = [{
    title: '任务名称',
    dataIndex: 'taskName',
    key: 'taskName'
  }, {
    title: '任务类型',
    dataIndex: 'taskType',
    key: 'taskType'
  }, {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    render: function render(status) {
      var statusMap = {
        'pending': {
          color: 'orange',
          text: '待执行'
        },
        'running': {
          color: 'blue',
          text: '运行中'
        },
        'completed': {
          color: 'green',
          text: '已完成'
        },
        'failed': {
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
    title: '通知配置',
    key: 'notification',
    render: function render(_, record) {
      // 这里可以检查是否有通知配置
      return /*#__PURE__*/React__default.createElement(Space, null, /*#__PURE__*/React__default.createElement(Button, {
        type: "link",
        size: "small",
        icon: /*#__PURE__*/React__default.createElement(MailOutlined$1, null),
        onClick: function onClick() {
          setSelectedTaskId(record.id);
          loadNotificationRule(record.id);
          loadNotificationHistory(record.id);
        }
      }, "\u914D\u7F6E\u901A\u77E5"));
    }
  }];
  var historyColumns = [{
    title: '类型',
    dataIndex: 'type',
    key: 'type',
    render: function render(type) {
      var typeMap = {
        'SUCCESS': {
          color: 'green',
          text: '成功通知'
        },
        'FAILED': {
          color: 'red',
          text: '失败通知'
        }
      };
      var typeInfo = typeMap[type] || {
        color: 'default',
        text: type
      };
      return /*#__PURE__*/React__default.createElement(Tag, {
        color: typeInfo.color
      }, typeInfo.text);
    }
  }, {
    title: '消息',
    dataIndex: 'message',
    key: 'message',
    ellipsis: true
  }, {
    title: '收件人',
    dataIndex: 'recipients',
    key: 'recipients',
    render: function render(recipients) {
      return (recipients === null || recipients === void 0 ? void 0 : recipients.join(', ')) || '-';
    }
  }, {
    title: '发送时间',
    dataIndex: 'sendTime',
    key: 'sendTime',
    render: function render(time) {
      return time ? new Date(time).toLocaleString() : '-';
    }
  }, {
    title: '状态',
    dataIndex: 'success',
    key: 'success',
    render: function render(success) {
      return /*#__PURE__*/React__default.createElement(Tag, {
        color: success ? 'success' : 'error'
      }, success ? '成功' : '失败');
    }
  }];
  var selectedTask = tasks.find(function (t) {
    return t.id === selectedTaskId;
  });
  return /*#__PURE__*/React__default.createElement("div", {
    className: "airdrop-notifications"
  }, /*#__PURE__*/React__default.createElement(Row, {
    gutter: 16
  }, /*#__PURE__*/React__default.createElement(Col, {
    span: 12
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
          loadNotificationRule(record.id);
          loadNotificationHistory(record.id);
        },
        style: {
          cursor: 'pointer',
          background: selectedTaskId === record.id ? '#e6f7ff' : 'transparent'
        }
      };
    }
  }))), /*#__PURE__*/React__default.createElement(Col, {
    span: 12
  }, selectedTaskId ? /*#__PURE__*/React__default.createElement(React__default.Fragment, null, /*#__PURE__*/React__default.createElement(Card, {
    title: "\u901A\u77E5\u914D\u7F6E - ".concat((selectedTask === null || selectedTask === void 0 ? void 0 : selectedTask.taskName) || selectedTaskId),
    extra: /*#__PURE__*/React__default.createElement(Space, null, /*#__PURE__*/React__default.createElement(Button, {
      type: "primary",
      icon: /*#__PURE__*/React__default.createElement(MailOutlined$1, null),
      onClick: function onClick() {
        return handleOpenConfig(selectedTaskId);
      }
    }, "\u914D\u7F6E\u901A\u77E5"), /*#__PURE__*/React__default.createElement(Button, {
      icon: /*#__PURE__*/React__default.createElement(SendOutlined$1, null),
      onClick: function onClick() {
        testForm.resetFields();
        if (notificationRule !== null && notificationRule !== void 0 && notificationRule.recipients) {
          testForm.setFieldsValue({
            recipients: notificationRule.recipients.join(',')
          });
        }
        setTestModalVisible(true);
      }
    }, "\u53D1\u9001\u6D4B\u8BD5")),
    style: {
      marginBottom: 16
    }
  }, notificationRule ? /*#__PURE__*/React__default.createElement(Descriptions, {
    column: 1,
    size: "small"
  }, /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u6536\u4EF6\u4EBA"
  }, ((_notificationRule$rec2 = notificationRule.recipients) === null || _notificationRule$rec2 === void 0 ? void 0 : _notificationRule$rec2.join(', ')) || '未配置'), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u6210\u529F\u65F6\u901A\u77E5"
  }, /*#__PURE__*/React__default.createElement(Tag, {
    color: notificationRule.notifyOnSuccess ? 'success' : 'default'
  }, notificationRule.notifyOnSuccess ? '是' : '否')), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u5931\u8D25\u65F6\u901A\u77E5"
  }, /*#__PURE__*/React__default.createElement(Tag, {
    color: notificationRule.notifyOnFailure ? 'error' : 'default'
  }, notificationRule.notifyOnFailure ? '是' : '否')), notificationRule.template && /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u90AE\u4EF6\u6A21\u677F"
  }, notificationRule.template)) : /*#__PURE__*/React__default.createElement(Alert, {
    message: "\u672A\u914D\u7F6E\u901A\u77E5\u89C4\u5219",
    description: "\u70B9\u51FB\"\u914D\u7F6E\u901A\u77E5\"\u6309\u94AE\u4E3A\u4EFB\u52A1\u914D\u7F6E\u90AE\u4EF6\u901A\u77E5\u89C4\u5219",
    type: "info",
    showIcon: true
  })), /*#__PURE__*/React__default.createElement(Card, {
    title: /*#__PURE__*/React__default.createElement("span", null, /*#__PURE__*/React__default.createElement(HistoryOutlined, null), " \u901A\u77E5\u5386\u53F2")
  }, /*#__PURE__*/React__default.createElement(Table, {
    columns: historyColumns,
    dataSource: notificationHistory,
    rowKey: function rowKey(record, index) {
      return "".concat(record.sendTime, "_").concat(index);
    },
    loading: loading,
    pagination: {
      pageSize: 10
    },
    locale: {
      emptyText: '暂无通知历史'
    }
  }))) : /*#__PURE__*/React__default.createElement(Card, null, /*#__PURE__*/React__default.createElement(Alert, {
    message: "\u8BF7\u9009\u62E9\u4EFB\u52A1",
    description: "\u4ECE\u5DE6\u4FA7\u5217\u8868\u4E2D\u9009\u62E9\u4E00\u4E2A\u4EFB\u52A1\u6765\u67E5\u770B\u548C\u914D\u7F6E\u901A\u77E5\u89C4\u5219",
    type: "info",
    showIcon: true
  })))), /*#__PURE__*/React__default.createElement(Modal, {
    title: "\u914D\u7F6E\u901A\u77E5\u89C4\u5219",
    visible: configModalVisible,
    onCancel: function onCancel() {
      setConfigModalVisible(false);
      form.resetFields();
    },
    onOk: function onOk() {
      return form.submit();
    },
    width: 600
  }, /*#__PURE__*/React__default.createElement(Form, {
    form: form,
    layout: "vertical",
    onFinish: handleConfigureNotification
  }, /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "recipients",
    label: "\u6536\u4EF6\u4EBA\u90AE\u7BB1",
    rules: [{
      required: true,
      message: '请输入收件人邮箱'
    }],
    tooltip: "\u591A\u4E2A\u90AE\u7BB1\u7528\u9017\u53F7\u5206\u9694"
  }, /*#__PURE__*/React__default.createElement(TextArea, {
    rows: 3,
    placeholder: "\u4F8B\u5982: user1@example.com, user2@example.com"
  })), /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "notifyOnSuccess",
    label: "\u4EFB\u52A1\u6210\u529F\u65F6\u901A\u77E5",
    valuePropName: "checked",
    initialValue: true
  }, /*#__PURE__*/React__default.createElement(Switch, null)), /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "notifyOnFailure",
    label: "\u4EFB\u52A1\u5931\u8D25\u65F6\u901A\u77E5",
    valuePropName: "checked",
    initialValue: true
  }, /*#__PURE__*/React__default.createElement(Switch, null)), /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "template",
    label: "\u90AE\u4EF6\u6A21\u677F\uFF08\u53EF\u9009\uFF09"
  }, /*#__PURE__*/React__default.createElement(Input, {
    placeholder: "\u81EA\u5B9A\u4E49\u90AE\u4EF6\u6A21\u677F"
  })), /*#__PURE__*/React__default.createElement(Alert, {
    message: "\u901A\u77E5\u8BF4\u660E",
    description: "\u914D\u7F6E\u540E\uFF0C\u5F53\u4EFB\u52A1\u6267\u884C\u5B8C\u6210\u6216\u5931\u8D25\u65F6\uFF0C\u7CFB\u7EDF\u4F1A\u81EA\u52A8\u53D1\u9001\u90AE\u4EF6\u901A\u77E5\u5230\u6307\u5B9A\u7684\u6536\u4EF6\u4EBA\u3002",
    type: "info",
    showIcon: true,
    style: {
      marginTop: 16
    }
  }))), /*#__PURE__*/React__default.createElement(Modal, {
    title: "\u53D1\u9001\u6D4B\u8BD5\u90AE\u4EF6",
    visible: testModalVisible,
    onCancel: function onCancel() {
      setTestModalVisible(false);
      testForm.resetFields();
    },
    onOk: function onOk() {
      return testForm.submit();
    },
    width: 500
  }, /*#__PURE__*/React__default.createElement(Form, {
    form: testForm,
    layout: "vertical",
    onFinish: handleSendTestEmail
  }, /*#__PURE__*/React__default.createElement(Form.Item, {
    name: "recipients",
    label: "\u6536\u4EF6\u4EBA\u90AE\u7BB1",
    rules: [{
      required: true,
      message: '请输入收件人邮箱'
    }],
    tooltip: "\u591A\u4E2A\u90AE\u7BB1\u7528\u9017\u53F7\u5206\u9694"
  }, /*#__PURE__*/React__default.createElement(TextArea, {
    rows: 3,
    placeholder: "\u4F8B\u5982: user1@example.com, user2@example.com"
  })), /*#__PURE__*/React__default.createElement(Alert, {
    message: "\u6D4B\u8BD5\u90AE\u4EF6",
    description: "\u5C06\u53D1\u9001\u4E00\u5C01\u6D4B\u8BD5\u90AE\u4EF6\u5230\u6307\u5B9A\u7684\u6536\u4EF6\u4EBA\uFF0C\u7528\u4E8E\u9A8C\u8BC1\u90AE\u4EF6\u914D\u7F6E\u662F\u5426\u6B63\u786E\u3002",
    type: "info",
    showIcon: true,
    style: {
      marginTop: 16
    }
  }))));
};

export { AirdropNotifications, AirdropNotifications as default };
