/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.cupcake

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cupcake.data.DataSource
import com.example.cupcake.data.OrderUiState
import com.example.cupcake.ui.OrderSummaryScreen
import com.example.cupcake.ui.OrderViewModel
import com.example.cupcake.ui.SelectOptionScreen
import com.example.cupcake.ui.StartOrderScreen

/**
 * enum values that represent the screens in the app
 */
enum class CupcakeScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    Flavor(title = R.string.choose_flavor),
    Pickup(title = R.string.choose_pickup_date),
    Summary(title = R.string.order_summary)
}

/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */
@Composable
fun CupcakeAppBar(
    currentScreen: CupcakeScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@Composable
fun CupcakeApp(
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {

    /*
    toDO: (Liste)
    - (Rang 1)
        # verstehe dieses "backStackEntry" - Meine Notes:
    - (Rang 3)
        # verstehe dieses by nochmal, ich glaube es war dieses extra: Deligation ..
          -- https://docs.google.com/document/d/16ynEMMuxBkdjEW6xZebMfbzNYeHGdzZRQqFH2wJ7hGI/edit?tab=t.sj8l5wsxodtv
        # verstehe dieses remember nochmal
     */
    /* ---------------------------------------------------------------- */
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    /* ---------------------------------------------------------------- */
    Log.i("INFO", "backStackEntry ==>")
    Log.i("INFO", backStackEntry.toString())
    Log.i("INFO", backStackEntry?.maxLifecycle.toString())
    Log.i("INFO", backStackEntry?.arguments.toString())

    /*
    toDO:
    - (Rang 1)
        #  verstehe dieses "currentScreen" - Meine Notes: Whenn ein Ziel vorhanden gib das zurück, ansonsten Start.name
    (Google Docs)
        # GK-Note: "valueOf" ist Kotlin spezifisch
            - The valueOf() method throws an IllegalArgumentException if the specified name does not match any of the enum constants defined in the class.
            - Link: https://kotlinlang.org/docs/enum-classes.html#working-with-enum-constants
     */
    /* ---------------------------------------------------------------- */
    // Get the name of the current screen
    val currentScreen = CupcakeScreen.valueOf(
        backStackEntry?.destination?.route ?: CupcakeScreen.Start.name
    )
    /* ---------------------------------------------------------------- */
    Log.i("INFO", "currentScreen ==>")
    Log.i("INFO", currentScreen.toString())
    Log.i("INFO", currentScreen.name.toString())
    Log.i("INFO", currentScreen.title.toString())

    Log.i("INFO", "##################################")
    Log.i("INFO", navController.previousBackStackEntry.toString())
    Log.i("INFO", navController.previousBackStackEntry?.arguments.toString())


    Scaffold(
        topBar = {
            CupcakeAppBar(
                currentScreen = currentScreen,
                // toDO: (Rang 1) verstehe dieses "previousBackStackEntry" - Meine Notes:
                // NOTE: Doku: previousBackStackEntry - Return: den letzten sichtbaren Eintrag auf dem hinteren Stapel oder null, wenn der hintere Stapel weniger als zwei sichtbare Einträge hat
                canNavigateBack = navController.previousBackStackEntry != null,
                // toDO: (Rang 1) verstehe dieses "navigateUp" - Meine Notes:
                navigateUp = { navController.navigateUp() }
		        // navController.popBackStack(CupcakeScreen.Start.name, inclusive = false)
                // navigateUp = { navController.popBackStack(CupcakeScreen.Pickup.name, inclusive = false) } //  navController.popBackStack(CupcakeScreen.Start.name, inclusive = false)
                /*
                toDO: (Google Docs)
                GK-Note:
                * navController.popBackStack() == navController.navigateUp() - ohne Argumente von popBackStack
                * - inclusive = false ==> Spring bis zu dir dieser Seite zurück: Was angegeben ist
                *       - Falls im Stack vorhanden, sonst passiert nichts
                * - inclusive = true ==> Spring bis zu dir dieser Seite zurück: Was angegeben ist, inklusive dieser Seite
                *     - Falls im Stack vorhanden, passiert nichts
                * - saveState Noch nicht ausgetestet
                * */
            )
            Log.i("info", "navController.popBackStack(CupcakeScreen.Flavor.name, inclusive = false)")
        }
    ) { innerPadding ->
        // toDO: (Rang 2)  verstehe dieses "uiState" - Mene Notes
        val uiState by viewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = CupcakeScreen.Start.name,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            composable(route = CupcakeScreen.Start.name) {
                StartOrderScreen(
                    quantityOptions = DataSource.quantityOptions,
                    onNextButtonClicked = {
                        viewModel.setQuantity(it)
                        navController.navigate(CupcakeScreen.Flavor.name)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.padding_medium))
                )
            }
            composable(route = CupcakeScreen.Flavor.name) {
                val context = LocalContext.current
                SelectOptionScreen(
                    subtotal = uiState.price,
                    onNextButtonClicked = { navController.navigate(CupcakeScreen.Pickup.name) },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    options = DataSource.flavors.map { id -> context.resources.getString(id) },
                    onSelectionChanged = { viewModel.setFlavor(it) },
                    modifier = Modifier.fillMaxHeight()
                )
            }
            composable(route = CupcakeScreen.Pickup.name) {
                SelectOptionScreen(
                    subtotal = uiState.price,
                    onNextButtonClicked = { navController.navigate(CupcakeScreen.Summary.name) },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    options = uiState.pickupOptions,
                    onSelectionChanged = { viewModel.setDate(it) },
                    modifier = Modifier.fillMaxHeight()
                )
            }
            composable(route = CupcakeScreen.Summary.name) {
                val context = LocalContext.current
                OrderSummaryScreen(
                    orderUiState = uiState,
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    onSendButtonClicked = { subject: String, summary: String ->
                        shareOrder(context, subject = subject, summary = summary)
                    },
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }
}

/**
 * Resets the [OrderUiState] and pops up to [CupcakeScreen.Start]
 */
// Das hier ist einfach der links unten "Cancel" - Button, siehe: SelectOptionScreen
private fun cancelOrderAndNavigateToStart(
    viewModel: OrderViewModel,
    navController: NavHostController
) {
    // toDO: (Rang 2) Verstehen, warum das ein ResetORder ist.
    viewModel.resetOrder()
    navController.popBackStack(CupcakeScreen.Start.name, inclusive = false)
}

/**
 * Creates an intent to share order details
 */
private fun shareOrder(context: Context, subject: String, summary: String) {
    // Create an ACTION_SEND implicit intent with order details in the intent extras
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, summary)
    }
    context.startActivity(
        Intent.createChooser(
            intent,
            context.getString(R.string.new_cupcake_order)
        )
    )
}
