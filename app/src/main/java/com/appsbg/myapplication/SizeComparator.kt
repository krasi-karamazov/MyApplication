package com.appsbg.myapplication

import android.util.Size

internal class SizeComparator: Comparator<Size> {

    override fun compare(o1: Size, o2: Size): Int {
        return (o1.width.toLong() * o1.height - o2.width.toLong() * o2.height).toInt()
    }
}