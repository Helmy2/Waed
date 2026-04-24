package io.github.helmy2.waed.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class WaedNavKey : NavKey {
    @Serializable
    data object Home : WaedNavKey()

    @Serializable
    data class CustomerDetail(val customerId: Long) : WaedNavKey()
}