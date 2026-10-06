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
import org.hamcrest.StringDescription
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static io.github.rodm.gradle.GradleMatchers.dependsOn
import static io.github.rodm.gradle.GradleMatchers.finalizedBy
import static io.github.rodm.gradle.GradleMatchers.hasConfiguration
import static io.github.rodm.gradle.GradleMatchers.hasDefaultDependency
import static io.github.rodm.gradle.GradleMatchers.hasDependency
import static io.github.rodm.gradle.GradleMatchers.hasPlugin
import static io.github.rodm.gradle.GradleMatchers.hasTask
import static org.hamcrest.CoreMatchers.containsString
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.CoreMatchers.not

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
    void 'hasPlugin matcher describes expectation and mismatch'() {
        project.apply plugin: 'base'

        var description = new StringDescription()
        hasPlugin('example').describeTo(description)
        assertThat(description.toString(), containsString('Project should have plugin "example"'))

        var mismatch = new StringDescription()
        hasPlugin('example').describeMismatch(project, mismatch)
        assertThat(mismatch.toString(), containsString('was ["LifecycleBasePlugin'))
        assertThat(mismatch.toString(), containsString('"BasePlugin'))
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
    void 'hasConfiguration matcher describes expectation and mismatch'() {
        project.configurations.create('example')

        var description = new StringDescription()
        hasConfiguration('demo').describeTo(description)
        assertThat(description.toString(), containsString('Project with a configuration called "demo"'))

        var mismatch = new StringDescription()
        hasConfiguration('demo').describeMismatch(project, mismatch)
        assertThat(mismatch.toString(), containsString('was ["example"]'))
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
    void 'hasDependency matcher describes expectation and mismatch'() {
        project.apply plugin: 'java'
        project.dependencies.implementation ('com.example:artifact:4.5.6')

        var description = new StringDescription()
        hasDependency('org.example.group:artifact:version').describeTo(description)
        assertThat(description.toString(), containsString('Configuration should contain dependency "org.example.group:artifact:version"'))

        var configuration = project.configurations.getByName('implementation')
        var mismatch = new StringDescription()
        hasDependency('org.example.group:artifact:version').describeMismatch(configuration, mismatch)
        assertThat(mismatch.toString(), containsString('was ["com.example:artifact:4.5.6"]'))
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
    void 'hasTask matcher describes expectation and mismatch'() {
        project.task('task1')
        project.task('task2')

        var description = new StringDescription()
        hasTask('example').describeTo(description)
        assertThat(description.toString(), containsString('Project should have task "example"'))

        var mismatch = new StringDescription()
        hasTask('example').describeMismatch(project, mismatch)
        assertThat(mismatch.toString(), containsString('was <[task1, task2]>'))
    }

    @Test
    void 'task does not depend on another task'() {
        var task1 = project.task('task1')
        var task2 = project.task('task2')

        assertThat(task1, not(dependsOn('task2')))
        assertThat(task2, not(dependsOn('task1')))
    }

    @Test
    void 'task does depend on another task'() {
        var task1 = project.task('task1')
        var task2 = project.task('task2')
        task1.dependsOn('task2')

        assertThat(task1, dependsOn('task2'))
        assertThat(task2, not(dependsOn('task1')))
    }

    @Test
    void 'dependsOn matcher describes expectation and mismatch'() {
        var task1 = project.task('task1')
        project.task('task2')
        project.task('task3')
        task1.dependsOn('task2')

        var description = new StringDescription()
        dependsOn('task3').describeTo(description)
        assertThat(description.toString(), containsString('a Task that depends on "task3"'))

        var mismatch = new StringDescription()
        dependsOn('task3').describeMismatch(task1, mismatch)
        assertThat(mismatch.toString(), containsString('task dependencies are ["task2"]'))
    }

    @Test
    void 'task is not finalized by another task'() {
        var task1 = project.task('task1')
        var task2 = project.task('task2')

        assertThat(task1, not(finalizedBy('task2')))
        assertThat(task2, not(finalizedBy('task1')))
    }

    @Test
    void 'task is finalized by another task'() {
        var task1 = project.task('task1')
        var task2 = project.task('task2')
        task1.finalizedBy('task2')

        assertThat(task1, finalizedBy('task2'))
        assertThat(task2, not(finalizedBy('task1')))
    }

    @Test
    void 'finalizedBy matcher describes expectation and mismatch'() {
        var task1 = project.task('task1')
        project.task('task2')
        project.task('task3')
        task1.finalizedBy('task2')

        var description = new StringDescription()
        finalizedBy('task3').describeTo(description)
        assertThat(description.toString(), containsString('a Task that is finalized by "task3"'))

        var mismatch = new StringDescription()
        finalizedBy('task3').describeMismatch(task1, mismatch)
        assertThat(mismatch.toString(), containsString('task was finalized by ["task2"]'))
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
