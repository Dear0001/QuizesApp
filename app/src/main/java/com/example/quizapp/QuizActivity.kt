package com.example.quizapp

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.quizapp.databinding.ActivityQuizBinding
import com.example.quizapp.databinding.ScoreDialogBinding

class QuizActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        var questionModelList: List<QuestionModel> = listOf()
        var time: String = ""
    }

    lateinit var binding: ActivityQuizBinding

    var currentQuestionIndex = 0
    var selectedAnswer = ""
    var score = 0
    var quizQuestions: MutableList<QuestionModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            btn0.setOnClickListener(this@QuizActivity)
            btn1.setOnClickListener(this@QuizActivity)
            btn2.setOnClickListener(this@QuizActivity)
            btn3.setOnClickListener(this@QuizActivity)
            nextBtn.setOnClickListener(this@QuizActivity)
        }

        prepareQuiz()
        startTimer()
    }

    private fun prepareQuiz() {
        // Shuffle the questions and pick the first 5
        quizQuestions = questionModelList.shuffled().take(5).toMutableList()
        loadQuestions()
    }

    private fun startTimer() {
        val totalTimeInMillis = time.toInt() * 60 * 1000L
        object : CountDownTimer(totalTimeInMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val minutes = seconds / 60
                val remainingSeconds = seconds % 60
                binding.timerIndicatorTextview.text = String.format("%02d:%02d", minutes, remainingSeconds)
            }

            override fun onFinish() {
                finishQuiz()
            }
        }.start()
    }

    private fun loadQuestions() {
        selectedAnswer = ""
        if (currentQuestionIndex == quizQuestions.size) {
            finishQuiz()
            return
        }

        binding.apply {
            questionIndicatorTextview.text = "Question ${currentQuestionIndex + 1} / ${quizQuestions.size}"
            questionProgressIndicator.progress =
                ((currentQuestionIndex.toFloat() / quizQuestions.size.toFloat()) * 100).toInt()
            val currentQuestion = quizQuestions[currentQuestionIndex]
            questionTextview.text = currentQuestion.question
            btn0.text = currentQuestion.options[0]
            btn1.text = currentQuestion.options[1]
            btn2.text = currentQuestion.options[2]
            btn3.text = currentQuestion.options[3]
        }
    }

    override fun onClick(view: View?) {
        binding.apply {
            btn0.setBackgroundColor(getColor(R.color.gray))
            btn1.setBackgroundColor(getColor(R.color.gray))
            btn2.setBackgroundColor(getColor(R.color.gray))
            btn3.setBackgroundColor(getColor(R.color.gray))
        }

        val clickedBtn = view as Button
        if (clickedBtn.id == R.id.next_btn) {
            // Next button is clicked
            if (selectedAnswer.isEmpty()) {
                Toast.makeText(applicationContext, "Please select an answer to continue", Toast.LENGTH_SHORT).show()
                return
            }
            if (selectedAnswer == quizQuestions[currentQuestionIndex].correct) {
                score++
                Log.i("Quiz Score", "Current score: $score")
            }
            currentQuestionIndex++
            loadQuestions()
        } else {
            // Options button is clicked
            selectedAnswer = clickedBtn.text.toString()
            clickedBtn.setBackgroundColor(getColor(R.color.orange))
        }
    }

    private fun finishQuiz() {
        val totalQuestions = quizQuestions.size // Always 5 since we're selecting 5 random questions
        val percentage = ((score.toFloat() / totalQuestions.toFloat()) * 100).toInt() // Calculate percentage
        val result = if (score >= 3) "Win" else "Loss" // Win if score >= 3

        // Bind the results to the dialog
        val dialogBinding = ScoreDialogBinding.inflate(layoutInflater)
        dialogBinding.apply {
            scoreProgressIndicator.progress = percentage // Update progress bar with percentage
            scoreProgressText.text = "$percentage%" // Display percentage text
            scoreTitle.text = if (score >= 3) "Congrats! You Win!" else "Sorry! You Lose!" // Dynamic result message
            scoreTitle.setTextColor(if (score >= 3) Color.BLUE else Color.RED) // Color based on result
            scoreSubtitle.text = "$score out of $totalQuestions are correct" // Display score
            finishBtn.setOnClickListener {
                finish() // Close the activity when done
            }
        }

        // Show the result dialog
        AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .show()
    }

}
















