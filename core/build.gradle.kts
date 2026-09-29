plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.android.kmp.library)
}

kotlin {
  jvmToolchain(25)

  android {
    namespace = "com.yatzkt.core"
    compileSdk = 37
    minSdk = 37
    compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21) }
  }

  jvm()

  iosX64()
  iosArm64()
  iosSimulatorArm64()

  js(IR) { browser() }

  @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class) wasmJs { browser() }

  sourceSets {
    commonMain.dependencies { implementation(libs.kotlinx.coroutines.core) }
    commonTest.dependencies {
      implementation(libs.kotlin.test)
      implementation(libs.kotlinx.coroutines.test)
    }
  }
}
