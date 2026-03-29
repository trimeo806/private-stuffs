// Solution 08: Slices

package main

import "fmt"

func removeDuplicates(nums []int) []int {
	seen := make(map[int]bool)
	result := []int{}
	for _, n := range nums {
		if !seen[n] {
			seen[n] = true
			result = append(result, n)
		}
	}
	return result
}

func reverse(nums []int) {
	for i, j := 0, len(nums)-1; i < j; i, j = i+1, j-1 {
		nums[i], nums[j] = nums[j], nums[i]
	}
}

func filter(nums []float64, fn func(float64) bool) []float64 {
	var result []float64
	for _, n := range nums {
		if fn(n) {
			result = append(result, n)
		}
	}
	return result
}

func chunk(nums []int, n int) [][]int {
	var result [][]int
	for i := 0; i < len(nums); i += n {
		end := i + n
		if end > len(nums) {
			end = len(nums)
		}
		result = append(result, nums[i:end])
	}
	return result
}

func flatten(matrix [][]int) []int {
	var result []int
	for _, row := range matrix {
		result = append(result, row...)
	}
	return result
}

func main() {
	fmt.Println("=== Slice Basics ===")
	s1 := make([]int, 3)
	s2 := make([]int, 3, 10)
	fmt.Println("s1:", s1, "len:", len(s1), "cap:", cap(s1))
	fmt.Println("s2:", s2, "len:", len(s2), "cap:", cap(s2))

	s3 := []int{1, 2, 3}
	s3 = append(s3, 4, 5)
	fmt.Println("after append:", s3)

	fmt.Println("s3[1:3]:", s3[1:3])
	fmt.Println("s3[:2]: ", s3[:2])
	fmt.Println("s3[3:]: ", s3[3:])

	src := []int{10, 20, 30}
	dst := make([]int, len(src))
	n := copy(dst, src)
	fmt.Println("copied:", n, "dst:", dst)
	dst[0] = 999
	fmt.Println("src:", src, "dst:", dst) // src unchanged

	fmt.Println("\n=== removeDuplicates ===")
	nums := []int{1, 2, 2, 3, 1, 4, 3}
	fmt.Println(removeDuplicates(nums))

	fmt.Println("\n=== reverse ===")
	r := []int{1, 2, 3, 4, 5}
	reverse(r)
	fmt.Println(r)

	fmt.Println("\n=== filter ===")
	scores := []float64{8.5, 4.0, 7.0, 9.5, 3.5, 6.0}
	passing := filter(scores, func(x float64) bool { return x >= 5.0 })
	fmt.Println("passing:", passing)

	fmt.Println("\n=== chunk ===")
	chunks := chunk([]int{1, 2, 3, 4, 5, 6, 7}, 3)
	fmt.Println(chunks)

	fmt.Println("\n=== flatten ===")
	matrix := [][]int{{1, 2}, {3, 4}, {5}}
	fmt.Println(flatten(matrix))

	_ = s1
	_ = s2
}
