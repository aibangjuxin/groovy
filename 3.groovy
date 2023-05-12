在启动Docker容器的时候，可以使用“-d”参数来使该容器在后台运行，并抑制 stdout 和 stderr 的输出。具体操作如下：

```
docker run -d {image} >/dev/null 2>&1
```

其中“-d”表示以后台模式运行容器，“>/dev/null 2>&1”则将容器的标准输出和标准错误输出重定向到 /dev/null 中，从而禁止了输出。

或者，也可以在使用“docker run”启动容器时，使用“-log-driver none”参数来禁止Docker将日志发送到标准输出。具体示例如下：

```
docker run -d --log-driver none {image}
```

这样，即使没有重定向日志输出到 /dev/null，Docker也不会将容器的输出写入标准输出中。

需要注意的是，禁止容器输出 stdout 和 stderr 并不利于对容器运行状态的监控和排查问题，建议在实际应用场景中慎重考虑。



