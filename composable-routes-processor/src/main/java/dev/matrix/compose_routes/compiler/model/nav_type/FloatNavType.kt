package dev.matrix.compose_routes.compiler.model.nav_type

import com.squareup.kotlinpoet.CodeBlock
import dev.matrix.compose_routes.compiler.AnnotationProcessorState
import dev.matrix.compose_routes.compiler.model.RouteDestinationArg
import javax.lang.model.type.TypeKind

class FloatNavType(private val kind: TypeKind) : NavType() {
    override fun getNavType(state: AnnotationProcessorState): CodeBlock {
        return CodeBlock.of("%T.FloatType", state.navType)
    }

    override fun serialize(state: AnnotationProcessorState, valName: String) = when (kind) {
        TypeKind.FLOAT -> {
            CodeBlock.of("%L", valName)
        }
        TypeKind.DOUBLE -> {
            CodeBlock.of("%L.toFloat()", valName)
        }
        else -> state.error("unsupported type, must never happen")
    }

    override fun deserialize(state: AnnotationProcessorState, valName: String) = when (kind) {
        TypeKind.FLOAT -> {
            CodeBlock.of("%L", valName)
        }
        TypeKind.DOUBLE -> {
            CodeBlock.of("%L.toDouble()", valName)
        }
        else -> state.error("unsupported type, must never happen")
    }

    override fun getValueFromNavBundle(
        state: AnnotationProcessorState,
        argument: RouteDestinationArg,
        bundleValName: String,
    ): CodeBlock {
        return CodeBlock.of("%L.getFloat(%S)", bundleValName, argument.name)
    }
}
