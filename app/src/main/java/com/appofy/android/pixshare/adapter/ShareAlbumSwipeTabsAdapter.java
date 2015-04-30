package com.appofy.android.pixshare.adapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.appofy.android.pixshare.fragments.AlbumsFragment;
import com.appofy.android.pixshare.fragments.FriendsFragment;
import com.appofy.android.pixshare.fragments.GroupsFragment;
import com.appofy.android.pixshare.fragments.ShareAlbumWithFriendsFragment;
import com.appofy.android.pixshare.fragments.ShareAlbumWithGroupsFragment;
import com.appofy.android.pixshare.fragments.SharedAlbumsFragment;

/**
 * Created by Mihir on 4/22/2015.
 */
public class ShareAlbumSwipeTabsAdapter extends FragmentPagerAdapter{

    private final int TAB_COUNT = 2;
    private String tabtitles[] = new String[] { "Share Album with Friends", "Share Album with Groups"};
    public ShareAlbumSwipeTabsAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ShareAlbumWithFriendsFragment();
            case 1:
                return new ShareAlbumWithGroupsFragment();
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles[position];
    }
}
