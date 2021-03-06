# A wrapper for the Go tutorial.
#
# To run the tutorial locally, build the container:
#
#     $ docker build --tag alexwlchan/gotutorial --file gotutorial.Dockerfile .
#
# And then run the container, making sure to publish port 3999:
#
#     $ docker run --publish 3999:3999 alexwlchan/gotutorial
#
# You can then view the tutorial at http://0.0.0.0:3999/ in your browser.
#
# Note: Safari redirects to localhost:3999, which seems to break the
# tutorial's ability to execute code.  I run it in Chrome instead.

FROM golang:alpine

RUN apk add --update git

RUN go get golang.org/x/tour

EXPOSE 3999

ENTRYPOINT ["bin/tour", "-http=0.0.0.0:3999"]
