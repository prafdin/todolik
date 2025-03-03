# https://github.com/sbt/docker-sbt/tree/master
FROM sbtscala/scala-sbt:eclipse-temurin-23.0.2_7_1.10.7_3.6.3

RUN apt-get update && apt-get install -y \
    dpkg-dev \
    lintian \
    fakeroot \
    && rm -rf /var/lib/apt/lists/*