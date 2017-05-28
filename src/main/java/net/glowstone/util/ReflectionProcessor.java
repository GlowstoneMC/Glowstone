package net.glowstone.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectionProcessor {

    private String line;
    private Object[] context;

    /**
     * Creates a new reflection processor instance
     *
     * @param line    the reflection line. The format of this line is defined by the following;
     *                Use $ to reference the first context (equivalent to $1)
     *                Use $x to reference a specific context, where 'x' is the index of the context (starting from 1)
     *                To reference a static method/field call, specifying the full package is required
     *                More info here: https://github.com/momothereal/OneLineReflection/blob/master/README.md
     * @param context the context(s) of the reflection line
     */
    public ReflectionProcessor(String line, Object... context) {
        this.line = line;
        this.context = context;
    }

    /**
     * Processes the given reflective line
     *
     * @return the resultant value of the reflective line
     */
    public Object process() {
        String tmpPackage = "";
        Object cxt = null;
        String[] sections = splitUpper();
        for (int i = 0; i < sections.length; i++) {
            String section = sections[i];
            if (section.equals("$") || section.equals("this")) {                     // Context #1
                cxt = context[0];
            } else if (section.length() > 0 && section.charAt(0) == '$' && section.substring(1, section.length()).matches("[0-9]+")) { // Context #X
                int index = Integer.valueOf(section.replace("$", "")) - 1;
                cxt = context[index];
            } else if (section.length() > 0 && section.charAt(0) == '\"' && section.length() > 0 && section.charAt(section.length() - 1) == '\"') {         // String literal
                section = section.substring(1, section.length() - 1);
                if (i == sections.length - 1)
                    return section;
                cxt = section;
            } else if (section.equals("true") || section.equals("false")) {          // Boolean literal
                Boolean value = Boolean.valueOf(section);
                if (i == sections.length - 1)
                    return value;
                cxt = value;
            } else if (section.replaceAll("[0-9]+", "").equals("")) {                // Integer literal
                Integer value = Integer.valueOf(section);
                if (i == sections.length - 1)
                    return value;
                cxt = value;
            } else if (section.replaceAll("[0-9]+", "").toLowerCase().equals("l")) { // Long literal
                Long value = Long.valueOf(section.substring(0, section.length() - 1));
                if (i == sections.length - 1)
                    return value;
                cxt = value;
            } else if (section.contains("(") && section.contains(")")) {             // Method
                String name = getMethodName(section);
                String[] parameters = getMethodParams(section);
                if (i == sections.length - 1)
                    return invokeMethod(cxt, name, parameters);
                cxt = invokeMethod(cxt, name, parameters);
            } else {
                Object field = invokeField(cxt, section);
                if (field == null) {                                                 // Class
                    tmpPackage += section + ".";
                    Object clazz = invokeClass(tmpPackage);
                    if (clazz != null) {
                        if (i == sections.length - 1)
                            return clazz;
                        cxt = clazz;
                        tmpPackage = "";
                    }
                } else {                                                             // Field
                    if (i == sections.length - 1)
                        return field;
                    cxt = field;
                }
            }
        }
        return cxt;
    }

    /**
     * Returns the returned value of an invoked method in a contextual object
     *
     * @param context the object
     * @param name    the name of the method
     * @return the invokation's return value
     */
    private Object invokeMethod(Object context, String name, String... parameters) {
        try {
            ArrayList<Object> params = new ArrayList<>();
            for (String parameter : parameters) {
                Object result = new ReflectionProcessor(parameter, this.context).process();
                if (result != null) {
                    params.add(result);
                }
            }
            Class[] classes = new Class[params.size()];
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                classes[i] = param.getClass();
            }
            Class clazz = context.getClass();
            if (context instanceof Class)
                clazz = (Class) context;
            Method method = getMethod(name, clazz, classes);
            if (!method.isAccessible())
                method.setAccessible(true);
            return method.invoke(context, params.toArray(new Object[params.size()]));
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Method getMethod(String name, Class clazz, Class... parameters) {
        try {
            return clazz.getMethod(name, parameters);
        } catch (NoSuchMethodException e) {
            for (Method method : clazz.getMethods()) {
                if (!method.getName().equals(name)) {
                    continue;
                }
                if (method.getParameterCount() != parameters.length) {
                    continue;
                }
                boolean matches = true;
                a:
                for (Class<?> param : method.getParameterTypes()) {
                    for (Class p : parameters) {
                        if (!p.equals(param)) {
                            matches = false;
                            break a;
                        }
                    }
                }
                if (matches) {
                    return method;
                }
                matches = true;
                b:
                for (Class<?> param : method.getParameterTypes()) {
                    for (Class p : parameters) {
                        if (!param.isAssignableFrom(p)) {
                            matches = false;
                            break b;
                        }
                    }
                }
                if (matches) {
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * Returns the value of a field in a contextual object
     *
     * @param context the object
     * @param name    the name of the field
     * @return the field's value in context
     */
    private Object invokeField(Object context, String name) {
        try {
            Field field = context.getClass().getField(name);
            if (!field.isAccessible())
                field.setAccessible(true);
            return field.get(context);
        } catch (Exception ignored) {
            // is it an enum?
            if (context != null && context instanceof Class) {
                boolean isEnum = ((Class) context).isEnum();
                if (isEnum) {
                    try {
                        return getMethod("valueOf", (Class) context, String.class).invoke(null, name);
                    } catch (Exception ignored1) {
                    }
                }
            }
        }
        return null;
    }

    /**
     * Invokes a class from its full-length name, including package
     *
     * @param name the name of the class, preceded with its package
     * @return the specified class, null if it is non-existent
     */
    private Class invokeClass(String name) {
        if (name.length() > 0 && name.charAt(name.length() - 1) == '.')
            name = name.substring(0, name.length() - 1);
        try {
            return (Class) ClassLoader.getSystemClassLoader().loadClass(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Gets the parameters inside a method parentheses enclosure
     *
     * @param section the method and its parameters, which are enclosed in parentheses and separated with commas (,)
     * @return the parameters
     */
    private String[] getMethodParams(String section) {
        int level = 0;
        boolean inString = false;
        String current = "";
        List<String> parameters = new ArrayList<>();

        char[] charArray = section.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (c == '(' && !inString) {
                level++;
                if (level > 1) {
                    current += c;
                }
                continue;
            }
            if (c == ')' && !inString) {
                level--;
                if (level == 0) {
                    parameters.add(current);
                    current = "";
                } else {
                    current += c;
                }
                continue;
            }
            if (level == 1 && c == ',' && !inString) {
                parameters.add(current);
                current = "";
                continue;
            }
            if (!inString && c == ' ')
                continue;
            if (c == '\"' && section.charAt(i - 1) != '\\') {
                inString = !inString;
            }
            if (level > 0)
                current += c;
        }
        return parameters.toArray(new String[parameters.size()]);
    }

    /**
     * Gets the name of a method from a segment
     *
     * @param section the segment containing the method (including parentheses)
     * @return the name of the method
     */
    private String getMethodName(String section) {
        StringBuilder builder = new StringBuilder();
        for (char c : section.toCharArray()) {
            if (c == '(')
                return builder.toString();
            builder.append(c);
        }
        return builder.toString();
    }

    private String[] splitUpper() {
        ArrayList<String> sections = new ArrayList<>();
        String current = "";
        int level = 0;
        for (char c : line.toCharArray()) {
            if (level == 0 && c == '.') {
                sections.add(current);
                current = "";
                continue;
            }
            if (c == '(') {
                level++;
            }
            if (c == ')') {
                level--;
            }
            current += c;
        }
        if (!current.equals(""))
            sections.add(current);
        return sections.toArray(new String[sections.size()]);
    }
}
