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

import org.jreleaser.model.Active.ALWAYS

plugins {
    id ("java-library")
    id ("groovy")
    id ("jacoco")
    id ("maven-publish")
    id ("org.jreleaser") version "1.13.1"
    id ("org.sonarqube") version "4.0.0.2929"
}

version = "0.2-SNAPSHOT"
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
    testRuntimeOnly ("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
    withJavadocJar()
    withSourcesJar()
}

tasks {
    test {
        useJUnitPlatform()
        finalizedBy (jacocoTestReport)
    }

    jacocoTestReport {
        reports {
            xml.required = true
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                name = "Gradle Matchers"
                description = "A library of Hamcrest matchers to support testing Gradle plugins"
                url = "https://github.com/rodm/gradle-matchers"

                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "rodm"
                        name = "Rod MacKenzie"
                        email = "rod.n.mackenzie@gmail.com"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/rodm/gradle-matchers.git"
                    developerConnection = "scm:git:ssh://github.com/rodm/gradle-matchers.git"
                    url = "https://github.com/rodm/gradle-matchers"
                }
            }
        }
    }

    repositories {
        maven {
            setUrl(layout.buildDirectory.dir("staging-deploy"))
        }
    }
}

jreleaser {
    project {
        description = "A library of Hamcrest matchers to support testing Gradle plugins"
        copyright = "2024 Rod MacKenzie"
    }

    signing {
        active = ALWAYS
        armored = true
    }

    deploy {
        maven {
            mavenCentral {
                create("central") {
                    active = ALWAYS
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepository("build/staging-deploy")
                }
            }
        }
    }

    release {
        github {
            skipRelease = true
        }
    }
}
