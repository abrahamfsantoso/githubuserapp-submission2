package com.dicoding.practice.submission2.view

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.practice.submission2.R
import com.dicoding.practice.submission2.databinding.ActivityMainBinding
import com.dicoding.practice.submission2.model.User
import com.dicoding.practice.submission2.viewmodel.MainViewModel
import com.dicoding.practice.submission2.viewmodel.UserAdapter


class MainActivity : AppCompatActivity() {
    private var listDataUser: ArrayList<User> = ArrayList()
    private lateinit var listAdapter: UserAdapter
    private lateinit var mainViewModel: MainViewModel

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listAdapter = UserAdapter(listDataUser)
        mainViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(MainViewModel::class.java)

        viewConfig()
        runGetDataGit()
        configMainViewModel(listAdapter)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu?.findItem(R.id.search)?.actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.find_username)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            /*
            Gunakan method ini ketika search selesai atau OK
             */
            override fun onQueryTextSubmit(query: String): Boolean {

                return false
            }

            /*
            Gunakan method ini untuk merespon tiap perubahan huruf pada searchView
             */
            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty()) {
                    listDataUser.clear()
                    viewConfig()
                    showLoading(true)
                    Handler(Looper.getMainLooper()).postDelayed({
                        listDataUser.clear()
                        viewConfig()
                        mainViewModel.getDataGitSearch(newText, applicationContext)
                        configMainViewModel(listAdapter)
                    }, 1000L)

                } else {
                    return true
                }
                return true
            }
        })

        val item = menu.findItem(R.id.app_bar_share)
        if (item != null) {
            item.isVisible = false
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.change_language) {
            val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(mIntent)
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun viewConfig() {
        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        binding.rvUsers.setHasFixedSize(true)

        listAdapter.notifyDataSetChanged()
        binding.rvUsers.adapter = listAdapter

    }


    private fun runGetDataGit() {
        mainViewModel.getDataGit(applicationContext)
        showLoading(true)
    }

    private fun configMainViewModel(adapter: UserAdapter) {
        mainViewModel.getListUsers().observe(this, { listUsers ->
            if (listUsers != null) {
                adapter.setData(listUsers)
                showLoading(false)
            }
        })
    }


    private fun showLoading(state: Boolean) {
        if (state) {
            binding.loadingProgress.visibility = View.VISIBLE
        } else {
            binding.loadingProgress.visibility = View.INVISIBLE
        }
    }

    companion object {
        val TAG = MainActivity::class.java.simpleName

    }
}