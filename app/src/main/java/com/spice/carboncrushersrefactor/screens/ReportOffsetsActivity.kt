package com.spice.carboncrushersrefactor.screens

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.spice.carboncrushersrefactor.Constants
import com.spice.carboncrushersrefactor.R
import com.spice.carboncrushersrefactor.SharedPreferencesHelper
import com.spice.carboncrushersrefactor.databinding.ActivityReportOffsetsBinding
import com.spice.carboncrushersrefactor.viewmodels.ReportOffsetsViewModel
import java.lang.NumberFormatException

class ReportOffsetsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReportOffsetsBinding
    private val viewModel by viewModels<ReportOffsetsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.pHelper = SharedPreferencesHelper(getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE))
        binding = ActivityReportOffsetsBinding.inflate(layoutInflater)
        binding.textInputOffset.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrBlank()) {
                    viewModel.offsetAmount = null
                } else {
                    try {
                        viewModel.offsetAmount = s.toString().toDouble()
                    } catch (e: NumberFormatException) {
                        viewModel.offsetAmount = null
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButton_terrapass -> viewModel.donationOption = ReportOffsetsViewModel.Donation.TERRAPASS
                R.id.radioButton_eden -> viewModel.donationOption = ReportOffsetsViewModel.Donation.EDEN
            }
        }
        binding.buttonSubmit.setOnClickListener {
            if (viewModel.isFilledOut) {
                viewModel.uploadData()
            } else {
                Toast.makeText(this, "Please fill out all fields before submitting.", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.toastMessage.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
        viewModel.dataSubmitted.observe(this, Observer { isSubmitted ->
            if (isSubmitted) {
                finish()
            }
        })
        setContentView(binding.root)
    }
}