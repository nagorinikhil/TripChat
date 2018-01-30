package com.inclass.tripchat.Activity.Fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inclass.tripchat.Activity.POJO.FriendRequest;
import com.inclass.tripchat.Activity.POJO.User;
import com.inclass.tripchat.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    ArrayList<User> userArrayList;
    ArrayList<String> friendArrayList;
    ArrayList<FriendRequest> friendReqSentArrayList, friendReqReceivedArrayList;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public FriendsFragment(ArrayList<User> userArrayList, ArrayList<String> friendArrayList, ArrayList<FriendRequest> friendReqSentArrayList, ArrayList<FriendRequest> friendReqReceivedArrayList) {
        this.userArrayList = userArrayList;
        this.friendArrayList = friendArrayList;
        this.friendReqSentArrayList = friendReqSentArrayList;
        this.friendReqReceivedArrayList = friendReqReceivedArrayList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        Log.d("Friends Fragment", "FRIENDS");



        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();

        tabLayout = (TabLayout)view.findViewById(R.id.tabs_friendsFragment);
        viewPager = (ViewPager)view.findViewById(R.id.viewpager_friendsFragment);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new MyFriendsFragment(), "My Friends");
        adapter.addFragment(new SearchPeopleFragment(), "Search People");
        adapter.addFragment(new AcceptFriendRequestFragment(), "Friend Requests");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d("FRIEND FRAGMENT", "ONPAUSE");

    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d("FRIEND FRAGMENT", "ONSTOP");
    }
}
