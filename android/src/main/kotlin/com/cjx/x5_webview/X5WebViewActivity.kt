package com.cjx.x5_webview

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.WebSettings
import android.widget.FrameLayout
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import com.tencent.smtt.sdk.WebChromeClient
import org.jetbrains.annotations.Nullable

private var fullscreenContainer: X5WebViewActivity.FullscreenHolder? = null
private var mCustomView: View? = null
private var mCustomViewCallback: IX5WebChromeClient.CustomViewCallback? = null

class X5WebViewActivity : Activity() {
    var webView: WebView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_ACTION_BAR)
        window.setFormat(PixelFormat.TRANSLUCENT)
        webView = WebView(this)
        setContentView(webView)

        initView()
    }

    private fun initView() {
        actionBar?.show()
        actionBar?.setDisplayHomeAsUpEnabled(true)
        title = intent.getStringExtra("title") ?: ""
        webView?.apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)

            settings.javaScriptEnabled = true
            settings.allowFileAccess = true
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                    view.loadUrl(url)
                    return super.shouldOverrideUrlLoading(view, url)
                }

                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest?): Boolean {
                    view.loadUrl(request?.url.toString())
                    return super.shouldOverrideUrlLoading(view, request)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
            loadUrl(intent.getStringExtra("url"))
        }

        var webChromeClient: WebChromeClient = object : WebChromeClient() {
            @Nullable
            override fun getVideoLoadingProgressView(): View? {
                return super.getVideoLoadingProgressView()
            }


            override fun onShowCustomView(view: View?, callback: IX5WebChromeClient.CustomViewCallback?) {
                super.onShowCustomView(view, callback)
                showCustomView(view!!, callback!!)
            }

            override fun onHideCustomView() {
                //                super.onHideCustomView();
                Log.e("abner", "onHideCustomView")
                super.onHideCustomView()

                hideCustomView()
            }
        }
        webView?.setWebChromeClient(webChromeClient)

    }


    /**
     * 视频播放全屏
     */

    private fun showCustomView(view: View, callback: IX5WebChromeClient.CustomViewCallback) {
        // if a view already exists then immediately terminate the new one
        if (mCustomView != null) {
            callback.onCustomViewHidden()
            return
        }
        setStatusBarVisibility(false)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        webView?.visibility = View.INVISIBLE
        val decor = window.decorView as FrameLayout
        fullscreenContainer = FullscreenHolder(this);
        fullscreenContainer!!.addView(view)
        decor.addView(fullscreenContainer)
        mCustomView = view
        mCustomViewCallback = callback
    }

    /**
     * 隐藏视频全屏
     */
    private fun hideCustomView() {
        if (mCustomView == null) {
            return
        }
        setStatusBarVisibility(true)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val decor = window.decorView as FrameLayout
        decor.removeView(fullscreenContainer)
        fullscreenContainer = null
        mCustomView = null
        mCustomViewCallback?.onCustomViewHidden()
        webView?.visibility = View.VISIBLE
    }

    /**
     * 全屏容器界面
     */
    internal class FullscreenHolder(ctx: Context) : FrameLayout(ctx) {

        init {
            setBackgroundColor(ctx.resources.getColor(android.R.color.black))
        }

        override fun onTouchEvent(evt: MotionEvent): Boolean {
            return true
        }
    }

    private fun setStatusBarVisibility(visible: Boolean) {
        val flag = if (visible) 0 else WindowManager.LayoutParams.FLAG_FULLSCREEN
        window.setFlags(flag, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onDestroy() {
        super.onDestroy()
        webView?.destroy()
    }

    override fun onPause() {
        super.onPause()
        webView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        webView?.onResume()
    }

}