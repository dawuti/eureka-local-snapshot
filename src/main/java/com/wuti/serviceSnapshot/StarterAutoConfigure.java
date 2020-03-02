package com.wuti.serviceSnapshot;



import com.wuti.serviceSnapshot.apollo.ApolloListener;
import com.wuti.serviceSnapshot.cache.APPListCache;
import com.wuti.serviceSnapshot.cache.APPListService;
import com.wuti.serviceSnapshot.listener.ApplicationEventEurekaListener;
import com.wuti.serviceSnapshot.listener.EurekaRegistryListener;
import com.wuti.serviceSnapshot.listener.RegistryListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@Configuration
@ConditionalOnClass(RegistryListener.class)
@EnableConfigurationProperties({LocalAppListCacheProperties.class})
@RibbonClients(defaultConfiguration = ServiceSnapshotAutoConfigure.class)
@EnableAspectJAutoProxy
@Slf4j
public class StarterAutoConfigure {

    @Bean
    @ConditionalOnMissingBean
//    @ConditionalOnProperty(prefix = "example.service", value = "enabled", havingValue = "true")
    RegistryListener starterService(APPListService appListService, LocalAppListCacheProperties properties,ApolloListener apolloListener) {
        return new EurekaRegistryListener(appListService,properties,apolloListener);
    }

    @Bean
    @ConditionalOnMissingBean
    APPListService APPListService(LocalAppListCacheProperties properties, ApolloListener apolloListener) {
        return new APPListCache(properties,apolloListener);
    }

    @Bean
    @ConditionalOnMissingBean
    ApplicationEventEurekaListener RegistryListener(RegistryListener registryListener) {
        return new ApplicationEventEurekaListener(registryListener);
    }
    @Bean
    @ConditionalOnMissingBean
    ApolloListener apolloListener() {
        return new ApolloListener();
    }


}
