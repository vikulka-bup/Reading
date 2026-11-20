package com.example.reading

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.reading.ui.theme.DarkText
import com.example.reading.ui.theme.PastelPink
import com.example.reading.ui.theme.PastelPinkDark
import com.example.reading.ui.theme.ReadingTheme
import java.io.InputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReadingTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    TxtReaderScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun TxtReaderScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    var fileContent by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isReading by remember { mutableStateOf(false) }  // false = выбор, true = чтение

    val openFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result.data?.data
            if (uri != null) {
                try {
                    val text = readTextFromUri(context, uri)
                    if (text.isBlank()) {
                        errorMessage = "Файл пустой"
                        fileContent = null
                    } else {
                        fileContent = text
                        errorMessage = null
                        isReading = true
                    }
                } catch (e: Exception) {
                    errorMessage = "Ошибка чтения файла: ${e.message}"
                    fileContent = null
                }
            }
        } else {
            Toast.makeText(context, "Файл не выбран", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (!isReading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp) // отступ между кнопкой и ошибкой
                ) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                            intent.addCategory(Intent.CATEGORY_OPENABLE)
                            intent.type = "text/plain"
                            openFileLauncher.launch(intent)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PastelPink,
                            contentColor = DarkText
                        )
                    ) {
                        Text("Открыть файл")
                    }

                    val error = errorMessage
                    if (error != null) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    TextButton(
                        onClick = {
                            isReading = false
                            fileContent = null
                            errorMessage = null
                        }
                    ) {
                        Text("← Назад", color = PastelPinkDark)
                    }
                }
                val content = fileContent
                if (content != null) {
                    Text(
                        text = content,
                        style = MaterialTheme.typography.bodyLarge,
                        color = DarkText,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    )
                }
            }
        }
    }
}

private fun readTextFromUri(context: Context, uri: Uri): String {
    val inputStream: InputStream = context.contentResolver.openInputStream(uri)
        ?: throw Exception("Не удалось открыть файл")
    try {
        val reader = inputStream.bufferedReader()
        return reader.readText()

    }
    finally {
        inputStream.close()
    }
}