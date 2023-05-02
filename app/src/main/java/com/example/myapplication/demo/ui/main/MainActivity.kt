package com.example.myapplication.demo.ui.main

import android.Manifest
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.location.Location
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.common.base.BaseActivity
import com.example.myapplication.common.data.database.entities.UserLocal
import com.example.myapplication.common.utils.EventBus
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.demo.ui.LocationMap
import com.example.myapplication.demo.ui.adapters.UserAdapterAdapter
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.*
import timber.log.Timber


class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main), OnMapReadyCallback {

    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var userAdapter: UserAdapterAdapter
    private val userLocal: List<UserLocal?>? = null
    private val REQUEST_PERMISSIONS_CODE = 1
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationPermissionCode = 2
   // private var images: ArrayList<String?>? = null
   var images = ArrayList<ImageDetails>()
    private val locations: MutableMap<String, MutableList<LatLng>> = HashMap()


    companion object {
        private const val PERMISSIONS_REQUEST_LOCATION = 100
    }


    // private var currentPage = 1
    private var lastPage: Int = 0

    // lateinit var data: MainData
    var isLoadMore: Boolean = false
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initEventBus()
        setUpUi()
        setUpObserver()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

//        if ((pref.currentPage ?: 1) <= 1) {
//            // currentPage = 1
//            pref.currentPage = 1
//            viewModel.callApi(pref.currentPage ?: 1, 10)
//            // currentPage++
//            //pref.currentPage = currentPage
//        } else {
//            //  viewModel.callApi(pref.currentPage?:1,10)
//        }

        // Check if location permissions are granted
        // Check if location permissions are granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        } else {
            // Get the last known location
            getLastKnownLocation()
        }
        queryImageStorage()

        val allImages: ArrayList<ImageDetails> = getAllImages(this)
        Toast.makeText(this, "Total Images: ${allImages.size}", Toast.LENGTH_SHORT).show()
        // images = getAllShownImagesPath()
//        GlobalScope.launch {
//            runBlocking {
//                delay(1000L)
//                images = allImages
//                Timber.e("url","${images.size}")
//                println("Middle  statement of runBlocking")
//            }
//
//        }



