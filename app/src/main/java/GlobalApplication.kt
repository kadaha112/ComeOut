import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // 카카오 SDK 초기화작업
        KakaoSdk.init(this,"87e49b817a3a721b533c58c263db621c")
    }
}