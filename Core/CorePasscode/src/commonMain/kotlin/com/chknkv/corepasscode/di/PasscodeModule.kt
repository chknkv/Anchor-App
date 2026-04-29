package com.chknkv.corepasscode.di

import com.chknkv.corepasscode.domain.PasscodeRepository
import com.chknkv.corepasscode.domain.PasscodeRepositoryImpl
import com.chknkv.corepasscode.presentation.biometrysetup.BiometrySetupViewModel
import com.chknkv.corepasscode.presentation.createpasscode.CreatePasscodeViewModel
import com.chknkv.corepasscode.presentation.enterpasscode.EnterPasscodeViewModel
import com.russhwolf.settings.Settings
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val passcodeSettingsQualifier = named("corePasscodeSettings")

/**
 * Koin-модуль passcode.
 *
 * **Подключение:**
 * ```kotlin
 * startKoin {
 *     modules(
 *         coreUtilsModule(AppIdentifier.DS),
 *         corePasscodeModule,
 *     )
 * }
 * ```
 *
 * Для отображения [com.chknkv.corepasscode.PasscodeFlow] достаточно вызвать:
 * ```kotlin
 * PasscodeFlow(
 *     mode = PasscodeFlowMode.Enter,
 *     onBack = null,
 *     onEnterSuccess = { /* ... */ },
 * )
 * ```
 */
val corePasscodeModule = module {
    single(passcodeSettingsQualifier) { Settings() }
    single<PasscodeRepository> { PasscodeRepositoryImpl(get(passcodeSettingsQualifier)) }

    viewModel { CreatePasscodeViewModel(get()) }
    viewModel { EnterPasscodeViewModel(get(), get()) }
    viewModel { BiometrySetupViewModel(get(), get()) }

    includes(platformPasscodeModule)
}

/**
 * Платформенная часть модуля (реализована в androidMain и iosMain).
 * Регистрирует [com.chknkv.corepasscode.domain.BiometricAuthenticator].
 */
expect val platformPasscodeModule: Module
