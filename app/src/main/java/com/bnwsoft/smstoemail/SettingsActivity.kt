package com.bnwsoft.smstoemail

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bnwsoft.smstoemail.ui.theme.SmsToEmailTheme

class SettingsActivity : ComponentActivity() {

    private fun requestSmsPermission() {
        val permission = "android.permission.SEND_SMS"
        val grant = ContextCompat.checkSelfPermission(this, permission)
        if (grant != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 1)
        }
    }

    override fun onStart() {
        super.onStart()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }

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

        requestSmsPermission()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(caller: SettingsActivity) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val dataUtils = DataUtils()

    val fwdSel = dataUtils.loadSmtpData(context, dataUtils.fwdSelection)

    val host = dataUtils.loadSmtpData(context, dataUtils.smtpHost)
    var port = dataUtils.loadSmtpData(context, dataUtils.smtpPort)
    val email = dataUtils.loadSmtpData(context, dataUtils.smtpEmail)
    val toEmail = dataUtils.loadSmtpData(context, dataUtils.smtpToEmail)
    val pass = dataUtils.loadSmtpData(context, dataUtils.smtpPass)

    val toPhone = dataUtils.loadSmtpData(context, dataUtils.smsToPhone)

    try {
        port.toInt()
    } catch (e: Exception) {
        port = "587"

        if (!LocalInspectionMode.current) {
            dataUtils.saveSmtpData(context, dataUtils.smtpPort, port)
        }
    }

    var fwdSelection: String by rememberSaveable { mutableStateOf(fwdSel) }

    var smtpHost: String by rememberSaveable { mutableStateOf(host) }
    var smtpPort: Int by rememberSaveable { mutableStateOf(port.toInt()) }
    var smtpEmail: String by rememberSaveable { mutableStateOf(email) }
    var smtpPass: String by rememberSaveable { mutableStateOf(pass) }
    var smtpToEmail: String by rememberSaveable { mutableStateOf(toEmail) }

    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var settingsStatus: String by rememberSaveable { mutableStateOf("") }

    var smsToPhone by rememberSaveable { mutableStateOf(toPhone) }

    Column {
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

        Box {
            if (!isSystemInDarkTheme()) {
                Image(
                    painterResource(id = R.drawable.background),
                    contentDescription = "",
                    contentScale = ContentScale.FillBounds, // or some other scale
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Spacer(modifier = Modifier.height(5.dp))

                //
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Forward to", modifier = Modifier.align(Alignment.CenterVertically))
                    RadioButton(
                        selected = (fwdSelection == "Email"),
                        onClick = {
                            fwdSelection = "Email"
                            dataUtils.saveSmtpData(context, dataUtils.fwdSelection, fwdSelection)
                        }
                    )
                    Text(
                        text = "E-mail",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    RadioButton(
                        selected = (fwdSelection == "SMS"),
                        onClick = {
                            fwdSelection = "SMS"
                            dataUtils.saveSmtpData(context, dataUtils.fwdSelection, fwdSelection)
                        }
                    )
                    Text(
                        text = "SMS",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                //
                if (fwdSelection == "Email") {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "Sender Settings:",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        TextField(
                            value = smtpHost,
                            onValueChange = {
                                smtpHost = it
                                dataUtils.saveSmtpData(context, dataUtils.smtpHost, smtpHost)
                            },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .fillMaxWidth(0.8f),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Uri
                            ),
                            label = { Text("SMTP Host") }
                        )

                        TextField(
                            value = smtpPort.toString(),
                            onValueChange = {
                                try {
                                    smtpPort = it.toInt()
                                    dataUtils.saveSmtpData(context, dataUtils.smtpPort, it)
                                } catch (e: Exception) {
                                    smtpPort = 0
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .fillMaxWidth(0.8f),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Number
                            ),
                            label = { Text("SMTP Port (TLS)") }
                        )

                        TextField(
                            value = smtpEmail,
                            onValueChange = {
                                smtpEmail = it
                                dataUtils.saveSmtpData(context, dataUtils.smtpEmail, smtpEmail)
                            },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .fillMaxWidth(0.8f),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Email
                            ),
                            label = { Text("E-mail (From)") }
                        )

                        TextField(
                            value = smtpPass,
                            onValueChange = {
                                smtpPass = it
                                dataUtils.saveSmtpData(context, dataUtils.smtpPass, smtpPass)
                            },
                            label = { Text("Password") },
                            singleLine = true,
                            placeholder = { Text("Password") },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .fillMaxWidth(0.8f),
                            trailingIcon = {
                                val image = if (passwordVisible)
                                    Icons.Filled.VisibilityOff
                                else Icons.Filled.Visibility

                                // Please provide localized description for accessibility services
                                val description =
                                    if (passwordVisible) "Hide password" else "Show password"

                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = image, description)
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            "Recipient Settings:",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        TextField(
                            value = smtpToEmail,
                            onValueChange = {
                                smtpToEmail = it
                                dataUtils.saveSmtpData(
                                    context,
                                    dataUtils.smtpToEmail,
                                    smtpToEmail
                                )
                            },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .fillMaxWidth(0.8f),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Email
                            ),
                            label = { Text("E-mail (To)") }
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Button(onClick = {
                            settingsStatus = "Sending E-mail, Please Wait ..."
                            Thread {
                                settingsStatus = try {
                                    SmtpManager().sendEmail(
                                        context,
                                        "This is a sample Text",
                                        "Test Sms To Email"
                                    )
                                    "An E-mail was sent successfully"
                                } catch (e: Exception) {
                                    e.message ?: e.toString()
                                }
                            }.start()
                        }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                            Text("Test E-mail Settings")
                        }

                        Text(
                            settingsStatus,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
                else if (fwdSelection == "SMS") {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "Recipient Settings:",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        TextField(
                            value = smsToPhone,
                            onValueChange = {
                                smsToPhone = it
                                dataUtils.saveSmtpData(context, dataUtils.smsToPhone, smsToPhone)
                            },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .fillMaxWidth(0.8f),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.Phone
                            ),
                            label = { Text("Phone Number") }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Greeting2Preview() {
    SmsToEmailTheme {
        SettingsView(SettingsActivity())
    }
}