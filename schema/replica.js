rsconf = {
	_id: "londonSafeTravelSet",
	members: [
		{_id: 0, host: "172.16.5.43:27017", priority:1},
		{_id: 1, host: "172.16.5.47:27017", priority:2},
		{_id: 2, host: "172.16.5.42:27017", priority:5}]
};

rs.initiate(rsconf);
