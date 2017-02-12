package fr.pinguet62.jpavalidator.model.id.simple.ok;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ID_SIMPLE")
public class IdSample {

    @Id
    @Column(name = "ok", nullable = false)
    int ok;

}