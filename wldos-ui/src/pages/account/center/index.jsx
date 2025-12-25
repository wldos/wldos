import { PlusOutlined, HomeOutlined, ContactsOutlined, ClusterOutlined } from '@ant-design/icons';
import { Avatar, Card, Col, Divider, Input, Row, Tag } from 'antd';
import React, { Component, useState, useRef } from 'react';
import { GridContent } from '@ant-design/pro-layout';
import { Link, connect } from 'umi';
import InfoList from './components/InfoList';
import styles from './Center.less';
import Articles from "@/pages/account/center/components/Articles";
import {saveTags} from "@/pages/account/center/service";

const operationTabList = [
  {
    key: 'info',
    tab: (
      <span>
        我的信息{' '}
        <span
          style={{
            fontSize: 14,
          }}
        >
        </span>
      </span>
    ),
  },
  {
    key: 'book',
    tab: (
      <span>
        我的作品{' '}
        <span
          style={{
            fontSize: 14,
          }}
        >
        </span>
      </span>
    ),
  },
  {
    key: 'applications',
    tab: (
      <span>
        我关注的{' '}
        <span
          style={{
            fontSize: 14,
          }}
        >
        </span>
      </span>
    ),
  },
  {
    key: 'projects',
    tab: (
      <span>
        我喜欢的{' '}
        <span
          style={{
            fontSize: 14,
          }}
        >
        </span>
      </span>
    ),
  },
];

const TagList = ({ tags }) => {
  const ref = useRef(null);
  const [newTags, setNewTags] = useState([]);
  const [inputVisible, setInputVisible] = useState(false);
  const [inputValue, setInputValue] = useState('');

  const showInput = () => {
    setInputVisible(true);

    if (ref.current) {
      // eslint-disable-next-line no-unused-expressions
      ref.current?.focus();
    }
  };

  const handleInputChange = (e) => { // 限制标签最大8个字符长度
    setInputValue(e.target.value.slice(0, 8));
  };

  const delTag = async (key) => {
    let tempsTags = [];

    const newTag = newTags.filter(tag => tag.key !== key);
    const oldTag = tags.filter(tag => tag.key !== key);

    if (newTag.length > 0) {
      setNewTags(newTag);
      tempsTags = [...tags, ...newTag];
    } else {
      tempsTags = [...oldTag, ...newTags];
    }

    // 后台保存
    await saveTags(tempsTags);
  };

  const handleInputConfirm = async () => {
    const tempsTags = [...tags, ...newTags];

    if (inputValue && tempsTags.filter((tag) => tag.label === inputValue).length === 0) {

      const newTag = {
        key: `tag-${tempsTags.length}`,
          label: inputValue,
      };
      tempsTags.push(newTag);

      // 后台保存
      await saveTags(tempsTags);

      setNewTags([...newTags, newTag]);
    }

    setInputVisible(false);
    setInputValue('');
  };

  return (
    <div className={styles.tags}>
      <div className={styles.tagsTitle}>标签</div>
      {(tags || []).concat(newTags).map((item) => (
        <Tag closable onClose={() => delTag(item.key)} key={item.key}>{item.label}</Tag>
      ))}
      {inputVisible && (
        <Input
          ref={ref}
          type="text"
          size="small"
          style={{
            width: 78,
          }}
          value={inputValue}
          onChange={handleInputChange}
          onBlur={handleInputConfirm}
          onPressEnter={handleInputConfirm}
        />
      )}
      {!inputVisible && (
        <Tag
          onClick={showInput}
          style={{
            borderStyle: 'dashed',
          }}
        >
          <PlusOutlined />
        </Tag>
      )}
    </div>
  );
};

class Center extends Component {
  state = {
    tabKey: 'info',
  };

  input = undefined;

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'account/fetchCurrent',
    });

    this.queryCategory(dispatch);
  }

  queryCategory = (dispatch) => {
    dispatch({
      type: 'bookSpace/fetchCategory',
    });
  };

  queryTag = (dispatch) => {
    dispatch({
      type: 'bookSpace/fetchTag',
    });
  };

  onTabChange = (key) => {
    this.setState({
      tabKey: key,
    });
  };

  renderChildrenByTabKey = (tabKey, u, categories, tagData) => {
    if (tabKey === 'info') { // 展现发布的信息列表
      return <InfoList count={16} id={u.id} pubType="info" categoryList={categories} tagData={tagData}/>;
    }

    if (tabKey === 'book') {
      return <InfoList count={16} id={u.id} categoryList={categories} tagData={tagData}/>;
    }

    if (tabKey === 'projects') {
      return <Articles count={16} url={`/archives-star/${u.id}.html`}/>;
    }

    if (tabKey === 'applications') {
      return <Articles count={16} url={`/archives-like/${u.id}.html`}/>;
    }

    return null;
  };

  renderUserInfo = (currentUser) => (
    <div className={styles.detail}>
      <p>
        <ContactsOutlined
          style={{
            marginRight: 8,
          }}
        />
        {currentUser.title}
      </p>
      <p>
        <ClusterOutlined
          style={{
            marginRight: 8,
          }}
        />
        {currentUser.company}
      </p>
      <p>
        <HomeOutlined
          style={{
            marginRight: 8,
          }}
        />
        {
          (
            currentUser.geographic || {
              province: {
                label: '',
              },
            }
          ).province.label
        }
        {
          (
            currentUser.geographic || {
              city: {
                label: '',
              },
            }
          ).city.label
        }
      </p>
    </div>
  );

  render() {
    const { tabKey } = this.state;
    const { currentUser = {}, currentUserLoading, categories, tagData } = this.props;
    if (!currentUser.id)
      return '';

    const dataLoading = currentUserLoading || !(currentUser && Object.keys(currentUser).length);
    return (
      <GridContent>
        <Row gutter={24}>
          <Col lg={7} md={24}>
            <Card
              bordered={false}
              style={{
                marginBottom: 24,
              }}
              loading={dataLoading}
            >
              {!dataLoading && (
                <div>
                  <div className={styles.avatarHolder}>
                    <img alt="" src={currentUser.avatar} />
                    <div className={styles.name}>{currentUser.nickname}</div>
                    <div>{currentUser.remark}</div>
                  </div>
                  {this.renderUserInfo(currentUser)}
                  <Divider dashed />
                  <TagList tags={currentUser.tags || []} />
                  <Divider
                    style={{
                      marginTop: 16,
                    }}
                    dashed
                  />
                  <div className={styles.team}>
                    <div className={styles.teamTitle}>类别</div>
                    <Row gutter={36}>
                      {currentUser.group &&
                        currentUser.group.map((item) => (
                          <Col key={item.id} lg={24} xl={12}>
                            <Link to={'#'}>
                              <Avatar size="small" src={item.orgLogo} />
                              {item.orgName}
                            </Link>
                          </Col>
                        ))}
                    </Row>
                  </div>
                </div>
              )}
            </Card>
          </Col>
          <Col lg={17} md={24}>
            <Card
              className={styles.tabsCard}
              bordered={false}
              tabList={operationTabList}
              activeTabKey={tabKey}
              onTabChange={this.onTabChange}
            >
              {this.renderChildrenByTabKey(tabKey, currentUser, categories, tagData)}
            </Card>
          </Col>
        </Row>
      </GridContent>
    );
  }
}

export default connect(({ loading, account, bookSpace }) => ({
  currentUser: account.currentUser,
  currentUserLoading: loading.effects['account/fetchCurrent'],
  categories: bookSpace.categories,
  tagData: bookSpace.tagData,
}))(Center);
