package me.tylerbwong.truss.processor

import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import me.tylerbwong.truss.runtime.BridgeComposable
import me.tylerbwong.truss.runtime.BridgeView
import me.tylerbwong.truss.utils.annotationNames
import me.tylerbwong.truss.utils.checkAndReportClasspath
import me.tylerbwong.truss.utils.containsSuperType
import me.tylerbwong.truss.visitor.BridgeComposableVisitor
import me.tylerbwong.truss.visitor.BridgeViewVisitor

@AutoService(SymbolProcessorProvider::class)
internal class TrussProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        TrussProcessor(environment)
}

private class TrussProcessor(environment: SymbolProcessorEnvironment) : SymbolProcessor {

    private val codeGenerator = environment.codeGenerator
    private val logger = environment.logger
    private val deferredSymbols = mutableListOf<KSAnnotated>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        processSymbols<BridgeView, KSFunctionDeclaration>(
            resolver = resolver,
            isSymbolValid = { function ->
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
            },
            visitorProvider = { BridgeViewVisitor(codeGenerator, logger) },
        )
        processSymbols<BridgeComposable, KSClassDeclaration>(
            resolver = resolver,
            isSymbolValid = { ksClass ->
                if (!ksClass.containsSuperType("android.view.View")) {
                    logger.error(
                        message = "@BridgeComposable should only be applied to sub-classes of android.view.View",
                        symbol = ksClass,
                    )
                    deferredSymbols += ksClass
                    false
                } else {
                    true
                }
            },
            visitorProvider = { BridgeComposableVisitor(resolver, codeGenerator) },
        )
        return deferredSymbols
    }

    private inline fun <reified S, reified K : KSAnnotated> processSymbols(
        resolver: Resolver,
        crossinline isSymbolValid: (K) -> Boolean,
        visitorProvider: () -> KSVisitorVoid,
        checkAndReportClasspath: () -> Boolean = { checkAndReportClasspath(resolver, logger) },
    ) {
        val symbolsToProcess = resolver
            .getSymbolsWithAnnotation(S::class.java.name)
            .filterIsInstance<K>()
            .filter { isSymbolValid(it) }

        if (checkAndReportClasspath()) {
            deferredSymbols += symbolsToProcess.toList()
        }

        symbolsToProcess.forEach { symbol ->
            symbol.accept(visitor = visitorProvider(), data = Unit)
        }
    }

    companion object {
        private const val COMPOSABLE_ANNOTATION = "androidx.compose.runtime.Composable"
    }
}
