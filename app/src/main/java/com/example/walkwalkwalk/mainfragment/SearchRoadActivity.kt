package com.example.walkwalkwalk.mainfragment

import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.walkwalkwalk.R
import com.example.walkwalkwalk.databinding.ActivitySearchRoadBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import okio.IOException
import java.util.Locale

class SearchRoadActivity : AppCompatActivity(), OnMapReadyCallback{

    private lateinit var binding: ActivitySearchRoadBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchRoadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        requestPermissions()
        initMap()

        binding.roadSearchBtn.setOnClickListener {
            val searchText = binding.roadSearchTxt.text.toString()
            if (searchText.isNotEmpty()) {
                searchLocation(searchText)
            }
        }

        binding.roadNowLoaction.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return@setOnClickListener
            }

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(it.latitude, it.longitude)))
                }
            }
        }

        binding.roadPlus.setOnClickListener {
            val cameraUpdate = CameraUpdate.zoomIn()
            naverMap.moveCamera(cameraUpdate)
        }

        binding.roadMinus.setOnClickListener {
            val cameraUpdate = CameraUpdate.zoomOut()
            naverMap.moveCamera(cameraUpdate)
        }
    }

    private fun requestPermissions() {
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    private fun initMap () {
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.road_naver_map_view) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.road_naver_map_view, it).commit()
            }
        mapFragment.getMapAsync(this)
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
    }

    private fun searchLocation (address: String) {
        val geoCoder = Geocoder(this, Locale.KOREA)
        try {
            val addresses = geoCoder.getFromLocationName(address, 1)
            if (addresses!!.isNotEmpty()) {
                naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(addresses[0].latitude, addresses[0].longitude)))
            } else {
                Toast.makeText(this, "주소를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "주소 검색 중 오류 발생", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}