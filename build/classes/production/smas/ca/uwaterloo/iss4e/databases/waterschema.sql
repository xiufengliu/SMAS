drop table IF EXISTS smas_water_hourlyreading cascade;
create table smas_water_hourlyreading(
 typeid    integer,
 custid    integer,
 meterid   integer,
 readtime  timestamp,
 reading   float8
);

create index idx_smas_water_hourlyreading_custid on  smas_water_hourlyreading using btree (custid);
create index idx_smas_water_hourlyreading_typeid on  smas_water_hourlyreading using btree (typeid);

drop table IF EXISTS smas_water_dailyreading cascade;
create table smas_water_dailyreading(
 typeid    integer,
 custid    integer,
 meterid   integer,
 readdate  date,
 reading   float8
);

create index idx_smas_water_dailyreading_typeid on smas_water_dailyreading using btree (typeid);
create index idx_smas_water_dailyreading_custid on smas_water_dailyreading using btree (custid);



----------------------- Start: PARX MODEL ----------
DROP TABLE IF EXISTS smas_water_dailyreadingbytype_parx;
CREATE TABLE smas_water_dailyreadingbytype_parx
(
  typename character varying(16),
  readdate date,
  reading double precision,
  predict double precision,
  readingwithprecip double precision,
  readingwithtemp double precision,
  baseload double precision,
  maxtemp double precision,
  precipmm double precision
)
WITH (
  OIDS=FALSE
);
ALTER TABLE smas_water_dailyreadingbytype_parx
  OWNER TO xiliu;

------------------------------------
CREATE OR REPLACE FUNCTION smas_water_dailyreadingbytype_parx(type_name varchar(10), weekday integer)
  RETURNS void AS
$BODY$
BEGIN
-- if weekday = 0, it will compute the weekends (SAT, SUN) and all holidays;
-- Otherwis, it is weekday from Monday -- Friday (1 - 5)
	drop table if exists smas_water_parx_temp;

	IF weekday=0 THEN
		create table smas_water_parx_temp AS
		select * from
		(select typename,
			readdate,
			maxtemp,
			precipmm,
			reading as y,
			lead(reading, 1)over() as y1,
			lead(reading, 2)over() as y2,
			(CASE WHEN maxtemp>16 THEN maxtemp-16 ELSE 0 END) as temp,
			(CASE WHEN precipmm>1.0 THEN 1.0        ELSE precipmm END) as precip
			from (select
				typename,
				readdate,
				maxtemp,
				precipmm,
				sum(reading) as reading
				from smas_water_dailyreadingbytype, smas_water_type
				where smas_water_dailyreadingbytype.subtypeid=smas_water_type.subtypeid and
				      typename=type_name AND
				      holidayflag=1
				group by 1,2,3,4 order by 2) A
		) B where y  is not null and
			  y1 is not null AND
			  y2 is not null ;
	ELSE
		create table smas_water_parx_temp AS
		select * from
		(select typename,
			readdate,
			maxtemp,
			precipmm,
			reading as y,
			lead(reading, 1)over() as y1,
			lead(reading, 2)over() as y2,
			(CASE WHEN maxtemp>16 THEN maxtemp-16 ELSE 0 END) as temp,
			(CASE WHEN precipmm>1.0 THEN 1.0        ELSE precipmm END) as precip
			from (select
				typename,
				readdate,
				maxtemp,
				precipmm,
				sum(reading) as reading
				from smas_water_dailyreadingbytype, smas_water_type
				where smas_water_dailyreadingbytype.subtypeid=smas_water_type.subtypeid and
				      typename=type_name AND
				      holidayflag=0
				      and extract(dow from readdate)=weekday
				group by 1,2,3,4 order by 2) A
		) B where y  is not null and
			  y1 is not null AND
			  y2 is not null;
	END IF;

	DROP TABLE IF EXISTS smas_water_parx_temp_linregr;
	DROP TABLE IF EXISTS smas_water_parx_temp_linregr_summary;

	PERFORM madlib.linregr_train( 'smas_water_parx_temp',
				     'smas_water_parx_temp_linregr',
				     'y',
				     'ARRAY[1, y1, y2, temp, precip]'
				   );


	INSERT INTO smas_water_dailyreadingbytype_parx
	SELECT smas_water_parx_temp.typename,
	       smas_water_parx_temp.readdate,
	       smas_water_parx_temp.y as reading,
	       madlib.linregr_predict(ARRAY[1, y1, y2, temp, precip],
			       m.coef
			     ) as predict,
	       smas_water_parx_temp.y-m.coef[4]*smas_water_parx_temp.temp as readingwithprecip,
	       smas_water_parx_temp.y-m.coef[5]*smas_water_parx_temp.precip as readingwithtemp,
	       smas_water_parx_temp.y-m.coef[4]*smas_water_parx_temp.temp-m.coef[5]*smas_water_parx_temp.precip as baseload,
	       smas_water_parx_temp.maxtemp,
	       smas_water_parx_temp.precipmm
	FROM smas_water_parx_temp, smas_water_parx_temp_linregr m;

	DROP TABLE IF EXISTS smas_water_parx_temp;
	DROP TABLE IF EXISTS smas_water_parx_temp_linregr;
	DROP TABLE IF EXISTS smas_water_parx_temp_linregr_summary;
	RETURN;
END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION smas_water_dailyreadingbytype_parx(varchar)
  OWNER TO xiliu;


