plugins {
        id("com.android.application")
        kotlin("android")
}

import com.android.build.api.artifact.SingleArtifact
abstract class ManifestReaderTask: DefaultTask() {

    @get:InputFile
    abstract val mergedManifest: RegularFileProperty

    @TaskAction
    fun taskAction() {

        val manifest = mergedManifest.asFile.get().readText()
        // ensure that merged manifest contains the right activity name.
        if (!manifest.contains("activity android:name=\"com.android.build.example.minimal.MyRealName\""))
            throw RuntimeException("Manifest Placeholder not replaced successfully")
    }
}

android {
    namespace = "com.android.build.example.minimal"
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(21)
    }
}
androidComponents {
    onVariants {
        val manifestReader = tasks.register<ManifestReaderTask>("${it.name}ManifestReader") {
            mergedManifest.set(it.artifacts.get(SingleArtifact.MERGED_MANIFEST))
        }
        it.manifestPlaceholders.put("MyName", "MyRealName")
    }
}