// Solution 01: Variables, Constants, and Types

package main

import "fmt"

const (
	MaxGrade     float64 = 10.0
	MinGrade     float64 = 0.0
	PassingGrade float64 = 5.0
	SchoolName   string  = "Go Academy"
)

type Grade float64
type StudentID int

type Semester int

const (
	SEMESTER_1 Semester = iota + 1
	SEMESTER_2
	SEMESTER_3
)

func main() {
	// var keyword
	var studentName string = "Nguyen Van An"
	var studentAge int = 20
	var isActive bool = true

	// short declaration
	subject := "Mathematics"
	score := 8.5

	// multiple assignment
	x, y, z := 1, 2, 3

	// zero values
	var emptyString string
	var emptyInt int
	var emptyBool bool
	var emptyFloat float64

	// custom types
	var mathGrade Grade = 9.5
	var id StudentID = 1001
	var semester Semester = SEMESTER_1

	// type conversion
	scoreInt := int(score)
	ageFloat := float64(studentAge)

	// ----- Print everything -----
	fmt.Println("=== Student Info ===")
	fmt.Println("Name:", studentName)
	fmt.Println("Age:", studentAge)
	fmt.Println("Active:", isActive)
	fmt.Println("Subject:", subject)
	fmt.Println("Score:", score)

	fmt.Println("\n=== Multiple Assignment ===")
	fmt.Println("x:", x, "y:", y, "z:", z)

	fmt.Println("\n=== Zero Values ===")
	fmt.Printf("string:'%s'  int:%d  bool:%v  float:%f\n",
		emptyString, emptyInt, emptyBool, emptyFloat)

	fmt.Println("\n=== Custom Types ===")
	fmt.Println("mathGrade:", mathGrade)
	fmt.Println("id:", id)
	fmt.Println("semester:", semester)

	fmt.Println("\n=== Type Conversion ===")
	fmt.Println("scoreInt:", scoreInt)
	fmt.Println("ageFloat:", ageFloat)

	fmt.Println("\n=== Constants ===")
	fmt.Println("MaxGrade:", MaxGrade)
	fmt.Println("MinGrade:", MinGrade)
	fmt.Println("PassingGrade:", PassingGrade)
	fmt.Println("SchoolName:", SchoolName)
}
