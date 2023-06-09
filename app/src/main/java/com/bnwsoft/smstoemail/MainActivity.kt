package com.bnwsoft.smstoemail

import android.app.ActivityManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bnwsoft.smstoemail.ui.theme.SmsToEmailTheme

class MainActivity : ComponentActivity() {

    private fun requestSmsPermission() {
        val permission = "android.permission.RECEIVE_SMS"
        val grant = ContextCompat.checkSelfPermission(this, permission)
        if (grant != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 1)
        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
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
                    MainContent()
                }
            }
        }

        requestSmsPermission()
    }

    override fun onStart() {
        super.onStart()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

        if( isMyServiceRunning(SmsBackgroundService::class.java)) {
            Log.d("TAG", "The service is already running")
        }
        else {
            Log.d("TAG", "The service was not running")
            val serviceIntent = Intent(applicationContext, SmsBackgroundService::class.java)
            applicationContext.startForegroundService(serviceIntent)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent() {
    val context = LocalContext.current
    val currentText: String by rememberSaveable { mutableStateOf(DataUtils().loadLog(context)) }

    var emptyLogInfo=""
    if (currentText.isBlank()) {
        emptyLogInfo = "\n\n\n- The log is currently empty -"
        emptyLogInfo += "\n\n(Use Settings to update your configuration)"
    }

    Column {
        TopAppBar(
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            title = {
                Text(
                    stringResource(id = R.string.title_activity_main),
                    color = MaterialTheme.colorScheme.background
                )
            },
            actions = {
                IconButton(onClick = {
                    context.startActivity(Intent(context, FiltersActivity::class.java))
                }) {
                    Icon(
                        Icons.Filled.FilterAlt, "Filters",
                        tint = MaterialTheme.colorScheme.background
                    )
                }
                IconButton(onClick = {
                    context.startActivity(Intent(context, SettingsActivity::class.java))
                }) {
                    Icon(
                        Icons.Filled.Settings, "Settings",
                        tint = MaterialTheme.colorScheme.background
                    )
                }
            }
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
            Column {
                if (currentText.isBlank()) {
                    Text(
                        emptyLogInfo,
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
                else {
                    Text(
                        currentText.trim(), modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState(0))
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    SmsToEmailTheme {
        MainContent()
    }
}