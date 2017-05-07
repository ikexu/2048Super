package main

import "time"

type Direct int

const (
	UP Direct = iota
	RIGHT
	DOWN
	LEFT
)

type Record struct {
	Grid [][]int `json:"data"`
	UUID string  `json:"key"`
	Step int     `json:"step"`
}

type Result struct {
	UUID   string `json:"key"`
	Direct Direct `json:"direct"`
}

type Resp struct {
	Code    int         `json:"code"`
	Message string      `json:"message"`
	Data    interface{} `json:"data"`
}

// Record ...
type GridsRecord struct {
	Grid      [][][]int `bson:"grid" json:"grid"`
	Score     int       `bson:"score" json:"score"`
	CreatedAt int64     `bson:"created_at" json:"-"`
}

// Save models
func (r *GridsRecord) Save() error {
	sc := session.Copy()
	defer sc.Close()

	c := sc.DB(conf.Mongodb.Database).C(conf.Mongodb.Collection)
	r.CreatedAt = time.Now().Unix()
	return c.Insert(r)
}
