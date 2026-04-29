package com.chknkv.corepasscode.domain

/**
 * Вычисляет SHA-256 хэш для переданного пароля.
 * 
 * @param passcode Исходная строка пароля.
 * @return Hex-строка (в нижнем регистре), представляющая хэш пароля.
 */
expect fun hashPasscode(passcode: String): String
