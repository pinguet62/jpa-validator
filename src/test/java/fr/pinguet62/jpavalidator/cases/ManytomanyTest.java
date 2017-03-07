package fr.pinguet62.jpavalidator.cases;

import static fr.pinguet62.jpavalidator.util.TestUtils.assertErrorWithColumn;
import static fr.pinguet62.jpavalidator.util.TestUtils.runCheck;
import static org.junit.Assert.fail;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.junit.Test;
import org.junit.runner.RunWith;

import fr.pinguet62.jpavalidator.exception.ValidationException;
import fr.pinguet62.jpavalidator.util.runner.SchemaRunner;
import fr.pinguet62.jpavalidator.util.runner.Script;

@RunWith(SchemaRunner.class)
public class ManytomanyTest {

    @Entity
    @Table(name = "EMPLOYEE")
    static class Employee {
        @ManyToMany
        @JoinTable(name = "LINK", joinColumns = { @JoinColumn(name = "EMPLOYEE_ID") }, inverseJoinColumns = {
                @JoinColumn(name = "PROJECT_ID") })
        List<Project> projects;
    }

    @Entity
    @Table(name = "PROJECT")
    static class Project {
        @ManyToMany(mappedBy = "projects")
        List<Employee> employees;
    }

    @Test
    @Script({ "create table EMPLOYEE ( ID_EMPLOYEE int primary key );", //
            "create table PROJECT ( ID_PROJECT int primary key );", //
            "create table LINK ( \n" + //
                    "    EMPLOYEE_ID int references EMPLOYEE (ID_EMPLOYEE), \n" + //
                    "    PROJECT_ID int references PROJECT (ID_PROJECT) \n" + //
                    ");" })
    public void test_JoinTable() {
        runCheck(Employee.class);
    }

    @Test
    @Script({ "create table EMPLOYEE ( ID_EMPLOYEE int primary key );", //
            "create table PROJECT ( ID_PROJECT int primary key );", //
            "create table LINK ( \n" + //
                    "    EMPLOYEE_ID int, \n" + //
                    "    PROJECT_ID int references PROJECT (ID_PROJECT) \n" + //
                    ");" })
    public void test_JoinTable_fk1_invalid() {
        try {
            runCheck(Employee.class);
            fail();
        } catch (ValidationException e) {
            assertErrorWithColumn(e, Employee.class, "LINK", "EMPLOYEE_ID");
        }
    }

    @Test
    @Script({ "create table EMPLOYEE ( ID_EMPLOYEE int primary key );", //
            "create table PROJECT ( ID_PROJECT int primary key );", //
            "create table LINK ( \n" + //
                    "    EMPLOYEE_ID int references EMPLOYEE (ID_EMPLOYEE), \n" + //
                    "    PROJECT_ID int \n" + //
                    ");" })
    public void test_JoinTable_fk2_invalid() {
        try {
            runCheck(Employee.class);
            fail();
        } catch (ValidationException e) {
            assertErrorWithColumn(e, Employee.class, "LINK", "PROJECT_ID");
        }
    }

    @Test
    @Script({ "create table EMPLOYEE ( ID_EMPLOYEE int primary key );", //
            "create table PROJECT ( ID_PROJECT int primary key );", //
            "create table LINK ( \n" + //
                    "    EMPLOYEE_ID int references EMPLOYEE (ID_EMPLOYEE), \n" + //
                    "    PROJECT_ID int references PROJECT (ID_PROJECT) \n" + //
                    ");" })
    public void test_mappedBy() {
        runCheck(Project.class);
    }

    @Test
    @Script({ "create table EMPLOYEE ( ID_EMPLOYEE int primary key );", //
            "create table PROJECT ( ID_PROJECT int primary key );", //
            "create table LINK ( \n" + //
                    "    EMPLOYEE_ID int references EMPLOYEE (ID_EMPLOYEE), \n" + //
                    "    PROJECT_ID int references PROJECT (ID_PROJECT) \n" + //
                    ");" })
    public void test_mappedBy_propertyType_invalid() {
        @Entity
        @Table(name = "PROJECT")
        class InvalidMappedbyType {
            @ManyToMany(mappedBy = "projects")
            List<String> employees;
        }

        try {
            runCheck(InvalidMappedbyType.class);
            fail();
        } catch (ValidationException e) {
            assertErrorWithColumn(e, InvalidMappedbyType.class, "mappedBy");
            assertErrorWithColumn(e, InvalidMappedbyType.class, "projects");
        }
    }

    @Test
    @Script({ "create table EMPLOYEE ( ID_EMPLOYEE int primary key );", //
            "create table PROJECT ( ID_PROJECT int primary key );", //
            "create table LINK ( \n" + //
                    "    EMPLOYEE_ID int references EMPLOYEE (ID_EMPLOYEE), \n" + //
                    "    PROJECT_ID int references PROJECT (ID_PROJECT) \n" + //
                    ");" })
    public void test_mappedBy_propertyUnknown() {
        @Entity
        @Table(name = "PROJECT")
        class UnknownMappedbyProperty {
            @ManyToMany(mappedBy = "unknown")
            List<Employee> employees;
        }

        try {
            runCheck(UnknownMappedbyProperty.class);
            fail();
        } catch (ValidationException e) {
            assertErrorWithColumn(e, UnknownMappedbyProperty.class, "unknown");
        }
    }

}
