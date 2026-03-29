// Exercise 01: Variables, Constants, and Types
//
// Topics: var, :=, const, iota, custom types, type conversion, zero values
//
// Run:     go run ./01-variables/exercise/
// Solution: go run ./01-variables/solution/

package main

import "fmt"

// TODO 1: Declare a constant block with:
//   - MaxGrade    float64 = 10.0
//   - MinGrade    float64 = 0.0
//   - PassingGrade float64 = 5.0
//   - SchoolName  string  = "Go Academy"

const (
	MaxGrade     float64 = 10.0
	MinGrade     float64 = 0.0
	PassingGrade float64 = 5.0
	SchoolName   string  = "Go Academy"
)

// TODO 2: Declare a custom type "Grade" based on float64
type Grade float64

// TODO 3: Declare a custom type "StudentID" based on int
type Student int

// TODO 4: Declare an iota-based constant for Semester
//
//	SEMESTER_1, SEMESTER_2, SEMESTER_3
//	(hint: type Semester int, then use iota starting from 1)
type Semester int

const (
	SEMESTER_1 Semester = iota + 1
	SEMESTER_2
	SEMESTER_3
)

func main() {
	// TODO 5: Declare variables using 'var' keyword
	//   var studentName string  = "Nguyen Van An"
	//   var studentAge  int     = 20
	//   var isActive    bool    = true
	var studentName string = "Tri"
	var studentAge int = 26
	var isActive bool = true

	// TODO 6: Declare variables using short declaration ':='
	//   subject := "Mathematics"
	//   score   := 8.5

	subject := "Math"
	score := 8.3

	// TODO 7: Multiple assignment in one line
	//   x, y, z := 1, 2, 3
	x, y, z := 2, 3, 4
	a, b, c := 5, 6, 7

	// TODO 8: Zero values — declare without assignment, then print
	//   var emptyString string
	//   var emptyInt    int
	//   var emptyBool   bool
	//   var emptyFloat  float64

	var (
		emptyBool   bool
		emptyFloat  float64
		emptyInt    int
		emptyString string
	)

	// TODO 9: Use your custom types
	//   var mathGrade  Grade     = 9.5
	//   var id         StudentID = 1001
	//   var semester   Semester  = SEMESTER_1
	type Grade float64
	type StudentID int
	type Semester string
	var mathGrade Grade = 9.5
	var id StudentID = 1001
	var semester Semester = "SEMESTER_1"

	// TODO 10: Type conversion
	//   scoreInt  := int(score)
	//   ageFloat  := float64(studentAge)
	scoreInt := int(score)
	ageFloat := float64(studentAge)

	// ----- Print everything -----
	fmt.Println("=== Student Info ===")
	// print: studentName, studentAge, isActive, subject, score
	fmt.Println(studentName)
	fmt.Println(studentAge)
	fmt.Println(isActive)
	fmt.Printf("%s score is: %.2f", subject, score)

	fmt.Println("\n=== Multiple Assignment ===")
	// print: x, y, z
	fmt.Printf("x: %d, y: %d, z: %d, a: %d, b: %d, c: %d", x, y, z, a, b, c)

	fmt.Println("\n=== Zero Values ===")
	// print: emptyString, emptyInt, emptyBool, emptyFloat
	fmt.Printf("emptyBool: %v, emptyString: %v, emptyFloat: %v, emptyInt: %v", emptyBool, emptyString, emptyFloat, emptyInt)

	fmt.Println("\n=== Custom Types ===")
	// print: mathGrade, id, semester
	fmt.Printf("mathGrade: %v, id: %v, semester: %v", mathGrade, id, semester)

	fmt.Println("\n=== Type Conversion ===")
	// print: scoreInt, ageFloat
	fmt.Printf("scoreInt: %v, ageFloat: %v", scoreInt, ageFloat)

	fmt.Println("\n=== Constants ===")
	// print: MaxGrade, MinGrade, PassingGrade, SchoolName
	fmt.Printf("MaxGrade: %v", MaxGrade)
	fmt.Printf("MinGrade: %v", MinGrade)
	fmt.Printf("PassingGrade: %v", PassingGrade)
	fmt.Printf("SchoolName: %v", SchoolName)
}
