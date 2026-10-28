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

import org.gradle.api.Action;
import org.gradle.api.Task;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.rodm.gradle.GradleMatchers.TEXT;

public class HasAction extends TypeSafeDiagnosingMatcher<Task> {

    public static Matcher<Task> hasAction(final Class<? super Action<Task>> type) {
        return new HasAction(type);
    }

    private final Class<? super Action<Task>> type;

    private HasAction(Class<? super Action<Task>> type) {
        this.type = type;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Task should have an action of type ").appendValue(type.getSimpleName());
    }

    @Override
    protected boolean matchesSafely(Task task, Description mismatchDescription) {
        List<String> actionNames = task.getActions().stream()
            .filter(HasAction::hasActionField)
            .map(HasAction::actionTypeName)
            .collect(Collectors.toList());
        mismatchDescription.appendText(TEXT).appendValueList("[", ", ", "]", actionNames);
        return actionNames.contains(type.getName());
    }

    private static boolean hasActionField(final Action<? super Task> action) {
        // TaskActionWrapper stores the wrapped action in a field called 'action'
        return getActionField(action) != null;
    }

    private static String actionTypeName(final Action<? super Task> action) {
        Action<? super Task> wrappedAction = unwrapAction(action);
        return wrappedAction == null ? "" : wrappedAction.getClass().getName();
    }

    private static Action<? super Task> unwrapAction(final Action<? super Task> action) {
        try {
            Field field = getActionField(action);
            field.setAccessible(true);
            return (Action<? super Task>) field.get(action);
        }
        catch (IllegalAccessException e) {
            // ignore - should not happen
            return null;
        }
    }

    private static Field getActionField(Action<? super Task> action) {
        try {
            return action.getClass().getDeclaredField("action");
        }
        catch (NoSuchFieldException e) {
            return null;
        }
    }
}
