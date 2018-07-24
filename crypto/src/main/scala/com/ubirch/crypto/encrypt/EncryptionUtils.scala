package com.ubirch.crypto.encrypt

import com.ubirch.crypto.encrypt.crypto.JavaCryptoEncryption

object AES extends JavaCryptoEncryption()

object DES extends JavaCryptoEncryption("DES")


