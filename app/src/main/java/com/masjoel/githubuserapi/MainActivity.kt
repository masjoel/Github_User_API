package com.masjoel.githubuserapi

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.androidnetworking.interfaces.JSONObjectRequestListener
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*


class MainActivity: AppCompatActivity() {
    private val listItems: ArrayList<User> = arrayListOf()
    private lateinit var adapter: ListUserAdapter
    private lateinit var mainViewModel: MainViewModel
    companion object{
        private const val ARG_CEK = "arg_cek"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvUser.setHasFixedSize(true)
        adapter = ListUserAdapter(listItems)
        adapter.notifyDataSetChanged()
        rvUser.layoutManager = LinearLayoutManager(this)
        rvUser.adapter = adapter
        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(MainViewModel::class.java)
        searchData()
        mainViewModel.getUserGithub().observe(this@MainActivity, Observer { userGit ->
            if (userGit != null) {
                adapter.setData(userGit)
                showList(userGit)
                showLoading(false)
            }
        })
        val cek = intent.getStringExtra(ARG_CEK).toString()
        if (cek.isEmpty() || cek=="null") getUserGit()

    }

    private fun searchData() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                showLoading(true)
                val charText = query.toLowerCase(Locale.getDefault())

                if (charText.isNotEmpty() && charText != "null") {
                    intent.putExtra(ARG_CEK, query)
                    mainViewModel.searchUser(query)
                }
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        searchView.setOnCloseListener(object : SearchView.OnCloseListener,
            android.widget.SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                listItems.clear()
                getUserGit()
                return false
            }
        })


    }

    private fun getUserGit() {
        val baseUrl = "https://api.github.com/users"
        AndroidNetworking.get(baseUrl)
            .addHeaders("Authorization", BuildConfig.GITHUB_TOKEN)
            .addHeaders("User-Agent", "request")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONArray(object : JSONArrayRequestListener {
                override fun onResponse(response: JSONArray) {
                    showLoading(false)
                    try {
                        for (i in 0 until response.length()) {
                            val item = response.getJSONObject(i)
                            val iName = item.getString("login")
                            readProfile(iName)
                        }
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                }
                override fun onError(anError: ANError) {
                    showLoading(false)
                    Toast.makeText(this@MainActivity, anError.errorDetail, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun readProfile(username: String) {
        AndroidNetworking.get("https://api.github.com/users/$username")
            .addHeaders("Authorization", "token 2703a57dd4f2de47e58bb8603caf985bd3c6ef0e")
            .addHeaders("User-Agent", "request")
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        val user = User()
                        user.name = response.getString("name")
                        user.username = response.getString("login")
                        user.avatar = response.getString("avatar_url")
                        listItems.add(user)
                        showList(listItems)
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                }
                override fun onError(anError: ANError) {}
            })
    }

    private fun showList(arrayList: ArrayList<User>) {
        rvUser.layoutManager = LinearLayoutManager(this)
        val adapter = ListUserAdapter(arrayList)
        rvUser.adapter = adapter
        adapter.notifyDataSetChanged()
        adapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                showSelectedUser(data)
            }
        })
    }

    private fun showSelectedUser(siUser: User) {
        val selectedUser: User = siUser
        val detailActivity = Intent(this@MainActivity, DetailUserActivity::class.java)
        detailActivity.putExtra(DetailUserActivity.EXTRA_USER, selectedUser)
        startActivity(detailActivity)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_change_settings -> {
                val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(mIntent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun showLoading(state: Boolean) {
        if (state) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }
}
