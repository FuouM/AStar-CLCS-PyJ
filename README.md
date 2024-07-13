# AStar-CLCS-PyJ
Python & Java implementation of A* for Constrained Longest Common Subsequence (CLCS)

## Overview
This project implements the A* algorithm for solving the Constrained Longest Common Subsequence problem, based on the C++ implementation described in the paper:

***"An A\* search algorithm for the constrained longest common subsequence problem"***
by Marko Djukanovic, Christoph Berger, Günther R. Raidl, Christian Blum

[DOI: 10.1016/j.ipl.2020.106041](https://doi.org/10.1016/j.ipl.2020.106041)

The repository includes:
- A* Algorithm implementation
- Deorowicz's Dynamic Programming solution (as a reference for result validation)

![Performance Comparison](p_0_5_seconds_avg.png)

## Key Observations
- Results may differ between A* and DP solutions, as multiple strings can satisfy the CLCS problem. However, the CLCS length should always be the same.
- A* outperforms DP for very long input strings.
- For shorter strings, DP solutions may be preferred due to lower initialization overhead.

## Compilation and Execution

### Compile
```
javac -d bin -cp src_java src_java\*.java
```

### Run
```
java -cp bin RunSimple
```

### Compile and Run (from src_java directory)
```
javac *.java & java RunSimple
```

## Additional Resources

### Original C++ Source Code
Available at [https://www.ac.tuwien.ac.at/files/resources/software/clcs.zip](https://www.ac.tuwien.ac.at/files/resources/software/clcs.zip)

### Test Instances
Available at [https://www.ac.tuwien.ac.at/files/resources/instances/clcs/](https://www.ac.tuwien.ac.at/files/resources/instances/clcs/)

