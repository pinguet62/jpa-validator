-- @Column#nullable()
create table COLUMN_NULLABLE (
	mandatory_error int null,
	mandatory_ok int not null,
	optional_error int not null
	optional_ok int null
);