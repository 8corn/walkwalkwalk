package com.example.walkwalkwalk.auth

import android.app.Dialog
import android.content.Intent
import android.net.http.SslError
import android.os.Bundle
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.JsResult
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.walkwalkwalk.databinding.ActivityJoinBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
            setOnClickListener {
                showKakaoAddressWebView()
            }
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

    private fun showKakaoAddressWebView() {
        binding.joinWebview.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            setSupportMultipleWindows(true)
        }

        binding.joinWebview.apply {
            addJavascriptInterface(WebViewData(), "php에서 적용한 name")
            webViewClient = client
            webChromeClient = chromeClient
            visibility = View.VISIBLE
            loadUrl("내 주소")
        }
    }

    private val client: WebViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            return false
        }

        override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failngUrl: String?) {
            Toast.makeText(this@JoinActivity, "웹페이지 로딩 실패: $description", Toast.LENGTH_SHORT).show()
        }

        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
            handler?.proceed()
        }
    }

    private inner class WebViewData {
        @JavascriptInterface
        fun getAddress(zoneCode: String, roadAddress: String, buildingName: String) {
            CoroutineScope(Dispatchers.Default).launch {
                withContext(CoroutineScope(Dispatchers.Main).coroutineContext) {
                    binding.joinFromTxt.setText("($zoneCode) $roadAddress $buildingName")
                }
            }
        }
    }

    private val chromeClient = object : WebChromeClient() {
        override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
            val newWebView = WebView(this@JoinActivity)

            newWebView.settings.javaScriptEnabled = true

            val dialog = Dialog(this@JoinActivity)

            dialog.setContentView(newWebView)

            val params = dialog.window!!.attributes

            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.attributes = params
            dialog.show()

            newWebView.webChromeClient = object : WebChromeClient() {
                override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                    return super.onJsAlert(view, url, message, result)
                    return true
                }

                override fun onCloseWindow(window: WebView?) {
                    dialog.dismiss()
                }
            }

            (resultMsg!!.obj as WebView.WebViewTransport).webView = newWebView
            resultMsg.sendToTarget()

            return true
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