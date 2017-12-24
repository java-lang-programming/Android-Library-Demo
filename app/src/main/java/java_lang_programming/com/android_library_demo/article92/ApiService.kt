package java_lang_programming.com.android_library_demo.article92

import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query

/*
 * Created by msuzuki on 2017/12/22.
 */
interface ApiService {
    @GET("searc")
    fun searchZip(@Query("zipcode") zipcode: String): Flowable<Search>
}