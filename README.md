Smart Meter Data Analysis System - SMAS
======================

Figure 1 shows the architecture of SMAS.  The real-time layer consumes raw smart meter data and runs simple alerting queries, such as those which detect very high consumption readings.  The batch layer packages raw data into chunks that are batch-loaded into the analytics database, e.g., once an hour or once a day.  The batch layer also accepts bulk-uploads, e.g., historical consumption data from legacy systems.  Thus far, the analytics layer was implemented (see Figure 2), which consists of an underlying database, analytics libraries, and a Web front-end.
The analytics layer uses PostgreSQL as the database and MADLib as the (in-database) machine learning library.  Highcharts (www.highcharts.com) is used as the visualization engine, and Tomcat as the web application server.  The main functionalities of this system include consumer feedback, pattern discovery, segmentation (clustering) analysis and forecasting.  

![The architecture of SMAS](https://dl.dropboxusercontent.com/u/8691433/benchmark/img/smas.png)

Here is the video of SMAS:
[![SMAS](https://www.youtube.com/watch?v=5717mOJSwfI&list=UU9F0rInEDHm1RiFD_R_TGMQ)](https://www.youtube.com/watch?v=5717mOJSwfI&list=UU9F0rInEDHm1RiFD_R_TGMQ)
