import cv2 as cv
vcap = cv.VideoCapture("rtsp://user:password@192.168.1.2:554/Streaming/Channels/101")
while(1):
    ret, frame = vcap.read()
    cv.imshow('VIDEO', frame)
    cv.waitKey(1)
