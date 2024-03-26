package activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.daehankang.comeout.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SignUpActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private val binding by lazy { ActivitySignUpBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var email= binding.signUpEmail
        var pw= binding.signUpPassword

        binding.btnSignup.setOnClickListener {
            var email = email.text.toString()
            var pw = pw.text.toString()

            auth = Firebase.auth

            init(email, pw)
        }

    }
    private fun init(email: String, pw: String){

        if(email.isNullOrEmpty() || pw.isNullOrEmpty()) {
            Toast.makeText(this, "이메일 혹은 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
        }
        else {
            auth.createUserWithEmailAndPassword(email,pw).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "회원가입 완료", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "이메일 형식인지 확인 또는 비밀번호 6자리이상 입력해주세요!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}