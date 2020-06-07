package com.blanke.ankireader.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.blanke.ankireader.R


/**
 * Created by blanke on 2017/6/8.
 */
class SettingsActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
//        //        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })
//        fragmentManager.beginTransaction().replace(
//            R.id.setting_fl_container,
//            SettingsFragment()
//        ).commit()
    }

    override fun setTitle(title: CharSequence) {
        super.setTitle(title)
        toolbar.title = title
    }
}
