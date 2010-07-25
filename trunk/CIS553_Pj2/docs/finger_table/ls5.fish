edge 0 4
edge 1 4
edge 2 4
edge 3 4
edge 0 1
edge 1 2
edge 2 3
time + 120000
echo --------- 0 Pings 3 before failing 4--------
0 3 Ping before failing 4
time + 20000
echo --------- Failing node 4 -----
fail 4
time + 120000
echo --------- 0 Pings 3 after failing 4--------
0 3 Ping after failing 4
time + 20000
echo --------- Restarting node 4 -----
restart 4
time + 60000
echo --------- 0 Pings 3 after restarting 4--------
0 3 Ping after restarting 4
time + 20000
exit