//        Thread{
//            allImages.forEach {
//                Timber.e("url","${it.uri}")
//                Timber.e("lat","${it.latitude}")
//                Timber.e("log","${it.longitude}")
//            }
//        }.start()



        userAdapter = UserAdapterAdapter()
      //  binding.rvTasks.adapter = userAdapter

        initAdapter()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSIONS_CODE
            )
        } else {
         //   getImageLocation()
        }

        val locationMap = LocationMap()
        locationMap.addLocation("Location 1", 37.7749, -122.4194)
        locationMap.addLocation("Location 1", 37.7749, -122.4194)
        locationMap.addLocation("Location 2", 40.7128, -74.0060)
        locationMap.addLocation("Location 3", 51.5074, -0.1278)

        val locations = locationMap.getLocations()


        locations.forEach {
            runOnUiThread {
                Timber.e("locations","${it.key}")
                it.value.forEach { latLng ->
                    Timber.e("latitude","${latLng.latitude}")
                    Timber.e("longitude","${latLng.longitude}")
                }
            }


        }





    }


    private fun queryImageStorage() {

        val imageProjection = arrayOf(
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media._ID
        )

        val imageSortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            imageProjection,
            null,
            null,
            imageSortOrder
        )

        cursor.use {
            it?.let {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                val dateColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)

                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val name = it.getString(nameColumn)
                    val size = it.getString(sizeColumn)
                    val date = it.getString(dateColumn)

                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    Log.d("contentUri","${contentUri.path}")

                    // add the URI to the list
                    // generate the thumbnail
                   // val thumbnail = (this as Context).contentResolver.loadThumbnail(contentUri, Size(480, 480), null)

                }
            } ?: kotlin.run {
                Log.e("TAG", "Cursor is null!")
            }
        }
    }


    private fun getAllImages(context: Context): ArrayList<ImageDetails> {


        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.LATITUDE,
            MediaStore.Images.Media.LONGITUDE,
            MediaStore.Images.Media.DATE_ADDED
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val latitudeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.LATITUDE)
            val longitudeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.LONGITUDE)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri: Uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                val latitude = cursor.getDouble(latitudeColumn)
                val longitude = cursor.getDouble(longitudeColumn)
                val imageDetails = ImageDetails(contentUri, latitude, longitude)
                images.add(imageDetails)
           //     Timber.e("url","${images.size}")

            }
        }

        return images
    }


    private fun getAllShownImagesPath(): ArrayList<String?> {
        val uri: Uri
        val cursor: Cursor?
        val column_index_data: Int
        val column_index_folder_name: Int
        val listOfAllImages = ArrayList<String?>()
        var absolutePathOfImage: String? = null
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.MediaColumns.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )
        cursor = applicationContext.contentResolver.query(
            uri, projection, null,
            null, null
        )
        column_index_data = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        column_index_folder_name = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data)
            listOfAllImages.add(absolutePathOfImage)
        }
        return listOfAllImages
    }


    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    // Use the location
                    val latitude = it.latitude
                    val longitude = it.longitude
                    val sydney = LatLng(latitude, longitude)
                    mMap.addMarker(MarkerOptions()
                        .position(sydney)
                        .title("Marker in Sydney"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
                    Toast.makeText(this, "Latitude: $latitude, Longitude: $longitude", Toast.LENGTH_LONG).show()
                } ?: run {
                    // Location is null
                    Toast.makeText(this, "Could not get current location", Toast.LENGTH_SHORT).show()
                }
            }
    }




    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                for (location in p0.locations) {
                    if (location != null) {
                        // Use the location
                        val latitude = location.latitude
                        val longitude = location.longitude
                        Timber.e("latitude","$latitude")
                        Timber.e("longitude","$longitude")
                        fusedLocationClient.removeLocationUpdates(this)
                        return
                    }
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }





    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastKnownLocation()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }





    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions()
            .position(sydney)
            .title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_PERMISSIONS_CODE && grantResults.isNotEmpty()
