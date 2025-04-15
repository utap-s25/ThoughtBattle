package com.example.thoughtbattle.ui.main.createDebate

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.thoughtbattle.R
import com.example.thoughtbattle.data.model.Debate
import com.example.thoughtbattle.databinding.ActivityCreateDebateBinding
import com.example.thoughtbattle.ui.MainViewModel
import com.sendbird.uikit.activities.OpenChannelActivity

class CreateDebateActivity: AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityCreateDebateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateDebateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initSubmitButton()

    }


    private fun initSubmitButton() {
        binding. debateSubmitButton.setOnClickListener {
            val title = binding.titleET.text.toString()
            if (title.isEmpty()) {
                Toast.makeText(this, getString(R.string.title_cannot_be_empty), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val sideA = binding.sideAEditText.text.toString()
            if (sideA.isEmpty()) {
                Toast.makeText(this, getString(R.string.side_a_cannot_be_empty), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val sideB = binding.sideBEditText.text.toString()
            if (sideB.isEmpty()) {
                Toast.makeText(this, getString(R.string.side_b_cannot_be_empty), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.createnewDebate(
                title,
                sideA,
                sideB,
                onSuccess = { debate:Debate ->
                    // Channel created successfully
                    Log.d("CreateDebateActivity", "Channel created successfully: ${debate.channelUrl}")
                    val intent = OpenChannelActivity.newIntent(
                        this@CreateDebateActivity,
                        OpenChannelActivity::class.java,
                        debate.channelUrl
                    )
                    startActivity(intent)
                    finish()
                },
                onError = { e: Exception ->
                    // Handle channel creation failure
                    Toast.makeText(this, "Failed to create channel: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

}