iptables -t nat -A <CHAIN_NAME> -s 192.168.0.0/24 ! -s 192.168.0.100 -j MASQUERADE



Here is the English translation:

Yes, using !CIDR prefix to exclude certain IPs in GKE's nonMasqueradeCIDRs configuration is a supported syntax. 

For example:

If your current nonMasqueradeCIDRs is:

nonMasqueradeCIDRs: "10.0.0.0/8" 


And you want to exclude 10.0.1.5 so that it can be SNATed, the correct configuration should be:

nonMasqueradeCIDRs: "10.0.0.0/8,!10.0.1.5/32"


This way, only 10.0.1.5 will be SNATed, other IPs in 10.0.0.0/8 will still not be SNATed.

Also, if you want to exclude multiple IPs, you can specify multiple !CIDRs, for example:

nonMasqueradeCIDRs: "10.0.0.0/8,!10.0.1.5/32,!10.0.2.10/32" 


The above configuration will SNAT 10.0.1.5 and 10.0.2.10, other IPs in 10.0.0.0/8 remain unchanged.

In summary, using the !CIDR prefix to exclude specific IPs in GKE's nonMasqueradeCIDRs configuration so that those IPs can be SNATed is a fully supported syntax. 

Please feel free to use it and exclude the IPs that need SNAT as needed in your actual configuration. Let me know if you have any other questions when configuring and using this feature.

These smaller CIDR blocks cover the IP range from 130.208.127.15 to 191.255.255.254, while excluding the IP address 130.208.127.15.

如果您想使用GKE网络策略（Network Policy）来允许对某个IP段的egress（出口）流量进行限制，但想要排除某个特定的IP地址，可以尝试使用以下规则：

```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: allow-egress
spec:
  podSelector: {}
  policyTypes:
  - Egress
  egress:
  - to:
    - ipBlock:
        cidr: <IP段的CIDR>
        except:
        - <要排除的IP地址>
    ports:
    - protocol: TCP
      port: <端口号>
```

在这个规则中，您可以使用 `ipBlock` 来指定要允许的IP段，使用 `except` 来指定要排除的IP地址。这将使得您的网络策略在针对特定IP段的egress流量时，排除掉您指定的IP地址。 

请注意，在您的规则中使用的IP段和IP地址应该遵循CIDR（无类域间路由选择）的格式。