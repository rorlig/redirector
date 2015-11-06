package com.verizon.redirector;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.verizon.redirector.model.DummyRequest;
import com.verizon.redirector.model.DummyResult;
import com.verizon.redirector.retrofit.RedirectorService;

import java.io.IOException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity {

    TextView redirectorValue;
    Button getRedirectBtn;
    Button postRedirectBtn;
    private RedirectorService service;
    private static final String  BASE_URL = "http://localhost:9000/api/redirector";

    private static final String  BASE_URL_EMULATOR = "http://10.0.2.2:9000/api/redirector";

    private static final String  BASE_URL_GENYMOTION_EMULATOR = "http://10.0.3.2:9000";

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client;

    private final Gson gson = new Gson();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getRedirectBtn = (Button) findViewById(R.id.btn_get_response);
        postRedirectBtn = (Button) findViewById(R.id.btn_post_response);
        redirectorValue = (TextView) findViewById(R.id.redirector_value);

        //initialize the redirectorservice..


        client = new OkHttpClient();
        client.setFollowRedirects(true);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_GENYMOTION_EMULATOR)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();




        service = retrofit.create(RedirectorService.class);


        getRedirectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<DummyResult> call = service.getRedirector();
//                try {
//                    call.execute();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                call.enqueue(new Callback<DummyResult>() {
                    @Override
                    public void onResponse(Response<DummyResult> response, Retrofit retrofit) {
                        Log.d("MainActivity", response.body().getResult());
                        redirectorValue.setText(response.body().getResult());
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.d("MainActivity", "failure " + t);
                    }
                });



            }

        });

        postRedirectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<DummyResult> call = service.postRedirector(new DummyRequest("Hello"));

                call.enqueue(new Callback<DummyResult>() {
                    @Override
                    public void onResponse(Response<DummyResult> response, Retrofit retrofit) {
                        Log.d("MainActivity", response.message() + " location " + response.headers().get("Location"));

                        // this
                        String redirectLocation = response.headers().get("Location");
                        // make an http post call with this location..
                        new RedirectPostHandler().execute(BASE_URL_GENYMOTION_EMULATOR + redirectLocation);

                        //                        redirectorValue.setText(response.body().getResult());


                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.d("MainActivity", "failure " + t);
                    }
                });
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    DummyResult doPostRequest(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        com.squareup.okhttp.Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexcepted code " + response);

        Log.d("MainActivity", response.body().charStream().toString());

        DummyResult dummyResult = gson.fromJson(response.body().charStream(), DummyResult.class);

        return dummyResult;
    }


    // Async Task Class
    class RedirectPostHandler extends AsyncTask<String, String, DummyResult> {
        @Override
        protected DummyResult doInBackground(String... params) {
            try {
                return doPostRequest(params[0], "{}");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(DummyResult dummyResult) {
            super.onPostExecute(dummyResult);
            redirectorValue.setText(dummyResult.getResult());
        }
    }
}
