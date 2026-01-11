plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

val detectedLocales = detectLocales()
val langsListString = "{${detectedLocales.sorted().joinToString(",") { "\"$it\"" }}}"

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    arg("room.generateKotlin", "true")
}

android {
    namespace = "com.machiav3lli.derdiedas"

    compileSdk = 36
    defaultConfig {
        applicationId = "com.machiav3lli.derdiedas"
        minSdk = 24
        targetSdk = 36
        versionCode = 2200
        versionName = "2.2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String[]", "DETECTED_LOCALES", langsListString)
    }

    buildTypes {
        named("release") {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            isMinifyEnabled = true
        }
        named("debug") {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
        }
    }
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.ksp)

    //Libs
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // UI
    implementation(libs.androidx.preference.ktx)
    implementation(libs.material)

    // Test
    implementation(libs.androidx.test.junit)
    implementation(libs.androidx.test.espresso.core)
}

fun detectLocales(): Set<String> {
    // in /res are (almost) all languages that have a translated string is saved. this is safer and saves some time
    val langsList = mutableSetOf<String>()
    fileTree("src/main/res").visit {
        if (this.file.name == "strings.xml" && this.file.readText().contains("<string")) {
            val languageCode = this.file.parentFile?.name?.removePrefix("values-")?.let {
                if (it == "values") "en" else it
            }
            languageCode?.let { langsList.add(it) }
        }
    }
    return langsList
}