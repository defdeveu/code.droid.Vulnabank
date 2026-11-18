package defdev.labs.droid.vulnabank

import android.content.Context

class AppContainer(context: Context) {
    val applicationContext: Context = context.applicationContext
    val endpoint: String = BuildConfig.LAB_ENDPOINT
}
