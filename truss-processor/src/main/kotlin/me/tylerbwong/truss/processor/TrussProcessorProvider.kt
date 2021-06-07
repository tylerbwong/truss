package me.tylerbwong.truss.processor

import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import me.tylerbwong.truss.runtime.BridgeView
import me.tylerbwong.truss.utils.annotationNames
import me.tylerbwong.truss.utils.checkAndReportClasspath
import me.tylerbwong.truss.utils.composeDependencies
import me.tylerbwong.truss.utils.isOnClasspath
import me.tylerbwong.truss.utils.platformDependencies
import me.tylerbwong.truss.visitor.BridgeViewVisitor

@AutoService(SymbolProcessorProvider::class)
class TrussProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        TrussProcessor(environment)
}

private class TrussProcessor(environment: SymbolProcessorEnvironment) : SymbolProcessor {

    private val codeGenerator = environment.codeGenerator
    private val logger = environment.logger
    private val deferredSymbols = mutableListOf<KSAnnotated>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbolsToProcess = resolver
            .getSymbolsWithAnnotation(BridgeView::class.java.name)
            .filterIsInstance<KSFunctionDeclaration>()
            .filter { function ->
                if (COMPOSABLE_ANNOTATION !in function.annotationNames) {
                    logger.error(
                        message = "@BridgeView should only be applied to @Composable functions",
                        symbol = function,
                    )
                    deferredSymbols += function
                    false
                } else {
                    true
                }
            }

        if (checkAndReportClasspath(resolver, logger)) {
            return symbolsToProcess.toList()
        }

        symbolsToProcess.forEach { symbol ->
            symbol.accept(
                visitor = BridgeViewVisitor(codeGenerator = codeGenerator, logger = logger),
                data = Unit,
            )
        }

        return deferredSymbols
    }

    companion object {
        private const val COMPOSABLE_ANNOTATION = "androidx.compose.runtime.Composable"
    }
}
