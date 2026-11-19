package defdev.labs.droid.vulnabank

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import defdev.labs.droid.vulnabank.data.Transaction
import defdev.labs.droid.vulnabank.error.ErrorMapper
import defdev.labs.droid.vulnabank.service.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TransactionsUiState(
    val isSending: Boolean = false,
    val isRefreshing: Boolean = false,
    @param:StringRes val sendError: Int? = null,
    val pendingDelete: Transaction? = null,
    val form: TransactionForm? = null,
)

class TransactionsViewModel(
    private val repository: TransactionRepository,
    private val text: (Int) -> String,
) : ViewModel() {
    val transactions: StateFlow<List<Transaction>?> = repository.getTransactionList()
        .map { list -> list.sortedByDescending { it.date } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val state = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = state.asStateFlow()

    fun refresh() {
        state.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            repository.getTransactionList().first()
            state.update { it.copy(isRefreshing = false) }
        }
    }

    fun openForm() {
        if (!state.value.isSending) state.update { it.copy(form = TransactionForm()) }
    }

    fun updateForm(form: TransactionForm) {
        state.update { it.copy(form = form) }
    }

    fun cancelForm() {
        state.update { it.copy(form = null) }
    }

    fun confirmForm() {
        val form = state.value.form ?: return
        if (!form.isValid) return
        state.update { it.copy(form = null) }
        sendTransaction(form.message())
    }

    private fun sendTransaction(message: String) {
        Log.d("message TO server: ", "message: $message")
        state.update { it.copy(isSending = true, sendError = null) }
        viewModelScope.launch {
            val result = repository.sendTransactionMessage(message)
            state.update { it.copy(isSending = false) }
            result.fold(
                onSuccess = { transaction ->
                    Log.d("message FROM server: ", "message: $transaction")
                    repository.insertTransactionItem(transaction)
                },
                onFailure = { failure ->
                    val error = ErrorMapper.parseError(failure)
                    repository.insertTransactionItem(
                        Transaction(state = 1, hash = text(error), date = System.currentTimeMillis()),
                    )
                    state.update { it.copy(sendError = error) }
                },
            )
        }
    }

    fun askDelete(transaction: Transaction) {
        state.update { it.copy(pendingDelete = transaction) }
    }

    fun dismissDelete() {
        state.update { it.copy(pendingDelete = null) }
    }

    fun confirmDelete() {
        val transaction = state.value.pendingDelete ?: return
        state.update { it.copy(pendingDelete = null) }
        viewModelScope.launch { repository.deleteTransactionById(transaction) }
    }
}
