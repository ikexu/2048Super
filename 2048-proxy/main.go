package main

import (
	"fmt"
	"strings"
	"time"

	mgo "gopkg.in/mgo.v2"

	"github.com/iris-contrib/middleware/cors"
	"github.com/iris-contrib/middleware/logger"
	"github.com/kataras/iris"
)

// Define topic
const (
	ComputerTopic = "computer-topic"
	ReturnTopic   = "return-topic"
)

var session *mgo.Session

func main() {
	initDB()
	defer session.Close()

	api := iris.New()
	api.Use(cors.Default())
	api.Use(logger.New())
	api.Post("/compute", compute)
	api.Post("/record", record)

	api.Listen(fmt.Sprintf(":%d", conf.Port))
}

// Init mongodb connecion
func initDB() {
	mongodb := conf.Mongodb
	info := &mgo.DialInfo{
		Addrs:    strings.Split(mongodb.Host, ","),
		Timeout:  60 * time.Second,
		Username: mongodb.Username,
		Password: mongodb.Password,
	}

	var err error
	session, err = mgo.DialWithInfo(info)
	if err != nil {
		panic("Connnect mongodb error:" + err.Error())
	}
}
