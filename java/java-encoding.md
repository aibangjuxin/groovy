了解您的环境是基于 Linux 系统并运行 Java 环境的 Pod，这确实有助于我们更精确地进行问题排查。让我们一步步来检查服务器的文件系统编码和 Java 运行时的默认编码设置：

1. 检查 Linux 系统的语言和字符集设置：

在 Pod 的终端中运行以下命令：

```bash
locale
```

这会显示当前系统的语言和字符集设置。理想情况下，您应该看到类似这样的输出：

```
LANG=en_US.UTF-8
LC_CTYPE="en_US.UTF-8"
...
```

确保 `LANG` 和 `LC_CTYPE` 设置为 UTF-8。

2. 检查文件系统编码：

运行以下命令来查看文件系统的编码：

```bash
mount | grep " / "
```

在输出中查找 `utf8` 或 `charset=utf8` 这样的选项。

3. 检查 Java 运行时的默认编码：

在您的 Java 应用程序中，添加以下代码来打印 Java 的默认编码：



```java
System.out.println("Default Charset=" + Charset.defaultCharset());
System.out.println("file.encoding=" + System.getProperty("file.encoding"));
System.out.println("sun.jnu.encoding=" + System.getProperty("sun.jnu.encoding"));
```

确保这些都显示为 UTF-8。

4. 如果默认编码不是 UTF-8，您可以在启动 Java 应用程序时指定编码：

```bash
java -Dfile.encoding=UTF-8 -jar your-application.jar
```

5. 检查 /tmp 目录的权限：

运行以下命令：

```bash
ls -ld /tmp
```

确保您的应用程序有权限写入该目录。

6. 文件操作测试：

您可以在 Java 代码中添加一个简单的测试，直接使用 `Files` 类来写入和读取一个包含中文字符的文件：



```java
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

public class FileEncodingTest {
    public static void main(String[] args) {
        try {
            String testString = "测试中文文件名.txt";
            Path path = Paths.get("/tmp/" + testString);
            
            // 写入文件
            Files.write(path, "测试内容".getBytes(StandardCharsets.UTF_8));
            
            // 读取文件
            String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            
            System.out.println("File name: " + path.getFileName());
            System.out.println("File content: " + content);
            
            // 删除测试文件
            Files.delete(path);
            
            System.out.println("Test completed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

```

运行这个测试程序，看看是否能成功创建、读取和删除包含中文字符的文件。

7. 检查日志文件：

查看您的应用程序日志和系统日志（如 `/var/log/syslog`），看是否有任何与编码或文件操作相关的错误消息。

8. 容器环境变量：

如果您的 Java 应用程序运行在容器中，检查容器的环境变量设置，确保没有覆盖默认的编码设置。

9. 使用 file 命令：

在创建文件后，您可以使用 `file` 命令来检查文件的编码：

```bash
file -i /tmp/your_uploaded_file.pdf
```

这应该显示文件的 MIME 类型和字符集。

通过以上步骤，您应该能够找出导致中文文件名问题的根源。如果问题仍然存在，可能需要进一步检查 Pod 的配置、网络设置或存储卷挂载等方面。

需要注意的是，在生产环境中进行这些测试时要格外小心，确保不会影响到实际的业务数据。如果可能的话，最好在一个与生产环境相同配置的测试环境中进行这些排查工作。
