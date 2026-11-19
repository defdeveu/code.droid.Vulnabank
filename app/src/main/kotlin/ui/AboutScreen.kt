package defdev.labs.droid.vulnabank.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import defdev.labs.droid.vulnabank.BuildConfig
import defdev.labs.droid.vulnabank.R
import defdev.labs.droid.vulnabank.theme.LabMono

@Composable
fun AboutScreen(onOpenUrl: (String) -> Unit) {
    val contributors = listOf(
        R.string.gyozo_fule to R.string.fgyozo_linkedin,
        R.string.zsombor_kovacs to R.string.zsombor_linkedin,
        R.string.timurxyz to R.string.timur_linkedin,
    )
    ContentColumn {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.app_info), style = MaterialTheme.typography.titleLargeEmphasized)
            Text(
                stringResource(R.string.application_version, BuildConfig.VERSION_NAME),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(stringResource(R.string.contributors), style = MaterialTheme.typography.titleSmallEmphasized)
            contributors.forEach { (name, url) ->
                val link = stringResource(url)
                TextButton(onClick = { onOpenUrl(link) }) {
                    Text(stringResource(name), style = MaterialTheme.typography.bodySmall.copy(fontFamily = LabMono))
                }
            }
        }
        val web = stringResource(R.string.defdeveu_web)
        TextButton(onClick = { onOpenUrl(web) }) {
            Text(stringResource(R.string.copyright), style = MaterialTheme.typography.bodyMedium)
        }
    }
}
