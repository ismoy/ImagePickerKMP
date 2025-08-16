
package io.github.ismoy.imagepickerkmp.di

import androidx.compose.runtime.Composable
import org.koin.compose.koinInject
import org.koin.core.qualifier.Qualifier

/**
 * Koin extensions for easier dependency injection in Compose.
 * 
 * SOLID: Single Responsibility - Only provides DI convenience functions
 */

/**
 * Inject dependency in Compose.
 */
@Composable
inline fun <reified T : Any> inject(
    qualifier: Qualifier? = null
): T = koinInject(qualifier = qualifier)
