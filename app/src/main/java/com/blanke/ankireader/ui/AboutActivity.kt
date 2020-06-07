package com.blanke.ankireader.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.blanke.ankireader.R
import java.util.*

/**
 * Created by blanke on 2017/4/5.
 */
class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
    }

    fun onClick(view: View) {
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        val payUrl = "HTTPS://QR.ALIPAY.COM/FKX02968MD7TU2OGNMIW5D"
        intent.data =
            Uri.parse("alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=$payUrl")
        if (intent.resolveActivity(this@AboutActivity.packageManager) != null) {
            startActivity(intent)
            return
        }
        intent.data = Uri.parse(payUrl.toLowerCase(Locale.getDefault()))
        startActivity(intent)
    }
}
