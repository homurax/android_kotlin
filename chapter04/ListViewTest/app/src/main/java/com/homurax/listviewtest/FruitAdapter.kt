package com.homurax.listviewtest

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class FruitAdapter(activity: Activity, val resourceId: Int, data: List<Fruit>) :
    ArrayAdapter<Fruit>(activity, resourceId, data) {

    // 用来缓存 ImageView 和 TextView
    inner class ViewHolder(val fruitImage: ImageView, val fruitName: TextView)

    // 每个子项滚动到屏幕内的时候会调用
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // false: 表示只让我们在父布局中声明的 layout 属性生效，但不会为这个 View 添加父布局
        // 因为一旦 View 有了父布局之后，它就不能再添加到 ListView 中了
        // val view = LayoutInflater.from(context).inflate(resourceId, parent, false)

        // convertView 用于将之前加载好的布局进行缓存 以便之后进行重用
        // val view: View = convertView ?: LayoutInflater.from(context).inflate(resourceId, parent, false)

        val view: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(resourceId, parent, false)
            val fruitImage: ImageView = view.findViewById(R.id.fruitImage)
            val fruitName: TextView = view.findViewById(R.id.fruitName)
            viewHolder = ViewHolder(fruitImage, fruitName)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        // val fruitImage: ImageView = view.findViewById(R.id.fruitImage)
        // val fruitName: TextView = view.findViewById(R.id.fruitName)
        val fruit = getItem(position)
        fruit?.let {
            viewHolder.fruitImage.setImageResource(it.imageId)
            viewHolder.fruitName.text = it.name
        }

        return view
    }
}