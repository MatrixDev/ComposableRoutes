package dev.matrix.compose_routes.compiler.model.nav_type

import com.squareup.kotlinpoet.CodeBlock
import dev.matrix.compose_routes.compiler.AnnotationProcessorState
import dev.matrix.compose_routes.compiler.model.RouteDestinationArg
import javax.lang.model.element.ElementKind
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind

sealed class NavType {
    companion object {
        fun from(state: AnnotationProcessorState, element: VariableElement): NavType {
            val utils = state.environment.typeUtils
            val type = state.unbox(element.asType())

            if (type.kind.isPrimitive) {
                return when (type.kind) {
                    TypeKind.BOOLEAN -> {
                        BooleanNavType
                    }
                    TypeKind.FLOAT, TypeKind.DOUBLE -> {
                        FloatNavType(type.kind)
                    }
                    else -> {
                        IntegerNavType(type.kind)
                    }
                }
            }

            if (type.kind == TypeKind.DECLARED) {
                val typeElement = (type as DeclaredType).asElement()
                if (typeElement.kind == ElementKind.ENUM) {
                    return EnumNavType(type)
                }
                if (typeElement == state.jStringTypeElement) {
                    return StringNavType
                }
            }

            if (utils.isAssignable(type, state.parcelableTypeElement.asType())) {
                return ParcelableNavType(state.parcelableSerializer)
            }

            if (utils.isAssignable(type, state.serializableTypeElement.asType())) {
                return SerializableNavType(state.serializableSerializer)
            }

            state.error("${element.asType()} type not supported", element = element)
        }
    }

    abstract fun toNavValue(state: AnnotationProcessorState, expression: CodeBlock): CodeBlock
    abstract fun fromNavValue(state: AnnotationProcessorState, expression: CodeBlock): CodeBlock

    open fun getNavType(state: AnnotationProcessorState): CodeBlock {
        return CodeBlock.of("%T.StringType", state.navType)
    }

    open fun getFromBundle(
        state: AnnotationProcessorState,
        argument: RouteDestinationArg,
        bundleValName: String,
    ): CodeBlock {
        return CodeBlock.of("%L.getString(%S).orEmpty()", bundleValName, argument.name)
    }
}
