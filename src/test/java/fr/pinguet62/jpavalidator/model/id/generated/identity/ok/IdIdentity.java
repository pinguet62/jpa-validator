package fr.pinguet62.jpavalidator.model.id.generated.identity.ok;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ID_GENERATED_IDENTITY_OK")
public class IdIdentity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ok", nullable = false)
    int ok;

}