CREATE OR REPLACE FUNCTION RUN_smas_water_dailyreadingbytype_parx(type_name varchar(10))
  RETURNS void AS
$BODY$
BEGIN	DELETE FROM smas_water_dailyreadingbytype_parx WHERE typename= type_name;
	FOR i IN 0..5 LOOP
	   PERFORM smas_water_dailyreadingbytype_parx(type_name, i);
	END LOOP;
END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION RUN_smas_water_dailyreadingbytype_parx(varchar)
  OWNER TO xiliu;

----------------------- END: PARX MODEL ----------








--Agriture:
-------------
-- Summer/Autumn
select extract('hour' from readtime) as hour, avg(reading) as reading from smas_water_hourlyreadingbytype where typeid=1 and readtime between '2012-09-01 00:00:00'::timestamp and '2013-03-06 23:00:00'::timestamp group by 1 order by 1;

-- Winter/Spring
select extract('hour' from readtime), avg(reading) from smas_water_hourlyreadingbytype where typeid=1 and readtime between '2013-03-17 00:00:00'::timestamp and '2013-08-31 23:00:00'::timestamp group by 1 order by 1;


-----MFRES-----------------------------
rs<-dbSendQuery(con, "select extract('hour' from readtime), avg(reading) from smas_water_hourlyreadingbytype where typeid=6 and readtime between '2013-03-21 00:00:00'::timestamp and '2013-06-20 23:00:00'::timestamp group by 1 order by 1")
spring<-fetch(rs,n=-1)

rs<-dbSendQuery(con, "select extract('hour' from readtime), avg(reading) from smas_water_hourlyreadingbytype where typeid=6 and (readtime between '2013-06-21 00:00:00'::timestamp and '2013-08-31 23:00:00' or readtime between  '2012-09-01 00:00:00'::timestamp and  '2012-09-20 00:00:00'::timestamp)  group by 1 order by 1")
summer<-fetch(rs,n=-1)

rs<-dbSendQuery(con, "select extract('hour' from readtime), avg(reading) from smas_water_hourlyreadingbytype where typeid=6 and readtime between '2012-09-21 00:00:00'::timestamp and '2012-12-20 23:00:00' group by 1 order by 1")
fall<-fetch(rs,n=-1)

rs<-dbSendQuery(con, "select extract('hour' from readtime), avg(reading) from smas_water_hourlyreadingbytype where typeid=6 and readtime between '2012-12-21 00:00:00'::timestamp and '2013-03-30 23:00:00' group by 1 order by 1")
winter<-fetch(rs,n=-1)
-----------------------

-----SFRES-----------------------------
rs<-dbSendQuery(con, "select extract('hour' from readtime), avg(reading) from smas_water_hourlyreadingbytype where typeid=5 and readtime between '2013-03-21 00:00:00'::timestamp and '2013-06-20 23:00:00'::timestamp group by 1 order by 1")
spring<-fetch(rs,n=-1)

rs<-dbSendQuery(con, "select extract('hour' from readtime), avg(reading) from smas_water_hourlyreadingbytype where typeid=5 and (readtime between '2013-06-21 00:00:00'::timestamp and '2013-08-31 23:00:00' or readtime between  '2012-09-01 00:00:00'::timestamp and  '2012-09-20 00:00:00'::timestamp)  group by 1 order by 1")
summer<-fetch(rs,n=-1)

rs<-dbSendQuery(con, "select extract('hour' from readtime), avg(reading) from smas_water_hourlyreadingbytype where typeid=5 and readtime between '2012-09-21 00:00:00'::timestamp and '2012-12-20 23:00:00' group by 1 order by 1")
fall<-fetch(rs,n=-1)

rs<-dbSendQuery(con, "select extract('hour' from readtime), avg(reading) from smas_water_hourlyreadingbytype where typeid=5 and readtime between '2012-12-21 00:00:00'::timestamp and '2013-03-30 23:00:00' group by 1 order by 1")
winter<-fetch(rs,n=-1)

-----Com-----------------------------
rs<-dbSendQuery(con, "select extract('hour' from readtime), avg(reading) from smas_water_hourlyreadingbytype where typeid=3 and readtime between '2013-03-15 00:00:00'::timestamp and '2013-08-31 23:00:00'::timestamp group by 1 order by 1")
spring<-fetch(rs,n=-1)

-----SFRES-----------------------------

rs<-dbSendQuery(con, "select extract('hour' from readtime), avg(reading) from smas_water_hourlyreadingbytype where typeid=4 and (readtime between '2013-06-21 00:00:00'::timestamp and '2013-08-31 23:00:00' or readtime between  '2012-09-01 00:00:00'::timestamp and  '2012-12-20 00:00:00'::timestamp)  group by 1 order by 1")
summer<-fetch(rs,n=-1)

rs<-dbSendQuery(con, "select extract('hour' from readtime), avg(reading) from smas_water_hourlyreadingbytype where typeid=4 and readtime between '2013-01-01 00:00:00'::timestamp and '2013-06-20 23:00:00' group by 1 order by 1")
winter<-fetch(rs,n=-1)


-----INST-----------------------------
rs<-dbSendQuery(con, "select extract('hour' from readtime), avg(reading) from smas_water_hourlyreadingbytype where typeid=2 and readtime between '2012-11-24 00:00:00'::timestamp and '2013-08-31 23:00:00'::timestamp group by 1 order by 1")
spring<-fetch(rs,n=-1)