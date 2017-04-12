package main

import "time"

// Record ...
type Record struct {
	Grid      [][][]int `bson:"grid" json:"grid"`
	Score     int       `bson:"score" json:"score"`
	CreatedAt int64     `bson:"created_at" json:"-"`
}

// Save models
func (r *Record) Save() error {
	sc := session.Copy()
	defer sc.Close()

	c := sc.DB(conf.Mongodb.Database).C(conf.Mongodb.Collection)
	r.CreatedAt = time.Now().Unix()
	return c.Insert(r)
}
