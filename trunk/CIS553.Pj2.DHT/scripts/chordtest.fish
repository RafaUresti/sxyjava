edge 0 1
edge 1 2
edge 0 2

time + 600000
0 dump linkstate
0 dump table
time + 10000
0 join 0
time + 10000
1 join 0
time + 10000
2 join 1
time + 10000
2 store britney spears
time + 10000
0 lookup britney
time + 10000
1 lookup britney
time + 10000
2 lookup britney
time + 10000
1 leave
time + 10000
2 lookup britney
exit
