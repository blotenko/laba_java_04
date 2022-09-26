package main

import (
	"fmt"
	"io"
	"os"
)

type busTable struct {
	punktA string
	puntkB string
	price  string
}

type b []busTable

func main() {
	b := new([4]busTable)
	b1 := make(chan [4]busTable)

	s := ""
	file, err := os.Open("hello.txt")
	if err != nil {
		fmt.Println(err)
		os.Exit(1)
	}
	defer file.Close()

	data := make([]byte, 64)

	for {
		n, err := file.Read(data)
		if err == io.EOF { // если конец файла
			break // выходим из цикла
		}

		s += string(data[:n])
	}
	str := ""
	j := 0
	for i := 0; i < len(s); i++ {
		if s[i] == ':' {
			b[j].punktA = str
			str = ""
		} else if s[i] == '-' {
			b[j].puntkB = str
			str = ""
		} else if s[i] == ' ' {
			b[j].price = str
			str = ""
			j++
		} else if i+1 == len(s) {
			str += string(s[i])
			b[j].price = str
			str = ""
			j++
		} else {
			str += string(s[i])
		}
	}

	fmt.Println(b)
	go changePrice("200", 0, b, b1)
	go addDeleteReys(0, b, b1)
	go findReys("A", "C", b)
	for {
		num, opened := <-b1 // получаем данные из потока
		if !opened {
			break // если поток закрыт, выход из цикла
		}
		fmt.Println(num)
	}

}

func changePrice(newPrice string, id int, b *[4]busTable, b1 chan [4]busTable) {
	b[id].price = newPrice
	//defer close(b1)
	b1 <- *b

}

func addDeleteReys(id int, b *[4]busTable, b1 chan [4]busTable) {
	for i := id; i < len(b)-1; i++ {
		b[i] = b[i+1]
	}
	b[int(len(b))-1].punktA = "newA"
	b[int(len(b))-1].puntkB = "newB"
	b[int(len(b))-1].price = "newPrice"
	//defer close(b1)
	b1 <- *b
}

func findReys(f string, l string, b *[4]busTable) {
	for i := 0; i < len(b)-1; i++ {
		if b[i].punktA == f && b[i].puntkB == l {
			fmt.Println("Price of finding reys :")
			fmt.Println(b[i].price)
			return
		}
	}
	sum := ""
	for i := 0; i < len(b)-1; i++ {
		if b[i].punktA == f {
			sum = b[i].price
			for {
				i++
				sum = sum + "+" + b[i].price
				if b[i].puntkB == l {
					sum = sum + "+" + b[i].price
					fmt.Println("Price of finding reys :")
					fmt.Println(sum)
					return
				}
			}
		}
	}
}
