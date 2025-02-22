/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.android.build.api.artifact.MultipleArtifact
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class ToyPlugin: Plugin<Project> {

    override fun apply(project: Project) {

        val android = project.extensions.getByType(ApplicationExtension::class.java)

        android.buildTypes.forEach {
            it.extensions.add("toy", ToyExtension::class.java)
        }

        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)

        androidComponents.beforeVariants { variantBuilder ->
            val buildType = android.buildTypes.getByName(variantBuilder.buildType)
            val toyExtension = buildType.extensions.findByName("toy") as ToyExtension

            val variantExtension = project.objects.newInstance(ToyVariantExtension::class.java)
            variantExtension.content.set(toyExtension?.content ?: "foo")
            variantBuilder.registerExtension(ToyVariantExtension::class.java, variantExtension)
        }

        androidComponents.onVariants { variant ->
            val content = variant.getExtension(ToyVariantExtension::class.java)?.content

            val taskProvider =
                project.tasks.register(variant.name + "AddAsset", AddAssetTask::class.java) { it.content.set(content) }

            variant.sources.assets?.addGeneratedSourceDirectory(
                taskProvider,
                AddAssetTask::outputDir)
        }
    }
}