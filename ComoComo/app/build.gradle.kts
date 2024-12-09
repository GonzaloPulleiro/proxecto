plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

}

android {
    namespace = "com.app.comocomo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.app.comocomo"
        minSdk = 21
        //noinspection EditedTargetSdkVersion
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures{
        viewBinding = true
        dataBinding = true
    }


}

dependencies {

    implementation(libs.androidx.core)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)



    api ("com.google.android.material:material:1.1.0-alpha06")
    implementation ("androidx.legacy:legacy-support-v4:1.0.0")
    implementation ("com.android.volley:volley:1.2.1")
    implementation ("com.google.code.gson:gson:2.8.5")
    implementation ("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.21")
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.ui.android)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation ("junit:junit:4.12")
    androidTestImplementation ("androidx.test:core:1.1.0")
    androidTestImplementation ("androidx.test.ext:junit:1.1.0")
    androidTestImplementation ("androidx.test:runner:1.2.0-alpha05")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.2.0-alpha05")

    implementation("androidx.databinding:databinding-runtime:8.7.2")
    implementation("androidx.databinding:databinding-adapters:8.7.2")
    implementation("androidx.databinding:databinding-ktx:8.7.2")

    // Para acceder a cámara o galeria
    implementation ("androidx.activity:activity-ktx:1.7.2")
    implementation ("androidx.fragment:fragment-ktx:1.8.5")

    // Para hashear contraseña
    implementation ("org.mindrot:jbcrypt:0.4")


}