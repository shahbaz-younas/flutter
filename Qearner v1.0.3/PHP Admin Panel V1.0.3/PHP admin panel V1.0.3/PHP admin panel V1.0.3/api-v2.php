<?php

/*
  API v1.0.0
  Quiz Qearner Online - WRTeam.in
  WRTeam Developers
 */
session_start();
header("Content-Type: application/json");
header("Expires: 0");
header("Last-Modified: " . gmdate("D, d M Y H:i:s") . " GMT");
header("Cache-Control: no-store, no-cache, must-revalidate");
header("Cache-Control: post-check=0, pre-check=0", false);
header("Pragma: no-cache");

//header("Content-Type: multipart/form-data");
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Headers: Origin, X-Requested-With, Content-Type, Accept, Authorization');

header('X-Frame-Options: DENY');
header('X-XSS-Protection: 1; mode=block');
header('X-Content-Type-Options: nosniff');

require __DIR__ . '/vendor/autoload.php';
include('library/crud.php');
include('library/functions.php');

$db = new Database();
$db->connect();

$fn = new Functions();
$config = $fn->get_configurations();
$coin_config = $fn->get_custom_coin();

include_once('library/verify-token.php');

if (isset($config['system_timezone']) && !empty($config['system_timezone'])) {
    date_default_timezone_set($config['system_timezone']);
} else {
    date_default_timezone_set('Asia/Kolkata');
}
if (isset($config['system_timezone_gmt']) && !empty($config['system_timezone_gmt'])) {
    $db->sql("SET `time_zone` = '" . $config['system_timezone_gmt'] . "'");
} else {
    $db->sql("SET `time_zone` = '+05:30'");
}

$db->sql("SET NAMES 'utf8'");
$response = array();
$access_key = "6808";

$toDate = date('Y-m-d');
$toDateTime = date('Y-m-d H:i:s');

use Kreait\Firebase\Factory;
use Firebase\Auth\Token\Exception\InvalidToken;




/*
  API methods
  ------------------------------------
  1. get_languages()
  2. get_categories_by_language()
  3. get_categories()
  4. get_subcategory_by_maincategory()
  5. get_questions_by_category()
  6. get_questions_by_subcategory()
  7. get_questions_by_type()
  8. get_questions_for_self_challenge()
  9. get_random_questions()
  10. get_random_questions_for_computer()
  11. report_question()
  12. user_signup()
  13. get_user_by_id()
  14. update_fcm_id()
  15. upload_profile_image()
  16. update_profile()
  17. set_monthly_leaderboard()
  18. get_monthly_leaderboard()
  19. get_datewise_leaderboard()
  20. get_global_leaderboard()
  21. get_system_configurations()
  22. get_about_us()
  23. get_privacy_policy_settings()
  24. get_terms_conditions_settings()
  25. get_instructions()
  26. get_notifications()
  29. set_users_statistics()
  30. get_users_statistics()
  31. set_played_status()
  32. get_played_status()
  33. get_daily_quiz()
  34. get_user_coin_score()
  35. set_user_coin_score()
  36. get_contest()
  37. get_questions_by_contest()
  38. contest_update_score()
  39. get_contest_score()
  40. add_point()
  41. payment_request()
  42. user_tracker()
  43. get_all_coin_list()
  44. set_daily_status()
  45. get_daily_status()
  46. account_remove() 
 *
 *
  functions
  ------------------------------------
  1. get_fcm_id($user_id)
  2. checkBattleExists($match_id)
  3. set_monthly_leaderboard($user_id, $score)
  4. checkUID($uid)
 */

// 1. get_languages()
if (isset($_POST['access_key']) && isset($_POST['get_languages']) && $_POST['get_languages'] == 1) {
    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (isset($_POST['id']) && !empty($_POST['id'])) {
        $id = $db->escapeString($_POST['id']);
        $sql = "SELECT * FROM `languages` WHERE `status`= 1 AND `id`=" . $id . " ORDER BY id ASC";
    } else {
        $sql = "SELECT * FROM `languages` WHERE `status`= 1 ORDER BY id ASC";
    }
    $db->sql($sql);
    $res = $db->getResult();
    if (!empty($res)) {
        $response['error'] = "false";
        $response['data'] = $res;
    } else {
        $response['error'] = "true";
        $response['message'] = "No data found!";
    }
    print_r(json_encode($response));
}

// 2. get_categories_by_language() - get categories list by language id
if (isset($_POST['access_key']) && isset($_POST['get_categories_by_language'])) {

    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (isset($_POST['language_id']) && !empty($_POST['language_id']) && isset($_POST['user_id']) && !empty($_POST['user_id'])) {
        $language_id = $db->escapeString($_POST['language_id']);
        $user_id = $db->escapeString($_POST['user_id']);
        $sql = "SELECT *,(select count(id) from question where question.category=c.id ) as no_of_que,
                        (SELECT @no_of_subcategories := count(*) from subcategory s WHERE s.maincat_id = c.id and s.status = 1 ) as no_of,
			(select `language` from `languages` l where l.id = c.language_id ) as language

			FROM `category` c where `language_id` = " . $language_id . " ORDER By CAST(c.row_order as unsigned) ASC";
        $db->sql($sql);
        $result = $db->getResult();


        if (!empty($result)) {
            for ($i = 0; $i < count($result); $i++) {
                $result[$i]['image'] = (!empty($result[$i]['image'])) ? DOMAIN_URL . 'images/category/' . $result[$i]['image'] : '';

                $sql1 = "SELECT * FROM `tbl_cat_sub_cat_complete` WHERE user_id='$user_id' AND category ='".$result[$i]['id']."'";
                $db->sql($sql1);
                $result1= $db->getResult();
                $result[$i]['is_played'] = (!empty($result1)) ? '1' :'0';
            }
            $response['error'] = "false";
            $response['data'] = $result;
        } else {
            $response['error'] = "true";
            $response['message'] = "No data found!";
        }
    } else {
        $response['error'] = "true";
        $response['message'] = "Please pass all the fields";
    }
    print_r(json_encode($response));
}

// 3. get_categories()
if (isset($_POST['access_key']) && isset($_POST['get_categories'])) {

    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    $user_id = $db->escapeString($_POST['user_id']);
    if (isset($_POST['id']) && isset($_POST['user_id']) && !empty($_POST['user_id'])) {
        $id = $db->escapeString($_POST['id']);

        $sql = "SELECT *,(select count(id) from question where question.category=c.id ) as no_of_que, (SELECT @no_of_subcategories := count(`id`) from subcategory s WHERE s.maincat_id = c.id and s.status = 1 ) as no_of FROM `category` c WHERE c.id = $id ORDER By CAST(c.row_order as unsigned) ASC";
        $db->sql($sql);
        $result = $db->getResult();


        //0 : Not  Played  1: Player





        if (!empty($result)) {
            $result[0]['image'] = (!empty($result[0]['image'])) ? DOMAIN_URL . 'images/category/' . $result[0]['image'] : '';
             $sql1 = "SELECT * FROM `tbl_cat_sub_cat_complete` WHERE user_id='$user_id' AND category ='".$result[0]['id']."'";
            $db->sql($sql1);
            $result1= $db->getResult();
            $result[0]['is_played'] = (!empty($result1)) ? '1' :'0';
            $response['error'] = "false";
            $response['data'] = $result[0];
        } else {
            $response['error'] = "true";
            $response['message'] = "No data found!";
        }
    } else {
        $sql = "SELECT *,(select count(id) from question where question.category=c.id ) as no_of_que,(SELECT @no_of_subcategories := count(`id`) from subcategory s WHERE s.maincat_id = c.id and s.status = 1 ) as no_of FROM `category` c ORDER By CAST(c.row_order as unsigned) ASC";
        $db->sql($sql);
        $result = $db->getResult();
        if (!empty($result)) {
            for ($i = 0; $i < count($result); $i++) {
                $result[$i]['image'] = (!empty($result[$i]['image'])) ? DOMAIN_URL . 'images/category/' . $result[$i]['image'] : '';

                $sql1 = "SELECT * FROM `tbl_cat_sub_cat_complete` WHERE user_id='$user_id' AND category ='".$result[$i]['id']."'";
                $db->sql($sql1);
                $result1= $db->getResult();
                $result[$i]['is_played'] = (!empty($result1)) ? '1' :'0';
            }
            $response['error'] = "false";
            $response['data'] = $result;
        } else {
            $response['error'] = "true";
            $response['message'] = "No data found!";
        }
    }
    print_r(json_encode($response));
}

// 4. get_subcategory_by_maincategory()
if (isset($_POST['access_key']) && isset($_POST['get_subcategory_by_maincategory'])) {

    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (isset($_POST['main_id']) && isset($_POST['user_id'])) {
        $user_id = $db->escapeString($_POST['user_id']);
        $id = $db->escapeString($_POST['main_id']);
        $sql = "SELECT *,(select count(id) from question where question.subcategory=subcategory.id ) as no_of FROM `subcategory` WHERE `maincat_id`='$id' and `status`=1 ORDER BY CAST(row_order as unsigned) ASC";
        $db->sql($sql);
        $result = $db->getResult();

        if (!empty($result)) {
            for ($i = 0; $i < count($result); $i++) {
                $result[$i]['image'] = (!empty($result[$i]['image'])) ? DOMAIN_URL . 'images/subcategory/' . $result[$i]['image'] : '';


                $sql1 = "SELECT * FROM `tbl_cat_sub_cat_complete` WHERE user_id='$user_id' AND  category='$id' AND subcategory ='".$result[$i]['id']."'";
                $db->sql($sql1);
                $result1= $db->getResult();
                $result[$i]['is_played'] = (!empty($result1)) ? '1' :'0';
            }
            $response['error'] = "false";
            $response['data'] = $result;
        } else {
            $response['error'] = "true";
            $response['message'] = "No data found!";
        }
    } else {
        $response['error'] = "true";
        $response['message'] = "Please pass all the fields";
    }
    print_r(json_encode($response));
}

// 5. get_questions_by_category()
if (isset($_POST['access_key']) && isset($_POST['get_questions_by_category'])) {

    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (isset($_POST['category'])) {
        $id = $db->escapeString($_POST['category']);
        $sql = "SELECT * FROM `question` WHERE category=" . $id . " ORDER BY id DESC";
        $db->sql($sql);
        $result = $db->getResult();

        if (!empty($result)) {
            for ($i = 0; $i < count($result); $i++) {
                $result[$i]['image'] = (!empty($result[$i]['image'])) ? DOMAIN_URL . 'images/questions/' . $result[$i]['image'] : '';
                $result[$i]['optione'] = ($fn->is_option_e_mode_enabled() && $result[$i]['optione'] != null) ? trim($result[$i]['optione']) : '';
                $result[$i]['optiona'] = trim($result[$i]['optiona']);
                $result[$i]['optionb'] = trim($result[$i]['optionb']);
                $result[$i]['optionc'] = trim($result[$i]['optionc']);
                $result[$i]['optiond'] = trim($result[$i]['optiond']);
            }
            $response['error'] = "false";
            $response['data'] = $result;
        } else {
            $response['error'] = "true";
            $response['message'] = "No data found!";
        }
    } else {
        $response['error'] = "true";
        $response['message'] = "Please pass all the fields";
    }
    print_r(json_encode($response));
}

// 6. get_questions_by_subcategory()
if (isset($_POST['access_key']) && isset($_POST['get_questions_by_subcategory'])) {

    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (isset($_POST['subcategory'])) {
        $id = $db->escapeString($_POST['subcategory']);
        $sql = "SELECT * FROM `question` where subcategory=" . $id . " ORDER by RAND()";
        $db->sql($sql);
        $result = $db->getResult();

        if (!empty($result)) {
            for ($i = 0; $i < count($result); $i++) {
                $result[$i]['image'] = (!empty($result[$i]['image'])) ? DOMAIN_URL . 'images/questions/' . $result[$i]['image'] : '';
                $result[$i]['optione'] = ($fn->is_option_e_mode_enabled() && $result[$i]['optione'] != null) ? trim($result[$i]['optione']) : '';
                $result[$i]['optiona'] = trim($result[$i]['optiona']);
                $result[$i]['optionb'] = trim($result[$i]['optionb']);
                $result[$i]['optionc'] = trim($result[$i]['optionc']);
                $result[$i]['optiond'] = trim($result[$i]['optiond']);
            }
            $response['error'] = "false";
            $response['data'] = $result;
        } else {
            $response['error'] = "true";
            $response['message'] = "No data found!";
        }
    } else {
        $response['error'] = "true";
        $response['message'] = "Please pass all the fields";
    }
    print_r(json_encode($response));
}



