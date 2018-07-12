package helloworld.demo.com.uploadfiles;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 2;
    private int PICK_IMAGE_REQUEST = 1;

    String filePath,strnam,strimg;
    EditText editText;
    ImageView imageView;
    Button button,sendbutton,retrievebutton;

    String PICTURE_BASE_URL = "http://www.godigitell.in/dev/hrportal/uploads/profile_picture/";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);
        sendbutton = (Button) findViewById(R.id.send);
        retrievebutton = (Button)findViewById(R.id.button2);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionCheck();
            }
        });

        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               sendNetworkRequest();
            }
        });

        retrievebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                retrieveRequest();
            }
        });



    }


    private void permissionCheck() {

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_MEDIA);
        } else {
            readDataExternal();
        }
    }

    private void readDataExternal() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_IMAGE_REQUEST);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_MEDIA:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    readDataExternal();
                }
                break;

            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri picUri = data.getData();

            filePath = RealPathUtil.getRealPathFromURI_API19(getApplicationContext(), picUri);

            imageView.setImageURI(picUri);

        }


    }


    private void sendNetworkRequest() {

        String id = "34457495";
        String s1 = editText.getText().toString().trim();


        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://www.godigitell.in/dev/hrportal/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        UserClient client = retrofit.create(UserClient.class);


        File file = new File(filePath);

        RequestBody idrequest = RequestBody.create(MediaType.parse("text/plain"),id);
        RequestBody namerequest = RequestBody.create(MediaType.parse("text/plain"),s1);
        MultipartBody.Part filepart = MultipartBody.Part.createFormData("photo",file.getName(), RequestBody.create(MediaType.parse("image/*"),file));
        Call<User> call = client.createAccount(idrequest,namerequest,filepart);


        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Toast.makeText(MainActivity.this,"Success",Toast.LENGTH_SHORT).show();
                Log.d("RESPONSE",response.toString());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(MainActivity.this,"Not Success",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void retrieveRequest() {

        String id = "34457495";

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://www.godigitell.in/dev/hrportal/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        GetUserClient client = retrofit.create(GetUserClient.class);

        RequestBody idrequest = RequestBody.create(MediaType.parse("text/plain"),id);

        Call<Results> call = client.retrieveAccount(idrequest);

        call.enqueue(new Callback<Results>() {
            @Override
            public void onResponse(Call<Results> call, Response<Results> response){

                Toast.makeText(MainActivity.this, "Retrieve Success", Toast.LENGTH_SHORT).show();

                String resultJSONResponse = new Gson().toJson(response.body());
               // Log.e("TAG", "response 33: "+new Gson().toJson(response.body()) );
                try {
                    JSONObject jsonObject = new JSONObject(resultJSONResponse);

                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject o = jsonArray.getJSONObject(i);

                                strnam = o.getString("name");
                                strimg = o.getString("photo");

                                showMethod();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<Results> call, Throwable t) {

                Toast.makeText(MainActivity.this,"Retrieve Not Success",Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void showMethod() {

        editText.setText(strnam);

        String Picture = PICTURE_BASE_URL + strimg;

        loadImageFromInternetUrl(Picture);

    }

    private void loadImageFromInternetUrl(String picture) {

        Glide

                .with(MainActivity.this)
                .load(picture)
                .into(imageView);


    }

}
