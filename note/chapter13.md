## Jetpack

Jetpack 是一个开发组件工具集，它的主要目的是帮助我们编写更加简洁的代码，并简化我们的开发过程。Jetpack 中的组件有一个特点，它们大部分不依赖于任何 Android 系统版本，这意味着这些组件通常是定义在 AndroidX 库当中的，并且拥有非常好的向下兼容性。

![](../images/chapter13/jetpack_components.png)

Jetpack 主要由基础、架构、行为、界面这 4 个部分组成。

## ViewModel

传统的开发模式下，Activity 的任务实在是太重了，既要负责逻辑处理，又要控制 UI 展示，还得处理网络回调。

ViewModel 专门用于存放与界面相关的数据，ViewModel 的生命周期和 Activity 不同，在手机屏幕发生旋转的时候不会被重新创建，只有当 Activity 退出的时候才会跟着 Activity 一起销毁。

ViewModel 的生命周期示意图。


![](../images/chapter13/viewmodel-lifecycle.png)

### ViewModel 的基本用法

```
implementation "androidx.lifecycle:lifecycle-extensions:2.2.0"
```

模拟一个计数器功能，旋转屏幕不丢失数据。

```kotlin
class MainViewModel : ViewModel() {
    var counter = 0
}

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel =  ViewModelProvider(this).get(MainViewModel::class.java)
        plusOneBtn.setOnClickListener {
            viewModel.counter++
            refreshCounter()
        }
    }

    private fun refreshCounter() {
        infoText.text = viewModel.counter.toString()
    }
}
```

绝对不可以直接创建 ViewModel 的实例，ViewModel 有独立的生命周期，并且长于 Activity。

`ViewModelProviders.of()` 已被废弃。

### 向 ViewModel 传递参数

保证计数器即使在退出程序后又重新打开的情况下，数据仍然不会丢失。

```kotlin
class MainViewModelFactory(private val countReserved: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(countReserved) as T
    }
}

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel
    lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sp = getPreferences(Context.MODE_PRIVATE)
        val countReserved = sp.getInt("count_reserved", 0)

        viewModel =  ViewModelProvider(this, MainViewModelFactory(countReserved)).get(MainViewModel::class.java)

        plusOneBtn.setOnClickListener {
            viewModel.counter++
            refreshCounter()
        }

        clearBtn.setOnClickListener {
            viewModel.counter = 0
            refreshCounter()
        }

        refreshCounter()
    }

    override fun onPause() {
        super.onPause()
        sp.edit {
            putInt("count_reserved", viewModel.counter)
        }
    }

    private fun refreshCounter() {
        infoText.text = viewModel.counter.toString()
    }
}
```

在 MainViewModelFactory 中可以创建 ViewModel，`create()` 方法的执行时机和 Activity 的声明周期无关。

## Lifecycles

我们需要时刻感知到 Activity 的生命周期，以便在适当的时侯进行相应的逻辑控制。

在一个非 Activity 的类中去感知 Activity 的生命周期的需求是广泛存在的，同时也衍生出一系列的解决方案，比如通过在 Activity 中嵌入一个隐藏的 Fragment 来进行感知，或者通过手写监听器的方式来感知。

```kotlin
class MyObserver : LifecycleObserver {
    fun activityStart() {
    }

    fun activityStop() {
    }
}

class MainActivity : AppCompatActivity() {
    lateinit var observer: MyObserver
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observer = MyObserver()
    }

    override fun onStart() {
        super.onStart()
        observer.activityStart()
    }

    override fun onStop() {
        super.onStop()
        observer.activityStop()
    }
}
```

手写监听器的方式需要在 Activity 中编写大量的逻辑处理。

使用 Lifecycles 组件。

```kotlin
class MyObserver : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun activityStart() {
        Log.d("MyObserver", "activityStart")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun activityStop() {
        Log.d("MyObserver", "activityStop")
    }

}
```

使用了 `@OnLifecycleEvent` 注解，传入了一种生命周期事件。

生命周期事件的类型一共有 7 中：ON_CREATE、ON_START、ON_RESUME、ON_PAUSE、ON_STOP、ON_DESTROY 分别匹配 Activity 中相应的生命周期回调；另外的一种 ON_ANY 类型表示可以匹配 Activity 的任何生命周期回调。

通过 LifecycleOwner 让 MyObserver 得到通知：

```kotlin
lifecycleOwner.lifecycle.addObserver(MyObserver())
```

只要 Activity 是继承自 AppCompatActivity，或者 Fragment 是继承自 androidx.fragment.app.Fragment 的，那么它们本身就是一个 LifecycleOwner 实例。

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    ...
    lifecycle.addObserver(MyObserver())
}
```

主动获知当前的生命周期状态。

```kotlin
class MyObserver(val lifecycle: Lifecycle) : LifecycleObserver {
    ...
}
```

有了 Lifecycle 对象后，可以通过 `lifecycle.currentState` 来主动获知当前的生命周期状态。

返回的声明周期状态是一个枚举类型，一共有五种状态：`INITIALIZED`、`DESTROYED`、`CREATED`、`STARTED`、`RESUMED` 。

Activity 生命周期状态与事件的对应关系。

![](../images/chapter13/lifecycle-states.svg)

