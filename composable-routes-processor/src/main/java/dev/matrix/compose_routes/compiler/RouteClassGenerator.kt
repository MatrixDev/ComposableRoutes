package dev.matrix.compose_routes.compiler

import com.squareup.kotlinpoet.*
import dev.matrix.compose_routes.compiler.model.RouteClassName
import dev.matrix.compose_routes.compiler.model.RouteDestination
import dev.matrix.compose_routes.compiler.model.nav_type.ParcelableNavType
import dev.matrix.compose_routes.compiler.model.nav_type.SerializableNavType
import javax.tools.StandardLocation

fun generateRoutesClass(
    state: AnnotationProcessorState,
    name: RouteClassName,
    destinations: Iterable<RouteDestination>,
) {
    // object

    val typeSpec = TypeSpec.objectBuilder(name.toClassName())
        .addFunction(generateRegisterAll(state, destinations))

    for (destination in destinations) {
        typeSpec.addType(generateDestinationClass(state, destination))
    }

    if (destinations.any { it.arguments.any { arg -> arg.navType is ParcelableNavType } }) {
        typeSpec.addFunction(state.parcelableSerializer.generateSerializer())
        typeSpec.addFunction(state.parcelableSerializer.generateDeserializer())
    }

    if (destinations.any { it.arguments.any { arg -> arg.navType is SerializableNavType } }) {
        typeSpec.addFunction(state.serializableSerializer.generateSerializer())
        typeSpec.addFunction(state.serializableSerializer.generateDeserializer())
    }

    // file

    val fileSpec = FileSpec.builder(name.classPackage, name.className)
        .addType(typeSpec.build())

    for (destination in destinations) {
        fileSpec.addFunction(generateNavigateToExtension(state, name, destination))
    }

    state.environment.filer.createResource(
        StandardLocation.SOURCE_OUTPUT,
        name.classPackage,
        "${name.className}.kt",
    ).openWriter().use { fileSpec.build().writeTo(it) }
}

private fun generateDestinationClass(
    state: AnnotationProcessorState,
    destination: RouteDestination,
): TypeSpec {
    return TypeSpec.objectBuilder(destination.name)
        .addProperty(generatePath(destination))
        .addFunction(generateInvoke(state, destination))
        .addFunction(generateRegister(state, destination))
        .addFunction(generateCompose(state, destination))
        .build()
}

private fun generatePath(destination: RouteDestination): PropertySpec {
    val path = buildString {
        append(destination.name)

        for (argument in destination.arguments) {
            if (argument.typeName.isNullable) {
                continue
            }
            append("/")
            append("{${argument.name}}")
        }

        var index = 0
        for (argument in destination.arguments) {
            if (!argument.typeName.isNullable) {
                continue
            }
            append(if (++index == 1) "?" else "&")
            append("${argument.name}={${argument.name}}")
        }
    }
    return PropertySpec.builder("PATH", STRING, KModifier.CONST)
        .initializer("%S", path)
        .build()
}

private fun generateInvoke(
    state: AnnotationProcessorState,
    destination: RouteDestination,
): FunSpec {
    val spec = FunSpec.builder("invoke")
        .returns(STRING)
        .addModifiers(KModifier.OPERATOR)

    for (argument in destination.arguments) {
        spec.addParameter(argument.name, argument.typeName)
    }

    if (destination.arguments.isEmpty()) {
        return spec.addStatement("return %S", destination.name).build()
    }

    spec.beginControlFlow("return buildString {")
    spec.addStatement("append(%S)", destination.name)

    var optionalAvailable = false
    for (argument in destination.arguments) {
        if (argument.typeName.isNullable) {
            optionalAvailable = true
            continue
        }

        var expression = CodeBlock.of("%L", argument.name)
        expression = argument.navType.toNavValue(state, expression)

        spec.addStatement("append('/')", expression)
        spec.addStatement("append(%L)", expression)
    }

    if (optionalAvailable) {
        spec.addStatement("var optionalArgumentIndex = 0")
        for (argument in destination.arguments) {
            if (!argument.typeName.isNullable) {
                continue
            }

            var expression = CodeBlock.of("%L", argument.name)
            expression = argument.navType.toNavValue(state, expression)

            spec.beginControlFlow("if (%L != null) {", argument.name)
            spec.addStatement("append(if (++optionalArgumentIndex == 1) '?' else '&')")
            spec.addStatement("append(\"%L=\")", argument.name)
            spec.addStatement("append(%L)", expression)
            spec.endControlFlow()
        }
    }

    return spec.endControlFlow().build()
}

