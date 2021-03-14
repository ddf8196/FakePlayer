# FakePlayer
Minecraft Bedrock Edition 假人客户端

## 环境依赖
* Java8或更高

## 使用方法
1. `unzip FakePlayer-0.2.2-SNAPSHOT.zip`
2. `cd FakePlayer-0.2.2-SNAPSHOT/bin`
3. `./FakePlayer-GUI`(GUI模式) 或 `./FakePlayer`(命令行模式)
4. 将公钥添加至server.properties
5. 公钥添加完成后重启BDS即可使用

## 注意事项
* **配置文件内含Xbox验证使用的密钥对, 请妥善保管, 切勿泄露**
* 配置文件丢失后再次运行假人客户端会自动重新生成，需要再次进行配置并将新的公钥添加到BDS

## 使用的第三方库和参考的项目
* [Protocol](https://github.com/CloudburstMC/Protocol)
* [Gson](https://github.com/google/gson)
* [FlatLaf](https://github.com/JFormDesigner/FlatLaf)
* [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket)
* SnakeYaml
* slf4j-nop
* [ProxyPass](https://github.com/CloudburstMC/ProxyPass)
* [MCBEClient](https://github.com/hmy2001/MCBEClient)
