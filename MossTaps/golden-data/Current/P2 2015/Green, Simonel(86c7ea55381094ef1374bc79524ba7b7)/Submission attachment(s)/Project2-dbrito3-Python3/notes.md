# Notes

Scripts for broad categories of problem types

Production rules
Check for equality
Try transforming the entire figure, choose an answer
with over 95% similarity to trasformed full frames.
Try to interpret the objects in the figure and transforming

Make brain that loads JSON data of seed weigts for
transformations and reflection amounts and such
Allow API to change weight for a transformation type
Store answer reasoning details, use brain to learn about
answers and adjust

Separate the problem and answers
Load A, B, C, into image objects
Check between A & B transformation (> 95% alike)
check complex transformations (> 95% alike)
store relationship in transform object/function
do the same between A & C
Add transform functions if they are different
Apply transformation to B & C. Check both against all
answers. If multiple possibilities (> 95% alike), then
keep trying, or try backup transformations that could have
been and use their weights to choose the best one
store last likely answers to go back to find possibilities

