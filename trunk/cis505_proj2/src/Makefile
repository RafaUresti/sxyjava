all: clean compile

compile:
	find -name "*.java" -exec javac {} +

clean:
	$(RM) -r *.class
cleanr:
	$(RM) -r *~ *\#

cleaner: clean cleanr

make:
	xemacs Makefile &
