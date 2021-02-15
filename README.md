# FakePlayer
Minecraft Bedrock Edition 假人客户端

## 环境依赖
* Java8或更高

## 使用方法
* 1.`java -jar FakePlayer-1.0-SNAPSHOT-all.jar`
* 2.根据提示进行初次配置，并将生成的公钥添加至server.properties
* 3.公钥添加完成并重启BDS后再次运行假人客户端即可

## 注意事项
* **配置文件位于`./config/config.yaml`, 内含Xbox验证使用的密钥对, 请妥善保管, 切勿泄露**
* 配置文件丢失后再次运行假人客户端会自动重新生成，需要再次进行配置并将新的公钥添加到BDS

## 使用的第三方库和参考的项目
* [Protocol](https://github.com/CloudburstMC/Protocol)
* [Gson](https://github.com/google/gson)
* SnakeYaml
* [ProxyPass](https://github.com/CloudburstMC/ProxyPass)
* [MCBEClient](https://github.com/hmy2001/MCBEClient)
