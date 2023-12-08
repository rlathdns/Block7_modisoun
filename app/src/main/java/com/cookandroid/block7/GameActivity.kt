package com.cookandroid.block7

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

class GameActivity : BaseActivity() {
    lateinit var blackBackground : ImageView
    lateinit var dayText : TextView

    lateinit var member_dameun: Member
    lateinit var member_eunju: Member
    lateinit var member_sowoon: Member
    lateinit var member_hyundong: Member

    lateinit var item_axe: Item
    lateinit var item_book: Item
    lateinit var item_card: Item
    lateinit var item_drone: Item
    lateinit var item_medkit: Item
    lateinit var item_flashlight: Item
    lateinit var item_gasmask: Item
    lateinit var item_lock: Item
    lateinit var item_map: Item
    lateinit var item_pesticide: Item
    lateinit var item_phone: Item
    lateinit var item_rattle: Item
    lateinit var item_toolbox: Item

    lateinit var food_kimbap: Food
    lateinit var food_water: Food

    lateinit var memberList: List<Member>

    lateinit var eventHandler: EventHandler

    lateinit var memberListIsIn: MutableList<Member>

    lateinit var itemList: List<Item>
    val itemListOwned = mutableListOf<Item>()
    val itemListNotOwned = mutableListOf<Item>() //초기에는 모든 아이템 없는걸로 취급
    val itemListBroken = mutableListOf<Item>()

    /* 무작위로 객체를 반환하는 메소드 */
    fun getRandomMemberFromListIsIn(): Member { // 무작위 멤버 : 안에 있는 멤버 중 무작위로 1명을 골라 반환함
        return memberListIsIn.random()
        // Item과 달리 null은 고려하지 않음.
    }
    fun getRandomItemitemListOwned(): Item? { // 무작위 아아템 : 무작위로 가지고 있는 아이템 중 하나의 아이템을 선택하는 메소드
        return if (itemListOwned.isNotEmpty()) { itemListOwned.random() }
        else { null } // 리스트가 비어있을 경우 null 반환
    }
    fun getRandomFood(): Food { // 무작위로 김밥, 물 중 하나를 선택하는 메소드
        val randomIndex = (0..1).random() // 0 또는 1 중에서 무작위 선택
        return if (randomIndex == 0) { food_kimbap }
        else { food_water }
    }

    // 멤버 외출
    fun explore(member: Member) {
        member.goExploring()
    }

    // 멤버 dayPase - 하루가 지날 때 호출
    fun memberDayPase() { for(member in memberList) member.dayPase() }

    // 아이템과 멤버의 visiblity 업데이트 메소드 - finishbutton 클릭 리스너에서 호출 + ...
    fun updateAllVisibility() {
        for(item in itemListOwned) item.updateItemVisibility()
        for(item in itemListBroken) item.updateItemVisibility()
        for(item in itemListNotOwned) item.updateItemVisibility()
        for(member in memberList) member.updateVisibility()
        food_kimbap.updateVisibility()
        food_water.updateVisibility()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_activity)

        val musicIntent = Intent(this, MusicService::class.java)
        musicIntent.action = "CHANGE_MUSIC"
        musicIntent.putExtra("MUSIC_RES_ID", R.raw.game_background_music) // 새 배경음악 리소스 ID
        startService(musicIntent)



        member_dameun  = Member(this,"dameun", "담은", findViewById(R.id.member_dameun))
        member_eunju  = Member(this,"eunju", "은주", findViewById(R.id.member_eunju))
        member_sowoon  = Member(this,"sowoon", "소운", findViewById(R.id.member_sowoon))
        member_hyundong = Member(this,"hyundong", "현동", findViewById(R.id.member_hyundong))

        memberList = listOf(member_dameun, member_eunju, member_sowoon, member_hyundong)
        memberListIsIn = mutableListOf(member_dameun, member_eunju, member_sowoon, member_hyundong)

        item_axe = Item(this, "axe", "도끼", findViewById(R.id.item_axe))
        item_book = Item(this, "book", "전공책", findViewById(R.id.item_book))
        item_card = Item(this, "card", "할리갈리", findViewById(R.id.item_card))
        item_drone = Item(this, "drone", "드론", findViewById(R.id.item_drone))
        item_medkit = Item(this, "medkit", "구급상자", findViewById(R.id.item_firstaidkit))
        item_flashlight = Item(this, "flashlight", "손전등",findViewById(R.id.item_flashlight))
        item_gasmask = Item(this, "gasmask", "방독면", findViewById(R.id.item_gasmask))
        item_lock = Item(this, "lock", "자물쇠", findViewById(R.id.item_lock))
        item_map = Item(this, "map", "지도", findViewById(R.id.item_map))
        item_pesticide = Item(this, "pesticide", "살충제", findViewById(R.id.item_pesticide))
        item_phone = Item(this, "phone", "휴대폰", findViewById(R.id.item_phone))
        item_rattle = Item(this, "rattle", "딸랑이", findViewById(R.id.item_rattle))
        item_toolbox = Item(this, "toolbox", "도구상자", findViewById(R.id.item_toolbox))

