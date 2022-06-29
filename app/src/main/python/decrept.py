import numpy as np
import cv2
from PIL import Image
import base64
import io
import pyzbar.pyzbar as pyzbar

def main(image):
    im_bytes = base64.b64decode(image)
    im_arr = np.frombuffer(im_bytes, dtype=np.uint8)  # im_arr is one-dim Numpy array
    img = cv2.imdecode(im_arr, flags=cv2.IMREAD_COLOR)

    detect_obj = pyzbar.decode(img)
    text = ""
    for obj in detect_obj:
        text = text + " " + str(obj.data)

    return text

'''filename= "code2.png"
img = cv2.imread(filename)
dec =cv2.QRCodeDetector()
detect_obj = pyzbar.decode(img)
text = ""
for obj in detect_obj:
    text =text+" "+str(obj.data)

print(text)'''
