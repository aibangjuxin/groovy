在Kong中，"route"和"service"是两个核心概念，用于定义API的路由和服务。

- **Route（路由）**：它定义了客户端如何访问Kong中的API。路由规定了API的路径、主机名、协议等信息，并将客户端请求转发到相应的后端服务。一个路由可以与一个或多个服务相关联。

- **Service（服务）**：它代表了一个后端服务或API的实例。服务定义了实际的服务地址、端口、协议等信息，Kong会将客户端请求转发到这些服务。

以下是一个关于route和service的JSON配置示例：

```json
{
  "services": [
    {
      "name": "example-service",
      "url": "http://example.com",
      "port": 80,
      "protocol": "http",
      "routes": [
        {
          "name": "example-route",
          "paths": ["/api"],
          "methods": ["GET", "POST"],
          "strip_path": false
        }
      ]
    }
  ]
}
```

这个示例中定义了一个名为"example-service"的服务，它指向"http://example.com"，使用HTTP协议，监听端口80。同时，这个服务关联了一个路由，该路由的路径为"/api"，允许的请求方法为GET和POST，而且不会剥离路径。

希望这个示例对你有帮助！


是的,Kong网关允许在一条路由上绑定多个域名。这可以通过几种方式实现:

1. 在路由的hosts字段指定多个域名:
json
{
    "hosts": ["http://example.com", "http://www.example.com"] 
}
这样访问example.com和www.example.com的请求都会匹配该路由。

2. 使用通配符*来匹配一级域名下的所有子域名:
json 
{
    "hosts": "*.http://example.com" 
}
http://xn--example-2n7or76z.com,http://www.example.com,api.example.com等域名的请求都会匹配。

3. 在多个路由上指定相同的服务和路由:
json
{
    "hosts": ["http://example.com"],
    "service": { "id": "service-id" },
    "routes": [ { "id": "route-id" } ]
}

{
    "hosts": ["http://www.example.com"], 
    "service": { "id": "service-id" },
    "routes": [ { "id": "route-id" } ]
}
虽然这不是在同一条路由上绑定多个域名,但由于路由和服务相同,所以访问两个域名的效果也是一样的。

所以综上,Kong网关支持在一条路由(或多条路由)上绑定多个域名,可以通过hosts字段指定多个域名,使用通配符或在多个路由上指定相同的服务和路由等方式实现。这允许我们在Kong上为多个域名代理到后端的API。

请让我知道如果有任何其他问题!

在Kong网关中,路由(Route)和服务(Service)有以下概念:

- 服务(Service):表示一个后端API或微服务。Kong网关通过服务将请求代理到后端API。一个服务可以有多个路由。

- 路由(Route):表示请求的路径及域名。路由将请求映射到一个服务。多个路由可以指向同一个服务。

那么空的路由和服务是什么意思呢?

- 空服务(Empty Service):是一个“空壳”服务,仅有服务名称但没有实际的后端API连接信息。空服务主要用于暂存。之后可以添加路由和填充服务信息将请求代理到后端API。

- 空路由(Empty Route):是一个“空壳”路由,仅有路由名称、路径和域名信息但没有实际指向的服务。访问空路由时会返回404。空路由主要用于提前占位,之后可以绑定到服务开始使用。

所以空的路由和服务本身不具有实际的转发和代理功能。它们主要作为占位符,用于服务和路由的预留和暂存,之后可以填充详细信息和关联实际的服务或路由使用。

使用空路由和空服务的好处是:

1. 可以预先命名和预留路由路径及域名,防止之后产生冲突。

2. 可以先设计路由和服务结构,之后逐步开发和上线实际API。

3. 方便管理和变更。空的路由和服务很容易重新关联到其他服务或路由。

总之,空路由和空服务为Kong的服务和路由管理提供了灵活性,可以很容易地调整和演进。

请让我知道如果还有其他疑问!


对,我理解您说的是问Kong Control Plane(即Kong Manager)端的空路由和空服务。

在Kong Control Plane,服务(Service)和路由(Route)的概念如下:

- 服务:定义后端运行的API或微服务信息,例如URL,负载均衡算法等。Kong CP会将服务的配置同步到Kong Data Plane的节点上。

- 路由:定义请求的路径,域名,服务ID等。Kong CP同样会将路由的配置同步到Kong DP的节点上。

那么在Kong Control Plane端,空路由和空服务的含义是:

