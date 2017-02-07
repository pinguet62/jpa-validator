package fr.pinguet62.jpavalidator.model.ok;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "EMPLOYEE")
public class Employee {

    @OneToOne
    @JoinColumn(name = "ADDRESS_ID")
    private Address address;

    @OneToMany(mappedBy = "employee")
    private List<Car> cars;

    @Id
    @Column(name = "ID_E", nullable = false)
    private Integer id;

    @ManyToMany
    @JoinTable(name = "LINK_EMP_PROJ", joinColumns = { @JoinColumn(name = "EMPLOYEE_ID") }, inverseJoinColumns = {
            @JoinColumn(name = "PROJECT_ID") })
    private List<Project> projects;

}