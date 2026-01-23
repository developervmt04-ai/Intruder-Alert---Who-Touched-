package com.example.thirdeye.ui.intruders.IntruderDetail.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thirdeye.R
import com.example.thirdeye.databinding.FragmentHistoryBinding
import com.example.thirdeye.ui.intruders.IntruderPhotosViewModel
import kotlinx.coroutines.launch


class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter

    val viewModel: IntruderPhotosViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentHistoryBinding.inflate(layoutInflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }



        setUpRv()

        historyAdapter.onClick={
            val action= HistoryFragmentDirections.actionHistoryFragmentToIntruderDetailFragment(it)
            findNavController().navigate(action)




        }
        lifecycleScope.launch {

            viewModel.images.collect {images ->
                if (images.isEmpty()){

                    binding.emptyLayout.visibility=View.VISIBLE
                    binding.historyRV.visibility= View.INVISIBLE

                }
                else{
                    binding.emptyLayout.visibility=View.INVISIBLE
                    binding.historyRV.visibility=View.VISIBLE


                }
                historyAdapter.differ.submitList(images)
            }
        }
        viewModel.loadImages()


    }

    private fun setUpRv() {
        historyAdapter= HistoryAdapter()
        binding.historyRV.layoutManager= LinearLayoutManager(requireContext())
        binding.historyRV.adapter=historyAdapter

    }


}