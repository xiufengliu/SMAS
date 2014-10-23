Smart Meter Data Analysis System - SMAS
======================

Figure  shows the design of the analytics layer, which uses PostgreSQL as the database and MADLib as the (in-database)
machine learning library. Furthermore, we use Highcharts (www.highcharts.com) as the visualization engine, and Tomcat as
the web application server. The main functionalities of SMAS include consumer feedback, segmentation (clustering) analysis and
forecasting. Each of these tasks requires a set of features and patterns to be extracted from the data (which are stored in materialized
views). We employ three algorithms for feature extraction, as detailed below.

![The architecture of SMAS](https://dl.dropboxusercontent.com/u/8691433/benchmark/img/smas.png)
