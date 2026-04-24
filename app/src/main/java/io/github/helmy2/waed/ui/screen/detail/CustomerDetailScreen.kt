package io.github.helmy2.waed.ui.screen.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.helmy2.waed.R
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
    val showEditDialog by viewModel.showEditDialog.collectAsState()
    val showDeleteDialog by viewModel.showDeleteDialog.collectAsState()
    val saveError by viewModel.saveError.collectAsState()

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteConfirmation() },
            title = { Text(stringResource(R.string.delete_customer)) },
            text = { Text(stringResource(R.string.are_you_sure_delete, customer?.customerName ?: "")) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCustomer { onNavigateBack() }
                    }
                ) {
                    Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideDeleteConfirmation() }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showEditDialog && customer != null) {
        EditCustomerDialog(
            customer = customer!!,
            error = saveError,
            onDismiss = { viewModel.hideEditDialog() },
            onSave = { pageNumber, name, debit ->
                viewModel.saveCustomer(pageNumber, name, debit) { }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.customer_details)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
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

        CustomerDetailsContent(
            customer = customer!!,
            onEdit = { viewModel.showEditDialog() },
            onDelete = { viewModel.showDeleteConfirmation() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        )
    }
}

@Composable
private fun CustomerDetailsContent(
    customer: io.github.helmy2.waed.data.local.entity.CustomerRecord,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DetailItem(label = stringResource(R.string.customer_name), value = customer.customerName)
        DetailItem(label = stringResource(R.string.page_number), value = customer.pageNumber.toString())
        DetailItem(label = stringResource(R.string.debit_amount), value = formatCurrency(customer.debit))

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onEdit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.edit))
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
                contentDescription = stringResource(R.string.delete)
            )
            Text(
                text = stringResource(R.string.delete_customer),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun EditCustomerDialog(
    customer: io.github.helmy2.waed.data.local.entity.CustomerRecord,
    error: SaveError?,
    onDismiss: () -> Unit,
    onSave: (Int, String, Double) -> Unit
) {
    var pageNumber by remember { mutableStateOf(customer.pageNumber.toString()) }
    var customerName by remember { mutableStateOf(customer.customerName) }
    var debit by remember { mutableStateOf(customer.debit.toString()) }

    var pageNumberError by remember { mutableStateOf<String?>(null) }
    var customerNameError by remember { mutableStateOf<String?>(null) }
    var debitError by remember { mutableStateOf<String?>(null) }

    val pageNumberRequired = stringResource(R.string.page_number_required)
    val nameRequired = stringResource(R.string.name_required)
    val debitRequired = stringResource(R.string.debit_required)

    val errorMessage = when (error) {
        is SaveError.PageNumberTaken -> stringResource(R.string.page_number_already_in_use, error.pageNumber)
        is SaveError.SaveFailed -> error.message
        null -> null
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_customer)) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = pageNumber,
                    onValueChange = { pageNumber = it.filter { c -> c.isDigit() } },
                    label = { Text(stringResource(R.string.page_number)) },
                    isError = pageNumberError != null,
                    supportingText = pageNumberError?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text(stringResource(R.string.customer_name)) },
                    isError = customerNameError != null,
                    supportingText = customerNameError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = debit,
                    onValueChange = { debit = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text(stringResource(R.string.debit_amount)) },
                    isError = debitError != null,
                    supportingText = debitError?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    pageNumberError = null
                    customerNameError = null
                    debitError = null

                    var isValid = true

                    if (pageNumber.isBlank()) {
                        pageNumberError = pageNumberRequired
                        isValid = false
                    }
                    if (customerName.isBlank()) {
                        customerNameError = nameRequired
                        isValid = false
                    }
                    if (debit.isBlank()) {
                        debitError = debitRequired
                        isValid = false
                    }

                    if (isValid) {
                        onSave(
                            pageNumber.toInt(),
                            customerName.trim(),
                            debit.toDouble()
                        )
                    }
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
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
    val formatter = NumberFormat.getCurrencyInstance(Locale("ar", "EG"))
    return formatter.format(amount)
}