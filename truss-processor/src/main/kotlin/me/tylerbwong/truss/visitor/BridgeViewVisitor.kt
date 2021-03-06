package me.tylerbwong.truss.visitor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import me.tylerbwong.truss.utils.abstractComposeViewClassName
import me.tylerbwong.truss.utils.attributeSetClassName
import me.tylerbwong.truss.utils.checkAndReportDefaultParameters
import me.tylerbwong.truss.utils.composableClassName
import me.tylerbwong.truss.utils.contextClassName
import me.tylerbwong.truss.utils.defaultValue
import me.tylerbwong.truss.utils.isPrimitive
import me.tylerbwong.truss.utils.writeTo

internal class BridgeViewVisitor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : KSVisitorVoid() {

    private var parameterCount = 0

    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
        if (function.checkAndReportDefaultParameters(logger)) return

        val functionName = function.simpleName.asString()
        val packageName = function.packageName.asString()
        val fileName = "${functionName}BridgeView"
        val dependencies = Dependencies(false)
        val file = codeGenerator.createNewFile(dependencies, packageName, fileName)
        val primitivePropertySpecs = function.parameters.filter { it.isPrimitive }.map {
            val name = it.name?.asString() ?: "property$parameterCount".also { parameterCount++ }
            val ksType = it.type.resolve()
            val typeDeclaration = ksType.declaration
            val type = ClassName(
                typeDeclaration.packageName.asString(),
                typeDeclaration.simpleName.asString(),
            )
            PropertySpec.builder(name, type)
                .mutable(mutable = true)
                .delegate("""mutableStateOf(${it.defaultValue()})""")
                .build()
        }
        val otherPropertySpecs = function.parameters.filter { !it.isPrimitive }.map {
            val name = it.name?.asString() ?: "prop$parameterCount".also { parameterCount++ }
            val ksType = it.type.resolve()
            val typeDeclaration = ksType.declaration
            val type = ClassName(
                typeDeclaration.packageName.asString(),
                typeDeclaration.simpleName.asString(),
            ).copy(nullable = true)
            PropertySpec.builder(name, type)
                .mutable(mutable = true)
                .delegate("""mutableStateOf(null)""")
                .build()
        }
        val allPropertySpecs = primitivePropertySpecs + otherPropertySpecs
        val fileSpecBuilder = FileSpec.builder(packageName, fileName)
        val typeSpecBuilder = TypeSpec.classBuilder(fileName)
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addAnnotation(JvmOverloads::class)
                    .addParameter("context", contextClassName)
                    .addParameter(
                        ParameterSpec.builder("attrs", attributeSetClassName.copy(nullable = true))
                            .defaultValue("null")
                            .build()
                    )
                    .addParameter(
                        ParameterSpec.builder("defStyle", Int::class)
                            .defaultValue("0")
                            .build()
                    )
                    .build()
            )
            .superclass(abstractComposeViewClassName)
            .addSuperclassConstructorParameter("context")
            .addSuperclassConstructorParameter("attrs")
            .addSuperclassConstructorParameter("defStyle")
            .addProperties(allPropertySpecs)

        val funSpecBuilder = FunSpec.builder("Content")
            .addAnnotation(composableClassName)
            .addModifiers(KModifier.OVERRIDE)

        if (otherPropertySpecs.isNotEmpty()) {
            funSpecBuilder.beginControlFlow("if (listOf(${otherPropertySpecs.joinToString { it.name }}).all { it != null })")
        }

        funSpecBuilder
            .addCode(
                if (allPropertySpecs.isEmpty()) {
                    "$functionName()"
                } else {
                    """
                       |$functionName(
                       |${allPropertySpecs.joinToString(separator = ",\n") { "  ${it.name} = ${it.name}${if (!it.isPrimitive) "!!" else ""}" }}
                       |)
                       |
                     """.trimMargin()
                }
            )

        if (otherPropertySpecs.isNotEmpty()) {
            funSpecBuilder.endControlFlow()
        }

        typeSpecBuilder.addFunction(funSpecBuilder.build())

        if (allPropertySpecs.isNotEmpty()) {
            fileSpecBuilder
                .addImport("androidx.compose.runtime", "getValue")
                .addImport("androidx.compose.runtime", "mutableStateOf")
                .addImport("androidx.compose.runtime", "setValue")
        }

        fileSpecBuilder.addType(typeSpecBuilder.build())

        fileSpecBuilder.writeTo(file)
    }
}
