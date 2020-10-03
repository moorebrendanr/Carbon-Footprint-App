package com.spice.carboncrushersrefactor.adapters

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.spice.carboncrushersrefactor.Constants
import com.spice.carboncrushersrefactor.R
import com.spice.carboncrushersrefactor.SharedPreferencesHelper
import com.spice.carboncrushersrefactor.models.Question
import com.spice.carboncrushersrefactor.viewmodels.DailyLogViewModel
import com.spice.carboncrushersrefactor.views.CustomInputLayout
import com.spice.carboncrushersrefactor.models.Question.Companion.QuestionID.*
import java.lang.IllegalStateException

class DailyLogAdapter(private val questions: List<Question>, context: Context,
                      private val viewModel: DailyLogViewModel) :
        RecyclerView.Adapter<DailyLogAdapter.LogViewHolder>() {

    private val pHelper = SharedPreferencesHelper(context
            .getSharedPreferences(Constants.SHARED_PREFS_NAME, MODE_PRIVATE))

    class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val layout = when (viewType) {
            Question.TYPE_SINGLE_INPUT -> R.layout.layout_question_type1
            Question.TYPE_MULTIPLE_INPUT -> R.layout.layout_question_type2
            Question.TYPE_OPTIONAL_INPUT -> R.layout.layout_question_type3
            else -> null
        }
        val view = LayoutInflater.from(parent.context)
                .inflate(layout!!, parent, false)
        return LogViewHolder(view)
    }

    override fun getItemCount() = questions.size

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val itemType = getItemViewType(position)
        val view = holder.itemView
        val question = questions[position]
        when (itemType) {
            Question.TYPE_SINGLE_INPUT -> {
                val textView: TextView = view.findViewById(R.id.textView)
                val textInput: TextInputLayout = view.findViewById(R.id.textInputLayout)
                val editText: TextInputEditText = view.findViewById(R.id.textInputEditText)
                textView.text = question.primaryText
                textInput.hint = question.inputUnit
                if (question.id == Q_MEAT) {
                    if (pHelper.noMeat) {
                        editText.setText(0.toString())
                    }
                }
                editText.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        when (question.id) {
                            Q_MEAT -> {
                                if (s.isNullOrBlank()) {
                                    viewModel.meatServings = 0
                                } else {
                                    viewModel.meatServings = s.toString().toInt()
                                }
                            }
                            Q_PACKAGED_FOOD -> {
                                if (s.isNullOrBlank()) {
                                    viewModel.packagedMeals = 0
                                } else {
                                    viewModel.packagedMeals = s.toString().toInt()
                                }
                            }
                            Q_NON_LOCAL_PRODUCE -> {
                                if (s.isNullOrBlank()) {
                                    viewModel.nonLocalProduce = 0
                                } else {
                                    viewModel.nonLocalProduce = s.toString().toInt()
                                }
                            }
                            else -> throw IllegalStateException("Question type does not match id.")
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                })
            }
            Question.TYPE_MULTIPLE_INPUT -> {
                val editTextCar: TextInputEditText = view.findViewById(R.id.editText_car)
                val editTextBus: TextInputEditText = view.findViewById(R.id.editText_bus)
                val editTextBicycle: TextInputEditText = view.findViewById(R.id.editText_bicycle)
                val editTextTrain: TextInputEditText = view.findViewById(R.id.editText_train)
                val editTextPlane: TextInputEditText = view.findViewById(R.id.editText_plane)
                val editTextWalking: TextInputEditText = view.findViewById(R.id.editText_walking)
                editTextCar.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (s.isNullOrBlank()) {
                            viewModel.travelCar = 0
                        } else {
                            viewModel.travelCar = s.toString().toInt()
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                })
                editTextBus.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (s.isNullOrBlank()) {
                            viewModel.travelBus = 0
                        } else {
                            viewModel.travelBus = s.toString().toInt()
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                })
                editTextBicycle.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (s.isNullOrBlank()) {
                            viewModel.travelBicycle = 0
                        } else {
                            viewModel.travelBicycle = s.toString().toInt()
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                })
                editTextTrain.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (s.isNullOrBlank()) {
                            viewModel.travelTrain = 0
                        } else {
                            viewModel.travelTrain = s.toString().toInt()
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                })
                editTextPlane.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (s.isNullOrBlank()) {
                            viewModel.travelPlane = 0
                        } else {
                            viewModel.travelPlane = s.toString().toInt()
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                })
                editTextWalking.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (s.isNullOrBlank()) {
                            viewModel.travelWalking = 0
                        } else {
                            viewModel.travelWalking = s.toString().toInt()
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                })
            }
            Question.TYPE_OPTIONAL_INPUT -> {
                val textView = view.findViewById<TextView>(R.id.textView)
                val textView2 = view.findViewById<TextView>(R.id.textView2)
                val customInputLayout = view.findViewById<CustomInputLayout>(R.id.customInputLayout)
                val group = view.findViewById<Group>(R.id.group)
                val imageView: ImageView = view.findViewById(R.id.imageView)
                val editText: TextInputEditText = view.findViewById(R.id.editText)
                group.visibility = View.GONE
                textView.text = questions[position].primaryText
                textView2.text = questions[position].secondaryText
                customInputLayout.alternateIcon = questions[position].inputIcon
                customInputLayout.alternateAffixText = questions[position].inputUnit
                customInputLayout.alternateEndIconContentDescription = questions[position].altText
                imageView.setImageResource(questions[position].imageResource!!)

                val radioGroup: RadioGroup = view.findViewById(R.id.radioGroup)
                radioGroup.setOnCheckedChangeListener { _, checkedId ->
                    when (checkedId) {
                        R.id.radioButton_yes -> {
                            group.visibility = View.VISIBLE
                            if (question.id == Q_ELECTRIC) {
                                viewModel.receivedElectricBill = true
                            } else if (question.id == Q_GAS) {
                                viewModel.receivedGasBill = true
                            }
                        }
                        R.id.radioButton_no -> {
                            group.visibility = View.GONE
                            if (question.id == Q_ELECTRIC) {
                                viewModel.receivedElectricBill = false
                            } else if (question.id == Q_GAS) {
                                viewModel.receivedGasBill = false
                            }
                        }
                    }
                }

                editText.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (question.id == Q_ELECTRIC) {
                            if (s.isNullOrBlank()) {
                                viewModel.electricBillAmount = null
                            } else {
                                viewModel.electricBillAmount = s.toString().toDouble()
                            }
                        } else if (question.id == Q_GAS) {
                            if (s.isNullOrBlank()) {
                                viewModel.gasBillAmount = null
                            } else {
                                viewModel.gasBillAmount = s.toString().toDouble()
                            }
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                })

                customInputLayout.onToggleListener = object : CustomInputLayout.OnToggleListener {
                    override fun onToggle(isAlternateUnit: Boolean) {
                        if (question.id == Q_ELECTRIC) {
                            if (isAlternateUnit) {
                                viewModel.electricBillUnit = Constants.KILOWATT_HOURS
                            } else {
                                viewModel.electricBillUnit = Constants.DOLLARS
                            }
                        } else if (question.id == Q_GAS) {
                            if (isAlternateUnit) {
                                viewModel.gasBillUnit = Constants.THERMS
                            } else {
                                viewModel.gasBillUnit = Constants.DOLLARS
                            }
                        }
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int) = questions[position].type
}