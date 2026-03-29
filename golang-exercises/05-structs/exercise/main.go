// Exercise 05: Structs
//
// Topics: struct declaration, initialization, anonymous fields,
//         embedded struct, struct comparison, struct tags (bonus)
//
// Run:      go run ./05-structs/exercise/
// Solution: go run ./05-structs/solution/

package main

import (
	"fmt"
)

// TODO 1: Declare struct "Address" with fields:
//
//	Street string
//	City   string
//	Zip    string
type Address struct {
	Street string
	City   string
	Zip    string
}

// TODO 2: Declare struct "Student" with fields:
//
//	ID      int
//	Name    string
//	Age     int
//	Score   float64
//	Address Address   ← embedded struct (not pointer)
type Student struct {
	ID      int
	Name    string
	Age     int
	Score   float64
	Address Address
}

// TODO 3: Declare struct "Classroom" with fields:
//
//	Name     string
//	Students []Student
type Classroom struct {
	Name     string
	Students []Student
}

// TODO 4: Write function "newStudent" that takes (id int, name string, age int)
//
//	and returns a Student with Score=0 and empty Address
func newStudent(id int, name string, age int) Student {
	student := Student{
		ID: id, Name: name, Age: age, Address: Address{},
	}
	return student
}

// TODO 5: Write function "fullAddress" that takes a Student
//
//	and returns a formatted string: "Street, City Zip"
func fullAddress(s Student) string {
	return s.Address.Street + s.Address.City + s.Address.Zip
}

// TODO 6: Write function "classAverage" that takes a Classroom
//
//	and returns the average score of all students
//	return 0 if classroom has no students
func classAverage(c Classroom) float64 {
	if len(c.Students) == 0 {
		return 0
	}
	sum := 0.0
	for i := range len(c.Students) {
		sum += c.Students[i].Score
	}
	return sum / float64(len(c.Students))
}

func main() {
	// Test TODO 2: Three ways to initialize a struct
	fmt.Println("=== Struct Initialization ===")

	// Way 1: field names (recommended)
	s1 := Student{ID: 1, Name: "An", Age: 20, Score: 8.5}

	// Way 2: positional (fragile, not recommended)
	s2 := Student{2, "Binh", 21, 7.0, Address{}}

	// Way 3: zero value then assign
	var s3 Student
	s3.ID = 3
	s3.Name = "Cuong"

	fmt.Println(s1, s2, s3)

	// Test TODO 1 & 2: Embedded struct
	fmt.Println("\n=== Embedded Struct ===")
	s := Student{
		ID: 1, Name: "An", Age: 20, Score: 9.0,
		Address: Address{Street: "123 Le Loi", City: "HCMC", Zip: "70000"},
	}
	fmt.Println("City:", s.Address.City) // access nested field
	fmt.Println("Full:", fullAddress(s))

	// Test TODO 4
	fmt.Println("\n=== newStudent ===")
	s = newStudent(1, "An", 20)
	fmt.Println(s)

	// Test TODO 6
	fmt.Println("\n=== classAverage ===")
	classroom := Classroom{
		Name: "Class A",
		Students: []Student{
			{Score: 8.0}, {Score: 6.5}, {Score: 9.0}, {Score: 4.5},
		},
	}
	fmt.Printf("Average: %.2f\n", classAverage(classroom))

	// Struct comparison
	fmt.Println("\n=== Struct Comparison ===")
	// Two structs are equal if all fields are equal
	a := Student{ID: 1, Name: "An"}
	b := Student{ID: 1, Name: "An"}
	c := Student{ID: 2, Name: "Binh"}
	fmt.Println("a == b:", a == b) // true
	fmt.Println("a == c:", a == c) // false

	fmt.Println("implement the TODOs above!")
}
