.PHONY: build clean

build:
	docker build -f build/Dockerfile --target bin --output out .

install: build
	sudo dpkg -i $(wildcard out/todolik_*_all.deb)

clean:
	rm -rf out
