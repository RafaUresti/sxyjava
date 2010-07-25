# Makefile for CIS 551 Project 4: Secure Banking System

JAVAC = /usr/java/jdk1.5.0/bin/javac
FLAGS = -nowarn -g -classpath lib/bcprov-jdk15-134.jar
JAVA_FILES = $(wildcard src/cis551proj4/*.java) $(wildcard src/cis551proj4/log/*.java)

.PHONY = all clean

all: $(JAVA_FILES)
	@echo 'Making all...'
	@$(JAVAC) $(FLAGS) $?

clean:
	rm -f $(JAVA_FILES:.java=.class)
	rm -f *~ src/cis551proj4/*~ src/cis551proj4/log/*~