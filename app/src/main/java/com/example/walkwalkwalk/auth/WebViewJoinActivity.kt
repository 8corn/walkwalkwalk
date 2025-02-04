package com.example.walkwalkwalk.auth

import android.app.Dialog
import android.net.http.SslError
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.JsResult
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.example.walkwalkwalk.R
import com.example.walkwalkwalk.databinding.ActivityWebViewJoinBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WebViewJoinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebViewJoinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWebViewJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showKakaoAddressWebView()

    }

    private fun showKakaoAddressWebView() {
        binding.webJoinWebview.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            setSupportMultipleWindows(true)
            cacheMode = WebSettings.LOAD_NO_CACHE
            domStorageEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        binding.webJoinWebview.apply {
            val url = getString(R.string.kakao_url_2)

            addJavascriptInterface(WebViewData(), "php에서 적용한 name")
            webViewClient = client
            webChromeClient = chromeClient
            visibility = View.VISIBLE

            loadUrl(url)
            Log.d("WebViewDebug", "kakao_url: $url")
        }
    }

    private val client: WebViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            return false
        }

        override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failngUrl: String?) {
            Log.e("WebViewError", "Error $errorCode: $description (URL: $failngUrl)")
            Toast.makeText(this@WebViewJoinActivity, "웹페이지 로딩 실패: $description", Toast.LENGTH_SHORT).show()

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
//                    binding.joinFromTxt.setText("($zoneCode) $roadAddress $buildingName")
                }
            }
        }
    }

    private val chromeClient = object : WebChromeClient() {
        override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
            val newWebView = WebView(this@WebViewJoinActivity)

            newWebView.settings.javaScriptEnabled = true

            val dialog = Dialog(this@WebViewJoinActivity)

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
}