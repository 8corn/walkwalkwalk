package com.example.walkwalkwalk.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.walkwalkwalk.databinding.ActivityJoinBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class JoinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJoinBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.joinPwdTxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                checkPassword()
            }
        })

        binding.joinPwdTxtCheck.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                checkPassword()
            }
        })

        binding.joinFromTxt.apply {
            isFocusable = false
            isCursorVisible = false
        }

        binding.joinFromTxt.setOnClickListener {
            val intent = Intent(this@JoinActivity, WebViewJoinActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.joinNextBtn.setOnClickListener {
            signup()
        }
    }

    private fun checkPassword() {
        val pwd = binding.joinPwdTxt.text.toString().trim()
        val confirmpwd = binding.joinPwdTxtCheck.text.toString().trim()

        when {
            pwd.isEmpty() || pwd.length < 8 -> {
                binding.joinPwdTxt.error = "영소문자, 특수문자, 숫자를 포함하여 8자 이상 입력하시오."
            }
            pwd != confirmpwd -> {
                binding.joinPwdTxtCheck.error = "비밀번호가 일치하지 않습니다."
            }
            else -> {
                binding.joinPwdCheckMsg.visibility = View.INVISIBLE
            }
        }
    }

    private fun signup() {
        val email = binding.joinIdTxt.toString().trim()
        val password = binding.joinPwdTxt.toString().trim()
        val name = binding.joinNameTxt.toString().trim()
        val phonenum = binding.joinNumberTxt.toString().trim()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val uID = auth.customAuthDomain

                    val userData = hashMapOf(
                        "uid" to uID,
                        "email" to email,
                        "password" to password,
                        "name" to name,
                        "phonemun" to phonenum,
                    )

                    firestore.collection("users")
                        .add(userData)
                        .addOnSuccessListener {
                            val intent = Intent(this, StartActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Log.e("Tag", "Firebase에 문서 저장 오류")
                            Toast.makeText(this, "회원가입을 실패하였습니다., ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "회원가입 실패 : ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    Log.e("firebase Join Fail", "Firebase Join Fail")
                }
            }
    }
}