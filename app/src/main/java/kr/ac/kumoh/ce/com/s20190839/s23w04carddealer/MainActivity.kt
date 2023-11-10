package kr.ac.kumoh.ce.com.s20190839.s23w04carddealer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kr.ac.kumoh.ce.com.s20190839.s23w04carddealer.databinding.ActivityMainBinding

data class Card(val shape: Int, val number: Int) // 카드 정보
class MainActivity : AppCompatActivity() {
    private lateinit var main: ActivityMainBinding
    private lateinit var model: CardDealerViewModel

    companion object{ // 족보
        const val LOYAL_STR_FLU = "로열 스트레이트 플러시"
        const val BACK_STR_FLU = "백 스트레이트 플러시"
        const val STR_FLU = "스트레이트 플러시"
        const val FOUR_CARD = "포카드"
        const val FULL_HOUSE = "풀 하우스"
        const val FLU = "플러시"
        const val MOUNTAIN = "마운틴"
        const val BACK_STR = "백 스트레이트"
        const val STR = "스트레이트"
        const val TRI = "트리플"
        const val TWO_P = "투 페어"
        const val ONE_P = "원 페어"
        const val TOP = "탑"
    }

    val cards: Array<Card> = Array(5) { Card(-1, -1)} // 카드 5장 배열
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        main = ActivityMainBinding.inflate(layoutInflater)
        setContentView(main.root)

        model = ViewModelProvider(this)[CardDealerViewModel::class.java]

        model.cards.observe(this, Observer {
            val res = IntArray(5)
            for(i in res.indices) {
                cards[i] = Card(it[i] / 13, it[i] % 13) // 카드 정보 저장
                res[i] = resources.getIdentifier(
                getCardName(it[i]),
                "drawable",
                packageName
            )}
            main.card1.setImageResource(res[0])
            main.card2.setImageResource(res[1])
            main.card3.setImageResource(res[2])
            main.card4.setImageResource(res[3])
            main.card5.setImageResource(res[4])
        })

        main.btnShuffle.setOnClickListener{
            model.shuffle()
            getPoker()
        }
    }

    private fun getCardName(c: Int): String {   // 랜덤 값에 따른 카드 리소스 배정
        if (c == -1) {
            return "c_red_joker"
        }
        var shape = when (c / 13) {
            0 -> "spades"
            1 -> "diamonds"
            2 -> "hearts"
            3 -> "clubs"
            else -> "error"
        }

        val number = when (c % 13) {
            0 -> "ace"
            in 1..9 -> (c % 13 + 1).toString()
            10 -> {
                shape = shape.plus("2")
                "jack"
            }
            11 -> {
                shape = shape.plus("2")
                "queen"
            }
            12 -> {
                shape = shape.plus("2")
                "king"
            }
            else -> "error"
        }

        return "c_${number}_of_${shape}"
    }

    private fun getPoker() {    // 포커 판별 함수
        cards.sortBy{ it.number }

        when {
            isLoyStrFlu() -> {
                main.txtResult.text = LOYAL_STR_FLU
            }
            isBackStrFlu() -> {
                main.txtResult.text = BACK_STR_FLU
            }
            isStrFlu() -> {
                main.txtResult.text = STR_FLU
            }
            isFourCard() -> {
                main.txtResult.text = FOUR_CARD
            }
            isFullHouse() -> {
                main.txtResult.text = FULL_HOUSE
            }
            isFlu() -> {
                main.txtResult.text = FLU
            }
            isMountain() -> {
                main.txtResult.text = MOUNTAIN
            }
            isBackStr() -> {
                main.txtResult.text = BACK_STR
            }
            isStr() -> {
                main.txtResult.text = STR
            }
            isTriple() -> {
                main.txtResult.text = TRI
            }
            isTwoPair() -> {
                main.txtResult.text = TWO_P
            }
            isOnePair() -> {
                main.txtResult.text = ONE_P
            }
            else -> {   // 탑 : 위 족보에 속하지 않은 나머지
                main.txtResult.text = TOP
            }
        }
    }

    private fun isOnePair(): Boolean {  // 원 페어 : 숫자 같은 2장
        val numberCount = cards.groupBy { it.number }.mapValues { it.value.size }

        return numberCount.containsValue(2)
    }

    private fun isTwoPair(): Boolean {  // 투 페어 : 원 페어가 2개 존재
        return cards.groupBy { it.number }.mapValues { it.value.size }.values.count { it == 2 } == 2
    }

    private fun isTriple(): Boolean {   // 트리플 : 숫자 같은 3장
        val numberCount = cards.groupBy { it.number }.mapValues { it.value.size }

        return numberCount.containsValue(3)
    }

    private fun isStr(): Boolean {  // 스트레이트 : 숫자 이어지는 5장
        for(i in 0 until cards.size - 1) {
            if (cards[i].number + 1 != cards[i+1].number) {
                return false
            }
        }
        return true
    }

    private fun isBackStr(): Boolean {  // 백 스트레이트 : A 2 3 4 5
        return cards[0].number == 0 && cards[1].number == 1 &&
                cards[2].number == 2 && cards[3].number == 3 && cards[4].number == 4
    }

    private fun isMountain(): Boolean { // 마운틴 : A K Q J 10
        return cards[0].number == 0 && cards[1].number == 9
                && cards[2].number == 10 && cards[3].number == 11 && cards[4].number == 12
    }

    private fun isFlu(): Boolean {  // 플러시 : 무늬 같은 5장
        return cards.map{it.shape}.toSet().size == 1
    }

    private fun isFullHouse(): Boolean {    // 풀 하우스 : 숫자 같은 3장 + 숫자 같은 2장
        val numberCount = cards.groupBy{it.number}.mapValues { it.value.size }

        return numberCount.containsValue(3) && numberCount.containsValue(2)
    }

    private fun isFourCard(): Boolean {     // 포 카드 : 숫자 같은 4장
        return cards.map{it.number}.toSet().size == 2
    }

    private fun isStrFlu(): Boolean {       // 스트레이트 플러시 : 숫자 이어지고 무늬 같은 5장
        for (i in 0 until cards.size - 1) {
            if(cards[i].number + 1 != cards[i+1].number || cards[i].shape != cards[i+1].shape) {
                return false
            }
        }
        return true
    }

    private fun isBackStrFlu(): Boolean {   // 백 스트레이트 플러시 : 무늬 같은 A 2 3 4 5
        return cards[0].number == 0 && cards[1].number == 1 && cards[2].number == 2
                && cards[3].number == 3 && cards[4].number == 4
                && cards.map{it.shape}.toSet().size == 1
    }

    private fun isLoyStrFlu(): Boolean {    // 로얄 스트레이트 플러시 : 무늬 같은 A K Q J 10
        return cards[0].number == 0 && cards[1].number == 9 && cards[2].number == 10
                && cards[3].number == 11 && cards[4].number == 12
                && cards.map{it.shape}.toSet().size == 1
    }
}