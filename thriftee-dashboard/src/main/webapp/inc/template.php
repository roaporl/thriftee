<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Fuame Services Dashboard</title>
    <link href="assets/bootstrap/css/bootstrap.min.css" rel="stylesheet" />
    <link href="assets/bootstrap/css/bootstrap-theme.min.css" rel="stylesheet" />
    <link href="assets/css/thriftee/thriftee.css" rel="stylesheet" />
</head>
<body>
    <div class="navbar navbar-inverse navbar-fixed-top">
        <div class="container-fluid">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse"
                    data-target=".navbar-collapse">
                    <span class="sr-only">Toggle navigation</span> <span
                        class="icon-bar"></span> <span class="icon-bar"></span> <span
                        class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="index.php">Fuame Services Dashboard</a>
            </div>
            <div class="navbar-collapse collapse">
                <!-- 
                <ul class="nav navbar-nav navbar-right">
                    <li><a href="#">Dashboard</a></li>
                    <li><a href="#">Settings</a></li>
                    <li><a href="#">Profile</a></li>
                    <li><a href="#">Help</a></li>
                </ul>
                <form class="navbar-form navbar-right">
                    <input type="text" class="form-control" placeholder="Search...">
                </form>
                -->
            </div>
        </div>
    </div>
    <div class="container-fluid">
        <div class="row">
            <div class="col-sm-3 col-md-2 sidebar">
                <h4>Services</h4>
                <ul class="nav nav-sidebar">
                    <?php global $thrift; ?>
                    <?php foreach ($thrift->clients as $name => $client) : ?>
                    <li><a href="service.php?service=<?php echo urlencode($name); ?>"><?php echo htmlentities($name); ?></a></li>
                    <?php endforeach; ?>
                </ul>
                <h4>Clients</h4>
                <ul class="nav nav-sidebar">
                    <li><a href="client.php?client=php">PHP</a></li>
                    <li><a href="client.php?client=html">HTML</a></li>
                </ul>
            </div>
            <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
                <div class="row">
                    <div class="col-md-12">

                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12">
                        <h1 class="page-header"><?php echo htmlentities($page_title); ?></h1>
                        <div class="row">
                            <div class="col-md-12">
                                <?php echo $page_content; ?>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <script data-main="assets/js/main.js" src="assets/js/require.js"></script>
</body>
</html>