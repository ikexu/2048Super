package main

import (
	"fmt"
	"log"
	"strings"
	"time"

	"github.com/iris-contrib/middleware/cors"
	"github.com/iris-contrib/middleware/logger"
	"github.com/kataras/iris"
	mgo "gopkg.in/mgo.v2"
)

var session *mgo.Session

func main() {
	initDB()
	defer session.Close()

	api := iris.New()
	api.Use(cors.Default())
	api.Use(logger.New())
	api.Post("/", record)

	api.Listen(fmt.Sprintf(":%d", conf.Port))
}

// This handler for save grid data
func record(ctx *iris.Context) {
	record := &Record{}
	if err := ctx.ReadJSON(&record); err != nil {
		log.Println("Read json error:", err.Error())
		return
	}
	if err := record.Save(); err != nil {
		log.Println("Save game record error:", err.Error())
	}
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
