import React from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import {
  Card,
  Typography,
  Steps,
  Alert,
  Space,
  Divider,
  List,
  Tag,
  Button,
} from 'antd';
import {
  GithubOutlined,
  FileTextOutlined,
  CodeOutlined,
  CheckCircleOutlined,
  InfoCircleOutlined,
  LinkOutlined,
  QuestionCircleOutlined,
} from '@ant-design/icons';
import { FormattedMessage } from 'umi';
import './index.less';

const { Title, Paragraph, Text } = Typography;
const { Step } = Steps;

const PluginContribute = () => {
  return (
    <PageContainer
      title={
        <FormattedMessage
          id="store.contribute.title"
          defaultMessage="Plugin contribution guide"
        />
      }
      content={
        <FormattedMessage
          id="store.contribute.content"
          defaultMessage="Learn how to contribute your plugin to the WLDOS plugin ecosystem."
        />
      }
    >
      <div className="plugin-contribute-container">
        <Card
          title={
            <Space>
              <InfoCircleOutlined />
              <span>
                <FormattedMessage
                  id="store.contribute.overview.title"
                  defaultMessage="Contribution overview"
                />
              </span>
            </Space>
          }
          style={{ marginBottom: 24 }}
        >
          <Alert
            message={
              <FormattedMessage
                id="store.contribute.overview.alert.title"
                defaultMessage="The WLDOS plugin ecosystem adopts an open-source contribution model"
              />
            }
            description={
              <div>
                <Paragraph>
                  <FormattedMessage
                    id="store.contribute.overview.alert.p1"
                    defaultMessage="WLDOS is an open-source platform. To maintain the quality and security of the plugin ecosystem, all plugin contributions must go through the official review process."
                  />
                  <FormattedMessage
                    id="store.contribute.overview.alert.p2"
                    defaultMessage="Developers can submit plugins via GitHub/GitLab and other code hosting platforms. After official review, plugins will be merged into the official repository and released with WLDOS."
                  />
                </Paragraph>
                <Paragraph>
                  <Text strong>
                    <FormattedMessage
                      id="store.contribute.overview.alert.tipPrefix"
                      defaultMessage="Important reminder:"
                    />
                  </Text>{' '}
                  <FormattedMessage
                    id="store.contribute.overview.alert.tipText"
                    defaultMessage="Users of third-party WLDOS instances are advised to use officially approved plugins and not to add plugins directly. This ensures the security, stability, and consistency of the plugin ecosystem."
                  />
                </Paragraph>
              </div>
            }
            type="info"
            showIcon
          />
        </Card>

        <Card
          title={
            <Space>
              <CodeOutlined />
              <span>
                <FormattedMessage
                  id="store.contribute.flow.title"
                  defaultMessage="Contribution process"
                />
              </span>
            </Space>
          }
          style={{ marginBottom: 24 }}
        >
          <Steps direction="vertical" size="small">
            <Step
              title={
                <FormattedMessage
                  id="store.contribute.flow.step1.title"
                  defaultMessage="Develop plugin"
                />
              }
              description={
                <div>
                  <Paragraph>
                    <FormattedMessage
                      id="store.contribute.flow.step1.p1"
                      defaultMessage="Develop your plugin according to the WLDOS plugin development guidelines, and make sure:"
                    />
                  </Paragraph>
                  <List
                    size="small"
                    dataSource={[
                      <FormattedMessage
                        id="store.contribute.flow.step1.item1"
                        key="1"
                        defaultMessage="Plugin functionality is complete and can run normally."
                      />,
                      <FormattedMessage
                        id="store.contribute.flow.step1.item2"
                        key="2"
                        defaultMessage="Code quality is good and follows coding conventions."
                      />,
                      <FormattedMessage
                        id="store.contribute.flow.step1.item3"
                        key="3"
                        defaultMessage="Includes a complete configuration file plugin.yml."
                      />,
                      <FormattedMessage
                        id="store.contribute.flow.step1.item4"
                        key="4"
                        defaultMessage="Provides a clear README document."
                      />,
                      <FormattedMessage
                        id="store.contribute.flow.step1.item5"
                        key="5"
                        defaultMessage="Passes basic functional tests."
                      />,
                    ]}
                    renderItem={(item) => (
                      <List.Item>
                        <CheckCircleOutlined style={{ color: '#52c41a', marginRight: 8 }} />
                        {item}
                      </List.Item>
                    )}
                  />
                </div>
              }
              icon={<CodeOutlined />}
            />
            <Step
              title={
                <FormattedMessage
                  id="store.contribute.flow.step2.title"
                  defaultMessage="Submit to code repository"
                />
              }
              description={
                <div>
                  <Paragraph>
                    <FormattedMessage
                      id="store.contribute.flow.step2.p1"
                      defaultMessage="Submit plugin code to a code hosting platform (GitHub/GitLab, etc.):"
                    />
                  </Paragraph>
                  <List
                    size="small"
                    dataSource={[
                      <FormattedMessage
                        id="store.contribute.flow.step2.item1"
                        key="1"
                        defaultMessage="Create a plugin project repository."
                      />,
                      <FormattedMessage
                        id="store.contribute.flow.step2.item2"
                        key="2"
                        defaultMessage="Upload plugin source code and build artifacts."
                      />,
                      <FormattedMessage
                        id="store.contribute.flow.step2.item3"
                        key="3"
                        defaultMessage="Write a detailed README documentation."
                      />,
                      <FormattedMessage
                        id="store.contribute.flow.step2.item4"
                        key="4"
                        defaultMessage="Add the necessary license files."
                      />,
                      <FormattedMessage
                        id="store.contribute.flow.step2.item5"
                        key="5"
                        defaultMessage="Create a release version (optional)."
                      />,
                    ]}
                    renderItem={(item) => (
                      <List.Item>
                        <CheckCircleOutlined style={{ color: '#52c41a', marginRight: 8 }} />
                        {item}
                      </List.Item>
                    )}
                  />
                </div>
              }
              icon={<GithubOutlined />}
            />
            <Step
              title={
                <FormattedMessage
                  id="store.contribute.flow.step3.title"
                  defaultMessage="Submit pull request"
                />
              }
              description={
                <div>
                  <Paragraph>
                    <FormattedMessage
                      id="store.contribute.flow.step3.p1"
                      defaultMessage="Submit a pull request to the WLDOS official plugin repository:"
                    />
                  </Paragraph>
                  <List
                    size="small"
                    dataSource={[
                      <FormattedMessage
                        id="store.contribute.flow.step3.item1"
                        key="1"
                        defaultMessage="Fork the WLDOS official plugin repository."
                      />,
                      <FormattedMessage
                        id="store.contribute.flow.step3.item2"
                        key="2"
                        defaultMessage="Add your plugin to the repository."
                      />,
                      <FormattedMessage
                        id="store.contribute.flow.step3.item3"
                        key="3"
                        defaultMessage="Fill out detailed PR description (plugin functionality, usage scenarios, etc.)."
                      />,
                      <FormattedMessage
                        id="store.contribute.flow.step3.item4"
                        key="4"
                        defaultMessage="Ensure the code passes CI checks."
                      />,
                      <FormattedMessage
                        id="store.contribute.flow.step3.item5"
                        key="5"
                        defaultMessage="Wait for official review."
                      />,
                    ]}
                    renderItem={(item) => (
                      <List.Item>
                        <CheckCircleOutlined style={{ color: '#52c41a', marginRight: 8 }} />
                        {item}
                      </List.Item>
                    )}
                  />
                  <div style={{ marginTop: 16 }}>
                    <Button 
                      type="link" 
                      icon={<LinkOutlined />}
                      href="https://github.com/wldos/wldos-plugins"
                      target="_blank"
                    >
                      <FormattedMessage
                        id="store.contribute.flow.step3.link"
                        defaultMessage="Visit the WLDOS official plugin repository"
                      />
                    </Button>
                  </div>
                </div>
              }
              icon={<GithubOutlined />}
            />
            <Step
              title={
                <FormattedMessage
                  id="store.contribute.flow.step4.title"
                  defaultMessage="Official review"
                />
              }
              description={
                <div>
                  <Paragraph>
                    <FormattedMessage
                      id="store.contribute.flow.step4.p1"
                      defaultMessage="The WLDOS official team will review your plugin:"
                    />
                  </Paragraph>
                  <List
                    size="small"
                    dataSource={[
                      <FormattedMessage
                        id="store.contribute.flow.step4.item1"
                        key="1"
                        defaultMessage="Code quality review."
                      />,
                      <FormattedMessage
                        id="store.contribute.flow.step4.item2"
                        key="2"
                        defaultMessage="Functional completeness testing."
                      />,
                      <FormattedMessage
                        id="store.contribute.flow.step4.item3"
                        key="3"
                        defaultMessage="Security checks."
                      />,
                      <FormattedMessage
                        id="store.contribute.flow.step4.item4"
                        key="4"
                        defaultMessage="Compliance with guidelines and policies."
                      />,
                      <FormattedMessage
                        id="store.contribute.flow.step4.item5"
                        key="5"
                        defaultMessage="Documentation completeness check."
                      />,
                    ]}
                    renderItem={(item) => (
                      <List.Item>
                        <CheckCircleOutlined style={{ color: '#52c41a', marginRight: 8 }} />
                        {item}
                      </List.Item>
                    )}
                  />
                  <Alert
                    message={
                      <FormattedMessage
                        id="store.contribute.flow.step4.alert.title"
                        defaultMessage="Review time"
                      />
                    }
                    description={
                      <FormattedMessage
                        id="store.contribute.flow.step4.alert.desc"
                        defaultMessage="The review time is usually 1-3 working days. For complex plugins, it may take longer. We will communicate with you in time."
                      />
                    }
                    type="info"
                    showIcon
                    style={{ marginTop: 16 }}
                  />
                </div>
              }
              icon={<CheckCircleOutlined />}
            />
            <Step
              title={
                <FormattedMessage
                  id="store.contribute.flow.step5.title"
                  defaultMessage="Merge and release"
                />
              }
              description={
                <div>
                  <Paragraph>
                    <FormattedMessage
                      id="store.contribute.flow.step5.p1"
                      defaultMessage="After passing the review, your plugin will be:"
                    />
                  </Paragraph>
                  <List
                    size="small"
                    dataSource={[
                      <FormattedMessage
                        id="store.contribute.flow.step5.item1"
                        key="1"
                        defaultMessage="Merged into the official plugin repository."
                      />,
                      <FormattedMessage
                        id="store.contribute.flow.step5.item2"
                        key="2"
                        defaultMessage="Added to the WLDOS plugin marketplace."
                      />,
                      <FormattedMessage
                        id="store.contribute.flow.step5.item3"
                        key="3"
                        defaultMessage="Released with the next WLDOS version."
                      />,
                      <FormattedMessage
                        id="store.contribute.flow.step5.item4"
                        key="4"
                        defaultMessage="Available to all WLDOS users."
                      />,
                    ]}
                    renderItem={(item) => (
                      <List.Item>
                        <CheckCircleOutlined style={{ color: '#52c41a', marginRight: 8 }} />
                        {item}
                      </List.Item>
                    )}
                  />
                </div>
              }
              icon={<CheckCircleOutlined />}
            />
          </Steps>
        </Card>

        <Card
          title={
            <Space>
              <FileTextOutlined />
              <span>
                <FormattedMessage
                  id="store.contribute.spec.title"
                  defaultMessage="Development guidelines"
                />
              </span>
            </Space>
          }
          style={{ marginBottom: 24 }}
        >
          <Alert
            message={
              <FormattedMessage
                id="store.contribute.spec.alert.title"
                defaultMessage="Plugin development must follow the following guidelines"
              />
            }
            description={
              <div>
                <Title level={5}>
                  <FormattedMessage
                    id="store.contribute.spec.section1.title"
                    defaultMessage="1. Code conventions"
                  />
                </Title>
                <List
                  size="small"
                  dataSource={[
                    <FormattedMessage
                      id="store.contribute.spec.section1.item1"
                      key="1"
                      defaultMessage="Follow Java and JavaScript coding conventions."
                    />,
                    <FormattedMessage
                      id="store.contribute.spec.section1.item2"
                      key="2"
                      defaultMessage="Code comments should be clear, especially for key logic."
                    />,
                    <FormattedMessage
                      id="store.contribute.spec.section1.item3"
                      key="3"
                      defaultMessage="Avoid hard-coding; use configuration files instead."
                    />,
                    <FormattedMessage
                      id="store.contribute.spec.section1.item4"
                      key="4"
                      defaultMessage="Handle errors properly with appropriate exception handling."
                    />,
                  ]}
                  renderItem={(item) => (
                    <List.Item>
                      <Text>{item}</Text>
                    </List.Item>
                  )}
                />
                
                <Divider />
                
                <Title level={5}>
                  <FormattedMessage
                    id="store.contribute.spec.section2.title"
                    defaultMessage="2. Plugin configuration"
                  />
                </Title>
                <List
                  size="small"
                  dataSource={[
                    <FormattedMessage
                      id="store.contribute.spec.section2.item1"
                      key="1"
                      defaultMessage="Must contain a valid plugin.yml configuration file."
                    />,
                    <FormattedMessage
                      id="store.contribute.spec.section2.item2"
                      key="2"
                      defaultMessage="Plugin code (code) must be unique and follow naming conventions."
                    />,
                    <FormattedMessage
                      id="store.contribute.spec.section2.item3"
                      key="3"
                      defaultMessage="Version numbers must follow semantic versioning (SemVer)."
                    />,
                    <FormattedMessage
                      id="store.contribute.spec.section2.item4"
                      key="4"
                      defaultMessage="Permission configuration should be clear and follow the principle of least privilege."
                    />,
                  ]}
                  renderItem={(item) => (
                    <List.Item>
                      <Text>{item}</Text>
                    </List.Item>
                  )}
                />
                
                <Divider />
                
                <Title level={5}>
                  <FormattedMessage
                    id="store.contribute.spec.section3.title"
                    defaultMessage="3. Security"
                  />
                </Title>
                <List
                  size="small"
                  dataSource={[
                    <FormattedMessage
                      id="store.contribute.spec.section3.item1"
                      key="1"
                      defaultMessage="Must not contain malicious code or backdoors."
                    />,
                    <FormattedMessage
                      id="store.contribute.spec.section3.item2"
                      key="2"
                      defaultMessage="API calls must perform permission checks."
                    />,
                    <FormattedMessage
                      id="store.contribute.spec.section3.item3"
                      key="3"
                      defaultMessage="User input must be validated and filtered."
                    />,
                    <FormattedMessage
                      id="store.contribute.spec.section3.item4"
                      key="4"
                      defaultMessage="Sensitive information must not be hard-coded in the code."
                    />,
                  ]}
                  renderItem={(item) => (
                    <List.Item>
                      <Text>{item}</Text>
                    </List.Item>
                  )}
                />
                
                <Divider />
                
                <Title level={5}>
                  <FormattedMessage
                    id="store.contribute.spec.section4.title"
                    defaultMessage="4. Documentation requirements"
                  />
                </Title>
                <List
                  size="small"
                  dataSource={[
                    <FormattedMessage
                      id="store.contribute.spec.section4.item1"
                      key="1"
                      defaultMessage="Provide a complete README.md document."
                    />,
                    <FormattedMessage
                      id="store.contribute.spec.section4.item2"
                      key="2"
                      defaultMessage="Explain plugin features, usage, and configuration options."
                    />,
                    <FormattedMessage
                      id="store.contribute.spec.section4.item3"
                      key="3"
                      defaultMessage="Provide installation and uninstallation instructions."
                    />,
                    <FormattedMessage
                      id="store.contribute.spec.section4.item4"
                      key="4"
                      defaultMessage="Include necessary screenshots or examples."
                    />,
                  ]}
                  renderItem={(item) => (
                    <List.Item>
                      <Text>{item}</Text>
                    </List.Item>
                  )}
                />
              </div>
            }
            type="warning"
            showIcon
          />
        </Card>

        <Card
          title={
            <Space>
              <CheckCircleOutlined />
              <span>
                <FormattedMessage
                  id="store.contribute.standard.title"
                  defaultMessage="Review standards"
                />
              </span>
            </Space>
          }
          style={{ marginBottom: 24 }}
        >
          <List
            dataSource={[
              {
                  title: (
                    <FormattedMessage
                      id="store.contribute.standard.item1.title"
                      defaultMessage="Functional completeness"
                    />
                  ),
                  description: (
                    <FormattedMessage
                      id="store.contribute.standard.item1.desc"
                      defaultMessage="Plugin functionality is complete, runs properly, and has no obvious bugs."
                    />
                  ),
              },
              {
                  title: (
                    <FormattedMessage
                      id="store.contribute.standard.item2.title"
                      defaultMessage="Code quality"
                    />
                  ),
                  description: (
                    <FormattedMessage
                      id="store.contribute.standard.item2.desc"
                      defaultMessage="Code structure is clear, follows best practices, and is maintainable."
                    />
                  ),
              },
              {
                  title: (
                    <FormattedMessage
                      id="store.contribute.standard.item3.title"
                      defaultMessage="Security"
                    />
                  ),
                  description: (
                    <FormattedMessage
                      id="store.contribute.standard.item3.desc"
                      defaultMessage="Passes security review with no security vulnerabilities or risks."
                    />
                  ),
              },
              {
                  title: (
                    <FormattedMessage
                      id="store.contribute.standard.item4.title"
                      defaultMessage="Compliance"
                    />
                  ),
                  description: (
                    <FormattedMessage
                      id="store.contribute.standard.item4.desc"
                      defaultMessage="Complies with WLDOS plugin development guidelines and platform policies."
                    />
                  ),
              },
              {
                  title: (
                    <FormattedMessage
                      id="store.contribute.standard.item5.title"
                      defaultMessage="Documentation completeness"
                    />
                  ),
                  description: (
                    <FormattedMessage
                      id="store.contribute.standard.item5.desc"
                      defaultMessage="Documentation is clear and complete, enabling users to understand and use the plugin."
                    />
                  ),
              }
            ]}
            renderItem={(item) => (
              <List.Item>
                <List.Item.Meta
                  title={
                    <Space>
                      <Tag color="blue">{item.title}</Tag>
                    </Space>
                  }
                  description={item.description}
                />
              </List.Item>
            )}
          />
        </Card>

        <Card
          title={
            <Space>
              <QuestionCircleOutlined />
              <span>
                <FormattedMessage
                  id="store.contribute.faq.title"
                  defaultMessage="FAQ"
                />
              </span>
            </Space>
          }
        >
          <List
            dataSource={[
              {
                    q: (
                      <FormattedMessage
                        id="store.contribute.faq.q1.q"
                        defaultMessage="Q: How long does it take to review my plugin?"
                      />
                    ),
                    a: (
                      <FormattedMessage
                        id="store.contribute.faq.q1.a"
                        defaultMessage="A: Usually 1-3 working days. If the plugin is complex or changes are needed, it may take longer. We will communicate with you in time."
                      />
                    ),
              },
              {
                    q: (
                      <FormattedMessage
                        id="store.contribute.faq.q2.q"
                        defaultMessage="Q: What if the review does not pass?"
                      />
                    ),
                    a: (
                      <FormattedMessage
                        id="store.contribute.faq.q2.a"
                        defaultMessage="A: We will explain in detail the reasons for rejection and suggestions for improvement. You can modify based on the feedback and resubmit."
                      />
                    ),
              },
              {
                    q: (
                      <FormattedMessage
                        id="store.contribute.faq.q3.q"
                        defaultMessage="Q: Can I submit commercial plugins?"
                      />
                    ),
                    a: (
                      <FormattedMessage
                        id="store.contribute.faq.q3.a"
                        defaultMessage="A: Yes. Commercial plugins need to be clearly marked and provide a free trial version. Commercial licensing is handled by the developer."
                      />
                    ),
              },
              {
                    q: (
                      <FormattedMessage
                        id="store.contribute.faq.q4.q"
                        defaultMessage="Q: Can I update the plugin after it is released?"
                      />
                    ),
                    a: (
                      <FormattedMessage
                        id="store.contribute.faq.q4.a"
                        defaultMessage="A: Yes. Submit a new pull request to update the plugin version. Updates must also go through the review process."
                      />
                    ),
              },
              {
                    q: (
                      <FormattedMessage
                        id="store.contribute.faq.q5.q"
                        defaultMessage="Q: How can I contact the official team?"
                      />
                    ),
                    a: (
                      <FormattedMessage
                        id="store.contribute.faq.q5.a"
                        defaultMessage="A: You can contact us via GitHub issues, email (306991142@qq.com), or the official community."
                      />
                    ),
              }
            ]}
            renderItem={(item) => (
              <List.Item>
                <div>
                  <Text strong>{item.q}</Text>
                  <br />
                  <Text type="secondary">{item.a}</Text>
                </div>
              </List.Item>
            )}
          />
        </Card>

        <Card
          title={
            <FormattedMessage
              id="store.contribute.links.title"
              defaultMessage="Related resources"
            />
          }
          style={{ marginTop: 24 }}
        >
          <Space direction="vertical" style={{ width: '100%' }}>
            <Button 
              type="link" 
              icon={<GithubOutlined />}
              href="https://github.com/wldos/wldos-plugins"
              target="_blank"
              block
              style={{ textAlign: 'left', height: 'auto', padding: '8px 0' }}
            >
              <div>
                <Text strong>
                  <FormattedMessage
                    id="store.contribute.links.repo.title"
                    defaultMessage="WLDOS official plugin repository"
                  />
                </Text>
                <br />
                <Text type="secondary" style={{ fontSize: 12 }}>
                  <FormattedMessage
                    id="store.contribute.links.repo.desc"
                    defaultMessage="View official plugin examples and submit your plugin."
                  />
                </Text>
              </div>
            </Button>
            
            <Button 
              type="link" 
              icon={<FileTextOutlined />}
              href="https://github.com/wldos/wldos-plugins/blob/main/README.md"
              target="_blank"
              block
              style={{ textAlign: 'left', height: 'auto', padding: '8px 0' }}
            >
              <div>
                <Text strong>
                  <FormattedMessage
                    id="store.contribute.links.docs.title"
                    defaultMessage="Plugin development documentation"
                  />
                </Text>
                <br />
                <Text type="secondary" style={{ fontSize: 12 }}>
                  <FormattedMessage
                    id="store.contribute.links.docs.desc"
                    defaultMessage="Detailed plugin development guides and API documentation."
                  />
                </Text>
              </div>
            </Button>
            
            <Button 
              type="link" 
              icon={<QuestionCircleOutlined />}
              href="https://github.com/wldos/wldos-plugins/issues"
              target="_blank"
              block
              style={{ textAlign: 'left', height: 'auto', padding: '8px 0' }}
            >
              <div>
                <Text strong>
                  <FormattedMessage
                    id="store.contribute.links.feedback.title"
                    defaultMessage="Feedback"
                  />
                </Text>
                <br />
                <Text type="secondary" style={{ fontSize: 12 }}>
                  <FormattedMessage
                    id="store.contribute.links.feedback.desc"
                    defaultMessage="Submit issues, suggestions, or questions about plugin development."
                  />
                </Text>
              </div>
            </Button>
          </Space>
        </Card>
      </div>
    </PageContainer>
  );
};

export default PluginContribute;

