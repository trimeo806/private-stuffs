// Exercise 13: Closures and Higher-Order Functions
//
// Topics: anonymous functions, closures (capturing variables),
//         function as value, function as parameter, function as return value
//
// Run:      go run ./13-closures/exercise/
// Solution: go run ./13-closures/solution/

package main

import "fmt"

// TODO 1: Write function "makeCounter" that returns a function
//   Each call to the returned function increments and returns a counter
//   Each call to makeCounter creates an INDEPENDENT counter
//   func makeCounter() func() int { ... }

// TODO 2: Write function "makeAdder" that takes a base int
//   returns a function that adds base to its argument
//   func makeAdder(base int) func(int) int { ... }

// TODO 3: Write function "makeMultiplier" that takes factor float64
//   returns a function func(float64) float64 that multiplies by factor
//   func makeMultiplier(factor float64) func(float64) float64 { ... }

// TODO 4: Write function "compose" that takes two functions f and g
//   both of type func(float64) float64
//   returns a new function that applies g first, then f
//   i.e. compose(f, g)(x) == f(g(x))
//   func compose(f, g func(float64) float64) func(float64) float64 { ... }

// TODO 5: Write function "memoize" that takes func(int) int
//   returns a new func(int) int that caches results
//   (if called with same arg again, return cached value — don't recompute)
//   func memoize(fn func(int) int) func(int) int { ... }

// TODO 6: Write function "once" that takes func()
//   returns a new func() that calls the original ONLY on the first call
//   subsequent calls do nothing
//   func once(fn func()) func() { ... }

func main() {
	// Test TODO 1
	fmt.Println("=== makeCounter ===")
	// c1 := makeCounter()
	// c2 := makeCounter()
	// fmt.Println(c1(), c1(), c1()) // 1 2 3
	// fmt.Println(c2(), c2())       // 1 2  (independent)

	// Test TODO 2
	fmt.Println("\n=== makeAdder ===")
	// add5 := makeAdder(5)
	// add10 := makeAdder(10)
	// fmt.Println(add5(3))  // 8
	// fmt.Println(add10(3)) // 13

	// Test TODO 3
	fmt.Println("\n=== makeMultiplier ===")
	// double := makeMultiplier(2)
	// triple := makeMultiplier(3)
	// fmt.Println(double(5))  // 10
	// fmt.Println(triple(5))  // 15

	// Test TODO 4
	fmt.Println("\n=== compose ===")
	// double := makeMultiplier(2)
	// addOne := func(x float64) float64 { return x + 1 }
	// doubleAddOne := compose(addOne, double) // first double, then addOne
	// fmt.Println(doubleAddOne(3)) // (3*2)+1 = 7

	// Test TODO 5
	fmt.Println("\n=== memoize ===")
	// callCount := 0
	// slowSquare := func(n int) int {
	//     callCount++
	//     fmt.Println("  computing", n, "^2...")
	//     return n * n
	// }
	// cachedSquare := memoize(slowSquare)
	// fmt.Println(cachedSquare(4))  // computes
	// fmt.Println(cachedSquare(4))  // cached — no "computing" print
	// fmt.Println(cachedSquare(5))  // computes
	// fmt.Println("total computations:", callCount) // should be 2

	// Test TODO 6
	fmt.Println("\n=== once ===")
	// calls := 0
	// greet := once(func() {
	//     calls++
	//     fmt.Println("Hello! (only once)")
	// })
	// greet(); greet(); greet()
	// fmt.Println("calls:", calls) // 1

	// Anonymous function immediately invoked
	fmt.Println("\n=== Immediately Invoked ===")
	result := func(a, b int) int {
		return a + b
	}(3, 4)
	fmt.Println("result:", result) // 7

	// Function stored in variable
	fmt.Println("\n=== Function as Value ===")
	ops := map[string]func(int, int) int{
		"add": func(a, b int) int { return a + b },
		"sub": func(a, b int) int { return a - b },
		"mul": func(a, b int) int { return a * b },
	}
	// TODO: call each op with (10, 3) and print results
	_ = ops
}
