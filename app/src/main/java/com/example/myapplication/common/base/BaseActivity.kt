package com.example.myapplication.common.base
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.myapplication.common.data.database.daos.AppDao
import com.example.myapplication.common.data.network.model.ResponseCode
import com.example.myapplication.common.data.prefs.SharedPref
import com.example.myapplication.common.utils.AppLoader
import com.example.myapplication.demo.App
import kotlinx.coroutines.channels.ReceiveChannel
import org.json.JSONObject
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.*


abstract class BaseActivity<VB : ViewDataBinding>(private val layoutRes: Int) : AppCompatActivity() {

    protected lateinit var binding: VB
    protected val listSubscription = ArrayList<ReceiveChannel<*>>()
    private val appLoader: AppLoader by lazy { AppLoader(this) }
    val handler: Handler by lazy { Handler(Looper.getMainLooper()) }
    val pref: SharedPref by lazy { App.getInstance().getPref() }
    val dao: AppDao by lazy { App.getInstance().getDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutRes)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }


    protected fun setNewLocale(language: String) {
        recreate()
    }



    override fun onDestroy() {
        super.onDestroy()
        listSubscription.forEach { it.cancel() }
    }





    protected fun updateLoaderUI(isShow: Boolean) {
        if (isShow) appLoader.show() else appLoader.dismiss()
    }

    fun handleError(throwable: Throwable) {
        when (throwable) {
            is HttpException -> {
                handleResponseError(throwable)
            }
            is ConnectException -> {
              //  showMessage(getString(R.string.msg_no_internet))
            }
            is SocketTimeoutException -> {
              //  showMessage(getString(R.string.time_out))
            }
        }
    }

    private fun handleResponseError(throwable: HttpException) {
        when (throwable.code()) {
            ResponseCode.InvalidData.code -> {
                val errorRawData = throwable.response()?.errorBody()?.string()
                if (!errorRawData.isNullOrEmpty()) {
                    val jsonObject = errorRawData?.let { JSONObject(it) }
                    val jObject = jsonObject?.optJSONObject("errors")
                    if (jObject != null) {
                        val keys: Iterator<String> = jObject.keys()
                        if (keys.hasNext()) {
                            val msg = StringBuilder()
                            while (keys.hasNext()) {
                                val key: String = keys.next()
                                if (jObject.get(key) is String) {
                                    msg.append("- ${jObject.get(key)}\n")
                                }
                            }
                           // errorDialog(msg.toString(), "Alert")
                        } else {
                            //errorDialog(jsonObject.optString("message", ""))
                        }
                    } else {
                       // errorDialog(JSONObject(errorRawData).optString("message"), "Alert")
                    }
                }
            }
            ResponseCode.Unauthenticated.code -> {
                val errorRawData = throwable.response()?.errorBody()?.string()?.trim()
                if (!errorRawData.isNullOrEmpty()) {

                } else {
                    onAuthFail()
                }
            }
            ResponseCode.ForceUpdate.code -> {

            }
            ResponseCode.InternalServerError.code,
            ResponseCode.BadRequest.code,
            ResponseCode.Unauthorized.code,
            ResponseCode.NotFound.code,
            ResponseCode.RequestTimeOut.code,
            ResponseCode.Conflict.code,
            ResponseCode.Blocked.code -> {

            }
        }
    }

    private fun onAuthFail() {
        pref.clearAppUserData()
        //TODO Redirect User to Login Screen
    }


}

