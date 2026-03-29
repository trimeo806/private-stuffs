// Exercise 07: Interfaces
//
// Topics: interface declaration, implementing interfaces,
//         empty interface (any), type assertion, type switch
//
// Run:      go run ./07-interfaces/exercise/
// Solution: go run ./07-interfaces/solution/

package main

import (
	"fmt"
	"math"
)

// TODO 1: Declare interface "Shape" with methods:
type Shape interface {
	Area() float64
	Perimeter() float64
	Name() string
}

// TODO 2: Declare struct "Circle" with field Radius float64
//
//	Implement Shape interface for Circle:
//	- Area()      = π * r²   (use math.Pi)
//	- Perimeter() = 2 * π * r
//	- Name()      = "Circle"
type Circle struct {
	Radius float64
}

func (c Circle) Area() float64 {
	return math.Pi * c.Radius * c.Radius
}

func (c Circle) Perimeter() float64 {
	return 2 * math.Pi * c.Radius
}

func (c Circle) Name() string {
	return "Circle"
}

// TODO 3: Declare struct "Triangle" with fields A, B, C float64 (sides)
//
//	Implement Shape interface for Triangle:
//	- Area()      use Heron's formula:
//	              s = (A+B+C)/2
//	              area = sqrt(s*(s-A)*(s-B)*(s-C))
//	- Perimeter() = A + B + C
//	- Name()      = "Triangle"
type Triangle struct {
	A, B, C float64
}

func (t Triangle) Area() float64 {
	s := (t.A + t.B + t.C) / 2
	return math.Sqrt(s * (s - t.A) * (s - t.B) * (s - t.C))
}

func (t Triangle) Perimeter() float64 {
	return t.A + t.B + t.C
}

func (t Triangle) Name() string {
	return "Triangle"
}

// TODO 4: Write function "printShape" that takes a Shape interface
//
//	and prints: "Name: Circle | Area: X.XX | Perimeter: X.XX"
func printShape(s Shape) {
	fmt.Printf("Name: %v | Area: %v | Perimeter: %v", s.Name(), s.Area(), s.Perimeter())
}

// TODO 5: Write function "largestArea" that takes []Shape
//
//	and returns the Shape with the largest area
func largestArea(shapes []Shape) Shape {
	currentArea, maxIdx := shapes[0].Area(), 0

	for i := range len(shapes) {
		if shapes[i].Area() > currentArea {
			currentArea = shapes[i].Area()
			maxIdx = i
		}
	}

	return shapes[maxIdx]
}

// -------------------------------------------------------
// Empty interface & type assertions
// -------------------------------------------------------

// TODO 6: Write function "describe" that takes any (empty interface)
//
//	and prints what type it is + its value using a type switch:
//	- int    → "int: X"
//	- float64 → "float64: X"
//	- string  → "string: X"
//	- bool    → "bool: X"
//	- Shape   → "Shape: Name=X Area=X.XX"
//	- default → "unknown type"
func describe(v any) {
	fmt.Printf("%T: %v\n", v, v)

	// Check dynamic type
	// switch val := v.(type) {
	// case int:
	// 	fmt.Println("int:", val)
	// case float64:
	// 	fmt.Println("float64:", val)
	// case string:
	// 	fmt.Println("string:", val)
	// case bool:
	// 	fmt.Println("bool:", val)
	// case Shape:
	// 	fmt.Printf("Shape: Name=%s Area=%.2f\n", val.Name(), val.Area())
	// default:
	// 	fmt.Println("unknown type")
	// }
}

func main() {
	_ = math.Pi // prevent import error

	// Test TODO 2 & 3
	fmt.Println("=== Shapes ===")
	c := Circle{Radius: 5}
	t := Triangle{A: 3, B: 4, C: 5}
	printShape(c)
	printShape(t)

	// Test TODO 5
	fmt.Println("\n=== Largest Area ===")
	shapes := []Shape{
		Circle{Radius: 3},
		Triangle{A: 5, B: 12, C: 13},
		Circle{Radius: 7},
	}
	largest := largestArea(shapes)
	fmt.Printf("Largest: %s (%.2f)\n", largest.Name(), largest.Area())

	// Test TODO 6
	fmt.Println("\n=== describe ===")
	describe(42)
	describe(3.14)
	describe("hello")
	describe(true)
	describe(Circle{Radius: 2})

	// Type assertion demo
	fmt.Println("\n=== Type Assertion ===")
	var s Shape = Circle{Radius: 4}
	c, ok := s.(Circle) // safe assertion
	fmt.Println("ok:", ok, "radius:", c.Radius)
	_, ok = s.(Triangle)
	fmt.Println("is Triangle:", ok)

	fmt.Println("implement the TODOs above!")
}
