package com.example.circlecut
import SessionManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.util.Objects

class ExpenseActivity : AppCompatActivity() {
    lateinit var tablayout:TabLayout;
    lateinit var viewPager2: ViewPager2;
    lateinit var ViewpagerAdapter:ViewpagerAdapter;
    val apiManager = ApiManager()
    lateinit var sessionManager:SessionManager
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
         sessionManager = SessionManager(this)
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
        GlobalScope.launch(Dispatchers.IO) {
            println(getwallet())
            val walladdr = getAddressFromResponse(getwallet())
            println(walladdr)
            val usr: ImageView = findViewById(R.id.wallet)
            usr.tooltipText=walladdr
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
    suspend fun getwallet():String{
        if (sessionManager.checkLogin()) {
            val userDetails = sessionManager.getUserDetails()
            val userId= userDetails[SessionManager.KEY_USER_ID].toString()
            val body=apiManager.getWalletDetails(userId);
            if(body!=null)
                return body;
        }
        return ""
    }
    fun getAddressFromResponse(jsonResponse: String): String? {
        try {
            val jsonObject = JSONObject(jsonResponse)
            val walletsArray = jsonObject.getJSONObject("data").getJSONArray("wallets")

            if (walletsArray.length() > 0) {
                val firstWallet = walletsArray.getJSONObject(0)
                return firstWallet.getString("address")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}