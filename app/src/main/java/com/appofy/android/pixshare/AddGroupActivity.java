package com.appofy.android.pixshare;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class AddGroupActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_friend:
                Intent addFriendIntent = new Intent(getApplicationContext(), AddFriendActivity.class);
                startActivity(addFriendIntent);
                return true;
            case R.id.invite_friends:
                Intent inviteFriendsIntent = new Intent(getApplicationContext(), InviteFriendsActivity.class);
                startActivity(inviteFriendsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
