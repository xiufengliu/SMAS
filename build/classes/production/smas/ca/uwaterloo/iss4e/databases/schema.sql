DROP TABLE smas_user;
CREATE TABLE smas_user(
    userID integer primary key,
    firstname varchar(40),
    lastname varchar(40),
    birthday date,
    username varchar(20),
    password varchar(20),
    meters integer[]
);

DROP TABLE smas_meter;
CREATE TABLE smas_meter(
    meterID integer primary key,
    MeterNumber varchar(30) ,
    CustomerPremiseNumber integer ,
    StreetNumber varchar(10) ,
    Address varchar(40) ,
    ConnectDate timestamp ,
    DisconnectDate timestamp,
    PeakKW numeric(38,8),
    Volts integer,
    Phase integer,
    AreaTown varchar(40),
    Enabled integer,
    Feeder varchar(20),
    PointX integer,
    PointY integer,
    TransformerID varchar(20) ,
    geom_Latitude numeric(15,13),
    geom_Longitude numeric(15,13),
    pool_state_id integer  DEFAULT '3',
    fsa_code varchar(3),
    da_id_2011 integer,
    da_id_2006 integer,
    property_type_id integer
);

DROP TABLE smas_feedback;
CREATE TABLE smas_feedback(
    meterID integer primary key,
    name varchar(100),
    rule varchar(40),
    receiver_type varchar(10),
    receiver_value integer[],
    startdate timestamp,
    enddate timestamp,
    repeat_interval integer,
    enabled integer
);

DROP TABLE smas_meterreading;

CREATE TABLE smas_meterreading(
    meterID integer,
    readdate date,
    readtime integer,
    reading numeric(10,2),
    temperature numeric(10,2),
    activity float8,
    isholiday integer
);
CREATE INDEX idx_smas_meterreading_meterid ON smas_meterreading USING btree (meterid);

DROP TABLE smas_threel;
CREATE TABLE  smas_threel(
    meterid integer primary key,
    params double precision[]
    );


DROP TYPE IF EXISTS smas_points_type CASCADE;

CREATE TYPE smas_points_type AS (
    pointA           float8[],
    pointB           float8[],
    pointC           float8[],
    pointD           float8[]
);


CREATE OR REPLACE FUNCTION smas_quantiled_threel(size integer, temarr double precision[], qua double precision[])
RETURNS smas_points_type AS
$BODY$
	DECLARE
	  rq1 float8;
	  rq2 float8;
	  rq3 float8;
	  total_or2 float8:= 'Infinity'::float8;
	  x1 float8;
	  x2 float8;ox2 float8;
	  x3 float8;ox3 float8;
	  xn float8;
	  y1 float8;
	  y2 float8;
	  y3 float8;
	  yn float8;
	  coef1 float8[];coeftmp1 float8[];
	  coef2 float8[];coeftmp2 float8[];
	  coef3 float8[];coeftmp3 float8[];
	  m2 float8;
	  m3 float8;
	  h2 float8;
	  h3 float8;
	  p2s float8[][];
	  p3s float8[][];
	  pos int4;

	  o_p1 float8[];
	  o_p2 float8[];
	  o_p3 float8[];
	  o_p4 float8[];

	  err1 float8:=0;
	  err2 float8:=0;
	  err3 float8:=0;
	  result smas_points_type;
