# This is code for loading the DW from the running example 
# presented in C. Thomsen & T.B. Pedersen: "pygrametl: A Powerful Programming 
# Framework for Extract--Transform--Load Programmers"
#
# It is made to be used with PostgreSQL and psycopg2 but you can
# modify it to work with another DBMS.


#  
#  Copyright (c) 2009, 2010 Christian Thomsen ( chr @ cs . aau . dk )
#  
#  This file is free software: you may copy, redistribute and/or modify it  
#  under the terms of the GNU General Public License version 2 
#  as published by the Free Software Foundation.
#  
#  This file is distributed in the hope that it will be useful, but  
#  WITHOUT ANY WARRANTY; without even the implied warranty of  
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
#  General Public License for more details.  
#  
#  You should have received a copy of the GNU General Public License  
#  along with this program.  If not, see http://www.gnu.org/licenses.  
#  


import datetime
import sys
import time
import datetime
# In this example, we use psycopg2. You can change it to another driver,
# but then the method pgcopybulkloader won't work as we use driver-specific
# code there. 
# You can make another function or declare facttbl (see further below) to 
# be a BatchFactTable such that you don't need special 
# bulk loading methods.

import psycopg2

# Depending on your system, you might have to do something like this
# where you append the path where pygrametl is installed


import pygrametl
from pygrametl.datasources import SQLSource, CSVSource, HashJoiningSource
from pygrametl.tables import CachedDimension, BulkFactTable


# Connection to target DW:
pgconn = psycopg2.connect(database="essex", user="xiliu", password="Abcd1234")
connection = pygrametl.ConnectionWrapper(pgconn)
connection.setasdefault()
#connection.execute('set search_path to essex')


# Methods
def pgcopybulkloader(name, atts, fieldsep, rowsep, nullval, filehandle):
    # Here we use driver-specific code to get fast bulk loading.
    # You can change this method if you use another driver or you can
    # use the FactTable or BatchFactTable classes (which don't require
    # use of driver-specifc code) instead of the BulkFactTable class.
    global connection
    curs = connection.cursor()
    curs.copy_from(filehandle, name, '\t', '')
    #curs.copy_from(file=filehandle, table=name, sep=fieldsep,
    #               null=str(nullval), columns=atts)

def datehandling(row):
    readdatetime = datetime.datetime.strptime(row['EndTime'],'%Y-%m-%d %H:%M:%S')
    row['readdate'] = readdatetime.strftime('%Y-%m-%d')
    row['readtime'] = readdatetime.hour

def accounttypehandling(row):
	row['type'] = row['subtype'].split('-')[0]
    
usage_type_dim = CachedDimension(
    name='smas_water_Type', 
    key='typeid',
    lookupatts=['subtype'],
    attributes=['type', 'subtype']
    )

customer_dim = CachedDimension(
    name='smas_water_customer', 
    key='custid',
    lookupatts=['accountno'],
    attributes=['accountno', 'street', 'city', 'province', 'postcode']
    )
    
meter_dim = CachedDimension(
    name='smas_water_meter', 
    key='meterid',
    lookupatts=['meterno'],
    attributes=['meterno', 'latitude', 'longitude']
    )

    
hourlyreading_fact = BulkFactTable(
    name='smas_water_hourlyreading', 
    keyrefs=['typeid', 'custid', 'meterid'],
    measures=['readtime', 'reading', 'temperature'], 
    bulkloader=pgcopybulkloader,
    bulksize=500000)    

dailyreading_fact = BulkFactTable(
    name='smas_water_dailyreading', 
    keyrefs=['typeid', 'custid', 'meterid'],
    measures=['readtime', 'reading', 'temperature'], 
    bulkloader=pgcopybulkloader,
    bulksize=500000)    

# Data sources - change the path if you have your files somewhere else  
#reading = SQLSource(connection,
#			'SELECT meterid,  datavalue, endtime FROM water_meterreading_tmp',
#			names=['meterno', 'reading', 'readtimestamp'])
# 
#ServicePointID,ChannelNumber,MeterID,RecordingDeviceID,AccountID,CustomerID,ChannelType,DataValue,EndTime,UOM_Description

inputdata = CSVSource(file('/data/waterdata/reading.csv', 'r', 16384),
					fieldnames=['accountno', 'meterno', 'subtype','unit','house','street','city','province','postcode','latitude', 'longitude', 'readtime', 'reading'],
					delimiter='|')

#customer = SQLSource(connection, 
#		'SELECT register_s, latitude, longitude, account_nu, account_ty, street, city, province, postal_cod  FROM water_user', 
#		names=['meterno', 'latitude', 'longitude', 'accountno', 'type', 'street', 'city', 'province', 'postcode'])
			
#inputdata = HashJoiningSource(reading, 'MeterID', customer, 'meterno')


def main():
    for row in inputdata:
        accounttypehandling(row)
        row['typeid'] = usage_type_dim.ensure(row)
        row['custid'] = customer_dim.ensure(row)
        row['meterid'] = meter_dim.ensure(row)
        row['temperature'] = 0.0
        hourlyreading_fact.insert(row)
    connection.commit()

if __name__ == '__main__':
    main()

