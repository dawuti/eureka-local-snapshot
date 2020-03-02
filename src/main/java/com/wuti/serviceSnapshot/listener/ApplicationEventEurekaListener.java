package com.wuti.serviceSnapshot.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;

/**
 * @author hschen
 */
@Slf4j
public class ApplicationEventEurekaListener {


    private RegistryListener registryListener;

    public ApplicationEventEurekaListener(RegistryListener registryListener) {
        this.registryListener = registryListener;
    }

    /**
     * eureka 事件监听
     *
     * @param heartbeatEvent
     */
    @EventListener(classes = HeartbeatEvent.class)
    public void listenEvent(ApplicationEvent heartbeatEvent) {
        log.debug("监听事件触发={}",heartbeatEvent);
        registryListener.onEvent(heartbeatEvent);
    }


}
