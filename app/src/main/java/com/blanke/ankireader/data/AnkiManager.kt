package com.blanke.ankireader.data

import android.database.sqlite.SQLiteDatabase
import android.os.Environment
import com.blanke.ankireader.Config
import com.blanke.ankireader.bean.Deck
import com.blanke.ankireader.bean.Note
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*
import java.util.regex.Pattern

/**
 * Created by on 2016/10/14.
 */
object AnkiManager {
    private val ANKI_HOME_PATH: String = Environment.getExternalStorageDirectory().absolutePath +
            File.separatorChar + "AnkiDroid" + File.separatorChar
    private val ANKI_DB_PATH: String = ANKI_HOME_PATH + "collection.anki2"
    private val ANKI_MEDIA_PATH: String = ANKI_HOME_PATH + "collection.media" + File.separatorChar
    private const val TABLE_NOTES: String = "notes"
    private const val TABLE_COL: String = "col"
    private fun openAnkiDb(): SQLiteDatabase {
        return SQLiteDatabase.openOrCreateDatabase(ANKI_DB_PATH, null)
    }


    /**
     * 得到所有的卡牌信息
     *
     * @return
     */
    fun getAllDecks(): MutableList<Deck> {
        val decks: MutableList<Deck> = ArrayList()
        val db = openAnkiDb()
        val sql = "select * from $TABLE_COL"
        val c = db.rawQuery(sql, null)
        while (c.moveToNext()) {
            val decksJson = c.getString(c.getColumnIndex("decks"))
            try {
                val jsonObject = JSONObject(decksJson)
                val iter = jsonObject.keys()
                while (iter.hasNext()) {
                    val idStr = iter.next()
                    val tempJson = jsonObject.getJSONObject(idStr)
                    val name = tempJson.getString("name")
                    val id = idStr.toLong()
                    val d = Deck(id, name)
                    decks.add(d)
                    //                    Logger.d(d.toString());
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        c.close()
        db.close()
        return decks
    }

    /**
     * 获得全部含有音频的 note
     *
     * @return
     */
    fun getAllHasMediaNotesByDeckId(deckIds: MutableList<Long>): List<Note> {
        val sources = getNotesByDeckId(deckIds)
        return sources.filter { it.mediaPaths.size > 0 }
    }

    /**
     * 根据文件名获取的完整路径
     *
     * @param filename
     * @return
     */
    private fun getFullMediaPath(filename: String): String {
        return ANKI_MEDIA_PATH + filename
    }

    fun getNotesByDeckId(decks: MutableList<Long>): MutableList<Note> {
        val notes: MutableList<Note> = ArrayList()
        val db = openAnkiDb()
        var sql = "select * from $TABLE_NOTES"
        if (decks.isNotEmpty()) {
            val sb = StringBuilder()
            for (i in decks.indices) {
                if (i != decks.size - 1) {
                    sb.append(decks[i].toString() + " , ")
                } else {
                    sb.append(decks[i])
                }
            }
            sql = ("select n.id,n.tags, flds, sfld" +
                    " from notes as n, cards as c where c.nid=n.id  and c.did in (" + sb.toString()
                    + " ) order by n.id desc")
        }
        val c = db.rawQuery(sql, null)
        while (c.moveToNext()) {
            val id = c.getLong(c.getColumnIndex("id"))
            val tags = c.getString(c.getColumnIndex("tags"))
            val flds = c.getString(c.getColumnIndex("flds")) //front
            val sfld = c.getString(c.getColumnIndex("sfld")) //front+back
            val mediaPaths = mutableListOf<String>()
            val r = Pattern.compile(Config.REG_SOUND)
            val m = r.matcher(flds)
            while (m.find()) {
                val path = m.group(1)
                mediaPaths.add(getFullMediaPath(path))
            }
            var back = flds.substring(sfld.length, flds.length)
            var front = sfld
            back = back.replace(Config.REG_SOUND.toRegex(), "")
            front = front.replace(Config.REG_SOUND.toRegex(), "")
            front = front.trim { it <= ' ' }
            back = back.trim { it <= ' ' }
            notes.add(Note(id, tags, front, back, mediaPaths))
        }
        c.close()
        db.close()
        return notes
    }
}
