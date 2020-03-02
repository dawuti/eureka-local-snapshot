package com.wuti.serviceSnapshot;


import com.wuti.serviceSnapshot.ribbon.ServiceSnapshotDomainExtractingServerList;
import com.netflix.client.config.IClientConfig;
import com.netflix.discovery.EurekaClient;
import com.netflix.loadbalancer.ServerList;
import com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.PropertiesFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Provider;

/**
 *  该bean是在applicationContext中的，一个ribbonclient有一个独立的context，该类需要配置在
 */
@Configuration
@Slf4j
public class ServiceSnapshotAutoConfigure {

    @Autowired
    private PropertiesFactory propertiesFactory;

    @Bean
    public ServerList<?> ribbonServerList(IClientConfig config, Provider<EurekaClient> eurekaClientProvider) {
        if (this.propertiesFactory.isSet(ServerList.class, config.getClientName())) {
            return this.propertiesFactory.get(ServerList.class, config, config.getClientName());
        }
        DiscoveryEnabledNIWSServerList discoveryServerList = new DiscoveryEnabledNIWSServerList(config, eurekaClientProvider);
        ServiceSnapshotDomainExtractingServerList serverList = new ServiceSnapshotDomainExtractingServerList(
                discoveryServerList, config, true);

        return serverList;
    }

}
