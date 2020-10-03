package com.spice.carboncrushersrefactor.screens

import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.spice.carboncrushersrefactor.Constants
import com.spice.carboncrushersrefactor.R
import com.spice.carboncrushersrefactor.SharedPreferencesHelper
import com.spice.carboncrushersrefactor.databinding.SettingsActivityBinding
import com.spice.carboncrushersrefactor.receivers.NotificationSender
import com.spice.carboncrushersrefactor.viewmodels.SettingsViewModel
import java.util.*
import kotlin.collections.ArrayList

class SettingsActivity : AppCompatActivity(),
        TimePickerFragment.OnCancelListener,
        DietSelectionFragment.DietDialogListener {
    private lateinit var binding: SettingsActivityBinding
    private lateinit var pHelper: SharedPreferencesHelper
    private val viewModel by viewModels<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pHelper = SharedPreferencesHelper(getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE))
        viewModel.pHelper = pHelper
        val alarmIntent = Intent(applicationContext, NotificationSender::class.java)
        val pendingIntent = PendingIntent.getBroadcast(applicationContext, 1,
                alarmIntent, PendingIntent.FLAG_NO_CREATE)
        binding.switchNotifications.isChecked = pendingIntent != null
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                TimePickerFragment().show(supportFragmentManager, "timePicker")
            } else {
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
                pHelper.reminderTime = null
                if (pendingIntent != null && alarmManager != null) {
                    Log.d("debugging", "Cancelled PendingIntent!")
                    alarmManager.cancel(pendingIntent)
                }
            }
        }
        binding.buttonDiet.setOnClickListener {
            DietSelectionFragment().show(supportFragmentManager, "Diet Selection")
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        viewModel.toastMessage.observe(this, Observer { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCancel() {
        binding.switchNotifications.isChecked = false
    }

    override fun onDietsSelected(diets: ArrayList<String>) {
        viewModel.updateDiets(diets)
    }
}

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    private lateinit var pHelper: SharedPreferencesHelper
    interface OnCancelListener {
        fun onCancel()
    }

    private var listener: OnCancelListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? OnCancelListener
        if (listener == null) {
            throw ClassCastException("$context must implement OnArticleSelectedListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pHelper = SharedPreferencesHelper(requireContext().getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE))
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity)).apply {
            setTitle("Daily log reminder")
            setMessage("Choose a time to receive a reminder every day.\n")
        }
    }


    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val intent = Intent(context, NotificationSender::class.java)
        val alarmIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }
        if (calendar < Calendar.getInstance()) {
            calendar.add(Calendar.DATE, 1)
        }
        pHelper.reminderTime = calendar.timeInMillis
        alarmManager?.setInexactRepeating(
                AlarmManager.RTC,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                alarmIntent
        )
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        listener?.onCancel()
    }
}

class DietSelectionFragment : DialogFragment() {
    private lateinit var listener: DietDialogListener

    interface DietDialogListener {
        fun onDietsSelected(diets: ArrayList<String>)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as DietDialogListener
        } catch (e: java.lang.ClassCastException) {
            throw ClassCastException((context.toString() +
                    " must implement DietDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val selectedItems = ArrayList<Int>()
        return activity?.let {
            val diets = arrayOf(
                    it.getString(R.string.vegetarian),
                    it.getString(R.string.vegan),
                    it.getString(R.string.paleo),
                    it.getString(R.string.keto),
                    it.getString(R.string.pescetarian),
                    it.getString(R.string.gluten_free),
                    it.getString(R.string.dairy_free)
            )
            val dietValues = arrayOf(
                    Constants.VEGETARIAN,
                    Constants.VEGAN,
                    Constants.PALEO,
                    Constants.KETO,
                    Constants.PESCETARIAN,
                    Constants.GLUTEN_FREE,
                    Constants.DAIRY_FREE
            )
            val builder = AlertDialog.Builder(it)
            builder.setTitle(it.getString(R.string.do_you_follow_any_type_of_diet))
                    .setMultiChoiceItems(diets, null) { _, which, isChecked ->
                        if (isChecked) {
                            selectedItems.add(which)
                        } else if (selectedItems.contains(which)) {
                            selectedItems.remove(which)
                        }
                    }
                    .setPositiveButton(android.R.string.ok) { dialog, which ->
                        val selectedDietVals = ArrayList<String>()
                        for (item in selectedItems) {
                            selectedDietVals.add(dietValues[item])
                        }
                        listener.onDietsSelected(selectedDietVals)
                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ -> }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}