import imageio.v3 as iio


frames = iio.imread("homer.gif", plugin="pyav")
print(frames[0][0])

new_width = 40
new_height = 40

# new_frames = []

# for i in range(len(frames)):
#     new_frame = []
#     height_step = int(len(frames[0]) / new_height)
#     width_step = int(len(frames[0][0]) / new_width)
#     for j in range(0, len(frames[i]), height_step):
#         new_line = []
#         for k in range(0, len(frames[i][j]), width_step):
#             new_line.append(frames[i][j][k])
#         new_frame.append(new_line)

#     new_frames.append(new_frame)

# print(len(frames))
# print(len(new_frames))

for i in range(len(frames)):
    iio.imwrite("frames/" + str(i) + ".png", frames[i])
