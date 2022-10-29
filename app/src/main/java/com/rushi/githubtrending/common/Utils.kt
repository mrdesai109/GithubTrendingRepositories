package com.rushi.githubtrending.common

import android.graphics.Color
import android.view.View
import android.widget.TextView

import com.google.android.material.snackbar.Snackbar




class Utils {

    companion object{
        fun showSnackBar(view: View?, msg: String?) {
            if (view != null) {
                println("Rushi : showSnackbar")
                val snackbar = Snackbar.make(view, msg!!, Snackbar.LENGTH_LONG)
                snackbar.show()
            }else{
                println("Rushi : showSnackbar : View NULL")
            }
        }
    }
}