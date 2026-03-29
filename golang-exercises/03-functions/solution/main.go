// Solution 03: Functions

package main

import (
	"errors"
	"fmt"
)

// 1: Basic function
func add(a, b int) int {
	return a + b
}

// 2: Multiple return values
func divide(a, b float64) (float64, error) {
	if b == 0 {
		return 0, errors.New("division by zero")
	}
	return a / b, nil
}

// 3: Named return values
func minMax(nums []float64) (min, max float64) {
	if len(nums) == 0 {
		return 0, 0
	}
	min, max = nums[0], nums[0]
	for _, n := range nums[1:] {
		if n < min {
			min = n
		}
		if n > max {
			max = n
		}
	}
	return // bare return
}

// 4: Variadic
func sum(nums ...float64) float64 {
	total := 0.0
	for _, n := range nums {
		total += n
	}
	return total
}

// 5: Function as parameter
func apply(nums []float64, fn func(float64) float64) []float64 {
	result := make([]float64, len(nums))
	for i, n := range nums {
		result[i] = fn(n)
	}
	return result
}

// 6: Recursion
func factorial(n int) int {
	if n <= 1 {
		return 1
	}
	return n * factorial(n-1)
}

func main() {
	fmt.Println("=== add ===")
	fmt.Println("3 + 5 =", add(3, 5))

	fmt.Println("\n=== divide ===")
	result, err := divide(10, 3)
	if err != nil {
		fmt.Println("Error:", err)
	} else {
		fmt.Printf("10 / 3 = %.4f\n", result)
	}
	_, err = divide(5, 0)
	if err != nil {
		fmt.Println("Error:", err)
	}

	fmt.Println("\n=== minMax ===")
	nums := []float64{3.5, 1.2, 8.9, 4.4, 7.1}
	min, max := minMax(nums)
	fmt.Printf("min=%.1f  max=%.1f\n", min, max)

	fmt.Println("\n=== sum (variadic) ===")
	fmt.Println("sum():", sum())
	fmt.Println("sum(1,2,3):", sum(1, 2, 3))
	fmt.Println("sum(nums...):", sum(nums...))

	fmt.Println("\n=== apply ===")
	doubled := apply([]float64{1, 2, 3, 4}, func(x float64) float64 { return x * 2 })
	fmt.Println("doubled:", doubled)

	fmt.Println("\n=== factorial ===")
	for _, n := range []int{0, 1, 5, 10} {
		fmt.Printf("%d! = %d\n", n, factorial(n))
	}
}
