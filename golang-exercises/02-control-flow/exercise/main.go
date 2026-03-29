// Exercise 02: Control Flow
//
// Topics: if/else, for (3 forms), switch, break, continue, goto
//
// Run:      go run ./02-control-flow/exercise/
// Solution: go run ./02-control-flow/solution/

package main

import "fmt"

func main() {
	// -------------------------------------------------------
	// PART A: if / else if / else
	// -------------------------------------------------------

	score := 7.5

	// TODO A1: Print the grade letter based on score:
	//   >= 9.0  → "Excellent"
	//   >= 7.0  → "Good"
	//   >= 5.0  → "Pass"
	//   < 5.0   → "Fail"
	switch true {
	case score >= 9.0:
		fmt.Println("Excelent")
	case score >= 7.0:
		fmt.Println("Good")
	case score >= 5.0:
		fmt.Println("Pass")
	default:
		fmt.Println("Falil")
	}

	// TODO A2: if with short statement
	//   if grade := score * 10; grade >= 90 { ... }
	//   (declare grade inside the if, print whether it's >= 90)
	if grade := score * 10; grade >= 90 {
		fmt.Println("Excellent")
	}

	// -------------------------------------------------------
	// PART B: for loops (3 forms)
	// -------------------------------------------------------

	// TODO B1: C-style for — print 1 to 5
	//   for i := 1; i <= 5; i++ { ... }
	for i := 1; i <= 5; i++ {
		fmt.Println(i)
	}

	// TODO B2: while-style for — sum numbers until sum > 20
	//   start with sum := 0, n := 1
	//   keep adding n to sum while sum <= 20
	//   print sum and n at the end
	sum := 0
	n := 1
	for sum < 20 {
		sum = sum + n
		n++
	}
	fmt.Println(sum)

	sumFor := 0
	for i := 0; sumFor <= 20; i++ {
		sumFor += i
	}
	fmt.Println(sumFor)

	// TODO B3: infinite loop with break
	//   count from 0, break when count == 3
	//   print each count
	count := 0
	for {
		count++
		if count == 3 {
			break
		}
	}

	// TODO B4: for with continue
	//   loop 1 to 10, skip even numbers, print odd numbers only
	for i := range 10 {
		if i%2 == 0 {
			continue
		}

		fmt.Println(i)
	}

	// TODO B5: range over a slice
	grades := []float64{8.5, 4.0, 7.0, 9.5, 3.5}
	//   range over grades, print index and value
	//   also count how many are passing (>= 5.0)
	_ = grades
	countLargerFive := 0
	for i := range len(grades) {
		fmt.Println(i)
		fmt.Println(grades[i])

		if grades[i] > 5.0 {
			countLargerFive += 1
		}
	}
	fmt.Println(countLargerFive)

	// -------------------------------------------------------
	// PART C: switch
	// -------------------------------------------------------

	day := "Monday"

	// TODO C1: switch on day — print whether it's a weekday or weekend
	switch day {
	case "Saturday", "Sunday":
		fmt.Println("Weekend")
	default:
		fmt.Println("Weekday")
	}

	// TODO C2: switch with no condition (acts like if/else chain)
	//   switch { case score >= 9: ... case score >= 7: ... }
	_ = day
	switch {
	case score >= 9:
		fmt.Println("Excellent")
	default:
		fmt.Println("Bad")
	}

	// TODO C3: switch with multiple values in a case
	//   month := 2
	//   switch month { case 1,3,5,7,8,10,12: "31 days"  case 4,6,9,11: "30 days"  case 2: "28/29 days" }
	month := 2
	_ = month
	switch month {
	case 1, 2, 3, 4:
		fmt.Println("Spring")
	default:
		fmt.Println("Rain")
	}
}
