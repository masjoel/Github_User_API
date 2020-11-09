package com.masjoel.githubuserapi

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.google.android.material.appbar.CollapsingToolbarLayout
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_detail_user.*
import kotlinx.android.synthetic.main.content_scrolling.*
import org.json.JSONObject

class DetailUserActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_USER = "extra_user"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_user)
        setSupportActionBar(findViewById(R.id.toolbar))
        val userGit = intent.getParcelableExtra(EXTRA_USER) as? User
        userGit?.username?.let { readProfile(it) }

        findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).title = userGit?.name
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        sectionsPagerAdapter.username = userGit?.username
        view_pager.adapter = sectionsPagerAdapter
        tabs.setupWithViewPager(view_pager)
        supportActionBar?.elevation = 0f

        val tvName: TextView = findViewById(R.id.tvName)
        val tvUsername: TextView = findViewById(R.id.tvUsername)
        val tvLocation: TextView = findViewById(R.id.tvLocation)
        val tvCompany: TextView = findViewById(R.id.tvCompany)
        val tvFollower: TextView = findViewById(R.id.tvFollower)
        val tvFollowing: TextView = findViewById(R.id.tvFollowing)
        val tvRepository: TextView = findViewById(R.id.tvRepository)

        tvName.text = userGit?.name
        tvUsername.text = userGit?.username
        tvLocation.text = userGit?.uLocation
        tvCompany.text = userGit?.uCompany
        tvFollower.text = userGit?.uFollowers
        tvFollowing.text = userGit?.uFollowing
        tvRepository.text = userGit?.uRepository
        Glide.with(this)
            .load(userGit?.avatar)
            .apply(RequestOptions().override(55, 55))
            .into(imgPhoto)
    }

    private fun readProfile(username: String) {
        AndroidNetworking.get("https://api.github.com/users/$username")
            .addHeaders("Authorization", BuildConfig.GITHUB_TOKEN)
            .addHeaders("User-Agent", "request")
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        tvName.text = response.getString("name")
                        tvUsername.text = response.getString("login")
                        tvCompany.text = response.getString("company")
                        tvLocation.text = response.getString("location")
                        tvRepository.text = response.getString("public_repos")
                        tvFollower.text = response.getString("followers")
                        tvFollowing.text = response.getString("following")
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                }
                override fun onError(anError: ANError) {}
            })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_detail_user, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_home -> {
                val backToHome = Intent(this, MainActivity::class.java)
                startActivity(backToHome)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}