
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
if(isset($_POST['map'])) {
	include_once "get_data_DB.php";
	connectToDB();
	// create a multi-dim array
	$data_points =array(array());
    //unset($data_points[0]);
	

	$miles="30";
	$miles=$_POST['miles'];
	//echo "miles=$miles";
	// show lat long
	$result = getLatLongWithinRadius($miles);
	
	$num_rows = mysql_num_rows($result);
	echo("Row Count=$num_rows \n");

	for ($i = 0; $i < $num_rows ; $i++) 
	{
		$row = mysql_fetch_array($result, MYSQL_ASSOC);

		// echo("</br><b>".$row['latitude']."-".$row['longitude']."::::</br>");
		// get latitude, longitude 
		// create an array to put the data
		$data_arr = array();      

		array_push($data_arr, $row['latitude'], $row['longitude']);
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
	$latitude = "28.634117";
	$longitude = "77.342119";
	$metres = $miles / 0.00062137;
	//$metres = gmp_div_q($miles , 0.00062137);
	//echo("</br><b>metres=$metres</br>");
    ?>
 
    <!-- google map will be shown here -->
    <div id="gmap_canvas">Loading map... count of data points...<?php echo count($data_points); ?></div>
    <div id='map-label'>Map shows approximate location.</div>
 
    <!-- JavaScript to show google map -->
    <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>    
    <script type="text/javascript">
        function init_map() {
            var myOptions = {
                zoom: 15,
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
			for($i = 0; $i < count($data_points); $i++) {?>
				var latlng = new google.maps.LatLng(<?php echo $data_points[$i][0]; ?>, <?php echo $data_points[$i][1]; ?>);
				var image = 'images/blue-pinpoint.png';
				var marker = new google.maps.Marker({ map: map, position: latlng}); //, icon: image});

				//marker = new google.maps.Marker({
				//	map: map,
				//	position: new google.maps.LatLng(<?php echo $latitude; ?>, <?php echo $longitude; ?>)
				//});
				infowindow = new google.maps.InfoWindow({ content: "<?php echo $data_points[$i][0], ", " ,$data_points[$i][1] ?>"  });
				//infowindow = new google.maps.InfoWindow({ content: "<?php echo $latitude, "," ,$longitude  ?>"});

				google.maps.event.addListener(marker, "click", function () { infowindow.open(map, marker); });
				infowindow.open(map, marker);				
			<?php }?>							


        }
        google.maps.event.addDomListener(window, 'load', init_map);
    </script>
 
    <?php
	closeConnect();
}
?>
 
<div id='latlong-info'>
    <div>All Data points(Lat,Long) from DB within defined miles from <br> (28.634117, 77.342119) - Gaur Green Avenue Abhay Khand 2 Indirapuram Ghaziabad India will be displayed</div>
</div>
 
<!-- enter any Lat Long -->
<form action="" method="post">
	<P><INPUT TYPE="TEXT" NAME="miles" PLACEHOLDER='Enter miles here'></P>
	<P><INPUT TYPE="SUBMIT" NAME="map" VALUE='Display Map!'></P>
</form>
 
<?php
include("includes/footer.php"); // include footer content
?>
</body>
</html>
