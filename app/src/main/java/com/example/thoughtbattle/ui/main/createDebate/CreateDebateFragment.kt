package com.example.thoughtbattle.ui.main.createDebate

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.thoughtbattle.R
import com.example.thoughtbattle.data.model.Debate
import com.example.thoughtbattle.databinding.FragmentCreateDebateBinding
import com.example.thoughtbattle.ui.MainViewModel
import com.sendbird.uikit.activities.OpenChannelActivity
import kotlinx.coroutines.launch


class CreateDebateFragment : Fragment(R.layout.fragment_create_debate) {
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var binding: FragmentCreateDebateBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCreateDebateBinding.bind(requireView())
        initSubmitButton()
    }

    private fun initSubmitButton() {
        binding.debateSubmitButton.setOnClickListener {
            val title = binding.titleET.text.toString()
            if (title.isEmpty()) {
                Toast.makeText(activity, getString(R.string.title_cannot_be_empty), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val sideA = binding.sideAEditText.text.toString()
            if (sideA.isEmpty()) {
                Toast.makeText(activity, getString(R.string.side_a_cannot_be_empty), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val sideB = binding.sideBEditText.text.toString()
            if (sideB.isEmpty()) {
                Toast.makeText(activity, getString(R.string.side_b_cannot_be_empty), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.createnewDebate(
                    title, sideA, sideB,
                    onSuccess = { debate ->

                        OpenChannelActivity.newIntent(context, debate.channelUrl)
                    },
                    onError = { errorMessage ->
                        Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}