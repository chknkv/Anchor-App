package com.chknkv.feature.addiction.domain.converter

import com.chknkv.feature.addiction.models.data.AddictionSelectionItemResponse
import com.chknkv.feature.addiction.models.domain.select.Addiction

/**
 * Конвертирует элемент группы привычек для онбординг-выбора из ответа сервера в доменную модель.
 */
internal fun AddictionSelectionItemResponse.toDomain(): Addiction = Addiction(id = id, name = name)
