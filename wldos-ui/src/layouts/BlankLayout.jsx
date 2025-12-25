import React from 'react';
import { Inspector } from 'react-dev-inspector';

const InspectorWrapper = process.env.NODE_ENV === 'development' ? Inspector : React.Fragment;

// 顶层稳定错误边界（模块顶层定义，避免热更导致子树重挂载）
class RootErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }
  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }
  handleReload = () => {
    // 简单恢复：整页刷新
    window.location.reload();
  };
  render() {
    if (this.state.hasError) {
      return (
        <div style={{ padding: 24 }}>
          <h3>应用发生错误</h3>
          <div style={{ color: '#999', margin: '8px 0 16px' }}>
            {String(this.state.error || 'Unknown error')}
          </div>
          <button onClick={this.handleReload} style={{ padding: '6px 12px' }}>刷新重试</button>
        </div>
      );
    }
    return this.props.children;
  }
}

const Layout = ({ children }) => {
  return (
    <RootErrorBoundary>
      <InspectorWrapper>{children}</InspectorWrapper>
    </RootErrorBoundary>
  );
};

export default Layout;
