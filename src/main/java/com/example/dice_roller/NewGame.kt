/**
Justification of the computer player stratergy:
    The computer strategy focus on re-roll according to the dice value. As the human player's dice rolls are not visible for the computer, this strategy target on getting the maximum value from a throw.
     -> The computer player avoids scoring dice values containing 1,2,3 to surpass the average values from a throw.
            * Re rolls are targeted to get a minimum score of 17.5 which is the total average of five dice.
            * To optimize the re-rolls the computer player will ignore values which are less than or equal to three.
     -> Furthermore the computer player takes the human players total score into account to catch up with the human player if it's far behind and to keep up the lead.


 **/


package com.example.dice_roller

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import kotlin.random.Random


class NewGame : AppCompatActivity() {

    //setting variables
    private lateinit var throwButton: Button
    private lateinit var scoreButton: Button
    private lateinit var setScore:Button
    private lateinit var selectMode:ToggleButton
    private var userSetScore = 101
    private var humanPlayer = Player()
    private var computerPlayer = Player()
    private lateinit var displayHumanScore: TextView
    private lateinit var displayComputerScore: TextView
    private lateinit var displayHumanWins: TextView
    private lateinit var displayComputerWins: TextView
    private var computerWins = 0
    private lateinit var humanDiceImages: List<ImageView>
    private lateinit var computerDiceImages: List<ImageView>
    private var currentHumanDice: MutableList<Int> = mutableListOf()
    private var humanScore: MutableList<Int> = mutableListOf()
    private var computerScore: MutableList<Int> = mutableListOf()
    private var currentComputerDice: MutableList<Int> = mutableListOf()
    private var humanNotSelected: MutableList<ImageView> = mutableListOf()
    private var computerNotSelected: MutableList<ImageView> = mutableListOf()
    private var humanRollCount = 0
    private var computerRollCount = 0
    private var checked = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.new_game)
        userSetScore = intent.getIntExtra(MainActivity.WINNING_SCORE, 0)
        findViewById<TextView>(R.id.winingScore).text = userSetScore.toString()

        //Retrieve the total number of wins for the computer and human players from shared preferences.
        //The total number of wins are stored as key-value pairs with keys "COMPUTER-SCORE" and "HUMAN-SCORE", respectively.
        val totalWins = getPreferences(Context.MODE_PRIVATE) ?: return
        findViewById<TextView>(R.id.computer_wins).setText(totalWins.getInt("COMPUTER-SCORE", 0).toString())
        findViewById<TextView>(R.id.human_wins).setText(totalWins.getInt("HUMAN-SCORE", 0).toString())

        //creating a list of human player dice
        humanDiceImages = listOf(
            findViewById(R.id.hd_1),
            findViewById(R.id.hd_2),
            findViewById(R.id.hd_3),
            findViewById(R.id.hd_4),
            findViewById(R.id.hd_5)

        )
        //creating a list of computer player dice
        computerDiceImages = listOf(
            findViewById(R.id.cd_1),
            findViewById(R.id.cd_2),
            findViewById(R.id.cd_3),
            findViewById(R.id.cd_4),
            findViewById(R.id.cd_5)

            )
        //

        scoreButton = findViewById(R.id.score_button)
        throwButton = findViewById(R.id.throw_button)

        displayHumanScore = findViewById(R.id.human_score)
        displayComputerScore = findViewById(R.id.computer_score)
        displayHumanWins = findViewById(R.id.human_wins)
        displayComputerWins = findViewById(R.id.computer_wins)
        selectMode = findViewById(R.id.toggleButton)

        //a switch button for the user to choose the game mode
        selectMode.setOnCheckedChangeListener { _, isChecked ->
            var msg = ""
            if (isChecked) {msg = "You are on hard mode"; checked = false}else { msg = "You are on easy mode"}
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            if(checked){
                selectMode.isEnabled = false
            }
        }

        //a button for the user to start throwing and dice
        throwButton.setOnClickListener{
            onThrow()
            if(checked){
                Log.d("Toggle","IS WORKING")
                randomStrategy()
                selectMode.isEnabled = false
            }
            else{
                Log.d("Toggle","IS WORKING HARD MODE" )
                selectMode.isEnabled = false
                computerTurn()
            }
            //humanPlayer.totalScore = 41
            //humanPlayer.totalScore = 41
            checkWinner()

        }

        //a for loop to make the human images vies clickable
        for(i in 0..4){
            humanDiceImages[i].setOnClickListener{
                if(humanRollCount != 0){
                    humanDiceImages[i].isSelected = !humanDiceImages[i].isSelected
                    humanDiceImages[i].setBackgroundColor(Color.MAGENTA)
                }
                else{
                    humanDiceImages[i].setBackgroundColor(Color.TRANSPARENT)
                }
            }
        }
        //a button to input the score of the players and check the winners
       scoreButton.setOnClickListener{
           if(humanRollCount > 0) {
               calculateHumanScore()
               calculateComputerScore()
               checkWinner()
           }
        }

    }

    //get the score of all the rolls of the human
    private fun calculateHumanScore() {
        for(imageView in humanDiceImages){
            val imageNo = resources.getResourceName((imageView.tag as Int)).toString().split("_") //get the dice gotten by the human
            val score = imageNo[3].toInt()
            humanScore.add(score)//add to array

        }
        humanPlayer.addScore(humanScore)  //passing to player class to get the total
        displayHumanScore.text = humanPlayer.totalScore.toString() //display the human sore on the screen

        humanScore.clear()

        humanRollCount = 0
    }

    //get the score of all the rolls of the human
    private fun calculateComputerScore(){
       if (computerRollCount <3){          //ensure that the computer rolls the dice three times per turn and selecting the
                                            // appropriate algorithm based on the game mode.
            while (computerRollCount < 3){
                //Thread.sleep(1000)
                if(checked){
                    randomSecondThird()
                }
                else{
                    comSecondThird()
                }

                computerRollCount++
            }
        }
        //get the current dice score
        for(imageView in computerDiceImages){
            val imageNo = resources.getResourceName((imageView.tag as Int)).toString().split("_")
            val score = imageNo[3].toInt()
            computerScore.add(score)

        }

        computerPlayer.addScore(computerScore)
        displayComputerScore.text = computerPlayer.totalScore.toString()

       computerRollCount = 0
        computerScore.clear()

    }

    //controls the computer player's rolls and scoring logic in a dice game,
    // ensuring that the computer rolls the dice three times per turn and selects the appropriate algorithm based on the game mode.
   private fun computerTurn(){
       when (computerRollCount){
           0-> {
               Log.d("comRoll0", "inside")
               currentComputerDice = rollDice(computerNotSelected.size)
               updateDiceViews(computerNotSelected, currentComputerDice)

               computerRollCount++
               currentComputerDice.clear()
               computerNotSelected.clear()
           }
           1 -> {
               Log.d("comRoll1", "inside")
               computerRollCount++
               Log.d("count", computerRollCount.toString())
               comSecondThird()
           }
           2 ->{
               Log.d("comRoll2", "inside")
               comSecondThird()
               computerRollCount = 0
               calculateComputerScore()

           }
       }
    }
