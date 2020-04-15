## Fragment 是什么

Android 自 3.0 版本开始引入了 Fragment 的概念，Fragment 是一种可以嵌入在活动当中的 UI 片段，它能让程序更加合理和充分地利用大屏幕的空间，因而在平板上应用得非常广泛。

## Fragment 的使用方式

### 动态添加 Fragment 

动态添加 Fragment  主要分为 5 步：

- 创建待添加 Fragment  的实例。
- 获取 FragmentManager，在 Activity 中可以直接通过调用 `getSupportFragmentManager()` 方法获取。
- 开启一个事务，通过调用 `beginTransaction()` 方法开启。
- 向容器内添加或替换 Fragment，一般使用 `replace()` 方法实现，需要传入容器的 id 和待添加的 Fragment 实例。
- 提交事务，调用 `commit()` 方法来完成。

```kotlin
private fun replaceFragment(fragment: Fragment) {
    val fragmentManager = supportFragmentManager
    val transaction = fragmentManager.beginTransaction()
    transaction.replace(R.id.rightLayout, fragment)
    // transaction.addToBackStack(null)
    transaction.commit()
}
```

### 在 Fragment  中实现返回栈

FragmentTransaction 中提供了一个 `addToBackStack()` 方法，可以用于将一个事务添加到返回栈中。它可以接收一个名字用于描述返回栈的状态，一般传入null 即可。

### Fragment 和 Activity 之前的交互

**Activity 中获取 Fragment **

为了方便 Fragment  和 Activity 之间进行通信，FragmentManager 提供了一个类似于 `findViewById()` 的方法，专门用于从布局文件中获取碎片的实例。

```kotlin
val fragment = supportFragmentManager.findFragmentById(R.id.leftFrag) as LeftFragment
```

`kotlin-android-extensions` 插件也对 `findFragmentById()` 方法进行了扩展，允许我们直接使用布局文件中定义的 Fragment  id 名称来自动获取相应的 Fragment  实例。

```kotlin
val fragment = leftFrag as LeftFragment
```

**Fragment 中调用 Activity **

在每个 Fragment 中都可以通过调用 `getActivity()` 方法来得到和当前 Fragment 相关联的 Activity 实例。当 Fragment 中需要使用 Context 对象时，也可以使用该方法，因为获取到的 Activity 本身就是一个 Context 对象。

```kotlin
if (activity != null) {
    val mainActivity = activity as MainActivity
}
```

**Fragment 之间通信**

首先在一个 Fragment 中可以得到与它相关联的 Activity，然后再通过这个 Activity 去获取另外一个 Fragment 的实例，这样也就实现了不同 Fragment 之间的通信功能。

## Fragment 的生命周期

### Fragment 的状态和回调

- 运行状态

  当一个 Fragment 是可见的，并且它所关联的 Activity 正处于运行状态时，该 Fragment 也处于运行状态。

- 暂停状态

  当一个 Activity 进入暂停状态时（由于另一个未占满屏幕的 Activity 被添加到了栈顶），与它相关联的可见 Fragment 就会进入到暂停状态。

- 停止状态

  当一个 Activity 进入停止状态时，与它相关联的 Fragment 就会进入到停止状态，或者通过调用 FragmentTransaction 的 `remove()`、`replace()`方法将 Fragment 从 Activity 中移除，但如果在事务提交之前调用 `addToBackStack()` 方法，这时的 Fragment 也会进入到停止状态。总的来说，进入停止状态的 Fragment 对用户来说是完全不可见的，有可能会被系统回收。

- 销毁状态

  当 Activity 被销毁时，与它相关联的 Fragment 就会进入到销毁状态。或者通过调用 FragmentTransaction 的 `remove()`、`replace()`方法将碎片从活动中移除，但在事务提交之前并没有调用 `addToBackStack()` 方法，这时的碎片也会进入到销毁状态。

Fragment 类中也提供了一系列的回调方法，以覆盖碎片生命周期的每个环节。Activity 中有的回调方法，Fragment 中几乎 都有，Fragment 还提供了一些附加的回调方法。

- `onAttach()`

  当 Fragment 和 Activity 建立关联的时候调用。

- `onCreateView()`

  为 Fragment 创建视图（加载布局）时调用。

- `onActivityCreated()`

  确保与 Fragment 相关联的 Activity 已经创建完毕时调用。

- `onDestroyView()`

  当与 Fragment 关联的视图被移除的时候调用。

- `onDetach()`

  当 Fragment 和 Activity 解除关联的时候调用。

![](../images/chapter05/fragment_lifecycle.png)

## 动态加载布局的技巧

### 使用限定符

Android 中一些常见的 **qualifier** 表示。

<table>
   <tr>
      <td>屏幕特征</td>
      <td>限定符</td>
      <td>描述</td>
   </tr>
   <tr>
      <th rowspan="4">大小</th>
      <td>small</td>
      <td>提供给小屏幕设备的资源</td>
   </tr>
   <tr>
      <td>normal</td>
      <td>提供给中等屏幕设备的资源</td>
   </tr>
   <tr>
      <td>large</td>
      <td>提供给大屏幕设备的资源</td>
   </tr>
   <tr>
      <td>xlagre</td>
      <td>提供给超大屏幕设备的资源</td>
   </tr>
   <tr>
      <th rowspan="5">分辨率</th>
      <td>ldpi</td>
      <td>提供给低分辨率设备的资源（120dpi 以下）</td>
   </tr>
   <tr>
      <td>mdpi</td>
      <td>提供给中等分辨率设备的资源（120dpi-160dpi）</td>
   </tr>
   <tr>
      <td>hdpi</td>
      <td>提供给高分辨率设备的资源（160dpi-240dpi）</td>
   </tr>
   <tr>
      <td>xhdpi</td>
      <td>提供给超高分辨率设备的资源（240dpi-320dpi）</td>
   </tr>
   <tr>
      <td>xxhdpi</td>
      <td>提供给超超高分辨率设备的资源（320dpi-480dpi）</td>
   </tr>
   <tr>
      <th rowspan="2">方向</th>
      <td>land</td>
      <td>提供给横屏设备的资源</td>
   </tr>
   <tr>
      <td>port</td>
      <td>提供给竖屏设备的资源</td>
   </tr>
</table>

### 使用最小宽度限定符

**smallest-width qualifier**

最小宽度限定符允许我们对屏幕的宽度指定一个最小值（以 dp 为单位），然后以这个最小值为临界点，屏幕宽度大于这个值的设备就加载一个布局，屏幕宽度小于这个值的设备就加载另一个布局。

在 res 目录下新建 `layout-sw600dp` 文件夹，然后在这个文件夹下新建 `activity_main.xml` 布局。

当程序运行在屏幕宽度大于等于 600dp 的设备上时，会加载 `layout-sw600dp/activity_main` 布局，当程序运行在屏幕宽度小于 600dp 的设备上时，则仍然加载默认的 `layout/activity_main` 布局。

















