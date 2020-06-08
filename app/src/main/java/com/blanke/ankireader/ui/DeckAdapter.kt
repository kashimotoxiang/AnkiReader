package com.blanke.ankireader.ui


import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.dragselectrecyclerview.IDragSelectAdapter
import com.blanke.ankireader.R
import com.blanke.ankireader.bean.Deck
import com.blanke.ankireader.ui.DeckAdapter.ViewHold


class DeckAdapter(
    private val context: Context
) :
    RecyclerView.Adapter<ViewHold>(),
    IDragSelectAdapter {

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
    }

    fun toggleSelected(position: Int) {
        if (selectPositions.contains(position)) {
            selectPositions.remove(position)
        } else {
            selectPositions.add(position)
        }
    }

    fun selectAll() {
        selectPositions.clear()
        for (i in 0 until itemCount) {
            selectPositions.add(i)
        }
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
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHold {
        val view = View.inflate(context, R.layout.item_deck, null)
        return ViewHold(view)
    }

    override fun onBindViewHolder(holder: ViewHold, position: Int) {
        holder.tv.text = datas[position].name
    }

    override fun setSelected(index: Int, selected: Boolean) {
        if (selected) {
            selectPositions.add(index)
        } else {
            selectPositions.remove(index)
        }
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
