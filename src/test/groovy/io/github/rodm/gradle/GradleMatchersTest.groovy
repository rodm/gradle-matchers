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

package io.github.rodm.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static io.github.rodm.gradle.GradleMatchers.hasConfiguration
import static io.github.rodm.gradle.GradleMatchers.hasDefaultDependency
import static io.github.rodm.gradle.GradleMatchers.hasDependency
import static io.github.rodm.gradle.GradleMatchers.hasPlugin
import static io.github.rodm.gradle.GradleMatchers.hasTask
import static org.hamcrest.CoreMatchers.containsString
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.CoreMatchers.not
import static org.junit.jupiter.api.Assertions.assertThrows

@SuppressWarnings('ConfigurationAvoidance')
class GradleMatchersTest {

    private Project project

    @BeforeEach
    void setup(@TempDir File projectDir) {
        project = ProjectBuilder.builder().withProjectDir(projectDir).build()
    }

    @Test
    void 'project does not have named plugin'() {
        assertThat(project, not(hasPlugin('example')))
    }

    @Test
    void 'project has named plugin'() {
        project.apply plugin: 'base'

        assertThat(project, hasPlugin('base'))
    }

    @Test
    void 'project without plugin throws exception'() {
        project.apply plugin: 'base'

        def e = assertThrows(AssertionError, () -> {
            assertThat(project, hasPlugin('example'))
        })
        assertThat(e.message, containsString('Project should have plugin "example"'))
    }

    @Test
    void 'project does not have named configuration'() {
        assertThat(project, not(hasConfiguration('demo')))
    }

    @Test
    void 'project has named configuration'() {
        project.configurations.create('demo')

        assertThat(project, hasConfiguration('demo'))
    }

    @Test
    void 'project without configuration throws exception'() {
        project.configurations.create('example')

        def e = assertThrows(AssertionError, {
            assertThat(project, hasConfiguration('demo'))
        })
        assertThat(e.message, containsString('Project with a configuration called "demo"'))
        assertThat(e.message, containsString('was ["example"]'))
    }

    @Test
    void 'configuration has a dependency'() {
        project.apply plugin: 'java'
        project.dependencies {
            implementation ('org.example.group:artifact-api:1.2.3')
            implementation ('org.example.group:artifact-impl:1.2.3')
        }

        Configuration configuration = project.configurations.getByName('implementation')
        assertThat(configuration, hasDependency('org.example.group', 'artifact-api', '1.2.3'))
        assertThat(configuration, hasDependency('org.example.group:artifact-impl:1.2.3'))
    }

    @Test
    void 'configuration does not have a dependency'() {
        project.apply plugin: 'java'

        Configuration configuration = project.configurations.getByName('implementation')
        assertThat(configuration, not(hasDependency('org.example.group:artifact:version')))
    }

    @Test
    void 'configuration has a default dependency'() {
        project.apply plugin: TestPlugin

        var configuration = project.configurations.getByName('example')
        assertThat(configuration, hasDefaultDependency('org.example.group', 'artifact', 'version'))
        assertThat(configuration, hasDefaultDependency('org.example.group:artifact:version'))
    }

    @Test
    void 'configuration does not have a default dependency'() {
        project.apply plugin: TestPlugin
        project.dependencies {
            example ('org.example.group:artifact:1.2.3')
        }

        Configuration configuration = project.configurations.getByName('example')
        assertThat(configuration, not(hasDefaultDependency('org.example.group:artifact:version')))
    }

    @Test
    void 'project does not have named task'() {
        assertThat(project, not(hasTask('example')))
    }

    @Test
    void 'project does have named task'() {
        project.task('example')

        assertThat(project, hasTask('example'))
    }

    @Test
    void 'project without task throws exception'() {
        def e = assertThrows(AssertionError, () -> {
            assertThat(project, hasTask('example'))
        })
        assertThat(e.message, containsString('Project should have task "example"'))
    }

    @Test
    @SuppressWarnings('ConfigurationAvoidance')
    void 'project without task lists known tasks in exception'() {
        project.task('task1')
        project.task('task2')

        def e = assertThrows(AssertionError, () -> {
            assertThat(project, hasTask('example'))
        })
        assertThat(e.message, containsString('was <[task1, task2]>'))
    }

    private static class TestPlugin implements Plugin<Project> {
        @Override
        void apply(Project project) {
            var configurations = project.getConfigurations();
            configurations.maybeCreate('example')
                .setDescription('Example configuration for testing.')
                .defaultDependencies(dependencies -> {
                    DependencyHandler handler = project.getDependencies();
                    dependencies.add(handler.create('org.example.group:artifact:version'));
                });
        }
    }
}
