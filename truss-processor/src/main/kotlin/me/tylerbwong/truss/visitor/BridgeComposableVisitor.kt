package me.tylerbwong.truss.visitor

import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.UNIT
import me.tylerbwong.truss.runtime.BridgeComposable
import me.tylerbwong.truss.utils.androidViewClassName
import me.tylerbwong.truss.utils.composableClassName
import me.tylerbwong.truss.utils.modifierClassName
import me.tylerbwong.truss.utils.writeTo

internal class BridgeComposableVisitor(
    private val resolver: Resolver,
    private val codeGenerator: CodeGenerator,
) : KSVisitorVoid() {

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        val generateFunctionsAsParameters = classDeclaration.annotations.find {
            it.annotationType.resolve().declaration.qualifiedName?.asString() == BridgeComposable::class.java.name
        }?.arguments?.singleOrNull()?.value as? Boolean ?: true
        val className = classDeclaration.simpleName.asString()
        val packageName = classDeclaration.packageName.asString()
        val functionName = className.removeSuffix("View")
        val dependencies = Dependencies(false)
        val file = codeGenerator.createNewFile(dependencies, packageName, functionName)
        val fileSpecBuilder = FileSpec.builder(packageName, functionName)

        val paramFunctionMap = mutableMapOf<KSFunctionDeclaration, List<KSValueParameter>>()
        val paramSpecBuilders = if (generateFunctionsAsParameters) {
            classDeclaration.getAllFunctions()
                .filter { declaration ->
                    declaration.parameters.isNotEmpty()
                            && declaration.returnType?.resolve() == resolver.builtIns.unitType
                            && declaration.isPublic()
                            && declaration.findOverridee() == null
                }
                .flatMap { declaration ->
                    paramFunctionMap[declaration] = declaration.parameters
                    declaration.parameters.mapNotNull { valueParam ->
                        valueParam.name?.asString()?.let { name ->
                            val type = valueParam.type.resolve().declaration
                            val builder = ParameterSpec.builder(
                                name = name,
                                type = ClassName(
                                    type.packageName.asString(),
                                    type.simpleName.asString(),
                                )
                            )
                            builder.addAnnotations(
                                valueParam.annotations.map {
                                    val annotation = it.annotationType.resolve().declaration
                                    AnnotationSpec.builder(
                                        ClassName(
                                            annotation.packageName.asString(),
                                            annotation.simpleName.asString(),
                                        )
                                    ).build()
                                }.toList()
                            )
                            builder.build()
                        }
                    }
                }
                .toList()
        } else {
            emptyList()
        }

        fun addApplyBlockIfNecessary(): String {
            return if (paramSpecBuilders.isNotEmpty()) {
                """
                |.apply {
                |      ${
                    paramFunctionMap.keys.joinToString("\n      ") { function ->
                        "${function.simpleName.asString()}(" +
                                "${
                                    paramFunctionMap[function].orEmpty().joinToString { param ->
                                        param.name?.asString() ?: ""
                                    }
                                })"
                    }
                }
                |      viewConfig()
                |    }
                """.trimMargin()
            } else {
                ".apply { viewConfig() }"
            }
        }

        val viewConfigLambdaTypeName = LambdaTypeName.get(
            receiver = ClassName(packageName, className),
            returnType = UNIT,
        )
        val funSpecBuilder = FunSpec.builder(functionName)
            .addAnnotation(composableClassName)
            .addParameters(paramSpecBuilders)
            .addParameter(
                ParameterSpec.builder("modifier", modifierClassName)
                    .defaultValue("%T", modifierClassName)
                    .build()
            )
            .addParameter(
                ParameterSpec.builder("viewConfig", viewConfigLambdaTypeName)
                    .build()
            )
            .addCode(
                """
                |%T(
                |  factory = { context ->
                |    $className(context)${addApplyBlockIfNecessary()}
                |  },
                |  modifier = modifier,
                |)
                """.trimMargin(),
                androidViewClassName,
            )
        fileSpecBuilder.addFunction(funSpecBuilder.build())
        fileSpecBuilder.writeTo(file)
    }
}
