# 4x4x4 Three-Phase Reduction Solver

This is a three-phase 4x4x4 solver than solves reduction for an arbitrary 4x4x4 cube. It is based on Charles Tsai's 8-stage solver.
The program requires 80MB of tables that take a few minutes to initialize. The solver itself can take 25-30 seconds., and it usualy produces solutions under 25 moves. This make it possible to generate 45-move random-state 4x4x4 scrambles (where the first 20 moves are only outer turns).

You can try out Shuang's demo [here](http://4x4x4reduction.appspot.com/).