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

import org.gradle.api.Task;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.List;
import java.util.stream.Collectors;

public class DependsOn extends TypeSafeDiagnosingMatcher<Task> {

    public static Matcher<Task> dependsOn(String name) {
        return new DependsOn(name);
    }

    private final String name;

    private DependsOn(String name) {
        this.name = name;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a Task that depends on ").appendValue(name);
    }

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
}
