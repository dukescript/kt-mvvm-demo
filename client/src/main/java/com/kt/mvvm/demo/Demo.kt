@file:JvmName("Main")

package com.kt.mvvm.demo


import net.java.html.boot.BrowserBuilder
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


class Demo : Model.Provider {
    override val objs = Model(this)

    var newTodo by observable("Buy Milk",{println("value has changed")})
    val todos: MutableList<String> by observableList()
    
    val add by action {
        todos += newTodo
        newTodo = ""
    }
}