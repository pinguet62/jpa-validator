package fr.pinguet62.jpavalidator.model.id.generated.sequence.invalid;

import static javax.persistence.GenerationType.SEQUENCE;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ID_GENERATED_SEQUENCE_INVALID")
public class IdSequence {

    @Id
    @GeneratedValue(strategy = SEQUENCE)
    @Column(name = "invalid", nullable = false)
    int invalid;

}