<?php
include_once "connect_to_DB.php";

// Retrieves all data points from the Location Table
function getLatLong() {
	global $result;
	// Performing SQL query
	$query = 'SELECT latitude,longitude FROM sci_tickets';
	$result = mysql_query($query) 
		or die('Query failed: ' . $query . mysql_error());

	// Printing results in HTML
	echo "<table>\n";
	while ($line = mysql_fetch_array($result, MYSQL_ASSOC)) {
		echo "\t<tr>\n";
		foreach ($line as $col_value) {
			echo "\t\t<td>$col_value</td>\n";
		}
		echo "\t</tr>\n";
	}
	echo "</table>\n";
}

// Retrieves data points within radius (default 30 miles) from the Location Table
function getLatLongWithinRadius($rad = 30) {
	global $result;
	// Performing SQL query
	$query = "SELECT ticket_id,latitude,longitude, (
    3959 * acos (
      cos ( radians(28.634117) )
      * cos( radians( latitude ) )
      * cos( radians( longitude ) - radians(77.342119) )
      + sin ( radians(28.634117) )
      * sin( radians( latitude ) )
    )	) AS distance FROM sci_tickets HAVING distance < $rad"; // ORDER BY distance LIMIT 0 , 20";
	// echo "\nquery = $query\n";
	$result = mysql_query($query) 
		or die('Query failed: ' . $query . mysql_error());
	
	// Printing results in HTML
	/*
	echo "<table>\n";
	while ($line = mysql_fetch_array($result, MYSQL_ASSOC)) {
		echo "\t<tr>\n";
		foreach ($line as $col_value) {
			echo "\t\t<td>$col_value</td>\n";
		}
		echo "\t</tr>\n";
	}
	echo "</table>\n";
	*/
	return $result;
}

?>
