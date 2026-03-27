package com.lib.lokdroid.navigation

sealed class Screen(open val route: String) {

    operator fun invoke(): String = route

    data object SingleLogging : Screen(route = "SingleLogging")

    data object MultipleLogging : Screen(route = "MultipleLogging")
}