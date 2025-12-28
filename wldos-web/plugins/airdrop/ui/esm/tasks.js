import { _ as _slicedToArray, a as _asyncToGenerator, b as _regenerator, t as taskAPI } from './api-c9f20415.js';
const React = window.React;
const { Button, Table, message, Tag, Space, Modal } = window.antd;

var AirdropTasks = function AirdropTasks() {
  var _React$useState = React.useState([]),
    _React$useState2 = _slicedToArray(_React$useState, 2),
    tasks = _React$useState2[0],
    setTasks = _React$useState2[1];
  var _React$useState3 = React.useState(false),
    _React$useState4 = _slicedToArray(_React$useState3, 2),
    loading = _React$useState4[0],
    setLoading = _React$useState4[1];

  // 加载任务列表
  var loadTasks = /*#__PURE__*/function () {
    var _ref = _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee() {
      var _res$data, res, _t;
      return _regenerator().w(function (_context) {
        while (1) switch (_context.p = _context.n) {
          case 0:
            _context.p = 0;
            setLoading(true);
            _context.n = 1;
            return taskAPI.getTasks();
          case 1:
            res = _context.v;
            setTasks((_res$data = res === null || res === void 0 ? void 0 : res.data) !== null && _res$data !== void 0 ? _res$data : []);
            _context.n = 3;
            break;
          case 2:
            _context.p = 2;
            _t = _context.v;
            message.error('加载任务列表失败: ' + _t.message);
          case 3:
            _context.p = 3;
            setLoading(false);
            return _context.f(3);
          case 4:
            return _context.a(2);
        }
      }, _callee, null, [[0, 2, 3, 4]]);
    }));
    return function loadTasks() {
      return _ref.apply(this, arguments);
    };
  }();

  // 组件挂载时加载数据
  React.useEffect(function () {
    loadTasks();
  }, []);

  // 事件处理函数
  var handleCreateTask = function handleCreateTask() {
    message.info('新建任务功能开发中...');
  };
  var handleViewTask = function handleViewTask(record) {
    message.info("\u67E5\u770B\u4EFB\u52A1: ".concat(record.taskName));
  };
  var handleEditTask = function handleEditTask(record) {
    message.info("\u7F16\u8F91\u4EFB\u52A1: ".concat(record.taskName));
  };
  var handleDeleteTask = function handleDeleteTask(record) {
    Modal.confirm({
      title: '确认删除',
      content: "\u786E\u5B9A\u8981\u5220\u9664\u4EFB\u52A1 \"".concat(record.taskName, "\" \u5417\uFF1F"),
      onOk: function onOk() {
        return _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee2() {
          var _t2;
          return _regenerator().w(function (_context2) {
            while (1) switch (_context2.p = _context2.n) {
              case 0:
                _context2.p = 0;
                _context2.n = 1;
                return taskAPI.deleteTask(record.id);
              case 1:
                message.success('任务删除成功');
                loadTasks(); // 重新加载列表
                _context2.n = 3;
                break;
              case 2:
                _context2.p = 2;
                _t2 = _context2.v;
                message.error('删除任务失败: ' + _t2.message);
              case 3:
                return _context2.a(2);
            }
          }, _callee2, null, [[0, 2]]);
        }))();
      }
    });
  };
  var columns = [{
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
        },
        'stopped': {
          color: 'gray',
          text: '已停止'
        }
      };
      var statusInfo = statusMap[status] || {
        color: 'default',
        text: status
      };
      return /*#__PURE__*/React.createElement(Tag, {
        color: statusInfo.color
      }, statusInfo.text);
    }
  }, {
    title: '优先级',
    dataIndex: 'priority',
    key: 'priority'
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
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime'
  }, {
    title: '操作',
    key: 'action',
    render: function render(_, record) {
      return /*#__PURE__*/React.createElement(Space, null, /*#__PURE__*/React.createElement(Button, {
        type: "link",
        size: "small",
        onClick: function onClick() {
          return handleViewTask(record);
        }
      }, "\u67E5\u770B"), /*#__PURE__*/React.createElement(Button, {
        type: "link",
        size: "small",
        onClick: function onClick() {
          return handleEditTask(record);
        }
      }, "\u7F16\u8F91"), /*#__PURE__*/React.createElement(Button, {
        type: "link",
        size: "small",
        danger: true,
        onClick: function onClick() {
          return handleDeleteTask(record);
        }
      }, "\u5220\u9664"));
    }
  }];
  return /*#__PURE__*/React.createElement("div", {
    className: "airdrop-tasks"
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      marginBottom: 16
    }
  }, /*#__PURE__*/React.createElement(Button, {
    type: "primary",
    onClick: handleCreateTask
  }, "\u65B0\u5EFA\u4EFB\u52A1"), /*#__PURE__*/React.createElement(Button, {
    style: {
      marginLeft: 8
    },
    onClick: loadTasks
  }, "\u5237\u65B0")), /*#__PURE__*/React.createElement(Table, {
    columns: columns,
    dataSource: tasks,
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
  }));
};

export { AirdropTasks, AirdropTasks as default };
