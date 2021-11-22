package dev.matrix.compose_routes.compiler.model

import com.squareup.kotlinpoet.MemberName
import dev.matrix.compose_routes.compiler.AnnotationProcessorState
import javax.lang.model.element.ExecutableElement
import javax.lang.model.type.TypeKind

data class RouteDestination(
    val name: String,
    val memberName: MemberName,
    val arguments: List<RouteDestinationArg>,
) {
    companion object {
        fun from(state: AnnotationProcessorState, element: ExecutableElement): RouteDestination {
            if (element.typeParameters.isNotEmpty()) {
                state.error("${element.simpleName} cannot be generic", element = element)
            }
            if (element.returnType.kind != TypeKind.VOID) {
                state.error("${element.simpleName} must return void", element = element)
            }
            // TODO error private

            val pkg = state.environment.elementUtils.getPackageOf(element)
            val name = element.simpleName.toString()
            val packageName = pkg.qualifiedName.toString()

            return RouteDestination(
                name = name,
                memberName = MemberName(packageName, name),
                arguments = element.parameters.map { RouteDestinationArg.from(state, it) },
            )
        }
    }

    val hasNavController = arguments.any { it.isNavController }
    val pathArguments = arguments.filter { !it.isNavController }
}
