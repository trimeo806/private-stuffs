// Solution 14: Strings, Runes, and Bytes

package main

import (
	"fmt"
	"strings"
	"unicode"
)

func runeCount(s string) int {
	return len([]rune(s))
}

func reverseString(s string) string {
	runes := []rune(s)
	for i, j := 0, len(runes)-1; i < j; i, j = i+1, j-1 {
		runes[i], runes[j] = runes[j], runes[i]
	}
	return string(runes)
}

func isPalindrome(s string) bool {
	s = strings.ToLower(s)
	runes := []rune(s)
	for i, j := 0, len(runes)-1; i < j; i, j = i+1, j-1 {
		if runes[i] != runes[j] {
			return false
		}
	}
	return true
}

func countVowels(s string) int {
	count := 0
	for _, r := range strings.ToLower(s) {
		switch r {
		case 'a', 'e', 'i', 'o', 'u':
			count++
		}
	}
	return count
}

func titleCase(s string) string {
	words := strings.Fields(s)
	for i, word := range words {
		runes := []rune(word)
		if len(runes) > 0 {
			runes[0] = unicode.ToUpper(runes[0])
		}
		words[i] = string(runes)
	}
	return strings.Join(words, " ")
}

func byteVsRune(s string) {
	fmt.Printf("string: %q\n", s)
	fmt.Printf("  bytes: %d  runes: %d\n", len(s), len([]rune(s)))
	for i, r := range s {
		fmt.Printf("  byte-index %d: '%c' (U+%04X)\n", i, r, r)
	}
}

func main() {
	fmt.Println("=== runeCount ===")
	fmt.Println(runeCount("Hello"))    // 5
	fmt.Println(runeCount("Xin chào")) // 8
	fmt.Println(runeCount("日本語"))     // 3

	fmt.Println("\n=== reverseString ===")
	fmt.Println(reverseString("Hello"))    // olleH
	fmt.Println(reverseString("Xin chào")) // oàhc niX

	fmt.Println("\n=== isPalindrome ===")
	fmt.Println(isPalindrome("racecar")) // true
	fmt.Println(isPalindrome("Racecar")) // true
	fmt.Println(isPalindrome("hello"))   // false

	fmt.Println("\n=== countVowels ===")
	fmt.Println(countVowels("Hello World")) // 3
	fmt.Println(countVowels("AEIOU"))       // 5

	fmt.Println("\n=== titleCase ===")
	fmt.Println(titleCase("hello world"))
	fmt.Println(titleCase("xin chào việt nam"))

	fmt.Println("\n=== byteVsRune ===")
	byteVsRune("Hi")
	byteVsRune("chào")

	fmt.Println("\n=== strings package ===")
	s := "  Hello, Golang World!  "
	fmt.Println(strings.TrimSpace(s))
	fmt.Println(strings.ToUpper(s))
	fmt.Println(strings.Contains(s, "Golang"))
	fmt.Println(strings.Replace(s, "Golang", "Go", 1))
	fmt.Println(strings.Split("a,b,c,d", ","))
	fmt.Println(strings.Join([]string{"x", "y", "z"}, "-"))
	fmt.Println(strings.Count(s, "l"))

	fmt.Println("\n=== Conversions ===")
	str := "hello"
	b := []byte(str)
	b[0] = 'H'
	fmt.Println(string(b)) // Hello

	r := []rune(str)
	for _, ch := range r {
		fmt.Printf("'%c' = %d\n", ch, ch)
	}
}
