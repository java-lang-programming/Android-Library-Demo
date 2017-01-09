package java_lang_programming.com.android_library_demo.api;

import java_lang_programming.com.android_library_demo.api.model.Weather;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * LivedoorWeatherWebService
 * // http://weather.livedoor.com/forecast/webservice/json/v1?city=400040
 */
public interface LivedoorWeatherWebService {
    @GET("v1")
    Call<Weather> webservice(@Query("city") String city);
    //@GET("v1")
    //LivedoorWeatherWebServiceCall<Weather> webservice(@Query("city") String city);
}
