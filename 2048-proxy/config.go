package main

import (
	"encoding/json"
	"io/ioutil"
)

var (
	conf config
)

// This models match config option
type config struct {
	Port    int         `json:"port"`
	Kafka   string      `json:"kafka"`
	Mongodb mongoConfig `json:"mongodb"`
}

type mongoConfig struct {
	Host       string `json:"host"`
	Username   string `json:"username"`
	Password   string `json:"password"`
	Database   string `json:"database"`
	Collection string `json:"collection"`
}

func init() {
	file, err := ioutil.ReadFile("config.json")
	if err != nil {
		panic("Make sure 'config.json' file exists!")
	}

	err = json.Unmarshal(file, &conf)
	if err != nil {
		panic("Make sure config file correct!")
	}
}
