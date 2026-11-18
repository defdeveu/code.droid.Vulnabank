package defdev.labs.droid.vulnabank.service

import android.content.SharedPreferences
import androidx.core.content.edit

interface AuthRepository {
    fun register(password: String)
    fun login(password: String): Boolean
    fun hasRegistration(): Boolean
    fun getServerAddress(): String
    fun setServerAddress(serverAddress: String)
}

class DefaultAuthRepository(
    private val sharedPreferences: SharedPreferences,
    private val cryptoService: CryptoService,
    private val defaultServerAddress: String,
) : AuthRepository {

    override fun getServerAddress(): String {
        var serverAddress = sharedPreferences.getString(SERVER_ADDRESS, null)
        if (serverAddress.isNullOrEmpty()) {
            serverAddress = defaultServerAddress
            setServerAddress(serverAddress)
        }
        return serverAddress
    }

    override fun setServerAddress(serverAddress: String) {
        sharedPreferences.edit { putString(SERVER_ADDRESS, serverAddress) }
    }

    override fun register(password: String) {
        sharedPreferences.edit { putString(PASSWORD, password.md5()) }
    }

    override fun login(password: String): Boolean =
        sharedPreferences.getString(PASSWORD, null) ?: "".md5() == password.md5()

    override fun hasRegistration(): Boolean =
        sharedPreferences.contains(PASSWORD)

    private fun String.md5(): String = cryptoService.convertPinToMD5(this)

    companion object {
        const val PREFERENCES_NAME = "shared_pref"
        const val PASSWORD = "PASSWORD"
        const val SERVER_ADDRESS = "SERVER_ADDRESS"
    }
}
