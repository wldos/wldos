import React, { Component, Suspense } from 'react';
import { FormattedMessage, connect, history } from 'umi';
import { GridContent } from '@ant-design/pro-layout';
import { Menu, Spin } from 'antd';
import BaseView from './components/base';
import BindingView from './components/binding';
import NotificationView from './components/notification';
import SecurityView from './components/security';
import {
  resolveVisibleSettingsTabs,
  settingsTabFromQuery,
  settingsTabToQuery,
} from './settingsTabRegistry';
import { fetchCmsCreatorCapabilitiesEnabled } from '@/pages/commercial/_shared/cmsCreatorCapabilities';
import styles from './style.less';

const CreatorPayeeSettingsView = React.lazy(() => import('./components/CreatorPayeeSettingsView'));
const CreatorWechatMpSettingsView = React.lazy(() => import('./components/CreatorWechatMpSettingsView'));

const { Item } = Menu;

function buildMenuMap(cmsCreatorCapabilities) {
  const map = {};
  resolveVisibleSettingsTabs({ cmsCreatorCapabilities }).forEach((tab) => {
    const defaultMessage =
      tab.key === 'creatorPayee' ? 'Creator payout' : tab.key;
    map[tab.key] = (
      <FormattedMessage id={tab.i18nId} defaultMessage={defaultMessage} />
    );
  });
  return map;
}

function initialSelectKeyFromLocation() {
  const fromQuery = settingsTabFromQuery(history.location?.query?.tab);
  return fromQuery || 'base';
}

class Settings extends Component {
  main = undefined;

  constructor(props) {
    super(props);
    this.state = {
      mode: 'inline',
      menuMap: buildMenuMap(false),
      selectKey: initialSelectKeyFromLocation(),
      cmsCreatorCapabilities: false,
      capabilitiesLoaded: false,
    };
  }

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'account/fetchCurrent',
    });
    fetchCmsCreatorCapabilitiesEnabled().then((enabled) => {
      const cmsCreatorCapabilities = enabled === true;
      const menuMap = buildMenuMap(cmsCreatorCapabilities);
      let selectKey = this.state.selectKey;
      if (!menuMap[selectKey]) {
        selectKey = 'base';
      }
      this.setState({ cmsCreatorCapabilities, capabilitiesLoaded: true, menuMap, selectKey });
    });
    window.addEventListener('resize', this.resize);
    this.unlistenHistory = history.listen(({ location }) => {
      const fromQuery = settingsTabFromQuery(location?.query?.tab);
      if (fromQuery == null || fromQuery === this.state.selectKey) {
        return;
      }
      this.setState({ selectKey: fromQuery });
    });
    this.resize();
  }

  componentWillUnmount() {
    window.removeEventListener('resize', this.resize);
    if (this.unlistenHistory) {
      this.unlistenHistory();
    }
  }

  getMenu = () => {
    const { menuMap } = this.state;
    return Object.keys(menuMap).map((item) => <Item key={item}>{menuMap[item]}</Item>);
  };

  getRightTitle = () => {
    const { selectKey, menuMap } = this.state;
    return menuMap[selectKey];
  };

  selectKey = (key) => {
    this.setState({ selectKey: key });
    const queryTab = settingsTabToQuery(key);
    history.replace({
      pathname: '/account/settings',
      query: queryTab ? { tab: queryTab } : {},
    });
  };

  resize = () => {
    if (!this.main) {
      return;
    }

    requestAnimationFrame(() => {
      if (!this.main) {
        return;
      }

      let mode = 'inline';
      const { offsetWidth } = this.main;

      if (this.main.offsetWidth < 641 && offsetWidth > 400) {
        mode = 'horizontal';
      }

      if (window.innerWidth < 768 && offsetWidth > 400) {
        mode = 'horizontal';
      }

      this.setState({
        mode,
      });
    });
  };

  renderChildren = (u) => {
    const { selectKey } = this.state;

    switch (selectKey) {
      case 'base':
        return <BaseView />;

      case 'security':
        return <SecurityView currentUser={u}/>;

      case 'binding':
        return <BindingView currentUser={u}/>;

      case 'notification':
        return <NotificationView currentUser={u}/>;

      case 'creatorPayee':
        return (
          <Suspense fallback={<Spin />}>
            <CreatorPayeeSettingsView />
          </Suspense>
        );

      case 'creatorWechatMp':
        return (
          <Suspense fallback={<Spin />}>
            <CreatorWechatMpSettingsView />
          </Suspense>
        );

      default:
        break;
    }

    return null;
  };

  render() {
    const { currentUser } = this.props;

    if (!currentUser.id) {
      return '';
    }

    const { mode, selectKey } = this.state;
    return (
      <GridContent>
        <div
          className={styles.main}
          ref={(ref) => {
            if (ref) {
              this.main = ref;
            }
          }}
        >
          <div className={styles.leftMenu}>
            <Menu mode={mode} selectedKeys={[selectKey]} onClick={({ key }) => this.selectKey(key)}>
              {this.getMenu()}
            </Menu>
          </div>
          <div className={styles.right}>
            <div className={styles.title}>{this.getRightTitle()}</div>
            {this.renderChildren(currentUser)}
          </div>
        </div>
      </GridContent>
    );
  }
}

export default connect(({ account }) => ({
  currentUser: account.currentUser,
}))(Settings);
