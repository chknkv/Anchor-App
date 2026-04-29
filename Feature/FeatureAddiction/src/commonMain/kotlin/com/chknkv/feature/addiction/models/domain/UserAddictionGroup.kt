package com.chknkv.feature.addiction.models.domain

/**
 * Доменная модель группы привычек пользователя по категории.
 *
 * @property category Категория группы.
 * @property addictions Список привычек пользователя в этой категории.
 */
internal data class UserAddictionGroup(
    val category: AddictionCategory,
    val addictions: List<UserAddiction>,
)