// 7. get_questions_by_type()
if (isset($_POST['access_key']) && isset($_POST['get_questions_by_type'])) {

    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (isset($_POST['type']) && !empty($_POST['type']) && isset($_POST['limit']) && !empty($_POST['limit'])) {
        $language_id = (isset($_POST['language_id']) && is_numeric($_POST['language_id'])) ? $db->escapeString($_POST['language_id']) : '';
        $type = $db->escapeString($_POST['type']);
        $limit = $db->escapeString($_POST['limit']);
        $sql = "SELECT * FROM `question` where question_type=" . $type;
        $sql .= (!empty($language_id)) ? " and `language_id`=" . $language_id : "";
        $sql .= " ORDER BY rand() DESC";
        $sql .= " LIMIT 0, " . $limit . "";
        $db->sql($sql);
        $result = $db->getResult();

        if (!empty($result)) {
            for ($i = 0; $i < count($result); $i++) {
                $result[$i]['image'] = (!empty($result[$i]['image'])) ? DOMAIN_URL . 'images/questions/' . $result[$i]['image'] : '';
                $result[$i]['optione'] = ($fn->is_option_e_mode_enabled() && $result[$i]['optione'] != null) ? trim($result[$i]['optione']) : '';
                $result[$i]['optiona'] = trim($result[$i]['optiona']);
                $result[$i]['optionb'] = trim($result[$i]['optionb']);
                $result[$i]['optionc'] = trim($result[$i]['optionc']);
                $result[$i]['optiond'] = trim($result[$i]['optiond']);
            }
            $response['error'] = "false";
            $response['data'] = $result;
        } else {
            $response['error'] = "true";
            $response['message'] = "No data found!";
        }
    } else {
        $response['error'] = "true";
        $response['message'] = "Please pass all the fields";
    }
    print_r(json_encode($response));
}

// 8. get_questions_for_self_challenge()
if (isset($_POST['access_key']) && isset($_POST['get_questions_for_self_challenge'])) {

    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (isset($_POST['limit']) && (isset($_POST['category']) || isset($_POST['subcategory']))) {
        $limit = $db->escapeString($_POST['limit']);

        $language_id = (isset($_POST['language_id']) && is_numeric($_POST['language_id'])) ? $db->escapeString($_POST['language_id']) : '';
        $id = (isset($_POST['category'])) ? $db->escapeString($_POST['category']) : $db->escapeString($_POST['subcategory']);

        $sql = "SELECT * FROM `question` ";
        $sql .= (isset($_POST['category'])) ? " WHERE `category`=" . $id : " WHERE `subcategory`=" . $id;
        $sql .= (!empty($language_id)) ? " AND `language_id`=" . $language_id : "";
        $sql .= " ORDER BY rand() DESC LIMIT 0, $limit";

        $db->sql($sql);
        $result = $db->getResult();

        if (!empty($result)) {
            for ($i = 0; $i < count($result); $i++) {
                $result[$i]['image'] = (!empty($result[$i]['image'])) ? DOMAIN_URL . 'images/questions/' . $result[$i]['image'] : '';
                $result[$i]['optione'] = ($fn->is_option_e_mode_enabled() && $result[$i]['optione'] != null) ? trim($result[$i]['optione']) : '';
                $result[$i]['optiona'] = trim($result[$i]['optiona']);
                $result[$i]['optionb'] = trim($result[$i]['optionb']);
                $result[$i]['optionc'] = trim($result[$i]['optionc']);
                $result[$i]['optiond'] = trim($result[$i]['optiond']);
            }
            $response['error'] = "false";
            $response['data'] = $result;
        } else {
            $response['error'] = "true";
            $response['message'] = "No data found!";
        }
    } else {
        $response['error'] = "true";
        $response['message'] = "Please fill all the data and submit!";
    }
    print_r(json_encode($response));
}

// 9. get_random_questions()
if (isset($_POST['access_key']) && isset($_POST['get_random_questions'])) {

    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }

    $match_id = $db->escapeString($_POST['match_id']);

    if (isset($_POST['destroy_match']) && $_POST['destroy_match'] == 1) {
        $sql = "DELETE FROM `battle_questions` WHERE `match_id` = '" . $match_id . "'";
        $db->sql($sql);
        $response['error'] = "false";
        $response['message'] = "Battle destroyed successfully";
        print_r(json_encode($response));
        return false;

    }

    /* delete old data automatically */
    $sql = "DELETE FROM `battle_questions` WHERE date_created < ('" . $toDate . "')";
    $db->sql($sql);

    $language_id = (isset($_POST['language_id']) && is_numeric($_POST['language_id'])) ? $db->escapeString($_POST['language_id']) : '';

    if (isset($_POST['category']) && !empty($_POST['category'])) {
        $category = $db->escapeString($_POST['category']);
    } else {
        $category = '';
    }
    if (!checkBattleExists($match_id)) {
        /* if match does not exist read and store the questions */

        $sql = "SELECT * FROM `question` ";
        $sql .= (!empty($language_id)) ? " WHERE `language_id` = $language_id " : "";
        $sql .= (!empty($language_id)) ? ((!empty($category)) ? " AND `category`='" . $category . "' " : "") : ((!empty($category)) ? " WHERE `category`='" . $category . "' " : "" );
        $sql .= " ORDER BY RAND() LIMIT 0,10";
        $db->sql($sql);
        $res = $db->getResult();

        if (empty($res)) {
            $response['error'] = "true";
            $response['message'] = "No questions found to compete with each other!";
        } else {
            $questions = $db->escapeString(json_encode($res));
            $sql = "INSERT INTO `battle_questions` (`match_id`, `questions`) VALUES ('$match_id','$questions')";
            $db->sql($sql);

            foreach ($res as $row) {
                $row['image'] = (!empty($row['image'])) ? DOMAIN_URL . 'images/questions/' . $row['image'] : '';
                $row['optione'] = ($fn->is_option_e_mode_enabled() && $row['optione'] != null) ? $row['optione'] : '';
                $temp[] = $row;
            }
            $res = $temp;
            $response['error'] = "false";
            $response['message'] = "Data sent to devices via FCM 1";
            $response['data'] = $res;
            $data['data'] = $res;
        }
    } else {
        /* read the questions and send it. */
        $sql = "SELECT * FROM `battle_questions` WHERE `match_id` = '" . $match_id . "'";

        $db->sql($sql);
        $res = $db->getResult();

        $res = json_decode($res[0]['questions'], 1);
        foreach ($res as $row) {
            $row['image'] = (!empty($row['image'])) ? DOMAIN_URL . 'images/questions/' . $row['image'] : '';
            $row['optione'] = ($fn->is_option_e_mode_enabled() && $row['optione'] != null) ? $row['optione'] : '';
            $temp[] = $row;
        }
        $res[0]['questions'] = json_encode($temp);
        $response['error'] = "false";
        $response['message'] = "Data sent to devices via FCM";
        $response['data'] = json_decode($res[0]['questions']);
        $data['data'] = json_decode($res[0]['questions']);
    }
    print_r(json_encode($response));
}

// 10. get_random_questions_for_computer()
if (isset($_POST['access_key']) && isset($_POST['get_random_questions_for_computer'])) {

    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    /* if match does not exist read and store the questions */
    $language_id = (isset($_POST['language_id']) && !empty($_POST['language_id']) && is_numeric($_POST['language_id'])) ? $db->escapeString($_POST['language_id']) : '';

    if (isset($_POST['category']) && !empty($_POST['category'])) {
        $category = $db->escapeString($_POST['category']);
    } else {
        $category = '';
    }

    $sql = "SELECT * FROM `question` ";
    $sql .= (!empty($language_id)) ? " where `language_id` = $language_id " : "";
    $sql .= (!empty($language_id)) ? ((!empty($category)) ? " AND `category`='" . $category . "' " : "") : ((!empty($category)) ? " WHERE `category`='" . $category . "' " : "" );
    $sql .= " ORDER BY RAND() LIMIT 0,10";
    $db->sql($sql);
    $res = $db->getResult();

    if (empty($res)) {
        $response['error'] = "true";
        $response['message'] = "No questions found to compete with each other!";
    } else {
        $tempRow = array();
        foreach ($res as $row) {
            $tempRow['id'] = $row['id'];
            $tempRow['category'] = $row['category'];
            $tempRow['subcategory'] = $row['subcategory'];

            $tempRow['image'] = (!empty($row['image'])) ? DOMAIN_URL . 'images/questions/' . $row['image'] : '';
            $tempRow['question'] = $row['question'];
            $tempRow['question_type'] = $row['question_type'];
            $tempRow['optiona'] = $row['optiona'];
            $tempRow['optionb'] = $row['optionb'];
            $tempRow['optionc'] = $row['optionc'];
            $tempRow['optiond'] = $row['optiond'];
            $tempRow['optione'] = ($fn->is_option_e_mode_enabled() && $row['optione'] != null) ? $row['optione'] : '';
            $tempRow['answer'] = $row['answer'];

            $tempRow['note'] = $row['note'];
            $newresult[] = $tempRow;
        }
        $response['error'] = "false";
        $response['message'] = "Data sent to devices via FCM 1";
        $response['data'] = $newresult;
    }
    print_r(json_encode($response));
}

// 11. report_question()
if (isset($_POST['report_question']) && isset($_POST['access_key'])) {

    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (isset($_POST['question_id']) && isset($_POST['user_id']) && isset($_POST['message'])) {
        $user_id = $db->escapeString($_POST['user_id']);
        $question_id = $db->escapeString($_POST['question_id']);
        $message = $db->escapeString($_POST['message']);
        $data = array(
            'question_id' => $question_id,
            'user_id' => $user_id,
            'message' => $message,
            'date' => $toDateTime
        );
        $db->insert('question_reports', $data);  // Table name, column names and respective values
        $res = $db->getResult();

        $response['error'] = false;
        $response['message'] = "Report submitted successfully";
        $response['id'] = $res[0];
    } else {
        $response['error'] = true;
        $response['message'] = "Please fill all the data and submit!";
    }
    print_r(json_encode($response));
}

