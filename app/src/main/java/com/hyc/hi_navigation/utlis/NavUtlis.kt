package com.hyc.hi_navigation.utlis

import android.content.ComponentName
import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphNavigator
import androidx.navigation.fragment.DialogFragmentNavigator
import androidx.navigation.fragment.FragmentNavigator
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hyc.hi_navigation.model.BottomBar
import com.hyc.hi_navigation.model.Destination
import java.io.BufferedReader
import java.io.InputStreamReader


object NavUtlis {
    var destinations = mutableMapOf<String,Destination>()
    public fun parseFile(context: Context,fileName:String): String? {
        val assets = context.assets
        try {
            val inputStream = assets.open(fileName)
            val bytes = BufferedReader(InputStreamReader(inputStream))
            val json = bytes.readText()
            inputStream.close()
            bytes.close()
            return json
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    public fun builderNavGraph(activity:FragmentActivity,controller:NavController, containerId:Int){
        val content = parseFile(activity, "destination.json")
        destinations = Gson().fromJson<HashMap<String, Destination>>(content,
            object : TypeToken<HashMap<String, Destination>>() {}.type)
        val provider = controller.navigatorProvider
        val graphNavigator = provider.getNavigator(NavGraphNavigator::class.java)
        val navGraph = NavGraph(graphNavigator)
        destinations.forEach {
            val destination = it.value
            when (destination.destType) {
                "activity" -> {
                    val navigator = provider.getNavigator(ActivityNavigator::class.java)
                    val node = navigator.createDestination()
                    node.id = destination.id
                    node.setComponentName(ComponentName(activity.packageName,destination.clazName))
                    navGraph.addDestination(node)
                }
                "fragment" -> {
                    val navigator = provider.getNavigator(FragmentNavigator::class.java)
                    val node = navigator.createDestination()
                    node.id = destination.id
                    node.setClassName(destination.clazName)
                    navGraph.addDestination(node)
                }
                "dialog" -> {
                    val navigator = provider.getNavigator(DialogFragmentNavigator::class.java)
                    val node = navigator.createDestination()
                    node.id = destination.id
                    node.setClassName(destination.clazName)
                    navGraph.addDestination(node)
                }
            }

            if (destination.asStarter){
                navGraph.setStartDestination(destination.id)
            }
        }
        controller.setGraph(navGraph,null)
    }

    fun builderBottomBar(navView:BottomNavigationView){
        val content = parseFile(navView.context,"main_tabs.json")
        val bottomBar = Gson().fromJson<BottomBar>(content, BottomBar::class.java)
        val menu = navView.menu
        bottomBar.tabs.forEach {tab->
            if (tab.enable && destinations.containsKey(tab.pageUrl)){
                val destination = destinations[tab.pageUrl]
                destination?.let {
                    val menuItem = menu.add(0, it.id, tab.index, tab.title)
                    menuItem.setIcon(androidx.core.R.drawable.ic_call_answer)
                }
            }
        }
    }
}