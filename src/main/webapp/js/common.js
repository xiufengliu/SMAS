var context = '/smas';

function controller(sent, callback){
    $.ajax({
        dataType: 'json',
        type : 'post',
        url: 'http://' + location.host + context + '/servlet',
        data: sent,
        success: function(data){
            $("#errmsg").remove();
            var err = data['errmsg'];
            if(err!=undefined){
                $("#errmsg-content" ).append("<div id='errmsg' class='alert alert-danger' data-dismiss='alert'>"+data['errmsg']+"</div>");
            } else{
                callback(data);
            }

        }
    });
}


var customerMgnt = (function(){
    var sent = {cmd: 'customermgnt', subCmd:'', meterid:-1, dbtype:'postgresql'};

    showValues = function(data)
    {

         $('#valueSelector option').each(function() {
                 if ( $(this).val() != -1 ) {
                      $(this).remove();
                  }
             });
        var optionValues = data['values'];
        if (optionValues!=null && optionValues.length>0){
            $.each(optionValues, function(index, value) {
                    $('#valueSelector').append("<option value='"+value+"'>"+value+"</option>");
                  });
        }
    };

    showValuesInMultiSelect = function(data)
    {
         $('#valueSelector option').each(function() {
                 if ( $(this).val() != -1 ) {
                      $(this).remove();
                  }
             });

        var optionValues = data['values'];
        if (optionValues!=null && optionValues.length>0){
            var options = [];
            for (var i=0; i<optionValues.length; ++i){
                options.push({label: optionValues[i], value: optionValues[i]});
            }
            $("#valueSelector").multiselect('dataprovider', options);
        }
    };


    return {
        getValues:function(subCmd){
                sent['subCmd'] = subCmd;
                controller(sent, showValues);
        },
        showValuesInMultiSelect:function(subCmd){
                        sent['subCmd'] = subCmd;
                        controller(sent, showValuesInMultiSelect);
                }
    }
})();






var googlemap = (function(){
    var sent = {cmd: 'meterloc', subCmd:'', radius:-1};

    //var geocoder;
    var markers = [];
    var selectedMarkers = [];
    var map;
    var colors = {
            me:"#9BC6EF",
            selected: "#FFFF70",
            unselected: "#7CBD6B"
        };
    var labels = {
        me:"A",
        selected:"B",
        unselected:"B"
    };
    var myID = -1;
    /*var unselectedImage = {
        url: "img/meter2.png",
        size: new google.maps.Size(28,54),
        origin: new google.maps.Point(0,0),
        anchor: new google.maps.Point(14,54)
    };*/


    //var styleIconClass = new StyledIcon(StyledIconTypes.CLASS,{color:"#ff0000"});
        var characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        getRandomColor = function() {
            var letters = '0123456789ABCDEF'.split('');
            var color = '#';
            for (var i = 0; i < 6; i++ ) {
                color += letters[Math.floor(Math.random() * 16)];
             }
            return color;
        };

     showMeters = function(data)
    {
        //alert(locations.length);
        var userInfo = data['userinfo'];
        var locations = data['locations'];
        myID = userInfo.mymeterids[0];
        var meterids = data['meterids'];
       // geocoder = new google.maps.Geocoder();
        //var myloc = new google.maps.LatLng(userInfo.latitude, userInfo.longtitude);
        var myOptions = {
            zoom: 18,
          //  center: myloc,
            mapTypeId: google.maps.MapTypeId.ROADMAP
        };

        map = new google.maps.Map(document.getElementById('map-cavans'), myOptions);
        for (var i = 0; i < meterids.length; i++) {
            var loc = new google.maps.LatLng(locations[i][0], locations[i][1]);
            if (meterids[i]!=myID){
                makeMarker(meterids[i], loc, labels.unselected, colors.unselected);
            } else {
                makeMarker(meterids[i], loc, labels.me, colors.me);
                map.setCenter(loc);
            }
        }
    };

    showClusters = function(data){
       // var userInfo = data['userinfo'];
        var clusters = data['clusterOnMap'];
        //myID = userInfo.meterid;
       // geocoder = new google.maps.Geocoder();
        var myloc = new google.maps.LatLng(42.2323784095630, -83.0792537765400);
        var myOptions = {
            zoom: 18,
            center: myloc,
            mapTypeId: google.maps.MapTypeId.ROADMAP
        };
        map = new google.maps.Map(document.getElementById('map-cavans'), myOptions);
        for (var i = 0; i < clusters.length; i++) {
            var pointsInCluster = clusters[i];
            var color = getRandomColor();
            var label = characters.charAt(i);
            for (var j=0; j<pointsInCluster.length; ++j){
                var point = pointsInCluster[j];
                var loc = new google.maps.LatLng(point['x'], point['y']);
                makeMarker(point['meterid'], loc, label, color);
            }
        }
       // makeMarker(myID, myloc, labels.me, colors.me);
        map.setCenter(myloc);
    };

    makeMarker = function (meterid, location, label, color)
    {
        //geocoder.geocode( { 'address': location}, function(results, status) {
          //  if (status == google.maps.GeocoderStatus.OK) {
                //map.setCenter(results[0].geometry.location);
                var meterid = meterid;
                var styleIconClass = new StyledIcon(StyledIconTypes.CLASS, {color:color});
                var marker = new StyledMarker({
                                styleIcon:new StyledIcon(StyledIconTypes.MARKER,{text:label}, styleIconClass),
                                position:location,
                                map:map
                            });

               /* var marker = new google.maps.Marker({
                    map: map,
                    icon: unselectedImage,
                    position: location
                });*/

                markers.push(marker);

                //alert(results[0].formatted_address);
                var contentString = 'Smart Meter';
                var who =  (meterid==myID?"Me-":"Meter-");

                var infowindow = new google.maps.InfoWindow({
                    content: '<a href="javascript: threelines.getOneHID('+meterid+');">'+who+meterid+'</a>'
                });

                google.maps.event.addListener(marker, 'click', function() {
                    infowindow.open(map,this);
                    if (meterid!=myID){
                        var index = -1;
                         for (var i=0; i<selectedMarkers.length; ++i){
                            if (selectedMarkers[i].marker==this) {
                                index = i;
                                break;
                            }
                        }
                        if (index>-1){
                            selectedMarkers.splice(index, 1);
                            styleIconClass.set("color", colors.unselected);
                        } else {
                            selectedMarkers.push({meterid:meterid, marker:this});
                            styleIconClass.set("color",colors.selected);
                        }
                    }
                });

            google.maps.event.addListener(marker, 'mouseover', function(){infowindow.open(map,this);});
            google.maps.event.addListener(marker, 'mouseout', function(){infowindow.close();});

            var zoomScale = 6;
            google.maps.event.addListener(marker, 'dblclick', function() {
                                                    zoomScale += 2;
                                                    map.setZoom(zoomScale);
                                                    map.setCenter(marker.getPosition());
                                                });
    };


    return {
        showMetersWithinRadius:function(){
                //initialize();
                sent['subCmd'] = 'getMeterLoc';
                sent['radius'] = 10;
                controller(sent, showMeters);
            },
        showClusters:showClusters,
        getSelectedIDs:function(){
            var IDs = [];
            for (var i=0; i<selectedMarkers.length; ++i){
                IDs.push(selectedMarkers[i].meterid);
            }
            return IDs;
        },
        resetMarkers:function(){
           while(selectedMarkers.length > 0) {
               selectedMarkers.pop();
           }
        }
    }
})();

