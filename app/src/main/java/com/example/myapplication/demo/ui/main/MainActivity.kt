package com.example.myapplication.demo.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.ExifInterface
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
    private val REQUEST_PERMISSIONS_CODE = 1

    // private var currentPage = 1
    private var lastPage: Int = 0

    // lateinit var data: MainData
    var isLoadMore: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initEventBus()
        setUpUi()
        setUpObserver()

        if ((pref.currentPage ?: 1) <= 1) {
            // currentPage = 1
            pref.currentPage = 1
            viewModel.callApi(pref.currentPage ?: 1, 10)
            // currentPage++
            //pref.currentPage = currentPage
        } else {
            //  viewModel.callApi(pref.currentPage?:1,10)
        }



        userAdapter = UserAdapterAdapter()
        binding.rvTasks.adapter = userAdapter

        initAdapter()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSIONS_CODE
            )
        } else {
            getImagesWithLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS_CODE && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getImagesWithLocation()
        }
    }

    private fun convertToDegree(stringDMS: String): Double {
        val dms = stringDMS.split("/").map { it.toDouble() }
        val degrees = dms[0] / dms[1]
        val minutes = dms[2] / dms[3] / 60
        val seconds = dms[4] / dms[5] / 3600
        return degrees + minutes + seconds
    }

    private fun getImageLocation() {
        val path = "/storage/emulated/0/DCIM/Camera/IMG_20230501_220518.jpg"
        val location = getImageLocation(path)
        if (location != null) {
            Timber.e("Latitude: ${location.first}, Longitude: ${location.second}", "")
            //textView.text = "Latitude: ${location.first}, Longitude: ${location.second}"
        } else {
            Timber.e("Location data not available", "")
            //  textView.text = "Location data not available"
        }
    }

    fun getImageLocation(path: String): Pair<Double, Double>? {
        val exif = ExifInterface(path)
        val latLong =
            exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)?.split(",")?.map { it.trim() }
        val latRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)
        val lonLong =
            exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)?.split(",")?.map { it.trim() }
        val lonRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)

        if (latLong.isNullOrEmpty() || latRef.isNullOrEmpty() || lonLong.isNullOrEmpty() || lonRef.isNullOrEmpty()) {
            return null // no location data available
        }

        val lat = convertToDegree(latLong[0]) * if (latRef == "N") 1 else -1
        val lon = convertToDegree(lonLong[0]) * if (lonRef == "E") 1 else -1

        return Pair(lat, lon)
    }


    private fun initAdapter() {
        binding.rvTasks.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == userAdapter.itemCount - 1) {
                    //        if (isLoadMore) {
                    viewModel.callApi(pref.currentPage ?: 1, 10)
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
                Timber.e("list", "${it.data.size}")

                if (isLoadMore) {
                    it.data.forEach { apiUser ->
                        viewModel.insertUser(UserLocal().apply {
                            airId = apiUser._id
                            displayname = apiUser.name
                            trips = apiUser.trips

                        }, apiUser)
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

                        }, apiUser)
                    }
                }
                isLoadMore = (pref.currentPage ?: 1) <= lastPage


            }
        }

        viewModel.localUser?.observe(this) {
            Timber.e("Local Users: ${it.size}")
            userAdapter.addAll(it)

        }
    }

    private fun getImagesWithLocation() {
        // The projection specifies the columns to retrieve from the MediaStore database
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.LATITUDE,
            MediaStore.Images.Media.LONGITUDE
        )

        // The selection specifies the WHERE clause of the query
        val selection =
            "${MediaStore.Images.Media.LATITUDE} IS NOT NULL AND ${MediaStore.Images.Media.LONGITUDE} IS NOT NULL"

        // The sortOrder specifies the ORDER BY clause of the query
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        // The query returns a Cursor object that contains the images with location data
        val cursor: Cursor? = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )

        // If the cursor is not null and contains images with location data
        if (cursor != null && cursor.count > 0) {
            // Loop through the images in the cursor
            while (cursor.moveToNext()) {
                // Get the ID, path, latitude, and longitude of the image from the cursor
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                val path =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                val latitude =
                    cursor.getDouble(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.LATITUDE))
                val longitude =
                    cursor.getDouble(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.LONGITUDE))

                // Display the location data in the TextView
                binding.run {
                    //textview.append("Image ID: $id\n")
//                    textView.append("Image Path: $path\n")
//                    textView.append("Latitude: $latitude\n")
//                    textView.append("Longitude: $longitude\n")
                }


                // Get the location data using the ExifInterface class
                val exif = ExifInterface(path)
                val latLong =
                    exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)?.split(",")?.map { it.trim() }
                val latRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)
                val lonLong = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)?.split(",")
                    ?.map { it.trim() }


                var latitudeFromExif: Double? = null
                var longitudeFromExif: Double? = null
                if (latLong != null && lonLong != null && latRef != null) {
                    val latDegrees = latLong[0].toDoubleOrNull()
                    val latMinutes = latLong[1].toDoubleOrNull()
                    val latSeconds = latLong[2].toDoubleOrNull()
                    val latSign = if (latRef == "N") 1 else -1
                    if (latDegrees != null && latMinutes != null && latSeconds != null) {
                        latitudeFromExif =
                            latSign * (latDegrees + latMinutes / 60.0 + latSeconds / 3600.0)
                    }
                    val lonDegrees = lonLong[0].toDoubleOrNull()
                    val lonMinutes = lonLong[1].toDoubleOrNull()
                    val lonSeconds = lonLong[2].toDoubleOrNull()
                    val lonRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)
                    val lonSign = if (lonRef == "E") 1 else -1
                    if (lonDegrees != null && lonMinutes != null && lonSeconds != null) {
                        longitudeFromExif = lonSign * (lonDegrees + lonMinutes / 60.0 + lonSeconds / 3600.0)
                    }
                }

                if (latitudeFromExif != null && longitudeFromExif != null) {
                    //  textView.append("Latitude (Exif): $latitudeFromExif\n")
                    //  textView.append("Longitude (Exif): $longitudeFromExif\n")
                }
            }
            // Close the cursor
            cursor.close()

        }

    }
}
