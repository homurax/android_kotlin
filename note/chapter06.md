## 广播机制简介

Android 中的广播主要可以分为两种类型：**标准广播**和**有序广播**。

- **标准广播**（normal broadcasts）是一种完全异步执行的广播，在广播发出之后，所有的 BroadcastReceiver 几乎都会在同一时刻接收到这条广播消息，因此它们之间没有任何先后顺序可言。这种广播的效率会比较高，但同时也意味着它是无法被截断的。

  ![](../images/chapter06/normal_broadcasts.png)

- **有序广播**（ordered broadcasts）则是一种同步执行的广播，在广播发出之后，同一时刻只会有一个 BroadcastReceiver 能够收到这条广播消息，当这个 BroadcastReceiver 中的逻辑执行完毕后，广播才会继续传递。所以此时的 BroadcastReceiver 是有先后顺序的，优先级高的 BroadcastReceiver 就可以先收到广播消息，并且前面的 BroadcastReceiver 还可以截断正在传递的广播，这样后面的 BroadcastReceiver 就无法收到广播消息了。

  ![](../images/chapter06/ordered_broadcasts.png)

## 接收系统广播

Android 内置了很多系统级别的广播，可以在应用程序中通过监听这些广播来得到各种系统的状态信息。

### 动态注册监听时间变化

注册 BroadcastReceiver 的方式一般有两种，在代码中注册和在 AndroidManifest.xml 中注册，其中前者也被称为**动态注册**，后者也被称为**静态注册**。

创建一个 BroadcastReceiver 需要新建一个类，让它继承自 BroadcastReceiver，并重写父类的 `onReceive()` 方法即可。当广播到来时， `onReceive()` 方法就会得到执行。

完整的系统广播列表可以到如下路径中去查看：

```
<Android SDK>\platforms\<任意 android api 版本>\data\broadcast_actions.txt
```

### 静态注册实现开机启动

理论上说动态注册能监听到的系统广播，静态注册也应该能监听到，在过去的 Android 系统中确实是这样。

由于大量恶意的应用程序利用这个机制在程序未启动的情况下监听系统广播，从而使任何应用都可以频繁地从后台被唤醒，严重影响了用户手机的电量和性能。

因此 Android 系统几乎每个版本都在削减静态注册 BroadcastReceiver 的功能。

在 Android 8.0 系统之后，所有**隐式广播**都不允许使用静态注册的方式来接收了。隐式广播指的是那些没有具体指定发送给哪个应用程序的广播，大多数系统广播属于隐式广播，但是少数特殊的系统广播目前仍然允许使用静态注册的方式来接收。

这些特殊的系统广播列表详见 [Implicit Broadcast Exceptions](https://developer.android.com/guide/components/broadcast-exceptions)

如果程序需要进行一些对用户来说比较敏感的操作，必须在 AndroidManifest.xml 文件中进行权限声明，否则程序将会直接崩溃。Android 6.0 系统中引入了更加严格的运行时权限。

```xml
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

不要在 `onReceive()` 方法中添加过多的逻辑或者进行任何的耗时操作，因为在 BroadcastReceiver 中是不允许开启线程的，当 `onReceive()` 方法运行了较长时间而没有结束时，程序就会报错。

## 发送自定义广播

默认情况下我们发出的自定义广播都是隐式广播，为了让静态注册的 BroadcastReceiver 能够接收到，调用 `setPackage()` 方法，指定这条广播发送给哪个应用程序的，从而让它变成一条显式广播。

```kotlin
button.setOnClickListener {
    val intent = Intent("com.homurax.broadcasttest.MY_BROADCAST")
    intent.setPackage(packageName)
    sendBroadcast(intent)
}
```

### 发送有序广播























