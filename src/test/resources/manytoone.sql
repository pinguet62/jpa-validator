create table MANYTOONE_PERSON (
	id_person character varying(99) primary key
);

create table MANYTOONE_BAD (
	id_bad character varying(99) primary key
);

create table MANYTOONE_CAR (
	invalid_target character varying(99) references MANYTOONE_BAD (id_bad),
	invalid_type character varying(42) references MANYTOONE_PERSON (id_person),
	ok character varying(99) references MANYTOONE_PERSON (id_person)
);