# The curl -X option

 is used to specify the HTTP method to be used in the request. The HTTP method is a verb that indicates the action to be performed on the resource identified by the request URL.

By default, curl uses the GET method, which retrieves the resource identified by the URL. However, you can use the -X option to specify a different HTTP method, such as POST, PUT, DELETE, or OPTIONS.

Here's an example command that uses the -X option to specify the POST method:

curl -X POST https://example.com/api/v1/create -d 'name=John&age=30'

In this example, the -X option is used to specify the POST method, which is used to create a new resource on the server. The -d option is used to specify the data to be sent in the request body, which is typically used for POST and PUT requests.

Here's another example that uses the -X option to specify the DELETE method:

curl -X DELETE https://example.com/api/v1/delete/123

In this example, the -X option is used to specify the DELETE method, which is used to delete a resource on the server. The URL https://example.com/api/v1/delete/123 identifies the resource to be deleted.

By using the -X option, you can perform a variety of HTTP methods with curl, allowing you to interactwith RESTful APIs and other web services that require specific HTTP methods to be used for different operations.


- By default, curl uses the GET method
```
The curl -X option is used to specify the HTTP method to be used in the request. Here are the most commonly used HTTP methods that can be specified using the -X option:

GET: Used to retrieve a resource from the server. This is the default method used by curl if no method is specified.

POST: Used to create a new resource on the server.

PUT: Used to update an existing resource on the server.

DELETE: Used to delete a resource from the server.

HEAD: Used to retrieve the headers for a resource without retrieving the resource itself.

OPTIONS: Used to retrieve the list of HTTP methods supported by the server for a given resource.

PATCH: Used to make partial updates to an existing resource on the server.

TRACE：
```
- Here are some examples of using the curl -X option with different HTTP methods:
```
# Perform a GET request
curl https://example.com/api/v1/resource

# Perform a POST request
curl -X POST https://example.com/api/v1/create -d 'name=John&age=30'

# Perform a PUT request
curl -X PUT https://example.com/api/v1/update/123 -d 'name=John&age=31'

# Perform a DELETE request
curl -X DELETE https://example.com/api/v1/delete/123

# Perform a HEAD request
curl -X HEAD https://example.com/api/v1/resource

# Perform an OPTIONS request
curl-X OPTIONS https://example.com/api/v1/resource

# Perform a PATCH request
curl -X PATCH https://example.com/api/v1/update/123 -d 'name=John'
```
Note that the specific set of HTTP methods supported by a server may vary depending on the implementation, and not all methods may be supported for all resources. When working with a RESTful API or other web service, it's important to consult the API documentation or server documentation to determine which methods are supported for each resource.

HTTP方法有:
```
# GET
用于获取资源的表示形式。请求不应该返回 resource 的副作用。

# HEAD 
类似于 GET 请求,只不过返回的响应中没有具体的内容,用于获取报头。

# POST
用于提交资源的 Side-effect 变化。POST 请求通常会导致服务器端创建或修改资源。

# PUT 
用于替换资源的所有当前表示。

# DELETE 
用于删除资源。

# CONNECT
HTTP/1.1 协议中预留给能够将连接改为管道方式的代理服务器。

# OPTIONS 
用于返回服务器针对特定资源所支持的 HTTP 方法。也可以用于测定服务器的功能集。

# TRACE
回显服务器收到的请求,主要用于测试或诊断。

# PATCH
是对 PUT 方法的补充,用来对资源进行部分修改。

其他方法还有:

# HEAD
与GET方法一致,只是返回的响应中没有具体的内容,用于获取报头。

# LINK
建立和描述两个资源之间的关系。

# UNLINK
销毁两个资源之间的关系。

# BASELINE-CONTROL
用于创建,修改或删除一个基线。

# CHECKOUT 
请求服务器 Checkout 一个版本控制资源。

# CHECKIN
请求服务器 Checkin 一个版本控制资源。

# UNCHECKOUT
取消 Checkout 一个版本控制资源。 

# MKACTIVITY
创建一个新的活动流。

# SEEOTHER 
重定向到一个不同的URI以便衔接资源。

# SOURCE
用于返回文本资源的源代码。

在 RESTful API 中,最常用的 HTTP 方法就是 GET,POST,PUT,DELETE。
```
# http methods 

