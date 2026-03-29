// Solution 05: Structs

package main

import "fmt"

type Address struct {
	Street string
	City   string
	Zip    string
}

type Student struct {
	ID      int
	Name    string
	Age     int
	Score   float64
	Address Address
}

type Classroom struct {
	Name     string
	Students []Student
}

func newStudent(id int, name string, age int) Student {
	return Student{ID: id, Name: name, Age: age}
}

func fullAddress(s Student) string {
	return fmt.Sprintf("%s, %s %s", s.Address.Street, s.Address.City, s.Address.Zip)
}

func classAverage(c Classroom) float64 {
	if len(c.Students) == 0 {
		return 0
	}
	total := 0.0
	for _, s := range c.Students {
		total += s.Score
	}
	return total / float64(len(c.Students))
}

func main() {
	fmt.Println("=== Struct Initialization ===")
	s1 := Student{ID: 1, Name: "An", Age: 20, Score: 8.5}
	s2 := Student{2, "Binh", 21, 7.0, Address{}}
	var s3 Student
	s3.ID = 3
	s3.Name = "Cuong"
	fmt.Println(s1)
	fmt.Println(s2)
	fmt.Println(s3)

	fmt.Println("\n=== Embedded Struct ===")
	s := Student{
		ID: 1, Name: "An", Age: 20, Score: 9.0,
		Address: Address{Street: "123 Le Loi", City: "HCMC", Zip: "70000"},
	}
	fmt.Println("City:", s.Address.City)
	fmt.Println("Full:", fullAddress(s))

	fmt.Println("\n=== newStudent ===")
	ns := newStudent(1, "An", 20)
	fmt.Println(ns)

	fmt.Println("\n=== classAverage ===")
	classroom := Classroom{
		Name: "Class A",
		Students: []Student{
			{Score: 8.0}, {Score: 6.5}, {Score: 9.0}, {Score: 4.5},
		},
	}
	fmt.Printf("Average: %.2f\n", classAverage(classroom))

	fmt.Println("\n=== Struct Comparison ===")
	a := Student{ID: 1, Name: "An"}
	b := Student{ID: 1, Name: "An"}
	c := Student{ID: 2, Name: "Binh"}
	fmt.Println("a == b:", a == b) // true
	fmt.Println("a == c:", a == c) // false
}
