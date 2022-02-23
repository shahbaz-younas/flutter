<?php

if (file_exists('install/index.php')) {
    header("location:install/");
    die();
}

session_start();

if (isset($_SESSION['id']) && isset($_SESSION['username'])) {
    header("location:home.php");
    return false;
}



?>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <!-- Meta, title, CSS, favicons, etc. -->
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Admin Login | Qearner - Learn More Earn More</title>
        <?php include 'include-css.php'; ?>
    </head>
    <body class="login">
        <div>
            <a class="hiddenanchor" id="signup"></a>
            <a class="hiddenanchor" id="signin"></a>
            <div class="login_wrapper">
                <div class="animate form login_form">
                    <section class="login_content">
                        <form method ="POST"  id ="login_form" action="check_login.php">
                            <h1 class="text-center">LOGIN</h1>
                            <div>
                                <input type="text"  id="username" name='username' class="form-control" placeholder="Username" />
                            </div>
                            <div>
                                <input type="password" id="password" name='password' class="form-control" placeholder="Password" />
                            </div>
                            <div class="row mt-20">
                                <input type ="submit" id="login_button" class="btn btn-default text-center" value="Log in">
                            </div>
                            <div class="clearfix"></div>
                            <div style ="display:none;" id="result">
                            </div>
                            <div class="separator">
                                <div class="clearfix"></div>
                                <br>
                                <img src="images/logo-460x114.png" alt="QUIZ" width='300'>
                                <p class="text-center"><br>© <?= date('Y') ?> WRTeam</p>
                            </div>
                        </form>
                    </section>
                </div>
            </div>
        </div>
        <!-- Including Jquery so All js Can run -->



        <?php
        
        include 'library/crud.php';
        
        $db = new Database();
        $db->connect();

        $sql1 = "SELECT * FROM settings WHERE type='validation_configuration'";
        $db->sql($sql1);
        $result1 = $db->getResult();
       
        $status = true;
        if(empty($result1)){
            $status = false;
        }else{
            $json = json_decode($result1[0]['message']);

            
            if(empty($json->configuration_key) || empty($json->system_key) || empty($result1[0]['message'])){
                $status = false;
            }

        }



        if( $status == false):   ?>

           <div class="modal fade" id='editlanguageModal' tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" data-backdrop="static">
                <div class="modal-dialog modal-md" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                           
                            <h4 class="modal-title" id="myModalLabel">Purchase Code</h4>
                        </div>
                        <div class="modal-body">
                            <form id="update_form"  method="POST" action ="check_login.php" data-parsley-validate class="form-horizontal form-label-left">
                                <input type='hidden' name="system_validation" id="system_validation" value='1'/>
                                
                                <div class="form-group row">
                                                    <div class="col-md-12">
                                                        <label>Purchase Code <small class="text-danger">*</small></label>
                                                        <input name="purchase_code" type="text" id="purchase_code" class="form-control" required placeholder="Enter Purchase Code" />
                                                    </div>
                                                </div>
                                                <?php
                                                        $appurl = (isset($_SERVER['HTTPS']) && $_SERVER['HTTPS'] === 'on' ? "https" : "http") . "://" . $_SERVER['HTTP_HOST'] . $_SERVER['REQUEST_URI'];
                                                        $appurl = preg_replace('#^https?://#i', '', $appurl);
                                                        $appurl = str_replace('install/index.php', '', $appurl);
                                                        $appurl = str_replace('install/', '', $appurl);
                                                        ?>
                                                        <input value="<?= $appurl; ?>" name="quiz_url" type="hidden" id="quiz_url" class="form-control" required/>
                                <div class="ln_solid"></div>
                                <div class="form-group">
                                    <div class="col-md-6 col-sm-6 col-xs-12">
                                        <button type="submit" id="update_btn" class="btn btn-success">Update</button>
                                    </div>
                                </div>
                            </form>
                            <div class="row"><div  class="col-md-offset-3 col-md-8" style ="display:none;" id="update_result"></div></div>
                        </div>
                    </div>
                </div>
            </div>

        <?php endif;  ?>

                
        <script type="text/javascript" src="js/jquery.min.js"></script>
        <script src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>

        <!-- Validadtion js -->
        <script src="https://cdn.jsdelivr.net/jquery.validation/1.16.0/jquery.validate.min.js"></script>
        <script>
            $("#login_form").validate({
                rules: {
                    username: "required",
                    password: "required",
                },
                messages: {
                    username: {
                        required: "Please Enter username"
                    },
                    password: {
                        required: "Please Enter password",

                    },
                }
            });
            $(document).on('submit', '#login_form', function (e) {
                e.preventDefault();
                $.ajax({
                    url: $(this).attr('action'),
                    type: "POST",
                    data: $(this).serialize(),
                    beforeSend: function () {
                        $('#login_button').html('Please Wait...');
                    },
                    // data: dataString,
                    success: function (result) {
                        if (result == 1) {
                            window.location.href = "home.php";
                        } else
                        {
                            $('#result').html(result);
                            $('#result').show();
                            $('#login_button').html('Log in');
                        }
                    }
                });
            });
        </script>

        <?php if($status == false): ?>

        <script type="text/javascript">
            $('#editlanguageModal').modal('show');
        </script>
         <?php endif;  ?>

            
    </body>
</html>
