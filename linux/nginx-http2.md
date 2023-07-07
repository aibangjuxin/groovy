# Nginx http/2
要配置Nginx支持HTTP/2协议，你需要执行以下步骤：

1. 确保你的Nginx版本支持HTTP/2。HTTP/2在Nginx版本1.9.5及以上得到支持。

2. 在Nginx的配置文件中，找到监听HTTP请求的`server`块，并确保添加`listen`指令并指定端口号，例如：
   ```
   listen 443 ssl http2;
   ```

3. 在配置中启用SSL/TLS加密以支持HTTPS连接。你需要配置SSL证书和私钥。

4. 重新加载Nginx配置，以使更改生效。

## 配置Nginx支持HTTP/2协议的好处包括：

1. 性能改进：HTTP/2采用二进制协议、多路复用和头部压缩等技术，可以减少网络延迟、提高网页加载速度和性能。

2. 并发处理：HTTP/2支持并行请求和响应，允许服务器同时发送多个请求，提高了并发处理能力。

3. 单一连接：HTTP/2可以在单个连接上处理多个请求和响应，避免了建立和维护多个连接的开销，减少了资源消耗。
## summary 
HTTP/2和WebSocket之间的关系是，HTTP/2可以在同一连接上同时支持WebSocket和其他HTTP请求。因此，如果你已经配置了支持HTTP/2的Nginx服务器，它也将能够处理WebSocket请求，无需额外的配置。这使得在同一端口上同时支持HTTP请求和WebSocket连接变得更加方便和高效。

当Nginx服务器配置支持HTTP/2时，它默认也会支持WebSocket，无需单独进行WebSocket配置。HTTP/2协议在设计上可以同时支持传统的HTTP请求和WebSocket连接。

因此，当使用HTTP/2时，你无需再单独配置以下指令来支持WebSocket：/其实配置应该也是可以的

```yaml
proxy_set_header Upgrade $http_upgrade;
proxy_set_header Connection "upgrade";
```

这是因为HTTP/2已经内置了对WebSocket的支持，它使用自己的帧和流控制机制来处理WebSocket通信。这样，你只需将Nginx配置为支持HTTP/2，就能够自动支持WebSocket，无需额外的配置。

请注意，你仍然需要正确配置SSL/TLS加密以支持HTTPS连接，并将Nginx的监听端口指定为HTTP/2。同时，确保你的应用程序和后端服务器也正确处理和支持WebSocket连接。

## enhance config
针对已配置且支持HTTP/2的Nginx，以下是一些优化建议和特殊配置需要注意的方面：

1. 调整HTTP/2并发设置：HTTP/2支持多路复用，可以并行处理多个请求。你可以通过调整`http2_max_concurrent_streams`参数来控制同时处理的并发请求数。根据你的服务器资源和负载情况，可以适当增加或减少该值。

2. 启用服务器推送（Server Push）：HTTP/2支持服务器推送，在请求响应中主动推送与页面相关的资源。这可以减少客户端发起的额外请求，加快页面加载速度。你可以使用Nginx的`http2_push`指令来配置服务器推送。

3. 配置缓存策略：对于经常请求的资源，可以配置Nginx的缓存策略，以减少服务器的负载和提高响应速度。你可以使用`proxy_cache`指令来启用代理缓存，或者使用其他缓存模块，如`fastcgi_cache`、`uwsgi_cache`等。

4. 适当调整缓冲区大小：HTTP/2协议使用二进制帧进行通信，可以适当调整Nginx的缓冲区大小以优化性能。可以通过配置`http2_recv_buffer_size`和`http2_send_buffer_size`来调整接收和发送缓冲区的大小。

5. 启用HTTP/2服务器推送优化：如果你的后端应用程序已经支持HTTP/2服务器推送，可以通过Nginx的`http2_push_preload`指令启用预加载，以提高推送资源的加载速度和效率。

6. 使用HTTP/2性能优化工具：有一些专门针对HTTP/2性能优化的工具可供使用，例如HPACK压缩算法的优化工具、优化HTTP/2流量的流量控制工具等。可以根据具体需求考虑使用这些工具。

除了上述优化建议外，确保服务器硬件配置足够强大、合理配置SSL/TLS加密参数以提高安全性，定期监测和调整服务器性能也是保持HTTP/2性能优化的关键。


提供一个示例的Nginx配置文件（nginx.conf），并对其中的一部分进行解释。请注意，以下配置是一个简化的示例，你可能需要根据你的具体需求进行适当的修改和补充。

```yaml
# 全局配置
worker_processes auto;
pid /run/nginx.pid;

# 设置事件模型和连接数
events {
    worker_connections 1024;
}

# HTTP配置
http {
    # 定义HTTP/2监听端口
    server {
        listen 443 ssl http2;
        server_name example.com;

        # 配置SSL/TLS加密
        ssl_certificate /path/to/certificate.crt;
        ssl_certificate_key /path/to/private.key;

        # 配置HTTP/2特定参数
        http2_max_concurrent_streams 128;
        http2_push_preload on;

        # 配置缓存
        proxy_cache_path /path/to/cache levels=1:2 keys_zone=my_cache:10m max_size=10g inactive=60m;

        # 配置代理和反向代理
        location / {
            proxy_pass http://backend_server;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection "upgrade";
            proxy_set_header Host $host;
        }
    }
}
```

解释：

1. 在全局配置中，`worker_processes`指定了Nginx使用的工作进程数，`pid`定义了Nginx主进程的PID文件路径。

2. 在事件配置块中，`worker_connections`指定了每个工作进程的最大连接数。

3. 在HTTP配置块中，首先定义了一个使用HTTP/2的服务器监听端口。通过`listen`指令指定端口和协议。

4. 配置SSL/TLS加密，需要指定相应的证书和私钥的路径。

5. 设置HTTP/2特定的参数，如`http2_max_concurrent_streams`用于控制并发处理的请求数量，`http2_push_preload`用于启用服务器推送的预加载。

6. 配置缓存路径和参数，使用`proxy_cache_path`指令定义缓存路径和相关参数，如缓存大小、过期时间等。

7. 在`location`块中，配置代理和反向代理。`proxy_pass`指令定义了后端服务器的地址，`proxy_set_header`指令用于设置请求头，包括`Upgrade`和`Connection`字段，以支持WebSocket连接。

这只是一个简单的示例，你需要根据你的具体情况和需求进行更详细的配置。同时，还需要确保你的后端服务器正确处理WebSocket连接，并根据实际需求进行其他配置项的添加和调整。