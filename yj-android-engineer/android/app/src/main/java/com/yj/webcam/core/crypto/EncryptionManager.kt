package com.yj.webcam.core.crypto

import android.util.Base64
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptionManager @Inject constructor() {

    private val secureRandom = SecureRandom()
    private val keyPair: KeyPair = generateECDHKeyPair()

    fun getPublicKeyBase64(): String {
        return Base64.encodeToString(keyPair.public.encoded, Base64.NO_WRAP)
    }

    fun deriveSharedAESKey(peerPublicKeyBase64: String, salt: ByteArray): SecretKey {
        val peerKeyBytes = Base64.decode(peerPublicKeyBase64, Base64.NO_WRAP)
        val keyFactory = KeyFactory.getInstance("EC")
        val peerPublicKey: PublicKey = keyFactory.generatePublic(X509EncodedKeySpec(peerKeyBytes))

        val keyAgreement = KeyAgreement.getInstance("ECDH")
        keyAgreement.init(keyPair.private)
        keyAgreement.doPhase(peerPublicKey, true)
        val sharedSecret = keyAgreement.generateSecret()

        // Simple HKDF-like key derivation using SHA-256 for standard 256-bit AES key
        val messageDigest = java.security.MessageDigest.getInstance("SHA-256")
        messageDigest.update(salt)
        val keyBytes = messageDigest.digest(sharedSecret)
        return SecretKeySpec(keyBytes, 0, 32, "AES")
    }

    fun encryptPayload(data: ByteArray, secretKey: SecretKey): EncryptedPayload {
        val iv = ByteArray(12)
        secureRandom.nextBytes(iv)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val gcmSpec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)

        val ciphertext = cipher.doFinal(data)
        return EncryptedPayload(iv = iv, ciphertext = ciphertext)
    }

    fun decryptPayload(encryptedPayload: EncryptedPayload, secretKey: SecretKey): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val gcmSpec = GCMParameterSpec(128, encryptedPayload.iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)
        return cipher.doFinal(encryptedPayload.ciphertext)
    }

    fun generateSalt(): ByteArray {
        val salt = ByteArray(16)
        secureRandom.nextBytes(salt)
        return salt
    }

    private fun generateECDHKeyPair(): KeyPair {
        val kpg = KeyPairGenerator.getInstance("EC")
        kpg.initialize(256)
        return kpg.generateKeyPair()
    }
}

data class EncryptedPayload(
    val iv: ByteArray,
    val ciphertext: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as EncryptedPayload
        if (!iv.contentEquals(other.iv)) return false
        if (!ciphertext.contentEquals(other.ciphertext)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = iv.contentHashCode()
        result = 31 * result + ciphertext.contentHashCode()
        return result
    }
}
