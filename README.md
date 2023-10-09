# box 暴剋兕


#### 源项目
#### https://github.com/CatVodTVOfficial/TVBoxOSC

---
#### 参考项目
#### https://github.com/CatVodTVOfficial/TVBoxOSC
#### https://github.com/q215613905/TVBoxOS
#### https://github.com/takagen99/Box

---
#### 数据源参考
[肥猫主页](http://肥猫.love)
</br>[饭太硬主页](http://饭太硬.top)</br></br>

---
#### 简介
TVBox 简易修改 多源版本

除box本来功能之外，其他主要修改实现的功能：
- 添加多仓多线路的处理逻辑，例如：
```
{
    "urls": [
        {
            "url": "http://饭太硬.top/tv",
            "name": "🚀饭太硬线路"
        },
        {
            "url": "http://肥猫.love",
            "name": "🚀肥猫线路"
        }
    ]
}
```
```
{
  "storeHouse": [
    {
      "sourceName": "默认",
      "sourceUrl": "https://raw.gitmirror.com/mlabalabala/TVResource/main/boxCfg/ori_source.json"
    },
    {
      "sourceName": "起飞",
      "sourceUrl": "https://raw.gitmirror.com/mlabalabala/TVResource/main/boxCfg/sp_source.json"
    }
  ]
}
```
- 对于触屏设备，点击主页数据源按钮可以进入应用列表，之后可能改成电视可以点击的按钮
- 其他默认功能的小修小补

默认线路位置 ```com/github/tvbox/osc/bbox/base/App.java```<br/>可以选择使用 ```raw.gitmirror.com``` 加速访问自建的仓库
<br/>有需求的可以自行修改（不过建议改自己的仓库，改其他网络线路可能存在如果线路失效需重新改代码重新生成比较麻烦）

[自建仓库](https://raw.gitmirror.com/mlabalabala/TVResource/main/boxCfg/default)
<br><br>蓝奏云限制分享apk文件，大家自行打包吧
<br>放一个链接，有办法的同学可以自己下载
<br>[码：6111](https://bunny6111.lanzouq.com/b04whwgwj)
# Actions中有生成脚本！！！


