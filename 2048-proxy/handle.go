package main

import (
	"log"

	"github.com/kataras/iris"
	"github.com/renstrom/shortuuid"
)

func compute(ctx *iris.Context) {
	r := &Record{}
	if err := ctx.ReadJSON(r); err != nil {
		log.Println("Read json error:", err.Error())
		return
	}
	r.UUID = shortuuid.New()
	if err := sendKafka(r); err != nil {
		ctx.JSON(iris.StatusOK, Resp{
			Code:    01,
			Message: err.Error(),
		})
		return
	}
	//if err := readKafka(r); err != nil {
	//ctx.JSON(iris.StatusOK, Resp{
	//Code:    01,
	//Message: err.Error(),
	//})
	//return
	//}
	ctx.JSON(iris.StatusOK, Resp{
		Code:    00,
		Message: "Success!",
	})
}

// This handler for save grid data
func record(ctx *iris.Context) {
	record := &GridsRecord{}
	if err := ctx.ReadJSON(&record); err != nil {
		log.Println("Read json error:", err.Error())
		return
	}
	if err := record.Save(); err != nil {
		log.Println("Save game record error:", err.Error())
	}
}
