package hu.bd.mealplanner

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hu.bd.mealplanner.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.tvHaveAccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.buttonRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val retypePassword = binding.etRetypePassword.text.toString()
            val nickname = binding.etNickName.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && retypePassword.isNotEmpty() && nickname.isNotEmpty()) {
                if (password == retypePassword){
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful){
                            val user = firebaseAuth.currentUser
                            user?.let {
                                val userData = hashMapOf(
                                    "nickname" to nickname
                                )

                                firestore.collection("users").document(it.uid)
                                    .set(userData)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, LoginActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Failed to save nickname: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                            }
                        }else{
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(this, "Passwords is not match", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}