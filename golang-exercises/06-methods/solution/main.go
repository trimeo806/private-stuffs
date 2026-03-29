// Solution 06: Methods and Pointer Receivers

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

// Value receivers
func (r Rectangle) Area() float64 {
	return r.Width * r.Height
}

func (r Rectangle) Perimeter() float64 {
	return 2 * (r.Width + r.Height)
}

func (r Rectangle) IsSquare() bool {
	return r.Width == r.Height
}

// Pointer receivers
func (r *Rectangle) Scale(f float64) {
	r.Width *= f
	r.Height *= f
}

func (r *Rectangle) Reset() {
	r.Width = 0
	r.Height = 0
}

// BankAccount
func (b *BankAccount) Deposit(amount float64) error {
	if amount <= 0 {
		return errors.New("deposit amount must be positive")
	}
	b.Balance += amount
	return nil
}

func (b *BankAccount) Withdraw(amount float64) error {
	if amount <= 0 {
		return errors.New("withdrawal amount must be positive")
	}
	if amount > b.Balance {
		return fmt.Errorf("insufficient funds: balance=%.2f, requested=%.2f", b.Balance, amount)
	}
	b.Balance -= amount
	return nil
}

func (b BankAccount) String() string {
	return fmt.Sprintf("Account[%s] Balance: %.2f", b.Owner, b.Balance)
}

func main() {
	fmt.Println("=== Rectangle ===")
	r := Rectangle{Width: 4, Height: 6}
	fmt.Println("Area:", r.Area())
	fmt.Println("Perimeter:", r.Perimeter())
	fmt.Println("IsSquare:", r.IsSquare())

	r.Scale(2)
	fmt.Println("After Scale(2):", r)

	r.Reset()
	fmt.Println("After Reset:", r)

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
	fmt.Println("Withdraw 5000, err:", err)
	fmt.Println(acc.String())
}
