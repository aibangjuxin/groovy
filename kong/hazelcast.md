Hazelcast是一个开源的in-memory数据网格(data grid)技术。它提供了以下主要功能:

- 分布式内存数据存储 - Hazelcast可以在多个服务器之间分配数据,实现高可用的内存数据存储。

- 数据分区 - 支持将数据分区到不同的节点上,实现可扩展性。

- 内存计算 - 在内存数据上进行计算和聚合操作。 

- 事件处理 - 通过publish/subscribe支持分布式事件处理。

- 分布式锁 - 提供分布式锁来 coordinate 对共享资源的访问。

- 集群管理 - 自动发现集群成员, 并处理节点加入、移除等事件。

如果Hazelcast和Kong组合使用,可以提供以下应用场景:

- 缓存 - Hazelcast可以作为Kong的本地缓存,加速读取速度。

- 会话存储 - 可以将用户会话数据存储在Hazelcast中,实现会话共享。

- 分布式锁 - Kong可以使用Hazelcast的分布式锁来协调对后端服务的访问。

- 负载均衡 - 根据Hazelcast中的数据分区来实现请求的负载均衡。

- 高可用 - Hazelcast可以在多个Kong节点之间同步数据,如果单节点故障可以快速切换。

- 混合部署 - 可以将Kong和Hazelcast混合部署在同一集群中,降低延迟。

综上,Hazelcast可以为Kong提供缓存、会话存储、分布式锁、负载均衡等功能,提高Kong的可扩展性、可用性和性能。两者集成可以实现更强大的API网关解决方案。

Hazelcast可以与Kong的rate limiting插件结合使用,提供分布式限流功能。

主要的实现思路是:

1. 在Hazelcast集群中维护限流规则,比如设置某个API的请求限流阈值是100 QPS。

2. Kong的rate limiting插件可以集成Hazelcast client,在收到请求时先查询Hazelcast获取当前接口的限流信息。

3. 根据限流规则,判断当前请求是否超过阈值,如果超过则可以拒绝请求,否则允许请求。

4. 在请求通过后,可以通过Hazelcast提供的分布式计数器和锁来保证原子地增加计数。

5.  Periodically重置计数器,开始新一轮的计数。

通过这种方式,限流计数器和规则维护在Hazelcast集群中,Kong Plugins从Hazelcast获取限流规则。即使Kong节点扩容也可以确保限流计数准确。

相比本地计数,使用Hazelcast实现分布式限流可以实现集群间限流规则一致性,且在Kong扩容时也可以平滑过渡,非常适合大流量场景下的限流需求。
