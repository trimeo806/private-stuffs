package exercises.e23_concurrency;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * Exercise 23: Concurrency â€” Threads, ExecutorService, CompletableFuture
 *
 * Spring Boot handles concurrency under the hood (each HTTP request = a thread).
 * Understanding threads is essential for:
 *   - Async processing (@Async in Spring)
 *   - Parallel API calls (CompletableFuture)
 *   - Thread-safe beans (singleton scope is default in Spring)
 *
 * In TypeScript/JavaScript, you have a single thread + event loop.
 * In Java, you have REAL threads â€” true parallelism, but also real race conditions.
 *
 * WHY THIS MATTERS FOR SPRING BOOT:
 *   - @Async methods run on a separate thread pool
 *   - WebClient uses CompletableFuture/Mono for non-blocking HTTP calls
 *   - Thread safety: controller/service beans are shared across all request threads
 */
public class E23_Concurrency {

    // === EXERCISE 1: Creating threads ===
    // TODO: Create a thread using a Runnable lambda that:
    //       - Prints "Hello from thread: " + Thread.currentThread().getName()
    //       - Sleeps for 1 second (Thread.sleep(1000))
    //       - Prints "Thread finished: " + Thread.currentThread().getName()
    //
    // TODO: Start 3 such threads.
    //       Notice: the output order is NOT guaranteed â€” that's concurrency!
    //
    // Syntax:
    //   Thread t = new Thread(() -> { ... });
    //   t.start();


    // === EXERCISE 2: ExecutorService â€” Thread pool ===
    // In Spring Boot, you don't create raw threads. You use thread pools.
    // Spring's @Async uses an ExecutorService internally.

    // TODO: Create a fixed thread pool with 3 threads:
    //       ExecutorService executor = Executors.newFixedThreadPool(3);
    //
    // TODO: Submit 5 tasks that each print their task number and thread name,
    //       then sleep for 500ms:
    //       executor.submit(() -> { ... });
    //
    // TODO: Shut down the executor and wait:
    //       executor.shutdown();
    //       executor.awaitTermination(5, TimeUnit.SECONDS);
    //
    // Observe: only 3 threads run at once â€” the other 2 tasks wait in the queue.


    // === EXERCISE 3: Future â€” getting results from threads ===
    // TODO: Create an ExecutorService
    // TODO: Submit a Callable<Integer> that simulates calculating a price:
    //       - Sleep for 1 second
    //       - Return 42
    //       Future<Integer> future = executor.submit(() -> { ... return 42; });
    //
    // TODO: Print "Doing other work..." while the task runs
    // TODO: Call future.get() to block and get the result
    //       Print "Result: " + result
    //
    // This is fundamental â€” Spring's async operations return Future/CompletableFuture.


    // === EXERCISE 4: CompletableFuture â€” modern async ===
    // CompletableFuture is Java's Promise equivalent. Spring WebClient uses it.

    // TODO: Create a method: static CompletableFuture<String> fetchUserAsync(int userId)
    //       - Simulates API call: sleep 1 second
    //       - Returns "User-" + userId
    //       - Use CompletableFuture.supplyAsync(() -> { ... })

    // TODO: Create a method: static CompletableFuture<String> fetchOrderAsync(int orderId)
    //       - Simulates API call: sleep 1.5 seconds
    //       - Returns "Order-" + orderId

    // TODO: In main(), call both methods and combine results:
    //       CompletableFuture<String> userFuture = fetchUserAsync(1);
    //       CompletableFuture<String> orderFuture = fetchOrderAsync(100);
    //       String combined = userFuture.thenCombine(orderFuture,
    //           (user, order) -> user + " placed " + order).join();
    //       Print: "User-1 placed Order-100"
    //
    // Key point: both calls run IN PARALLEL â€” total time ~1.5s, not 2.5s!


    // === EXERCISE 5: CompletableFuture chaining ===
    // Just like JavaScript Promise chaining:  fetch().then().then().catch()

    // TODO: Chain async operations:
    //       CompletableFuture.supplyAsync(() -> "Hello")
    //           .thenApply(s -> s + " World")            // .then(s => s + " World")
    //           .thenApply(String::toUpperCase)           // .then(s => s.toUpperCase())
    //           .thenAccept(System.out::println)          // .then(s => console.log(s))
    //           .exceptionally(ex -> { ... return null; });   // .catch(err => ...)
    //
    // TODO: Create a chain that:
    //       1. Fetches a user (supplyAsync)
    //       2. Transforms the user name to uppercase (thenApply)
    //       3. Fetches orders for that user (thenCompose â€” like flatMap/Promise chain)
    //       4. Prints the result (thenAccept)
    //       Handle any errors with exceptionally()


    // === EXERCISE 6: Thread safety problem ===
    // TODO: Create a shared counter (int count = 0)
    //       Spawn 1000 threads that each increment count++
    //       Print the final count â€” it will likely NOT be 1000! (race condition)
    //
    // TODO: Fix it using synchronized or AtomicInteger:
    //       AtomicInteger safeCount = new AtomicInteger(0);
    //       safeCount.incrementAndGet();
    //       This time the result WILL be 1000.
    //
    // In Spring Boot, this is why @Service beans must be stateless â€”
    // multiple request threads share the same bean instance!


    public static void main(String[] args) throws Exception {

        // === Test Exercise 1 ===
        // TODO: Create and start 3 threads

        // === Test Exercise 2 ===
        // TODO: Run 5 tasks on a 3-thread pool

        // === Test Exercise 3 ===
        // TODO: Submit a callable and get the result

        // === Test Exercise 4 ===
        // TODO: Run parallel async calls and combine

        // === Test Exercise 5 ===
        // TODO: Build a CompletableFuture chain

        // === Test Exercise 6 ===
        // TODO: Demonstrate the race condition, then fix it

        System.out.println("=== Exercise 23 Complete ===");
    }
}
