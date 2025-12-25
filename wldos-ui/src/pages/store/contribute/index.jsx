import React from 'react';
import { 
  PageContainer 
} from '@ant-design/pro-layout';
import { 
  Card, 
  Typography, 
  Steps, 
  Alert, 
  Space, 
  Divider,
  List,
  Tag,
  Button
} from 'antd';
import { 
  GithubOutlined,
  FileTextOutlined,
  CodeOutlined,
  CheckCircleOutlined,
  InfoCircleOutlined,
  LinkOutlined,
  QuestionCircleOutlined
} from '@ant-design/icons';
import './index.less';

const { Title, Paragraph, Text } = Typography;
const { Step } = Steps;

const PluginContribute = () => {
  return (
    <PageContainer
      title="插件贡献指南"
      content="了解如何为 WLDOS 插件生态贡献您的插件"
    >
      <div className="plugin-contribute-container">
        {/* 概述 */}
        <Card 
          title={
            <Space>
              <InfoCircleOutlined />
              <span>贡献概述</span>
            </Space>
          }
          style={{ marginBottom: 24 }}
        >
          <Alert
            message="WLDOS 插件生态采用开源贡献模式"
            description={
              <div>
                <Paragraph>
                  WLDOS 是一个开源平台，为了维护插件生态的质量和安全，所有插件贡献都需要通过官方审核流程。
                  开发者可以通过 GitHub/GitLab 等代码托管平台提交插件，经过官方审核后，插件将被合并到官方仓库并随 WLDOS 发布。
                </Paragraph>
                <Paragraph>
                  <Text strong>重要提示：</Text> 第三方 WLDOS 实例用户建议使用官方审核通过的插件，不建议直接添加插件。
                  这确保了插件生态的安全性、稳定性和一致性。
                </Paragraph>
              </div>
            }
            type="info"
            showIcon
          />
        </Card>

        {/* 贡献流程 */}
        <Card 
          title={
            <Space>
              <CodeOutlined />
              <span>贡献流程</span>
            </Space>
          }
          style={{ marginBottom: 24 }}
        >
          <Steps direction="vertical" size="small">
            <Step
              title="开发插件"
              description={
                <div>
                  <Paragraph>
                    按照 WLDOS 插件开发规范开发您的插件，确保：
                  </Paragraph>
                  <List
                    size="small"
                    dataSource={[
                      '插件功能完整，能够正常运行',
                      '代码质量良好，遵循开发规范',
                      '包含完整的 plugin.yml 配置文件',
                      '提供清晰的 README 文档',
                      '通过基本的功能测试'
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
              title="提交到代码仓库"
              description={
                <div>
                  <Paragraph>
                    将插件代码提交到代码托管平台（GitHub/GitLab 等）：
                  </Paragraph>
                  <List
                    size="small"
                    dataSource={[
                      '创建插件项目仓库',
                      '上传插件源代码和构建产物',
                      '编写详细的 README 说明文档',
                      '添加必要的许可证文件',
                      '创建 Release 版本（可选）'
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
              title="提交 Pull Request"
              description={
                <div>
                  <Paragraph>
                    向 WLDOS 官方插件仓库提交 Pull Request：
                  </Paragraph>
                  <List
                    size="small"
                    dataSource={[
                      'Fork WLDOS 官方插件仓库',
                      '将您的插件添加到仓库中',
                      '填写详细的 PR 说明（插件功能、使用场景等）',
                      '确保代码通过 CI 检查',
                      '等待官方审核'
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
                      访问 WLDOS 官方插件仓库
                    </Button>
                  </div>
                </div>
              }
              icon={<GithubOutlined />}
            />
            <Step
              title="官方审核"
              description={
                <div>
                  <Paragraph>
                    WLDOS 官方团队将对您的插件进行审核：
                  </Paragraph>
                  <List
                    size="small"
                    dataSource={[
                      '代码质量审查',
                      '功能完整性测试',
                      '安全性检查',
                      '规范符合性验证',
                      '文档完整性检查'
                    ]}
                    renderItem={(item) => (
                      <List.Item>
                        <CheckCircleOutlined style={{ color: '#52c41a', marginRight: 8 }} />
                        {item}
                      </List.Item>
                    )}
                  />
                  <Alert
                    message="审核时间"
                    description="审核时间通常为 1-3 个工作日，复杂插件可能需要更长时间。"
                    type="info"
                    showIcon
                    style={{ marginTop: 16 }}
                  />
                </div>
              }
              icon={<CheckCircleOutlined />}
            />
            <Step
              title="合并发布"
              description={
                <div>
                  <Paragraph>
                    审核通过后，您的插件将被：
                  </Paragraph>
                  <List
                    size="small"
                    dataSource={[
                      '合并到官方插件仓库',
                      '添加到 WLDOS 插件市场',
                      '随下一个 WLDOS 版本发布',
                      '供所有 WLDOS 用户使用'
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

        {/* 开发规范 */}
        <Card 
          title={
            <Space>
              <FileTextOutlined />
              <span>开发规范</span>
            </Space>
          }
          style={{ marginBottom: 24 }}
        >
          <Alert
            message="插件开发必须遵循以下规范"
            description={
              <div>
                <Title level={5}>1. 代码规范</Title>
                <List
                  size="small"
                  dataSource={[
                    '遵循 Java 和 JavaScript 编码规范',
                    '代码注释清晰，关键逻辑必须有说明',
                    '避免硬编码，使用配置文件',
                    '错误处理完善，有适当的异常捕获'
                  ]}
                  renderItem={(item) => (
                    <List.Item>
                      <Text>{item}</Text>
                    </List.Item>
                  )}
                />
                
                <Divider />
                
                <Title level={5}>2. 插件配置</Title>
                <List
                  size="small"
                  dataSource={[
                    '必须包含有效的 plugin.yml 配置文件',
                    '插件编码（code）必须唯一且符合命名规范',
                    '版本号遵循语义化版本规范（SemVer）',
                    '权限配置清晰，最小权限原则'
                  ]}
                  renderItem={(item) => (
                    <List.Item>
                      <Text>{item}</Text>
                    </List.Item>
                  )}
                />
                
                <Divider />
                
                <Title level={5}>3. 安全性</Title>
                <List
                  size="small"
                  dataSource={[
                    '不得包含恶意代码或后门',
                    'API 调用必须进行权限验证',
                    '用户输入必须进行验证和过滤',
                    '敏感信息不得硬编码在代码中'
                  ]}
                  renderItem={(item) => (
                    <List.Item>
                      <Text>{item}</Text>
                    </List.Item>
                  )}
                />
                
                <Divider />
                
                <Title level={5}>4. 文档要求</Title>
                <List
                  size="small"
                  dataSource={[
                    '提供完整的 README.md 文档',
                    '说明插件功能、使用方法和配置项',
                    '提供安装和卸载说明',
                    '包含必要的截图或示例'
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

        {/* 审核标准 */}
        <Card 
          title={
            <Space>
              <CheckCircleOutlined />
              <span>审核标准</span>
            </Space>
          }
          style={{ marginBottom: 24 }}
        >
          <List
            dataSource={[
              {
                title: '功能完整性',
                description: '插件功能完整，能够正常运行，无明显 Bug'
              },
              {
                title: '代码质量',
                description: '代码结构清晰，遵循最佳实践，可维护性好'
              },
              {
                title: '安全性',
                description: '通过安全审查，无安全漏洞和风险'
              },
              {
                title: '规范符合性',
                description: '符合 WLDOS 插件开发规范和平台政策'
              },
              {
                title: '文档完整性',
                description: '文档清晰完整，用户能够理解和使用'
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

        {/* 常见问题 */}
        <Card 
          title={
            <Space>
              <QuestionCircleOutlined />
              <span>常见问题</span>
            </Space>
          }
        >
          <List
            dataSource={[
              {
                q: 'Q: 我的插件审核需要多长时间？',
                a: 'A: 通常为 1-3 个工作日。如果插件功能复杂或需要修改，可能需要更长时间。我们会及时与您沟通。'
              },
              {
                q: 'Q: 审核不通过怎么办？',
                a: 'A: 我们会详细说明不通过的原因和改进建议。您可以根据反馈修改后重新提交。'
              },
              {
                q: 'Q: 可以提交商业插件吗？',
                a: 'A: 可以。商业插件需要明确标注，并提供免费试用版本。商业授权由开发者自行处理。'
              },
              {
                q: 'Q: 插件发布后可以更新吗？',
                a: 'A: 可以。通过提交新的 Pull Request 来更新插件版本。更新同样需要经过审核流程。'
              },
              {
                q: 'Q: 如何联系官方团队？',
                a: 'A: 可以通过 GitHub Issues、邮件（306991142@qq.com）或官方社区联系我们。'
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

        {/* 相关链接 */}
        <Card 
          title="相关资源"
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
                <Text strong>WLDOS 官方插件仓库</Text>
                <br />
                <Text type="secondary" style={{ fontSize: 12 }}>
                  查看官方插件示例和提交您的插件
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
                <Text strong>插件开发文档</Text>
                <br />
                <Text type="secondary" style={{ fontSize: 12 }}>
                  详细的插件开发指南和 API 文档
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
                <Text strong>问题反馈</Text>
                <br />
                <Text type="secondary" style={{ fontSize: 12 }}>
                  提交问题、建议或咨询插件开发相关问题
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

