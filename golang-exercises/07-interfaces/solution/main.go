// Solution 07: Interfaces

package main

import (
	"fmt"
	"math"
)

type Shape interface {
	Area() float64
	Perimeter() float64
	Name() string
}

// Circle
type Circle struct {
	Radius float64
}

func (c Circle) Area() float64      { return math.Pi * c.Radius * c.Radius }
func (c Circle) Perimeter() float64 { return 2 * math.Pi * c.Radius }
func (c Circle) Name() string       { return "Circle" }

// Triangle
type Triangle struct {
	A, B, C float64
}

func (t Triangle) Area() float64 {
	s := (t.A + t.B + t.C) / 2
	return math.Sqrt(s * (s - t.A) * (s - t.B) * (s - t.C))
}
func (t Triangle) Perimeter() float64 { return t.A + t.B + t.C }
func (t Triangle) Name() string       { return "Triangle" }

func printShape(s Shape) {
	fmt.Printf("Name: %s | Area: %.2f | Perimeter: %.2f\n",
		s.Name(), s.Area(), s.Perimeter())
}

func largestArea(shapes []Shape) Shape {
	if len(shapes) == 0 {
		return nil
	}
	largest := shapes[0]
	for _, s := range shapes[1:] {
		if s.Area() > largest.Area() {
			largest = s
		}
	}
	return largest
}

func describe(v any) {
	switch val := v.(type) {
	case int:
		fmt.Println("int:", val)
	case float64:
		fmt.Println("float64:", val)
	case string:
		fmt.Println("string:", val)
	case bool:
		fmt.Println("bool:", val)
	case Shape:
		fmt.Printf("Shape: Name=%s Area=%.2f\n", val.Name(), val.Area())
	default:
		fmt.Println("unknown type")
	}
}

func main() {
	fmt.Println("=== Shapes ===")
	c := Circle{Radius: 5}
	t := Triangle{A: 3, B: 4, C: 5}
	printShape(c)
	printShape(t)

	fmt.Println("\n=== Largest Area ===")
	shapes := []Shape{
		Circle{Radius: 3},
		Triangle{A: 5, B: 12, C: 13},
		Circle{Radius: 7},
	}
	largest := largestArea(shapes)
	fmt.Printf("Largest: %s (%.2f)\n", largest.Name(), largest.Area())

	fmt.Println("\n=== describe ===")
	describe(42)
	describe(3.14)
	describe("hello")
	describe(true)
	describe(Circle{Radius: 2})

	fmt.Println("\n=== Type Assertion ===")
	var s Shape = Circle{Radius: 4}
	ci, ok := s.(Circle)
	fmt.Println("ok:", ok, "radius:", ci.Radius)
	_, ok = s.(Triangle)
	fmt.Println("is Triangle:", ok)
}
