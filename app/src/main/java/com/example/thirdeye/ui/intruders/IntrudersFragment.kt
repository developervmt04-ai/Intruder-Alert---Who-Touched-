package com.example.thirdeye.ui.intruders

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.thirdeye.R
import com.example.thirdeye.ads.NativeAdController
import com.example.thirdeye.ads.NativeAdType
import com.example.thirdeye.billing.AdController
import com.example.thirdeye.data.encryptedStorage.EncryptedStorageRepository
import com.example.thirdeye.data.models.IntrudersImages
import com.example.thirdeye.databinding.FragmentIntrudersBinding
import com.example.thirdeye.ui.dialogs.biometricDialogs.FingerPrintDialog
import com.google.android.gms.ads.AdRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IntrudersFragment : Fragment() {
    private lateinit var binding: FragmentIntrudersBinding
    private val intruderImageAdapter by lazy {

        IntruderImageAdapter()
    }

    private lateinit var nativeAdController: NativeAdController
    private val viewModel: IntruderPhotosViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntrudersBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nativeAdController = NativeAdController(requireContext())

        binding.backIcon.setOnClickListener {
            findNavController().popBackStack()

        }

        binding.premiumLayout.setOnClickListener {
            findNavController().navigate(
                R.id.payWallFragment,
                null,
                NavOptions.Builder().setLaunchSingleTop(true)
                    .build()
            )


        }





        binding.historyIcon.setOnClickListener {
            findNavController().navigate(R.id.action_intrudersFragment_to_historyFragment)
        }




        setUpIntruderRv()

        intruderImageAdapter.onClick = { it ->
            val actions =
                IntrudersFragmentDirections.actionIntrudersFragmentToIntruderDetailFragment(it)
            findNavController().navigate(actions)

        }
        intruderImageAdapter.onLockedClick = {
            lifecycleScope.launch {


            }


        }

        lifecycleScope.launchWhenStarted {
            viewModel.images.collect { list ->
                if (list.isEmpty()) {
                    binding.emptyLayout.visibility = View.VISIBLE

                } else {
                    binding.emptyLayout.visibility = View.INVISIBLE


                }
                intruderImageAdapter.differ.submitList(list)
                if (viewModel.isIntruderLimitReached()){
                    binding.premiumLayout.visibility=View.VISIBLE




                }
            }

        }

        viewModel.loadImages()

    }

    override fun onResume() {
        super.onResume()

        val currentImages = viewModel.images.value
        val ids = currentImages.map { it.id }
        viewModel.markImagesSeen(ids)


        if (AdController.shouldShowAdd()) {
            nativeAdController.loadNativeAd(binding.nativeAdRoot, NativeAdType.MEDIUM)


        } else {


        }
    }


    private fun setUpIntruderRv() {
        binding.intruderRv.layoutManager = GridLayoutManager(requireActivity(), 3)
        binding.intruderRv.adapter = intruderImageAdapter

    }


}