package com.jeffcarey.android.pcapblog

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

class WebPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = FrameLayout(this)
        view.id = R.id.web_page_container
        setContentView(view)

        val fm = supportFragmentManager
        val currentFragment = fm.findFragmentById(R.id.web_page_container)

        if (currentFragment == null) {
            val fragment = WebPageFragment.newInstance(intent.data)
            fm.beginTransaction()
                .add(R.id.web_page_container, fragment)
                .commit()
        }
    }

    companion object {
        fun newIntent(context: Context, webPageUri: Uri?): Intent {
            return Intent(context, WebPageActivity::class.java).apply {
                data = webPageUri
            }
        }
    }
}