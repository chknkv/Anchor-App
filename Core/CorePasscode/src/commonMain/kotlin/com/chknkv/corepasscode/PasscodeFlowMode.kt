package com.chknkv.corepasscode

/**
 * Режим работы флоу управления паролем (passcode).
 * 
 * Данный интерфейс определяет поведение навигации и логику экранов внутри [PasscodeFlow].
 */
sealed interface PasscodeFlowMode {

    /**
     * Режим входа. 
     * Если пароль не установлен — запускает создание. 
     * Если установлен — запрашивает ввод для входа в приложение.
     */
    data object Enter : PasscodeFlowMode

    /**
     * Режим смены пароля. 
     * Всегда запрашивает текущий пароль (если есть) перед переходом к созданию нового.
     */
    data object Change : PasscodeFlowMode
}
