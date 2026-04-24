package io.github.helmy2.waed.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import io.github.helmy2.waed.ui.screen.detail.CustomerDetailScreen
import io.github.helmy2.waed.ui.screen.search.SearchScreen

@Composable
fun WaedNavHost(
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(WaedNavKey.Home)

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<WaedNavKey.Home> {
                SearchScreen(
                    onNavigateToDetail = { customerId ->
                        backStack.add(WaedNavKey.CustomerDetail(customerId))
                    },
                )
            }

            entry<WaedNavKey.CustomerDetail> { route ->
                CustomerDetailScreen(
                    customerId = route.customerId,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                )
            }
        },
    )
}