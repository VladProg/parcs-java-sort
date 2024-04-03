all: run

clean:
	rm -f out/Sort.jar

out/Sort.jar: out/parcs.jar src/Sort.java
	@mkdir -p temp
	@javac -cp out/parcs.jar -d temp src/Sort.java
	@jar cf out/Sort.jar -C temp .
	@rm -rf temp/

build: out/Sort.jar

run: out/Sort.jar
	@cd out && java -cp 'parcs.jar:Sort.jar' Sort $(WORKERS)
