<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Smart Meter Data Analytics System</title>
    <link href="plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="plugins/jquery-ui/jquery-ui.min.css" rel="stylesheet">
    <link href="plugins/font-awesome/css/font-awesome.min.css" rel="stylesheet">
    <link href="plugins/fancybox/jquery.fancybox.css" rel="stylesheet">
    <link href="plugins/multiselect/bootstrap-multiselect.css" rel="stylesheet">

    <link href="plugins/select2/select2.css" rel="stylesheet">
    <link href="css/style.css" rel="stylesheet">


    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <!--<script src="http://code.jquery.com/jquery.js"></script>-->
    <script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?v=3.exp&sensor=false"></script>
    <script type="text/javascript" src="js/StyledMarker.js"></script>
    <script src="plugins/jquery/jquery-2.1.0.min.js"></script>
    <script src="plugins/jquery-ui/jquery-ui.min.js"></script>
   
    <script src="plugins/bootstrap/js/bootstrap.min.js"></script>
    <script src="plugins/justified-gallery/jquery.justifiedgallery.min.js"></script>
    <script src="plugins/tinymce/tinymce.min.js"></script>
    <script src="plugins/tinymce/jquery.tinymce.min.js"></script>
    <script type="text/javascript" src="plugins/highcharts/highcharts.js"></script>
    <script type="text/javascript" src="plugins/highcharts/modules/exporting.js"></script>
    <script type="text/javascript" src="plugins/multiselect/bootstrap-multiselect.js"></script>

    <!-- All functions for this theme + document.ready processing -->
    <script src="js/common.js"></script>
    <script src="js/user.js"></script>
</head>
<body>
<!--Start Header-->

<header class="navbar">
    <div class="container-fluid expanded-panel">
        <div class="row">
            <div id="logo" class="col-xs-12 col-sm-2">
                <a href="#">SMAS</a>
            </div>
            <div id="top-panel" class="col-xs-12 col-sm-10">
                <div class="row">
                    <div class="col-xs-8 col-sm-4">
                        <a href="#" class="show-sidebar">
                            <i class="fa fa-bars"></i>
                        </a>
                    </div>
                    <div class="col-xs-4 col-sm-8 top-panel-right">
                        <ul class="nav navbar-nav pull-right panel-menu">

                            <li class="dropdown">
                                <a href="#" class="dropdown-toggle account" data-toggle="dropdown">
                                    <div class="avatar">
                                        <img src="img/xiufeng.jpg" class="img-rounded" alt="avatar" />
                                    </div>
                                    <i class="fa fa-angle-down pull-right"></i>
                                    <div class="user-mini pull-right">
                                        <span class="welcome">Welcome,</span>
                                        <span>${userinfo.firstname}&#32;${userinfo.lastname}</span>
                                    </div>
                                </a>
                                <ul class="dropdown-menu">

                                    <li>
                                        <a href="/smas/servlet?cmd=authorize&subCmd=logout">
                                            <i class="fa fa-power-off"></i>
                                            <span class="hidden-sm text">Logout</span>
                                        </a>
                                    </li>
                                </ul>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>
</header>
<!--End Header-->
<!--Start Container-->
<div id="main" class="container-fluid">
    <div class="row">
        <div id="sidebar-left" class="col-xs-2 col-sm-2">
            <ul class="nav main-menu">
           <!--     <li>
                    <a href="ajax/dashboard.html" class="active ajax-link">
                        <i class="fa fa-dashboard"></i>
                        <span class="hidden-xs">Home</span>
                    </a>
                </li> -->
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle">
                        <i class="fa fa-bar-chart-o"></i>
                        <span class="hidden-xs">Electricity</span>
                    </a>
                    <ul class="dropdown-menu">
                        <li><a class="ajax-link" href="ajax/admin/loadanalysis.html">Consumption Analysis</a></li>
                        <li><a class="ajax-link" href="ajax/admin/segmentationnew.html">Segmentation Analysis</a></li>
                        <li><a class="ajax-link" href="ajax/admin/forcasting.html">Forecasting</a></li>
                    </ul>
                </li>

                <li class="dropdown">
                    <a href="#" class="dropdown-toggle">
                        <i class="fa fa-bar-chart-o"></i>
                        <span class="hidden-xs">Administration</span>
                    </a>
                    <ul class="dropdown-menu">
                        <li><a class="ajax-link" href="ajax/account/accountmgnt.html">Account Management</a></li>
                    </ul>
                </li>

                <!--
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle">
                        <i class="fa fa-bar-chart-o"></i>
                        <span class="hidden-xs">Water</span>
                    </a>
                    <ul class="dropdown-menu">
                        <li><a class="ajax-link" href="ajax/admin/waterloadanalysis.html">Load Time-series</a></li>
                        <li><a class="ajax-link" href="ajax/admin/waterloadanalysisbyexovar.html">Load Analysis With Exogenous Variables</a></li>
                        <li><a class="ajax-link" href="ajax/admin/waterloadprofilingparx.html">Load Profiling with PARX</a></li>
                    </ul>
                </li> -->
            </ul>
        </div>

        <!--Start Content-->
        <div id="content" class="col-xs-12 col-sm-10">
            <div id="ajax-content"></div>
        </div>
        <input type="hidden" id="level" value='-1' />
        <input type="hidden" id="value" value='-1' />
    </div>
</div>
</body>
</html>
