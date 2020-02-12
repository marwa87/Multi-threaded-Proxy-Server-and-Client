Compile the code:
================
open a terminal and from inside the code folder issue the following command

javac -d . -cp ./;jsoup-1.12.1.jar *.java
javac -d . -cp ./;jsoup-1.12.1.jar *.java


Run the proxy server:
=====================
in the terminal from inside the code folder after compilation issue the following command

java -cp ./;jsoup-1.12.1.jar ProxyServer PORT

Example : 
java -cp ./;jsoup-1.12.1.jar ProxyServer 1234

where PORT is the port number you want the proxy server to run


Run the HttpClient:
===================
in a new terminal from inside the code folder after compilation issue the following command

java -cp ./;jsoup-1.12.1.jar HttpClient 127.0.0.1 PORT URL OUTPUT_FOLDER

Example:
java -cp ./;jsoup-1.12.1.jar HttpClient 127.0.0.1 1234 https://www.ucmo.edu ./dest2
java -cp ./;jsoup-1.12.1.jar HttpClient 127.0.0.1 1234 https://www.google.com ./dest


Get the IP of the URL ( blacklist)
======================================
java IpAddress https://www.google.com

save the ip address in txt file in the same folde 
when client request the blacklist ip it will give the website is blakclist.

########################################################

where arguments are as below:

PORT is the same PORT as proxy server
URL is the absolute website URL
OUTPUT_FOLDER the name of the output folder where all data will be saved.


NB: In the current code version data are not saved but only displayed to console.