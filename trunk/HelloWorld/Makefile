main : main.o
g++ -o main main.o -L C:/MinGW/lib/gcc-lib/mingw32/3.2.3/ -lstdc++
main.o : main.c
g++ -ggdb -c main.c
all :
${MAKE} main
clean :
-del main.o