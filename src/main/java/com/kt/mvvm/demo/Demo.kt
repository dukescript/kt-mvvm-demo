@file:JvmName("Main")

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
package com.kt.mvvm.demo


import net.java.html.boot.BrowserBuilder
import net.java.html.js.JavaScriptBody

import com.dukescript.api.kt.Model
import com.dukescript.api.kt.action
import com.dukescript.api.kt.actionWithData
import com.dukescript.api.kt.applyBindings
import com.dukescript.api.kt.computed
import com.dukescript.api.kt.observable
import com.dukescript.api.kt.observableList
import com.dukescript.api.kt.loadJSON

fun main(args: Array<String>) {
    BrowserBuilder.newBrowser().loadPage("pages/index.html")
            .loadFinished {
                onPageLoad()
            }
            .showAndWait();
    System.exit(0);
}

fun onPageLoad() {
    val model = Demo()
    applyBindings(model);
}

@JavaScriptBody(args = arrayOf("msg"), body = "return confirm(msg);")
private fun confirm(msg: String): Boolean {
    throw IllegalStateException("not implemented confirm: ${msg}")
}

class Demo : Model.Provider {
    override val objs = Model(this)

    var newTodo by observable("Buy Milk")
    val todos: MutableList<String> by observableList()

    val numTodos by computed {
        todos.size
    }

    val add by action {
        todos += newTodo
        newTodo = ""
    }

    val clear by action {
        if (confirm("Clear all todos?")) {
            todos.clear()
        }
    }
}