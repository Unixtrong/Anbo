package com.unixtrong.anbo.compo.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.unixtrong.anbo.compo.discover.DiscoverFragment;
import com.unixtrong.anbo.compo.home.HomeFragment;
import com.unixtrong.anbo.compo.message.MessageFragment;

/**
 * Author(s): danyun
 * Date: 2017/9/3
 */
class MainPagerAdapter extends FragmentStatePagerAdapter {
    private HomeFragment mHomeFragment;
    private MessageFragment mMessageFragment;
    private DiscoverFragment mDiscoverFragment;

    MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 2:
                if (mDiscoverFragment == null) {
                    mDiscoverFragment = DiscoverFragment.newInstance("");
                }
                return mDiscoverFragment;
            case 1:
                if (mMessageFragment == null) {
                    mMessageFragment = MessageFragment.newInstance("");
                }
                return mMessageFragment;
            case 0:
            default:
                if (mHomeFragment == null) {
                    mHomeFragment = HomeFragment.newInstance("");
                }
                return mHomeFragment;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

}
