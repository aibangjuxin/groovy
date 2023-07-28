使用Helm安装squid proxy到Kubernetes集群的namespace里,可以按以下步骤操作:

1. 准备squid的docker image,比如:squid:latest

2. 准备Helm chart的模板文件values.yaml,文件内容如下:
``` yaml
image:
  repository: squid
  tag: latest

service:
  type: ClusterIP
  port: 3128
  
resources:
  limits:
    memory: 512Mi
  requests:
    memory: 256Mi
```

3. 在chart目录下创建一个Deployment的模板文件templates/deployment.yaml:
``` yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: {{ .Release.Name }}
spec:
  replicas: 1
  selector:
    matchLabels:
      app: {{ .Release.Name }}
  template:
    metadata:
      labels:
        app: {{ .Release.Name }}
    spec:
      containers:
      - name: {{ .Release.Name }}
        image: {{ .Values.image.repository }}:{{ .Values.image.tag }}
        ports:
        - containerPort: {{ .Values.service.port }}
        resources:
          limits:
            memory: {{ .Values.resources.limits.memory }}
          requests:
            memory: {{ .Values.resources.requests.memory }}
``` 

4. 在chart目录下创建一个Service的模板文件templates/service.yaml:

``` yaml  
apiVersion: v1
kind: Service
metadata:
  name: {{ .Release.Name }}
spec:
  type: {{ .Values.service.type }}
  ports:
  - port: {{ .Values.service.port }}
  selector:
    app: {{ .Release.Name }}
``` 

5. 在chart目录下,运行helm install:

helm install my-squid ./


这样就可以在Kubernetes集群的default namespace里安装一个squid的deployment和service。可以根据需要修改values.yaml文件来配置参数。