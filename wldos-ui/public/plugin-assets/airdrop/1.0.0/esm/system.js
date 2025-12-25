const React = window.React;
const { Typography, Card, Space, Button, message } = window.antd;

/**
 * 系统管理组件
 */
var Text = Typography.Text;
var AirdropSystem = function AirdropSystem() {
  return /*#__PURE__*/React.createElement("div", {
    className: "airdrop-system"
  }, /*#__PURE__*/React.createElement(Card, {
    title: "\u7CFB\u7EDF\u7BA1\u7406",
    style: {
      margin: 16
    }
  }, /*#__PURE__*/React.createElement(Space, {
    direction: "vertical",
    size: "large",
    style: {
      width: '100%'
    }
  }, /*#__PURE__*/React.createElement("div", null, /*#__PURE__*/React.createElement(Text, {
    strong: true
  }, "\u7CFB\u7EDF\u914D\u7F6E"), /*#__PURE__*/React.createElement("br", null), /*#__PURE__*/React.createElement(Text, {
    type: "secondary"
  }, "\u7CFB\u7EDF\u7BA1\u7406\u529F\u80FD\u5F00\u53D1\u4E2D...")), /*#__PURE__*/React.createElement(Button, {
    type: "primary",
    onClick: function onClick() {
      return message.info('系统配置功能开发中...');
    }
  }, "\u7CFB\u7EDF\u914D\u7F6E"))));
};

export { AirdropSystem, AirdropSystem as default };