// 12. user_signup()
if (isset($_POST['access_key']) && isset($_POST['user_signup'])) {

    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (isset($_POST['type']) && isset($_POST['firebase_id']) && ($_POST['firebase_id'] != 'null') && ($_POST['firebase_id'] != 'NULL')) {

        $firebase_id = $db->escapeString($_POST['firebase_id']);
        $type = $db->escapeString($_POST['type']);
        $email = (isset($_POST['email'])) ? $db->escapeString($_POST['email']) : '';
        $name = filter_var((isset($_POST['name'])) ? $db->escapeString($_POST['name']) : '', FILTER_SANITIZE_STRING);
        $mobile = (isset($_POST['mobile'])) ? $db->escapeString($_POST['mobile']) : '';
        $profile = (isset($_POST['profile'])) ? $db->escapeString($_POST['profile']) : '';

        $fcm_id = (isset($_POST['fcm_id'])) ? $db->escapeString($_POST['fcm_id']) : '';
        $refer_code = filter_var((isset($_POST['refer_code'])) ? $db->escapeString($_POST['refer_code']) : '', FILTER_SANITIZE_STRING);
        $friends_code = filter_var((isset($_POST['friends_code'])) ? $db->escapeString($_POST['friends_code']) : '', FILTER_SANITIZE_STRING);
        $points = '0';
        $status = '1';
        $device_id = (isset($_POST['device_id'])) ? $db->escapeString($_POST['device_id']) : '';


        if ($firebase_id != '') {

            $getuid = checkUID($firebase_id);

            if ($getuid == TRUE) {
                if (!empty($friends_code)) {
                    $code = $fn->valid_friends_refer_code($friends_code);
                    if (!$code['is_valid']) {
                        $friends_code = '';
                    }
                }
                $sql = "SELECT * FROM users WHERE firebase_id='$firebase_id' AND email='$email' AND type='$type'";
                $db->sql($sql);
                $res = $db->getResult();
                if (!empty($res)) {
                    $user_id = $res[0]['id'];

                    $friends_code_is_used = $fn->check_friends_code_is_used_by_user($user_id);
                    if (!($friends_code_is_used['is_used']) && $friends_code != '') {
                        /* give coins to both the users 50 & 100 for each */
                        $sql = "UPDATE `users` SET `friends_code`='" . $friends_code . "', `coins` = `coins` + " . $coin_config['refer_coin'] . "  WHERE id = " . $res[0]['id'];
                        $db->sql($sql);
                        $resf = $db->getResult();


                        $data = array(
                            'uid' => $firebase_id,
                            'points' => $coin_config['refer_coin'],
                            'type' => 'Earn By Use refer Code',
                            'coin_status' => '0',
                            'user_id' => $res[0],
                            'date' => $toDateTime,//$datetime->format('Y\-m\-d\ h:i:s'),
                            'type_two' => 'Refer'
                        );
                        $db->insert('tbl_tracker', $data);  // Table name, column names and respective values
                        $res = $db->getResult();

                        $credited = $fn->credit_coins_to_friends_code($friends_code);
                    }
                    if (!empty($fcm_id)) {
                        $sql = " UPDATE `users` SET fcm_id='" . $fcm_id . "' WHERE `id` = " . $res[0]['id'];
                        $db->sql($sql);
                    }
                    if (!$fn->is_refer_code_set($user_id) && !empty($refer_code)) {
                        $sql = " UPDATE `users` SET refer_code='" . $refer_code . "' WHERE `id` = " . $res[0]['id'];
                        $db->sql($sql);
                    }

                    foreach ($res as $row) {
                        if (filter_var($row['profile'], FILTER_VALIDATE_URL) === false) {
                            // Not a valid URL. Its a image only or empty
                            $tempRow['profile'] = (!empty($row['profile'])) ? DOMAIN_URL . 'uploads/profile/' . $row['profile'] : '';
                        } else {
                            /* if it is a ur than just pass url as it is */
                            $tempRow['profile'] = $row['profile'];
                        }

                        $tempRow['user_id'] = $row['id'];
                        $tempRow['firebase_id'] = $row['firebase_id'];
                        $tempRow['name'] = $row['name'];
                        $tempRow['email'] = $row['email'];
                        $tempRow['mobile'] = $row['mobile'];
                        $tempRow['type'] = $row['type'];
                        $tempRow['fcm_id'] = $row['fcm_id'];
                        $tempRow['refer_code'] = $row['refer_code'];
                        $tempRow['coins'] = $row['coins'];
                        $tempRow['status'] = $row['status'];
                        $tempRow['date_registered'] = $row['date_registered'];
                        $tempRow['token'] = generate_token($row['firebase_id']);
                        $newresult[] = $tempRow;
                    }

                    $sql = "UPDATE `users` SET `device_id`='" . $device_id . "' WHERE `id`=" . $row['id'] . "";
                    $db->sql($sql);
                    $response['error'] = "false";
                    $response['message'] = "Successfully logged in";
                    $response['data'] = $newresult[0];
                } else {
                    $data = array(
                        'firebase_id' => $firebase_id,
                        'name' => $name,
                        'email' => $email,
                        'mobile' => $mobile,
                        'type' => $type,
                        'profile' => $profile,
                        'fcm_id' => $fcm_id,
                        'refer_code' => $refer_code,
                        'friends_code' => $friends_code,
                        'coins' => '0',
                        'device_id' => $device_id,
                        'status' => $status
                    );
                    $sql = $db->insert('users', $data);
                    $res = $db->getResult();

                    
                    $data = array(
                        'user_id' => "$res[0]",
                        'firebase_id' => $firebase_id,
                        'name' => $name,
                        'email' => $email,
                        'profile' => $profile,
                        'mobile' => $mobile,
                        'fcm_id' => $fcm_id,
                        'refer_code' => $refer_code,
                        'coins' => '0',
                        'type' => $type,

                        'status' => $status
                    );

                     $welcome_bonus = array(
                            'uid' => $firebase_id,
                            'points' => $coin_config['start_coin'],
                            'type' => 'Welcome Bonus Coin',
                            'coin_status' => '0',
                            'user_id' => $res[0],
                            'date' => $toDateTime,//$datetime->format('Y\-m\-d\ h:i:s'),
                            'type_two' => 'Welcome Bonus'
                        );
                        $db->insert('tbl_tracker', $welcome_bonus);  // Table name, column names and respective values
                    
                    
                    if ($friends_code != '') {
                        $data['coins'] = $coin_config['refer_coin'];
                        $sql = "UPDATE `users` SET `coins` = `coins` + " . $coin_config['refer_coin'] . "  WHERE `id` = " . $res[0];
                        $db->sql($sql);

                        $data = array(
                            'uid' => $firebase_id,
                            'points' => $coin_config['refer_coin'],
                            'type' => 'Earn By Use refer Code',
                            'coin_status' => '0',
                            'user_id' => $res[0],
                            'date' => $toDateTime,//$datetime->format('Y\-m\-d\ h:i:s'),
                            'type_two' => 'Refer'
                        );
                        $db->insert('tbl_tracker', $data);  // Table name, column names and respective values
                        $res = $db->getResult();

                        $credited = $fn->credit_coins_to_friends_code($friends_code);
                    }
                    $data['token'] =  generate_token($firebase_id);
                    $response['error'] = "false";
                    $response['message'] = "User Registered successfully";
                    $response['data'] = $data;
                }
            } else {
                $response['error'] = "true";
                $response['message'] = "Firebase ID Required";
            }
        } else {
            $response['error'] = "true";
            $response['message'] = "Firebase ID Required";
        }
    } else {
        $response['error'] = "true";
        $response['message'] = "Please pass all the fields";
    }
    print_r(json_encode($response));
}

// 13. get_user_by_id()
if (isset($_POST['access_key']) && isset($_POST['get_user_by_id'])) {

    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (isset($_POST['id'])) {
        $id = $db->escapeString($_POST['id']);
        
        $sql = "SELECT users.*,IFNULL((SELECT IFNULL(SUM(points),0) FROM tbl_tracker WHERE coin_status='0' AND tbl_tracker.uid=users.firebase_id) + (SELECT IFNULL(SUM(points),0) FROM tbl_tracker WHERE coin_status='1' AND tbl_tracker.uid=users.firebase_id),0) as TotalCoins FROM `users` WHERE users.id = $id ";

        $db->sql($sql);
        $result = $db->getResult();

        $sql = "SELECT r.score,r.user_rank FROM (SELECT s.*, @user_rank := @user_rank + 1 user_rank FROM ( SELECT user_id, sum(score) score  FROM monthly_leaderboard m join users u on u.id = m.user_id GROUP BY user_id ) s, (SELECT @user_rank := 0) init ORDER BY score DESC ) r INNER join users u on u.id = r.user_id WHERE r.user_id =" . $id;
        $db->sql($sql);
        $my_rank = $db->getResult();

        if (!empty($result)) {
                if (filter_var($result[0]['profile'], FILTER_VALIDATE_URL) === false) {
                    // Not a valid URL. Its a image only or empty
                    $result[0]['profile'] = (!empty($result[0]['profile'])) ? DOMAIN_URL . 'uploads/profile/' . $result[0]['profile'] : '';
                } else {
                    /* if it is a ur than just pass url as it is */
                    $result[0]['profile'] = $result[0]['profile'];
                }

                $sql = "SELECT * FROM `tbl_custom_coin` WHERE type='per_coin' OR type='amount' OR type='paypal_coin' OR type='paypal_amount' ";

                $db->sql($sql);
                $get_amount = $db->getResult();

                $user_coin = (!empty($result[0]['TotalCoins'])) ? $result[0]['TotalCoins'] : 0;
                $convert_amount  =  (float) 0;
                $paypal_convert_amount =  (float) 0;
                if (!empty($get_amount)) {
                    $per_coin =  floatval($get_amount[0]['coin']);
                    $amount = floatval($get_amount[1]['coin']) ;
                    $paypalCoin = floatval($get_amount[2]['coin']) ;
                    $paypalamount =  floatval($get_amount[3]['coin']) ;
                    $convert_amount = floatval((($user_coin * $amount) /($per_coin)));
                    $paypal_convert_amount =   (float)  (($user_coin * $paypalamount) / $paypalCoin);
                }
                //Get Free Coin Status
                $sql1 = "SELECT * FROM `tbl_daily_status` WHERE user_id='$id' AND type='1' AND date(todaydate)='$toDate'";
                $db->sql($sql1);
                $result1= $db->getResult();
                if ($result1) {
                    $result[0]['free_coin_status'] = '1';
                } else {
                    $result[0]['free_coin_status'] = '0';
                }
                //Get Daily Quiz Status
                $sql2 = "SELECT * FROM `tbl_daily_status` WHERE user_id='$id' AND type='0' AND date(todaydate)='$toDate'";
                $db->sql($sql2);
                $result2= $db->getResult();


                if ($result2) {
                    $result[0]['daily_quiz_status'] = '1';
                } else {
                    $result[0]['daily_quiz_status'] = '0';
                }

                $result[0]['Coin_Amount'] = round((string)$convert_amount, 2);
                $result[0]['Paypal_Coin_Amount'] = round((string)$paypal_convert_amount, 2);
                $result[0]['all_time_score'] = (isset($my_rank[0]['score'])) ? $my_rank[0]['score'] : "0";
                $result[0]['all_time_rank'] = (isset($my_rank[0]['user_rank'])) ? $my_rank[0]['user_rank'] : "0";

                $response['error'] = "false";
                $response['data'] = $result[0];
            

        } else {
            $response['error'] = "true";
            $response['message'] = "No data found!";
        }
    } else {
        $response['error'] = "true";
        $response['message'] = "Please Pass all the fields!";
    }
    print_r(json_encode($response));
}

// 14. update_fcm_id()
if (isset($_POST['access_key']) && isset($_POST['update_fcm_id'])) {

    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (isset($_POST['fcm_id']) && isset($_POST['user_id']) && !empty($_POST['user_id']) && !empty($_POST['fcm_id'])) {
        $fcm_id = $db->escapeString($_POST['fcm_id']);
        $id = $db->escapeString($_POST['user_id']);

        $sql = "UPDATE `users` SET `fcm_id`='" . $fcm_id . "' WHERE `id`='" . $id . "'";
        $db->sql($sql);
        $response['error'] = "false";
        $response['message'] = " FCM updated successfully";
    } else {
        $response['error'] = "true";
        $response['message'] = "Please pass all the fields";
    }
    print_r(json_encode($response));
}

// 15. upload_profile_image()
if (isset($_POST['access_key']) && isset($_POST['upload_profile_image'])) {

    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (isset($_POST['user_id'])) {
        // Path to move uploaded files
        $target_path = "uploads/profile/";
        // Folder create if not exists
        if (!is_dir($target_path)) {
            mkdir($target_path, 0777, true); /* 3rd parameter is required in recursive mode */
        }
        $id = $db->escapeString($_POST['user_id']);
        $old_profile = '';

        $sql = "select `profile` from `users` where id = " . $id;
        $db->sql($sql);
        $res = $db->getResult();

        if (!empty($res) && isset($res[0]['profile'])) {
            if (filter_var($res[0]['profile'], FILTER_VALIDATE_URL) === FALSE) {
                // Not a valid URL. Its an image only
                $old_profile = (!empty($res[0]['profile'])) ? $target_path . '' . $res[0]['profile'] : '';
            }
        }

        // final file url that is being uploaded
        $file_upload_url = $target_path;

        if (isset($_FILES['image']['name'])) {
            $allowedExts = array("gif", "jpeg", "jpg", "png", "JPEG", "JPG", "PNG");

            $extension = pathinfo($_FILES["image"]["name"])['extension'];
            if (!(in_array($extension, $allowedExts))) {
                $response['error'] = "true";
                $response['message'] = 'Image type is invalid';
                echo json_encode($response);
                return false;
            }
            $filename = microtime(true) . '.' . strtolower($extension);
            $target_path = $target_path . $filename;

            try {
                // Throws exception incase file is not being moved
                if (!move_uploaded_file($_FILES['image']['tmp_name'], $target_path)) {
                    // make error flag true
                    $response['error'] = "true";
                    $response['message'] = 'Could not move the file!';
                }
                $sql = "UPDATE `users` SET `profile`='" . $filename . "' WHERE `id`=" . $id . "";
                $db->sql($sql);
                if (!empty($old_profile) && file_exists($old_profile)) {
                    unlink($old_profile);
                }

                // File successfully uploaded
                $response['error'] = "false";
                $response['message'] = 'File uploaded successfully!';
                $response['file_path'] = DOMAIN_URL . $file_upload_url . $filename;
            } catch (Exception $e) {
                // Exception occurred. Make error flag true
                $response['error'] = "true";
                $response['message'] = $e->getMessage();
            }
        } else {
            // File parameter is missing
            $response['error'] = "true";
            $response['message'] = 'Not received any file!';
        }
    } else {
        $response['error'] = "true";
        $response['message'] = "Please pass all the fields";
    }
    print_r(json_encode($response));
}

// 16. update_profile()
if (isset($_POST['access_key']) && isset($_POST['update_profile'])) {

    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (isset($_POST['user_id']) && !empty($_POST['user_id']) && isset($_POST['name'])) {

        $id = $db->escapeString($_POST['user_id']);
        $name = $db->escapeString($_POST['name']);

        $sql = "UPDATE `users` SET `name`='" . $name . "'";
        $sql .= (isset($_POST['mobile']) && !empty($_POST['mobile'])) ? " ,`mobile`='" . $_POST['mobile'] . "'" : "";
        $sql .= (isset($_POST['email']) && !empty($_POST['email'])) ? " ,`email`='" . $_POST['email'] . "'" : "";
        $sql .= " WHERE `id`='" . $id . "'";
        $db->sql($sql);

        $response['error'] = "false";
        $response['message'] = "Profile updated successfully";
    } else {
        $response['error'] = "true";
        $response['message'] = "Please pass all the fields";
    }
    print_r(json_encode($response));
}

