package com.osisupermoses.quizapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.Typeface.BOLD
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import com.osisupermoses.quizapp.databinding.ActivityQuizQuestionsBinding
import com.osisupermoses.quizapp.databinding.DialogCustomBackConfirmationBinding


class QuizQuestionsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityQuizQuestionsBinding
    private var doubleBackToExitPressedOnce = false

    private var countDownTimer: CountDownTimer? = null
    private var timerProgress = 0
    private var timerDuration: Long = 10


    private lateinit var pB: ProgressBar
    private lateinit var tvProgress: TextView
    private lateinit var tvQuestion: TextView
    private lateinit var image: ImageView
    private lateinit var tvOptionOne: TextView
    private lateinit var tvOptionTwo: TextView
    private lateinit var tvOptionThree: TextView
    private lateinit var tvOptionFour: TextView
    private lateinit var btnSubmit: Button

    private var mQuestionList: ArrayList<Question>? = null
    private var mCurrentPosition = 1

    private var mSelectedOptionPosition: Int = 0
    private var mCorrectAnswers: Int = 0
    private var mUsername: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizQuestionsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        pB = binding.progressBar
        tvProgress = binding.tvProgress
        tvQuestion = binding.tvQuestion
        image = binding.ivImage
        tvOptionOne = binding.tvOptionOne
        tvOptionTwo = binding.tvOptionTwo
        tvOptionThree = binding.tvOptionThree
        tvOptionFour = binding.tvOptionFour
        btnSubmit = binding.btnSubmit

        mUsername = intent.getStringExtra(Constants.USER_NAME)

        mQuestionList = Constants.getQuestions()

        setQuestion()

        tvOptionOne.setOnClickListener(this)
        tvOptionTwo.setOnClickListener(this)
        tvOptionThree.setOnClickListener(this)
        tvOptionFour.setOnClickListener(this)
