//package com.massttr.provider.ui.main.notification
//
//import android.os.Bundle
//import android.view.View
//import androidx.appcompat.app.AppCompatDelegate
//import androidx.fragment.app.viewModels
//import com.example.myapplication.common.base.BaseFragment
//import com.common.stickyheader.StickyHeaderDecoration
//import com.demo.exmple.NotificationAdapter
//import com.massttr.provider.R
//import com.massttr.provider.chat.FixerSocketService
//import com.massttr.provider.databinding.FragmentNotificationBinding
//import com.massttr.provider.ui.main.availableTasks.viewTask.ViewTaskAcceptActivity
//import com.massttr.provider.ui.main.myprofiles.manages.documents.DocumentsActivity
//import com.massttr.user.utils.AVAILABLE_FIXDETAIL
//import com.massttr.user.utils.EventBus
//import com.massttr.user.utils.setAdapter
//import kotlinx.coroutines.DelicateCoroutinesApi
//import kotlinx.coroutines.ObsoleteCoroutinesApi
//
//@DelicateCoroutinesApi
//@ObsoleteCoroutinesApi
//class NotificationFragment :
//    BaseFragment<FragmentNotificationBinding>(R.layout.fragment_notification) {
//    private val viewModel: NotificationViewModel by viewModels()
//    private lateinit var notificationAdapter: NotificationAdapter
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        initView()
//        setObserver()
//        onClickListener()
//    }
//
//    private fun setObserver() {
//        viewModel.run {
//            appLoader.observe(requireActivity()) { updateLoaderUI(it) }
//            apiErrors.observe(requireActivity()) { handleError(it) }
//            notification.observe(requireActivity()) {
//                notificationAdapter.addAll(it.data)
//            }
//        }
//    }
//
//    private fun onClickListener() {
//        notificationAdapter.setItemClickListener { _, _, response ->
//            if (response.type == 1) {
//                if (response.jobs?.job_status_id == 2) {
//                    val bundle = Bundle()
//                    bundle.putBoolean("UpcomingFix", true)
//                    EventBus.publish(bundle)
//                } else if (response.jobs?.job_status_id == 5) {
//                    requireActivity().startActivity<ViewTaskAcceptActivity>(
//                        AVAILABLE_FIXDETAIL to response.job_id,
//                        ViewTaskAcceptActivity.REMOVE to false
//                    )
//                } else if (response.jobs?.job_status_id == 6) {
//                    requireActivity().startActivity<ViewTaskAcceptActivity>(
//                        AVAILABLE_FIXDETAIL to response.job_id,
//                        ViewTaskAcceptActivity.REMOVE to true
//                    )
//                } else if (response.jobs?.job_status_id == 10 || response.jobs?.job_status_id == 1) {
//                    requireActivity().startActivity<ViewTaskAcceptActivity>(AVAILABLE_FIXDETAIL to response.job_id)
//                }
//            } else {
//                requireActivity().startActivity<DocumentsActivity>(DocumentsActivity.NOTIFICATION to 2)
//            }
//        }
//    }
//
//    private fun initView() {
//        FixerSocketService.getInstance()?.readNotification()
//        binding.toolbar.tvTitle.text = getString(R.string.notification)
//        binding.run {
//            toolbar.tvTitle.text = getString(R.string.notification)
//            notificationAdapter = NotificationAdapter(requireContext())
//            rvNotification.setAdapter(notificationAdapter, binding.viewNoFound)
//            rvNotification.adapter = notificationAdapter
//            rvNotification.addItemDecoration(StickyHeaderDecoration(notificationAdapter, false, false)
//            )
//        }
//        viewModel.notificationList()
//    }
//}