// 17. set_monthly_leaderboard()
if (isset($_POST['access_key']) && isset($_POST['set_monthly_leaderboard'])) {

    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (!empty($_POST['access_key']) && !empty($_POST['user_id']) && isset($_POST['score']) && $_POST['score'] != '') {
        $user_id = $db->escapeString($_POST['user_id']);
        $score = $db->escapeString($_POST['score']);

        set_monthly_leaderboard($user_id, $score);

        $response['error'] = "false";
        $response['message'] = "successfully update score";
    } else {
        $response['error'] = "true";
        $response['message'] = "Please pass all the fields";
    }
    print_r(json_encode($response));
}

// 18. get_monthly_leaderboard()
if (isset($_POST['access_key']) && isset($_POST['get_monthly_leaderboard'])) {

    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (empty($_POST['date']) || !isset($_POST['date'])) {
        $response['error'] = "true";
        $response['message'] = "Please fill all the data and submit!";
        print_r(json_encode($response));
        return false;
    }

    $offset = (isset($_POST['offset']) && !empty($_POST['offset']) && is_numeric($_POST['offset'])) ? $db->escapeString($_POST['offset']) : 0;
    $limit = (isset($_POST['limit']) && !empty($_POST['limit']) && is_numeric($_POST['limit'])) ? $db->escapeString($_POST['limit']) : 25;

    $date = $db->escapeString($_POST['date']);

    /* get the total no of records */
    $sql = "SELECT COUNT(m.id) as `total` FROM `monthly_leaderboard` m JOIN users ON users.id = m.user_id WHERE ( MONTH( m.date_created ) = MONTH('" . $date . "') AND YEAR( m.date_created ) = YEAR('" . $date . "') ) ORDER BY m.score DESC";
    $db->sql($sql);
    $total = $db->getResult();

    $sql = "SELECT r.*,u.email,u.name,u.profile FROM (
        SELECT s.*, @user_rank := @user_rank + 1 user_rank FROM
        ( SELECT user_id, sum(score) score FROM monthly_leaderboard m join users u on u.id = m.user_id
         WHERE ( MONTH( m.date_created ) = month('" . $date . "') AND YEAR( m.date_created ) = year('" . $date . "') )
         GROUP BY user_id) s,
        (SELECT @user_rank := 0) init ORDER BY score DESC
    ) r
    INNER join users u on u.id = r.user_id ORDER BY r.user_rank ASC LIMIT $offset,$limit";
    $db->sql($sql);
    $res = $db->getResult();

    if (isset($_POST['user_id']) && !empty($_POST['user_id'])) {
        $user_id = $db->escapeString($_POST['user_id']);
        $sql = "SELECT r.*,u.email,u.name,u.profile FROM (
        SELECT s.*, @user_rank := @user_rank + 1 user_rank FROM
        ( SELECT user_id, sum(score) score FROM monthly_leaderboard m join users u on u.id = m.user_id
         WHERE ( MONTH( m.date_created ) = month('" . $date . "') AND YEAR( m.date_created ) = year('" . $date . "') )
         GROUP BY user_id) s,
        (SELECT @user_rank := 0) init ORDER BY score DESC
    ) r
    INNER join users u on u.id = r.user_id WHERE user_id =" . $user_id . " ORDER BY r.user_rank ASC LIMIT $offset,$limit";
        $db->sql($sql);
        $my_rank = $db->getResult();
        if (!empty($my_rank)) {
            if (filter_var($my_rank[0]['profile'], FILTER_VALIDATE_URL) === FALSE) {
                // Not a valid URL. Its a image only or empty
                $my_rank[0]['profile'] = (!empty($my_rank[0]['profile'])) ? DOMAIN_URL . 'uploads/profile/' . $my_rank[0]['profile'] : '';
            }
            $user_rank['my_rank'] = $my_rank[0];
            array_unshift($res, $user_rank);
        } else {
            $my_rank = array(
                'id' => $user_id,
                'user_rank' => 0
            );
            $user_rank['my_rank'] = $my_rank;
            array_unshift($res, $user_rank);
        }
    }

    if (!empty($res)) {
        foreach ($res as $row) {
            if (isset($row['profile'])) {
                if (filter_var($row['profile'], FILTER_VALIDATE_URL) === FALSE) {
                    // Not a valid URL. Its a image only or empty
                    $row['profile'] = (!empty($row['profile'])) ? DOMAIN_URL . 'uploads/profile/' . $row['profile'] : '';
                }
            }
            $tempRow[] = $row;
        }
        $response['error'] = "false";
        $response['total'] = $total[0]['total'];
        $response['data'] = $tempRow;
    } else {
        $response['error'] = "true";
        $response['message'] = "Data not found";
    }
    print_r(json_encode($response));
}

// 19. get_datewise_leaderboard()
if (isset($_POST['access_key']) && isset($_POST['get_datewise_leaderboard'])) {

    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }

    if ((empty($_POST['from']) || !isset($_POST['from'])) || ( empty($_POST['to']) || !isset($_POST['to']))) {
        $response['error'] = "true";
        $response['message'] = "Please fill all the data and submit!";
        print_r(json_encode($response));
        return false;
    }

    $from = $db->escapeString($_POST['from']);
    $to = $db->escapeString($_POST['to']);

    $limit = (isset($_POST['limit']) && !empty($_POST['limit']) && is_numeric($_POST['limit'])) ? $db->escapeString($_POST['limit']) : 25;
    $offset = (isset($_POST['offset']) && !empty($_POST['offset']) && is_numeric($_POST['offset'])) ? $db->escapeString($_POST['offset']) : 0;

    /* get the total no of records */
    $sql = "SELECT COUNT(d.id) as `total` FROM `daily_leaderboard` d JOIN users ON users.id = d.user_id where (DATE(`date_created`) BETWEEN date('" . $from . "') and date('" . $to . "')) ORDER BY score DESC";
    $db->sql($sql);
    $total = $db->getResult();

    $sql = "SELECT r.*,u.email,u.name,u.profile FROM ( SELECT s.*, @user_rank := @user_rank + 1 user_rank FROM ( SELECT user_id, score FROM daily_leaderboard d join users u on u.id = d.user_id WHERE ((DATE(d.date_created) BETWEEN date('" . $from . "') and date('" . $to . "')))) s, (SELECT @user_rank := 0) init ORDER BY score DESC ) r INNER join users u on u.id = r.user_id ORDER BY r.user_rank ASC LIMIT $offset,$limit";
    $db->sql($sql);
    $res = $db->getResult();

    if (isset($_POST['user_id']) && !empty($_POST['user_id'])) {
        $user_id = $db->escapeString($_POST['user_id']);

        $sql = "SELECT r.*,u.email,u.name,u.profile FROM ( SELECT s.*, @user_rank := @user_rank + 1 user_rank FROM ( SELECT user_id, score FROM daily_leaderboard d join users u on u.id = d.user_id WHERE ((DATE(d.date_created) BETWEEN date('" . $from . "') and date('" . $to . "')))) s, (SELECT @user_rank := 0) init ORDER BY score DESC ) r INNER join users u on u.id = r.user_id WHERE user_id =" . $user_id . "";
        $db->sql($sql);
        $my_rank = $db->getResult();
        if (!empty($my_rank)) {
            if (filter_var($my_rank[0]['profile'], FILTER_VALIDATE_URL) === FALSE) {
                // Not a valid URL. Its a image only or empty
                $my_rank[0]['profile'] = (!empty($my_rank[0]['profile'])) ? DOMAIN_URL . 'uploads/profile/' . $my_rank[0]['profile'] : '';
            }
            $user_rank['my_rank'] = $my_rank[0];
            array_unshift($res, $user_rank);
        } else {
            $my_rank = array(
                'id' => $user_id,
                'user_rank' => 0
            );
            $user_rank['my_rank'] = $my_rank;
            array_unshift($res, $user_rank);
        }
    }

    if (!empty($res)) {
        foreach ($res as $row) {
            if (isset($row['profile'])) {
                if (filter_var($row['profile'], FILTER_VALIDATE_URL) === FALSE) {
                    // Not a valid URL. Its a image only or empty
                    $row['profile'] = (!empty($row['profile'])) ? DOMAIN_URL . 'uploads/profile/' . $row['profile'] : '';
                }
            }
            $tempRow[] = $row;
        }
        $response['error'] = "false";
        $response['total'] = $total[0]['total'];
        $response['data'] = $tempRow;
    } else {
        $response['error'] = "true";
        $response['message'] = "Data not found";
    }
    print_r(json_encode($response));
}

// 20. get_global_leaderboard()
if (isset($_POST['access_key']) && isset($_POST['get_global_leaderboard'])) {

    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }

    $limit = (isset($_POST['limit']) && !empty($_POST['limit']) && is_numeric($_POST['limit'])) ? $db->escapeString($_POST['limit']) : 25;
    $offset = (isset($_POST['offset']) && !empty($_POST['offset']) && is_numeric($_POST['offset'])) ? $db->escapeString($_POST['offset']) : 0;

    /* get the total no of records */
    //$sql = "SELECT COUNT(m.id) as `total` FROM `monthly_leaderboard` m ";
    $sql = "SELECT COUNT(DISTINCT m.user_id) as `total` FROM `monthly_leaderboard` m JOIN users u ON u.id=m.user_id";
    $db->sql($sql);
    $total = $db->getResult();

    $sql = "SELECT r.*,u.email,u.name,u.profile FROM ( SELECT s.*, @user_rank := @user_rank + 1 user_rank FROM ( SELECT user_id, sum(score) score FROM monthly_leaderboard m join users u on u.id = m.user_id GROUP BY user_id) s, (SELECT @user_rank := 0) init ORDER BY score DESC) r INNER join users u on u.id = r.user_id ORDER BY r.user_rank ASC LIMIT $offset,$limit";
    $db->sql($sql);
    $res = $db->getResult();

    if (isset($_POST['user_id']) && !empty($_POST['user_id'])) {
        $user_id = $db->escapeString($_POST['user_id']);

        $sql = "SELECT r.*,u.email,u.name,u.profile FROM (SELECT s.*, @user_rank := @user_rank + 1 user_rank FROM ( SELECT user_id, sum(score) score FROM monthly_leaderboard m join users u on u.id = m.user_id GROUP BY user_id ) s, (SELECT @user_rank := 0) init ORDER BY score DESC ) r INNER join users u on u.id = r.user_id WHERE r.user_id =" . $user_id;
        $db->sql($sql);
        $my_rank = $db->getResult();
        if (!empty($my_rank)) {
            if (filter_var($my_rank[0]['profile'], FILTER_VALIDATE_URL) === FALSE) {
                // Not a valid URL. Its a image only or empty
                $my_rank[0]['profile'] = (!empty($my_rank[0]['profile'])) ? DOMAIN_URL . 'uploads/profile/' . $my_rank[0]['profile'] : '';
            }

            $user_rank['my_rank'] = $my_rank[0];
            array_unshift($res, $user_rank);
        } else {
            $my_rank = array(
                'id' => $user_id,
                'user_rank' => 0,
            );
            $user_rank['my_rank'] = $my_rank;
            array_unshift($res, $user_rank);
        }
    }

    if (!empty($res)) {
        foreach ($res as $row) {
            if (isset($row['profile'])) {
                if (filter_var($row['profile'], FILTER_VALIDATE_URL) === FALSE) {
                    // Not a valid URL. Its a image only or empty
                    $row['profile'] = (!empty($row['profile'])) ? DOMAIN_URL . 'uploads/profile/' . $row['profile'] : '';
                }
            }
            $tempRow[] = $row;
        }
        $response['error'] = "false";
        $response['total'] = $total[0]['total'];
        $response['data'] = $tempRow;
    } else {
        $response['error'] = "true";
        $response['message'] = "Data not found";
    }
    print_r(json_encode($response));
}

// 21. get_system_configurations()
if (isset($_POST['access_key']) && isset($_POST['get_system_configurations'])) {

    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (!empty($config)) {
        $response['error'] = "false";
        $response['data'] = $config;
    } else {
        $response['error'] = "true";
        $response['message'] = "No configurations found yet!";
    }
    print_r(json_encode($response));
}

// 22. get_about_us()
if (isset($_POST['access_key']) && isset($_POST['get_about_us']) && $_POST['get_about_us'] == 1) {

    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }

    if (!empty($_POST['access_key'])) {
        $sql = "SELECT * FROM `settings` WHERE type='about_us'";
        $db->sql($sql);
        $res = $db->getResult();
        if (!empty($res)) {
            $response['error'] = "false";
            $response['data'] = $res[0]['message'];
        } else {
            $response['error'] = "true";
            $response['message'] = "No data found!";
        }
    } else {
        $response['error'] = "true";
        $response['message'] = "Please pass all the fields";
    }
    print_r(json_encode($response));
}

