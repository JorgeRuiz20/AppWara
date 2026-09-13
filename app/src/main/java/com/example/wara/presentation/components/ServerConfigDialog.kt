package com.example.wara.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wara.ui.theme.WaraPrimary

@Composable
fun ServerConfigDialog(
    currentUrl: String,
    onDismissRequest: () -> Unit,
    onSaveUrl: (String) -> Unit
) {
    var inputUrl by remember(currentUrl) { mutableStateOf(currentUrl) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = "Configuración de Servidor",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Selecciona o ingresa la URL base del backend WARA:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = inputUrl,
                    onValueChange = { inputUrl = it },
                    label = { Text("URL del Backend") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Accesos directos recomendados:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                PresetItem(
                    title = "Dispositivo Físico USB (127.0.0.1:5050)",
                    url = "http://127.0.0.1:5050/",
                    onClick = { inputUrl = "http://127.0.0.1:5050/" }
                )

                PresetItem(
                    title = "Emulador Android AVD (10.0.2.2:5050)",
                    url = "http://10.0.2.2:5050/",
                    onClick = { inputUrl = "http://10.0.2.2:5050/" }
                )

                PresetItem(
                    title = "IP Red Local PC (10.242.115.116:5050)",
                    url = "http://10.242.115.116:5050/",
                    onClick = { inputUrl = "http://10.242.115.116:5050/" }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSaveUrl(inputUrl) },
                colors = ButtonDefaults.buttonColors(containerColor = WaraPrimary)
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun PresetItem(
    title: String,
    url: String,
    onClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = url,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
