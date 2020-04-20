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

## 调用摄像头和相册

### 调用摄像头拍照

应用关联缓存目录：

```
/sdcard/Android/data/<package name>/cache
```

从 Android 6.0 系统开始，读写 SD 卡被列为了危险权限，需要进行运行时权限处理。应用关联缓存目录不需要。

从 Android 10.0 系统开始，公有的 SD 卡目录已经不再允许被应用程序直接访问了，而是要使用作用域存储。

从 Android 7.0 系统开始，直接使用本地真实路径的 Uri 被认为是不安全的，而 FileProvider 是一种特殊的 ContentProvider，提高了应用的安全性。

```kotlin
class MainActivity : AppCompatActivity() {

    val takePhoto = 1
    lateinit var imageUrl: Uri
    lateinit var outputImage: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        takePhotoBtn.setOnClickListener {
            // 应用关联缓存目录 /sdcard/Android/data/<package name>/cache
            outputImage = File(externalCacheDir, "output_image.jpg")
            if (outputImage.exists()) {
                outputImage.delete()
            }
            outputImage.createNewFile()
            imageUrl = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // context / 任意唯一的字符串 / File 对象
                FileProvider.getUriForFile(this, "com.homurax.cameraalbumtest.fileprovider", outputImage)
            } else { // 系统版本低于 Android 7.0
                Uri.fromFile(outputImage)
            }
            // 启动相机
            val intent = Intent("android.media.action.IMAGE_CAPTURE")
            // 指定图片的输出地址
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUrl)
            startActivityForResult(intent, takePhoto)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            takePhoto -> {
                if (resultCode == Activity.RESULT_OK) {
                    val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUrl))
                    imageView.setImageBitmap(rotateIfRequired(bitmap))
                }
            }
        }
    }

    private fun rotateIfRequired(bitmap: Bitmap): Bitmap {
        var exif = ExifInterface(outputImage.path)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270)
            else -> bitmap
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix().apply {
            postRotate(degree.toFloat())
        }
        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        // 回收不再需要的 Bitmap
        bitmap.recycle()
        return rotatedBitmap
    }

}
```

### 从相册中选择图片

```kotlin
fromAlbumBtn.setOnClickListener {
    // 打开文件选择器
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    // 指定只显示照片
    intent.type = "image/*"
    startActivityForResult(intent, fromAlbum)
}
```

```kotlin
when (requestCode) {
    ...
    fromAlbum -> {
        if (resultCode == Activity.RESULT_OK && data != null) {
            data.data?.let { uri ->
                // 将选择的照片显示
                val bitmap = getBitmapFromUri(uri)
                imageView.setImageBitmap(bitmap)
            }
        }
    }
}
```

```kotlin
private fun getBitmapFromUri(uri: Uri) = contentResolver.openFileDescriptor(uri, "r")?.use {
    BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
}
```

## 播放多媒体文件

### 播放音频

MediaPlayer 类中常用的控制方法。

| 方法名          | 功能描述                                                     |
| :-------------- | :----------------------------------------------------------- |
| setDataSource() | 设置要播放的音频文件的位置                                   |
| prepare()       | 在开始播放之前调用这个方法完成准备工作                       |
| start()         | 开始或继续播放音频                                           |
| pause()         | 暂停播放音频                                                 |
| reset()         | 将 MediaPlayer 对象重置到刚刚创建的状态                      |
| seekTo()        | 从指定的位置开始播放音频                                     |
| stop()          | 停止播放音频。调用这个方法后的 MediaPlayer 对象无法再播放音频 |
| release()       | 释放掉与 MediaPlayer 对象相关的资源                          |
| isPlaying()     | 判断当前 MediaPlayer 是否正在播放音频                        |
| getDuration()   | 获取载入的音频文件的时长                                     |

Android Studio 允许我们在项目工程中创建一个 assets 目录，并在这个目录下存放任意文件和子目录，这些文件和子目录在项目打包时会一并被打包到安装文件中，然后在程序中就可以借助 AssetManager 这个类提供的接口对 assets  目录下的文件进行读取。

```kotlin
class MainActivity : AppCompatActivity() {

    private val mediaPlayer = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initMediaPlayer()
        play.setOnClickListener {
            if (!mediaPlayer.isPlaying) {
                // 开始播放
                mediaPlayer.start()
            }
        }
        pause.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                // 暂停播放
                mediaPlayer.pause()
            }
        }
        stop.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                // 停止播放
                mediaPlayer.reset()
                initMediaPlayer()
            }
        }
    }

    private fun initMediaPlayer() {
        // 获取 AssetManager 实例
        val assetManager = assets
        val fd = assetManager.openFd("music.mp3")
        mediaPlayer.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
        mediaPlayer.prepare()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
    }
}

```

### 播放视频









