package com.example.boggle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.findFragment

class ScoreFragment : Fragment() {
    private var refresh= false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val sharedViewModel: SharedViewModel by activityViewModels()
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.score_fragment_layout, container, false)
        val textView = view.findViewById<TextView>(R.id.text_view)
        sharedViewModel.scoreLiveDate.observe(viewLifecycleOwner) { score ->
            textView.text = "Score:$score"
        }
        val newGame = view.findViewById<Button>(R.id.button)
        newGame.setOnClickListener {
            sharedViewModel.setNewGame(!refresh)
        }
        return view
    }
}