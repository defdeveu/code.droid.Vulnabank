package defdev.labs.droid.vulnabank

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import defdev.labs.droid.vulnabank.theme.VulnaBankTheme
import defdev.labs.droid.vulnabank.ui.ContentColumn
import defdev.labs.droid.vulnabank.ui.LabHeader

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        setContent {
            VulnaBankTheme {
                Scaffold(topBar = { LabHeader() }) { insets ->
                    ContentColumn(Modifier.padding(insets)) {
                        Text(
                            stringResource(R.string.transactions),
                            style = MaterialTheme.typography.titleLargeEmphasized,
                        )
                    }
                }
            }
        }
    }
}
