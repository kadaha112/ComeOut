package activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.daehankang.comeout.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestoreDB: FirebaseFirestore
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signUpToolbar.setNavigationOnClickListener { finish() }

        auth = FirebaseAuth.getInstance()
        firestoreDB = FirebaseFirestore.getInstance()

        binding.signUpButton.setOnClickListener {
            val email = binding.signEmail.text.toString().trim()
            val password = binding.signPassword.text.toString().trim()
            val confirmPassword = binding.signConfirmPassword.text.toString().trim()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(applicationContext, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(applicationContext, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(applicationContext, "비밀번호를 다시 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(applicationContext, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        val user = auth.currentUser

                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {

                        if (task.exception is FirebaseAuthUserCollisionException) {
                            Toast.makeText(
                                baseContext, "이미 등록된 이메일입니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                baseContext, "회원가입 실패: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
        }
    }
}

