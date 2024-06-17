package activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.daehankang.comeout.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import data.UserAccount
import network.RetrofitApiService
import network.RetrofitHelper
import retrofit2.Call
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val key = Utility.getKeyHash(this)
        Log.i("key", key)

        KakaoSdk.init(this,"87e49b817a3a721b533c58c263db621c")

        auth = Firebase.auth

        binding.signUp.setOnClickListener { startActivity(Intent(this, SignUpActivity::class.java)) }
        binding.browse.setOnClickListener { startActivity(Intent(this,MainActivity::class.java)) }
        binding.signUpLoginBtn.setOnClickListener { clickBtnLogin() }
        binding.clickGoogle.setOnClickListener { clickGoogle() }
        binding.clickKakao.setOnClickListener { clickKakao() }
        binding.clickNaver.setOnClickListener { clickNaver() }


    }

    private fun clickBtnLogin(){

            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(applicationContext, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show()
                return
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(applicationContext, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
                return
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        val errorMessage = when(task.exception){
                            is FirebaseAuthInvalidUserException -> "가입되지 않은 이메일입니다."
                            is FirebaseAuthInvalidCredentialsException -> "잘못된 비밀번호 입니다."
                            else -> "로그인 실패 : ${task.exception?.message}"

                    }
                    }
                }
        }
    private fun clickGoogle(){


        val signInOptions: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()

        val intent:Intent= GoogleSignIn.getClient(this, signInOptions).signInIntent
        resultLauncher.launch(intent)
    }

    val resultLauncher= registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

        val intent:Intent?= it.data
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(intent)

        val account: GoogleSignInAccount= task.result
        val id:String= account.id.toString()
        val email:String= account.email ?: ""

        Toast.makeText(this, "구글로그인 성공", Toast.LENGTH_SHORT).show()
        G.userAccount= UserAccount(id, email)

        startActivity(Intent(this, MainActivity::class.java))
        finish()

    }
    private fun clickKakao(){
        val callback:(OAuthToken?, Throwable?)->Unit = { token, error ->
            if(error != null) {

            }else{
                Toast.makeText(this, "카카오로그인 성공", Toast.LENGTH_SHORT).show()

                UserApiClient.instance.me { user, error ->
                    if(user!=null){
                        val id:String= user.id.toString()
                        val nickname:String = user.kakaoAccount?.profile?.nickname ?: ""

                        Toast.makeText(this, "$id\n$nickname", Toast.LENGTH_SHORT).show()
                        G.userAccount= UserAccount(id, nickname)

                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }
            }
        }
        if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)){
            UserApiClient.instance.loginWithKakaoTalk(this, callback= callback)
        }else{
            UserApiClient.instance.loginWithKakaoAccount(this, callback= callback)
        }
    }
    private fun clickNaver(){

        // 네아로 SDK 초기화
        NaverIdLoginSDK.initialize(this,"U2MGzMk8kIiCv2p4niWQ", "RCckMk82YM","comeout")

        // 로그인 요청
        NaverIdLoginSDK.authenticate(this,object : OAuthLoginCallback {
            override fun onError(errorCode: Int, message: String) {

            }

            override fun onFailure(httpStatus: Int, message: String) {

            }

            override fun onSuccess() {
                Toast.makeText(this@LoginActivity, "네이버 로그인 성공", Toast.LENGTH_SHORT).show()


                val accessToken:String? = NaverIdLoginSDK.getAccessToken()

                val retrofit = RetrofitHelper.getRetrofitInstance("https://openapi.naver.com")
                val retrofitApiService = retrofit.create(RetrofitApiService::class.java)
                val call = retrofitApiService.getNidUserInfo("Bearer ${accessToken}")
                call.enqueue(object : retrofit2.Callback<String>{
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        val s = response.body()
                        AlertDialog.Builder(this@LoginActivity).setMessage(s).create().show()

                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {

                    }
                })
            }
        })

    }

}
