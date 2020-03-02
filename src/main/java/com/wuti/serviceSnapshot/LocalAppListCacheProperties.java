package com.wuti.serviceSnapshot;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "eureka.local.eache")
public class LocalAppListCacheProperties {


    /**
     * 缓存服务的实例快照的数量，一个含有实例的applicationinfo为一个库按照,   todo
     * 默认20
     */
    private Integer instanceSnapshotNum = 20;

    /**
     * 响应heartBeat事件的时间间隔，s  如果该时间比心跳时间
     */
    private Integer triggerHeartBeatEventInterval =20;


    public Integer getInstanceSnapshotNum() {
        return instanceSnapshotNum;
    }

    public void setInstanceSnapshotNum(Integer instanceSnapshotNum) {
        this.instanceSnapshotNum = instanceSnapshotNum;
    }

    public Integer getTriggerHeartBeatEventInterval() {
        return triggerHeartBeatEventInterval;
    }

    public void setTriggerHeartBeatEventInterval(Integer triggerHeartBeatEventInterval) {
        this.triggerHeartBeatEventInterval = triggerHeartBeatEventInterval;
    }
}
