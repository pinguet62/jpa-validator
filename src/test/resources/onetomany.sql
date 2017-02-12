create table ONETOMANY_PERSON (
	id_person int /*not null*/ primary key
);

create table ONETOMANY_CAR (
	person_id int references ONETOMANY_PERSON (id_person)
);