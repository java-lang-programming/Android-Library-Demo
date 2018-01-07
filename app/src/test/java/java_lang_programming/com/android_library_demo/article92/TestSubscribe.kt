package java_lang_programming.com.android_library_demo.article92

import io.reactivex.Flowable
import org.junit.Test

/**
 * Created by msuzuki on 2017/12/26.
 */
class TestSubscribe() {

    @Test
    fun testconnectNormalSubscriber() {
        // https://qiita.com/kubode/items/aebef4593e42a3b367be
//        val subscriber = TestSubscriber().create()
//        TestSubscriber
        val sample = Search("hello", arrayListOf(), 200)

        val test = Flowable.just(sample)
                .test()

        // http://hydrakecat.hatenablog.jp/entry/2016/12/14/RxJava_%E3%81%AE%E3%83%86%E3%82%B9%E3%83%88%282%29%3A_RxJavaHooks%2C_RxAndroidPlugins
        test.awaitTerminalEvent()
        test.assertValue(sample)
        test.assertComplete()
                //.co
                //.assertValue(Search("hello", arrayListOf(), 200))
        // floawablaを返す。
    }

}
