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

echo -------------------- Inject Data --------------------------------
0 store Boston Celtics
4 store NewJersey Nets
8 store NewYork Knicks
12 store Philadelphia 76ers
16 store Toronto Raptors
1 store Chicago Bulls
5 store Cleveland Cavaliers
9 store Detroit Pistons
13 store Indiana Pacers
17 store Milwaukee Bucks
2 store Atlanta Hawks
6 store Charlotte Bobcats
10 store Miami Heat
14 store Orlando Magic
18 store Washington Wizards
3 store Dallas Mavericks
7 store Houston Rockets
11 store Memphis Grizzlies
15 store NewOrleans Hornets
19 store SanAntonio Spurs
0 store GoldenState Warriors
1 store LosAngeles Clippers
2 store LosAngeles Lakers
3 store Phoenix Suns
4 store Sacramento Kings
5 store Denver Nuggets
6 store Minnesota Timberwolves
7 store Portland TrailBlazers
8 store OklahomaCity Thunder
9 store Utah Jazz

time + 30000

echo -------------------- Lookup Test ------------------------------
//correct and consistent lookup result - 2pt each (20pt total)

0 lookup LosAngeles
time + 10000
4 lookup LosAngeles
time + 10000
8 lookup LosAngeles
time + 10000
12 lookup LosAngeles
time + 10000
16 lookup LosAngeles
time + 10000
7 lookup Philadelphia
time + 10000
8 lookup Boston
time + 10000
9 lookup Dallas
time + 10000
10 lookup Houston
time + 10000
11 lookup Portland
time + 30000

echo -------------------- Voluntary Leaving -------------------
1 leave
time + 10000
2 leave
time + 10000
3 leave
time + 10000
4 leave
time + 10000

echo -------------------- Ring Size = 16 -------------------------
//well-formed chord ring - 3pt
//correct lookup result - 5pt

0 ringstate
time + 10000
0 lookupdebug Philadelphia
time + 10000
13 lookupdebug Philadelphia
time + 10000
19 lookupdebug Philadelphia
time + 10000
5 lookupdebug Philadelphia
time + 10000

13 leave
time + 10000
14 leave
time + 10000
15 leave
time + 10000
8 leave
time + 10000

echo -------------------- Ring Size = 12 -------------------------
//well-formed chord ring - 3pt
//correct lookup result - 5pt

12 ringstate
time + 10000
12 lookupdebug Philadelphia
time + 10000
7 lookupdebug LosAngeles
time + 10000
9 lookupdebug Houston
time + 10000

13 join 19
time + 10000
14 join 19
time + 10000
15 join 19
time + 10000

echo -------------------- Ring Size = 15 -------------------------
//well-formed chord ring - 3pt
//correct lookup result - 5pt

13 ringstate
time + 10000
13 lookupdebug Philadelphia
time + 10000
14 lookupdebug LosAngeles
time + 10000
15 lookupdebug Houston
time + 10000

1 join 13
time + 10000
2 join 15
time + 10000

echo -------------------- Ring Size = 17 -------------------------
//well-formed chord ring - 3pt
//correct lookup result - 5pt

1 ringstate
time + 10000
1 lookupdebug Philadelphia
time + 10000
2 lookupdebug Houston
time + 10000

3 join 1
time + 10000
4 join 6
time + 10000
8 join 0
time + 10000

echo -------------------- Ring Size = 20 -------------------------
//well-formed chord ring - 3pt
//correct lookup result - 5pt

8 ringstate
time + 10000
8 lookupdebug Philadelphia
time + 10000
8 lookupdebug Houston
time + 10000

exit


