import React, { useState, useEffect } from 'react';
import { Card, Steps, Alert, Space, Typography } from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { connect } from 'umi';
import Step1 from './components/Step1';
import Step2 from './components/Step2';
import Step4 from './components/Step4';
import styles from './style.less';
import {genTdkCrumbs} from "@/utils/utils";

const { Step } = Steps;
const { Title, Text } = Typography;

const getCurrentStepAndComponent = (current) => {
  switch (current) {
    case 'info':
      return {
        step: 2,
        component: <Step2 />,
      };

    case 'result':
      return {
        step: 3,
        component: <Step4 />,
      };

    case 'category':
    default:
      return {
        step: 1,
        component: <Step1 />,
      };
  }
};

// 步骤配置
const stepConfig = {
  1: {
    title: '选择分类',
    description: '请选择您要发布的信息所属的分类',
    icon: '📂'
  },
  2: {
    title: '填写信息',
    description: '填写详细信息，包括标题、内容、图片等',
    icon: '✏️'
  },
  3: {
    title: '发布信息',
    description: '确认信息无误后发布',
    icon: '🚀'
  }
};

const StepForm = ({ current, dispatch }) => {
  const [stepComponent, setStepComponent] = useState(<Step1 />);
  const [currentStep, setCurrentStep] = useState(1);

  useEffect(() => {
    const { step, component } = getCurrentStepAndComponent(current);
    setCurrentStep(step);
    setStepComponent(component);
    const crumbs = {seoCrumbs: {title: '发布信息', description: '', keywords: '', crumbs: []}};
    genTdkCrumbs(crumbs, dispatch, undefined,  '');
  }, [current, dispatch]);

  const currentStepInfo = stepConfig[currentStep];

  return (
    <PageContainer 
      title="发布信息" 
      content="选择分类，填写信息并发布。" 
      breadcrumb={false}
    >
      <Card bordered={false}>
        {/* 步骤提示 */}
        <Alert
          message={
            <Space>
              <span style={{ fontSize: '18px' }}>{currentStepInfo.icon}</span>
              <div>
                <Title level={4} style={{ margin: 0 }}>
                  {currentStepInfo.title}
                </Title>
                <Text type="secondary">{currentStepInfo.description}</Text>
              </div>
            </Space>
          }
          type="info"
          showIcon={false}
          style={{ marginBottom: 24 }}
        />

        <Steps current={currentStep} className={styles.steps}>
          <Step 
            title="选择分类" 
            description="选择信息分类"
            icon={<span style={{ fontSize: '16px' }}>📂</span>}
          />
          <Step 
            title="填写信息" 
            description="填写详细信息"
            icon={<span style={{ fontSize: '16px' }}>✏️</span>}
          />
          <Step 
            title="发布信息" 
            description="确认并发布"
            icon={<span style={{ fontSize: '16px' }}>🚀</span>}
          />
        </Steps>
        
        {stepComponent}
      </Card>
    </PageContainer>
  );
};

export default connect(({ bookInfo }) => ({
  current: bookInfo.current,
}))(StepForm);
