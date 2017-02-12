package fr.pinguet62.jpavalidator.model.onetomany.badReverseTargetMappedbyField;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "ONETOMANY_PERSON")
public class Person {

    @OneToMany(mappedBy = "person")
    List<Car> cars;

}