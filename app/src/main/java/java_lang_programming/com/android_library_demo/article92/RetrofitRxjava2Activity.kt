package java_lang_programming.com.android_library_demo.article92

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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
                        subscription?.request(1000)
                    }

                    override fun onNext(search: Search?) {
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
                        //Log.d("RetrofitRxjava2", "onComplete")
                    }

                    override fun onError(throwable: Throwable?) {
                        throwable?.let {
                            val errorDialogFragment = ErrorDialogFragment.newInstance(it.message ?: "error")
                            errorDialogFragment.show(supportFragmentManager, "ErrorDialogFragment")
                        }
                    }
                })
    }

    // リトライ接続
    private var connectRetryListener = View.OnClickListener {
        val service = retrofit.create(ApiService::class.java)
        var retroyCount = 0
        service.searchZip(zipcode = zipCode.text.toString())
                .retryWhen(object : io.reactivex.functions.Function<Flowable<Throwable>, Flowable<Long>> {
                    override fun apply(t: Flowable<Throwable>): Flowable<Long> {
                        return t.flatMap(object : io.reactivex.functions.Function<Throwable, Flowable<Long>> {
                            override fun apply(throwable: Throwable): Flowable<Long> {
                                retroyCount = retroyCount + 1
                                if (retroyCount > 2 && throwable.message == "HTTP 404 Not Found") {
                                    return Flowable.error(throwable)
                                }
                                return Flowable.timer(5, TimeUnit.SECONDS)
                            }
                        })
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Search> {
                    override fun onSubscribe(subscription: Subscription?) {
                        subscription?.request(1000)
                    }

                    override fun onNext(search: Search?) {
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
                    }

                    override fun onError(t: Throwable?) {
                        t?.let {
                            val errorDialogFragment = ErrorDialogFragment.newInstance(it.message + " : retry ${retroyCount}回" ?: "error")
                            errorDialogFragment.show(supportFragmentManager, "ErrorDialogFragment")
                        }
                    }

                })
    }

    override fun onClickClose() {
        val fragment = supportFragmentManager.findFragmentByTag("ErrorDialogFragment")
        if (fragment is ErrorDialogFragment) {
            fragment.dismiss()
        }
    }

    override fun onClickRetry() {
        val fragment = supportFragmentManager.findFragmentByTag("ErrorDialogFragment")
        if (fragment is ErrorDialogFragment) {
            fragment.dismiss()
        }
        connectNormal()
    }
}
