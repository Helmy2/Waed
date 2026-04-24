package io.github.helmy2.waed.ui.screen.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    customerId: Long,
    viewModel: CustomerDetailViewModel = koinViewModel { parametersOf(customerId) },
    onNavigateBack: () -> Unit
) {
    val customer by viewModel.customer.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()
    val showDeleteDialog by viewModel.showDeleteDialog.collectAsState()
    val error by viewModel.error.collectAsState()

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteConfirmation() },
            title = { Text("Delete Customer") },
            text = { Text("Are you sure you want to delete ${customer?.customerName}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCustomer { onNavigateBack() }
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideDeleteConfirmation() }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Customer" else "Customer Details") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isEditing) {
                            viewModel.cancelEditing()
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (customer == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isEditing) {
                EditCustomerContent(
                    customer = customer!!,
                    error = error,
                    onSave = { pageNumber, name, debit ->
                        viewModel.saveCustomer(pageNumber, name, debit) { }
                    },
                    onCancel = { viewModel.cancelEditing() }
                )
            } else {
                CustomerDetailsContent(
                    customer = customer!!,
                    onEdit = { viewModel.startEditing() },
                    onDelete = { viewModel.showDeleteConfirmation() }
                )
            }
        }
    }
}

@Composable
private fun CustomerDetailsContent(
    customer: io.github.helmy2.waed.data.local.entity.CustomerRecord,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column {
        DetailItem(label = "Customer Name", value = customer.customerName)
        DetailItem(label = "Page Number", value = customer.pageNumber.toString())
        DetailItem(label = "Debit Amount", value = formatCurrency(customer.debit))

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onEdit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Edit")
        }

        OutlinedButton(
            onClick = onDelete,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete"
            )
            Text(
                text = "Delete Customer",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun EditCustomerContent(
    customer: io.github.helmy2.waed.data.local.entity.CustomerRecord,
    error: String?,
    onSave: (Int, String, Double) -> Unit,
    onCancel: () -> Unit
) {
    var pageNumber by remember { mutableStateOf(customer.pageNumber.toString()) }
    var customerName by remember { mutableStateOf(customer.customerName) }
    var debit by remember { mutableStateOf(customer.debit.toString()) }

    var pageNumberError by remember { mutableStateOf<String?>(null) }
    var customerNameError by remember { mutableStateOf<String?>(null) }
    var debitError by remember { mutableStateOf<String?>(null) }

    OutlinedTextField(
        value = pageNumber,
        onValueChange = { pageNumber = it.filter { c -> c.isDigit() } },
        label = { Text("Page Number") },
        isError = pageNumberError != null,
        supportingText = pageNumberError?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )

    OutlinedTextField(
        value = customerName,
        onValueChange = { customerName = it },
        label = { Text("Customer Name") },
        isError = customerNameError != null,
        supportingText = customerNameError?.let { { Text(it) } },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )

    OutlinedTextField(
        value = debit,
        onValueChange = { debit = it.filter { c -> c.isDigit() || c == '.' } },
        label = { Text("Debit Amount") },
        isError = debitError != null,
        supportingText = debitError?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )

    if (error != null) {
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.weight(1f)
        ) {
            Text("Cancel")
        }
        Button(
            onClick = {
                pageNumberError = null
                customerNameError = null
                debitError = null

                var isValid = true

                if (pageNumber.isBlank()) {
                    pageNumberError = "Page number is required"
                    isValid = false
                }
                if (customerName.isBlank()) {
                    customerNameError = "Name is required"
                    isValid = false
                }
                if (debit.isBlank()) {
                    debitError = "Debit is required"
                    isValid = false
                }

                if (isValid) {
                    onSave(
                        pageNumber.toInt(),
                        customerName.trim(),
                        debit.toDouble()
                    )
                }
            },
            modifier = Modifier.weight(1f)
        ) {
            Text("Save")
        }
    }
}

@Composable
private fun DetailItem(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("ar", "SA"))
    return formatter.format(amount)
}