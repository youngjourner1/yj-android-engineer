package com.yj.webcam.core.crypto

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class EncryptionManagerTest {

    @Test
    fun testEncryptionAndDecryptionFlow() {
        val managerAlice = EncryptionManager()
        val managerBob = EncryptionManager()

        val alicePubKey = managerAlice.getPublicKeyBase64()
        val bobPubKey = managerBob.getPublicKeyBase64()

        val salt = managerAlice.generateSalt()

        val aliceAESKey = managerAlice.deriveSharedAESKey(bobPubKey, salt)
        val bobAESKey = managerBob.deriveSharedAESKey(alicePubKey, salt)

        assertNotNull(aliceAESKey)
        assertNotNull(bobAESKey)

        val sampleText = "YJWebcamSecretFrameDataPayload".toByteArray(Charsets.UTF_8)
        val encrypted = managerAlice.encryptPayload(sampleText, aliceAESKey)

        val decrypted = managerBob.decryptPayload(encrypted, bobAESKey)
        assertArrayEquals(sampleText, decrypted)
    }
}
