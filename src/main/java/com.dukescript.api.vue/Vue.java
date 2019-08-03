package com.dukescript.api.vue;

import net.java.html.js.JavaScriptBody;
import org.netbeans.html.context.spi.Contexts;
import org.netbeans.html.json.spi.FunctionBinding;
import org.netbeans.html.json.spi.PropertyBinding;
import org.netbeans.html.json.spi.Technology;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = Contexts.Provider.class)
public final class Vue implements Contexts.Provider {
    @Override
    public void fillContext(Contexts.Builder bldr, Class<?> type) {
        bldr.register(Technology.class, new VueTech(), 37037);
    }
}

final class VueTech implements Technology.BatchInit<VueTech.Item>,
Technology.ApplyId<VueTech.Item>, Technology.ValueMutated<VueTech.Item> {
    @Override
    public Item wrapModel(Object model, PropertyBinding[] props, FunctionBinding[] functions) {
        String[] propNames = new String[props.length];
        Object[] propValues = new Object[props.length];
        for (int i = 0; i < props.length; i++) {
            propNames[i] = props[i].getPropertyName();
            propValues[i] = props[i].getValue();
        }
        String[] functionNames = new String[functions.length];
        for (int i = 0; i < functions.length; i++) {
            functionNames[i] = functions[i].getFunctionName();
        }
        final Object data = vueData(propNames, propValues);
        final Object methods = vueMethods(model, functionNames, functions);
        return new Item(data, methods);
    }

    @JavaScriptBody(args = { "names", "values" }, body = ""
        + "var obj = {};\n"
        + "for (var i = 0; i < names.length; i++) {\n"
        + "  obj[names[i]] = values[i];\n"
        + "}\n"
        + "return obj;\n"
    )
    private static native Object vueData(String[] names, Object[] values);

    @JavaScriptBody(args = { "model", "names", "bindings" }, javacall = true, body = ""
        + " var obj = {}; \n "
        + " function createCallback(b) {\n"
        + "   return function(event) {\n"
        + "    @com.dukescript.api.vue.VueTech::vueMethodCall"
        + "(Lorg/netbeans/html/json/spi/FunctionBinding;Ljava/lang/Object;Ljava/lang/Object;)(b, model, event); \n"
        + "   };\n"
        + " }\n"
        + " for (var i = 0; i < names.length; i++) {\n"
        + "  obj[names[i]] = createCallback(bindings[i]);\n"
        + " }\n"
        + " return obj;\n"
    )
    private static native Object vueMethods(Object model, String[] names, Object[] bindings);

    static void vueMethodCall(FunctionBinding fb, Object model, Object event) {
        fb.call(model, event);
    }

    @Override
    public Item wrapModel(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <M> M toModel(Class<M> type, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void bind(PropertyBinding pb, Object o, Item data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void valueHasMutated(Item data, String name) {
        valueHasMutated(data, name, null, null);
    }

    @Override
    public void valueHasMutated(Item data, String name, Object oldValue, Object newValue) {
        vueChangeData(data.js, name, newValue);
    }

    @Override
    public void expose(FunctionBinding fb, Object o, Item data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void applyBindings(String id, Item item) {
        item.js = vueCreate(id, item.data, item.methods);
    }

    @Override
    public void applyBindings(Item data) {
        throw new UnsupportedOperationException();
    }

    @JavaScriptBody(args = { "id", "data", "methods" }, body =
        " var app = new Vue({ \n" +
        "   el : id, \n" +
        "   data: data, \n" +
        "   methods: methods \n" +
        " }); \n" +
        " return app; \n"
    )
    private static native Object vueCreate(String id, Object data, Object methods);

    @JavaScriptBody(args = { "app", "name", "value" }, wait4js = false, body = "app[name] = value;")
    private static native void vueChangeData(Object app, String name, Object value);

    @Override
    public Object wrapArray(Object[] arr) {
        return arr;
    }

    @Override
    public void runSafe(Runnable r) {
        r.run();
    }

    static final class Item {
        Object js;
        final Object data;
        final Object methods;

        Item(Object data, Object methods) {
            this.data = data;
            this.methods = methods;
        }
    }
}
