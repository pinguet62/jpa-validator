create table MANYTOMANY_EMPLOYEE (
	id_employee int /*not null*/ primary key
);

create table MANYTOMANY_PROJECT (
	id_project int /*not null*/ primary key
);

create table MANYTOMANY_LINK (
	employee_id int references MANYTOMANY_EMPLOYEE (id_employee),
	project_id int references MANYTOMANY_PROJECT (id_project)
);
