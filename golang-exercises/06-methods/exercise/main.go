// Exercise 06: Methods and Pointer Receivers
//
// Topics: value receiver, pointer receiver, method on custom type,
//         when to use each, method chaining (bonus)
//
// Run:      go run ./06-methods/exercise/
// Solution: go run ./06-methods/solution/

package main

import (
	"errors"
	"fmt"
)

type Rectangle struct {
	Width  float64
	Height float64
}

type BankAccount struct {
	Owner   string
	Balance float64
}

// -------------------------------------------------------
// Rectangle methods (value receivers — read-only)
// -------------------------------------------------------

// TODO 1: Value receiver method "Area" on Rectangle
//
//	returns Width * Height
//
// func (r Rectangle) Area() float64 { ... }
func (r Rectangle) Area() float64 {
	return r.Height * r.Width

}

// TODO 2: Value receiver method "Perimeter" on Rectangle
//
//	returns 2 * (Width + Height)
func (r Rectangle) Perimeter() float64 {
	return 2 * (r.Width + r.Height)
}

// TODO 3: Value receiver method "IsSquare" on Rectangle
//
//	returns true if Width == Height
//
// func (r Rectangle) IsSquare() bool { ... }
func (r Rectangle) IsSquare() bool {
	return r.Height == r.Width
}

// -------------------------------------------------------
// Rectangle methods (pointer receivers — mutates state)
// -------------------------------------------------------

// TODO 4: Pointer receiver method "Scale" on Rectangle
//
//	multiplies both Width and Height by factor f
func (r *Rectangle) Scale(f float64) {
	r.Width *= f
	r.Height *= f
}

// TODO 5: Pointer receiver method "Reset" on Rectangle
//
//	sets Width and Height to 0
func (r *Rectangle) Reset() {
	r.Height = 0
	r.Width = 0
}

// -------------------------------------------------------
// BankAccount methods
// -------------------------------------------------------

// TODO 6: Pointer receiver "Deposit"
//
//	adds amount to Balance, return error if amount <= 0
func (b *BankAccount) Deposit(amount float64) error {
	if amount <= 0 {
		return errors.New("amount need to be larger than 0")
	}
	b.Balance = b.Balance + amount
	return nil
}

// TODO 7: Pointer receiver "Withdraw"
//
//	subtracts amount from Balance
//	return error if amount <= 0 or amount > Balance
func (b *BankAccount) Withdraw(amount float64) error {
	switch {
	case amount <= 0, amount > b.Balance:
		return errors.New("amount need to be larger than 0")
	default:
		b.Balance = b.Balance - amount
		return nil
	}

}

// TODO 8: Value receiver "String" — returns formatted string
//
//	"Account[Owner] Balance: X.XX"
func (b *BankAccount) String() string {
	return fmt.Sprintf("Account [%v] Blance: %v", b.Owner, b.Balance)
}

func main() {
	// Test Rectangle
	fmt.Println("=== Rectangle ===")
	r := Rectangle{Width: 4, Height: 6}
	fmt.Println("Area:", r.Area())           // 24
	fmt.Println("Perimeter:", r.Perimeter()) // 20
	fmt.Println("IsSquare:", r.IsSquare())   // false

	// Test pointer receiver via value variable (Go auto-takes address)
	r.Scale(2)
	fmt.Println("After Scale(2):", r) // {8, 12}

	r.Reset()
	fmt.Println("After Reset:", r) // {0, 0}
	_ = r

	// Test BankAccount
	fmt.Println("\n=== BankAccount ===")
	acc := BankAccount{Owner: "An", Balance: 1000}
	fmt.Println(acc.String())

	err := acc.Deposit(500)
	fmt.Println("Deposit 500, err:", err)
	fmt.Println(acc.String())

	err = acc.Withdraw(200)
	fmt.Println("Withdraw 200, err:", err)
	fmt.Println(acc.String())

	err = acc.Withdraw(5000)
	fmt.Println("Withdraw 5000, err:", err) // should error
	_ = acc

	fmt.Println("implement the TODOs above!")
}
