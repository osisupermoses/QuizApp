package com.osisupermoses.quizapp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.osisupermoses.quizapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var doubleBackToExitPressedOnce = false

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val startButton = binding.btnStart
        val nameEditText = binding.etName

        startButton.setOnClickListener {

            if (nameEditText.text.toString().isEmpty()) {

                Toast.makeText(this, "Please enter your name.", Toast.LENGTH_SHORT)
                    .show()
            } else {

                val intent = Intent(this, QuizQuestionsActivity::class.java)
                // (Pass the name through intent using the constant variable which we have created.)
                intent.putExtra(Constants.USER_NAME, nameEditText.text.toString())
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true

        Toast.makeText(
            this,
            resources.getString(R.string.please_click_back_again_to_exit),
            Toast.LENGTH_SHORT
        ).show()

        Handler(Looper.getMainLooper()).postDelayed(
            { doubleBackToExitPressedOnce = false }, 2000)
    }
}