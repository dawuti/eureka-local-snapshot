package com.wuti.serviceSnapshot.listener;



import com.wuti.serviceSnapshot.LocalAppListCacheProperties;
import com.wuti.serviceSnapshot.apollo.ApolloListener;
import com.wuti.serviceSnapshot.cache.APPListService;
import com.netflix.discovery.shared.Applications;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.eureka.CloudEurekaClient;
import org.springframework.context.ApplicationEvent;

/**
 * 加载服务路由，eureka实现
 *
 * @author hschen
 */
@Slf4j
public class EurekaRegistryListener implements RegistryListener {


    private Long lastTriggerTime = 0L;

    private APPListService appListService;
    private LocalAppListCacheProperties localAppListCacheProperties;
    private ApolloListener apolloListener;

    public EurekaRegistryListener(APPListService appListService, LocalAppListCacheProperties localAppListCacheProperties, ApolloListener apolloListener) {
        this.appListService = appListService;
        this.localAppListCacheProperties = localAppListCacheProperties;
        this.apolloListener = apolloListener;
    }

    /**
     * 注册中心触发事件，可以从中获取服务<br>
     * <p>
     * 这个方法做的事情有2个：<br>
     * <p>
     * 1. 找出新注册的服务，调用pullRoutes方法<br>
     * 2. 找出删除的服务，调用removeRoutes方法<br>
     *
     * @param applicationEvent 事件体
     */
    @Override
    public void onEvent(ApplicationEvent applicationEvent) {

        Long currentTimeMillis = System.currentTimeMillis();
        if (lastTriggerTime == 0L) {
            lastTriggerTime = currentTimeMillis;
        }else {
            if (currentTimeMillis - lastTriggerTime < apolloListener.getTriggerHeartBeatEventInterval() * 1000) {
                return;
            }
        }

        lastTriggerTime = currentTimeMillis;
        Object source = applicationEvent.getSource();
        CloudEurekaClient cloudEurekaClient = (CloudEurekaClient) source;
        Applications applications = cloudEurekaClient.getApplications();

        applications.getRegisteredApplications().forEach(application -> {
            log.info("ApplicationEvent,name={}", application.getName());
            appListService.put(application.getName(), application);
        });

    }

}
