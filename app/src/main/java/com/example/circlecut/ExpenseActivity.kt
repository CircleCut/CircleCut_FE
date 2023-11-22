package com.example.circlecut
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TableLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

class ExpenseActivity : AppCompatActivity() {
    lateinit var tablayout:TabLayout;
    lateinit var viewPager2: ViewPager2;
    lateinit var ViewpagerAdapter:ViewpagerAdapter;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        val floatbutton : FloatingActionButton = findViewById(R.id.floatingActionButton2)
        tablayout=findViewById(R.id.tablayout)
        ViewpagerAdapter=ViewpagerAdapter(this);
        viewPager2=findViewById(R.id.viewpager)
        viewPager2.adapter=ViewpagerAdapter;
        floatbutton.setOnClickListener{
            startActivity(Intent(this@ExpenseActivity,AddExpense::class.java))
        }
        tablayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    viewPager2.setCurrentItem(tab.position)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        viewPager2.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            @Override
            public override fun onPageSelected(position:Int) {
                super.onPageSelected(position);
                tablayout.getTabAt(position)?.select()
            }
        } )
    }
}