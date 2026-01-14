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
    private lateinit var intruderImageAdapter: IntruderImageAdapter
    private val viewModel: IntruderPhotosViewModel by activityViewModels ()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentIntrudersBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backIcon.setOnClickListener {
                findNavController().popBackStack()

        }
        binding.historyIcon.setOnClickListener {
            findNavController().navigate(R.id.historyFragment,
                null,
                NavOptions.Builder().setPopUpTo(R.id.homeFragment,true).setLaunchSingleTop(true).build()

                )
        }




        setUpIntruderRv()

        intruderImageAdapter.onClick={it->
            val actions= IntrudersFragmentDirections.actionIntrudersFragmentToIntruderDetailFragment(it)
            findNavController().navigate(actions)

        }
        intruderImageAdapter.onLockedClick ={
            lifecycleScope.launch {
                viewModel.unlockImage(it.id)
            }




        }

        lifecycleScope.launchWhenStarted {
            viewModel.images.collect { list ->
                if (list.isEmpty()){
                    binding.emptyLayout.visibility= View.VISIBLE

                }
                else{
                    binding.emptyLayout.visibility= View.INVISIBLE


                }
                intruderImageAdapter.differ.submitList(list)
            }
        }

        viewModel.loadImages()
    }

    override fun onResume() {
        super.onResume()


        if (AdController.shouldShowAdd()){
            binding.adView.visibility=View.VISIBLE
            viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                binding.adView.loadAd(AdRequest.Builder().build())
            }


        }
        else{
            binding.adView.visibility=View.GONE


        }
    }









    private fun setUpIntruderRv() {
        intruderImageAdapter= IntruderImageAdapter()
        binding.intruderRv.layoutManager= GridLayoutManager(requireActivity(),3)
        binding.intruderRv.adapter=intruderImageAdapter

    }


}