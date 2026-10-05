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
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.tasks.TaskContainer;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.List;
import java.util.stream.Collectors;

public class GradleMatchers {

    private GradleMatchers() {}

    private static final String TEXT = " was ";

    public static Matcher<Project> hasPlugin(String id) {
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
                mismatchDescription.appendText(TEXT).appendValueList("[", ", ", "]", classNames);
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
                mismatchDescription.appendText(TEXT).appendValueList("[", ",", "]", configurationNames);
                return configurations.findByName(name) != null;
            }
        };
    }

    public static Matcher<Configuration> hasDependency(String group, String name, String version) {
        return hasDependency(group + ":" + name + ":" + version);
    }

    public static Matcher<Configuration> hasDependency(String dependencyNotation) {
        return new HasDependency(dependencyNotation);
    }

    public static Matcher<Configuration> hasDefaultDependency(String group, String name, String version) {
        return hasDefaultDependency(group + ":" + name + ":" + version);
    }

    public static Matcher<Configuration> hasDefaultDependency(String dependencyNotation) {
        return new HasDefaultDependency(dependencyNotation);
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
                mismatchDescription.appendText(TEXT).appendValue(tasks.getNames());
                return tasks.findByName(name) != null;
            }
        };
    }

    public static Matcher<Task> dependsOn(String name) {
        return new TypeSafeDiagnosingMatcher<Task>() {
            @Override
            protected boolean matchesSafely(Task task, Description mismatchDescription) {
                List<String> names = task.getTaskDependencies().getDependencies(task).stream()
                    .map(Task::getName)
                    .collect(Collectors.toList());
                mismatchDescription
                    .appendText("task dependencies are ")
                    .appendValueList("[", ", ", "]", names);
                return names.contains(name);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a Task that depends on ").appendValue(name);
            }
        };
    }

    public static Matcher<Task> finalizedBy(final String name) {
        return new TypeSafeDiagnosingMatcher<Task>() {
            @Override
            protected boolean matchesSafely(Task task, Description mismatchDescription) {
                List<String> names = task.getFinalizedBy().getDependencies(task).stream()
                    .map(Task::getName)
                    .collect(Collectors.toList());
                mismatchDescription
                    .appendText("task was finalized by ")
                    .appendValueList("[", ", ", "]", names);
                return names.contains(name);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a Task that is finalized by ").appendValue(name);
            }
        };
    }

    private static class HasDependency extends TypeSafeDiagnosingMatcher<Configuration> {

        private final String dependencyNotation;

        private HasDependency(String dependencyNotation) {
            this.dependencyNotation = dependencyNotation;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Configuration should contain dependency ").appendValue(dependencyNotation);
        }

        @Override
        protected boolean matchesSafely(Configuration configuration, Description mismatchDescription) {
            List<String> dependencies = getDependencies(configuration);
            mismatchDescription.appendText(TEXT).appendValue(dependencies);
            return dependencies.contains(dependencyNotation);
        }

        List<String> getDependencies(Configuration configuration) {
            return configuration.getDependencies().stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        }
    }

    private static class HasDefaultDependency extends HasDependency {
        private HasDefaultDependency(String dependencyNotation) {
            super(dependencyNotation);
        }

        List<String> getDependencies(Configuration configuration) {
            return configuration.getIncoming().getDependencies().stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        }
    }
}
