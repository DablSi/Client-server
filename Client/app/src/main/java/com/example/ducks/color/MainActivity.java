package com.example.ducks.color;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    LinearLayout linearLayout;
    boolean isClicked = true, isTrue = true;
    public static String URL = "http://192.168.1.8:8080/";
    Button get, send;
    TextView list;
    private String android_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        send = findViewById(R.id.send);
        get = findViewById(R.id.get);
        list = findViewById(R.id.list);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SendAsyncTask().execute();

            }
        });
        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetAsyncTask().execute();
            }
        });
    }

    class SendAsyncTask extends AsyncTask <Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            Service service = retrofit.create(Service.class);
            Call<Void> call = service.put(android_id, 0, System.currentTimeMillis() + (long)10000);
            try {
                call.execute();
                Log.d("SEND_AND_RETURN", "Ready.");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    class GetAsyncTask extends AsyncTask {
        Long time = null;

        @Override
        protected Object doInBackground(Object[] objects) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            Service service = retrofit.create(Service.class);
            while (time == null) {
                Call<Long> call = service.get(android_id);
                try {
                    Response<Long> userResponse = call.execute();
                    time = userResponse.body();
                    Log.d("SEND_AND_RETURN", "" + (time - System.currentTimeMillis()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(time - System.currentTimeMillis());
                new NewThread().execute();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            list.setText(time.toString());
        }
    }

    class NewThread extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            linearLayout = findViewById(R.id.ll);
            while (isTrue) {
                if (!isClicked) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            linearLayout.setBackgroundColor(0xff303f9f);
                        }
                    });
                    isClicked = true;
                } else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            linearLayout.setBackgroundColor(0xff000000);
                        }
                    });
                    isClicked = false;
                }
                //linearLayout.invalidate();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
