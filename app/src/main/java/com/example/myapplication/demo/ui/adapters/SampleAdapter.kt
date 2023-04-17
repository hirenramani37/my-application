package com.example.myapplication.demo.ui.adapters

import android.view.View
import com.example.myapplication.common.base.BaseAdapter
import com.example.myapplication.common.data.network.model.data_class_exmple
import com.example.myapplication.R
import com.example.myapplication.databinding.ListItemSpinnerBinding


class SampleAdapter : BaseAdapter<ListItemSpinnerBinding, data_class_exmple.data_class_exmpleItem>(R.layout.list_item_spinner) {


    override fun setClickableView(binding: ListItemSpinnerBinding): List<View?> =
        listOf(binding.tvItemText)



    override fun onBind(
        binding: ListItemSpinnerBinding,
        position: Int,
        item: data_class_exmple.data_class_exmpleItem,
        payloads: MutableList<Any>?
    ) {
        binding.run {
            tvItemText.text = item.category
        }

    }

}