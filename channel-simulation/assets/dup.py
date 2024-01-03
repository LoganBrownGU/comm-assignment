import imageio.v3 as iio


frame = iio.imread("frames/0.png")
for i in range(50):
    iio.imwrite("frames/" + str(i) + ".png", frame)
