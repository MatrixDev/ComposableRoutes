package dev.matrix.compose_routes.compiler.model.nav_type

import com.squareup.kotlinpoet.CodeBlock
import dev.matrix.compose_routes.compiler.AnnotationProcessorState
import dev.matrix.compose_routes.compiler.model.RouteDestinationArg
import javax.lang.model.type.TypeKind

class IntegerNavType(private val kind: TypeKind) : NavType() {
    override fun getNavType(state: AnnotationProcessorState): CodeBlock {
        return CodeBlock.of("%T.IntType", state.navType)
    }

    override fun serialize(state: AnnotationProcessorState, valName: String) = when (kind) {
        TypeKind.INT -> {
            CodeBlock.of("%L", valName)
        }
        TypeKind.BYTE,
        TypeKind.SHORT,
        TypeKind.LONG,
        TypeKind.CHAR -> {
            CodeBlock.of("%L.toInt()", valName)
        }
        else -> state.error("unsupported type ${kind}, must never happen")
    }

    override fun deserialize(state: AnnotationProcessorState, valName: String) = when (kind) {
        TypeKind.BYTE -> {
            CodeBlock.of("%L.toByte()", valName)
        }
        TypeKind.CHAR -> {
            CodeBlock.of("%L.toChar()", valName)
        }
        TypeKind.SHORT -> {
            CodeBlock.of("%L.toShort()", valName)
        }
        TypeKind.INT -> {
            CodeBlock.of("%L", valName)
        }
        TypeKind.LONG -> {
            CodeBlock.of("%L.toLong()", valName)
        }
        else -> state.error("unsupported type ${kind}, must never happen")
    }

    override fun getValueFromNavBundle(
        state: AnnotationProcessorState,
        argument: RouteDestinationArg,
        bundleValName: String,
    ): CodeBlock {
        return CodeBlock.of("%L.getInt(%S)", bundleValName, argument.name)
    }
}
