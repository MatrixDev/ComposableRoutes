package dev.matrix.compose_routes

/**
 * @author matrixdev
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class ComposableRoute(
    val generatedClassName: String = "NavRoutes",
    val generatedClassPackage: String = "dev.matrix.compose_routes",
)
