all: run

clean:
	rm -f out/Sort.jar out/*.txt

generate:
	python3 src/generator.py 100000 > out/input-100000.txt
	python3 src/generator.py 1000000 > out/input-1000000.txt
	python3 src/generator.py 10000000 > out/input-10000000.txt

out/Sort.jar: out/parcs.jar src/Sort.java
	@mkdir -p temp
	@javac -cp out/parcs.jar -d temp src/Sort.java
	@jar cf out/Sort.jar -C temp .
	@rm -rf temp/

build: out/Sort.jar

run: out/Sort.jar
	@cd out && java -cp 'parcs.jar:Sort.jar' Sort $(WORKERS)
