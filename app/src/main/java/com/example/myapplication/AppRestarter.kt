package com.example.myapplication

import android.content.Context

object AppRestarter {
    fun restartProcess(context: Context) {
        val pm = context.packageManager
        val intent = pm.getLaunchIntentForPackage(context.packageName) ?: return
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
        Runtime.getRuntime().exit(0)
    }
}
