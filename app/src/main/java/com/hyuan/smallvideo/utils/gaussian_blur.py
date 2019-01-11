# -*- coding: utf-8 -*-
import math
radius = 3

row_len = radius * 2 + 1
matrix = []
sigma = 1.5


n = 0
while n < row_len:
    row = []
    m = 0
    while m < row_len:
        row.append(0.0)
        m += 1
    matrix.append(row)
    n+=1

def printMatrix():
    n = 0
    while n < len(matrix):
        print(matrix[n])
        n+=1
    print("\n")

def calculateWeight():
    centerX = radius
    centerY = radius
    weightTotal = 0.0
    for x in range(0, row_len, 1):
        for y in range(0, row_len, 1):
            xd = float(x - centerX)
            yd = float(y - centerY)
            distance = (-1.0 * (xd*xd + yd*yd)) / (2.0 * sigma * sigma)
            weight = math.exp(distance)/(2.0 * math.pi * sigma * sigma)
            matrix[x][y] = weight
            weightTotal += weight
    printMatrix()
    for x in range(0, row_len, 1):
        for y in range(0, row_len, 1):
            matrix[x][y] = matrix[x][y]/weightTotal
    printMatrix()

def getMatrix(pos):
    x = pos/row_len
    y = pos%row_len
    return matrix[x][y]

def generateCode():
    model = "sampleColor = texture(inputImageTexture, blurCoordinates[%d]);\n"+ \
    "distanceFromCenterColor = min(distance(centerColor, sampleColor) * distanceNormaliztionFactor, 1.0);\n" + \
    "gaussianWeight = %.4f * (1.0 - distanceFromCenterColor);\n" + \
    "gaussianWeightTotal += gaussianWeight;\n" + \
    "sum += sampleColor * gaussianWeight;\n"
    n = 0
    while n < row_len * row_len:
        weight = getMatrix(n)
        code = model%(n, weight)
        n+=1
        print code

calculateWeight()
generateCode()