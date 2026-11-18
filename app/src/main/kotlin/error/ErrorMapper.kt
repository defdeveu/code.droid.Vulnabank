package defdev.labs.droid.vulnabank.error

import androidx.annotation.StringRes
import defdev.labs.droid.vulnabank.R
import java.net.UnknownHostException

object ErrorMapper {

    @StringRes
    fun parseError(throwable: Throwable?): Int = when (throwable) {
        is UnknownHostException -> R.string.no_internet
        else -> R.string.general
    }
}
