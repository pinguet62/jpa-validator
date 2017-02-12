package fr.pinguet62.jpavalidator;

import static org.springframework.util.ClassUtils.getDefaultClassLoader;
import static org.springframework.util.ClassUtils.resolveClassName;

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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

/** Utilities functions for JPA and reflection. */
public class JpaUtils {

    /**
     * List all {@link Field} annotated with {@code javax.persistence} annotations.
     * <p>
     * Ignore {@link Transient}.<br>
     * Iterate on {@link Embedded} elements.
     */
    public static List<Field> getAnnotatedFields(Class<?> entity) {
        List<Field> fields = new ArrayList<>();
        for (Field field : entity.getDeclaredFields()) {
            if (field.isAnnotationPresent(Transient.class))
                continue;
            if (field.isAnnotationPresent(Embedded.class))
                fields.addAll(getAnnotatedFields(field.getType()));
            else
                fields.add(field);
        }
        return fields;
    }

    /**
     * List all {@link Class} annotated with {@link Entity}.
     *
     * @param basePackage The package to scan.
     * @see ClassPathScanningCandidateComponentProvider
     */
    public static List<Class<?>> getEntities(String basePackage) {
        ClassPathScanningCandidateComponentProvider candidateComponentProvider = new ClassPathScanningCandidateComponentProvider(
                false);
        candidateComponentProvider.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
        List<Class<?>> classes = new ArrayList<>();
        for (BeanDefinition candidate : candidateComponentProvider.findCandidateComponents(basePackage)) {
            Class<?> cls = resolveClassName(candidate.getBeanClassName(), getDefaultClassLoader());
            classes.add(cls);
        }
        return classes;
    }

    /**
     * Get the first argument {@link Class} of generic type.
     * <p>
     * For example {@code List<Foo>} class, this method will return {@code Foo} class.
     */
    public static Class<?> getFirstArgumentType(Type genericType) {
        return (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
    }

    /**
     * Get {@link Annotation} on {@link Field}, or on {@link Class} of {@link Field} if not present.
     *
     * @return {@code null} if not found, otherwise the {@link Annotation} instance.
     */
    public static <T extends Annotation> T getOnFieldOrClass(Field field, Class<T> annotationType) {
        T annotation = field.getDeclaredAnnotation(annotationType);
        if (annotation != null)
            return annotation;
        return field.getDeclaringClass().getDeclaredAnnotation(annotationType);
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
            throw new RuntimeException(e);
        }
    }

}