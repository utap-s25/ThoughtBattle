package com.example.thoughtbattle.ui.main

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thoughtbattle.R
import com.example.thoughtbattle.data.model.invalidUser
import com.example.thoughtbattle.databinding.FragmentGroupChannelListBinding
import com.example.thoughtbattle.ui.MainViewModel
import com.example.thoughtbattle.ui.auth.AuthUser
import com.google.android.material.chip.Chip
import com.google.firebase.auth.oAuthProvider
import com.sendbird.android.ConnectionState
import com.sendbird.android.SendbirdChat
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.channel.query.MembershipFilter
import com.sendbird.android.channel.query.PublicGroupChannelListQuery
import com.sendbird.android.params.PublicGroupChannelListQueryParams

class DebateListFragment : Fragment(R.layout.fragment_group_channel_list) {
    private lateinit var binding: FragmentGroupChannelListBinding
    private lateinit var adapter: DebateListAdapter
    private val viewModel: MainViewModel by activityViewModels()
    private var publicChannelQuery: PublicGroupChannelListQuery? = null
    private var debateTopic: String = ""
private lateinit var authUser: AuthUser
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGroupChannelListBinding.bind(view)

        setupRecyclerView()
        setupRefreshLayout()
        initDebateTopics()

    }

    override fun onResume() {
        super.onResume()
        viewModel.observerUserAfterLogin().observe(viewLifecycleOwner) {
            if (it != invalidUser) {
                SendbirdChat.connect(it.id) { user, e ->
                    if (e != null) {
                        Toast.makeText(
                            context,
                            "Connection failed: ${e.message}",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        loadInitialChannels()

                    }
                    refreshChannels()
                }

            }
        }
        binding.  indeterminateBar.visibility = View.GONE


    }

   private fun initDebateTopics(){
       binding.debateTopics.setOnCheckedStateChangeListener { group, checkedIds ->
           val selectedChip = group.findViewById<Chip>(checkedIds[0])
           val selectedTopic = selectedChip.text.toString()
           if(selectedTopic!="All") {
               debateTopic = selectedTopic
               createNewQuery()
               loadPublicChannels()
           }else{
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
                    Toast.makeText(context, "Connection failed: ${e.message}", Toast.LENGTH_SHORT).show()
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
        publicChannelQuery = GroupChannel.createPublicGroupChannelListQuery(
            PublicGroupChannelListQueryParams().apply {
                customTypesFilter=listOf(debateTopic)
                membershipFilter = MembershipFilter.ALL
                includeEmpty = true
            }
        )
    }

    private fun loadPublicChannels() {
        if (SendbirdChat.connectionState != ConnectionState.OPEN) {
            binding.swipeRefreshLayout.isRefreshing = false
            Toast.makeText(context, "Not connected to chat", Toast.LENGTH_SHORT).show()
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













