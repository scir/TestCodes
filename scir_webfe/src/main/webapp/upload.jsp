<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>File Upload</title>
</head>

<body>
	<center>
		<h2>File Upload</h2>
		<form method="post" action="AddTicket" enctype="multipart/form-data">
			<table style="width:600px" border="1">
				<tr>
					<td style="width:150px">Problem / Suggestion: </td>
					<td><input type=text name="summary" /></td>
				</tr>
				<tr>
					<td>Type: </td>
					<td>
						<select name="type">
							<option value="Electricity">Electricity</option>
							<option value="Road">Road</option>
							<option value="Sanitation">Sanitation</option>
							<option value="Sewage">Sewage</option>
							<option value="Water">Water</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>Severity: </td>
					<td>
						<select name="severity">
							<option value="Normal">Normal</option>
							<option value="Low">Low</option>
							<option value="High">High</option>
							<option value="Urgent">Urgent</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>Image: </td>
					<td><input type="file" name="imgFile" /> (Select Image to Upload)</td>
				</tr>
				<tr><td colspan="2">&nbsp;</td></tr>
				<tr>
					<td colspan="2" align="center">
						<input type=hidden name="deviceId" value="DEV-MI3-5345365" />
						<input type=hidden name="msisdn" value="9891100100" />
						<input type=hidden name="latitude" value="28.6139" />
						<input type=hidden name="longitude" value="77.2090" />
						<input type=hidden name="time" value="2015-11-30 16:25:45" />
						<input type="submit" value="Upload" />
					</td>
				</tr>
			</table>
			
		</form>
	</center>
</body>

</html>