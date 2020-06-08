package com.blanke.ankireader.ui


import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.dragselectrecyclerview.DragSelectRecyclerView
import com.afollestad.materialcab.MaterialCab
import com.blanke.ankireader.R
import com.blanke.ankireader.bean.Deck
import com.blanke.ankireader.config.PlayConfig
import com.blanke.ankireader.data.AnkiManager
import com.github.florent37.runtimepermission.kotlin.askPermission

class MainActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var mainRv: DragSelectRecyclerView
    private lateinit var mAdapter: DeckAdapter
    private lateinit var cab: MaterialCab
    private lateinit var playConfig: PlayConfig
    override fun onCreate(savedInstanceState: Bundle?) {
        // set main activity
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // tool bar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        playConfig = PlayConfig.loadConfig(this)
        initRecyclerView()
        askPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
            Toast.makeText(
                this@MainActivity, "权限申请成功",
                Toast.LENGTH_SHORT
            ).show()
            prepareLoadData()
        }.onDeclined { e ->
            Toast.makeText(
                this@MainActivity, "权限申请失败，程序无法运行",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun initRecyclerView() {
        mainRv = findViewById(R.id.main_rv)
        mainRv.layoutManager = LinearLayoutManager(this)
        mAdapter = DeckAdapter(this)
        mainRv.adapter = mAdapter
    }


    private fun prepareLoadData() {
        val decks: MutableList<Deck> = try {
            AnkiManager.getAllDecks()
        } catch (e: Exception) {
            Toast.makeText(this, R.string.msg_anki_not_install, Toast.LENGTH_LONG).show()
            return
        }
        if (decks.size == 0) {
            Toast.makeText(this, R.string.msg_deck_empty, Toast.LENGTH_LONG).show()
        }
        mAdapter.setDatas(decks)
        mAdapter.selectDecks(playConfig.playDeckIds) //已经选择的
    }


    private fun start(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }
}
