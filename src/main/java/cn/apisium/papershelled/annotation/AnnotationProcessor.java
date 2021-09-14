package cn.apisium.papershelled.annotation;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.StandardLocation;
import java.io.Writer;
import java.util.*;

public class AnnotationProcessor {
    private static final class PaperShelledPluginDescription {
        public List<String> mixins = new ArrayList<>();
    }
    public static void process(TypeElement main, ProcessingEnvironment env) {
        PaperShelledDescription psDesc = main.getAnnotation(PaperShelledDescription.class);
        if (psDesc != null) {
            try (Writer out = env.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "",
                    "plugin.yml").openWriter()) {
                PaperShelledPluginDescription obj = new PaperShelledPluginDescription();
                for (Mixins mixin : psDesc.mixins()) {
                    Class<?>[] classes = mixin.value();
                    if (classes.length == 0) continue;
                    String[] packageNames = classes[0].getPackage().getName().split("\\.");
                    for (int j = 1; j < classes.length; j++) {
                        String[] names = classes[j].getPackage().getName().split("\\.");
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
                        for (Class<?> clazz : classes) {
                            String pkg = clazz.getPackage().getName().substring(pkgName.length());
                            arr.add(pkg.startsWith(".") ? pkg.substring(1) : pkg);
                        }
                        json.addProperty("required", mixin.required());
                        json.addProperty("package", pkgName);
                        json.addProperty("compatibilityLevel", mixin.compatibilityLevel());
                        json.add("mixins", arr);
                        new Gson().toJson(json, out2);
                    }
                    obj.mixins.add(file);
                }
                new Gson().toJson(obj, out);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }
}
