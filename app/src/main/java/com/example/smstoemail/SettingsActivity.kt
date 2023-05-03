package com.example.smstoemail

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smstoemail.ui.theme.SmsToEmailTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmsToEmailTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SettingsView(this)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(caller: SettingsActivity) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val dataUtils = DataUtils()

    val host = dataUtils.loadSmtpData(context, dataUtils.SMTP_HOST)
    var port = dataUtils.loadSmtpData(context, dataUtils.SMTP_PORT)
    val email = dataUtils.loadSmtpData(context, dataUtils.SMTP_EMAIL)
    val pass = dataUtils.loadSmtpData(context, dataUtils.SMTP_PASS)

    try {
        port.toInt()
    } catch (e: Exception) {
        port = "0"
    }

    var smtpHost: String by rememberSaveable { mutableStateOf(host) }
    var smtpPort: Int by rememberSaveable { mutableStateOf(port.toInt()) }
    var smtpEmail: String by rememberSaveable { mutableStateOf(email) }
    var smtpPass: String by rememberSaveable { mutableStateOf(pass) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    var settingsStatus: String by rememberSaveable { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        TopAppBar(
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            title = {
                Text(
                    "SMTP Settings",
                    color = MaterialTheme.colorScheme.background
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    caller.finish()
                }) {
                    Icon(
                        Icons.Rounded.ArrowBack,
                        "Back",
                        tint = MaterialTheme.colorScheme.background
                    )
                }
            },
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = smtpHost,
            onValueChange = {
                smtpHost = it
                dataUtils.saveSmtpData(context, dataUtils.SMTP_HOST, smtpHost)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            label = { Text("SMTP Host") }
        )

        TextField(
            value = smtpPort.toString(),
            onValueChange = {
                try {
                    smtpPort = it.toInt()
                    dataUtils.saveSmtpData(context, dataUtils.SMTP_PORT, it)
                } catch (e: Exception) {
                    smtpPort = 0
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number
            ),
            label = { Text("SMTP Port") }
        )

        TextField(
            value = smtpEmail,
            onValueChange = {
                smtpEmail = it
                dataUtils.saveSmtpData(context, dataUtils.SMTP_EMAIL, smtpEmail)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email
            ),
            label = { Text("E-mail") }
        )

        TextField(
            value = smtpPass,
            onValueChange = {
                smtpPass = it
                dataUtils.saveSmtpData(context, dataUtils.SMTP_PASS, smtpPass)
            },
            label = { Text("Password") },
            singleLine = true,
            placeholder = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.VisibilityOff
                else Icons.Filled.Visibility

                // Please provide localized description for accessibility services
                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, description)
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            settingsStatus = "Sending E-mail, Please Wait ..."
            Thread {
                try {
                    SmtpManager().sendEmail(context, "This is a sample Text")
                    settingsStatus = "An E-mail was sent successfully"
                } catch (e: Exception) {
                    settingsStatus = e.message ?: e.toString()
                }
            }.start()
        }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Test E-mail Settings")
        }

        Text(settingsStatus, modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

@Preview(showBackground = true)
@Composable
fun Greeting2Preview() {
    SmsToEmailTheme {
        SettingsView(SettingsActivity())
    }
}