//////////////////////////////////////////////////////
//////////////////////////////////////////////////////
//
//      MAIN DOCUMENT READY SCRIPT OF DEVOOPS THEME
//
//      In this script main logic of theme
//
//////////////////////////////////////////////////////
//////////////////////////////////////////////////////
function LoadAjaxContent(url){
	$('.preloader').show();
	$.ajax({
		mimeType: 'text/html; charset=utf-8', // ! Need set mimeType only when run from local file
		url:  url,
		type: 'GET',
		success: function(data) {
			$('#ajax-content').html(data);
			$('.preloader').hide();
		},
		error: function (jqXHR, textStatus, errorThrown) {
			alert(errorThrown);
		},
		dataType: "html",
		async: false
	});
}

//
//  Dynamically load  jQuery Timepicker plugin
//  homepage: http://trentrichardson.com/examples/timepicker/
//
function LoadTimePickerScript(callback){
	if (!$.fn.timepicker){
		$.getScript('plugins/jquery-ui-timepicker-addon/jquery-ui-timepicker-addon.min.js', callback);
	}
	else {
		if (callback && typeof(callback) === "function") {
			callback();
		}
	}
}

function LoadDataTablesScripts(callback){
	function LoadDatatables(){
		$.getScript('plugins/datatables/jquery.dataTables.js', function(){
			$.getScript('plugins/datatables/ZeroClipboard.js', function(){
				$.getScript('plugins/datatables/TableTools.js', function(){
					$.getScript('plugins/datatables/dataTables.bootstrap.js', callback);
				});
			});
		});
	}
	if (!$.fn.dataTables){
		LoadDatatables();
	}
	else {
		if (callback && typeof(callback) === "function") {
			callback();
		}
	}
}

