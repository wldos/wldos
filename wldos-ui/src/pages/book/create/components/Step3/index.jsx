import React, {useEffect, useRef, useState} from 'react';
import {Button, Form, Input, message,} from 'antd';
import {connect} from 'umi';
import '@/pages/book/components/BaseView.less';
import {Editor} from "@tinymce/tinymce-react";
import styles from "@/pages/book/create/components/Step2/index.less";
import {formItemLayout} from "@/pages/book/create/components/Step2";

const Step3 = ({data, dispatch, submitting }) => {
  const [form] = Form.useForm();
  const FormItem = Form.Item;
  const editorRef = useRef(null);
  const [content, setContent] = useState('');

  useEffect(() => {
    if (data && data.id) {
      form.setFieldsValue({
        id: data.id,
      });
    }
  }, [data]);

  useEffect(() => {
    form.setFieldsValue({pubContent: content});
  }, [content]);

  const {getFieldsValue} = form;

  const onPrev = () => {
    if (dispatch) {
      const values = getFieldsValue();
      dispatch({
        type: 'bookInfo/saveStepFormData',
        payload: {...data, ...values},
      });
      dispatch({
        type: 'bookInfo/saveCurrentStep',
        payload: 'info',
      });
    }
  };

  const doSave = async (values) => {
    if (!values.pubContent) {
      message.error("请描述详情！");
      return;
    }
    if (dispatch) {
      dispatch({
        type: 'bookInfo/submitStepForm',
        payload: values,
      });
    }
  };

  return (
    <>
      <Form
        layout="vertical"
        style={{height: '40px'}}
        form={form}
        className={styles.stepForm}
        hideRequiredMark
        onFinish={doSave}
      >
        <FormItem name="id" noStyle>
          <Input type="hidden"/>
        </FormItem>
        <FormItem name="pubContent" noStyle>
          <textarea hidden
                    value={content} onChange={(e) => setContent(e.target.value)}/>
        </FormItem>
        <FormItem
          wrapperCol={{
            xs: {
              span: 24,
              offset: formItemLayout.labelCol.span,
            },
            sm: {
              span: formItemLayout.wrapperCol.span,
              offset: 10,
            },
          }}
        >
          <Button type="primary" onClick={form.submit} loading={submitting}>
            提交
          </Button>
          <Button onClick={onPrev} style={{ marginLeft: 8, }}>
            上一步
          </Button>
        </FormItem>
      </Form>
    </>
  );
};

export default connect(({bookInfo, loading}) => ({
  submitting: loading.effects['bookInfo/submitStepForm'],
  data: bookInfo.step,
  privacyEnum: bookInfo.privacyEnum,
}))(Step3);
