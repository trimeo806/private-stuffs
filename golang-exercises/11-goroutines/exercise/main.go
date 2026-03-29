// Exercise 11: Goroutines and Channels
//
// Topics: go keyword, unbuffered channel, buffered channel,
//         range over channel, select, sync.WaitGroup, sync.Mutex
//
// Run:      go run ./11-goroutines/exercise/
// Solution: go run ./11-goroutines/solution/

package main

import (
	"fmt"
	"sync"
	"time"
)

// TODO 1: Write function "printNumbers" that takes a channel chan int
//   reads numbers from the channel and prints them
//   until the channel is closed
// func printNumbers(ch <-chan int) { ... }

// TODO 2: Write function "generateNumbers" that takes (n int, ch chan<- int)
//   sends numbers 1..n to the channel, then closes it
// func generateNumbers(n int, ch chan<- int) { ... }

// TODO 3: WaitGroup exercise
//   Write function "fetchAll" that simulates fetching 5 URLs concurrently
//   Each "fetch" takes 100ms (time.Sleep) and prints "fetched: url-X"
//   Use sync.WaitGroup to wait for all goroutines to finish
// func fetchAll() { ... }

// TODO 4: Mutex exercise
//   There's a shared counter below. Write function "safeIncrement"
//   that increments                it 1000 times using a goroutine + Mutex to prevent race condition
//   Then in main, spawn 5 goroutines each calling safeIncrement, wait for all,
//   print the final counter (should be 5000)

var counter int
var mu sync.Mutex

// func safeIncrement(wg *sync.WaitGroup) { ... }

// TODO 5: Select + timeout
//   Write function "withTimeout" that takes a channel chan string and duration
//   - if a value arrives on ch before timeout: print "received: X"
//   - if timeout fires first: print "timeout!"
// func withTimeout(ch <-chan string, timeout time.Duration) { ... }

func main() {
	_ = time.Second // prevent import error

	// Test TODO 1 & 2
	fmt.Println("=== Channel: generate & print ===")
	// ch := make(chan int)
	// go generateNumbers(5, ch)
	// printNumbers(ch)

	// Test TODO 3
	fmt.Println("\n=== WaitGroup: fetchAll ===")
	// fetchAll()

	// Test TODO 4
	fmt.Println("\n=== Mutex: safe counter ===")
	// var wg sync.WaitGroup
	// for i := 0; i < 5; i++ {
	//     wg.Add(1)
	//     go safeIncrement(&wg)
	// }
	// wg.Wait()
	// fmt.Println("Final counter:", counter) // should be 5000

	// Test TODO 5
	fmt.Println("\n=== Select: timeout ===")
	// ch1 := make(chan string, 1)
	// // send a value after 50ms
	// go func() {
	//     time.Sleep(50 * time.Millisecond)
	//     ch1 <- "hello"
	// }()
	// withTimeout(ch1, 200*time.Millisecond)  // should receive
	// withTimeout(make(chan string), 100*time.Millisecond) // should timeout

	// Buffered channel demo
	fmt.Println("\n=== Buffered Channel ===")
	// TODO: create a buffered channel with capacity 3
	//   send 3 values without a receiver (won't block because buffered)
	//   then receive and print all 3
	// bch := make(chan int, 3)
	// bch <- 10; bch <- 20; bch <- 30
	// fmt.Println(<-bch, <-bch, <-bch)

	fmt.Println("implement the TODOs above!")
}
