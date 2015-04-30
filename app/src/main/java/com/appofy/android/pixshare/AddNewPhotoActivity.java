package com.appofy.android.pixshare;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.appofy.android.pixshare.util.Constants;
import com.appofy.android.pixshare.util.SessionManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;


public class AddNewPhotoActivity extends ActionBarActivity {
    private static int RESULT_LOAD_IMG = 1;
    private Button mChoosePhotosBtn;
    String url = Constants.initialURL;
    String sectionurl = "/photo";
    String suburl = "/album/photo";
    String desturl = null;
    int albumId;
    int userId;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_photo);
        session = new SessionManager(getApplicationContext());

        albumId = getIntent().getIntExtra("albumId",0);

        if(albumId != 0) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMG);
        } else {
            Toast.makeText(getApplicationContext(), "Album id is 0", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_LOAD_IMG) {
                if(data.getData()!=null) {
                    // Single image
                    final Uri selectedImage = data.getData();
                    System.out.println("Selected Image "+selectedImage);


                    desturl = url + sectionurl + suburl;

                    userId = Integer.parseInt(session.getUserDetails().get("userId"));
                    System.out.println("In AlbumsFragment UserID:"+userId);
                    // Make RESTful webservice call using AsyncHttpClient object

                    AsyncHttpClient client = new AsyncHttpClient();

                    RequestParams chkParams = new RequestParams();
                    chkParams.put("userId", userId);
                    chkParams.put("caption", "");
                    chkParams.put("albumId",albumId);
                    chkParams.put("latitude", 0);
                    chkParams.put("longitude", 0);
                    try {
                        String path = getRealPathFromURI(getApplicationContext(),selectedImage);
                        System.out.println(path);
                        chkParams.put("file",new File(path));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    client.post(desturl, chkParams, new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                            try {
                                JSONObject photoJSON = new JSONObject(new String(response));
                                if (photoJSON.getString("responseFlag").equals("success")) {
                                    Intent i = new Intent(getApplicationContext(), AlbumGridActivity.class);
                                    i.putExtra("albumId",albumId);
                                    startActivity(i);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Something went wrong, please contact Admin", Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Error Occurred!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                            // When Http response code is '404'
                            if (statusCode == 404) {
                                Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                            }
                            // When Http response code is '500'
                            else if (statusCode == 500) {
                                Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                            }
                            // When Http response code other than 404, 500
                            else {
                                Toast.makeText(getApplicationContext(), "Unexpected Error occurred, Check Internet Connection!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });



                    /*String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();*//*
                *//*ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));*/
                }
                else if(data.getClipData()!=null){
                    // Multiple images
                    final ClipData clip = data.getClipData();
                    desturl = url + sectionurl + suburl;
                    for (int i = 0; i < clip.getItemCount(); i++) {
                        ClipData.Item item = clip.getItemAt(i);
                        Uri uri = item.getUri();
                        System.out.println("In Selected Images Loop:"+uri);
                        userId = Integer.parseInt(session.getUserDetails().get("userId"));
                        System.out.println("In AlbumsFragment UserID:"+userId);
                        // Make RESTful webservice call using AsyncHttpClient object

                        AsyncHttpClient client = new AsyncHttpClient();

                        RequestParams chkParams = new RequestParams();
                        chkParams.put("userId", userId);
                        chkParams.put("caption", "");
                        chkParams.put("albumId",albumId);
                        chkParams.put("latitude", 0);
                        chkParams.put("longitude", 0);
                        try {
                            String path = getRealPathFromURI(getApplicationContext(),uri);
                            System.out.println(path);
                            chkParams.put("file",new File(path));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        client.post(desturl, chkParams, new AsyncHttpResponseHandler() {

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                                try {
                                    JSONObject photoJSON = new JSONObject(new String(response));
                                    if (photoJSON.getString("responseFlag").equals("success")) {

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Something went wrong, please contact Admin", Toast.LENGTH_LONG).show();
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), "Error Occurred!", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                                // When Http response code is '404'
                                if (statusCode == 404) {
                                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                                }
                                // When Http response code is '500'
                                else if (statusCode == 500) {
                                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                                }
                                // When Http response code other than 404, 500
                                else {
                                    Toast.makeText(getApplicationContext(), "Unexpected Error occurred, Check Internet Connection!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        // Process the uri...
                    }

                    Intent i = new Intent(getApplicationContext(), LandingActivity.class);
                    startActivity(i);

                }

                else
                {
                    Toast.makeText(this,"No Image Selected", Toast.LENGTH_LONG).show();
                }




            }
        }



    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        // Will return "image:x*"
        String wholeID = DocumentsContract.getDocumentId(contentUri);

// Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

// where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{ id }, null);

        String filePath = "";

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }

        cursor.close();
        return filePath;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_new_photo, menu);
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
