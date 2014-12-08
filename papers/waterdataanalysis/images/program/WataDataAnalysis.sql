---------Figure 4 The average weekly profile of single family residential water consumption-------------------------

-- Summer 
(select extract(DOW from readtime) as weekday, 
       extract('hour' from readtime) as hours,  
       avg(reading)/20034 as reading 
from smas_water_hourlyreadingbytype where (subtypeid=1 OR subtypeid=2) AND
					   holidayFLAG=0 AND
					   (readtime between '2012-09-01 00:00:00'::timestamp and '2012-10-09 23:00:00' OR
					    readtime between '2013-07-01 00:00:00'::timestamp and '2013-08-31 23:00:00') 
					   GROUP BY 1,2 ORDER BY 1,2)
union
(select 6 as weekday, 
       extract('hour' from readtime) as hours,  
       avg(reading)/20034 as reading 
from smas_water_hourlyreadingbytype where (subtypeid=1 OR subtypeid=2) AND
					   holidayFLAG=1 AND
					   (readtime between '2012-09-01 00:00:00'::timestamp and '2012-10-09 23:00:00' OR
					    readtime between '2013-07-01 00:00:00'::timestamp and '2013-08-31 23:00:00') 
					   GROUP BY 1,2 ORDER BY 1,2) order by 1,2



-- Winter 
(select extract(DOW from readtime) as weekday, 
       extract('hour' from readtime) as hours,  
       avg(reading)/20034 as reading 
from smas_water_hourlyreadingbytype where (subtypeid=1 OR subtypeid=2) AND
					   holidayFLAG=0 AND
					   readtime between '2012-10-10 00:00:00'::timestamp and '2013-06-30 23:00:00' 
					   GROUP BY 1,2 ORDER BY 1,2)
union
(select 6 as weekday, 
       extract('hour' from readtime) as hours,  
       avg(reading)/20034 as reading 
from smas_water_hourlyreadingbytype where (subtypeid=1 OR subtypeid=2) AND
					   holidayFLAG=1 AND
					   readtime between '2012-10-10 00:00:00'::timestamp and '2013-06-30 23:00:00'
					   GROUP BY 1,2 ORDER BY 1,2) order by 1,2


--------------------------------------------

SELECT readdate, A.readarr[floor(A.cnt*0.1)] as q10
FROM (
 SELECT 
	readdate, 
	count(*) AS cnt, 
	array_agg(reading ORDER BY reading) AS readarr 
  FROM smas_water_dailyreading WHERE subtypeid=1 or subtypeid=2 GROUP BY 1 ORDER BY 1
) AS A;


drop table mas_water_dailyreadingtenpct;

create table smas_water_dailyreadingtenpct 
as select A.summerflag, A.weekday, A.readarr[floor(A.cnt*0.1)] as tenpct from (
	select 1 as summerflag, 
		  extract(dow from readdate) as weekday, 
		  count(*) as cnt, 
		  array_agg(reading order by reading) as readarr 
	from smas_water_dailyreading 
	where  (subtypeid=1 or subtypeid=2) and holidayflag=0 and
	(readdate between '2012-09-01'::date and '2012-10-09'::date OR readdate between '2013-06-01'::date and '2013-08-31'::date) 
	group by 1,2
) A order by 1,2;




library(RPostgreSQL)

con<-dbConnect(dbDriver('PostgreSQL'), dbname='essex');

# Average Weekly load profile of water consumption (Figure2)
rs<-dbSendQuery(con, "(select extract(DOW from readtime) as weekday, extract('hour' from readtime) as hours,  sum(reading)/(20034*count(1)) as reading from smas_water_hourlyreadingbytype where (subtypeid=1 OR subtypeid=2) AND holidayFLAG=0 GROUP BY 1,2 ORDER BY 1,2) union (select 6 as weekday, extract('hour' from readtime) as hours, sum(reading)/(2034*count(1)) as reading from smas_water_hourlyreadingbytype where (subtypeid=1 OR subtypeid=2) AND holidayFLAG=1 GROUP BY 1,2 ORDER BY 1,2) order by 1,2");

data<-fetch(rs, n=-1);
pdf('/tmp/figure.pdf')
plot.ts(ts(data$reading, frequency=24, start=c(1,2,3,4,5,6)), ylab='Average water consumption per capita, m3', xlab='');
dev.off()


rs<-dbSendQuery(con, "select readdate, maxtemp, precipmm,sum(reading)/20034 as reading from smas_water_dailyreadingbytype where (subtypeid=1 OR subtypeid=2) group by 1,2,3 order by 1");
data<-fetch(rs, n=-1);

pdf('/tmp/figure.pdf')
par(mfrow=c(3,1))
plot(ts(data$maxtemp))
plot(ts(data$precipmm))
plot(ts(data$reading))
dev.off()
-----------------------
rs<-dbSendQuery(con, "select maxtemp, precipmm, reading/custsize as reading from smas_water_dailyreading_sfres where (extract('month' from readdate) between 1 and 4 OR extract('month' from readdate) between 11 and 12) order by 1")
data<-fetch(rs, n=-1);
pdf('/tmp/all.pdf')
par(mfrow=c(2,2))
plot(data$precipmm, data$reading, pch=16, cex=0.6)
abline(lm(data$reading~data$precipmm),col='red')
plot(summerdata$precipmm, summerdata$reading, pch=16, cex=0.6)
abline(lm(summerdata$reading~summerdata$precipmm),col='red')
plot(data$maxtemp, data$reading, pch=16, cex=0.6)
abline(lm(data$reading~data$maxtemp),col='red')
plot(summerdata$maxtemp, summerdata$reading, pch=16, cex=0.6)
abline(lm(summerdata$reading~summerdata$maxtemp),col='red')
dev.off()



