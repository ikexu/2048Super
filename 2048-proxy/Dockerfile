FROM alpine
MAINTAINER Wendell Sun <iwendellsun@gmail.com>
VOLUME ["/conf"]
WORKDIR /
COPY 2048-proxy /
EXPOSE 9001
ENTRYPOINT ["/bin/sh", "-c", "/2048-proxy -f /conf/config.json -k $KAFKA_URL -m $MONGO_URL"]
