package com.wuti.serviceSnapshot.ribbon;

import com.wuti.serviceSnapshot.cache.APPListService;
import com.google.common.collect.Lists;
import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ServerList;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.eureka.DomainExtractingServerList;

import java.util.List;

@Slf4j
public class ServiceSnapshotDomainExtractingServerList extends DomainExtractingServerList {

    private IClientConfig clientConfig;
    private Boolean isSecure = null;

    @Autowired
    private APPListService appListService;


    public ServiceSnapshotDomainExtractingServerList(ServerList<DiscoveryEnabledServer> list, IClientConfig clientConfig, boolean approximateZoneFromHostname) {
        super(list, clientConfig, approximateZoneFromHostname);
        this.clientConfig = clientConfig;
    }

    @Override
    public List<DiscoveryEnabledServer> getInitialListOfServers() {
        List<DiscoveryEnabledServer> list = super.getInitialListOfServers();
        list = getServerList(list);
        return list;
    }

    private List<DiscoveryEnabledServer> getServerList(List<DiscoveryEnabledServer> acquiredList) {
        List<DiscoveryEnabledServer> list = Lists.newArrayList();

        String clientName = clientConfig.getClientName();
        if (isSecure == null) {
            isSecure = Boolean.parseBoolean("" + clientConfig.getProperty(CommonClientConfigKey.IsSecure, "false"));
        }
        log.debug("ServiceSnapshotDomainExtractingServerList备用缓存，appName={}", clientName);
        list.addAll(appListService.getServerList(clientName, isSecure, acquiredList));

        return list;
    }

    @Override
    public List<DiscoveryEnabledServer> getUpdatedListOfServers() {
        List<DiscoveryEnabledServer> list = super.getUpdatedListOfServers();
        list = getServerList(list);
        return list;
    }
}
