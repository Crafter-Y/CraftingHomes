package de.craftery.craftinghomes.annotation;

import de.craftery.craftinghomes.annotation.annotations.Command;
import de.craftery.craftinghomes.annotation.annotations.DataModel;
import de.craftery.craftinghomes.annotation.annotations.I18nDef;
import de.craftery.craftinghomes.annotation.annotations.I18nSource;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("de.craftery.craftinghomes.annotation.annotations.*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CraftingAnnotationProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        boolean success = processI18nAnnotations(roundEnv);
        success = processCommandAnnotations(roundEnv) && success;
        success = processDataModelAnnotations(roundEnv) && success;
        return success;
    }

    private boolean processDataModelAnnotations(RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(DataModel.class);
        if (elements.size() == 0) {
            return false;
        }
        List<String> commands = new ArrayList<>();
        for (Element element : elements) {
            if (!(element instanceof TypeElement)) {
                error("Target must be a class");
                return false;
            }
            TypeElement providerClass = (TypeElement) element;

            commands.add(providerClass.getQualifiedName().toString());
        }

        try {
            FileObject file = this.processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "datasources.txt");
            try (Writer w = file.openWriter()) {
                String raw = String.join("\n", commands);
                w.write(raw);
                w.flush();
                w.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    private boolean processCommandAnnotations(RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Command.class);
        if (elements.size() == 0) {
            return false;
        }

        List<String> commands = new ArrayList<>();
        for (Element element : elements) {
            if (!(element instanceof TypeElement)) {
                error("Target must be a class");
                return false;
            }
            TypeElement providerClass = (TypeElement) element;

            commands.add(providerClass.getQualifiedName().toString());
        }

        try {
            FileObject file = this.processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "commands.txt");
            try (Writer w = file.openWriter()) {
                String raw = String.join("\n", commands);
                w.write(raw);
                w.flush();
                w.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
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
        if (!(providerElement instanceof TypeElement)) {
            error("Entry element must be a interface!");
            return false;
        }
        TypeElement providerClass = (TypeElement) providerElement;

        if (providerClass.getKind() != ElementKind.INTERFACE) {
            error("Entry element must be a interface!");
            return false;
        }

        Map<String, String> translations = new HashMap<>();
        Map<String, List<Map.Entry<String, String>>> parameters = new HashMap<>();

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

            List<Map.Entry<String, String>> methodParameters = new ArrayList<>();
            for (VariableElement parameter : method.getParameters()) {
                String paramType;
                switch (parameter.asType().toString()) {
                    case "java.lang.String": { paramType = "String"; break;}
                    case "java.lang.Integer": { paramType = "Integer"; break;}
                    default: {
                        error("Unimplemented parameter type: " + parameter.asType().toString() + " in method " + method.getSimpleName().toString());
                        return false;
                    }
                }
                methodParameters.add(new AbstractMap.SimpleEntry<>(parameter.getSimpleName().toString(), paramType));
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

                    List<String> paramTypes = parameters.get(implementation.getKey()).stream()
                            .map(el -> el.getValue() + " " + el.getKey()).collect(Collectors.toList());
                    out.print(String.join(", ", paramTypes));

                    out.println(") {");
                    out.print("        String translation = this.getTranslation(\"");
                    out.print(implementation.getKey());
                    out.println("\");");

                    for (Map.Entry<String, String> parameter : parameters.get(implementation.getKey())) {
                        String replaceMethod;
                        switch (parameter.getValue()) {
                            case "String": { replaceMethod = "replaceString"; break; }
                            case "Integer": { replaceMethod = "replaceInteger"; break;}
                            default: {
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
