#!/usr/bin/env python3

import glob
import matplotlib.pyplot as plt


# def lineplot(x_data, y_data, x_label="", y_label="", title=""):
#     # Create the plot object
#     _, ax = plt.subplots()
#
#     # Plot the best fit line, set the linewidth (lw), color and
#     # transparency (alpha) of the line
#     ax.plot(x_data, y_data, lw=2, color='#539caf', alpha=1)
#
#     # Label the axes and provide a title
#     ax.set_title(title)
#     ax.set_xlabel(x_label)
#     ax.set_ylabel(y_label)


runs = {}

for filename in glob.glob('*.log'):
    file = open(filename, 'r')
    whole = file.read()
    name = filename.replace('\uf03a', ':')
    runs[name] = {}
    islands = whole.split('\n\n\n')
    for island in islands:
        lines = island.split('\n')
        runs[name][lines[0]] = lines[1:]

runs_to_be_removed = []

for run in runs.keys():
    if '2018' not in run:
        runs_to_be_removed.append(run)
    else:
        islands_to_be_removed = []
        for island in runs[run].keys():
            if 'map' not in island:
                islands_to_be_removed.append(island)
        for island in islands_to_be_removed:
            del runs[run][island]

for run in runs_to_be_removed:
    del runs[run]

print(runs)

# sum_of_points_by_run = []
#
# for key in runs.keys():
#     sum_of_points = 0
#     for island in runs[key].keys():
#         sum_of_points += 1
#     sum_of_points_by_run.append(sum_of_points)
#
# plt.plot(sum_of_points_by_run)
# plt.ylabel('some numbers')
# plt.show()
