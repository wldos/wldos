import React from "react";
import {Card, List, Typography} from "antd";
import {Link} from "umi";
import moment from "moment";
import AvatarList from "@/components/AvatarList";
import {createImageErrorHandler} from "@/utils/imageUtils";
import styles from './style.less';
import {typeUrl} from "@/utils/utils";

const SideCar = ({listSide}) => listSide && (
  <List
    rowKey="id"
    grid={{
      gutter: 24, xs: 1, sm: 1, md: 1, lg: 1, xl: 1, xxl: 1,
    }}
    dataSource={listSide}
    renderItem={({cover, pubType, id, members, pubTitle, subTitle, createTime}) => (
      <List.Item>
          <Card className={styles.card} hoverable onClick={() => window.open(typeUrl({pubType, id}), '_blank')}
                cover={
                  <img 
                    alt={pubTitle} 
                    src={cover} 
                    loading="lazy"
                    onError={createImageErrorHandler({ width: '100%', height: '100%', fontSize: '12' })}
                  />
                }>
            <Card.Meta
              title={<Link to={typeUrl({pubType, id})} target="_blank" rel="noopener">{pubTitle}</Link>}
            />
            <div className={styles.cardItemContent}>
              <span>{moment(createTime).fromNow()}</span>
              <div className={styles.avatarList}>
                <AvatarList size="small">
                  {members.map((member, i) => (
                    <AvatarList.Item
                      key={`${id}-${i}`}
                      src={member.avatar}
                      tips={member.name}
                    />
                  ))}
                </AvatarList>
              </div>
            </div>
          </Card>
      </List.Item>
    )}
  />
);

export default SideCar;