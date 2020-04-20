## 将程序运行到手机上

设置 → 系统 → 开发者选项 → USB 调试

从 Android 4.2 系统开始，开发者选项默认是隐藏的，需要先进入“关于手机”界面，连续点击版本号那一栏，开发者选项就会显示出来。

## 使用通知

Notification 是 Android 系统中比较有特色的一个功能。当某个应用程序希望像用户发出一些提示信息，而该应用程序又不在前台运行时，就可以借助通知来实现。

### 创建通知渠道

Android 8.0 系统引入了通知渠道这个概念。

每条通知都要属于一个对应的渠道，每个应用程序都可以自由地创建当前应用拥有哪些通渠道。用户可以关闭具体的渠道，而不需要屏蔽所有信息。

```kotlin
// 获取 NotificationManager
val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    // 使用 NotificationChannel 构建通知渠道
    val channel = NotificationChannel(channelId, channelName, importance)
    manager.createNotificationChannel(channel)
}
```

渠道 ID 可以随便定义，需要保证全局唯一性。渠道名称是给用户看的，需要可以清楚地表达这个渠道的用途。通知的重要等级主要有 `IMPORTANCE_HIGH`、`IMPORTANCE_DEFAULT`、`IMPORTANCE_LOW`、`IMPORTANCE_MIN` ，重要程度从高到低。

### 通知的基本用法

通知可以在 Activity 里创建，也可以在 BroadcastReceiver 里创建，还可以在 Service 里创建。

在 Activity 里创建通知的场景比较少，因为一般只有当程序进入后台的时候才需要使用通知。

使用 AndroidX 库中的 NotificationCompat 来保证兼容性。

```kotlin
val notification = NotificationCompat.Builder(context, channelId).build()

val notification = NotificationCompat.Builder(context, channelId)
    .setContentTitle("This is content title") // 标题内容
    .setContentText("This is content text") // 正文内容
    .setSmallIcon(R.drawable.small_icon) // 小图标 显示在系统状态栏
    .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.large_icon)) // 大图标 下拉系统状态栏时可以看到
    .build()
```

调用 NotificationManager 的 `notify()` 方法让通知显示出来。

```kotlin
// 要保证每个通知指定的 id 都是不同的
manager.notify(1, notification)
```

Intent 倾向于立即执行某个动作，而 PendingIntent 倾向于在某个合适的时机执行某个动作。

PendingIntent 提供了几个静态方法用于获取实例，根据需求选择是使用 `getActivity()`、`getBroadcast()` 还是 `getService()`。

```kotlin
val intent = Intent(this, NotificationActivity::class.java)
// requestCode 一般用不到
// flags 用于确定 PendingIntent 的行为
val pi = PendingIntent.getActivity(this, 0, intent, 0)
val notification = NotificationCompat.Builder(this, "normal")
    ...
    .setContentIntent(pi)
    .build()
```

如果我们没有在代码中对该通知进行取消，它就会一直显示在系统的状态栏上。取消有两种方式：一种是在 NotificationCompat.Builder 中再连缀一个 `setAutoCancel()` 方法；一种是显示地调用 NotificationManager 的 `cancel()` 方法将它取消。

```kotlin
val notification = NotificationCompat.Builder(this, "normal")
    ...
    .setAutoCancel(true)
    .build()
```

```kotlin
class NotificationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // 通知的 id
        manager.cancel(1)
    }
}
```

### 通知的进阶技巧

**`setStyle()` 方法**

这个方法允许我们构建出富文本的通知内容。

过长的通知内容多余的部分会使用省略号代替，可以通过 `setStyle()` 方法显示一段长文字。

`NotificationCompat.BigTextStyle().bigText()`

```kotlin
val notification = NotificationCompat.Builder(this, "normal")
    ...
    .setStyle(NotificationCompat.BigTextStyle().bigText("Learn how to build notifications, send and sync data, and use voice actions. Get the official Android IDE and developer tools to build apps for Android."))
    .build()
```

显示一张大图片，`NotificationCompat.BigPictureStyle().bigPicture()`

```kotlin
val notification = NotificationCompat.Builder(this, "normal")
    ...
    .setStyle(NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(resources, R.drawable.big_image)))
    .build()
```

**不同重要等级的通知渠道对通知的行为的影响**

通知渠道的重要等级越高，发出的通知越容易获得用户的注意。高重要等级的通知渠道发出的通知可以弹出横幅、发出声音，低重要渠道的发出的通知不仅可能会在某些情况下被隐藏，而且可能会被改变显示的顺序。

开发者只能在创建通知渠道的时候为它指定初始的重要等级，如果用户不认可，可以随时进行修改，开发者对此无权再进行调整和变更，因为通知渠道一旦创建就不能再通过代码修改了。

所以需要创建一个新的通知渠道测试：

```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    val channel = NotificationChannel("normal", "Normal", NotificationManager.IMPORTANCE_DEFAULT)
    manager.createNotificationChannel(channel)
    val channel2 = NotificationChannel("important", "Important", NotificationManager.IMPORTANCE_HIGH)
    manager.createNotificationChannel(channel2)
}
```

















