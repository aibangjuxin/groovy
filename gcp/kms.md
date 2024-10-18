<https://cloud.google.com/bigquery/docs/customer-managed-encryption?hl=zh>

<https://cloud.google.com/bigquery/docs/customer-managed-encryption?hl=zh-cn#project_default_key>
设置项目默认密钥

除非指定其他 Cloud KMS 密钥，否则您可以设置项目范围的默认 Cloud KMS 密钥，此默认密钥将应用于该位置的项目中所有查询结果和新创建的表。默认密钥不会应用到现有的表。如果更改默认密钥，则系统不会修改现有的任何表，并且更改后的密钥只会应用到更改默认密钥后创建的新表。

要确认您的 Google Cloud 项目是否已经设置了默认的工程级别密钥，您可以通过 Cloud Console 或使用 `gcloud` CLI 来检查项目的加密设置。下面是具体的步骤。

### 1. 通过 Google Cloud Console 检查项目级别的默认密钥

1. **进入 Cloud Console**：
   - 打开 [Google Cloud Console](https://console.cloud.google.com/)。
2. **选择您的项目**：

   - 确保您已经选择了正确的项目，查看项目选择器菜单并选择目标项目。

3. **导航到 BigQuery**：
   - 在导航菜单中选择 **BigQuery**。
4. **查看加密设置**：
   - 转到 **Settings（设置）**。
   - 在 **Customer-managed encryption** 部分，您可以看到默认的 Cloud KMS 密钥信息。如果已设置默认密钥，会显示密钥的详细信息（如密钥名称、密钥环等）。

### 2. 使用 `gcloud` 命令行工具检查项目级别的默认密钥

您也可以使用 `gcloud` 命令来查看是否已设置默认密钥。

1. **查看项目范围的默认 KMS 密钥**：
   您可以使用以下命令查看您的项目是否设置了默认密钥：

   ```bash
   gcloud kms keys list --location=your-location --keyring=your-keyring --project=your-project-id
   ```

   这会列出项目在指定位置（location）和密钥环（keyring）中的所有 Cloud KMS 密钥。如果项目已经设置了默认的加密密钥，您可以在 BigQuery 中的查询结果或新表设置中看到使用的默认密钥。

### 3. 组织级别的密钥

Google Cloud 的组织级别加密策略目前不允许在组织范围内设置默认 Cloud KMS 密钥。您需要在项目范围内单独设置默认密钥。因此，所有的加密管理都在项目级别处理，无法直接在组织级别统一配置默认加密密钥。

总结：可以通过 Cloud Console 检查 BigQuery 设置，也可以通过 `gcloud` 命令行查看项目的 Cloud KMS 密钥配置情况。目前，默认的 Cloud KMS 密钥设置是项目级别的，无法直接为组织级别设置。
