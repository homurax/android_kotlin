## 如何编写程序界面

Android 应用程序的界面主要是通过编写 XML 的方式来实现的。最近几年，Google 又推出了一个全新的界面布局：ConstraintLayout 。ConstraintLayout 不是非常适合通过编写 XML 的方式来开发界面，而是更加适合在可视化编辑器中使用拖放控件的方式来进行操作。

## 常用控件的使用方式

### TextView

`android:layout_width` 和 `android:layout_height` 指定了控件的宽度和高度，Android 中所有控件都具有这两个属性。

- `match_parent` 

  让当前控件的大小和父布局的大小一样，也就是由父布局来决定当前控件的大小。

- `wrap_content`

  让当前控件的大小能够刚好包含住里面的内容，也就是由控件内容决定当前控件的大小。
  
- 固定值 / dp

  dp 是一种与屏幕密度无关的尺寸单位，可以保证不同分辨率的手机上的显示效果尽可能地一致。

`android:gravity` 来指定文字的对齐方式，可以用“|”来同时指定多个值，指定的 `center`，效果等同于 `center_vertical|center_horizontal`，表示文字在垂直和水平方向都居中对齐。

`android:textColor` 属性可以指定文字的颜色。

`android:textSize` 属性可以指定文字的大小。文字大小要使用 sp 作为单位，当用户在系统中修改了文字显示尺寸时，应用程序中的文字大小也会跟着变化。

### Button

Android 系统默认会将按钮上的英文字母全部转换成大写。可以使用配置来禁用这一默认特性：`android:textAllCaps="false"` 。

### EditText

`android:hint` 属性用来指定一段提示性的文本。

`android:maxLines` 指定 EditText 的最大行数，这样当输入的内容超过指定的行数时，文本就会向上滚动，而 EditText 则不会再继续拉伸。

`editText.text.toString()` 获取文本内容。

### ImageView

修改显示的图片：`ImageView.setImageResource(R.drawable.img_2)`

### ProgressBar

ProgressBar 用于在界面上显示一个进度条，表示我们的程序正在加载一些数据。
`android:visibility` 是所有的 Android 控件都具有的可见属性。

- visibl

  可见的，默认值。

- invisible

  不可见，但是它仍然占据着原来的位置和大小，可以理解成控件变成透明状态了。

- gone

  控件不仅不可见，而且不再占用任何屏幕空间。

获取/设置 ProgressBar 状态：`getVisibility()/setVisibility()`

获取/设置 ProgressBar 的当前进度：`getProgress()/setProgress()`

### AlertDialog

AlertDialog 可以在当前的界面弹出一个对话框，这个对话框是置顶于所有界面元素之上的，能够屏蔽掉其他控件的交互能力，因此 AlertDialog 一般都是用于提示一些非常重要的内容或者警告信息。

```kotlin
AlertDialog.Builder(this).apply {
    setTitle("This is Dialog")
    setMessage("Something important.")
    setCancelable(false)
    setPositiveButton("OK") { dialog, which ->
    }
    setNegativeButton("Cancel") { dialog, which ->
    }
    show()
}
```

## 三种基本布局

布局是一种可用于放置很多控件的容器，它可以按照一定的规律调整内部控件的位置。布局的内部除了放置控件外，也可以放置布局，可以嵌套。

![](../images/chapter04/layout_controls.png)

### LinearLayout

LinearLayout 会将它所包含的控件在线性方向上依次排列。

`android:orientation` 指定排列方向是 `vertical` 还是 `horizontal` 。

如果 LinearLayout 的排列方向是 horizontal，内部的控件就绝对不能将宽度指定为 match_parent，因为这样的话，单独一个控件就会将整个水平方向占满，其他的控件就没有可放置的位置了。同样的道理，如果 LinearLayout 的排列方向是 vertical，内部的控件就不能将高度指定为 match_parent 。

`android:gravity` 用于指定文字在控件中的对齐方式。

`android:layout_gravity` 用于指定控件在布局中的对齐方式。

