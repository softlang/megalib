Steps to create a minimal MongoDB sharding setup on the local machine:

1. Create the folders `./data/shardsvr1/`, `/data/shardsvr2/` and `/data/configsvr/`

2. Start two shard servers:
`mongod --shardsvr --dbpath ./data/shardsvr1 --bind_ip 127.0.0.1`
`mongod --shardsvr --dbpath ./data/shardsvr2 --bind_ip 127.0.0.2`

3. Start a configuration server and initiate default single host replica set:
`mongod --configsvr --replSet c1 --dbpath ./data/configsvr --bind_ip 127.0.0.1`
`mongo --eval 'rs.initiate()' 127.0.0.1:27019`

4. Start mongos:
`mongos --configdb c1/127.0.0.1 --bind_ip 127.0.0.1`

5. Connect mongos to the shards:
`mongo --eval 'sh.addShard("127.0.0.1:27018")'`
`mongo --evel 'sh.addShard("127.0.0.2:27018")'`

6. Enable sharding for your database:
`mongo --eval 'sh.enableSharding("<database>")'`

7. Shard your collection:
`mongo --eval 'sh.shardCollection("<database>.<collection>", { <key> : <direction> } )'`
(See https://docs.mongodb.com/manual/reference/method/sh.shardCollection for details)
