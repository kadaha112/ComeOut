import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // 카카오 SDK 초기화작업
        KakaoSdk.init(this,"934e09db2936ee2fa2e31476ae76aa52")
    }
}