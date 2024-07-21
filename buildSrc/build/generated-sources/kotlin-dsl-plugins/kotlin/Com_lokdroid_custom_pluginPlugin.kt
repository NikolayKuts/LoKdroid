/**
 * Precompiled [com.lokdroid.custom.plugin.gradle.kts][Com_lokdroid_custom_plugin_gradle] script plugin.
 *
 * @see Com_lokdroid_custom_plugin_gradle
 */
public
class Com_lokdroid_custom_pluginPlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("Com_lokdroid_custom_plugin_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
