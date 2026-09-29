package generators;

import com.mifmif.common.regex.Generex;
import models.BaseModel;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomModelGenerator {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int DEFAULT_STRING_LENGTH = 10;
    private static final int DEFAULT_COLLECTION_SIZE = 1;

    public static <T extends BaseModel> T generate(Class<T> modelClass) {
        T model = instantiate(modelClass);
        for (Field field : collectFields(modelClass)) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            try {
                field.set(model, generateFieldValue(field));
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Cannot set field " + field.getName(), e);
            }
        }
        return model;
    }

    private static Object generateFieldValue(Field field) {
        GeneratorRule rule = field.getAnnotation(GeneratorRule.class);
        if (rule != null && field.getType() == String.class) {
            return generateByRegex(rule.regex());
        }
        return generateValue(field.getType(), field.getGenericType());
    }

    private static Object generateValue(Class<?> type, Type genericType) {
        if (type == String.class) {
            return randomString(DEFAULT_STRING_LENGTH);
        }
        if (type == int.class || type == Integer.class) {
            return ThreadLocalRandom.current().nextInt(1, 1000);
        }
        if (type == long.class || type == Long.class) {
            return ThreadLocalRandom.current().nextLong(1, 1000);
        }
        if (type == double.class || type == Double.class) {
            return Math.round(ThreadLocalRandom.current().nextDouble(1, 1000) * 100.0) / 100.0;
        }
        if (type == float.class || type == Float.class) {
            return (float) (Math.round(ThreadLocalRandom.current().nextDouble(1, 1000) * 100.0) / 100.0);
        }
        if (type == boolean.class || type == Boolean.class) {
            return ThreadLocalRandom.current().nextBoolean();
        }
        if (type.isEnum()) {
            Object[] constants = type.getEnumConstants();
            return constants[ThreadLocalRandom.current().nextInt(constants.length)];
        }
        if (BaseModel.class.isAssignableFrom(type)) {
            return generate(type.asSubclass(BaseModel.class));
        }
        if (Collection.class.isAssignableFrom(type)) {
            return generateCollection(genericType);
        }
        return null;
    }

    private static Object generateCollection(Type genericType) {
        if (!(genericType instanceof ParameterizedType parameterized)) {
            return new ArrayList<>();
        }
        Type argument = parameterized.getActualTypeArguments()[0];
        if (!(argument instanceof Class<?> elementType)) {
            return new ArrayList<>();
        }
        List<Object> values = new ArrayList<>();
        for (int i = 0; i < DEFAULT_COLLECTION_SIZE; i++) {
            values.add(generateValue(elementType, elementType));
        }
        return values;
    }

    private static String generateByRegex(String regex) {
        String pattern = regex.replaceAll("^\\^", "").replaceAll("\\$$", "");
        return new Generex(pattern).random();
    }

    private static String randomString(int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(ALPHABET.charAt(ThreadLocalRandom.current().nextInt(ALPHABET.length())));
        }
        return builder.toString();
    }

    private static List<Field> collectFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> current = type; current != null && current != Object.class; current = current.getSuperclass()) {
            fields.addAll(List.of(current.getDeclaredFields()));
        }
        return fields;
    }

    private static <T> T instantiate(Class<T> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("No accessible no-arg constructor in " + type.getName(), e);
        }
    }
}
