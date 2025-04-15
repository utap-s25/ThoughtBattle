package com.example.thoughtbattle.ui.main


import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.thoughtbattle.R
import com.example.thoughtbattle.databinding.ActivityHomeBinding
import com.example.thoughtbattle.extensions.setInsetMargin
import com.example.thoughtbattle.widgets.CustomTabView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sendbird.android.params.OpenChannelListQueryParams
import com.sendbird.uikit.SendbirdUIKit
import com.sendbird.uikit.fragments.OpenChannelListFragment

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val themeResId = SendbirdUIKit.getDefaultThemeMode().resId
        setTheme(themeResId)
        ActivityHomeBinding.inflate(layoutInflater).apply {
            setContentView(root)
            val context = this@HomeActivity

            background.setBackgroundResource( R.color.background_50)
            titleBar.setBackgroundResource( R.color.background_50)
            titleBar.setTitleTextColor(
                ResourcesCompat.getColor(
                    resources,  R.color.onlight_text_high_emphasis, null
                )
            )
            description.setTextColor(
                ResourcesCompat.getColor(
                    resources, R.color.onlight_text_mid_emphasis, null
                )
            )
            setSupportActionBar(titleBar)
            root.setInsetMargin(window)
            viewPager.adapter = MainAdapter(this@HomeActivity)
            val backgroundRedId =  R.color.background_50
            tabLayout.setBackgroundResource(backgroundRedId)

            TabLayoutMediator(tabLayout, viewPager) { tab: TabLayout.Tab, position: Int ->
                when (position) {
                    TAB_ACTIVE -> tab.customView = CustomTabView(context).apply {
                        setBadgeVisibility(View.GONE)
                        setTitle(getString(R.string.text_active))
                        setIcon(R.drawable.ic_heat)
                    }
                    TAB_NEW -> tab.customView = CustomTabView(context).apply {

                        setTitle(getString(R.string.text_new))
                        setIcon(R.drawable.ic_new)
                    }

                }
            }.attach()
            description.visibility = View.VISIBLE
            description.setText(R.string.text_active_description)
            supportActionBar?.setTitle(R.string.text_active)
            viewPager.offscreenPageLimit = 3
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                /**
                 * This method will be invoked when a new page becomes selected. Animation is not
                 * necessarily complete.
                 *
                 * @param position Position index of the new selected page.
                 */
                override fun onPageSelected(position: Int) {
                    when (position) {
                        TAB_ACTIVE -> {
                            description.visibility = View.VISIBLE
                            description.setText(R.string.text_active_description)
                            supportActionBar?.setTitle(R.string.text_active)
                        }
                        TAB_NEW -> {

                            supportActionBar?.setTitle(R.string.text_new)
                            description.visibility = View.VISIBLE
                            description.setText(R.string.text_new_description)
                        }

                    }
                }
            })
        }
    }

    companion object {
        private const val TAB_ACTIVE = 0
        private const val TAB_NEW = 1
    }

    private class MainAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {


                    OpenChannelListFragment.Builder()
                        .setCustomFragment(DebateListFragment())
                        .setUseHeader(false)
                        .setUseRefreshLayout(false)
                        .build()
                }

                else -> {
                    // lol we may want to change this to user profile? idk but sendbird doesnt not let us order chats by dates or anything...
                    val params = OpenChannelListQueryParams()
                                        OpenChannelListFragment.Builder()
                        .setCustomFragment(DebateListFragment())
                        .setUseHeader(false)
                        .setCustomQueryParams(params)
                        .build()
                }


            }
        }

        /**
         * Returns the total number of items in the data set held by the adapter.
         *
         * @return The total number of items in this adapter.
         */
        override fun getItemCount(): Int {
            return PAGE_SIZE
        }

        companion object {
            private const val PAGE_SIZE = 2
        }
    }
}