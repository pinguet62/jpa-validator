package fr.pinguet62.jpavalidator;

import static org.springframework.util.ClassUtils.getDefaultClassLoader;
import static org.springframework.util.ClassUtils.resolveClassName;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.TypeFilter;

public class ReflectionUtils {

    public static List<Class<?>> scanClasses(String basePackage, TypeFilter filter) {
        ClassPathScanningCandidateComponentProvider candidateComponentProvider = new ClassPathScanningCandidateComponentProvider(
                false);
        candidateComponentProvider.addIncludeFilter(filter);
        List<Class<?>> classes = new ArrayList<>();
        for (BeanDefinition candidate : candidateComponentProvider.findCandidateComponents(basePackage)) {
            Class<?> cls = resolveClassName(candidate.getBeanClassName(), getDefaultClassLoader());
            classes.add(cls);
        }
        return classes;
    }

}