package defdev.labs.droid.vulnabank

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import defdev.labs.droid.vulnabank.theme.VulnaBankTheme
import defdev.labs.droid.vulnabank.ui.LoginScreen

class LoginActivity : ComponentActivity() {
    private val container: AppContainer
        get() = (application as VulnaBankApplication).container

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        setContent {
            VulnaBankTheme {
                val model: LoginViewModel = viewModel(factory = container.viewModelFactory)
                LoginScreen(model) { showMain() }
            }
        }
    }

    private fun showMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
