edge 0 1
edge 1 2
edge 2 3
edge 3 4
edge 4 5
edge 5 6
edge 6 7
edge 8 9
edge 9 10
edge 9 18
edge 10 11
edge 11 12
edge 12 13
edge 13 14
edge 14 15
edge 16 17
edge 17 18
edge 18 19
edge 19 20
edge 20 21
edge 21 22
edge 22 23
edge 24 25
edge 0 8
edge 8 16
edge 16 24
edge 1 9
edge 9 17
edge 17 25
edge 2 10
edge 10 18
edge 3 11
edge 11 19
edge 4 12
edge 12 20
edge 5 13
edge 13 21
edge 6 14
edge 14 22
edge 7 15
edge 15 23
time + 120000
1 4 Ping before failing 2
time + 20000
echo ----------failing 2-----------
fail 2
time + 120000
1 4 Ping after failing 2
time + 120000
echo ----------restarting 2-----------
restart 2
time + 120000
1 4 Ping after restarting 2
exit