// 23. get_privacy_policy_settings()
if (isset($_POST['access_key']) && isset($_POST['privacy_policy_settings']) && $_POST['privacy_policy_settings'] == 1) {

    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (!empty($_POST['access_key'])) {
        $sql = "SELECT * FROM `settings` WHERE type='privacy_policy'";
        $db->sql($sql);
        $res = $db->getResult();
        if (!empty($res)) {
            $response['error'] = "false";
            $response['data'] = $res[0]['message'];
        } else {
            $response['error'] = "true";
            $response['message'] = "No data found!";
        }
    } else {
        $response['error'] = "true";
        $response['message'] = "Please pass all the fields";
    }
    print_r(json_encode($response));
}

// 24. get_terms_conditions_settings()
if (isset($_POST['access_key']) && isset($_POST['get_terms_conditions_settings']) && $_POST['get_terms_conditions_settings'] == 1) {

    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (!empty($_POST['access_key'])) {
        $sql = "SELECT * FROM `settings` WHERE type='terms_conditions'";
        $db->sql($sql);
        $res = $db->getResult();
        if (!empty($res)) {
            $response['error'] = "false";
            $response['data'] = $res[0]['message'];
        } else {
            $response['error'] = "true";
            $response['message'] = "No data found!";
        }
    } else {
        $response['error'] = "true";
        $response['message'] = "Please pass all the fields";
    }
    print_r(json_encode($response));
}

// 25. get_instructions()
if (isset($_POST['access_key']) && isset($_POST['get_instructions']) && $_POST['get_instructions'] == 1) {

    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (!empty($_POST['access_key'])) {
        $sql = "SELECT * FROM `settings` WHERE type='instructions'";
        $db->sql($sql);
        $res = $db->getResult();
        if (!empty($res)) {
            $response['error'] = "false";
            $response['data'] = $res[0]['message'];
        } else {
            $response['error'] = "true";
            $response['message'] = "No data found!";
        }
    } else {
        $response['error'] = "true";
        $response['message'] = "Please pass all the fields";
    }
    print_r(json_encode($response));
}

// 26. get_notifications()
if (isset($_POST['access_key']) && isset($_POST['get_notifications'])) {

    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }

    $limit = (isset($_POST['limit']) && !empty($_POST['limit']) && is_numeric($_POST['limit'])) ? $db->escapeString($_POST['limit']) : 10;
    $offset = (isset($_POST['offset']) && !empty($_POST['offset']) && is_numeric($_POST['offset'])) ? $db->escapeString($_POST['offset']) : 0;

    $sort = (isset($_POST['sort']) && !empty($_POST['sort'])) ? $db->escapeString($_POST['sort']) : 'id';
    $order = (isset($_POST['order']) && !empty($_POST['order'])) ? $db->escapeString($_POST['order']) : 'DESC';

    $sql = "SELECT * FROM `notifications` m where users = 'all' ORDER BY $sort $order limit $offset,$limit";
    $db->sql($sql);
    $result = $db->getResult();
    if (!empty($result)) {
        for ($i = 0; $i < count($result); $i++) {
            if (filter_var($result[$i]['image'], FILTER_VALIDATE_URL) === FALSE) {
                /* Not a valid URL. Its a image only or empty */
                $result[$i]['image'] = (!empty($result[$i]['image'])) ? DOMAIN_URL . 'images/notifications/' . $result[$i]['image'] : '';
            } else {
                /* if it is a ur than just pass url as it is */
                $result[$i]['image'] = $result[$i]['image'];
            }
        }
        $response['error'] = "false";
        $response['data'] = $result;
    } else {
        $response['error'] = "true";
        $response['message'] = "No notifications to read.";
    }
    print_r(json_encode($response));
}


// 29. set_users_statistics()
if (isset($_POST['access_key']) && isset($_POST['set_users_statistics'])) {

    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (!empty($_POST['access_key']) && !empty($_POST['user_id']) && isset($_POST['category_id']) && isset($_POST['questions_answered']) && isset($_POST['correct_answers']) && $_POST['ratio'] != "") {

        $user_id = $db->escapeString($_POST['user_id']);
        $questions_answered = $db->escapeString($_POST['questions_answered']);
        $correct_answers = $db->escapeString($_POST['correct_answers']);
        $category_id = $db->escapeString($_POST['category_id']);
        $ratio = $db->escapeString($_POST['ratio']);

        // update users coins if set
        if (isset($_POST['coins']) && $_POST['coins'] != '' && is_numeric($_POST['coins'])) {
            $coins = $db->escapeString($_POST['coins']);
            $sql = "UPDATE `users` SET `coins` = $coins  WHERE id = " . $user_id;
            $db->sql($sql);
        }

        $sql = "SELECT * FROM `users_statistics` WHERE `user_id`=" . $user_id . "";
        $db->sql($sql);
        $result1 = $db->getResult();

        if (!empty($result1)) {
            $qa = $result1[0]['questions_answered'];
            $ca = $result1[0]['correct_answers'];
            $sc = $result1[0]['strong_category'];
            $r1 = $result1[0]['ratio1'];
            $wc = $result1[0]['weak_category'];
            $r2 = $result1[0]['ratio2'];
            $bp = $result1[0]['best_position'];

            $sql1 = "SELECT r.* FROM "
                    . "(SELECT s.*, @user_rank := @user_rank + 1 user_rank  FROM "
                    . "(SELECT user_id, sum(score) score FROM monthly_leaderboard m GROUP BY user_id ) s, "
                    . "(SELECT @user_rank := 0) init ORDER BY score DESC ) r  "
                    . "INNER join users u on u.id = r.user_id WHERE r.user_id =" . $user_id;
            $db->sql($sql1);
            $my_rank = $db->getResult();
            $rank1 = $my_rank[0]['user_rank'];
            if ($rank1 < $bp || $bp == 0) {
                $bp = $rank1;
                $sql = "UPDATE `users_statistics` SET `best_position`= '" . $bp . "' WHERE user_id=" . $user_id;
                $db->sql($sql);
            }

            if ($ratio > 50) {
                /* update strong category */
                /* when ratio is > 50 he is strong in this particular category */
                $sql = "UPDATE `users_statistics` SET `questions_answered`= `questions_answered` + '" . $questions_answered . "', `correct_answers`= `correct_answers` + '" . $correct_answers . "',";
                $sql .= ( $ratio > $r1 || $sc == 0 ) ? "`strong_category`= '" . $category_id . "', `ratio1`= '" . $ratio . "', " : "";
                $sql .= ( $wc == $category_id ) ? "`weak_category`= '0', " : "";
                $sql .= "`best_position`= '" . $bp . "' WHERE user_id=" . $user_id;
                $db->sql($sql);

                $response['error'] = "false";
                $response['message'] = "Strong Updated successfully";
            } else {
                /* update weak category */
                /* when ratio is < 50 he is weak in this particular category */
                $sql = "UPDATE `users_statistics` SET `questions_answered`= `questions_answered` + '" . $questions_answered . "', `correct_answers`= `correct_answers` + '" . $correct_answers . "',";
                $sql .= ( $ratio < $r2 || $wc == 0 ) ? "`weak_category`= '" . $category_id . "',`ratio2`= '" . $ratio . "'," : "";
                $sql .= ( $sc == $category_id ) ? "`strong_category`= '0', " : "";
                $sql .= " `best_position`= '" . $bp . "' WHERE user_id=" . $user_id;
                $db->sql($sql);
                $response['error'] = "false";
                $response['message'] = "Weak Updated successfully";
            }
        } else {
            if ($ratio > 50) {
                $sql = "INSERT INTO `users_statistics` (`user_id`, `questions_answered`,`correct_answers`, `strong_category`, `ratio1`, `weak_category`, `ratio2`, `best_position`) VALUES ('" . $user_id . "','" . $questions_answered . "','" . $correct_answers . "','" . $category_id . "','" . $ratio . "','0','0','0')";
                $db->sql($sql);
                $response['error'] = "false";
                $response['message'] = "Strong inserted successfully";
            } else {
                $sql = "INSERT INTO `users_statistics` (`user_id`, `questions_answered`,`correct_answers`, `strong_category`, `ratio1`, `weak_category`, `ratio2`, `best_position`) VALUES ('" . $user_id . "','" . $questions_answered . "','" . $correct_answers . "','0','0','" . $category_id . "','" . $ratio . "','0')";
                $db->sql($sql);
                $response['error'] = "false";
                $response['message'] = "Weak inserted successfully";
            }
        }
    } else {
        $response['error'] = "true";
        $response['message'] = "Please pass all the fields";
    }
    print_r(json_encode($response));
}

// 30. get_users_statistics()
if (isset($_POST['access_key']) && isset($_POST['get_users_statistics'])) {

    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (isset($_POST['user_id'])) {
        $user_id = $db->escapeString($_POST['user_id']);
        $sql = "SELECT us.*,u.name,u.profile,(SELECT category_name FROM category c WHERE c.id=us.strong_category) as strong_category, (SELECT category_name FROM category c WHERE c.id=us.weak_category) as weak_category FROM `users_statistics` us LEFT JOIN users u on u.id = us.user_id WHERE `user_id`=" . $user_id;
        $db->sql($sql);
        $result = $db->getResult();

        if (!empty($result)) {
            if ($result[0]['strong_category'] == null) {
                $result[0]['strong_category'] = "0";
            }
            if ($result[0]['weak_category'] == null) {
                $result[0]['weak_category'] = "0";
            }
            if (filter_var($result[0]['profile'], FILTER_VALIDATE_URL) === FALSE) {
                // Not a valid URL. Its a image only or empty
                $result[0]['profile'] = (!empty($result[0]['profile'])) ? DOMAIN_URL . 'uploads/profile/' . $result[0]['profile'] : '';
            } else {
                /* if it is a ur than just pass url as it is */
                $result[0]['profile'] = $result[0]['profile'];
            }
            $response['error'] = "false";
            $response['data'] = $result[0];
        } else {
            $response['error'] = "true";
            $response['message'] = "No data found!";
        }
    } else {
        $response['error'] = "true";
        $response['message'] = "Please Pass all the fields!";
    }
    print_r(json_encode($response));
}

// 31. set_played_status()
if (isset($_POST['access_key']) && isset($_POST['set_played_status'])) {

    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (!empty($_POST['access_key']) && !empty($_POST['user_id']) && !empty($_POST['category'])) {
        $user_id = $db->escapeString($_POST['user_id']);
        $category = (empty($_POST['category'])) ? 0 : $db->escapeString($_POST['category']);
        $subcategory = (empty($_POST['subcategory'])) ? 0 : $db->escapeString($_POST['subcategory']);

        $sql1 = "SELECT * FROM `tbl_cat_sub_cat_complete` WHERE user_id='$user_id' AND category ='$category' AND subcategory='$subcategory'";
         $db->sql($sql1);
         $result1= $db->getResult();

         if(empty($result1)){
             $sql = 'INSERT INTO `tbl_cat_sub_cat_complete` (`user_id`, `category`, `subcategory`) VALUES (' . $user_id . ',' . $category . ',' . $subcategory . ')';
             $db->sql($sql);
             $response['error'] = "false";
             $response['message'] = "successfully insert data";
         }else{
            $response['error'] = "true";
            $response['message'] = "Record Insert Already";
         }






    } else {
        $response['error'] = "true";
        $response['message'] = "Please pass all the fields";
    }
    print_r(json_encode($response));
}

// 32. get_played_status()
if (isset($_POST['access_key']) && isset($_POST['get_played_status'])) {

    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (!empty($_POST['access_key']) && !empty($_POST['user_id']) && !empty($_POST['category'])) {
        $user_id = $db->escapeString($_POST['user_id']);
        $category = (empty($_POST['category'])) ? 0 : $db->escapeString($_POST['category']);
        $subcategory = (empty($_POST['subcategory'])) ? 0 : $db->escapeString($_POST['subcategory']);

        $sql = "SELECT * FROM tbl_cat_sub_cat_complete WHERE user_id='$user_id' AND category='$category' AND subcategory='$subcategory'";
        $db->sql($sql);
        $result = $db->getResult();

        if (!empty($result)) {
            $response['error'] = "false";
            $response['data'] = $result[0];
        } else {

            $response['error'] = "true";
            $response['message'] = "No Data Found";
        }
    } else {
        $response['error'] = "true";
        $response['message'] = "Please pass all the fields";
    }
    print_r(json_encode($response));
}


