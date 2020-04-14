package com.homurax.recyclerviewtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.homurax.listviewtest.Fruit
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val fruitList = ArrayList<Fruit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFruits()
        // 用于指定 RecyclerView 的布局方式
        // val layoutManager = LinearLayoutManager(this)
        // layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        // 布局分为 3 列，纵向排列
        val layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager
        val adapter = FruitAdapter(fruitList)
        recyclerView.adapter = adapter
    }

    private fun initFruits() {
        repeat(2) {
            fruitList.add(Fruit(getRandomLengthName("Apple"), R.drawable.apple_pic))
            fruitList.add(Fruit(getRandomLengthName("Banana"), R.drawable.banana_pic))
            fruitList.add(Fruit(getRandomLengthName("Orange"), R.drawable.orange_pic))
            fruitList.add(Fruit(getRandomLengthName("Watermelon"), R.drawable.watermelon_pic))
            fruitList.add(Fruit(getRandomLengthName("Pear"), R.drawable.pear_pic))
            fruitList.add(Fruit(getRandomLengthName("Grape"), R.drawable.grape_pic))
            fruitList.add(Fruit(getRandomLengthName("Pineapple"), R.drawable.pineapple_pic))
            fruitList.add(Fruit(getRandomLengthName("Strawberry"), R.drawable.strawberry_pic))
            fruitList.add(Fruit(getRandomLengthName("Cherry"), R.drawable.cherry_pic))
            fruitList.add(Fruit(getRandomLengthName("Mango"), R.drawable.mango_pic))
        }
    }

    private fun getRandomLengthName(str: String): String {
        val n = (1..20).random()
        val builder = StringBuilder()
        repeat(n) {
            builder.append(str)
        }
        return builder.toString()
    }
}
