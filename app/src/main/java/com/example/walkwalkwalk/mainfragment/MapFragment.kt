package com.example.walkwalkwalk.mainfragment

import com.example.walkwalkwalk.R
import com.example.walkwalkwalk.databinding.ActivityMainFragmentBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap

class MapFragment : BaseMapFragment<ActivityMainFragmentBinding>(R.layout.activity_main_fragment) {
    override var mapView: MapView = MapView(requireContext())

    override fun initOnCreateView() {
        mapView = binding.mainFragmentMap
    }

    override fun initOnMapReady(naverMap: NaverMap) {
        naverMap.uiSettings.isZoomControlEnabled = true
        naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(37.5670135, 126.9783740)))
    }

    override fun iniViewCreated() {

    }

    override fun initOnResume() {
        mapView?.onResume()
    }

}