package com.chknkv.feature.addiction.domain.converter

import com.chknkv.feature.addiction.models.data.AddictionSelectedRequest

/**
 * Конвертирует множество выбранных ID привычек в тело запроса для сохранения выбора.
 */
internal fun Set<Int>.toAddictionSelectedRequest(): AddictionSelectedRequest =
    AddictionSelectedRequest(ids = toList())
