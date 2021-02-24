#!/bin/bash
mongoimport --db calendar --collection courses --drop --file datasets/courses.json
mongoimport --db calendar --collection rooms --drop --file datasets/rooms.json
mongoimport --db calendar --collection lectures --drop --file datasets/lectures.json