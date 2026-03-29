// Exercise 09: Maps
//
// Topics: make, CRUD, check existence (comma-ok), iterate,
//         delete, map of slices, counting/grouping patterns
//
// Run:      go run ./09-maps/exercise/
// Solution: go run ./09-maps/solution/

package main

import (
	"fmt"
	"strings"
)

// TODO 1: Write function "wordCount" that takes a string (space-separated words)
//
//	and returns map[string]int counting how many times each word appears
//	e.g. "the cat sat on the mat" → {"the":2, "cat":1, "sat":1, ...}
func wordCount(sentence string) map[string]int {
	res := make(map[string]int)
	sentenceArray := strings.Split(sentence, " ")
	for i := range sentenceArray {
		value, ok := res[sentenceArray[i]]
		if ok {
			res[sentenceArray[i]] = value + 1
		} else {
			res[sentenceArray[i]] = 1
		}

	}

	return res
}

// TODO 2: Write function "groupByScore" that takes map[string]float64 (name→score)
//
//	and returns map[string][]string grouping names by grade letter:
//	"A" (>=9), "B" (>=7), "C" (>=5), "F" (<5)
func groupByScore(scores map[string]float64) map[string][]string {
	result := make(map[string][]string)
	grade := ""
	for name, score := range scores {
		switch {
		case score >= 9:
			grade = "A"

		case score >= 7:
			grade = "B"

		case score >= 5:
			grade = "C"

		default:
			grade = "F"
		}

		result[grade] = append(result[grade], name)
	}

	return result

}

// TODO 3: Write function "invertMap" that takes map[string]int
//
//	and returns map[int]string (swap keys and values)
//	if two keys have the same value, keep any one of them
func invertMap(m map[string]int) map[int]string {
	result := make(map[int]string)

	for key, value := range m {
		result[value] = key
	}

	return result
}

// TODO 4: Write function "mergeMaps" that takes two map[string]int
//
//	and returns a new merged map. If a key exists in both, sum the values.
func mergeMaps(a, b map[string]int) map[string]int {
	r := make(map[string]int)
	for k, v := range a {
		r[k] += v
	}

	for k, v := range b {
		r[k] += v
	}

	return r
}

func main() {
	// Map basics
	fmt.Println("=== Map Basics ===")

	// create with make
	scores := make(map[string]float64)
	scores["An"] = 8.5
	scores["Binh"] = 7.0
	scores["Cuong"] = 9.5
	fmt.Println("scores:", scores)

	// read
	fmt.Println("An's score:", scores["An"])

	// TODO: check if "Dung" exists using comma-ok pattern
	val, ok := scores["Dung"]
	fmt.Println("Dung exists:", ok, "val:", val)

	// update
	scores["An"] = 9.0
	fmt.Println("An updated:", scores["An"])

	// delete
	delete(scores, "Binh")
	fmt.Println("after delete Binh:", scores)

	// iterate (order is random!)
	fmt.Println("\nAll scores:")
	// TODO: range over scores, print each name and score

	// Test TODO 1
	fmt.Println("\n=== wordCount ===")
	wc := wordCount("the cat sat on the mat the cat")
	fmt.Println(wc)
	// expected: map[cat:2 mat:1 on:1 sat:1 the:3]

	// Test TODO 2
	fmt.Println("\n=== groupByScore ===")
	grades := map[string]float64{
		"An": 9.5, "Binh": 7.2, "Cuong": 4.0,
		"Dung": 8.5, "Em": 5.5, "Phuong": 3.0,
	}
	groups := groupByScore(grades)
	for grade, names := range groups {
		fmt.Println(grade, ":", names)
	}

	// Test TODO 3
	fmt.Println("\n=== invertMap ===")
	m := map[string]int{"a": 1, "b": 2, "c": 3}
	fmt.Println(invertMap(m)) // map[1:a 2:b 3:c]

	// Test TODO 4
	fmt.Println("\n=== mergeMaps ===")
	m1 := map[string]int{"a": 1, "b": 2}
	m2 := map[string]int{"b": 3, "c": 4}
	fmt.Println(mergeMaps(m1, m2)) // map[a:1 b:5 c:4]
}
