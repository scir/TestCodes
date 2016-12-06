
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
    #latitudelongitude-examples{
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
	// put data points in a multi-dim array
	$data_points =array(
		array("28.634117", "77.342119"),
		array("28.636788", "77.374073"),
		array("28.634836", "77.362324"),
		array("28.644942", "77.335388")
	);
    
	// create an array to put the data
	$data_arr = array();      
	
    // if there are data points
    if($_POST['latitude'] && $_POST['longitudeg'])
	{
		// get latitudeitude, longitudegitude 
		array_push($data_arr, $_POST['latitude'], $_POST['longitudeg']);

        //$latitudeitude = $data_arr[0];
        //$longitudegitude = $data_arr[1];
		array_push($data_points,$data_arr);
		//$latitudeitude = "28.6";
        //$longitudegitude = "77.3";
        //$formatted_address = $data_arr[2];
    // if unable to geocode the address
    }
	//else{
    //    echo "No map found.";
    //}
	$latitudeitude = $data_points[0][0];
	$longitudegitude = $data_points[0][1];
    ?>
 
    <!-- google map will be shown here -->
    <div id="gmap_canvas">Loading map... count of data points...<?php echo count($data_points); ?></div>
    <div id='map-label'>Map shows approximate location.</div>
 
    <!-- JavaScript to show google map -->
    <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>    
    <script type="text/javascript">
        function init_map() {
            var myOptions = {
                zoom: 14,
                center: new google.maps.LatLng(<?php echo $latitudeitude; ?>, <?php echo $longitudegitude; ?>),
                mapTypeId: google.maps.MapTypeId.ROADMAP
            };
			// Draw a map on DIV "gmap_canvas"
            map = new google.maps.Map(document.getElementById("gmap_canvas"), myOptions);
			
			//geocoder = new google.maps.Geocoder();
			<?php 
			for($i=0;$i<count($data_points);$i++){?>
				var latitudelng = new google.maps.LatLng(<?php echo $data_points[$i][0]; ?>, <?php echo $data_points[$i][1]; ?>);
				var image = 'images/blue-pinpoint.png';
				var marker = new google.maps.Marker({ map: map, position: latitudelng}); //, icon: image});

				//marker = new google.maps.Marker({
				//	map: map,
				//	position: new google.maps.LatLng(<?php echo $latitudeitude; ?>, <?php echo $longitudegitude; ?>)
				//});
				infowindow = new google.maps.InfoWindow({ content: "<?php echo $data_points[$i][0], ", " ,$data_points[$i][1] ?>"  });
				//infowindow = new google.maps.InfoWindow({ content: "<?php echo $latitudeitude, "," ,$longitudegitude  ?>"});

				google.maps.event.addListener(marker, "click", function () { infowindow.open(map, marker); });
				infowindow.open(map, marker);				
			<?php }?>							


        }
        google.maps.event.addDomListener(window, 'load', init_map);
    </script>
 
    <?php
 
}
?>
 
<div id='latitudelongitudeg-examples'>
    <div>Lat Long Displayed :</div>
    <div>1. (28.634117, 77.342119) for Gaur Green Avenue Abhay Khand 2 Indirapuram Ghaziabad India</div>
    <div>2. (28.636788, 77.374073) for Shipra Sun city Indirapuram Ghaziabad India</div>
	<div>3. (28.634836, 77.362324) for Mahagun Mansion Indirapuram Ghaziabad India</div>
	<div>4. (28.644942, 77.335388) for Mahagun Metro Mall Indirapuram Ghaziabad India</div>
</div>
 
<!-- enter any Lat Long -->
<form action="" method="post">
    <input type='text' name='latitude' placeholder='Enter any Lat here' />
	<input type='text' name='longitudeg' placeholder='Enter any Long here' />
    <input type='submit' name='map' value='Display Map!' />
</form>
 
<?php
include("includes/footer.php"); // include footer content
?>
</body>
</html>
