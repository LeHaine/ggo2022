import com.lehaine.littlekt.gradle.texturepacker.littleKt
import com.lehaine.littlekt.gradle.texturepacker.packing
import com.lehaine.littlekt.gradle.texturepacker.texturePacker
import org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

repositories {
    maven(url = "https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("com.lehaine.littlekt.gradle.texturepacker")
}

littleKt {
    texturePacker {
        inputDir = "../art/export_tiles/"
        outputDir = "src/commonMain/resources/"
        outputName = "tiles.atlas"
        packing {
            extrude = 2
        }
    }
}

kotlin {
    android()
    jvm {
        compilations {
            val main by getting

            val mainClass = (findProperty("jvm.mainClass") as? String)?.plus("Kt")
                ?: project.logger.log(
                    LogLevel.ERROR,
                    "Property 'jvm.mainClass' has either changed or has not been set. Check 'gradle.properties' and ensure it is properly set!"
                )
            tasks {
                register<Copy>("copyResources") {
                    group = "package"
                    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
                    from(main.output.resourcesDir)
                    destinationDir = File("$buildDir/publish")
                }
                register<Jar>("packageFatJar") {
                    group = "package"
                    archiveClassifier.set("all")
                    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
                    dependsOn(named("jvmJar"))
                    dependsOn(named("copyResources"))
                    manifest {
                        attributes["Main-Class"] = mainClass
                    }
                    destinationDirectory.set(File("$buildDir/publish/"))
                    from(
                        main.runtimeDependencyFiles.map { if (it.isDirectory) it else zipTree(it) },
                        main.output.classesDirs
                    )
                    doLast {
                        project.logger.lifecycle("[LittleKt] The packaged jar is available at: ${outputs.files.first().parent}")
                    }
                }
            }
        }
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }
    js(KotlinJsCompilerType.IR) {
        binaries.executable()
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }

        this.attributes.attribute(
            KotlinPlatformType.attribute,
            KotlinPlatformType.js
        )

        compilations.all {
            kotlinOptions.sourceMap = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.rune.core)
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting
        val jsMain by getting {
            dependencies {
                implementation(libs.kotlinx.html.js)
            }
        }
        val jsTest by getting
        val androidMain by getting
    }
}

android {
    sourceSets["main"].apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        assets.srcDirs("src/commonMain/resources")
    }
    compileSdk = (findProperty("android.compileSdk") as String).toInt()

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
    }
}