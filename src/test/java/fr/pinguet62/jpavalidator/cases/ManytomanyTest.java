package fr.pinguet62.jpavalidator.cases;

import static fr.pinguet62.jpavalidator.util.TestUtils.runCheck;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.junit.Test;
import org.junit.runner.RunWith;

import fr.pinguet62.jpavalidator.ValidationException;
import fr.pinguet62.jpavalidator.util.runner.SchemaRunner;
import fr.pinguet62.jpavalidator.util.runner.Script;

@RunWith(SchemaRunner.class)
public class ManytomanyTest {

    @Entity
    @Table(name = "EMPLOYEE")
    public static class Employee {
        @ManyToMany
        @JoinTable(name = "LINK", joinColumns = { @JoinColumn(name = "EMPLOYEE_ID") }, inverseJoinColumns = {
                @JoinColumn(name = "PROJECT_ID") })
        List<Project> projects;
    }

    @Entity
    @Table(name = "PROJECT")
    public static class MappedbyInvalidType {
        @ManyToMany(mappedBy = "projects")
        List<String> employees;
    }

    @Entity
    @Table(name = "PROJECT")
    public static class MappedbyUnknownProperty {
        @ManyToMany(mappedBy = "unknown")
        List<Employee> employees;
    }

    @Entity
    @Table(name = "PROJECT")
    public static class Project {
        @ManyToMany(mappedBy = "projects")
        List<Employee> employees;
    }

    @Test
    @Script({ "create table EMPLOYEE ( ID_EMPLOYEE int /*not null*/ primary key );", //
            "create table PROJECT ( ID_PROJECT int /*not null*/ primary key );", //
            "create table LINK ( \n" + //
                    "    EMPLOYEE_ID int references EMPLOYEE (ID_EMPLOYEE), \n" + //
                    "    PROJECT_ID int references PROJECT (ID_PROJECT) \n" + //
                    ");" })
    public void test_JoinTable() {
        runCheck(Employee.class);
    }

    @Test
    @Script({ "create table EMPLOYEE ( ID_EMPLOYEE int /*not null*/ primary key );", //
            "create table PROJECT ( ID_PROJECT int /*not null*/ primary key );", //
            "create table LINK ( \n" + //
                    "    EMPLOYEE_ID int /*references EMPLOYEE (ID_EMPLOYEE)*/, \n" + //
                    "    PROJECT_ID int references PROJECT (ID_PROJECT) \n" + //
                    ");" })
    public void test_JoinTable_noFk1() {
        try {
            runCheck(Employee.class);
            fail();
        } catch (ValidationException e) {
            assertEquals(1, e.getErrors().size());
        }
    }

    @Test
    @Script({ "create table EMPLOYEE ( ID_EMPLOYEE int /*not null*/ primary key );", //
            "create table PROJECT ( ID_PROJECT int /*not null*/ primary key );", //
            "create table LINK ( \n" + //
                    "    EMPLOYEE_ID int references EMPLOYEE (ID_EMPLOYEE), \n" + //
                    "    PROJECT_ID int /*references PROJECT (ID_PROJECT)*/ \n" + //
                    ");" })
    public void test_JoinTable_noFk2() {
        try {
            runCheck(Employee.class);
            fail();
        } catch (ValidationException e) {
            assertEquals(1, e.getErrors().size());
        }
    }

    @Test
    @Script({ "create table EMPLOYEE ( ID_EMPLOYEE int /*not null*/ primary key );", //
            "create table PROJECT ( ID_PROJECT int /*not null*/ primary key );", //
            "create table LINK ( \n" + //
                    "    EMPLOYEE_ID int references EMPLOYEE (ID_EMPLOYEE), \n" + //
                    "    PROJECT_ID int references PROJECT (ID_PROJECT) \n" + //
                    ");" })
    public void test_MappedBy() {
        runCheck(Project.class);
    }

    @Test
    @Script({ "create table EMPLOYEE ( ID_EMPLOYEE int /*not null*/ primary key );", //
            "create table PROJECT ( ID_PROJECT int /*not null*/ primary key );", //
            "create table LINK ( \n" + //
                    "    EMPLOYEE_ID int references EMPLOYEE (ID_EMPLOYEE), \n" + //
                    "    PROJECT_ID int references PROJECT (ID_PROJECT) \n" + //
                    ");" })
    public void test_MappedBy_invalidType() {
        try {
            runCheck(MappedbyInvalidType.class);
            fail();
        } catch (ValidationException e) {
            assertEquals(1, e.getErrors().size());
        }
    }

    @Test
    @Script({ "create table EMPLOYEE ( ID_EMPLOYEE int /*not null*/ primary key );", //
            "create table PROJECT ( ID_PROJECT int /*not null*/ primary key );", //
            "create table LINK ( \n" + //
                    "    EMPLOYEE_ID int references EMPLOYEE (ID_EMPLOYEE), \n" + //
                    "    PROJECT_ID int references PROJECT (ID_PROJECT) \n" + //
                    ");" })
    public void test_MappedBy_unknownProperty() {
        try {
            runCheck(MappedbyUnknownProperty.class);
            fail();
        } catch (ValidationException e) {
            assertEquals(1, e.getErrors().size());
        }
    }

}
