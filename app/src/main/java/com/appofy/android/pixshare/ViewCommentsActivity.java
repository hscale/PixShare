package com.appofy.android.pixshare;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appofy.android.pixshare.util.Constants;
import com.appofy.android.pixshare.util.CustomListComments;
import com.appofy.android.pixshare.util.SessionManager;
import com.facebook.login.LoginManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ViewCommentsActivity extends ActionBarActivity {
    String sectionurl = "/photo";
    String suburl = "/album/photo";
    String desturl = null;
    SessionManager session;
    ListView lv;
    Button mPostCommentBtn;
    ArrayList<String> mComments;
    TextView mLikes;
    EditText mPostComment;
    Activity mActivity;
    ArrayAdapter<String> adapter;
    Button mInputPostLikeBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_comments);
        session = new SessionManager(this);
        desturl = Constants.initialURL + sectionurl + suburl;
        mActivity = this;
        mPostComment = (EditText) findViewById(R.id.inputPostComment);
        mPostCommentBtn = (Button) findViewById(R.id.inputPostCommentBtn);
        mLikes = (TextView) findViewById(R.id.likes);
        lv = (ListView) findViewById(R.id.comments_list_view);
        mInputPostLikeBtn= (Button) findViewById(R.id.inputPostLikeBtn);
        mPostCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams chkParams = new RequestParams();
                chkParams.put("photoId", getIntent().getIntExtra("photoId",0));
                chkParams.put("userId",session.getUserDetails().get("userId"));
                chkParams.put("comment",mPostComment.getText());
                client.post(Constants.initialURL + sectionurl + "/album/photo/comment", chkParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        try {

                            JSONObject photoInfoJSON = new JSONObject(new String(response));
                            if (photoInfoJSON.getString("responseFlag").equals("success")) {

                                Intent i = new Intent(getApplicationContext(), ViewCommentsActivity.class);
                                i.putExtra("photoId",getIntent().getIntExtra("photoId",0));
                                startActivity(i);

                            } else {
                                Toast.makeText(mActivity, "Something went wrong, please contact Admin", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(mActivity, "Error Occurred!", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                        // When Http response code is '404'
                        if (statusCode == 404) {
                            Toast.makeText(mActivity, "Requested resource not found", Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(mActivity, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(mActivity, "Unexpected Error occurred, Check Internet Connection!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }


          });

        mInputPostLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams chkParams = new RequestParams();
                chkParams.put("photoId", getIntent().getIntExtra("photoId",0));
                chkParams.put("userId",session.getUserDetails().get("userId"));
                client.post(Constants.initialURL + sectionurl + "/album/photo/like", chkParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        try {

                            JSONObject photoInfoJSON = new JSONObject(new String(response));
                            if (photoInfoJSON.getString("responseFlag").equals("success")) {

                                Intent i = new Intent(getApplicationContext(), ViewCommentsActivity.class);
                                i.putExtra("photoId",getIntent().getIntExtra("photoId",0));
                                startActivity(i);

                            } else {
                                Toast.makeText(mActivity, "Something went wrong, please contact Admin", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(mActivity, "Error Occurred!", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                        // When Http response code is '404'
                        if (statusCode == 404) {
                            Toast.makeText(mActivity, "Requested resource not found", Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(mActivity, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(mActivity, "Unexpected Error occurred, Check Internet Connection!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }


        });

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams chkParams = new RequestParams();
        chkParams.put("photoId", getIntent().getIntExtra("photoId",0));
        client.get(desturl, chkParams, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {

                    JSONObject photoInfoJSON = new JSONObject(new String(response));
                    if (photoInfoJSON.getString("responseFlag").equals("success")) {
                        System.out.println(photoInfoJSON);
                        JSONObject photoObject = photoInfoJSON.getJSONObject("photo");
                        JSONArray commentsArray = photoObject.getJSONArray("comments");
                        mComments = new ArrayList<String>();
                        if(commentsArray.length()!=0) {
                            int count = commentsArray.length();
                            for (int i = 0; i < count; i++) {
                                JSONObject commentObj = commentsArray.getJSONObject(i);
                                mComments.add(commentObj.getString("userName") + ":" + commentObj.getString("comment"));

                            }
                            System.out.println(mComments.toString());
                            adapter = new CustomListComments(mActivity, mComments);
                            lv.setAdapter(adapter);
                        }

                        //show likes
                        JSONArray likesArray = photoObject.getJSONArray("likes");


                        String likes="Likes: <" + likesArray.length()+">";
                        if(likesArray.length()!=0) {
                            int count = likesArray.length();
                            for (int i = 0; i < count; i++) {
                                JSONObject likeObj = likesArray.getJSONObject(i);
                                likes+=likeObj.getString("userName")+", ";
                                if(likeObj.getInt("userId")== Integer.parseInt(session.getUserDetails().get("userId")))
                                {
                                    mInputPostLikeBtn.setEnabled(false);
                                }
                            }
                        }
                        mLikes.setText(likes);
                    } else {
                        Toast.makeText(mActivity, "Something went wrong, please contact Admin", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(mActivity, "Error Occurred!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(mActivity, "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(mActivity, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(mActivity, "Unexpected Error occurred, Check Internet Connection!", Toast.LENGTH_LONG).show();
                }
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_comments, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
