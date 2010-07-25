# Makefile for Fishnet

JAVAC = /usr/java/jdk1.5.0/bin/javac
FLAGS = -nowarn -g
JAVA_FILES = $(wildcard lib/*.java) $(wildcard proj/*.java)

.PHONY = all clean

all: $(JAVA_FILES)
	@echo 'Making all...'
	@$(JAVAC) $(FLAGS) $?

clean:
	rm -f $(JAVA_FILES:.java=.class)
	rm -f *~ lib/*~ proj/*~
