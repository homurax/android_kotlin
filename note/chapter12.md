## 什么是 Material Design

Material Design 是由 Google 的设计工程师们基于传统优秀的设计原则，结合丰富的创意和科学技术所发明的一套全新的界面设计语言，包含了视觉、运动、互动效果等特性。

在 2015 年的 Google I/O 大会上推出了一个 Design Support 库，这个库将 Material Design 中最具代表性的一些控件和效果进行了封装，使得开发者即使在不了解 Material Design 的情况下也能非常轻松地将自己的应 Material 化。后来 Design Support 库又改名成了 Material 库，用于给 Google 全平台类的产品提供 Material Design 的支持。

## Toolbar

ActionBar 就是每个 Activity 默认最顶部的那个标题栏，由于设计的原因，被限定只能位于 Activity 的顶部。

Toolbar 继承了 ActionBar 的所有功能，灵活性高，可以配合其他控件完成一些 Material Design 的效果。

不带有 ActionBar  的主题通常有

- `Theme.AppCompat.NoActionBar `

  表示深色主题，界面的主题颜色设成深色，陪衬颜色设成浅色。

- `Theme.AppCompat.Light.NoActionBar`

  表示浅色主题，界面的主题颜色设成浅色，陪衬颜色设成深色。

*/res/values/styles.xml*

```xml
<resources>

    <!-- Base application theme. -->
    <style name="AppTheme" parent="Theme.AppCompat.Light.NoActionBar">
        <!-- Customize your theme here. -->
        <item name="colorPrimary">@color/colorPrimary</item>
        <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
        <item name="colorAccent">@color/colorAccent</item>
    </style>

</resources>
```

各种属性指定颜色的位置。

![](../images/chapter12/color_position.png)

colorAccent 不只是用来指定这样一个按钮的颜色，而是更多表达了一个强调的意思，比如一些控件的选中状态也会使用 colorAccent 的颜色。

**使用 Toolbar**

```xml
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <androidx.appcompat.widget.Toolbar
        android:id="@+id/toolbar"
        android:layout_width="match_parent"
        android:layout_height="?attr/actionBarSize"
        android:background="@color/colorPrimary"
        android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
        app:popupTheme="@style/ThemeOverlay.AppCompat.Light" />
    
</FrameLayout>
```

里使用 `xmlns:app` 指定了一个新的命名空间，许多 Material 属性是在新系统中新增的，为了能够兼容老系统，就不能那个使用 `android:attribute` 这样的写法了，而是应该使用 `app:attribute` 。

将 Toolbar 的高度设置为 actionBar 的高度，背景色设置成了 colorPrimary 。

刚才在 `styles.xml` 中将程序的主题指定成了淡色主题，因此 Toolbar 现在也是淡色主题，而 Toolbar 上面的各种元素就会自动使用深色系，从而和主体颜色区别开。但是之前使用 ActionBar 时文字都是白色的，现在变成黑色的会很难看。

为了能让 Toolbar 单独使用深色主题，这里使用 ` android:theme` 属性，将 Toolbar 的主题指定成了 `ThemeOverlay.AppCompat.Dark.ActionBar` 。使用 `app:popupTheme` 属性单独将弹出的菜单项指定成了淡色主题。

**为 Toolbar 添加 action 按钮**

```xml
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">

    <item
        android:id="@+id/backup"
        android:icon="@drawable/ic_backup"
        android:title="@string/backup"
        app:showAsAction="always" />
    <item
        android:id="@+id/delete"
        android:icon="@drawable/ic_delete"
        android:title="@string/delete"
        app:showAsAction="ifRoom" />
    <item
        android:id="@+id/settings"
        android:icon="@drawable/ic_settings"
        android:title="@string/settings"
        app:showAsAction="never" />
</menu>
```

再次使用了 app 命名空间，同样是为了能够兼容低版本的系统。

showAsAction 用来指定按钮的显示位置，主要有一下几种值可选：

- always

  永远显示在 Toolbar 中，如果屏幕空间不够则不显示

- ifRoom

  屏幕空间足够的情况下显示在 Toolbar 中，不够的话就显示在菜单当中

- never

  永远显示在菜单当中

Toolbar 中的 action 按钮只会显示图标，菜单中的 action 按钮只会显示文字。

## 滑动菜单

所谓的滑动菜单，就是将一些菜单选项隐藏起来，而不是放置在主屏幕上，然后可以通过滑动的方式将菜单显示出来。这种方式既节省了屏幕空间，又实现了非常好的动画效果，是 Material Design 中推荐的做法。

AndroidX 库中提供了一个 DrawerLayout 控件。

```xml
<androidx.drawerlayout.widget.DrawerLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/drawerLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <androidx.appcompat.widget.Toolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize"
            android:background="@color/colorPrimary"
            android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
            app:popupTheme="@style/ThemeOverlay.AppCompat.Light" />
    </FrameLayout>

    <TextView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:gravity="start"
        android:background="#FFF"
        android:text="@string/this_is_menu"
        android:textSize="30sp" />

</androidx.drawerlayout.widget.DrawerLayout>
```

第二个子控件使用了一个 TextView，用于作为滑动菜单中显示的内容，其实使用什么都可以，DrawerLayout 并没有限制只能使用固定的控件。

第二个子控件 layout_gravity 这个属性是必须指定的，因为需要告诉 DrawerLayout 滑动菜单是在屏幕的左边还是右边，指定 left 表示滑动菜单在左边，指定 right 表示滑动菜单在右边。

指定 start，表示会根据系统语言进行判断，如果系统语言是从左往右的，比如英语、汉语，滑动菜单就在左边，如果系统语言是从右往左的，比如阿拉伯语，滑动菜单就在右边。

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    ...
    supportActionBar?.let {
        it.setDisplayHomeAsUpEnabled(true)
        it.setHomeAsUpIndicator(R.drawable.ic_menu)
    }
}

