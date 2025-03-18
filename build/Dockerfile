# syntax=docker/dockerfile:1

# https://github.com/sbt/docker-sbt/tree/master
FROM sbtscala/scala-sbt:eclipse-temurin-23.0.2_7_1.10.7_3.6.3 AS build

WORKDIR /src

RUN apt-get update && apt-get install -y \
    dpkg-dev \
    lintian \
    fakeroot

COPY . .
RUN --mount=type=cache,target=/root/.cache/coursier \
     sbt Debian/packageBin

FROM scratch AS bin

COPY --from=build /src/target /