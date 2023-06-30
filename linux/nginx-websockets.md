# 配置 Nginx 支持 websockets 

nginx
# 启用 websockets 支持
location / {
    ...
    proxy_http_version 1.1;
    proxy_set_header Upgrade $http_upgrade; 
    proxy_set_header Connection "upgrade";
}

# TCP 连接超时  
proxy_read_timeout 420s;  # TCP 读取超时,默认60s  
proxy_send_timeout 420s; # TCP 写超时,默认60s 

# TCP 链接复用
proxy_set_header Connection "";
proxy_buffering off; # 关闭代理缓存


建议:

- 合理设置 proxy_read_timeout 和 proxy_send_timeout 以防止长时间不活跃的链接,比如420s  
- 关闭代理缓存 proxy_buffering off,对 websockets 来说缓存是无效的
- 不设置 Connection 头,让 Nginx 和后端自行处理链接复用
- 选择长连接 keepalive 来提高性能


当使用Nginx配置WebSocket支持时，以下是一些建议，涉及TCP超时和连接复用：

1. 增加TCP超时时间：WebSocket连接通常需要更长的超时时间，以允许客户端和服务器之间的持久连接。你可以通过在Nginx配置中设置`proxy_connect_timeout`和`proxy_read_timeout`的值来增加超时时间。例如：
   ```
   proxy_connect_timeout 7d;
   proxy_read_timeout 7d;
   ```

2. 启用连接复用：为了提高性能，可以启用Nginx的连接复用功能。这可以通过设置`keepalive`参数来实现。你可以在Nginx配置的`http`块中添加以下指令来启用连接复用：
   ```
   upstream backend {
       server backend.example.com;
       keepalive 64;
   }
   ```

   这将启用64个保持活动连接。你还可以根据需要调整`keepalive_timeout`的值。

请注意，这些配置是针对代理WebSocket连接的情况。如果你希望Nginx作为WebSocket服务器，你需要使用`proxy_pass`指令并将WebSocket连接代理到适当的后端服务器。

`keepalive` 是一个网络协议中的概念，用于保持客户端和服务器之间的长连接以减少资源的消耗和提高性能。

在上下文中，`keepalive` 是用于配置Nginx的连接复用功能的参数。当启用连接复用时，Nginx会保持与后端服务器的一定数量的持久连接，这些连接可以被重复使用，而不需要每次请求都重新建立连接。这种重用连接的机制可以显著减少连接的建立和断开的开销，从而提高性能。

`keepalive` 参数用于指定要保持的活动连接的数量。例如，如果将`keepalive` 设置为64，Nginx将保持最多64个与后端服务器的活动连接。这意味着在一段时间内，如果有更多的请求到达，Nginx会重复使用这些连接，而不是为每个请求创建新的连接。这减少了连接的建立和断开所带来的开销。

同时，还可以通过设置`keepalive_timeout`参数来定义连接的超时时间。如果一段时间内没有新的请求到达，连接将被关闭以释放资源。通过适当配置`keepalive_timeout`，可以平衡连接的持久性和资源的释放。

总之，`keepalive` 参数允许Nginx保持与后端服务器的一定数量的持久连接，以提高性能和减少连接的建立和断开开销。

当配置WebSocket支持时，以下是一些优化建议，以提高Nginx的性能和WebSocket的稳定性：

1. 启用TCP_NOPUSH：通过启用`tcp_nopush`参数，可以减少发送网络数据的延迟，提高性能。将以下指令添加到Nginx配置的`http`块中：
   ```
   tcp_nopush on;
   ```

2. 调整工作进程数：根据服务器的硬件配置和负载情况，适当调整Nginx的工作进程数。可以通过在Nginx配置的`events`块中设置`worker_processes`参数来完成。例如：
   ```
   worker_processes auto;
   ```

   使用`auto`值将根据可用的CPU核心数自动设置进程数。

3. 配置文件描述符限制：WebSocket连接可能需要大量的文件描述符。确保操作系统的文件描述符限制足够高，以支持预期的并发连接数。你可以通过增加操作系统的`nofile`限制或修改`nginx.conf`文件中的`worker_rlimit_nofile`参数来实现。

4. 使用更高的端口范围：默认情况下，Nginx使用1024以下的端口需要特权访问。如果WebSocket服务需要在低于1024的端口上运行，可以考虑将Nginx配置为以特权用户身份运行。然后，使用`setcap`命令将可执行文件设置为具有网络绑定能力。

5. 启用Gzip压缩：启用Gzip压缩可以减小数据传输的大小，提高网络传输效率。使用以下指令在Nginx配置中启用Gzip压缩：
   ```
   gzip on;
   gzip_comp_level 5;
   gzip_min_length 256;
   gzip_proxied any;
   gzip_types application/javascript application/json application/octet-stream;
   ```

   这些指令将压缩JavaScript、JSON和二进制文件，并设置压缩级别和最小长度。

这些优化建议可以提高Nginx在配置WebSocket支持时的性能和稳定性。根据你的具体需求和服务器环境，你可以选择适用的优化配置。

# Nginx 配置 WebSocket 优化建议

markdown
- 使用最新的 Nginx 版本,它支持最新的 WebSocket 特性

- 适当提高 worker_connections 的数量,WebSocket 连接较多时会需要

- 合理配置 send/read 超时时间,比如设置为多个小时

- 关闭 gzip 压缩, WebSocket 数据通常已经被压缩

- 关闭限速,WebSocket 传输需要即时性

- 使用长连接 keepalive 

- 增加 upstream 后端服务器数,提高并发处理能力

- 监控 WebSocket 连接数,根据负载动态调整 Nginx  worker_connections 和 upstream 服务器数


总的来说,要满足 WebSocket 的实时性和高并发特点,关键是:

- 足够的连接数
- 合理的超时配置
- 关闭压缩和限速
- 扩展后端服务器
- 动态调整参数

监控指标可以包括:连接数、CPU 利用率、内存利用率等,按需调整 Nginx 和后端规模。

