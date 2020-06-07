package com.blanke.ankireader.weiget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.blanke.ankireader.Config
import com.blanke.ankireader.R

/**
 * Created by blanke on 2017/6/8.
 */
object ChoseTextSizeDialog {
    interface onChoseTextSizeListener {
        open fun onChoseSize(size: Int)
    }

    fun show(
        context: Context,
        @StringRes titleId: Int,
        defaultSize: Int,
        listener: onChoseTextSizeListener
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_textsize_chose, null)
        val dialogTextsizeTvTest = view.findViewById<View>(R.id.dialog_textsize_tv_test) as TextView
        val dialogTextsizePb = view.findViewById<View>(R.id.dialog_textsize_sb) as SeekBar
        dialogTextsizePb.max = Config.FONT_SIZE
        dialogTextsizePb.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                dialogTextsizeTvTest.textSize = progress.toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        dialogTextsizePb.progress = defaultSize
        dialogTextsizeTvTest.textSize = defaultSize.toFloat()
        AlertDialog.Builder(context)
            .setTitle(titleId)
            .setCancelable(true)
            .setView(view)
            .setPositiveButton(R.string.confirm) { dialog, which ->
                listener.onChoseSize(
                    dialogTextsizePb.progress
                )
            }
            .show()
    }

}
