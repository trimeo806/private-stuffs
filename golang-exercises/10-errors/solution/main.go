// Solution 10: Error Handling

package main

import (
	"errors"
	"fmt"
)

// Custom error types
type ValidationError struct {
	Field   string
	Message string
}

func (e *ValidationError) Error() string {
	return fmt.Sprintf("validation failed on '%s': %s", e.Field, e.Message)
}

type NotFoundError struct {
	Resource string
	ID       int
}

func (e *NotFoundError) Error() string {
	return fmt.Sprintf("%s with ID %d not found", e.Resource, e.ID)
}

// Sentinel error
var ErrUnauthorized = errors.New("unauthorized access")

func validateAge(age int) error {
	if age < 0 {
		return &ValidationError{Field: "age", Message: "cannot be negative"}
	}
	if age > 150 {
		return &ValidationError{Field: "age", Message: "unrealistic value"}
	}
	return nil
}

func findUser(id int) error {
	if id <= 0 {
		return ErrUnauthorized
	}
	if id > 100 {
		return &NotFoundError{Resource: "user", ID: id}
	}
	return nil
}

func processUser(id int) error {
	if err := findUser(id); err != nil {
		return fmt.Errorf("processUser: %w", err)
	}
	fmt.Printf("user %d processed\n", id)
	return nil
}

func main() {
	fmt.Println("=== validateAge ===")
	for _, age := range []int{25, -1, 200} {
		err := validateAge(age)
		if err != nil {
			fmt.Println("age", age, "→ error:", err)
			var ve *ValidationError
			if errors.As(err, &ve) {
				fmt.Println("  field:", ve.Field)
			}
		} else {
			fmt.Println("age", age, "→ OK")
		}
	}

	fmt.Println("\n=== processUser ===")
	for _, id := range []int{50, -1, 999} {
		err := processUser(id)
		if err != nil {
			fmt.Println("id", id, "→ error:", err)

			if errors.Is(err, ErrUnauthorized) {
				fmt.Println("  → unauthorized!")
			}

			var nfe *NotFoundError
			if errors.As(err, &nfe) {
				fmt.Printf("  → %s #%d not found\n", nfe.Resource, nfe.ID)
			}
		}
	}
}
