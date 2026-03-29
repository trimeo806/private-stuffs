// Solution 11: Goroutines and Channels

package main

import (
	"fmt"
	"sync"
	"time"
)

func printNumbers(ch <-chan int) {
	for n := range ch {
		fmt.Println("received:", n)
	}
}

func generateNumbers(n int, ch chan<- int) {
	for i := 1; i <= n; i++ {
		ch <- i
	}
	close(ch)
}

func fetchAll() {
	var wg sync.WaitGroup
	urls := []string{"url-1", "url-2", "url-3", "url-4", "url-5"}
	for _, url := range urls {
		url := url // capture
		wg.Add(1)
		go func() {
			defer wg.Done()
			time.Sleep(100 * time.Millisecond)
			fmt.Println("fetched:", url)
		}()
	}
	wg.Wait()
	fmt.Println("all fetched")
}

var counter int
var mu sync.Mutex

func safeIncrement(wg *sync.WaitGroup) {
	defer wg.Done()
	for i := 0; i < 1000; i++ {
		mu.Lock()
		counter++
		mu.Unlock()
	}
}

func withTimeout(ch <-chan string, timeout time.Duration) {
	select {
	case val := <-ch:
		fmt.Println("received:", val)
	case <-time.After(timeout):
		fmt.Println("timeout!")
	}
}

func main() {
	fmt.Println("=== Channel: generate & print ===")
	ch := make(chan int)
	go generateNumbers(5, ch)
	printNumbers(ch)

	fmt.Println("\n=== WaitGroup: fetchAll ===")
	fetchAll()

	fmt.Println("\n=== Mutex: safe counter ===")
	var wg sync.WaitGroup
	for i := 0; i < 5; i++ {
		wg.Add(1)
		go safeIncrement(&wg)
	}
	wg.Wait()
	fmt.Println("Final counter:", counter) // 5000

	fmt.Println("\n=== Select: timeout ===")
	ch1 := make(chan string, 1)
	go func() {
		time.Sleep(50 * time.Millisecond)
		ch1 <- "hello"
	}()
	withTimeout(ch1, 200*time.Millisecond)
	withTimeout(make(chan string), 100*time.Millisecond)

	fmt.Println("\n=== Buffered Channel ===")
	bch := make(chan int, 3)
	bch <- 10
	bch <- 20
	bch <- 30
	fmt.Println(<-bch, <-bch, <-bch)
}
