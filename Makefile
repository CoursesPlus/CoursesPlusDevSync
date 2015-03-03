JFLAGS = -g -cp .
JC = javac
JAR = jar
JARFLAGS = cfe output.jar tk.coursesplus.development.devsync.CoursesPlusDevSync -C ./src/
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $(wildcard src/tk/coursesplus/development/devsync/*.java)

CLASSES = \
	$(patsubst %,%,$(wildcard src/tk/coursesplus/development/devsync/*.java))
JARCLASSES = \
	$(patsubst src/%,%,$(wildcard src/tk/coursesplus/development/devsync/*.java))

all: classes jar

default: classes

packages: jar

jar:
	$(JAR) $(JARFLAGS) $(JARCLASSES:.java=.class)

classes: $(CLASSES:.java=.class)

clean:
	$(RM) src/tk/coursesplus/development/devsync/*.class
	$(RM) output.jar
