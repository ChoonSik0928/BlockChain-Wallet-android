package com.choonsik.blockchainwallet.util.crypt

import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment
import java.util.concurrent.Executors

object CryptManager {

    fun encryptPlainText(keyAlias: String, data: String, password: String): String {
        val keyStore = KeyStoreWrapper.createKey(keyAlias, password)
        return CipherWrapper.encrypt(data, keyStore, useInitializationVector = true)
    }

    fun decryptPlainText(keyAlias: String, data: String, password: String): String {
        val key = KeyStoreWrapper.getKey(keyAlias, password)
        return CipherWrapper.decrypt(data, key, useInitializationVector = true)
    }

    fun encryptWithBioMetric(
        fragment: Fragment,
        keyAlias: String,
        data: String,
        password: String,
        success: (result: String) -> Unit,
        error: (errorCode: Int, errorMessage: String) -> Unit,
        useInitializationVector: Boolean = true
    ) {
        val executor = Executors.newSingleThreadExecutor()
        val keyStore = KeyStoreWrapper.createKey(keyAlias, password)
        val cipher = CipherWrapper.getEncryptCipher(keyStore)

        val biometricPrompt =
            BiometricPrompt(
                fragment,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        val encryptedData =
                            CipherWrapper.encrypt(data, cipher, useInitializationVector)
                        success(encryptedData)
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        error(errorCode, errString.toString())
                    }
                })

        val promptInfo = biometricPromptInfo()
        biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
    }

    fun decryptWithBioMetric(
        fragment: Fragment,
        keyAlias: String,
        data: String,
        password: String,
        success: (result: String) -> Unit,
        error: (errorCode: Int, errorMessage: String) -> Unit,
        useInitializationVector: Boolean = true
    ) {
        val executor = Executors.newSingleThreadExecutor()
        val key = KeyStoreWrapper.getKey(keyAlias, password)
        var cipher = if (useInitializationVector) {
            CipherWrapper.getDecryptCipher(key, CipherWrapper.getIVSpec(data))
        } else {
            CipherWrapper.getDecryptCipher(key)
        }

        val biometricPrompt =
            BiometricPrompt(
                fragment,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        val decryptData =
                            CipherWrapper.decrypt(cipher, data, useInitializationVector)
                        success(decryptData)
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        error(errorCode, errString.toString())
                    }
                })

        val promptInfo = biometricPromptInfo()
        biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
    }

    private fun biometricPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle("Title")
            .setSubtitle("Subtitle")
            .setDescription("Description")
            .setNegativeButtonText("Cancel")
            .build()
    }
}