//        btnSubmit.setOnClickListener (this)
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_option_one -> {
                selectedOptionView(tvOptionOne,1)
            }
            R.id.tv_option_two -> {
                selectedOptionView(tvOptionTwo,2)
            }
            R.id.tv_option_three -> {
                selectedOptionView(tvOptionThree,3)
            }
            R.id.tv_option_four -> {
                selectedOptionView(tvOptionFour,4)
            }
            R.id.btn_submit -> {
                if (mSelectedOptionPosition == 0) {
                    when {
                        mCurrentPosition <= mQuestionList!!.size -> {
                            setQuestion()
                            optionClickEnabled()
                        }
                        else -> {
                            val intent = Intent(this, ResultActivity::class.java)
                            intent.putExtra(Constants.USER_NAME, mUsername)
                            intent.putExtra(Constants.CORRECT_ANSWERS, mCorrectAnswers)
                            intent.putExtra(Constants.TOTAL_QUESTIONS, mQuestionList!!.size)
                            startActivity(intent)
                        }
                    }
                } else {
                    val question = mQuestionList!![mCurrentPosition - 1]
                    if (question.correctAnswer != mSelectedOptionPosition) {
                        answerView(mSelectedOptionPosition, R.drawable.wrong_option_border_bg)
                        Toast.makeText(this,
                            "Oops!", Toast.LENGTH_SHORT).show()
                    } else {
                        mCorrectAnswers++
                        Toast.makeText(this,
                            "Correct!", Toast.LENGTH_SHORT).show()
                    }
                    answerView(question.correctAnswer, R.drawable.correct_option_border_bg)

                    optionClickDisabled()

                    if (mCurrentPosition == mQuestionList!!.size) {
                        optionClickEnabled()
                        btnSubmit.text = "FINISH"
                        pauseTimer()
                    } else {
                        btnSubmit.text = "GO TO NEXT QUESTION"
                        pauseTimer()
                    }
                    mSelectedOptionPosition = 0
                    mCurrentPosition++
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setQuestion() {
        val question = mQuestionList!![mCurrentPosition - 1]

        defaultOptionsView()

        if (mCurrentPosition == mQuestionList!!.size && mSelectedOptionPosition != 0) {
            btnSubmit.text = "FINISH"
        } else {
            btnSubmit.text = "SUBMIT"
        }

        pB.progress = mCurrentPosition
        tvProgress.text = "$mCurrentPosition/${pB.max}"
        tvQuestion.text = question.question
        image.setImageResource(question.image)
        tvOptionOne.text = question.optionOne
        tvOptionTwo.text = question.optionTwo
        tvOptionThree.text = question.optionThree
        tvOptionFour.text = question.optionFour

        if (countDownTimer != null) {
                countDownTimer?.cancel()
                timerProgress = 0
        }

        setTimerProgressBar()
    }

    private fun defaultOptionsView() {
        val options = ArrayList<TextView>()
        options.add(0,tvOptionOne)
        options.add(1,tvOptionTwo)
        options.add(2,tvOptionThree)
        options.add(3,tvOptionFour)

        options.forEach {
            it.setTextColor(Color.parseColor("#788089"))
            it.typeface = Typeface.DEFAULT
            it.background = ContextCompat.getDrawable(
                this, R.drawable.default_option_border_bg
            )
        }
    }

    private fun selectedOptionView(tv: TextView, selectedOptionNum: Int) {
        defaultOptionsView()

        mSelectedOptionPosition = selectedOptionNum

        tv.setTextColor(Color.parseColor("#363A43"))
        tv.setTypeface(tv.typeface, BOLD)
        tv.background = ContextCompat.getDrawable(
            this, R.drawable.selected_option_border_bg
        )
    }

    private fun answerView(answer: Int, drawableView: Int) {
        when(answer) {
            1 -> {
                tvOptionOne.background = ContextCompat.getDrawable(this, drawableView)
            }
            2 -> {
                tvOptionTwo.background = ContextCompat.getDrawable(this, drawableView)
            }
            3 -> {
                tvOptionThree.background = ContextCompat.getDrawable(this, drawableView)
            }
            4 -> {
                tvOptionFour.background = ContextCompat.getDrawable(this, drawableView)
            }
        }
    }

    private fun optionClickDisabled() {
        tvOptionOne.isClickable = false
        tvOptionTwo.isClickable = false
        tvOptionThree.isClickable = false
        tvOptionFour.isClickable = false

    }

    private fun optionClickEnabled() {
        tvOptionOne.isClickable = true
        tvOptionTwo.isClickable = true
        tvOptionThree.isClickable = true
        tvOptionFour.isClickable = true

    }

    private fun setTimerProgressBar() {
        binding.timerProgressBar.progress = timerProgress
        countDownTimer = object : CountDownTimer(
            timerDuration * 1000, 1000
        ) {
            override fun onTick(millisUntilFinished: Long) {
                if (mSelectedOptionPosition == 0) {
                    btnSubmit.setOnClickListener {
                        Toast.makeText(
                            this@QuizQuestionsActivity,
                            "Please select an option.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    btnSubmit.setOnClickListener(this@QuizQuestionsActivity)
                }

                timerProgress++
                binding.timerProgressBar.progress = 10 - timerProgress
                binding.tvTimer.text = (10 - timerProgress).toString()
            }

            override fun onFinish() {
                if (mCurrentPosition < mQuestionList!!.size) {
                    mCurrentPosition++

                    setQuestion()
                    optionClickEnabled()

                } else {
                    finish()
                    val intent = Intent(
                        this@QuizQuestionsActivity, ResultActivity::class.java
                    )
                    intent.putExtra(Constants.USER_NAME, mUsername)
                    intent.putExtra(Constants.CORRECT_ANSWERS, mCorrectAnswers)
                    intent.putExtra(Constants.TOTAL_QUESTIONS, mQuestionList!!.size)
                    startActivity(intent)
                }
            }
        }.start()
    }

    private fun pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer?.cancel()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (countDownTimer != null) {

            countDownTimer?.cancel()
            timerProgress = 0
        }
    }

    private fun customDialogForBackButton() {
        val customDialog = Dialog(this)
        val dialogBinding = DialogCustomBackConfirmationBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)
        dialogBinding.btnYes.setOnClickListener {
            this@QuizQuestionsActivity.finish()
            customDialog.dismiss()
        }
        dialogBinding.btnNo.setOnClickListener {
            customDialog.dismiss()
        }

        customDialog.show()
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            customDialogForBackButton()
        }

        this.doubleBackToExitPressedOnce = true
        val question = mQuestionList!![mCurrentPosition - 1]
        if (question.id == 1) {
            Toast.makeText(
                this,
                resources.getString(R.string.please_click_back_again_to_exit),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this,
                resources.getString(R.string.you_cant_go_back),
                Toast.LENGTH_SHORT
            ).show()
        }

        Handler(Looper.getMainLooper()).postDelayed(
            { doubleBackToExitPressedOnce = false }, 2000)
    }
}