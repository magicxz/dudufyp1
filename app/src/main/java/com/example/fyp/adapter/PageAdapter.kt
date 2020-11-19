package com.example.fyp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.fyp.fragment.CommunityFragment
import com.example.fyp.fragment.DeliveryFragment
import com.example.fyp.fragment.RestaurantFragment

class PageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> {return DeliveryFragment()
            }
            1 -> {return RestaurantFragment()
            }
            2 -> {return CommunityFragment()
            }
            else -> {return DeliveryFragment()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position){
            0 -> (return "Delivery")
            1 -> (return "Restaurants")
            2 -> (return "Community")
        }
        return super.getPageTitle(position)
    }

    override fun getCount(): Int {
        return 3
    }
}