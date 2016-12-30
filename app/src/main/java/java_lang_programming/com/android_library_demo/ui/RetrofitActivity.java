package java_lang_programming.com.android_library_demo.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

import java_lang_programming.com.android_library_demo.R;
import java_lang_programming.com.android_library_demo.api.LivedoorWeatherWebService;
import java_lang_programming.com.android_library_demo.api.model.Weather;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitActivity extends AppCompatActivity {

    public static final String TAG = "RetrofitActivity";
    public static final String API_URL = "http://weather.livedoor.com/forecast/webservice/json/";
    private Handler handler = new Handler();
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        result = (TextView) findViewById(R.id.result);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            Thread thread = new Thread(new Runnable() {

                //Log.d(MainActivity.TAG, "channel :" + channel.toString());
                //Log.d(TAG, "channel :" + channel.toString());
                //} catch (IOException e) {
                //Log.d(TAG, "error :" + e.getMessage());
                //e.printStackTrace();
                //}

                @Override
                public void run() {
                    Log.d(TAG, "MainActivity");

//                    OkHttpClient client = new OkHttpClient.Builder()
//                            .addInterceptor(new LoggingInterceptor())
//                            .build();
//
//                    Retrofit retrofit = new Retrofit.Builder()
//                            .baseUrl(API_URL)
//                            .addConverterFactory(GsonConverterFactory.create())
//                            .client(client)
//                            .build();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(API_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    LivedoorWeatherWebService service = retrofit.create(LivedoorWeatherWebService.class);
                    Call<Weather> call = service.webservice("400040");

                    Weather weather = null;
                    try {
                        // Response<Weather> response =  call.execute();
                        // response.
                        weather = call.execute().body();
                        if (weather != null) {
                            Log.d(TAG, "weather is not null");
                        } else {
                            Log.d(TAG, "weather is null");
                        }
                        //Log.d(TAG, "channel :" + channel.toString());
                    } catch (IOException e) {
                        Log.d(TAG, "weather :" + e.getMessage());
                        e.printStackTrace();
                    }

                    final Weather temp_weather = weather;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (temp_weather != null) {
                                result.setText(String.valueOf(temp_weather.pinpointLocations.size()));
                            }
                        }
                    });
                }
            });

            thread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
