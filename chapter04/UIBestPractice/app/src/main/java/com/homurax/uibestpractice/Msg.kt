package com.homurax.uibestpractice

class Msg(val content: String, val type: Int) {
    companion object {
        // 使用 const 定义常量
        // 只有在单例类、companion object 或顶层方法中才可以使用 const 关键字
        const val TYPE_RECEIVED = 0
        const val TYPE_SENT = 1
    }
}