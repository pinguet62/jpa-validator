create table ID_GENERATED_IDENTITY_INVALID (
	invalid int /* not null*/ primary key
);

create table ID_GENERATED_IDENTITY_OK (
	ok int /* not null*/ generated by default as identity primary key
);