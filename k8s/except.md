要生成一个适用于动态IP地址的except部分的模板，您可以使用以下格式：

```
- ipBlock:
    cidr: <CIDR地址范围>
    except:
    - ip: <排除的IP地址1>
    - ip: <排除的IP地址2>
    ...
```

对于您的示例，您可以编写一个脚本来生成except部分，如下所示：

```python
except_ips = ["10.208.1.1", "10.208.33.1"]  # 动态IP地址列表
except_template = "- ip: {}"  # except部分的模板

except_section = ""
for ip in except_ips:
    except_section += except_template.format(ip) + "\n"

# 使用except_section替换您的网络策略中的except部分
network_policy = """
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: allow-rt-egress
  namespace: lex
spec:
  podSelector: {}
  policyTypes:
    - Egress
  egress:
    - to:
        - ipBlock:
            cidr: 12.0.0.0/2
            except:
              {except_section}
        - ipBlock:
            cidr: 10.0.0.0/8
            except:
              {except_section}
      ports:
      - protocol: TCP
        except:
        - port: 18080
          protocol: TCP
"""

network_policy = network_policy.format(except_section=except_section.strip())

print(network_policy)
```

这将生成适用于动态IP地址列表的except部分的模板，您可以将其替换到您的网络策略中。


如果您想在Shell脚本中使用`dig +short fqdn`命令来生成动态IP列表，然后将其用于except部分的模板，可以尝试以下代码：

```bash
#!/bin/bash

fqdn="example.com"  # 替换为您的FQDN

# 使用dig命令获取动态IP列表
ip_list=$(dig +short "$fqdn")

except_section=""
while IFS= read -r ip; do
  except_section+="    - ip: $ip\n"
done <<< "$ip_list"

# 使用except_section替换您的网络策略中的except部分
network_policy=$(cat <<EOF
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: allow-rt-egress
  namespace: lex
spec:
  podSelector: {}
  policyTypes:
    - Egress
  egress:
    - to:
        - ipBlock:
            cidr: 12.0.0.0/2
            except:
$except_section
        - ipBlock:
            cidr: 10.0.0.0/8
            except:
$except_section
      ports:
      - protocol: TCP
        except:
        - port: 18080
          protocol: TCP
EOF
)

echo "$network_policy"
```

将此代码保存为Shell脚本文件（例如`generate_network_policy.sh`），然后通过运行`bash generate_network_policy.sh`来执行它。脚本将使用`dig +short fqdn`命令获取动态IP列表，并将其用于except部分的模板生成。然后，它将打印出完整的网络策略，您可以根据需要将其重定向到文件中或进一步处理。请确保将`fqdn`变量替换为您实际的FQDN。