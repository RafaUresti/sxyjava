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



time + 10000
0 join 0
time + 10000
1 join 0
time + 10000
2 join 1
time + 10000



exit
