<?php
$servername = "localhost";
$username = "root";
$password = "password";
$database = "scir";
$link;
$result;

// Connects to the database 
function connectToDB() {
	global $servername, $username, $password, $database, $link;
	// Attempt to connect to DB 
	$link = mysql_connect($servername, $username, $password) or die('Could not connect: ' . mysql_error());
	//echo 'Connected successfully';
	// Attempt to select database
	mysql_select_db($database) or die('Could not select database ' . $database . ": " . mysql_error());
}


// Free resultset and Closing connection
function closeConnect() {
	global $link, $result;
	mysql_free_result($result);
	mysql_close($link);
}
?>
