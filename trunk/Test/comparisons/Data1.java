package comparisons;

public class Data1 {

	static String a = "usa 12\r\n" + 
			"** SMTP server: start\r\n" + 
			"** Cluster in-commu server starts...\r\n" + 
			"Input GENERAL command\r\n" + 
			"Pop3 listening...\r\n" + 
			"** Pop3 server begin new handler.\r\n" + 
			"** Pop3 server handler thread started.\r\n" + 
			"+OK POP3 server ready\r\n" + 
			"-- Pop3Handler: exception\r\n" + 
			"-- Cluster_Internal_Request_Handler: exception\r\n" + 
			"java.net.SocketException: Software caused connection abort: recv failed\r\n" + 
			"	at java.net.SocketInputStream.socketRead0(Native Method)\r\n" + 
			"	at java.net.SocketInputStream.read(SocketInputStream.java:129)\r\n" + 
			"	at java.io.ObjectInputStream$PeekInputStream.read(ObjectInputStream.java:2266)\r\n" + 
			"	at java.io.ObjectInputStream$PeekInputStream.readFully(ObjectInputStream.java:2279)\r\n" + 
			"	at java.io.ObjectInputStream$BlockDataInputStream.readShort(ObjectInputStream.java:2750)\r\n" + 
			"	at java.io.ObjectInputStream.readStreamHeader(ObjectInputStream.java:780)\r\n" + 
			"	at java.io.ObjectInputStream.<init>(ObjectInputStream.java:280)\r\n" + 
			"	at edu.upenn.cis505.g48.cluster_internal_communication.Cluster_Internal_Request_Handler.begin_handle(Cluster_Internal_Request_Handler.java:38)\r\n" + 
			"	at edu.upenn.cis505.g48.cluster_internal_communication.Cluster_Internal_Request_Handler.run(Cluster_Internal_Request_Handler.java:72)\r\n" + 
			"	at java.lang.Thread.run(Thread.java:619)\r\n" + 
			"** Server: BEGIN to get data from other server in my cluster\r\n" + 
			"\r\n" + 
			"** Server: Fail to get init data\r\n" + 
			"send [usa-45112]SMTP server get request: [QUIT\r\n" + 
			"]\r\n" + 
			"receive QUIT, close this service thread\r\n" + 
			"<usa-45112> cluster_chanel get request [DATA_REQUEST]\r\n" + 
			"1The data before transfer: \r\n" + 
			"** Disp server data:\r\n" + 
			"** domain [PA.com]: [psu] [upenn] [drexel] \r\n" + 
			"** domain [CA]: [ucla] [caltech] [stanford] \r\n" + 
			"** domain [usa]: [postmaster] \r\n" + 
			"11 upenn PA.com<usa-45112> cluster_chanel get request [INCOMING_EMAIL]\r\n" + 
			"Cluster email: 222 From:127.0.0.1:45113_#0\r\n" + 
			"222 From:127.0.0.1:45113_#0 step 2 done\r\n" + 
			"delivered email: time1_222 From:127.0.0.1:45113_#0\r\n" + 
			"<usa-45112> cluster_chanel get request [INCOMING_EMAIL]\r\n" + 
			"Cluster email: 222 From:127.0.0.1:45113_#1\r\n" + 
			"222 From:127.0.0.1:45113_#1 step 2 done\r\n" + 
			"delivered email: time2_222 From:127.0.0.1:45113_#1\r\n" + 
			"\r\n" + 
			"<usa-45112> cluster_chanel get request [INCOMING_EMAIL]\r\n" + 
			"Cluster email: 222 From:127.0.0.1:45113_#2\r\n" + 
			"222 From:127.0.0.1:45113_#2 step 2 done\r\n" + 
			"Failed to establish socket connection to 127.0.0.1:45223\r\n" + 
			"111 From:127.0.0.1:45112_#0 step 1 done\r\n" + 
			"delivered email: time3_111 From:127.0.0.1:45112_#0\r\n" + 
			"delivered email: time3_222 From:127.0.0.1:45113_#2\r\n" + 
			"111 From:127.0.0.1:45112_#0 step 3 done\r\n" + 
			"<usa-45112> cluster_chanel get request [INCOMING_EMAIL]\r\n" + 
			"Cluster email: 222 From:127.0.0.1:45113_#3\r\n" + 
			"222 From:127.0.0.1:45113_#3 step 2 done\r\n" + 
			"Failed to establish socket connection to 127.0.0.1:45223\r\n" + 
			"111 From:127.0.0.1:45112_#1 step 1 done\r\n" + 
			"delivered email: time4_111 From:127.0.0.1:45112_#1\r\n" + 
			"delivered email: time4_222 From:127.0.0.1:45113_#3\r\n" + 
			"111 From:127.0.0.1:45112_#1 step 3 done\r\n" + 
			"<usa-45112> cluster_chanel get request [INCOMING_EMAIL]\r\n" + 
			"Cluster email: 222 From:127.0.0.1:45113_#4\r\n" + 
			"222 From:127.0.0.1:45113_#4 step 2 done\r\n" + 
			"Failed to establish socket connection to 127.0.0.1:45223\r\n" + 
			"111 From:127.0.0.1:45112_#2 step 1 done\r\n" + 
			"delivered email: time5_111 From:127.0.0.1:45112_#2\r\n" + 
			"delivered email: time5_222 From:127.0.0.1:45113_#4\r\n" + 
			"111 From:127.0.0.1:45112_#2 step 3 done\r\n" + 
			"<usa-45112> cluster_chanel get request [INCOMING_EMAIL]\r\n" + 
			"Cluster email: 222 From:127.0.0.1:45113_#5\r\n" + 
			"222 From:127.0.0.1:45113_#5 step 2 done\r\n" + 
			"Failed to establish socket connection to 127.0.0.1:45223\r\n" + 
			"111 From:127.0.0.1:45112_#3 step 1 done\r\n" + 
			"delivered email: time6_111 From:127.0.0.1:45112_#3\r\n" + 
			"delivered email: time6_222 From:127.0.0.1:45113_#5\r\n" + 
			"111 From:127.0.0.1:45112_#3 step 3 done\r\n" + 
			"<usa-45112> cluster_chanel get request [INCOMING_EMAIL]\r\n" + 
			"Cluster email: 222 From:127.0.0.1:45113_#6\r\n" + 
			"222 From:127.0.0.1:45113_#6 step 2 done\r\n" + 
			"Failed to establish socket connection to 127.0.0.1:45223\r\n" + 
			"111 From:127.0.0.1:45112_#4 step 1 done\r\n" + 
			"delivered email: time7_111 From:127.0.0.1:45112_#4\r\n" + 
			"delivered email: time7_222 From:127.0.0.1:45113_#6\r\n" + 
			"111 From:127.0.0.1:45112_#4 step 3 done\r\n" + 
			"<usa-45112> cluster_chanel get request [INCOMING_EMAIL]\r\n" + 
			"Cluster email: 222 From:127.0.0.1:45113_#7\r\n" + 
			"222 From:127.0.0.1:45113_#7 step 2 done\r\n" + 
			"Failed to establish socket connection to 127.0.0.1:45223\r\n" + 
			"111 From:127.0.0.1:45112_#5 step 1 done\r\n" + 
			"delivered email: time8_111 From:127.0.0.1:45112_#5\r\n" + 
			"delivered email: time8_222 From:127.0.0.1:45113_#7\r\n" + 
			"111 From:127.0.0.1:45112_#5 step 3 done\r\n" + 
			"<usa-45112> cluster_chanel get request [INCOMING_EMAIL]\r\n" + 
			"Cluster email: 222 From:127.0.0.1:45113_#8\r\n" + 
			"222 From:127.0.0.1:45113_#8 step 2 done\r\n" + 
			"Failed to establish socket connection to 127.0.0.1:45223\r\n" + 
			"111 From:127.0.0.1:45112_#6 step 1 done\r\n" + 
			"delivered email: time9_111 From:127.0.0.1:45112_#6\r\n" + 
			"delivered email: time9_222 From:127.0.0.1:45113_#8\r\n" + 
			"111 From:127.0.0.1:45112_#6 step 3 done\r\n" + 
			"<usa-45112> cluster_chanel get request [INCOMING_EMAIL]\r\n" + 
			"Cluster email: 222 From:127.0.0.1:45113_#9\r\n" + 
			"222 From:127.0.0.1:45113_#9 step 2 done\r\n" + 
			"Failed to establish socket connection to 127.0.0.1:45223\r\n" + 
			"111 From:127.0.0.1:45112_#7 step 1 done\r\n" + 
			"delivered email: time10_111 From:127.0.0.1:45112_#7\r\n" + 
			"delivered email: time10_222 From:127.0.0.1:45113_#9\r\n" + 
			"111 From:127.0.0.1:45112_#7 step 3 done\r\n" + 
			"<usa-45112> cluster_chanel get request [INCOMING_EMAIL]\r\n" + 
			"Cluster email: 222 From:127.0.0.1:45113_#10\r\n" + 
			"222 From:127.0.0.1:45113_#10 step 2 done\r\n" + 
			"Failed to establish socket connection to 127.0.0.1:45223\r\n" + 
			"111 From:127.0.0.1:45112_#8 step 1 done\r\n" + 
			"delivered email: time11_111 From:127.0.0.1:45112_#8\r\n" + 
			"delivered email: time11_222 From:127.0.0.1:45113_#10\r\n" + 
			"111 From:127.0.0.1:45112_#8 step 3 done\r\n" + 
			"Failed to establish socket connection to 127.0.0.1:45223\r\n" + 
			"<usa-45112> cluster_chanel get request [INCOMING_EMAIL]\r\n" + 
			"Cluster email: 222 From:127.0.0.1:45113_#11\r\n" + 
			"222 From:127.0.0.1:45113_#11 step 2 done\r\n" + 
			"111 From:127.0.0.1:45112_#9 step 1 done\r\n" + 
			"delivered email: time12_111 From:127.0.0.1:45112_#9\r\n" + 
			"delivered email: time12_222 From:127.0.0.1:45113_#11\r\n" + 
			"111 From:127.0.0.1:45112_#9 step 3 done\r\n" + 
			"<usa-45112> cluster_chanel get request [INCOMING_EMAIL]\r\n" + 
			"Cluster email: 222 From:127.0.0.1:45113_#12\r\n" + 
			"222 From:127.0.0.1:45113_#12 step 2 done\r\n" + 
			"Failed to establish socket connection to 127.0.0.1:45223\r\n" + 
			"111 From:127.0.0.1:45112_#10 step 1 done\r\n" + 
			"delivered email: time13_111 From:127.0.0.1:45112_#10\r\n" + 
			"delivered email: time13_222 From:127.0.0.1:45113_#12\r\n" + 
			"111 From:127.0.0.1:45112_#10 step 3 done\r\n" + 
			"<usa-45112> cluster_chanel get request [INCOMING_EMAIL]\r\n" + 
			"Cluster email: 222 From:127.0.0.1:45113_#13\r\n" + 
			"222 From:127.0.0.1:45113_#13 step 2 done\r\n" + 
			"Failed to establish socket connection to 127.0.0.1:45223\r\n" + 
			"111 From:127.0.0.1:45112_#11 step 1 done\r\n" + 
			"delivered email: time14_111 From:127.0.0.1:45112_#11\r\n" + 
			"delivered email: time14_222 From:127.0.0.1:45113_#13\r\n" + 
			"111 From:127.0.0.1:45112_#11 step 3 done\r\n" + 
			"<usa-45112> cluster_chanel get request [INCOMING_EMAIL]\r\n" + 
			"Cluster email: 222 From:127.0.0.1:45113_#14\r\n" + 
			"222 From:127.0.0.1:45113_#14 step 2 done\r\n" + 
			"Failed to establish socket connection to 127.0.0.1:45223\r\n" + 
			"111 From:127.0.0.1:45112_#12 step 1 done\r\n" + 
			"delivered email: time15_111 From:127.0.0.1:45112_#12\r\n" + 
			"delivered email: time15_222 From:127.0.0.1:45113_#14\r\n" + 
			"111 From:127.0.0.1:45112_#12 step 3 done\r\n" + 
			"Failed to establish socket connection to 127.0.0.1:45223\r\n" + 
			"111 From:127.0.0.1:45112_#13 step 1 done\r\n" + 
			"delivered email: time16_111 From:127.0.0.1:45112_#13\r\n" + 
			"<usa-45112> cluster_chanel get request [INCOMING_EMAIL]\r\n" + 
			"Cluster email: 222 From:127.0.0.1:45113_#15\r\n" + 
			"222 From:127.0.0.1:45113_#15 step 2 done\r\n" + 
			"111 From:127.0.0.1:45112_#13 step 3 done\r\n" + 
			"Failed to establish socket connection to 127.0.0.1:45223\r\n" + 
			"111 From:127.0.0.1:45112_#14 step 1 done\r\n" + 
			"delivered email: time16_222 From:127.0.0.1:45113_#15\r\n" + 
			"delivered email: time17_111 From:127.0.0.1:45112_#14\r\n" + 
			"<usa-45112> cluster_chanel get request [INCOMING_EMAIL]\r\n" + 
			"Cluster email: 222 From:127.0.0.1:45113_#16\r\n" + 
			"222 From:127.0.0.1:45113_#16 step 2 done\r\n" + 
			"delivered email: time17_222 From:127.0.0.1:45113_#16\r\n" + 
			"111 From:127.0.0.1:45112_#14 step 3 done\r\n" + 
			"Failed to establish socket connection to 127.0.0.1:45223\r\n" + 
			"<usa-45112> cluster_chanel get request [INCOMING_EMAIL]\r\n" + 
			"Cluster email: 222 From:127.0.0.1:45113_#17\r\n" + 
			"222 From:127.0.0.1:45113_#17 step 2 done\r\n" + 
			"111 From:127.0.0.1:45112_#15 step 1 done\r\n" + 
			"delivered email: time18_111 From:127.0.0.1:45112_#15\r\n" + 
			"delivered email: time18_222 From:127.0.0.1:45113_#17\r\n" + 
			"111 From:127.0.0.1:45112_#15 step 3 done\r\n" + 
			"Failed to establish socket connection to 127.0.0.1:45223\r\n" + 
			"111 From:127.0.0.1:45112_#16 step 1 done\r\n" + 
			"delivered email: time19_111 From:127.0.0.1:45112_#16\r\n" + 
			"111 From:127.0.0.1:45112_#16 step 3 done\r\n" + 
			"<usa-45112> cluster_chanel get request [INCOMING_EMAIL]\r\n" + 
			"Cluster email: 222 From:127.0.0.1:45113_#18\r\n" + 
			"222 From:127.0.0.1:45113_#18 step 2 done\r\n" + 
			"Failed to establish socket connection to 127.0.0.1:45223\r\n" + 
			"111 From:127.0.0.1:45112_#17 step 1 done\r\n" + 
			"delivered email: time19_222 From:127.0.0.1:45113_#18\r\n" + 
			"delivered email: time20_111 From:127.0.0.1:45112_#17\r\n" + 
			"111 From:127.0.0.1:45112_#17 step 3 done\r\n" + 
			"<usa-45112> cluster_chanel get request [INCOMING_EMAIL]\r\n" + 
			"Cluster email: 222 From:127.0.0.1:45113_#19\r\n" + 
			"222 From:127.0.0.1:45113_#19 step 2 done\r\n" + 
			"Failed to establish socket connection to 127.0.0.1:45223\r\n" + 
			"111 From:127.0.0.1:45112_#18 step 1 done\r\n" + 
			"delivered email: time20_222 From:127.0.0.1:45113_#19\r\n" + 
			"delivered email: time21_111 From:127.0.0.1:45112_#18\r\n" + 
			"111 From:127.0.0.1:45112_#18 step 3 done\r\n" + 
			"Failed to establish socket connection to 127.0.0.1:45223\r\n" + 
			"111 From:127.0.0.1:45112_#19 step 1 done\r\n" + 
			"delivered email: time22_111 From:127.0.0.1:45112_#19\r\n" + 
			"Input GENERAL command\r\n" + 
			"111 From:127.0.0.1:45112_#19 step 3 done\r\n" + 
			"";}
