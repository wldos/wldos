const React = window.React;
const React__default = window.React;
const { Typography, Card, Space, Tag } = window.antd;
import { A as AntdIcon, _ as _objectSpread2 } from './AntdIcon-9c99e1c1.js';

// This icon file is generated automatically.
var AppstoreOutlined$2 = { "icon": { "tag": "svg", "attrs": { "viewBox": "64 64 896 896", "focusable": "false" }, "children": [{ "tag": "path", "attrs": { "d": "M464 144H160c-8.8 0-16 7.2-16 16v304c0 8.8 7.2 16 16 16h304c8.8 0 16-7.2 16-16V160c0-8.8-7.2-16-16-16zm-52 268H212V212h200v200zm452-268H560c-8.8 0-16 7.2-16 16v304c0 8.8 7.2 16 16 16h304c8.8 0 16-7.2 16-16V160c0-8.8-7.2-16-16-16zm-52 268H612V212h200v200zM464 544H160c-8.8 0-16 7.2-16 16v304c0 8.8 7.2 16 16 16h304c8.8 0 16-7.2 16-16V560c0-8.8-7.2-16-16-16zm-52 268H212V612h200v200zm452-268H560c-8.8 0-16 7.2-16 16v304c0 8.8 7.2 16 16 16h304c8.8 0 16-7.2 16-16V560c0-8.8-7.2-16-16-16zm-52 268H612V612h200v200z" } }] }, "name": "appstore", "theme": "outlined" };
var AppstoreOutlinedSvg = AppstoreOutlined$2;

var AppstoreOutlined = function AppstoreOutlined(props, ref) {
  return /*#__PURE__*/React.createElement(AntdIcon, _objectSpread2(_objectSpread2({}, props), {}, {
    ref: ref,
    icon: AppstoreOutlinedSvg
  }));
};
var RefIcon = /*#__PURE__*/React.forwardRef(AppstoreOutlined);
var AppstoreOutlined$1 = RefIcon;

/**
 * 插件演示首页组件
 */
var Home = function Home() {
  // 在组件内部解构，确保 Typography 已经从 window.antd 获取
  var Title = Typography.Title,
    Paragraph = Typography.Paragraph;
  return /*#__PURE__*/React__default.createElement("div", {
    style: {
      padding: 24
    }
  }, /*#__PURE__*/React__default.createElement(Card, null, /*#__PURE__*/React__default.createElement(Space, {
    direction: "vertical",
    size: "large",
    style: {
      width: '100%'
    }
  }, /*#__PURE__*/React__default.createElement("div", null, /*#__PURE__*/React__default.createElement(Title, {
    level: 2
  }, /*#__PURE__*/React__default.createElement(AppstoreOutlined$1, {
    style: {
      marginRight: 8
    }
  }), "\u63D2\u4EF6\u5F00\u53D1\u6F14\u793A"), /*#__PURE__*/React__default.createElement(Tag, {
    color: "blue"
  }, "v1.0.0")), /*#__PURE__*/React__default.createElement(Paragraph, null, "\u8FD9\u662F\u4E00\u4E2A WLDOS \u63D2\u4EF6\u5F00\u53D1\u6F14\u793A\u9879\u76EE\uFF0C\u4F5C\u4E3A\u63D2\u4EF6\u5F00\u53D1\u8005\u7684\u57FA\u7840\u6846\u67B6\u548C\u53C2\u8003\u793A\u4F8B\u3002"), /*#__PURE__*/React__default.createElement(Card, {
    title: "\u529F\u80FD\u7279\u6027",
    size: "small"
  }, /*#__PURE__*/React__default.createElement("ul", null, /*#__PURE__*/React__default.createElement("li", null, "\u5B8C\u6574\u7684\u63D2\u4EF6\u7ED3\u6784\u793A\u4F8B"), /*#__PURE__*/React__default.createElement("li", null, "\u540E\u7AEF Controller\u3001Service\u3001Entity \u793A\u4F8B"), /*#__PURE__*/React__default.createElement("li", null, "\u524D\u7AEF React \u7EC4\u4EF6\u793A\u4F8B"), /*#__PURE__*/React__default.createElement("li", null, "\u83DC\u5355\u548C\u8DEF\u7531\u914D\u7F6E\u793A\u4F8B"), /*#__PURE__*/React__default.createElement("li", null, "Hook \u673A\u5236\u4F7F\u7528\u793A\u4F8B"), /*#__PURE__*/React__default.createElement("li", null, "API \u8C03\u7528\u793A\u4F8B"))), /*#__PURE__*/React__default.createElement(Card, {
    title: "\u5FEB\u901F\u5F00\u59CB",
    size: "small"
  }, /*#__PURE__*/React__default.createElement("ol", null, /*#__PURE__*/React__default.createElement("li", null, "\u590D\u5236\u6B64\u63D2\u4EF6\u4F5C\u4E3A\u57FA\u7840\u6846\u67B6"), /*#__PURE__*/React__default.createElement("li", null, "\u4FEE\u6539 plugin.yml \u4E2D\u7684\u63D2\u4EF6\u4FE1\u606F"), /*#__PURE__*/React__default.createElement("li", null, "\u6839\u636E\u4E1A\u52A1\u9700\u6C42\u4FEE\u6539 Java \u4EE3\u7801"), /*#__PURE__*/React__default.createElement("li", null, "\u6839\u636E\u4E1A\u52A1\u9700\u6C42\u4FEE\u6539\u524D\u7AEF\u7EC4\u4EF6"), /*#__PURE__*/React__default.createElement("li", null, "\u8FD0\u884C ", /*#__PURE__*/React__default.createElement("code", null, "mvn clean package"), " \u6784\u5EFA\u63D2\u4EF6"), /*#__PURE__*/React__default.createElement("li", null, "\u5C06\u751F\u6210\u7684 ZIP \u6587\u4EF6\u4E0A\u4F20\u5230\u7CFB\u7EDF\u5B89\u88C5"))))));
};

export { Home as default };
