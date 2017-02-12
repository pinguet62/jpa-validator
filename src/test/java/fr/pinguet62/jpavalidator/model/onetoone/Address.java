package fr.pinguet62.jpavalidator.model.onetoone;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ONETOONE_ADDRESS")
public class Address {

    @OneToOne(mappedBy = "address")
    Person person;

}