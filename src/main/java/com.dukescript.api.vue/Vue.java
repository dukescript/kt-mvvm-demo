package com.dukescript.api;

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
        Technology.ApplyId<VueTech.Item> {
    @Override
    public Item wrapModel(Object arg0, PropertyBinding[] props, FunctionBinding[] arg2) {
        String[] names = new String[props.length];
        Object[] values = new Object[props.length];
        for (int i = 0; i < props.length; i++) {
            names[i] = props[i].getPropertyName();
            values[i] = props[i].getValue();
        }
        final Object data = vueData(names, values);
        return new Item(data);
    }

    @JavaScriptBody(args = { "names", "values" }, body = ""
        + "var obj = {};\n"
        + "for (var i = 0; i < names.length; i++) {\n"
        + "  obj[names[i]] = values[i];\n"
        + "}\n"
        + "return obj;\n"
    )
    private static native Object vueData(String[] names, Object[] values);

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
    public void valueHasMutated(Item data, String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void expose(FunctionBinding fb, Object o, Item data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void applyBindings(String id, Item item) {
        vueCreate(id, item.data);
    }

    @Override
    public void applyBindings(Item data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @JavaScriptBody(args = { "id", "data" }, wait4js = false, body =
        "var app = new Vue({ \n" +
        "  el : id, \n" +
        "  data: data \n" +
        "}); \n" +
        "return app; \n"
    )
    private static native void vueCreate(String id, Object data);

    @Override
    public Object wrapArray(Object[] arr) {
        return arr;
    }

    @Override
    public void runSafe(Runnable r) {
        r.run();
    }

    static final class Item {
        final Object data;

        Item(Object data) {
            this.data = data;
        }
    }
}
