package com.tagok.app.di.modules

import android.content.Context

object AppModule
{
    lateinit var appContext: Context
        private set

    fun init(context: Context)
    {
        appContext = context.applicationContext
    }
}