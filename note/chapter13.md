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

## LiveData

LiveData 是 Jetpack 提供的一种响应式变成组件，它可以包含任何类型的数据，并在数据发生变的时候通知给它的观察者。LiveData 特别适合与 ViewModel 结合在一起使用。

### LiveData 的基本用法

Activity 中手动获取 ViewModel 中的数据这种交互方式，如果 ViewModel 内部开启了线程去执行一些耗时的逻辑，那么再点击按钮后立即去获取最新的数据，得到的肯定还是之前的数据。

ViewModel 生命周期长于 Activity，如果把 Activity 的实例传给 ViewModel，就很有可能因为 Activity 无法释放而造成内存泄漏。

可以将计数器的计数使用 LiveData 来包装，在 Acitivty 中去观察它。

```kotlin
class MainViewModel(countReserved: Int) : ViewModel() {

    var counter = MutableLiveData<Int>()

    init {
        counter.value = countReserved
    }

    fun plusOne() {
        val count = counter.value ?: 0
        counter.value = count + 1
    }

    fun clear() {
        counter.value = 0
    }

}
```

MutableLiveData 是一种可变的 LiveData。

- `getValue()`
- `setValue()` 只能在主线程调用。
- `postValue()` 用于在非主线程中给 LiveData 设置数据。

```kotlin
plusOneBtn.setOnClickListener {
    viewModel.plusOne()
}

clearBtn.setOnClickListener {
    viewModel.clear()
}

viewModel.counter.observe(this, Observer { count ->
    infoText.text = count.toString()
})
```

任何 LiveData 对象都可以调用它的 `observe()` 方法来观察数据的变化。

第一个参数是 LifecycleOwner 对象；第二个参数是一个 Observer 接口，当 LiveData 中包含数据发生变化时，就会回调这里。

```java
public void observe(LifecycleOwner owner, Observer<? super T> observer) {
    ...
}
```

`observe()` 方法同时接收两个单抽象方法接口参数时，要么同时使用函数式 API 的写法，要么都不用使用函数式 API 的写法。

2019 年的 Google I/O 大会上，Android 团队官宣了 Kotlin First，并且承诺未来会在 Jetpack 中提供更多专门面向 kotlin 语言的 API 。`lifecycle-livedata-ktx` 在 2.2.0 版本中加入了对 `observe()` 方法的语法扩展。

```
implementation "androidx.lifecycle:lifecycle-livedata-ktx:2.2.0"
```

目前对外暴露的是可变的 LiveData，推荐的做法是永远只暴露不可变的 LiveData 给外部。

```kotlin
class MainViewModel(countReserved: Int) : ViewModel() {
    
    val counter : LiveData<Int>
        get() = _counter

    private val _counter = MutableLiveData<Int>()

    init {
        _counter.value = countReserved
    }

    fun plusOne() {
        val count = _counter.value ?: 0
        _counter.value = count + 1
    }

    fun clear() {
        _counter.value = 0
    }

}
```

重新定义 counter 变量，类型为不可变的 LiveData，`get()` 属性方法中返回 _counter 变量。

### map 和 switchMap

**`map()` 方法作用是将实际包含数据的 LiveData 和仅用于观察数据的 LiveData 进行转换。**

```kotlin
data class User(var firstName: String, var lastName: String, val age: Int)

class MainViewModel(countReserved: Int) : ViewModel() {

    private val userLiveData = MutableLiveData<User>()

    val userName: LiveData<String> = Transformations.map(userLiveData) { user ->
        "${user.firstName} ${user.lastName}"
    }
    
    ...
}
```

外部不关心 age，外部使用的时候只要观察 userName 这个 LiveData 就可以了。当 userLiveData 的数据发生变化时，`map()` 方法会监听并转换函数中的逻辑，然后再将转换之后的数据通知给 userName 的观察者。

---

实际开发中，ViewModel 中的 LiveData 对象很可能是调用另外的方法获取的。

```kotlin
object Repository {

    fun getUser(userId: String): LiveData<User> {
        val liveData = MutableLiveData<User>()
        liveData.value = User(userId, userId, 0)
        return liveData
    }
}

class MainViewModel(countReserved: Int) : ViewModel() {

    fun getUser(userId: String): LiveData<User> {
        return Repository.getUser(userId)
    }
    ...
}
```

如果 Activity 中仍然使用 `viewModel.getUser(userId).observe(this, Observer {...}) ` 的方式去观察，他因为 `getUser()` 方法每次调用返回的都是一个新的 LiveData 实例，上述写法会一直观察老的 LiveData 实例。

**`switchMap()` 方法可以将这个 LiveData 对象转换成另一个可观察的 LiveData 对象。**

```kotlin
class MainViewModel(countReserved: Int) : ViewModel() {

    private val userIdLiveData = MutableLiveData<String>()

    val user: LiveData<User> = Transformations.switchMap(userIdLiveData) { userId ->
        Repository.getUser(userId)
    }

    fun getUser(userId: String) {
        userIdLiveData.value = userId
    }
    ...
}
```

userIdLiveData 对象用来观察 userId 的数据变化，然后调用 `switchMap()` 方法，用来对另一个可观察的 LiveData 对象进行转换。

当外部调用 MainViewModel 的 `getUser()` 方法来获取用户数据时，并不会发起任何请求或者函数调用，只会将 userId 值设置到 userIdLiveData 当中。

userIdLiveData 的数据发生变化，观察 userIdLiveData 的 `switchMap()` 方法就会执行，调用转换函数。在转换函数中调用 `Repository.getUser()` 方法获得真正的用户数据，同时 `switchMap()` 方法会将 `Repository.getUser()` 方法返回的 LiveData 对象转换成一个可观察的 LiveData 对象。Activity 观察这个 user 对象就可以了。

```kotlin
getUserBtn.setOnClickListener {
    val userId = (0..10000).random().toString()
    viewModel.getUser(userId)
}
viewModel.user.observe(this, Observer { user ->
    infoText.text = user.toString()
})
```













