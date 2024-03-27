package activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.daehankang.comeout.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

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

    }

}
