package com.spice.carboncrushersrefactor.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.spice.carboncrushersrefactor.Constants
import com.spice.carboncrushersrefactor.R
import com.spice.carboncrushersrefactor.SharedPreferencesHelper
import com.spice.carboncrushersrefactor.databinding.FragmentOffsetsBinding
import com.spice.carboncrushersrefactor.viewmodels.OffsetsViewModel
import kotlin.math.roundToInt

class OffsetsFragment : Fragment() {
    private lateinit var binding: FragmentOffsetsBinding
    private lateinit var pHelper: SharedPreferencesHelper
    private val viewModel by viewModels<OffsetsViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        pHelper = SharedPreferencesHelper(requireActivity().getSharedPreferences(Constants.SHARED_PREFS_NAME,
                Context.MODE_PRIVATE))
        viewModel.pHelper = pHelper

        binding = FragmentOffsetsBinding.inflate(layoutInflater)
        setOffset(0)
        binding.buttonPurchaseOffset.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.terrapass.com/product/productindividuals-families"))
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(intent)
            }
        }
        binding.buttonDonateOffset.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://edenprojects.org/donate/"))
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(intent)
            }
        }
        binding.buttonReportOffset.setOnClickListener {
            startActivityForResult(Intent(context, ReportOffsetsActivity::class.java), 12)
        }
        return binding.root
    }

    private fun setOffset(offsetAmount: Int) {
        val totalCarbon = pHelper.avgCarbon
        binding.textViewOffsetRatio.text = getString(R.string.offset_ratio, offsetAmount, totalCarbon)
        val ratio: Double = offsetAmount.toDouble() / totalCarbon
        binding.textViewOffsetPercentage.text = getString(R.string.offset_percentage, (ratio*100).roundToInt())
        binding.imageViewTreeAlive.alpha = ratio.toFloat()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.toastMessage.observe(viewLifecycleOwner, Observer {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })
        viewModel.offsetTotal.observe(viewLifecycleOwner, Observer {
            setOffset(it)
        })
        viewModel.getOffsetTotal()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 12) {
            viewModel.getOffsetTotal()
        }
    }
}