package dev.matrix.compose_routes.compiler.model.nav_type

import com.squareup.kotlinpoet.CodeBlock
import dev.matrix.compose_routes.compiler.AnnotationProcessorState
import dev.matrix.compose_routes.compiler.model.RouteDestinationArg
import javax.lang.model.type.TypeKind

class IntegerNavType(private val kind: TypeKind) : NavType() {
    override fun getNavType(state: AnnotationProcessorState): CodeBlock {
        return CodeBlock.of("%T.IntType", state.navType)
    }

    override fun toNavValue(state: AnnotationProcessorState, expression: CodeBlock) = when (kind) {
        TypeKind.INT -> {
            expression
        }
        TypeKind.BYTE,
        TypeKind.SHORT,
        TypeKind.LONG,
        TypeKind.CHAR -> {
            CodeBlock.of("(%L).toInt()", expression)
        }
        else -> state.error("unsupported type ${kind}, must never happen")
    }

    override fun fromNavValue(state: AnnotationProcessorState, expression: CodeBlock) = when (kind) {
        TypeKind.BYTE -> {
            CodeBlock.of("(%L).toByte()", expression)
        }
        TypeKind.CHAR -> {
            CodeBlock.of("(%L).toChar()", expression)
        }
        TypeKind.SHORT -> {
            CodeBlock.of("(%L).toShort()", expression)
        }
        TypeKind.INT -> {
            expression
        }
        TypeKind.LONG -> {
            CodeBlock.of("(%L).toLong()", expression)
        }
        else -> state.error("unsupported type ${kind}, must never happen")
    }

    override fun getFromBundle(
        state: AnnotationProcessorState,
        argument: RouteDestinationArg,
        bundleValName: String,
    ): CodeBlock {
        return CodeBlock.of("%L.getInt(%S)", bundleValName, argument.name)
    }
}
