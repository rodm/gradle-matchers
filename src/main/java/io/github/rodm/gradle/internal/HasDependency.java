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
import org.gradle.api.artifacts.Configuration;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HasDependency extends TypeSafeDiagnosingMatcher<Configuration> {

    public static Matcher<Configuration> hasDependency(String dependencyNotation) {
        return new HasDependency(dependencyNotation, dependencies);
    }

    public static Matcher<Configuration> hasDefaultDependency(String dependencyNotation) {
        return new HasDependency(dependencyNotation, defaultDependencies);
    }

    private static final Function<Configuration, List<String>> dependencies = (Configuration c) ->
        c.getDependencies().stream()
            .map(Object::toString)
            .collect(Collectors.toList());

    private static final Function<Configuration, List<String>> defaultDependencies = (Configuration c) ->
        c.getIncoming().getDependencies().stream()
            .map(Object::toString)
            .collect(Collectors.toList());

    private final String dependencyNotation;
    private final Function<Configuration, List<String>> collector;

    private HasDependency(String dependencyNotation, Function<Configuration, List<String>> collector) {
        this.dependencyNotation = dependencyNotation;
        this.collector = collector;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Configuration should contain dependency ").appendValue(dependencyNotation);
    }

    @Override
    protected boolean matchesSafely(Configuration configuration, Description mismatchDescription) {
        List<String> dependencies = collector.apply(configuration);
        mismatchDescription.appendText(GradleMatchers.TEXT).appendValueList("[", ", ", "]", dependencies);
        return dependencies.contains(dependencyNotation);
    }
}
