package com.dukescript.api.vue;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import net.java.html.js.JavaScriptBody;
import net.java.html.js.JavaScriptResource;
import net.java.html.json.Models;
import org.netbeans.html.context.spi.Contexts;
import org.netbeans.html.json.spi.FunctionBinding;
import org.netbeans.html.json.spi.PropertyBinding;
import org.netbeans.html.json.spi.Technology;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = Contexts.Provider.class)
public final class Vue implements Contexts.Provider {

    public static ComponentBuilder component(String tagName) {
        return new ComponentBuilder(tagName);
    }


    public static final class ComponentBuilder {
        String tagName;
        String template;
        Class<?> modelClass;
        Set<String> props = new LinkedHashSet<>();

        ComponentBuilder(String tagName) {
            this.tagName = tagName;
        }

        public ComponentBuilder model(Class<?> modelClass) {
            this.modelClass = modelClass;
            return this;
        }
        public ComponentBuilder template(String textOrReference) {
            this.template = textOrReference;
            return this;
        }

        public ComponentBuilder props(String... names) {
            props.addAll(Arrays.asList(names));
            return this;
        }

        public void register() {
            VueTech.registerComponent(this);
        }
    }

    static Object createComponent(Object callback) throws Exception {
        Object data = ((VueCallback)callback).initialize();
        if (data == null) {
            return VueTech.emptyObj();
        }
        assert Models.isModel(data.getClass());
        Object raw = Models.toRaw(data);
        return raw;
    }

    @Override
    public void fillContext(Contexts.Builder bldr, Class<?> type) {
        bldr.register(Technology.class, new VueTech(), 37037);
    }
}

@JavaScriptResource("vue.js")
final class VueTech implements Technology.BatchInit<VueTech.Item>,
Technology.ApplyId<VueTech.Item>, Technology.ValueMutated<VueTech.Item>,
Technology.ToJavaScript<VueTech.Item> {
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

        for (int i = 0; i < props.length; i++) {
            if (props[i].isReadOnly() || props[i].isConstant()) {
                propNames[i] = null;
            }
            if (propValues[i] instanceof Object[]) {
                propNames[i] = null;
            }
        }

        final Object watch = vueWatch(propNames, props);
        return new Item(data, methods, watch);
    }

    @JavaScriptBody(args = { "names", "values" }, body = ""
        + "var obj = {};\n"
        + "for (var i = 0; i < names.length; i++) {\n"
        + "  obj[names[i]] = values[i];\n"
        + "}\n"
        + "return obj;\n"
    )
    private static native Object vueData(String[] names, Object[] values);

    @JavaScriptBody(args = { "names", "bindings" }, javacall = true, body = ""
        + "var obj = {};\n"
            + " function createCallback(b) {\n"
            + "   return function(newValue) {\n"
            + "    @com.dukescript.api.vue.VueTech::vueValueChange"
            + "(Lorg/netbeans/html/json/spi/PropertyBinding;Ljava/lang/Object;)(b, newValue); \n"
            + "   };\n"
            + " }\n"
            + " for (var i = 0; i < names.length; i++) {\n"
            + "  if (names[i] !== null) {\n"
            + "    obj[names[i]] = createCallback(bindings[i]);\n"
            + "  }\n"
            + " }\n"
            + "return obj;\n"
    )
    private static native Object vueWatch(String[] names, PropertyBinding[] bindings);

    static void vueValueChange(PropertyBinding binding, Object newValue) {
        binding.setValue(newValue);
    }

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
    public void applyBindings(Item data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void applyBindings(String id, Item item) {
        item.js = vueCreate(id, item.data, item.methods, item.watch);
    }

    @Override
    public Object toJavaScript(Item item) {
        if (item.js == null) {
            item.js = vueCreate(null, item.data, item.methods, item.watch);
        }
        return item.js;
    }

    @JavaScriptBody(args = { "id", "data", "methods", "watch" }, body =
        " var app = new Vue({ \n" +
        "   el : id, \n" +
        "   data: data, \n" +
        "   methods: methods, \n" +
        "   watch: watch \n" +
        " }); \n" +
        " return app; \n"
    )
    private static native Object vueCreate(String id, Object data, Object methods, Object watch);

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
        final Object watch;

        Item(Object data, Object methods, Object watch) {
            this.data = data;
            this.methods = methods;
            this.watch = watch;
        }
    }

    static void registerComponent(Vue.ComponentBuilder b) {
        registerComponent0(
            b.tagName,
            b.props.toArray(new String[0]),
            b.template, () -> {
                if (b.modelClass == null) {
                    return null;
                }
                return b.modelClass.newInstance();
            }
        );
    }

    @JavaScriptBody(args = {  }, body = "return {};")
    static native Object emptyObj();

    @JavaScriptBody(
        args = {"name", "props", "template", "callback"},
        javacall = true, wait4js = false, body
            = "Vue.component(name, { \n"
            + "   data: function () { \n "
            + "     var v = @com.dukescript.api.vue.Vue::createComponent(Ljava/lang/Object;)(callback); \n"
            + "     for (var i = 0; i < props.length; i++) { \n"
            + "       delete v[props[i]]; \n"
            + "     } \n"
            + "     return v; \n"
            + "   }, \n"
            + "   props : props, \n"
            + "   template : template \n"
            + "});"
    )
    private static native void registerComponent0(String name, String[] props, String template, VueCallback callback);
}

@FunctionalInterface
interface VueCallback {
    Object initialize() throws Exception;
}