BEGIN
	FOR s1 IN 10..20 LOOP
		SELECT (lr).coef, (lr).r2 INTO coeftmp1, rq1 FROM (
			SELECT (madlib.linregr(A.q, ARRAY[1.0, t])) AS lr  from (select unnest(temarr) as t, unnest(qua) as q) AS A WHERE A.t<=s1
		) AS lrq;

		FOR s2 IN (s1+5)..(s1+10) LOOP
			SELECT (lr).coef, (lr).r2 INTO coeftmp2, rq2 FROM (
			  SELECT (madlib.linregr(A.q, ARRAY[1.0, t])) AS lr  FROM (SELECT unnest(temarr) AS t, unnest(qua) AS q) AS A WHERE A.t BETWEEN s1 AND s2
			) AS lrq;
			SELECT (lr).coef, (lr).r2 INTO coeftmp3, rq3 FROM (
			  SELECT (madlib.linregr(A.q, ARRAY[1.0, t])) AS lr  FROM (SELECT unnest(temarr) AS t, unnest(qua) AS q) AS A WHERE A.t>=s2
			) AS lrq;
		   IF (rq1+rq2+rq3<total_or2) THEN
                ox2 := s1;
                ox3 := s2;
                coef1 := coeftmp1;
                coef2 := coeftmp2;
                coef3 := coeftmp3;
                total_or2 := rq1+rq2+rq3;
		   END IF;
		END LOOP;
	END LOOP;

    RAISE NOTICE 'ox2, ox3=%, %', ox2, ox3;
    RAISE NOTICE 'coef1, coef2, coef3=%, %, %', coef1, coef2, coef3;

	m2 :=((coef1[1]+coef1[2]*ox2)+(coef2[1]+coef2[2]*ox2))/2.0;
	h2 :=((coef2[1]+coef2[2]*ox2)-(coef1[1]+coef1[2]*ox2))/2.0;
    RAISE NOTICE 'm2, h2=%, %', m2,h2;
	m3 :=((coef2[1]+coef2[2]*ox3)+(coef3[1]+coef3[2]*ox3))/2.0;
	h3 :=((coef3[1]+coef3[2]*ox3)-(coef2[1]+coef2[2]*ox3))/2.0;
    RAISE NOTICE 'm3, h3=%, %', m3,h3;
	p2s := ARRAY[[ox2-0.5, m2+h2]]||ARRAY[ox2,m2+h2]||ARRAY[ox2+0.5, m2+h2]
		  ||ARRAY[ox2-0.5, m2]||ARRAY[ox2,m2]||ARRAY[ox2+0.5,m2]
		  ||ARRAY[ox2-0.5,m2-h2]||ARRAY[ox2,m2-h2]||ARRAY[ox2+0.5,m2-h2];

	p3s := ARRAY[[ox3-0.5, m3+h3]]||ARRAY[ox3,m3+h3]||ARRAY[ox3+0.5, m3+h3]
		  ||ARRAY[ox3-0.5, m3]||ARRAY[ox3,m3]||ARRAY[ox3+0.5,m3]
		  ||ARRAY[ox3-0.5,m3-h3]||ARRAY[ox3,m3-h3]||ARRAY[ox3+0.5,m3-h3];

    RAISE NOTICE 'p2s=%', p2s;
    RAISE NOTICE 'p3s=%', p3s;

	total_or2 :=  'Infinity'::float8;
	FOR i in 1..9 LOOP
	   x1 := temArr[1];
	   y1 := coef1[1]+coef1[2]*x1;
	   x2 := unnest(p2s[i:i][1:1]);
	   y2 := unnest(p2s[i:i][2:2]);
	   -- y=y1+(y2-y1)/(x2-x1)*(x-x1) --line 1
	   err1 := 0;
	   FOR idx IN 1..size LOOP
            IF temArr[idx]<=x2 THEN
               err1 := err1 + power(qua[idx]-(y1+(y2-y1)/(x2-x1)*(temArr[idx]-x1)),2);
            ELSE
               pos:=idx;
               exit;
            END IF;
	   END LOOP;

	   FOR j in 1..9 LOOP
		x3 := unnest(p3s[j:j][1:1]);
		y3 := unnest(p3s[j:j][2:2]);
		xn := temArr[size];
		yn := coef3[1]+coef3[2]*xn;
		-- y=y2+(y3-y2)/(x3-x2)*(x-x2) --line 2
		-- y=y3+(yn-y3)/(xn-x3)*(x-x3) --line 3

        err2 := 0;
        err3 := 0;
		FOR idx IN pos..size LOOP
			if temArr[idx]<=x3 then
			   err2 := err2 + power(qua[idx]-(y2+(y3-y2)/(x3-x2)*(temArr[idx]-x2)),2);
			else
			   err3 := err3 + power(qua[idx]-(y3+(yn-y3)/(xn-x3)*(temArr[idx]-x3)),2);
			end if;
		END LOOP;
	 --RAISE notice 'err1+err2+err3=%, %', err1+err2+err3,total_or2;
		IF (err1+err2+err3<total_or2) THEN
		   total_or2 := err1+err2+err3;
		   o_p1 := ARRAY[x1, y1];
		   o_p2 := ARRAY[x2, y2];
		   o_p3 := ARRAY[x3, y3];
		   o_p4 := ARRAY[xn, yn];
		END IF;
	   END LOOP;
	END LOOP;

	SELECT o_p1, o_p2, o_p3, o_p4 INTO result;
	RETURN result;
