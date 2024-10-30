以下是 AWS 与 GCP 部分主要产品和服务的对比，重点关注同类服务的名称和功能对比：
```mermaid
功能类别	AWS 产品	GCP 产品	功能描述
计算服务	EC2 (Elastic Compute)	Compute Engine	提供虚拟机实例，可以选择不同配置和大小，支持弹性伸缩。
容器服务	EKS (Elastic Kubernetes Service)	GKE (Google Kubernetes Engine)	托管的 Kubernetes 服务，支持容器编排和管理。
无服务器计算	Lambda	Cloud Functions	提供无服务器执行环境，可以运行代码而无需管理服务器。
存储服务	S3 (Simple Storage Service)	Cloud Storage	对象存储服务，适合存储和检索任意大小的数据。
块存储	EBS (Elastic Block Store)	Persistent Disk	为计算实例提供块存储，支持数据持久化。
文件存储	EFS (Elastic File System)	Filestore	托管的文件存储服务，支持多实例共享文件系统。
数据库（关系型）	RDS (Relational Database Service)	Cloud SQL	托管关系型数据库，支持 MySQL、PostgreSQL 等。
数据库（NoSQL）	DynamoDB	Firestore / Datastore	提供无模式的 NoSQL 数据库，适合高可用性和扩展性要求。
数据仓库	Redshift	BigQuery	分析数据仓库，适合大规模数据的分析和查询。
缓存服务	ElastiCache	Memorystore	托管的缓存服务，支持 Redis 和 Memcached。
消息队列	SQS (Simple Queue Service)	Pub/Sub	消息传递服务，用于系统间的异步通信。
API 管理	API Gateway	API Gateway	管理和发布 API，支持 API 的安全、分析等功能。
内容分发网络	CloudFront	Cloud CDN	内容分发网络，提供全球分发加速服务。
监控服务	CloudWatch	Stackdriver Monitoring	监控和管理服务，提供资源、应用监控和警报。
身份与访问管理	IAM (Identity and Access Management)	IAM	访问和权限管理，控制用户和资源的访问权限。
DevOps/CI/CD	CodePipeline / CodeBuild	Cloud Build	持续集成和持续部署服务，用于自动化构建和部署流程。
```
此表格简要总结了 AWS 和 GCP 常见服务的对应关系，并在功能上提供了一些基本的对比。如有其他功能或服务类别的具体需求，可以进一步展开对比。