// 33. get_daily_quiz()
if (isset($_POST['access_key']) && isset($_POST['get_daily_quiz'])) {

    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (isset($_POST['get_daily_quiz'])) {

        $questions = $response = array();
        $language_id = (isset($_POST['language_id']) && is_numeric($_POST['language_id'])) ? $db->escapeString($_POST['language_id']) : '0';

        $sql = "SELECT * from daily_quiz WHERE date_published='$toDate' AND `language_id`=" . $language_id . "";
        $db->sql($sql);
        $res = $db->getResult();
        if (!empty($res)) {
            $questions = $res[0]['questions_id'];

            $sql = "SELECT * FROM `question` WHERE `id` IN (" . $questions . ") ORDER BY FIELD(id," . $questions . ")";
            $db->sql($sql);
            $result = $db->getResult();

            if (!empty($result)) {
                for ($i = 0; $i < count($result); $i++) {
                    $result[$i]['image'] = (!empty($result[$i]['image'])) ? DOMAIN_URL . 'images/questions/' . $result[$i]['image'] : '';
                    $result[$i]['optione'] = ($fn->is_option_e_mode_enabled() && $result[$i]['optione'] != null) ? trim($result[$i]['optione']) : '';
                    $result[$i]['optiona'] = trim($result[$i]['optiona']);
                    $result[$i]['optionb'] = trim($result[$i]['optionb']);
                    $result[$i]['optionc'] = trim($result[$i]['optionc']);
                    $result[$i]['optiond'] = trim($result[$i]['optiond']);
                }
                $response['error'] = "false";
                $response['data'] = $result;
            } else {
                $response['error'] = "true";
                $response['message'] = "No data found!";
            }
        } else {
            $response['error'] = "true";
            $response['message'] = "No data found!";
        }
    } else {
        $response['error'] = "true";
        $response['message'] = "Pass all mandatory fields";
    }
    print_r(json_encode($response));
}

// 34. get_user_coin_score() - get user details
if (isset($_POST['access_key']) && isset($_POST['get_user_coin_score'])) {

    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (isset($_POST['user_id'])) {
        $user_id = $db->escapeString($_POST['user_id']);
        $sql = "SELECT coins FROM `users` WHERE id = $user_id ";
        $db->sql($sql);
        $result = $db->getResult();

        $sql1 = "SELECT r.score,r.user_rank FROM (SELECT s.*, @user_rank := @user_rank + 1 user_rank FROM ( SELECT user_id, sum(score) score FROM monthly_leaderboard m GROUP BY user_id ) s, (SELECT @user_rank := 0) init ORDER BY score DESC ) r INNER join users u on u.id = r.user_id WHERE r.user_id =" . $user_id;
        $db->sql($sql1);
        $my_rank = $db->getResult();

        if (!empty($result)) {
            $result[0]['score'] = (isset($my_rank[0]['score'])) ? $my_rank[0]['score'] : 0;
            $response['error'] = "false";
            $response['data'] = $result[0];
        } else {
            $response['error'] = "true";
            $response['message'] = "No data found!";
        }
    } else {
        $response['error'] = "true";
        $response['message'] = "Please Pass all the fields!";
    }
    print_r(json_encode($response));
}

// 35. set_user_coin_score() - get user details
if (isset($_POST['access_key']) && isset($_POST['set_user_coin_score'])) {

    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (isset($_POST['user_id']) && isset($_POST['coins']) && !empty($_POST['user_id'])) {
        $user_id = $db->escapeString($_POST['user_id']);
        $coins = $db->escapeString($_POST['coins']);

        if (isset($_POST['score']) && !empty($_POST['score'])) {
            $sql = "SELECT id, user_id FROM `monthly_leaderboard` WHERE `user_id`=" . $user_id . " and month(monthly_leaderboard.date_created) = month('" . $toDate . "') and year(monthly_leaderboard.date_created) = year('" . $toDate . "') ";
            $db->sql($sql);
            $result = $db->getResult();
            $score = $db->escapeString($_POST['score']);
            set_monthly_leaderboard($user_id, $score);
        }

        $sql = "SELECT coins FROM `users` WHERE id = $user_id ";
        $db->sql($sql);
        $result = $db->getResult();

        $sql1 = "SELECT r.score,r.user_rank FROM (SELECT s.*, @user_rank := @user_rank + 1 user_rank FROM ( SELECT user_id, sum(score) score FROM monthly_leaderboard m GROUP BY user_id ) s, (SELECT @user_rank := 0) init ORDER BY score DESC ) r INNER join users u on u.id = r.user_id WHERE r.user_id =" . $user_id;
        $db->sql($sql1);
        $my_rank = $db->getResult();

        if (!empty($result)) {
            $result[0]['score'] = (isset($my_rank[0]['score'])) ? $my_rank[0]['score'] : 0;
            $response['error'] = "false";
            $response['message'] = "successfully insert record";
            $response['data'] = $result[0];
        } else {
            $response['error'] = "true";
            $response['message'] = "No data found!";
        }
    } else {
        $response['error'] = "true";
        $response['message'] = "Please Pass all the fields!";
    }
    print_r(json_encode($response));
}

// 36. get_contest()
if (isset($_POST['access_key']) && isset($_POST['get_contest'])) {

    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (isset($_POST['user_id']) && !empty($_POST['user_id'])) {
        $user_id = $db->escapeString($_POST['user_id']);

        /* selecting live quiz ids */
        $sql = "SELECT id FROM `contest` where ('$toDate') between CAST(`start_date` AS DATE) and CAST(`end_date` AS DATE)";
        $db->sql($sql);
        $result = $db->getResult();

        $live_type_ids = $past_type_ids = '';
        if (!empty($result)) {
            foreach ($result as $type_id) {
                $live_type_ids .= $type_id['id'] . ', ';
            }
            $live_type_ids = rtrim($live_type_ids, ', ');

            /* getting past quiz ids & its data which user has played */
            $sql = "SELECT `contest_id` FROM `contest_leaderboard` WHERE `contest_id` in ($live_type_ids) and `user_id` = $user_id ORDER BY `id` DESC";
            $db->sql($sql);
            $result = $db->getResult();

            if (!empty($result)) {
                foreach ($result as $type_id) {
                    $past_type_ids .= $type_id['contest_id'] . ', ';
                }
                $past_type_ids = rtrim($past_type_ids, ', ');

                $sql = "SELECT *, (select SUM(points) FROM contest_prize WHERE contest_prize.contest_id=contest.id) as points, (select count(contest_id) FROM contest_prize WHERE contest_prize.contest_id=contest.id) as top_users,(SELECT COUNT(*) from contest_leaderboard where contest_leaderboard.contest_id = contest.id ) as `participants` FROM `contest` WHERE `id` in ($past_type_ids) ORDER BY `id` DESC";
                $db->sql($sql);
                $past_result = $db->getResult();
                unset($result);
                foreach ($past_result as $quiz) {
                    $quiz['image'] = (!empty($quiz['image'])) ? DOMAIN_URL . 'images/contest/' . $quiz['image'] : '';
                    $quiz['start_date'] = date("d-M", strtotime($quiz['start_date']));
                    $quiz['end_date'] = date("d-M", strtotime($quiz['end_date']));
                    $s = "SELECT top_winner, points FROM `contest_prize` WHERE contest_id= " . $quiz['id'];
                    $db->sql($s);
                    $points = $db->getResult();
                    $quiz['points'] = $points;
                    $result[] = $quiz;
                }
                $past_result = $result;
                $response['past_contest']['error'] = false;
                
                $response['past_contest']['message'] = "Contest you have played";
                $response['past_contest']['data'] = (!empty($past_result)) ? $past_result : '';
            } else {
                $sql = "SELECT q.*, (select SUM(points) FROM contest_prize WHERE contest_prize.contest_id=q.id) as points, (select count(contest_id) FROM contest_prize WHERE contest_prize.contest_id=q.id) as top_users,(SELECT COUNT(*) from contest_leaderboard where l.contest_id = q.id )as `participants` FROM `contest_leaderboard` as l, `contest` as q WHERE l.user_id = '$user_id' and l.contest_id = q.id ORDER BY q.`id`  DESC";
                $db->sql($sql);
                $past_result = $db->getResult();
                if (!empty($past_result)) {
                    foreach ($past_result as $quiz) {
                        $quiz['image'] = (!empty($quiz['image'])) ? DOMAIN_URL . 'images/contest/' . $quiz['image'] : '';
                        $quiz['start_date'] = date("d-M", strtotime($quiz['start_date']));
                        $quiz['end_date'] = date("d-M", strtotime($quiz['end_date']));
                        $s = "SELECT top_winner, points FROM `contest_prize` WHERE contest_id= " . $quiz['id'];
                        $db->sql($s);
                        $points = $db->getResult();
                        $quiz['points'] = $points;
                        $result[] = $quiz;
                    }
                    $past_result = $result;
                    $response['past_contest']['error'] = false;
                    $response['past_contest']['message'] = "Contest you have played";
                    $response['past_contest']['data'] = (!empty($past_result)) ? $past_result : '';
                } else {
                    $response['past_contest']['error'] = true;
                    $response['past_contest']['message'] = "You have not played any contest yet. Go and play the contest once there is a live contest";
                }
            }

            /* getting all quiz details by ids retrieved */
            $sql = (empty($past_type_ids)) ?
                    "SELECT *, (select SUM(points) FROM contest_prize WHERE contest_prize.contest_id=contest.id) as points, (select count(contest_id) FROM contest_prize WHERE contest_prize.contest_id=contest.id) as top_users,(SELECT COUNT(*) from contest_leaderboard where contest_leaderboard.contest_id = contest.id )as `participants` FROM `contest` WHERE `id` in ($live_type_ids) AND status='1' ORDER BY `id` DESC" :
                    "SELECT *, (select SUM(points) FROM contest_prize WHERE contest_prize.contest_id=contest.id) as points, (select count(contest_id) FROM contest_prize WHERE contest_prize.contest_id=contest.id) as top_users,(SELECT COUNT(*) from contest_leaderboard where contest_leaderboard.contest_id = contest.id )as `participants` FROM `contest` WHERE `id` in ($live_type_ids) and `id` not in ($past_type_ids) AND status='1' ORDER BY `id` DESC"
            ;

            $db->sql($sql);
            $live_result = $db->getResult();
            $result = array();
            if (!empty($live_result)) {
                foreach ($live_result as $quiz) {
                    $quiz['image'] = (!empty($quiz['image'])) ? DOMAIN_URL . 'images/contest/' . $quiz['image'] : '';
                    $quiz['start_date'] = date("d-M", strtotime($quiz['start_date']));
                    $quiz['end_date'] = date("d-M", strtotime($quiz['end_date']));
                    $s = "SELECT top_winner, points FROM `contest_prize` WHERE contest_id= " . $quiz['id'];
                    $db->sql($s);
                    $points = $db->getResult();
                    $quiz['points'] = $points;
                    $result[] = $quiz;
                }
                $live_result = $result;
                $response['live_contest']['error'] = false;
                $response['live_contest']['message'] = "Play & Win exciting prizes";
                $response['live_contest']['data'] = (!empty($live_result)) ? $live_result : '';
            } else {
                $response['live_contest']['error'] = true;
                $response['live_contest']['message'] = "No contest is available to play right now. Come back again";
            }
        } else {
            $sql = "SELECT q.*, (select SUM(points) FROM contest_prize WHERE contest_prize.contest_id=q.id) as points, (select count(contest_id) FROM contest_prize WHERE contest_prize.contest_id=q.id) as top_users,(SELECT COUNT(*) from contest_leaderboard where l.contest_id = q.id )as `participants` FROM `contest_leaderboard` as l, `contest` as q WHERE l.user_id = '$user_id' and l.contest_id = q.id ORDER BY q.`id`  DESC";
            $db->sql($sql);
            $past_result = $db->getResult();
            if (!empty($past_result)) {
                foreach ($past_result as $quiz) {
                    $quiz['image'] = (!empty($quiz['image'])) ? DOMAIN_URL . 'images/contest/' . $quiz['image'] : '';
                    $quiz['start_date'] = date("d-M", strtotime($quiz['start_date']));
                    $quiz['end_date'] = date("d-M", strtotime($quiz['end_date']));
                    $s = "SELECT top_winner, points FROM `contest_prize` WHERE contest_id= " . $quiz['id'];
                    $db->sql($s);
                    $points = $db->getResult();
                    $quiz['points'] = $points;
                    $result[] = $quiz;
                }
                $past_result = $result;
                $response['past_contest']['error'] = false;
                $response['past_contest']['message'] = "Contest you have played";
                $response['past_contest']['data'] = (!empty($past_result)) ? $past_result : '';
            } else {
                $response['past_contest']['error'] = true;
                $response['past_contest']['message'] = "You have not played any contest yet. Go and play the contest once there is a live contest";
            }
            $response['live_contest']['error'] = true;
            $response['live_contest']['message'] = "No contest is available to play right now. Come back again";
        }

        /* selecting upcoming quiz ids */
        $sql = "SELECT id FROM `contest` where (CAST(`start_date` AS DATE) > '$toDate')";
        $db->sql($sql);
        $result = $db->getResult();
        $upcoming_type_ids = '';
        if (!empty($result)) {
            foreach ($result as $type_id) {
                $upcoming_type_ids .= $type_id['id'] . ', ';
            }
            $upcoming_type_ids = rtrim($upcoming_type_ids, ', ');

            /* getting all quiz details by ids retrieved */
            $sql = "SELECT *, (select SUM(points) FROM contest_prize WHERE contest_prize.contest_id=contest.id) as points, (select count(contest_id) FROM contest_prize WHERE contest_prize.contest_id=contest.id) as top_users FROM `contest` WHERE `id` in ($upcoming_type_ids) ORDER BY `id` DESC";
            $db->sql($sql);
            $upcoming_result = $db->getResult();
            $result = array();
            if (!empty($upcoming_result)) {
                foreach ($upcoming_result as $quiz) {
                    $quiz['image'] = (!empty($quiz['image'])) ? DOMAIN_URL . 'images/contest/' . $quiz['image'] : '';
                    $quiz['start_date'] = date("d-M", strtotime($quiz['start_date']));
                    $quiz['end_date'] = date("d-M", strtotime($quiz['end_date']));
                    $s = "SELECT top_winner, points FROM `contest_prize` WHERE contest_id= " . $quiz['id'];
                    $db->sql($s);
                    $points = $db->getResult();
                    $quiz['points'] = $points;
                    $quiz['participants'] = "";
                    $result[] = $quiz;
                }
                $upcoming_result = $result;
            }
            $response['upcoming_contest']['error'] = false;
            $response['upcoming_contest']['message'] = "Please stay tune to play & win exciting prizes.";
            $response['upcoming_contest']['data'] = (!empty($upcoming_result)) ? $upcoming_result : '';
        } else {
            $response['upcoming_contest']['error'] = true;
            $response['upcoming_contest']['message'] = "No upcoming contest to show. Soon we will be announcing the one.";
        }
    } else {
        $response['error'] = "true";
        $response['message'] = "Please pass all the fields";
    }
    print_r(json_encode($response));
}

