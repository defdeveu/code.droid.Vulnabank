package defdev.labs.droid.vulnabank

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import defdev.labs.droid.vulnabank.data.TransactionsDatabase
import defdev.labs.droid.vulnabank.service.AuthRepository
import defdev.labs.droid.vulnabank.service.CryptoService
import defdev.labs.droid.vulnabank.service.DefaultAuthRepository
import defdev.labs.droid.vulnabank.service.DefaultCryptoService
import defdev.labs.droid.vulnabank.service.DefaultTransactionRepository
import defdev.labs.droid.vulnabank.service.HttpTransactionService
import defdev.labs.droid.vulnabank.service.TransactionRepository
import defdev.labs.droid.vulnabank.service.TransactionService
import kotlinx.coroutines.Dispatchers

class AppContainer(context: Context) {
    private val applicationContext: Context = context.applicationContext

    val cryptoService: CryptoService = DefaultCryptoService(applicationContext)

    val authRepository: AuthRepository = DefaultAuthRepository(
        sharedPreferences = applicationContext.getSharedPreferences(
            DefaultAuthRepository.PREFERENCES_NAME,
            Context.MODE_PRIVATE,
        ),
        cryptoService = cryptoService,
        defaultServerAddress = BuildConfig.LAB_ENDPOINT,
    )

    val database: TransactionsDatabase by lazy { TransactionsDatabase.create(applicationContext) }

    val transactionService: TransactionService by lazy {
        HttpTransactionService(authRepository.getServerAddress())
    }

    val transactionRepository: TransactionRepository by lazy {
        DefaultTransactionRepository(
            dao = database.transactionsDao(),
            cryptoService = cryptoService,
            transactionService = transactionService,
            ioDispatcher = Dispatchers.IO,
        )
    }

    val viewModelFactory: ViewModelProvider.Factory = viewModelFactory {
        initializer { LoginViewModel(authRepository) }
    }
}
