# FakePlayer
Minecraft Bedrock Edition 假人客户端

## 简介
为BDS开发的假人客户端，利用网络协议模拟真实玩家登录服务器，并可通过BDS的Xbox验证   
理论上也可在其他服务端及单人存档使用（暂时无法通过除BDS外的其他服务端的Xbox验证，单人存档使用需打开多人游戏）

## 安装及使用
1. 安装Java8或更高版本
2. 下载并解压FakePlayer-版本号.zip
3. 运行解压出的bin目录下的FakePlayer-GUI（GUI模式）或FakePlayer（命令行模式） 
   推荐以GUI模式启动，以下操作均为GUI模式下进行
4. 点击服务器公钥后的查看按钮，选择"将公钥添加至server.properties",并选择BDS的server.properties，如添加时BDS正在运行，添加完成后需要重启BDS   
（此步骤是添加额外公钥以通过BDS的Xbox验证，若服务器未开启online-mode（Xbox验证），此步骤可暂时跳过）   
（除BDS外的其他服务端貌似均不支持利用配置文件添加额外公钥，所以目前无法利用此方法通过其他服务端的Xbox验证）
5. 设置好服务器地址及端口，点击保存配置后添加假人即可（服务器地址一般使用默认的localhost即可，不建议远程连接假人）

## 注意事项
* 假人和正常玩家一样，不能绕过白名单，在开启了白名单的服务器中使用需要手动将假人名称添加至白名单中
* 如果出现假人总是掉线重连，且信息为disconnectionScreen.timeout，请将server.properties中的player-idle-timeout设为0

## 功能
* 较为稳定的挂机
* 假人死亡自动复活

## 正在开发中的功能
* 假人投掷三叉戟
* 假人睡觉
* 假人背包相关操作

## 支持的版本
* 理论上支持1.7.0到1.17.10之间的所有版本

## WebSocket API
* FakePlayer提供WebSocket API以供其他插件/程序与FakePlayer进行通信，目前支持的操作有添加/删除假人，获取假人列表等
* 具体消息格式见[FakePlayer Wiki](https://github.com/ddf8196/FakePlayer/wiki/WebSocket-API)
* WebSocket API仅为本地进程间通信设计，消息采用明文传输，不考虑安全性，因此请勿尝试远程连接WebSocket，也请不要对外开放WebSocket端口

## 使用的第三方库
* [Protocol](https://github.com/CloudburstMC/Protocol)
* [Gson](https://github.com/google/gson)
* [FlatLaf](https://github.com/JFormDesigner/FlatLaf)
* [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket)
* SnakeYaml
* slf4j-nop