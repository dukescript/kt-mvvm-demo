/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2017 Anton Epple <toni.epple@eppleton.de>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.kt.mvvm.demo;

import com.dukescript.api.javafx.beans.FXBeanInfo;
import com.dukescript.api.vue.Vue;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import static net.java.html.json.Models.applyBindings;

public final class Demo implements FXBeanInfo.Provider {
    private final StringProperty desc = new SimpleStringProperty(this, "desc", "Buy Milk");
    private final BooleanBinding emptyDesc = Bindings.equal(desc, "");
    private final ListProperty<Item> todos = new SimpleListProperty<>(this, "todos", FXCollections.observableArrayList());
    private final IntegerBinding numTodos = Bindings.createIntegerBinding(this::pendingTodos, todos);
    private final BooleanBinding noFinishedTodos = Bindings.createBooleanBinding(() -> {
        for (Item item : todos) {
            if (item.done.get()) {
                return false;
            }
        }
        return true;
    }, todos, numTodos);

    final class Item implements FXBeanInfo.Provider {
        final BooleanProperty done = new SimpleBooleanProperty(this, "done", false);
        final StringProperty text = new SimpleStringProperty(this, "text", "");

        Item(String text) {
            this.text.setValue(text);
            this.done.addListener((o) -> {
                numTodos.invalidate();
            });
        }

        private final FXBeanInfo info = FXBeanInfo.newBuilder(this)
            .property(done)
            .property(text)
            .build();

        @Override
        public FXBeanInfo getFXBeanInfo() {
            return info;
        }
    }

    void addTodo() {
        todos.add(new Item(desc.getValue()));
        desc.setValue("");
    }

    void clearTodos() {
        todos.removeIf((item) -> item.done.get());
    }

    int pendingTodos() {
        int cnt = 0;
        for (Item item : todos) {
            if (!item.done.get()) {
                cnt++;
            }
        }
        return cnt;
    }

    private final FXBeanInfo info = FXBeanInfo.newBuilder(this)
            .property(desc)
            .property("emptyDesc", emptyDesc)
            .property(todos)
            .property("numTodos", numTodos)
            .property("noFinishedTodos", noFinishedTodos)
            .action("addTodo", this::addTodo)
            .action("clearTodos", this::clearTodos)
            .build();

    @Override
    public FXBeanInfo getFXBeanInfo() {
        return info;
    }

    public static void onPageLoad() {
        Vue.component("todo-item")
            .props("item")
            .template("<span><input type='checkbox' v-model='item.done'> {{item.text}}</span>")
            .register();
        Demo model = new Demo();
        applyBindings(model, "#app");
    }

}