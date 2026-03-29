// Exercise 10: Error Handling
//
// Topics: error interface, fmt.Errorf, custom error types,
//         errors.Is, errors.As, error wrapping with %w
//
// Run:      go run ./10-errors/exercise/
// Solution: go run ./10-errors/solution/

package main

import (
	"errors"
	"fmt"
)

// TODO 1: Create a custom error type "ValidationError"
//
//	Fields: Field string, Message string
//	Implement the error interface: Error() string
//	Format: "validation failed on 'Field': Message"
type ValidationError struct {
	Field   string
	Message string
}

type IError interface {
	Error() string
}

func (e ValidationError) Error() string {
	return fmt.Sprintf("validation failed on %v: %v", e.Field, e.Message)

}

// TODO 2: Create a custom error type "NotFoundError"
//
//	Fields: Resource string, ID int
//	Implement the error interface: Error() string
//	Format: "Resource with ID X not found"
type NotFoundError struct {
	Resource string
	ID       int
}

func (e *NotFoundError) Error() string {
	return fmt.Sprintf("%v with ID %v not found", e.Resource, e.ID)
}

// Sentinel error (predeclared error value)
// TODO 3: Declare a sentinel error:
var ErrUnauthorized = errors.New("unauthorized access")

// -------------------------------------------------------

// TODO 4: Write function "validateAge" that takes an int
//   - if age < 0: return ValidationError{Field:"age", Message:"cannot be negative"}
//   - if age > 150: return ValidationError{Field:"age", Message:"unrealistic value"}
//   - otherwise: return nil
func validateAge(age int) error {
	err := ValidationError{Field: "age", Message: "cannot be negative"}

	if age < 0 {
		return errors.New(err.Error())
	}

	if age > 150 {
		err = ValidationError{Field: "age", Message: "unrealistic value"}
		return errors.New(err.Error())
	}

	return nil
}

// TODO 5: Write function "findUser" that takes id int
//   - if id <= 0: return ErrUnauthorized (sentinel)
//   - if id > 100: return NotFoundError{Resource:"user", ID: id}
//   - otherwise: return nil (user found)
func findUser(id int) error {
	if id <= 0 {
		return ErrUnauthorized
	}

	if id > 100 {
		err := NotFoundError{Resource: "user", ID: id}
		return errors.New(err.Error())
	}

	return nil
}

// TODO 6: Write function "processUser" that calls findUser
//
//	If findUser returns error, wrap it with fmt.Errorf("processUser: %w", err)
//	Otherwise print "user X processed"
func processUser(id int) error {
	err := findUser(id)
	if err != nil {
		return fmt.Errorf("processUser: %w", err)
	}

	return nil
}

func main() {
	_ = errors.Is // prevent import error
	_ = errors.As

	// Test TODO 4
	fmt.Println("=== validateAge ===")
	for _, age := range []int{25, -1, 200} {
		err := validateAge(age)
		if err != nil {
			fmt.Println("age", age, "→ error:", err)
			// TODO: use errors.As to check if it's a ValidationError
			var ve *ValidationError
			if errors.As(err, &ve) {
				fmt.Println("  field:", ve.Field)
			}
		} else {
			fmt.Println("age", age, "→ OK")
		}
	}

	// Test TODO 5 & 6
	fmt.Println("\n=== processUser ===")
	for _, id := range []int{50, -1, 999} {
		err := processUser(id)
		if err != nil {
			fmt.Println("id", id, "→ error:", err)

			// Check with errors.Is (works through wrapping)
			if errors.Is(err, ErrUnauthorized) {
				fmt.Println("  → unauthorized!")
			}

			// Check with errors.As (works through wrapping)
			var nfe *NotFoundError
			if errors.As(err, &nfe) {
				fmt.Printf("  → %s #%d not found\n", nfe.Resource, nfe.ID)
			}
		} else {
			fmt.Println("id", id, "→ OK")
		}
	}

	fmt.Println("implement the TODOs above!")
}
