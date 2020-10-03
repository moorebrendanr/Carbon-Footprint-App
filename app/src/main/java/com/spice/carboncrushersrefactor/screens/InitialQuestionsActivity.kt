package com.spice.carboncrushersrefactor.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.spice.carboncrushersrefactor.Constants
import com.spice.carboncrushersrefactor.R
import com.spice.carboncrushersrefactor.SharedPreferencesHelper
import com.spice.carboncrushersrefactor.databinding.ActivityInitialQuestionsBinding
import com.spice.carboncrushersrefactor.viewmodels.InitialQuestionsViewModel
import kotlin.reflect.KProperty

/**
 * Activity for the initial questions when app is first used.
 */
class InitialQuestionsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInitialQuestionsBinding
    private val viewModel by viewModels<InitialQuestionsViewModel>()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInitialQuestionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.pHelper = SharedPreferencesHelper(getSharedPreferences(Constants.SHARED_PREFS_NAME,
                Context.MODE_PRIVATE))

        addListeners()

        binding.imageViewVegetarian.isSelected = viewModel.diets.contains(Constants.VEGETARIAN)
        binding.imageViewVegan.isSelected = viewModel.diets.contains(Constants.VEGAN)
        binding.imageViewPaleo.isSelected = viewModel.diets.contains(Constants.PALEO)
        binding.imageViewKeto.isSelected = viewModel.diets.contains(Constants.KETO)
        binding.imageViewPescetarian.isSelected = viewModel.diets.contains(Constants.PESCETARIAN)
        binding.imageViewGlutenFree.isSelected = viewModel.diets.contains(Constants.GLUTEN_FREE)
        binding.imageViewDairyFree.isSelected = viewModel.diets.contains(Constants.DAIRY_FREE)

        binding.buttonSubmit.setOnClickListener {
            if (viewModel.areQuestionsAnswered) {
                viewModel.saveData()
                viewModel.uploadData()
                intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    /**
     * Callback for diet selection.
     *
     * @param view the view that was clicked
     */
    fun onDietSelection(view: View) {
        var imageView: ImageView? = null
        var diet: String? = null
        when (view.id) {
            R.id.imageView_vegetarian, R.id.textView_vegetarian -> {
                imageView = binding.imageViewVegetarian
                diet = Constants.VEGETARIAN
            }
            R.id.imageView_vegan, R.id.textView_vegan -> {
                imageView = binding.imageViewVegan
                diet = Constants.VEGAN
            }
            R.id.imageView_paleo, R.id.textView_paleo -> {
                imageView = binding.imageViewPaleo
                diet = Constants.PALEO
            }
            R.id.imageView_keto, R.id.textView_keto -> {
                imageView = binding.imageViewKeto
                diet = Constants.KETO
            }
            R.id.imageView_pescetarian, R.id.textView_pescetarian -> {
                imageView = binding.imageViewPescetarian
                diet = Constants.PESCETARIAN
            }
            R.id.imageView_glutenFree, R.id.textView_glutenFree -> {
                imageView = binding.imageViewGlutenFree
                diet = Constants.GLUTEN_FREE
            }
            R.id.imageView_dairyFree, R.id.textView_dairyFree -> {
                imageView = binding.imageViewDairyFree
                diet = Constants.DAIRY_FREE
            }
        }

        if (imageView != null && diet != null) {
            if (imageView.isSelected) {
                viewModel.diets.remove(diet)
                imageView.isSelected = false
            } else {
                viewModel.diets.add(diet)
                imageView.isSelected = true
            }
        }
    }

    private fun addListeners() {
        viewModel.toastMessage.observe(this, Observer {message ->
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        })
        viewModel.dietNeedsFocus.observe(this, Observer { value ->
            if (value) { binding.radioGroupDiet.requestFocus() }
        })
        viewModel.carNeedsFocus.observe(this, Observer { value ->
            if (value) { binding.editTextCar.requestFocus() }
        })
        viewModel.electricNeedsFocus.observe(this, Observer { value ->
            if (value) { binding.editTextElectricity.requestFocus() }
        })
        viewModel.gasNeedsFocus.observe(this, Observer { value ->
            if (value) { binding.editTextGas.requestFocus() }
        })
        viewModel.flightNeedsFocus.observe(this, Observer { value ->
            if (value) { binding.editTextFlight.requestFocus() }
        })
        viewModel.home1needsFocus.observe(this, Observer { value ->
            if (value) { binding.editTextHome.requestFocus() }
        })
        viewModel.home2needsFocus.observe(this, Observer { value ->
            if (value) { binding.editTextHome2.requestFocus() }
        })

        binding.radioGroupDiet.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButton_dietYes -> {
                    binding.groupDiet.visibility = View.VISIBLE
                    viewModel.followsDiet = true
                }
                R.id.radioButton_dietNo -> {
                    binding.groupDiet.visibility = View.GONE
                    viewModel.followsDiet = false
                }
            }
        }

        binding.radioGroupCar.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButton_carYes -> viewModel.ownsCar = true
                R.id.radioButton_carNo -> viewModel.ownsCar = false
            }
        }

        binding.editTextCar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrBlank()) {
                    viewModel.carMiles = null
                } else {
                    viewModel.carMiles = s.toString().toInt()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        binding.editTextElectricity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrBlank()) {
                    viewModel.electricBill = null
                } else {
                    viewModel.electricBill = s.toString().toInt()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.radioGroupGas.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButton_gasYes -> viewModel.hasNaturalGas = true
                R.id.radioButton_gasNo -> viewModel.hasNaturalGas = false
            }
        }

        binding.editTextGas.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrBlank()) {
                    viewModel.gasBill = null
                } else {
                    viewModel.gasBill = s.toString().toInt()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.radioGroupFlight.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButton_flightYes -> viewModel.hasFlown = true
                R.id.radioButton_flightNo -> viewModel.hasFlown = false
            }
        }

        binding.editTextFlight.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrBlank()) {
                    viewModel.flightMiles = null
                } else {
                    viewModel.flightMiles = s.toString().toInt()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.editTextHome.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrBlank()) {
                    viewModel.homeSize = null
                } else {
                    viewModel.homeSize = s.toString().toInt()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.editTextHome2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrBlank()) {
                    viewModel.people = null
                } else {
                    viewModel.people = s.toString().toInt()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }
}