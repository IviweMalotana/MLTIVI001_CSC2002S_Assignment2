JFLAGS = -g
JC = javac

.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES= \
	Water.java \
	Terrain.java \
	FlowPanel.java \
	Flow.java \

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class

run:
	java Flow "medsample_in.txt"