END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;


CREATE OR REPLACE FUNCTION smas_threel(hid integer)
  RETURNS SETOF smas_points_type  AS
$BODY$
DECLARE
  lines10 smas_points_type;
  lines50 smas_points_type;
  lines90 smas_points_type;
  size int4;
  temArr float8[];
  q10Arr float8[];
  q50Arr float8[];
  q90Arr float8[];
BEGIN
	SELECT  array_agg(A.tem),
	        array_agg(A.readarr[floor(A.cnt*0.1)]),
	        array_agg(A.readarr[floor(A.cnt*0.5)]),
	        array_agg(A.readarr[floor(A.cnt*0.9)])
	        INTO temArr, q10Arr, q50Arr, q90Arr
	 FROM (
		 SELECT
			round(temperature) AS tem,
			count(*) AS cnt,
			array_agg(reading ORDER BY reading) AS readarr
		 FROM smas_meterreading  WHERE meterid=hid GROUP BY tem HAVING count(*)>20 ORDER BY tem
	) AS A;

    size := array_length(temArr,1);
    IF size>2 THEN
      lines10 := smas_quantiled_threel(size, temArr, q10Arr);
      lines50 := smas_quantiled_threel(size, temArr, q50Arr);
      lines90 := smas_quantiled_threel(size, temArr, q90Arr);
      RETURN NEXT lines10; RETURN NEXT lines50; RETURN NEXT lines90;
    ELSE
        RETURN;
    END IF;
END
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
----------------

CREATE OR REPLACE FUNCTION smas_PARX(holidayFlag integer)
RETURNS void AS
$BODY$
BEGIN
	DROP TABLE IF EXISTS smas_parx_temp;
	CREATE TEMP TABLE smas_parx_temp (
		meterid INTEGER,
		season INTEGER,
		yt float8,
		x1 float8,
		x2 float8,
		x3 float8,
		ox1 float8,
		ox2 float8,
		ox3 float8
	 );

	INSERT INTO  smas_parx_temp
	SELECT * FROM (
		SELECT  meterid,
			readtime as season,
			reading as yt,
			LEAD(reading, 24*1) OVER (PARTITION BY meterid order by readdate desc, readtime desc) as x1,
			LEAD(reading, 24*2) OVER (PARTITION BY meterid order by readdate desc, readtime desc) as x2,
			LEAD(reading, 24*3) OVER (PARTITION BY meterid order by readdate desc, readtime desc) as x3,
			(CASE 	WHEN temperature>20 THEN temperature-20
				ELSE 0
			END) AS ox1,
			(CASE 	WHEN temperature<16 AND temperature>=5 THEN 16-temperature
				ELSE 0
			END) AS ox2,
			(CASE 	WHEN temperature<5 THEN 5-temperature
				ELSE 0
			END) AS ox3
		FROM  smas_meterreading WHERE isHoliday=holidayFlag
	) A WHERE yt IS NOT NULL AND
		  x1 IS NOT NULL AND
		  x2 IS NOT NULL AND
		  x3 IS NOT NULL;

	DROP TABLE IF EXISTS smas_linregr;
	DROP TABLE IF EXISTS smas_linregr_summary;

	PERFORM madlib.linregr_train( 'smas_parx_temp','smas_linregr','yt','ARRAY[x1, x2, x3, ox1, ox2, ox3]','meterid, season');

	UPDATE smas_meterreading
	SET activity= GREATEST(reading-LEAST(( A.coef[3]*(CASE WHEN temperature>20 THEN temperature-20 ELSE 0 END)
			       +A.coef[4]*(CASE WHEN temperature<16 AND temperature>=5 THEN 16-temperature ELSE 0 END)
			       +A.coef[5]*(CASE WHEN temperature<5 THEN 5-temperature ELSE 0 END)
			      ), 0.2), 0.0)
	FROM smas_linregr A
	WHERE smas_meterreading.isHoliday = holidayFlag AND
	      smas_meterreading.meterid=A.meterid AND
	      smas_meterreading.readtime=A.season;

	DROP TABLE smas_linregr;
	DROP TABLE smas_linregr_summary;
	DROP TABLE smas_parx_temp;
	RETURN;
