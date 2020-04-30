编写一个库提供给其他项目去使用，可以统称为 SDK 开发。

## 实现 PermissionX 开源库

对运行时权限的 API 封装并不是一件容易的事，因为这个操作是有特定的上下文依赖的。一般需要在 Activity 中接收 `onRequestPermissionsResult()` 方法的回调才行，所以不能那个简单地将整个操作封装到一个独立的类中。

受此限制，也衍生出了一些特别的解决方案，比如将运行时权限的操作封装到 BaseActivity 中，或者提供一个透明的 Activity 来处理运行时权限等。

Google 在 Fragment 中提供了相同的 API，使得在 Fragment 也能申请运行时权限。Fragment 并不必须有界面，所以可以向 Activity 中添加一个隐藏的 Fragment，在这个隐藏的 Fragment 中对运行时权限 API 封装。

---

```kotlin
typealias PermissionCallback = (Boolean, List<String>) -> Unit

class InvisibleFragment : Fragment() {

    private var callback: PermissionCallback? = null

    fun requestNow(cb: PermissionCallback, vararg permissions: String) {
        callback = cb
        requestPermissions(permissions, 1)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            val deniedList = ArrayList<String>()
            for ((index, result) in grantResults.withIndex()) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    deniedList.add(permissions[index])
                }
            }
            val allGranted = deniedList.isEmpty()
            callback?.let { it(allGranted, deniedList) }
        }
    }

}
```

**typealias** 关键字可以用于给任意类型指定一个别名，从而让代码更加简洁易懂。

InvisibleFragment 中并没有重写 `onCreateView()` 方法来加载某个布局，因为它自然就是一个不可见的 Fragment，只需要将它添加到 Activity 中即可。

---

```kotlin
object PermissionX {

    private const val TAG = "InvisibleFragment"

    fun request(
        activity: FragmentActivity,
        vararg permissions: String,
        callback: PermissionCallback
    ) {
        val fragmentManager = activity.supportFragmentManager
        val existedFragment = fragmentManager.findFragmentByTag(TAG)
        val fragment = if (existedFragment != null) {
            existedFragment as InvisibleFragment
        } else {
            val invisibleFragment = InvisibleFragment()
            fragmentManager.beginTransaction().add(invisibleFragment, TAG).commitNow()
            invisibleFragment
        }
        fragment.requestNow(callback, *permissions)
    }

}
```

FragmentActivity 是 AppCompatActivity 的父类。

需要注意 permissions 参数在这里实际上是一个数组，对于数组，是不可以直接将它传递给另一个接收可变长参数的方法。`*` 表示将一个数组转换成可变长参数传递过去，而不是指针的意思。

## 对开源库进行测试

`app/build.gradle` 中引入 library 模块：

```
dependencies {
    ...
    implementation project(':library')
}
```

```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        makeCallBtn.setOnClickListener {
            PermissionX.request(this, Manifest.permission.CALL_PHONE) { allGranted, deniedList ->
                if (allGranted) {
                    call()
                } else {
                    Toast.makeText(this, "You denied $deniedList", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun call() {
        try {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:10086")
            startActivity(intent)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}

```

## 将开源库发布到 jcenter 仓库

```
buildscript {
    ext.kotlin_version = '1.3.72'
    repositories {
        google()
        jcenter()
    }
    ...
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}
```

google 仓库中包含的主要是 Google 自家的扩展依赖库，jcenter 仓库中包含的大多数一些第三方的开源库。

**Bintray**：https://bintray.com/

Bintray 官方提供的用于将代码发布到 jcenter 仓库的插件使用有点复杂，需要编写很多 Gradle 脚本。

这里使用 ***bintray-release***：https://github.com/novoda/bintray-release

`library/build.gradle` 中追加：

```
apply plugin: 'com.novoda.bintray-release'

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.novoda:bintray-release:0.9.2'
    }
}

publish {
    userOrg = 'homurax'
    groupId = 'com.permissionx.homurax'
    artifactId = 'permissionx'
    publishVersion = '1.0.0'
    desc = 'Make Android runtime permission request easy.'
    website = 'https://github.com/homurax/android_kotlin'
}
```

上传：

```
gradlew clean build bintrayUpload -PbintrayUser=USER -PbintrayKey=KEY -PdryRun=false
```





























