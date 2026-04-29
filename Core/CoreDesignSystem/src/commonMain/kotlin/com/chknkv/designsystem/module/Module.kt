package com.chknkv.designsystem.module

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.theme.Tokens
import com.chknkv.designsystem.theme.getThemedColor

/**
 * A container component (card) used to group related content.
 *
 * Provides standard styling including background color, clipping to [ModuleShape],
 * and predefined external and internal paddings.
 *
 * @param modifier The modifier to be applied to the module's layout.
 * @param outPaddingValues External padding applied around the entire module.
 * @param innerPaddingValues Internal padding applied inside the module's background.
 * @param shape The shape of the module container. Defaults to [ModuleShape].
 * @param content The composable content to be displayed inside the module.
 */
@Composable
fun Module(
    modifier: Modifier = Modifier,
    outPaddingValues: PaddingValues = outPaddingModuleValues,
    innerPaddingValues: PaddingValues = innerPaddingModuleValues,
    shape: Shape = ModuleShape,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(outPaddingValues)
            .clip(shape)
            .background(Tokens.Module.getThemedColor())
    ) {
        Box(modifier = Modifier.padding(innerPaddingValues)) {
            content()
        }
    }
}

/**
 * Default external padding values for [Module].
 */
val outPaddingModuleValues = PaddingValues(
    top = 8.dp,
    start = 16.dp,
    end = 16.dp,
    bottom = 0.dp
)

/**
 * Default internal padding values for [Module].
 */
val innerPaddingModuleValues = PaddingValues(
    horizontal = 14.dp,
    vertical = 0.dp
)

/**
 * Standard corner radius for all module containers.
 */
val ModuleShape = RoundedCornerShape(24.dp)