// handle the computer player's second and third rolls in a dice game, specifically when the computer has at least one die with a score of 1, 2, or 3.
    private fun comSecondThird() {
        var ReRoll = false
        for (imageView in computerDiceImages) { //retrieves the score of the corresponding die using its tag value
            val parts = resources.getResourceName((imageView.tag as Int)).toString().split("_")
            currentComputerDice.add(parts[3].toInt()) //retrieves the current scores of the computer's dice and adds them
        }
        Log.d("currentComScoresList",currentComputerDice.toString())

        if (1 in currentComputerDice || 2 in currentComputerDice || 3 in currentComputerDice){//list contains any of the values 1, 2, or 3.
                                                                                                // If it does, a boolean variable comReRoll is set to true
            ReRoll = true
        }
        Log.d("bool", ReRoll.toString())
        if(ReRoll){
            for(i in currentComputerDice.indices){
                if (currentComputerDice[i] == 1 || currentComputerDice[i] == 2 || currentComputerDice[i] == 3){
                    computerNotSelected.add(computerDiceImages[i])  //adds the image views of dice that have a score of 1, 2, or 3
                }
            }
            currentHumanDice = rollDice(computerNotSelected.size) // rolls the dice for the computer's next roll
            updateDiceViews(computerNotSelected, currentHumanDice) // updates the views for the computer's dice

            computerNotSelected.clear()
        }
        currentComputerDice.clear()
        //computerRollCount++
    }
