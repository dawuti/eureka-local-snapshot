package com.wuti.serviceSnapshot.cache;

import com.netflix.discovery.shared.Application;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;

import java.util.List;

public interface APPListService {

    void put(String appName, Application application);

    List<DiscoveryEnabledServer> getServerList(String appName, Boolean isSecure, List<DiscoveryEnabledServer> acquiredList);

}
