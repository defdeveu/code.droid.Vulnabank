package defdev.labs.droid.vulnabank.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import defdev.labs.droid.vulnabank.R
import defdev.labs.droid.vulnabank.TransactionsViewModel
import defdev.labs.droid.vulnabank.data.Transaction
import defdev.labs.droid.vulnabank.theme.LabMono
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TransactionsScreen(model: TransactionsViewModel) {
    val state by model.uiState.collectAsStateWithLifecycle()
    val transactions by model.transactions.collectAsStateWithLifecycle()

    Box(Modifier.fillMaxSize()) {
        PullToRefreshBox(
            isRefreshing = state.isRefreshing || transactions == null,
            onRefresh = model::refresh,
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 104.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item {
                    Column(
                        Modifier.widthIn(max = 720.dp).fillMaxWidth().padding(bottom = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(stringResource(R.string.transactions), style = MaterialTheme.typography.titleLargeEmphasized)
                        if (state.isSending) {
                            LinearWavyProgressIndicator(Modifier.fillMaxWidth().padding(top = 8.dp))
                            Text(stringResource(R.string.please_wait), style = MaterialTheme.typography.bodyLarge)
                        }
                        state.sendError?.let {
                            Text(
                                stringResource(it),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }
                val list = transactions.orEmpty()
                if (transactions != null && list.isEmpty()) {
                    item {
                        Text(
                            stringResource(R.string.no_transactions),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.widthIn(max = 720.dp).fillMaxWidth(),
                        )
                    }
                }
                items(list, key = { it.id ?: it.date }) { transaction ->
                    TransactionRow(transaction, onDelete = { model.askDelete(transaction) })
                }
            }
        }
        ExtendedFloatingActionButton(
            text = { Text(stringResource(R.string.new_transaction)) },
            icon = { Icon(painterResource(R.drawable.ic_add), contentDescription = null) },
            onClick = model::openForm,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
        )
    }

    state.form?.let { form ->
        NewTransactionDialog(
            form = form,
            onChange = model::updateForm,
            onCancel = model::cancelForm,
            onConfirm = model::confirmForm,
        )
    }

    state.pendingDelete?.let { transaction ->
        AlertDialog(
            onDismissRequest = model::dismissDelete,
            title = { Text(stringResource(R.string.delete_title)) },
            text = { Text(stringResource(R.string.delete_message, transaction.hash.orEmpty())) },
            confirmButton = { TextButton(onClick = model::confirmDelete) { Text(stringResource(android.R.string.ok)) } },
            dismissButton = { TextButton(onClick = model::dismissDelete) { Text(stringResource(android.R.string.cancel)) } },
        )
    }
}

@Composable
private fun TransactionRow(transaction: Transaction, onDelete: () -> Unit) {
    val failed = transaction.state == 1
    Surface(
        modifier = Modifier.widthIn(max = 720.dp).fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = if (failed) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Row(
            Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                val label = when (transaction.state) {
                    0 -> stringResource(R.string.success)
                    1 -> stringResource(R.string.fail)
                    else -> ""
                }
                Text(
                    label,
                    style = MaterialTheme.typography.titleSmallEmphasized,
                    color = if (failed) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                )
                SelectionContainer {
                    Text(
                        transaction.hash.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = LabMono),
                    )
                }
                Text(
                    SimpleDateFormat.getInstance().format(Date(transaction.date)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = onDelete) {
                Icon(painterResource(R.drawable.ic_delete), contentDescription = stringResource(R.string.delete))
            }
        }
    }
}
