package com.example.boggle

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Locale
import kotlin.math.max


class MainGameFragment : Fragment() {
    private var score = 0
    private val word = mutableListOf<Char>()
    private val wordBag = mutableListOf<String>()
    private val buttonIds = arrayOf(
        R.id.button11, R.id.button12, R.id.button13, R.id.button14,
        R.id.button21, R.id.button22, R.id.button23, R.id.button24,
        R.id.button31, R.id.button32, R.id.button33, R.id.button34,
        R.id.button41, R.id.button42, R.id.button43, R.id.button44
    )
    private val buttonAvail = arrayOf(
        true, true, true, true,
        true, true, true, true,
        true, true, true, true,
        true, true, true, true
    )
    private val vowels = listOf('A', 'E', 'I', 'O', 'U')
    private val consonants = ('A'..'Z').toList() - vowels
    private val doubleCons = listOf('S', 'Z', 'P', 'X', 'Q' )
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val sharedViewModel: SharedViewModel by activityViewModels()

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.main_game_fragment_layout, container, false)
        val currText = view.findViewById<TextView>(R.id.Word)
        sharedViewModel.refreshLiveData.observe(viewLifecycleOwner) {
            word.clear()
            currText.text = ""
            enableallButtons(view)
            wordBag.clear()
            assignButtons(view)
        }
        val sub = view.findViewById<Button>(R.id.buttonSubmit)
        sub.setOnClickListener{
            calculateScore()
            sharedViewModel.setScore(score)
            word.clear()
            currText.text = ""
            enableallButtons(view)
        }
        for (row in 1..4) {
            for(col in 1..4){
                val button = view.findViewById<Button>(buttonIds[(row-1)*4+col-1])
                Log.d("Main Fragment", "set button $row$col and char is ${button.text.toString()}")
                button.setOnClickListener {
                    Log.d("Main Fragment", "Button$row$col clicked")
                    val newChar = button.text.toString()[0]
                    Log.d("Main Fragment", "Char is ----------- $newChar")
                    word.add(newChar)
                    val currentText = currText.text.toString()
                    val newText = currentText + newChar
                    currText.text = newText
                    buttonAvail[(row-1)*4+col-1] = false
                    button.isEnabled = false
                    button.alpha = 0.5f
                    disableButtons(view, row, col)
                }
            }
        }
        val clear = view.findViewById<Button>(R.id.buttonClear)
        clear.setOnClickListener{
            currText.text = ""
            word.clear()
            enableallButtons(view)
        }
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        assignButtons(view)
    }
    private fun assignButtons(view: View){
        val letters = ('A'..'Z').toList() // List of alphabets from A to Z
        for (buttonId in buttonIds) {
            val button = view.findViewById<Button>(buttonId)
            val randomIndex = (letters.indices).random() // Generate random index
            val randomLetter = letters[randomIndex] // Get random letter
            button.text = randomLetter.toString()
        }
        for(it in buttonAvail.indices){
            buttonAvail[it] = true
        }
    }
    private fun calculateScore(){
        var temp = 0
        var double =  false
        if(word.size < 4){
            score = max(0, score - 10)
            Toast.makeText(context, "Word is too short, -10", Toast.LENGTH_SHORT).show()
        }else if(containlessthan2vowel()){
            score = max(0, score - 10)
            Toast.makeText(context, "Word has less than 2 Vowel", Toast.LENGTH_SHORT).show()
        }else if(wordBag.contains(word.joinToString (""))){
            score = max(0, score - 10)
            Toast.makeText(context, "Word used", Toast.LENGTH_SHORT).show()
            Log.d("Main Fragment", "words are ----------- $wordBag")
        }else{
            var w = word.joinToString("")
            var first = w[0]
            if(first == 'y'){
                first = 'i'
            }
            if(isStringInRawFile(requireContext().applicationContext, w, first)){
                val result = word.map { char ->
                    when (char) {
                        in vowels -> temp += 5
                        in consonants -> if (char in doubleCons){
                            double = true
                            temp += 1
                        }else{
                            temp += 1
                        }
                        else -> throw IllegalArgumentException("$char is not a vowel or consonant")
                    }
                }
                if(double){
                    temp *= 2
                }
                score += temp
                Toast.makeText(context, "That is correct, +$temp", Toast.LENGTH_SHORT).show()
                Log.d("MainGameFragment", "word $w added")
                wordBag.add(w)
                Log.d("MainGameFragment", "wordbag is $wordBag ")
                Log.d("MainGameFragment", "does bag contain word? ${wordBag.contains(w)} ")
            }else{
                score = max(0, score - 10)
                Toast.makeText(context, "That is incorrect, -10", Toast.LENGTH_SHORT).show()
            }

        }
        word.clear()
    }
    private fun isStringInRawFile(context: Context, searchString: String, filename: Char): Boolean {

        Log.d("MainGameFragment", "word $filename found")
        val inputStream = resources.openRawResource(context.resources.getIdentifier(filename.toString().lowercase(), "raw", context.packageName))
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        var line: String?
        bufferedReader.use { bufferedReader ->
            line = bufferedReader.readLine()
            while (line != null) {
                if (line == searchString.lowercase(Locale.getDefault())) {
                    Log.d("MainGameFragment", "word $searchString found")
                    return true
                }
                line = bufferedReader.readLine()
            }
        }

        Log.d("MainGameFragment", "word $searchString not found")

        return false
    }
    private fun disableButtons(view: View, r: Int, c: Int){
        for (row in 1..4) {
            for(col in 1..4) {
                val button = view.findViewById<Button>(buttonIds[(row-1)*4+col-1])
                if(row < r-1 || row > r + 1 || col < c - 1 || col > c + 1 || !buttonAvail[(row-1)*4+col-1]){
                    button.isEnabled = false
                    button.alpha = 0.5f
                }else{
                    button.isEnabled = true
                    button.alpha = 1f
                }
            }
        }
    }
    private fun enableallButtons(view: View){
        for (row in 1..4) {
            for(col in 1..4) {
                val button = view.findViewById<Button>(buttonIds[(row-1)*4+col-1])
                buttonAvail[(row-1)*4+col-1] = true
                button.isEnabled = true
                button.alpha = 1f
            }
        }
    }
    private fun containlessthan2vowel():Boolean{
        var count =0
        val c= word.map { char ->
            when (char) {
                in vowels ->count+=1
                else -> 0
            }
        }
        return count < 2
    }
}