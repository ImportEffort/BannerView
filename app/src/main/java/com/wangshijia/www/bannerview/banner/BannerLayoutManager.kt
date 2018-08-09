package com.wangshijia.www.bannerview.banner

import android.support.v7.widget.RecyclerView
import android.util.Log

class BannerLayoutManager : RecyclerView.LayoutManager() {

    private val TAG = BannerLayoutManager::class.java.simpleName
    private var offsetX = 0 //水平偏移
    private var realScrollX: Int = 0
    private var totalWith = 0

    private var mLeftX = 0 //卡片左端点的位置

    init {
    }

    /**
     * 返回 RecyclerView layout Params
     */
    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }


    /**
     * 1 在RecyclerView初始化时，会被调用两次。
     * 2 在调用adapter.notifyDataSetChanged()时，会被调用。
     * 3 在调用setAdapter替换Adapter时,会被调用。
     * 4 在RecyclerView执行动画时，它也会被调用。
     */
    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        if (itemCount == 0) {
            removeAndRecycleAllViews(recycler)
            return
        }
        //在 preLayout 阶段不做任何操作
        if (childCount == 0 && state.isPreLayout) {
            return
        }

        //临时移除所有子 View 并 detach 掉
        detachAndScrapAttachedViews(recycler)

        fillData(recycler, state, 0)
    }

    private fun fillData(recycler: RecyclerView.Recycler, state: RecyclerView.State, dy: Int) {
        var lastViewWidth = 0
        for (i in 0 until itemCount) {
            val child = recycler.getViewForPosition(i)
            addView(child)
            measureChildWithMargins(child, 0, 0)

            val width = getDecoratedMeasuredWidth(child)
            val height = getDecoratedMeasuredHeight(child)
            layoutDecoratedWithMargins(child, mLeftX, 0, mLeftX + width, height)
            mLeftX += width
            if (i == itemCount - 1)
                lastViewWidth = width
        }
        //TODO 与屏幕宽度比较
        totalWith = mLeftX - lastViewWidth

        Log.d(TAG, "totalWidth   $totalWith")
    }

    override fun canScrollHorizontally(): Boolean {
        return true
    }


    /**
     * dx 左滑为正，dy 右滑为负
     * 这里要处理左右边界
     */
    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        if (itemCount == 0 || dx == 0) {
            return 0
        }

        when {
            // 左边界
            dx < 0 -> {
                val wantToScrollX = offsetX + dx
                //要滑动的比能滑动的少 那么就滑动吧
                realScrollX = when {
                    wantToScrollX >= 0 -> dx
                    //要滑动的比能滑动的多，那么有两种情况，情况一 已经位于最左边了 情况二 下一次滑动到最左边
                    wantToScrollX < 0 && offsetX > 0 -> -offsetX
                    else -> 0
                }
            }
            //右边界
            else -> {
                val wantToScrollX = offsetX + dx - totalWith
                realScrollX = when {
                    wantToScrollX <= 0 -> dx
                    //如果要滑动的距离加上已经滑动距离大于总长度， 此时需要计算超出的偏移量
                    //但是只需要在，已经滑动距离小于总长度的时候滑动
                    wantToScrollX > 0 && offsetX < totalWith -> totalWith - offsetX
                    else -> 0
                }
            }
        }
        offsetX += realScrollX
        // 改方法参数 dx > 0  右偏移  与  scrollHorizontallyBy 中 的 dx 正好相反
        // 不考虑边界的时候 传入 -dx 就能跟随滑动
        offsetChildrenHorizontal(-realScrollX)
        Log.e(TAG, "缓存数量 View 数量  $childCount")
        return realScrollX
    }
}