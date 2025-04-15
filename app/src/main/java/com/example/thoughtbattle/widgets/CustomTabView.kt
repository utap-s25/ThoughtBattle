package com.example.thoughtbattle.widgets


import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.example.thoughtbattle.R
import com.example.thoughtbattle.databinding.ViewCustomTabBinding
import com.example.thoughtbattle.extensions.getDrawable
import com.example.thoughtbattle.extensions.setAppearance


/**
 * View displaying icon and badge in tabs.
 */
class CustomTabView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var tintColorRedId = 0
    private val binding: ViewCustomTabBinding

    init {
        val inflater = LayoutInflater.from(getContext())
        binding = ViewCustomTabBinding.inflate(inflater, this, true).apply {

            tintColorRedId =  R.drawable.selector_tab_tint
            val badgeTextAppearance = R.style.SendbirdCaption2Primary300
            val badgeBackgroundRes = R.drawable.shape_badge_background
            val titleTextAppearance = R.style.SendbirdCaption2Primary300
            badge.setAppearance(context, badgeTextAppearance)
            badge.setBackgroundResource(badgeBackgroundRes)
            title.setAppearance(context, titleTextAppearance)
            title.setTextColor(AppCompatResources.getColorStateList(context, tintColorRedId))
        }
    }

    fun setBadgeVisibility(visibility: Int) {
        binding.badge.visibility = visibility
    }

    fun setBadgeCount(countString: String?) {
        binding.badge.text = countString
    }

    fun setIcon(@DrawableRes iconResId: Int) {
        binding.icon.setImageDrawable(context.getDrawable(iconResId, tintColorRedId))
    }

    fun setTitle(title: String?) {
        binding.title.text = title
    }
}