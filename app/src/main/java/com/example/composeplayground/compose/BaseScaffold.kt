package com.example.composeplayground.compose

import android.annotation.SuppressLint
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun BaseScaffold() {
    Scaffold {
        CardV5()
    }
}