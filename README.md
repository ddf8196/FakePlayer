# FakePlayer
Minecraft Bedrock Edition 假人客户端

***

[![License](https://img.shields.io/badge/license-MIT-blue)](https://github.com/ddf8196/FakePlayer/blob/main/LICENSE)

## 简介
为BDS开发的假人客户端，利用网络协议模拟真实玩家登录服务器，并可通过BDS的Xbox验证   
理论上也可在其他服务端及单人存档使用（暂时无法通过除BDS外的其他服务端的Xbox验证，单人存档使用需打开多人游戏）

## 注意事项 | Q&A
* 假人和正常玩家一样，不能绕过白名单，在开启了白名单的服务器中使用需要手动将假人名称添加至白名单中
* 如果出现假人总是掉线重连，且信息为disconnectionScreen.timeout，请将server.properties中的player-idle-timeout设为0
* 如果点开bat，控制台一闪就没了，可能是以下几种情况：
  1. Java版本错误
  2. Windows下**路径太长**
  3. 待补充

## 安装及使用
### Windows
1. 安装Java 8或更高版本
2. 下载并解压FakePlayer-版本号.zip
3. 运行解压出的bin目录下的FakePlayer-GUI.bat（GUI模式）或FakePlayer.bat（命令行模式） 
   推荐以GUI模式启动，以下操作均为GUI模式下进行
4. 点击服务器公钥后的查看按钮，选择"将公钥添加至server.properties",并选择BDS的server.properties，如添加时BDS正在运行，添加完成后需要重启BDS   
（此步骤是添加额外公钥以通过BDS的Xbox验证，若服务器未开启online-mode（Xbox验证），此步骤可暂时跳过）   
（除BDS外的其他服务端貌似均不支持利用配置文件添加额外公钥，所以目前无法利用此方法通过其他服务端的Xbox验证）
5. 设置好服务器地址及端口，点击保存配置后添加假人即可（服务器地址一般使用默认的localhost即可，不建议远程连接假人）
### Linux
1. 安装Java 8或更高版本 (`apt/yum install`)
2. 下载并解压FakePlayer-版本号.zip (`wget`, `unzip`)
3. 运行解压出的bin目录下的FakePlayer-GUI（GUI模式）或FakePlayer（命令行模式） (`./FakePlayer-GUI`, `./FakePlayer`)  
（一般需要先`chmod 777 ./FakePlayer`添加可执行权限， GUI版本应该不支持SSH, 需要桌面版系统）
4. 点击服务器公钥后的查看按钮，选择"将公钥添加至server.properties",并选择BDS的server.properties，如添加时BDS正在运行，添加完成后需要重启BDS   
（此步骤是添加额外公钥以通过BDS的Xbox验证，若服务器未开启online-mode（Xbox验证），此步骤可暂时跳过）   
（除BDS外的其他服务端貌似均不支持利用配置文件添加额外公钥，所以目前无法利用此方法通过其他服务端的Xbox验证）
5. 设置好服务器地址及端口，点击保存配置后添加假人即可（服务器地址一般使用默认的localhost即可，不建议远程连接假人）

## 功能
* 较为稳定的挂机
* 死亡自动复活
* 投掷三叉戟
* [游戏内聊天消息控制](https://github.com/ddf8196/FakePlayer/wiki/%E8%81%8A%E5%A4%A9%E6%B6%88%E6%81%AF%E6%8E%A7%E5%88%B6)
* [WebSocket API](https://github.com/ddf8196/FakePlayer/wiki/WebSocket-API)

## 正在开发中的功能
* 更多背包相关操作
* 更加完善的物品注册
* 方块注册
* 实体注册
* 实体交互
* JavaScript API

## 支持的版本
* 理论上支持1.7.0到1.19.60之间的所有版本

## WebSocket API
* FakePlayer提供WebSocket API以供其他插件/程序与FakePlayer进行通信，目前支持的操作有添加/删除假人，获取假人列表等
* 具体消息格式见[FakePlayer Wiki](https://github.com/ddf8196/FakePlayer/wiki/WebSocket-API)
* WebSocket API仅为本地进程间通信设计，消息采用明文传输，不考虑安全性，因此请勿尝试远程连接WebSocket，也请不要对外开放WebSocket端口

## Contributors
<table>
  <tr>
    <td align="center">
      <a href="https://github.com/ddf8196">
        <img src="https://avatars.githubusercontent.com/u/73578766?s=100&v=4" width="60px;" alt=""/><br/>
        <sub><b>ddf8196</b></sub>
      </a><br/>
      <a href="https://github.com/ddf8196/FakePlayer/commits?author=ddf8196" title="Code">💻</a>
    </td>
    <td align="center">
      <a href="https://github.com/Jasonzyt">
        <img src="https://avatars.githubusercontent.com/u/66063199?s=100&v=4" width="60px;" alt=""/><br/>
        <sub><b>Jasonzyt</b></sub>
      </a><br/>
      <a href="https://github.com/ddf8196/FakePlayer/commits?author=Jasonzyt" title="Code">💻</a>
    </td>
    <td align="center">
      <a href="https://github.com/yqs112358">
        <img src="https://avatars.githubusercontent.com/u/37969157?s=100&v=4" width="60px;" alt=""/><br/>
        <sub><b>yqs112358</b></sub>
      </a><br/>
      <a href="https://github.com/ddf8196/FakePlayer/commits?author=yqs112358" title="Code">💻</a>
    </td>
    <td align="center">
      <a href="https://github.com/xiaoqch">
        <img src="https://avatars.githubusercontent.com/u/37901097?s=100&v=4" width="60px;" alt=""/><br/>
        <sub><b>xiaoqch</b></sub>
      </a><br/>
      <a href="https://github.com/ddf8196/FakePlayer/commits?author=xiaoqch" title="Code">💻</a>
    </td>
    <td align="center">
      <a href="https://github.com/CivicXFB">
        <img src="https://avatars.githubusercontent.com/u/61540722?s=100&v=4" width="60px;" alt=""/><br/>
        <sub><b>CivicXFB</b></sub>
      </a><br/>
      <a href="https://github.com/ddf8196/FakePlayer/commits?author=CivicXFB" title="Code">💻</a>
    </td>
    <td align="center">
      <a href="https://github.com/hhhxiao">
        <img src="https://avatars.githubusercontent.com/u/33011851?s=100&v=4" width="60px;" alt=""/><br/>
        <sub><b>AgNO3</b></sub>
      </a><br/>
      <a href="https://github.com/ddf8196/FakePlayer/commits?author=hhhxiao" title="Code">💻</a>
    </td>
    <td align="center">
      <a href="https://github.com/TimothyGrass">
        <img src="https://avatars.githubusercontent.com/u/77906640?s=100&v=4" width="60px;" alt=""/><br/>
        <sub><b>TimothyGrass</b></sub>
      </a><br/>
      <a href="https://github.com/ddf8196/FakePlayer/blob/main/src/main/resources/i18n/Messages_en_US.properties" title="Translation">🌍</a>
    </td>
  </tr>
</table>

## 使用的第三方库
* [Protocol](https://github.com/CloudburstMC/Protocol) ([Apache License 2.0](https://github.com/CloudburstMC/Protocol/blob/develop/LICENSE))
* [Gson](https://github.com/google/gson) ([Apache License 2.0](https://github.com/google/gson/blob/master/LICENSE))
* [FlatLaf](https://github.com/JFormDesigner/FlatLaf) ([Apache License 2.0](https://github.com/JFormDesigner/FlatLaf/blob/main/LICENSE)) 
* [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket) ([MIT License](https://github.com/TooTallNate/Java-WebSocket/blob/master/LICENSE))
* [MiGLayout Swing](https://github.com/mikaelgrev/miglayout) ([BSD 3-Clause "New" or "Revised" license](http://www.debian.org/misc/bsd.license))
* [Brigadier](https://github.com/Mojang/brigadier) ([MIT License](https://github.com/Mojang/brigadier/blob/master/LICENSE))
* [PNGJ](https://github.com/leonbloy/pngj) ([Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt))
* [Rhino](https://github.com/mozilla/rhino) ([Mozilla Public License 2.0](https://github.com/mozilla/rhino/blob/master/LICENSE.txt))
* SnakeYaml ([Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt))
* SLF4J NOP Binding ([MIT License](	http://www.opensource.org/licenses/mit-license.php))