//
//  Function maked all .box selector is draggable, to disable for concrete element add class .no-drop
//
function WinMove(){
	$( "div.box").not('.no-drop')
		.draggable({
			revert: true,
			zIndex: 2000,
			cursor: "crosshair",
			handle: '.box-name',
			opacity: 0.8
		})
		.droppable({
			tolerance: 'pointer',
			drop: function( event, ui ) {
				var draggable = ui.draggable;
				var droppable = $(this);
				var dragPos = draggable.position();
				var dropPos = droppable.position();
				draggable.swap(droppable);
				setTimeout(function() {
					var dropmap = droppable.find('[id^=map-]');
					var dragmap = draggable.find('[id^=map-]');
					if (dragmap.length > 0 || dropmap.length > 0){
						dragmap.resize();
						dropmap.resize();
					}
					else {
						draggable.resize();
						droppable.resize();
					}
				}, 50);
				setTimeout(function() {
					draggable.find('[id^=map-]').resize();
					droppable.find('[id^=map-]').resize();
				}, 250);
			}
		});
}

function MessagesMenuWidth(){
	var W = window.innerWidth;
	var W_menu = $('#sidebar-left').outerWidth();
	var w_messages = (W-W_menu)*16.666666666666664/100;
	$('#messages-menu').width(w_messages);
}

$(document).ready(function () {
	$('.show-sidebar').on('click', function () {
		$('div#main').toggleClass('sidebar-show');
		setTimeout(MessagesMenuWidth, 250);
	});
	var ajax_url = location.hash.replace(/^#/, '');
	if (ajax_url.length < 1) {
		ajax_url = 'ajax/dashboard.html';
	}

	//LoadAjaxContent(ajax_url);

	$('.main-menu').on('click', 'a', function (e) {
		var parents = $(this).parents('li');
		var li = $(this).closest('li.dropdown');
		var another_items = $('.main-menu li').not(parents);
		another_items.find('a').removeClass('active');
		another_items.find('a').removeClass('active-parent');
		if ($(this).hasClass('dropdown-toggle') || $(this).closest('li').find('ul').length == 0) {
			$(this).addClass('active-parent');
			var current = $(this).next();
			if (current.is(':visible')) {
				li.find("ul.dropdown-menu").slideUp('fast');
				li.find("ul.dropdown-menu a").removeClass('active')
			}
			else {
				another_items.find("ul.dropdown-menu").slideUp('fast');
				current.slideDown('fast');
			}
		}
		else {
			if (li.find('a.dropdown-toggle').hasClass('active-parent')) {
				var pre = $(this).closest('ul.dropdown-menu');
				pre.find("li.dropdown").not($(this).closest('li')).find('ul.dropdown-menu').slideUp('fast');
			}
		}
		if ($(this).hasClass('active') == false) {
			$(this).parents("ul.dropdown-menu").find('a').removeClass('active');
			$(this).addClass('active')
		}
		if ($(this).hasClass('ajax-link')) {
			e.preventDefault();
			if ($(this).hasClass('add-full')) {
				$('#content').addClass('full-content');
			}
			else {
				$('#content').removeClass('full-content');
			}
			var url = $(this).attr('href');
			window.location.hash = url;
			LoadAjaxContent(url);
		}
		if ($(this).attr('href') == '#') {
			e.preventDefault();
		}
	});
	var height = window.innerHeight - 49;
	$('#main').css('min-height', height)
		.on('click', '.expand-link', function (e) {
			var body = $('body');
			e.preventDefault();
			var box = $(this).closest('div.box');
			var button = $(this).find('i');
			button.toggleClass('fa-expand').toggleClass('fa-compress');
			box.toggleClass('expanded');
			body.toggleClass('body-expanded');
			var timeout = 0;
			if (body.hasClass('body-expanded')) {
				timeout = 100;
			}
			setTimeout(function () {
				box.toggleClass('expanded-padding');
			}, timeout);
			setTimeout(function () {
				box.resize();
				box.find('[id^=map-]').resize();
			}, timeout + 50);
		})
		.on('click', '.collapse-link', function (e) {
			e.preventDefault();
			var box = $(this).closest('div.box');
			var button = $(this).find('i');
			var content = box.find('div.box-content');
			content.slideToggle('fast');
			button.toggleClass('fa-chevron-up').toggleClass('fa-chevron-down');
			setTimeout(function () {
				box.resize();
				box.find('[id^=map-]').resize();
			}, 50);
		})
		.on('click', '.close-link', function (e) {
			e.preventDefault();
			var content = $(this).closest('div.box');
			content.remove();
		});

	$('body').on('click', 'a.close-link', function(e){
		e.preventDefault();
		CloseModalBox();
	});
	$('#top-panel').on('click','a', function(e){
		if ($(this).hasClass('ajax-link')) {
			e.preventDefault();
			if ($(this).hasClass('add-full')) {
				$('#content').addClass('full-content');
			}
			else {
				$('#content').removeClass('full-content');
			}
			var url = $(this).attr('href');
			window.location.hash = url;
			LoadAjaxContent(url);
		}
	});
	$('#search').on('keydown', function(e){
		if (e.keyCode == 13){
			e.preventDefault();
			$('#content').removeClass('full-content');
			ajax_url = 'ajax/page_search.html';
			window.location.hash = ajax_url;
			LoadAjaxContent(ajax_url);
		}
	});
});