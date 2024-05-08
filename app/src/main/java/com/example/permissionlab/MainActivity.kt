import android.content.Intent
import android.Manifest
import android.provider.Settings
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.permissionlab.ui.theme.PermissionLabTheme

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach { entry ->
                val permission = entry.key
                val isGranted = entry.value
                if (isGranted) {
                    showToast("Дозвіл для $permission надано")
                } else {
                    showToast(getPermissionExplanation(permission))
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, permission)) {
                        // Перехід до налаштувань додатку для надання дозволу
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.parse("package:$packageName")
                        startActivity(intent)
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PermissionLabTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PermissionScreen { permission ->
                        requestPermissionLauncher.launch(permission)
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun getPermissionExplanation(permission: String): String {
        return when (permission) {
            Manifest.permission.ACCESS_FINE_LOCATION -> "Пояснення, чому потрібен дозвіл для пошуку вашого місця розташування"
            Manifest.permission.READ_CONTACTS -> "Пояснення, чому потрібен дозвіл для читання ваших контактів"
            else -> "Пояснення, чому потрібен дозвіл"
        }
    }
}

@Composable
fun PermissionScreen(requestPermissionLauncher: (Array<String>) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                val permissionsToRequest = arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_CONTACTS
                )
                requestPermissionLauncher(permissionsToRequest)
            }
        ) {
            Text("Request Permissions")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PermissionScreen(requestPermissionLauncher = {})
}
