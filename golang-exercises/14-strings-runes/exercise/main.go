// Exercise 14: Strings, Runes, and Bytes
//
// Topics: string as byte sequence, rune (Unicode code point),
//         len vs rune count, range over string, strings package,
//         string ↔ []byte ↔ []rune conversion
//
// Run:      go run ./14-strings-runes/exercise/
// Solution: go run ./14-strings-runes/solution/

package main

import (
	"fmt"
	"strings"
	"unicode"
)

// TODO 1: Write function "runeCount" that takes a string
//   and returns the number of Unicode characters (not bytes)
// func runeCount(s string) int { ... }

// TODO 2: Write function "reverseString" that reverses a string
//   correctly handling Unicode (use []rune, not []byte)
//   e.g. "Xin chào" → "oàhc niX"
// func reverseString(s string) string { ... }

// TODO 3: Write function "isPalindrome" that returns true
//   if a string reads the same forwards and backwards
//   ignore case: "Racecar" → true
// func isPalindrome(s string) bool { ... }

// TODO 4: Write function "countVowels" that returns
//   the number of vowels (a,e,i,o,u — case insensitive) in a string
// func countVowels(s string) int { ... }

// TODO 5: Write function "titleCase" that capitalizes
//   the first letter of each word (handle Unicode correctly)
//   e.g. "hello world" → "Hello World"
// func titleCase(s string) string { ... }

// TODO 6: Write function "byteVsRune" that takes a string
//   prints both the byte count and rune count
//   then iterates with range, printing each rune's byte-index and character
// func byteVsRune(s string) { ... }

func main() {
	_ = strings.ToLower  // prevent import error
	_ = unicode.ToUpper

	// Test TODO 1
	fmt.Println("=== runeCount ===")
	// fmt.Println(runeCount("Hello"))         // 5
	// fmt.Println(runeCount("Xin chào"))      // 8 (not 10!)
	// fmt.Println(runeCount("日本語"))          // 3

	// Test TODO 2
	fmt.Println("\n=== reverseString ===")
	// fmt.Println(reverseString("Hello"))     // olleH
	// fmt.Println(reverseString("Xin chào")) // oàhc niX

	// Test TODO 3
	fmt.Println("\n=== isPalindrome ===")
	// fmt.Println(isPalindrome("racecar"))    // true
	// fmt.Println(isPalindrome("Racecar"))    // true
	// fmt.Println(isPalindrome("hello"))      // false

	// Test TODO 4
	fmt.Println("\n=== countVowels ===")
	// fmt.Println(countVowels("Hello World")) // 3
	// fmt.Println(countVowels("AEIOU"))       // 5

	// Test TODO 5
	fmt.Println("\n=== titleCase ===")
	// fmt.Println(titleCase("hello world"))       // Hello World
	// fmt.Println(titleCase("xin chào việt nam")) // Xin Chào Việt Nam

	// Test TODO 6
	fmt.Println("\n=== byteVsRune ===")
	// byteVsRune("Hi")
	// byteVsRune("chào")

	// Strings package demo
	fmt.Println("\n=== strings package ===")
	s := "  Hello, Golang World!  "
	fmt.Println(strings.TrimSpace(s))
	fmt.Println(strings.ToUpper(s))
	fmt.Println(strings.Contains(s, "Golang"))
	fmt.Println(strings.Replace(s, "Golang", "Go", 1))
	fmt.Println(strings.Split("a,b,c,d", ","))
	fmt.Println(strings.Join([]string{"x", "y", "z"}, "-"))
	fmt.Println(strings.Count(s, "l"))

	// String ↔ []byte ↔ []rune
	fmt.Println("\n=== Conversions ===")
	str := "hello"
	// TODO: convert to []byte, change first byte to 'H', convert back
	// b := []byte(str)
	// b[0] = 'H'
	// fmt.Println(string(b))   // Hello

	// TODO: convert to []rune, print each rune as character and its code point
	// r := []rune(str)
	// for _, ch := range r {
	//     fmt.Printf("'%c' = %d\n", ch, ch)
	// }
	_ = str
}
