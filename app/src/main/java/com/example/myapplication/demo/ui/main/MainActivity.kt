package com.example.myapplication.demo.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.common.base.BaseActivity
import com.example.myapplication.common.data.database.entities.UserLocal
import com.example.myapplication.common.utils.EventBus
import com.example.myapplication.demo.ui.adapters.UserAdapterAdapter
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var userAdapter: UserAdapterAdapter
   private val userLocal: List<UserLocal?>? = null
   // private var currentPage = 1
    private var lastPage: Int = 0

    // lateinit var data: MainData
    var isLoadMore: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initEventBus()
        setUpUi()
        setUpObserver()

        if((pref.currentPage ?: 1) <= 1){
            // currentPage = 1
            pref.currentPage = 1
            viewModel.callApi(pref.currentPage?:1,10)
           // currentPage++
            //pref.currentPage = currentPage
        }else{
          //  viewModel.callApi(pref.currentPage?:1,10)
        }



        userAdapter = UserAdapterAdapter()
            binding.rvTasks.adapter = userAdapter

        initAdapter()
    }


    private fun initAdapter() {
        binding.rvTasks.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == userAdapter.itemCount - 1) {
            //        if (isLoadMore) {
                        viewModel.callApi(pref.currentPage?:1,10)
              //      }
                }
            }
        })
    }

    private fun initEventBus() {
        EventBus.subscribe<Intent>(listSubscription) {

        }

        EventBus.subscribe<Int>(listSubscription) {

        }
    }

    private fun setUpUi() {
        //binding.textMessage.text = "Activity Main Binding"
//        binding.textMessage.setOnClickListener {
//            viewModel.insertUser(UserLocal().apply {
//                displayname = "hiren"
//                trips = "hiren"
//                airId = "hiren"
//            })
//        }

    }

    private fun setUpObserver() {
        viewModel.apiErrors.observe(this) { handleError(it) }
        viewModel.appLoader.observe(this) { updateLoaderUI(it) }
        viewModel.userInfoError.observe(this) { it.printStackTrace() }

        viewModel.userInfo.observe(this) {
           // viewModel.deleteUser(UserLocal())
            runOnUiThread {
             Timber.e("list","${it.data.size}")

                if (isLoadMore) {
                    it.data.forEach { apiUser ->
                        viewModel.insertUser(UserLocal().apply {
                            airId = apiUser._id
                            displayname = apiUser.name
                            trips = apiUser.trips

                        },apiUser)
                    }
                    pref.currentPage = pref.currentPage!! + 1
                    //pref.currentPage = currentPage
                    userAdapter.notifyDataSetChanged()
                } else {

                    lastPage = it.totalPages ?: 0
                    pref.currentPage = pref.currentPage!! + 1
                  //  pref.currentPage = currentPage
                    it.data.forEach { apiUser ->
                        viewModel.insertUser(UserLocal().apply {
                            airId = apiUser._id
                            displayname = apiUser.name
                            trips = apiUser.trips

                        },apiUser)
                    }
                }
                isLoadMore = (pref.currentPage?:1) <= lastPage



            }
        }

        viewModel.localUser?.observe(this) {
            Timber.e("Local Users: ${it.size}")
            userAdapter.addAll(it)

        }
    }
}