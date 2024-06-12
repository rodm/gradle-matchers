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

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static io.github.rodm.gradle.GradleMatchers.hasTask
import static org.hamcrest.CoreMatchers.containsString
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.CoreMatchers.not
import static org.junit.jupiter.api.Assertions.assertThrows

class GradleMatchersTest {

    private Project project

    @BeforeEach
    void setup(@TempDir File projectDir) {
        project = ProjectBuilder.builder().withProjectDir(projectDir).build()
    }

    @Test
    void 'project does not have named task'() {
        assertThat(project, not(hasTask('example')))
    }

    @Test
    @SuppressWarnings('ConfigurationAvoidance')
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
}
