## 四大组件

- Activity

  应用中看的到的东西，都是放在**活动**中的。

- Service

  **服务**在后台运行，退出应用仍可继续运行。

- Broadcast Receiver

  **广播接收器**允许应用接收来自各处的广播消息，以及向外发出广播消息。

- Content Provider

  **内容提供器**为应用程序之间共享数据提供了可能。

## 目录结构

- `.gradle` 和 `.idea` : 自动生成的文件
- `app` : 项目的代码、资源都在这个目录下
  - `build`: 和外层类似，结构更复杂
  - `libs`: 存放第三方 jar 包
  - `androidTest`: 编写 Android Test 测试用例
  - `java`: 存放所有 Java/Kotlin 代码的地方
  - `res`: 图片、布局、字符串等等资源
  - `AndroidManifest.xml`: 整个 Android 项目的配置文件，程序中定义的所有四大组件都需要在这个文件里注册，另外还可以在这个文件中给应用程序添加权限声明。
  - `test`: 编写 Unit Test 测试用例
  - `build.gradle`: 是 app 模块的 gradle 构建脚本，会指定很多项目构建相关的配置
  - ` proguard-rules.pro`: 用于指定项目代码的混淆规则
- `build` : 编译时自动生成的文件
- `gradle` : 目录下包含了gradle wrapper 的配置文件，使用 gradle wrapper 的方式不需要提前将 gradle 下载好，而是会自动根据本地的缓存情况决定是否需要联网下载 gradle。
- `build.gradle` : 全局的 gradle 构建脚本
-  `gradle.properties` : 是全局的 gradle 配置文件
-  `gradlew` 和 `gradlew.bat` : 用来在命令行界面中执行 gradle 命令
-  `local.properties` : 用于指定本机中的 Android SDK 路径
-  `settings.gradle` : 用于指定项目中所有引入的模块

## 资源引用

以字符串为例

- 在代码中通过 `R.string.app_name` 获得该字符串的引用
- 在 XML 中通过 `@string/app_name` 获得该字符串的引用

## build.gradle

外层目录下

```groovy
// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    ext.kotlin_version = '1.3.71'
    repositories {
        // google 自家扩展依赖库
        google()
        // 第三方开源库
        jcenter()
        
    }
    dependencies {
        // gradle 插件
        classpath 'com.android.tools.build:gradle:3.6.2'
        // kotlin 插件
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
```

app 目录下

```groovy
// com.android.application  应用程序模块 可以直接运行
// com.android.library  库模块
apply plugin: 'com.android.application'
// 使用 kotlin 进行开发
apply plugin: 'kotlin-android'
// kotlin 的扩展功能
apply plugin: 'kotlin-android-extensions'

android {
    compileSdkVersion 29
    buildToolsVersion "29.0.2"

    defaultConfig {
        applicationId "com.homurax.helloworld"
        // 最低兼容版本
        minSdkVersion 21
        // 充分测试版本
        targetSdkVersion 29
        // 项目的版本号
        versionCode 1
        // 项目的版本名
        versionName "1.0"
        // 启用 JUnit 测试
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        // debug 闭包：生成测试版本安装文件的配置
        // release 闭包：生成正式版本安装文件的配置
        release {
            // 是否进行混淆
            minifyEnabled false
            // 混淆时使用规则
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }

}

// 本地依赖：对本地 jar 包或目录添加依赖关系
// 库依赖：对项目中的库模块添加依赖关系 implementation project(':helper')
// 远程依赖：对 jcenter 仓库上的开源项目添加依赖关系
dependencies {
    // 本地依赖声明
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    // 远程依赖声明 域名:工程名:版本号
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
    implementation 'androidx.appcompat:appcompat:1.1.0'
    implementation 'androidx.core:core-ktx:1.2.0'
    implementation 'androidx.constraintlayout:constraintlayout:1.1.3'
    // 声明测试用例库
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'androidx.test.ext:junit:1.1.1'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.2.0'
}
```
