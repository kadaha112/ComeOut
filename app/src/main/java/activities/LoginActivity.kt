package activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.daehankang.comeout.R
import com.daehankang.comeout.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.signUp.setOnClickListener {startActivity(Intent(this, activities.SignUpActivity::class.java))
        finish()}

        var id : TextInputLayout = binding.inputLayoutEmail
        var pw : TextInputLayout = binding.inputLayoutPassword

        val login = binding.signUpLoginBtn

        auth= Firebase.auth

        login.setOnClickListener {
            login(id.editText.toString(), pw.editText.toString())
        }
    }
    private fun login(id: String, pw: String) {

        if (id.isNullOrEmpty() || pw.isNullOrEmpty()) {
            Toast.makeText(this, "빈 값을 입력하셨습니다.", Toast.LENGTH_SHORT).show()
        } else {
            auth.signInWithEmailAndPassword(id, pw)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "로그인에 성공했습니다!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    else {
                        Toast.makeText(this, "아이디와 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}