        itemList = listOf(item_axe, item_book, item_card, item_drone, item_medkit, item_flashlight,
                            item_gasmask, item_lock, item_map, item_pesticide, item_phone, item_rattle, item_toolbox)
        //모든 아이템들에 대한 lose처리
        for(item in itemList) item.loseItem()

        food_kimbap = Food(this, "kimbap", "김밥", findViewById(R.id.food_kimbap))
        food_water = Food(this, "water", "물", findViewById(R.id.food_water))

        eventHandler = EventHandler(this)

        blackBackground = findViewById(R.id.black_background)
        dayText = findViewById(R.id.day_text)

        var date = 1
        /* 1일차에 기본적으로 수행되는 로직들 */
        dayText.setText("${date}일차") //dayText 업데이트
        randomizeItems() // 아이템 랜덤 생성
        updateAllVisibility()

        firstdayfadeout(blackBackground, dayText)

        // finishbutton 클릭 리스너
        val finishbutton : Button = findViewById(R.id.finish_button)
        finishbutton.setOnClickListener {
            date++
            dayText.setText("${date}일차")
            animateScreenTransition(blackBackground, dayText)

            memberDayPase() // 모든 멤버 dayPase
            updateAllVisibility() // 아이템, 멤버, 식량 visivility 업데이트
        }
        val optionButtonInGame: Button = findViewById(R.id.option_ingame)
        optionButtonInGame.setOnClickListener {
            click_sound()
            val intent = Intent(this, OptionDialog::class.java)
            startActivity(intent)
        }
    }
    fun click_sound(){
        val music_intent = Intent(this, MusicService::class.java)
        music_intent.action = "PLAY_CLICK_SOUND"
        startService(music_intent)
    }

    // 1일차에 아이템을 랜덤으로 생성하는 함수
    fun randomizeItems() {
        // 식량 랜덤으로 생성
        food_kimbap.getFoodRandom(3,5)
        food_water.getFoodRandom(3,5)

        // 아이템을 랜덤으로 6개 골라 얻음
        val randomItems = itemList.shuffled().take(6)
        randomItems.forEach { it.getItme() }
    }

    // 일차가 넘어갈 때 페이드인 애니메이션 ?
    fun animateScreenTransition(background: ImageView, dayText: TextView) {
        // 배경의 페이드 인 애니메이션
        ObjectAnimator.ofFloat(background, "alpha", 0f, 1f).apply {
            duration = 1000 // n초 동안
            start()
        }.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // 배경 애니메이션이 끝나면 텍스트의 페이드 인 애니메이션 시작
                ObjectAnimator.ofFloat(dayText, "alpha", 0f, 1f).apply {
                    duration = 500 // 3초 동안
                    start()
                }.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        // 텍스트 애니메이션이 끝나면 2초 동안 유지
                        dayText.postDelayed({
                            // 텍스트의 페이드 아웃 애니메이션
                            ObjectAnimator.ofFloat(dayText, "alpha", 1f, 0f).apply {
                                duration = 2000 // 3초 동안
                                start()
                            }
                            // 배경의 페이드 아웃 애니메이션
                            ObjectAnimator.ofFloat(background, "alpha", 1f, 0f).apply {
                                duration = 2000 // 3초 동안
                                start()
                            }
                        }, 2000) // 2초 동안 대기
                    }
                })
            }
        })
    }

    // 첫째날 페이드인 애니메이션 ?
    fun firstdayfadeout(background: ImageView, dayText: TextView) {
        // 배경의 페이드 인 애니메이션
        ObjectAnimator.ofFloat(background, "alpha", 0f, 1f).apply {
            duration = 0 // n초 동안
            start()
        }.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // 배경 애니메이션이 끝나면 텍스트의 페이드 인 애니메이션 시작
                ObjectAnimator.ofFloat(dayText, "alpha", 0f, 1f).apply {
                    duration = 2000 // 3초 동안
                    start()
                }.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        // 텍스트 애니메이션이 끝나면 2초 게이동안 유지
                        dayText.postDelayed({
                            // 텍스트의 페이드 아웃 애니메이션
                            ObjectAnimator.ofFloat(dayText, "alpha", 1f, 0f).apply {
                                duration = 2000 // 3초 동안
                                start()
                            }
                            // 배경의 페이드 아웃 애니메이션
                            ObjectAnimator.ofFloat(background, "alpha", 1f, 0f).apply {
                                duration = 2000 // 3초 동안
                                start()
                            }
                        }, 2000) // 2초 동안 대기
                    }
                 })
            }
        })
    }
}