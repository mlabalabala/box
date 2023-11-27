# Jade box

### [竖屏版](https://github.com/mlabalabala/TVBoxOS-Mobile)
### [横屏版](https://github.com/mlabalabala/box)
###
#### 源项目
#### https://github.com/CatVodTVOfficial/TVBoxOSC

---
#### 参考项目
#### https://github.com/CatVodTVOfficial/TVBoxOSC
#### https://github.com/q215613905/TVBoxOS
#### https://github.com/takagen99/Box
#### [竖屏版源项目    https://github.com/XiaoRanLiu3119/TVBoxOS-Mobile](https://github.com/XiaoRanLiu3119/TVBoxOS-Mobile)

---
#### 数据源参考
[肥猫主页](http://肥猫.love)
</br>[饭太硬主页](http://饭太硬.top)</br></br>

---
#### 简介
TVBox 简易修改 多源版本 支持安卓4.4

除box本来功能之外，其他主要修改实现的功能：
- 对于触屏设备，在某些线路首页加载过慢，在卡主页时点击app名字可以直接进入设置调整线路选择；已经加载完成之后点击app名字进入应用列表。设置按钮移到右上角，可以在加载时通过电机设置按钮进入设置界面。
- 增加简单更新功能，目的是为了提醒有可用的新版本，有可用更新时，在设置界面的“检测更新”会显示小红点提醒，点击忽略更新后不在用小红点提醒，但是可以通过点击检测更新来实现更新。**永远不会强制更新**。此版本（玉幂草_SHJ_202311080127.apk）之前的都没有更新模块，如果不喜欢提醒的话可以选择其他之前版本。
- 其他默认功能的小修小补
- 添加自定义GITHUB加速站，可以选择速度快的镜像站来获取基础信息
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
      "sourceUrl": "https://raw.githubusercontent.com/mlabalabala/TVResource/main/boxCfg/ori_source.json"
    },
    {
      "sourceName": "起飞",
      "sourceUrl": "https://raw.githubusercontent.com/mlabalabala/TVResource/main/boxCfg/sp_source.json"
    }
  ]
}
```

- 应用名更改位置```app/src/main/res/values/strings.xml```
- 默认线路位置 ```app/src/main/java/com/github/tvbox/osc/bbox/constant/URL.java```

- [自建仓库](https://raw.bunnylblbblbl.eu.org/https://raw.githubusercontent.com/mlabalabala/TVResource/main/boxCfg/default)
- 蓝奏云限制分享apk文件，大家自行打包吧 。放一个链接，有办法的同学可以自己下载吧[**码：6111**](https://bunny6111.lanzouq.com/b04whwgwj)
### 测试可能不太够，有BUG请提issue
### Actions中有生成脚本


