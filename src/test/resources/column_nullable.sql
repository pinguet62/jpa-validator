-- @Column#nullable()
create table COLUMN_NULLABLE (
	mandatory_error int not null,
	mandatory_ok int not null,
	optional_error int null,
	optional_ok int null
);