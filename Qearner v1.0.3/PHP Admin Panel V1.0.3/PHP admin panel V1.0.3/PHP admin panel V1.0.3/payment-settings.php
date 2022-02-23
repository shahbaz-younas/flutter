<?php
session_start();
if (!isset($_SESSION['id']) && !isset($_SESSION['username'])){
    header("location:index.php");
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
        <title>Payment Settings | <?= ucwords($_SESSION['company_name']) ?> Admin Panel  </title>
        <?php include 'include-css.php'; ?>
    </head>
    <body class="nav-md">
        <div class="container body">
            <div class="main_container">
                <?php include 'sidebar.php'; ?>
                <!-- page content -->
                <div class="right_col" role="main">
                    <!-- top tiles -->
                    <br />
                    <div class="row">
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <div class="x_panel">
                                <div class="x_title">
                                    <h2>Payment Settings <small>Update system settings here</small></h2>
                                    <div class="clearfix"></div>
                                </div>
                                <div class="x_content">
                                    <br />
                                    <?php
                                    $sql = "select * from `settings` WHERE type='pay_setting' ";
                                    $db->sql($sql);
                                    $res = $db->getResult();
                                   
                                    
                                    if(!empty($res)){
                                    $data = $res[0]; /* payment request message */
                                    }
                                 
                                    ?>
                                    <div class="col-md-offset-1">
                                        <h4>Payment Request <small>Enable / Disable Payment Request for all App Users</small></h4>
                                    </div>
                                    <div class="col-md-12"><hr style="margin-top: 5px;"></div>
                                    <form id="register_form"  method="POST" action ="db_operations.php"data-parsley-validate class="form-horizontal form-label-left">
                                        <input type="hidden" id="update_payment_settings" name="update_payment_settings" required value='1'/>
                                        <div class="form-group">
                                            <label class="control-label col-md-3 col-sm-3 col-xs-12">Status</label>
                                            <div class="col-md-9 col-sm-6 col-xs-12">
                                                <div id="status" class="btn-group">
                                                    <label class="btn btn-primary" data-toggle-class="btn-primary" data-toggle-passive-class="btn-default">
                                                        <input type="radio" name="status" value="1" class="valid" <?php if(!empty($res)){ echo ($data['status'] == 1) ? 'checked' : ''; } ?>> Enable
                                                    </label>
                                                    <label class="btn btn-default" data-toggle-class="btn-primary" data-toggle-passive-class="btn-default">
                                                        <input type="radio" name="status" value="0" class="valid" <?php if(!empty($res)){ echo ($data['status'] == 0) ? 'checked' : ''; } ?>> Disable
                                                    </label>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="control-label col-md-3 col-sm-3 col-xs-12" for="message">Message for Users</label>
                                            <div class="col-md-6 col-sm-6 col-xs-12">
                                                <textarea name='message' id='message' class='form-control' <?php if(!empty($res)){ echo($data['status'] == 1) ? 'readonly' : ''; } ?>><?php if(!empty($res)){ echo $data['message']; } ?></textarea>
                                            </div>
                                        </div>
                                        <div class="ln_solid"></div>
                                        <div class="form-group">
                                            <div class="col-md-6 col-sm-6 col-xs-12 col-md-offset-3">
                                                <button type="submit" id="submit_btn" class="btn btn-success">Update Settings</button>
                                            </div>
                                    </form>
                                </div>
                                <div class="row">
                                    <div  class="col-md-offset-3 col-md-4" style ="display:none;" id="result">
                                    </div>
                                </div>



                            </div>

                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- /page content -->
        <!-- footer content -->
        <?php include 'footer.php'; ?>
        <!-- /footer content -->
    </div>
</div>
<!-- jQuery -->
<script>
    $('#register_form').validate({
        rules: {
            message: "required",
        }
    });
</script>
<script>
    $('#register_form').on('submit', function (e) {
        e.preventDefault();
        var formData = new FormData(this);
        if ($("#register_form").validate().form()) {
            if (confirm('Are you sure? Want to change the status of Payment Request? This will reflect to all app users')) {
                $.ajax({
                    type: 'POST',
                    url: $(this).attr('action'),
                    data: formData,
                    beforeSend: function () {
                        $('#submit_btn').html('Please updating..');
                    },
                    cache: false,
                    contentType: false,
                    processData: false,
                    success: function (result) {
                        $('#result').html(result);
                        $('#result').show().delay(3000).fadeOut();
                        $('#submit_btn').html('Submit');
                        // $('#register_form')[0].reset();
                    }
                });
            }
        }
    });
</script>
<script>
    $('#status').on('change', 'input[name=status]:checked', function () {
        var status = $(this).val();
        if (status == 1)
            $('#message').prop('readonly', true);
        else
            $('#message').prop('readonly', false);

    });
</script>

<script>
    $('#policy_form').on('submit', function (e) {
        e.preventDefault();
        var formData = new FormData(this);
        if ($("#policy_form").validate().form()) {
            if (confirm('Are you sure? Want to change the status of Payment Request? This will reflect to all app users')) {
                $.ajax({
                    type: 'POST',
                    url: $(this).attr('action'),
                    data: formData,
                    beforeSend: function () {
                        $('#submit_privacy_btn').html('Please updating..');
                    },
                    cache: false,
                    contentType: false,
                    processData: false,
                    success: function (result) {
                        $('#privacy_result').html(result);
                        $('#privacy_result').show().delay(3000).fadeOut();
                        $('#submit_privacy_btn').html('Update Policy');
                        // $('#register_form')[0].reset();
                    }
                });
            }
        }
    });
</script>
</body>
</html>