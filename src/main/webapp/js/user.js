var loadAnalysis = (function(){
    var sent = {
        cmd: 'loadAnalysis',
        subCmd:'',
        level: -1,
        value: -1,
        compareMode:-1,
        radius:0,
        selectedIDs:'',
        timeLevel:2,
        startDate:'',
        endDate:''
    };

    viewEnergy_show = function(data){$('#timeseries-cavans').highcharts({
            chart: {
                zoomType: 'x',
                type: 'line'
            },
            title: {
                text: 'Electricity consumption data'
            },
            subtitle: {
                text: document.ontouchstart === undefined ?
                    'Click and drag in the plot area to zoom in' :
                    'Pinch the chart to zoom in'
            },
            xAxis: {
                type: 'datetime',
               // tickInterval: 30*24*3600 * 1000
               // minRange: 3600*1000 // Cannot zoom in more than 1hour
               minRange: data['minRange'],
               dateTimeLabelFormats: {
                   second: '%H:%M:%S',
                   minute: '%H:%M',
                   hour: '%H:%M',
                   day: '%e. %b',
                   week: '%e. %b',
                   month: '%b \'%y', //month formatted as month only
                   year: '%Y'
               }
            },
            yAxis: {
                title: {
                    text: 'Energy Consumption, kWh'
                }
            },
             tooltip: {
                    crosshairs: true,
                    shared: true,
                    valueSuffix: 'kWh'
            		  },
            legend: {
                enabled: true
            },
            plotOptions: {
                area: {
                   /* fillColor: {
                        linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1},
                        stops: [
                            [0, Highcharts.getOptions().colors[0]],
                            [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                        ]
                    },*/
                    marker: {
                        radius: 2
                    },
                    lineWidth: 1,
                    states: {
                        hover: {
                            lineWidth: 1
                        }
                    },
                    threshold: null
                }
            },
            series: data['series']
        })};

    return {
        viewMyOwn:function(){
            sent['subCmd'] = 'viewMyEnergyLoad';
            sent['level'] = $('#level').val();
            sent['value'] = $('#value').val();
            sent['timeLevel'] = 2;
            sent['startDate'] = '01/01/1990';
            sent['endDate'] = '12/31/2019';
            controller(sent, function(data){
                             viewEnergy_show(data);
                              });
        },

        viewEnergyLoad:function(){
            sent['subCmd'] = 'viewEnergyLoad';
            sent['level'] = $('#level').val();
            sent['value'] = $('#value').val();
            sent['timeLevel'] = $('#timeLevel option:selected').val();
            sent['startDate'] = $('#startDate').val();
            sent['endDate'] = $('#endDate').val();
            controller(sent, function(data){
                             viewEnergy_show(data);
                              });
        },

        view: function(level, levelValue, timeLevel, startDate, endDate){
            sent['subCmd'] = 'viewEnergyLoad';
            sent['level'] = level;
            sent['value'] = levelValue;
            sent['timeLevel'] = timeLevel;
            sent['startDate'] = startDate;
            sent['endDate'] = endDate;
            controller(sent, function(data){
                             viewEnergy_show(data);
                              });
        },
        compareOthers:function(){
            sent['subCmd'] = 'compareOthers';
            sent['level'] = $('#level').val();
            sent['value'] = $('#value').val();
            sent['radius'] = $('#radius').val();
            sent['compareMode'] = $('#compareMode').val();
            sent['startDate'] = $('#startDate').val();
            sent['endDate'] = $('#endDate').val();
            sent['compareTarget'] = $('#compareTarget option:selected').val();
            sent['selectedIDs'] = JSON.stringify(googlemap.getSelectedIDs());
            sent['timeLevel'] = $('#timeLevel option:selected').val();

            controller(sent, function(data){
                             viewEnergy_show(data);
                             });
        }
    }
})();

