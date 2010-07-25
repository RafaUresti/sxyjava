echo -------------------- Network Topology --------------------
edge 0 1
edge 0 7
edge 1 2
edge 2 3
edge 3 4
edge 3 5
edge 4 9
edge 5 6
edge 6 9
edge 7 8
edge 8 9
edge 9 10
edge 0 19
edge 10 11
edge 10 17
edge 11 12
edge 12 13
edge 13 14
edge 13 15
edge 14 19
edge 15 16
edge 16 19
edge 17 18
edge 18 19
time + 60000

echo -------------------- Build Chord Ring ----------------------
0 join 0
time + 10000
1 join 0
time + 10000
2 join 1
time + 10000
3 join 2
time + 10000
4 join 3
time + 10000
5 join 4
time + 10000
6 join 5
time + 10000
7 join 0
time + 10000
8 join 1
time + 10000
9 join 2
time + 10000
10 join 3
time + 10000
11 join 8
time + 10000
12 join 9
time + 10000
13 join 10
time + 10000
14 join 11
time + 10000
15 join 12
time + 10000
16 join 0
time + 10000
17 join 1
time + 10000
18 join 16
time + 10000
19 join 17
time + 30000

// well-formed chord ring - 10pt
0 ringstate

time + 30000

exit


