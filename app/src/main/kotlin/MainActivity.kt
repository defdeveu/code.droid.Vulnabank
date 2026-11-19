package defdev.labs.droid.vulnabank

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import defdev.labs.droid.vulnabank.theme.VulnaBankTheme
import defdev.labs.droid.vulnabank.ui.MainScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val container: AppContainer
        get() = (application as VulnaBankApplication).container

    private var resumedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        lifecycleScope.launch {
            container.appInForeground.collect {
                if (resumedOnce) {
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }
        setContent {
            VulnaBankTheme {
                MainScreen(
                    transactionsViewModel = viewModel(factory = container.viewModelFactory),
                    settingsViewModel = viewModel(factory = container.viewModelFactory),
                    onOpenUrl = { url -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) },
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        resumedOnce = true
    }
}
