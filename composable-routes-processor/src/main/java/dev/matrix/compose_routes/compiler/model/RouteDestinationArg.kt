package dev.matrix.compose_routes.compiler.model

import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import dev.matrix.compose_routes.compiler.AnnotationProcessorState
import dev.matrix.compose_routes.compiler.model.nav_type.NavType
import org.jetbrains.annotations.Nullable
import javax.lang.model.element.VariableElement

data class RouteDestinationArg(
    val name: String,
    val navType: NavType,
    val typeName: TypeName,
) {
    companion object {
        fun from(state: AnnotationProcessorState, element: VariableElement): RouteDestinationArg {
            val type = NavType.from(state, element)

            val jTypeName = state.unbox(element.asType())
            val kTypeName = when (jTypeName == state.jStringTypeElement.asType()) {
                true -> STRING
                else -> jTypeName.asTypeName()
            }.copy(nullable = element.getAnnotation(Nullable::class.java) != null)

            return RouteDestinationArg(
                name = element.simpleName.toString(),
                navType = type,
                typeName = kTypeName,
            )
        }
    }
}
