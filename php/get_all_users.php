<?php
 
/*
 * Following code will list all the users
 */
 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();
 
// get all products from products table
$result = mysql_query("SELECT * FROM Users") or die(mysql_error());
 
// check for empty result
if (mysql_num_rows($result) > 0) {
    // looping through all results

    $response["users"] = array();
 
    while ($row = mysql_fetch_array($result)) {
        // temp user array
        $user = array();
        $user["uid"] = $row["uid"];
        $user["name"] = $row["name"];
        $user["email"] = $row["email"];
	$user["password"] = $row["password"];
        $user["created_at"] = $row["created_at"];

 
        // push single product into final response array
        array_push($response["users"], $user);
    }
    // success
    $response["success"] = 1;
 
    // echoing JSON response
    echo json_encode($response);
} else {
    // no products found
    $response["success"] = 0;
    $response["message"] = "No users found";
 
    // echo no users JSON
    echo json_encode($response);
}
?>
