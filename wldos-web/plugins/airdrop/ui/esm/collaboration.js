import { _ as _slicedToArray, a as _asyncToGenerator, b as _regenerator, t as taskAPI, I as collaborationAPI } from './api-72a577fe.js';
const React = window.React;
const React__default = window.React;
const { useState, useRef, useEffect } = window.React;
const { Select, Input, Form, Row, Col, Card, Table, Space, Button, Alert, Descriptions, Tag, Modal, message } = window.antd;
import { A as AntdIcon, _ as _objectSpread2 } from './AntdIcon-16063a91.js';
import { S as SaveOutlined } from './SaveOutlined-3c0aade6.js';
import { H as HistoryOutlined } from './HistoryOutlined-8527818a.js';

// This icon file is generated automatically.
var TeamOutlined$2 = { "icon": { "tag": "svg", "attrs": { "viewBox": "64 64 896 896", "focusable": "false" }, "children": [{ "tag": "path", "attrs": { "d": "M824.2 699.9a301.55 301.55 0 00-86.4-60.4C783.1 602.8 812 546.8 812 484c0-110.8-92.4-201.7-203.2-200-109.1 1.7-197 90.6-197 200 0 62.8 29 118.8 74.2 155.5a300.95 300.95 0 00-86.4 60.4C345 754.6 314 826.8 312 903.8a8 8 0 008 8.2h56c4.3 0 7.9-3.4 8-7.7 1.9-58 25.4-112.3 66.7-153.5A226.62 226.62 0 01612 684c60.9 0 118.2 23.7 161.3 66.8C814.5 792 838 846.3 840 904.3c.1 4.3 3.7 7.7 8 7.7h56a8 8 0 008-8.2c-2-77-33-149.2-87.8-203.9zM612 612c-34.2 0-66.4-13.3-90.5-37.5a126.86 126.86 0 01-37.5-91.8c.3-32.8 13.4-64.5 36.3-88 24-24.6 56.1-38.3 90.4-38.7 33.9-.3 66.8 12.9 91 36.6 24.8 24.3 38.4 56.8 38.4 91.4 0 34.2-13.3 66.3-37.5 90.5A127.3 127.3 0 01612 612zM361.5 510.4c-.9-8.7-1.4-17.5-1.4-26.4 0-15.9 1.5-31.4 4.3-46.5.7-3.6-1.2-7.3-4.5-8.8-13.6-6.1-26.1-14.5-36.9-25.1a127.54 127.54 0 01-38.7-95.4c.9-32.1 13.8-62.6 36.3-85.6 24.7-25.3 57.9-39.1 93.2-38.7 31.9.3 62.7 12.6 86 34.4 7.9 7.4 14.7 15.6 20.4 24.4 2 3.1 5.9 4.4 9.3 3.2 17.6-6.1 36.2-10.4 55.3-12.4 5.6-.6 8.8-6.6 6.3-11.6-32.5-64.3-98.9-108.7-175.7-109.9-110.9-1.7-203.3 89.2-203.3 199.9 0 62.8 28.9 118.8 74.2 155.5-31.8 14.7-61.1 35-86.5 60.4-54.8 54.7-85.8 126.9-87.8 204a8 8 0 008 8.2h56.1c4.3 0 7.9-3.4 8-7.7 1.9-58 25.4-112.3 66.7-153.5 29.4-29.4 65.4-49.8 104.7-59.7 3.9-1 6.5-4.7 6-8.7z" } }] }, "name": "team", "theme": "outlined" };
var TeamOutlinedSvg = TeamOutlined$2;

var TeamOutlined = function TeamOutlined(props, ref) {
  return /*#__PURE__*/React.createElement(AntdIcon, _objectSpread2(_objectSpread2({}, props), {}, {
    ref: ref,
    icon: TeamOutlinedSvg
  }));
};
var RefIcon$1 = /*#__PURE__*/React.forwardRef(TeamOutlined);
var TeamOutlined$1 = RefIcon$1;

