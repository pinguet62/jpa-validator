package fr.pinguet62.jpavalidator.model.manytomany;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "MANYTOMANY_EMPLOYEE")
public class Employee {

    @ManyToMany
    @JoinTable(name = "MANYTOMANY_LINK", joinColumns = { @JoinColumn(name = "EMPLOYEE_ID") }, inverseJoinColumns = {
            @JoinColumn(name = "PROJECT_ID") })
    List<Project> projects;

}