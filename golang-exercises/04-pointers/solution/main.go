// Solution 04: Pointers

package main

import "fmt"

func increment(n *int) {
	*n++
}

func swap(a, b *int) {
	*a, *b = *b, *a
}

func newScore(v float64) *float64 {
	p := new(float64)
	*p = v
	return p
}

func safeDouble(p *float64) {
	if p == nil {
		fmt.Println("nil pointer, skipping")
		return
	}
	*p *= 2
}

type Student struct {
	Name  string
	Score float64
}

func updateScore(s *Student, score float64) {
	s.Score = score
}

func main() {
	fmt.Println("=== increment ===")
	x := 10
	fmt.Println("before:", x)
	increment(&x)
	fmt.Println("after:", x) // 11

	fmt.Println("\n=== swap ===")
	a, b := 5, 99
	fmt.Println("before: a=", a, "b=", b)
	swap(&a, &b)
	fmt.Println("after:  a=", a, "b=", b)

	fmt.Println("\n=== newScore ===")
	p := newScore(8.5)
	fmt.Println("pointer:", p)
	fmt.Println("value:  ", *p)

	fmt.Println("\n=== safeDouble ===")
	score := 4.5
	sp := &score
	safeDouble(sp)
	fmt.Println("doubled:", *sp) // 9.0
	safeDouble(nil)              // nil warning

	fmt.Println("\n=== updateScore ===")
	s := Student{Name: "An", Score: 7.0}
	fmt.Println("before:", s)
	updateScore(&s, 9.5)
	fmt.Println("after: ", s)

	fmt.Println("\n=== Pointer Basics ===")
	y := 42
	ptr := &y
	fmt.Println("y   =", y)
	fmt.Println("&y  =", ptr)
	fmt.Println("*ptr=", *ptr)
	*ptr = 100
	fmt.Println("y after *ptr=100:", y) // 100
}
