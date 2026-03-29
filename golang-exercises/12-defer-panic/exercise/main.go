// Exercise 12: Defer, Panic, and Recover
//
// Topics: defer execution order, defer in loops, panic,
//         recover(), using defer for cleanup
//
// Run:      go run ./12-defer-panic/exercise/
// Solution: go run ./12-defer-panic/solution/

package main

import (
	"fmt"
	"os"
)

// TODO 1: Write function "deferOrder" — no parameters
//   Use 3 defer statements printing "first", "second", "third"
//   Then print "main body"
//   Observe: defers run in LIFO order (last-in, first-out)
// func deferOrder() { ... }

// TODO 2: Write function "deferInLoop" that takes n int
//   In a loop from 1 to n, defer a print of each i
//   Observe the order they print
// func deferInLoop(n int) { ... }

// TODO 3: Write function "safeDiv" that takes a, b int
//   returns (result int, err error)
//   Use defer + recover() to catch a divide-by-zero panic
//   If b == 0, manually panic("division by zero")
//   The recover should set err and return 0, err
// func safeDiv(a, b int) (result int, err error) { ... }

// TODO 4: Write function "readFile" that takes a filename string
//   - Open the file with os.Open
//   - Use defer to close it (always runs even on error path)
//   - If open fails, return the error
//   - Otherwise print "file opened: filename" and return nil
// func readFile(filename string) error { ... }

// TODO 5: Write function "mustPositive" that takes n int
//   - if n <= 0: panic(fmt.Sprintf("expected positive, got %d", n))
//   - otherwise return n * 2
// func mustPositive(n int) int { ... }

// TODO 6: Write function "safeMustPositive" that takes n int
//   Calls mustPositive inside, uses recover() to handle the panic
//   Returns (int, error) — (result, nil) on success or (0, error) on panic
// func safeMustPositive(n int) (result int, err error) { ... }

func main() {
	_ = os.Open // prevent import error

	// Test TODO 1
	fmt.Println("=== deferOrder ===")
	// deferOrder()
	// expected output:
	//   main body
	//   third
	//   second
	//   first

	// Test TODO 2
	fmt.Println("\n=== deferInLoop ===")
	// deferInLoop(3)
	// expected: 3, 2, 1

	// Test TODO 3
	fmt.Println("\n=== safeDiv ===")
	// r, err := safeDiv(10, 2)
	// fmt.Println("10/2 =", r, "err:", err)
	// r, err = safeDiv(10, 0)
	// fmt.Println("10/0 =", r, "err:", err)

	// Test TODO 4
	fmt.Println("\n=== readFile ===")
	// err := readFile("go.mod")    // exists
	// fmt.Println("go.mod:", err)
	// err = readFile("ghost.txt")  // doesn't exist
	// fmt.Println("ghost.txt:", err)

	// Test TODO 5 & 6
	fmt.Println("\n=== safeMustPositive ===")
	// r, err := safeMustPositive(5)
	// fmt.Println("5 →", r, err)
	// r, err = safeMustPositive(-3)
	// fmt.Println("-3 →", r, err)

	fmt.Println("implement the TODOs above!")
}
