//package com.demo.exmple
//
//import android.content.Context
//import android.content.res.ColorStateList
//import android.text.format.DateUtils
//import android.view.View
//import androidx.core.content.ContextCompat
//import com.example.myapplication.common.base.BaseStickyHeaderAdapter
//import com.common.data.network.model.NotificationResponse
//import com.example.myapplication.common.data.prefs.SharedPref
//import com.massttr.provider.BuildConfig
//import com.massttr.provider.MyApp
//import com.massttr.provider.R
//import com.massttr.provider.databinding.ListItemHederBinding
//import com.massttr.provider.databinding.ListItemNotificationBinding
//import com.massttr.user.utils.getSystemDateStringFromUTCDate
//import com.massttr.user.utils.gone
//import com.massttr.user.utils.timeToConUTC
//import kotlinx.coroutines.DelicateCoroutinesApi
//import kotlinx.coroutines.ObsoleteCoroutinesApi
//import java.text.SimpleDateFormat
//import java.util.*
//
//@DelicateCoroutinesApi
//@ObsoleteCoroutinesApi
//class NotificationAdapter(val context: Context) :
//    BaseStickyHeaderAdapter<ListItemHederBinding, ListItemNotificationBinding, NotificationResponse>(
//        R.layout.list_item_heder,
//        R.layout.list_item_notification
//    ) {
//    val pref: SharedPref by lazy { MyApp.getInstance().getPref() }
//
//    private val dateFormatOld = SimpleDateFormat(
//        "yyyy-MM-dd HH:mm:ss", Locale.getDefault()
//    )
//    private val dateFormatEnglish = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//    private val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
//    override fun setClickableView(binding: ListItemNotificationBinding): List<View?> =
//        listOf(binding.root)
//
//
//    override fun onBind(
//        binding: ListItemNotificationBinding,
//        position: Int,
//        item: NotificationResponse,
//        payloads: MutableList<Any>?,
//    ) {
//        binding.run {
//            if (pref.isArabic == true) {
//                tvTitle.text = item.ar_title
//                tvDis.text = item.ar_message
//            } else {
//                tvTitle.text = item.title
//                tvDis.text = item.message
//            }
//            tvTime.text = dateFormat.format(dateFormatOld.parse(timeToConUTC(item.created_at)) as Date)
//            //2=Accept, 3=Start, 4=End, 5=CompleteJob, 6=User Cancel
//            if (item.type == 1) {
//                if (item.jobs?.fixer_id == pref.userInfo?.id) {
//                    when (item.jobs?.job_status_id) {
//                        2 -> {
//                            if (pref.isArabic == true) tvAvailable.text =
//                                item.jobs.ar_status else tvAvailable.text = item.jobs.status
//                            tvAvailable.setTextColor(
//                                ContextCompat.getColor(
//                                    context,
//                                    R.color.colorBlack
//                                )
//                            )
//                            tvAvailable.backgroundTintList =
//                                ColorStateList.valueOf(
//                                    ContextCompat.getColor(
//                                        context,
//                                        R.color.colorAccent
//                                    )
//                                )
//                        }
//                        5 -> {
//                            if (pref.isArabic == true) {
//                                available(binding, item.jobs.ar_status)
//                            } else {
//                                available(binding, item.jobs.status)
//                            }
//                        }
//                        6 -> {
//                            if (pref.isArabic == true) tvAvailable.text =
//                                item.jobs.ar_status else tvAvailable.text = item.jobs.status
//                            tvAvailable.setTextColor(
//                                ContextCompat.getColor(
//                                    context,
//                                    R.color.colorBlack
//                                )
//                            )
//                            tvAvailable.backgroundTintList =
//                                ColorStateList.valueOf(
//                                    ContextCompat.getColor(
//                                        context,
//                                        R.color.lightColorNotification
//                                    )
//                                )
//                        }
//                    }
//                } else {
//                    if (item.jobs?.job_status_id == 2 || item.jobs?.job_status_id == 5 || item.jobs?.job_status_id == 6) {
//                        noAvailable(binding, context.getString(R.string.not_available))
//                    } else if (item.jobs?.job_status_id == 10 || item.jobs?.job_status_id == 1) {
//                        available(binding, context.getString(R.string.available))
//                    }
//                }
//            } else {
//                tvAvailable.gone()
//            }
//
//
//
//        }
//    }
//
//    private fun available(binding: ListItemNotificationBinding, status: String) {
//        binding.tvAvailable.text = status
//        binding.tvAvailable.setTextColor(
//            ContextCompat.getColor(
//                context,
//                R.color.colorGreenChat
//            )
//        )
//        binding.tvAvailable.backgroundTintList =
//            ColorStateList.valueOf(
//                ContextCompat.getColor(
//                    context,
//                    R.color.colorGreenLight
//                )
//            )
//    }
//
//    private fun noAvailable(binding: ListItemNotificationBinding, status: String) {
//        binding.tvAvailable.text = status
//        binding.tvAvailable.setTextColor(ContextCompat.getColor(context, R.color.colorBlack))
//        binding.tvAvailable.backgroundTintList =
//            ColorStateList.valueOf(
//                ContextCompat.getColor(
//                    context,
//                    R.color.colorHorizontalLine
//                )
//            )
//    }
//
//    override fun includeItem(query: CharSequence?, item: NotificationResponse): Boolean = true
//
//    override fun getMyHeaderId(position: Int): Long {
//        if (displayList[position].headerId ?: -1 < 0)
//            displayList[position].headerId = getChatHeaderId(displayList[position].created_at)
//        return displayList[position].headerId ?: 0
//    }
//
//    override fun onBindHeader(headerBinding: ListItemHederBinding, position: Int) {
//        if (displayList[position].headerValue.isNullOrEmpty())
//            displayList[position].headerValue = getChatHeaderValue(displayList[position].created_at)
//
//        headerBinding.tvDay.text = displayList[position].headerValue
//    }
//
//
//
//    private fun isValid(date1: String): Boolean {
//        val cal = Calendar.getInstance();
//        cal.add(Calendar.DATE, -1)
//        return date1 == dateFormatEnglish.format(cal.time)
//    }
//
//    fun getNotificationHeaderId(utcDateStr: String): Long {
//        val sysDateStr = getNotificationSystemDateFromUTCDate(utcDateStr, "yyyy-MM-dd")
//        val date =
//            SimpleDateFormat("yyyy-MM-dd", Locale(if (pref.isArabic == true) "ar" else "en")).parse(
//                sysDateStr
//            ) ?: Date()
//
//        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale(if (pref.isArabic == true) "ar" else "en"))
//        val onlyDateString = sdf.format(date)
//        val onlyDate = sdf.parse(onlyDateString) ?: Date()
//
//        return onlyDate.time
//    }
//
//    fun getNotificationSystemDateFromUTCDate(utcDateString: String, outFormat: String): String {
//        var systemDateString = ""
//        try {
//
//            val formatter =
//                SimpleDateFormat("yyyy-MM-dd", Locale(if (pref.isArabic == true) "ar" else "en"))
//            formatter.timeZone = TimeZone.getTimeZone("UTC")
//            val utcDate = formatter.parse(utcDateString) ?: Date()
//
//            val dateFormatter =
//                SimpleDateFormat(outFormat, Locale.getDefault()) //this format changeable
//            dateFormatter.timeZone = TimeZone.getDefault()
//            systemDateString = dateFormatter.format(utcDate)
//            //val sysDate = SimpleDateFormat(DATE_TIME_FORMAT, Locale(if(SharedPref.isArabic) "ar" else "en")).parse(systemDateString) ?: Date()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return systemDateString
//    }
//
//
//    fun getChatHeaderId(utcDateStr: String?): Long {
//        if (utcDateStr.isNullOrEmpty()) return 0L
//
//        val sysDateStr = utcDateStr.getSystemDateStringFromUTCDate(BuildConfig.UtcFormat)
//        val date = SimpleDateFormat(BuildConfig.UtcFormat, Locale.getDefault()).parse(sysDateStr)
//
//        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        val onlyDateString = sdf.format(date)
//        val onlyDate = sdf.parse(onlyDateString)
//
//        return onlyDate.time
//    }
//
//    fun getChatHeaderValue(utcDateStr: String?): String {
//        if (utcDateStr.isNullOrEmpty()) return ""
//
//        val sysDateStr = utcDateStr.getSystemDateStringFromUTCDate(BuildConfig.UtcFormat)
//        val sysDate = SimpleDateFormat(BuildConfig.UtcFormat, Locale.getDefault()).parse(sysDateStr)
//
//        val currentDate = Date()
//        val diff = currentDate.time - sysDate.time
//        val totalHours = diff / (1000 * 60 * 60)
//
//        val currentCal = Calendar.getInstance()
//        val lastSeenDateCal = Calendar.getInstance()
//        lastSeenDateCal.time = sysDate
//
//        val currentSysDate = currentCal.get(Calendar.DATE)
//        val lastSeenDate = lastSeenDateCal.get(Calendar.DATE)
//
//        return if (totalHours < 24 && lastSeenDate == currentSysDate) {
//            context.getString(R.string.today)
//        } else if (DateUtils.isToday(sysDate.time + DateUtils.DAY_IN_MILLIS)) {
//            context.getString(R.string.yesterday)
//        } else {
//            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
//            sdf.format(sysDate)
//        }
//    }
//}