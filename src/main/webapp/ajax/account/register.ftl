<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Smart Meter Data Analytics System</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="plugins/font-awesome/css/font-awesome.min.css" rel="stylesheet">
    <link href="css/style.css" rel="stylesheet">
    <script src="plugins/jquery/jquery-2.1.0.min.js"></script>
    <script src="js/common.js"></script>
</head>
<body>
<div class="container-fluid">
    <div class="row">
        <div class="col-xs-12 col-md-6 col-md-offset-3 col-sm-6 col-sm-offset-3">
            <div class="box">
                <div class="box-content">
                    <div class="text-center">
                        <h3 class="page-header">Account Registration</h3>
                    </div>
                    <form class="form-horizontal" role="form" action="/smas/servlet" method="post">
                        <div class="form-group">
                            <label class="col-sm-2 control-label">Username</label>
                            <div class="col-sm-4  has-error">
                                <input type="text" class="form-control"  placeholder="Username" name="username" value=${account.username}>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-2 control-label">Password</label>
                            <div class="col-sm-4  has-error">
                                <input type="password" class="form-control"  placeholder="Password" data-toggle="tooltip" data-placement="bottom" title="Tooltip for password" name="password" value=${account.password}>
                            </div>
                            <label class="col-sm-2 control-label">Re-type password</label>
                            <div class="col-sm-4  has-error">
                                <input type="password" class="form-control"  placeholder="Re-type password" data-toggle="tooltip" data-placement="bottom" title="Tooltip for re-type password" name="retypepassword" value=${account.retypePassword}>
                            </div>
                        </div>
                        <fieldset>
                            <legend>Personal Information</legend>
                        <div class="form-group">
                            <label class="col-sm-2 control-label">First name</label>
                            <div class="col-sm-4">
                                <input type="text" class="form-control" placeholder="First name" data-toggle="tooltip" data-placement="bottom" title="Tooltip for name" name="firstname" value=${account.firstName}>
                            </div>
                            <label class="col-sm-2 control-label">Last name</label>
                            <div class="col-sm-4">
                                <input type="text" class="form-control" placeholder="Last name" data-toggle="tooltip" data-placement="bottom" title="Tooltip for last name" name="lastname" value=${account.lastName}>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-2 control-label">Email</label>
                            <div class="col-sm-4">
                                <input type="text" class="form-control email" placeholder="Email" name="email" value=${account.email}>
                            </div>
                        </div>
                       </fieldset>
                        <div class="text-center">
                            <input type="hidden" name="cmd" class="form-control" value="authorize" />
                            <input type="hidden" name="subCmd" class="form-control" value="register" />
                            <button type="submit" class="btn btn-primary">Submit</button>
                        </div>
                        <div class="text-left">
                              <a href="/smas/servlet" class="txt-default">&#60;&#60;&#60; Return</a>
                        </div>
                    </form>
                        ${account.message}
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
