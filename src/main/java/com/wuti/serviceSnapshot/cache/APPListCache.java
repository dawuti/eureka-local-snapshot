package com.wuti.serviceSnapshot.cache;

import com.wuti.serviceSnapshot.LocalAppListCacheProperties;
import com.wuti.serviceSnapshot.apollo.ApolloListener;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;


@Slf4j
public class APPListCache implements APPListService {

    private LocalAppListCacheProperties localAppListCacheProperties;
    private ApolloListener listener;


    // 最大size使用配置文件  只能重启之后才能生效，eureka本地缓存
    Map<String, EvictingQueue<Application>> tmpCacheMap = Maps.newConcurrentMap();
    Map<String, List<Application>> cacheMap = Maps.newConcurrentMap();

    // app lock  map
    Map<String, Object> lockMap = Maps.newConcurrentMap();


    public APPListCache(LocalAppListCacheProperties localAppListCacheProperties, ApolloListener listener) {
        this.localAppListCacheProperties = localAppListCacheProperties;
        this.listener = listener;
    }

    public void put(String appName, Application application) {
        if (CollectionUtils.isEmpty(application.getInstances())) {
            return;
        }
        EvictingQueue<Application> queueTmp = tmpCacheMap.computeIfAbsent(appName.toUpperCase(), k -> EvictingQueue.create(localAppListCacheProperties.getInstanceSnapshotNum() - 1));
        List<Application> list = Lists.newCopyOnWriteArrayList();

        Object o = lockMap.computeIfAbsent(appName.toUpperCase(), k -> new Object());
        //todo
        synchronized (o) {
            queueTmp.add(application);
            log.info("{}===>{}",appName,application.hashCode());
            // toArray 是通过copy 新建的数组实例
            Application[] listTmp = queueTmp.toArray(new Application[0]);
            List<Application> applicationList = Lists.newArrayList(listTmp);
            // 数组反转
            Collections.reverse(applicationList);
            list.addAll(applicationList);
            cacheMap.put(appName, list);
        }
    }

    @Override
    public List<DiscoveryEnabledServer> getServerList(String appName, Boolean isSecure,List<DiscoveryEnabledServer> acquiredList) {
        List<DiscoveryEnabledServer> discoveryEnabledServers = Lists.newArrayList();

        if (listener.getServiceSnapshotSwitch() == 0) {
            // none
            return acquiredList;
        } else if (listener.getServiceSnapshotSwitch() == 1) {
            // 如果已获取列表为空，则去快照服务列表
            if (!CollectionUtils.isEmpty(acquiredList)){
                return acquiredList;
            }
            discoveryEnabledServers = getServerListFromSnapshot(appName, isSecure);
        } else if (listener.getServiceSnapshotSwitch() == 2) {
            // 不管已获取的list表，只取快照的
            discoveryEnabledServers = getServerListFromSnapshot(appName, isSecure);

        }

        return discoveryEnabledServers;
    }



    private List<DiscoveryEnabledServer> getServerListFromSnapshot(String appName, Boolean isSecure) {
        log.debug("使用service snapshot 缓存负载server，appName={}", appName);
        List<DiscoveryEnabledServer> discoveryEnabledServers = Lists.newArrayList();
        Object o = lockMap.computeIfAbsent(appName.toUpperCase(), k -> new Object());
        Application application = null;
        synchronized (o) {
            List<Application> list = cacheMap.get(appName.toUpperCase());
            Integer index = listener.getIndexMap().computeIfAbsent(appName.toUpperCase(), v->0);

            log.info("使用service snapshot 缓存负载流量，appName={},index={},list={}",appName,index,list);
            //
            if (CollectionUtils.isEmpty(list)) {
                return discoveryEnabledServers;
            }
            if (index > list.size() - 1) {
                index = 0;
            }
            application = list.get(index);
        }
        if (application == null || CollectionUtils.isEmpty(application.getInstances())){
            return  discoveryEnabledServers;
        }

        for (InstanceInfo instanceInfo : application.getInstances()) {
            DiscoveryEnabledServer server = new DiscoveryEnabledServer(instanceInfo, isSecure);
            server.setAlive(true);
            DomainExtractingServer domainExtractingServer = new DomainExtractingServer(server, isSecure, false, true);
            discoveryEnabledServers.add(domainExtractingServer);
        }
        log.info("使用service snapshot 缓存负载流量，appName={},选择的快照result={}",appName,discoveryEnabledServers);

        return discoveryEnabledServers;
    }

}
