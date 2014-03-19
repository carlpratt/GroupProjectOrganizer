<?php
echo "INDEX";
mysql_connect("sql3.freemysqlhosting.net","sql330658","eX8*dK8*");
mysql_select_db("sql330658");
 
$q=mysql_query("SELECT * FROM gpo_users");
//$q=mysql_query("INSERT INTO Users (Name) VALUES ('Joe')");
while($e=mysql_fetch_assoc($q))
        $output[]=$e;
 
print(json_encode($output));
echo $e;

phpinfo();
 
mysql_close();
?>
