db.Disruption.aggregate([
    /*{
        $match: {subCategory: "Burst Water Main"}
    },*/
    {
        $project: {
            start: {$multiply : [{$floor: {$divide: [
            	{$toLong: "$start"}, 3600000]}}, 3600]},
            // @TODO take min between $end and current_date
            end: {$multiply : [{$ceil: {$divide: 
            	[{$toLong: "$end"}, 3600000]}}, 3600]},
            id: "$id",
            category: "$category"
        }
    },
    {
        $project: {
            dates: {
                $map: {
                    input: {$range: ["$start", "$end", 3600]},
                    as: "i",
                    in: "$$i"
                }
            },
            id: "$id",
            category: "$category"
        }
    },
    {
        $unwind: "$dates"
    },
    {
        $project: {
            date: {$toDate: {$multiply: ["$dates", 1000]}},
            id: "$id",
            category: "$category"
        }
    },
    {
        $project: {
            year: {$year: "$date"},
            dayOfYear: {$dayOfYear: "$date"},
            hour: {$hour: "$date"},
            id: "$id",
            category: "$category"
        }
    },
    {
        $group: {
            _id: {year: "$year", dayOfYear: "$dayOfYear", hour: "$hour"},
            count: {
                $count: {}
            }
        }
    },
    {
        $group: {
            _id: "$_id.hour",
            count: {
                $avg: "$count"
            }
        }
    }
])
