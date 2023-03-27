package com.example.dice_roller


class Player {
    var totalScore: Int = 0

    //get list of integers, add them
    fun addScore (diceValues : MutableList<Int>)
    {
        var totalDieScore = 0
        //Log.d("dicevalues",diceValues.toString())
        totalDieScore = diceValues.sum()
        //Log.d("tot",totalDieScore.toString())
        this.totalScore += totalDieScore
        //Log.d("tot2",totalScore.toString())
    }

}