//handle the human player's dice rolls in a dice game,
    private fun onThrow() {
        when(humanRollCount){
            0 -> {
                humanNotSelected.addAll(humanDiceImages)
                computerNotSelected.addAll(computerDiceImages)

                currentHumanDice = rollDice(humanNotSelected.size)
                updateDiceViews(humanNotSelected,currentHumanDice)

                humanRollCount ++
                humanNotSelected.clear()
            }
            1 -> {
                for(i in humanDiceImages.indices) { //iterate over each image view object in the list
                    if(!humanDiceImages[i].isSelected) {
                        humanNotSelected.add(humanDiceImages[i]) //adds the image views for any non-selected dice to the list

                    }
                    else{
                            humanDiceImages[i].setBackgroundColor(Color.TRANSPARENT) //sets the background color of any selected dice to transparent
                            humanDiceImages[i].isSelected= !humanDiceImages[i].isSelected

                    }
                }
                currentHumanDice = rollDice(humanNotSelected.size)
                updateDiceViews(humanNotSelected, currentHumanDice) // updates the views for the human player's dice

                humanRollCount ++
                humanNotSelected.clear()

            }
            2 -> {
                for(i in humanDiceImages.indices) {
                    if(!humanDiceImages[i].isSelected) {
                        humanNotSelected.add(humanDiceImages[i])
                    }
                    else{
                        humanDiceImages[i].setBackgroundColor(Color.TRANSPARENT)
                        humanDiceImages[i].isSelected= !humanDiceImages[i].isSelected
                    }
                }
                currentHumanDice = rollDice(humanNotSelected.size)
                updateDiceViews(humanNotSelected, currentHumanDice)

                humanRollCount=0
                humanNotSelected.clear()
                calculateHumanScore()

            }
        }
    }

//updates the views for the player's dice
    private fun updateDiceViews(playerNotSelected: MutableList<ImageView>, currentPlayerDice: MutableList<Int>) {
        for (i in 0 until playerNotSelected.size){
            playerNotSelected[i].setImageResource(currentPlayerDice[i])
            playerNotSelected[i].tag = currentPlayerDice[i]

        }
    }
//generate random dice rolls for the unselected dice
    private fun rollDice(playerNotSelected:Int): MutableList<Int> {

        var rolls: MutableList<Int> = mutableListOf()
        for (i in 0 until playerNotSelected){
            val diceImage = when((1..5).random()){
                1 -> R.drawable.die_face_1
                2 -> R.drawable.die_face_2
                3 -> R.drawable.die_face_3
                4 -> R.drawable.die_face_4
                5 -> R.drawable.die_face_5
                else -> R.drawable.die_face_6

            }
            rolls.add(i,diceImage)

        }
        return rolls
    }
