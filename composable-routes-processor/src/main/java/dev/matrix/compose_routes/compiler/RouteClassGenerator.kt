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
            if (!argument.typeName.isNullable) {
                continue
            }
            append("/")
            append("{${argument.name}}")
        }

        var index = 0
        for (argument in destination.arguments) {
            if (argument.typeName.isNullable) {
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
        val expression = argument.navType.serialize(state, argument.name)
        spec.addStatement("append(%P)", CodeBlock.of("/\${%L}", expression))
    }

    if (optionalAvailable) {
        spec.addStatement("var optionalArgumentIndex = 0")
        for (argument in destination.arguments) {
            if (!argument.typeName.isNullable) {
                continue
            }

            val expression = CodeBlock.of(
                "%L=\${%L}",
                argument.name,
                argument.navType.serialize(state, argument.name),
            )

            spec.beginControlFlow("if (%L != null) {", argument.name)
            spec.addStatement("append(if (++optionalArgumentIndex == 1) '?' else '&')")
            spec.addStatement("append(%P)", expression)
            spec.endControlFlow()
        }
    }

    return spec.endControlFlow().build()
}

private fun generateRegister(
    state: AnnotationProcessorState,
    destination: RouteDestination,
): FunSpec {
    val spec = FunSpec.builder("register")
        .addParameter("builder", state.navGraphBuilder)

    spec.addStatement("builder.%M(", state.navComposeFun)
    spec.addStatement("route = PATH,")
    spec.addStatement("arguments = listOf(")
    for (argument in destination.arguments) {
        spec.addStatement(
            "%M(%S) { type = %L },",
            state.navArgumentFun,
            argument.name,
            argument.navType.getNavType(state),
        )
    }
    spec.addStatement("),")
    spec.beginControlFlow(")")
    spec.addStatement("Compose(it)", destination.memberName)
    spec.endControlFlow()

    return spec.build()
}

private fun generateNavigateToExtension(
    state: AnnotationProcessorState,
    name: RouteClassName,
    destination: RouteDestination,
): FunSpec {
    val spec = FunSpec.builder("navigateTo${destination.name}")
        .receiver(state.navHostController)

    val arguments = CodeBlock.builder()
    for (argument in destination.arguments) {
        spec.addParameter(argument.name, argument.typeName)
        arguments.add("%L = %L,", argument.name, argument.name)
    }

    spec.addStatement(
        "navigate(%T.%L(%L))",
        name.toClassName(),
        destination.name,
        arguments.build(),
    )

    return spec.build()
}

private fun generateCompose(
    state: AnnotationProcessorState,
    destination: RouteDestination,
): FunSpec {
    val spec = FunSpec.builder("Compose")
        .addAnnotation(state.composableAnnotation)
        .addParameter("entry", state.navBackStackEntry)

    if (destination.arguments.isEmpty()) {
        spec.addAnnotation(
            AnnotationSpec.builder(Suppress::class)
                .addMember("%S", "UNUSED_PARAMETER")
                .build()
        )
        return spec.addStatement("%L()", destination.memberName).build()
    }

    for (argument in destination.arguments) {
        spec.addStatement(
            "val %L = %L",
            argument.name,
            argument.navType.getValueFromNavBundle(state, argument, "entry.arguments!!"),
        )
    }

    spec.addStatement("%L(", destination.memberName)
    for (argument in destination.arguments) {
        spec.addStatement("%L,", argument.navType.deserialize(state, argument.name))
    }
    spec.addStatement(")")

    return spec.build()
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
