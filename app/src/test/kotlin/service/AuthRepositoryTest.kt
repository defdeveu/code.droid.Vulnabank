package defdev.labs.droid.vulnabank.service

import android.content.SharedPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthRepositoryTest {
    private val preferences = FakePreferences()
    private val repository = DefaultAuthRepository(preferences, FakeCrypto(), "https://example.test/request")

    @Test
    fun registrationIsDetected() {
        assertFalse(repository.hasRegistration())
        repository.register("1234")
        assertTrue(repository.hasRegistration())
    }

    @Test
    fun loginAcceptsRegisteredPin() {
        repository.register("1234")
        assertTrue(repository.login("1234"))
    }

    @Test
    fun loginRejectsOtherPin() {
        repository.register("1234")
        assertFalse(repository.login("4321"))
    }

    @Test
    fun registeringAgainReplacesPin() {
        repository.register("1234")
        repository.register("5678")
        assertFalse(repository.login("1234"))
        assertTrue(repository.login("5678"))
    }

    @Test
    fun serverAddressStartsWithDefault() {
        assertEquals("https://example.test/request", repository.getServerAddress())
        assertEquals("https://example.test/request", preferences.getString(DefaultAuthRepository.SERVER_ADDRESS, null))
    }

    @Test
    fun serverAddressCanBeChanged() {
        repository.setServerAddress("http://example.test/request")
        assertEquals("http://example.test/request", repository.getServerAddress())
    }
}

private class FakeCrypto : CryptoService {
    override fun encryptAES(message: String) = message
    override fun decryptAES(message: String) = message
    override fun encryptRSA() = ""
    override fun signRSA(message: String) = ""
    override fun convertPinToMD5(plainPin: String) = "h:$plainPin"
}

private class FakePreferences : SharedPreferences {
    val values = mutableMapOf<String, Any?>()

    override fun getAll(): MutableMap<String, *> = values
    override fun getString(key: String?, defValue: String?): String? = values[key] as String? ?: defValue
    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? = defValues
    override fun getInt(key: String?, defValue: Int): Int = defValue
    override fun getLong(key: String?, defValue: Long): Long = defValue
    override fun getFloat(key: String?, defValue: Float): Float = defValue
    override fun getBoolean(key: String?, defValue: Boolean): Boolean = defValue
    override fun contains(key: String?): Boolean = values.containsKey(key)
    override fun edit(): SharedPreferences.Editor = Editor()
    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) = Unit
    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) = Unit

    inner class Editor : SharedPreferences.Editor {
        private val pending = mutableMapOf<String, Any?>()
        override fun putString(key: String?, value: String?): SharedPreferences.Editor = apply { pending[checkNotNull(key)] = value }
        override fun putStringSet(key: String?, values: MutableSet<String>?): SharedPreferences.Editor = this
        override fun putInt(key: String?, value: Int): SharedPreferences.Editor = this
        override fun putLong(key: String?, value: Long): SharedPreferences.Editor = this
        override fun putFloat(key: String?, value: Float): SharedPreferences.Editor = this
        override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor = this
        override fun remove(key: String?): SharedPreferences.Editor = apply { pending[checkNotNull(key)] = null }
        override fun clear(): SharedPreferences.Editor = this
        override fun commit(): Boolean {
            apply()
            return true
        }
        override fun apply() {
            values.putAll(pending)
        }
    }
}
