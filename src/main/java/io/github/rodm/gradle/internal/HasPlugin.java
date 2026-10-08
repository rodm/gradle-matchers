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
import org.gradle.api.Project;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.List;
import java.util.stream.Collectors;

public class HasPlugin extends TypeSafeDiagnosingMatcher<Project> {

    public static Matcher<Project> hasPlugin(String id) {
        return new HasPlugin(id);
    }

    private final String id;

    private HasPlugin(String id) {
        this.id = id;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("Project should have plugin ").appendValue(id).appendText(" applied");
    }

    @Override
    protected boolean matchesSafely(final Project project, final Description mismatchDescription) {
        List<String> classNames = project.getPlugins().stream()
            .map(plugin -> plugin.getClass().getSimpleName())
            .collect(Collectors.toList());
        mismatchDescription.appendText(GradleMatchers.TEXT).appendValueList("[", ", ", "]", classNames);
        return project.getPluginManager().hasPlugin(id);
    }
}
