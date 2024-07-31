当然,我们可以使用Shell脚本来实现类似的功能。Shell脚本在处理文本输出方面非常强大,特别适合解析`openssl s_client`的输出。让我为你创建一个Shell脚本版本的解析器。



```bash
#!/bin/bash

# 检查是否提供了输入文件
if [ $# -ne 1 ]; then
    echo "Usage: $0 <openssl_output_file>"
    exit 1
fi

input_file="$1"

# 函数：提取证书信息
extract_cert_info() {
    local cert="$1"
    echo "$cert" | openssl x509 -noout -text
}

# 读取整个文件内容
content=$(cat "$input_file")

echo "Connection Information:"
echo "$content" | grep -E "New, TLSv|Cipher is" | sed 's/^[[:space:]]*//'

echo -e "\nCertificates:"
# 提取所有证书
certs=$(echo "$content" | awk '/-----BEGIN CERTIFICATE-----/,/-----END CERTIFICATE-----/' RS='-----END CERTIFICATE-----' ORS='-----END CERTIFICATE-----\n')

cert_count=0
while read -r cert; do
    if [[ $cert == *"BEGIN CERTIFICATE"* ]]; then
        ((cert_count++))
        echo -e "\nCertificate $cert_count:"
        cert_info=$(extract_cert_info "$cert")
        echo "$cert_info" | grep -E "Subject:|Issuer:|Validity|Public Key Algorithm:" | sed 's/^[[:space:]]*/    /'
    fi
done <<< "$certs"

echo -e "\nServer Parameters:"
echo "$content" | grep "Server Temp Key:" | sed 's/^[[:space:]]*//'

echo -e "\nSSL Session:"
echo "$content" | sed -n '/SSL-Session:/,/---/p' | grep -v -E "^[[:space:]]*$|---" | sed 's/^[[:space:]]*//'

echo -e "\nVerification Result:"
echo "$content" | grep "Verify return code:" | sed 's/^[[:space:]]*//'
```

这个Shell脚本提供了类似于之前Python脚本的功能,用于解析`openssl s_client`的输出。以下是使用方法和脚本功能的详细说明：

使用方法：
1. 保存`openssl s_client`的输出到文件：
   ```
   openssl s_client -connect public-stg.api.gov.sg:443 -showcerts > output.txt
   ```

2. 给脚本执行权限：
   ```
   chmod +x openssl_parser.sh
   ```

3. 运行脚本：
   ```
   ./openssl_parser.sh output.txt
   ```

脚本功能：

1. 连接信息：
   显示SSL/TLS版本和使用的加密套件。

2. 证书链：
   解析每个证书的关键信息，包括：
   - 主题（Subject）
   - 颁发者（Issuer）
   - 有效期（Validity）
   - 公钥算法（Public Key Algorithm）

3. 服务器密钥交换参数：
   如果存在，显示临时密钥信息。

4. SSL会话参数：
   显示会话ID、SSL版本等信息。

5. 验证结果：
   显示证书链验证的结果。

这个脚本可以帮助你快速了解证书链的结构和各个证书之间的关系。通过观察每个证书的Subject和Issuer，你可以理解证书链的层次结构。

例如，你可能会看到类似这样的结构：

1. 服务器证书（叶子证书）：
   - Subject: CN=public-stg.api.gov.sg
   - Issuer: CN=Some Intermediate CA

2. 中间证书：
   - Subject: CN=Some Intermediate CA
   - Issuer: CN=Root CA

3. 根证书（如果包含在输出中）：
   - Subject: CN=Root CA
   - Issuer: CN=Root CA （自签名）

这个脚本的优点是它直接使用Shell命令，不需要额外的Python环境，更适合在各种Linux系统上快速使用。

如果你需要对输出进行进一步的定制或者提取特定的信息，我可以帮你修改这个脚本以满足你的需求。你有任何特定的部分需要更详细的信息吗？​​​​​​​​​​​​​​​​
