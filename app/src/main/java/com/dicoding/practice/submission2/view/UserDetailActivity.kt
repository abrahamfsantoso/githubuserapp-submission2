package com.dicoding.practice.submission2.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import com.bumptech.glide.Glide
import com.dicoding.practice.submission2.R
import com.dicoding.practice.submission2.databinding.ActivityUserDetailBinding
import com.dicoding.practice.submission2.model.User
import com.dicoding.practice.submission2.viewmodel.ViewPagerDetailAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class UserDetailActivity : AppCompatActivity() {

    private lateinit var nameUser: String
    private lateinit var content: String
    private lateinit var binding: ActivityUserDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (supportActionBar != null) {
            (supportActionBar as ActionBar).title = "Detail User"
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setData()
        viewPagerConfig()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        if (menu != null) {
            val item = menu.findItem(R.id.search)
            item.isVisible = false
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_share -> {
                val shareUser = "Github User:\n$nameUser\n$content"
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareUser)
                shareIntent.type = "text/html"
                startActivity(Intent.createChooser(shareIntent, "Share using"))
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }

        if (item.itemId == R.id.change_language) {
            val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(mIntent)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun viewPagerConfig() {
        val viewPagerDetail = ViewPagerDetailAdapter(this)
        binding.viewPager.adapter = viewPagerDetail
        val tabs: TabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(tabs, binding.viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        supportActionBar?.elevation = 0f
    }


    private fun setData() {
        showLoading(true)

        Handler(Looper.getMainLooper()).postDelayed({
            showLoading(false)
            val user = intent.getParcelableExtra<User>(EXTRA_USER) as User
            val image = user.avatar
            nameUser = user.name.toString()
            content = "${user.username.toString()}\n" +
                    "${user.company.toString()}\n" +
                    "${user.location.toString()}\n" +
                    "${user.repository.toString()}\n" +
                    "${user.followers.toString()}\n" +
                    user.following.toString()
            Glide.with(this).load(image).into(binding.ivAvatarReceived)
            binding.tvNameReceived.text = nameUser
            binding.tvObjectReceived.text = content
        }, 1000L)

    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.loadingProgressUserDetail.visibility = View.VISIBLE
        } else {
            binding.loadingProgressUserDetail.visibility = View.INVISIBLE
        }
    }

    companion object {
        const val EXTRA_USER = "extra_user"

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )
    }

}