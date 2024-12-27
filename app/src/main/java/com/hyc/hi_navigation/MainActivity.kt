package com.hyc.hi_navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hyc.hi_navigation.utlis.NavUtlis

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView = findViewById<BottomNavigationView>(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        val hostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)

        //进行导航图关联
        NavUtlis.builderNavGraph(this,hostFragment?.childFragmentManager!!, navController,R.id.nav_host_fragment)
        //进行BottomNavigationView 关联
        NavUtlis.builderBottomBar(navView)

        navView.setOnItemSelectedListener {
            navController.navigate(it.itemId)
            return@setOnItemSelectedListener true
        }
    }
}