// This icon file is generated automatically.
var UserOutlined$2 = { "icon": { "tag": "svg", "attrs": { "viewBox": "64 64 896 896", "focusable": "false" }, "children": [{ "tag": "path", "attrs": { "d": "M858.5 763.6a374 374 0 00-80.6-119.5 375.63 375.63 0 00-119.5-80.6c-.4-.2-.8-.3-1.2-.5C719.5 518 760 444.7 760 362c0-137-111-248-248-248S264 225 264 362c0 82.7 40.5 156 102.8 201.1-.4.2-.8.3-1.2.5-44.8 18.9-85 46-119.5 80.6a375.63 375.63 0 00-80.6 119.5A371.7 371.7 0 00136 901.8a8 8 0 008 8.2h60c4.4 0 7.9-3.5 8-7.8 2-77.2 33-149.5 87.8-204.3 56.7-56.7 132-87.9 212.2-87.9s155.5 31.2 212.2 87.9C779 752.7 810 825 812 902.2c.1 4.4 3.6 7.8 8 7.8h60a8 8 0 008-8.2c-1-47.8-10.9-94.3-29.5-138.2zM512 534c-45.9 0-89.1-17.9-121.6-50.4S340 407.9 340 362c0-45.9 17.9-89.1 50.4-121.6S466.1 190 512 190s89.1 17.9 121.6 50.4S684 316.1 684 362c0 45.9-17.9 89.1-50.4 121.6S557.9 534 512 534z" } }] }, "name": "user", "theme": "outlined" };
var UserOutlinedSvg = UserOutlined$2;

var UserOutlined = function UserOutlined(props, ref) {
  return /*#__PURE__*/React.createElement(AntdIcon, _objectSpread2(_objectSpread2({}, props), {}, {
    ref: ref,
    icon: UserOutlinedSvg
  }));
};
var RefIcon = /*#__PURE__*/React.forwardRef(UserOutlined);
var UserOutlined$1 = RefIcon;

