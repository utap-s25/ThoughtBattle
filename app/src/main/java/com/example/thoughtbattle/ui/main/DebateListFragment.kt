package com.example.thoughtbattle.ui.main

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thoughtbattle.MainActivity
import com.example.thoughtbattle.R
import com.example.thoughtbattle.data.model.User
import com.example.thoughtbattle.data.model.invalidUser
import com.example.thoughtbattle.databinding.FragmentGroupChannelListBinding
import com.example.thoughtbattle.ui.MainViewModel
import com.example.thoughtbattle.ui.auth.AuthUser
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.oAuthProvider
import com.sendbird.android.ConnectionState
import com.sendbird.android.SendbirdChat
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.channel.query.MembershipFilter
import com.sendbird.android.channel.query.PublicGroupChannelListQuery
import com.sendbird.android.params.PublicGroupChannelListQueryParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DebateListFragment : Fragment(R.layout.fragment_group_channel_list) {
    private lateinit var binding: FragmentGroupChannelListBinding
    private lateinit var adapter: DebateListAdapter
    private val viewModel: MainViewModel by activityViewModels()
    private var publicChannelQuery: PublicGroupChannelListQuery? = null
    private var debateTopic: String = ""
    private lateinit var menuHost: MenuHost

    private lateinit var authUser: User
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGroupChannelListBinding.bind(view)
        menuHost = requireActivity()
        binding.indeterminateBar.visibility = View.VISIBLE



        setupRecyclerView()
        setupRefreshLayout()
        initDebateTopics()
        viewModel.observeAuthUser().observe(viewLifecycleOwner) {
            user ->
            authUser = user
            SendbirdChat.connect(FirebaseAuth.getInstance().currentUser!!.uid) { _, e ->
            }
            loadInitialChannels()
            binding.indeterminateBar.visibility = View.GONE
        }
        refreshChannels()




    }




    override fun onResume() {
        super.onResume()
        binding.indeterminateBar.visibility = View.VISIBLE

        viewModel.observeAuthUser().observe(viewLifecycleOwner) {

            SendbirdChat.connect(FirebaseAuth.getInstance().currentUser!!.uid){ _, e ->
            }
            loadPublicChannels()

        }



    }

    private fun initDebateTopics() {
        binding.debateTopics.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedChip = group.findViewById<Chip>(checkedIds[0])
            val selectedTopic = selectedChip.text.toString()
            if (selectedTopic != "All") {
                debateTopic = selectedTopic
                createNewQuery()
                loadPublicChannels()
            } else {
                debateTopic = ""
                createNewQuery()
                loadPublicChannels()
            }


        }
    }

    private fun setupRecyclerView() {
        adapter = DebateListAdapter().apply {
            setOnItemClickListener { channel ->
                handleChannelClick(channel)
            }
        }

        binding.recyclerGroupChannels.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@DebateListFragment.adapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (!recyclerView.canScrollVertically(1)) {
                        loadMoreChannels()
                    }
                }
            })
        }
    }

    private fun setupRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshChannels()
        }
    }


    private fun attemptReconnect() {
        val prefs = requireContext().getSharedPreferences("sendbird", 0)
        val userId = prefs.getString("user_id", null) ?: run {
            Toast.makeText(context, "Not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        SendbirdChat.connect(userId) { user, e ->
            activity?.runOnUiThread {
                if (e != null) {
                    Toast.makeText(context, "Connection failed: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    loadInitialChannels()
                }
            }
        }
    }

    private fun loadInitialChannels() {
        if (publicChannelQuery == null) {
            createNewQuery()
            loadPublicChannels()
        }
    }

    private fun refreshChannels() {
        binding.swipeRefreshLayout.isRefreshing = true
        adapter.clearChannels()
        publicChannelQuery = null
        createNewQuery()
        loadPublicChannels()
    }

    private fun createNewQuery() {
        if (debateTopic== "All"){
            publicChannelQuery = GroupChannel.createPublicGroupChannelListQuery(
                PublicGroupChannelListQueryParams().apply {
                    membershipFilter = MembershipFilter.ALL
                    includeEmpty = true
                    customTypesFilter= listOf("History", "Politics", "Science", "Technology", "Sports", "Finance", "Environment", "Pop Culture", "Gaming", "Arts", "Media", "Health", "Religion", "Conspiracy", "Entertainment", "Social", "Education", "Film and Books")
                })
        }else {
            publicChannelQuery = GroupChannel.createPublicGroupChannelListQuery(
                PublicGroupChannelListQueryParams().apply {
                    customTypesFilter = listOf(debateTopic)
                    membershipFilter = MembershipFilter.ALL
                    includeEmpty = true
                }
            )
        }
    }

    private fun loadPublicChannels() {
        if (SendbirdChat.connectionState != ConnectionState.OPEN) {
            binding.swipeRefreshLayout.isRefreshing = false

            return
        }

        publicChannelQuery?.let { query ->
            if (query.isLoading) return

            binding.swipeRefreshLayout.isRefreshing = true
            query.next { channels, e ->
                activity?.runOnUiThread {
                    binding.swipeRefreshLayout.isRefreshing = false

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

    private fun loadMoreChannels() {
        publicChannelQuery?.let { query ->
            if (query.hasNext && !query.isLoading) {
                loadPublicChannels()
            }
        }
    }


    private fun handleChannelClick(channel: GroupChannel) {
        channel.join { e ->
            activity?.runOnUiThread {
                if (e != null) {
                    Toast.makeText(context, "Join failed: ${e.message}", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.logChannelJoinEvent(channel.url, channel.name, viewModel.currentAuthUser.value!!.id, channel.customType!!)
                    viewModel.updateUserDebateHistory(channel)

                    navigateToChannel(channel.url)
                }
            }
        }
    }

    private fun navigateToChannel(channelUrl: String) {

        findNavController().navigate(
            R.id.action_home_to_debate_chat,
            Bundle().apply { putString("KEY_CHANNEL_URL", channelUrl) }
        )
    }
}













