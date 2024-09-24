# one page 
Definition（定义）
APPD is an advanced application performance monitoring (APM) tool. It tracks and analyzes application performance in real-time.
（APPD是一种先进的应用程序性能监测（APM）工具。它实时跟踪和分析应用程序性能。）
Purpose（目的）
APPD monitors application status, identifies bottlenecks, and optimizes performance. It's essential for maintaining smooth operations.
（APPD监测应用程序状态，识别瓶颈并优化性能。它对于维持平稳运行至关重要。）

the Benefits of Custom APPD Aggregation（自定义APPD聚合的好处）

Deep Insights（深入洞察）
Gain detailed understanding of application performance. Identify trends and patterns for optimization.
（深入了解应用程序性能。识别优化的趋势和模式。）

Accelerated Troubleshooting（加速故障排除）
Quickly pinpoint and resolve issues. Reduce downtime and improve overall system reliability.
（快速定位和解决问题。减少停机时间并提高整体系统可靠性。）

Performance Tracking（性能跟踪）
Monitor progress over time. Set benchmarks and track improvements in application efficiency.
（随着时间的推移监测进展。设置基准并跟踪应用程序效率的改进。）


Continuous Optimization（持续优化）
Use insights to fine-tune applications. Ensure peak performance and user satisfaction.
（利用洞察来微调应用程序。确保峰值性能和用户满意度。）


# How to onboarding
Raise tickets with APPD support to onboard and get started with APPD.

The technical lead communicates with the project manager about the requirements and confirms the functions to be implemented on A and the detailed information of the API to be deployed.
技术负责人与项目经理就需求进行沟通，确认在A上实现的功能和部署API的详细信息。

auto send welcome email 
1. A will auto trigger onboarding pipeline. 
2. create a team github repository. 
3. send welcome mail to team members. 

# About tier and node 

## Tier和Node的概念

在AppDynamics中:

- **Tier**是由具有相同功能的一个或多个节点组成的单元[1][5]。Tier反映了应用程序的逻辑架构。
- **Node**是应用程序的一个实例[1][2]。每个节点都属于一个Tier。

## 为用户自定义Tier和Node的好处

1. **更好地反映应用程序的逻辑架构**。每个用户团队可以根据自己的需求定义合适的Tier和Node[1][5]。
2. **更细粒度的监控和故障排查**。每个用户团队可以针对自己的Tier和Node进行监控和分析[1][6]。
3. **更灵活的配置和管理**。每个用户团队可以独立地配置和管理自己的Tier和Node[4][6]。

## 默认情况下的限制

AppDynamics没有对单个Tier或Node设置硬性配额限制[1][2][5]。但是:

- 不同的许可证类型可能会对监控的节点数量有所限制。
- 过多的Tier和Node可能会影响性能和可用性。

因此,建议根据实际需求合理地设置Tier和Node,避免过度配置。如果需要监控大量节点,可以考虑使用集群代理(Cluster Agent)等方式[1]。

总之,为用户自定义Tier和Node可以带来更好的监控体验,但需要根据实际情况合理配置,避免资源浪费。

Citations:
[1] https://stackoverflow.com/questions/69104331/how-to-decide-the-node-and-tiers-in-appdynamics
[2] https://docs.appdynamics.com/appd/24.x/latest/en/application-monitoring/install-app-server-agents/node-js-agent
[3] https://docs.appdynamics.com/appd/24.x/latest/en/application-monitoring/install-app-server-agents/agent-installer/manage-tier-naming-rules
[4] https://docs.appdynamics.com/appd/24.x/latest/en/application-monitoring/tiers-and-nodes/move-and-rename-nodes
[5] https://docs.appdynamics.com/22.6/en/application-monitoring/tiers-and-nodes
[6] https://community.appdynamics.com/t5/Infrastructure-Server-Network-Database/What-are-tiers-and-nodes/td-p/39407
[7] https://docs.appdynamics.com/display/PRMY23/Name%2B.NET%2BNodes
[8] https://www.youtube.com/watch?v=Lh-2eFdM2z4



在AppDynamics中，**Tier Name**和**Tier Node**分别代表应用层的不同概念：

1. **Tier Name**：它表示应用程序逻辑上的一个分层，比如前端、后端、数据库层等。每个Tier包含若干个服务节点（Node），用于监控和组织不同的业务逻辑。
   
2. **Tier Node**：Node是在特定Tier中的实际实例，通常代表实际运行的服务器或服务。Tier Name和Tier Node一起帮助你组织、监控和分析应用程序的性能。

### 允许自定义Tier Name和Tier Node的好处
你现在想让特定用户使用自定义的Tier Name和Tier Node，有以下几点好处：

1. **独立性和可见性**：
   - 各个用户可以拥有自己独立的Tier Name和Tier Node，便于区分不同团队或服务的性能数据。这样可以更好地隔离不同用户的监控信息，防止混淆。
   
