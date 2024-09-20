



https://cloud.google.com/kubernetes-engine/docs/how-to/internal-load-balance-ingress
https://cloud.google.com/kubernetes-engine/docs/how-to/deploying-gateways

我的GKE环境里面现在按照这2个文档部署了对应的服务,但是我现在想要用Gateway方式来替换我的GKE ingress,我想了解,最大的区别是什么,

Key Differences between Ingress and Gateway in GKE
You're interested in understanding the major differences between using Gateway and Ingress in your GKE environment. While both are used for traffic management, Gateway offers a more modern and feature-rich approach. Here's a breakdown of the key distinctions:
* Abstraction Level: Ingress, a core Kubernetes resource, works at a higher level of abstraction. It relies on Ingress Controllers, which are often tied to specific load balancing solutions, to translate Ingress rules into concrete configurations. Gateway, being a newer API, provides a more granular and flexible way to manage traffic. It introduces new objects like GatewayClass, Gateway, HTTPRoute, etc. allowing for more control and customisation.
* Policy-Based Configuration: One of the most significant advantages of Gateway is its use of policies for configuration. You can attach policies to Gateways, Services, or ServiceImports to tailor traffic routing, security, and health checks without needing to recreate these resources. Ingress, on the other hand, often requires modifying the Ingress resource itself or relying on annotations specific to the Ingress Controller, making it less flexible and potentially requiring redeployments.
* Integration with Google Cloud Armor and IAP: Gateway allows seamless integration with Google Cloud Armor for enhanced backend security through GCPBackendPolicy. It also supports configuration of IAP for robust access control. These features often require more complex workarounds with Ingress.
* Granular Traffic Management: Gateway provides fine-grained control over traffic parameters like session affinity, connection draining timeouts, and access logging sampling rates, configured directly through policies. While some of these features might be available through Ingress annotations depending on the Ingress Controller, Gateway provides a standardised approach.
Important Considerations for Migration:
The sources primarily focus on Gateway's capabilities and configuration, and don't directly compare it with Ingress for migration. You should consider these points:
* Effort and Complexity: Migrating from Ingress to Gateway involves changes to your infrastructure configuration and potentially your application deployment workflows. Assess the complexity and resources required for this migration.
* Feature Parity: While Gateway offers a richer feature set, ensure it supports all functionalities provided by your existing Ingress setup or if suitable workarounds exist.
* Learning Curve: Familiarize yourself with Gateway's concepts, objects, and configuration patterns, as it differs from the Ingress model.
Transitioning to Gateway can bring enhanced flexibility and control over your traffic routing and security. However, carefully evaluating the migration path, considering the points above, and potentially consulting the Google Cloud documentation for specific Ingress-to-Gateway migration guidance will ensure a smooth transition.

