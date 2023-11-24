import imageio.v3 as iio


frames = iio.imread("cat.gif", plugin="pyav")

for i in range(len(frames)):
    iio.imwrite("frames/" + str(i) + ".png", frames[i])