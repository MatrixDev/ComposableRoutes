package dev.matrix.compose_routes.compiler.model.nav_type

import com.squareup.kotlinpoet.CodeBlock
import dev.matrix.compose_routes.compiler.AnnotationProcessorState
import dev.matrix.compose_routes.compiler.serializers.SerializableSerializer

class SerializableNavType(private val serializer: SerializableSerializer) : NavType() {
    override fun serialize(state: AnnotationProcessorState, valName: String): CodeBlock {
        return serializer.serialize(valName)
    }

    override fun deserialize(state: AnnotationProcessorState, valName: String): CodeBlock {
        return serializer.deserialize(valName)
    }
}