var profileEnergy_threel = (function(){
    var sent = {
        cmd: 'profileEnergy',
        subCmd:'getThreel',
        value:-1,
        startDate:'2008-07-01',
        endDate:'2020-12-31'
    };


    draw_threel = function(data){
                $('#threel-cavans').highcharts({
                    xAxis: {
                        min: -15,
                        max: 35,
                        title: {text: 'Weather Temperature'}
                    },
                    yAxis: {
                        min: 0,
                        title: {text: 'Energy Consumption, kWh'}
                    },
                    title: {
                        text: 'Load disaggregation: base load + activity load'
                    },
                    series: [
                    {
                        type: 'line',
                        name: '10th',
                        color: '#FF0000',
                        data: data['10th'],
                        marker: {
                            enabled: false
                        },
                        states: {
                            hover: {
                                lineWidth: 0
                            }
                        },
                        enableMouseTracking: true
                    },
                    {
                        type: 'line',
                        name: '50th',
                        color: '#000000',
                        data: data['50th'],
                        marker: {
                            enabled: false
                        },
                        states: {
                            hover: {
                                lineWidth: 0
                            }
                        },
                        enableMouseTracking: true
                    },
                    {
                        type: 'line',
                        name: '90th',
                        color: '#002EB8',
                        data: data['90th'],
                        marker: {
                            enabled: false
                        },
                        states: {
                            hover: {
                                lineWidth: 0
                            }
                        },
                        enableMouseTracking: true
                    },
                    {
                        type: 'scatter',
                        name: 'Observations',
                        data: data['points'],
                        marker: {
                            radius: 1
                        }
                    }]
                });
    };

    draw_cluster = function(data){};

    return {
        getThreel: function(){
            sent['value'] = $('#value').val();
            sent['startDate'] = $('#startDate').val();
            sent['endDate'] = $('#endDate').val();
            controller(sent, draw_threel);
        }
    }
})();


var profileEnergy_histogram = (function(){
    var sent = {
            cmd: 'profileEnergy',
            subCmd:'getHistogram',
            value:-1,
            startDate:'2008-07-01',
            endDate:'2020-12-31',
            nbuckets:20
     };

    draw_histogram = function(data){$('#histogram-cavans').highcharts({
        chart: {
                renderTo:'container',
                defaultSeriesType:'column',
                //backgroundColor:'#eee',
                borderWidth:0,
                //borderColor:'#ccc',
                //plotBackgroundColor:'#fff',
                plotBorderWidth:1,
                plotBorderColor:'#ccc'
            },
            credits:{enabled:false},
            exporting:{enabled:false},
            title:{text:'Load distribution'},
            legend:{
                //enabled:false
            },
            tooltip:{
                borderWidth:1,
                formatter:function() {
                    var up = parseFloat(this.x) + data['binSize'];
                    up = up.toFixed(2);
                    return '<b>Range:</b><br/> '+ this.x +'<=x<'+up +'<br/>'+
                    '<b>Count:</b> '+ this.y;
                }
            },
            plotOptions:{
                column:{
                    shadow:false,
                    borderWidth:.5,
                    borderColor:'#666',
                    pointPadding:0,
                    groupPadding:0,
                    color: 'rgba(204,204,204,.85)'
                },
                spline:{
                    shadow:false,
                    marker:{
                        radius:1
                    }
                },
                areaspline:{
                    color:'rgb(69, 114, 167)',
                    fillColor:'rgba(69, 114, 167,.25)',
                    shadow:false,
                    marker:{
                        radius:1
                    }
                }
            },
            xAxis:data['xAxis'],
            yAxis:{
                title:{text:'Number of hours'},
                //maxPadding:0,
               // gridLineColor:'#e9e9e9',
                //tickWidth:1,
                //tickLength:3,
                //tickColor:'#ccc',
                //lineColor:'#ccc',
                //tickInterval:25,
                //endOnTick:false,
            },
            series:data['series']
    })};

    return {
        getHistogram:function(){
            sent['value'] = $('#value').val();
            sent['startDate'] = $('#startDate').val();
            sent['endDate'] = $('#endDate').val();
            sent['nbuckets'] = $('#nbuckets option:selected').text();
            controller(sent, draw_histogram);
        }
    }
})();