Please note that some servers may not support all of the HTTP request methods. If you try to use a request method that is not supported, the server may return an error.


HTTP methods are the actions that can be performed on a resource. There are 9 HTTP methods in total:

- GET: Used to retrieve a resource.
- HEAD: Similar to GET, but only returns the header of the resource.
- POST: Used to create a new resource.
- PUT: Used to update an existing resource.
- DELETE: Used to delete a resource.
- CONNECT: Used to establish a tunnel to a remote server.
- OPTIONS: Used to get information about the communication options for a resource.
- TRACE: Used to return the message that was sent to the server.
- PATCH: Used to partially update a resource.

The HTTP method used for a request determines what action is performed on the resource. For example, a GET request will retrieve the contents of a web page, while a POST request will create a new user account.

Here are some examples of how HTTP methods are used in web applications:

- When you visit a website, your browser sends a GET request to the web server. The web server then returns the HTML code for the website.
- When you fill out a form on a website and click submit, your browser sends a POST request to the web server. The web server then stores the data from the form in a database.
- When you delete a file from your computer, your file manager sends a DELETE request to the web server. The web server then removes the file from the server's hard drive.

HTTP methods are an important part of the HTTP protocol. They allow clients and servers to communicate with each other and perform actions on resources.

Nginx proxy_pass默认会重写转发请求的`User-Agent`头信息。

当Nginx接收到客户端请求后,它会在转发请求给upstream服务器之前,重写请求中的`User-Agent`头信息。默认的`User-Agent`值会被重写为:

User-Agent: Proxy on nginx 


如果不希望Nginx重写`User-Agent`头信息,可以在`proxy_pass`指令中加入`proxy_set_header User-Agent $http_user_agent;来保留原始的User-Agent`:

location / {
    proxy_set_header User-Agent $http_user_agent;
    proxy_pass http://backend; 
}


这样,Nginx转发给upstream的请求头中`User-Agent`的值就会和客户端原始请求中的`User-Agent`值一致。

上述配置对于那些依赖客户端`User-Agent`来判断请求来源的服务很有用,比如一些移动端API等。如果不配置`proxy_set_header User-Agent $http_user_agent`,这些服务可能会误将Nginx代理的请求判断为来自桌面端浏览器。

所以,总结来说,Nginx proxy_pass默认的`User-Agent`值为`Proxy on nginx`,通过`proxy_set_header User-Agent $http_user_agent;指令可以选择保留原始的User-Agent`头信息。
## Nginx proxy_pass和http proxy Tunnel有什么区别?

proxy_pass和`http proxy tunnel`的主要区别是:

proxy_pass是一个反向代理,它会将请求转发给upstream服务器,并将upstream服务器的响应转发回客户端。在这个过程中,Nginx会重写请求和响应中的一些头信息,比如`Location`、`Content-Length`等。

http proxy tunnel是一个正向代理,它只是简单地将请求转发给远程服务器,并将响应转发回客户端。在这个过程中,Nginx不会修改请求和响应中的任何内容。

## 实例配置

proxy_pass配置:
location / {
    proxy_pass http://backend;
}


http proxy tunnel配置:
location / {
    proxy_http_version 1.1;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection "upgrade";
    proxy_pass http://backend;
} 


## 实际场景

- proxy_pass通常用于反向代理web后端服务
- http proxy tunnel通常用于转发WebSocket或其他非HTTP协议

所以,如果你配置了`proxy_pass`访问一个服务,但是实际交互使用的协议是WebSocket,这时就需要使用`http proxy tunnel`了。