package com.example.walkwalkwalk.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.walkwalkwalk.LoadingActivity
import com.example.walkwalkwalk.MainActivity
import com.example.walkwalkwalk.R
import com.example.walkwalkwalk.databinding.ActivityStartBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse

class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private val googleSignInLuancher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(this, "구글 로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
                Log.e("Tag", "Google Login Fail", e)
            } catch (e: Exception) {
                Toast.makeText(this, "예기치 못한 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show()
                Log.e("GoogleLogin", "예기치 못한 오류: ${e.message}", e)
            }
        } else {
            Toast.makeText(this, "구글 로그인이 취소되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        NaverIdLoginSDK.initialize(
            this,
            getString(R.string.social_login_naver_client_id),
            getString(R.string.social_login_naver_client_secret),
            getString(R.string.social_login_naver_client_name)
        )

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        if (auth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.startLoginBtn.setOnClickListener {
            signin()
        }

        binding.startJoinBtn.setOnClickListener {
            val intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)
        }

        binding.startGoogleLogin.setOnClickListener {
            googleLogin()
        }

        binding.startNaverLogin.setOnClickListener {
            startNaverLogin()
        }
    }

    private fun signin() {
        val email = binding.startIdTxt.text.toString().trim()
        val password = binding.startPwdTxt.text.toString().trim()

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener (this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "로그인 실패 ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun googleLogin() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLuancher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, LoadingActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    task.exception?.let { exception ->
                        Toast.makeText(this, "구글 로그인 실패 ", Toast.LENGTH_SHORT).show()
                        Log.e("GoogleLogin", "구글 로그인 실패", task.exception)
                    }
                }
            }
    }

    private fun startNaverLogin() {

        val oAuthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                    override fun onSuccess(result: NidProfileResponse) {
                        val name = result.profile?.name ?: "name"
                        val email = result.profile?.email ?: "email"
                        val phonenum = result.profile?.mobile ?: "phonenum"

                        Log.d("NaverLogin", "이름: $name, 이메일: $email, 전화번호: $phonenum")

                        val intent = Intent(this@StartActivity, LoadingActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    override fun onError(errorCode: Int, message: String) {
                        Toast.makeText(this@StartActivity, "프로필 정보를 가져오는데 실패하였습니다.", Toast.LENGTH_SHORT).show()
                        Log.e("Tag", "네이버에서 정보를 가져오는 과정에 오류가 났습니다")
                    }

                    override fun onFailure(httpStatus: Int, message: String) {
                        Toast.makeText(this@StartActivity, "네이버 로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                        Log.e("TAG", "네이버에서 정보 가져오기 실패")
                    }
                })
            }
            override fun onError(errorCode: Int, message: String) {
                val naverAccessToken = NaverIdLoginSDK.getAccessToken()
                Toast.makeText(this@StartActivity, "네이버 인증에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                Log.d("Tag", "naverAccessToken : $naverAccessToken")
            }

            override fun onFailure(httpStatus: Int, message: String) {
                Toast.makeText(this@StartActivity, "네이버 인증 실패: $message", Toast.LENGTH_SHORT).show()
                Log.e("TAG", "네이버 엑세스에서 실패")
            }
        }
        NaverIdLoginSDK.authenticate(this, oAuthLoginCallback)
    }
}