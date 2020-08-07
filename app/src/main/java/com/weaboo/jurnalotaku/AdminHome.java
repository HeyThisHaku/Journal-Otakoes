package com.weaboo.jurnalotaku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import java.util.Stack;
import java.util.Vector;

import adapter.FragmentAdapter;

public class AdminHome extends AppCompatActivity {
    private FragmentAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        adapter = new FragmentAdapter(getSupportFragmentManager(),new Vector<Fragment>(),new Stack<String>());
        ViewPager vp = findViewById(R.id.fragmentLeader);
        setAdapter(vp);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(vp);
    }
    private void setAdapter(ViewPager vp){
        FragmentAdapter currentAdapter;
        Vector<String> listTitle = new Vector<>();
        listTitle.add("Manage Post");
        listTitle.add("Manage OtherPost");
        listTitle.add("Manage Journal");

        Vector<Fragment> listFragment = new Vector<>();
        listFragment.add(new FragmentManagePost());
        listFragment.add(new FragmentManageOther());
        listFragment.add(new FragmentManageJournal());
        currentAdapter = new FragmentAdapter(getSupportFragmentManager(),listFragment,listTitle);
        vp.setAdapter(currentAdapter);
    }
}
