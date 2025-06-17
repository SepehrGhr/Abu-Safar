FROM golang:1.24-alpine

RUN apk add --no-cache git


ENV GOPROXY="https://goproxy.io,direct"

WORKDIR /app

COPY . .

RUN go get github.com/lib/pq

ENV DB_URL=""
ENV DB_USERNAME=""
ENV DB_PASSWORD=""

CMD ["go", "run", "main.go"]