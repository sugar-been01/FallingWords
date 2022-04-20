JAVAC=/usr/bin/javac
SRCDIR=src
BINDIR=bin
docdir=./docs

default:
	$(JAVAC) -d $(BINDIR) $(SRCDIR)/*.java
clean:
	rm $(BINDIR)/*.class
app:
	java -cp bin WordApp
docs:
	javadoc -d $(docdir) $(SRCDIR)/*.java

