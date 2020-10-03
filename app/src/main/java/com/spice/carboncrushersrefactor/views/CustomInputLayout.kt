package com.spice.carboncrushersrefactor.views

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputLayout
import com.spice.carboncrushersrefactor.R

/**
 * Custom TextInputLayout
 */
class CustomInputLayout(context: Context, attrs: AttributeSet?) : TextInputLayout(context, attrs) {
    var isAlternateUnit = false
        private set
    var onToggleListener: OnToggleListener? = null
    var alternateIcon: Int? = null
    var alternateAffixText: String? = null
    var alternateEndIconContentDescription: String? = null

    interface OnToggleListener {
        fun onToggle(isAlternateUnit: Boolean)
    }

    init {
        setEndIconDrawable(R.drawable.ic_money)
        prefixText = "$"
        endIconContentDescription = "dollars"
        setEndIconOnClickListener { toggle() }
    }

    private fun toggle() {
        if (isAlternateUnit) {
            isAlternateUnit = false
            setEndIconDrawable(R.drawable.ic_money)
            prefixText = "$"
            suffixText = null
            endIconContentDescription = "dollars"
        } else {
            isAlternateUnit = true
            if (alternateIcon == null) {
                endIconDrawable = null
            } else {
                setEndIconDrawable(alternateIcon!!)
                setEndIconActivated(false)
//                Toast.makeText(context, "Icon changed", Toast.LENGTH_SHORT).show()
            }
            prefixText = null
            suffixText = alternateAffixText
            endIconContentDescription = alternateEndIconContentDescription
        }
        onToggleListener?.onToggle(isAlternateUnit)
    }
}