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

import io.github.rodm.gradle.internal.DependsOn;
import io.github.rodm.gradle.internal.FinalizedBy;
import io.github.rodm.gradle.internal.HasConfiguration;
import io.github.rodm.gradle.internal.HasDependency;
import io.github.rodm.gradle.internal.HasPlugin;
import io.github.rodm.gradle.internal.HasTask;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.hamcrest.Matcher;

public class GradleMatchers {

    private GradleMatchers() {}

    public static final String TEXT = " was ";

    public static Matcher<Project> hasPlugin(String id) {
        return HasPlugin.hasPlugin(id);
    }

    public static Matcher<Project> hasConfiguration(String name) {
        return HasConfiguration.hasConfiguration(name);
    }

    public static Matcher<Configuration> hasDependency(String group, String name, String version) {
        return HasDependency.hasDependency(group + ":" + name + ":" + version);
    }

    public static Matcher<Configuration> hasDependency(String dependencyNotation) {
        return HasDependency.hasDependency(dependencyNotation);
    }

    public static Matcher<Configuration> hasDefaultDependency(String group, String name, String version) {
        return HasDependency.hasDefaultDependency(group + ":" + name + ":" + version);
    }

    public static Matcher<Configuration> hasDefaultDependency(String dependencyNotation) {
        return HasDependency.hasDefaultDependency(dependencyNotation);
    }

    public static Matcher<Project> hasTask(final String name) {
        return HasTask.hasTask(name);
    }

    public static Matcher<Task> dependsOn(String name) {
        return DependsOn.dependsOn(name);
    }

    public static Matcher<Task> finalizedBy(final String name) {
        return FinalizedBy.finalizedBy(name);
    }
}
