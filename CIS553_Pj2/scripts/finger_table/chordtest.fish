edge 0 1
edge 1 2
edge 2 3
edge 3 0

time + 600000
0 dump linkstate
0 dump table

1 dump linkstate
1 dump table

2 dump linkstate
2 dump table

3 dump linkstate
3 dump table

time + 10000
0 join 0
time + 10000
1 join 0
time + 10000
2 join 1
time + 10000
3 join 2
time + 1000000
0 peers
1 peers
2 peers
3 peers

exit
