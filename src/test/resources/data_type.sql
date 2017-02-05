create table DATA_TYPE (
	-- Column#nullable()
	MANDATORY int not null,
	OPTIONAL int,
	-- Column#length()
	STRING varchar(42),
	-- Column#precision() + Column#scale()
	DECIMAL numeric(5,2)
);