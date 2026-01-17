package com.machiav3lli.derdiedas.utils

import android.content.Context
import android.content.res.Resources

fun Context.getStringByName(name: String) = try {
    val resId = resources.getIdentifier(name.replace("-", "_"), "string", packageName)
    getString(resId)
} catch (e: Resources.NotFoundException) {
    ""
}