package com.wuti.serviceSnapshot.apollo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Set;

@Slf4j
public class ApolloListener{


    private static final String SERVICE_SNAPSHOT_SWITCH = "service.snapshot.switch";
    public static final String SERVICE_SNAPSHOT_INDEX= "service.snapshot.index";
    // 索引map
    private Map<String, Integer> indexMap = Maps.newConcurrentMap();

    // 0:不走缓存   1：如果acquireList为空，则查询缓存   2：走缓存（测试用，生产不建议使用）
    @Value("${service.snapshot.switch:1}")
    private volatile int serviceSnapshotSwitch;

    @Value("${service.snapshot.index:}")
    private volatile String serviceSnapshotIndex;

    @Value("${service.snapshot.HeartBeatEventInterval:60}")
    private volatile int triggerHeartBeatEventInterval;

    @EventListener(classes = EnvironmentChangeEvent.class)
    public void listenEvent(ApplicationEvent event) {
        log.debug("监听事件触发={}",event);
        EnvironmentChangeEvent e = (EnvironmentChangeEvent)event;
        if(e.getKeys().contains(SERVICE_SNAPSHOT_INDEX)){
            // 解析
            if(StringUtils.isBlank(serviceSnapshotIndex)){
                return;
            }
            JSONObject  jsonObject = null;
            try {
                jsonObject = JSON.parseObject(serviceSnapshotIndex);
            } catch (Exception ex) {
                log.warn("serviceSnapshotIndex json parse失败",ex);
            }
            if(CollectionUtils.isEmpty(jsonObject)){
                return;
            }
            Set<String> keys = jsonObject.keySet();
            for(String key:keys){
                try {
                    setAppNameIndex(key.toUpperCase(),jsonObject.getInteger(key));
                } catch (Exception ex) {
                    log.warn("setAppNameIndex 错误",ex);
                }
            }
        }
    }



    public int getServiceSnapshotSwitch() {
        return serviceSnapshotSwitch;
    }

    public void setAppNameIndex(String appName, Integer index) {
        if (index == null){
            index = 0;
        }
        indexMap.put(appName.toUpperCase(),index);
    }

    public Map<String, Integer> getIndexMap() {
        return indexMap;
    }

    public int getTriggerHeartBeatEventInterval() {
        return triggerHeartBeatEventInterval;
    }
}
