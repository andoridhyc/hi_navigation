基于注解处理器，对于 Navigation 改造

使用方法如下：
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView = findViewById<BottomNavigationView>(R.id.navView)

        val navController = findNavController(R.id.nav_host_fragment)
        //进行导航图关联
        NavUtlis.builderNavGraph(this, navController,R.id.nav_host_fragment)
        //进行BottomNavigationView 关联
        NavUtlis.builderBottomBar(navView)

        navView.setOnItemSelectedListener {
            navController.navigate(it.itemId)
            return@setOnItemSelectedListener true
        }
    }
}
