package com.samuel.jarvisguard

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.samuel.jarvisguard.data.SecurityRepository
import com.samuel.jarvisguard.ui.auth.PinScreen
import com.samuel.jarvisguard.ui.dashboard.DashboardScreen
import com.samuel.jarvisguard.ui.dashboard.SecurityActivityScreen
import com.samuel.jarvisguard.ui.onboarding.SetupScreen
import com.samuel.jarvisguard.ui.settings.SettingsScreen
import com.samuel.jarvisguard.ui.theme.JarvisGuardTheme
import com.samuel.jarvisguard.ui.theme.OledBlack

enum class MainDestination {
    SETUP,
    DASHBOARD,
    SETTINGS,
    ACTIVITY_LOG,
    CHANGE_PIN
}

class MainActivity : AppCompatActivity() {

    private lateinit var repository: SecurityRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = SecurityRepository.getInstance(this)

        setContent {
            JarvisGuardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = OledBlack
                ) {
                    val initialDestination = if (repository.preferences.isSetupComplete) {
                        MainDestination.DASHBOARD
                    } else {
                        MainDestination.SETUP
                    }

                    var currentDestination by remember { mutableStateOf(initialDestination) }

                    Crossfade(
                        targetState = currentDestination,
                        label = "MainScreenNavigation"
                    ) { destination ->
                        when (destination) {
                            MainDestination.SETUP -> {
                                SetupScreen(
                                    repository = repository,
                                    onSetupCompleted = {
                                        currentDestination = MainDestination.DASHBOARD
                                    }
                                )
                            }
                            MainDestination.DASHBOARD -> {
                                DashboardScreen(
                                    repository = repository,
                                    onNavigateToSettings = {
                                        currentDestination = MainDestination.SETTINGS
                                    },
                                    onNavigateToActivity = {
                                        currentDestination = MainDestination.ACTIVITY_LOG
                                    },
                                    onTestLockScreen = {
                                        val testIntent = Intent(this@MainActivity, LockActivity::class.java).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        startActivity(testIntent)
                                    }
                                )
                            }
                            MainDestination.SETTINGS -> {
                                SettingsScreen(
                                    repository = repository,
                                    onBack = {
                                        currentDestination = MainDestination.DASHBOARD
                                    },
                                    onChangePin = {
                                        currentDestination = MainDestination.CHANGE_PIN
                                    }
                                )
                            }
                            MainDestination.ACTIVITY_LOG -> {
                                SecurityActivityScreen(
                                    repository = repository,
                                    onBack = {
                                        currentDestination = MainDestination.DASHBOARD
                                    }
                                )
                            }
                            MainDestination.CHANGE_PIN -> {
                                var newPin by remember { mutableStateOf("") }
                                var isConfirming by remember { mutableStateOf(false) }
                                var pinError by remember { mutableStateOf<String?>(null) }

                                if (!isConfirming) {
                                    PinScreen(
                                        title = "NEW SECURITY PIN",
                                        subtitle = "Enter 6-digit backup code",
                                        errorMessage = pinError,
                                        onPinComplete = { entered ->
                                            newPin = entered
                                            pinError = null
                                            isConfirming = true
                                        },
                                        onCancel = {
                                            currentDestination = MainDestination.SETTINGS
                                        }
                                    )
                                } else {
                                    PinScreen(
                                        title = "CONFIRM NEW PIN",
                                        subtitle = "Re-enter the 6-digit backup code",
                                        errorMessage = pinError,
                                        onPinComplete = { confirm ->
                                            if (confirm == newPin) {
                                                repository.pinSecurityManager.savePin(confirm)
                                                currentDestination = MainDestination.SETTINGS
                                            } else {
                                                pinError = "PINs do not match. Try again."
                                                isConfirming = false
                                                newPin = ""
                                            }
                                        },
                                        onCancel = {
                                            currentDestination = MainDestination.SETTINGS
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

