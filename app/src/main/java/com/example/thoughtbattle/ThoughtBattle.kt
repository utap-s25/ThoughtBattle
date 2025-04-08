package com.example.thoughtbattle

import android.app.Application
import com.google.firebase.FirebaseApp
import com.sendbird.android.*
import com.sendbird.android.exception.SendbirdException
import com.sendbird.android.handler.InitResultHandler
import com.sendbird.android.params.InitParams

//apparently having this class will be really important since i think we will need a global context?? yeah lol
class ThoughtBattle : Application() {
    override fun onCreate() {
        super.onCreate()
//we initialize the firebase app and sendbird app. this seems like best place to do it

        FirebaseApp.initializeApp(this)

        val params = InitParams(BuildConfig.SENDBIRD_APP_ID, this, false)
        SendbirdChat.init(params, object : InitResultHandler {
            override fun onMigrationStarted() {}
            override fun onInitFailed(e: SendbirdException) {}
            override fun onInitSucceed() {}
        })

    }
}