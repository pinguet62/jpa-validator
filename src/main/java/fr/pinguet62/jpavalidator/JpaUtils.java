package fr.pinguet62.jpavalidator;

import static fr.pinguet62.jpavalidator.ReflectionUtils.scanClasses;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

/** Utilities functions for JPA and reflection. */
public class JpaUtils {

    /**
     * List all {@link Field} annotated with {@code javax.persistence} annotations.
     * <p>
     * Ignore {@link Transient}.<br>
     * Recursive iteration into {@link Embedded} elements.<br>
     * Recursive iteration into {@link Class#getSuperclass()}.
     */
    public static List<Field> getAnnotatedFields(Class<?> entity) {
        List<Field> fields = new ArrayList<>();

        for (Field field : entity.getDeclaredFields()) {
            // TODO tmp fix for unit-test inline class
            if (field.toString().matches(".*\\.this\\$[0-9]+$"))
                continue;

            if (field.isAnnotationPresent(Transient.class))
                continue;

            if (field.isAnnotationPresent(Embedded.class))
                fields.addAll(getAnnotatedFields(field.getType()));
            else
                fields.add(field);
        }

        if (entity.getSuperclass() != null) // Object.class.getSuperclass() == null
            fields.addAll(getAnnotatedFields(entity.getSuperclass()));

        return fields;
    }

    /**
     * List all {@link Class} annotated with {@link Entity}.
     *
     * @param basePackage The package to scan.
     * @see ClassPathScanningCandidateComponentProvider
     */
    public static List<Class<?>> getEntities(String basePackage) {
        return scanClasses(basePackage, new AnnotationTypeFilter(Entity.class));
    }

    /**
     * Get {@link Annotation} on {@link Field}, or on {@link Class} of {@link Field} if not present.
     *
     * @return {@code null} if not found, otherwise the {@link Annotation} instance.
     */
    public static <T extends Annotation> T getFieldOrClassAnnotation(Field field, Class<T> annotationType) {
        T annotation = field.getDeclaredAnnotation(annotationType);
        if (annotation != null)
            return annotation;
        return field.getDeclaringClass().getDeclaredAnnotation(annotationType);
    }

    /**
     * Get the first argument {@link Class} of generic type.
     * <p>
     * For example {@code List<Foo>} class, this method will return {@code Foo} class.
     */
    public static Class<?> getFirstArgumentType(Type genericType) {
        return (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
    }

    /** @return {@link Table#name()} */
    public static String getTableName(Class<?> entity) {
        Table table = entity.getDeclaredAnnotation(Table.class);
        if (table != null)
            return table.name();
        else
            return entity.getSimpleName(); // default naming
    }

    /**
     * Get the target {@link Field} of {@code mappedBy} attribute.
     *
     * @param mappedBy The {@code mappedBy} attribute, corresponding to the property of target entity.
     */
    public static Field getTargetField(Class<?> targetEntity, String mappedBy) {
        try {
            return targetEntity.getDeclaredField(mappedBy);
        } catch (NoSuchFieldException | SecurityException e) {
            return null;
        }
    }

}