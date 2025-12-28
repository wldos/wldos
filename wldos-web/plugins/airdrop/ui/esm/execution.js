import { _ as _slicedToArray, a as _asyncToGenerator, b as _regenerator, e as executionAPI, t as taskAPI } from './api-c9f20415.js';
const React = window.React;
const { Button, Table, Tag, Space, message, Modal } = window.antd;

var AirdropExecution = function AirdropExecution() {
  var _React$useState = React.useState([]),
    _React$useState2 = _slicedToArray(_React$useState, 2),
    executions = _React$useState2[0],
    setExecutions = _React$useState2[1];
  var _React$useState3 = React.useState(false),
    _React$useState4 = _slicedToArray(_React$useState3, 2),
    loading = _React$useState4[0],
    setLoading = _React$useState4[1];

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

  // 组件挂载时加载数据
  React.useEffect(function () {
    loadExecutions();
  }, []);

  // 事件处理函数
  var handleRefresh = function handleRefresh() {
    loadExecutions();
  };
  var handleViewDetail = function handleViewDetail(record) {
    message.info("\u67E5\u770B\u8BE6\u60C5: ".concat(record.taskName));
  };
  var handleReExecute = function handleReExecute(record) {
    Modal.confirm({
      title: '确认重新执行',
      content: "\u786E\u5B9A\u8981\u91CD\u65B0\u6267\u884C\u4EFB\u52A1 \"".concat(record.taskName, "\" \u5417\uFF1F"),
      onOk: function onOk() {
        return _asyncToGenerator(/*#__PURE__*/_regenerator().m(function _callee2() {
          var _t2;
          return _regenerator().w(function (_context2) {
            while (1) switch (_context2.p = _context2.n) {
              case 0:
                _context2.p = 0;
                _context2.n = 1;
                return taskAPI.executeTask(record.taskId);
              case 1:
                message.success('任务重新执行中...');
                loadExecutions(); // 重新加载记录
                _context2.n = 3;
                break;
              case 2:
                _context2.p = 2;
                _t2 = _context2.v;
                message.error('重新执行失败: ' + _t2.message);
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
      return /*#__PURE__*/React.createElement(Tag, {
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
      return /*#__PURE__*/React.createElement(Space, null, /*#__PURE__*/React.createElement(Button, {
        type: "link",
        size: "small",
        onClick: function onClick() {
          return handleViewDetail(record);
        }
      }, "\u67E5\u770B\u8BE6\u60C5"), /*#__PURE__*/React.createElement(Button, {
        type: "link",
        size: "small",
        onClick: function onClick() {
          return handleReExecute(record);
        }
      }, "\u91CD\u65B0\u6267\u884C"));
    }
  }];
  return /*#__PURE__*/React.createElement("div", {
    className: "airdrop-execution"
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      marginBottom: 16
    }
  }, /*#__PURE__*/React.createElement(Button, {
    type: "primary",
    onClick: handleRefresh
  }, "\u5237\u65B0")), /*#__PURE__*/React.createElement(Table, {
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
  }));
};

export { AirdropExecution, AirdropExecution as default };
