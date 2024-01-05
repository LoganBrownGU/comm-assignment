import matplotlib.pyplot as plt
import numpy as np
import sys

data = []
axis_labels = []
legends = []

for i in range(1, len(sys.argv)):
    with open(sys.argv[i]) as f:
        legends.append(f.readline().strip())
        axis_labels = f.readline().split(",")
        axis_labels[0].strip()
        axis_labels[1].strip()
        data_set = [[], []]
        data.append(data_set)
        
        contents = f.readlines()
        for line in contents:
            data_set[0].append(float(line.split(",")[0]))
            data_set[1].append(float(line.split(",")[1]))

plt.figure(figsize=(16, 5.5), dpi=150)
for data_set in data:
    plt.plot(data_set[0], data_set[1])

label_size = 18
plt.xlabel(axis_labels[0], fontsize=label_size)
plt.ylabel(axis_labels[1], fontsize=label_size)
# plt.yticks(np.arange(0, 0.325, 0.025))
# plt.xticks(np.arange(5, 21, 1))
plt.grid(True)
plt.legend(legends)
#plt.show()
plt.savefig("figures/modulation.png")