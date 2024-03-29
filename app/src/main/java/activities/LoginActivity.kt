package activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.daehankang.comeout.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
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

    }
    private fun clickKakao(){

    }
    private fun clickNaver(){

        // 네아로 SDK 초기화
        NaverIdLoginSDK.initialize(this,"U2MGzMk8kIiCv2p4niWQ", "RCckMk82YM","comeout")

        // 로그인 요청
        NaverIdLoginSDK.authenticate(this,object : OAuthLoginCallback {
            override fun onError(errorCode: Int, message: String) {
                Toast.makeText(this@LoginActivity, "$message", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(httpStatus: Int, message: String) {
                Toast.makeText(this@LoginActivity, "$message", Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess() {
                Toast.makeText(this@LoginActivity, "로그인에 성공했습니다", Toast.LENGTH_SHORT).show()

                // 사용자 정보를 받아오기.. -- REST API로 받아야 함.
                // 로그인에 성공하면. REST API로 요청할 수 있는 토큰(token)
                val accessToken:String? = NaverIdLoginSDK.getAccessToken()

                // Restofit 작업을 통해 사용자 정보 가져오기
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
                        Toast.makeText(this@LoginActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        })

    }

}
