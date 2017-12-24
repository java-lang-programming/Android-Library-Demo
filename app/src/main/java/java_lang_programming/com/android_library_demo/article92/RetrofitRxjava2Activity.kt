package java_lang_programming.com.android_library_demo.article92

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java_lang_programming.com.android_library_demo.R
import kotlinx.android.synthetic.main.activity_retrofit_rxjava2.*
import kotlinx.android.synthetic.main.content_retrofit_rxjava2.*
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit

class RetrofitRxjava2Activity : AppCompatActivity(), ErrorDialogFragment.OnFragmentInteractionListener {
    
    private val retrofit = Retrofit.Builder()
            .baseUrl("http://zipcloud.ibsnet.co.jp/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrofit_rxjava2)
        setSupportActionBar(toolbar)

        btnNormalConnect.setOnClickListener(connectListener)
        btnRetryConnect.setOnClickListener(connectRetryListener)
    }

    private var connectListener = View.OnClickListener {
        connectNormal()
    }

    // 通常接続
    private fun connectNormal() {
        val service = retrofit.create(ApiService::class.java)
        service.searchZip(zipcode = zipCode.text.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Search> {
                    override fun onSubscribe(subscription: Subscription?) {
                        Log.d("RetrofitRxjava2", "onSubscribe")
                        subscription?.request(1000)
                    }

                    override fun onNext(search: Search?) {
                        Log.d("RetrofitRxjava2", "onNext")
                        search?.results?.forEach({
                            answer.text = with(StringBuilder()) {
                                append(it.address1)
                                append(it.address2)
                                append(it.address3)
                                append(it.kana1)
                                append(it.kana2)
                                append(it.kana3)
                                append(it.prefcode)
                                append(it.zipcode)
                            }
                        })
                    }

                    override fun onComplete() {
                        Log.d("RetrofitRxjava2", "onComplete")
                    }

                    override fun onError(throwable: Throwable?) {
                        Log.d("RetrofitRxjava2", "onError : " + throwable?.message)
                        throwable?.let {
                            val errorDialogFragment = ErrorDialogFragment.newInstance(it.message ?: "error")
                            errorDialogFragment.show(supportFragmentManager, "ErrorDialogFragment")
                        }
                    }
                })
    }

//    // これはpolling
//    // http://nagochi.hatenablog.jp/entry/2017/03/14/001430
//    private var connectPollingListener = View.OnClickListener {
//        val service = retrofit.create(ApiService::class.java)
//        service.searchZip(zipcode = zipCode.text.toString())
//                .retryWhen(object : io.reactivex.functions.Function<Flowable<Throwable>, Flowable<Long>> {
//                    override fun apply(t: Flowable<Throwable>): Flowable<Long> {
//                        return t.flatMap(object : io.reactivex.functions.Function<Throwable, Flowable<Long>> {
//                            override fun apply(throwable: Throwable): Flowable<Long> {
//                                Log.d("RetrofitRxjava2", "apply Throwable ")
//                                val errorDialogFragment = ErrorDialogFragment.newInstance(throwable.message ?: "")
//                                errorDialogFragment.show(supportFragmentManager, "ErrorDialogFragment")
//
////                                if (throwable) {
////
////                                }
//                                return Flowable.timer(5, TimeUnit.SECONDS)
//                            }
//                        })
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(object : Subscriber<Search> {
//                    override fun onSubscribe(subscription: Subscription?) {
//                        Log.d("RetrofitRxjava2", "onSubscribe")
//                        subscription?.request(1000)
//                    }
//
//                    override fun onNext(search: Search?) {
//                        Log.d("RetrofitRxjava2", "onNext")
//                        search?.results?.forEach({
//                            answer.text = with(StringBuilder()) {
//                                append(it.address1)
//                                append(it.address2)
//                                append(it.address3)
//                                append(it.kana1)
//                                append(it.kana2)
//                                append(it.kana3)
//                            }
//                        })
//                    }
//
//                    override fun onComplete() {
//                        Log.d("RetrofitRxjava2", "onComplete")
//                    }
//
//                    override fun onError(t: Throwable?) {
//                        Log.d("RetrofitRxjava2", "onError : " + t?.message)
//                    }
//
//                })
//    }

    // http://nagochi.hatenablog.jp/entry/2017/03/14/001430
    private var connectRetryListener = View.OnClickListener {
        val service = retrofit.create(ApiService::class.java)
        val zipcode = zipCode.text.toString()
        var retroyCount = 0
        service.searchZip(zipcode = zipCode.text.toString())
                .retryWhen(object : io.reactivex.functions.Function<Flowable<Throwable>, Flowable<Long>> {
                    override fun apply(t: Flowable<Throwable>): Flowable<Long> {
                        return t.flatMap(object : io.reactivex.functions.Function<Throwable, Flowable<Long>> {
                            override fun apply(throwable: Throwable): Flowable<Long> {
                                Log.d("RetrofitRxjava2", "apply Throwable " + retroyCount)
                                retroyCount = retroyCount + 1
                                retryCount.text = retroyCount.toString()
                                if (retroyCount > 2) {
                                    Log.d("RetrofitRxjava2", "apply retroyCount ")
                                    return Flowable.error(throwable)
                                }
//                                val errorDialogFragment = ErrorDialogFragment.newInstance(throwable.message ?: "")
//                                errorDialogFragment.show(supportFragmentManager, "ErrorDialogFragment")

//                                if (throwable) {
//
//                                }
                                //
                                return Flowable.timer(5, TimeUnit.SECONDS)
                                // return Flowable.just(Search(throwable.message, arrayListOf(), 600))
                                //return service.searchZip(zipcode)
                            }
                        })
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Search> {
                    override fun onSubscribe(subscription: Subscription?) {
                        Log.d("RetrofitRxjava2", "onSubscribe")
                        subscription?.request(1000)
                    }

                    override fun onNext(search: Search?) {
                        Log.d("RetrofitRxjava2", "onNext")
                        search?.results?.forEach({
                            answer.text = with(StringBuilder()) {
                                append(it.address1)
                                append(it.address2)
                                append(it.address3)
                                append(it.kana1)
                                append(it.kana2)
                                append(it.kana3)
                            }
                        })
                    }

                    override fun onComplete() {
                        Log.d("RetrofitRxjava2", "onComplete")
                    }

                    override fun onError(t: Throwable?) {
                        Log.d("RetrofitRxjava2", "onError : " + t?.message)
                    }

                })
    }

    override fun onClickClose() {
        val fragment = supportFragmentManager.findFragmentByTag("ErrorDialogFragment")
        if (fragment is ErrorDialogFragment) {
            Log.d("RetrofitRxjava2", "onClickClose fragment.dismiss() : ")
            fragment.dismiss()
        }

        //if (!disposableConnectNormal.isDisposed) "disposableConnectNormal.dispose " + disposableConnectNormal.dispose()
    }

    override fun onClickRetry() {
        val fragment = supportFragmentManager.findFragmentByTag("ErrorDialogFragment")
        if (fragment is ErrorDialogFragment) {
            Log.d("RetrofitRxjava2", "onClickRetry fragment.dismiss() : ")
            fragment.dismiss()
        }
        connectNormal()
    }
}
