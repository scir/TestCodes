
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
	
	table, th, td {
		border: 0px solid black;
		border-collapse: collapse;
    }
    
	th, td {
		padding: 5px;
		text-align: left;    
	}
	
    input[type=text]{
        padding:0.5em;
        width:12em;
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
	include_once "get_data_DB.php";
	connectToDB();
	// create a multi-dim array
	$data_points =array(array());
    //unset($data_points[0]);
	

	$km="30";
	$km=$_POST['km'];
	$latitude=$_POST['lat'];
	$longitude=$_POST['lon'];
	//echo "km=$km";
	// show lat long
	$result = getLatLongWithinRadius($km, $latitude, $longitude);
	
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
			echo("</br><b>".$data_points[$i][0]."- ".$data_points[$i][1]."- ".$data_points[$i][2]."-  ".$data_points[$i][3]." oooo</br>");
		}
	*/	
		//$latitude = "28.634117";
		//$longitude = "77.342119";
		$metres = $km / 0.00062137;
		//echo("</br><b>metres=$metres</br>");
		?>
	 
		<!-- google map will be shown here -->
		<div id="gmap_canvas">Loading map... count of data points...<?php echo count($data_points); ?></div>
		<div id='map-label'>Map shows approximate location.</div>
	 
		<!-- JavaScript to show google map -->
		<!-- NOTE/ TODO : Specific API key need to be put up here to enable map display ! --> 
		<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>    
		<script type="text/javascript">
			function init_map() 
			{
				var myOptions = {
					zoom: 14,
					center: new google.maps.LatLng(<?php echo $latitude; ?>, <?php echo $longitude; ?>),
					mapTypeId: google.maps.MapTypeId.ROADMAP
				};
				// Draw a map on DIV "gmap_canvas"
				map = new google.maps.Map(document.getElementById("gmap_canvas"), myOptions);
				
				// create a circle radius
				var myCircle = new google.maps.Circle({
					radius: <?php echo $metres; ?>,
					strokeColor: "#FF0000",
					strokeOpacity: 0.8,
					strokeWeight: 2,
					fillColor: "#FF0000",
					fillOpacity: 0.35,
					map: map,
					center: new google.maps.LatLng(<?php echo $latitude; ?>, <?php echo $longitude; ?>)
				});
				
				//geocoder = new google.maps.Geocoder();
				<?php 
				for($i = 0; $i < count($data_points); $i++) {
				?>
					var latlng = new google.maps.LatLng(<?php echo $data_points[$i][0]; ?>, <?php echo $data_points[$i][1]; ?>);
					// get marker icon by type
					//var marker_icon = "images/sewage.png";
					var marker_icon = "images/" + '<?php echo strtolower($data_points[$i][2]) ?>' + ".png";

					// NOTE: This command is executed first to create link from absolute path to relative path
					// ln -s /Applications/Data/SCIR/images/ images/pic

					var img_db = "images/pic/" + '<?php echo $data_points[$i][3]; ?>';
					//var img_db = "../../../../../Applications/Data/SCIR/images/20160203/+919810156066_1454518262711_Image.jpg";
					//var img_db = "../../../../../Applications/Data/SCIR/images/20151205/9891100100_1449305225085_Mickey.jpeg";
					//var img_db = "http://103.242.60.127:8888/SmartCityInfraMgmt/images/" + "+919810156066_1454572155553_Image.jpg";
 
					var marker = new google.maps.Marker({ map: map, position: latlng, icon: marker_icon});
					//var contentString="<img width='80' src=\"images\" />"; //width='80' 
					//var contentString="<img src='images/road.png' />"; //width='80' 
					var contentString="<img src=" + img_db + " alt=" + img_db + " />"; 
					//var contentString=img_db;
					//marker = new google.maps.Marker({
					//	map: map,
					//	position: new google.maps.LatLng(<?php echo $latitude; ?>, <?php echo $longitude; ?>)
					//});
					infowindow = new google.maps.InfoWindow({ content: contentString });
					//infowindow.setContent('<img src=' + img_db + '/>');
					//infowindow.setContent('<img src=' + img_db + ' alt=' + img_db + ' />');
					//infowindow.setContent(img_db);
					//infowindow = new google.maps.InfoWindow({ content: "<?php echo $data_points[$i][0], ", " ,$data_points[$i][1] ?>"  });
					//infowindow = new google.maps.InfoWindow({ content: "<?php echo $latitude, "," ,$longitude  ?>"});

					//google.maps.event.addListener(marker, "click", function () { infowindow.open(map, marker); });
					//infowindow.open(map, marker);
					  google.maps.event.addListener(marker, 'click', (function(marker, img_db) {return function() {
							infowindow.setContent('<img src=' + img_db + ' alt=' + img_db + ' />');
							//infowindow.setContent(img_db);
							infowindow.open(map, marker);
						}
					})(marker,img_db));					

				<?php 
				//echo "contentString=$data_points[$i][3]";
				} 
				?>							


			}
			google.maps.event.addDomListener(window, 'load', init_map);
		</script>
	 
		<?php
	}
	else
	{
	?>
		<div id="gmap_canvas">No datapoints found in DB within the given radius from specified location</div>
	<?php
	}
	closeConnect();
}
else {
?>
 
<div id='latlong-info'>
    <p>All Data points within defined kilometers from defined center will be displayed on the map <br><br>  </p> 


<!-- enter any Lat Long -->
<form method="post" action="<?php echo htmlspecialchars($_SERVER["PHP_SELF"]);?>">
<table style="width:50%">

<tr>
<th> Enter radius in km : </th>
<th> <INPUT TYPE="TEXT" NAME="km" value='3'> </th>
</tr>

<tr>
<th> Enter datapoint for centre location :</th>
<td> <INPUT TYPE="TEXT" NAME="lat" value ="28.634117" > </td>
<td> <INPUT TYPE="TEXT" NAME="lon" value ="77.342119" > </td>
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
