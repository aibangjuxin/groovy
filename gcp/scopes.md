在计算机和Google Cloud Platform（GCP）的认证上下文中，"scopes" 通常指的是OAuth 2.0的权限范围（scopes）。这些权限范围定义了给予应用程序或者服务账号的权限，决定了它可以访问的Google服务的API接口。例如，当一个应用程序或者实例使用一个服务账号进行认证时，它会请求一个或多个scopes，这些定义了它在GCP上可以做哪些操作。

对于GCP的Compute Engine实例，您可以在创建时指定需要的访问权限scopes，或者在设置服务账号时指定。例如：

sh
gcloud compute instances create [INSTANCE_NAME] \
  --scopes=https://www.googleapis.com/auth/devstorage.read_write,https://www.googleapis.com/auth/logging.write


在这个命令中，实例将会有权读写Cloud Storage和写Cloud Logging的权限。

OAuth 2.0 scopes是GCP安全模型一个重要的组成部分，确保了只有授权的应用程序才能访问用户数据，并且用户或管理员对授权了哪些权限有明确的了解。通过精心选择scopes，可以遵循最小特权原则，保证应用程序仅拥有完成其功能所需的权限，降低了潜在的安全风险。
