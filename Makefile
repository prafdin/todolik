BUILD_IMG := todolik-build-image
WORKDIR := /app

DOCKERFILE := build/Dockerfile

.PHONY: build clean

docker-build:
	docker build -f $(DOCKERFILE) -t $(BUILD_IMG) .

build: docker-build
	docker run --rm \
		-v $(PWD):$(WORKDIR)\
		-w $(WORKDIR) $(BUILD_IMG) \
		sbt \
		Debian/packageBin
# Очистка результата
clean:
	rm -f $(OUT)
