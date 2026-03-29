// Solution 13: Closures and Higher-Order Functions

package main

import "fmt"

func makeCounter() func() int {
	count := 0
	return func() int {
		count++
		return count
	}
}

func makeAdder(base int) func(int) int {
	return func(n int) int {
		return base + n
	}
}

func makeMultiplier(factor float64) func(float64) float64 {
	return func(x float64) float64 {
		return x * factor
	}
}

func compose(f, g func(float64) float64) func(float64) float64 {
	return func(x float64) float64 {
		return f(g(x))
	}
}

func memoize(fn func(int) int) func(int) int {
	cache := make(map[int]int)
	return func(n int) int {
		if v, ok := cache[n]; ok {
			return v
		}
		result := fn(n)
		cache[n] = result
		return result
	}
}

func once(fn func()) func() {
	called := false
	return func() {
		if !called {
			called = true
			fn()
		}
	}
}

func main() {
	fmt.Println("=== makeCounter ===")
	c1 := makeCounter()
	c2 := makeCounter()
	fmt.Println(c1(), c1(), c1()) // 1 2 3
	fmt.Println(c2(), c2())       // 1 2

	fmt.Println("\n=== makeAdder ===")
	add5 := makeAdder(5)
	add10 := makeAdder(10)
	fmt.Println(add5(3))  // 8
	fmt.Println(add10(3)) // 13

	fmt.Println("\n=== makeMultiplier ===")
	double := makeMultiplier(2)
	triple := makeMultiplier(3)
	fmt.Println(double(5)) // 10
	fmt.Println(triple(5)) // 15

	fmt.Println("\n=== compose ===")
	addOne := func(x float64) float64 { return x + 1 }
	doubleAddOne := compose(addOne, makeMultiplier(2))
	fmt.Println(doubleAddOne(3)) // 7

	fmt.Println("\n=== memoize ===")
	callCount := 0
	slowSquare := func(n int) int {
		callCount++
		fmt.Println("  computing", n, "^2...")
		return n * n
	}
	cachedSquare := memoize(slowSquare)
	fmt.Println(cachedSquare(4))
	fmt.Println(cachedSquare(4)) // cached
	fmt.Println(cachedSquare(5))
	fmt.Println("total computations:", callCount) // 2

	fmt.Println("\n=== once ===")
	calls := 0
	greet := once(func() {
		calls++
		fmt.Println("Hello! (only once)")
	})
	greet()
	greet()
	greet()
	fmt.Println("calls:", calls) // 1

	fmt.Println("\n=== Immediately Invoked ===")
	result := func(a, b int) int { return a + b }(3, 4)
	fmt.Println("result:", result)

	fmt.Println("\n=== Function as Value ===")
	ops := map[string]func(int, int) int{
		"add": func(a, b int) int { return a + b },
		"sub": func(a, b int) int { return a - b },
		"mul": func(a, b int) int { return a * b },
	}
	for name, op := range ops {
		fmt.Printf("%s(10,3) = %d\n", name, op(10, 3))
	}
}
