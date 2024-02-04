package com.autsing.codedroid.utils

import com.autsing.codedroid.ui.graphs.MainGraphDestinations
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Navigator @Inject constructor() {
    private val destination: MutableStateFlow<MainGraphDestinations> =
        MutableStateFlow(MainGraphDestinations.Config)

    fun getDestination(): MainGraphDestinations {
        return destination.value
    }

    fun observeDestination(): Flow<MainGraphDestinations> {
        return destination
    }

    fun navigateTo(dst: MainGraphDestinations) {
        destination.value = dst
    }
}