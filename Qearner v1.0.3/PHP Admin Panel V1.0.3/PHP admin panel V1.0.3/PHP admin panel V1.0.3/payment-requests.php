<?php
session_start();
if (!isset($_SESSION['id']) && !isset($_SESSION['username']))
    header("location:index.php");
    
?>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <!-- Meta, title, CSS, favicons, etc. -->
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>User Payment Requests | <?= ucwords($_SESSION['company_name']) ?> - Admin Panel </title>
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
                                    <h2>Payment Requests <small>User's Payment Requests Details</small></h2>
                                    <div class="clearfix"></div>
                                </div>
                                <div class="x_content">
                                    <div class="row">
                                        <form id="bulk_update_form" method="POST" action ="db_operations.php" class="form-horizontal form-label-left">
                                            <input type='hidden' name="ids" id="bulk_ids" value='' required/>
                                            <input type='hidden' name="update_bulk_payment_request" id="update_bulk_payment_request" value='1'/>
                                            <div class="col-md-2 col-md-offset-2">
                                                <a class='btn btn-warning' href='#' id='get_bulk_selection_btn'>Get Selected Requests</a>
                                            </div>
                                            <div class="col-md-4">
                                                <select id='bulk_status' name='status' class='form-control' required>
                                                    <option value=''>Select Status</option>
                                                    <option value='0'>Pending</option>
                                                    <option value='1'>Completed</option>
                                                </select>
                                            </div>
                                            <div class="col-md-4">
                                                <button type="submit" id="bulk_submit_btn" class="btn btn-primary">Change Status</button>
                                            </div>
                                            <div class="row"><div  class="col-md-offset-3 col-md-4" style ="display:none;" id="bulk_result"></div></div>
                                            <div class='col-md-12'>
                                                <hr>
                                            </div>
                                        </form>
                                    </div>
                                    <div id="toolbar">
                                        <select id='export_select' class="form-control" >
                                            <option value="basic">Export This Page</option>
                                            <option value="all">Export All</option>
                                            <option value="selected">Export Selected</option>
                                        </select>
                                    </div>
                                    <table aria-describedby="table" class='table-striped' id='payment_list'
                                           data-toggle="table"
                                           data-url="get-list.php?table=payment_requests"
                                           data-click-to-select="true"
                                           data-side-pagination="server"
                                           data-pagination="true"
                                           data-page-list="[5, 10, 20, 50, 100, 200]"
                                           data-search="true" data-show-columns="true"
                                           data-show-refresh="true" data-trim-on-search="false"
                                           data-sort-name="id" data-sort-order="desc"
                                           data-mobile-responsive="true"
                                           data-toolbar="#toolbar" data-show-export="true"
                                           data-maintain-selected="true"
                                           data-export-types='["txt","excel"]'
                                           data-export-options='{
                                           "fileName": "activity-list-<?= date('d-m-y') ?>",
                                           "ignoreColumn": ["state"]	
                                           }'
                                           data-query-params="queryParams_1"
                                           >
                                        <thead>
                                            <tr>
                                                <th scope="col" data-field="state" data-checkbox="true"></th>
                                                <th scope="col" data-field="id" data-sortable="true">ID</th>
                                                <th scope="col" data-field="uid" data-sortable="true">Uid</th>
                                                <th scope="col" data-field="name" data-sortable="true">name</th>
                                                <th scope="col" data-field="payment_address" data-sortable="true">Payment Address</th>
                                                <th scope="col" data-field="request_type" data-sortable="true">Request Type</th>
                                                <th scope="col" data-field="request_amount" data-sortable="true">Requested Amount</th>
                                                <th scope="col" data-field="points_used" data-sortable="true">Points Used</th>
                                                <th scope="col" data-field="remarks" data-sortable="true">Remarks</th>
                                                <th scope="col" data-field="status" data-sortable="true">Status</th>
                                                <th scope="col" data-field="date" data-sortable="true">Date</th>
                                                <th scope="col" data-field="operate" data-sortable="true" data-events="actionEvents">Operate</th>
                                            </tr>
                                        </thead>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- /page content -->
            <div class="modal fade" id='editStatusModal' tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel">
                <div class="modal-dialog modal-md" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                            <h4 class="modal-title" id="myModalLabel">Update Payment Status</h4>
                        </div>
                        <div class="modal-body">
                            <form id="update_form"  method="POST" action ="db_operations.php" data-parsley-validate class="form-horizontal form-label-left">
                                <input type='hidden' name="id" id="id" value=''/>
                                <input type='hidden' name="uid" id="uid" value=''/>
                                <input type='hidden' name="points_used" id="points_used" value=''/>
                                <input type='hidden' name="update_payment_request" id="update_payment_request" value='1'/>
                                <div class="form-group">
                                    <label class="control-label col-md-3 col-sm-3 col-xs-12">Status</label>
                                    <div class="col-md-6 col-sm-6 col-xs-12">
                                        <textarea class='form-control' name='remarks' id='remarks' placeholder='Any message to be given' rows=5></textarea>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="control-label col-md-3 col-sm-3 col-xs-12">Status</label>
                                    <div class="col-md-9 col-sm-6 col-xs-12">
                                        <div id="status" class="btn-group" >
                                            <label class="btn btn-warning" data-toggle-class="btn-primary" data-toggle-passive-class="btn-default">
                                                <input type="radio" name="status" value="0"> Pending
                                            </label>
                                            <label class="btn btn-success" data-toggle-class="btn-primary" data-toggle-passive-class="btn-default">
                                                <input type="radio" name="status" value="1">  Completed
                                            </label>
                                            <label class="btn btn-danger" data-toggle-class="btn-primary" data-toggle-passive-class="btn-default">
                                                <input type="radio" name="status" value="2">  Wrong Details
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                <div class="ln_solid"></div>
                                <div class="form-group">
                                    <div class="col-md-6 col-sm-6 col-xs-12 col-md-offset-3">
                                        <button type="submit" id="submit_btn" class="btn btn-success">Submit</button>
                                    </div>
                                </div>
                            </form>
                            <div class="row"><div  class="col-md-offset-3 col-md-8" style ="display:none;" id="result"></div></div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- footer content -->
            <?php include 'footer.php'; ?>
            <!-- /footer content -->
        </div>
    </div>
    <!-- jQuery -->
    <script>
        var $table = $('#payment_list');
        $('#toolbar').find('select').change(function () {
            $table.bootstrapTable('refreshOptions', {
                exportDataType: $(this).val()
            });
        });
    </script>
    <script>
        function queryParams_1(p) {
            var username = '<?= (isset($_GET['username'])) ? $_GET['username'] : '' ?>';
            return {
                "username": username,
                limit: p.limit,
                sort: p.sort,
                order: p.order,
                offset: p.offset,
                search: p.search
            };
        }
    </script>
    <script>
        window.actionEvents = {
            'click .edit-status': function (e, value, row, index) {
                // alert('You click remove icon, row: ' + JSON.stringify(row));
                $("input[name=status][value=0]").prop('checked', true);
                if ($(row.status).text() == 'Completed')
                    $("input[name=status][value=1]").prop('checked', true);
                if ($(row.status).text() == 'Wrong details')
                    $("input[name=status][value=2]").prop('checked', true);

                $('#remarks').val(row.remarks);
                $('#uid').val(row.uid);
                $('#points_used').val(row.points_used);
                $('#id').val(row.id);
                $("input[name=status][value=" + row.status + "]").prop('checked', true);
            }
        };
    </script>
    <script>
        $('#update_form').validate({
            rules: {
                status: "required",
            }
        });
    </script>
    <script>
        $('#update_form').on('submit', function (e) {
            e.preventDefault();
            var formData = new FormData(this);
            var status = $('input[name=status]:checked').val();
            var msg = (status == 2) ? "Are you sure? All of the points used will be reversed & Can\'t reverse the status after its updated as WRONG DETAILS!" : "Are you sure?want to update payment status";

            if (confirm(msg)) {
                if ($("#update_form").validate().form()) {
                    $.ajax({
                        type: 'POST',
                        url: $(this).attr('action'),
                        data: formData,
                        beforeSend: function () {
                            $('#submit_btn').html('Please wait..');
                        },
                        cache: false,
                        contentType: false,
                        processData: false,
                        success: function (result) {
                            $('#result').html(result);
                            $('#result').show();
                            $('#result').show().delay(3000).fadeOut();
                            $('#submit_btn').html('Submit');
                            $('#payment_list').bootstrapTable('refresh');
                        }
                    });
                }
            }
        });
    </script>
    <script>
        $('#get_bulk_selection_btn').on('click', function () {
            var rows = $('#payment_list').bootstrapTable('getSelections');
            var ids = '';
            $.each(rows, function (key, valueObj) {
                ids += valueObj.id + ', ';
                // alert(key + " - " + valueObj.id );
            });
            ids = ids.replace(/,\s*$/, "");
            $('#bulk_ids').val(ids);
            alert(ids);
        });
    </script>
    <script>
        $('#bulk_update_form').on('submit', function (e) {
            e.preventDefault();
            var formData = new FormData(this);
            var msg = "Are you sure?want to update payment status for all";

            if (confirm(msg)) {
                if ($("#bulk_update_form").validate().form()) {
                    $.ajax({
                        type: 'POST',
                        url: $(this).attr('action'),
                        data: formData,
                        beforeSend: function () {
                            $('#bulk_submit_btn').html('Please wait..');
                        },
                        cache: false,
                        contentType: false,
                        processData: false,
                        success: function (result) {
                            $('#bulk_result').html(result);
                            $('#bulk_result').show().delay(3000).fadeOut();
                            $('#bulk_submit_btn').html('Change Status');
                            $('#payment_list').bootstrapTable('refresh');
                        }
                    });
                }
            }
        });
    </script>
</body>
</html>