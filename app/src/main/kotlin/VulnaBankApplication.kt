package defdev.labs.droid.vulnabank

import android.app.Application

class VulnaBankApplication : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}
