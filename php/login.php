<?php
 
/*
 * Following code will get single user if they exist
 * A user is identified by their email and password
 */
  
// include db connect class
require_once __DIR__ . '/db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();

// array for JSON response
$response = array();
 
// check for post data
if (isset($_POST['email']) && isset($_POST['password'])) {
    $email = $_POST['email'];
    $password = $_POST['password'];
 
    // get a user from gpo_users table
    $result = mysql_query("SELECT * FROM Users WHERE email = '$email' AND password = '$password'");

    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {
 
            $result = mysql_fetch_array($result);
 
            $user = array();
            $user["uid"] = $result["uid"];
            $user["name"] = $result["name"];
            $user["email"] = $result["email"];
            $user["password"] = $result["password"];
            $user["created_at"] = $result["created_at"];
            $user["updated_at"] = $result["updated_at"];
            // success
            $response["success"] = 1;
 
            // user node
            $response["user"] = array();
 
            array_push($response["user"], $user);
 
            // echoing JSON response
            echo json_encode($response);
        } else {
            // no user found
            $response["success"] = 0;
            $response["message"] = "No user found for that email and password";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no user found
        $response["success"] = 0;
        $response["message"] = "Results were null";
 
        // echo no users JSON
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>
