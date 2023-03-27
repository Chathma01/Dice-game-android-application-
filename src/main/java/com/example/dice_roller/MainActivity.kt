package com.example.dice_roller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    companion object {
        const val WINNING_SCORE = "com.example.application.example.WINNING_SCORE" //provide a constant value
    }
    private lateinit var newGameBtn:Button
    private lateinit var aboutBtn:Button //button variables

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.main_new)


        newGameBtn= findViewById(R.id.newGame_btn)
        aboutBtn= findViewById(R.id.about_btn)


        newGameBtn.setOnClickListener {
            setWinningScorePopup()


        }
            aboutBtn.setOnClickListener {
                showAboutWindow()
            }

    }

//display about details window when the About button is clicked
    private fun showAboutWindow() {
        val scorePopupView = layoutInflater.inflate(R.layout.about_pop_up, null)

        // Create a PopupWindow object
        val scorePopupWindow = PopupWindow(
            scorePopupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        scorePopupWindow.showAtLocation(newGameBtn, Gravity.CENTER, 0, 0)

        val closeButton = scorePopupView.findViewById<Button>(R.id.cancel_button)

        closeButton.setOnClickListener {
            scorePopupWindow.dismiss()              // Dismiss the popup window
        }
    }
//display the window to change the winning score when the new game button is clicked
    private fun setWinningScorePopup(){
        val scorePopupView = layoutInflater.inflate(R.layout.score_set_popup_window, null)

        // Create a PopupWindow object
        val scorePopupWindow = PopupWindow(
            scorePopupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        scorePopupWindow.showAtLocation(newGameBtn, Gravity.CENTER, 0, 0)

        // Get references to the views in the popup window
        val closeButton = scorePopupView.findViewById<Button>(R.id.cancel_button)
        val startGameButton = scorePopupView.findViewById<Button>(R.id.start_game)

        closeButton.setOnClickListener { // Set a click listener for the close button
            scorePopupWindow.dismiss()              // Dismiss the popup window
        }

        startGameButton.setOnClickListener {
            val scoreText = Integer.parseInt(scorePopupView.findViewById<TextInputEditText>(R.id.current_game_score).text.toString())
            val intent = Intent(this, NewGame::class.java)
            intent.putExtra(WINNING_SCORE, scoreText)
            startActivity(intent)
        }
    }


}