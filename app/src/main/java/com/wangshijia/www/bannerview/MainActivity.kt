package com.wangshijia.www.bannerview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.wangshijia.www.bannerview.banner.BannerLayoutManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rv = findViewById<RecyclerView>(R.id.recyclerView)
        rv.layoutManager = BannerLayoutManager()
//        rv.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        val testAdapter= TestAdapter()
        rv.adapter = testAdapter

        testAdapter.setNewData(listOf("one","two","three","four","five","six","seven","eight","night","ten"))

    }

    inner class TestAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item) {
        override fun convert(helper: BaseViewHolder?, item: String?) {
            helper?.setText(R.id.txt,item)
        }

    }
}
