<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Smart Meter Data Analytics System</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="plugins/bootstrap/bootstrap.css" rel="stylesheet">
    <link href="http://netdna.bootstrapcdn.com/font-awesome/4.0.3/css/font-awesome.css" rel="stylesheet">
    <link href='http://fonts.googleapis.com/css?family=Righteous' rel='stylesheet' type='text/css'>
    <link href="css/style.css" rel="stylesheet">

    <script src="http://getbootstrap.com/docs-assets/js/html5shiv.js"></script>
    <script src="http://getbootstrap.com/docs-assets/js/respond.min.js"></script>
    <script src="plugins/jquery/jquery-2.1.0.min.js"></script>
    <script src="js/common.js"></script>
</head>
<body>
<div class="container-fluid">
    <div id="page-login" class="row">
        <div class="col-xs-12 col-md-4 col-md-offset-4 col-sm-6 col-sm-offset-3">
            <div class="text-right">
                <a href="/smas/servlet?cmd=authorize&subCmd=getRegistrationForm" class="txt-default">Need an account?</a>
            </div>
            <div class="box">
                <div class="box-content">
                    <div class="text-center">
                        <h3 class="page-header">Smart Meter Data Analytics System</h3>
                    </div>
                    <form action="/smas/servlet" method="post">
                        <div class="form-group">
                            <label class="control-label">Username</label>
                            <input type="text" class="form-control" name="username" />
                        </div>
                        <div class="form-group">
                            <label class="control-label">Password</label>
                            <input type="password" class="form-control" name="password" />
                        </div>
                        <div class="text-center">
                            <input type="hidden" name="cmd" class="form-control" value="authorize" />
                            <input type="hidden" name="subCmd" class="form-control" value="login" />
                            <button type="submit" class="btn btn-primary btn-lg">Submit</button>
                        </div>
                    </form>
                    <#if errmsg??>${errmsg}</#if>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
