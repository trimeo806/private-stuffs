// Exercise 08: Slices
//
// Topics: make, append, copy, slice expressions [low:high],
//         2D slices, passing slices to functions
//
// Run:      go run ./08-slices/exercise/
// Solution: go run ./08-slices/solution/

package main

import "fmt"

// TODO 1: Write function "removeDuplicates" that takes []int
//
//	and returns a new []int with duplicates removed (preserve order)
//	e.g. [1,2,2,3,1,4] → [1,2,3,4]
func removeDuplicates(nums []int) []int {
	seen := make(map[int]bool)
	result := make([]int, 0, len(nums))

	for i := range len(nums) {
		if seen[nums[i]] {
			continue
		}

		seen[nums[i]] = true
		result = append(result, nums[i])
	}

	return result
}

// TODO 2: Write function "reverse" that reverses a []int IN PLACE
//
//	(modify the original slice, don't create a new one)
func reverse(nums []int) {
	for i := range len(nums) / 2 {
		j := len(nums) - 1 - i
		nums[i], nums[j] = nums[j], nums[i]
	}
}

// TODO 3: Write function "filter" that takes []float64 and a predicate func
//
//	returns a new slice containing only elements where fn(element) == true
func filter(nums []float64, fn func(float64) bool) []float64 {
	result := make([]float64, 0, len(nums)) // hoặc: result := []float64{}
	for i := range len(nums) {
		if fn(nums[i]) {
			result = append(result, nums[i])
		}
	}

	return result
}

// TODO 4: Write function "chunk" that splits a []int into chunks of size n
//
//	e.g. chunk([1,2,3,4,5], 2) → [[1,2],[3,4],[5]]
func chunk(nums []int, n int) [][]int {
	if len(nums) == 0 {
		return nil
	}
	result := make([][]int, 0)
	for i := 0; i < len(nums); i += n {
		end := i + n
		if end > len(nums) {
			end = len(nums)
		}

		chunk := nums[i:end]
		result = append(result, chunk)
	}

	return result
}

// TODO 5: Write function "flatten" that takes [][]int and returns []int
//
//	e.g. [[1,2],[3,4],[5]] → [1,2,3,4,5]
func flatten(matrix [][]int) []int {
	result := make([]int, 0)
	for i := range matrix {
		for j := range len(matrix[i]) {
			result = append(result, matrix[i][j])
		}
	}
	return result
}

func main() {
	// Slice basics
	fmt.Println("=== Slice Basics ===")
	// make
	s1 := make([]int, 3)     // len=3, cap=3, zero-valued
	s2 := make([]int, 3, 10) // len=3, cap=10
	fmt.Println("s1:", s1, "len:", len(s1), "cap:", cap(s1))
	fmt.Println("s2:", s2, "len:", len(s2), "cap:", cap(s2))

	// append
	s3 := []int{1, 2, 3}
	s3 = append(s3, 4, 5)
	fmt.Println("after append:", s3)

	// slice expression
	fmt.Println("s3[1:3]:", s3[1:3]) // [2 3]
	fmt.Println("s3[:2]: ", s3[:2])  // [1 2]
	fmt.Println("s3[3:]: ", s3[3:])  // [4 5]

	// copy
	src := []int{10, 20, 30}
	dst := make([]int, len(src))
	n := copy(dst, src)
	fmt.Println("copied:", n, "dst:", dst)

	// TODO: Modify dst and verify src is unchanged
	dst[0] = 999
	fmt.Println("src:", src, "dst:", dst)

	// Test TODO 1
	fmt.Println("\n=== removeDuplicates ===")
	nums := []int{1, 2, 2, 3, 1, 4, 3}
	fmt.Println(removeDuplicates(nums)) // [1 2 3 4]

	// Test TODO 2
	fmt.Println("\n=== reverse ===")
	r := []int{1, 2, 3, 4, 5}
	reverse(r)
	fmt.Println(r) // [5 4 3 2 1]

	// Test TODO 3
	fmt.Println("\n=== filter ===")
	scores := []float64{8.5, 4.0, 7.0, 9.5, 3.5, 6.0}
	passing := filter(scores, func(x float64) bool { return x >= 5.0 })
	fmt.Println("passing:", passing)

	// Test TODO 4
	fmt.Println("\n=== chunk ===")
	chunks := chunk([]int{1, 2, 3, 4, 5, 6, 7}, 3)
	fmt.Println(chunks) // [[1 2 3] [4 5 6] [7]]

	// Test TODO 5
	fmt.Println("\n=== flatten ===")
	matrix := [][]int{{1, 2}, {3, 4}, {5}}
	fmt.Println(flatten(matrix)) // [1 2 3 4 5]

	_ = s1
	_ = s2
}