// 37. get_questions_by_contest()
if (isset($_POST['access_key']) && isset($_POST['get_questions_by_contest'])) {

    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (isset($_POST['contest_id']) && !empty($_POST['contest_id']) && !empty($_POST['access_key'])) {
        $contest_id = $db->escapeString($_POST['contest_id']);
        $sql = "SELECT * FROM `contest_questions` WHERE `contest_id` = $contest_id ORDER BY id DESC";
        $db->sql($sql);
        $result = $db->getResult();
        if (!empty($result)) {
            for ($i = 0; $i < count($result); $i++) {
                $result[$i]['image'] = (!empty($result[$i]['image'])) ? DOMAIN_URL . 'images/contest-question/' . $result[$i]['image'] : '';
                $result[$i]['optione'] = ($fn->is_option_e_mode_enabled() && $result[$i]['optione'] != null) ? trim($result[$i]['optione']) : '';
                $result[$i]['optiona'] = trim($result[$i]['optiona']);
                $result[$i]['optionb'] = trim($result[$i]['optionb']);
                $result[$i]['optionc'] = trim($result[$i]['optionc']);
                $result[$i]['optiond'] = trim($result[$i]['optiond']);
            }
            $response['error'] = "false";
            $response['data'] = $result;
        } else {
            $response['error'] = "true";
            $response['message'] = "No data found!";
        }
    } else {
        $response['error'] = "true";
        $response['message'] = "Please pass all the fields";
    }
    print_r(json_encode($response));
}

// 38. contest_update_score() - set the score of the user after he plays the contest
if (isset($_POST['access_key']) && isset($_POST['contest_update_score'])) {

    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (isset($_POST['user_id']) && !empty($_POST['user_id']) && !empty($_POST['contest_id']) && isset($_POST['score']) && isset($_POST['correct_answers']) && isset($_POST['questions_attended'])) {
        $user_id = $db->escapeString($_POST['user_id']);
        $contest_id = $db->escapeString($_POST['contest_id']);
        $questions_attended = $db->escapeString($_POST['questions_attended']);
        $correct_answers = $db->escapeString($_POST['correct_answers']);
        $score = $db->escapeString($_POST['score']);

        $sql = "select * from `contest_leaderboard` WHERE `user_id`='" . $user_id . "' and `contest_id`='" . $contest_id . "' ";
        $db->sql($sql);
        $res = $db->getResult();
        if (empty($res)) {
            $sql = "INSERT INTO `contest_leaderboard`(`user_id`, `contest_id`, `questions_attended`, `correct_answers`, `score`,`last_modified`,`date_created`) VALUES
			(" . $user_id . "," . $contest_id . ", " . $questions_attended . "," . $correct_answers . "," . $score . ",'" . $toDateTime . "','" . $toDateTime . "')";
            $db->sql($sql);  // Table name, column names and respective values
            echo $sql;
            set_monthly_leaderboard($user_id, $score);
            $response['error'] = "false";
            $response['message'] = "Score insert successfully";
        } else {
            $id = $res[0]['id'];
            $sql = 'UPDATE `contest_leaderboard` SET `questions_attended`="' . $questions_attended . '",`correct_answers`="' . $correct_answers . '",`score`="' . $score . '",`last_modified`="' . $toDateTime . '" WHERE `id`=' . $id;
            $db->sql($sql);  // Table name, column names and respective values
            $res = $db->getResult();
            set_monthly_leaderboard($user_id, $score);
            $response['error'] = "false";
            $response['message'] = "Score updated successfully";
        }
    } else {
        $response['error'] = "true";
        $response['message'] = "Please pass all the fields";
    }
    print_r(json_encode($response));
}

// 39. get_contest_leaderboard() - get the top 15 players list for a contest
if (isset($_POST['access_key']) && isset($_POST['get_contest_leaderboard'])) {

    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (isset($_POST['contest_id']) && !empty($_POST['contest_id'])) {
        $contest_id = $db->escapeString($_POST['contest_id']);

        $offset = (isset($_POST['offset']) && !empty($_POST['offset']) && is_numeric($_POST['offset'])) ? $db->escapeString($_POST['offset']) : 0;
        $limit = (isset($_POST['limit']) && !empty($_POST['limit']) && is_numeric($_POST['limit'])) ? $db->escapeString($_POST['limit']) : 25;

//        $sql = "SELECT @user_rank:= @user_rank + 1 as user_rank, s.* FROM ( SELECT contest_leaderboard.user_id, users.name, users.profile, contest_leaderboard.score FROM contest_leaderboard, users WHERE contest_id = " . $contest_id . " and users.id = contest_leaderboard.user_id ORDER BY score DESC LIMIT 15 ) s cross join (SELECT @user_rank := 0) r";
        $sql = "SELECT r.*,u.name,u.profile FROM (SELECT s.*, @user_rank := @user_rank + 1 user_rank FROM ( SELECT user_id, score FROM contest_leaderboard c join users u on u.id = c.user_id  WHERE contest_id=" . $contest_id . " ) s, (SELECT @user_rank := 0) init ORDER BY score DESC ) r INNER join users u on u.id = r.user_id ORDER BY r.user_rank ASC LIMIT $offset,$limit";
        $db->sql($sql);
        $res = $db->getResult();
        for ($i = 0; $i < count($res); $i++) {
            if (filter_var($res[$i]['profile'], FILTER_VALIDATE_URL) === FALSE) {
                // Not a valid URL. Its a image only or empty
                $res[$i]['profile'] = (!empty($res[$i]['profile'])) ? DOMAIN_URL . 'uploads/profile/' . $res[$i]['profile'] : '';
            } else {
                $res[$i]['profile'] = $res[$i]['profile'];
            }
        }
        if (isset($_POST['user_id']) && !empty($_POST['user_id'])) {
            $user_id = $db->escapeString($_POST['user_id']);
            //$sql = "SELECT id , user_id , contest_id , score , user_rank FROM ( SELECT * , (@user_rank := @user_rank + 1) AS user_rank FROM contest_leaderboard CROSS JOIN( SELECT @user_rank := 0 ) AS init_var_var where contest_id = '" . $contest_id . "' ORDER BY contest_leaderboard.score DESC ) AS logins_ordered_user_ranked WHERE user_id = '" . $user_id . "' and contest_id = '" . $contest_id . "' ";
            $sql = "SELECT r.*,u.name,u.profile FROM (SELECT s.*, @user_rank := @user_rank + 1 user_rank FROM ( SELECT user_id, score FROM contest_leaderboard c join users u on u.id = c.user_id  WHERE contest_id=" . $contest_id . " ) s, (SELECT @user_rank := 0) init ORDER BY score DESC ) r INNER join users u on u.id = r.user_id WHERE user_id = '" . $user_id . "' ORDER BY r.user_rank ASC";
            $db->sql($sql);
            $my_rank = $db->getResult();
            if (!empty($my_rank)) {
                if (filter_var($my_rank[0]['profile'], FILTER_VALIDATE_URL) === FALSE) {
                    // Not a valid URL. Its a image only or empty
                    $my_rank[0]['profile'] = (!empty($my_rank[0]['profile'])) ? DOMAIN_URL . 'uploads/profile/' . $my_rank[0]['profile'] : '';
                }
                $response['my_rank'] = $my_rank[0];
            }
        }
        if (empty($res)) {
            $response['error'] = "true";
            $response['message'] = "No contest played yet! No rankings found!";
        } else {
            $response['error'] = "false";
            $response['data'] = $res;
        }
    } else {
        $response['error'] = "true";
        $response['message'] = "Please pass all the fields";
    }
    print_r(json_encode($response));
}


//40. add_point - Add  Points to user account and add the tracking activity
if (isset($_POST['access_key']) && isset($_POST['add_point'])) {

    $firebase_id = $db->escapeString($_POST['firebase_id']);
    $user_id = $db->escapeString($_POST['user_id']);
    $points = $db->escapeString($_POST['points']);
    $type = $db->escapeString($_POST['type']);
    $type_two = $db->escapeString($_POST['type_two']);
    $coin_status = $db->escapeString($_POST['coin_status']);
    $type = trim($type);
    if (!verify_token()) {
        return false;
    }


    if($coin_config['max_app_coin'] >= $points){
        if (!empty($firebase_id) && !empty($type)) {
            $getuid = checkUID($firebase_id);

            if ($getuid == TRUE) {

                $data = array(
                    'uid' => $firebase_id,
                    'points' => $points,
                    'type' => $type,
                    'coin_status' => $coin_status,
                    'user_id' => $user_id,
                    'date' => $toDateTime,//$datetime->format('Y\-m\-d\ h:i:s'),
                    'type_two' => $type_two
                );
                // print_r($data);
                // return false;

                $db->insert('tbl_tracker', $data);  // Table name, column names and respective values
                $res = $db->getResult();
                /* now add points with remaining points of the user */

                $sql1 = "UPDATE users SET coins = coins + '" . $points . "'";
                $sql1 .= " WHERE `users`.`firebase_id` ='" . $firebase_id . "'";
                $db->sql($sql1);

                $response['error'] = false;
                $response['message'] = "Points added to your wallet";
                $response['id'] = $res[0];
            } else {
                $response['error'] = true;
                $response['message'] = "Invalid ID";
            }
        } else {
            $response['error'] = true;
            $response['message'] = "Please fill all the data and submit!";
        }

    }else{
        $response['error'] = true;
        $response['message'] = "Request Not Allow";
    }
    print_r(json_encode($response));
}


