// Solution 12: Defer, Panic, and Recover

package main

import (
	"fmt"
	"os"
)

func deferOrder() {
	defer fmt.Println("first")
	defer fmt.Println("second")
	defer fmt.Println("third")
	fmt.Println("main body")
}

func deferInLoop(n int) {
	for i := 1; i <= n; i++ {
		i := i // capture
		defer fmt.Println("defer:", i)
	}
}

func safeDiv(a, b int) (result int, err error) {
	defer func() {
		if r := recover(); r != nil {
			err = fmt.Errorf("recovered: %v", r)
			result = 0
		}
	}()
	if b == 0 {
		panic("division by zero")
	}
	return a / b, nil
}

func readFile(filename string) error {
	f, err := os.Open(filename)
	if err != nil {
		return err
	}
	defer f.Close() // always runs
	fmt.Println("file opened:", filename)
	return nil
}

func mustPositive(n int) int {
	if n <= 0 {
		panic(fmt.Sprintf("expected positive, got %d", n))
	}
	return n * 2
}

func safeMustPositive(n int) (result int, err error) {
	defer func() {
		if r := recover(); r != nil {
			err = fmt.Errorf("%v", r)
		}
	}()
	result = mustPositive(n)
	return
}

func main() {
	fmt.Println("=== deferOrder ===")
	deferOrder()

	fmt.Println("\n=== deferInLoop ===")
	deferInLoop(3)

	fmt.Println("\n=== safeDiv ===")
	r, err := safeDiv(10, 2)
	fmt.Println("10/2 =", r, "err:", err)
	r, err = safeDiv(10, 0)
	fmt.Println("10/0 =", r, "err:", err)

	fmt.Println("\n=== readFile ===")
	err = readFile("go.mod")
	fmt.Println("go.mod:", err)
	err = readFile("ghost.txt")
	fmt.Println("ghost.txt:", err)

	fmt.Println("\n=== safeMustPositive ===")
	res, err := safeMustPositive(5)
	fmt.Println("5 →", res, err)
	res, err = safeMustPositive(-3)
	fmt.Println("-3 →", res, err)
}
