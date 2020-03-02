package com.wuti.serviceSnapshot.cache;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import org.springframework.cloud.netflix.ribbon.eureka.ZoneUtils;

public class DomainExtractingServer extends DiscoveryEnabledServer {

    private String id;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public DomainExtractingServer(DiscoveryEnabledServer server, boolean useSecurePort,
                                  boolean useIpAddr, boolean approximateZoneFromHostname) {
        // host and port are set in super()
        super(server.getInstanceInfo(), useSecurePort, useIpAddr);
        if (server.getInstanceInfo().getMetadata().containsKey("zone")) {
            setZone(server.getInstanceInfo().getMetadata().get("zone"));
        }
        else if (approximateZoneFromHostname) {
            setZone(ZoneUtils.extractApproximateZone(server.getHost()));
        }
        else {
            setZone(server.getZone());
        }
        setId(extractId(server));
        setAlive(server.isAlive());
        setReadyToServe(server.isReadyToServe());
    }

    private String extractId(Server server) {
        if (server instanceof DiscoveryEnabledServer) {
            DiscoveryEnabledServer enabled = (DiscoveryEnabledServer) server;
            InstanceInfo instance = enabled.getInstanceInfo();
            if (instance.getMetadata().containsKey("instanceId")) {
                return instance.getHostName()+":"+instance.getMetadata().get("instanceId");
            }
        }
        return super.getId();
    }
}
