package fr.pinguet62.jpavalidator.model.ok;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "PROJECT")
public class Project {

    @ManyToMany(mappedBy = "projects")
    private List<Employee> employees;

    @Id
    @Column(name = "ID_P", nullable = false)
    private Integer id;

}