import React, { useCallback, useEffect, useState } from 'react';
import { Modal, Spin, message } from 'antd';
import { ReloadOutlined } from '@ant-design/icons';
import { useIntl } from 'umi';
import {
  fetchClickChallengeForForget,
  verifyClickChallengeForForget,
} from '@/pages/user/forget/service';
import styles from './ClickCaptchaModal.less';

const MODE_IMAGE = 'image';

const ClickCaptchaModal = ({ open, onCancel, onSuccess }) => {
  const intl = useIntl();
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [challengeUuid, setChallengeUuid] = useState('');
  const [mode, setMode] = useState('');
  const [order, setOrder] = useState([]);
  const [tiles, setTiles] = useState([]);
  const [imageBase64, setImageBase64] = useState('');
  const [picked, setPicked] = useState([]);
  const [clickPoints, setClickPoints] = useState([]);
  const [clickMarkers, setClickMarkers] = useState([]);
  const [verifyFailed, setVerifyFailed] = useState(false);

  const loadChallenge = useCallback(async () => {
    setLoading(true);
    setPicked([]);
    setClickPoints([]);
    setClickMarkers([]);
    setVerifyFailed(false);
    try {
      const res = await fetchClickChallengeForForget();
      if (res?.code !== 200 || !res?.data?.challengeUuid) {
        message.error(intl.formatMessage({ id: 'userForget.secondary.load-failed' }));
        return;
      }
      const data = res.data;
      setChallengeUuid(data.challengeUuid);
      setMode(data.mode || '');
      setOrder(Array.isArray(data.order) ? data.order : []);
      setTiles(Array.isArray(data.tiles) ? data.tiles : []);
      setImageBase64(data.imageBase64 || '');
    } finally {
      setLoading(false);
    }
  }, [intl]);

  useEffect(() => {
    if (open) {
      loadChallenge();
    }
  }, [open, loadChallenge]);

  const submitVerify = async (payload) => {
    setSubmitting(true);
    try {
      const res = await verifyClickChallengeForForget({
        challengeUuid,
        ...payload,
      });
      if (res?.code !== 200 || !res?.data?.graphPass) {
        setVerifyFailed(true);
        await loadChallenge();
        return;
      }
      onSuccess(res.data.graphPass);
    } finally {
      setSubmitting(false);
    }
  };

  const onTileClick = async (text) => {
    if (submitting || loading || !challengeUuid || order.length === 0) {
      return;
    }
    const nextIndex = picked.length;
    const expected = order[nextIndex];
    if (text !== expected) {
      setVerifyFailed(true);
      setPicked([]);
      await loadChallenge();
      return;
    }
    const nextPicked = [...picked, text];
    setPicked(nextPicked);
    if (nextPicked.length < order.length) {
      return;
    }
    await submitVerify({ clicked: nextPicked });
  };

  const onImageClick = async (event) => {
    if (submitting || loading || !challengeUuid || order.length === 0) {
      return;
    }
    const img = event.currentTarget;
    const rect = img.getBoundingClientRect();
    const scaleX = img.naturalWidth / rect.width;
    const scaleY = img.naturalHeight / rect.height;
    const displayX = event.clientX - rect.left;
    const displayY = event.clientY - rect.top;
    const x = Math.round(displayX * scaleX);
    const y = Math.round(displayY * scaleY);
    const nextPoints = [...clickPoints, { x, y }];
    const nextMarkers = [
      ...clickMarkers,
      { displayX, displayY, index: clickMarkers.length + 1 },
    ];
    setClickPoints(nextPoints);
    setClickMarkers(nextMarkers);
    if (nextPoints.length < order.length) {
      return;
    }
    await submitVerify({ clicks: nextPoints });
  };

  return (
    <Modal
      title={intl.formatMessage({ id: 'userForget.secondary.title' })}
      visible={open}
      footer={null}
      onCancel={onCancel}
      destroyOnClose
      maskClosable={false}
      width={420}
      className={styles.outerModal}
    >
      <Spin spinning={loading || submitting}>
        <div className={styles.card}>
          <div className={styles.cardTitle}>
            {intl.formatMessage({ id: 'userForget.secondary.cardTitle' })}
          </div>
          {mode === MODE_IMAGE && imageBase64 ? (
            <>
              <div className={styles.imageWrap}>
                <img
                  className={styles.captchaImage}
                  src={`data:image/jpeg;base64,${imageBase64}`}
                  alt=""
                  onClick={onImageClick}
                />
                {clickMarkers.map((marker) => (
                  <span
                    key={`${marker.index}-${marker.displayX}-${marker.displayY}`}
                    className={styles.marker}
                    style={{ left: marker.displayX, top: marker.displayY }}
                  >
                    {marker.index}
                  </span>
                ))}
                {verifyFailed && (
                  <div className={styles.imageError}>
                    {intl.formatMessage({ id: 'userForget.secondary.verify-failed' })}
                  </div>
                )}
              </div>
              <p className={styles.instruction}>
                {intl.formatMessage({ id: 'userForget.secondary.instruction-prefix' })}{' '}
                {order.map((char, idx) => (
                  <span key={`${char}-${idx}`} className={styles.highlight}>
                    &quot;{char}&quot;
                  </span>
                ))}
              </p>
            </>
          ) : (
            <>
              <p className={styles.hint}>
                {intl.formatMessage({ id: 'userForget.secondary.tile-hint' })}
              </p>
              <p className={styles.order}>
                {order.map((char, idx) => (
                  <span key={`${char}-${idx}`}>
                    {idx > 0 ? ' → ' : ''}
                    <strong>{char}</strong>
                  </span>
                ))}
              </p>
              {verifyFailed && (
                <p className={styles.tileError}>
                  {intl.formatMessage({ id: 'userForget.secondary.verify-failed' })}
                </p>
              )}
              <div className={styles.tiles}>
                {tiles.map((text, idx) => (
                  <button
                    key={`${text}-${idx}`}
                    type="button"
                    className={styles.tile}
                    disabled={submitting || loading}
                    onClick={() => onTileClick(text)}
                  >
                    {text}
                  </button>
                ))}
              </div>
              <p className={styles.progress}>
                {intl.formatMessage(
                  { id: 'userForget.secondary.progress' },
                  { current: picked.length, total: order.length },
                )}
              </p>
            </>
          )}
          <button
            type="button"
            className={styles.refresh}
            disabled={loading || submitting}
            onClick={loadChallenge}
          >
            <ReloadOutlined />{' '}
            {intl.formatMessage({ id: 'userForget.secondary.refresh' })}
          </button>
        </div>
      </Spin>
    </Modal>
  );
};

export default ClickCaptchaModal;
