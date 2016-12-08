
<!DOCTYPE html>
<html lang="en">
<head>
  
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
 
    <title>SmartCity Infra Management</title>
     
    <style>
    body{
        font-family:arial;
        font-size:.8em;
    }
     
    input[type=text]{
        padding:0.5em;
        width:20em;
    }
     
    input[type=submit]{
        padding:0.4em;
    }
     
    #gmap_canvas{
        width:100%;
        height:30em;
    }
     
    #map-label,
    #latlon-info{
        margin:1em 0;
		font-weight: bold;
    }
    </style>

</head>	

<body>	

<?php
	$page_title = "Home – My Website";
	$page_description = "Description of this page";

    include("includes/header.php"); ?>

<?php
// if($_POST){
//if(isset($_POST['map'])) {
if ($_SERVER["REQUEST_METHOD"] == "POST") {
	$date_from = $_POST['date1'];
	$date_to = $_POST['date2'];
	include_once "validations.php";
	include_once "get_data_DB.php";
	
	//validate date range
	if(! validateDateRange($date_from, $date_to))
	{
		return;
	}
		connectToDB();
		// create a multi-dim array
		$data_points = array(array());
		//unset($data_points[0]);
		
		// get lat long
		$result = getLatLongWithinDates($date_from, $date_to);
		
		$num_rows = mysql_num_rows($result);
		echo("Number of complaints displayed are $num_rows \n");
		
		if ($num_rows > 0)
		{		
			for ($i = 0; $i < $num_rows; $i++) 
			{
				$row = mysql_fetch_array($result, MYSQL_ASSOC);

				//echo("</br><b>".$row['latitude']."-".$row['longitude']."::::</br>");
				// get latitude, longitude 
				// create an array to put the data
				$data_arr = array();      

				//push latitude,longitude,ticket_type,image_url
				array_push($data_arr, $row['latitude'], $row['longitude'], $row['ticket_type'], $row['image_url']);

				//echo("</br><b>".$data_arr[0]."-".$data_arr[1]."aaaa</br>");
				$data_points[$i]= $data_arr;
				//echo("</br><b>".$data_points[$i][0]."-".$data_points[$i][1]."dddd</br>");
				
			}  //close loop
			/*
			for ($i = 0; $i < count($data_points); $i++)
			{
				echo("</br><b>".$data_points[$i][0]."-".$data_points[$i][1]."oooo</br>");
			}
			*/
			//$latitude = "28.634117";
			//$longitude = "77.342119";
			
			// Assume first Data point to be the centre
			$latitude = $data_points[0][0];
			$longitude = $data_points[0][1];

		
		?>
	 
		<!-- google map will be shown here -->
		<div id="gmap_canvas">Loading map... count of data points...<?php echo count($data_points); ?></div>
		<div id='map-label'>Map shows approximate location.</div>
	 
		<!-- JavaScript to show google map -->
		<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>    
		<script type="text/javascript">
			function init_map() {
				var myOptions = {
					zoom: 5,
					center: new google.maps.LatLng(<?php echo $latitude; ?>, <?php echo $longitude; ?>),
					mapTypeId: google.maps.MapTypeId.ROADMAP
				};
				// Draw a map on DIV "gmap_canvas"
				map = new google.maps.Map(document.getElementById("gmap_canvas"), myOptions);
				
			
				//geocoder = new google.maps.Geocoder();
				<?php 
				for($i = 0; $i < count($data_points); $i++) {?>
					var latlng = new google.maps.LatLng(<?php echo $data_points[$i][0]; ?>, <?php echo $data_points[$i][1]; ?>);
					
					// get marker icon by type
					//var marker_icon = "images/sewage.png";
					var marker_icon = "images/" + '<?php echo strtolower($data_points[$i][2]) ?>' + ".png";

					// NOTE: This command is executed first to create link from absolute path to relative path
					// ln -s /Applications/Data/SCIR/images/ images/pic
					//var img_db = '<?php echo $data_points[$i][3] ?>';
					var img_db = "images/pic/" + '<?php echo $data_points[$i][3]; ?>';
					var marker = new google.maps.Marker({ map: map, position: latlng, icon: marker_icon});
					//var contentString="<img width='80' src=\"images\" />"; //width='80' 
					//var contentString="<img src='images/road.png' />"; //width='80' 
					var contentString="<img src=" + img_db + " alt=" + img_db + " />";
					//marker = new google.maps.Marker({
					//	map: map,
					//	position: new google.maps.LatLng(<?php echo $latitude; ?>, <?php echo $longitude; ?>)
					//});
					infowindow = new google.maps.InfoWindow({ content: contentString });
					//infowindow.setContent('<img src=' + img_db + '>');

					//marker = new google.maps.Marker({
					//	map: map,
					//	position: new google.maps.LatLng(<?php echo $latitude; ?>, <?php echo $longitude; ?>)
					//});
					//infowindow = new google.maps.InfoWindow({ content: "<?php echo $data_points[$i][0], ", " ,$data_points[$i][1] ?>"  });
					//infowindow = new google.maps.InfoWindow({ content: "<?php echo $latitude, "," ,$longitude  ?>"});

					//google.maps.event.addListener(marker, "click", function () { infowindow.open(map, marker); });
					//infowindow.open(map, marker);	
					google.maps.event.addListener(marker, 'click', (function(marker, img_db) {return function() {
						infowindow.setContent('<img src=' + img_db + ' alt=' + img_db + ' />');
						infowindow.open(map, marker);
					}
					})(marker, img_db));						
				<?php }?>							


			}
			google.maps.event.addDomListener(window, 'load', init_map);
		</script>
	 
	<?php
	}
	else
	{
	?>
		<div id="gmap_canvas">No datapoints found in DB within the given Date Range</div>
	<?php
	}	
	closeConnect();
}
else {
?>
 
<div id='latlong-info'>
    <div>All Data points within defined dates will be displayed on the map <br><br>  </p> 

 
<!-- enter any Lat Long -->
<form method="post" action="<?php echo htmlspecialchars($_SERVER["PHP_SELF"]);?>">
<table style="width:50%">
<tr>
<td> Enter a Date range: </td>
<td> <INPUT TYPE="DATE" NAME="date1" MIN="2015-01-01" value="2016-01-01"> </td>
<td> <INPUT TYPE="DATE" NAME="date2" MIN="2015-01-01" value="2016-01-01"><br></td>
</tr>
<tr>
<td> <INPUT TYPE="SUBMIT" NAME="map" VALUE='Show Complaints on Map'></td>
</tr>
</table>

</form>
</div>
<?php } ?>


<?php
include("includes/footer.php"); // include footer content
?>
</body>
</html>