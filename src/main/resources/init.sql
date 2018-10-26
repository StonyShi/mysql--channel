

show master status;


SHOW TABLES;

show databases;
show processlist ;

show warnings;

show errors;

show columns from config.app;

select `TABLE_CATALOG`, `TABLE_SCHEMA`, `TABLE_NAME`, `COLUMN_NAME`, `ORDINAL_POSITION`, `COLUMN_DEFAULT`, `IS_NULLABLE`, `DATA_TYPE`, `CHARACTER_MAXIMUM_LENGTH`, `CHARACTER_OCTET_LENGTH`, `NUMERIC_PRECISION`, `NUMERIC_SCALE`, `DATETIME_PRECISION`, `CHARACTER_SET_NAME`, `COLLATION_NAME`, `COLUMN_TYPE`, `COLUMN_KEY`, `EXTRA`, `PRIVILEGES`, `COLUMN_COMMENT`
 from information_schema.columns
where table_schema = 'test'
and table_name = 'test' ;





show status like 'slave%';
show slave status;



SHOW MASTER status;

SHOW BINARY LOGS;

SHOW MASTER LOGS;

show global variables like 'gtid_purged';
show global variables like 'binlog_checksum';

select * from performance_schema.events_statements_current;

SHOW SLAVE STATUS;


show global variables like 'binlog_checksum';
show global variables like 'binlog_%';

show global variables like '%version%';

set @master_binlog_checksum= @@global.binlog_checksum;


show binlog events limit 10;

SHOW BINLOG EVENTS FROM 1021 limit 10;

SHOW SLAVE STATUS;

show variables like '%version%';

show variables like '%log_bin%';

show variables like '%binlog%';

show variables like '%datadir%';

-- set global expire_logs_days=7;
select @@expire_logs_days;


SELECT now(),time(now());
SELECT now(),second(now());
SELECT now(),extract(SECOND FROM now());
select UNIX_TIMESTAMP();



show status like 'slave%';




select `TABLE_SCHEMA`, `TABLE_NAME`, `COLUMN_NAME`,  `COLUMN_DEFAULT`, `IS_NULLABLE`, `DATA_TYPE`,  `NUMERIC_PRECISION`, `NUMERIC_SCALE`, `CHARACTER_SET_NAME`, `COLUMN_TYPE`, `COLUMN_KEY`,`COLUMN_COMMENT`
 from information_schema.columns
where table_schema = 'test'
and table_name = 't3' ;



SELECT CHARACTER_SET_NAME, COLLATION_NAME, ID
       FROM INFORMATION_SCHEMA.COLLATIONS
       WHERE IS_DEFAULT = 'yes'
       ORDER BY ID;







