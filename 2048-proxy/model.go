package main

type Record struct {
	Grid [][]int `json:"grid"`
	UUID string  `json:"-"`
}

type Resp struct {
	Code    int         `json:code`
	Message string      `json:"message"`
	Data    interface{} `json:"data"`
}