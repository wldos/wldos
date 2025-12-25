import React, {useEffect, useState} from 'react';
import {connect} from "umi";
import {Avatar, Button, Comment, Form, Input, List, Tooltip} from 'antd';
import moment from 'moment';
import {doComment, queryComment, delComment} from "@/pages/book/detail/service";


const {TextArea} = Input;

const Editor = ({onChange, onSubmit, submitting, value, isLogin}) => (
  <>
    <Form.Item>
      {isLogin ? (<TextArea rows={4} onChange={onChange} value={value}/>)
        : (<><a href="/user/login" rel="nofollow">登录</a>以发表评论</>)}
    </Form.Item>
    <Form.Item>
      {isLogin && (
        <Button htmlType="submit" loading={submitting} onClick={onSubmit} type="primary">
          评论
        </Button>)}
    </Form.Item>
  </>
);

const ReplyBox = ({style, currentUser, handleChange, handleSubmit, submitting, value}) => (
  <Comment
    style={style}
    avatar={
      currentUser?.nickname && <Avatar
        src={currentUser.avatar}
        alt={currentUser.nickname}
      />
    }
    content={
      <Editor
        onChange={handleChange}
        onSubmit={handleSubmit}
        submitting={submitting}
        value={value}
        isLogin={!!currentUser?.id}
      />
    }
  />);

const CommentList = ({comments, setReply, replyId, currentUser, handleChange, handleSubmit, submitting, value, delReply}) => comments && (
  <List
    dataSource={comments}
    header={comments && `${comments.length}个回复`}
    itemLayout="horizontal"
    renderItem={props => <Comment
      id={props.id}
      actions={replyId === props.id ? [<span key="cancel-reply" onClick={() => setReply(props.id)}>取消</span>,
      <ReplyBox key={props.id} currentUser={currentUser} handleChange={handleChange} handleSubmit={handleSubmit} submitting={submitting} value={value}/>,
      ]
      : [<span key="reply-to" onClick={() => setReply(props.id)}>回复</span>,currentUser?.nickname && <span key="del-rel" onClick={() => delReply(props.id)}>删除</span>,]}
      author={<a>{props.author}</a>}
      avatar={
      <Avatar
      src={props.avatar}
      alt={props.author}
      />
    }
      content={<p>{props.content}</p>}
      datetime={
      <Tooltip
      title={moment(props.datetime).format('YYYY-MM-DD HH:mm:ss')}>
      <span>{moment(props.datetime).fromNow()}</span>
      </Tooltip>
    }
      >{props.children && CommentList({comments: props.children, setReply, replyId, currentUser, handleChange, handleSubmit, submitting, value, delReply})}</Comment>}
  />
);

const Comments = (props) => {
  const {id, currentUser = {}} = props;
  const [comments, setComments] = useState([]);
  const [submitting, setSubmitting] = useState(false);
  const [value, setValue] = useState('');
  const [replyId, setReplyId] = useState(undefined);

  useEffect(async () => {
    const res = await queryComment({id});

    if (res?.data) {
      setComments(res.data);
    }
  }, []);

  const handleChange = e => {
    setValue(e.target.value);
  };

  const setReply = (parentId) => setReplyId(parentId === replyId ? undefined : parentId);

  const handleSubmit = () => {
    if (!value)
      return;

    setSubmitting(true);

    const params = {
      pubId: id, // 文章id
      author: currentUser?.nickname,
      content: value,
    };

    if (replyId){
      params.parentId = replyId;
    }

    setTimeout(async () => {
      const res = await doComment(params);

      if (!res?.data)
        return;

      setSubmitting(false);
      setValue('');

      const result = await queryComment({id});

      if (result?.data) {
        if (replyId){
          setReplyId(undefined);
        }
        setComments(result.data);
      }

    }, 1000);
  };

  const delReply = (cid) => {
    setTimeout(async () => {
      const res = await delComment({id: cid, pubId: id});

      if (!res?.data)
        return;

      const result = await queryComment({id});

      if (result?.data) {
        if (replyId){
          setReplyId(undefined);
        }
        setComments(result.data);
      }

    }, 1000);
  };

  return (
    <>
      {comments && comments.length > 0 && <CommentList comments={comments} setReply={setReply} replyId={replyId} currentUser={currentUser}
                                                       handleChange={handleChange} handleSubmit={handleSubmit} submitting={submitting} value={value} delReply={delReply}/>}
      <ReplyBox style={{display: replyId ? 'none' : 'block'}}
        currentUser={currentUser}
        handleChange={handleChange}
        handleSubmit={handleSubmit}
        submitting={submitting}
        value={value}

      />
    </>
  );
};

export default connect(({user, loading}) => ({
  currentUser: user.currentUser,
  loading: loading.models.user,
}))(Comments);
