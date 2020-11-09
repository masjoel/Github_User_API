package com.masjoel.githubuserapi

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import org.json.JSONObject

class MainViewModel : ViewModel() {
    val listUser = MutableLiveData<ArrayList<User>>()
    lateinit var context: Context

    fun searchUser(newText: String) {
        val listItems = ArrayList<User>()
        AndroidNetworking.get("https://api.github.com/search/users")
            .addHeaders("Authorization", BuildConfig.GITHUB_TOKEN)
            .addHeaders("User-Agent", "request")
            .addQueryParameter("q", newText)
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        val item = response.getJSONArray("items")
                        for (i in 0 until item.length()) {
                            val user = User()
                            user.username = item.getJSONObject(i).getString("login")
                            user.name = item.getJSONObject(i).getString("login")
                            user.avatar = item.getJSONObject(i).getString("avatar_url")
                            listItems.add(user)
                        }
                        listUser.postValue(listItems)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(anError: ANError) {
                    Toast.makeText(context,anError.errorDetail,Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun getUserGithub(): LiveData<ArrayList<User>> {
        return listUser
    }

}