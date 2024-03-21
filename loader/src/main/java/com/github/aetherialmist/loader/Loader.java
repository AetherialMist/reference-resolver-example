package com.github.aetherialmist.loader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Loader {

    public static final String LOADER_FILE_PATH = "META-INF/loader.properties";

    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    private static final Map<String, String> resolverMap = Collections.synchronizedMap(new HashMap<>());
    private static final Map<Class<?>, Resolver<?>> cache = Collections.synchronizedMap(new HashMap<>());

    private static void initialize() {
        if (initialized.get()) {
            return;
        }
        Loader.resolverMap.putAll(loadResolverMap());
        initialized.set(true);
    }

    private static Map<String, String> loadResolverMap() {
        Map<String, String> resolverMap = new HashMap<>();

        Enumeration<URL> propertyFiles;
        try {
            propertyFiles = Loader.class.getClassLoader().getResources(LOADER_FILE_PATH);
        } catch (IOException e) {
            throw new LoaderException("Failed to load all property file locations.", e);
        }

        while (propertyFiles.hasMoreElements()) {
            URL resource = propertyFiles.nextElement();
            Properties properties = loadProperties(resource);
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                String resolverClassName = (String) entry.getKey();
                String referenceValueClassName = (String) entry.getValue();
                resolverMap.put(referenceValueClassName, resolverClassName);
            }
        }
        return resolverMap;
    }

    private static Properties loadProperties(URL resource)  {
        Properties properties = new Properties();
        try (InputStream is = resource.openStream()) {
            properties.load(is);
        } catch (IOException e) {
            throw new LoaderException("Failed to load properties from file: " + resource, e);
        }
        return properties;
    }

    public static <T extends ReferenceValue> T resolveReference(String reference, Class<T> type, ClassLoader classLoader) {
        initialize();
        return type.cast(cache.computeIfAbsent(type, t -> createResolver(type, classLoader)).resolve(reference));
    }

    private static <T extends ReferenceValue> Resolver<T> createResolver(Class<T> type, ClassLoader classLoader) {
        String resolverClassName = resolverMap.get(type.getName());
        if (resolverClassName == null) {
            throw new IllegalStateException("No resolver found for type " + type);
        }
        try {
            // If the resolver is not of the correct type, mapped in the 'META-INF/loader.properties' file, that is a defect of the implementor of the Resolver interface.
            @SuppressWarnings("unchecked") Resolver<T> resolver = (Resolver<T>) classLoader.loadClass(resolverClassName).getDeclaredConstructor().newInstance();
            return resolver;
        } catch (ClassNotFoundException e) {
            throw new LoaderException("Resolver ClassNotFound: " + resolverClassName, e);
        } catch (InvocationTargetException e) {
            throw new LoaderException("Resolver instantiation failed with exception: " + resolverClassName, e);
        } catch (InstantiationException e) {
            throw new LoaderException("Resolver Class is abstract. This is invalid implementation: " + resolverClassName, e);
        } catch (IllegalAccessException e) {
            throw new LoaderException("Constructor is not accessible due to security constraints: " + resolverClassName, e);
        } catch (NoSuchMethodException e) {
            throw new LoaderException("Resolver does not have a no-argument, public constructor: " + resolverClassName, e);
        }
    }

    /**
     * @deprecated Use {@link #resolveReference(String, Class, ClassLoader)} instead.
     */
    @Deprecated(forRemoval = true)
    public static <T extends ReferenceValue> T resolveReference(String reference, Class<T> type) {
        initialize();
        // THIS IS BAD
        // https://stackoverflow.com/a/36228195/18105226
        return resolveReference(reference, type, Thread.currentThread().getContextClassLoader());
    }

    private Loader() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}
