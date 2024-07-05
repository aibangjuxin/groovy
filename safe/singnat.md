要对用户发送的数据进行签名验证，您需要了解数字签名的基本原理和过程。数字签名的主要目的是确保数据的完整性和真实性，防止伪造和抵赖。以下是如何进行数据签名和验证的详细步骤：

### 数据签名过程

1. **生成哈希值**：首先，对需要签名的原始数据进行哈希运算，生成一个固定长度的摘要（哈希值）。
2. **加密哈希值**：使用签名者的私钥对生成的哈希值进行加密，得到数字签名。
3. **发送数据和签名**：将原始数据和数字签名一起发送给接收方。

示例代码（Java）：
```java
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

public class DigitalSignatureExample {
    public static void main(String[] args) throws Exception {
        // 生成RSA公钥/私钥对
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        // 原始数据
        String data = "This is a sample data";
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);

        // 生成数据的哈希值
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(dataBytes);

        // 使用私钥对哈希值进行加密，生成数字签名
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(keyPair.getPrivate());
        signature.update(hash);
        byte[] digitalSignature = signature.sign();

        // 将数字签名和原始数据一起发送
        System.out.println("Original Data: " + data);
        System.out.println("Digital Signature: " + Base64.getEncoder().encodeToString(digitalSignature));
    }
}
```

### 签名验证过程

1. **接收数据和签名**：接收方接收到包含原始数据和数字签名的消息。
2. **生成哈希值**：接收方对接收到的原始数据进行哈希运算，生成新的哈希值。
3. **解密签名**：使用签名者的公钥对数字签名进行解密，得到解密后的哈希值。
4. **对比哈希值**：将解密后的哈希值与生成的新哈希值进行比较。如果两者一致，则验证通过；否则验证失败。

示例代码（Java）：
```java
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

public class DigitalSignatureVerification {
    public static void main(String[] args) throws Exception {
        // 假设已经有公钥和数字签名
        PublicKey publicKey = ...; // 从证书或其他来源获取
        String receivedData = "This is a sample data";
        String receivedSignature = ...; // 从发送方接收到的数字签名

        byte[] dataBytes = receivedData.getBytes(StandardCharsets.UTF_8);
        byte[] signatureBytes = Base64.getDecoder().decode(receivedSignature);

        // 生成数据的哈希值
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(dataBytes);

        // 使用公钥解密数字签名
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(hash);
        boolean isVerified = signature.verify(signatureBytes);

        if (isVerified) {
            System.out.println("Signature is valid.");
        } else {
            System.out.println("Signature is invalid.");
        }
    }
}
```

### 总结

用户需要使用他们的私钥对数据进行签名，您则需要使用用户提供的公钥来验证签名。签名验证的关键在于确保解密后的哈希值与重新计算的哈希值一致。这样可以确保数据的完整性和真实性[1][2][3][5]。

Citations:
[1] https://www.liaoxuefeng.com/wiki/1252599548343744/1304227943022626
[2] https://patents.google.com/patent/CN104301119A/zh
[3] https://blog.csdn.net/u010312474/article/details/102815899
[4] https://help.fanruan.com/finereport9.0/doc-view-845.html
[5] https://blog.csdn.net/qq_34664239/article/details/131068011
