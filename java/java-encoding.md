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


如果您无法在启动 Java 应用程序时指定编码，还有一些其他方法可以尝试解决编码问题。以下是一些替代方案：

1. 在代码中显式设置文件编码：

在您的文件操作代码中，总是明确指定使用 UTF-8 编码，而不是依赖系统默认编码。



```java
@RequestMapping(value = "/upload/file", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<CommonResponse> uploadFile(@RequestParam("file") MultipartFile file) {
    Data data = new Data();
    data.setStatus("fail!!");
    if (file.isEmpty()) {
        return new ResponseEntity<>(new CommonResponse("File is empty", data), HttpStatus.BAD_REQUEST);
    }
    
    String originalFilename;
    try {
        originalFilename = new String(file.getOriginalFilename().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        // 移除不安全的字符
        originalFilename = originalFilename.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
        // 确保文件名不为空
        if (originalFilename.isEmpty()) {
            originalFilename = "unnamed_file";
        }
    } catch (Exception e) {
        log.error("Error decoding filename", e);
        return new ResponseEntity<>(new CommonResponse("Failed to decode filename", data), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    String filePath = "/tmp/" + timestamp + "_" + originalFilename;
    Path dest = Paths.get(filePath);
    
    try {
        Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
        data.setStatus("ok!");
        data.setDetails("File uploaded successfully");
        log.info("Uploaded file to: {}", filePath);
    } catch (IOException e) {
        log.error("Error during file upload", e);
        return new ResponseEntity<>(new CommonResponse("Error uploading file", data), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    return new ResponseEntity<>(new CommonResponse("File uploaded successfully", data), HttpStatus.OK);
}

```

在这个更新的版本中：

- 我们使用 `new String(file.getOriginalFilename().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)` 来确保正确解码文件名。
- 使用 `Files.copy()` 方法替代了手动的字节复制，这样可以避免潜在的编码问题。

2. 使用 `InputStreamReader` 和 `OutputStreamWriter`：

当需要读写文本文件时，总是使用这些类并指定 UTF-8 编码：

```java
try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
    // 读写操作
}
```

3. 在应用程序入口点设置默认编码：

虽然不如启动参数可靠，但您可以在应用程序的主方法开始时尝试设置默认编码：

```java
public static void main(String[] args) {
    System.setProperty("file.encoding", "UTF-8");
    Charset.defaultCharset(); // 触发缓存重新计算
    // 其余的应用程序代码...
}
```

请注意，这种方法并不总是有效，因为 `file.encoding` 是一个只读属性，一旦 JVM 启动就无法更改。

4. 使用 `java.nio` 包：

尽可能使用 `java.nio` 包中的类，如 `Files`、`Paths` 等，它们通常能更好地处理编码问题。

5. 环境变量：

如果您可以修改容器或 Pod 的环境变量，可以尝试设置：

```
LANG=en_US.UTF-8
LC_ALL=en_US.UTF-8
```

6. 使用 Base64 编码：

如果文件名仍然出现问题，可以考虑将文件名进行 Base64 编码后再存储，使用时再解码：

```java
String encodedFilename = Base64.getEncoder().encodeToString(originalFilename.getBytes(StandardCharsets.UTF-8));
String decodedFilename = new String(Base64.getDecoder().decode(encodedFilename), StandardCharsets.UTF_8);
```

7. 日志记录：

确保在整个过程中记录详细的日志，包括原始文件名、处理后的文件名、使用的编码等，这样可以帮助您更容易地定位问题。

8. 文件系统适配：

如果问题只出现在特定的文件系统上，考虑使用 Java 的 `FileSystem` API 来处理不同的文件系统：

```java
FileSystems.getDefault().provider().getPath(filePath);
```

这些方法应该能帮助您在无法更改 JVM 启动参数的情况下处理编码问题。记住，最重要的是在所有的文件操作中始终显式指定 UTF-8 编码，而不是依赖系统默认值。如果问题仍然存在，可能需要进一步检查整个系统栈，包括网络传输、数据库存储等环节，确保整个过程中编码保持一致。
