<?php
echo "INDEX";

// include db connect class
require_once __DIR__ . '/db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();
 
phpinfo();
 
mysql_close();
?>
