package de.craftery.craftinghomes.annotation;

import de.craftery.craftinghomes.annotation.annotations.I18nDef;
import de.craftery.craftinghomes.annotation.annotations.I18nSource;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@SupportedAnnotationTypes("de.craftery.craftinghomes.annotation.annotations.*")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class CraftingAnnotationProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return processI18nAnnotations(roundEnv);
    }

    private boolean processI18nAnnotations(RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(I18nSource.class);
        if (elements.size() > 1) {
            error("There can only be one I18nSource per project for now!");
            return false;
        }
        if (elements.isEmpty()) {
            return false;
        }

        Element providerElement = elements.iterator().next();
        if (!(providerElement instanceof TypeElement providerClass)) {
            error("Entry element must be a interface!");
            return false;
        }

        if (providerClass.getKind() != ElementKind.INTERFACE) {
            error("Entry element must be a interface!");
            return false;
        }

        Map<String, String> translations = new HashMap<>();
        Map<String, Map<String, String>> parameters = new HashMap<>();

        for (Element type : providerClass.getEnclosedElements()) {
            if (type.getKind() != ElementKind.METHOD) {
                error("Each entry in this interface must be a method!");
                return false;
            }
            ExecutableElement method = (ExecutableElement) type;

            if (!method.getReturnType().toString().equals("java.lang.String")) {
                error("Each entry in this interface must return a String! Got: " + method.getReturnType().toString());
                return false;
            }
            if (type.getAnnotation(I18nDef.class) == null) {
                error("Each entry in this interface must be annotated with @I18nDef!");
                return false;
            }

            Map<String, String> methodParameters = new HashMap<>();
            for (VariableElement parameter : method.getParameters()) {
                String paramType;
                switch (parameter.asType().toString()) {
                    case "java.lang.String" -> paramType = "String";
                    case "java.lang.Integer" -> paramType = "Integer";
                    default -> {
                        error("Unimplemented parameter type: " + parameter.asType().toString() + " in method " + method.getSimpleName().toString());
                        return false;
                    }
                }
                methodParameters.put(parameter.getSimpleName().toString(), paramType);
            }

            String def = type.getAnnotation(I18nDef.class).def();
            translations.put(type.getSimpleName().toString(), def);
            parameters.put(type.getSimpleName().toString(), methodParameters);
        }

        String providerPath = providerClass.getQualifiedName().toString();
        String providerName = providerClass.getAnnotation(I18nSource.class).name();

        String packageName = null;
        int lastDot = providerPath.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = providerPath.substring(0, lastDot);
        }
        String generatedClassName = packageName + "." + providerName;

        try {
            JavaFileObject builderFile = processingEnv.getFiler()
                    .createSourceFile(generatedClassName);

            try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
                // class header
                if (packageName != null) {
                    out.print("package ");
                    out.print(packageName);
                    out.println(";");
                    out.println();
                }

                out.print("public class ");
                out.print(providerName);
                out.print(" extends BaseI18nProvider implements ");
                out.print(providerPath);
                out.println(" {");

                // adding all translations to outer object
                out.println("    @Override");
                out.println("    protected void addAllTranslations() {");

                for (Map.Entry<String, String> implementation : translations.entrySet()) {
                    out.print("        this.defTranslations.put(\"");
                    out.print(implementation.getKey());
                    out.print("\", \"");
                    out.print(implementation.getValue());
                    out.println("\");");
                }

                out.println("    }");

                // implement all methods of interface
                for (Map.Entry<String, String> implementation : translations.entrySet()) {
                    out.println();
                    out.println("    @Override");
                    out.print("    public String ");
                    out.print(implementation.getKey());
                    out.print("(");

                    List<String> paramTypes = parameters.get(implementation.getKey()).entrySet().stream()
                            .map(el -> el.getValue() + " " + el.getKey()).toList();
                    out.print(String.join(", ", paramTypes));

                    out.println(") {");
                    out.print("        String translation = this.getTranslation(\"");
                    out.print(implementation.getKey());
                    out.println("\");");

                    for (Map.Entry<String, String> parameter : parameters.get(implementation.getKey()).entrySet()) {
                        String replaceMethod;
                        switch (parameter.getValue()) {
                            case "String" -> replaceMethod = "replaceString";
                            case "Integer" -> replaceMethod = "replaceInteger";
                            default -> {
                                error("Unimplemented parameter type: " + parameter.getValue());
                                return false;
                            }
                        }

                        out.print("        translation = this.");
                        out.print(replaceMethod);
                        out.print("(translation, \"${");
                        out.print(parameter.getKey());
                        out.print("}\", ");
                        out.print(parameter.getKey());
                        out.println(");");

                    }

                    out.println("        return translation;");

                    out.println("    }");
                }

                out.println();
                out.println("}");
            }
        } catch (IOException e) {
            error("Failed to create file: " + e.getMessage());
            return false;
        }

        return true;
    }

    private void error(String message) {
        this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message);
    }
}
