package com.blanke.ankireader.ui


import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
import com.blanke.ankireader.event.StartPlayEvent
import com.blanke.ankireader.event.StopPlayEvent
import com.blanke.ankireader.play.PlayerService
import com.blanke.ankireader.ui.settings.SettingsActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mylhyl.acp.Acp
import com.mylhyl.acp.AcpListener
import com.mylhyl.acp.AcpOptions
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity(), DeckAdapter.Listener, MaterialCab.Callback {
    private lateinit var toolbar: Toolbar
    private lateinit var mainRv: DragSelectRecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var mAdapter: DeckAdapter
    private lateinit var cab: MaterialCab
    private lateinit var playConfig: PlayConfig
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EventBus.getDefault().register(this)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        playConfig = PlayConfig.loadConfig(this)
        initRecyclerView()
        initFab()
        Acp.getInstance(this).request(AcpOptions.Builder()
            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .build(),
            object : AcpListener {
                override fun onGranted() {
                    prepareLoadData()
                }

                override fun onDenied(permissions: MutableList<String>) {
                    Toast.makeText(
                        this@MainActivity, "权限申请失败，程序无法运行",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            })
    }

    private fun initFab() {
        fab = findViewById(R.id.main_fab)
        fab.setOnClickListener(View.OnClickListener {
            if (PlayerService.isRunning()) {
                stopPlayService()
            } else {
                PlayConfig.saveDeckIds(
                    this@MainActivity
                    , *mAdapter.getSelectDeckIds()
                )
                startPlayService()
            }
            fab.isEnabled = false
        })
        initPlayFabDrawable()
    }

    private fun initPlayFabDrawable() {
        fab.isEnabled = true
        if (PlayerService.isRunning()) {
            fab.setImageResource(R.drawable.ic_stop)
        } else {
            fab.setImageResource(R.drawable.ic_play)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun stopPlay(event: StopPlayEvent) {
        initPlayFabDrawable()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun startPlay(event: StartPlayEvent) {
        initPlayFabDrawable()
    }

    private fun initRecyclerView() {
        mainRv = findViewById(R.id.main_rv)
        mainRv.layoutManager = LinearLayoutManager(this)
        mAdapter = DeckAdapter(this, this)
        mainRv.adapter = mAdapter
    }

    private fun toggleFab(show: Boolean) {
        if (!show && PlayerService.isRunning()) {
            return
        }
        if (show) {
            fab.show()
        } else {
            fab.hide()
        }
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

    private fun startPlayService() {
        val intent2 = Intent(this, PlayerService::class.java)
        startService(intent2)
    }

    private fun stopPlayService() {
        val intent2 = Intent(this, PlayerService::class.java)
        stopService(intent2)
    }

    override fun onClick(index: Int) {
        // 点击转发给长按事件
        onLongClick(index)
    }

    override fun onLongClick(index: Int) {
        if (mAdapter.isSelected()) {
            mAdapter.toggleSelected(index)
        } else {
            mainRv.setDragSelectActive(true, index)
        }
    }

    override fun onSelectionChanged(count: Int) {
        if (count > 0) {
            cab = MaterialCab(this, R.id.cab_stub)
                .setMenu(R.menu.menu_selected_deck)
                .setCloseDrawableRes(R.drawable.ic_arrow_back)
                .start(this)
            cab.toolbar.setTitleTextColor(Color.WHITE)
            cab.setTitle(mAdapter.getSelectedCount().toString() + "")
            toggleFab(true) //show fab
        } else if (cab.isActive) {
            cab.reset().finish()
            toggleFab(false) //hide fab
        }
    }

    override fun onCabCreated(cab: MaterialCab, menu: Menu): Boolean {
        return true
    }

    override fun onCabItemClicked(item: MenuItem): Boolean {
        if (item.itemId == R.id.select_deck_done_all) {
            mAdapter.selectAll()
        }
        return true
    }

    override fun onCabFinished(cab: MaterialCab): Boolean {
        mAdapter.clearSelected()
        toggleFab(false)
        return true
    }

    override fun onBackPressed() {
        if (mAdapter.isSelected()) {
            mAdapter.clearSelected()
            cab.reset().finish()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun start(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_main_about -> start(AboutActivity::class.java)
            R.id.menu_main_settings -> start(SettingsActivity::class.java)
        }
        return true
    }
}
