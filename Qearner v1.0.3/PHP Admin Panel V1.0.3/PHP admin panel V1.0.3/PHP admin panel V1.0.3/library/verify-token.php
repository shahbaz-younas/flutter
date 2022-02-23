<?php
include_once('jwt.php');
include_once('crud.php');




function generate_token($id) {
    $jwt = new JWT();
    $payload = [
        'iat' => time(), /* issued at time */
        'iss' => 'quiz',
        'FirebaseID'  => $id,
        'exp' => time() + (30 * 60 * 60), /* expires after 1 minute */
        'sub' => 'quiz Authentication'
    ];
    $token = $jwt::encode($payload, JWT_SECRET_KEY);
    return "Bearer ".$token;
}
// generate_token();
// $token = generate_token();
// print_r($token);

function verify_token() {
    $db = new Database();
    $db->connect();
    $jwt = new JWT();

    $status = true;
    $message = '';
    try {
        $token = $jwt->getBearerToken();
        $FirebaseToken =  $jwt->getUserToken();
        $DeviceID =  $jwt->getDeviceID();
    } catch (Exception $e) {
        $response['error'] = "true";
        $response['login'] = 'true';
        $response['message'] = $e->getMessage();
        print_r(json_encode($response));
        return false;
    }
    if (!empty($token)) {
        try {
            // JWT::$leeway = 60;
            $payload = $jwt->decode($token, JWT_SECRET_KEY, ['HS256']);
            if($payload->FirebaseID == $FirebaseToken){
                if (!isset($payload->iss) || $payload->iss != 'quiz' || !isset($payload->FirebaseID)) {
                    $message = 'Invalid Hash';
                    $status = false;
                } else {
                    $status = true;
                }
            
            }
            $sql = "SELECT * FROM `users` WHERE firebase_id='$FirebaseToken'";
            $db->sql($sql);
            $result = $db->getResult();
            if(strval($DeviceID) != strval($result[0]['device_id'])){
                $message = 'Unauthorized access not allowed';
                $status = false;
            }
            if($status == true){
                return true;
            }else{
                $response['error'] = "true";
                $response['login'] = 'true';
                $response['message'] = $message;
                print_r(json_encode($response));
                return false;
            }


        } catch (Exception $e) {
            $response['error'] = "true";
            $response['message'] = $e->getMessage();
            print_r(json_encode($response));
            return false;
        }
    } else {
        $response['error'] = "true";
        $response['message'] = "Unauthorized access not allowed";
        print_r(json_encode($response));
        return false;
    }
}

?>