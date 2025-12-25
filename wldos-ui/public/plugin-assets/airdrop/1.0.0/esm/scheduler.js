const React = window.React;
const { Card } = window.antd;

/**
 * 脚本首页组件
 */
var AirdropLayout = function AirdropLayout(_ref) {
  var children = _ref.children;
  return /*#__PURE__*/React.createElement("div", {
    className: "airdrop-layout"
  }, /*#__PURE__*/React.createElement(Card, {
    title: "\u811A\u672C\u795E\u5668",
    style: {
      margin: 16
    }
  }, children));
};

export { AirdropLayout, AirdropLayout as default };
