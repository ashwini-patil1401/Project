<?php
    if($_SERVER['REQUEST_METHOD'] == 'POST') {
        /*Creating variables*/
        $name = $_POST["name"];
        $address = $_POST["address"];
        $age = $_POST["age"];


        $dbhost = "localhost"; /*most of the time it's localhost*/
        $username = "yourusername";
        $password = "yourpassword";
        $dbname = "mydatabase";

        $mysql = mysqli_connect($dbhost, $username, $password, $dbname); /*It connects*/
        $query = "INSERT INTO yourtable (name,address,age) VALUES $name, $address, $age";
        $query = "SELECT name,address,age from yourtable";
        $query = "SELECT sex,location from yourtable";
	$query = "SELECT sex,location from mytable where sex = 'female'";
	$query = "SELECT sex,location from mytable where age in (select age from kids)";
        mysqli_query($mysql, $query);
		        $name = $_POST["name"];
        $address = $_POST["address"];
        $age = $_POST["age"];

    }
?>