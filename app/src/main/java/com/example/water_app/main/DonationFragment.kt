package com.example.water_app.main

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.water_app.databinding.ActivityRecyclerviewBinding
import com.example.water_app.databinding.ActivityRecyclerviewGridBinding
import com.example.water_app.donation.CommunicationActivity
import com.example.water_app.recyclerview.DonationAdapter
import com.example.water_app.repository.Repository
import com.example.water_app.user.MySharedPreferences
import com.example.water_app.viewmodel.MainViewModel
import com.example.water_app.viewmodel.MainViewModelFactory


class DonationFragment : Fragment() {

    // 뷰바인딩
    private lateinit var binding: ActivityRecyclerviewGridBinding

    // 뷰 모델 가져오기
    private lateinit var viewModel : MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // 뷰바인딩
        binding = ActivityRecyclerviewGridBinding.inflate(inflater, container, false)

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)

        val mbr_sn = MySharedPreferences.getUserSn(requireContext()).toInt()

        viewModel = ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java)
        viewModel.getMy(mbr_sn)
        viewModel.getDonationListResponse.observe(viewLifecycleOwner, Observer {
            // 통신 성공
            if(it.isSuccessful){
                val donationList = it.body()

                //리사이클러뷰
                binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
                binding.recyclerView.setHasFixedSize(true)
                binding.recyclerView.adapter = DonationAdapter(requireContext(), donationList)

                // 아이템 간 간격
                binding.recyclerView.addItemDecoration(RecyclerViewDecoration(20, 20,0,40))

                // OnClickListener
                val adapter = DonationAdapter(requireContext(), donationList)

                adapter.setItemClickListener(object : DonationAdapter.OnItemClickListener{
                    override fun onClick(v: View, position: Int) {
                        activity?.let{
                            val intent = Intent(context, CommunicationActivity::class.java)
                            startActivity(intent)
                        }
                    }
                })
                binding.recyclerView.adapter = adapter
            }
        })
        return binding.root
    }

    // 뒤로가기 메인 고정
    private lateinit var callback: OnBackPressedCallback
    lateinit var mainActivity: MainActivity

    // 뒤로가기 고정
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        mainActivity = context as MainActivity
    }

    // 뒤로가기 고정
    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    // 리싸이클러뷰 간격
    class RecyclerViewDecoration(private val divLeft: Int, private val divRight: Int, private val divHeight: Int, private val divTop: Int) : ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.left = divLeft
            outRect.right = divRight
            outRect.bottom = divHeight
            outRect.top = divTop
        }
    }
}