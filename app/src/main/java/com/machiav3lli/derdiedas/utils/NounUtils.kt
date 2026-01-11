package com.machiav3lli.derdiedas.utils

import android.content.Context
import android.content.res.Resources
import com.machiav3lli.derdiedas.data.Noun

fun Context.getStringByName(name: String) = try {
    val resId = resources.getIdentifier(name.replace("-", "_"), "string", packageName)
    getString(resId)
} catch (e: Resources.NotFoundException) {
    null
}

fun MutableList<Noun>.updateIds() = mapIndexed { index, noun ->
    noun.withID(index + 1L)
}.toMutableList()