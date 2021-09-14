package cn.apisium.papershelled.annotation;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.function.Supplier;

public class AnnotationProcessor {
    private static final class PaperShelledPluginDescription {
        public List<String> mixins = new ArrayList<>();
    }
    public static void process(TypeElement main, ProcessingEnvironment env) throws IOException {
        Types tu = env.getTypeUtils();
        Elements eu = env.getElementUtils();
        boolean flag = false;
        Mixin[] mixins = main.getAnnotationsByType(Mixin.class);
        PaperShelledPluginDescription obj = new PaperShelledPluginDescription();
        for (Mixin mixin : mixins) {
            String[][] classes = getTypeMirrors(mixin::value).stream().map(it -> {
                TypeElement elm = (TypeElement) tu.asElement(it);
                return new String[] {
                        eu.getPackageOf(elm).getQualifiedName().toString(),
                        elm.getSimpleName().toString()
                };
            }).toArray(String[][]::new);
            if (classes.length == 0) continue;
            String[] packageNames = classes[0][0].split("\\.");
            for (int j = 1; j < classes.length; j++) {
                String[] names = classes[j][0].split("\\.");
                int len = Math.min(packageNames.length, names.length);
                for (int i = 0; i < len; i++) {
                    if (!names[i].equals(packageNames[i]))  {
                        packageNames = Arrays.copyOf(packageNames, i);
                        break;
                    }
                }
                if (len < packageNames.length) packageNames = Arrays.copyOf(packageNames, len);
            }
            String pkgName = String.join(".", packageNames);
            String file = (pkgName.isEmpty() ? new Date().getTime() : pkgName) + ".mixins.json";
            try (Writer out2 = env.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "",
                    file).openWriter()) {
                JsonObject json = new JsonObject();
                JsonArray arr = new JsonArray();
                for (String[] clazz : classes) {
                    String pkg = clazz[0].substring(pkgName.length());
                    if (pkg.startsWith(".")) pkg = pkg.substring(1);
                    arr.add((pkg.isEmpty() ? "" : pkg + ".") + clazz[1]);
                }
                json.addProperty("required", mixin.required());
                json.addProperty("package", pkgName);
                if (!mixin.compatibilityLevel().isEmpty())
                    json.addProperty("compatibilityLevel", mixin.compatibilityLevel());
                if (!mixin.minVersion().isEmpty())
                    json.addProperty("minVersion", mixin.minVersion());
                json.add("mixins", arr);
                new Gson().toJson(json, out2);
            }
            flag = true;
            obj.mixins.add(file);
        }
        if (flag) try (Writer out = env.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "",
                "papershelled.plugin.json").openWriter()) {
            new Gson().toJson(obj, out);
        }
    }

    private static List<? extends TypeMirror> getTypeMirrors(Supplier<?> runnable) {
        try {
            runnable.get();
        } catch (MirroredTypesException e) {
            return e.getTypeMirrors();
        }
        throw new RuntimeException();
    }
}
