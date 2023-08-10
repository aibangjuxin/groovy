在Kong的配置中，CP和DP分别代表Control Plane和Data Plane，它们是Kong的架构组件。CP负责管理和配置Kong节点，而DP则处理实际的API流量。CP和DP之间通过WSS（WebSocket Secure）协议进行通信，确保配置的同步和实时性。

CP（Control Plane）负责以下工作：
1. 管理API和插件的配置信息。
2. 接收来自管理员的配置更改请求，如添加、更新或删除API、插件等。
3. 将配置变更传播给DP，以便应用到实际的API流量处理。
4. 提供管理界面（例如Kong的Admin API）供管理员操作。

DP（Data Plane）负责以下工作：
1. 处理实际的API流量，包括请求的转发、鉴权、限流等。
2. 从CP接收配置信息，并根据配置进行流量处理。
3. 向CP报告节点的健康状况和统计数据，以帮助监控和决策。

关于WebSocket支持，Kong高级版本中确实支持WebSocket的API。您可以通过Kong的插件来启用WebSocket支持，例如"websocket-termination"插件。这使得Kong能够处理WebSocket连接，以及在WebSocket连接上执行插件功能，如认证、鉴权、限流等。

总之，CP和DP在Kong中扮演着不同的角色，CP负责配置和管理，DP负责实际的API流量处理，它们通过WSS协议进行通信以保持同步。高级版本的Kong支持WebSocket的API，并且您可以通过插件来启用和配置WebSocket支持功能。