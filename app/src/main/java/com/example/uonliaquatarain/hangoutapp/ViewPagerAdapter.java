package com.example.uonliaquatarain.hangoutapp;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> list_fragment = new ArrayList<>();
    private final List<String> list_title = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return list_fragment.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return list_title.get(position);
    }

    @Override
    public int getCount() {
        return list_fragment.size();
    }

    public void AddFragment(Fragment fragment, String title){
        list_fragment.add(fragment);
        list_title.add(title);
    }

}
