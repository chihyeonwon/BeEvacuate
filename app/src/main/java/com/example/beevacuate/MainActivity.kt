package com.example.beevacuate

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //gps 권한
        val permissionList = arrayOf<String>(
            // 위치 권한
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )

        // 권한 요청
        ActivityCompat.requestPermissions(this@MainActivity, permissionList, 1)
        ActivityCompat.requestPermissions(this@MainActivity, permissionList, 3)

        val runbtn:ImageButton= findViewById(R.id.runBtn) //국민행동요령 버튼
        val shelterbtn:ImageButton = findViewById(R.id.shelterBtn) //임시주거소 버튼
        val disasterbtn:ImageButton = findViewById(R.id.disasterBtn) //재난문자 버튼

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        val toolbar: Toolbar = findViewById(R.id.toolbar) // toolBar를 통해 App Bar 생성
        setSupportActionBar(toolbar) // 툴바 적용

        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 드로어를 꺼낼 홈 버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(R.drawable.navi_menu) // 홈버튼 이미지 변경
        supportActionBar?.setDisplayShowTitleEnabled(false) // 툴바에 타이틀 안보이게

        // 네비게이션 드로어 생성
        drawerLayout = findViewById(R.id.drawer_layout)

        // 네비게이션 드로어 내에있는 화면의 이벤트를 처리하기 위해 생성
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this) //navigation 리스너

        runbtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, ActionActivity::class.java))
        }

        disasterbtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, DisasterTextActivity::class.java))
        }

        shelterbtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, ShelterCheckActivity::class.java))
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)

        return true
    }

    // 툴바 메뉴 버튼이 클릭 됐을 때 실행하는 함수
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // 클릭한 툴바 메뉴 아이템 id 마다 다르게 실행하도록 설정
        when(item!!.itemId){
            android.R.id.home->{
                // 햄버거 버튼 클릭시 네비게이션 드로어 열기
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_action-> {
                Toast.makeText(this,"국민행동요령 실행",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@MainActivity, ActionActivity::class.java))
            }
            R.id.menu_message-> {
                Toast.makeText(this,"재난문자 실행",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@MainActivity, DisasterTextActivity::class.java))
            }
            R.id.menu_search-> {
                Toast.makeText(this,"대피소조회 실행",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@MainActivity, ShelterCheckActivity::class.java))
            }
            R.id.menu_search-> {
                Toast.makeText(this,"신고하기 실행",Toast.LENGTH_SHORT).show()
                // 신고하기 기능 추가
            }
        }
        drawerLayout.closeDrawers() // 네비게이션 드로어를 닫음
        return true
    }

    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers()
            // 테스트를 위해 뒤로가기 버튼시 Toast 메시지
            Toast.makeText(this,"back btn clicked",Toast.LENGTH_SHORT).show()
        } else{
            super.onBackPressed()
        }
    }
}