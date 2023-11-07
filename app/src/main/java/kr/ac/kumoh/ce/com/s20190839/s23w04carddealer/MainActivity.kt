package kr.ac.kumoh.ce.com.s20190839.s23w04carddealer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kr.ac.kumoh.ce.com.s20190839.s23w04carddealer.databinding.ActivityMainBinding
import java.util.Arrays.sort
import java.util.EnumSet.range
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var main: ActivityMainBinding
    private lateinit var model: CardDealerViewModel

    val shapes = Array(5){ -1 }
    val numbers = Array(5){ -1 }
    var i = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        main = ActivityMainBinding.inflate(layoutInflater)
        setContentView(main.root)

        model = ViewModelProvider(this)[CardDealerViewModel::class.java]

        model.cards.observe(this, Observer {
            val res = IntArray(5)
            for(i in res.indices) {
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
        sort(shapes)
        sort(numbers)
        val numbersSet = setOf(numbers)
        if(shapes[0] == shapes[1] && shapes[1] == shapes[2] && shapes[2] == shapes[3] && shapes[3] == shapes[4] && shapes[4] == shapes[5]) {

        }
    }

}