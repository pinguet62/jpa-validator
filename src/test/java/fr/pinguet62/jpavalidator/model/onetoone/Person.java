package fr.pinguet62.jpavalidator.model.onetoone;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ONETOONE_PERSON")
public class Person {

    @OneToOne
    @JoinColumn(name = "address_id")
    Address address;

}