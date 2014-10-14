
 function render_usersettings(name, data){
    $("#parapanel").remove();
    $.get("http://" + location.host + context + "/alg/"+name+".html", function(htmlform){
        $( ".panel-group").append(htmlform);
        $.each(data, function(index, value) {
            if (index==0){
                $('#homeid').append("<option 'selected'>"+value+"</option>");
            } else{
                $('#homeid').append("<option >"+value+"</option>");
            }
        });
    });
}


var threelines = (function(){
    var sent = {cmd: 'threelines', subCmd:'', homeid:-1, dbtype:'postgresql'};

    threel_form = function(data){
                  $("#parapanel").remove();
                  $.get("http://" + location.host + context + "/alg/threelines.html", function(htmlform){
                      $( ".panel-group").append(htmlform);
                      $.each(data['homeids'], function(index, value) {
                          if (value==data['selectedhomeid']){
                              $('#homeid').append("<option 'selected'>"+value+"</option>");
                          } else{
                              $('#homeid').append("<option >"+value+"</option>");
                          }
                      });
                  });
              };

    draw_threel = function(data){
                $('#cavans').highcharts({
                    xAxis: {
                        min: -15,
                        max: 35,
                        title: {text: 'Weather Temperature'}
                    },
                    yAxis: {
                        min: 0,
                        title: {text: 'Energy Consumption'}
                    },
                    title: {
                        text: 'Three lines model'
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
        getAll:function(){
                    sent['subCmd'] = 'getAll';
                     sent['homeid'] = -1;
                    controller(sent, function(data){
                                    threel_form(data);
                                    draw_threel(data);
                                 });
        },
        getOne: function(){
             sent['subCmd'] = 'getOne';
             sent['homeid'] = $('#homeid').val();
             controller(sent, draw_threel);
        },
        getOneHID:function(homeid){
            sent['subCmd'] = 'getAll';
            sent['homeid'] = homeid;
            controller(sent, function(data){
                    threel_form(data);
                    draw_threel(data);
            });
        }
    }
})();



var timeseries = (function(){
    var sent = {cmd: 'timeseries', subCmd:'', meterid:-1, dbtype:'postgresql'};

    draw_timeseries = function(data){$('#cavans').highcharts({
                           				chart: {
                           					zoomType: 'x'
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
                           					minRange: 3600*1000 // Cannot zoom in more than 1hour
                           				},
                           				yAxis: {
                           					title: {
                           						text: 'Energy Consumption'
                           					}
                           				},
                           				legend: {
                           					enabled: false
                           				},
                           				plotOptions: {
                           					area: {
                           						fillColor: {
                           							linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1},
                           							stops: [
                           								[0, Highcharts.getOptions().colors[0]],
                           								[1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                           							]
                           						},
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
                           				series: data
                           			})};

    return {
        getHomeIDsAndReadings:function(){
            sent['subCmd'] = 'getHomeIDsAndReadings';
            controller(sent, function(data){
                             render_usersettings('timeseries', data[0]);
                             draw_timeseries([data[1]]);
                        })
        },

        getReadingsByID:function(){
            sent['subCmd'] = 'getReadingsByID';
            sent['meterid'] = $('#meterid').val();
            controller(sent, draw_timeseries);
        }
    }
})();


var histogram = (function(){
    var sent = {cmd: 'histogram', subCmd:'', meterid:-1, nbuckets:20,  dbtype:'postgresql'};

    draw_histogram = function(data){$('#cavans').highcharts({
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
            title:{text:'Histogram'},
            legend:{
                //enabled:false
            },
            tooltip:{
                borderWidth:1,
                formatter:function() {
                    var up = parseFloat(this.x) + data['options'];
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
        getOneAndHomeIDs:function(){
            sent['subCmd'] = 'getOneAndHomeIDs';
            controller(sent, function(data){
                render_usersettings('histogram', data[0]);
                draw_histogram(data[1]);
            })
        },


        getOne:function(){
            sent['subCmd'] = 'getOne';
            sent['meterid'] = $('#meterid option:selected').text();
            sent['nbuckets'] = $('#nbuckets option:selected').text();
            controller(sent, draw_histogram);
        }
    }
})();



var parx = (function(){
    var sent = {cmd: 'parx', subCmd:'', meterid:-1, startDate:'2008-07-01', endDate:'2020-12-31', order:3,  dbtype:'postgresql'};

    parx_form = function (data){
        $("#parapanel").remove();
        $.get("http://" + location.host + context + "/alg/parx.html", function(htmlform){
            $( ".panel-group").append(htmlform);
            $.each(data['meterids'], function(index, value) {
                if (index==0){
                    $('#meterid').append("<option 'selected'>"+value+"</option>");
                } else{
                    $('#meterid').append("<option >"+value+"</option>");
                }
            });
            var startEndDate = data['startenddate'];
            $('#startDate').val(startEndDate['startdate']);
            $('#endDate').val(startEndDate['enddate']);
        });
    };

    draw_parx = function (data) {
                        $('#cavans').highcharts({
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
        getAll:function(){
            sent['subCmd'] = 'getAll';
            controller(sent, function(data){
                parx_form(data);
                draw_parx(data);
            })
        },
        getOne:function(){
            sent['subCmd'] = 'getOne';
            sent['meterid'] = $('#meterid option:selected').text();
            sent['startDate'] = $('#startDate').val();
            sent['endDate'] = $('#endDate').val();
            controller(sent, function(data){
                                var startEndDate = data['startenddate'];
                                $('#startDate').val(startEndDate['startdate']);
                                $('#endDate').val(startEndDate['enddate']);
                                draw_parx(data);
                              });
        }
    }
})();


var clustering = (function(){
    var sent = {cmd: 'threelines', subCmd:'', selectedfeatures:'', distance:'dist_norm2', ncluster:4, dbtype:'postgresql'};

    clustering_form = function (){
                      $("#parapanel").remove();
                      $.get("http://" + location.host + context + "/alg/clustering.html", function(htmlform){
                          $( ".panel-group").append(htmlform);
                      });
    };

    clustering_draw = function (data) {
             $('#cavans').highcharts({
                  chart: {
                      type: 'scatter',
                      zoomType: 'xy'
                  },
                  title: {
                      text: 'Clustering'
                  },
                  xAxis: {
                      title: {
                          enabled: true,
                          text: ''
                      },
                      startOnTick: true,
                      endOnTick: true,
                      showLastLabel: true
                  },
                  yAxis: {
                      title: {
                          text: ''
                      }
                  },
                  legend: {
                      layout: 'vertical',
                      align: 'center',
                      verticalAlign: 'bottom',
                      layout:'horizontal',
                      borderWidth: 0,
                    //  floating: true,
                      backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'
                  },

                  series: data['series'],
                  tooltip: {
                            headerFormat: '<b>{series.name}</b><br>',
                            useHTML:true,
                           // pointFormat: '{point.x, point.y}'
                           formatter: function() {return ' ' +
                                           'x: ' + this.point.x + '<br />' +
                                           'y: ' + this.point.y + '<br />' +
                                           '<a href="javascript: threelines.getOneHID('+this.point['meterid']+');">meterid: ' + this.point['meterid'] +'</a>';
                                       }
                  }
              });
    };


    return {
        showForm: clustering_form,
        getClusters:function(){
            sent['subCmd'] = 'getClusters';
            if( $('#features :selected').length > 0){
                var selectedfeatures = [];
                $('#features :selected').each(function(i, selected) {selectedfeatures[i] = $(selected).val()});
                sent['selectedfeatures'] = JSON.stringify(selectedfeatures);
                sent['distance'] = $('#distance option:selected').val();
                sent['ncluster'] = $('#ncluster option:selected').text();
                controller(sent, clustering_draw);
            }
        }
    }
})();


