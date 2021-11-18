package dev.matrix.compose_routes.compiler

import com.google.auto.service.AutoService
import dev.matrix.compose_routes.ComposableRoute
import dev.matrix.compose_routes.compiler.model.RouteClassName
import dev.matrix.compose_routes.compiler.model.RouteDestination
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.ISOLATING)
class AnnotationProcessor : AbstractProcessor() {

    override fun getSupportedSourceVersion() = checkNotNull(SourceVersion.latestSupported())
    override fun getSupportedAnnotationTypes() = mutableSetOf(ComposableRoute::class.java.name)

    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnvironment: RoundEnvironment,
    ): Boolean {
        val state = AnnotationProcessorState(processingEnv)
        try {
            val elements = roundEnvironment.getElementsAnnotatedWith(ComposableRoute::class.java)
                .filterIsInstance<ExecutableElement>()

            val routeClasses = HashMap<RouteClassName, HashMap<String, RouteDestination>>()
            for (element in elements) {
                val annotation = element.getAnnotation(ComposableRoute::class.java)
                    ?: state.error(
                        "$element is not annotated with ${ComposableRoute::class.simpleName}",
                        element = element,
                    )

                val destination = RouteDestination.from(state, element)
                val className = RouteClassName(
                    className = annotation.generatedClassName,
                    classPackage = annotation.generatedClassPackage,
                )

                val destinations = routeClasses.getOrPut(className) { HashMap() }
                if (destinations.containsKey(destination.name)) {
                    error("destination ${destination.name} already exists")
                }

                destinations[destination.name] = destination
            }

            for (entry in routeClasses.entries) {
                generateRoutesClass(state, entry.key, entry.value.values)
            }
        } catch (e: Exception) {
            state.error(e.toString())
        }
        return true
    }
}
