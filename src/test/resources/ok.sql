create table ADDRESS (
	ID_A int primary key, -- Id
	EMPLOYEE_ID int /*references EMPLOYEE(ID_E)*/ -- OneToOne
);

create table EMPLOYEE (
	ID_E int primary key, -- Id
	ADDRESS_ID int references ADDRESS(ID_A) -- OneToOne
	-- OneToMany: see CAR
	-- ManyToMany: see PROJECT
);

alter table ADDRESS add foreign key (EMPLOYEE_ID) references EMPLOYEE (ID_E); -- OneToOne

create table CAR (
	ID_C int primary key, -- Id
	EMPLOYEE_ID int references EMPLOYEE(ID_E) -- ManyToOne
);

create table PROJECT (
	ID_P int primary key -- Id
	-- ManyToMany: see EMPLOYEE
);

-- ManyToMany
create table LINK_EMP_PROJ (
	EMPLOYEE_ID int references EMPLOYEE(ID_E),
	PROJECT_ID int references PROJECT(ID_P)
);
