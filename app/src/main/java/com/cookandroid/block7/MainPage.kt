package com.cookandroid.block7

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.Settings.Global
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView


class MainPage : BaseActivity() {
    lateinit var selected_gamestarte_image: ImageView
    lateinit var selected_option_image: ImageView
    lateinit var selected_more_image: ImageView
    lateinit var selected_achivement_image: ImageView
    lateinit var selected_exit_image: ImageView
    lateinit var gamestarte_image: ImageView
    lateinit var option_image: ImageView
    lateinit var more_image: ImageView
    lateinit var achivement_image: ImageView
    lateinit var exit_image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page)
        startService(Intent(this, MusicService::class.java))

        selected_gamestarte_image = findViewById(R.id.selected_gamestart)
        selected_option_image = findViewById(R.id.selected_option)
        selected_more_image = findViewById(R.id.selected_more)
        selected_achivement_image = findViewById(R.id.selected_achive)
        selected_exit_image = findViewById(R.id.selected_exit)
        gamestarte_image = findViewById(R.id.gamestart)
        option_image = findViewById(R.id.option)
        more_image = findViewById(R.id.more)
        achivement_image = findViewById(R.id.achive)
        exit_image = findViewById(R.id.exit)

        // 게임 시작 버튼에 클릭 리스너 추가
        val startGameButton: ImageButton = findViewById(R.id.startGame)
        startGameButton.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    startGameButton.setImageResource(R.drawable.gamestart)
                }
                MotionEvent.ACTION_UP -> {
                    click_sound()
                    startGameButton.setImageResource(R.drawable.sgamestart)

                    // 이미지 보이기


                    val intent = Intent(this, GameActivity::class.java)
                    startActivity(intent)

                }
                MotionEvent.ACTION_CANCEL -> {
                    startGameButton.setImageResource(R.drawable.gamestart)
                }
            }
            true
        }

        // 옵션 이미지에 클릭 리스너 추가
        val optionButton: ImageButton = findViewById(R.id.option_game)
        optionButton.setOnClickListener {
            click_sound()

            // 이미지 보이기
            selected_option_image.visibility = View.VISIBLE

            // 옵션 다이얼로그 띄우기 등의 작업 추가
            val intent = Intent(this, OptionDialog::class.java)
            startActivity(intent)
        }

        // 종료 이미지에 클릭 리스너 추가
        val exitFrame: ImageButton = findViewById(R.id.Cancel)
        exitFrame.setOnClickListener {
            click_sound()

            // 이미지 보이기
            selected_exit_image.visibility = View.VISIBLE

            // 종료 작업 등 추가
            finishAffinity()
        }


        val achievementFrame: ImageButton = findViewById(R.id.achieve)
        achievementFrame.setOnClickListener{
            click_sound()
            val intent = Intent(this, AchievementsDialog::class.java)
            startActivity(intent)
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


