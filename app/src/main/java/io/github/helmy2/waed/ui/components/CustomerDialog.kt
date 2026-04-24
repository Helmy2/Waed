package io.github.helmy2.waed.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.helmy2.waed.data.local.entity.CustomerRecord

@Composable
fun CustomerDialog(
    customer: CustomerRecord?,
    error: String?,
    onDismiss: () -> Unit,
    onSave: (CustomerRecord) -> Unit
) {
    var pageNumber by remember { mutableStateOf(customer?.pageNumber?.toString() ?: "") }
    var customerName by remember { mutableStateOf(customer?.customerName ?: "") }
    var debit by remember { mutableStateOf(customer?.debit?.toString() ?: "0.0") }

    var pageNumberError by remember { mutableStateOf<String?>(null) }
    var customerNameError by remember { mutableStateOf<String?>(null) }
    var debitError by remember { mutableStateOf<String?>(null) }

    val isEdit = customer != null

    fun validate(): Boolean {
        pageNumberError = null
        customerNameError = null
        debitError = null

        var isValid = true

        if (pageNumber.isBlank()) {
            pageNumberError = "Page number is required"
            isValid = false
        } else if (pageNumber.toIntOrNull() == null) {
            pageNumberError = "Enter a valid number"
            isValid = false
        }

        if (customerName.isBlank()) {
            customerNameError = "Name is required"
            isValid = false
        }

        if (debit.isBlank()) {
            debitError = "Debit amount is required"
            isValid = false
        } else if (debit.toDoubleOrNull() == null) {
            debitError = "Enter a valid amount"
            isValid = false
        }

        return isValid
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = if (isEdit) "Edit Customer" else "Add Customer")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = pageNumber,
                    onValueChange = {
                        pageNumber = it.filter { char -> char.isDigit() }
                        pageNumberError = null
                    },
                    label = { Text("Page Number") },
                    isError = pageNumberError != null,
                    supportingText = pageNumberError?.let { { Text(it) } },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = customerName,
                    onValueChange = {
                        customerName = it
                        customerNameError = null
                    },
                    label = { Text("Customer Name") },
                    isError = customerNameError != null,
                    supportingText = customerNameError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = debit,
                    onValueChange = {
                        debit = it.filter { char -> char.isDigit() || char == '.' }
                        debitError = null
                    },
                    label = { Text("Debit Amount") },
                    isError = debitError != null,
                    supportingText = debitError?.let { { Text(it) } },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (error != null) {
                    Text(
                        text = error,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (validate()) {
                        val newCustomer = CustomerRecord(
                            id = customer?.id ?: 0,
                            pageNumber = pageNumber.toInt(),
                            customerName = customerName.trim(),
                            debit = debit.toDouble()
                        )
                        onSave(newCustomer)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}