//41. payment_request - Payment request by the user
if (isset($_POST['access_key']) && isset($_POST['payment_request'])) {

    $sql = "select * from `settings` WHERE type='pay_setting'";

    $db->sql($sql);
    $res = $db->getResult();

    /* if payment request is disabled i.e, users aren't allowed to make payment request */

    if (!verify_token()) {
        return false;
    }
    if ($res[0]['status'] == 0) {
        $response['error'] = "true";
        $response['message'] = $res[0]['message']; /* message set by admin */
        print_r(json_encode($response));
        return false;
    }



    if ($access_key != $_POST['access_key'] && $access_key_new != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }


    /* Register the Payment Request of user */
    $firebase_id = $db->escapeString($_POST['firebase_id']);

    $user_points = 0;
    $sql = "SELECT id,coins,status FROM users WHERE firebase_id='".$firebase_id."'";
    $db->sql($sql);
    $res = $db->getResult();


   // print_r($res);
    if (empty($res)) {
        $response['error'] = "true";
        $response['message'] = "Your account doesn't exists!";
        print_r(json_encode($response));
        return false;
    } else {
        $status = $res[0]['status'];
        $user_points = $res[0]['coins'];
        if ($status == 0) {
            $response['error'] = "true";
            $response['message'] = "Your account has been blocked or suspended! Please contact admin";
            print_r(json_encode($response));
            return false;
        }
    }

    $payment_address = $db->escapeString($_POST['payment_address']);
    $request_type = $db->escapeString($_POST['request_type']);
    $request_amount = $db->escapeString($_POST['request_amount']);
    $points_used = $db->escapeString($_POST['points_used']);
    $remarks = $db->escapeString($_POST['remarks']);
    $status = $db->escapeString($_POST['status']);
    $user_id = $db->escapeString($_POST['user_id']);


    /* check if user already made request before 24 hours */
    $sql = "SELECT `id`,`date` from `payment_requests` where `uid` = '$firebase_id'  ORDER by id DESC LIMIT 1";
    $db->sql($sql);
    $res = $db->getResult();

    if ($res) {
        $current_time = $toDateTime;
        $last_request = $res[0]['date'];
        $hourdiff = round((strtotime($current_time) - strtotime($last_request)) / 3600, 1);
        // echo $current_time." ".$last_request;
        // echo "\n".$hourdiff;
        if ($hourdiff < '48') {
            $response['error'] = "true";
            $response['message'] = "You have already made a payment request. Please wait for 48 hours after you made the previous request.";
            print_r(json_encode($response));
            return false;
        }
        // return false;
    }
    if (!empty($firebase_id) && !empty($points_used) && !empty($payment_address)) {
        $data = array(
            'uid' => $firebase_id,
            'user_id' => $user_id,
            'payment_address' => $payment_address,
            'request_type' => $request_type,
            'request_amount' => $request_amount,
            'points_used' => $points_used,
            'remarks' => $remarks,
            'status' => $status,
            'date' => $toDateTime//$datetime->format('Y\-m\-d\ h:i:s'),
        );

        $db->insert('payment_requests', $data);  // Table name, column names and respective values
        $res = $db->getResult();

        /* now set points to 0 */
        $sql = "UPDATE `users` SET `coins` = '0' WHERE `users`.`firebase_id` ='" . $firebase_id . "'";
        $db->sql($sql);

        $response['error'] = false;
        $response['message'] = "Request has been registered successfully";
        $response['request_id'] = $res[0];
    } else {
        $response['error'] = true;
        $response['message'] = "Please fill all the data and submit!";
    }
    print_r(json_encode($response));
}



// 42. user_tracker() - get login details by user's username
if(isset($_POST['access_key']) && isset($_POST['user_tracker'])){

    if (!verify_token()) {
        return false;
    }
	if($access_key != $_POST['access_key'] && $access_key_new != $_POST['access_key']){
		$response['error'] = "true";
		$response['message'] = "Invalid Access Key";
		print_r(json_encode($response));
		return false;
	}

	$uid = $db->escapeString($_POST['firebase_id']);

	if(!empty($uid)){

		$sql = "SELECT MAX(id) as id,date FROM `tbl_tracker` WHERE uid='".$uid."'";
		$db->sql($sql);
		$result = $db->getResult();



		if(empty($result) || $result[0]['id'] == ''){
			$sql = "SELECT Min(id) as id,date FROM `tbl_tracker` WHERE uid='".$uid."'";
			$db->sql($sql);
			$result = $db->getResult();
		}

		$id = $result[0]['id'];
		$date = date('d-M-Y', strtotime($result[0]['date']));

		$sql = "SELECT * FROM `tbl_tracker` where `uid`='".$uid."' ORDER BY `id` DESC ";
		 //echo $sql;
		$db->sql($sql);
		$result = $db->getResult();
                for ($i = 0; $i < count($result); $i++) {
                         $result[$i]['date'] = date('d/m/Y, h:i A', strtotime($result[$i]['date'] ));
                }
		if (!empty($result)) {
			$response['error'] = "false";
			$response['message'] = "Tracking history from ".$date." onwards";
			$response['data'] = $result;
		}else{
			$response['error'] = "true";
			$response['message'] = "No tracking history found!";
		}
	}else{
		$response['error'] = "true";
		$response['message'] = "Please pass all the fields";
	}
	print_r(json_encode($response));
}


// 43. get_all_coin_list()
if (isset($_POST['access_key']) && isset($_POST['get_all_coin_list'])) {
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (!empty($config)) {
        $response['error'] = "false";
        $response['data'] = $coin_config;
    } else {
        $response['error'] = "true";
        $response['message'] = "No configurations found yet!";
    }
    print_r(json_encode($response));
}



// 44. set_daily_status()
if (isset($_POST['access_key']) && isset($_POST['set_daily_status'])) {
    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (!empty($_POST['access_key']) && !empty($_POST['user_id']) && $_POST['type'] !='') {
        $user_id = $db->escapeString($_POST['user_id']);

        $type =  $db->escapeString($_POST['type']);

        $sql1 = "SELECT * FROM `tbl_daily_status` WHERE user_id='$user_id' AND type='$type' AND date(todaydate)='$toDate'";
         $db->sql($sql1);
         $result1= $db->getResult();

         //echo $sql1;

         if(empty($result1)){
             $sql = 'INSERT INTO tbl_daily_status (user_id, type,todaydate) VALUES (' . $user_id . ',' . $type . ',"' . $toDate . '")';
             $db->sql($sql);


             $response['error'] = "false";
             $response['message'] = "successfully insert data";
         }else{
            $response['error'] = "true";
            $response['message'] = "Record Insert Already";
         }

    } else {
        $response['error'] = "true";
        $response['message'] = "Please pass all the fields";
    }
    print_r(json_encode($response));
}


// 45. get_daily_status()
if (isset($_POST['access_key']) && isset($_POST['get_daily_status'])) {

    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (!empty($_POST['access_key']) && !empty($_POST['user_id']) && !empty($_POST['type'])) {
        $user_id = $db->escapeString($_POST['user_id']);

        $type =  $db->escapeString($_POST['type']);

        $sql1 = "SELECT * FROM `tbl_daily_status` WHERE user_id='$user_id' AND type='$type' AND date(todaydate)='$toDate'";
        $db->sql($sql1);
        $result1= $db->getResult();



         if($result1){
             $response['error'] = "true";
             $response['message'] = "Record Already Exits";
         }else{
            $response['error'] = "false";
            $response['message'] = "Record Not Exits";
         }

    } else {
        $response['error'] = "true";
        $response['message'] = "Please pass all the fields";
    }
    print_r(json_encode($response));
}


// 46. account_remove()
if (isset($_POST['access_key']) && isset($_POST['account_remove'])) {

    if (!verify_token()) {
        return false;
    }
    if ($access_key != $_POST['access_key']) {
        $response['error'] = "true";
        $response['message'] = "Invalid Access Key";
        print_r(json_encode($response));
        return false;
    }
    if (!empty($_POST['access_key']) && !empty($_POST['user_id'])) {
        $user_id = $db->escapeString($_POST['user_id']);

        $sql1 = "SELECT * FROM `users`  WHERE id='$user_id'";
        $db->sql($sql1);
        $result1= $db->getResult();

        $profile = $result1[0]['profile'];


        $sql = 'DELETE FROM `users` WHERE `id`=' . $user_id;
        if ($db->sql($sql)) {
            if (!empty($result1[0]['profile']) && file_exists('uploads/profile/' . $result1[0]['profile'])) {
                unlink('uploads/profile/' . $result1[0]['profile']);
            }
            $sql1 = 'DELETE FROM `contest_leaderboard` WHERE `user_id`=' . $user_id;
            $db->sql($sql1);

            $sql2 = 'DELETE FROM `daily_leaderboard` WHERE `user_id`=' . $user_id;
            $db->sql($sql2);

            $sql2 = 'DELETE FROM `monthly_leaderboard` WHERE `user_id`=' . $user_id;
            $db->sql($sql2);

            $sql3 = 'DELETE FROM `payment_requests` WHERE `user_id`=' . $user_id;
            $db->sql($sql3);

            $sql4 = 'DELETE FROM `tbl_bookmark` WHERE `user_id`=' . $user_id;
            $db->sql($sql4);

            $sql5 = 'DELETE FROM `tbl_cat_sub_cat_complete` WHERE `user_id`=' . $user_id;
            $db->sql($sql5);

            $sql5 = 'DELETE FROM `tbl_daily_status` WHERE `user_id`=' . $user_id;
            $db->sql($sql5);

            $sql6 = 'DELETE FROM `tbl_rooms` WHERE `user_id`=' . $user_id;
            $db->sql($sql6);

            $sql7 = 'DELETE FROM `tbl_tracker` WHERE `user_id`=' . $user_id;
            $db->sql($sql7);

            $sql8 = 'DELETE FROM `users_statistics` WHERE `user_id`=' . $user_id;
            $db->sql($sql8);


            $response['error'] = "false";
            $response['message'] = "Delete Successfull";
        }


    } else {
        $response['error'] = "true";
        $response['message'] = "Please pass all the fields";
    }
    print_r(json_encode($response));
}

function get_fcm_id($user_id) {
    $db = new Database();
    $db->connect();

    $sql = "SELECT `fcm_id` FROM `users` where `id` = " . $user_id;
    $db->sql($sql);
    $res = $db->getResult();
    return $res[0]['fcm_id'];
}

function checkBattleExists($match_id) {
    $db = new Database();
    $db->connect();

    $sql = "SELECT `id` FROM `battle_questions` where `match_id` = '" . $match_id . "'";
    $db->sql($sql);
    $res = $db->getResult();
    return $res;
    if (empty($res)) {
        return false;
    } else {
        return true;
    }
}

function set_monthly_leaderboard($user_id, $score) {
    if (isset($user_id) && isset($score) && !empty($user_id)) {
        $db = new Database();
        $db->connect();
        $toDateTime = date('Y-m-d H:i:s');
        $toDate = date('Y-m-d');
        $sql = "SELECT id, user_id, score FROM `monthly_leaderboard` WHERE `user_id`=" . $user_id . " and month(monthly_leaderboard.date_created) = month('" . $toDate . "')
            and year(monthly_leaderboard.date_created) = year('" . $toDate . "') ";
        $db->sql($sql);
        $result = $db->getResult();

        $sql1 = "SELECT id, user_id FROM `daily_leaderboard` WHERE `user_id`=" . $user_id;
        $db->sql($sql1);
        $result1 = $db->getResult();

        if (!empty($result) && !empty($result1)) {
            $sql2 = "SELECT id, user_id, score FROM `daily_leaderboard` WHERE `user_id`=" . $user_id . " and day(daily_leaderboard.date_created) = day('" . $toDate . "') ";
            $db->sql($sql2);
            $result2 = $db->getResult();

            if (!empty($result2)) {
                $old = $result2[0]['score'];
                $new = $old + $score;
                $score1 = ($new <= 0) ? 0 : $score;
                if ($new <= 0) {
                    $sql1 = "UPDATE `daily_leaderboard` SET `score`= '" . $score1 . "' WHERE id = " . $result2[0]['id'] . " and user_id=" . $user_id;
                } else {
                    $sql1 = "UPDATE `daily_leaderboard` SET `score`= `score` + '" . $score1 . "' WHERE id = " . $result2[0]['id'] . " and user_id=" . $user_id;
                }
                $db->sql($sql1);
            } else {
                $score1 = ($score <= 0) ? 0 : $score;
                $sql1 = "UPDATE `daily_leaderboard` SET `date_created` = '" . $toDateTime . "', `score`= '" . $score1 . "' WHERE user_id=" . $user_id;
                $db->sql($sql1);
            }
            $old1 = $result[0]['score'];
            $new1 = $old1 + $score;
            $score1 = ($new1 <= 0) ? 0 : $score;
            if ($new1 <= 0) {
                $sql = "UPDATE `monthly_leaderboard` SET `score`= '" . $score1 . "' WHERE id = " . $result[0]['id'] . " and user_id=" . $user_id;
            } else {
                $sql = "UPDATE `monthly_leaderboard` SET `score`= `score` + '" . $score1 . "' WHERE id = " . $result[0]['id'] . " and user_id=" . $user_id;
            }
            $db->sql($sql);
        } else {
            $score1 = ($score <= 0) ? 0 : $score;
            if (!empty($result1[0]['user_id'])) {
                $sql1 = "UPDATE `daily_leaderboard` SET `date_created` = '" . $toDateTime . "', `score`= '" . $score1 . "' WHERE id = " . $result1[0]['id'] . " and user_id=" . $user_id;
                $db->sql($sql1);
            } else {
                $sql1 = 'INSERT INTO `daily_leaderboard` (`user_id`, `score`, `last_updated`) VALUES (' . $user_id . ',' . $score1 . ',"' . $toDateTime . '")';
                $db->sql($sql1);
            }
            $sql = 'INSERT INTO `monthly_leaderboard` (`user_id`, `score`, `last_updated`) VALUES (' . $user_id . ',' . $score1 . ',"' . $toDateTime . '")';
            $db->sql($sql);
        }
    }
}

function checkUID($uid) {

    $factory = (new Factory)->withServiceAccount('firebase/firebase_credentials.json');
    $firebaseauth = $factory->createAuth();

    try {
        $user = (array) $firebaseauth->getUser($uid);
        if ($user['uid'] == $uid) {
            return TRUE;
        } else {
            return FALSE;
        }
    } catch (\Kreait\Firebase\Exception\Auth\UserNotFound $e) {
        return FALSE;
    }
}

?>
