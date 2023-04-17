package com.example.myapplication.demo.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.myapplication.common.base.BaseFragment
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentMainBinding

class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main) {

    private val viewModel: MainFragmentViewModel by viewModels<MainFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textMessage.text = "Fragment Main Binding"
    }
}