当 LinearLayout 的排列方向是 horizontal 时，只有垂直方向上的对齐方式才会生效，因为此时水平方向上的长度是不固定的，每添加一个控件，水平方向上的长度都会改变，因而无法指定该方向上的对齐方式。

同样的道理，当 LinearLayout 的排列方向是 vertical 时，只有水平方向上的对齐方式才会生效。

`android:layout_weight` 可以用于按照比例分配屏幕宽度，比较规范的写法是使用 `android:layout_weight` 时，同时指定 `android:layout_width="0dp"` 。

### RelativeLayout 

RelativeLayout 可以通过相对定位的方式让控件出现在布局的任何位置。RelativeLayout 中的属性非常多。

布局属性都记录在 [`RelativeLayout.LayoutParams`](https://developer.android.com/reference/android/widget/RelativeLayout.LayoutParams) 。

相对于父布局定位、相对于控件定位。

- `android:layout_alignParentTop` 使该视图的**顶部边缘**与父视图的顶部边缘匹配。
- `android:layout_alignParentBottom` 使该视图的**底部边缘**与父视图的底部边缘匹配。
- `android:layout_centerInParent` 将此子级**在其父级中水平和垂直居中**。
- `android:layout_alignParentStart` 使该视图的**起始边缘**与父视图的起始边缘匹配。
- `android:layout_alignParentEnd` 使该视图的**末端边缘**与父视图的末端边缘匹配。 
- `android:layout_alignParentLeft` 使该视图的**左边缘**与父视图的左边缘匹配。
- `android:layout_alignParentRight` 使该视图的**右边缘**与父视图的右边缘匹配。
- `android:layout_above` 将此视图的**底边边缘**定位在给定的锚视图ID**上方**。
- `android:layout_below` 将此视图的**顶部边缘**定位在给定的锚视图ID**之下**。
- `android:layout_toStartOf` 将此视图的**末端边缘**定位到给定锚视图ID的起点。
- `android:layout_toEndOf` 将此视图的**开始边缘**定位到给定锚视图ID的末尾。
- `android:layout_toLeftOf` 将此视图的**右边缘**定位到给定的锚视图ID的左侧。
- `android:layout_toRightOf` 将此视图的**左边缘**定位在给定的锚视图ID的右侧。

### **FrameLayout**

FrameLayout 所有的控件都会默认摆放在布局的左上角。

## 创建自定义控件

所有控件都是直接或间接继承自 View 的，所有的布局都是直接或间接继承自 ViewGroup 的。

View 是 Android 中最基本的一种 UI 组件，它可以在屏幕上绘制一块矩形区域，并能响应这块区域的各种事件，因此，我们使用的各种控件其实就是在 View 的基础之上又添加了各自特有的功能。

而 ViewGroup 则是一种特殊的 View，它可以包含很多子 View 和子 ViewGroup，是一个用于放置控件和布局的容器。

### 引入布局

```xml
<include layout="@layout/title" />
```

### 创建自定义控件

```kotlin
class TitleLayout(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    init {
        // 对标题栏布局进行动态加载 构建 LayoutInflater 对象，动态加载布局文件
        // 传入布局文件id 和 父布局
        LayoutInflater.from(context).inflate(R.layout.title, this)
        titleBack.setOnClickListener {
            // Kotlin 中的类型强制转换使用的关键字是 as
            val activity = context as Activity
            activity.finish()
        }
        titleEdit.setOnClickListener {
            Toast.makeText(context, "You clicked Edit button.", Toast.LENGTH_SHORT).show()
        }
    }
}
```

## ListView

已使用 adapter 的形式给 ListView 提供数据。

**repeat， Kotlin 中的标准函数，它允许传入一个数值 n，然后会把 Lambda 表达式中的内容执行 n 遍。**

ArrayAdapter 使用中，使用 inner class ViewHolder 来缓存 ImageView 和 TextView 。

ViewHolder 实例绑定在 View 的 tag 上。

## RecyclerView

```
implementation "androidx.recyclerview:recyclerview:1.1.0"
```

纵向滚动、横向滚动、瀑布流布局。

RecyclerView.Adapter 使用中，同样使用 inner class ViewHolder 来缓存 ImageView 和 TextView 。ViewHolder 继承了 RecyclerView.ViewHolder 。

`onCreateViewHolder()` 中创建 ViewHolder 实例，注册点击事件。

`onBindViewHolder()` 中对 RecyclerView 子项赋值，每个子项滚动到屏幕内时执行。

`getItemCount()` 用于获取子项数量。

`getItemViewType()` 返回当前 position 对应的类型。



ListView 的布局排列是由自身去管理的，而 RecyclerView 则将这个工作交给了 LayoutManager。

LayoutManager 中制定了一套可扩展的布局排列接口，子类只要按照接口的规范来实现，就能定制出各种不同排列方式的布局了。

LinearLayoutManager 可以用于实现线性布局。

GridLayoutManager 可以用于实现网格布局。

StaggeredGridLayoutManager 可以用于实现瀑布流布局。

## UI 的最佳实践

### 9-Patch 图片制作

可以在图片 4 个边框绘制一个个的小黑点，在上边框和左边框绘制的部分表示当图片需要拉伸时就拉伸黑点标记的区域，在下边框和右边框绘制的部分表示内容允许被放置的区域。

使用鼠标在图片边缘拖动就可以进行绘制，按住 Shift 键拖动可以进行擦除。

### 聊天界面

**使用 const 定义常量，只有在单例类、companion object 或顶层方法中才可以使用 const 关键字。**

## Kotlin : 延迟初始化和密封类

## 对变量延迟初始化

由于全局变量 adapter 是在 `onCreate()` 方法中初始化的，所以不得不先将其赋值为 null，同时把它的类型声明成 `MsgAdapter?` 。其他方法中调用 adapter 时仍然要进行判空处理。

使用 **`lateinit`** 关键字延迟初始化，它可以告诉 Kotlin 编译器，会在之后对其初始化，就不用赋值为 null 了，同时类型声明也就可以改为 `MsgAdapter` 了，其他方法中也就不再需要空值处理。

但是如果在变量还没有初始化的情况下就直接使用它，程序一定会崩溃，并且抛出一个 `UninitializedPropertyAccessException` 异常。

可以通过 `::adapter.isInitialized` 来判断 adapter  是否已经初始化。

### 使用密封类优化代码

```kotlin
interface Result
class Success(val msg: String) : Result
class Failure(val error: Exception) : Result

fun getResultMsg(result: Result) = when (result) {
    is Success -> result.msg
    is Failure -> "Error is ${result.error.message}"
    else -> throw IllegalArgumentException()
}
```

如果缺少 else 条件，Kotlin 编译器会认为这里缺少条件分支，无法编译通过。实际上 Result 的执行结果只可能是 Success 或者 Failure，else 条件是永远走不到的。这里抛出异常只是为了满足 Kotlin 编译器语法检查的需要。

此时 else 还有一个潜在风险，如果新增 Result 实现类，但是忘记在 `getResultMsg()` 中添加分支，就会进入 else 分支抛出异常。

Kotlin 的密封类可以很好地解决这个问题，密封类的关键字是 **`sealed class`** 。

```kotlin
sealed class Result
class Success(val msg: String) : Result()
class Failure(val error: Exception) : Result()

fun getResultMsg(result: Result) = when (result) {
    is Success -> result.msg
    is Failure -> "Error is ${result.error.message}"
}
```

密封类是一个可继承的类，因此在继承它的时候需要在后面加上一对括号。

当 when 语句中传入一个密封类变量作为条件的时候，Kotlin 会自动检查该密封类有那些子类，并强制要求将每一个子类所对应的条件全部处理。

密封类及其所有子类只能定义在同一个文件的顶层位置，不能嵌套在其他类中，这是密封类底层的实现机制所限制的。
