Smart Meter Data Analysis System - SMAS
======================

Figure 1 shows the architecture of SMAS, which consists of real-time layer, batch layer and analytics layer. The real-time layer consumes raw smart meter data and runs simple alerting queries, such as those which detect very high consumption readings.  The batch layer packages raw data into chunks that are batch-loaded into the analytics database, e.g., once an hour or once a day.  The batch layer also accepts bulk-uploads, e.g., historical consumption data from legacy systems.  Thus far, we have implemented the analytics layer (see Figure 2), which consists of an underlying database, analytics libraries, and a Web front-end. The analytics layer uses PostgreSQL as the database,  MADLib (madlib.net) as the (in-database) machine learning library,  Highcharts (www.highcharts.com) as the visualization engine, and Tomcat as the web application server. 
![The architecture of SMAS](https://raw.githubusercontent.com/xiufengliu/SMAS/master/src/main/webapp/img/meter3.png)

The functionalities:
============================
This system is still under development. The current implementation has the following functionalities:

* *Consumption time series analytics;*
* *Consumption profiling;*
* *Pattern discovery;*
* *Segmentation (clustering) analysis;*
* *Consumer feedback;*
* *Forecasting*



Installation & Usage
===========================
To use SMAS, users first have to install the open source RDBMS [PostgreSQL](http://www.postgresql.org/), the in-database machine learning toolkit, [MADlib](www.madlib.net), the open source application server [Tomcat](http://tomcat.apache.org/). Then, run the the sql script to create the smas database, schema, tables and the analytics functions.

When the above steps are done, run the gradlew to compile the the source codes, deploy the generated binary packages, and start the application, etc.


Video
======================
[Here](https://www.youtube.com/watch?v=5717mOJSwfI&list=UU9F0rInEDHm1RiFD_R_TGMQ) is the video of introducing SMAS.

Publication
========================
[Here](https://db.tt/0SWiDFC6) is the demo paper accepted by [ICDE 2015](http://www.icde2015.kr/cfd.html)
