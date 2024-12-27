package com.hyc.hi_navigation.model

data class BottomBar(
    val selectTab:Int,
    val tabs:List<Tab>,
)
data class Tab(
    val size:Int,
    val enable:Boolean,
    val index:Int,
    val pageUrl:String,
    val title:String,
)