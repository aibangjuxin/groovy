为了实现更精细的控制和管理上传文件和请求体的大小限制，可以采用以下几种方案：

### 方案一：Nginx 配置多个 `location` 块

通过在不同的 `location` 块中设置不同的 `client_max_body_size`，来针对不同的URL路径实现不同的限制。

```nginx
http {
    server {
        listen 80;

        # 全局默认的 client_max_body_size
        client_max_body_size 10M;

        location /upload {
            # 对于 /upload 路径，限制为 50M
            client_max_body_size 50M;
        }

        location /api {
            # 对于 /api 路径，限制为 20M
            client_max_body_size 20M;
        }

        location / {
            # 默认的 client_max_body_size 10M
        }
    }
}
```

### 方案二：使用 Lua 或者 Nginx 的第三方模块

使用 Lua 或者 Nginx 的第三方模块（如 `ngx_http_lua_module`）来进行更精细的控制。

#### 使用 Lua 进行请求体大小限制

需要安装 `ngx_http_lua_module` 模块。假设已经安装了 Lua 模块，可以在 `nginx.conf` 中配置如下：

```nginx
http {
    lua_shared_dict limits 10m;

    init_by_lua_block {
        local limit_req = require "resty.limit.req"
        local limit = limit_req.new("limits", 200, 100)
    }

    server {
        listen 80;

        location /upload {
            client_max_body_size 50M;

            access_by_lua_block {
                local limit_req = require "resty.limit.req"
                local lim, err = limit_req.new("limits", 200, 100)
                if not lim then
                    ngx.log(ngx.ERR, "failed to instantiate a resty.limit.req object: ", err)
                    return ngx.exit(500)
                end

                local key = ngx.var.binary_remote_addr
                local delay, err = lim:incoming(key, true)
                if not delay then
                    if err == "rejected" then
                        return ngx.exit(429)
                    end
                    ngx.log(ngx.ERR, "failed to limit req: ", err)
                    return ngx.exit(500)
                end

                if delay >= 0.001 then
                    ngx.sleep(delay)
                end
            }
        }

        location /api {
            client_max_body_size 20M;

            access_by_lua_block {
                local limit_req = require "resty.limit.req"
                local lim, err = limit_req.new("limits", 200, 100)
                if not lim then
                    ngx.log(ngx.ERR, "failed to instantiate a resty.limit.req object: ", err)
                    return ngx.exit(500)
                end

                local key = ngx.var.binary_remote_addr
                local delay, err = lim:incoming(key, true)
                if not delay then
                    if err == "rejected" then
                        return ngx.exit(429)
                    end
                    ngx.log(ngx.ERR, "failed to limit req: ", err)
                    return ngx.exit(500)
                end

                if delay >= 0.001 then
                    ngx.sleep(delay)
                end
            }
        }
    }
}
```

### 方案三：使用 Nginx 的 `limit_except` 指令

如果只需限制特定请求方法（如 POST 和 PUT），可以使用 `limit_except` 指令。

```nginx
http {
    server {
        listen 80;

        location / {
            client_max_body_size 10M;

            limit_except GET {
                client_max_body_size 50M;
            }
        }

        location /upload {
            limit_except GET {
                client_max_body_size 50M;
            }
        }

        location /api {
            limit_except GET {
                client_max_body_size 20M;
            }
        }
    }
}
```

### 方案四：使用 API 网关（如 Kong）

通过使用 API 网关（如 Kong）来实现更精细的控制。Kong 提供了丰富的插件，可以用于请求体和文件上传的大小限制。

#### 安装并配置 Kong

假设已经安装并运行了 Kong，可以通过 Kong 管理接口配置请求大小限制。

```bash
# 为指定服务或路由添加 request-size-limiting 插件
curl -i -X POST http://localhost:8001/services/{service}/plugins \
    --data "name=request-size-limiting" \
    --data "config.allowed_payload_size=50"  # 单位：MB
```

### 方案五：后端应用层面控制

在后端应用程序中进行控制，确保接收到的请求体和文件大小在合理范围内。

#### 示例（以 Python Flask 为例）

```python
from flask import Flask, request, abort

app = Flask(__name__)

@app.route('/upload', methods=['POST'])
def upload():
    if request.content_length > 50 * 1024 * 1024:
        abort(413, 'File too large')
    # 文件处理逻辑
    return 'File uploaded successfully'

@app.route('/api', methods=['POST'])
def api():
    if request.content_length > 20 * 1024 * 1024:
        abort(413, 'Request body too large')
    # 请求处理逻辑
    return 'Request processed successfully'

if __name__ == '__main__':
    app.run(debug=True)
```

### 方案对比

- **Nginx 配置多个 `location` 块**：适合简单的路径基于限制，不需要额外模块。
- **Lua 模块**：适合需要更细粒度控制的场景，但需要安装和配置 Lua 模块。
- **`limit_except` 指令**：适合基于HTTP方法的限制，但控制粒度较粗。
- **API 网关（Kong）**：提供丰富的功能和插件，适合大型应用和多服务场景。
- **后端应用层面控制**：灵活但需要开发和维护代码。

根据你的需求和环境，选择合适的方案来实现对上传文件和请求体大小的精细控制。