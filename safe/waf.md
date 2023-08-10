WAF(Web 应用程序防火墙)是一种专门用于保护 Web 应用程序安全的防火墙。它的主要功能包括:

- HTTP流量监测和过滤 - WAF可以检查和过滤客户端到Web应用服务器的HTTP/HTTPS流量,拦截各种已知和未知的Web攻击。

- 应用漏洞防护 - 通过规则和签名识别和阻止针对常见Web应用漏洞的攻击,如SQL注入、XSS、CSRF等。

- 应用程序DDoS防护 - 可以防范HTTP洪水型DDoS攻击。 

- 用户鉴权 - 可以进行用户会话的验证和管理。

- 防篡改和anti-automation - 防止自动工具的恶意操作。

- 日志审核 - 详细记录流量日志,用于安全分析、审计和取证。

- 与IDS/IPS集成 - 可以和IDS/IPS等其他安全设备集成,共享威胁情报。

WAF常以软件或硬件的形式部署,可以独立运行也可以和Web服务器集成。通过实时检测和阻断Web应用漏洞利用企图,WAF为Web应用提供了一道额外的安全防线。


OWASP ModSecurity Core Rule Set (CRS) 是一组开源的 Web 应用程序防火墙(WAF)规则,它提供了保护 Web 应用程序免受常见攻击的功能。其主要特点包括:

- 由开放 Web 应用程序安全项目(OWASP)维护和发布。OWASP是一个致力于提高软件安全的非营利组织。

- 基于ModSecurity Web应用防火墙。它提供规则引擎和处理引擎来分析和拦截恶意请求。

- 提供针对常见Web应用程序漏洞和攻击的检测和防护规则,如SQL注入,XSS,RFI等。规则集按威胁类型进行分类。

- 默认规则集旨在最大限度减少误报,通过仅启用严重的已知攻击签名来实现。

- 规则可自定义和扩展。默认规则可调整敏感度,也可以添加自定义规则。

- 提供规则排除机制,可以针对特定的站点需求进行规则优化。

- 社区驱动的规则更新。新兴的威胁可以快速得到响应和覆盖。

总体来说,OWASP CRS通过其广泛覆盖的规则集和不断更新的维护,为实现低误报率和高安全性提供了一个坚实基础。它是构建Web应用程序防火墙的推荐选择。