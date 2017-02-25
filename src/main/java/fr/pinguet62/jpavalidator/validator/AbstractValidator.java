package fr.pinguet62.jpavalidator.validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.persistence.Entity;

import fr.pinguet62.jpavalidator.NotYetImplemented;

public abstract class AbstractValidator {

    protected final Class<?> entity;

    private Consumer<String> errorHandler;

    private Field field;

    protected final String tableName;

    protected AbstractValidator(Class<?> entity, String tableName) {
        this.entity = entity;
        this.tableName = tableName;
    }

    private boolean checkNotYetSupportedAnnotations(Field field) {
        for (Class<? extends Annotation> annotation : getNotYetSupportedAnnotations())
            if (field.isAnnotationPresent(annotation))
                throw new NotYetImplemented(field.toString() + ": @" + annotation.getSimpleName());
        return true;
    }

    private boolean checkSupportedAnnotations(Field field) {
        for (Annotation annotation : field.getDeclaredAnnotations()) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            if (annotationType.getPackage().equals(Entity.class.getPackage()))
                if (!getSupportedAnnotations().contains(annotationType)) {
                    throwError("unsupported annotation: @" + annotation.annotationType().getSimpleName());
                    return false;
                }
        }
        return true;
    }

    protected void doCheck(Supplier<Boolean> fct, String message) {
        if (fct.get() == false)
            throwError(message);
    }

    // TODO abstract
    protected abstract boolean doProcess(Field field);

    /**
     * {@link Annotation}s who are authorized but not yet supported.<br>
     * This method will be removed after full implementation of JPA annotations.
     */
    public Collection<Class<? extends Annotation>> getNotYetSupportedAnnotations() {
        return new ArrayList<>();
    }

    /**
     * Authorized {@link Annotation}s for the current {@link Field}.<br>
     * Only {@code javax.persistence.XXX} annotations are checked supported.
     */
    public abstract Collection<Class<? extends Annotation>> getSupportedAnnotations();

    public boolean process(Field field) {
        return doProcess(field);
    }

    public void setOnError(Consumer<String> errorHandler) {
        this.errorHandler = errorHandler;
    }

    /** Test if the {@link Field} can be processed by this {@link AbstractValidator}. */
    public abstract boolean support(Field field);

    protected void throwError(String details) {
        errorHandler.accept(entity.getSimpleName() + "(" + tableName + ") > " + field.getName() + ": " + details);
    }

    /**
     * <ol>
     * <li>{@link #checkSupportedAnnotations(Field)}</li>
     * </ol>
     */
    public boolean validate(Field field) {
        this.field = field;
        return checkSupportedAnnotations(field) && checkNotYetSupportedAnnotations(field) && process(field);
    }

}