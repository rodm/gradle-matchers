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
package io.github.rodm.gradle;

import org.gradle.api.Named;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.tasks.TaskContainer;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.List;
import java.util.stream.Collectors;

public class GradleMatchers {

    static Matcher<Project> hasPlugin(String id) {
        return new TypeSafeDiagnosingMatcher<Project>() {
            @Override
            public void describeTo(final Description description) {
                description.appendText("Project should have plugin ").appendValue(id).appendText(" applied");
            }

            @Override
            protected boolean matchesSafely(final Project project, final Description mismatchDescription) {
                List<String> classNames = project.getPlugins().stream()
                    .map(plugin -> plugin.getClass().getSimpleName())
                    .collect(Collectors.toList());
                mismatchDescription.appendText(" was ").appendValueList("[", ", ", "]", classNames);
                return project.getPluginManager().hasPlugin(id);
            }
        };
    }

    public static Matcher<Project> hasConfiguration(String name) {
        return new TypeSafeDiagnosingMatcher<Project>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("Project with a configuration called ").appendValue(name);
            }

            @Override
            protected boolean matchesSafely(Project project, Description mismatchDescription) {
                final ConfigurationContainer configurations = project.getConfigurations();
                List<String> configurationNames = configurations.stream()
                    .map(Named::getName)
                    .collect(Collectors.toList());
                mismatchDescription.appendText(" was ").appendValueList("[", ",", "]", configurationNames);
                return configurations.findByName(name) != null;
            }
        };
    }

    public static Matcher<Project> hasTask(final String name) {
        return new TypeSafeDiagnosingMatcher<Project>() {
            @Override
            public void describeTo(final Description description) {
                description.appendText("Project should have task ").appendValue(name);
            }

            @Override
            protected boolean matchesSafely(final Project project, final Description mismatchDescription) {
                TaskContainer tasks = project.getTasks();
                mismatchDescription.appendText(" was ").appendValue(tasks.getNames());
                return tasks.findByName(name) != null;
            }
        };
    }
}