END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION smas_PARX(integer)
  OWNER TO xiliu;
--------------------

DROP TABLE smas_consumptionpattern;
CREATE TABLE smas_consumptionpattern(
    meterid integer primary key,
    tenpct float8[],
    tenpct_C integer,
    fiftypct float8[],
    fiftypct_C integer,
    ninetypct float8[],
    ninetypct_C integer,
    average float8[],
    average_C integer,
    activity_avg_weekday float8[],
    activity_avg_weekday_C integer,
    activity_avg_holiday float8[],
    activity_avg_holiday_C integer
);

DROP TABLE smas_consumptionpattern_centroid;
CREATE TABLE smas_consumptionpattern_centroid(
    measure varchar(30),
    clusterid integer,
    cent float8[]
);


INSERT INTO smas_consumptionpattern(
    meterid, tenpct, fiftypct, ninetypct, average
) SELECT
	A.meterid,
	array_agg(A.readarr[floor(A.cnt*0.1)]),
	array_agg(A.readarr[floor(A.cnt*0.5)]),
	array_agg(A.readarr[floor(A.cnt*0.9)]),
	array_agg(A.AvgReading)
 FROM (
	 SELECT
		meterid,
		readtime,
		count(*) AS cnt,
		array_agg(reading ORDER BY reading) AS readarr,
		avg(reading) AS AvgReading
	 FROM smas_meterreading GROUP BY meterid, readtime ORDER BY meterid, readtime
) AS A GROUP BY A.meterid;


UPDATE smas_consumptionpattern
SET activity_avg_weekday = (
    SELECT
        array_agg(A.AvgActivity)
    FROM (
        SELECT readtime,
               avg(activity) as AvgActivity
        FROM smas_meterreading WHERE  smas_meterreading.meterid=smas_consumptionpattern.meterid AND
                                      smas_meterreading.isHoliday=0
        GROUP BY readtime ORDER BY readtime
    ) A
);

UPDATE smas_consumptionpattern
SET activity_avg_holiday = (
    SELECT
        array_agg(A.AvgActivity)
    FROM (
        SELECT readtime,
               avg(activity) as AvgActivity
        FROM smas_meterreading WHERE  smas_meterreading.meterid=smas_consumptionpattern.meterid AND
                                      smas_meterreading.isHoliday=1
        GROUP BY readtime ORDER BY readtime
    ) A
);




DROP TABLE smas_norm_consumptionpattern;
CREATE TABLE smas_norm_consumptionpattern(
    meterid integer primary key,
    tenpct float8[],
    tenpct_C integer,
    fiftypct float8[],
    fiftypct_C integer,
    ninetypct float8[],
    ninetypct_C integer,
    average float8[],
    average_C integer,
    activity_avg_weekday float8[],
    activity_avg_weekday_C integer,
    activity_avg_holiday float8[],
    activity_avg_holiday_C integer
);

