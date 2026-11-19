package defdev.labs.droid.vulnabank

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner

class VulnaBankApplication : Application(), DefaultLifecycleObserver {
    val container: AppContainer by lazy { AppContainer(this) }

    override fun onCreate() {
        super<Application>.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        container.onAppInForeground()
    }
}
