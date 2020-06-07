package com.blanke.ankireader.ui

import android.content.Context


import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.dragselectrecyclerview.IDragSelectAdapter
import com.blanke.ankireader.R
import com.blanke.ankireader.bean.Deck
import com.blanke.ankireader.ui.DeckAdapter.ViewHold

import java.util.*

class DeckAdapter(
    private val context: Context,
    private var listener: Listener
) :
    RecyclerView.Adapter<ViewHold>(),
    IDragSelectAdapter {
    interface Listener {
        open fun onClick(index: Int)
        open fun onLongClick(index: Int)
        open fun onSelectionChanged(selectedCount: Int)
    }

    private var datas: MutableList<Deck>
    private var selectPositions: MutableList<Int>

    init {
        datas = ArrayList()
        selectPositions = mutableListOf()
    }

    fun isSelected(): Boolean {
        return selectPositions.size > 0
    }

    fun clearSelected() {
        selectPositions.clear()
        notifyDataSetChanged()
    }

    fun toggleSelected(position: Int) {
        if (selectPositions.contains(position)) {
            selectPositions.remove(position)
        } else {
            selectPositions.add(position)
        }
        notifyItemChanged(position)
        notifySelectedCount()
    }

    fun getSelectedCount(): Int {
        return selectPositions.size
    }

    fun selectAll() {
        selectPositions.clear()
        for (i in 0 until itemCount) {
            selectPositions.add(i)
        }
        notifySelectedCount()
        notifyDataSetChanged()
    }

    fun selectDecks(deckIds: MutableList<Long>) {
        for (deckId in deckIds) {
            for (i in datas.indices) {
                if (deckId == datas[i].id) {
                    setSelected(i, true)
                    break
                }
            }
        }
    }

    fun getSelectDeckIds(): LongArray {
        val deckIds = LongArray(selectPositions.size)
        for (i in selectPositions.indices) {
            deckIds[i] = datas[selectPositions[i]].id
        }
        return deckIds
    }

    fun setDatas(datas: MutableList<Deck>) {
        this.datas = datas
        notifyDataSetChanged()
    }

    private fun notifySelectedCount() {
        listener.onSelectionChanged(selectPositions.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHold {
        val view = View.inflate(context, R.layout.item_deck, null)
        return ViewHold(view)
    }

    override fun onBindViewHolder(holder: ViewHold, position: Int) {
        holder.tv.text = datas[position].name
        holder.itemView.isSelected = selectPositions.contains(position)
        holder.itemView.setOnLongClickListener(OnLongClickListener {
            listener.onLongClick(position)
            return@OnLongClickListener true
        })
        holder.itemView.setOnClickListener {
            listener.onClick(position)
        }
    }

    override fun setSelected(index: Int, selected: Boolean) {
        if (selected) {
            selectPositions.add(index)
        } else {
            selectPositions.remove(index)
        }
        notifyItemChanged(index)
        notifySelectedCount()
    }

    override fun isIndexSelectable(index: Int): Boolean {
        return true
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    class ViewHold(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv: TextView = itemView.findViewById<View>(R.id.item_deck_name) as TextView
    }


}