DROP TABLE smas_norm_consumptionpattern_centroid;
CREATE TABLE smas_norm_consumptionpattern_centroid(
    measure varchar(30),
    clusterid integer,
    cent float8[]
);


INSERT INTO smas_norm_consumptionpattern
(
	meterid,
	tenpct,
	fiftypct,
	ninetypct,
	average,
	activity_avg_weekday,
	activity_avg_holiday
)SELECT A.meterid,
	array_agg(e10/t10),
	array_agg(e50/t50),
	array_agg(e90/t90),
	array_agg(e_avg/t_avg),
	array_agg(e_avg_w/t_avg_w),
	array_agg(e_avg_h/t_avg_h)
FROM (SELECT meterid,
	unnest(tenpct) as e10,
	unnest(fiftypct) as e50,
	unnest(ninetypct) as e90,
	unnest(average) as e_avg,
	unnest(activity_avg_weekday) as e_avg_w,
	unnest(activity_avg_holiday) as e_avg_h
	FROM smas_consumptionpattern) A,
(SELECT meterid,
	sum(e10) as t10,
	sum(e50) as t50,
	sum(e90) as t90,
	sum(e_avg) as t_avg,
	sum(e_avg_w) as t_avg_w,
	sum(e_avg_h) as t_avg_h
FROM (
	SELECT meterid,
		unnest(tenpct) as e10,
		unnest(fiftypct) as e50,
		unnest(ninetypct) as e90,
		unnest(average) as e_avg,
		unnest(activity_avg_weekday) as e_avg_w,
		unnest(activity_avg_holiday) as e_avg_h
	FROM smas_consumptionpattern) C
GROUP BY meterid) B
WHERE A.meterid=B.meterid
GROUP BY A.meterid;


CREATE OR REPLACE FUNCTION smas_KCS(k integer, tablename varchar(20),  measure varchar(10))
  RETURNS text AS
$BODY$
    #import sys,logging
    #LOG_FILENAME = '/tmp/plpython.log'
    #logging.basicConfig(filename=LOG_FILENAME,level=logging.DEBUG)

    from pyksc import ksc
    import numpy as np
    rv = plpy.execute('SELECT meterid, %s FROM %s ORDER BY meterid' % (measure, tablename))
    readings = []
    meterids = []
    for i in range(len(rv)):
        meterids.append(rv[i]['meterid'])
        readings.append(rv[i][measure])
    cents, assign, shift, distc = ksc.ksc(np.array(readings), k)
    #logging.debug('--------cents-----------')
    #logging.debug(cents)

    plpy.execute("DELETE FROM  %s_centroid WHERE measure='%s'" % (tablename, measure))
    for i in range(len(cents)):
        cent = str(map(float, cents[i]))
        cent = cent.replace('[', '{').replace(']', '}')
        plpy.execute("INSERT INTO %s_centroid VALUES('%s', %d, '%s')" % (tablename, measure, i, cent))

    for i in range(len(meterids)):
        plpy.execute('UPDATE %s set %s_C=%d WHERE meterid=%d' % (tablename, measure, assign[i], meterids[i]))

$BODY$
  LANGUAGE plpythonu VOLATILE
  COST 100;
ALTER FUNCTION smas_KCS(integer, varchar(20), varchar(10))
  OWNER TO xiliu;


CREATE OR REPLACE FUNCTION smas_run_clustering(k integer)
  RETURNS text AS
$BODY$
    tables = ['smas_consumptionpattern', 'smas_norm_consumptionpattern']
    measures = ['tenpct', 'fiftypct','ninetypct','average', 'activity_avg_weekday', 'activity_avg_holiday']
    for tbl in tables:
        for measure in measures:
            plpy.execute("SELECT smas_KCS(%d, '%s', '%s')" % (k, tbl, measure))
$BODY$
  LANGUAGE plpythonu VOLATILE
  COST 100;
ALTER FUNCTION smas_run_clustering(integer)
  OWNER TO xiliu;


