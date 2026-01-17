package com.machiav3lli.derdiedas.utils

import android.content.Context
import com.machiav3lli.derdiedas.data.Noun
import com.machiav3lli.derdiedas.data.NounDatabase
import java.io.UnsupportedEncodingException

fun Context.createNounListFromAsset(): List<Noun> {
    var nounsString: String? = null
    try {
        nounsString = FileUtils.getNounList(this)
    } catch (e: UnsupportedEncodingException) {
        e.printStackTrace()
    }
    val nouns = FileUtils.getLines(nounsString!!)
    return nouns.mapIndexed { i, it ->
        val noun = it.split(",").toTypedArray()[0]
        val gender = it.split(",").toTypedArray()[1]
        Noun(0, noun, gender, 0, i)
    }
}

suspend fun Context.getNounsCount(): Pair<Int,Int> {
    val db = NounDatabase.getInstance(this)
    return Pair(db.nounDao.getMasteredNounCount(),db.nounDao.getAllNounCount())
}
