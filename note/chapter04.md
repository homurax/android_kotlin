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

