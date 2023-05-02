//package com.example.myapplication.common.utils
//
//import android.Manifest
//import android.content.Context
//import android.content.pm.PackageManager
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//
//object PermissionUtils {
//    fun asktAccessFineLocationPermission(activity: AppCompatActivity, requestId: Int) {
//        ActivityCompat.requestPermissions(
//            activity,
//            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//            requestId
//        )
//    }
//
//    fun checkAccessFineLocationGranted(context: Context): Boolean {
//        return ContextCompat
//            .checkSelfPermission(
//                context,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//    }
//
//    fun isLocationEnabled(context: Context): Boolean {
//        val gfgLocationManager: GfgLocationManager =
//            context.getSystemService(Context.LOCATION_SERVICE) as GfgLocationManager
//        return gfgLocationManager.isProviderEnabled(GfgLocationManager.GPS_PROVIDER)
//                || gfgLocationManager.isProviderEnabled(GfgLocationManager.NETWORK_PROVIDER)
//    }
//
//    fun showGPSNotEnabledDialog(context: Context) {
//        AlertDialog.Builder(context)
//            .setTitle(context.getString(R.string.gps_gfg_enabled))
//            .setMessage(context.getString(R.string.required_for_this_app))
//            .setCancelable(false)
//            .setPositiveButton(context.getString(R.string.enable_now)) { _, _ ->
//                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
//            }
//            .show()
//    }
//}
