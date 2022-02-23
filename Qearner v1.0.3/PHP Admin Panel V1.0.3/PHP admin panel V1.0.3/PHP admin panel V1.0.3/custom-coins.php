<?php
session_start();
if (!isset($_SESSION['id']) && !isset($_SESSION['username'])) {
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
        <title>Custom Coin | <?= ucwords($_SESSION['company_name']) ?> Admin Panel  </title>
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
                                    <h2>Custom Coin</h2>
                                    <div class="clearfix"></div>
                                </div>
                                <div class="x_content">
                                    <form id="custom_coin_form" method="POST" action ="db_operations.php"data-parsley-validate class="form-horizontal form-label-left">
                                        <input type="hidden" id="custom_coin" name="custom_coin" required="" value="1" aria-required="true">


                                        <?php
                                        $db->sql("SET NAMES 'utf8'");
                                        $sql = "SELECT * FROM tbl_custom_coin";
                                        $db->sql($sql);
                                        $res = $db->getResult();

                                        $data = array();

                                        if (!empty($res)) {
                                            foreach ($res as $row) {
                                                $data[$row['type']] = $row['coin'];
                                            }
                                        }
                                        ?>

                                                                                <div class="row">
                                            <div class="col-md-3 col-xs-12">
                                                <div class="form-group">
                                                    <label class="" for="app_link">Per Coin</label>
                                                    <input type="text" id="coin" name="per_coin" required="required" class="form-control col-md-12 col-xs-12" value="<?php echo ($data['per_coin'] != "") ? $data['per_coin'] : "" ?>">
                                                </div>
                                            </div>

                                            <div class="col-md-3 col-xs-12">
                                                <div class="form-group">
                                                    <label class="" for="app_link">Rupees</label>
                                                    <input type="text" id="amount" name="amount" required="required" class="form-control col-md-12 col-xs-12" value="<?php echo ($data['amount'] != "") ? $data['amount'] : "" ?>">
                                                </div>
                                            </div>

                                        </div>
                                        <hr>

                                        <div class="row">
                                            <div class="col-md-3 col-xs-12">
                                                <div class="form-group">
                                                    <label class="" for="app_link">Paypal Per Coin</label>
                                                    <input type="text" id="paypalcoin" name="paypal_coin" required="required" class="form-control col-md-12 col-xs-12" value="<?php echo ($data['paypal_coin'] != "") ? $data['paypal_coin'] : "" ?>">
                                                </div>
                                            </div>

                                            <div class="col-md-3 col-xs-12">
                                                <div class="form-group">
                                                    <label class="" for="app_link">Paypal Rupees</label>
                                                    <input type="text" id="paypalamount" name="paypal_amount" required="required" class="form-control col-md-12 col-xs-12" value="<?php echo ($data['paypal_amount'] != "") ? $data['paypal_amount'] : "" ?>">
                                                </div>
                                            </div>

                                        </div>
                                        <hr>
                                        <div class="row">
                                            <div class="col-md-3 col-xs-12">
                                                <div class="form-group">
                                                    <label class="" for="app_link">True Answer</label>
                                                    <input type="number" min="0" id="true_ans" name="true_answer" required="required"  class="form-control col-md-12 col-xs-12" value="<?php echo ($data['true_answer'] != "") ? $data['true_answer'] : "" ?>">
                                                </div>
                                            </div>

                                            <div class="col-md-3 col-xs-12">
                                                <div class="form-group">
                                                    <label class="" for="app_link">Penalty Per Wrong Answer</label>
                                                    <input type="number" min="0" id="penalty_wrong_answer" name="penalty_wrong_answer" required="required" class="form-control col-md-12 col-xs-12" value="<?php echo ($data['penalty_wrong_answer'] != "") ? $data['penalty_wrong_answer'] : "" ?>">
                                                </div>
                                            </div>
                                            <!-- <div class="col-md-3 col-xs-12">
                                                <div class="form-group">
                                                    <label class="" for="app_link">Daily Quiz Coin</label>
                                                    <input type="number" min="0" id="daily_quiz_coin" name="daily_quiz_coin" required="required" class="form-control col-md-12 col-xs-12" value="<?php echo ($data['daily_quiz_coin'] != "") ? $data['daily_quiz_coin'] : "" ?>">
                                                </div>
                                            </div> -->
                                            <div class="col-md-3 col-xs-12">
                                                <div class="form-group">
                                                    <label class="" for="app_link">Battle Quiz Entry Coin</label>
                                                    <input type="number" min="0" id="battle_quiz_entry_coin" name="battle_quiz_entry_coin" required="required" class="form-control col-md-12 col-xs-12" value="<?php echo ($data['battle_quiz_entry_coin'] != "") ? $data['battle_quiz_entry_coin'] : "" ?>">
                                                </div>
                                            </div>

                                        </div>
                                        <hr>

                                        <div class="row">
                                            <div class="col-md-3 col-xs-12">
                                                <div class="form-group">
                                                    <label class="" for="app_link">Battle Winner Coin</label>
                                                    <input type="number" min="0" id="battle_winner_coin" name="battle_winner_coin" required="required" class="form-control col-md-12 col-xs-12" value="<?php echo ($data['battle_winner_coin'] != "") ? $data['battle_winner_coin'] : "" ?>">
                                                </div>
                                            </div>
                                            <div class="col-md-3 col-xs-12">
                                                <div class="form-group">
                                                    <label class="" for="app_link">Refer Coin</label>
                                                    <input type="number" min="0" id="refer_coin" name="refer_coin" required="required" class="form-control col-md-12 col-xs-12" value="<?php echo ($data['refer_coin'] != "") ? $data['refer_coin'] : "" ?>">
                                                </div>
                                            </div>
                                            <div class="col-md-3 col-xs-12">
                                                <div class="form-group">
                                                    <label class="" for="app_link">Earn Coin</label>
                                                    <input type="number" min="0" id="earn_coin" name="earn_coin" required="required" class="form-control col-md-12 col-xs-12" value="<?php echo ($data['earn_coin'] != "") ? $data['earn_coin'] : "" ?>">
                                                </div>
                                            </div>
                                            <div class="col-md-3 col-xs-12">
                                                <div class="form-group">
                                                    <label class="" for="app_link">Reward Coin</label>
                                                    <input type="number" min="0" id="reward_coin" name="reward_coin" required="required" class="form-control col-md-12 col-xs-12" value="<?php echo ($data['reward_coin'] != "") ? $data['reward_coin'] : "" ?>">
                                                </div>
                                            </div>


                                        </div>
                                         <hr>
                                        <div class="row">
                                            <div class="col-md-3 col-xs-12">
                                                <div class="form-group">
                                                    <label class="" for="app_link">Maximum Application Coin</label>
                                                    <input type="number" min="0" id="max_app_coin" name="max_app_coin" required="required" class="form-control col-md-12 col-xs-12" value="<?php echo ($data['max_app_coin'] != "") ? $data['max_app_coin'] : "" ?>">
                                                </div>

                                            </div>
                                             <div class="col-md-3 col-xs-12">
                                                <div class="form-group">
                                                    <label class="" for="app_link">Start Coin</label>
                                                    <input type="number" min="0" id="start_coin" name="start_coin" required="required" class="form-control col-md-12 col-xs-12" value="<?php echo ($data['start_coin'] != "") ? $data['start_coin'] : "" ?>">
                                                </div>

                                            </div>
                                            <div class="col-md-3 col-xs-12">
                                                <div class="form-group">
                                                    <label class="" for="app_link">Daily Earn Coins</label>
                                                    <input type="number" min="0" id="daily_earn_coins" name="daily_earn_coins" required="required" class="form-control col-md-12 col-xs-12" value="<?php echo ($data['daily_earn_coins'] != "") ? $data['daily_earn_coins'] : "" ?>">
                                                </div>
                                               
                                            </div>

                                        </div>

                                </div>


                                <br>


                                <div class="row">
                                    <div class="col-md-12 col-xs-12">
                                        <div class="ln_solid"></div>
                                        <div id="result"></div>
                                        <div class="form-group">
                                            <div class="col-md-6 col-sm-6 col-xs-12">
                                                <button type="submit" id="submit_btn" class="btn btn-warning">Save Settings</button>
                                            </div>
                                        </div>
                                    </div>
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
        <!-- /page content -->
        <!-- footer content -->
<?php include 'footer.php'; ?>
        <!-- /footer content -->
    </div>
</div>
<!-- jQuery -->

<script>
    $('#custom_coin_form').on('submit', function (e) {



        e.preventDefault();
        var formData = new FormData(this);
        if ($("#custom_coin_form").validate().form()) {
            if (confirm('Are you sure? Want to change the Custom Message?')) {
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
                        $('#result').html(result);
                        $('#result').show().delay(5000).fadeOut();
                        $('#submit_btn').html('Save Settings');

                         $('#register_form')[0].reset();
                    }
                });
            }
        }

    });
</script>

</body>
</html>
