/*
 * Copyright 2024 Rod MacKenzie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    id ("java-library")
    id ("groovy")
}

version = "0.1-SNAPSHOT"
group = "io.github.rodm"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly (gradleApi())
    compileOnly ("org.hamcrest:hamcrest:2.2")

    testImplementation (gradleApi())
    testImplementation ("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation ("org.hamcrest:hamcrest:2.2")
}

tasks {
    test {
        useJUnitPlatform()
    }
}