2. **细粒度监控**：
   - 每个用户的性能监控可以独立进行，帮助更精确地分析和调试性能问题。例如，用户可以只看到自己应用的性能情况，而不会混杂其他团队的数据。

3. **更好的优化和调优**：
   - 使用独立的Tier Name和Tier Node后，每个团队可以根据自身的需求进行个性化的监控配置，例如设置特定的警报阈值或分析规则，进行针对性优化。

### Quota限制
默认情况下，AppDynamics并没有硬性的Tier Name或Tier Node数量限制，但在以下几个方面可能会受到影响：

1. **许可和资源使用**：
   - 你的AppDynamics许可证可能会限制可以创建的Tier或Node的数量，具体取决于购买的License类型。如果节点数量超出许可范围，可能需要购买更多的License。

2. **性能和存储资源**：
   - 如果在一个Tier Name或Tier Node下有大量的节点或大量的数据收集，AppDynamics可能会增加服务器的负载，影响系统性能。因此，管理好节点的数量和监控范围也很重要。

3. **可视化与管理复杂性**：
   - 共享同一个Tier Name和Tier Node的情况下，数据可能会变得复杂且难以分析。创建过多的节点也会导致监控页面的可读性下降。因此，合理规划每个Tier下的节点数量可以减轻管理和分析上的负担。

### 总结
允许特定用户使用自定义Tier Name和Tier Node可以增强独立性、监控的精确性，同时防止混淆数据，便于用户或团队专注于自己应用的性能调优。不过需要注意的是，AppDynamics的许可、资源限制以及监控复杂性可能会影响系统的扩展和稳定性。



以下是按照段落对应翻译的内容：
1. 标题：Custom APPD Data Aggregation: Enhancing Monitoring Capabilities（自定义APPD数据聚合：增强监测能力）

Discover how custom APPD data aggregation revolutionizes application performance monitoring. This powerful tool allows users to tailor data collection to their specific needs, enhancing analysis and decision-making capabilities.（了解自定义APPD数据聚合如何革新应用程序性能监测。这个强大的工具允许用户根据其特定需求定制数据收集，增强分析和决策能力。）

2. 标题：Understanding APPD: Your Performance Monitoring Ally（理解APPD：您的性能监测盟友）

Definition（定义）
Purpose（目的）
Benefits（好处）
APPD is an advanced application performance monitoring (APM) tool. It tracks and analyzes application performance in real-time.
（APPD是一种先进的应用程序性能监测（APM）工具。它实时跟踪和分析应用程序性能。）
APPD monitors application status, identifies bottlenecks, and optimizes performance. It's essential for maintaining smooth operations.
（APPD监测应用程序状态，识别瓶颈并优化性能。它对于维持平稳运行至关重要。）
With APPD, teams can proactively address issues. It ensures optimal user experience and system efficiency.
（借助APPD，团队可以主动解决问题。它确保最佳的用户体验和系统效率。）

3. 标题：Custom APPD Aggregation: Tailored to Your Needs（自定义APPD聚合：满足您的需求）

1. Data Aggregation（数据聚合）
Users can customize data aggregation to their own Tier Name and Tier Node. This allows for precise monitoring.
（用户可以根据自己的层级名称和层级节点自定义数据聚合。这允许进行精确监测。）

2. Multi-Dimensional Monitoring（多维监测）
Supports analysis based on various metrics, time ranges, and custom conditions. It offers a comprehensive view.
（支持基于各种指标、时间范围和自定义条件的分析。它提供了一个全面的视图。）

3. User Control（用户控制）
Provides full control and flexibility. Users can define specific data points to monitor, enhancing relevance.
（提供完全的控制和灵活性。用户可以定义要监测的特定数据点，增强相关性。）

The Power of Customization: Unleashing APPD's Potential（定制的力量：释放APPD的潜力）
Improved Decision-Making（改进的决策制定）
Enhanced User Experience（增强的用户体验）
Unmatched Flexibility（无与伦比的灵活性）
Adapt to specific needs effortlessly. Adjust monitoring strategies on-the-fly to match evolving business requirements.
（轻松适应特定需求。即时调整监测策略以匹配不断变化的业务需求。）
Access real-time KPIs for quick, data-driven decisions. Stay ahead of potential issues and optimize performance proactively.
（访问实时关键绩效指标（KPI）以进行快速、数据驱动的决策。领先于潜在问题并主动优化性能。）
Discover and solve problems swiftly. Ensure smooth application performance and keep users satisfied with responsive systems.
（快速发现和解决问题。确保应用程序性能平稳，并使用响应迅速的系统让用户满意。）

4. 标题：Implementing Custom APPD Aggregation in GKE: Step 1（在GKE中实施自定义APPD聚合：步骤1）

