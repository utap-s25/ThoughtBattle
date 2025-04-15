package com.example.thoughtbattle.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.example.thoughtbattle.R
import com.example.thoughtbattle.databinding.ViewCustomMenuIconButtonBinding
import com.example.thoughtbattle.ui.main.createDebate.CreateDebateActivity
import com.sendbird.uikit.fragments.OpenChannelListFragment

/**
 * Displays an open channel list screen used for community.
 */
class DebateListFragment : OpenChannelListFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.debate_list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val createMenuItem = menu.findItem(R.id.action_create_channel)
        val binding = ViewCustomMenuIconButtonBinding.inflate(layoutInflater)

        if (context == null) return

        binding.icon.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.icon_create,
                null
            )
        )
        binding.icon.setBackgroundResource(
            R.drawable.sb_button_uncontained_background_light
        )
        binding.root.setOnClickListener { onOptionsItemSelected(createMenuItem) }
        createMenuItem.actionView = binding.root
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_create_channel && context != null) {
            val intent = Intent(context, CreateDebateActivity::class.java)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
    }
}