<?php

session_start();


include 'library/crud.php';
$db = new Database();
$db->connect();

if (isset($_POST['username']) && isset($_POST['password'])) {
   

    $username = htmlspecialchars(stripslashes($_POST['username']));
    $username = $db->escapeString($username);

    $password = htmlspecialchars(stripslashes($_POST['password']));
    $password = $db->escapeString($password);

    $pwordhash = md5($password);

    if (!empty($username) && !empty($password)) {
        $sql = "SELECT * FROM authenticate WHERE auth_username='$username' AND auth_pass='$pwordhash' ";
        if ($db->sql($sql)) {
            $result = $db->getResult();

            if (!empty($result)) {
                if (strcmp($result[0]["auth_username"], $username) == 0) {
                    foreach ($result as $row) {
                        $_SESSION['username'] = $row["auth_username"];
                        $_SESSION['company_name'] = 'Qearner - Learn More Earn More';
                    }
                    echo "1";
                } else {
                    echo "<p class='alert alert-danger'>Id or password does not match</p>";
                }
            } else {
                echo "<p class='alert alert-danger'>username or password does not match</p>";
            }
        } else {
            echo " <p class='alert alert-danger'>Please import database</p></p>";
        }
    } else {
        echo " <p class='alert alert-danger'>!!every field is mandetary</p></p>";
    }
}

if (isset($_SESSION['configuration_key']) && isset($_SESSION['system_key'])) {

      $sql1 = "SELECT * FROM settings WHERE type='validation_configuration'";
      $db->sql($sql1);
      $result1 = $db->getResult();


      $jsonData = array(
        'configuration_key' => $_SESSION['configuration_key'],
        'system_key' => $_SESSION['system_key']
      );

      if(empty($result1)){
          

          $data = json_encode($jsonData);
          $sql2 = "INSERT INTO settings (type,message,status) VALUES ('validation_configuration','" . $data . "','0')";
          $db->sql($sql2);
          unset($_SESSION['configuration_key']);
          unset($_SESSION['system_key']);
      }

}



if (isset($_POST['system_validation'])) {

    
    $sql4 = "SELECT * FROM settings WHERE type='validation_configuration'";
    $db->sql($sql4);
    $result1 = $db->getResult();

    $jsonData = array(
      'configuration_key' => md5($_POST['quiz_url']),
      'system_key' => md5($_POST['purchase_code'])
    );

    $data = json_encode($jsonData);
    $sql5 = "INSERT INTO settings (type,message,status) VALUES ('validation_configuration','" . $data . "','0')";
    
    
    if(empty($result1)){
        $db->sql($sql5);
        }else{
            $json = json_encode($result1[0]['message']);


            if(empty($json->configuration_key) || empty($json->system_key) || empty($result1[0]['message'])){
                $db->sql($sql3);

                $sql = "UPDATE `settings` SET `message`='" . $data . "' WHERE type='validation_configuration' ";
                $db->sql($sql);
            }

        }

   
        header("location:index.php");    

}
?>
