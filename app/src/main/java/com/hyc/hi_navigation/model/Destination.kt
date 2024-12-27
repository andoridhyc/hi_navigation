package com.hyc.hi_navigation.model

data class Destination(
    val pageUrl: String, //页面在路由中的名称
    val asStarter: Boolean, //是否是启动页
    val id : Int,           //页面在路由中的id
    val destType:String,    //页面类型
    val clazName:String,    //页面类名
)