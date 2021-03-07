package com.egemeninceler.donempro.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Model (
    var x: Int,
    var y: Int,
    var type: String,
    var acc: Double
) : Parcelable