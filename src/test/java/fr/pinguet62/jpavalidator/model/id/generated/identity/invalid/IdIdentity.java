package fr.pinguet62.jpavalidator.model.id.generated.identity.invalid;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ID_GENERATED_IDENTITY_INVALID")
public class IdIdentity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "invalid", nullable = false)
    int invalid;

}