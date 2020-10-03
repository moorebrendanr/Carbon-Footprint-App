package com.spice.carboncrushersrefactor.screens

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.spice.carboncrushersrefactor.Constants
import com.spice.carboncrushersrefactor.R
import com.spice.carboncrushersrefactor.SharedPreferencesHelper
import com.spice.carboncrushersrefactor.adapters.DailyLogAdapter
import com.spice.carboncrushersrefactor.databinding.FragmentDailyLogBinding
import com.spice.carboncrushersrefactor.models.Question
import com.spice.carboncrushersrefactor.models.Question.Companion.QuestionID.*
import com.spice.carboncrushersrefactor.viewmodels.DailyLogViewModel

/**
 * The Daily Log
 */
class DailyLogFragment : Fragment() {
    private lateinit var binding: FragmentDailyLogBinding
    private val viewModel by viewModels<DailyLogViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewModel.pHelper = SharedPreferencesHelper(requireActivity().getSharedPreferences(Constants.SHARED_PREFS_NAME,
                MODE_PRIVATE))
        binding = FragmentDailyLogBinding.inflate(layoutInflater)
        val pHelper = SharedPreferencesHelper(requireContext().getSharedPreferences(Constants.SHARED_PREFS_NAME, MODE_PRIVATE))

        val recyclerView = binding.recyclerView
        val questions = mutableListOf(
                Question(Question.TYPE_SINGLE_INPUT,
                        Q_MEAT,
                        primaryText = getString(R.string.meat_servings),
                        inputUnit = getString(R.string.servings)),
                Question(Question.TYPE_SINGLE_INPUT,
                        Q_PACKAGED_FOOD,
                        primaryText = getString(R.string.packaged_food),
                        inputUnit = getString(R.string.meals)),
                Question(Question.TYPE_SINGLE_INPUT,
                        Q_NON_LOCAL_PRODUCE,
                        primaryText = getString(R.string.non_local_produce),
                        inputUnit = getString(R.string.servings)),
                Question(Question.TYPE_MULTIPLE_INPUT, Q_TRAVEL),
                Question(Question.TYPE_OPTIONAL_INPUT,
                        Q_ELECTRIC,
                        primaryText = getString(R.string.receive_electric_bill),
                        secondaryText = getString(R.string.how_much_electric),
                        altText = getString(R.string.kilowatt_hours),
                        inputUnit = getString(R.string.kWh),
                        inputIcon = R.drawable.ic_electricity,
                        imageResource = R.drawable.ic_electric)
        )

        if (pHelper.hasNaturalGas) {
            questions.add(Question(Question.TYPE_OPTIONAL_INPUT,
                    Q_GAS,
                    primaryText = getString(R.string.receive_gas_bill),
                    secondaryText = getString(R.string.how_much_gas),
                    altText = getString(R.string.therms),
                    inputUnit = getString(R.string.thm),
                    inputIcon = R.drawable.ic_fire,
                    imageResource = R.drawable.ic_gas))
        }

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = DailyLogAdapter(questions, context, viewModel)
        }

        binding.buttonSubmit.setOnClickListener {
            if (viewModel.areQuestionsAnswered) {
                viewModel.uploadData()
            }
        }

        viewModel.fetchMostRecentDailyLogAndUpdate()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.toastMessage.observe(viewLifecycleOwner, Observer { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        })

        viewModel.dataSubmitted.observe(viewLifecycleOwner, Observer { submitted ->
            if (submitted) {
                binding.buttonSubmit.text = getString(R.string.update)
            }
        })

        viewModel.alreadyLoggedToday.observe(viewLifecycleOwner, Observer {isLogged ->
            if (isLogged) {
                binding.buttonSubmit.text = getString(R.string.update)
            } else {
                binding.buttonSubmit.text = getString(R.string.submit)
            }
        })
    }
}