override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
        android.R.id.home -> drawerLayout.openDrawer(GravityCompat.START)
        ...
    }
    return true
}
```

设置显示导航栏，设置导航按钮图标。实际上，Toolbar 最左侧的这个按钮就叫作 Home 按钮，它默认的图标是一个返回的箭头，含义是返回上一个 Activity 。这里将它默认的样式和作用都进行了修改。

Home 按钮的 id 永远都是 `android.R.id.home`。然后调用 DrawerLayout 的 `openDrawer()` 方法将滑动菜单展示出来，传入一个 Gravity 参数，为了保证这里的行为和 XML 中定义的一致，传入 `GravityCompat.START` 。

### NavigationView

NavigationView 是 Material 库中提供的一个控件，它不仅是严格按照 Material Design 的要求来进行设计的，而且还可以将滑动菜单页面的实现变得非常简单。

```
implementation 'com.google.android.material:material:1.0.0'
implementation 'de.hdodenhof:circleimageview:3.1.0'
```

```xml
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <group android:checkableBehavior="single">
        <item
            android:id="@+id/navCall"
            android:icon="@drawable/nav_call"
            android:title="@string/call" />
        <item
            android:id="@+id/navFriends"
            android:icon="@drawable/nav_friends"
            android:title="@string/friends" />
        <item
            android:id="@+id/navLocation"
            android:icon="@drawable/nav_location"
            android:title="@string/location" />
        <item
            android:id="@+id/navMail"
            android:icon="@drawable/nav_mail"
            android:title="@string/mail" />
        <item
            android:id="@+id/navTask"
            android:icon="@drawable/nav_task"
            android:title="@string/tasks" />
    </group>
</menu>
```

group 表示一个组，checkableBehavior 指定为 single 表示组中的所有菜单项只能单选。

```xml
<com.google.android.material.navigation.NavigationView
    android:id="@+id/navView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_gravity="start"
    app:menu="@menu/nav_menu"
    app:headerLayout="@layout/nav_header"/>
```

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    ...
    // 设置默认选中
    navView.setCheckedItem(R.id.navCall)
    // 菜单项选中事件监听
    navView.setNavigationItemSelectedListener {
        drawerLayout.closeDrawers()
        true
    }
}
```

## 悬浮按钮和可交互提示

### FloatingActionButton

这个控件可以比较轻松地实现悬浮按钮的效果。

```xml
<com.google.android.material.floatingactionbutton.FloatingActionButton
    android:id="@+id/fab"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="bottom|end"
    android:layout_margin="16dp"
    android:src="@drawable/ic_done" />
```

还里使用 `app:elevation` 属性来给 FloatingActionButton 指定一个高度值，高度值越大，投影范围也越大，但是投影效果越淡，高度值越小，投影范围也越小，但是投影效果越浓。当然这些效果的差异其实都不怎么明显。

FloatingActionButton 和普通的 Button 其实没什么两样，都是调用 `setOnClickListener()` 方法来设置按钮的点击事件。

### Snackbar

Toast 的作用是告诉用户现在发生了什么事情，但同时用户只能被动接收这个事情，因为没有什么办法能让用户进行选择。

Snackbar 则在这方面进行了扩展，它允许在提示当中加入一个可交互按钮，当用户点击按钮的时候可以执行一些额外的逻辑操作。

```kotlin
fab.setOnClickListener {
    Snackbar.make(it, "Data deleted", Snackbar.LENGTH_SHORT)
        .setAction("Undo") {
            Toast.makeText(this, "Data restored", Toast.LENGTH_SHORT).show()
        }
        .show()
}
```

### CoordinatorLayout

CoordinatorLayout 可以说是一个加强版的 FrameLayout，这个布局也是由 Material 库提供的。CoordinatorLayout 可以监听其所有子控件的各种事件，然后自动帮助我们做出最为合理的响应。

点击悬浮按钮，悬浮按钮自动向上偏移了 Snackbar 的同等高度，从而确保不会被遮挡住，当 Snackbar 消失的时候，悬浮按钮会自动向下偏移回到原来位置。另外悬浮按钮的向上和向下偏移也是伴随着动画效果的，且和 Snackbar 完全同步。

Snackbar 并不是 CoordinatorLayout 的子控件，但是它却可以被监听到。

在 Snackbar 的 `make()` 方法中传入的第一个参数是用来指定 Snackbar 是基于哪个 View 来触发的。而  FloatingActionButton 是 CoordinatorLayout 中的子控件，因此这个事件就理所应当能被监听到了。

## 卡片式布局

### MaterialCardView

MaterialCardView 也是一个 FrameLayout，只是额外提供了圆角和阴影等效果，看上去会有立体的感觉。

```xml
<com.google.android.material.card.MaterialCardView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:cardCornerRadius="4dp"
    android:elevation="5dp">

    <TextView
        android:id="@+id/infoText"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>

</com.google.android.material.card.MaterialCardView>
```

可以通过 `app:cardCornerRadius` 属性指定卡片圆角的弧度，数值越大，圆角的弧度也越大。另外还可以通过 `app:elevation` 属性指定卡片的高度，高度值越大，投影范围也越大，但是投影效果越淡，高度值越小，投影范围也越小，但是投影效果越浓，这一点和 FloatingActionButton 是一致的。



```
implementation 'com.github.bumptech.glide:glide:4.11.0'
```

Glide 是一个超级强大的图片加载库，它不仅可以用于加载本地图片，还可以加载网络图片、GIF 图片甚至是本地视频。最重要的是，Glide 的用法非常简单，只需几行代码就能轻松实现复杂的图片加载功能。











