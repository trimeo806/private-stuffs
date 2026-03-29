// Solution 02: Control Flow

package main

import "fmt"

func main() {
	// -------------------------------------------------------
	// PART A: if / else if / else
	// -------------------------------------------------------
	score := 7.5

	// A1: grade letter
	if score >= 9.0 {
		fmt.Println("Excellent")
	} else if score >= 7.0 {
		fmt.Println("Good")
	} else if score >= 5.0 {
		fmt.Println("Pass")
	} else {
		fmt.Println("Fail")
	}

	// A2: if with short statement
	if grade := score * 10; grade >= 90 {
		fmt.Println("grade >= 90:", grade)
	} else {
		fmt.Println("grade < 90:", grade)
	}

	// -------------------------------------------------------
	// PART B: for loops
	// -------------------------------------------------------

	// B1: C-style
	fmt.Print("1 to 5: ")
	for i := 1; i <= 5; i++ {
		fmt.Print(i, " ")
	}
	fmt.Println()

	// B2: while-style
	sum, n := 0, 1
	for sum <= 20 {
		sum += n
		n++
	}
	fmt.Println("sum:", sum, "n:", n)

	// B3: infinite loop with break
	count := 0
	for {
		fmt.Println("count:", count)
		count++
		if count == 3 {
			break
		}
	}

	// B4: continue — odd numbers only
	fmt.Print("Odd: ")
	for i := 1; i <= 10; i++ {
		if i%2 == 0 {
			continue
		}
		fmt.Print(i, " ")
	}
	fmt.Println()

	// B5: range over slice
	grades := []float64{8.5, 4.0, 7.0, 9.5, 3.5}
	passing := 0
	for i, g := range grades {
		fmt.Printf("  [%d] %.1f\n", i, g)
		if g >= 5.0 {
			passing++
		}
	}
	fmt.Println("Passing:", passing)

	// -------------------------------------------------------
	// PART C: switch
	// -------------------------------------------------------

	// C1: switch on string
	day := "Monday"
	switch day {
	case "Saturday", "Sunday":
		fmt.Println(day, "is a weekend")
	default:
		fmt.Println(day, "is a weekday")
	}

	// C2: switch with no condition
	switch {
	case score >= 9.0:
		fmt.Println("switch: Excellent")
	case score >= 7.0:
		fmt.Println("switch: Good")
	case score >= 5.0:
		fmt.Println("switch: Pass")
	default:
		fmt.Println("switch: Fail")
	}

	// C3: multiple values in case
	month := 2
	switch month {
	case 1, 3, 5, 7, 8, 10, 12:
		fmt.Println("31 days")
	case 4, 6, 9, 11:
		fmt.Println("30 days")
	case 2:
		fmt.Println("28 or 29 days")
	}
}
