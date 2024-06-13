
import jetbrains.buildServer.configs.kotlin.CheckoutMode.AUTO
import jetbrains.buildServer.configs.kotlin.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.project
import jetbrains.buildServer.configs.kotlin.triggers.VcsTrigger.QuietPeriodMode.USE_DEFAULT
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot.AgentCheckoutPolicy.NO_MIRRORS
import jetbrains.buildServer.configs.kotlin.version

version = "2024.03"

project {

    val vcsRoot = GitVcsRoot {
        id("GradleMatchers")
        name = "gradle-matchers"
        url = "https://github.com/rodm/gradle-matchers"
        branch = "refs/heads/main"
        branchSpec = """
            +:refs/heads/(*)
            +:refs/tags/(*)
        """.trimIndent()
        useTagsAsBranches = true
        checkoutPolicy = NO_MIRRORS
    }
    vcsRoot(vcsRoot)

    params {
        param("teamcity.ui.settings.readOnly", "true")
    }

    val buildTemplate = template {
        id("Build")
        name = "Build"

        params {
            param("gradle.opts", "")
            param("gradle.tasks", "clean build")
        }

        vcs {
            root(vcsRoot)
            checkoutMode = AUTO
        }

        steps {
            gradle {
                tasks = "%gradle.tasks%"
                buildFile = ""
                gradleParams = "--show-version %gradle.opts%"
                enableStacktrace = true
                jdkHome = "%java8.home%"
            }
        }

        features {
            feature {
                id = "perfmon"
                type = "perfmon"
            }
        }

        triggers {
            vcs {
                quietPeriodMode = USE_DEFAULT
                branchFilter = ""
            }
        }
    }

    buildType {
        templates(buildTemplate)
        id("Build1")
        name = "Build - Java 8"
    }

    buildType {
        templates(buildTemplate)
        id("ReportCodeQuality")
        name = "Report - Code Quality"

        params {
            param("gradle.opts", "%sonar.opts%")
            param("gradle.tasks", "clean build sonar")
        }
    }
}
