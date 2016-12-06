
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
     
    #img-canvas{
        width:100%;
        height:10em;
    }
     
    #img-label{
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
if($_POST){
    if($_POST['fname'])
	{
		$imagename="images/" . $_POST['fname']. ".png";
	} else {
		$imagename="images/blue-pinpoint.png";
	}
	
?>

    <!-- Image will be shown here -->
    <div id="img-canvas">Loading Image</div>
    <div id='img-label'>Image is displayed</div>
<?php		
	echo("<br>img src=$imagename<br>");
	echo("<img src=\"$imagename\" />");
}
?>

 
<!-- submit button -->
<form action="" method="post">
	<input type='text' name='fname' placeholder='Enter Image File Name here' />
    <input type='submit' value='Display Image!' />
</form>
 
<?php
include("includes/footer.php"); // include footer content
?>
</body>
</html>