package defdev.labs.droid.vulnabank.ui

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ModalWideNavigationRail
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import defdev.labs.droid.vulnabank.R
import defdev.labs.droid.vulnabank.SettingsViewModel
import defdev.labs.droid.vulnabank.TransactionsViewModel
import kotlinx.coroutines.launch

enum class Destination(@param:StringRes val label: Int, @param:DrawableRes val icon: Int) {
    TRANSACTIONS(R.string.transactions, R.drawable.ic_payments),
    SETTINGS(R.string.settings, R.drawable.ic_settings),
    ABOUT(R.string.about, R.drawable.ic_movie_info),
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainScreen(
    transactionsViewModel: TransactionsViewModel,
    settingsViewModel: SettingsViewModel,
    onOpenUrl: (String) -> Unit,
) {
    var destination by rememberSaveable { mutableStateOf(Destination.TRANSACTIONS) }
    BackHandler(enabled = destination != Destination.TRANSACTIONS) {
        destination = Destination.TRANSACTIONS
    }
    val railState = rememberWideNavigationRailState()
    val scope = rememberCoroutineScope()
    val expanded = railState.targetValue == WideNavigationRailValue.Expanded

    Scaffold(topBar = { LabHeader() }) { insets ->
        Row(Modifier.fillMaxSize().padding(insets)) {
            val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
            Box(Modifier.fillMaxHeight().width(76.8.dp)) {
                ModalWideNavigationRail(
                modifier = Modifier.fillMaxHeight(),
                expandedHeaderTopPadding = statusBarTop + 64.dp,
                state = railState,
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = WideNavigationRailDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                ),
                header = {
                    IconButton(
                        onClick = { scope.launch { railState.toggle() } },
                        modifier = Modifier.padding(start = if (expanded) 24.dp else 14.4.dp),
                    ) {
                        Icon(
                            painterResource(if (expanded) R.drawable.ic_menu_open else R.drawable.ic_menu),
                            contentDescription = stringResource(if (expanded) R.string.collapse_rail else R.string.expand_rail),
                        )
                    }
                },
            ) {
                Destination.entries.forEach { item ->
                    WideNavigationRailItem(
                        selected = destination == item,
                        onClick = {
                            destination = item
                            scope.launch { railState.collapse() }
                        },
                        icon = { Icon(painterResource(item.icon), contentDescription = if (expanded) null else stringResource(item.label)) },
                        label = if (expanded) {
                            { Text(stringResource(item.label)) }
                        } else {
                            null
                        },
                        railExpanded = expanded,
                    )
                }
            }
            }
            when (destination) {
                Destination.TRANSACTIONS -> TransactionsScreen(transactionsViewModel)
                Destination.SETTINGS -> SettingsScreen(settingsViewModel)
                Destination.ABOUT -> AboutScreen(onOpenUrl)
            }
        }
    }
}
