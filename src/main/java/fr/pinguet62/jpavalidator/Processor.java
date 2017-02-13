package fr.pinguet62.jpavalidator;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static javax.persistence.GenerationType.AUTO;
import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.GenerationType.SEQUENCE;
import static javax.persistence.GenerationType.TABLE;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.TableGenerator;

/** {@link Entity} validator. */
public class Processor implements Consumer<List<Class<?>>> {

    // TODO Thread safe
    private final ValidationException validation = new ValidationException();

    private final JdbcMetadataChecker visitor;

    public Processor(JdbcMetadataChecker visitor) {
        this.visitor = visitor;
    }

    @Override
    public void accept(List<Class<?>> entities) {
        for (Class<?> entity : entities) {
            if (!entity.isAnnotationPresent(Entity.class))
                throw new IllegalArgumentException(entity + " must be annotated with " + Entity.class.getSimpleName());

            String tableName = JpaUtils.getTableName(entity);
            doCheck(() -> visitor.checkTable(tableName),
                    () -> validation.getErrors().add(display(entity) + ": error with table " + tableName));

            for (Field field : JpaUtils.getAnnotatedFields(entity)) {
                if (field.isAnnotationPresent(Column.class)) {
                    Column column = field.getDeclaredAnnotation(Column.class);
                    String columnName = column.name();
                    Boolean nullable;
                    if (field.isAnnotationPresent(Id.class) && field.isAnnotationPresent(GeneratedValue.class)) {
                        if (column.nullable() == false) {
                            validation.getErrors()
                                    .add(display(entity, field) + ": @" + Column.class.getSimpleName()
                                            + "(nullable) must be true when used with @" + Id.class.getSimpleName() + " & @"
                                            + GeneratedValue.class.getSimpleName());
                            continue;
                        }
                        nullable = false;
                    } else
                        nullable = column.nullable();
                    doCheck(() -> visitor.checkColumn(tableName, columnName, nullable),
                            () -> validation.getErrors().add(display(entity, field) + ": error with column " + columnName));

                    // Character
                    if (field.getType().equals(String.class)) {
                        int length = (column == null ? 255 : column.length());
                        doCheck(() -> visitor.checkCharacter(tableName, columnName, length), () -> validation.getErrors()
                                .add(display(entity, field) + ": error with column character " + columnName));
                    }
                    // Numeric
                    if (asList(Float.TYPE, Float.class, BigDecimal.class).contains(field.getType()))
                        doCheck(() -> visitor.checkNumeric(tableName, columnName, column.precision(), column.scale()),
                                () -> validation.getErrors()
                                        .add(display(entity, field) + ": error with column numeric " + columnName));

                    // Primary key
                    if (field.isAnnotationPresent(Id.class))
                        checkId(tableName, columnName, entity, field);
                }

                // Foreign key: ManyToOne or OneToOne
                if (field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class))
                    checkManyToOne(tableName, entity, field);

                // OneToMany
                if (field.isAnnotationPresent(OneToMany.class))
                    checkOneToMany(entity, field);

                // ManyToMany
                if (field.isAnnotationPresent(ManyToMany.class)) {
                    ManyToMany manyToMany = field.getDeclaredAnnotation(ManyToMany.class);
                    // JoinTable
                    if (field.isAnnotationPresent(JoinTable.class) && manyToMany.mappedBy().equals(""))
                        checkManyToMany(tableName, entity, field);
                    // mappedBy
                    else if (!manyToMany.mappedBy().equals("") && !field.isAnnotationPresent(JoinTable.class)) {
                        Class<?> targetEntity = JpaUtils.getFirstArgumentType(field.getGenericType());
                        String mappedTableName = JpaUtils.getTableName(targetEntity);
                        Field mappedByField = JpaUtils.getTargetField(targetEntity, manyToMany.mappedBy());
                        checkManyToMany(mappedTableName, entity, mappedByField);
                    } else {
                        validation.getErrors().add("@" + ManyToMany.class.getSimpleName()
                                + " must define, either #mappedBy(), or @" + JoinTable.class.getSimpleName());
                        continue;
                    }
                }
            }
        }

