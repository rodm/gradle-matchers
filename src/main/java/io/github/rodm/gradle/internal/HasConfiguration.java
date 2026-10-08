/*
 * Copyright 2026 Rod MacKenzie
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
package io.github.rodm.gradle.internal;

import io.github.rodm.gradle.GradleMatchers;
import org.gradle.api.Named;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.List;
import java.util.stream.Collectors;

public class HasConfiguration extends TypeSafeDiagnosingMatcher<Project> {

    public static Matcher<Project> hasConfiguration(String name) {
        return new HasConfiguration(name);
    }

    private final String name;

    private HasConfiguration(String name) {
        this.name = name;
    }

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
        mismatchDescription.appendText(GradleMatchers.TEXT).appendValueList("[", ",", "]", configurationNames);
        return configurations.findByName(name) != null;
    }
}
