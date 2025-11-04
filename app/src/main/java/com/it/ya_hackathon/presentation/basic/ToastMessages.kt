package com.it.ya_hackathon.presentation.basic

import android.content.Context
import android.widget.Toast
import com.it.ya_hackathon.R

fun showInternalErrorToast(localContext: Context) {
    Toast.makeText(
        localContext,
        R.string.internal_error,
        Toast.LENGTH_SHORT
    ).show()
}