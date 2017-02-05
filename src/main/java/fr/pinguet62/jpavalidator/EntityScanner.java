package fr.pinguet62.jpavalidator;

import static org.springframework.util.ClassUtils.getDefaultClassLoader;
import static org.springframework.util.ClassUtils.resolveClassName;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javax.persistence.Entity;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

public class EntityScanner implements Supplier<List<Class<?>>> {

    private final String basePackage;

    public EntityScanner(String basePackage) {
        this.basePackage = basePackage;
    }

    @Override
    public List<Class<?>> get() {
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

}