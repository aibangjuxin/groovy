在使用 `openssl` 工具时，命令的选项和参数需要正确拼写和使用。以下是你提到的两个常见错误以及如何解决它们：

### 1. `-certifile` 选项错误

在 `openssl crl2pkcs7` 命令中，选项应为 `-certfile` 而不是 `-certifile`。这是一个拼写错误。正确的命令如下：

```bash
openssl crl2pkcs7 -nocrl -certfile ./output.crt | openssl pkcs7 -print_certs -text -noout
```

### 2. `unable to load PKCS7 object` 错误

该错误可能是由于输入文件格式不正确或文件中缺少数据导致的。确保 `output.crt` 是有效的证书文件。这个命令链的第一部分是使用 `openssl crl2pkcs7` 将证书转换为 PKCS#7 格式，而第二部分是使用 `openssl pkcs7` 打印证书的详细信息。

### 详细操作步骤

1. **检查证书文件格式**

   首先，确认 `output.crt` 是一个有效的PEM格式或DER格式的证书。你可以使用以下命令检查：

   ```bash
   openssl x509 -in ./output.crt -text -noout
   ```

   这将显示证书的详细信息。如果输出中包含有效的证书信息，文件应该是正确的。

2. **转换证书**

   如果证书是PEM格式，且需要转换为PKCS#7格式，请使用以下命令：

   ```bash
   openssl crl2pkcs7 -nocrl -certfile ./output.crt -outform PEM -out certs.p7b
   ```

   然后使用以下命令打印证书的详细信息：

   ```bash
   openssl pkcs7 -in certs.p7b -print_certs -text -noout
   ```

   如果你不需要输出到文件，可以直接管道输出：

   ```bash
   openssl crl2pkcs7 -nocrl -certfile ./output.crt | openssl pkcs7 -print_certs -text -noout
   ```

### 总结

- 确保命令中的选项和参数拼写正确。
- 确保输入文件格式正确并且是有效的证书文件。
- 使用正确的工具和选项来处理不同的文件格式和类型。
