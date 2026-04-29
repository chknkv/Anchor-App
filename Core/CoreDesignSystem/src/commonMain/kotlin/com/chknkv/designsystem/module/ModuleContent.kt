package com.chknkv.designsystem.module

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.Footnote
import com.chknkv.designsystem.Headline

/**
 * An enhanced module component that includes an optional title above and a description below.
 *
 * It can wrap the content in a [Module] card or display it as a plain block with paddings.
 *
 * @param modifier The modifier to be applied to the [Module] or content container.
 * @param inModule Whether to wrap the content in a [Module] component.
 * @param title Optional headline text displayed above the module.
 * @param titlePadding Paddings for the [title].
 * @param description Optional footnote text displayed below the module.
 * @param outPaddingValues External paddings for the [Module].
 * @param innerPaddingValues Internal paddings for the content.
 * @param content The composable content to be displayed.
 */
@Composable
fun ModuleContent(
    modifier: Modifier = Modifier,
    inModule: Boolean = true,
    title: String? = null,
    titlePadding: PaddingValues = titleModulePaddingValue,
    description: String? = null,
    outPaddingValues: PaddingValues = outPaddingModuleValues,
    innerPaddingValues: PaddingValues = innerPaddingModuleValues,
    content: @Composable BoxScope.() -> Unit
) {
    Column {
        if (title != null) {
            Headline(
                text = title,
                modifier = Modifier.padding(titlePadding),
                isSecondary = true
            )
        }

        if (inModule) {
            Module(
                modifier = modifier,
                outPaddingValues = outPaddingValues,
                innerPaddingValues = innerPaddingValues,
                content = content
            )
        } else {
            Box(
                modifier = Modifier.padding(innerPaddingValues)
            ) {
                content()
            }
        }

        if (description != null) {
            Footnote(
                text = description,
                modifier = Modifier.padding(start = 28.dp, top = 6.dp, end = 16.dp),
                isSecondary = true
            )
        }
    }
}

/**
 * Default padding values for the [ModuleContent] title.
 */
val titleModulePaddingValue = PaddingValues(start = 30.dp, top = 24.dp, end = 16.dp)
