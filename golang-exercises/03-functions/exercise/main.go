// Exercise 03: Functions
//
// Topics: basic functions, multiple return values, named returns,
//         variadic functions, functions as values, recursion
//
// Run:      go run ./03-functions/exercise/
// Solution: go run ./03-functions/solution/

package main

import (
	"errors"
	"fmt"
)

// TODO 1: Write function "add" that takes two ints and returns their sum
// func add(a, b int) int { ... }
func add(a, b int) int {
	sum := int(a) + int(b)
	return sum
}

// TODO 2: Write function "divide" that returns (float64, error)
//   - return an error if b == 0: errors.New("division by zero")
//   - otherwise return a/b, nil
//
// func divide(a, b float64) (float64, error) { ... }
func divide(a, b float64) (float64, error) {
	if b == 0 {
		return 0, errors.New("division by zero")
	}

	return a / b, nil
}

// TODO 3: Named return values
//
//	Write function "minMax" that returns the min and max of a slice
//	Use named returns: func minMax(nums []float64) (min, max float64)
//	Hint: use a bare return at the end
//
// func minMax(nums []float64) (min, max float64) { ... }
func minMax(nums []float64) (min, max float64) {
	min, max = nums[0], nums[0]
	for i := range len(nums) {
		if nums[i] < min {
			min = nums[i]
		}

		if nums[i] > max {
			max = nums[i]
		}
	}
	return min, max
}

// TODO 4: Variadic function
//
//	Write function "sum" that takes any number of float64 and returns their sum
func sum(nums ...float64) float64 {
	result := float64(0)
	for i := range len(nums) {
		result += nums[i]
	}

	return float64(result)

}

// TODO 5: Function as a value / parameter
//
//	Write function "apply" that takes a slice of float64 and a function,
//	applies the function to each element, returns a new slice
func apply(nums []float64, fn func(float64) float64) []float64 {
	for i := range len(nums) {
		nums[i] = fn(float64(nums[i]))
	}

	return nums
}

// TODO 6: Recursion
//
//	Write function "factorial" that returns n! recursively
func factorial(n int) int {
	if n == 0 {
		return 1
	}

	return factorial(n-1) * n
}

func main() {
	// Test TODO 1
	fmt.Println("=== add ===")
	result := add(3, 5)
	fmt.Println("3 + 5 =", result)

	// Test TODO 2
	fmt.Println("\n=== divide ===")
	divideResult, err := divide(10, 3)
	if err != nil {
		fmt.Println("Error:", err)
	} else {
		fmt.Printf("10 / 3 = %.4f\n", divideResult)
	}
	_, err = divide(5, 0)
	if err != nil {
		fmt.Println("Error:", err)
	}

	// Test TODO 3
	fmt.Println("\n=== minMax ===")
	nums := []float64{3.5, 1.2, 8.9, 4.4, 7.1}
	min, max := minMax(nums)
	fmt.Printf("min=%.1f  max=%.1f\n", min, max)

	// Test TODO 4
	fmt.Println("\n=== sum (variadic) ===")
	fmt.Println("sum():", sum())
	fmt.Println("sum(1,2,3):", sum(1, 2, 3))
	// spread a slice:  fmt.Println("sum(nums...):", sum(nums...))

	// Test TODO 5
	fmt.Println("\n=== apply ===")
	doubled := apply([]float64{1, 2, 3, 4}, func(x float64) float64 { return x * 2 })
	fmt.Println("doubled:", doubled)

	// Test TODO 6
	fmt.Println("\n=== factorial ===")
	for _, n := range []int{0, 1, 5, 10} {
		fmt.Printf("%d! = %d\n", n, factorial(n))
	}

	_ = errors.New // prevent import error while TODOs are incomplete
}
