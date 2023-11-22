package com.example.circlecut

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewpagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
            return 3
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0->return FriendsExpense()
            1->return GroupExpense()
            2->return ActivityExpense()
            else->return FriendsExpense()
        }
    }
}