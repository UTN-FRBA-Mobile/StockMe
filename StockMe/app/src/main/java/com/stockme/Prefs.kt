package com.stockme

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean
import org.androidannotations.annotations.sharedpreferences.SharedPref


@SharedPref(value = SharedPref.Scope.UNIQUE)
interface Prefs {

    @DefaultBoolean(false)
    fun loggedIn(): Boolean

}