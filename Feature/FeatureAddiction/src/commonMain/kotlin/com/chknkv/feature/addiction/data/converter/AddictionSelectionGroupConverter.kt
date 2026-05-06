package com.chknkv.feature.addiction.data.converter

import com.chknkv.feature.addiction.data.converter.base.toDomain
import com.chknkv.feature.addiction.models.data.AddictionSelectionGroups
import com.chknkv.feature.addiction.models.data.AddictionSelectionGroupsResponse
import com.chknkv.feature.addiction.models.data.AddictionSelectionItem
import com.chknkv.feature.addiction.models.domain.AddictionSelectionInGroup
import com.chknkv.feature.addiction.models.domain.AddictionsSelectionGroups

/**
 * Конвертирует [AddictionSelectionGroupsResponse] в список domain-model [AddictionsSelectionGroups].
 */
internal fun AddictionSelectionGroupsResponse.toDomain(): List<AddictionsSelectionGroups> = items.map { it.toDomain() }

/**
 * Конвертирует data-model [AddictionSelectionGroups] в domain-model [AddictionsSelectionGroups].
 */
internal fun AddictionSelectionGroups.toDomain(): AddictionsSelectionGroups = AddictionsSelectionGroups(
    category = categoryKey.toDomain(),
    addictions = addictions.map { it.toDomain() },
)

/**
 * Конвертирует data-model [AddictionSelectionItem] в domain-model [AddictionSelectionInGroup].
 */
internal fun AddictionSelectionItem.toDomain(): AddictionSelectionInGroup = AddictionSelectionInGroup(id = id, name = name)
