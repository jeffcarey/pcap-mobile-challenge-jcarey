package com.jeffcarey.android.pcapblog

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment

private const val ARG_URI = "web_page_uri"

class WebPageFragment : Fragment() {
    private lateinit var uri: Uri
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uri = arguments?.getParcelable(ARG_URI) ?: Uri.EMPTY
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = ConstraintLayout(this.requireContext())

        webView = WebView(this.requireContext())
        val layoutParams: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
        layoutParams.topMargin = 10
        webView.id = R.id.web_view
        webView.webViewClient  = WebViewClient()
        webView.loadUrl(uri.toString())
        view.addView(webView, layoutParams)

        return view
    }

    companion object {
        fun newInstance(uri: Uri?): WebPageFragment {
            return WebPageFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_URI, uri)
                }
            }
        }
    }


}