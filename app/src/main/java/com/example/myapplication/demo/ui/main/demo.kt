import android.content.pm.PackageManager
import android.media.ExifInterface
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private val REQUEST_PERMISSIONS_CODE = 123
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the MapView and set the OnMapReadyCallback
        mapView = findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // Request permissions to access the user's photos
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSIONS_CODE
            )
        } else {
            // If the permissions are already granted, start getting the location data
            getImagesWithLocation()
        }
    }

    override fun onMapReady(map: GoogleMap?) {
        // Initialize the GoogleMap object
        googleMap = map ?: return

        // Set the map type and enable the zoom controls
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.uiSettings.isZoomControlsEnabled = true
    }

    private fun getImagesWithLocation() {
        // Set up a projection that retrieves the ID, latitude, and longitude columns from the Photos table
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.LATITUDE,
            MediaStore.Images.Media.LONGITUDE
        )

        // Set up a selection criteria to exclude photos without latitude and longitude data
        val selection =
            "${MediaStore.Images.Media.LATITUDE} IS NOT NULL AND ${MediaStore.Images.Media.LONGITUDE} IS NOT NULL"

        // Query the Photos table to retrieve the ID, latitude, and longitude columns for all photos with location data
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        )

        // Iterate through the cursor and add a marker for each photo on the map
        cursor?.use { cursor ->
            while (cursor.moveToNext()) {
                // Get the latitude and longitude from the Exif data of the photo
                val exif = ExifInterface(getContentUri(cursor.getLong(0)).toString())
                val latLong = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)?.split(",")
                val latRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)
                val lonLong = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)?.split(",")
                val lonRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)

                if (latLong != null && lonLong != null && latRef != null && lonRef != null) {
                    val latDegrees = latLong[0].toDoubleOrNull()
                    val latMinutes = latLong[1].toDoubleOrNull()
                    val latSeconds = latLong[2].toDoubleOrNull()?.div(1000.0) ?: 0.0
                    val latRefSign = if (latRef == "N") 1 else -1
                    val latitude = latDegrees?.plus((latMinutes?.div(60.0) ?: 0.0))
                        ?.plus((latSeconds?.div(3600.0) ?: 0.0))?.times(latRefSign)

                    val lonDegrees = lonLong[0].toDoubleOrNull()
                    val lonMinutes = lonLong[1].toDoubleOrNull()
                    val lonSeconds = lonLong[2].toDoubleOrNull()?.div(1000.0) ?: 0.0
                    val lonRefSign = if (lonRef == "E") 1 else -1
                    val longitude = lonDegrees?.plus((lonMinutes?.div(60.0) ?: 0.0))
                        ?.plus((lonSeconds?.div(3600.0) ?: 0.0))?.times(lonRefSign)

                    // Create a LatLng object from the latitude and longitude coordinates
                    val latLng = LatLng(latitude ?: 0.0, longitude ?: 0.0)

                    // Add a marker to the map at the LatLng position
                    googleMap.addMarker(MarkerOptions().position(latLng))
                }
            }
        }

        // Move the camera to the last marker added to the map
        val lastLatLng = googleMap.markers.lastOrNull()?.position
        if (lastLatLng != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 12f))
        }
    }

    private fun getContentUri(id: Long): Any {
        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(id.toString())
            .build()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Handle the result of the permission request
        if (requestCode == REQUEST_PERMISSIONS_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getImagesWithLocation()
        } else {
            // If the user denies the permission request, display a message indicating that the permission was denied
            findViewById<TextView>(R.id.text_view).text =
                getString(R.string.permission_denied_message)
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }


    private fun getImagesWithLocation(googleMap: GoogleMap) {
        // Define an array of columns to retrieve from the MediaStore.Images table
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.LATITUDE,
            MediaStore.Images.ImageColumns.LONGITUDE
        )

        // Define a cursor to retrieve the images with location data
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            "${MediaStore.Images.ImageColumns.LATITUDE} IS NOT NULL AND ${MediaStore.Images.ImageColumns.LONGITUDE} IS NOT NULL",
            null,
            null
        )

        // Check if the cursor is not null and contains at least one row
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Retrieve the latitude and longitude coordinates from the cursor
                val latitude =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LATITUDE))
                val longitude =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LONGITUDE))

                // Parse the latitude and longitude coordinates from the Exif data
                if (latitude != null && longitude != null) {
                    val latLong = latitude.split(",")
                    val latRef = latLong[latLong.size - 1]
                    val lonLong = longitude.split(",")
                    val lonRef = lonLong[lonLong.size - 1]

                    val latDegrees = latLong[0].toDoubleOrNull()
                    val latMinutes = latLong[1].toDoubleOrNull()
                    val latSeconds = latLong[2].toDoubleOrNull()?.div(1000.0) ?: 0.0
                    val latRefSign = if (latRef == "N") 1 else -1
                    val latitude = latDegrees?.plus((latMinutes?.div(60.0) ?: 0.0))
                        ?.plus((latSeconds?.div(3600.0) ?: 0.0))

                    val lonDegrees = lonLong[0].toDoubleOrNull()
                    val lonMinutes = lonLong[1].toDoubleOrNull()
                    val lonSeconds = lonLong[2].toDoubleOrNull()?.div(1000.0) ?: 0.0
                    val lonRefSign = if (lonRef == "E") 1 else -1
                    val longitude = lonDegrees?.plus((lonMinutes?.div(60.0) ?: 0.0))?.plus((lonSeconds?.div(3600.0) ?: 0.0))


                    if (latitude != null && longitude != null) {
                        val location = LatLng(latitude * latRefSign, longitude * lonRefSign)
                        googleMap.addMarker(MarkerOptions().position(location))
                    }

                }
            }while (cursor.moveToNext())
            cursor.close()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // If the READ_EXTERNAL_STORAGE permission is granted, retrieve the images with location data
            mapView.getMapAsync(this)
        } else {
            // If the READ_EXTERNAL_STORAGE permission is not granted, show an error message
            Toast.makeText(this, "The app needs access to your photos to show the locations on the map.", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val REQUEST_PERMISSIONS_CODE = 1
    }

                }
