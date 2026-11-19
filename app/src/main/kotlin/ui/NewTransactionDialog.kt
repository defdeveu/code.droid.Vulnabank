package defdev.labs.droid.vulnabank.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import defdev.labs.droid.vulnabank.FormField
import defdev.labs.droid.vulnabank.R
import defdev.labs.droid.vulnabank.TransactionForm

@Composable
fun NewTransactionDialog(
    form: TransactionForm,
    onChange: (TransactionForm) -> Unit,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    val invalid = form.invalidField
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(stringResource(R.string.start_transaction)) },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FormTextField(
                    value = form.name,
                    onValueChange = { onChange(form.copy(name = it)) },
                    label = R.string.recipient_name,
                    error = if (invalid == FormField.NAME) R.string.name_error else null,
                    keyboard = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                )
                FormTextField(
                    value = form.accountNumber,
                    onValueChange = { onChange(form.copy(accountNumber = it)) },
                    label = R.string.account_number,
                    error = if (invalid == FormField.ACCOUNT_NUMBER) R.string.account_number_error else null,
                    keyboard = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
                FormTextField(
                    value = form.amount,
                    onValueChange = { onChange(form.copy(amount = it)) },
                    label = R.string.amount,
                    error = if (invalid == FormField.AMOUNT) R.string.amount_error else null,
                    keyboard = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
                FormTextField(
                    value = form.comment,
                    onValueChange = { onChange(form.copy(comment = it)) },
                    label = R.string.comment,
                    error = null,
                    keyboard = KeyboardOptions.Default,
                    singleLine = false,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = form.isValid) { Text(stringResource(android.R.string.ok)) }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text(stringResource(android.R.string.cancel)) }
        },
    )
}

@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: Int,
    error: Int?,
    keyboard: KeyboardOptions,
    singleLine: Boolean = true,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(label), fontWeight = MaterialTheme.typography.labelLargeEmphasized.fontWeight) },
        isError = error != null,
        supportingText = error?.let { { Text(stringResource(it)) } },
        singleLine = singleLine,
        keyboardOptions = keyboard,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth(),
    )
}
