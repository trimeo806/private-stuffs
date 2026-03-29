// Solution 09: Maps

package main

import (
	"fmt"
	"strings"
)

func wordCount(sentence string) map[string]int {
	counts := make(map[string]int)
	for _, word := range strings.Fields(sentence) {
		counts[word]++
	}
	return counts
}

func groupByScore(scores map[string]float64) map[string][]string {
	groups := map[string][]string{"A": {}, "B": {}, "C": {}, "F": {}}
	for name, score := range scores {
		switch {
		case score >= 9:
			groups["A"] = append(groups["A"], name)
		case score >= 7:
			groups["B"] = append(groups["B"], name)
		case score >= 5:
			groups["C"] = append(groups["C"], name)
		default:
			groups["F"] = append(groups["F"], name)
		}
	}
	return groups
}

func invertMap(m map[string]int) map[int]string {
	result := make(map[int]string)
	for k, v := range m {
		result[v] = k
	}
	return result
}

func mergeMaps(a, b map[string]int) map[string]int {
	result := make(map[string]int)
	for k, v := range a {
		result[k] += v
	}
	for k, v := range b {
		result[k] += v
	}
	return result
}

func main() {
	fmt.Println("=== Map Basics ===")
	scores := make(map[string]float64)
	scores["An"] = 8.5
	scores["Binh"] = 7.0
	scores["Cuong"] = 9.5
	fmt.Println("scores:", scores)
	fmt.Println("An's score:", scores["An"])

	val, ok := scores["Dung"]
	fmt.Println("Dung exists:", ok, "val:", val)

	scores["An"] = 9.0
	fmt.Println("An updated:", scores["An"])

	delete(scores, "Binh")
	fmt.Println("after delete Binh:", scores)

	fmt.Println("\nAll scores:")
	for name, score := range scores {
		fmt.Printf("  %s: %.1f\n", name, score)
	}

	fmt.Println("\n=== wordCount ===")
	wc := wordCount("the cat sat on the mat the cat")
	fmt.Println(wc)

	fmt.Println("\n=== groupByScore ===")
	grades := map[string]float64{
		"An": 9.5, "Binh": 7.2, "Cuong": 4.0,
		"Dung": 8.5, "Em": 5.5, "Phuong": 3.0,
	}
	groups := groupByScore(grades)
	for grade, names := range groups {
		fmt.Println(grade, ":", names)
	}

	fmt.Println("\n=== invertMap ===")
	m := map[string]int{"a": 1, "b": 2, "c": 3}
	fmt.Println(invertMap(m))

	fmt.Println("\n=== mergeMaps ===")
	m1 := map[string]int{"a": 1, "b": 2}
	m2 := map[string]int{"b": 3, "c": 4}
	fmt.Println(mergeMaps(m1, m2))
}
