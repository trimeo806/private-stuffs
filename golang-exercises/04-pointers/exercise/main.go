// Exercise 04: Pointers
//
// Topics: & (address-of), * (dereference), pointer to function,
//         nil pointer check, new(), pointer to struct
//
// Run:      go run ./04-pointers/exercise/
// Solution: go run ./04-pointers/solution/

package main

import "fmt"

// TODO 1: Write function "increment" that takes a pointer to int
//
//	and adds 1 to the value at that pointer
func increment(n *int) {
	*n = *n + 1
}

// TODO 2: Write function "swap" that takes two pointers to int
//
//	and swaps their values
func swap(a, b *int) {
	t := *a
	*a = *b
	*b = t
}

// TODO 3: Write function "newScore" that uses new() to allocate
//
//	a float64 pointer, sets it to the given value, returns the pointer
func newScore(v float64) *float64 {
	p := new(float64)
	*p = v
	return p
}

// TODO 4: Write function "safeDouble" that takes a *float64
//   - if pointer is nil, print "nil pointer, skipping" and return
//   - otherwise double the value at the pointer
func safeDouble(p *float64) {
	if p == nil {
		fmt.Println("nil pointer, skipping")
		return
	}

	*p = *p * 2
}

// TODO 5: Struct with pointer
type Student struct {
	Name  string
	Score float64
}

// Write function "updateScore" that takes a *Student and a float64
// and updates the student's Score field
func updateScore(s *Student, score float64) {
	s.Score = score
}

func main() {
	// Test TODO 1
	fmt.Println("=== increment ===")
	x := 10
	fmt.Println("before:", x)
	increment(&x)
	fmt.Println("after:", x) // expected: 11

	// Test TODO 2
	fmt.Println("\n=== swap ===")
	a, b := 5, 99
	fmt.Println("before: a=", a, "b=", b)
	swap(&a, &b)
	fmt.Println("after:  a=", a, "b=", b) // expected: a=99, b=5

	// Test TODO 3
	fmt.Println("\n=== newScore ===")
	p := newScore(8.5)
	fmt.Println("pointer:", p)  // memory address
	fmt.Println("value:  ", *p) // 8.5

	// Test TODO 4
	fmt.Println("\n=== safeDouble ===")
	score := 4.5
	sp := &score
	safeDouble(sp)
	fmt.Println("doubled:", *sp) // 9.0
	safeDouble(nil)              // should print nil warning
	_ = sp

	// Test TODO 5
	fmt.Println("\n=== updateScore ===")
	s := Student{Name: "An", Score: 7.0}
	fmt.Println("before:", s)
	updateScore(&s, 9.5)
	fmt.Println("after: ", s) // Score should be 9.5

	// ---- Pointer basics demo ----
	fmt.Println("\n=== Pointer Basics ===")
	y := 42
	ptr := &y
	fmt.Println("y   =", y)
	fmt.Println("&y  =", ptr)  // memory address
	fmt.Println("*ptr=", *ptr) // 42

	// TODO 6: Change y's value through ptr (without touching y directly)
	*ptr = 100
	fmt.Println("y after *ptr=100:", y) // expected: 100
	_ = x
}
