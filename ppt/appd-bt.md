## 200个Business Transaction的限制

在AppDynamics中，每个应用程序最多可以有200个Business Transaction(BT)。这是因为:

- AppDynamics会自动发现应用程序中的常见框架和入口点，并将其创建为BT[1][2]。
- 如果应用程序有太多的BT，可能会导致性能问题和配置限制[4]。
- 因此AppDynamics建议采用"越少越好"的理念，只关注最关键的服务[2]。

## 如何处理超过200个BT的情况

如果应用程序有超过200个BT,可以采取以下措施:

1. **使用Transaction Match Rules**来合并或过滤掉不重要的BT[1][2]
2. **使用Scopes**将BT规则限定在特定的Tier上[4]
3. **使用Rule Masking**来防止上层BT覆盖掉下层BT[4]
4. **使用Live Preview**先预览BT规则的效果再应用[4]

通过上述方法,可以在不超过200个BT限制的情况下,仍然覆盖应用程序的关键流程。

## 总结

AppDynamics对BT的数量有200个的限制,主要是为了控制开销,提高性能。如果超过了这个限制,可以采取一些手段来优化BT的定义,确保覆盖应用程序的关键流程。

Citations:
[1] https://docs.appdynamics.com/appd/24.x/latest/en/application-monitoring/business-transactions/troubleshoot-business-transaction-performance-with-transaction-snapshots
[2] https://www.wwt.com/article/appdynamics-essentials-business-transactions
[3] https://appd-modernization.awsworkshop.io/50_mobilize/5_business_txns.html
[4] https://www.wwt.com/article/appdynamics-business-transactions-playbook
[5] https://stackoverflow.com/questions/73707198/appdynamics-business-transactions-by-url-parameter-values
[6] https://docs.appdynamics.com/display/PRMY23/Call%2BGraphs
[7] https://www.youtube.com/watch?v=ClCgCYNq4Ts
[8] https://www.youtube.com/watch?v=0pSp0pUAQOI
