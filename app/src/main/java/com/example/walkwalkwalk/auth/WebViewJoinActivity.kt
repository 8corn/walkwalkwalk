package com.example.walkwalkwalk.auth

import android.net.http.SslError
import android.os.Bundle
import android.os.Message
import android.util.Log
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
import com.example.walkwalkwalk.R
import com.example.walkwalkwalk.databinding.ActivityWebViewJoinBinding

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

            addJavascriptInterface(WebViewData(), "window")
            webViewClient = client
            webChromeClient = chromeClient

            loadUrl(url)
            Log.d("WebViewDebug", "kakao_url: $url")
        }
    }

    private val client: WebViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            return false
        }

        @Deprecated("Use onReceivedHttpError and onReceivedSslError instead")
        override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failngUrl: String?) {
            Log.e("WebViewError", "Error $errorCode: $description (URL: $failngUrl)")
            Toast.makeText(this@WebViewJoinActivity, "웹페이지 로딩 실패: $description", Toast.LENGTH_SHORT).show()
        }

        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
            handler?.cancel()
        }
    }

    private inner class WebViewData {
        @JavascriptInterface
        fun getAddress(zoneCode: String, roadAddress: String, buildingName: String) {
            Log.d("WebViewDebug", "getAddress 호출됨: ($zoneCode) $roadAddress $buildingName")
        }
    }

    private val chromeClient = object : WebChromeClient() {
        override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
            val newWebView = WebView(view?.context!!).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
            }

            val parent = view.parent as ViewGroup
            parent.addView(newWebView)

            newWebView.webChromeClient = object : WebChromeClient() {
                override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                    return super.onJsAlert(view, url, message, result)
                    return true
                }

                override fun onCloseWindow(window: WebView?) {
                    parent.removeView(window)
                }
            }

            (resultMsg!!.obj as WebView.WebViewTransport).webView = newWebView
            resultMsg.sendToTarget()

            return true
        }
    }
}