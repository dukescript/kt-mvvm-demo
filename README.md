Skeletal application for the *JavaFX Light* project. 

The example is composing two technologies - [JavaFX and its properties](https://dukescript.com/javadoc/javafx/)
and [Material CSS](https://materializecss.com/) - a set of slick components
to design your UI. The example is using simple [data-bind](https://knockoutjs.com/documentation)
directives to connect the *JavaFX beans* with the *UI components*.

The application logic is code in [Java](src/main/java/com/kt/mvvm/demo/Demo.java#L36) and
defines bean with *JavaFX* properties:

```java
private final StringProperty desc = new SimpleStringProperty(this, "desc", "Buy Milk");
private final ListProperty<String> todos = new SimpleListProperty<>(this, "todos", FXCollections.observableArrayList());
private final IntegerBinding numTodos = Bindings.createIntegerBinding(todos::size, todos);

void addTodo() {
    todos.getValue().add(desc.getValue());
    desc.setValue("");
}
```

those properties are then [declaratively assigned](src/main/webapp/pages/index.html#L54)
to the *Materialize UI* components to form a [slick, modern application](https://dukescript.com/javafxlight/).

Get started with a few simple commands:

```bash
$ git clone https://github.com/dukescript/kt-mvvm-demo.git
$ cd kt-mvvm-demo
$ git checkout FXBeanInfo
$ ./gradlew run # desktop
$ ./gradlew bck2brwsrShow # browser
$ ./gradlew installDebug # Android
$ ./gradlew moeLaunch # iOS
```
