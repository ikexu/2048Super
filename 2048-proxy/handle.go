package main

import (
	"log"

	"github.com/orcaman/concurrent-map"

	"github.com/kataras/iris"
	"github.com/renstrom/shortuuid"
)

var (
	m = cmap.New()
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

	resultChan := make(chan *Result)
	defer close(resultChan)

	m.Set(r.UUID, resultChan)
	defer m.Remove(r.UUID)

	result := <-resultChan

	//rd := rand.New(rand.NewSource(time.Now().UnixNano()))
	ctx.JSON(iris.StatusOK, &Resp{
		Code:    00,
		Message: "Success!",
		//Data:    rd.Intn(4),
		Data: result.Direct,
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

func result(ctx *iris.Context) {
	result := &Result{}
	if err := ctx.ReadJSON(&result); err != nil {
		log.Println("Read json error:", err.Error())
		ctx.JSON(iris.StatusOK, Resp{
			Code:    01,
			Message: err.Error(),
		})
		return
	}
	if len(result.UUID) == 0 {
		ctx.JSON(iris.StatusOK, Resp{
			Code:    01,
			Message: "Key is empty",
		})
		return
	}

	// Retrieve item from map.
	if tmp, ok := m.Get(result.UUID); ok {
		ch := tmp.(chan *Result)
		ch <- result
	}

	ctx.JSON(iris.StatusOK, &Resp{
		Code:    00,
		Message: "Success!",
	})
}
