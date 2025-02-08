package com.example.walkwalkwalk.markfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.walkwalkwalk.databinding.ActivityBookmarkFragmentBinding

class BookMarkFragment : Fragment() {
    private var _binding: ActivityBookmarkFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityBookmarkFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bookmarkList = arrayListOf(
            BookMark(com.example.walkwalkwalk.R.drawable.bookmark_star, "즐겨찾기2", 30),
            BookMark(com.example.walkwalkwalk.R.drawable.bookmark_star, "오늘의 달리기", 45)
        )

        binding.bookmarkFrgRvProfile.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
            setHasFixedSize(true)
            adapter = BookAdapter(bookmarkList)
        }
    }
}