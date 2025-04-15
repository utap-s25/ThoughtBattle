package com.example.thoughtbattle

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import com.example.thoughtbattle.data.repository.SendBirdRepository
import com.example.thoughtbattle.ui.auth.AuthUser
import com.google.firebase.FirebaseApp

//apparently having this class will be really important since i think we will need a global context?? yeah lol
class ThoughtBattle : Application() {
    private lateinit var authUser: AuthUser

    override fun onCreate() {
        super.onCreate()
//we initialize the firebase app and sendbird app. this seems like best place to do it





    }
    fun Context.isDarkThemeOn(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
    }
}