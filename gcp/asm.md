- [Compare ASM in cluster control plane and google-managed control plane](#compare-asm-in-cluster-control-plane-and-google-managed-control-plane)
  - [安装 ASM 控制平面比较](#安装-asm-控制平面比较)
  - [Comparison of Installing ASM in Cluster Control Plane vs. Installing ASM with Google-managed Control Plane](#comparison-of-installing-asm-in-cluster-control-plane-vs-installing-asm-with-google-managed-control-plane)
  - [Install ASM with Google-managed control plane 和 Anthos Service Mesh for Multi-Cluster 的概念区别](#install-asm-with-google-managed-control-plane-和-anthos-service-mesh-for-multi-cluster-的概念区别)
- [ASM all here](#asm-all-here)
  - [Google-managed control plane](#google-managed-control-plane)
    - [代管式 Anthos Service Mesh](#代管式-anthos-service-mesh)
      - [预配代管式 Anthos Service Mesh || Provision managed Anthos Service Mesh](#预配代管式-anthos-service-mesh--provision-managed-anthos-service-mesh)
- [建议使用的方案如下:](#建议使用的方案如下)
  - [前提条件](#前提条件)
  - [使用要求](#使用要求)
    - [依据上面要求, 我这边可能需要操作的步骤](#依据上面要求-我这边可能需要操作的步骤)
    - [ask ai](#ask-ai)
    - [对于自行安装的集群内 Anthos Service Mesh，Google 支持当前 Anthos Service Mesh 版本和前两个 (n-2) 次要版本](#对于自行安装的集群内-anthos-service-meshgoogle-支持当前-anthos-service-mesh-版本和前两个-n-2-次要版本)
# Compare ASM in cluster control plane and google-managed control plane

**install ASM in cluster control plane**

* 将 ASM 安装在集群控制平面上，意味着 ASM 组件（例如，ASM 代理和 ASM 控制平面组件）将与集群控制平面组件一起部署在同一组节点上。
* 这通常适用于小型集群或对性能和延迟要求不高的集群，因为 ASM 组件与集群控制平面组件共享相同的资源。
* 这种部署方式的优点是简单易管理，因为 ASM 组件与集群控制平面组件一起管理。
* 缺点是，ASM 组件可能会与集群控制平面组件争夺资源，从而影响集群的性能和稳定性。

**install asm with google-managed control plane**

* 将 ASM 与 Google 托管控制平面一起安装，意味着 ASM 组件将部署在与集群控制平面组件分离的专用节点上。
* 这通常适用于大型集群或对性能和延迟要求较高的集群，因为 ASM 组件与集群控制平面组件隔离，不会争夺资源。
* 这种部署方式的优点是，ASM 组件与集群控制平面组件隔离，不会影响集群的性能和稳定性。
* 缺点是，ASM 组件与集群控制平面组件分离，管理起来可能更加复杂。

在集群控制平面安装ASM和在Google托管的控制平面上安装ASM的概念上的区别主要在于控制平面的管理方式。在"install ASM in cluster control plane"中，你将在自己的集群控制平面上安装ASM，而在"install asm with google-managed control plane"中，Google将负责托管控制平面，包括ASM的安装和管理。这意味着你无需关心控制平面的底层维护，Google会为你处理。

表格输出对比及其优缺点如下：

**优点：**
- **install ASM in cluster control plane:**
  - 简单易管理，ASM 组件与集群控制平面组件共享相同的资源。
- **install asm with google-managed control plane:**
  - ASM 组件与集群控制平面组件隔离，不会争夺资源，适用于大型集群或对性能和延迟要求较高的场景。

**缺点：**
- **install ASM in cluster control plane:**
  - ASM 组件可能与集群控制平面组件争夺资源，影响集群性能和稳定性。
- **install asm with google-managed control plane:**
  - 管理起来可能更加复杂，因为ASM组件与集群控制平面组件分离。

关于**install ASM in cluster control plane**和**install asm with google-managed control plane**的描述是准确的，前者适用于简单易管理的情境，而后者更适用于要求隔离性和性能的场景。



## 安装 ASM 控制平面比较

| 特性 | 安装 ASM 在集群控制平面 | 安装 ASM 具有 Google 托管控制平面 |
|---|---|---|
| 控制平面位置 | 在集群中 | 在 Google Kubernetes Engine 中 |
| 控制平面管理 | 由用户管理 | 由 Google 管理 |
| 可用性 | 仅在 GKE 上可用 | 在 GKE 和其他 Kubernetes 发行版上可用 |
| 可扩展性 | 受集群大小限制 | 可扩展到数千个集群 |
| 安全性 | 由用户负责 | 由 Google 托管 |
| 成本 | 免费 | 需要 Google Cloud 订阅 |
| 易用性 | 复杂 | 简单 |

**概念差异**

* 安装 ASM 在集群控制平面意味着将控制平面部署为 Kubernetes 集群中的 pod。 用户负责管理控制平面的生命周期和安全性。
* 安装 ASM 具有 Google 托管控制平面意味着控制平面由 Google 在 GKE 中托管。 Google 负责管理控制平面的生命周期和安全性。

**特点差异**

| 特性 | 安装 ASM 在集群控制平面 | 安装 ASM 具有 Google 托管控制平面 |
|---|---|---|
| 控制平面版本 | 用户可以选择版本 | 使用最新的稳定版本 |
| 数据平面代理 | 用户可以选择代理版本 | 使用最新的稳定版本 |
| Istio 网关 | 用户负责安装和配置 | 由 Google 自动安装和配置 |
| telemetry | 用户负责收集和存储遥测数据 | Google 收集和存储遥测数据 |
| 可观察性 | 用户负责设置监控和警报 | Google 提供开箱即用的监控和警报 |

**结论**

* 安装 ASM 在集群控制平面适合需要更多控制和灵活性的用户。
* 安装 ASM 具有 Google 托管控制平面适合想要简单易用解决方案的用户。

**推荐**

* 对于大多数用户，建议使用安装 ASM 具有 Google 托管控制平面。 它易于使用，并提供 Google 的安全性和可靠性。
* 对于需要更多控制和灵活性的用户，可以选择安装 ASM 在集群控制平面。

**参考资料**

* Provision managed Anthos Service Mesh with asmcli: [https://cloud.google.com/service-mesh/docs/managed/provision-managed-anthos-service-mesh-asmcli](https://cloud.google.com/service-mesh/docs/managed/provision-managed-anthos-service-mesh-asmcli)
* Install Anthos Service Mesh: [https://cloud.google.com/service-mesh/docs/unified-install/install-anthos-service-mesh](https://cloud.google.com/service-mesh/docs/unified-install/install-anthos-service-mesh)



## Comparison of Installing ASM in Cluster Control Plane vs. Installing ASM with Google-managed Control Plane

| Feature | Install ASM in Cluster Control Plane | Install ASM with Google-managed Control Plane |
|---|---|---|
| Control Plane Location | In the cluster | In Google Kubernetes Engine |
| Control Plane Management | Managed by the user | Managed by Google |
| Availability | Available on GKE only | Available on GKE and other Kubernetes distributions |
| Scalability | Limited by cluster size | Scalable to thousands of clusters |
| Security | User responsibility | Managed by Google |
| Cost | Free | Requires a Google Cloud subscription |
| Ease of Use | Complex | Simple |

**Conceptual Differences**

* Installing ASM in the cluster control plane means deploying the control plane as pods in a Kubernetes cluster. The user is responsible for managing the lifecycle and security of the control plane.
* Installing ASM with a Google-managed control plane means the control plane is hosted by Google in GKE. Google is responsible for managing the lifecycle and security of the control plane.

**Feature Differences**

| Feature | Install ASM in Cluster Control Plane | Install ASM with Google-managed Control Plane |
|---|---|---|
| Control Plane Version | User can choose the version | Uses the latest stable version |
| Data Plane Proxy | User can choose the proxy version | Uses the latest stable version |
| Istio Gateway | User is responsible for installation and configuration | Automatically installed and configured by Google |
| Telemetry | User is responsible for collecting and storing telemetry data | Google collects and stores telemetry data |
| Observability | User is responsible for setting up monitoring and alerting | Google provides out-of-the-box monitoring and alerting |

**Conclusion**

* Installing ASM in the cluster control plane is suitable for users who need more control and flexibility.
* Installing ASM with a Google-managed control plane is suitable for users who want a simple and easy-to-use solution.

**Recommendation**

* For most users, it is recommended to use Install ASM with a Google-managed control plane. It is easy to use and provides Google's security and reliability.
* For users who need more control and flexibility, they can choose to install ASM in the cluster control plane.

**References**

* Provision managed Anthos Service Mesh with asmcli: [https://cloud.google.com/service-mesh/docs/managed/provision-managed-anthos-service-mesh-asmcli](https://cloud.google.com/service-mesh/docs/managed/provision-managed-anthos-service-mesh-asmcli): [https://cloud.google.com/service-mesh/docs/managed/provision-managed-anthos-service-mesh-asmcli](https://cloud.google.com/service-mesh/docs/managed/provision-managed-anthos-service-mesh-asmcli)
* Install Anthos Service Mesh: [https://cloud.google.com/service-mesh/docs/unified-install/install-anthos-service-mesh](https://cloud.google.com/service-mesh/docs/unified-install/install-anthos-service-mesh): [https://cloud.google.com/service-mesh/docs/unified-install/install-anthos-service-mesh](https://cloud.google.com/service-mesh/docs/unified-install/install-anthos-service-mesh)




install ASM in cluster control plane和install asm with google-managed control plane的主要区别如下:

| | install ASM in cluster control plane | install asm with google-managed control plane |
|-|:-|:-|
|控制平面| 用户自行管理控制平面 | Google 托管控制平面 |
|集群访问| 用户需要访问集群来安装ASM | 用户不需要访问集群来安装ASM |
|控制ASM| 用户可以完全控制ASM的安装和配置 | Google 控制ASM的部署,用户有限配置 |
|维护| 用户负责维护和更新ASM | Google 负责维护和更新ASM |
|定价| 用户需要支付ASM使用成本 | ASM 成本包含在托管集群定价中 |
|灵活性| 用户可以自定义ASM配置 | 用户ASM配置有限 |
|安全性| 用户自行负责ASM安全 | Google 确保 ASM 的安全性 |

主要区别在于 install ASM in cluster control plane 需要用户自行管理和维护ASM,而 install asm with google-managed control plane 则是Google提供托管的ASM服务。前者灵活性更高,后者更简单方便。

Here is the comparison in English:

| | install ASM in cluster control plane | install asm with google-managed control plane |
|-|:-|:-|  
|Control Plane| User self-manages control plane | Google managed control plane |
|Cluster Access| User needs access to cluster to install ASM | User does not need cluster access to install ASM |  
|ASM Control| User has full control over ASM installation and configuration | Google controls ASM deployment, user has limited configuration |
|Maintenance| User responsible for maintaining and updating ASM | Google handles ASM maintenance and updates |
|Pricing| User pays for ASM usage costs | ASM cost included in managed cluster pricing |
|Flexibility| User can customize ASM configuration | Limited user ASM configuration |
|Security| User responsible for ASM security | Google ensures ASM security |

The main difference is that install ASM in cluster control plane requires the user to self-manage and maintain ASM, while install asm with google-managed control plane is a managed ASM service provided by Google. The former has more flexibility and the latter is simpler and more convenient.


https://cloud.google.com/service-mesh/docs/unified-install/install-anthos-service-mesh?hl=zh-cn#gke_1


install asm with google-managed control plane With anthos Service Mesh for Multi-Cluster 从概念上来说有什么区别?
## Install ASM with Google-managed control plane 和 Anthos Service Mesh for Multi-Cluster 的概念区别

**1. 控制平面**

* **Install ASM with Google-managed control plane:**
    * 控制平面由 Google 托管，用户无需管理控制平面的底层基础设施。
    * 控制平面是多集群的，可以管理多个集群上的 ASM 组件。
* **Anthos Service Mesh for Multi-Cluster:**
    * 控制平面由用户自行管理，用户需要负责控制平面的部署和维护。
    * 控制平面可以是单集群的，也可以是多集群的。

**2. 数据平面**

* **Install ASM with Google-managed control plane:**
    * 数据平面由 ASM 组件组成，部署在每个集群中。
    * ASM 组件由 Google 托管，用户无需管理 ASM 组件的底层基础设施。
* **Anthos Service Mesh for Multi-Cluster:**
    * 数据平面由 Istio 组件组成，部署在每个集群中。
    * 用户需要负责 Istio 组件的部署和维护。

**3. 功能**

* **Install ASM with Google-managed control plane:**
    * 提供 ASM 的所有功能，包括流量管理、服务发现、安全性和可观察性。
    * 提供多集群管理功能，可以统一管理多个集群上的 ASM 组件。
* **Anthos Service Mesh for Multi-Cluster:**
    * 提供 Istio 的所有功能，包括流量管理、服务发现、安全性和可观察性。
    * 提供多集群管理功能，可以统一管理多个集群上的 Istio 组件。

**4. 适用场景**

* **Install ASM with Google-managed control plane:**
    * 适用于需要使用 ASM 全部功能的场景。
    * 适用于需要统一管理多个集群上的 ASM 组件的场景。
* **Anthos Service Mesh for Multi-Cluster:**
    * 适用于需要使用 Istio 全部功能的场景。
    * 适用于需要统一管理多个集群上的 Istio 组件的场景。

**5. 总结**

* **Install ASM with Google-managed control plane:**
    * 是一种更易于管理的部署方式，由 Google 托管控制平面和数据平面。
    * 提供 ASM 的所有功能，并支持多集群管理。
* **Anthos Service Mesh for Multi-Cluster:**
    * 提供更大的灵活性，用户可以自行管理控制平面。
    * 提供 Istio 的所有功能，并支持多集群管理。

**选择哪种部署方式取决于您的具体需求和环境。**

**以下是一些建议：**

* 如果您需要使用 ASM 的全部功能，并且希望 Google 托管控制平面和数据平面，则可以选择 **Install ASM with Google-managed control plane**。
* 如果您需要使用 Istio 的全部功能，并且希望自行管理控制平面，则可以选择 **Anthos Service Mesh for Multi-Cluster**。

**其他资源**

* Install ASM with Google-managed control plane documentation: <移除了无效网址>
* Anthos Service Mesh for Multi-Cluster documentation: <移除了无效网址>


在概念上，"Google-managed control plane" 和 "Anthos Service Mesh for Multi-Cluster" 以及 "ASM" （Anthos Service Mesh）是 Google Cloud 平台上用于管理和部署服务网格的关键组件。

1. **Google-managed control plane**:
   - Google-managed control plane 是 Google Cloud 上托管的控制平面，用于管理 Kubernetes 集群的操作和资源。这包括 GKE（Google Kubernetes Engine）中的控制平面，它由 Google 自动管理和维护。用户不必担心管理控制平面的详细操作，Google 会负责确保其可用性、安全性和性能。
   - 使用 Google-managed control plane，您可以专注于应用程序和服务的部署和运行，而无需担心底层基础设施的管理。

2. **Anthos Service Mesh for Multi-Cluster**:
   - Anthos Service Mesh 是 Google Cloud 上的服务网格解决方案，它建立在 Istio 之上，用于实现微服务架构中的服务通信、安全、监控和控制。
   - Anthos Service Mesh for Multi-Cluster 是 Anthos Service Mesh 的一个功能，它允许您在多个 Kubernetes 集群之间创建统一的服务网格。这意味着您可以在不同的 GKE 集群中部署应用程序，并使用 Anthos Service Mesh 实现这些应用程序之间的通信和管理。

因此，将 "install ASM with Google-managed control plane" 和 "Anthos Service Mesh for Multi-Cluster" 结合起来考虑，意味着您将在使用 Google 托管的控制平面的同时，通过 Anthos Service Mesh 在多个集群之间创建和管理服务网格。这使您能够在不同环境中实现统一的服务管理、安全和监控。


structure
# ASM all here 
https://cloud.google.com/service-mesh/docs/overview?hl=zh-cn
https://cloud.google.com/service-mesh/docs/overview?hl=zh-cn

## Google-managed control plane
如果我想在GCP平台上使用Google-managed control plane来管理Kubernetes集群，我需要安装和配置哪些组件和工具？
如果您想在Google Cloud Platform(GCP)上使用Google-managed control plane来管理Kubernetes集群,您需要以下组件和工具:

1. gcloud命令行工具 - 这是用于管理GCP资源和服务的命令行接口。您需要安装并配置gcloud来与您的GCP项目交互。

2. Kubernetes Engine - 这是GCP上托管的Kubernetes服务。您可以使用gcloud工具来创建和管理Kubernetes Engine集群。

3. kubectl - 这是Kubernetes的命令行接口,用于管理Kubernetes集群和应用程序。您需要在本地机器上安装kubectl并使用身份验证凭证连接到GCP上的Kubernetes Engine集群。

4. 节点池 - 您至少需要创建一个节点池来运行您的容器化应用程序。节点池定义了运行应用程序的计算实例的配置。

5. 网络 - 您需要为集群配置网络和子网选项,以启用pod网络连接。GCP会自动为您的集群设置网络,但您可以自定义它。

6. IAM角色和服务账户 - 为了管理集群,您需要具有适当的IAM(身份和访问管理)权限。GCP会自动管理服务账户,但您可以添加更细致的访问控制。

在设置了所有这些组件后,您就可以使用Google-managed控制平面(GKE Masters)来处理如自动扩展、升级和监控等集群管理任务。


### 代管式 Anthos Service Mesh 

- 托管式 Anthos Service Mesh 支持的功能
  - https://cloud.google.com/service-mesh/docs/managed/supported-features-mcp?hl=zh-cn

https://cloud.google.com/service-mesh/docs/supported-features?hl=zh-cn

对 Anthos Service Mesh 的支持遵循 GKE Enterprise 版本支持政策。
对于代管式 Anthos Service Mesh，Google 支持每个发布渠道中提供的当前 Anthos Service Mesh 版本。
For managed Anthos Service Mesh, Google supports the current Anthos Service Mesh versions available in each release channel.


代管式 Anthos Service Mesh 是由 Google 代管的服务网格，您只需启用它即可。Google 会以向后兼容的方式代您处理可靠性、升级、扩缩和安全性问题
代管式 Anthos Service Mesh
代管式 Anthos Service Mesh 由代管式控制平面和代管式数据平面组成。借助代管式 Anthos Service Mesh，Google 能够代您处理升级、扩缩和安全性，以让您最大限度地减少用户手动维护工作。启用代管式数据平面后，Google 会安装一个集群内控制器，该控制器可代您管理边车代理。

下图显示了代管式 Anthos Service Mesh 的 Anthos Service Mesh 组件和功能
Architecture 架构图
https://cloud.google.com/service-mesh/docs/overview?hl=zh-cn#managed_anthos_service_mesh
https://cloud.google.com/static/service-mesh/docs/images/mesh-arch-managed-asm.svg
![Architecture](https://cloud.google.com/static/service-mesh/docs/images/mesh-arch-managed-asm.svg)


#### 预配代管式 Anthos Service Mesh || Provision managed Anthos Service Mesh
https://cloud.google.com/service-mesh/docs/managed/provision-managed-anthos-service-mesh?hl=zh-cn
代管式 Anthos Service Mesh 是由 Google 代管的服务网格，您只需启用它即可。Google 会以向后兼容的方式代您处理可靠性、升级、扩缩和安全性问题。

本页面介绍了如何使用舰队功能 API 来设置代管式 Anthos Service Mesh。

- 使用 Fleet API 启用代管式 Anthos Service Mesh 时：
  - Google 会应用推荐的控制平面配置
  - Google 会启用自动数据平面代管
  - 系统会根据您的 Google Kubernetes Engine (GKE) 集群的发布渠道在相应的 Anthos Service Mesh 发布渠道中注册您的集群，并使控制平面和数据平面与最新发布保持同步。
  - 虽然 Google 默认会在整个服务网格中实现端点发现和跨集群负载均衡，但您必须创建相应的防火墙规则。
- 如有需要，您可以采用下面的初始配置路径：
  - 需要使用 gcloud 通过 Google Cloud API 和 IAM 配置代管式 Anthos Service Mesh。
  - 需要使用与其他舰队功能相同的 API 配置 Anthos Service Mesh。
  - 需要为每个集群自动获取 Anthos Service Mesh 的推荐配置。


# 建议使用的方案如下:
- recommend using the fleet feature API to automatically configure managed Anthos Service Mesh
```bash
注意：我们建议使用舰队功能的 API 自动配置代管式 Anthos Service Mesh，
不过您也可以使用 asmcli 工具配置代管式 Anthos Service Mesh。
如果您要使用 VPC-SC、私有证书授权机构 (CA) 或要选择与您的 Google Kubernetes Engine (GKE) 渠道不同的 Anthos Service Mesh 渠道，则必须使用该手动配置方法。

Note: We recommend using the fleet feature API to automatically configure managed Anthos Service Mesh, 
but you can also configure managed Anthos Service Mesh using the asmcli tool.
You must use that manual configuration to use VPC-SC, Private CA, or to choose an Anthos Service Mesh channel that is different from your Google Kubernetes Engine (GKE) channel.

```






- https://cloud.google.com/service-mesh/docs/managed/provision-managed-anthos-service-mesh?hl=zh-cn
- 详细的操作步骤就是上面这个链接
## 前提条件
- 获取预配代管式 Anthos Service Mesh 所需的权限
- 已在您的集群上启用 Workload Identity。
## 使用要求
- 一个或多个在受支持的 GKE 版本中具有受支持区域的集群。
- 确保集群有足够的容量来容纳代管式 Anthos Service Mesh 在集群中安装的所需组件。
  - kube-system 命名空间中的 mdp-controller 部署请求 cpu：50m，内存：128Mi。
  - kube-system 命名空间中的 istio-cni-node 守护进程集在每个节点上请求 cpu：100m，内存：100Mi。
- 确保从中预配代管式 Anthos Service Mesh 的客户端机器与 API 服务器之间有网络连接。
- 集群必须注册到舰队。此操作包含在说明中，也可以在预配之前单独完成。
- 您的项目必须启用服务网格的舰队功能。此操作包含在说明中，也可以单独完成。
- 仅 GKE 1.21.3 及更高版本支持 GKE Autopilot。
- 预配代管式 Anthos Service Mesh 时，默认安装 Istio CNI。
- 代管式 Anthos Service Mesh 可以在单项目单网络或多项目单网络环境中使用多个 GKE 集群。
  - 如果您要加入的集群不在同一个项目中，则它们必须向同一舰队宿主项目注册，并且这些集群必须位于同一网络的共享 VPC配置中。
  - 对于单项目多集群环境，舰队项目可以与集群项目相同。如需详细了解舰队，请参阅舰队概览。
  - 对于多项目环境，我们建议您将舰队托管在与集群项目不同的项目中。如果您的组织政策和现有配置允许，我们建议您将共享 VPC 项目用作舰队宿主项目。如需了解详情，请参阅通过共享 VPC 设置集群。
### 依据上面要求, 我这边可能需要操作的步骤
- 集群必须注册到舰队。此操作包含在说明中，也可以在预配之前单独完成。
- 创建一个空舰队 
  - 创建空舰队
  - 默认情况下，当您首次在项目中注册集群时，或者在 Google Cloud 控制台中启用 GKE Enterprise 时，系统会在舰队宿主项目中创建新舰队。如果要在注册任何集群之前创建新的指定舰队（例如为团队访问权限设置范围），请运行以下命令：
  - gcloud alpha container fleet create --display-name=NAME [--project=FLEET_HOST_PROJECT_ID]
如果未指定 display-name，则新舰队具有基于其舰队宿主项目名称的默认显示名称。
- 加入舰队
  - https://cloud.google.com/service-mesh/docs/managed/provision-managed-anthos-service-mesh?hl=zh-cn#register_clusters_to_a_fleet
  - https://cloud.google.com/anthos/fleet-management/docs/register/gke?hl=zh-cn
    - 如果集群已启用 GKE Workload Identity，则这两个命令都会为集群启用舰队 Workload Identity。
    - 如果集群已启用 GKE Workload Identity，则这两个命令都会为集群启用舰队 Workload Identity。
    - 如需将 Google Cloud 上的现有 GKE 集群注册到其项目的舰队，请运行以下命令：
    - gcloud container clusters update CLUSTER_NAME --enable-fleet
    - 将 CLUSTER_NAME 替换为您要注册的集群的名称。此名称也会成为集群的成员资格名称。
    - 如需将集群注册到其他项目中的舰队，请使用带有 --fleet-project 标志的同一命令，如下所示。在运行此命令之前，请确保您已设置跨项目注册的必要权限。
    - gcloud container clusters update CLUSTER_NAME --enable-fleet --fleet-project=PROJECT_ID_OR_NUMBER
    - 将 CLUSTER_NAME 替换为您要注册的集群的名称。此名称也会成为集群的成员资格名称。
    - 将 PROJECT_ID_OR_NUMBER 替换为您选择的舰队宿主项目的项目 ID 或编号。
- 将集群注册到舰队 https://cloud.google.com/service-mesh/docs/managed/provision-managed-anthos-service-mesh?hl=zh-cn#register_clusters_to_a_fleet
- 这个文章还是在ALl这里的
```bash
使用舰队 Workload Identity 注册 GKE 集群--location 标志是集群的计算可用区或区域（例如 us-central1-a 或 us-central1）。


gcloud container clusters update CLUSTER_NAME \
  --location CLUSTER_LOCATION \
  --fleet-project FLEET_PROJECT_ID
验证是否已注册集群：


gcloud container fleet memberships list --project FLEET_PROJECT_ID
输出示例：


NAME                 EXTERNAL_ID                           LOCATION
cluster-1            1d8e255d-2b55-4df9-8793-0435461a2cbc  us-central1
记下 MEMBERSHIP_NAME，因为您在启用自动管理时需要用到。

如果集群的项目与舰队宿主项目不同，则您必须允许舰队项目中的 Anthos Service Mesh 服务账号访问集群项目，并在集群项目上启用所需的 API。您只需为每个集群项目执行一次此操作。

如果您之前使用 asmcli 为此集群项目和舰队项目的组合配置了代管式 Anthos Service Mesh，则这些更改已应用生效，您无需再运行以下命令。

向舰队项目中的服务账号授予访问集群项目的权限：


gcloud projects add-iam-policy-binding "CLUSTER_PROJECT_ID" \
  --member "serviceAccount:service-FLEET_PROJECT_NUMBER@gcp-sa-servicemesh.iam.gserviceaccount.com" \
  --role roles/anthosservicemesh.serviceAgent
在集群的项目上启用 Mesh API：


gcloud services enable mesh.googleapis.com \
  --project=CLUSTER_PROJECT_ID
```
### ask ai 

我准备在我的GCP工程中支持ASM 而且使用预配代管式 Anthos Service Mesh. 参考这个链接https://cloud.google.com/service-mesh/docs/overview?hl=zh-cn 给我一个大概的安装或者应用步骤,比如需要准备什么.

好的,来讨论一下在GCP中启用Anthos Service Mesh(ASM)预配托管版本的步骤:

1. 准备GCP项目和集群

   - 确保您的GCP项目已启用必要的API,如Anthos,Kubernetes Engine API等

   - 创建一个基于GKE的Kubernetes集群(版本≥1.16)

   - 配置授权和IAM权限,授予ASM要求的最低权限

2. 安装服务网格

   - 使用Google Cloud Console搜索并启用“Anthos Service Mesh”

   - 选择托管控制平面选项

   - 根据提示设置位置、集群和其他选项配置ASM

   - 这将自动在集群上安装和配置所有的组件

3. 准备应用程序

   - 对您的应用程序和服务添加必要的Kubernetes注解,用于与ASM集成  

   - 为sidecar注入配置namespaces、deployments等

   - 为流量管理配置destination rules等策略

4. 验证安装

   - 检查各个组件的pod和服务是否正常运行

   - 尝试进行sidecar注入,验证是否成功注入Envoy代理

   - 生成并注入一些测试流量,检查遥测和观察性数据

主要准备工作就是配置好集群和IAM,以及对接应用程序。ASM组件和控制平面会自动安装。可以根据需要配置探针,policies等进一步自定义服务网格。

希望这给了您一个基本的ASM启用流程概述!有任何问题随时问我。












### 对于自行安装的集群内 Anthos Service Mesh，Google 支持当前 Anthos Service Mesh 版本和前两个 (n-2) 次要版本
For self-installed in-cluster Anthos Service Mesh, Google supports the current and previous two (n-2) minor versions of Anthos Service Mesh.


我们需要的是 代管式 Anthos Service Mesh