Select.Option;
var TextArea = Input.TextArea;
var AirdropCollaboration = function AirdropCollaboration() {
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
    collaborationSession = _useState6[0],
    setCollaborationSession = _useState6[1];
  var _useState7 = useState(''),
    _useState8 = _slicedToArray(_useState7, 2),
    collaborationContent = _useState8[0],
    setCollaborationContent = _useState8[1];
  var _useState9 = useState([]),
    _useState0 = _slicedToArray(_useState9, 2),
    collaborationHistory = _useState0[0],
    setCollaborationHistory = _useState0[1];
  var _useState1 = useState([]),
    _useState10 = _slicedToArray(_useState1, 2),
    participants = _useState10[0],
    setParticipants = _useState10[1];
  var _useState11 = useState({}),
    _useState12 = _slicedToArray(_useState11, 2),
    lockedLines = _useState12[0],
    setLockedLines = _useState12[1]; // lineNumber -> userId
  var _useState13 = useState(false),
    _useState14 = _slicedToArray(_useState13, 2);
    _useState14[0];
    _useState14[1];
  var _useState15 = useState(false),
    _useState16 = _slicedToArray(_useState15, 2),
    joinModalVisible = _useState16[0],
    setJoinModalVisible = _useState16[1];
  var _useState17 = useState(false),
    _useState18 = _slicedToArray(_useState17, 2),
    historyModalVisible = _useState18[0],
    setHistoryModalVisible = _useState18[1];
  var _Form$useForm = Form.useForm(),
    _Form$useForm2 = _slicedToArray(_Form$useForm, 1);
    _Form$useForm2[0];
  var contentRef = useRef(null);

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

  // 加入协作会话
  var handleJoinSession = /*#__PURE__*/function () {
    var _ref2 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee2(taskId) {
      var res, _t2;
      return _regenerator().w(function (_context2) {
        while (1) switch (_context2.p = _context2.n) {
          case 0:
            _context2.p = 0;
            _context2.n = 1;
            return collaborationAPI.createSession(taskId);
          case 1:
            res = _context2.v;
            setCollaborationSession(res === null || res === void 0 ? void 0 : res.data);
            setSelectedTaskId(taskId);
            loadCollaborationContent(taskId);
            loadCollaborationHistory(taskId);
            message.success('已加入协作会话');
            _context2.n = 3;
            break;
          case 2:
            _context2.p = 2;
            _t2 = _context2.v;
            message.error('加入协作会话失败: ' + _t2.message);
          case 3:
            return _context2.a(2);
        }
      }, _callee2, null, [[0, 2]]);
    }));
    return function handleJoinSession(_x) {
      return _ref2.apply(this, arguments);
    };
  }();

  // 离开协作会话
  var handleLeaveSession = /*#__PURE__*/function () {
    var _ref3 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee3() {
      var _t3;
      return _regenerator().w(function (_context3) {
        while (1) switch (_context3.p = _context3.n) {
          case 0:
            _context3.p = 0;
            _context3.n = 1;
            return collaborationAPI.leaveSession(selectedTaskId);
          case 1:
            setCollaborationSession(null);
            setCollaborationContent('');
            setCollaborationHistory([]);
            setParticipants([]);
            setSelectedTaskId(null);
            message.success('已离开协作会话');
            _context3.n = 3;
            break;
          case 2:
            _context3.p = 2;
            _t3 = _context3.v;
            message.error('离开协作会话失败: ' + _t3.message);
          case 3:
            return _context3.a(2);
        }
      }, _callee3, null, [[0, 2]]);
    }));
    return function handleLeaveSession() {
      return _ref3.apply(this, arguments);
    };
  }();

  // 加载协作内容
  var loadCollaborationContent = /*#__PURE__*/function () {
    var _ref4 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee4(taskId) {
      var _res$data, _res$data2, _res$data3, res, _t4;
      return _regenerator().w(function (_context4) {
        while (1) switch (_context4.p = _context4.n) {
          case 0:
            _context4.p = 0;
            _context4.n = 1;
            return collaborationAPI.getContent(taskId);
          case 1:
            res = _context4.v;
            setCollaborationContent((res === null || res === void 0 || (_res$data = res.data) === null || _res$data === void 0 ? void 0 : _res$data.content) || '');
            setParticipants((res === null || res === void 0 || (_res$data2 = res.data) === null || _res$data2 === void 0 ? void 0 : _res$data2.participants) || []);
            setLockedLines((res === null || res === void 0 || (_res$data3 = res.data) === null || _res$data3 === void 0 ? void 0 : _res$data3.lockedLines) || {});
            _context4.n = 3;
            break;
          case 2:
            _context4.p = 2;
            _t4 = _context4.v;
            message.error('加载协作内容失败: ' + _t4.message);
          case 3:
            return _context4.a(2);
        }
      }, _callee4, null, [[0, 2]]);
    }));
    return function loadCollaborationContent(_x2) {
      return _ref4.apply(this, arguments);
    };
  }();

  // 加载协作历史
  var loadCollaborationHistory = /*#__PURE__*/function () {
    var _ref5 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee5(taskId) {
      var res, _t5;
      return _regenerator().w(function (_context5) {
        while (1) switch (_context5.p = _context5.n) {
          case 0:
            _context5.p = 0;
            _context5.n = 1;
            return collaborationAPI.getHistory(taskId);
          case 1:
            res = _context5.v;
            setCollaborationHistory((res === null || res === void 0 ? void 0 : res.data) || []);
            _context5.n = 3;
            break;
          case 2:
            _context5.p = 2;
            _t5 = _context5.v;
            message.error('加载协作历史失败: ' + _t5.message);
          case 3:
            return _context5.a(2);
        }
      }, _callee5, null, [[0, 2]]);
    }));
    return function loadCollaborationHistory(_x3) {
      return _ref5.apply(this, arguments);
    };
  }();

  // 应用变更
  var handleApplyChange = /*#__PURE__*/function () {
    var _ref6 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee6(change) {
      var _t6;
      return _regenerator().w(function (_context6) {
        while (1) switch (_context6.p = _context6.n) {
          case 0:
            _context6.p = 0;
            _context6.n = 1;
            return collaborationAPI.applyChange(selectedTaskId, change);
          case 1:
            message.success('变更已应用');
            loadCollaborationContent(selectedTaskId);
            loadCollaborationHistory(selectedTaskId);
            _context6.n = 3;
            break;
          case 2:
            _context6.p = 2;
            _t6 = _context6.v;
            message.error('应用变更失败: ' + _t6.message);
          case 3:
            return _context6.a(2);
        }
      }, _callee6, null, [[0, 2]]);
    }));
    return function handleApplyChange(_x4) {
      return _ref6.apply(this, arguments);
    };
  }();

  // 保存内容
  var handleSaveContent = /*#__PURE__*/function () {
    var _ref7 = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee7() {
      var newContent, change;
      return _regenerator().w(function (_context7) {
        while (1) switch (_context7.n) {
          case 0:
            if (contentRef.current) {
              _context7.n = 1;
              break;
            }
            return _context7.a(2);
          case 1:
            newContent = contentRef.current.value || collaborationContent;
            change = {
              type: 'UPDATE',
              content: newContent,
              timestamp: Date.now()
            };
            _context7.n = 2;
            return handleApplyChange(change);
          case 2:
            return _context7.a(2);
        }
      }, _callee7);
    }));
    return function handleSaveContent() {
      return _ref7.apply(this, arguments);
    };
  }();
  useEffect(function () {
    loadTasks();
  }, []);
  useEffect(function () {
    if (selectedTaskId && collaborationSession) {
      // 定期刷新协作内容
      var interval = setInterval(function () {
        loadCollaborationContent(selectedTaskId);
      }, 2000); // 每2秒刷新一次

      return function () {
        return clearInterval(interval);
      };
    }
  }, [selectedTaskId, collaborationSession]);
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
        icon: /*#__PURE__*/React__default.createElement(TeamOutlined$1, null),
        onClick: function onClick() {
          setSelectedTaskId(record.id);
          setJoinModalVisible(true);
        }
      }, "\u534F\u4F5C\u7F16\u8F91");
    }
  }];
  var historyColumns = [{
    title: '操作类型',
    dataIndex: 'type',
    key: 'type',
    render: function render(type) {
      var typeMap = {
        'UPDATE': {
          color: 'blue',
          text: '更新'
        },
        'LOCK': {
          color: 'orange',
          text: '锁定'
        },
        'UNLOCK': {
          color: 'green',
          text: '解锁'
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
    title: '操作者',
    dataIndex: 'userId',
    key: 'userId'
  }, {
    title: '操作时间',
    dataIndex: 'timestamp',
    key: 'timestamp',
    render: function render(time) {
      return time ? new Date(time).toLocaleString() : '-';
    }
  }, {
    title: '描述',
    dataIndex: 'description',
    key: 'description',
    ellipsis: true
  }];
  var selectedTask = tasks.find(function (t) {
    return t.id === selectedTaskId;
  });
  collaborationContent ? collaborationContent.split('\n') : [];
  return /*#__PURE__*/React__default.createElement("div", {
    className: "airdrop-collaboration"
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
    }
  }))), /*#__PURE__*/React__default.createElement(Col, {
    span: 16
  }, collaborationSession ? /*#__PURE__*/React__default.createElement(React__default.Fragment, null, /*#__PURE__*/React__default.createElement(Card, {
    title: /*#__PURE__*/React__default.createElement("span", null, /*#__PURE__*/React__default.createElement(TeamOutlined$1, null), " \u534F\u4F5C\u7F16\u8F91 - ", (selectedTask === null || selectedTask === void 0 ? void 0 : selectedTask.taskName) || selectedTaskId),
    extra: /*#__PURE__*/React__default.createElement(Space, null, /*#__PURE__*/React__default.createElement(Button, {
      icon: /*#__PURE__*/React__default.createElement(SaveOutlined, null),
      type: "primary",
      onClick: handleSaveContent
    }, "\u4FDD\u5B58"), /*#__PURE__*/React__default.createElement(Button, {
      icon: /*#__PURE__*/React__default.createElement(HistoryOutlined, null),
      onClick: function onClick() {
        loadCollaborationHistory(selectedTaskId);
        setHistoryModalVisible(true);
      }
    }, "\u5386\u53F2\u8BB0\u5F55"), /*#__PURE__*/React__default.createElement(Button, {
      danger: true,
      onClick: handleLeaveSession
    }, "\u79BB\u5F00\u4F1A\u8BDD")),
    style: {
      marginBottom: 16
    }
  }, /*#__PURE__*/React__default.createElement(Alert, {
    message: "\u534F\u4F5C\u7F16\u8F91",
    description: "\u591A\u4EBA\u53EF\u4EE5\u540C\u65F6\u7F16\u8F91\u811A\u672C\u3002\u9501\u5B9A\u884C\u53EF\u4EE5\u9632\u6B62\u51B2\u7A81\u3002",
    type: "info",
    showIcon: true,
    style: {
      marginBottom: 16
    }
  }), /*#__PURE__*/React__default.createElement(Descriptions, {
    column: 2,
    size: "small",
    style: {
      marginBottom: 16
    }
  }, /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u53C2\u4E0E\u8005"
  }, /*#__PURE__*/React__default.createElement(Space, null, participants.map(function (p, index) {
    return /*#__PURE__*/React__default.createElement(Tag, {
      key: index,
      icon: /*#__PURE__*/React__default.createElement(UserOutlined$1, null)
    }, p.userId || p.name || '用户' + (index + 1));
  }))), /*#__PURE__*/React__default.createElement(Descriptions.Item, {
    label: "\u5DF2\u9501\u5B9A\u884C"
  }, Object.keys(lockedLines).length, " \u884C")), /*#__PURE__*/React__default.createElement("div", {
    style: {
      border: '1px solid #d9d9d9',
      borderRadius: 4
    }
  }, /*#__PURE__*/React__default.createElement("div", {
    style: {
      background: '#fafafa',
      padding: '8px 16px',
      borderBottom: '1px solid #d9d9d9',
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center'
    }
  }, /*#__PURE__*/React__default.createElement("span", null, "\u811A\u672C\u5185\u5BB9"), /*#__PURE__*/React__default.createElement(Space, null, /*#__PURE__*/React__default.createElement(Button, {
    size: "small",
    icon: /*#__PURE__*/React__default.createElement(SaveOutlined, null),
    onClick: handleSaveContent
  }, "\u4FDD\u5B58"))), /*#__PURE__*/React__default.createElement(TextArea, {
    ref: contentRef,
    value: collaborationContent,
    onChange: function onChange(e) {
      return setCollaborationContent(e.target.value);
    },
    rows: 20,
    style: {
      fontFamily: 'monospace',
      fontSize: 12,
      border: 'none',
      borderRadius: 0
    },
    placeholder: "\u8F93\u5165\u811A\u672C\u5185\u5BB9..."
  })), Object.keys(lockedLines).length > 0 && /*#__PURE__*/React__default.createElement(Card, {
    size: "small",
    style: {
      marginTop: 16
    }
  }, /*#__PURE__*/React__default.createElement("div", {
    style: {
      marginBottom: 8,
      fontWeight: 500
    }
  }, "\u5DF2\u9501\u5B9A\u884C\uFF1A"), Object.entries(lockedLines).map(function (_ref0) {
    var _ref1 = _slicedToArray(_ref0, 2),
      lineNumber = _ref1[0],
      userId = _ref1[1];
    return /*#__PURE__*/React__default.createElement(Tag, {
      key: lineNumber,
      style: {
        marginBottom: 4
      }
    }, "\u884C ", lineNumber, " - ", userId);
  })))) : /*#__PURE__*/React__default.createElement(Card, null, /*#__PURE__*/React__default.createElement(Alert, {
    message: "\u672A\u52A0\u5165\u534F\u4F5C\u4F1A\u8BDD",
    description: "\u9009\u62E9\u4E00\u4E2A\u4EFB\u52A1\u5E76\u52A0\u5165\u534F\u4F5C\u4F1A\u8BDD\u5F00\u59CB\u591A\u4EBA\u534F\u4F5C\u7F16\u8F91",
    type: "info",
    showIcon: true
  })))), /*#__PURE__*/React__default.createElement(Modal, {
    title: "\u52A0\u5165\u534F\u4F5C\u4F1A\u8BDD",
    visible: joinModalVisible,
    onCancel: function onCancel() {
      setJoinModalVisible(false);
      setSelectedTaskId(null);
    },
    onOk: function onOk() {
      if (selectedTaskId) {
        handleJoinSession(selectedTaskId);
        setJoinModalVisible(false);
      }
    },
    width: 500
  }, /*#__PURE__*/React__default.createElement(Alert, {
    message: "\u534F\u4F5C\u7F16\u8F91\u8BF4\u660E",
    description: "\u52A0\u5165\u534F\u4F5C\u4F1A\u8BDD\u540E\uFF0C\u53EF\u4EE5\u4E0E\u5176\u4ED6\u7528\u6237\u540C\u65F6\u7F16\u8F91\u811A\u672C\u3002\u7CFB\u7EDF\u4F1A\u81EA\u52A8\u540C\u6B65\u53D8\u66F4\u3002",
    type: "info",
    showIcon: true
  }), selectedTaskId && /*#__PURE__*/React__default.createElement("div", {
    style: {
      marginTop: 16
    }
  }, /*#__PURE__*/React__default.createElement("strong", null, "\u4EFB\u52A1\uFF1A"), (selectedTask === null || selectedTask === void 0 ? void 0 : selectedTask.taskName) || selectedTaskId)), /*#__PURE__*/React__default.createElement(Modal, {
    title: "\u534F\u4F5C\u5386\u53F2",
    visible: historyModalVisible,
    onCancel: function onCancel() {
      setHistoryModalVisible(false);
    },
    footer: [/*#__PURE__*/React__default.createElement(Button, {
      key: "close",
      onClick: function onClick() {
        return setHistoryModalVisible(false);
      }
    }, "\u5173\u95ED")],
    width: 800
  }, /*#__PURE__*/React__default.createElement(Table, {
    columns: historyColumns,
    dataSource: collaborationHistory,
    rowKey: function rowKey(record, index) {
      return "".concat(record.timestamp, "_").concat(index);
    },
    pagination: {
      pageSize: 10
    },
    locale: {
      emptyText: '暂无协作历史'
    }
  })));
};

export { AirdropCollaboration, AirdropCollaboration as default };
