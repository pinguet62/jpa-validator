create table ONETOONE_ADDRESS (
	id_address int primary key,
	person_id int --references ONETOONE_PERSON (id_per)
);

create table ONETOONE_PERSON (
	id_person int primary key,
	address_id int references ONETOONE_ADDRESS (id_address)
);

alter table ONETOONE_ADDRESS add foreign key (person_id) references ONETOONE_PERSON (id_person);