plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// Version constants
val soraEditorVersion = "0.23.4"
val androidxCoreVersion = "1.13.1"
val materialVersion = "1.12.0"
val coroutinesVersion = "1.9.0"
val lsp4jVersion = "0.21.2"

android {
    namespace = "com.redcode.editor"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.redcode.editor"
        minSdk = 24
        targetSdk = 34
        versionCode = (project.findProperty("VERSION_CODE") as String?)?.toIntOrNull() ?: 1
        versionName = (project.findProperty("VERSION_NAME") as String?) ?: "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            // Check for signing properties from CI/CD
            val keystoreFile = project.findProperty("android.injected.signing.store.file") as String?
            val keystorePassword = project.findProperty("android.injected.signing.store.password") as String?
            val signingKeyAlias = project.findProperty("android.injected.signing.key.alias") as String?
            val signingKeyPassword = project.findProperty("android.injected.signing.key.password") as String?
            
            if (keystoreFile != null && keystorePassword != null && signingKeyAlias != null && signingKeyPassword != null) {
                storeFile = file(keystoreFile)
                storePassword = keystorePassword
                keyAlias = signingKeyAlias
                keyPassword = signingKeyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Apply signing config if available
            if (signingConfigs.getByName("release").storeFile != null) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // AndroidX Core
    implementation("androidx.core:core-ktx:$androidxCoreVersion")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.activity:activity-ktx:1.9.3")
    implementation("androidx.fragment:fragment-ktx:1.8.5")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    
    // Material Design
    implementation("com.google.android.material:material:$materialVersion")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    
    // Sora Editor
    implementation("io.github.Rosemoe.sora-editor:editor:$soraEditorVersion")
    implementation("io.github.Rosemoe.sora-editor:language-textmate:$soraEditorVersion")
    implementation("io.github.Rosemoe.sora-editor:language-java:$soraEditorVersion")
    
    // LSP Support (for future use)
    implementation("org.eclipse.lsp4j:org.eclipse.lsp4j:$lsp4jVersion")
    implementation("org.eclipse.lsp4j:org.eclipse.lsp4j.jsonrpc:$lsp4jVersion")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    
    // File handling
    implementation("androidx.documentfile:documentfile:1.0.1")
    
    // JSON
    implementation("com.google.code.gson:gson:2.11.0")
    
    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    
    // ViewPager2
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    
    // Core library desugaring
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
