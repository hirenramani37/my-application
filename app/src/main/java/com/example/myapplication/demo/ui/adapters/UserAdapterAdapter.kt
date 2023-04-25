package com.example.myapplication.demo.ui.adapters

import android.view.View
import com.example.myapplication.common.base.BaseAdapter
import com.example.myapplication.R
import com.example.myapplication.common.data.database.entities.UserLocal
import com.example.myapplication.databinding.ListItemSpinnerBinding


class UserAdapterAdapter : BaseAdapter<ListItemSpinnerBinding, UserLocal>(R.layout.list_item_spinner) {


    override fun setClickableView(binding: ListItemSpinnerBinding): List<View?> =
        listOf(binding.root)



    override fun onBind(
        binding: ListItemSpinnerBinding,
        position: Int,
        item: UserLocal,
        payloads: MutableList<Any>?
    ) {
        binding.run {
           tvAirId.text = item.airId
            tvName.text = item.displayname
            tvAirTrips.text= item.trips.toString()
        }

    }

}