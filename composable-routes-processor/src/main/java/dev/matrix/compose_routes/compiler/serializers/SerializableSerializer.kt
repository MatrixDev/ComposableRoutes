package dev.matrix.compose_routes.compiler.serializers

import com.squareup.kotlinpoet.*
import dev.matrix.compose_routes.compiler.AnnotationProcessorState

class SerializableSerializer(private val state: AnnotationProcessorState) {
    private val oosClassName = ClassName("java.io", "ObjectOutputStream")
    private val oisClassName = ClassName("java.io", "ObjectInputStream")
    private val bosClassName = ClassName("java.io", "ByteArrayOutputStream")
    private val bisClassName = ClassName("java.io", "ByteArrayInputStream")
    private val base64ClassName = ClassName("java.util", "Base64")

    fun serialize(expression: CodeBlock): CodeBlock {
        return CodeBlock.of("(%L).serializeToString()", expression)
    }

    fun deserialize(expression: CodeBlock): CodeBlock {
        return CodeBlock.of("(%L).deserializeToSerializable()", expression)
    }

    fun generateSerializer(): FunSpec {
        return FunSpec.builder("serializeToString")
            .receiver(state.serializableClassName)
            .returns(STRING)
            .addModifiers(KModifier.PRIVATE)
            .addStatement("val bos = %T()", bosClassName)
            .addStatement("val oos = %T(bos)", oosClassName)
            .addStatement("oos.writeObject(this)")
            .addStatement("oos.flush()")
            .addStatement(
                "return %T.getUrlEncoder().encodeToString(bos.toByteArray())",
                base64ClassName,
            )
            .build()
    }

    fun generateDeserializer(): FunSpec {
        val type = TypeVariableName("T", state.parcelableClassName)
        return FunSpec.builder("deserializeToSerializable")
            .addTypeVariable(type.copy(reified = true))
            .addModifiers(KModifier.INLINE)
            .addModifiers(KModifier.PRIVATE)
            .receiver(STRING)
            .returns(type)
            .addStatement("val bytes = %T.getUrlDecoder().decode(this)", base64ClassName)
            .addStatement("val bis = %T(bytes)", bisClassName)
            .addStatement("val ois = %T(bis)", oisClassName)
            .addStatement("return ois.readObject() as %T", type)
            .build()
    }
}
