package main

import (
	"encoding/json"
	"flag"
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
	var configPath = flag.String("f", "config.json", "config file path")
	var kafkaUrl = flag.String("k", "", "kafka url")
	var mongoDBUrl = flag.String("m", "", "mongodb url")
	flag.Parse()

	file, err := ioutil.ReadFile(*configPath)
	if err != nil {
		panic("Make sure 'config.json' file exists!")
	}

	err = json.Unmarshal(file, &conf)
	if err != nil {
		panic("Make sure config file correct!")
	}
	if len(*kafkaUrl) > 0 {
		conf.Kafka = *kafkaUrl
	}

	if len(*mongoDBUrl) > 0 {
		conf.Mongodb.Host = *mongoDBUrl
	}

}
