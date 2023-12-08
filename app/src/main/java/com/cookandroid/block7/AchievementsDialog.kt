package com.cookandroid.block7

import android.content.Intent
import android.os.Bundle
import android.widget.Button

class AchievementsDialog : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.achievements_dialog)
        startService(Intent(this, MusicService::class.java))


        val exit: Button = findViewById<Button>(R.id.Close_Achieve_button)
        exit.setOnClickListener {
            click_sound()
            finish()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, MusicService::class.java))
    }

    fun click_sound() {
        val music_intent = Intent(this, MusicService::class.java)
        music_intent.action = "PLAY_CLICK_SOUND"
        startService(music_intent)
    }
}