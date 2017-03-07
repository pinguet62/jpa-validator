package fr.pinguet62.jpavalidator;

import static fr.pinguet62.jpavalidator.ReflectionUtils.scanClasses;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.core.type.filter.AssignableTypeFilter;

import fr.pinguet62.jpavalidator.exception.ValidationException;
import fr.pinguet62.jpavalidator.validator.AbstractValidator;

public class Processor implements Consumer<List<Class<?>>> {

    private final List<Class<?>> validators = scanClasses(AbstractValidator.class.getPackage().getName(),
            new AssignableTypeFilter(AbstractValidator.class));

    @Override
    public void accept(List<Class<?>> entities) {
        ValidationException validation = new ValidationException();
        for (Class<?> entity : entities)
            for (Field field : JpaUtils.getAnnotatedFields(entity))
                for (Class<?> validatorClass : validators) {
                    AbstractValidator validator = buildValidator(validatorClass, entity);
                    validator.setOnError(message -> validation.getErrors().add(message));
                    if (validator.support(field)) {
                        System.out.println(validator.getClass());
                        validator.validate(field);
                    }
                }
        if (!validation.getErrors().isEmpty())
            throw validation;
    }

    private AbstractValidator buildValidator(Class<?> validatorClass, Class<?> entity) {
        try {
            Constructor<?> constructor = validatorClass.getDeclaredConstructor(Class.class, String.class);
            return (AbstractValidator) constructor.newInstance(entity, JpaUtils.getTableName(entity));
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}