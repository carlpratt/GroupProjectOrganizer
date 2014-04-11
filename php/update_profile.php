<?php

/*
 * Following code will update user information
 * A user is identified by their id (uid)
 */

// array for JSON response
$response = array();


$uid = $_POST['uid'];
$name = $_POST['name'];
$email = $_POST['email'];
$phone = $_POST['phone'];
$facebook = $_POST['facebook'];
$google = $_POST['google'];

// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();

// mysql update row with matched pid
$result = mysql_query("UPDATE Users SET
    name = '$name', email = '$email', phone = '$phone', facebook = '$facebook', google = '$google'
    WHERE uid = $uid");

// check if row inserted or not
if ($result) {
    // successfully updated
    $response["success"] = 1;
    $response["message"] = "User successfully updated.";

    // echoing JSON response
    echo json_encode($response);
} else {

}
?>