Define Tier Name（定义层级名称）
Users determine the logical hierarchy for data aggregation. This forms the foundation of the monitoring structure.
（用户确定数据聚合的逻辑层次结构。这构成了监测结构的基础。）
Define Tier Node（定义层级节点）
Specify the nodes within the hierarchy. This allows for granular control over data collection points.
（指定层次结构中的节点。这允许对数据收集点进行精细控制。）
Customize Metrics（自定义指标）
Select relevant metrics for each tier and node. Tailor the monitoring to specific application needs.
（为每个层级和节点选择相关指标。根据特定应用需求定制监测。）

5. 标题：Implementing Custom APPD Aggregation in GKE: Steps 2 and 3（在GKE中实施自定义APPD聚合：步骤2和3）

1. Data Source Connection（数据源连接）
Establish a connection between the application and APPD's aggregation service. Ensure seamless data flow.
（在应用程序和APPD的聚合服务之间建立连接。确保无缝的数据流动。）

2. Configure Data Transmission（配置数据传输）
Set up parameters for sending performance data. Define frequency and conditions for data updates.
（设置发送性能数据的参数。定义数据更新的频率和条件。）

3. Data Visualization Setup（数据可视化设置）
Create an API or interface for viewing aggregated data. Design intuitive dashboards for easy analysis.
（创建用于查看聚合数据的API或接口。设计直观的仪表板以便于分析。）

6. 标题：Maximizing the Benefits of Custom APPD Aggregation（最大化自定义APPD聚合的好处）

Deep Insights（深入洞察）
Gain detailed understanding of application performance. Identify trends and patterns for optimization.
（深入了解应用程序性能。识别优化的趋势和模式。）
Accelerated Troubleshooting（加速故障排除）
Quickly pinpoint and resolve issues. Reduce downtime and improve overall system reliability.
（快速定位和解决问题。减少停机时间并提高整体系统可靠性。）
Performance Tracking（性能跟踪）
Monitor progress over time. Set benchmarks and track improvements in application efficiency.
（随着时间的推移监测进展。设置基准并跟踪应用程序效率的改进。）
Continuous Optimization（持续优化）
Use insights to fine-tune applications. Ensure peak performance and user satisfaction.
（利用洞察来微调应用程序。确保峰值性能和用户满意度。）

7. 标题：Embracing the Future of Application Monitoring（拥抱应用程序监测的未来）

1. Flexible Solution（灵活的解决方案）
Custom APPD aggregation offers unparalleled flexibility in performance monitoring. Adapt to any business need effortlessly.
（自定义APPD聚合在性能监测方面提供了无与伦比的灵活性。轻松适应任何业务需求。）

2. Simple Integration（简单集成）
With straightforward setup steps, users can quickly implement custom monitoring strategies. Start benefiting immediately.
（通过简单的设置步骤，用户可以快速实施自定义监测策略。立即开始受益。）

3. Continuous Innovation（持续创新）
As technology evolves, custom APPD aggregation will remain at the forefront. Stay ahead with cutting-edge monitoring capabilities.
（随着技术的发展，自定义APPD聚合将保持领先地位。凭借前沿的监测能力保持领先。）






Homepage

• Title: Custom APPD Data Aggregation to User's Tier Name and Tier Node
• Subtitle: Enhance Data Monitoring and Analysis Capabilities

Slide 1: What is APPD?

• Definition: An application performance monitoring (APM) tool used to track and analyze application performance.
• Purpose: Real-time monitoring of application status, identification of bottlenecks, and optimization of performance.

Slide 2: The Functions of Custom APPD Aggregation

• Data Aggregation and Visualization:
    • Users can customize the data aggregation to their own Tier Name and Tier Node.
• Multi-Dimensional Monitoring:
    • Supports monitoring and analysis based on different metrics, time ranges, and custom conditions.
• User Control:
    • Gives users full control and flexibility, allowing them to define specific data to be monitored.

Slide 3: The Benefits of Custom APPD Aggregation

• Improve Decision-Making Ability: Obtain key performance indicators (KPIs) in real-time and make data-driven decisions quickly.
• Optimize User Experience: Discover and solve problems in a timely manner by monitoring application performance.
• Flexibility and Scalability: Adapt to user-specific needs and adjust monitoring strategies at any time.

Slide 4: Implementing Custom APPD Aggregation in GKE

• Step 1: Define Tier Name and Tier Node
    • Users determine the logical hierarchy and nodes of the aggregated data.
• Step 2: Data Source Connection
    • Send performance data from the application to the aggregation service of APPD.
• Step 3: Data Visualization and Analysis
    • Provide an API or interface to allow users to view and analyze aggregated data.

Slide 5: Conclusion

• The custom APPD data aggregation function provides users with a more flexible performance monitoring solution.
• Through simple integration steps, users can customize monitoring strategies. 
