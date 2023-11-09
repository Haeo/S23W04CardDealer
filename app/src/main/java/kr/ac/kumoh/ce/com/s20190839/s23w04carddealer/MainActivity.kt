package kr.ac.kumoh.ce.com.s20190839.s23w04carddealer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kr.ac.kumoh.ce.com.s20190839.s23w04carddealer.databinding.ActivityMainBinding
import kotlin.random.Random

data class Card(val shape: Int, val number: Int)
class MainActivity : AppCompatActivity() {
    private lateinit var main: ActivityMainBinding
    private lateinit var model: CardDealerViewModel

    companion object{
        val LOYAL_STR_FLU = "로열 스트레이트 플러시"
        val BACK_STR_FLU = "백 스트레이트 플러시"
        val STR_FLU = "스트레이트 플러시"
        val FOUR_CARD = "포카드"
        val FULL_HOUSE = "풀 하우스"
        val FLU = "플러시"
        val MOUNTAIN = "마운틴"
        val BACK_STR = "백 스트레이트"
        val STR = "스트레이트"
        val TRI = "트리플"
        val TWO_P = "투 페어"
        val ONE_P = "원 페어"
        val TOP = "탑"
    }

    val cards: Array<Card> = Array(5) { Card(-1, -1)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        main = ActivityMainBinding.inflate(layoutInflater)
        setContentView(main.root)

        model = ViewModelProvider(this)[CardDealerViewModel::class.java]

        model.cards.observe(this, Observer {
            val res = IntArray(5)
            for(i in res.indices) {
                cards[i] = Card(it[i] / 13, it[i] % 13)
                res[i] = resources.getIdentifier(
                getCardName(it[i]),
                "drawable",
                packageName
            )}
            main.card1!!.setImageResource(res[0])
            main.card2!!.setImageResource(res[1])
            main.card3!!.setImageResource(res[2])
            main.card4!!.setImageResource(res[3])
            main.card5!!.setImageResource(res[4])
        })

        main.btnShuffle.setOnClickListener{
            model.shuffle()
            getPoker()
        }

    }

    private fun getCardName(c: Int): String {
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

    private fun getPoker() {
        cards.sortBy{ it.number }

        when {
            isLoyStrFlu() -> main.txtResult.text = LOYAL_STR_FLU
            isBackStrFlu() -> main.txtResult.text = BACK_STR_FLU
            isStrFlu() -> main.txtResult.text = STR_FLU
            isFourCard() -> main.txtResult.text = FOUR_CARD
            isFullHouse() -> main.txtResult.text = FULL_HOUSE
            isFlu() -> main.txtResult.text = FLU
            isMountain() -> main.txtResult.text = MOUNTAIN
            isBackStr() -> main.txtResult.text = BACK_STR
            isStr() -> main.txtResult.text = STR
            isTriple() -> main.txtResult.text = TRI
            isTwoPair() -> main.txtResult.text = TWO_P
            isOnePair() -> main.txtResult.text = ONE_P
            else -> main.txtResult.text = TOP
        }
    }

    private fun isOnePair(): Boolean {
        val numberCount = cards.groupBy { it.number }.mapValues { it.value.size }

        return numberCount.containsValue(2)
    }

    private fun isTwoPair(): Boolean {
        return cards.groupBy { it.number }.mapValues { it.value.size }.values.count { it == 2 } == 2
    }

    private fun isTriple(): Boolean {
        val numberCount = cards.groupBy { it.number }.mapValues { it.value.size }

        return numberCount.containsValue(3)
    }

    private fun isStr(): Boolean {
        for(i in 0 until cards.size - 1) {
            if (cards[i].number + 1 != cards[i+1].number) {
                return false
            }
        }
        return true
    }

    private fun isBackStr(): Boolean {
        return cards[0].number == 0 && cards[1].number == 1 &&
                cards[2].number == 2 && cards[3].number == 3 && cards[4].number == 4
    }

    private fun isMountain(): Boolean {
        return cards[0].number == 0 && cards[1].number == 9
                && cards[2].number == 10 && cards[3].number == 11 && cards[4].number == 12
    }

    private fun isFlu(): Boolean {
        return cards.map{it.shape}.toSet().size == 1
    }

    private fun isFullHouse(): Boolean {
        val numberCount = cards.groupBy{it.number}.mapValues { it.value.size }

        return numberCount.containsValue(3) && numberCount.containsValue(2)
    }

    private fun isFourCard(): Boolean {
        return cards.map{it.number}.toSet().size == 2
    }

    private fun isStrFlu(): Boolean {
        for (i in 0 until cards.size - 1) {
            if(cards[i].number + 1 != cards[i+1].number || cards[i].shape != cards[i+1].shape) {
                return false
            }
        }
        return true
    }

    private fun isBackStrFlu(): Boolean {
        return cards[0].number == 0 && cards[1].number == 1 && cards[2].number == 2
                && cards[3].number == 3 && cards[4].number == 4
                && cards.map{it.shape}.toSet().size == 1
    }

    private fun isLoyStrFlu(): Boolean {
        return cards[0].number == 0 && cards[1].number == 9 && cards[2].number == 10
                && cards[3].number == 11 && cards[4].number == 12
                && cards.map{it.shape}.toSet().size == 1
    }
}