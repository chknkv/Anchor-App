package com.chknkv.corepasscode.di

import com.chknkv.corepasscode.domain.BiometricAuthenticator
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * iOS-реализация платформенного модуля passcode.
 */
actual val platformPasscodeModule: Module = module {
    single { BiometricAuthenticator() }
}
