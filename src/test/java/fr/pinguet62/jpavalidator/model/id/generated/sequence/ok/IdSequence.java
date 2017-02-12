package fr.pinguet62.jpavalidator.model.id.generated.sequence.ok;

import static javax.persistence.GenerationType.SEQUENCE;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "ID_GENERATED_SEQUENCE_OK")
public class IdSequence {

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "foo")
    @SequenceGenerator(name = "foo", sequenceName = "ID_GENERATED_SEQUENCE_OK_SEQUENCE")
    @Column(name = "ok", nullable = false)
    int ok;

}