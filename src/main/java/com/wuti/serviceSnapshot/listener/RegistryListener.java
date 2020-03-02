package com.wuti.serviceSnapshot.listener;

import org.springframework.context.ApplicationEvent;

/**
 * 发现新服务，更新路由信息
 *
 * @author hschen
 */
public interface RegistryListener {

    void onEvent(ApplicationEvent applicationEvent);

}
