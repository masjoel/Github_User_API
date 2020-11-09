package com.masjoel.githubuserapi

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray

class FollowingFragment : Fragment() {
    companion object {
        private const val ARG_USERNAME = "username"
        fun newInstance(username: String?): FollowingFragment{
            val fragment = FollowingFragment()
            val bundle = Bundle()
            bundle.putString(ARG_USERNAME, username)
            fragment.arguments = bundle
            return fragment
        }
        private lateinit var rvFollowing: RecyclerView
        private val list: ArrayList<User> = arrayListOf()
        private var adapter: ListUserAdapter? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_following, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val username = arguments?.getString(ARG_USERNAME)
        rvFollowing = view.findViewById(R.id.rvFollowing)
        rvFollowing.setHasFixedSize(true)
        rvFollowing.layoutManager = LinearLayoutManager(activity)
        adapter = ListUserAdapter(list)
        rvFollowing.adapter = adapter
        getFollower(username)
    }

    private fun getFollower(username: String?) {
        AndroidNetworking.get("https://api.github.com/users/$username/following")
            .addHeaders("Authorization", BuildConfig.GITHUB_TOKEN)
            .addHeaders("User-Agent", "request")
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONArray(object : JSONArrayRequestListener {
                override fun onResponse(response: JSONArray) {
                    showLoading(false)
                    try {
                        list.clear()
                        for (i in 0 until response.length()) {
                            val item = response.getJSONObject(i)
                            val user = User()
                            user.username = item.getString("login")
                            user.name = item.getString("login")
                            user.avatar = item.getString("avatar_url")
                            list.add(user)
                        }
                        showList()
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                }
                override fun onError(anError: ANError) {}
            })
    }

    private fun showList() {
        rvFollowing.layoutManager = LinearLayoutManager(activity)
        val adapter = ListUserAdapter(list)
        rvFollowing.adapter = adapter
        adapter.notifyDataSetChanged()
        adapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                showSelectedUser(data)
            }
        })
    }

    private fun showSelectedUser(siUser: User) {
        val selectedUser: User = siUser
        val detailActivity = Intent(activity, DetailUserActivity::class.java)
        detailActivity.putExtra(DetailUserActivity.EXTRA_USER, selectedUser)
        startActivity(detailActivity)
    }

    fun showLoading(state: Boolean) {
        if (state) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }
}