//            && grantResults[0] == PackageManager.PERMISSION_GRANTED
//        ) {
//            getImageLocation()
//        }
//    }

    private fun convertToDegree(stringDMS: String): Double {
        val dms = stringDMS.split("/").map { it.toDouble() }
        val degrees = dms[0] / dms[1]
        val minutes = dms[2] / dms[3] / 60
        val seconds = dms[4] / dms[5] / 3600
        return degrees + minutes + seconds
    }

    private fun getImageLocation() {
        val path = "/storage/emulated/0/DCIM/Camera/IMG_20230409_191613.jpg"
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
        val latLong = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)?.split(",")?.map { it.trim() }
        val latRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)
        val lonLong = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)?.split(",")?.map { it.trim() }
        val lonRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)

        if (latLong.isNullOrEmpty() || latRef.isNullOrEmpty() || lonLong.isNullOrEmpty() || lonRef.isNullOrEmpty()) {
            return null // no location data available
        }

        val lat = convertToDegree(latLong[0]) * if (latRef == "N") 1 else -1
        val lon = convertToDegree(lonLong[0]) * if (lonRef == "E") 1 else -1

        return Pair(lat, lon)
    }


    private fun initAdapter() {
//        binding.rvTasks.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
//                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == userAdapter.itemCount - 1) {
//                    //        if (isLoadMore) {
//                    viewModel.callApi(pref.currentPage ?: 1, 10)
//                    //      }
//                }
//            }
//        })
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

//    private fun getImagesWithLocation() {
//        // The projection specifies the columns to retrieve from the MediaStore database
//        val projection = arrayOf(
//            MediaStore.Images.Media._ID,
//            MediaStore.Images.Media.DATA,
//            MediaStore.Images.Media.LATITUDE,
//            MediaStore.Images.Media.LONGITUDE
//        )
//
//        // The selection specifies the WHERE clause of the query
//        val selection =
//            "${MediaStore.Images.Media.LATITUDE} IS NOT NULL AND ${MediaStore.Images.Media.LONGITUDE} IS NOT NULL"
//
//        // The sortOrder specifies the ORDER BY clause of the query
//        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
//
//        // The query returns a Cursor object that contains the images with location data
//        val cursor: Cursor? = contentResolver.query(
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//            projection,
//            selection,
//            null,
//            sortOrder
//        )
//
//        // If the cursor is not null and contains images with location data
//        if (cursor != null && cursor.count > 0) {
//            // Loop through the images in the cursor
//            while (cursor.moveToNext()) {
//                // Get the ID, path, latitude, and longitude of the image from the cursor
//                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
//                val path =
//                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
//                val latitude =
//                    cursor.getDouble(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.LATITUDE))
//                val longitude =
//                    cursor.getDouble(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.LONGITUDE))
//
//                // Display the location data in the TextView
//                binding.run {
//                    //textview.append("Image ID: $id\n")
////                    textView.append("Image Path: $path\n")
////                    textView.append("Latitude: $latitude\n")
////                    textView.append("Longitude: $longitude\n")
//                }
//
//
//                // Get the location data using the ExifInterface class
//                val exif = ExifInterface(path)
//                val latLong =
//                    exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)?.split(",")?.map { it.trim() }
//                val latRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)
//                val lonLong = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)?.split(",")
//                    ?.map { it.trim() }
//
//
//                var latitudeFromExif: Double? = null
//                var longitudeFromExif: Double? = null
//                if (latLong != null && lonLong != null && latRef != null) {
//                    val latDegrees = latLong[0].toDoubleOrNull()
//                    val latMinutes = latLong[1].toDoubleOrNull()
//                    val latSeconds = latLong[2].toDoubleOrNull()
//                    val latSign = if (latRef == "N") 1 else -1
//                    if (latDegrees != null && latMinutes != null && latSeconds != null) {
//                        latitudeFromExif =
//                            latSign * (latDegrees + latMinutes / 60.0 + latSeconds / 3600.0)
//                    }
//                    val lonDegrees = lonLong[0].toDoubleOrNull()
//                    val lonMinutes = lonLong[1].toDoubleOrNull()
//                    val lonSeconds = lonLong[2].toDoubleOrNull()
//                    val lonRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)
//                    val lonSign = if (lonRef == "E") 1 else -1
//                    if (lonDegrees != null && lonMinutes != null && lonSeconds != null) {
//                        longitudeFromExif = lonSign * (lonDegrees + lonMinutes / 60.0 + lonSeconds / 3600.0)
//                    }
//                }
//
//                if (latitudeFromExif != null && longitudeFromExif != null) {
//                    //  textView.append("Latitude (Exif): $latitudeFromExif\n")
//                    //  textView.append("Longitude (Exif): $longitudeFromExif\n")
//                }
//            }
//            // Close the cursor
//            cursor.close()
//
//        }
//
//    }


    data class ImageDetails(val uri: Uri, val latitude: Double, val longitude: Double)





//    private fun getAllImages(context: Context): ArrayList<ImageDetails> {
//        val images = ArrayList<ImageDetails>()
//
//        val projection = arrayOf(
//            MediaStore.Images.Media._ID,
//            MediaStore.Images.Media.DISPLAY_NAME,
//            MediaStore.Images.Media.LATITUDE,
//            MediaStore.Images.Media.LONGITUDE,
//            MediaStore.Images.Media.DATE_ADDED
//        )
//
//        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
//
//        context.contentResolver.query(
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//            projection,
//            null,
//            null,
//            sortOrder
//        )?.use { cursor ->
//            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
//            val latitudeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.LATITUDE)
//            val longitudeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.LONGITUDE)
//            while (cursor.moveToNext()) {
//                val id = cursor.getLong(idColumn)
//                val contentUri: Uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
//                val latitude = cursor.getDouble(latitudeColumn)
//                val longitude = cursor.getDouble(longitudeColumn)
//                val imageDetails = ImageDetails(contentUri, latitude, longitude)
//                images.add(imageDetails)
//            }
//        }
//
//        return images
//    }
}


