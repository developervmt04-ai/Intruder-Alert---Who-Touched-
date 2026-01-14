package com.example.thirdeye.ui.intruders.IntruderDetail

import android.media.tv.AdRequest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.thirdeye.R
import com.example.thirdeye.billing.AdController
import com.example.thirdeye.databinding.FragmentIntruderDetailBinding
import com.example.thirdeye.ui.dialogs.DeleteDialog
import com.example.thirdeye.ui.intruders.IntruderPhotosViewModel
import com.example.thirdeye.utils.ShareHelper
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


class IntruderDetailFragment : Fragment() {
    private lateinit var binding: FragmentIntruderDetailBinding
    private val args: IntruderDetailFragmentArgs by navArgs()
    private val viewModel: IntruderPhotosViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentIntruderDetailBinding.inflate(layoutInflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val intruderData=args.images
        binding.homeImage.setImageBitmap(intruderData?.bitmap)

        binding.deleteIcon.setOnClickListener {
            lifecycleScope.launch {

                intruderData?.file?.let { file ->
                    DeleteDialog.showDeleteDialog(requireContext(), onYes = {
                        viewModel.deleteImage(file)
                        requireActivity().onBackPressed()
                    })

                }

            }

        }
        binding.historyIcon.setOnClickListener {
            findNavController().navigate(
                R.id.historyFragment,
                null,
                androidx.navigation.NavOptions.Builder()
                    .setPopUpTo(R.id.intruderDetailFragment, true)
                    .setLaunchSingleTop(true)
                    .build()
            )


        }

        binding.backIcon.setOnClickListener {
            findNavController().navigateUp()

        }
        binding.shareIcon.setOnClickListener {
            intruderData?.bitmap?.let { bitmap ->
                ShareHelper.shareFile(
                    context = requireContext(),
                    bitmap = bitmap,
                    mimeType = "image/*",
                    chooserTitle = "Share via"
                )
            }
        }

        binding.showGrid.setOnClickListener {

            findNavController().navigate(
                R.id.intrudersFragment,
                null,
                androidx.navigation.NavOptions.Builder()
                    .setPopUpTo(R.id.intruderDetailFragment, true)
                    .setLaunchSingleTop(true)
                    .build()
            )
        }
        val dateFormater= SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)

        val timerFormatter= SimpleDateFormat("hh:mm a", Locale.ENGLISH)

        val date =intruderData?.timeStamp

        binding.time.text=timerFormatter.format(date)
        binding.date.text=dateFormater.format(date)









    }

    override fun onResume() {
        super.onResume()

        if (AdController.shouldShowAdd()){
            binding.adView.visibility=View.VISIBLE
            viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                binding.adView.loadAd(com.google.android.gms.ads.AdRequest.Builder().build())
            }


        }
        else{
            binding.adView.visibility=View.GONE


        }
    }

}