        if (!validation.getErrors().isEmpty())
            throw validation;
    }

    /** @see Id */
    private void checkId(String tableName, String columnName, Class<?> entity, Field field) {
        if (!field.isAnnotationPresent(Id.class))
            throw new IllegalArgumentException(
                    display(entity, field) + ": must be annotated with " + ManyToMany.class.getSimpleName());

        doCheck(() -> visitor.checkPrimaryKey(tableName, columnName), () -> validation.getErrors()
                .add(format(display(entity, field) + ": error with PK %s.%s", tableName, columnName)));

        if (field.isAnnotationPresent(GeneratedValue.class)) {
            GeneratedValue generatedValue = field.getDeclaredAnnotation(GeneratedValue.class);
            // AUTO: JPA implementation
            if (generatedValue.strategy().equals(AUTO)) {
                Collection<Class<? extends Annotation>> unsupportedAnnotations = asList(TableGenerator.class,
                        SequenceGenerator.class);
                if (unsupportedAnnotations.stream().anyMatch(field::isAnnotationPresent)) {
                    validation.getErrors()
                            .add(display(entity, field) + ": " + GeneratedValue.class.getSimpleName() + "(strategy="
                                    + AUTO.name() + ") cannot be used with theses annotations: "
                                    + unsupportedAnnotations.stream().map(Class::getSimpleName).collect(joining(", ")));
                    return;
                }
                throw new NotYetImplemented();
            }
            // IDENTITY: PK database constraint
            else if (generatedValue.strategy().equals(IDENTITY)) {
                Collection<Class<? extends Annotation>> unsupportedAnnotations = asList(TableGenerator.class,
                        SequenceGenerator.class);
                if (unsupportedAnnotations.stream().anyMatch(field::isAnnotationPresent)) {
                    validation.getErrors()
                            .add(display(entity, field) + ": " + GeneratedValue.class.getSimpleName() + "(strategy="
                                    + IDENTITY.name() + ") cannot be used with theses annotations: "
                                    + unsupportedAnnotations.stream().map(Class::getSimpleName).collect(joining(", ")));
                    return;
                }

                doCheck(() -> visitor.checkAutoIncrement(tableName, columnName, true), () -> validation.getErrors().add(
                        format(display(entity, field) + ": error with auto-increment column: %s.%s", tableName, columnName)));
            }
            // TABLE
            else if (generatedValue.strategy().equals(TABLE)) {
                Collection<Class<? extends Annotation>> unsupportedAnnotations = asList(SequenceGenerator.class);
                if (unsupportedAnnotations.stream().anyMatch(field::isAnnotationPresent)) {
                    validation.getErrors()
                            .add(display(entity, field) + ": " + GeneratedValue.class.getSimpleName() + "(strategy="
                                    + TABLE.name() + ") cannot be used with theses annotations: "
                                    + unsupportedAnnotations.stream().map(Class::getSimpleName).collect(joining(", ")));
                    return;
                }

                throw new NotYetImplemented();
            }
            // SEQUENCE
            else if (generatedValue.strategy().equals(SEQUENCE)) {
                Collection<Class<? extends Annotation>> unsupportedAnnotations = asList(TableGenerator.class);
                if (unsupportedAnnotations.stream().anyMatch(field::isAnnotationPresent)) {
                    validation.getErrors()
                            .add(display(entity, field) + ": " + GeneratedValue.class.getSimpleName() + "(strategy="
                                    + SEQUENCE.name() + ") cannot be used with theses annotations: "
                                    + unsupportedAnnotations.stream().map(Class::getSimpleName).collect(joining(", ")));
                    return;
                }

                SequenceGenerator sequenceGenerator = JpaUtils.getOnFieldOrClass(field, SequenceGenerator.class);
                if (sequenceGenerator == null) {
                    validation.getErrors().add(display(entity, field) + ": (field or class) must be annotated with @"
                            + SequenceGenerator.class.getSimpleName());
                    return;
                }

                if (!generatedValue.generator().equals(sequenceGenerator.name())) {
                    validation.getErrors().add(display(entity, field) + ": @" + GeneratedValue.class.getSimpleName()
                            + "(generator) and @" + SequenceGenerator.class.getSimpleName() + "(name) doesn't match");
                    return;
                }

                String sequenceName = sequenceGenerator.sequenceName();
                doCheck(() -> visitor.checkSequence(sequenceName),
                        () -> validation.getErrors().add("Error with sequence: " + sequenceName));

                doCheck(() -> visitor.checkAutoIncrement(tableName, columnName, true), () -> validation.getErrors()
                        .add(format("Error with auto-increment column: %s.%s", tableName, columnName)));
            }
            // ???
            else
                throw new RuntimeException("Unknown " + GenerationType.class.getName() + " enum value: " + generatedValue);
        } else {
            Collection<Class<? extends Annotation>> unsupportedAnnotations = asList(TableGenerator.class,
                    SequenceGenerator.class);
            if (unsupportedAnnotations.stream().anyMatch(field::isAnnotationPresent)) {
                validation.getErrors()
                        .add(display(entity, field) + ": " + GeneratedValue.class.getSimpleName()
                                + " must be used to use theses annotations: "
                                + unsupportedAnnotations.stream().map(Class::getSimpleName).collect(joining(", ")));
                return;
            }

            // doCheck(() -> visitor.checkAutoIncrement(tableName, columnName, false),
            // () -> validation.getErrors().add(format("Error with auto-increment column: %s.%s", tableName,
            // columnName)));
        }
    }

    /** @see ManyToMany */
    private void checkManyToMany(String tableName, Class<?> entity, Field field) {
        if (!field.isAnnotationPresent(ManyToMany.class))
            throw new IllegalArgumentException(
                    display(entity, field) + ": must be annotated with " + ManyToMany.class.getSimpleName());

        JoinTable joinTable = field.getDeclaredAnnotation(JoinTable.class);
        String linkTableName = joinTable.name();
        {
            // Direct
            if (joinTable.joinColumns().length != 1)
                throw new NotYetImplemented();
            JoinColumn joinColumn = joinTable.joinColumns()[0];
            doCheck(() -> visitor.checkForeignKey(linkTableName, joinColumn.name(), tableName),
                    () -> validation.getErrors().add(format(display(entity, field) + ": error with FK from %s.%s to %s",
                            linkTableName, joinColumn.name(), tableName)));
        }
        {
            // Reverse
            if (joinTable.inverseJoinColumns().length != 1)
                throw new NotYetImplemented();
            JoinColumn joinColumn = joinTable.inverseJoinColumns()[0];
            Class<?> targetEntity = JpaUtils.getFirstArgumentType(field.getGenericType());
            String targetTableName = JpaUtils.getTableName(targetEntity);
            doCheck(() -> visitor.checkForeignKey(linkTableName, joinColumn.name(), targetTableName),
                    () -> validation.getErrors().add(format(display(entity, field) + ": error with FK from %s.%s to %s",
                            linkTableName, joinColumn.name(), targetTableName)));
        }
    }

    /** @see ManyToOne */
    private void checkManyToOne(String tableName, Class<?> entity, Field field) {
        JoinColumn joinColumn = field.getDeclaredAnnotation(JoinColumn.class);
        if (joinColumn != null) {
            String tgtTableName = JpaUtils.getTableName(field.getType());
            String columnName = joinColumn.name();
            doCheck(() -> visitor.checkForeignKey(tableName, columnName, tgtTableName), () -> validation.getErrors().add(
                    format(display(entity, field) + ": error with FK from %s.%s to %s", tableName, columnName, tgtTableName)));
        }
    }

    /**
     * Get target {@link OneToMany#mappedBy()} {@link Field} and {@link #checkManyToOne(String, Field)}.
     *
     * @see OneToMany
     */
    private void checkOneToMany(Class<?> entity, Field field) {
        if (!field.isAnnotationPresent(OneToMany.class))
            throw new IllegalArgumentException(
                    display(entity, field) + ": must be annotated with " + OneToMany.class.getSimpleName());

        if (field.isAnnotationPresent(JoinTable.class))
            throw new NotYetImplemented();

        OneToMany oneToMany = field.getDeclaredAnnotation(OneToMany.class);
        Class<?> targetEntity = JpaUtils.getFirstArgumentType(field.getGenericType());
        // Target entity
        Field mappedByField = JpaUtils.getTargetField(targetEntity, oneToMany.mappedBy());
        Class<?> mappedByFieldType = mappedByField.getType();
        if (!mappedByFieldType.equals(field.getDeclaringClass())) {
            validation.getErrors().add(display(entity, field) + ": is \"mappedBy\" to target field of different type");
            return;
        }

        String mappedTableName = JpaUtils.getTableName(targetEntity);
        checkManyToOne(mappedTableName, entity, mappedByField);
    }

    private String display(Class<?> entity) {
        return entity.getSimpleName();
    }

    private String display(Class<?> entity, Field field) {
        return entity.getSimpleName() + "." + field.getName();
    }

    private void doCheck(Supplier<Boolean> fct, Runnable errorFct) {
        boolean success = fct.get();
        if (!success)
            errorFct.run();
    }

}