//a random dice rolling strategy for the computer player in a dice game,
// with a chance to roll the dice for a second and third time based on a randomly generated boolean value.
    private fun randomStrategy(){

        when(computerRollCount){
            0 -> {
                Log.d("comRoll", "1")
                currentComputerDice = rollDice(computerNotSelected.size)
                updateDiceViews(computerNotSelected, currentComputerDice)
                computerRollCount++
            }
            1 -> {
                Log.d("comRoll", "2")
                var choose = Random.nextBoolean()
                Log.d("count", computerRollCount.toString())
                if(choose){
                    randomSecondThird()
                }
                computerRollCount++
            }
            2 -> {
                Log.d("comRoll", "1")
                var choose = Random.nextBoolean()
                Log.d("count", computerRollCount.toString())
                if(choose){
                    randomSecondThird()
                }
            calculateComputerScore()
            }
        }


    }
    //random selection algorithm for the computer player's second and third rolls in a dice game,
    // with a chance to select a die to keep for the current roll based on a randomly generated integer.
    private fun randomSecondThird(){
        var tempArray = mutableListOf<Int>()
        var comReRoll = Random.nextBoolean()
        //comReRoll = (0 until 2).random()
        for (imageView in computerDiceImages) {
            val parts = resources.getResourceName((imageView.tag as Int)).toString().split("_")
            //currentComputerDice.add(parts[3].toInt())
            tempArray.add(parts[3].toInt())
            //Log.d("PARTS",parts.toString())
        }
        //Log.d("currentComScoresList",tempArray.toString())

        if(comReRoll){
            var shouldSelect = Random.nextBoolean()
            for(i in tempArray.indices){
                if (shouldSelect){
                    computerNotSelected.add(computerDiceImages[i])
                }
            }
            tempArray = rollDice(computerNotSelected.size)
            updateDiceViews(computerNotSelected, tempArray)

            computerNotSelected.clear()
        }
        tempArray.clear()
    }
    //display the window when the human player wins
    private fun winPopupWindow(){
        val winPopupView = layoutInflater.inflate(R.layout.win_popup_window, null)

        val popupWindow = PopupWindow(
            winPopupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )


        popupWindow.showAtLocation(winPopupView, Gravity.CENTER, 0, 0)
    }
    //display the window when the human player loses
    private fun losePopupWindow(){
        val losePopupView = layoutInflater.inflate(R.layout.lose_popup_window, null)
        val popupWindow = PopupWindow(
            losePopupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        // Show the popup window
        popupWindow.showAtLocation(losePopupView, Gravity.CENTER, 0, 0)
    }
    //display the window when the game is a tie
    private fun tiePopupWindow(){
        val tiePopupView = layoutInflater.inflate(R.layout.tie_popup_window, null)
        val popupWindow = PopupWindow(
            tiePopupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        // Show the popup window
        popupWindow.showAtLocation(tiePopupView, Gravity.CENTER, 0, 0)
    }
    //disable the buttons when the game is over
    private fun gameOver(){
        throwButton.isEnabled = false
        scoreButton.isEnabled = false
        selectMode.isEnabled = false

    }
    //checks the winner
    private fun checkWinner(){
        if(humanPlayer.totalScore >= userSetScore){
            if(humanPlayer.totalScore > computerPlayer.totalScore){
                storeWins(0,1)
                winPopupWindow()
                gameOver()

            }
            else if(humanPlayer.totalScore == computerPlayer.totalScore){
                tiePopupWindow() //set the players for rolls after the tie
                humanRollCount = 2
                computerRollCount = 2

            }
            else{
                computerWins++
                storeWins(1,0)
                losePopupWindow()
                gameOver()

            }
        }
        else if(computerPlayer.totalScore >= userSetScore){
            computerWins++
            storeWins(1,0)
            losePopupWindow()
            gameOver()

        }


    }
//store the total score for both the computer player and the human player in a shared preferences file
    private fun storeWins(computerPoint:Int,humanPoint:Int){
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            val currentComputerPoint = sharedPref.getInt("COMPUTER-SCORE", 0)
            val currentHumanPoint = sharedPref.getInt("HUMAN-SCORE", 0)
            putInt("COMPUTER-SCORE",currentComputerPoint+computerPoint)
            putInt("HUMAN-SCORE",currentHumanPoint+humanPoint)
            apply()
        }
    }

}
