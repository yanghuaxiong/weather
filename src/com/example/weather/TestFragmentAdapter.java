package com.example.weather;

import java.util.LinkedList;
import java.util.List;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;

class TestFragmentAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
    protected List<String> months = new LinkedList<String>();
    Fragment fragment;
    
    public TestFragmentAdapter(FragmentManager fm) {
        super(fm);
        months.add("折线图显示");
        months.add("柱状图显示");
        months.add("饼图显示");
    }
    
    @Override
    public Fragment getItem(int position) {
    	
       switch(position){
          case 0:
        	  fragment = new FirstFragment();
        	  break;
          case 1:
        	  fragment = new SecondFragment();
        	  break;
          case 2:
        	  fragment = new ThridFragment();
        	  break;
       }
       return fragment;
    }

    @Override
    public int getCount() {
        return months.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return months.get(position);
    }

    @Override
    public int getIconResId(int index) {
      return 0;
    }
}