package com.chknkv.feature.addiction.data.converter

import com.chknkv.feature.addiction.data.converter.base.toDomain
import com.chknkv.feature.addiction.models.data.AddictionAllInGroup
import com.chknkv.feature.addiction.models.data.AddictionsAllGroup
import com.chknkv.feature.addiction.models.data.AddictionAllGroupsBody
import com.chknkv.feature.addiction.models.domain.AddictionAllGroups
import com.chknkv.feature.addiction.models.domain.AddictionAllGroup

/**
 * Конвертирует data-model [AddictionAllGroupsBody] в список domain-model [AddictionAllGroups].
 */
internal fun AddictionAllGroupsBody.toDomain(): List<AddictionAllGroups> =
    items?.map { it.toDomain(isCreateNewAvailable) } ?: emptyList()

/**
 * Конвертирует data-model [AddictionsAllGroup] в domain-model [AddictionAllGroups].
 *
 * @param isCreateNewAvailable Флаг доступности создания новой привычки из тела ответа.
 */
internal fun AddictionsAllGroup.toDomain(isCreateNewAvailable: Boolean): AddictionAllGroups = AddictionAllGroups(
    category = categoryKey.toDomain(),
    addictions = addictions.map { it.toDomain() },
    isCreateNewAvailable = isCreateNewAvailable,
)

/**
 * Конвертирует data-model [AddictionAllInGroup] в domain-model [AddictionAllGroup].
 */
internal fun AddictionAllInGroup.toDomain(): AddictionAllGroup = AddictionAllGroup(
    id = id,
    name = name,
    icon = iconKey.toDomain(),
    gradient = gradientKey.toDomain(),
    controlDays = controlDays
)