- 空服务:在Kong CP中创建的服务,但是并未指定任何实际的后端API信息,例如URL是空的。空服务本身不会对请求产生任何代理或转发作用。它仅作为占位符存在,供路由或其他服务引用。

- 空路由:在Kong CP中创建的路由,但是并未指定指向任何实际服务的服务ID。访问空路由不会产生任何代理或转发,并返回404。空路由也仅作为占位符存在,之后可以关联到实际服务开始生效。

所以在Kong CP端,空路由和空服务的作用也主要是占位符和预留。它们可以:

1. 提前占位路由路径和域名,防止之后产生冲突。

2. 先设计 Kong 配置的结构和框架,然后逐步开发和部署实际的 API 和微服务。

3. 可以随时重新绑定到其他服务或路由,因此提供了灵活性和可变更性。

Kong CP端的路由和服务的配置最终会同步到Kong DP的节点上。所以在填充实际的服务信息和关联服务之前,空路由和空服务不会产生实际的代理效果。它们主要用于管理和设计Kong网关的配置框架。

请 let 我知道如果还有其他疑问!我很乐意提供更多详细的解释和帮助。

In Kong Control Plane (Kong Manager), Routes and Services have the following concepts:

- Service: Defines the backend API/microservice information, such as URL, load balancing algorithm, etc. Kong CP will sync the service configuration to Kong Data Plane nodes.

- Route: Defines the path, domain name, service ID, etc. Kong CP also syncs route configuration to Kong DP nodes.

So in Kong Control Plane, empty routes and services mean:

- Empty Service: A service created in Kong CP but does not specify any actual backend API information, e.g. the URL is empty. Empty services themselves do not proxy or forward any requests. It only acts as a placeholder for routes or other services to reference.

- Empty Route: A route created in Kong CP but does not specify the ID of any actual service. Accessing an empty route does not produce any proxy or forwarding and returns 404. Empty routes also only act as placeholders and can be associated with actual services to take effect later.

Therefore, in Kong CP, empty routes and services also mainly act as placeholders and reservations. They can:

1. Reserve route paths and domain names in advance to prevent conflicts later.

2. Design the structure and framework of Kong configuration first, and then gradually develop and deploy actual APIs and microservices.

3. Be rebound to other services or routes at any time, providing flexibility and variability.

The configuration of routes and services in Kong CP will eventually be synced to Kong DP nodes. Therefore, before filling in the actual service information and associating services, empty routes and services do not produce actual proxy effects. They are mainly used to manage and design the framework of Kong gateway configuration.

 Please let me know if you have any other questions! I would be happy to provide more detailed explanations and help.

In summary, the main purposes of empty routes and services in Kong CP are:

1. Placeholder: Reserve path, domain and service names etc.
2. Framework design: Design the architecture before actual implementation.  
3. Flexibility: Easy to adjust by binding to actual services or routes.

They provide a flexible way to manage Kong configuration, but do not take effect on their own. The configuration will work only after binding to actual backend services.


这里是一个Kong CP端的路由(Route)配置JSON示例:
json
{
  "name": "example-route",
  "protocols": [
    "http",
    "https"
  ],
  "methods": [
    "GET",
    "POST"
  ],
  "paths": [
    "/example"
  ],
  "strip_path": true,  
  "preserve_host": false,
  "service": {
    "name": "example-service"
  }
}


对应的,这里是一个Kong CP端的服务(Service)配置JSON示例:
json 
{
  "name": "example-service",
  "url": "http://example.com",  
  "protocol": "http",          
  "host": "http://example.com",
  "port": 80,
  "path": "/api/",
  "retries": 5,
  "connect_timeout": 60000,
  "write_timeout": 60000,
  "read_timeout": 60000     
}


这个路由配置会:

- 为/example路径和HTTP方法为GET和POST的请求
-  stripping "/example" 前缀
- 然后代理到example-service服务

example-service服务配置定义了后端API的URL,超时时间等信息。

所以总结来说:

- Route定义Kong网关如何匹配和处理请求,以及将请求映射到哪个Service。

- Service定义后端API或微服务的详细信息,以供Kong网关转发请求。

- Kong CP是用来创建和管理Route和Service配置的控制面板。它会将配置同步到Kong DP的各个节点上。

- 在Kong DP的节点上,请求首先被Route匹配,然后被代理到对应的Service指向的后端API。

这个简单的示例展示了Route和Service在Kong CP和DP中的关系和作用。请 let 我知道如果您有任何其他问题!

