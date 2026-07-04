import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidOrbitConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            dependencies {
                add("implementation", libs.findLibrary("orbit-core").get())
                add("implementation", libs.findLibrary("orbit-viewmodel").get())
                add("implementation", libs.findLibrary("orbit-compose").get())
                add("testImplementation", libs.findLibrary("orbit-test").get())
            }
        }
    }
}
