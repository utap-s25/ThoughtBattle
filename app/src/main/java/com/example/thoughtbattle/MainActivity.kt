package com.example.thoughtbattle

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thoughtbattle.data.model.Debate
import com.example.thoughtbattle.data.repository.SendBirdManager
import com.example.thoughtbattle.databinding.ActivityMainBinding
import com.example.thoughtbattle.ui.MainViewModel
import com.example.thoughtbattle.ui.auth.AuthActivity
import com.example.thoughtbattle.ui.debate.DebateChatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sendbird.android.channel.OpenChannel
import com.example.thoughtbattle.ui.main.ChannelAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var channelAdapter: ChannelAdapter
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate() called")

        binding = ActivityMainBinding.inflate(layoutInflater)
        Log.d("MainActivity", "binding inflated")

        setContentView(binding.root)
        Log.d("MainActivity", "setContentView() called")

        auth = Firebase.auth

        val currentUser = auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }

        SendBirdManager.initialize(applicationContext)

        val userId = Firebase.auth.currentUser?.uid ?: run {
            finish()
            return@run
        }

        SendBirdManager.connect(userId.toString(), {
            loadChannels()
        }, { e ->
            Log.e("MainActivity", "SendBird connection error: ${e.message}")
            Toast.makeText(this, "Chat service error: ${e.message}", Toast.LENGTH_SHORT).show()
        })

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        binding.createDebateButton.setOnClickListener {
            showCreateDebateDialog()
        }

        binding.channelsRecyclerView.layoutManager = LinearLayoutManager(this)
        channelAdapter = ChannelAdapter { channel ->
            openChannel(channel)
        }
        binding.channelsRecyclerView.adapter = channelAdapter
        viewModel.channels.observe(this, Observer { channels ->
            channelAdapter.updateChannels(channels)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_home -> {
                val currentUser = auth.currentUser
                if (currentUser == null) {
                    startActivity(Intent(this, AuthActivity::class.java))
                    finish()
                }
                true
            }
            R.id.action_sign_out -> {
                auth.signOut()
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadChannels() {
        viewModel.loadChannels()
    }

    private fun showCreateDebateDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_debate, null)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Create New Debate")
            .setView(dialogView)
            .setPositiveButton("Create") { _, _ ->
                val title = dialogView.findViewById<EditText>(R.id.debateTitleEditText).text.toString()
                val sideA = dialogView.findViewById<EditText>(R.id.sideAEditText).text.toString()
                val sideB = dialogView.findViewById<EditText>(R.id.sideBEditText).text.toString()

                if (title.isNotEmpty() && sideA.isNotEmpty() && sideB.isNotEmpty()) {
                    createDebate(title, sideA, sideB)
                } else {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun createDebate(title: String, sideA: String, sideB: String) {
        SendBirdManager.createOpenChannel(title) { channelUrl ->
            val debate = Debate(
                title = title,
                sideA = sideA,
                sideB = sideB,
                creatorId = Firebase.auth.currentUser?.uid ?: "",
                channelUrl = channelUrl
            )

            Firebase.firestore.collection("debates")
                .add(debate)
                .addOnSuccessListener {
                    loadChannels()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to create debate: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun openChannel(channel: OpenChannel) {
        val intent = Intent(this, DebateChatActivity::class.java).apply {
            putExtra("channel_url", channel.url)
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        SendBirdManager.disconnect()
    }
}