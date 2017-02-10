package fr.pinguet62.jpavalidator.model.manytomany;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "MANYTOMANY_PROJECT")
public class Project {

    @ManyToMany(mappedBy = "projects")
    private List<Employee> employees;

}