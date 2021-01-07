package com.example.eventmaster.ui.events

import android.os.Bundle
import android.view.Window
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.eventmaster.R


class PaymentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        supportActionBar?.hide(); // hide the title bar
        setContentView(R.layout.activity_payment)
        val extrasUrl= intent.extras?.get("url") as String

        val webView = findViewById<WebView>(R.id.webViewPayment)
        //webView.loadUrl("about:blank")
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                setResult(11, intent)
                finish()
                webView.destroy()
                return false // then it is not handled by default action
            }
        }
        webView.loadUrl(extrasUrl)
    }

    override fun onBackPressed() {
        setResult(7)
        super.onBackPressed()
    }
}