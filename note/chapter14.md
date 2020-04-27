## 全局获取 Context 的技巧

Android 提供了一个 Application 类，每当应用程序启动的时候，系统就会自动将这个类进行初始化。

可以定制一个自己的 Application 类，以便于管理程序内一些全局的状态信息，比如全局 Contex 。

```kotlin
class MyApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

}
```

将 Context 设置成静态变量很容易会产生内存泄露的问题，但是由于这里获取的不是 Activity 或 Service 中的 Context，而是 Application 中的 Context，它全局只会存在一份实例，并且整个应用程序的生命周期内都不会回收，因此是不存在内存泄露风险的。

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.homurax.materialtest">

    <application
        android:name=".MyApplication"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        ...
    </application>

</manifest>
```

告知系统程序启动的时候应该初始化 MyApplication 类，而不是默认的 Application 类。

在项目的任何地方使用 Context，只需要调用一下 `MyApplication.context` 就可以了。

```kotlin
fun String.showToast(duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(MyApplication.context, this, duration).show()
}

fun Int.showToast(duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(MyApplication.context, this, duration).show()
}
```

## 使用 Intent 传递对象

### Serializable 方式

```kotlin
class Person : Serializable {
    var name = ""
    var age = 0
}

// FirstActivity
val person = Person()
person.name = "Tom"
person.age = 20
val intent = Intent(this, SecondActivity::class.java).apply {
    putExtra("person_date", person)
}
startActivity(intent)

// SecondActivity
val person = intent.getSerializableExtra("person_date") as Person
```

### Parcelable

Parcelable 方式的实现原理是将一个完整的对象进行分解，分解后的每一部分都是 Intent 所支持的数据类型，这样就可以实现传递对象的功能了。

```kotlin
class Person : Parcelable {
    var name = ""
    var age = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        // 写出
        parcel.writeString(name)
        parcel.writeInt(age)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Person> {
        override fun createFromParcel(parcel: Parcel): Person {
            val person = Person()
            // 读取
            person.name = parcel.readString() ?: ""
            person.age = parcel.readInt()
            return person
        }

        override fun newArray(size: Int): Array<Person?> {
            return arrayOfNulls(size)
        }
    }
}

// FirstActivity 中仍然可以使用相同的代码来传递 Person 对象

// SecondActivity
val person = intent?.getParcelableExtra("person_date") as Person
```

**读取的顺序一定要和写出的顺序完全相同。**

---

Kotlin 提供了另外一种更加简便的用法，前提是要传递的所有数据都必须封装在对象的主构造函数中。

```kotlin
@Parcelize
class Person(var name: String, var age: Int) : Parcelable
```

## 定制自己的日志工具

我们希望能够自由地控制日志的打印，当程序处于开发阶段时就让日志打印出来，当程序上线之后就把日志屏蔽掉。

```kotlin
object LogUtil {

    private const val VERBOSE = 1

    private const val DEBUG = 2

    private const val INFO = 3

    private const val WARN = 4

    private const val ERROR = 5

    private var level = VERBOSE

    fun v(tag: String, msg: String) {
        if (level <= VERBOSE) {
            Log.v(tag, msg)
        }
    }

    fun d(tag: String, msg: String) {
        if (level <= DEBUG) {
            Log.d(tag, msg)
        }
    }

    fun i(tag: String, msg: String) {
        if (level <= INFO) {
            Log.i(tag, msg)
        }
    }

    fun w(tag: String, msg: String) {
        if (level <= WARN) {
            Log.w(tag, msg)
        }
    }

    fun e(tag: String, msg: String) {
        if (level <= ERROR) {
            Log.e(tag, msg)
        }
    }

}
```

## 调试 Android 程序

打断点进入 debug 模式的调试模式下，程序的运行效率会大大降低，如果断点位置加在一个比较靠后的位置，需要执行很多操作才能运行到这个断点，那么前面这些操作就会有一些卡顿的感觉。



Android 还提供了另外一种调试的方式，可以让程序随时进入调试模式。

使用正常的方式启动，在需要调试前点击 Android Studio 顶部工具栏的 `Attach debugger to Android process` 按钮。

弹框中选择进程，就会让选中的进程进入调试模式了。

## 深色主题

Android 10.0 系统中，Google 引入了深色主题这一特性，夜间模式成为官方支持的功能。

`Settings → Display → Dark theme`

最简单的一种适配方式就是使用 Force Dark，它是一种能让应用程序快速适配深色主题，并且几乎不用编写额外代码的方式。Force Dark 的工作原理是系统会分析浅色主题应用下的每一层 View，并且在这些 View 绘制到屏幕之前，自动将它们的颜色转换成更加适合深色主题的颜色。只有原本使用浅色主题的应用才能使用这种方式。

`android:forceDarkAllowed` 属性是从 API 29，也就是 Android 10.0 系统开始才有的，之前的系统无法指定这个属性。

***/res/values-v29/styles.xml***

```xml
<resources>
    <style name="AppTheme" parent="Theme.AppCompat.Light.NoActionBar">
        <item name="colorPrimary">@color/colorPrimary</item>
        <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
        <item name="colorAccent">@color/colorAccent</item>
        <item name="android:forceDarkAllowed">true</item>
    </style>
</resources>
```

---

AppCompat 库内置的主题除了浅色主题和深色主题，现在也多了一个 DayNight 主题。

使用了这个主题后，当用户在系统设置中开启了深色主题时，应用程序会自动使用深色主题，反之则会使用浅色主题。

首先删除 ` values-v29` 目录及目录下的内容。

修改 ***/res/values/styles.xml***

```xml
<resources>
    <!-- Base application theme. -->
    <style name="AppTheme" parent="Theme.AppCompat.DayNight.NoActionBar">
        <!-- Customize your theme here. -->
        <item name="colorPrimary">@color/colorPrimary</item>
        <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
        <item name="colorAccent">@color/colorAccent</item>
    </style>
    ...
</resources>
```

界面主要内容都应经切换成了深色主题，标题栏和悬浮按钮仍然保持着和浅色主题一样的颜色。

因为标题栏和悬浮按钮使用的是我们定义在 colors.xml 中的几种颜色值。这种指定颜色值引用的方式相当于对控件的颜色进行了硬编码，DayNight 主题是不能对这些颜色进行动态转换的。

需要进行一些主题差异型编程。

***/res/values-night/colors.xml***  中指定深色主题下的颜色值。

```xml
<resources>
    <color name="colorPrimary">#303030</color>
    <color name="colorPrimaryDark">#232323</color>
    <color name="colorAccent">#008577</color>
</resources>
```

在 DayNight 主题下，最好还是减少通过硬编码的方式来指定控件的颜色。应该更多地使用能够依据当前主题自动切换颜色的主题属性。我们可以使用主题属性来指定背景以及文字的颜色。

```xml
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="?android:attr/colorBackground">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:text="@string/hello_world"
        android:textSize="40sp"
        android:textColor="?android:attr/textColorPrimary" />

</FrameLayout>
```

代码中可以判断当前系统是否是深色主题：

```kotlin
object Util {
    fun isDarkTheme(context: Context): Boolean {
        val flag = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return flag == Configuration.UI_MODE_NIGHT_YES
    }
}
```

Kotlin 取消了按位运算符的写法，改成了使用英文关键字。

Kotlin 中的 `and` 关键字对应 Java 中的 `&` 运算符；`or` 关键字对应 Java 中的 `|` 运算符；`xor` 关键字对应 Java 中的 `^` 运算符。