private fun generateRegister(
    state: AnnotationProcessorState,
    destination: RouteDestination,
): FunSpec {
    val code = CodeBlock.builder()

    code.addStatement("builder.%M(", state.navComposeFun)
    code.indent()
    code.addStatement("route = PATH,")
    code.addStatement("arguments = listOf(")
    code.indent()
    for (argument in destination.arguments) {
        code.addStatement("%M(%S) {", state.navArgumentFun, argument.name)
        code.indent()
        code.addStatement("nullable = true")
        code.addStatement("type = %L", argument.navType.getNavType(state))
        code.unindent()
        code.addStatement("},")
    }
    code.unindent()
    code.addStatement("),")
    code.unindent()
    code.addStatement(") {")
    code.indent()
    code.addStatement("Compose(it)", destination.memberName)
    code.unindent()
    code.addStatement("}")

    return FunSpec.builder("register")
        .addParameter("builder", state.navGraphBuilder)
        .addCode(code.build())
        .build()
}

private fun generateCompose(
    state: AnnotationProcessorState,
    destination: RouteDestination,
): FunSpec {
    val code = CodeBlock.builder()

    code.add("%L(", destination.memberName)
    if (destination.arguments.isNotEmpty()) {
        code.addStatement("")
        code.indent()
        for (argument in destination.arguments) {
            var expression = argument.navType.getFromBundle(state, argument, "entry.arguments!!")
            expression = argument.navType.fromNavValue(state, expression)

            if (argument.typeName.isNullable) {
                code.addStatement(
                    "%L = when (entry.arguments!!.containsKey(%S)) {",
                    argument.name,
                    argument.name,
                )
                code.indent()
                code.addStatement("true -> %L", expression)
                code.addStatement("else -> null")
                code.unindent()
                code.addStatement("},")
            } else {
                code.addStatement("%L = %L,", argument.name, expression)
            }
        }
        code.unindent()
    }
    code.add(")")

    return FunSpec.builder("Compose")
        .addAnnotation(state.composableAnnotation)
        .also {
            if (destination.arguments.isEmpty()) it.addAnnotation(
                AnnotationSpec.builder(Suppress::class)
                    .addMember("%S", "UNUSED_PARAMETER")
                    .build()
            )
        }
        .addParameter("entry", state.navBackStackEntry)
        .addCode(code.build())
        .build()
}

private fun generateNavigateToExtension(
    state: AnnotationProcessorState,
    name: RouteClassName,
    destination: RouteDestination,
): FunSpec {
    val code = CodeBlock.builder()

    code.add(
        "navigate(%T.%L(",
        name.toClassName(),
        destination.name,
    )
    if (destination.arguments.isNotEmpty()) {
        code.addStatement("")
        code.indent()
        for (argument in destination.arguments) {
            code.addStatement("%L = %L,", argument.name, argument.name)
        }
        code.unindent()
    }
    code.addStatement("))")

    return FunSpec.builder("navigateTo${destination.name}")
        .receiver(state.navHostController)
        .also {
            for (argument in destination.arguments) {
                it.addParameter(argument.name, argument.typeName)
            }
        }
        .addCode(code.build())
        .build()
}

private fun generateRegisterAll(
    state: AnnotationProcessorState,
    destinations: Iterable<RouteDestination>,
): FunSpec {
    val spec = FunSpec.builder("registerAll")
        .addParameter("builder", state.navGraphBuilder)

    for (destination in destinations) {
        spec.addStatement("%L.register(builder)", destination.name)
    }

    return spec.build()
}
