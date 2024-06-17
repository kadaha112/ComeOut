package activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.daehankang.comeout.R
import com.daehankang.comeout.databinding.ActivityWriteBinding

class WriteActivity : AppCompatActivity() {

    private val binding by lazy { ActivityWriteBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.writeToolbar.setNavigationOnClickListener { finish() }
        binding.pickPhoto.setOnClickListener { clickPhoto() }

    }

    private fun clickPhoto(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) resultLauncher.launch(Intent(
            MediaStore.ACTION_PICK_IMAGES))
        else resultLauncher.launch(Intent(Intent.ACTION_OPEN_DOCUMENT).setType("image/*"))

    }
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == RESULT_OK){
            val uri = it.data?.data
            if (uri != null){
                binding.iv.setImageURI(uri)

            }
        }
    }
}