package dev.fastcampus.payment.config

import org.jasypt.encryption.StringEncryptor
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


const val PASSWORD = "FASTCAMPUS"
const val ALGORITHM = "PBEWithMD5AndDES"

@Configuration
class EncryptionConfig {
    @Bean("jasyptStringEncryptor")
    fun encryptor(): StringEncryptor {
        val config = SimpleStringPBEConfig().apply {
            password = PASSWORD
            algorithm = ALGORITHM
            setKeyObtentionIterations("1000")
            setPoolSize("2")
            providerName = "SunJCE"
            setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator")
            stringOutputType = "base64"
        }
        return PooledPBEStringEncryptor().apply {
            setConfig(config)
        }
    }
}