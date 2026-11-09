package com.samuel.jarvisguard

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.samuel.jarvisguard.data.SecurityEvent
import com.samuel.jarvisguard.data.SecurityRepository
import com.samuel.jarvisguard.security.PinSecurityManager
import com.samuel.jarvisguard.ui.auth.LockScreen
import com.samuel.jarvisguard.ui.auth.PinScreen
import com.samuel.jarvisguard.ui.components.ScannerState
import com.samuel.jarvisguard.ui.theme.JarvisGuardTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LockActivity : AppCompatActivity() {

    private lateinit var repository: SecurityRepository
    private var isAuthSuccess = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Protect screen against capture and recents previews
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        repository = SecurityRepository.getInstance(this)
        repository.sessionManager.isLockActivityActive.set(true)

        // Intercept back button to return to home screen (protects WhatsApp)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_HOME)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(homeIntent)
                finish()
            }
        })

        setContent {
            JarvisGuardTheme {
                var showPinScreen by remember { mutableStateOf(false) }
                var scannerState by remember { mutableStateOf(ScannerState.SCANNING) }
                var pinErrorMessage by remember { mutableStateOf<String?>(null) }
                var lockoutRemaining by remember { mutableStateOf(0) }

                fun triggerBiometrics() {
                    val biometricManager = repository.biometricAuthManager
                    if (repository.preferences.isBiometricEnabled && biometricManager.isBiometricAvailable()) {
                        scannerState = ScannerState.SCANNING
                        biometricManager.authenticate(
                            activity = this@LockActivity,
                            onSuccess = {
                                isAuthSuccess = true
                                scannerState = ScannerState.SUCCESS
                                repository.logEvent(
                                    SecurityEvent.Type.AUTH_SUCCESS,
                                    "Fingerprint verified successfully."
                                )
                                repository.sessionManager.grantSession()
                                lifecycleScope.launch {
                                    delay(700)
                                    finish()
                                }
                            },
                            onFailed = {
                                scannerState = ScannerState.FAILED
                                repository.logEvent(
                                    SecurityEvent.Type.AUTH_FAILURE,
                                    "Fingerprint not recognized."
                                )
                            },
                            onError = { _, errString ->
                                scannerState = ScannerState.FAILED
                                repository.logEvent(
                                    SecurityEvent.Type.AUTH_FAILURE,
                                    "Biometric error: $errString"
                                )
                            },
                            onNegativeButton = {
                                showPinScreen = true
                            }
                        )
                    } else {
                        // If biometrics not enrolled or disabled, directly prompt PIN
                        showPinScreen = true
                    }
                }

                // Auto trigger biometric on start
                androidx.compose.runtime.LaunchedEffect(Unit) {
                    delay(300)
                    triggerBiometrics()
                }

                Crossfade(targetState = showPinScreen, label = "LockScreenMode") { isPin ->
                    if (isPin) {
                        PinScreen(
                            errorMessage = pinErrorMessage,
                            lockoutSeconds = lockoutRemaining,
                            onPinComplete = { enteredPin ->
                                val result = repository.pinSecurityManager.verifyPin(enteredPin)
                                when (result) {
                                    is PinSecurityManager.VerificationResult.Success -> {
                                        isAuthSuccess = true
                                        repository.logEvent(
                                            SecurityEvent.Type.PIN_SUCCESS,
                                            "Security PIN verified."
                                        )
                                        repository.sessionManager.grantSession()
                                        finish()
                                    }
                                    is PinSecurityManager.VerificationResult.Failure -> {
                                        repository.logEvent(
                                            SecurityEvent.Type.PIN_FAILURE,
                                            "Invalid PIN attempt."
                                        )
                                        pinErrorMessage = "Invalid PIN. ${result.remainingAttemptsBeforeLockout} attempts left."
                                    }
                                    is PinSecurityManager.VerificationResult.LockedOut -> {
                                        repository.logEvent(
                                            SecurityEvent.Type.PIN_FAILURE,
                                            "PIN locked out."
                                        )
                                        lockoutRemaining = result.lockTimeRemainingSeconds
                                        pinErrorMessage = "Too many failures. Wait ${result.lockTimeRemainingSeconds}s"
                                    }
                                }
                            },
                            onSwitchToBiometric = if (repository.biometricAuthManager.isBiometricAvailable()) {
                                {
                                    showPinScreen = false
                                    triggerBiometrics()
                                }
                            } else null
                        )
                    } else {
                        LockScreen(
                            scannerState = scannerState,
                            onScannerClick = {
                                triggerBiometrics()
                            },
                            onSecurityPanelClick = {
                                showPinScreen = true
                            },
                            onPinRequested = {
                                showPinScreen = true
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        repository.sessionManager.isLockActivityActive.set(false)
        repository.sessionManager.isAuthPending.set(false)
    }
}

