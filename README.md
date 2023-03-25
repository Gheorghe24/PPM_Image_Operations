# Scala Image Manipulation Program

This program is written in Scala and contains a collection of functions that manipulate images represented as matrices of pixels.

## Code Breakdown

### Importing Required Packages

The first two lines import necessary utility functions from two different Scala packages: `util.Pixel` and `util.Util`.

```scala
import util.Pixel
import util.Util
```

### Defining Image and GrayscaleImage Types

The Image and GrayscaleImage types are defined as type aliases for matrices of pixels and matrices of grayscale values, respectively.

```scala
type Image = Array[Array[Pixel]]

type GrayscaleImage = Array[Array[Double]]
```

### Defining the `main` Function

The `main` function is the entry point of the program. It takes a single argument, which is the name of the image file to be processed. The `main` function then calls the `processImage` function, which is defined later in the code.

```scala

def main(args: Array[String]): Unit = {
  val filename = args(0)
  processImage(filename)
}
```

### fromStringPPM Function

fromStringPPM is a function that takes a list of characters representing an image in the PPM format and returns a matrix of pixels. Here are the steps it takes:

1. The input list of characters is converted to a string and split into a list of lines.
2. The second line of the image is parsed to extract the width and height of the image.
3. The remaining lines are grouped into chunks of width to create a matrix of pixel values.
4. A nested list of Pixel objects is constructed from the pixel values.

### toStringPPM Function

toStringPPM takes an Image and returns a list of characters representing the image in the PPM format. Here are the steps it takes:

1. The width and height of the image are extracted from the matrix of pixels.
2. A PPM header string is constructed from the width and height.
3. Each pixel in the image is converted to a string representation of its RGB values.
4. The header and pixel strings are combined into a single string, which is converted to a list of characters.

### Concatenation Functions

verticalConcat takes two matrices of pixels and returns a new matrix with the second image concatenated vertically below the first image.

```scala

def verticalConcat(image1: Image, image2: Image): Image = {
  image1 ++ image2
}

```

horizontal Concat takes two matrices of pixels and returns a new matrix with the second image concatenated horizontally to the right of the first image.

```scala
def horizontalConcat(image1: Image, image2: Image): Image = {
  image1.zip(image2).map{ case (row1, row2) => row1 ++ row2 }
}
```

### Image Processing Functions

### applyConvolution Function

This function takes a grayscale image and a 2D kernel, and applies
the convolution operation on the image using the kernel.

It returns a new grayscale image that contains the result of the convolution operation.

The function first computes the radius of the kernel (which is half of its length),
and uses the getNeighbors function to get the neighbors of each pixel in the image
within a window of size kernel.length.

It then applies the convolution operation on each window using the combineImagesWithOp function and the kernel, and computes the sum of the resulting image.

### ModuloPascal Function

### Helper Functions

`combineImagesWithOp` takes two matrices of pixels and a function that takes two pixels and returns a pixel.

It returns a new matrix of pixels where each pixel is the result of applying the function
to the corresponding pixels in the input matrices.

---

`replaceAtIndex` takes a list and an index and a new value,
and returns a new list with the value at the given index replaced with the new value.

The function uses a tail-recursive helper function helper to iterate over the elements of the original list.

If the current list is empty, the helper function returns the reverse of the current result.

If the current index is equal to the target index, the helper function adds the value to the current result and increments the index.

Otherwise, the helper function adds the head of the current list to the current result and increments the index.

---

`computeValue` The function computeValue is a recursive function that takes three arguments: i and j,
which represent the row and column indices of the Pascal triangle, and computedValues,
which is a Map that caches the computed values of the triangle.

The function first checks if the value (i, j) is already in the computedValues map.
If it is, the function returns the map and the cached value.
Otherwise, it checks if the current value of j is either 0 or equal to i.
If it is, then the value at (i, j) is 1, and the function adds this value to the computedValues map and returns the map and 1.

If j is not 0 or equal to i, then the function recursively calls itself twice
to compute the values of (i-1, j-1) and (i-1, j), respectively.
It then adds these two values together, takes the result modulo m, and adds the result to the computedValues map.

Finally, the function returns the map and the computed result.

---

The moduloPascal function takes three parameters:

- m: an integer value that serves as the modulus of the function.
- funct: a function that maps an integer value to a pixel value.
- size: an integer value representing the size of the image to be generated.

The function first initializes an imageMatrix of size size x size with all black pixels.

It then proceeds to create a Map called computedValues to store the computed values of the Pascal triangle, where the key is a tuple (i, j)
representing the row and column index of the Pascal triangle, and the value is the computed value at that index.

The computeValue function is a recursive helper function that computes the value of a given index in the Pascal triangle.

It first checks if the value has already been computed and stored in the computedValues map.

If it has, the function returns the stored value.

If it hasn't, the function computes the value recursively using the formula for Pascal's triangle: C(i, j) = C(i-1, j-1) + C(i-1, j).

Since the values can get very large, the function also takes the modulo m of the computed value at each step, which helps to keep the values small and avoid integer overflow.

The computed value is then stored in the computedValues map for future use.

The updatedImageMatrix variable is then generated by iterating over each row of the imageMatrix and computing the corresponding values of the Pascal triangle using the computeValue function. The resulting values are then passed to the funct function to generate a pixel value, which is used to replace the corresponding index in the row. The resulting row is then added to updatedImageMatrix.

Finally, the updatedImageMatrix is returned as the generated image.

---

### Memoization Concept

Memoization is a technique used to improve the performance of recursive functions by storing the results of the recursive calls in a cache.

In the case of the moduloPascal function, the computeValue function is a recursive function that computes the value of a given index in the Pascal triangle.

In the given example, the function computeValue uses memoization to store the computed values of the Pascal triangle in a Map data structure.

When the function is called with the same inputs again, it checks if the value is already stored in the map and returns it instead of computing it again. This helps to reduce the number of recursive calls and thus improves the performance of the function.

## Contributing

Pull requests are welcome. For major changes, please open an issue first
to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License

[MIT](https://choosealicense.com/licenses/mit/)
