package dev.matrix.compose_routes.compiler.model

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
            val typeName = state.unbox(element.asType()).asTypeName().copy(
                nullable = element.getAnnotation(Nullable::class.java) != null,
            )

            return RouteDestinationArg(
                name = element.simpleName.toString(),
                navType = type,
                typeName = typeName,
            )
        }
    }
}
