package com.lib.lokdroid.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavGraph(
    navHostController: NavHostController,
    singleLogDemonstration: @Composable () -> Unit,
    multipleLogDemonstration: @Composable () -> Unit,
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.SingleLogging.route
    ) {
        composable(route = Screen.SingleLogging.route) { singleLogDemonstration() }
        composable(route = Screen.MultipleLogging.route) { multipleLogDemonstration() }
    }
}