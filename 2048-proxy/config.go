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
	Port  int    `json:"port"`
	Kafka string `json:"kafka"`
}

func init() {
	file, err := ioutil.ReadFile("conf/config.json")
	if err != nil {
		panic("Make sure 'conf/config.json' file exists!")
	}

	err = json.Unmarshal(file, &conf)
	if err != nil {
		panic("Make sure config file correct!")
	}
}
