package com.example.thoughtbattle.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels

import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.thoughtbattle.R
import com.example.thoughtbattle.data.model.User
import com.example.thoughtbattle.data.repository.FirebaseRepository
import com.example.thoughtbattle.data.repository.SendBirdRepository
import com.example.thoughtbattle.databinding.FragmentUserProfileBinding
import com.example.thoughtbattle.ui.MainViewModel
import com.example.thoughtbattle.ui.main.DebateListAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.sendbird.android.ConnectionState
import com.sendbird.android.SendbirdChat
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.channel.query.GroupChannelListQuery
import com.sendbird.android.channel.query.MembershipFilter
import com.sendbird.android.channel.query.MyMemberStateFilter
import com.sendbird.android.channel.query.PublicGroupChannelListQuery
import com.sendbird.android.channel.query.QueryType
import com.sendbird.android.params.GroupChannelListQueryParams
import com.sendbird.android.params.PublicGroupChannelListQueryParams



class UserProfileFragment : Fragment(R.layout.fragment_user_profile) {
    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var profileUserId: String
    private var channelQuery: GroupChannelListQuery? = null
    private lateinit var adapter: DebateListAdapter
    private var isLoading:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
      profileUserId = args?.getString("PROFILE_USER_ID") ?: ""
        Log.d("UserProfileFragment", "profileUserId: $profileUserId")
        viewModel.setUserId(profileUserId)



    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("UserProfileFragment", "onViewCreated called")
        viewModel.setUserId(profileUserId)

        viewModel.loadingState.observe(viewLifecycleOwner) { isLoading ->
            binding.indeterminateBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        Log.d("UserProfileFragment", "Before observeUserProfile")
        viewModel.observeUserProfile().observe(viewLifecycleOwner) { user ->
            Log.d("UserProfileFragment", "observeUserProfile called with user: $user")
            user?.let {
                Log.d("UserProfileFragment", "User: $user")
                binding.username.text = user.username
                Glide.with(this)
                    .load(user.profileImageUrl)
                    .into(binding.profileImage)

                val isCurrentUser = FirebaseAuth.getInstance().currentUser?.uid == user.id
                binding.editProfileButton.visibility = if (isCurrentUser) View.VISIBLE else View.GONE

            }
        }
        Log.d("UserProfileFragment", "After observeUserProfile")

        setupRecyclerView()
        setupClickListeners()
        createNewQuery()
    }

    private fun setupObservers() {
        viewModel.observeUserProfile().observe(viewLifecycleOwner) { user ->
            user?.let {
                Log.d("UserProfileFragment", "User: $user")
                binding.username.setText(user.username)
                Glide.with(this)
                    .load(user.profileImageUrl)

                    .into(binding.profileImage)


                val isCurrentUser = FirebaseAuth.getInstance().currentUser?.uid == user.id
                binding.editProfileButton.visibility = if (isCurrentUser) View.VISIBLE else View.GONE
                //binding.aiRecommendationsCard.visibility = if (isCurrentUser) View.VISIBLE else View.GONE
            }
        }

        viewModel.loadingState.observe(viewLifecycleOwner) { isLoading ->
            // Handle loading state
        }

        viewModel.errorState.observe(viewLifecycleOwner) { error ->
            error?.takeIf { it.isNotEmpty() }?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun setupClickListeners() {
        binding.editProfileButton.setOnClickListener {
            findNavController().navigate(R.id.action_edit_profile)
        }


    }

    private fun setupRecyclerView() {
        adapter = DebateListAdapter().apply {
            setOnItemClickListener { channel ->
                handleChannelClick(channel)
            }
        }

        binding.debateHistoryList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@UserProfileFragment.adapter

        }
        createNewQuery()
        loadPublicChannels()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun handleChannelClick(channel: GroupChannel) {
        channel.join { e ->
            activity?.runOnUiThread {
                if (e != null) {
                    Toast.makeText(context, "Join failed: ${e.message}", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.updateUserDebateHistory(channel)
                    val bundle = Bundle()
                    bundle.putString("KEY_CHANNEL_URL", channel.url)
                    findNavController().navigate(R.id.debateFragment, bundle)
                }
            }
        }
    }
    private fun createNewQuery() {
        val params= GroupChannelListQueryParams ( myMemberStateFilter = MyMemberStateFilter.ALL,
        includeEmpty = true,
        )

        params.setUserIdsIncludeFilter(listOf(profileUserId), QueryType.AND)
        channelQuery = GroupChannel.createMyGroupChannelListQuery(params)
    }

    private fun loadPublicChannels() {

        channelQuery?.let { query ->
            if (query.isLoading) return


            query.next { channels, e ->
                activity?.runOnUiThread {


                    e?.let {
                        Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                        return@runOnUiThread
                    }

                    channels?.let {
                        if (query.hasNext) {
                            adapter.addChannels(it)
                        } else {
                            adapter.setChannels(it)
                        }

                    }
                }
            }
        }
    }

}