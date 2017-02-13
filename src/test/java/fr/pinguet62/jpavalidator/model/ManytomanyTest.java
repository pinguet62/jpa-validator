package fr.pinguet62.jpavalidator.model;

import static fr.pinguet62.jpavalidator.util.TestUtils.runCheck;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.junit.Test;
import org.junit.runner.RunWith;

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
    public static class Project {
        @ManyToMany(mappedBy = "projects")
        List<Employee> employees;
    }

    @Test
    @Script({ "create table EMPLOYEE ( ID_EMPLOYEE int /*not null*/ primary key );",
            "create table PROJECT ( ID_PROJECT int /*not null*/ primary key );",
            "create table LINK ( \n" + //
                    "    EMPLOYEE_ID int references EMPLOYEE (ID_EMPLOYEE), \n" + //
                    "    PROJECT_ID int references PROJECT (ID_PROJECT) \n" + //
                    ");" })
    public void test() {
        runCheck(Employee.class, Project.class);
    }

}
