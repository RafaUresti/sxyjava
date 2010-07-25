# Makefile for NEMS

JAVAC = /usr/java/jdk1.5.0/bin/javac
FLAGS = -nowarn -g
JAVA_FILES = $(wildcard src/cis551proj3/main/*.java) $(wildcard src/cis551proj3/cascii/*.java) $(wildcard src/cis551proj3/cracker/*.java) $(wildcard src/cis551proj3/sdes/*.java) $(wildcard src/cis551proj3/dict/*.java)

.PHONY = all clean

all: $(JAVA_FILES)
	@echo 'Making all...'
	@$(JAVAC) $(FLAGS) $?

clean:
	rm -f $(JAVA_FILES:.java=.class)
	rm -f *~ src/cis551proj3/main/*~ src/cis551proj3/cascii/*~ src/cis551proj3/cracker/*~ src/cis551proj3/sdes/*~ src/cis551proj3/dict/*~
