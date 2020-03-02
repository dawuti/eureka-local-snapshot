# eureka-local-snapshot
spring-cloud eureka 本地缓存实例信息，预防eureka假死

可以打成spring-boot-starter使用

1 监听HeartbeatEvent的事件，获取所有的服务的实例列表，存储在本地

2 扩展DomainExtractingServerList类，创建ServiceSnapshotDomainExtractingServerList，在getServerList时么有获取到实例列表的时候，在存储在本地的快照中获取

3 Map<String, EvictingQueue<Application>> tmpCacheMap = Maps.newConcurrentMap();来存储，后进先出

4 监听EnvironmentChangeEvent获取配置变量
  ${service.snapshot.switch:1}：// 0:不走缓存   1：如果acquireList为空，则查询缓存   2：走缓存（测试用，生产不建议使用）
  ${service.snapshot.HeartBeatEventInterval:60}：  缓存周期
  ${service.snapshot.index:}： 获取新实例的顺序

