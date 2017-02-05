package fr.pinguet62.jpavalidator;

import static java.util.Arrays.asList;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import fr.pinguet62.jpavalidator.checker.Checker;
import fr.pinguet62.jpavalidator.checker.Checker.NotImplementedException;

/**
 * @todo Support for composite {@link IdClass}
 * @todo Support for multi {@link JoinTable#joinColumns()} & {@link JoinTable#inverseJoinColumns()}
 */
public class Processor implements Consumer<List<Class<?>>> {

    /**
     * Get the first argument {@link Class} of generic type.<br>
     * For example {@code List<Foo>} class, this method will return {@code Foo} class.
     */
    private static Class<?> getFirstArgumentType(Type genericType) {
        return (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
    }

    private final Checker visitor;

    public Processor(Checker visitor) {
        this.visitor = visitor;
    }

    @Override
    public void accept(List<Class<?>> entities) {
        for (Class<?> entity : entities) {
            String tableName = getTableName(entity);
            doCheck(() -> visitor.checkTable(tableName));

            for (Field field : entity.getDeclaredFields()) {
                // Not mapped
                if (field.isAnnotationPresent(Transient.class))
                    continue;

                Column column = field.getDeclaredAnnotation(Column.class);
                if (column != null) {
                    String columnName = column.name();
                    doCheck(() -> visitor.checkColumn(tableName, columnName, column.nullable()));

                    // Character
                    if (field.getType().equals(String.class)) {
                        int length = (column == null ? 255 : column.length());
                        doCheck(() -> visitor.checkCharacter(tableName, columnName, length));
                    }
                    // Numeric
                    if (asList(Float.class, Long.class, BigDecimal.class).contains(field.getType()))
                        doCheck(() -> visitor.checkNumeric(tableName, columnName, column.precision(), column.scale()));

                    // Primary key
                    Id id = field.getDeclaredAnnotation(Id.class);
                    if (id != null)
                        doCheck(() -> visitor.checkId(tableName, columnName));
                }

                // Foreign key: ManyToOne or OneToOne
                if (field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class))
                    checkManyToOne(tableName, field);

                // OneToMany
                if (field.isAnnotationPresent(OneToMany.class)) {
                    OneToMany oneToMany = field.getDeclaredAnnotation(OneToMany.class);
                    Class<?> targetEntity = getFirstArgumentType(field.getGenericType());
                    String mappedTableName = getTableName(targetEntity);
                    Field mappedField;
                    try {
                        mappedField = targetEntity.getDeclaredField(oneToMany.mappedBy());
                    } catch (NoSuchFieldException | SecurityException e) {
                        throw new RuntimeException(e);
                    }
                    checkManyToOne(mappedTableName, mappedField);
                }

                // ManyToMany
                if (field.isAnnotationPresent(ManyToMany.class)) {
                    ManyToMany manyToMany = field.getDeclaredAnnotation(ManyToMany.class);
                    // JoinTable
                    if (field.isAnnotationPresent(JoinTable.class) && manyToMany.mappedBy().equals(""))
                        checkManyToMany(tableName, field);
                    // mappedBy
                    else if (!manyToMany.mappedBy().equals("") && !field.isAnnotationPresent(JoinTable.class)) {
                        Class<?> targetEntity = getFirstArgumentType(field.getGenericType());
                        String mappedTableName = getTableName(targetEntity);
                        Field mappedField;
                        try {
                            mappedField = targetEntity.getDeclaredField(manyToMany.mappedBy());
                        } catch (NoSuchFieldException | SecurityException e) {
                            throw new RuntimeException(e);
                        }
                        checkManyToMany(mappedTableName, mappedField);
                    } else
                        throw new RuntimeException("@" + ManyToMany.class.getSimpleName()
                                + " must define, either #mappedBy(), or @" + JoinTable.class.getSimpleName());
                }
            }
        }
    }

    /** @see ManyToMany */
    private void checkManyToMany(String tableName, Field field) {
        JoinTable joinTable = field.getDeclaredAnnotation(JoinTable.class);
        String linkTableName = joinTable.name();
        {
            // Direct
            JoinColumn joinColumn = joinTable.joinColumns()[0];
            doCheck(() -> visitor.checkForeignKey(linkTableName, joinColumn.name(), tableName));
        }
        {
            // Reverse
            JoinColumn joinColumn = joinTable.inverseJoinColumns()[0];
            Class<?> targetEntity = getFirstArgumentType(field.getGenericType());
            String targetTableName = getTableName(targetEntity);
            doCheck(() -> visitor.checkForeignKey(linkTableName, joinColumn.name(), targetTableName));
        }
    }

    /** @see ManyToOne */
    private void checkManyToOne(String tableName, Field field) {
        JoinColumn joinColumn = field.getDeclaredAnnotation(JoinColumn.class);
        if (joinColumn != null) {
            String tgtTableName = getTableName(field.getType());
            String columnName = joinColumn.name();
            doCheck(() -> visitor.checkForeignKey(tableName, columnName, tgtTableName));
        }
    }

    private void doCheck(Supplier<Boolean> fct) {
        // default
        try {
            boolean success = fct.get();
            if (!success)
                System.err.println("Invalid");
        } catch (NotImplementedException e) {}
    }

    private String getTableName(Class<?> entity) {
        Table table = entity.getDeclaredAnnotation(Table.class);
        if (table != null)
            return table.name();
        else
            return entity.getSimpleName();
    }

}