var profileEnergy_avgHourlyActivityLoad = (function(){
    var sent = {
        cmd: 'profileEnergy',
        subCmd:'getAvgHourlyActivityLoad',
        value:-1,
        startDate:'2008-07-01',
        endDate:'2020-12-31',
        order:3
    };

    draw_avgHourlyActivityLoad = function (data) {
        $('#avgactivityload-cavans').highcharts({
            title: {
                text: 'Hourly Average Activity Load',
                x: -20 //center
            },
            xAxis: {
                min:0,
                max:23,
                tickInterval:1
            },
            yAxis: {
                title: {
                    text: 'Activity Load, kWh'
                },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {
                valueSuffix: 'kWh'
            },
            legend: {
                layout: 'vertical',
                align: 'center',
                verticalAlign: 'bottom',
                layout:'horizontal',
                borderWidth: 0
            },
            series: data['series']
        });
    };


    return {
        getAvgHourlyActivityLoad:function(){
            sent['value'] = $('#value').val();
            sent['startDate'] = $('#startDate').val();
            sent['endDate'] = $('#endDate').val();
            controller(sent, draw_avgHourlyActivityLoad);
        }
    }
})();



var segmentation_old = (function(){
    var sent = {cmd: 'segmentation',
                subCmd:'',
                startDate:'',
                endDate:'',
                distance:'dist_norm2',
                ncluster:4};

    showCentroids = function (data) {
             $('#centroid-cavans').highcharts({
                  title: {
                       text: 'Centroid',
                       x: -20,
                       style: {fontSize: '20px'}
                   },
                  xAxis: data['xAxis'],
                  yAxis: {
                       title: {
                           text: 'Number of households',
                           style: {fontSize: '15px' }
                       },
                       plotLines: [{
                           value: 0,
                           width: 1,
                           color: '#808080'
                       }],
                       style: {fontSize: '20px'}
                  },
                  tooltip:{
                          borderWidth:1,
                          formatter:function() {
                              var up = parseFloat(this.x) + data['binSize'];
                              up = up.toFixed(2);
                              return '<b>Range:</b><br/> '+ this.x +'<=x<'+up +'<br/>'+
                              '<b>Count:</b> '+ this.y;
                          }
                  },
                  plotOptions: {
                       series: {
                          events: {
                               legendItemClick: function () {
                                  toggleMarkers(this.index);
                               }
                          },
                          showInLegend: true
                       }
                  },
                  legend: {
                       layout: 'vertical',
                       align: 'center',
                       verticalAlign: 'bottom',
                       layout:'horizontal',
                       borderWidth: 0
                  },
                  series: data['heights']
              });
    };


    var markers =[];

    var characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';

     bindInfoWindow =  function(marker, map,   infowindow, html) {
            google.maps.event.addListener(marker, 'mouseover', function() {
                infowindow.setContent(html);
                infowindow.open(map, marker);
            });
           google.maps.event.addListener(marker, 'mouseout', function(){
                                                                        setTimeout(function(){
                                                                            infowindow.close();
                                                                            }, 2000);
                                                                       });
        };

   toggleMarkers = function(idx){
        var markersInCluster = markers[idx];
        for (var i=0; i<markersInCluster.length; ++i){
            markersInCluster[i].setVisible(!markersInCluster[i].getVisible());
        }
    };

    showClustersOnGoogleMap = function(data){
        var clusters = data['clusterOnMap'];
        var colors = data['colors'];
        var myOptions = {
            zoom: 12,
            center: new google.maps.LatLng(42.2323784095630, -83.0792537765400),
            mapTypeId: google.maps.MapTypeId.ROADMAP
        };
        map = new google.maps.Map(document.getElementById('map-cavans'), myOptions);
        markers = [];

        for (var i = 0; i < clusters.length; i++) {
            var markersInCluster = [];
            var pointsInCluster = clusters[i];
            var color = colors[i];
            var label = characters.charAt(i);
            for (var j=0; j<pointsInCluster.length; ++j){
                var point = pointsInCluster[j];
                var meterid = point['meterid'];
                var name = point['name'];
                var value = point['value'];
                var location = new google.maps.LatLng(point['x'], point['y']);
                var styleIconClass = new StyledIcon(StyledIconTypes.CLASS, {color:color});
                var marker = new StyledMarker({
                                                styleIcon:new StyledIcon(StyledIconTypes.MARKER,{text:label}, styleIconClass),
                                                position:location,
                                                map:map
                                             });
                markersInCluster.push(marker);
                map.setCenter(marker.getPosition())
               var infowindow = new google.maps.InfoWindow();
               var html = '<div style="width:200px; height:80px;"><p><a href="javascript:viewOthersEnergy('+meterid+');">View time-series</a></p>'+name+'='+value +'</div>';
               bindInfoWindow(marker, map, infowindow, html);
            }
            markers.push(markersInCluster);
        }
    };
    return {
        view:function(){
            sent['subCmd'] = $('#features option:selected').val();
            sent['distance'] = $('#distance option:selected').val();
            sent['ncluster'] = $('#ncluster option:selected').val();
            sent['startDate'] = $('#startDate').val();
            sent['endDate'] = $('#endDate').val();
            controller(sent, function(data){
                                showCentroids(data);
                                showClustersOnGoogleMap(data);
                            });
        }
    }
})();


var forcasting = (function(){
    var sent = {
        cmd: 'forcasting',
        subCmd:'',
        level:-1,
        value: -1,
        forcasttime:2,
        forcasttimeunit:-1
    };

    forcast_show = function(data){$('#forcast-cavans').highcharts({
            chart: {
                zoomType: 'x',
                type: 'line'
            },
            title: {
                text: 'Electricity consumption data'
            },
            subtitle: {
                text: document.ontouchstart === undefined ?
                    'Click and drag in the plot area to zoom in' :
                    'Pinch the chart to zoom in'
            },
            xAxis: {
                type: 'datetime',
               // tickInterval: 30*24*3600 * 1000
               // minRange: 3600*1000 // Cannot zoom in more than 1hour
               minRange: data['minRange'],
               dateTimeLabelFormats: {
                   second: '%H:%M:%S',
                   minute: '%H:%M',
                   hour: '%H:%M',
                   day: '%e. %b',
                   week: '%e. %b',
                   month: '%b \'%y', //month formatted as month only
                   year: '%Y'
               }
            },
            yAxis: {
                title: {
                    text: 'Energy Consumption'
                }
            },
             tooltip: {
                    crosshairs: true,
                    shared: true,
                    valueSuffix: 'kWh'
            		  },
            legend: {
                enabled: true
            },
            series: data['series']
        })};

    return {
        forcast:function(){
            sent['subCmd'] = $('#forcastmodel option:selected').val();
            sent['level'] = $('#level').val();
            sent['value'] = $('#value').val();
            sent['forcasttime'] = $('#forcasttime').val();
            sent['forcasttimeunit'] = $('#forcasttimeunit').val();
            controller(sent, function(data){
                             forcast_show(data);
                              });
        }
    }
})();


var consumptionpattern = (function(){
    var sent = {
        cmd: 'consumptionpattern',
        subCmd:'',
        level:-1,
        value: -1,
        measure:''
    };

    draw_centroid = function (k, title, max, data) {
        $('#cluster'+k).highcharts({
            title: {
                text: title +k,
                x: -20 //center
            },
            xAxis: {
                min:0,
                max:23,
                tickInterval:1,
                title: {
                        text: 'Hours of the Day'
                       },
            },
            yAxis: {
                title: {
                    text: 'Normalized Consumption'
                },
                plotLines: [{
                    value: 0,
                    width: 0.5,
                    color: '#808080'
                }],
                min:0,
                max:max
            },
            tooltip: {
                valueSuffix: ''
            },
             legend: {
                        enabled: false
                    },
            series: data
        });
    };

    draw_centroids =  function(data){
        $('#consumptionpattern-cavans').children().remove();
        var cents = data['series'];
        var rowid = 0;
        for (var i=0; i<cents.length; ++i){
            var r = i%4;
            if (r == 0){
                $('#consumptionpattern-cavans').append("<div id='r"+rowid+"'></div>");
                rowid += 1;
            }
           $('#r'+(rowid-1)).append("<div id='cluster"+i+"' class='col-md-3'></div>");
           draw_centroid(i, data['title'], data['max'], cents[i]);
        }
    };

    return {
        view:function(){
            sent['subCmd'] = $('#subCmd option:selected').val();
            sent['measure'] = $('#measure option:selected').val();
            controller(sent, function(data){
                             draw_centroids(data);
                              });
        }
    }
})();


var segmentation = (function(){
    var sent = {
        cmd: 'consumptionpattern',
        subCmd:'',
        level:-1,
        value: -1,
        measure:''
    };

    showConsumptionPatternCentroids = function (data) {
             $('#consumptionpattern-cavans').highcharts({
                  title: {
                       text: 'Consumption Patterns',
                       x: -20,
                       style: {fontSize: '20px'}
                   },
                 xAxis: {
                             min:0,
                             max:23,
                             tickInterval:1,
                             title: {
                                     text: 'Hours of the Day'
                                    },
                         },
                 yAxis: {
                     title: {
                         text: 'Normalized Consumption'
                     },
                     plotLines: [{
                         value: 0,
                         width: 0.5,
                         color: '#808080'
                     }],
                     min:0,
                     max:data['max']
                 },
                 tooltip: {
                     valueSuffix: ''
                 },
                 series: data['series']
              });
    };


    var markers =[];

    var characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';

     bindInfoWindow =  function(marker, map,   infowindow, html) {
            google.maps.event.addListener(marker, 'mouseover', function() {
                infowindow.setContent(html);
                infowindow.open(map, marker);
            });
           google.maps.event.addListener(marker, 'mouseout', function(){
                                                                        setTimeout(function(){
                                                                            infowindow.close();
                                                                            }, 2000);
                                                                       });
        };
   toggleMarkers = function(idx){
        var markersInCluster = markers[idx];
        for (var i=0; i<markersInCluster.length; ++i){
            markersInCluster[i].setVisible(!markersInCluster[i].getVisible());
        }
    };

    showClusteredMetersOnMap = function(data){
        var clusters = data['clusterOnMap'];
        var colors = data['colors'];
        var myOptions = {
            zoom: 12,
            center: new google.maps.LatLng(42.2323784095630, -83.0792537765400),
            mapTypeId: google.maps.MapTypeId.ROADMAP
        };
        map = new google.maps.Map(document.getElementById('map-cavans'), myOptions);
        markers = [];

        for (var i = 0; i < clusters.length; i++) {
            var markersInCluster = [];
            var pointsInCluster = clusters[i];
            var color = colors[i];
            var label = characters.charAt(i);
            for (var j=0; j<pointsInCluster.length; ++j){
                var point = pointsInCluster[j];
                var name = point['name']
                var location = new google.maps.LatLng(point['x'], point['y']);
                var styleIconClass = new StyledIcon(StyledIconTypes.CLASS, {color:color});
                var marker = new StyledMarker({
                                                styleIcon:new StyledIcon(StyledIconTypes.MARKER,{text:label}, styleIconClass),
                                                position:location,
                                                map:map
                                             });
                markersInCluster.push(marker);
                map.setCenter(marker.getPosition())
               var infowindow = new google.maps.InfoWindow();
               var html = '<div style="width:200px; height:80px;">'+name+'</div>';
               bindInfoWindow(marker, map, infowindow, html);
            }
            markers.push(markersInCluster);
        }
    };

    return {
          view:function(){
                    sent['subCmd'] = $('#subCmd option:selected').val();
                    sent['measure'] = $('#measure option:selected').val();
                    controller(sent, function(data){
                        showConsumptionPatternCentroids(data);
                        showClusteredMetersOnMap(data);
                    });
                }
    }
})();



//////////////////////////////////////////////////////////

var WaterLoadAnalysis = (function(){
    var sent = {
        cmd: 'waterloadanalysis',
        subCmd:'',
        custid: -1,
        exovar:'',
        typenames:null,
        subtypeids:null,
        timelevel:'',
        custidstart:1
    };

    populateCustomers = function(data){
        var custs = data['customers'];
        $("#type").multiselect("enable");
        $("#subtype").multiselect("enable");
        $('#cust').find('option:last').remove();
        for (var i=0; i<custs.length; ++i){
            var custno = custs[i][1];
            var selected = '';
            if (i==custs.length-1){
                custno = '...';
            }
            $('#cust').append($("<option></option>").attr("value",custs[i][0]).text(custno));
        }
    };

    var types = null;
    populateTypes = function(data){
        var typeNames = data['typenames'];
        types = data['types'];
        var typeOptions = [];
        for (var i=0; i<typeNames.length; ++i){
            typeOptions.push({label: typeNames[i], value: typeNames[i]});
            //options.push({label: types[i][1], value: types[i][0]});
        }
        $("#type").multiselect('dataprovider', typeOptions);
    };
    populateSubCustType = function(){
         var subTypeOptions = [];
         $('#type :selected').each(function(i, selected) {
             var typename = $(selected).text();
             var subtypes = types[typename];
              for (var i=0; i<subtypes.length; ++i){
                subTypeOptions.push({label: subtypes[i][1], value: subtypes[i][0]});
              }
         });
         $("#subtype").multiselect('dataprovider', subTypeOptions);
    };
    showLoadProfile = function(data){$('#timeseries-cavans').highcharts({
            chart: {
                zoomType: 'x',
                type: 'line'
            },
            title: {
                text: data['title']
            },
            subtitle: {
                text: document.ontouchstart === undefined ?
                    'Click and drag in the plot area to zoom in' :
                    'Pinch the chart to zoom in'
            },
            xAxis: {
                type: 'datetime',
               // tickInterval: 30*24*3600 * 1000
               // minRange: 3600*1000 // Cannot zoom in more than 1hour
               minRange: data['minRange'],
               dateTimeLabelFormats: {
                   second: '%H:%M:%S',
                   minute: '%H:%M',
                   hour: '%H:%M',
                   day: '%e. %b',
                   week: '%e. %b',
                   month: '%b \'%y', //month formatted as month only
                   year: '%Y'
               }
            },
            yAxis: {
                title: {
                    text: 'Consumption, m3'
                }
            },
             tooltip: {
                    crosshairs: true,
                    shared: true,
                    valueSuffix: 'm3'
                      },
            legend: {
                enabled: true
            },
            plotOptions: {
                area: {
                   /* fillColor: {
                        linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1},
                        stops: [
                            [0, Highcharts.getOptions().colors[0]],
                            [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                        ]
                    },*/
                    marker: {
                        radius: 2
                    },
                    lineWidth: 1,
                    states: {
                        hover: {
                            lineWidth: 1
                        }
                    },
                    threshold: null
                }
            },
            series: data['series']
        })};

    showLoadProfileByExoVar = function (data) {
        $('#timeseries-cavans').highcharts({
            title: {
                text: 'Average Daily Water Consumption of Customers',
                x: -20 //center
            },
            xAxis: {
                 title: {
                      text: data['xTitle']
                 },
                min:data['xRange'][0],
                max:data['xRange'][1],
                tickInterval:1
            },
            yAxis: {
                title: {
                    text: 'Average daily consumption, m3'
                },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {
                valueSuffix: 'm3'
            },
            legend: {
                layout: 'vertical',
                align: 'center',
                verticalAlign: 'bottom',
                layout:'horizontal',
                borderWidth: 0
            },
            series: data['series']
        });
    };

    return {
        populateAll:function(){
            sent['subCmd'] = 'getCustomers';
            sent['custidstart'] = 1;
            controller(sent, populateCustomers);
            sent['subCmd'] = 'getTypes';
            controller(sent, populateTypes);
        },
        populateCustType:function(){
            sent['subCmd'] = 'getTypes';
            controller(sent, populateTypes);
        },

        populateSubCustType:populateSubCustType,

        getMoreCustomers:function(){
            sent['subCmd'] = 'getCustomers';
            sent['custidstart'] = $('#cust').find('option:last').val();
            controller(sent, populateCustomers);
        },
        viewConsumption:function(){
            sent['subCmd'] = 'getLoadProfile';
            sent['custid'] = $('#cust option:selected').val();
            var typenames = [];
            var subtypeids = [];
            $('#type :selected').each(function(i, selected) {
                typenames[i] = $(selected).text();
            });
            $('#subtype :selected').each(function(i, selected) {
                subtypeids[i] = $(selected).val();
            });
            sent['typenames'] =  typenames.join(',');
            sent['subtypeids'] =  subtypeids.join(',');
            sent['timelevel'] = $('#timelevel option:selected').val();
            controller(sent, showLoadProfile);
        },
        viewConsumptionByExoVar:function(){
            sent['subCmd'] = 'getLoadProfileByExoVar';
             var typenames = [];
            var subtypeids = [];
            $('#type :selected').each(function(i, selected) {
                typenames[i] = $(selected).text();
            });
            $('#subtype :selected').each(function(i, selected) {
                subtypeids[i] = $(selected).val();
            });
            sent['typenames'] =  typenames.join(',');
            sent['subtypeids'] =  subtypeids.join(',');
            sent['exovar'] = $('#exovar option:selected').val();
            controller(sent, showLoadProfileByExoVar);
        },
        viewConsumptionProfilingWithPRAX:function(){
            sent['subCmd'] = 'getLoadProfileWithPARX';
            sent['typenames'] =  $('#type option:selected').val();
            controller(sent, showLoadProfile);
        }
    }
})();

var feedbackService = (function(){
    var sent = {
            cmd: 'feedbackservice',
            subCmd:'',
            ruleID:'',
            ruleName: '',
            typeID: -1,
            params:'',
            typeName:'',
            recvIDs: '',
            recvValues: '',
            message: '',
            nextStartTime: '',
            powerFlag: 1,
            repeatInterval: ''
    };

    createFeedback = function(data){
        var rule = data['rule'];
        var newRowContent = "<tr id='rule"+rule["ruleID"]+"'>" +
                           "<td>"+rule["ruleID"]+"</td>" +
                           "<td>"+rule["ruleName"]+"</td>" +
                           "<td>"+rule["typeName"]+"</td>" +
                           "<td>"+rule["recvValues"]+"</td>" +
                           "<td>"+rule["nextStartTime"]+"</td>" +
                           "<td>"+rule["repeatInterval"]+"</td>" +
                           "<td><div class='toggle-switch toggle-switch-success' onchange='javascript:feedbackService.toggleEnable("+rule["ruleID"]+")'>" +
                               "<label>"  +
                                   "<input type='checkbox' unchecked>"  +
                                   "<div class='toggle-switch-inner'></div>" +
                                   "<div class='toggle-switch-switch'><i class='fa fa-check'></i></div>" +
                               "</label>" +
                           "</div>" +
                           "</td>" +
                           "<td>" +
                               "<button type='button' class='btn btn-sm btn-danger' onclick='javascript:feedbackService.removeRule("+rule["ruleID"]+");'>Delete</button>" +
                           "</td>" +
                       "</tr>";
        $("#feedbacks tbody").append(newRowContent);
    };

    showFormAndCreatedRules = function(data){
        var ruleTypes = data['ruleTypes'];
        if (ruleTypes!=null && ruleTypes.length>0){
            $.each(ruleTypes, function(index, ruleType) {
               $('#typeID').append("<option value='"+ruleType.typeID+"'>"+ruleType.name+"</option>");
            });
        }
        var users = data['users'];
        if (users!=null && users.length>0){
            var options = [];
            for (var i=0; i<users.length; ++i){
                options.push({label: users[i].username, value: users[i].userID});
            }
            $("#username").multiselect('dataprovider', options);
        }

        var rules = data["rules"];
        for (var i=0; i<rules.length; ++i){
            var rule = rules[i];
            var chkFlag = 'unchecked';
            if (rule['enableFlag']==1){
                chkFlag = 'checked';
            }
            var newRowContent = "<tr id='rule"+rule["ruleID"]+"'>" +
                   "<td>"+rule["ruleID"]+"</td>" +
                   "<td>"+rule["ruleName"]+"</td>" +
                   "<td>"+rule["typeName"]+"</td>" +
                   "<td>"+rule["recvValues"]+"</td>" +
                   "<td>"+rule["nextStartTime"]+"</td>" +
                   "<td>"+rule["repeatInterval"]+"</td>" +
                   "<td><div class='toggle-switch toggle-switch-success' onchange='javascript:feedbackService.toggleEnable("+rule["ruleID"]+")'>" +
                       "<label>"  +
                           "<input type='checkbox' "+chkFlag+">"  +
                           "<div class='toggle-switch-inner'></div>" +
                           "<div class='toggle-switch-switch'><i class='fa fa-check'></i></div>" +
                       "</label>" +
                   "</div>" +
                   "</td>" +
                   "<td>" +
                       "<button type='button' class='btn btn-sm btn-danger' onclick='javascript:feedbackService.removeRule("+rule["ruleID"]+");'>Delete</button>" +
                   "</td>" +
               "</tr>";
            $("#feedbacks tbody").append(newRowContent);
        }
    };

    createForm = function(data){
        $("#formtemplate").children().remove();
        $("#formtemplate").append( data['formtemplate'] );
    };

    removeFeedback = function(data){
       $('#rule'+sent['ruleID']).remove();
    };

    return {
        showFormAndRules: function(){
            sent['subCmd'] = 'read';
            controller(sent, showFormAndCreatedRules);
        },
        changeRuleType:function(){
            sent['subCmd'] = 'readForm';
            sent['typeID'] = $('#typeID option:selected').val();
            controller(sent, createForm);
        },
        addRule: function(){
                sent['subCmd'] = 'create';
                sent['ruleName'] = $('#ruleName').val();
                sent['typeID'] = $('#typeID option:selected').val();
                sent['typeName'] = $('#typeID option:selected').text();
                var recvIDs = [];
                var recvValues = [];
                $('#username :selected').each(function(i, selected) {
                     recvIDs[i] = $(selected).val();
                     recvValues[i] = $(selected).text();
                });
                var params = $('.param').map( function(){return $(this).val(); }).get();
                sent['params'] = params.join(';');
                sent['recvIDs'] =  recvIDs.join(';');
                sent['recvValues'] =  recvValues.join(';');
                sent['message'] = $('#message').val();
                sent['powerFlag'] = 1;
                sent['nextStartTime'] = $('#nextStartTime').val();
                sent['repeatInterval'] = $('#repeatInterval').val();
                controller(sent, createFeedback);
        },
        removeRule:function(ruleid){
            sent['subCmd'] = 'delete';
            sent['ruleID'] = ruleid;
            controller(sent, removeFeedback);
        },
        toggleEnable:function(ruleid){
            sent['subCmd'] = 'update';
            sent['ruleID'] = ruleid;
            controller(sent, function(data){});
        }
    }
})();


var message = (function(){
    var sent = {
            cmd: 'message',
            subCmd:'',
            msgID:-1
    };

    dispMessage = function(data){
        var messages = data['messages'];
        var msgIDs = data['msgIDs'];
        var msgIcons = data['msgIcons'];
        var msgBoxes = data['msgBoxes'];
        for (var i=0; i<msgIDs.length; ++i){
            var newMessageMenu =   "<li>" +
                                   "     <a href='index.html' class='' id='"+msgIDs[i]+"'>" +
                                   "         <i class='"+msgIcons[i]+"'></i>" +
                                   "         <span class='hidden-xs'>"+msgBoxes[i]+"</span>" +
                                   "     </a>" +
                                   "</li>";
            $("#messages-menu").append(newMessageMenu);
        }

        for (var i=0; i<messages.length; ++i){
            var msg = messages[i];
            var newMessageContent = "<div class='row one-list-message msg-"+msg['labelName']+"-item' id='msg-"+msg['msgID']+"'>" +
                                "     <div class='col-xs-2 checkbox'>" +
                                "       <label>" +
                                "           <input type='checkbox'><b>From: </b>"+ msg['senderName'] +
                                "           <i class='fa fa-square-o small'></i>" +
                                "       </label>" +
                                "    </div>" +
                                "    <div class='col-xs-5 message-title'><b>Subject: </b>"+msg['title']+"</div>" +
                                "    <div class='col-xs-5 message-date'>"+msg['sendTime']+"</div>" +
                                "</div>" +
                                "<div class='row one-list-message msg-"+msg['msgID']+"-item' style='display: none;'>" +
                                "    <div class='box'>" +
                                "        <div class='midicon'>" +
                                "			<img src='img/exclamation.jpg'/>" +
                                "		</div>" +
                                "        <div class='page-feed-content'>" +
                                "            <small class='time'>"+msg['senderName']+"</small>" +
                                "            <p>"+msg['content']+"</p>" +
                                "        </div>" +
                                "    </div>" +
                                "</div>";
            $("#messages-list").append(newMessageContent);
        }

    };

    removeMessage = function(data){
       $('#rule'+sent['msgID']).remove();
    };

    return {
        dispMsg: function(){
            sent['subCmd'] = 'read';
            controller(sent, dispMessage);
        },
        removeMsg:function(msgID){
            sent['subCmd'] = 'delete';
            sent['msgID'] = msgID;
            controller(sent, removeMessage);
        }
    }
})();
